package zmaster587.advancedRocketry.tile;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.IFluidTankProperties;
import zmaster587.advancedRocketry.api.*;
import zmaster587.advancedRocketry.api.fuel.FuelRegistry.FuelType;
import zmaster587.advancedRocketry.block.BlockRocketMotor;
import zmaster587.advancedRocketry.block.BlockSeat;
import zmaster587.advancedRocketry.entity.EntityStationDeployedRocket;
import zmaster587.advancedRocketry.tile.hatch.TileSatelliteHatch;
import zmaster587.advancedRocketry.util.StorageChunk;
import zmaster587.libVulpes.block.BlockFullyRotatable;
import zmaster587.libVulpes.block.RotatableBlock;
import zmaster587.libVulpes.interfaces.INetworkEntity;
import zmaster587.libVulpes.network.PacketEntity;
import zmaster587.libVulpes.network.PacketHandler;
import zmaster587.libVulpes.util.ZUtils;

public class TileUnmannedVehicleAssembler extends TileRocketAssemblingMachine {

	private final static int MAX_SIZE = 17, MAX_SIZE_Y = 17, MIN_SIZE = 3, MIN_SIZE_Y = 3;

	/**
	 * Does not make sure the structure is complete, only gets max bounds!
	 * @param world the world
	 * @param x coord to evaluate from
	 * @param y coord to evaluate from
	 * @param z coord to evaluate from
	 * @return AxisAlignedBB bounds of structure if valid  otherwise null
	 */
	@Override
	public AxisAlignedBB getRocketPadBounds(World world, BlockPos pos2) {
		EnumFacing direction = RotatableBlock.getFront(world.getBlockState(pos2)).getOpposite();
		int xMin, zMin, xMax, zMax, yMax, yMin;
		int yCurrent = pos2.getY();
		int xCurrent = pos2.getX();
		int zCurrent = pos2.getZ();
		xMax = xMin = xCurrent;
		zMax = zMin = zCurrent;
		int xSize, zSize;

		yMax = ZUtils.getContinuousBlockLength(world, EnumFacing.UP, getPos().add(0,1,0), MAX_SIZE_Y, AdvancedRocketryBlocks.blockStructureTower);

		//Get min and maximum Z/X bounds
		if(direction.getFrontOffsetX() != 0) {
			xSize = ZUtils.getContinuousBlockLength(world, direction, pos2.add(0, yMax, 0), MAX_SIZE, AdvancedRocketryBlocks.blockStructureTower);
			zMin = ZUtils.getContinuousBlockLength(world, EnumFacing.NORTH, pos2.add(0, 0, -1), MAX_SIZE, AdvancedRocketryBlocks.blockStructureTower) + 1;
			zMax = ZUtils.getContinuousBlockLength(world, EnumFacing.SOUTH, pos2.add(0, 0, 1), MAX_SIZE - zMin, AdvancedRocketryBlocks.blockStructureTower);
			zSize = zMin + zMax;

			zMin = zCurrent - zMin +1;
			zMax = zCurrent + zMax;

			if(direction.getFrontOffsetX() > 0) {
				xMax = xCurrent + xSize - 1;
				xMin++;
			}

			if(direction.getFrontOffsetX() < 0) {
				xMin = xCurrent - xSize+1;
				xMax--;
			}
		}
		else {
			zSize = ZUtils.getContinuousBlockLength(world, direction, pos2.add(0, yMax, 0), MAX_SIZE, AdvancedRocketryBlocks.blockStructureTower);
			xMin = ZUtils.getContinuousBlockLength(world, EnumFacing.WEST, pos2.add(-1, 0, 0), MAX_SIZE, AdvancedRocketryBlocks.blockStructureTower) + 1;
			xMax = ZUtils.getContinuousBlockLength(world, EnumFacing.EAST, pos2.add(1, 0, 0), MAX_SIZE - xMin, AdvancedRocketryBlocks.blockStructureTower);
			xSize = xMin + xMax;

			xMin = xCurrent - xMin +1;
			xMax = xCurrent + xMax;


			if(direction.getFrontOffsetZ() > 0) {
				zMax = zCurrent + zSize - 1;
				zMin++;
			}

			if(direction.getFrontOffsetZ() < 0) {
				zMin = zCurrent - zSize+1;
				zMax --;
			}
		}

		//if tower does not meet criteria then reutrn null
		if(yMax < MIN_SIZE_Y || xSize < MIN_SIZE || zSize < MIN_SIZE) {
			return null;
		}

		return new AxisAlignedBB(xMin, yCurrent, zMin, xMax, yCurrent + yMax - 1, zMax);
	}

	public void assembleRocket() {

		if(bbCache == null || world.isRemote)
			return;
		//Need to scan again b/c something may have changed
		scanRocket(world, getPos(), bbCache);

		if(status != ErrorCodes.SUCCESS)
			return;
		StorageChunk storageChunk;

		//Breaks if nothing is there
		try {
			storageChunk = StorageChunk.cutWorldBB(world, bbCache);
		} catch(NegativeArraySizeException e) {
			return;
		}


		EntityStationDeployedRocket rocket = new EntityStationDeployedRocket(world, storageChunk, stats.copy(),bbCache.minX + (bbCache.maxX-bbCache.minX)/2f +.5f, getPos().getY() , bbCache.minZ + (bbCache.maxZ-bbCache.minZ)/2f +.5f);

		//TODO: setRocketDirection
		rocket.forwardDirection = RotatableBlock.getFront(world.getBlockState(getPos())).getOpposite();
		rocket.launchDirection = EnumFacing.DOWN;

		//Change engine direction
		for(int x = 0; x < storageChunk.getSizeX(); x++) {
			for(int y = 0; y < storageChunk.getSizeY(); y++) {
				for(int z = 0; z < storageChunk.getSizeZ(); z++) {

					BlockPos pos3 = new BlockPos(x,y,z);
					if(storageChunk.getBlockState(pos3).getBlock() instanceof BlockRocketMotor ) {
						storageChunk.setBlockState(pos3, storageChunk.getBlockState(pos3).withProperty(BlockFullyRotatable.FACING, rocket.forwardDirection)  );
					}
				}		
			}	
		}

		world.spawnEntity(rocket);
		NBTTagCompound nbtdata = new NBTTagCompound();

		rocket.writeToNBT(nbtdata);
		PacketHandler.sendToNearby(new PacketEntity((INetworkEntity)rocket, (byte)0, nbtdata), rocket.world.provider.getDimension(), this.pos, 64);

		stats.reset();
		this.status = ErrorCodes.UNSCANNED;
		this.markDirty();

		for(IInfrastructure infrastructure : getConnectedInfrastructure()) {
			rocket.linkInfrastructure(infrastructure);
		}
	}


	//TODO get direction of rocket
	@Override
	public void scanRocket(World world, BlockPos pos2, AxisAlignedBB bb) {

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

					BlockPos currPos = new BlockPos(xCurr, yCurr, zCurr);

					if(!world.isAirBlock(currPos)) {
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

						BlockPos currPos = new BlockPos(xCurr, yCurr, zCurr);
						if(!world.isAirBlock(currPos)) {
							IBlockState state = world.getBlockState(currPos);
							Block block = state.getBlock();
							numBlocks++;
							//If rocketEngine increaseThrust
							if(block instanceof IRocketEngine) {
								thrust += ((IRocketEngine)block).getThrust(world, currPos);
								fuelUse += ((IRocketEngine)block).getFuelConsumptionRate(world, xCurr, yCurr, zCurr);
								stats.addEngineLocation(xCurr - actualMinX - ((actualMaxX - actualMinX)/2f), yCurr - actualMinY, zCurr - actualMinZ - ((actualMaxZ - actualMinZ)/2f));
							}

							if(block instanceof IFuelTank) {
								fuel+= ((IFuelTank)block).getMaxFill(world, currPos, state);
							}

							if(block instanceof BlockSeat) {

								if(stats.hasSeat()) 
									stats.addPassengerSeat((int)(xCurr - actualMinX - ((actualMaxX - actualMinX)/2f)) , (int)(yCurr  -actualMinY), (int)(zCurr - actualMinZ - ((actualMaxZ - actualMinZ)/2f)));
								else
									stats.setSeatLocation((int)(xCurr - actualMinX - ((actualMaxX - actualMinX)/2f)) , (int)(yCurr  -actualMinY), (int)(zCurr - actualMinZ - ((actualMaxZ - actualMinZ)/2f)));
							}

							if(block instanceof IMiningDrill) {
								drillPower += ((IMiningDrill)block).getMiningSpeed(world, currPos);
							}

							if(block instanceof IIntake) {
								stats.setStatTag("intakePower", (int)stats.getStatTag("intakePower") + ((IIntake)block).getIntakeAmt(state));
							}

							TileEntity tile= world.getTileEntity(currPos);
							if(tile instanceof TileSatelliteHatch)
								hasSatellite = true;
							if(tile instanceof TileGuidanceComputer)
								hasGuidance = true;

							if(tile instanceof IFluidHandler) {
								for(IFluidTankProperties info : ((IFluidHandler)tile).getTankProperties())
									fluidCapacity += info.getCapacity();
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