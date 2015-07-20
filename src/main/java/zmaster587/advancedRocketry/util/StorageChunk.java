/* Temporarily stores tile/blocks to move a block of them
 * 
 * 
 */

package zmaster587.advancedRocketry.util;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraftforge.common.util.ForgeDirection;

public class StorageChunk implements IBlockAccess, IInventory {

	Block blocks[][][];
	short metas[][][];
	int sizeX, sizeY, sizeZ;
	int sizeInv;

	private int invPosition;

	ArrayList<TileEntity> tileEntities;

	//To store inventories (just chests right now)
	ArrayList<IInventory> inventories;
	ItemStack invCache[];

	public StorageChunk() {
		sizeX = 0;
		sizeY = 0;
		sizeZ = 0;
		tileEntities = new ArrayList<TileEntity>();
		inventories = new ArrayList<IInventory>();
		sizeInv = -1;
		invPosition = 0;
	}

	protected StorageChunk(int xSize, int ySize, int zSize) {
		blocks = new Block[xSize][ySize][zSize];
		metas = new short[xSize][ySize][zSize];

		sizeX = xSize;
		sizeY = ySize;
		sizeZ = zSize;

		tileEntities = new ArrayList<TileEntity>();
		inventories = new ArrayList<IInventory>();

		sizeInv = -1;

		invPosition = 0;
	}

	public int getSizeX() { return sizeX; }
	public int getSizeY() { return sizeY; }
	public int getSizeZ() { return sizeZ; }

	public void incrementInvPos() {
		initInventories();

		if(sizeInv > ((invPosition+1)*27) )
			invPosition++;
	}


	public void decrementInvPos() {
		initInventories();

		if(invPosition > 0 )
			invPosition--;
	}

	public void setInvPos(int pos) {
		if(pos < 0)
			invPosition = 0;
		invPosition = (pos*27 > sizeInv) ? sizeInv/27 : pos;
	}

	public int getInvPos() {
		return invPosition;
	}

	public List<TileEntity> getTileEntityList() {

		return tileEntities;
	}

	@Override
	public Block getBlock(int x, int y, int z) {
		if(x < 0 || x >= sizeX || y < 0 || y >= sizeY || z < 0 || z >= sizeZ)
			return Blocks.air;

		if(blocks[x][y][z] != Blocks.air)
			return blocks[x][y][z];
		return blocks[x][y][z];
	}

	public void writeToNBT(NBTTagCompound nbt) {
		nbt.setInteger("xSize", sizeX);
		nbt.setInteger("ySize", sizeY);
		nbt.setInteger("zSize", sizeZ);

		for(int x = 0; x < sizeX; x++) {
			for(int y = 0; y < sizeY; y++) {
				for(int z = 0; z < sizeZ; z++) {

					NBTTagCompound tag = new NBTTagCompound();
					tag.setInteger("block", Block.getIdFromBlock(blocks[x][y][z]));
					tag.setShort("meta", metas[x][y][z]);

					NBTTagCompound tileNbtData = null;

					for(TileEntity tile : tileEntities) {
						NBTTagCompound tileNbt = new NBTTagCompound();

						tile.writeToNBT(tileNbt);

						if(tileNbt.getInteger("x") == x && tileNbt.getInteger("y") == y && tileNbt.getInteger("z") == z){
							tileNbtData = tileNbt;
							break;
						}
					}

					if(tileNbtData != null)
						tag.setTag("tile", tileNbtData);

					nbt.setTag(String.format("%d.%d.%d", x,y,z), tag);
				}

			}
		}
	}



	public void readFromNBT(NBTTagCompound nbt) {
		sizeX = nbt.getInteger("xSize");
		sizeY = nbt.getInteger("ySize");
		sizeZ = nbt.getInteger("zSize");

		blocks = new Block[sizeX][sizeY][sizeZ];
		metas = new short[sizeX][sizeY][sizeZ];

		tileEntities.clear();
		inventories.clear();
		sizeInv = -1;

		for(int x = 0; x < sizeX; x++) {
			for(int y = 0; y < sizeY; y++) {
				for(int z = 0; z < sizeZ; z++) {



					NBTTagCompound tag = (NBTTagCompound)nbt.getTag(String.format("%d.%d.%d", x,y,z));

					if(!tag.hasKey("block"))
						continue;
					int blockId = tag.getInteger("block"); 
					blocks[x][y][z] = Block.getBlockById(blockId);
					metas[x][y][z] = tag.getShort("meta");


					if(tag.hasKey("tile")) {
						TileEntity tile = TileEntity.createAndLoadEntity(tag.getCompoundTag("tile"));
						if(Thread.currentThread().getName().equalsIgnoreCase("Client thread")) {
							try {
								tile.setWorldObj((World)Class.forName("net.minecraft.client.Minecraft").getField("theWorld").get(Class.forName("net.minecraft.client.Minecraft").getMethod("getMinecraft").invoke(null)));
							} catch (Exception e) {
								e.printStackTrace();
							}
						}
						tileEntities.add(tile);

						//Machines would throw a wrench in the works
						if(tile instanceof TileEntityChest) {
							inventories.add((IInventory)tile);
						}
					}

				}
			}
		}

	}

	public static StorageChunk copyWorldBB(World world, AxisAlignedBB bb) {
		int actualMinX = (int)bb.maxX,
				actualMinY = (int)bb.maxY,
				actualMinZ = (int)bb.maxZ,
				actualMaxX = (int)bb.minX,
				actualMaxY = (int)bb.minY,
				actualMaxZ = (int)bb.minZ;


		for(int x = (int)bb.minX; x <= bb.maxX; x++) {
			for(int z = (int)bb.minZ; z <= bb.maxZ; z++) {
				for(int y = (int)bb.minY; y<= bb.maxY; y++) {

					Block block = world.getBlock(x, y, z);

					if(!block.isAir(world, x, y, z)) {
						if(x < actualMinX)
							actualMinX = x;
						if(y < actualMinY)
							actualMinY = y;
						if(z < actualMinZ)
							actualMinZ = z;
						if(x > actualMaxX)
							actualMaxX = x;
						if(y > actualMaxY)
							actualMaxY = y;
						if(z > actualMaxZ)
							actualMaxZ = z;
					}
				}
			}
		}

		StorageChunk ret = new StorageChunk((actualMaxX - actualMinX + 1), (actualMaxY - actualMinY + 1), (actualMaxZ - actualMinZ + 1));

		//Iterate though the bounds given storing blocks/meta/tiles
		for(int x = actualMinX; x <= actualMaxX; x++) {
			for(int z = actualMinZ; z <= actualMaxZ; z++) {
				for(int y = actualMinY; y<= actualMaxY; y++) {


					ret.blocks[x - actualMinX][y - actualMinY][z - actualMinZ] = world.getBlock(x, y, z);
					ret.metas[x - actualMinX][y - actualMinY][z - actualMinZ] = (short)world.getBlockMetadata(x, y, z);

					TileEntity entity = world.getTileEntity(x, y, z);
					if(entity != null) {
						NBTTagCompound nbt = new NBTTagCompound();
						entity.writeToNBT(nbt);

						//Transform tileEntity coords
						nbt.setInteger("x",nbt.getInteger("x") - actualMinX);
						nbt.setInteger("y",nbt.getInteger("y") - actualMinY);
						nbt.setInteger("z",nbt.getInteger("z") - actualMinZ);

						TileEntity newTile = TileEntity.createAndLoadEntity(nbt);

						if(entity instanceof TileEntityChest)
							ret.inventories.add((IInventory)newTile);

						ret.tileEntities.add(newTile);
					}
				}
			}
		}

		return ret;
	}

	//pass the coords of the xmin, ymin, zmin as well as the world to move the rocket
	public void pasteInWorld(World world, int xCoord, int yCoord ,int zCoord) {

		//Set all the blocks
		for(int x = 0; x < sizeX; x++) {
			for(int z = 0; z < sizeZ; z++) {
				for(int y = 0; y< sizeY; y++) {

					if(blocks[x][y][z] != null)
						world.setBlock(xCoord + x, yCoord + y, zCoord + z, blocks[x][y][z], metas[x][y][z], 2);
				}
			}
		}

		//Set tiles for each block
		for(TileEntity tile : tileEntities) {
			NBTTagCompound nbt = new NBTTagCompound();
			tile.writeToNBT(nbt);
			int x = nbt.getInteger("x");
			int y = nbt.getInteger("y");
			int z = nbt.getInteger("z");

			int tmpX = x + xCoord;
			int tmpY = y + yCoord;
			int tmpZ = z + zCoord;

			//Set blocks of tiles again to avoid weirdness caused by updates
			//world.setBlock(xCoord + x, yCoord + y, zCoord + z, blocks[x][y][z], metas[x][y][z], 2);


			nbt.setInteger("x",tmpX);
			nbt.setInteger("y",tmpY);
			nbt.setInteger("z",tmpZ);

			TileEntity entity = world.getTileEntity(tmpX, tmpY, tmpZ);

			if(entity != null)
				entity.readFromNBT(nbt);
		}
	}


	@Override
	public TileEntity getTileEntity(int x, int y,
			int z) {
		for(TileEntity tileE : tileEntities) {
			if( tileE.xCoord == x &&  tileE.yCoord == y &&  tileE.zCoord == z)
				return tileE;
		}
		return null;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public int getLightBrightnessForSkyBlocks(int x, int y,
			int z, int meta) {
		Entity ent = Minecraft.getMinecraft().renderViewEntity;
		return Minecraft.getMinecraft().theWorld.getLightBrightnessForSkyBlocks((int)ent.posX, (int)ent.posY, (int)ent.posZ, meta);
	}

	@Override
	public int getBlockMetadata(int x, int y, int z) {
		// Need bounds check... Thank you renderBlockStairs
		if(x < 0 || x >= sizeX || y < 0 || y >= sizeY || z < 0 || z >= sizeZ )
			return 0;
		return metas[x][y][z];
	}

	@Override
	public int isBlockProvidingPowerTo(int p_72879_1_, int p_72879_2_,
			int p_72879_3_, int p_72879_4_) {
		return 0;
	}

	@Override
	public boolean isAirBlock(int x, int y, int z) {
		return blocks[x][y][z] == Blocks.air;
	}

	@Override
	public BiomeGenBase getBiomeGenForCoords(int p_72807_1_, int p_72807_2_) {
		return null;
	}

	@Override
	public int getHeight() {
		return sizeY;
	}

	@Override
	public boolean extendedLevelsInChunkCache() {
		return false;
	}

	@Override
	public boolean isSideSolid(int x, int y, int z, ForgeDirection side,
			boolean _default) {

		if(x < 0 || x >= sizeX || y < 0 || y >= sizeY || z < 0 || z >= sizeZ  || x + side.offsetX < 0 || x + side.offsetX >= sizeX || y + side.offsetY < 0 || y + side.offsetY >= sizeY || z + side.offsetZ < 0 || z + side.offsetZ >= sizeZ)
			return false;

		return blocks[x + side.offsetX][y + side.offsetY][z + side.offsetZ].isBlockSolid(this, x, y, z, metas[x][y][z]);
	}

	public static StorageChunk cutWorldBB(World worldObj, AxisAlignedBB bb) {
		StorageChunk chunk = StorageChunk.copyWorldBB(worldObj, bb);
		for(int x = (int)bb.minX; x <= bb.maxX; x++) {
			for(int z = (int)bb.minZ; z <= bb.maxZ; z++) {
				for(int y = (int)bb.minY; y<= bb.maxY; y++) {

					worldObj.setBlockToAir(x, y, z);
					List<EntityItem> stacks = worldObj.getEntitiesWithinAABB(EntityItem.class, bb);

					for(EntityItem stack : stacks)
						stack.setDead();
				}
			}
		}
		return chunk;
	}

	private void initInventories() {
		//Get number of inventories on board
		if(doesInvCacheExist()) {
			LinkedList<ItemStack> list = new LinkedList<ItemStack>();
			sizeInv = 0;
			for(IInventory i : inventories) {
				int size = i.getSizeInventory();

				for(int j = 0; j < size; j++) {
					list.add(i.getStackInSlot(j));
				}

				//must be in sections of 27
				sizeInv += Math.max(27,size);
			}


			invCache = new ItemStack[sizeInv];
			invCache = list.toArray(invCache);
		}
	}

	private boolean doesInvCacheExist() {
		return sizeInv == -1;
	}

	@Override
	public int getSizeInventory() {

		initInventories();
		return sizeInv == 0 ? sizeInv : inventories.get(invPosition).getSizeInventory();
	}

	@Override
	public ItemStack getStackInSlot(int slot) {
		initInventories();

		return inventories.get(invPosition).getStackInSlot(slot);
	}

	@Override
	public ItemStack decrStackSize(int slot, int amt) {
		return inventories.get(invPosition).decrStackSize(slot,amt);
	}

	@Override
	public ItemStack getStackInSlotOnClosing(int slot) {

		return inventories.get(invPosition).getStackInSlotOnClosing(slot);
	}

	@Override
	public void setInventorySlotContents(int slot, ItemStack stack) {
		inventories.get(invPosition).setInventorySlotContents(slot, stack);
	}

	@Override
	public String getInventoryName() {
		return "Rocket";
	}

	@Override
	public boolean hasCustomInventoryName() {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public int getInventoryStackLimit() {
		return 64;
	}

	@Override
	public void markDirty() {
	}

	@Override
	public boolean isUseableByPlayer(EntityPlayer player) {
		return true;
	}

	@Override
	public void openInventory() {

	}

	@Override
	public void closeInventory() {

	}

	@Override
	public boolean isItemValidForSlot(int slot, ItemStack stack) {
		return inventories.get(invPosition).isItemValidForSlot(slot, stack);
	}
}
