/*
 * Purpose: validate the rocket structure as well as give feedback to the player as to what needs to be 
 * changed to complete the rocket structure
 * Also will be used to "build" the rocket components from the placed frames, control fuel flow etc
 */

package zmaster587.advancedRocketry.tile;

import zmaster587.advancedRocketry.AdvancedRocketry;
import zmaster587.libVulpes.block.RotatableBlock;
import zmaster587.libVulpes.util.ZUtils;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

public class TileRocketBuilder extends TileEntity {

	private final int MAX_SIZE = 16;
	private final int MIN_SIZE = 3;
	private final int MAX_SIZE_Y = 32;
	private final int MIN_SIZE_Y = 4;

	public TileRocketBuilder() {
	}

	@Override
	public boolean canUpdate() {
		return false;
	}

	
	/**
	 * 
	 * @param world the world
	 * @param x coord to evaluate from
	 * @param y coord to evaluate from
	 * @param z coord to evaluate from
	 * @return AxisAlignedBB bounds of structure if valid  otherwise null
	 */
	public AxisAlignedBB getRocketBounds(World world,int x, int y, int z) {
		ForgeDirection direction = RotatableBlock.getFront(world.getBlockMetadata(x, y, z)).getOpposite();
		int xMin, zMin, xMax, zMax;
		int yCurrent = y -1;
		int xCurrent = x + direction.offsetX;
		int zCurrent = z + direction.offsetZ;
		xMax = xMin = xCurrent;
		zMax = zMin = zCurrent;
		int xSize, zSize;

		if(world.isRemote)
			return null;
		
		//Get min and maximum Z/X bounds
		if(direction.offsetX != 0) {
			xSize = ZUtils.getContinuousBlockLength(world, direction, xCurrent, yCurrent, zCurrent, MAX_SIZE, AdvancedRocketry.launchpad);
			zMin = ZUtils.getContinuousBlockLength(world, ForgeDirection.NORTH, xCurrent, yCurrent, zCurrent, MAX_SIZE, AdvancedRocketry.launchpad);
			zMax = ZUtils.getContinuousBlockLength(world, ForgeDirection.SOUTH, xCurrent, yCurrent, zCurrent+1, MAX_SIZE - zMin, AdvancedRocketry.launchpad);
			zSize = zMin + zMax;
			
			zMin = zCurrent - zMin + 1;
			zMax = zCurrent + zMax;
			
			if(direction.offsetX > 0) {
				xMax = xCurrent + xSize-1;
			}
			
			if(direction.offsetX < 0) {
				xMin = xCurrent - xSize+1;
			}
		}
		else {
			zSize = ZUtils.getContinuousBlockLength(world, direction, xCurrent, yCurrent, zCurrent, MAX_SIZE, AdvancedRocketry.launchpad);
			xMin = ZUtils.getContinuousBlockLength(world, ForgeDirection.WEST, xCurrent, yCurrent, zCurrent, MAX_SIZE, AdvancedRocketry.launchpad);
			xMax = ZUtils.getContinuousBlockLength(world, ForgeDirection.EAST, xCurrent+1, yCurrent, zCurrent, MAX_SIZE - xMin, AdvancedRocketry.launchpad);
			xSize = xMin + xMax;
			
			xMin = xCurrent - xMin + 1;
			xMax = xCurrent + xMax;
			
			if(direction.offsetZ > 0) {
				zMax = zCurrent + zSize-1;
			}
			
			if(direction.offsetZ < 0) {
				zMin = zCurrent - zSize+1;
			}
		}

		
		int maxTowerSize = 0;
		//Check perimeter for structureBlocks and get the size
		for(int i = xMin; i < xMax; i++) {
			if(world.getBlock(i, yCurrent, zMin-1) == AdvancedRocketry.structureTower) {
				maxTowerSize = Math.max(maxTowerSize, ZUtils.getContinuousBlockLength(world, ForgeDirection.UP, i, yCurrent, zMin-1, MAX_SIZE_Y, AdvancedRocketry.structureTower));
			}
			
			if(world.getBlock(i, yCurrent, zMax+1) == AdvancedRocketry.structureTower) {
				maxTowerSize = Math.max(maxTowerSize, ZUtils.getContinuousBlockLength(world, ForgeDirection.UP, i, yCurrent, zMax+1, MAX_SIZE_Y, AdvancedRocketry.structureTower));
			}
		}

		for(int i = zMin; i < zMax; i++) {
			if(world.getBlock(xMin-1, yCurrent, i) == AdvancedRocketry.structureTower) {
				maxTowerSize = Math.max(maxTowerSize, ZUtils.getContinuousBlockLength(world, ForgeDirection.UP, xMin-1, yCurrent, i, MAX_SIZE_Y, AdvancedRocketry.structureTower));
			}
			
			if(world.getBlock(xMin+1, yCurrent, i) == AdvancedRocketry.structureTower) {
				maxTowerSize = Math.max(maxTowerSize, ZUtils.getContinuousBlockLength(world, ForgeDirection.UP, xMin+1, yCurrent, i, MAX_SIZE_Y, AdvancedRocketry.structureTower));
			}
		}
		
		if(maxTowerSize < MIN_SIZE_Y || xSize < MIN_SIZE || zSize < MIN_SIZE) {
			return null;
		}
		
		return AxisAlignedBB.getBoundingBox(xMin, yCurrent+1, zMin, xMax, yCurrent + maxTowerSize, zMax);
	}

	public void startBuild(int x, int y, int z) {

	}
}
