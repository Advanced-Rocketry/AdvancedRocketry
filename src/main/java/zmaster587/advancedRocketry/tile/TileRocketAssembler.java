package zmaster587.advancedRocketry.tile;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.fluid.Fluids;
import net.minecraft.inventory.container.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Util;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.api.distmarker.Dist;
import zmaster587.advancedRocketry.AdvancedRocketry;
import zmaster587.advancedRocketry.api.*;
import zmaster587.advancedRocketry.api.RocketEvent.RocketLandedEvent;
import zmaster587.advancedRocketry.api.fuel.FuelRegistry.FuelType;
import zmaster587.advancedRocketry.block.*;
import zmaster587.advancedRocketry.dimension.DimensionManager;
import zmaster587.advancedRocketry.entity.EntityRocket;
import zmaster587.advancedRocketry.network.PacketInvalidLocationNotify;
import zmaster587.advancedRocketry.tile.satellite.TileSatelliteBay;
import zmaster587.advancedRocketry.util.StorageChunk;
import zmaster587.libVulpes.LibVulpes;
import zmaster587.libVulpes.api.LibvulpesGuiRegistry;
import zmaster587.libVulpes.block.RotatableBlock;
import zmaster587.libVulpes.client.util.ProgressBarImage;
import zmaster587.libVulpes.interfaces.ILinkableTile;
import zmaster587.libVulpes.inventory.ContainerModular;
import zmaster587.libVulpes.inventory.GuiHandler;
import zmaster587.libVulpes.inventory.GuiHandler.guiId;
import zmaster587.libVulpes.inventory.modules.*;
import zmaster587.libVulpes.items.ItemLinker;
import zmaster587.libVulpes.network.PacketEntity;
import zmaster587.libVulpes.network.PacketHandler;
import zmaster587.libVulpes.network.PacketMachine;
import zmaster587.libVulpes.tile.IMultiblock;
import zmaster587.libVulpes.tile.TileEntityFEConsumer;
import zmaster587.libVulpes.util.HashedBlockPosition;
import zmaster587.libVulpes.util.INetworkMachine;
import zmaster587.libVulpes.util.IconResource;
import zmaster587.libVulpes.util.ZUtils;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 * Purpose: validate the rocket structure as well as give feedback to the player as to what needs to be
 * changed to complete the rocket structure
 * Also will be used to "build" the rocket components from the placed frames, control fuel flow etc
 **/
public class TileRocketAssembler extends TileEntityFEConsumer implements IButtonInventory, INetworkMachine, IDataSync, IModularInventory, IProgressBar, ILinkableTile {

	private final static int MAXSCANDELAY = 10;
	private final static int ENERGYFOROP = 100;
	private final static int MAX_SIZE = 16;
	private final static int MAX_SIZE_Y = 64;
	private final static int MIN_SIZE = 3;
	private final static int MIN_SIZE_Y = 4;

	protected static final ResourceLocation backdrop =  new ResourceLocation("advancedrocketry","textures/gui/rocketbuilder.png");
	private static final ProgressBarImage horizontalProgressBar = new ProgressBarImage(89, 9, 81, 17, 176, 0, 80, 15, 0, 2, Direction.EAST, backdrop);
	protected static final ProgressBarImage verticalProgressBar = new ProgressBarImage(76, 93, 8, 52, 176, 15, 2, 38, 3, 2, Direction.UP, backdrop);

	private ModuleText thrustText, weightText, fuelText, accelerationText;
	protected ModuleText errorText;

	private int totalProgress;
	private int progress; // How long until scan is finished from 0 -> num blocks
	private int prevProgress; // Used for client/server sync
	private boolean building; //True is rocket is being built, false if only scanning or otherwise

	private int lastRocketID;
	protected StatsRocket stats;
	protected AxisAlignedBB bbCache;
	protected ErrorCodes status;
	
	private static final Block[] viableBlocks = {AdvancedRocketryBlocks.blockLaunchpad, AdvancedRocketryBlocks.blockLandingPad};

	private List<HashedBlockPosition> blockPos;

	protected enum ErrorCodes {
		SUCCESS(LibVulpes.proxy.getLocalizedString("msg.rocketbuilder.success")),
		NOFUEL(LibVulpes.proxy.getLocalizedString("msg.rocketbuilder.nofuel")),
		NOSEAT(LibVulpes.proxy.getLocalizedString("msg.rocketbuilder.noseat")),
		NOENGINES(LibVulpes.proxy.getLocalizedString("msg.rocketbuilder.noengines")),
		NOGUIDANCE(LibVulpes.proxy.getLocalizedString("msg.rocketbuilder.noguidance")),
		UNSCANNED(LibVulpes.proxy.getLocalizedString("msg.rocketbuilder.unscanned")),
		SUCCESS_STATION(LibVulpes.proxy.getLocalizedString("msg.rocketbuilder.success_station")),
		EMPTY(LibVulpes.proxy.getLocalizedString("msg.rocketbuilder.empty")),
		FINISHED(LibVulpes.proxy.getLocalizedString("msg.rocketbuilder.finished")),
		INCOMPLETESTRCUTURE(LibVulpes.proxy.getLocalizedString("msg.rocketbuilder.incompletestructure")),
		NOSATELLITEHATCH(LibVulpes.proxy.getLocalizedString("msg.rocketbuilder.nosatellitehatch")),
		NOSATELLITECHIP(LibVulpes.proxy.getLocalizedString("msg.rocketbuilder.nosatellitechip")),
		OUTPUTBLOCKED(LibVulpes.proxy.getLocalizedString("msg.rocketbuilder.outputblocked")),
		INVALIDBLOCK(LibVulpes.proxy.getLocalizedString("msg.rocketbuild.invalidblock")),
		COMBINEDTHRUST(LibVulpes.proxy.getLocalizedString("msg.rocketbuild.combinedthrust"));
		
		String code;
		ErrorCodes(String code) {
			this.code = code;
		}

		public String getErrorCode() { return code; } 
	}

	public TileRocketAssembler() {
		this(AdvancedRocketryTileEntityType.TILE_ROCKET_ASSEMBLER);
	}
	
	public TileRocketAssembler(TileEntityType<?> type) {
		super(type, 100000);

		blockPos = new LinkedList<>();

		status = ErrorCodes.UNSCANNED;
		stats = new StatsRocket();
		building = false;
		prevProgress = 0;
		MinecraftForge.EVENT_BUS.register(this);
	}

	@Override
	public void remove() {
		super.remove();
		MinecraftForge.EVENT_BUS.unregister(this);
		for(HashedBlockPosition pos : blockPos) {
			TileEntity tile = world.getTileEntity(pos.getBlockPos());

			if(tile instanceof IMultiblock)
				((IMultiblock)tile).setIncomplete();
		}
	}

	@Override
	public void onChunkUnloaded() {
		super.onChunkUnloaded();
		MinecraftForge.EVENT_BUS.unregister(this);
	}

	public ErrorCodes getStatus() {
		return status;
	}
	public StatsRocket getRocketStats() { return stats; }

	public AxisAlignedBB getBBCache() { return bbCache;}

	public int getTotalProgress() { return totalProgress; }
	public void setTotalProgress(int scanTotalBlocks) { this.totalProgress = scanTotalBlocks; }

	public int getProgress() { return progress; }
	public void setProgress(int scanTime) { this.progress = scanTime; }

	public double getNormallizedProgress() {
		return progress/(double)(totalProgress*MAXSCANDELAY);
	}

	public float getAcceleration( float gravitationalMultiplier) {
		return stats.getAcceleration(gravitationalMultiplier);
	}

	public int getWeight()  { return stats.getWeight(); }

	public int getThrust() { return stats.getThrust(); }

	public float getNeededThrust() {return getWeight();}

	public float getNeededFuel(FuelType fuelType) { return getAcceleration( getGravityMultiplier() ) > 0 ? 2*stats.getBaseFuelRate(fuelType)*MathHelper.sqrt((2*(ARConfiguration.getCurrentConfig().orbit.get()-this.getPos().getY()))/getAcceleration(getGravityMultiplier())) : 0; }

	public float getGravityMultiplier () { return DimensionManager.getInstance().getDimensionProperties(ZUtils.getDimensionIdentifier(world)).getGravitationalMultiplier(); }

	public int getFuel(FuelType fuelType) {return (int) (stats.getFuelCapacity(fuelType)*ARConfiguration.getCurrentConfig().fuelCapacityMultiplier.get());}

	public boolean isBuilding() { return building; }

	public void setStatus(int value) {
		status = ErrorCodes.values()[value];
	}

	@Override
	public int getPowerPerOperation() {
		return ENERGYFOROP;
	}

	@Override
	public void performFunction() {
		if(progress >= (totalProgress*MAXSCANDELAY)) {
			if(!world.isRemote) {
				if(building)
					assembleRocket();
				else
					scanRocket(world, pos, bbCache);
			}
			totalProgress = -1;
			progress = 0;
			prevProgress = 0;
			building = false; //Done building

			//TODO call function instead
			if(thrustText != null)
				updateText();
		}

		progress++;

		if(!this.world.isRemote && this.energy.getUniversalEnergyStored() < getPowerPerOperation() && progress - prevProgress > 0) {
			prevProgress = progress;
			PacketHandler.sendToNearby(new PacketMachine(this, (byte)2), this.world, this.getPos(), 32);
		}

	}

	@Override
	public boolean canPerformFunction() {
		return isScanning();
	}

	@Override
	public AxisAlignedBB getRenderBoundingBox() {
		if(isScanning() && bbCache != null) {
			return bbCache;
		}
		return super.getRenderBoundingBox();
	}

	public boolean isScanning() { return totalProgress > 0; }

	public void scanRocket(World world, BlockPos pos2, AxisAlignedBB bb) {

		int thrust = 0;
		int thrustNuclearCoreLimit = 0;
		int thrustNuclearTotalLimit = 0;
		int fuelUse = 0;
		int fuelCapacity = 0;
		FuelType rocketType = null;
		FuelType currentType = null;
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

					BlockPos currBlockPos = new BlockPos(xCurr, yCurr, zCurr);
					if(!world.isAirBlock(currBlockPos)) {
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
		boolean invalidBlock = false;

		if(verifyScan(bb, world)) {
			for(int yCurr = (int) bb.minY; yCurr <= bb.maxY; yCurr++) {
				for(int xCurr = (int) bb.minX; xCurr <= bb.maxX; xCurr++) {
					for(int zCurr = (int) bb.minZ; zCurr <= bb.maxZ; zCurr++) {

						BlockPos currBlockPos = new BlockPos(xCurr, yCurr, zCurr);
						BlockPos abovePos = new BlockPos(xCurr, yCurr + 1, zCurr);
						BlockPos belowPos = new BlockPos(xCurr, yCurr - 1, zCurr);

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

							final float x = xCurr - actualMinX - ((actualMaxX - actualMinX) / 2f);
							final float z = zCurr - actualMinZ - ((actualMaxZ - actualMinZ) / 2f);

							//Check fuel-type determining blocks
							if(block instanceof IRocketEngine && (world.getBlockState(belowPos).getBlock().isAir(world.getBlockState(belowPos), world, belowPos) || world.getBlockState(belowPos).getBlock() instanceof BlockLandingPad || world.getBlockState(belowPos).getBlock() == AdvancedRocketryBlocks.blockLaunchpad)) {
								fuelUse += ((IRocketEngine) block).getFuelConsumptionRate(world, xCurr, yCurr, zCurr);
								thrust += ((IRocketEngine)block).getThrust(world, currBlockPos);
								currentType = ((IRocketEngine) block).getFuelType(world, currBlockPos);
								stats.addEngineLocation(x, yCurr - actualMinY, z);
							} else if(block instanceof IFuelTank) {
								fuelCapacity += (((IFuelTank) block).getMaxFill(world, currBlockPos, state) * ARConfiguration.getCurrentConfig().fuelCapacityMultiplier.get());
								currentType = ((IFuelTank) block).getFuelType(world, currBlockPos);
							} else if (block instanceof IRocketNuclearCore && ((world.getBlockState(belowPos).getBlock() instanceof  IRocketNuclearCore) || (world.getBlockState(belowPos).getBlock() instanceof  IRocketEngine))) {
								thrustNuclearCoreLimit += ((IRocketNuclearCore) block).getMaxThrust(world, currBlockPos);
							}

							if (rocketType != null && currentType != null && currentType != rocketType) {
								status = ErrorCodes.COMBINEDTHRUST;
								return;
							} else
								rocketType = currentType;

							if(block instanceof BlockSeat && !world.getBlockState(abovePos).isSuffocating(world, abovePos)) {
								stats.addPassengerSeat((int) (x), yCurr - actualMinY, (int) (z));
							}

							if(block instanceof IMiningDrill) {
								drillPower += ((IMiningDrill)block).getMiningSpeed(world, currBlockPos);
							}

							TileEntity tile= world.getTileEntity(currBlockPos);
							if(tile instanceof TileSatelliteBay) hasSatellite = true;
							if(tile instanceof TileGuidanceComputer) hasGuidance = true;
						}
					}
				}
			}

			int nuclearWorkingFluidUse = 0;
			if (thrustNuclearCoreLimit > 0) {
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
			stats.setDrillingPower(drillPower);

			//Set status
			if(invalidBlock)
				status = ErrorCodes.INVALIDBLOCK;
		    else if(!hasGuidance && !hasSatellite)
				status = ErrorCodes.NOGUIDANCE;
			else if(getThrust() <= getNeededThrust())
				status = ErrorCodes.NOENGINES;
			else if(rocketType != null && getFuel(rocketType) < getNeededFuel(rocketType))
				status = ErrorCodes.NOFUEL;
			else
				status = ErrorCodes.SUCCESS;
		}
	}


	private void removeReplaceableBlocks(AxisAlignedBB bb) {
		for(int yCurr = (int) bb.minY; yCurr <= bb.maxY; yCurr++) {
			for(int xCurr = (int) bb.minX; xCurr <= bb.maxX; xCurr++) {
				for(int zCurr = (int) bb.minZ; zCurr <= bb.maxZ; zCurr++) {

					BlockPos currBlockPos = new BlockPos(xCurr, yCurr, zCurr);

					if(!world.isAirBlock(currBlockPos)) {
						BlockState state = world.getBlockState(currBlockPos);
						Block block = state.getBlock();
						if(ARConfiguration.getCurrentConfig().blackListRocketBlocks.contains(block) && block.isReplaceable(state, Fluids.WATER))
						{
							if(!world.isRemote)
								world.removeBlock(currBlockPos, false);
						}
					}
				}
			}
		}
	}

	public void assembleRocket() {
		if(bbCache == null || world.isRemote) return;
		//Need to scan again b/c something may have changed
		scanRocket(world, pos, bbCache);

		if(status != ErrorCodes.SUCCESS) return;

		// Remove replacable blocks that don't belong on the rocket
		removeReplaceableBlocks(bbCache);

		StorageChunk storageChunk;
		try {
			storageChunk = StorageChunk.cutWorldBB(world, bbCache);
		} catch(NegativeArraySizeException e) {
			return;
		}

		EntityRocket rocket = new EntityRocket(world, storageChunk, stats.copy(),bbCache.minX + (bbCache.maxX-bbCache.minX)/2f +.5f, this.getPos().getY() , bbCache.minZ + (bbCache.maxZ-bbCache.minZ)/2f +.5f);

		world.addEntity(rocket);
		CompoundNBT nbtdata = new CompoundNBT();

		rocket.writeUnlessRemoved(nbtdata);
		PacketHandler.sendToNearby(new PacketEntity(rocket, (byte)0, nbtdata), rocket.world, this.pos, 64);

		stats.reset();
		this.status = ErrorCodes.FINISHED;
		this.markDirty();
		world.notifyBlockUpdate(pos, world.getBlockState(pos),  world.getBlockState(pos), 3);

		for(IInfrastructure infrastructure : getConnectedInfrastructure()) {
			rocket.linkInfrastructure(infrastructure);
		}
	}

	/**
	 * Does not make sure the structure is complete, only gets max bounds!
	 * @param world the world
	 * @param pos coords to evaluate from
	 * @return AxisAlignedBB bounds of structure if valid  otherwise null
	 */
	public AxisAlignedBB getRocketPadBounds(World world, BlockPos pos) {
		Direction direction = RotatableBlock.getFront(world.getBlockState(pos)).getOpposite();
		int xMin, zMin, xMax, zMax;
		int yCurrent = pos.getY() -1;
		int xCurrent = pos.getX() + direction.getXOffset();
		int zCurrent = pos.getZ() + direction.getZOffset();
		xMax = xMin = xCurrent;
		zMax = zMin = zCurrent;
		int xSize, zSize;

		BlockPos currPos = new BlockPos(xCurrent, yCurrent, zCurrent);
		
		if(world.isRemote)
			return null;

		//Get min and maximum Z/X bounds
		if(direction.getXOffset() != 0) {
			xSize = ZUtils.getContinuousBlockLength(world, direction, currPos, MAX_SIZE, viableBlocks);
			zMin = ZUtils.getContinuousBlockLength(world, Direction.NORTH, currPos, MAX_SIZE, viableBlocks);
			zMax = ZUtils.getContinuousBlockLength(world, Direction.SOUTH, currPos.add(0,0,1), MAX_SIZE - zMin, viableBlocks);
			zSize = zMin + zMax;
			
			zMin = zCurrent - zMin + 1;
			zMax = zCurrent + zMax;

			if(direction.getXOffset() > 0) {
				xMax = xCurrent + xSize-1;
			}

			if(direction.getXOffset() < 0) {
				xMin = xCurrent - xSize+1;
			}
		} else {
			zSize = ZUtils.getContinuousBlockLength(world, direction, currPos, MAX_SIZE, viableBlocks);
			xMin = ZUtils.getContinuousBlockLength(world, Direction.WEST, currPos, MAX_SIZE, viableBlocks);
			xMax = ZUtils.getContinuousBlockLength(world, Direction.EAST, currPos.add(1,0,0), MAX_SIZE - xMin, viableBlocks);
			xSize = xMin + xMax;

			xMin = xCurrent - xMin + 1;
			xMax = xCurrent + xMax;

			if(direction.getZOffset() > 0) {
				zMax = zCurrent + zSize-1;
			}

			if(direction.getZOffset() < 0) {
				zMin = zCurrent - zSize+1;
			}
		}


		int maxTowerSize = 0;
		//Check perimeter for structureBlocks and get the size
		for(int i = xMin; i <= xMax; i++) {
			if(world.getBlockState(new BlockPos(i, yCurrent, zMin-1)).getBlock() == AdvancedRocketryBlocks.blockStructureTower) {
				maxTowerSize = Math.max(maxTowerSize, ZUtils.getContinuousBlockLength(world, Direction.UP, new BlockPos(i, yCurrent, zMin-1), MAX_SIZE_Y, AdvancedRocketryBlocks.blockStructureTower));
			}
			if(world.getBlockState(new BlockPos(i, yCurrent, zMax+1)).getBlock() == AdvancedRocketryBlocks.blockStructureTower) {
				maxTowerSize = Math.max(maxTowerSize, ZUtils.getContinuousBlockLength(world, Direction.UP, new BlockPos(i, yCurrent, zMax+1), MAX_SIZE_Y, AdvancedRocketryBlocks.blockStructureTower));
			}
		}

		for(int i = zMin; i <= zMax; i++) {
			if(world.getBlockState(new BlockPos(xMin-1, yCurrent, i)).getBlock() == AdvancedRocketryBlocks.blockStructureTower) {
				maxTowerSize = Math.max(maxTowerSize, ZUtils.getContinuousBlockLength(world, Direction.UP, new BlockPos(xMin-1, yCurrent, i), MAX_SIZE_Y, AdvancedRocketryBlocks.blockStructureTower));
			}
			if(world.getBlockState(new BlockPos(xMax+1, yCurrent, i)).getBlock() == AdvancedRocketryBlocks.blockStructureTower) {
				maxTowerSize = Math.max(maxTowerSize, ZUtils.getContinuousBlockLength(world, Direction.UP, new BlockPos(xMax+1, yCurrent, i), MAX_SIZE_Y, AdvancedRocketryBlocks.blockStructureTower));
			}
		}

		//if tower does not meet criteria then reutrn null
		if(maxTowerSize < MIN_SIZE_Y || xSize < MIN_SIZE || zSize < MIN_SIZE) {
			return null;
		}

		return new AxisAlignedBB(new BlockPos(xMin, yCurrent+1, zMin), new BlockPos(xMax, yCurrent + maxTowerSize - 1, zMax));
	}


	protected boolean verifyScan(AxisAlignedBB bb, World world) {
		boolean whole = true;

		boundLoop:
			for(int xx = (int)bb.minX; xx <= (int)bb.maxX; xx++) {
				for(int zz = (int)bb.minZ; zz <= (int) bb.maxZ; zz++) {
					Block blockAtSpot = world.getBlockState(new BlockPos(xx, (int)bb.minY-1, zz)).getBlock();
					boolean contained = false;
					for(Block b : viableBlocks) {
						if(blockAtSpot == b) {
							contained = true;
							break;
						}
					}
					
					if(!contained) {
						whole = false;
						break boundLoop;
					}
				}
			}

		return whole;
	}

	public int getVolume(World world, AxisAlignedBB bb) {
		return (int) ((bb.maxX - bb.minX) * (bb.maxY - bb.minY) * (bb.maxZ - bb.minZ));
	}

	@Override
	public CompoundNBT write(CompoundNBT nbt) {
		super.write(nbt);

		stats.writeToNBT(nbt);
		nbt.putInt("scanTime", progress);
		nbt.putInt("scanTotalBlocks", totalProgress);
		nbt.putBoolean("building", building);
		nbt.putInt("status", status.ordinal());

		if(bbCache != null) {
			CompoundNBT tag = new CompoundNBT();
			tag.putDouble("minX", bbCache.minX);
			tag.putDouble("minY", bbCache.minY);
			tag.putDouble("minZ", bbCache.minZ);
			tag.putDouble("maxX", bbCache.maxX);
			tag.putDouble("maxY", bbCache.maxY);
			tag.putDouble("maxZ", bbCache.maxZ);

			nbt.put("bb", tag);
		}


		if(!blockPos.isEmpty()) {
			int[] array = new int[blockPos.size()*3];
			int counter = 0;
			for(HashedBlockPosition pos : blockPos) {
				array[counter] = pos.x;
				array[counter+1] = pos.y;
				array[counter+2] = pos.z;
				counter += 3;
			}

			nbt.putIntArray("infrastructureLocations", array);
		}
		return nbt;
	}

	@Override
	public void read(BlockState state, CompoundNBT nbt) {
		super.read(state, nbt);

		stats.readFromNBT(nbt);

		prevProgress = progress = nbt.getInt("scanTime");
		totalProgress = nbt.getInt("scanTotalBlocks");
		status = ErrorCodes.values()[nbt.getInt("status")];

		building = nbt.getBoolean("building");
		if(nbt.contains("bb")) {

			CompoundNBT tag = nbt.getCompound("bb");
			bbCache = new AxisAlignedBB(tag.getDouble("minX"), 
					tag.getDouble("minY"), tag.getDouble("minZ"),
					tag.getDouble("maxX"), tag.getDouble("maxY"), tag.getDouble("maxZ"));

		}

		blockPos.clear();
		if(nbt.contains("infrastructureLocations")) {
			int[] array = nbt.getIntArray("infrastructureLocations");

			for(int counter = 0; counter < array.length; counter += 3) {
				blockPos.add(new HashedBlockPosition(array[counter], array[counter+1], array[counter+2]));
			}
		}
	}

	@Override
	public SUpdateTileEntityPacket getUpdatePacket() {
		super.getUpdatePacket();
		CompoundNBT nbt = new CompoundNBT();

		write(nbt);

		return new SUpdateTileEntityPacket(pos, 0, nbt);
	}

	@Override
	public void onDataPacket(NetworkManager net, SUpdateTileEntityPacket pkt) {
		read(getBlockState(), pkt.getNbtCompound());
	}

	@Override
	public void writeDataToNetwork(PacketBuffer out, byte id) {
		//Used to sync clinet/server
		if(id == 2) {
			out.writeInt(energy.getUniversalEnergyStored());
			out.writeInt(this.progress);
		}
		else if(id == 3) {
			out.writeInt(lastRocketID);
		}

	}

	@Override
	public void readDataFromNetwork(PacketBuffer in, byte id,
			CompoundNBT nbt) {

		if(id == 2) {
			nbt.putInt("pwr", in.readInt());
			nbt.putInt("tik", in.readInt());
		}
		else if(id == 3 ) {
			nbt.putInt("id", in.readInt());
		}

	}

	public boolean canScan() {
		return bbCache != null;
	}

	@Override
	public void useNetworkData(PlayerEntity player, Dist side, byte id,
			CompoundNBT nbt) {
		if(id == 0) {

			bbCache = getRocketPadBounds(world, pos);
			if(!canScan())
				return;

			totalProgress = (int) (ARConfiguration.getCurrentConfig().buildSpeedMultiplier.get()*this.getVolume(world, bbCache)/10);
			this.markDirty();
			world.notifyBlockUpdate(pos, world.getBlockState(pos),  world.getBlockState(pos), 3);
		} else if(id == 1) {

			if(isScanning())
				return;

			building = true;

			bbCache = getRocketPadBounds(world, pos);
			if(!canScan())
				return;

			totalProgress =(int) (ARConfiguration.getCurrentConfig().buildSpeedMultiplier.get()*this.getVolume(world,bbCache)/10);
			this.markDirty();
			world.notifyBlockUpdate(pos, world.getBlockState(pos),  world.getBlockState(pos), 3);

		} else if(id == 2) {
			energy.setEnergyStored(nbt.getInt("pwr"));
			this.progress = nbt.getInt("tik");
		} else if(id == 3) {
			EntityRocket rocket = (EntityRocket) world.getEntityByID(nbt.getInt("id"));
			for(IInfrastructure infrastructure : getConnectedInfrastructure()) {
				rocket.linkInfrastructure(infrastructure);
			}
		}
	}

	protected void updateText() {
		thrustText.setText(isScanning() ? (LibVulpes.proxy.getLocalizedString("msg.rocketbuilder.thrust") + ": ???") :  String.format("%s: %dkN",LibVulpes.proxy.getLocalizedString("msg.rocketbuilder.thrust"), getThrust() * 10));
		weightText.setText(isScanning() ? (LibVulpes.proxy.getLocalizedString("msg.rocketbuilder.weight") + ": ???")  : String.format("%s: %.2fkN", LibVulpes.proxy.getLocalizedString("msg.rocketbuilder.weight"), (getWeight() * 10 * getGravityMultiplier()) ));
		fuelText.setText(isScanning() ? (LibVulpes.proxy.getLocalizedString("msg.rocketbuilder.fuel") + ": ???") :  String.format("%s: %fmb/s", LibVulpes.proxy.getLocalizedString("msg.rocketbuilder.fuel"), getRocketStats().getBaseFuelRate((stats.getFuelCapacity(FuelType.LIQUID_MONOPROPELLANT) > 0) ? FuelType.LIQUID_MONOPROPELLANT : (stats.getFuelCapacity(FuelType.NUCLEAR_WORKING_FLUID) > 0) ? FuelType.NUCLEAR_WORKING_FLUID : FuelType.LIQUID_BIPROPELLANT)));
		accelerationText.setText(isScanning() ? (LibVulpes.proxy.getLocalizedString("msg.rocketbuilder.acc") + ": ???") : String.format("%s: %.2fm/s\u00b2", LibVulpes.proxy.getLocalizedString("msg.rocketbuilder.acc"), getAcceleration(getGravityMultiplier())*20f));
		if(!world.isRemote) { 
			if(getRocketPadBounds(world, pos) == null)
				setStatus(ErrorCodes.INCOMPLETESTRCUTURE.ordinal());
			else if( ErrorCodes.INCOMPLETESTRCUTURE.equals(getStatus()))
				setStatus(ErrorCodes.UNSCANNED.ordinal());
		}

		errorText.setText(getStatus().getErrorCode());
	}

	@Override
	public List<ModuleBase> getModules(int ID, PlayerEntity player) {
		List<ModuleBase> modules = new LinkedList<>();

		modules.add(new ModulePower(160, 90, this));

		if(world.isRemote)
			modules.add(new ModuleImage(4, 9, new IconResource(4, 9, 168, 74, backdrop)));

		modules.add(new ModuleProgress(89, 47, 0, horizontalProgressBar, this));
		modules.add(new ModuleProgress(89, 66, 1, horizontalProgressBar, this));
		modules.add(new ModuleProgress(89, 28, 3, horizontalProgressBar, this));
		modules.add(new ModuleProgress(89, 9, 4, horizontalProgressBar, this));

		modules.add(new ModuleProgress(149, 90, 2, verticalProgressBar, this));


		modules.add(new ModuleButton(5, 94, LibVulpes.proxy.getLocalizedString("msg.rocketbuilder.scan"), this,  zmaster587.libVulpes.inventory.TextureResources.buttonScan).setAdditionalData(0));

		ModuleButton buttonBuild;
		modules.add(buttonBuild = new ModuleButton(5, 120, LibVulpes.proxy.getLocalizedString("msg.rocketbuilder.build"), this,  zmaster587.libVulpes.inventory.TextureResources.buttonBuild).setAdditionalData(1));
		buttonBuild.setColor(0xFFFF2222);

		modules.add(thrustText = new ModuleText(8, 15, "", 0xFF22FF22));
		modules.add(weightText = new ModuleText(8, 34, "", 0xFF22FF22));
		modules.add(fuelText = new ModuleText(8, 52, "", 0xFF22FF22));
		modules.add(accelerationText = new ModuleText(8, 71, "", 0xFF22FF22));
		modules.add(errorText = new ModuleText(5, 84, "", 0xFFFFFF22));

		updateText();

		for(int i = 0; i < 5; i++)
			modules.add(new ModuleSync(i, this));


		return modules;
	}

	@Override
	public String getModularInventoryName() {
		return "";
	}

	@Override
	public float getNormallizedProgress(int id) {
		if(isScanning() && id != 2)
			return 0f;

		switch(id) {
		case 0:
			FuelType fuelType = (stats.getBaseFuelRate(FuelType.LIQUID_MONOPROPELLANT) > 0) ? FuelType.LIQUID_MONOPROPELLANT : (stats.getBaseFuelRate(FuelType.NUCLEAR_WORKING_FLUID) > 0) ? FuelType.NUCLEAR_WORKING_FLUID : FuelType.LIQUID_BIPROPELLANT;
			return (this.getAcceleration(getGravityMultiplier()) > 0) ? MathHelper.clamp(0.5f + 0.5f*((this.getFuel(fuelType) - this.getNeededFuel(fuelType))/this.getNeededFuel(fuelType)), 0f, 1f) : 0;
		case 1:
			return MathHelper.clamp(0.5f + this.getAcceleration(getGravityMultiplier())*10, 0f, 1f);
		case 2:
			return (float)this.getNormallizedProgress();
		case 3:
			return this.getWeight() > 0 ? 0.5f : 0f;
		case 4:
			return this.getThrust() > 0 ? 0.9f : 0f;
		}

		return 0f;
	}

	@Override
	public void setProgress(int id, int progress) {
		if(id == 2)
			setProgress(progress);
	}

	@Override
	public int getProgress(int id) {
		if(id == 2)
			return getProgress();
		return 0;
	}

	@Override
	public int getTotalProgress(int id) {
		if(id == 2)
			return getTotalProgress();
		return 0;
	}

	@Override
	public void setTotalProgress(int id, int progress) {
		if(id == 2) {
			setTotalProgress(progress);
			updateText();
		}
	}

	@Override
	public void setData(int id, int value) {
		switch(id) {
		case 0:
			getRocketStats().setFuelRate(FuelType.LIQUID_MONOPROPELLANT, value);
			break;
		case 1:
			getRocketStats().setWeight(value);
			break;
		case 2:
			getRocketStats().setThrust(value);
			break;
		case 3:
			getRocketStats().setFuelCapacity(FuelType.LIQUID_MONOPROPELLANT, value);
			break;
		case 4:
			setStatus(value);
		}
		updateText();
	}

	@Override
	public int getData(int id) {
		switch(id) {
		case 0:
			return (int)getRocketStats().getBaseFuelRate(FuelType.LIQUID_MONOPROPELLANT);
		case 1:
			return getRocketStats().getWeight();
		case 2:
			return getRocketStats().getThrust();
		case 3:
			return getRocketStats().getFuelCapacity(FuelType.LIQUID_MONOPROPELLANT);
		case 4:
			return getStatus().ordinal();
		}
		return 0;
	}

	@Override
	public void onInventoryButtonPressed(ModuleButton button) {
		int buttonId = (int)button.getAdditionalData();
		
		PacketHandler.sendToServer(new PacketMachine(this,(byte)( buttonId)) );
		updateText();
	}

	@Override
	public boolean canInteractWithContainer(PlayerEntity entity) {
		return true;
	}

	@Override
	public boolean canConnectEnergy(Direction arg0) {
		return true;
	}

	@Override
	@ParametersAreNonnullByDefault
	public boolean onLinkStart(ItemStack item, TileEntity entity,
			PlayerEntity player, World world) {
		return true;
	}

	@Override
	@ParametersAreNonnullByDefault
	public boolean onLinkComplete(ItemStack item, TileEntity entity,
			PlayerEntity player, World world) {
		TileEntity tile = world.getTileEntity(ItemLinker.getMasterCoords(item));

		if(tile instanceof IInfrastructure) {
			HashedBlockPosition pos = new HashedBlockPosition(tile.getPos());
			if(!blockPos.contains(pos))
				blockPos.add(pos);

			if(getBBCache() == null) {
				bbCache = getRocketPadBounds(world, getPos());
			}

			if(getBBCache() != null) {

				List<EntityRocketBase> rockets = world.getEntitiesWithinAABB(EntityRocketBase.class, bbCache);
				for(EntityRocketBase rocket : rockets) {
					rocket.linkInfrastructure((IInfrastructure) tile);
				}
			}

			if(!world.isRemote) {
				player.sendMessage(new TranslationTextComponent("msg.linker.success"), Util.DUMMY_UUID);

				if(tile instanceof IMultiblock)
					((IMultiblock)tile).setMasterBlock(getPos());
			}

			ItemLinker.resetPosition(item);
			return true;
		}
		return false;
	}

	public void removeConnectedInfrastructure(TileEntity tile) {
		blockPos.remove(new HashedBlockPosition(tile.getPos()));

		if(getBBCache() == null) {
			bbCache = getRocketPadBounds(world, this.getPos());
		}

		if(getBBCache() != null) {
			List<EntityRocketBase> rockets = world.getEntitiesWithinAABB(EntityRocketBase.class, bbCache);

			for(EntityRocketBase rocket : rockets) {
				rocket.unlinkInfrastructure((IInfrastructure) tile);
			}
		}

	}

	public List<IInfrastructure> getConnectedInfrastructure() {
		List<IInfrastructure> infrastructure = new LinkedList<>();

		Iterator<HashedBlockPosition> iter = blockPos.iterator();

		while(iter.hasNext()) {
			HashedBlockPosition position = iter.next();
			TileEntity tile = world.getTileEntity(position.getBlockPos());
			if(tile instanceof IInfrastructure) {
				infrastructure.add((IInfrastructure)tile);
			}
			else
				iter.remove();
		}

		return infrastructure;
	}

	@SubscribeEvent
	public void onRocketLand(RocketLandedEvent event) {
		if(event.world.isRemote)
			return;
		EntityRocketBase rocket = (EntityRocketBase)event.getEntity();

		
		//This apparently happens sometimes
		if(world == null)
		{
			AdvancedRocketry.logger.debug("World null for rocket builder during rocket land event @ " + this.pos);
			return;
		}
		
		if(getBBCache() == null) {
			bbCache = getRocketPadBounds(world, pos);
		}

		if(getBBCache() != null) {
			
			// PRevent deadlock
			if(!world.isAreaLoaded(new BlockPos(bbCache.minX, bbCache.minY, bbCache.minZ), new BlockPos(bbCache.maxX, bbCache.maxY, bbCache.maxZ)))
				return;
			
			List<EntityRocketBase> rockets = world.getEntitiesWithinAABB(EntityRocketBase.class, bbCache);

			if(rockets.contains(rocket)) {
				lastRocketID = rocket.getEntityId();
				for(IInfrastructure infrastructure : getConnectedInfrastructure()) {
					rocket.linkInfrastructure(infrastructure);
				}
				
				PacketHandler.sendToPlayersTrackingEntity(new PacketMachine(this, (byte)3), rocket);
			}
		}
	}

	@Nonnull
	@Override
	public ITextComponent getDisplayName() {
		return new TranslationTextComponent(getModularInventoryName());
	}

	@Override
	@ParametersAreNonnullByDefault
	public Container createMenu(int id, PlayerInventory inv, PlayerEntity player) {
		return new ContainerModular(LibvulpesGuiRegistry.CONTAINER_MODULAR_TILE, id, player, getModules(getModularInvType().ordinal(), player), this, getModularInvType());
	}

	@Override
	public GuiHandler.guiId getModularInvType() {
		return guiId.MODULARNOINV;
	}
}
