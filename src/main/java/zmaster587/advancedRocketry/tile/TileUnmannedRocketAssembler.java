package zmaster587.advancedRocketry.tile;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.fluid.Fluids;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import zmaster587.advancedRocketry.api.*;
import zmaster587.advancedRocketry.api.fuel.FuelRegistry.FuelType;
import zmaster587.advancedRocketry.block.*;
import zmaster587.advancedRocketry.dimension.DimensionManager;
import zmaster587.advancedRocketry.entity.EntityStationDeployedRocket;
import zmaster587.advancedRocketry.network.PacketInvalidLocationNotify;
import zmaster587.advancedRocketry.util.StorageChunk;
import zmaster587.libVulpes.block.BlockFullyRotatable;
import zmaster587.libVulpes.block.RotatableBlock;
import zmaster587.libVulpes.network.PacketEntity;
import zmaster587.libVulpes.network.PacketHandler;
import zmaster587.libVulpes.util.HashedBlockPosition;
import zmaster587.libVulpes.util.ZUtils;

public class TileUnmannedRocketAssembler extends TileRocketAssembler {

	private final static int MAX_SIZE = 17, MAX_SIZE_Y = 17, MIN_SIZE = 3, MIN_SIZE_Y = 3;

	public TileUnmannedRocketAssembler()
	{
		super(AdvancedRocketryTileEntityType.TILE_STATION_DEPLOYED_ASSEMBLER);
	}
	
	/**
	 * Does not make sure the structure is complete, only gets max bounds!
	 * @param world the world
	 * @param pos2 coords to evaluate from
	 * @return AxisAlignedBB bounds of structure if valid  otherwise null
	 */
	@Override
	public AxisAlignedBB getRocketPadBounds(World world, BlockPos pos2) {
		Direction direction = RotatableBlock.getFront(world.getBlockState(pos2)).getOpposite();
		int xMin, zMin, xMax, zMax, yMax, yMin;
		int yCurrent = pos2.getY();
		int xCurrent = pos2.getX();
		int zCurrent = pos2.getZ();
		xMax = xMin = xCurrent;
		zMax = zMin = zCurrent;
		int xSize, zSize;

		yMax = ZUtils.getContinuousBlockLength(world, Direction.UP, getPos().add(0,1,0), MAX_SIZE_Y, AdvancedRocketryBlocks.blockStructureTower);

		//Get min and maximum Z/X bounds
		if(direction.getXOffset() != 0) {
			xSize = ZUtils.getContinuousBlockLength(world, direction, pos2.add(0, yMax, 0), MAX_SIZE, AdvancedRocketryBlocks.blockStructureTower);
			zMin = ZUtils.getContinuousBlockLength(world, Direction.NORTH, pos2.add(0, 0, -1), MAX_SIZE, AdvancedRocketryBlocks.blockStructureTower) + 1;
			zMax = ZUtils.getContinuousBlockLength(world, Direction.SOUTH, pos2.add(0, 0, 1), MAX_SIZE - zMin, AdvancedRocketryBlocks.blockStructureTower);
			zSize = zMin + zMax;

			zMin = zCurrent - zMin +1;
			zMax = zCurrent + zMax;

			if(direction.getXOffset() > 0) {
				xMax = xCurrent + xSize - 1;
				xMin++;
			}

			if(direction.getXOffset() < 0) {
				xMin = xCurrent - xSize+1;
				xMax--;
			}
		}
		else {
			zSize = ZUtils.getContinuousBlockLength(world, direction, pos2.add(0, yMax, 0), MAX_SIZE, AdvancedRocketryBlocks.blockStructureTower);
			xMin = ZUtils.getContinuousBlockLength(world, Direction.WEST, pos2.add(-1, 0, 0), MAX_SIZE, AdvancedRocketryBlocks.blockStructureTower) + 1;
			xMax = ZUtils.getContinuousBlockLength(world, Direction.EAST, pos2.add(1, 0, 0), MAX_SIZE - xMin, AdvancedRocketryBlocks.blockStructureTower);
			xSize = xMin + xMax;

			xMin = xCurrent - xMin +1;
			xMax = xCurrent + xMax;


			if(direction.getZOffset() > 0) {
				zMax = zCurrent + zSize - 1;
				zMin++;
			}

			if(direction.getZOffset() < 0) {
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
		rocket.launchDirection = Direction.DOWN;

		//Change engine direction
		for(int x = 0; x < storageChunk.getSizeX(); x++) {
			for(int y = 0; y < storageChunk.getSizeY(); y++) {
				for(int z = 0; z < storageChunk.getSizeZ(); z++) {

					BlockPos pos3 = new BlockPos(x,y,z);
					if(storageChunk.getBlockState(pos3).getBlock() instanceof BlockGenericRocketMotor ) {
						storageChunk.setBlockState(pos3, storageChunk.getBlockState(pos3).with(BlockFullyRotatable.FACING, rocket.forwardDirection)  );
					}
				}		
			}	
		}

		world.addEntity(rocket);
		CompoundNBT nbtdata = new CompoundNBT();

		rocket.writeUnlessRemoved(nbtdata);
		PacketHandler.sendToNearby(new PacketEntity(rocket, (byte)0, nbtdata), rocket.world, this.pos, 64);

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
		int thrustNuclearCoreLimit = 0;
		int thrustNuclearTotalLimit = 0;
		int fuelUse = 0;
		int fuelCapacity = 0;
		FuelType rocketType = null;
		FuelType currentType = null;
		int numBlocks = 0;
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

		boolean hasGuidance = false;
		boolean invalidBlock = false;

		if(verifyScan(bb, world)) {
			for(int yCurr = (int) bb.minY; yCurr <= bb.maxY; yCurr++) {
				for(int xCurr = (int) bb.minX; xCurr <= bb.maxX; xCurr++) {
					for(int zCurr = (int) bb.minZ; zCurr <= bb.maxZ; zCurr++) {

						BlockPos currBlockPos = new BlockPos(xCurr, yCurr, zCurr);
						BlockPos behindPos = currBlockPos.offset(world.getBlockState(this.getPos()).get(BlockFullyRotatable.FACING).getOpposite());

						if(!world.isAirBlock(currBlockPos)) {
							BlockState state = world.getBlockState(currBlockPos);
							Block block = state.getBlock();

							//Check blacklist
							if(ARConfiguration.getCurrentConfig().blackListRocketBlocks.contains(block)) {
								if(!block.isReplaceable( state, Fluids.WATER)) {
									invalidBlock = true;
									if(!world.isRemote)
										PacketHandler.sendToNearby(new PacketInvalidLocationNotify(new HashedBlockPosition(xCurr, yCurr, zCurr)), ZUtils.getDimensionIdentifier(world), getPos(), 64);
								}
								continue;
							}
							numBlocks++;

							//Check fuel-type determining blocks
							if(block instanceof IRocketEngine && (world.getBlockState(behindPos).getBlock().isAir(world.getBlockState(behindPos), world, behindPos) || world.getBlockState(behindPos).getBlock() instanceof BlockLandingPad || world.getBlockState(behindPos).getBlock() == AdvancedRocketryBlocks.blockLaunchpad)) {
								fuelUse += ((IRocketEngine) block).getFuelConsumptionRate(world, xCurr, yCurr, zCurr);
								thrust += ((IRocketEngine)block).getThrust(world, currBlockPos);
								currentType = ((IRocketEngine) block).getFuelType(world, currBlockPos);
								stats.addEngineLocation(xCurr - actualMinX - ((actualMaxX - actualMinX)/2f), yCurr - actualMinY, zCurr - actualMinZ - ((actualMaxZ - actualMinZ)/2f));
							} else if(block instanceof IFuelTank) {
								fuelCapacity += (((IFuelTank) block).getMaxFill(world, currBlockPos, state) * ARConfiguration.getCurrentConfig().fuelCapacityMultiplier.get());
								currentType = ((IFuelTank) block).getFuelType(world, currBlockPos);
							} else if (block instanceof IRocketNuclearCore && ((world.getBlockState(behindPos).getBlock() instanceof  IRocketNuclearCore) || (world.getBlockState(behindPos).getBlock() instanceof  IRocketEngine))) {
								thrustNuclearCoreLimit += ((IRocketNuclearCore) block).getMaxThrust(world, currBlockPos);
							}

							if (rocketType != null && currentType != null && currentType != rocketType) {
								status = ErrorCodes.COMBINEDTHRUST;
								return;
							} else
								rocketType = currentType;

							if(block instanceof IIntake) {
								stats.setStatTag("intakePower", (int)stats.getStatTag("intakePower") + ((IIntake)block).getIntakeAmt(state));
							}

							TileEntity tile= world.getTileEntity(currBlockPos);
							if(tile instanceof TileGuidanceComputer) hasGuidance = true;
						}
					}
				}
			}

			int nuclearWorkingFluidUse = 0;
			if (thrust > 0) {
				//Only run the number of engines our cores can support - we can't throttle these effectively because they're small, so they shut off if they don't get full power
				thrustNuclearTotalLimit = Math.min(thrust, thrustNuclearCoreLimit);
				nuclearWorkingFluidUse = (int) (fuelUse * (thrustNuclearTotalLimit / (float) thrust));
				thrustNuclearTotalLimit = (nuclearWorkingFluidUse * thrust) / fuelUse;

				fuelUse = nuclearWorkingFluidUse;
				thrust = thrustNuclearTotalLimit;
			}

			//Set fuel stats
			if (rocketType != null) {
				stats.setBaseFuelRate(rocketType, fuelUse);
				stats.setFuelCapacity(rocketType, fuelCapacity);
			}
			//Non-fuel stats
			stats.setWeight(numBlocks);
			stats.setThrust(thrust);

			//Set status
			if(invalidBlock)
				status = ErrorCodes.INVALIDBLOCK;
		    else if(!hasGuidance)
				status = ErrorCodes.NOGUIDANCE;
			else if(getThrust() <= getNeededThrust())
				status = ErrorCodes.NOENGINES;
			else if(rocketType != null && getFuel(rocketType) < getNeededFuel(rocketType))
				status = ErrorCodes.NOFUEL;
			else
				status = ErrorCodes.SUCCESS;
		}
	}

	@Override
	public float getNeededFuel(FuelType fuelType) {
		return getAcceleration(DimensionManager.getInstance().getDimensionProperties(world).getGravitationalMultiplier()) > 0 ? stats.getFuelRate(fuelType) : 0;
	}

	//No additional scanning is needed
	@Override
	protected boolean verifyScan(AxisAlignedBB bb, World world) {
		return true;
	}

}