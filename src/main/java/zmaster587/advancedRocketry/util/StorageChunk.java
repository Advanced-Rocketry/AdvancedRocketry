/* Temporarily stores tile/blocks to move a block of them
 * 
 * 
 */

package zmaster587.advancedRocketry.util;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraftforge.common.util.ForgeDirection;

public class StorageChunk implements IBlockAccess {
	Block blocks[][][];
	short metas[][][];
	int sizeX, sizeY, sizeZ;

	ArrayList<NBTTagCompound> tile;
	ArrayList<TileEntity> tileEntities;

	public StorageChunk() {
		sizeX = 0;
		sizeY = 0;
		sizeZ = 0;
		tile = new ArrayList<NBTTagCompound>();
		tileEntities = new ArrayList<TileEntity>();
	}

	protected StorageChunk(int xSize, int ySize, int zSize) {
		blocks = new Block[xSize][ySize][zSize];
		metas = new short[xSize][ySize][zSize];

		sizeX = xSize;
		sizeY = ySize;
		sizeZ = zSize;

		tile = new ArrayList<NBTTagCompound>();
		tileEntities = new ArrayList<TileEntity>();
	}

	public int getSizeX() { return sizeX; }
	public int getSizeY() { return sizeY; }
	public int getSizeZ() { return sizeZ; }

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

					for(NBTTagCompound tileNbt : tile) {
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
						tile.add(tag.getCompoundTag("tile"));
						TileEntity tile = TileEntity.createAndLoadEntity(tag.getCompoundTag("tile"));
						if(Thread.currentThread().getName().equalsIgnoreCase("Client thread"))
							tile.setWorldObj(Minecraft.getMinecraft().theWorld);
						tileEntities.add(tile);
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

						ret.tile.add(nbt);
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
		for(NBTTagCompound nbt : tile) {
			int x = nbt.getInteger("x");
			int y = nbt.getInteger("y");
			int z = nbt.getInteger("z");

			int tmpX = x + xCoord;
			int tmpY = y + yCoord;
			int tmpZ = z + zCoord;

			//Set blocks of tiles again to avoid weirdness caused by updates
			world.setBlock(xCoord + x, yCoord + y, zCoord + z, blocks[x][y][z], metas[x][y][z], 2);


			nbt.setInteger("x",tmpX);
			nbt.setInteger("y",tmpY);
			nbt.setInteger("z",tmpZ);

			TileEntity entity = world.getTileEntity(tmpX, tmpY, tmpZ);

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
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isSideSolid(int x, int y, int z, ForgeDirection side,
			boolean _default) {

		if(x + side.offsetX < 0 || x + side.offsetX >= sizeX || y + side.offsetY < 0 || y + side.offsetY >= sizeY || z + side.offsetZ < 0 || x + side.offsetZ >= sizeZ)
			return false;
			
		return blocks[x + side.offsetX][y + side.offsetY][z + side.offsetZ].isBlockSolid(this, x, y, z, metas[x][y][z]);
	}
}
