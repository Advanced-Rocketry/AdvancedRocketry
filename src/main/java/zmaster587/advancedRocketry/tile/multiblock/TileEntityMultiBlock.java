package zmaster587.advancedRocketry.tile.multiblock;

import java.util.HashSet;
import java.util.LinkedList;

import buildcraft.factory.TileHopper;
import zmaster587.advancedRocketry.api.AdvRocketryBlocks;
import zmaster587.advancedRocketry.block.multiblock.BlockMultiblockStructure;
import zmaster587.advancedRocketry.tile.TileInputHatch;
import zmaster587.advancedRocketry.tile.TileOutputHatch;
import zmaster587.advancedRocketry.tile.TileRFBattery;
import zmaster587.advancedRocketry.tile.data.TileDataBus;
import zmaster587.libVulpes.block.RotatableBlock;
import zmaster587.libVulpes.tile.IMultiblock;
import zmaster587.libVulpes.util.Vector3F;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

public class TileEntityMultiBlock extends TileEntity {

	/*CanRender must be seperate from incomplete because some multiblocks must be completed on the client but
	because chunks on the client */
	protected boolean completeStructure, canRender;
	protected byte timeAlive = 0;
	
	public TileEntityMultiBlock() {
		completeStructure = false;
		canRender = false;
	}
	

	public boolean isComplete() {
		return completeStructure;
	}
	
	public boolean canRender() {
		return canRender;
	}
	
	public String getMachineName() {
		return "";
	}
	
	public boolean isUsableByPlayer(EntityPlayer player) {
		return player.getDistance(xCoord, yCoord, zCoord) < 64;
	}
	
	@Override
	public Packet getDescriptionPacket() {
		NBTTagCompound nbt = new NBTTagCompound();
		nbt.setBoolean("canRender", canRender);
		writeToNBT(nbt);
		return new S35PacketUpdateTileEntity(xCoord, yCoord, zCoord, 0, nbt);
	}
	
	@Override
	public void onDataPacket(NetworkManager net, S35PacketUpdateTileEntity pkt) {
		NBTTagCompound nbt = pkt.func_148857_g();

		canRender = nbt.getBoolean("canRender");
		readFromNBT(nbt);
	}
	
	/**
	 * @param world world
	 * @param destroyedX x coord of destroyed block
	 * @param destroyedY y coord of destroyed block
	 * @param destroyedZ z coord of destroyed block
	 * @param blockBroken set true if the block is being broken, otherwise some other means is being used to disassemble the machine
	 */
	public void deconstructMultiBlock(World world, int destroyedX, int destroyedY, int destroyedZ, boolean blockBroken) {
		canRender = completeStructure = false;
		worldObj.setBlockMetadataWithNotify(xCoord, yCoord, zCoord, this.blockMetadata & 7, 2); //Turn off machine

		this.markDirty();
		worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);

		//UNDO all the placeholder blocks
		ForgeDirection front = getFrontDirection();

		Object[][][] structure = getStructure();
		Vector3F<Integer> offset = getControllerOffset(structure);


		//Mostly to make sure IMultiblocks lose their choke-hold on this machines and to revert placeholder blocks
		for(int y = 0; y < structure.length; y++) {
			for(int z = 0; z < structure[0].length; z++) {
				for(int x = 0; x< structure[0][0].length; x++) {

					int globalX = xCoord + (x - offset.x)*front.offsetZ - (z-offset.z)*front.offsetX;
					int globalY = yCoord - y + offset.y;
					int globalZ = zCoord - (x - offset.x)*front.offsetX  - (z-offset.z)*front.offsetZ;



					//This block is being broken anyway so don't bother
					if(blockBroken && globalX == destroyedX &&
							globalY == destroyedY &&
							globalZ == destroyedZ)
						continue;
					TileEntity tile = worldObj.getTileEntity(globalX, globalY, globalZ);
					Block block = worldObj.getBlock(globalX, globalY, globalZ);


					if(block instanceof BlockMultiblockStructure) {
						((BlockMultiblockStructure)block).destroyStructure(worldObj, globalX, globalY, globalZ, worldObj.getBlockMetadata(globalX, globalY, globalZ));
					}

					if(tile instanceof TilePlaceholder) {
						TilePlaceholder placeholder = (TilePlaceholder)tile;

						//Must set incomplete BEFORE changing the block to prevent stack overflow!
						placeholder.setIncomplete();

						worldObj.setBlock(tile.xCoord, tile.yCoord, tile.zCoord, placeholder.getReplacedBlock(), placeholder.getReplacedBlockMeta(), 3);

						//Dont try to set a tile if none existed
						if(placeholder.getReplacedTileEntity() != null) {
							NBTTagCompound nbt = new NBTTagCompound();
							placeholder.getReplacedTileEntity().writeToNBT(nbt);

							worldObj.getTileEntity(tile.xCoord, tile.yCoord, tile.zCoord).readFromNBT(nbt);
						}
					}
					//Make all pointers incomplete
					else if(tile instanceof IMultiblock) {
						((IMultiblock)tile).setIncomplete();
					}
				}
			}
		}
	}
	
	public ForgeDirection getFrontDirection() {
		//Make sure meta is not -1
		this.blockMetadata = worldObj.getBlockMetadata(xCoord, yCoord, zCoord);
		return RotatableBlock.getFront(this.blockMetadata);
	}
	
	public Object[][][] getStructure() {
		return null;
	}
	
	public boolean attemptCompleteStructure() {
		if(!completeStructure)
			canRender = completeStructure = completeStructure();
		return completeStructure;
	}
	
	/**
	 * Returns a hashset of blocks which are allowable in spaces set as *
	 */
	protected HashSet<Block> getAllowableWildCardBlocks() {
		return new HashSet<Block>();
	}
	
	public void resetCache() {
	}
	
	
	/**
	 * Use '*' to allow any kind of Hatch, or energy device or anything returned by getAllowableWildcards
	 * Use 'L' for liquid hatches TODO
	 * Use 'I' for input hatch
	 * Use 'O' for output hatch
	 * Use 'P' for power
	 * Use 'D' for data hatch
	 * Use 'c' for the main Block, there can only be one
	 * Use a class extending tile entity to require that tile be at that location
	 * Use a Block to force the user to place that block there
	 * @return true if the structure is valid
	 */
	protected boolean completeStructure() {

		//Make sure the environment is clean
		resetCache();

		Object[][][] structure = getStructure();

		Vector3F<Integer> offset = getControllerOffset(structure);

		ForgeDirection front = getFrontDirection();

		//Store tile entities for later processing so we don't risk the check failing halfway through leaving half the multiblock assigned
		LinkedList<TileEntity> tiles = new LinkedList<TileEntity>();

		for(int y = 0; y < structure.length; y++) {
			for(int z = 0; z < structure[0].length; z++) {
				for(int x = 0; x< structure[0][0].length; x++) {

					int globalX = xCoord + (x - offset.x)*front.offsetZ - (z-offset.z)*front.offsetX;
					int globalY = yCoord - y + offset.y;
					int globalZ = zCoord - (x - offset.x)*front.offsetX  - (z-offset.z)*front.offsetZ;

					TileEntity tile = worldObj.getTileEntity(globalX, globalY, globalZ);
					Block block = worldObj.getBlock(globalX, globalY, globalZ);

					if(tile != null)
						tiles.add(tile);

					//If the other block already thinks it's complete just assume valid
					if(tile instanceof TilePlaceholder) {
						if(((IMultiblock)tile).getMasterBlock() != this)
							return false;
						else 
							continue;
					}

					if(structure[y][z][x] instanceof Character && (Character)structure[y][z][x] == '*') {

						if(!(tile instanceof TileInventoryHatch) && !(tile instanceof TileRFBattery) && !getAllowableWildCardBlocks().contains(block)) {	
							return false;
						}

					}
					else if(structure[y][z][x] instanceof Character && (Character)structure[y][z][x] == 'D') {
						if(!(tile instanceof TileDataBus))
							return false;
					}
					else if(structure[y][z][x] instanceof Character && (Character)structure[y][z][x] == 'P') {
						if(!(tile instanceof TileRFBattery)) //TODO make universal
							return false;
					}
					else if(structure[y][z][x] instanceof Character && (Character)structure[y][z][x] == 'I') {
						if(!(tile instanceof TileInputHatch))
							return false;
					}
					else if(structure[y][z][x] instanceof Character && (Character)structure[y][z][x] == 'O') {
						if(!(tile instanceof TileOutputHatch))
							return false;
					}
					else if(structure[y][z][x] instanceof Block && block != structure[y][z][x]) {

						return false;
					}
					else if(structure[y][z][x] instanceof Class<?> && tile.getClass() != structure[y][z][x]) {

						return false;
					}

				}
			}
		}

		for(int y = 0; y < structure.length; y++) {
			for(int z = 0; z < structure[0].length; z++) {
				for(int x = 0; x< structure[0][0].length; x++) {

					int globalX = xCoord + (x - offset.x)*front.offsetZ - (z-offset.z)*front.offsetX;
					int globalY = yCoord - y + offset.y;
					int globalZ = zCoord - (x - offset.x)*front.offsetX  - (z-offset.z)*front.offsetZ;

					TileEntity tile = worldObj.getTileEntity(globalX, globalY, globalZ);
					Block block = worldObj.getBlock(globalX, globalY, globalZ);

					if(block instanceof BlockMultiblockStructure) {
						((BlockMultiblockStructure)block).completeStructure(worldObj, globalX, globalY, globalZ, worldObj.getBlockMetadata(globalX, globalY, globalZ));
					}

					if(!block.isAir(worldObj, globalX, globalY, globalZ) && !(tile instanceof IMultiblock) && !(tile instanceof TileEntityMultiBlock)) {
						byte meta = (byte)worldObj.getBlockMetadata(globalX, globalY, globalZ);

						worldObj.setBlock(globalX, globalY, globalZ, AdvRocketryBlocks.blockPlaceHolder);
						TilePlaceholder newTile = (TilePlaceholder)worldObj.getTileEntity(globalX, globalY, globalZ);

						newTile.setReplacedBlock(block);
						newTile.setReplacedBlockMeta(meta);
						newTile.setReplacedTileEntity(tile);
						newTile.setMasterBlock(xCoord, yCoord, zCoord);
					}
				}
			}
		}

		//Now that we know the multiblock is valid we can assign
		for(TileEntity tile : tiles) {
			integrateTile(tile);
		}
		markDirty();
		worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
		
		return true;
	}

	/**
	 * This is used so classes extending this one can have their own handling of tiles without overriding the method
	 * @param tile Current tile in multiblock
	 */
	protected void integrateTile(TileEntity tile) {
		if(tile instanceof IMultiblock)
			((IMultiblock) tile).setComplete(xCoord, yCoord, zCoord);
	}
	
	protected Vector3F<Integer> getControllerOffset(Object[][][] structure) {
		for(int y = 0; y < structure.length; y++) {
			for(int z = 0; z < structure[0].length; z++) {
				for(int x = 0; x< structure[0][0].length; x++) {
					if(structure[y][z][x] instanceof Character && (Character)structure[y][z][x] == 'c')
						return new Vector3F<Integer>(x, y, z);
				}
			}
		}
		return null;
	}
}
