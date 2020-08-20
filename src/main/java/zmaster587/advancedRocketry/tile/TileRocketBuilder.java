/*
 * Purpose: validate the rocket structure as well as give feedback to the player as to what needs to be 
 * changed to complete the rocket structure
 * Also will be used to "build" the rocket components from the placed frames, control fuel flow etc
 */

package zmaster587.advancedRocketry.tile;

import io.netty.buffer.ByteBuf;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import zmaster587.advancedRocketry.AdvancedRocketry;
import zmaster587.advancedRocketry.api.*;
import zmaster587.advancedRocketry.api.RocketEvent.RocketLandedEvent;
import zmaster587.advancedRocketry.api.fuel.FuelRegistry.FuelType;
import zmaster587.advancedRocketry.block.BlockSeat;
import zmaster587.advancedRocketry.entity.EntityRocket;
import zmaster587.advancedRocketry.network.PacketInvalidLocationNotify;
import zmaster587.advancedRocketry.tile.hatch.TileSatelliteHatch;
import zmaster587.advancedRocketry.util.StorageChunk;
import zmaster587.libVulpes.LibVulpes;
import zmaster587.libVulpes.block.RotatableBlock;
import zmaster587.libVulpes.client.util.ProgressBarImage;
import zmaster587.libVulpes.interfaces.ILinkableTile;
import zmaster587.libVulpes.interfaces.INetworkEntity;
import zmaster587.libVulpes.inventory.modules.*;
import zmaster587.libVulpes.items.ItemLinker;
import zmaster587.libVulpes.network.PacketEntity;
import zmaster587.libVulpes.network.PacketHandler;
import zmaster587.libVulpes.network.PacketMachine;
import zmaster587.libVulpes.tile.IMultiblock;
import zmaster587.libVulpes.tile.TileEntityRFConsumer;
import zmaster587.libVulpes.util.HashedBlockPosition;
import zmaster587.libVulpes.util.INetworkMachine;
import zmaster587.libVulpes.util.IconResource;
import zmaster587.libVulpes.util.ZUtils;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public class TileRocketBuilder extends TileEntityRFConsumer implements IButtonInventory, INetworkMachine, IDataSync, IModularInventory, IProgressBar, ILinkableTile {

	private final int MAX_SIZE = 16;
	private final int MIN_SIZE = 3;
	private final int MAX_SIZE_Y = 64;
	private final int MIN_SIZE_Y = 4;
	private final int MAXSCANDELAY = 10;
	private final int ENERGYFOROP = 100;
	//private final int ENERGY = 100;

	protected static final ResourceLocation backdrop =  new ResourceLocation("advancedrocketry","textures/gui/rocketBuilder.png");
	private static final ProgressBarImage horizontalProgressBar = new ProgressBarImage(89, 9, 81, 17, 176, 0, 80, 15, 0, 2, EnumFacing.EAST, backdrop);
	protected static final ProgressBarImage verticalProgressBar = new ProgressBarImage(76, 93, 8, 52, 176, 15, 2, 38, 3, 2, EnumFacing.UP, backdrop);

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
	
	static final Block viableBlocks[] = {AdvancedRocketryBlocks.blockLaunchpad, AdvancedRocketryBlocks.blockLandingPad};

	private List<HashedBlockPosition> blockPos;

	protected static enum ErrorCodes {
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
		INVALIDBLOCK(LibVulpes.proxy.getLocalizedString("msg.rocketbuild.invalidblock"));
		
		String code;
		private ErrorCodes(String code) {
			this.code = code;
		}

		public String getErrorCode() { return code; } 
	}

	public TileRocketBuilder() {
		super(100000);

		blockPos = new LinkedList<HashedBlockPosition>();

		status = ErrorCodes.UNSCANNED;
		stats = new StatsRocket();
		building = false;
		prevProgress = 0;
		MinecraftForge.EVENT_BUS.register(this);
	}

	@Override
	public void invalidate() {
		super.invalidate();
		MinecraftForge.EVENT_BUS.unregister(this);
		for(HashedBlockPosition pos : blockPos) {
			TileEntity tile = world.getTileEntity(pos.getBlockPos());

			if(tile instanceof IMultiblock)
				((IMultiblock)tile).setIncomplete();
		}
	}

	@Override
	public void onChunkUnload() {
		super.onChunkUnload();
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

	public float getAcceleration() {
		return stats.getAcceleration();
	}

	public int getWeight()  { return stats.getWeight(); }

	public int getThrust() { return (int) (stats.getThrust()); }

	public float getNeededThrust() {return getWeight();}

	public float getNeededFuel() { return getAcceleration() > 0 ? 2*stats.getFuelRate(FuelType.LIQUID_MONOPROPELLANT)*MathHelper.sqrt((2*(ARConfiguration.getCurrentConfig().orbit-this.getPos().getY()))/getAcceleration()) : 0; }

	public int getFuel() {return (int) (stats.getFuelCapacity(FuelType.LIQUID_MONOPROPELLANT)*ARConfiguration.getCurrentConfig().fuelCapacityMultiplier);}

	public boolean isBuilding() { return building; }

	public void setBuilding(boolean building) { this.building = building; }

	public void setStatus(int value) {
		status = ErrorCodes.values()[value];
	}

	@Override
	public boolean shouldRenderInPass(int pass) {
		return pass == 1;
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
			PacketHandler.sendToNearby(new PacketMachine(this, (byte)2), this.world.provider.getDimension(), this.getPos(), 32);
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

					BlockPos currBlockPos = new BlockPos(xCurr, yCurr, zCurr);
					IBlockState state = world.getBlockState(currBlockPos);
					Block block = state.getBlock();

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

						if(!world.isAirBlock(currBlockPos)) {
							IBlockState state = world.getBlockState(currBlockPos);
							Block block = state.getBlock();

							if(ARConfiguration.getCurrentConfig().blackListRocketBlocks.contains(block))
							{
								if(!block.isReplaceable(world, currBlockPos))
								{
									invalidBlock = true;
									if(!world.isRemote)
										PacketHandler.sendToNearby(new PacketInvalidLocationNotify(new HashedBlockPosition(xCurr, yCurr, zCurr)), world.provider.getDimension(), getPos(), 64);
								}
								continue;
							}
							
							numBlocks++;
							
							//If rocketEngine increaseThrust
							if(block instanceof IRocketEngine) {
								thrust += ((IRocketEngine)block).getThrust(world, currBlockPos);
								fuelUse += ((IRocketEngine)block).getFuelConsumptionRate(world, xCurr, yCurr, zCurr);
								stats.addEngineLocation(xCurr - actualMinX - ((actualMaxX - actualMinX)/2f), yCurr - actualMinY, zCurr - actualMinZ - ((actualMaxZ - actualMinZ)/2f));
							}

							if(block instanceof IFuelTank) {
								fuel += ((IFuelTank)block).getMaxFill(world, currBlockPos, state);
							}

							if(block instanceof BlockSeat) {
								stats.addPassengerSeat((int)(xCurr - actualMinX - ((actualMaxX - actualMinX)/2f)) , (int)(yCurr  -actualMinY), (int)(zCurr - actualMinZ - ((actualMaxZ - actualMinZ)/2f)));
							}

							if(block instanceof IMiningDrill) {
								drillPower += ((IMiningDrill)block).getMiningSpeed(world, currBlockPos);
							}

							TileEntity tile= world.getTileEntity(currBlockPos);
							if(tile instanceof TileSatelliteHatch)
								hasSatellite = true;
							if(tile instanceof TileGuidanceComputer)
								hasGuidance = true;
								
						}
					}
				}
			}
			stats.setFuelRate(FuelType.LIQUID_MONOPROPELLANT,fuelUse);
			stats.setWeight(numBlocks);
			stats.setThrust(thrust);
			stats.setFuelCapacity(FuelType.LIQUID_MONOPROPELLANT,fuel);
			stats.setDrillingPower(drillPower);

			//Set status
			//TODO: warn if seat OR satellite missing
			//if(!stats.hasSeat() && !hasSatellite) 
			//status = ErrorCodes.NOSEAT;
			/*else*/ 
			if(invalidBlock)
				status = ErrorCodes.INVALIDBLOCK;
			else if(!hasGuidance && !hasSatellite)
				status = ErrorCodes.NOGUIDANCE;
			else if(getFuel() <= getNeededFuel()) 
				status = ErrorCodes.NOFUEL;
			else if(getThrust() <= getNeededThrust()) 
				status = ErrorCodes.NOENGINES;
			else
				status = ErrorCodes.SUCCESS;
		}
	}


	private void removeReplaceableBlocks(AxisAlignedBB bb)
	{
		for(int yCurr = (int) bb.minY; yCurr <= bb.maxY; yCurr++) {
			for(int xCurr = (int) bb.minX; xCurr <= bb.maxX; xCurr++) {
				for(int zCurr = (int) bb.minZ; zCurr <= bb.maxZ; zCurr++) {

					BlockPos currBlockPos = new BlockPos(xCurr, yCurr, zCurr);

					if(!world.isAirBlock(currBlockPos)) {
						IBlockState state = world.getBlockState(currBlockPos);
						Block block = state.getBlock();
						if(ARConfiguration.getCurrentConfig().blackListRocketBlocks.contains(block) && block.isReplaceable(world, currBlockPos))
						{
							if(!world.isRemote)
								world.setBlockToAir(currBlockPos);
						}
					}
				}
			}
		}
	}

	public void assembleRocket() {

		if(bbCache == null || world.isRemote)
			return;
		//Need to scan again b/c something may have changed
		scanRocket(world, pos, bbCache);

		if(status != ErrorCodes.SUCCESS)
			return;

		// Remove replacable blocks that don't belong on the rocket
		removeReplaceableBlocks(bbCache);

		StorageChunk storageChunk;
		try {
			storageChunk = StorageChunk.cutWorldBB(world, bbCache);
		} catch(NegativeArraySizeException e) {
			return;
		}

		EntityRocket rocket = new EntityRocket(world, storageChunk, stats.copy(),bbCache.minX + (bbCache.maxX-bbCache.minX)/2f +.5f, this.getPos().getY() , bbCache.minZ + (bbCache.maxZ-bbCache.minZ)/2f +.5f);

		world.spawnEntity(rocket);
		NBTTagCompound nbtdata = new NBTTagCompound();

		rocket.writeToNBT(nbtdata);
		PacketHandler.sendToNearby(new PacketEntity((INetworkEntity)rocket, (byte)0, nbtdata), rocket.world.provider.getDimension(), this.pos, 64);

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
	 * @param x coord to evaluate from
	 * @param y coord to evaluate from
	 * @param z coord to evaluate from
	 * @return AxisAlignedBB bounds of structure if valid  otherwise null
	 */
	public AxisAlignedBB getRocketPadBounds(World world, BlockPos pos) {
		EnumFacing direction = RotatableBlock.getFront(world.getBlockState(pos)).getOpposite();
		int xMin, zMin, xMax, zMax;
		int yCurrent = pos.getY() -1;
		int xCurrent = pos.getX() + direction.getFrontOffsetX();
		int zCurrent = pos.getZ() + direction.getFrontOffsetZ();
		xMax = xMin = xCurrent;
		zMax = zMin = zCurrent;
		int xSize, zSize;

		BlockPos currPos = new BlockPos(xCurrent, yCurrent, zCurrent);
		
		if(world.isRemote)
			return null;

		//Get min and maximum Z/X bounds
		if(direction.getFrontOffsetX() != 0) {
			xSize = ZUtils.getContinuousBlockLength(world, direction, currPos, MAX_SIZE, viableBlocks);
			zMin = ZUtils.getContinuousBlockLength(world, EnumFacing.NORTH, currPos, MAX_SIZE, viableBlocks);
			zMax = ZUtils.getContinuousBlockLength(world, EnumFacing.SOUTH, currPos.add(0,0,1), MAX_SIZE - zMin, viableBlocks);
			zSize = zMin + zMax;
			
			zMin = zCurrent - zMin + 1;
			zMax = zCurrent + zMax;

			if(direction.getFrontOffsetX() > 0) {
				xMax = xCurrent + xSize-1;
			}

			if(direction.getFrontOffsetX() < 0) {
				xMin = xCurrent - xSize+1;
			}
		}
		else {
			zSize = ZUtils.getContinuousBlockLength(world, direction, currPos, MAX_SIZE, viableBlocks);
			xMin = ZUtils.getContinuousBlockLength(world, EnumFacing.WEST, currPos, MAX_SIZE, viableBlocks);
			xMax = ZUtils.getContinuousBlockLength(world, EnumFacing.EAST, currPos.add(1,0,0), MAX_SIZE - xMin, viableBlocks);
			xSize = xMin + xMax;

			xMin = xCurrent - xMin + 1;
			xMax = xCurrent + xMax;

			if(direction.getFrontOffsetZ() > 0) {
				zMax = zCurrent + zSize-1;
			}

			if(direction.getFrontOffsetZ() < 0) {
				zMin = zCurrent - zSize+1;
			}
		}


		int maxTowerSize = 0;
		//Check perimeter for structureBlocks and get the size
		for(int i = xMin; i <= xMax; i++) {
			if(world.getBlockState(new BlockPos(i, yCurrent, zMin-1)).getBlock() == AdvancedRocketryBlocks.blockStructureTower) {
				maxTowerSize = Math.max(maxTowerSize, ZUtils.getContinuousBlockLength(world, EnumFacing.UP, new BlockPos(i, yCurrent, zMin-1), MAX_SIZE_Y, AdvancedRocketryBlocks.blockStructureTower));
			}

			if(world.getBlockState(new BlockPos(i, yCurrent, zMax+1)).getBlock() == AdvancedRocketryBlocks.blockStructureTower) {
				maxTowerSize = Math.max(maxTowerSize, ZUtils.getContinuousBlockLength(world, EnumFacing.UP, new BlockPos(i, yCurrent, zMax+1), MAX_SIZE_Y, AdvancedRocketryBlocks.blockStructureTower));
			}
		}

		for(int i = zMin; i <= zMax; i++) {
			if(world.getBlockState(new BlockPos(xMin-1, yCurrent, i)).getBlock() == AdvancedRocketryBlocks.blockStructureTower) {
				maxTowerSize = Math.max(maxTowerSize, ZUtils.getContinuousBlockLength(world, EnumFacing.UP, new BlockPos(xMin-1, yCurrent, i), MAX_SIZE_Y, AdvancedRocketryBlocks.blockStructureTower));
			}

			if(world.getBlockState(new BlockPos(xMax+1, yCurrent, i)).getBlock() == AdvancedRocketryBlocks.blockStructureTower) {
				maxTowerSize = Math.max(maxTowerSize, ZUtils.getContinuousBlockLength(world, EnumFacing.UP, new BlockPos(xMax+1, yCurrent, i), MAX_SIZE_Y, AdvancedRocketryBlocks.blockStructureTower));
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
				for(int zz = (int)bb.minZ; zz <= (int)bb.maxZ && whole; zz++) {
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
	public NBTTagCompound writeToNBT(NBTTagCompound nbt) {
		super.writeToNBT(nbt);

		stats.writeToNBT(nbt);
		nbt.setInteger("scanTime", progress);
		nbt.setInteger("scanTotalBlocks", totalProgress);
		nbt.setBoolean("building", building);
		nbt.setInteger("status", status.ordinal());

		if(bbCache != null) {
			NBTTagCompound tag = new NBTTagCompound();
			tag.setDouble("minX", bbCache.minX);
			tag.setDouble("minY", bbCache.minY);
			tag.setDouble("minZ", bbCache.minZ);
			tag.setDouble("maxX", bbCache.maxX);
			tag.setDouble("maxY", bbCache.maxY);
			tag.setDouble("maxZ", bbCache.maxZ);

			nbt.setTag("bb", tag);
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

			nbt.setIntArray("infrastructureLocations", array);
		}
		return nbt;
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt) {
		super.readFromNBT(nbt);

		stats.readFromNBT(nbt);

		prevProgress = progress = nbt.getInteger("scanTime");
		totalProgress = nbt.getInteger("scanTotalBlocks");
		status = ErrorCodes.values()[nbt.getInteger("status")];

		building = nbt.getBoolean("building");
		if(nbt.hasKey("bb")) {

			NBTTagCompound tag = nbt.getCompoundTag("bb");
			bbCache = new AxisAlignedBB(tag.getDouble("minX"), 
					tag.getDouble("minY"), tag.getDouble("minZ"),
					tag.getDouble("maxX"), tag.getDouble("maxY"), tag.getDouble("maxZ"));

		}

		blockPos.clear();
		if(nbt.hasKey("infrastructureLocations")) {
			int array[] = nbt.getIntArray("infrastructureLocations");

			for(int counter = 0; counter < array.length; counter += 3) {
				blockPos.add(new HashedBlockPosition(array[counter], array[counter+1], array[counter+2]));
			}
		}
	}

	@Override
	public SPacketUpdateTileEntity getUpdatePacket() {
		super.getUpdatePacket();
		NBTTagCompound nbt = new NBTTagCompound();

		writeToNBT(nbt);

		return new SPacketUpdateTileEntity(pos, 0, nbt);
	}

	@Override
	public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity pkt) {
		readFromNBT(pkt.getNbtCompound());
	}

	//Creates the effects for building the rocket and changes state to build
	public void startBuild(int x, int y, int z) {

	}

	@Override
	public void writeDataToNetwork(ByteBuf out, byte id) {
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
	public void readDataFromNetwork(ByteBuf in, byte id,
			NBTTagCompound nbt) {

		if(id == 2) {
			nbt.setInteger("pwr", in.readInt());
			nbt.setInteger("tik", in.readInt());
		}
		else if(id == 3 ) {
			nbt.setInteger("id", in.readInt());
		}

	}

	public boolean canScan() {
		return bbCache != null;
	}

	@Override
	public void useNetworkData(EntityPlayer player, Side side, byte id,
			NBTTagCompound nbt) {
		if(id == 0) {
			AxisAlignedBB bb = getRocketPadBounds(world, pos);

			bbCache = bb;
			if(!canScan())
				return;

			totalProgress = (int) (ARConfiguration.getCurrentConfig().buildSpeedMultiplier*this.getVolume(world, bbCache)/10);
			this.markDirty();
			world.notifyBlockUpdate(pos, world.getBlockState(pos),  world.getBlockState(pos), 3);
		}
		else if(id == 1) {

			if(isScanning())
				return;

			building = true;
			AxisAlignedBB bb = getRocketPadBounds(world, pos);

			bbCache = bb;
			if(!canScan())
				return;

			totalProgress =(int) (ARConfiguration.getCurrentConfig().buildSpeedMultiplier*this.getVolume(world,bbCache)/10);
			this.markDirty();
			world.notifyBlockUpdate(pos, world.getBlockState(pos),  world.getBlockState(pos), 3);

		}
		else if(id == 2) {
			energy.setEnergyStored(nbt.getInteger("pwr"));
			this.progress = nbt.getInteger("tik");
		}
		else if(id == 3) {
			EntityRocket rocket = (EntityRocket) world.getEntityByID(nbt.getInteger("id"));
			for(IInfrastructure infrastructure : getConnectedInfrastructure()) {
				rocket.linkInfrastructure(infrastructure);
			}
		}
	}

	protected void updateText() {
		thrustText.setText(isScanning() ? (LibVulpes.proxy.getLocalizedString("msg.rocketbuilder.thrust") + ": ???") :  String.format("%s: %dN",LibVulpes.proxy.getLocalizedString("msg.rocketbuilder.thrust"), getThrust()));
		weightText.setText(isScanning() ? (LibVulpes.proxy.getLocalizedString("msg.rocketbuilder.weight") + ": ???")  : String.format("%s: %dN", LibVulpes.proxy.getLocalizedString("msg.rocketbuilder.weight"),getWeight()));
		fuelText.setText(isScanning() ? (LibVulpes.proxy.getLocalizedString("msg.rocketbuilder.fuel") + ": ???") :  String.format("%s: %dmb/s", LibVulpes.proxy.getLocalizedString("msg.rocketbuilder.fuel"), getRocketStats().getFuelRate(FuelType.LIQUID_MONOPROPELLANT)));
		accelerationText.setText(isScanning() ? (LibVulpes.proxy.getLocalizedString("msg.rocketbuilder.acc") + ": ???") : String.format("%s: %.2fm/s\u00b2", LibVulpes.proxy.getLocalizedString("msg.rocketbuilder.acc"), getAcceleration()*20f));
		if(!world.isRemote) { 
			if(getRocketPadBounds(world, pos) == null)
				setStatus(ErrorCodes.INCOMPLETESTRCUTURE.ordinal());
			else if( ErrorCodes.INCOMPLETESTRCUTURE.equals(getStatus()))
				setStatus(ErrorCodes.UNSCANNED.ordinal());
		}

		errorText.setText(getStatus().getErrorCode());
	}

	@Override
	public List<ModuleBase> getModules(int ID, EntityPlayer player) {
		List<ModuleBase> modules = new LinkedList<ModuleBase>();

		modules.add(new ModulePower(160, 90, this));

		if(world.isRemote)
			modules.add(new ModuleImage(4, 9, new IconResource(4, 9, 168, 74, backdrop)));

		modules.add(new ModuleProgress(89, 47, 0, horizontalProgressBar, this));
		modules.add(new ModuleProgress(89, 66, 1, horizontalProgressBar, this));
		modules.add(new ModuleProgress(89, 28, 3, horizontalProgressBar, this));
		modules.add(new ModuleProgress(89, 9, 4, horizontalProgressBar, this));

		modules.add(new ModuleProgress(149, 90, 2, verticalProgressBar, this));


		modules.add(new ModuleButton(5, 94, 0, LibVulpes.proxy.getLocalizedString("msg.rocketbuilder.scan"), this,  zmaster587.libVulpes.inventory.TextureResources.buttonScan));

		ModuleButton buttonBuild;
		modules.add(buttonBuild = new ModuleButton(5, 120, 1, LibVulpes.proxy.getLocalizedString("msg.rocketbuilder.build"), this,  zmaster587.libVulpes.inventory.TextureResources.buttonBuild));
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
			return (this.getAcceleration() > 0) ? MathHelper.clamp(0.5f + 0.5f*((this.getFuel() - this.getNeededFuel())/this.getNeededFuel()), 0f, 1f) : 0;
		case 1:
			return MathHelper.clamp(0.5f + this.getAcceleration()*10, 0f, 1f);
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
			return getRocketStats().getFuelRate(FuelType.LIQUID_MONOPROPELLANT);
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
	public void onInventoryButtonPressed(int buttonId) {
		PacketHandler.sendToServer(new PacketMachine(this,(byte)( buttonId)) );
		updateText();
	}

	@Override
	public boolean canInteractWithContainer(EntityPlayer entity) {
		return true;
	}

	@Override
	public boolean canConnectEnergy(EnumFacing arg0) {
		return true;
	}

	@Override
	public boolean onLinkStart(ItemStack item, TileEntity entity,
			EntityPlayer player, World world) {
		return true;
	}

	@Override
	public boolean onLinkComplete(ItemStack item, TileEntity entity,
			EntityPlayer player, World world) {
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
				player.sendMessage(new TextComponentTranslation("msg.linker.success"));

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
		List<IInfrastructure> infrastructure = new LinkedList<IInfrastructure>();

		Iterator<HashedBlockPosition> iter = blockPos.iterator();

		while(iter.hasNext()) {
			HashedBlockPosition position = iter.next();
			TileEntity tile = world.getTileEntity(position.getBlockPos());
			if((tile = world.getTileEntity(position.getBlockPos())) instanceof IInfrastructure) {
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
}
