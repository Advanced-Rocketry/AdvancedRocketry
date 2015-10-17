package zmaster587.advancedRocketry.tile.multiblock;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import zmaster587.advancedRocketry.api.AdvancedRocketryBlocks;
import zmaster587.advancedRocketry.block.multiblock.BlockMultiBlockComponentVisible;
import zmaster587.advancedRocketry.block.multiblock.BlockMultiblockStructure;
import zmaster587.libVulpes.block.BlockMeta;
import zmaster587.libVulpes.block.RotatableBlock;
import zmaster587.libVulpes.tile.IMultiblock;
import zmaster587.libVulpes.tile.TilePointer;
import zmaster587.libVulpes.util.Vector3F;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

public class TileMultiBlock extends TileEntity {

	/*CanRender must be seperate from incomplete because some multiblocks must be completed on the client but
	because chunks on the client.  It is also used to determine if the block on the server has ever been complete */
	protected boolean completeStructure, canRender;
	protected byte timeAlive = 0;

	public TileMultiBlock() {
		completeStructure = false;
		canRender = false;
	}


	/**
	 * Note: it may be true on the server but not the client.  This is because the client needs to form the multiblock
	 * so the tile has references to other blocks in its structure for gui display etc
	 * @return true if the structure is complete
	 */
	public boolean isComplete() {
		return completeStructure;
	}

	/**
	 * 
	 * @return true if the block should be rendered as complete
	 */
	@SideOnly(Side.CLIENT)
	public boolean canRender() {
		return canRender;
	}

	/**
	 * @return the unlocalized name of the machine
	 */
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
		writeNetworkData(nbt);
		return new S35PacketUpdateTileEntity(xCoord, yCoord, zCoord, 0, nbt);
	}

	@Override
	public void onDataPacket(NetworkManager net, S35PacketUpdateTileEntity pkt) {
		NBTTagCompound nbt = pkt.func_148857_g();

		canRender = nbt.getBoolean("canRender");
		readNetworkData(nbt);
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
		worldObj.setBlockMetadataWithNotify(xCoord, yCoord, zCoord, this.getBlockMetadata() & 7, 2); //Turn off machine

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

					destroyBlockAt(globalX, globalY, globalZ, block, tile);

				}
			}
		}
	}

	/**
	 * Called when the multiblock is being deconstructed.  This is called for each block in the structure.
	 * Provided in case of special handling
	 * @param x
	 * @param y
	 * @param z
	 * @param block
	 * @param tile
	 */
	protected void destroyBlockAt(int x, int y, int z, Block block, TileEntity tile) {

		//if it's an instance of multiblock structure call its destroyStructure method
		if(block instanceof BlockMultiblockStructure) {
			((BlockMultiblockStructure)block).destroyStructure(worldObj, x, y, z, worldObj.getBlockMetadata(x, y, z));
		}

		//If the the tile is a placeholder then make sure to replace it with its original block and tile
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

	public ForgeDirection getFrontDirection() {
		return RotatableBlock.getFront(this.getBlockMetadata());
	}

	public Object[][][] getStructure() {
		return null;
	}

	public boolean attemptCompleteStructure() {
		//if(!completeStructure)
			canRender = completeStructure = completeStructure();
		return completeStructure;
	}

	public List<BlockMeta> getAllowableWildCardBlocks() {
		List<BlockMeta> list =new ArrayList<BlockMeta>();
		return list;
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
					int meta = worldObj.getBlockMetadata(globalX, globalY, globalZ);

					if(block == AdvancedRocketryBlocks.blockPhantom)
						return false;
					
					if(tile != null)
						tiles.add(tile);

					//If the other block already thinks it's complete just assume valid
					if(tile instanceof TilePointer) {
						if(((IMultiblock)tile).hasMaster() && ((IMultiblock)tile).getMasterBlock() != this) {
							return false;
						}
						else 
							continue;
					}
					//Make sure the structure is valid
					if(!(structure[y][z][x] instanceof Character && (Character)structure[y][z][x] == 'c') && !(structure[y][z][x] instanceof Block && (Block)structure[y][z][x] == Blocks.air && worldObj.isAirBlock(globalX, globalY, globalZ)) && !getAllowableBlocks(structure[y][z][x]).contains(new BlockMeta(block,meta)))
						return false;
				}
			}
		}

		//Notify all blocks in the structure that it's being build and assimilate them
		for(int y = 0; y < structure.length; y++) {
			for(int z = 0; z < structure[0].length; z++) {
				for(int x = 0; x< structure[0][0].length; x++) {

					int globalX = xCoord + (x - offset.x)*front.offsetZ - (z-offset.z)*front.offsetX;
					int globalY = yCoord - y + offset.y;
					int globalZ = zCoord - (x - offset.x)*front.offsetX  - (z-offset.z)*front.offsetZ;


					Block block = worldObj.getBlock(globalX, globalY, globalZ);
					TileEntity tile = worldObj.getTileEntity(globalX, globalY, globalZ);

					if(block instanceof BlockMultiBlockComponentVisible) {
						((BlockMultiBlockComponentVisible)block).hideBlock(worldObj, globalX, globalY, globalZ, worldObj.getBlockMetadata(globalX, globalY, globalZ));
						
						tile = worldObj.getTileEntity(globalX, globalY, globalZ);

						if(tile instanceof IMultiblock)
							((IMultiblock)tile).setComplete(this.xCoord, this.yCoord, this.zCoord);
					}
					else if(block instanceof BlockMultiblockStructure) {
						if(shouldHideBlock(worldObj, globalX, globalY, globalZ, block))
							((BlockMultiblockStructure)block).hideBlock(worldObj, globalX, globalY, globalZ, worldObj.getBlockMetadata(globalX, globalY, globalZ));
					}

					if(!block.isAir(worldObj, globalX, globalY, globalZ) && !(tile instanceof IMultiblock) && !(tile instanceof TileMultiBlock)) {
						replaceStandardBlock(globalX,globalY, globalZ, block, tile);
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
	 * @return a list containing allowable block and metadatas for machine item outputs
	 */
	public List<BlockMeta> getAllowableOutputBlocks() {
		List<BlockMeta> list = new LinkedList<BlockMeta>();
		list.add(new BlockMeta(AdvancedRocketryBlocks.blockHatch, 1));
		return list;
	}

	/**
	 * @return a list containing allowable block and metadatas for machine item outputs
	 */
	public List<BlockMeta> getInputs() {
		List<BlockMeta> list = new LinkedList<BlockMeta>();
		list.add(new BlockMeta(AdvancedRocketryBlocks.blockHatch, 0));
		return list;
	}
	
	/**
	 * @return a list containing allowable block and metadatas for machine data ports
	 */
	public List<BlockMeta> getDataBlocks() {
		List<BlockMeta> list = new LinkedList<BlockMeta>();
		list.add(new BlockMeta(AdvancedRocketryBlocks.blockHatch, 2));
		return list;
	}
	
	/**
	 * @return a list containing allowable block and metadatas for machine power inputs
	 */
	public List<BlockMeta> getPowerInputBlocks() {
		List<BlockMeta> list = new LinkedList<BlockMeta>();
		list.add(new BlockMeta(AdvancedRocketryBlocks.blockRFBattery, -1));
		return list;
	}
	
	public List<BlockMeta> getAllowableBlocks(Object input) {
		if(input instanceof Character && (Character)input == '*') {
			return getAllowableWildCardBlocks();
		}
		else if(input instanceof Character && (Character)input == 'D') {
			return getDataBlocks();
		}
		else if(input instanceof Character && (Character)input == 'P') {
			return getPowerInputBlocks();
		}
		else if(input instanceof Character && (Character)input == 'I') {
			return getInputs();
		}
		else if(input instanceof Character && (Character)input == 'O') {
			return getAllowableOutputBlocks();
		}
		else if(input instanceof Block) {
			List<BlockMeta> list = new ArrayList<BlockMeta>();
			list.add(new BlockMeta((Block) input, BlockMeta.WILDCARD));
			return list;
		}
		else if(input instanceof BlockMeta) {
			List<BlockMeta> list = new ArrayList<BlockMeta>();
			list.add((BlockMeta) input);
			return list;
		}
		List<BlockMeta> list = new ArrayList<BlockMeta>();
		return list;
	}
	
	public boolean shouldHideBlock(World world, int x, int y, int z, Block tile) {
		return false;
	}

	/**
	 * Called when replacing a block that is not specifically designed to be compatible with the multiblocks.  Eg iron black
	 * Most multiblocks have a renderer and so these blocks are converted to an invisible pointer
	 * @return
	 */
	protected void replaceStandardBlock(int xCoord, int yCoord, int zCoord, Block block, TileEntity tile) {

		byte meta = (byte)worldObj.getBlockMetadata(xCoord, yCoord, zCoord);

		worldObj.setBlock(xCoord, yCoord, zCoord, AdvancedRocketryBlocks.blockPlaceHolder);
		TilePlaceholder newTile = (TilePlaceholder)worldObj.getTileEntity(xCoord, yCoord, zCoord);

		newTile.setReplacedBlock(block);
		newTile.setReplacedBlockMeta(meta);
		newTile.setReplacedTileEntity(tile);
		newTile.setMasterBlock(this.xCoord, this.yCoord, this.zCoord);
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

	protected void writeNetworkData(NBTTagCompound nbt) {
		
	}
	
	protected void readNetworkData(NBTTagCompound nbt) {
		
	}
	
	@Override
	public void writeToNBT(NBTTagCompound nbt) {
		super.writeToNBT(nbt);
		writeNetworkData(nbt);
		nbt.setBoolean("completeStructure", completeStructure);
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt) {
		super.readFromNBT(nbt);
		readNetworkData(nbt);
		completeStructure = nbt.getBoolean("completeStructure");
	}
}
