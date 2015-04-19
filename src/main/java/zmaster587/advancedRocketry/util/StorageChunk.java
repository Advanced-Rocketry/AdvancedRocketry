/* Temporarily stores tile/blocks to move a block of them
 * 
 * 
 */

package zmaster587.advancedRocketry.util;

import java.util.ArrayList;

import net.minecraft.block.Block;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.World;

public class StorageChunk {
	Block blocks[][][];
	short metas[][][];
	int sizeX, sizeY, sizeZ;

	ArrayList<NBTTagCompound> tile;

	private StorageChunk(int xSize, int ySize, int zSize) {
		blocks = new Block[xSize][ySize][zSize];
		metas = new short[xSize][ySize][zSize];

		sizeX = xSize;
		sizeY = ySize;
		sizeZ = zSize;

		tile = new ArrayList<NBTTagCompound>();
	}

	public static StorageChunk copyWorldBB(World world, AxisAlignedBB bb) {
		StorageChunk ret = new StorageChunk((int)(bb.maxX - bb.minX + 1), (int)(bb.maxY - bb.minY + 1), (int)(bb.maxZ - bb.minZ + 1));

		//Iterate though the bounds given storing blocks/meta/tiles
		for(int x = (int)bb.minX; x <= bb.maxX; x++) {
			for(int z = (int)bb.minZ; z <= bb.maxZ; z++) {
				for(int y = (int)bb.minY; y<= bb.maxY; y++) {


					ret.blocks[x - (int)bb.minX][y - (int)bb.minY][z - (int)bb.minZ] = world.getBlock(x, y, z);
					ret.metas[x - (int)bb.minX][y - (int)bb.minY][z - (int)bb.minZ] = (short)world.getBlockMetadata(x, y, z);

					TileEntity entity = world.getTileEntity(x, y, z);
					if(entity != null) {
						NBTTagCompound nbt = new NBTTagCompound();
						entity.writeToNBT(nbt);

						//Transform tileEntity coords
						nbt.setInteger("x",nbt.getInteger("x") - (int)bb.minX);
						nbt.setInteger("y",nbt.getInteger("y") - (int)bb.minY);
						nbt.setInteger("z",nbt.getInteger("z") - (int)bb.minZ);

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
}
