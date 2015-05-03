package zmaster587.advancedRocketry.util;

import java.util.List;

import zmaster587.advancedRocketry.block.BlockSeat;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.World;

public class RocketStorageChunk extends StorageChunk {
	public int lastSeatX;
	public int lastSeatY;
	public int lastSeatZ;

	private RocketStorageChunk(int i, int j, int k) {
		super(i,j,k);
	}

	public RocketStorageChunk() {
		super();
	}

	public static RocketStorageChunk cutWorldBB(World world, AxisAlignedBB bb)  {
		RocketStorageChunk chunk = copyWorldBB(world, bb);

		for(int x = (int)bb.minX; x <= bb.maxX; x++) {
			for(int z = (int)bb.minZ; z <= bb.maxZ; z++) {
				for(int y = (int)bb.minY; y<= bb.maxY; y++) {
					world.setBlockToAir(x, y, z);
					
				}
			}
		}

		List<Entity> items = world.getEntitiesWithinAABB(EntityItem.class, bb);
		
		for(Entity item : items)
			item.setDead();
		
		return chunk;
	}

	public static RocketStorageChunk copyWorldBB(World world, AxisAlignedBB bb) {
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

		RocketStorageChunk ret = new RocketStorageChunk((actualMaxX - actualMinX + 1), (actualMaxY - actualMinY + 1), (actualMaxZ - actualMinZ + 1));

		//Iterate though the bounds given storing blocks/meta/tiles
		for(int x = actualMinX; x <= actualMaxX; x++) {
			for(int z = actualMinZ; z <= actualMaxZ; z++) {
				for(int y = actualMinY; y<= actualMaxY; y++) {

					Block block = world.getBlock(x, y, z);
					ret.blocks[x - actualMinX][y - actualMinY][z - actualMinZ] = block;
					ret.metas[x - actualMinX][y - actualMinY][z - actualMinZ] = (short)world.getBlockMetadata(x, y, z);

					if(block instanceof BlockSeat) {
						ret.lastSeatX = x - actualMinX;
						ret.lastSeatY = y - actualMinY;
						ret.lastSeatZ = z - actualMinZ;
					}

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

	@Override
	public void writeToNBT(NBTTagCompound nbt) {
		super.writeToNBT(nbt);

		nbt.setInteger("playerXPos", lastSeatX);
		nbt.setInteger("playerYPos", lastSeatY);
		nbt.setInteger("playerZPos", lastSeatZ);
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt) {
		super.readFromNBT(nbt);

		lastSeatX = nbt.getInteger("playerXPos");
		lastSeatY = nbt.getInteger("playerYPos");
		lastSeatZ = nbt.getInteger("playerZPos");
	}
}
