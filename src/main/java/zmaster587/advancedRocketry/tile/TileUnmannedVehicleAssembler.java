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
import zmaster587.advancedRocketry.block.*;
import zmaster587.advancedRocketry.dimension.DimensionManager;
import zmaster587.advancedRocketry.entity.EntityStationDeployedRocket;
import zmaster587.advancedRocketry.util.StorageChunk;
import zmaster587.libVulpes.block.BlockFullyRotatable;
import zmaster587.libVulpes.block.RotatableBlock;
import zmaster587.libVulpes.network.PacketEntity;
import zmaster587.libVulpes.network.PacketHandler;
import zmaster587.libVulpes.util.ZUtils;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class TileUnmannedVehicleAssembler extends TileRocketAssemblingMachine {

	private final static int MAX_SIZE = 17, MAX_SIZE_Y = 17, MIN_SIZE = 3, MIN_SIZE_Y = 3;

	/**
	 * Does not make sure the structure is complete, only gets max bounds!
	 * @param world the world
	 * @param pos2 coords to evaluate from
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
		PacketHandler.sendToNearby(new PacketEntity(rocket, (byte)0, nbtdata), rocket.world.provider.getDimension(), this.pos, 64);

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

		int thrustMonopropellant = 0;
		int thrustBipropellant = 0;
		int thrustNuclearNozzleLimit = 0;
		int thrustNuclearReactorLimit = 0;
		int thrustNuclearTotalLimit = 0;
		int monopropellantfuelUse = 0;
		int bipropellantfuelUse = 0;
		int nuclearWorkingFluidUseMax = 0;
		int fuelCapacityMonopropellant = 0;
		int fuelCapacityBipropellant = 0;
		int fuelCapacityOxidizer = 0;
		int fuelCapacityNuclearWorkingFluid = 0;
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
								if (block instanceof BlockNuclearRocketMotor) {
									nuclearWorkingFluidUseMax += ((IRocketEngine) block).getFuelConsumptionRate(world, xCurr, yCurr, zCurr);
									thrustNuclearNozzleLimit += ((IRocketEngine)block).getThrust(world, currPos);
								} else if (block instanceof BlockBipropellantRocketMotor) {
									bipropellantfuelUse += ((IRocketEngine) block).getFuelConsumptionRate(world, xCurr, yCurr, zCurr);
									thrustBipropellant += ((IRocketEngine)block).getThrust(world, currPos);
								} else if (block instanceof BlockRocketMotor) {
									monopropellantfuelUse += ((IRocketEngine) block).getFuelConsumptionRate(world, xCurr, yCurr, zCurr);
									thrustMonopropellant += ((IRocketEngine)block).getThrust(world, currPos);
								}
								stats.addEngineLocation(xCurr - actualMinX - ((actualMaxX - actualMinX)/2f), yCurr - actualMinY, zCurr - actualMinZ - ((actualMaxZ - actualMinZ)/2f));
							}

							if(block instanceof IFuelTank) {
								if (block instanceof BlockFuelTank) {
									fuelCapacityMonopropellant += (((IFuelTank) block).getMaxFill(world, currPos, state) * ARConfiguration.getCurrentConfig().fuelCapacityMultiplier);
								} else if (block instanceof BlockBipropellantFuelTank) {
									fuelCapacityBipropellant += (((IFuelTank) block).getMaxFill(world, currPos, state) * ARConfiguration.getCurrentConfig().fuelCapacityMultiplier);
								} else if(block instanceof BlockOxidizerFuelTank) {
									fuelCapacityOxidizer += (((IFuelTank) block).getMaxFill(world, currPos, state) * ARConfiguration.getCurrentConfig().fuelCapacityMultiplier);
								} else if(block instanceof BlockNuclearFuelTank) {
									fuelCapacityNuclearWorkingFluid += (((IFuelTank) block).getMaxFill(world, currPos, state) * ARConfiguration.getCurrentConfig().fuelCapacityMultiplier);
								}
							}

							if (block instanceof  IRocketNuclearCore) {
								thrustNuclearReactorLimit += ((IRocketNuclearCore) block).getMaxThrust(world, currPos);
							}

							if(block instanceof IIntake) {
								stats.setStatTag("intakePower", (int)stats.getStatTag("intakePower") + ((IIntake)block).getIntakeAmt(state));
							}

							TileEntity tile= world.getTileEntity(currPos);

							if(tile instanceof IFluidHandler) {
								for(IFluidTankProperties info : ((IFluidHandler)tile).getTankProperties())
									fluidCapacity += info.getCapacity();
							}
						}
					}
				}
			}

			int nuclearWorkingFluidUse = 0;
			if (thrustNuclearNozzleLimit > 0) {
				//Only run the number of engines our cores can support - we can't throttle these effectively because they're small, so they shut off if they don't get full power
				thrustNuclearTotalLimit = Math.min(thrustNuclearNozzleLimit, thrustNuclearReactorLimit);
				nuclearWorkingFluidUse = (int) (nuclearWorkingFluidUseMax * (thrustNuclearTotalLimit / (float) thrustNuclearNozzleLimit));
				thrustNuclearTotalLimit = (nuclearWorkingFluidUse * thrustNuclearNozzleLimit) / nuclearWorkingFluidUseMax;
			}

			//Set fuel stats
			//Thrust depending on rocket type
			stats.setBaseFuelRate(FuelType.LIQUID_MONOPROPELLANT, monopropellantfuelUse);
			stats.setBaseFuelRate(FuelType.LIQUID_BIPROPELLANT, bipropellantfuelUse);
			stats.setBaseFuelRate(FuelType.LIQUID_OXIDIZER, bipropellantfuelUse);
			stats.setBaseFuelRate(FuelType.NUCLEAR_WORKING_FLUID, nuclearWorkingFluidUse);
			//Fuel storage depending on rocket type
			stats.setFuelCapacity(FuelType.LIQUID_MONOPROPELLANT, fuelCapacityMonopropellant);
			stats.setFuelCapacity(FuelType.LIQUID_BIPROPELLANT, fuelCapacityBipropellant);
			stats.setFuelCapacity(FuelType.LIQUID_OXIDIZER, fuelCapacityOxidizer);
			stats.setFuelCapacity(FuelType.NUCLEAR_WORKING_FLUID, thrustNuclearTotalLimit);

			//Non-fuel stats
			stats.setThrust(Math.max(thrustMonopropellant, thrustBipropellant));
			stats.setWeight(numBlocks);
			stats.setStatTag("liquidCapacity", fluidCapacity);

			//Total stats, used to check if the user has tried to apply two or more types of thrust/fuel
			int totalFuel = fuelCapacityBipropellant + fuelCapacityNuclearWorkingFluid + fuelCapacityMonopropellant;
			int totalFuelUse = bipropellantfuelUse + nuclearWorkingFluidUse + monopropellantfuelUse;

			//Set status
			if (((fuelCapacityBipropellant > 0 && totalFuel > fuelCapacityBipropellant) || (fuelCapacityMonopropellant > 0 && totalFuel > fuelCapacityMonopropellant) || (fuelCapacityNuclearWorkingFluid > 0 && totalFuel > fuelCapacityNuclearWorkingFluid))
				||
				((thrustBipropellant > 0 && totalFuelUse > bipropellantfuelUse) || (thrustMonopropellant > 0 && totalFuelUse > monopropellantfuelUse) || (thrustNuclearTotalLimit > 0 && totalFuelUse > nuclearWorkingFluidUse)))
				status = ErrorCodes.COMBINEDTHRUST;
			else if(getThrust() < getNeededThrust())
				status = ErrorCodes.NOENGINES;
			else if(((thrustBipropellant > 0) && getFuel(FuelType.LIQUID_BIPROPELLANT) < getNeededFuel(FuelType.LIQUID_BIPROPELLANT)) || ((thrustMonopropellant > 0) && getFuel(FuelType.LIQUID_MONOPROPELLANT) < getNeededFuel(FuelType.LIQUID_MONOPROPELLANT)) || ((thrustNuclearTotalLimit > 0) && getFuel(FuelType.NUCLEAR_WORKING_FLUID) < getNeededFuel(FuelType.NUCLEAR_WORKING_FLUID)))
				status = ErrorCodes.NOFUEL;
			else
				status = ErrorCodes.SUCCESS;
		}
	}

	@Override
	public float getNeededFuel(@Nonnull FuelType fuelType) {
		return getAcceleration(DimensionManager.getInstance().getDimensionProperties(world.provider.getDimension()).getGravitationalMultiplier()) > 0 ? stats.getFuelRate(fuelType) : 0;
	}

	//No additional scanning is needed
	@Override
	protected boolean verifyScan(AxisAlignedBB bb, World world) {
		return true;
	}

}