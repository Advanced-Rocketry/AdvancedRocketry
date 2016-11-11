package zmaster587.advancedRocketry.tile;

import net.minecraft.block.Block;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.FluidTankInfo;
import net.minecraftforge.fluids.IFluidHandler;
import zmaster587.advancedRocketry.api.AdvancedRocketryBlocks;
import zmaster587.advancedRocketry.api.IFuelTank;
import zmaster587.advancedRocketry.api.IInfrastructure;
import zmaster587.advancedRocketry.api.IIntake;
import zmaster587.advancedRocketry.api.IMiningDrill;
import zmaster587.advancedRocketry.api.IRocketEngine;
import zmaster587.advancedRocketry.api.fuel.FuelRegistry.FuelType;
import zmaster587.advancedRocketry.block.BlockRocketMotor;
import zmaster587.advancedRocketry.block.BlockSeat;
import zmaster587.advancedRocketry.entity.EntityRocket;
import zmaster587.advancedRocketry.entity.EntityStationDeployedRocket;
import zmaster587.advancedRocketry.tile.TileRocketBuilder.ErrorCodes;
import zmaster587.advancedRocketry.tile.hatch.TileSatelliteHatch;
import zmaster587.advancedRocketry.util.StorageChunk;
import zmaster587.libVulpes.block.RotatableBlock;
import zmaster587.libVulpes.interfaces.INetworkEntity;
import zmaster587.libVulpes.network.PacketEntity;
import zmaster587.libVulpes.network.PacketHandler;
import zmaster587.libVulpes.util.ZUtils;

public class TileStationDeployedAssembler extends TileRocketBuilder {

	private final static int MAX_SIZE = 12, MAX_SIZE_Y = 12, MIN_SIZE = 3, MIN_SIZE_Y = 3;

	/**
	 * Does not make sure the structure is complete, only gets max bounds!
	 * @param world the world
	 * @param x coord to evaluate from
	 * @param y coord to evaluate from
	 * @param z coord to evaluate from
	 * @return AxisAlignedBB bounds of structure if valid  otherwise null
	 */
	public AxisAlignedBB getRocketPadBounds(World world,int x, int y, int z) {
		ForgeDirection direction = RotatableBlock.getFront(world.getBlockMetadata(x, y, z)).getOpposite();
		int xMin, zMin, xMax, zMax, yMax, yMin;
		int yCurrent = y;
		int xCurrent = x;
		int zCurrent = z;
		xMax = xMin = xCurrent;
		zMax = zMin = zCurrent;
		int xSize, zSize;

		yMax = ZUtils.getContinuousBlockLength(world, ForgeDirection.UP, this.xCoord, this.yCoord + 1, this.zCoord, MAX_SIZE_Y, AdvancedRocketryBlocks.blockStructureTower);

		//Get min and maximum Z/X bounds
		if(direction.offsetX != 0) {
			xSize = ZUtils.getContinuousBlockLength(world, direction, xCurrent, yCurrent + yMax, zCurrent, MAX_SIZE, AdvancedRocketryBlocks.blockStructureTower);
			zMin = ZUtils.getContinuousBlockLength(world, ForgeDirection.NORTH, xCurrent, yCurrent, zCurrent-1, MAX_SIZE, AdvancedRocketryBlocks.blockStructureTower) + 1;
			zMax = ZUtils.getContinuousBlockLength(world, ForgeDirection.SOUTH, xCurrent, yCurrent, zCurrent+1, MAX_SIZE - zMin, AdvancedRocketryBlocks.blockStructureTower);
			zSize = zMin + zMax;

			zMin = zCurrent - zMin +1;
			zMax = zCurrent + zMax;

			if(direction.offsetX > 0) {
				xMax = xCurrent + xSize - 1;
				xMin++;
			}

			if(direction.offsetX < 0) {
				xMin = xCurrent - xSize+1;
				xMax--;
			}
		}
		else {
			zSize = 1+ ZUtils.getContinuousBlockLength(world, direction, xCurrent, yCurrent + yMax, zCurrent, MAX_SIZE, AdvancedRocketryBlocks.blockStructureTower);
			xMin = ZUtils.getContinuousBlockLength(world, ForgeDirection.WEST, xCurrent - 1, yCurrent, zCurrent, MAX_SIZE, AdvancedRocketryBlocks.blockStructureTower) + 1;
			xMax = ZUtils.getContinuousBlockLength(world, ForgeDirection.EAST, xCurrent + 1, yCurrent, zCurrent, MAX_SIZE - xMin, AdvancedRocketryBlocks.blockStructureTower);
			xSize = xMin + xMax;

			xMin = xCurrent - xMin +1;
			xMax = xCurrent + xMax;


			if(direction.offsetZ > 0) {
				zMax = zCurrent + zSize + 1;
				zMin++;
			}

			if(direction.offsetZ < 0) {
				zMin = zCurrent - zSize-1;
				zMax --;
			}
		}

		//if tower does not meet criteria then reutrn null
		if(yMax < MIN_SIZE_Y || xSize < MIN_SIZE || zSize < MIN_SIZE) {
			return null;
		}

		return AxisAlignedBB.getBoundingBox(xMin, yCurrent, zMin, xMax, yCurrent + yMax - 1, zMax);
	}

	public void assembleRocket() {

		if(bbCache == null || worldObj.isRemote)
			return;
		//Need to scan again b/c something may have changed
		scanRocket(worldObj, xCoord, yCoord, zCoord, bbCache);

		if(status != ErrorCodes.SUCCESS)
			return;

		StorageChunk storageChunk = StorageChunk.cutWorldBB(worldObj, bbCache);


		EntityStationDeployedRocket rocket = new EntityStationDeployedRocket(worldObj, storageChunk, stats.copy(),bbCache.minX + (bbCache.maxX-bbCache.minX)/2f +.5f, yCoord , bbCache.minZ + (bbCache.maxZ-bbCache.minZ)/2f +.5f);

		//TODO: setRocketDirection
		rocket.forwardDirection = RotatableBlock.getFront(this.blockMetadata).getOpposite();
		rocket.launchDirection = ForgeDirection.DOWN;

		//Change engine direction
		for(int x = 0; x < storageChunk.getSizeX(); x++) {
			for(int y = 0; y < storageChunk.getSizeY(); y++) {
				for(int z = 0; z < storageChunk.getSizeZ(); z++) {

					if(storageChunk.getBlock(x, y, z) instanceof BlockRocketMotor) {
						storageChunk.setBlockMeta(x, y, z, rocket.forwardDirection.ordinal());
						storageChunk.getTileEntity(x, y, z).blockMetadata = rocket.forwardDirection.ordinal();
						((TileModelRenderRotatable)storageChunk.getTileEntity(x, y, z)).rotation = rocket.forwardDirection;
					}
				}		
			}	
		}

		worldObj.spawnEntityInWorld(rocket);
		NBTTagCompound nbtdata = new NBTTagCompound();

		rocket.writeToNBT(nbtdata);
		PacketHandler.sendToNearby(new PacketEntity((INetworkEntity)rocket, (byte)0, nbtdata), rocket.worldObj.provider.dimensionId, xCoord, yCoord, zCoord, 64);

		stats.reset();
		this.status = ErrorCodes.UNSCANNED;
		this.markDirty();
		this.worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);

		for(IInfrastructure infrastructure : getConnectedInfrastructure()) {
			rocket.linkInfrastructure(infrastructure);
		}
	}


	//TODO get direction of rocket
	@Override
	public void scanRocket(World world, int x, int y, int z, AxisAlignedBB bb) {

		int thrust = 0;
		int fuelUse = 0;
		int fuel = 0;
		int numBlocks = 0;
		float drillPower = 0f;
		stats.reset();

		int actualMinX = (int)bb.maxX,
				actualMinY = (int)bb.maxY,
				actualMinZ = (int)bb.maxZ,
				actualMaxX = (int)bb.minX,
				actualMaxY = (int)bb.minY,
				actualMaxZ = (int)bb.minZ;


		for(int xCurr = (int)bb.minX; xCurr <= bb.maxX; xCurr++) {
			for(int zCurr = (int)bb.minZ; zCurr <= bb.maxZ; zCurr++) {
				for(int yCurr = (int)bb.minY; yCurr<= bb.maxY; yCurr++) {

					Block block = world.getBlock(xCurr, yCurr, zCurr);

					if(!block.isAir(world, xCurr, yCurr, zCurr)) {
						if(xCurr < actualMinX)
							actualMinX = xCurr;
						if(yCurr < actualMinY)
							actualMinY = yCurr;
						if(zCurr < actualMinZ)
							actualMinZ = zCurr;
						if(xCurr > actualMaxX)
							actualMaxX = xCurr;
						if(yCurr > actualMaxY)
							actualMaxY = yCurr;
						if(zCurr > actualMaxZ)
							actualMaxZ = zCurr;
					}
				}
			}
		}

		boolean hasSatellite = false;
		boolean hasGuidance = false;
		int fluidCapacity = 0;

		if(verifyScan(bb, world)) {
			for(int yCurr = (int) bb.minY; yCurr <= bb.maxY; yCurr++) {
				for(int xCurr = (int) bb.minX; xCurr <= bb.maxX; xCurr++) {
					for(int zCurr = (int) bb.minZ; zCurr <= bb.maxZ; zCurr++) {

						if(!world.isAirBlock(xCurr, yCurr, zCurr)) {
							Block block = world.getBlock(xCurr, yCurr, zCurr);
							int meta = world.getBlockMetadata(xCurr, yCurr, zCurr);
							numBlocks++;
							//If rocketEngine increaseThrust
							if(block instanceof IRocketEngine) {
								thrust += ((IRocketEngine)block).getThrust(world, xCurr, yCurr, z);
								fuelUse += ((IRocketEngine)block).getFuelConsumptionRate(world, xCurr, yCurr, zCurr);
								stats.addEngineLocation(xCurr - actualMinX - ((actualMaxX - actualMinX)/2f), yCurr - actualMinY, zCurr - actualMinZ - ((actualMaxZ - actualMinZ)/2f));
							}

							if(block instanceof IFuelTank) {
								fuel+= ((IFuelTank)block).getMaxFill(world, xCurr, yCurr, zCurr, world.getBlockMetadata(xCurr, yCurr, zCurr));
							}

							if(block instanceof BlockSeat) {

								if(stats.hasSeat()) 
									stats.addPassengerSeat((int)(xCurr - actualMinX - ((actualMaxX - actualMinX)/2f)) , (int)(yCurr  -actualMinY), (int)(zCurr - actualMinZ - ((actualMaxZ - actualMinZ)/2f)));
								else
									stats.setSeatLocation((int)(xCurr - actualMinX - ((actualMaxX - actualMinX)/2f)) , (int)(yCurr  -actualMinY), (int)(zCurr - actualMinZ - ((actualMaxZ - actualMinZ)/2f)));
							}

							if(block instanceof IMiningDrill) {
								drillPower += ((IMiningDrill)block).getMiningSpeed(world, xCurr, yCurr, zCurr);
							}
							
							if(block instanceof IIntake) {
								stats.setStatTag("intakePower", (int)stats.getStatTag("intakePower") + ((IIntake)block).getIntakeAmt(meta));
							}

							TileEntity tile= world.getTileEntity(xCurr, yCurr, zCurr);
							if(tile instanceof TileSatelliteHatch)
								hasSatellite = true;
							if(tile instanceof TileGuidanceComputer)
								hasGuidance = true;
							
							if(tile instanceof IFluidHandler) {
								for(FluidTankInfo info : ((IFluidHandler)tile).getTankInfo(ForgeDirection.UNKNOWN))
									fluidCapacity += info.capacity;
							}
						}
					}
				}
			}
			stats.setFuelRate(FuelType.LIQUID,fuelUse);
			stats.setWeight(numBlocks);
			stats.setThrust(thrust);
			stats.setFuelCapacity(FuelType.LIQUID,fuel);
			stats.setDrillingPower(drillPower);
			stats.setStatTag("liquidCapacity", fluidCapacity);

			//Set status
			//TODO: warn if seat OR satellite missing
			//if(!stats.hasSeat() && !hasSatellite) 
			//status = ErrorCodes.NOSEAT;
			/*else*/
			if(getFuel() < getNeededFuel()*(1 + fluidCapacity/1000)) 
				status = ErrorCodes.NOFUEL;
			else if(getThrust() < getNeededThrust()) 
				status = ErrorCodes.NOENGINES;
			else
				status = ErrorCodes.SUCCESS;
		}
	}
	
	@Override
	public float getNeededFuel() {
		return getAcceleration() > 0 ? stats.getFuelRate(FuelType.LIQUID) : 0;
	}

	//No additional scanning is needed
	@Override
	protected boolean verifyScan(AxisAlignedBB bb, World world) {
		return true;
	}

}