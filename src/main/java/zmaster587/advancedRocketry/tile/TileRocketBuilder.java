/*
 * Purpose: validate the rocket structure as well as give feedback to the player as to what needs to be 
 * changed to complete the rocket structure
 * Also will be used to "build" the rocket components from the placed frames, control fuel flow etc
 */

package zmaster587.advancedRocketry.tile;

import java.util.LinkedList;
import java.util.List;

import io.netty.buffer.ByteBuf;
import cpw.mods.fml.relauncher.Side;
import zmaster587.advancedRocketry.AdvancedRocketry;
import zmaster587.advancedRocketry.api.AdvancedRocketryBlocks;
import zmaster587.advancedRocketry.api.fuel.FuelRegistry.FuelType;
import zmaster587.advancedRocketry.api.Configuration;
import zmaster587.advancedRocketry.api.IFuelTank;
import zmaster587.advancedRocketry.api.IRocketEngine;
import zmaster587.advancedRocketry.api.StatsRocket;
import zmaster587.advancedRocketry.block.BlockSeat;
import zmaster587.advancedRocketry.client.render.util.ProgressBarImage;
import zmaster587.advancedRocketry.entity.EntityRocket;
import zmaster587.advancedRocketry.inventory.TextureResources;
import zmaster587.advancedRocketry.inventory.modules.IButtonInventory;
import zmaster587.advancedRocketry.inventory.modules.IDataSync;
import zmaster587.advancedRocketry.inventory.modules.IModularInventory;
import zmaster587.advancedRocketry.inventory.modules.IProgressBar;
import zmaster587.advancedRocketry.inventory.modules.ModuleBase;
import zmaster587.advancedRocketry.inventory.modules.ModuleButton;
import zmaster587.advancedRocketry.inventory.modules.ModuleImage;
import zmaster587.advancedRocketry.inventory.modules.ModulePower;
import zmaster587.advancedRocketry.inventory.modules.ModuleProgress;
import zmaster587.advancedRocketry.inventory.modules.ModuleSync;
import zmaster587.advancedRocketry.inventory.modules.ModuleText;
import zmaster587.advancedRocketry.network.PacketEntity;
import zmaster587.advancedRocketry.network.PacketHandler;
import zmaster587.advancedRocketry.network.PacketMachine;
import zmaster587.advancedRocketry.tile.Satellite.TileSatelliteHatch;
import zmaster587.advancedRocketry.util.StorageChunk;
import zmaster587.libVulpes.block.RotatableBlock;
import zmaster587.libVulpes.interfaces.INetworkEntity;
import zmaster587.libVulpes.tile.TileEntityRFConsumer;
import zmaster587.libVulpes.util.INetworkMachine;
import zmaster587.libVulpes.util.IconResource;
import zmaster587.libVulpes.util.ZUtils;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

public class TileRocketBuilder extends TileEntityRFConsumer implements IButtonInventory, INetworkMachine, IDataSync, IModularInventory, IProgressBar {

	private final int MAX_SIZE = 16;
	private final int MIN_SIZE = 3;
	private final int MAX_SIZE_Y = 64;
	private final int MIN_SIZE_Y = 4;
	private final int MAXSCANDELAY = 10;
	private final int ENERGYFOROP = 100;
	//private final int ENERGY = 100;
	
	protected static final ResourceLocation backdrop =  new ResourceLocation("advancedrocketry","textures/gui/rocketBuilder.png");
	private static final ProgressBarImage horizontalProgressBar = new ProgressBarImage(89, 9, 81, 17, 176, 0, 80, 15, 0, 2, ForgeDirection.EAST, backdrop);
	protected static final ProgressBarImage verticalProgressBar = new ProgressBarImage(76, 93, 8, 52, 176, 15, 2, 38, 3, 2, ForgeDirection.UP, backdrop);
	
	private ModuleText thrustText, weightText, fuelText, accelerationText;
	protected ModuleText errorText;

	private int totalProgress;
	private int progress; // How long until scan is finished from 0 -> num blocks
	private int prevProgress; // Used for client/server sync
	private boolean building; //True is rocket is being built, false if only scanning or otherwise

	private StatsRocket stats;
	protected AxisAlignedBB bbCache;
	protected ErrorCodes status;

	public enum ErrorCodes {
		SUCCESS("Clear for liftoff!"),
		NOFUEL("Not enough fuel capacity!"),
		NOSEAT("Missing Seat or satellite!"),
		NOENGINES("You do not have enough thrust!"),
		NOGUIDANCE("Missing Guidance Computer"),
		UNSCANNED("Rocket unscanned."),
		SUCCESS_STATION("Ready!"),
		EMPTY("Nothing here"),
		FINISHED("Build Complete!");;

		String code;
		private ErrorCodes(String code) {
			this.code = code;
		}

		public String getErrorCode() { return code; } 
	}

	public TileRocketBuilder() {
		super(100000);
		status = ErrorCodes.UNSCANNED;
		stats = new StatsRocket();
		building = false;
		prevProgress = 0;
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

	public int getThrust() { return (int) (stats.getThrust()*Configuration.rocketThrustMultiplier); }

	public float getNeededThrust() {return getWeight();}

	public float getNeededFuel() { return getAcceleration() > 0 ? stats.getFuelRate(FuelType.LIQUID)*MathHelper.sqrt_float((2*(Configuration.orbit-this.yCoord))/getAcceleration()) : 0; }

	public int getFuel() {return (int) (stats.getFuelCapacity(FuelType.LIQUID)*Configuration.fuelCapacityMultiplier);}

	public boolean isBuilding() { return building; }

	public void setBuilding(boolean building) { this.building = building; }

	public void setStatus(int value) {
		status = ErrorCodes.values()[value];
	}

	@Override
	public boolean canUpdate() {
		return true;
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
			if(!worldObj.isRemote) {
				if(building)
					assembleRocket();
				else
					scanRocket(worldObj, xCoord, yCoord, zCoord, bbCache);
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

		if(!this.worldObj.isRemote && this.energy.getEnergyStored() < getPowerPerOperation() && progress - prevProgress > 0) {
			prevProgress = progress;
			PacketHandler.sendToNearby(new PacketMachine(this, (byte)2), this.worldObj.provider.dimensionId, this.xCoord, this.yCoord, this.zCoord, 32);
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

	public void scanRocket(World world, int x, int y, int z, AxisAlignedBB bb) {

		int thrust = 0;
		int fuelUse = 0;
		int fuel = 0;
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

		if(verifyScan(bb, world)) {
			for(int yCurr = (int) bb.minY; yCurr <= bb.maxY; yCurr++) {
				for(int xCurr = (int) bb.minX; xCurr <= bb.maxX; xCurr++) {
					for(int zCurr = (int) bb.minZ; zCurr <= bb.maxZ; zCurr++) {

						if(!world.isAirBlock(xCurr, yCurr, zCurr)) {
							Block block = world.getBlock(xCurr, yCurr, zCurr);
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

							TileEntity tile= world.getTileEntity(xCurr, yCurr, zCurr);
							if(tile instanceof TileSatelliteHatch)
								hasSatellite = true;
							if(tile instanceof TileGuidanceComputer)
								hasGuidance = true;
						}
					}
				}
			}
			stats.setFuelRate(FuelType.LIQUID,fuelUse);
			stats.setWeight(numBlocks);
			stats.setThrust(thrust);
			stats.setFuelCapacity(FuelType.LIQUID,fuel);

			//Set status
			//TODO: warn if seat OR satellite missing
			if(!stats.hasSeat() && !hasSatellite) 
				status = ErrorCodes.NOSEAT;
			else if(stats.hasSeat() && !hasGuidance)
				status = ErrorCodes.NOGUIDANCE;
			else if(getFuel() < getNeededFuel()) 
				status = ErrorCodes.NOFUEL;
			else if(getThrust() < getNeededThrust()) 
				status = ErrorCodes.NOENGINES;
			else
				status = ErrorCodes.SUCCESS;
		}
	}


	public void assembleRocket() {

		if(bbCache == null)
			return;
		//Need to scan again b/c something may have changed
		scanRocket(worldObj, xCoord, yCoord, zCoord, bbCache);

		if(status != ErrorCodes.SUCCESS)
			return;

		StorageChunk storageChunk = StorageChunk.cutWorldBB(worldObj, bbCache);

		EntityRocket rocket = new EntityRocket(worldObj, storageChunk, stats.copy(),bbCache.minX + (bbCache.maxX-bbCache.minX)/2f +.5f, yCoord , bbCache.minZ + (bbCache.maxZ-bbCache.minZ)/2f +.5f);

		worldObj.spawnEntityInWorld(rocket);
		NBTTagCompound nbtdata = new NBTTagCompound();

		rocket.writeToNBT(nbtdata);
		PacketHandler.sendToNearby(new PacketEntity((INetworkEntity)rocket, (byte)0, nbtdata), rocket.worldObj.provider.dimensionId, xCoord, yCoord, zCoord, 64);

		stats.reset();
		this.status = ErrorCodes.UNSCANNED;
		this.markDirty();
		this.worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
	}

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
			xSize = ZUtils.getContinuousBlockLength(world, direction, xCurrent, yCurrent, zCurrent, MAX_SIZE, AdvancedRocketryBlocks.blockLaunchpad);
			zMin = ZUtils.getContinuousBlockLength(world, ForgeDirection.NORTH, xCurrent, yCurrent, zCurrent, MAX_SIZE, AdvancedRocketryBlocks.blockLaunchpad);
			zMax = ZUtils.getContinuousBlockLength(world, ForgeDirection.SOUTH, xCurrent, yCurrent, zCurrent+1, MAX_SIZE - zMin, AdvancedRocketryBlocks.blockLaunchpad);
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
			zSize = ZUtils.getContinuousBlockLength(world, direction, xCurrent, yCurrent, zCurrent, MAX_SIZE, AdvancedRocketryBlocks.blockLaunchpad);
			xMin = ZUtils.getContinuousBlockLength(world, ForgeDirection.WEST, xCurrent, yCurrent, zCurrent, MAX_SIZE, AdvancedRocketryBlocks.blockLaunchpad);
			xMax = ZUtils.getContinuousBlockLength(world, ForgeDirection.EAST, xCurrent+1, yCurrent, zCurrent, MAX_SIZE - xMin, AdvancedRocketryBlocks.blockLaunchpad);
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
		for(int i = xMin; i <= xMax; i++) {
			if(world.getBlock(i, yCurrent, zMin-1) == AdvancedRocketryBlocks.blockStructureTower) {
				maxTowerSize = Math.max(maxTowerSize, ZUtils.getContinuousBlockLength(world, ForgeDirection.UP, i, yCurrent, zMin-1, MAX_SIZE_Y, AdvancedRocketryBlocks.blockStructureTower));
			}

			if(world.getBlock(i, yCurrent, zMax+1) == AdvancedRocketryBlocks.blockStructureTower) {
				maxTowerSize = Math.max(maxTowerSize, ZUtils.getContinuousBlockLength(world, ForgeDirection.UP, i, yCurrent, zMax+1, MAX_SIZE_Y, AdvancedRocketryBlocks.blockStructureTower));
			}
		}

		for(int i = zMin; i <= zMax; i++) {
			if(world.getBlock(xMin-1, yCurrent, i) == AdvancedRocketryBlocks.blockStructureTower) {
				maxTowerSize = Math.max(maxTowerSize, ZUtils.getContinuousBlockLength(world, ForgeDirection.UP, xMin-1, yCurrent, i, MAX_SIZE_Y, AdvancedRocketryBlocks.blockStructureTower));
			}

			if(world.getBlock(xMax+1, yCurrent, i) == AdvancedRocketryBlocks.blockStructureTower) {
				maxTowerSize = Math.max(maxTowerSize, ZUtils.getContinuousBlockLength(world, ForgeDirection.UP, xMax+1, yCurrent, i, MAX_SIZE_Y, AdvancedRocketryBlocks.blockStructureTower));
			}
		}

		//if tower does not meet criteria then reutrn null
		if(maxTowerSize < MIN_SIZE_Y || xSize < MIN_SIZE || zSize < MIN_SIZE) {
			return null;
		}

		return AxisAlignedBB.getBoundingBox(xMin, yCurrent+1, zMin, xMax, yCurrent + maxTowerSize, zMax);
	}


	protected boolean verifyScan(AxisAlignedBB bb, World world) {
		boolean whole = true;

		boundLoop:
			for(int xx = (int)bb.minX; xx <= (int)bb.maxX; xx++) {
				for(int zz = (int)bb.minZ; zz <= (int)bb.maxZ && whole; zz++) {
					if(world.getBlock(xx, (int)bb.minY-1, zz) != AdvancedRocketryBlocks.blockLaunchpad) {
						whole = false;
						break boundLoop;
					}
				}
			}

		return whole;
	}

	public int getVolume(World world, int x, int y, int z, AxisAlignedBB bb) {


		return (int) ((bb.maxX - bb.minX) * (bb.maxY - bb.minY) * (bb.maxZ - bb.minZ));

		/*int numBlocks = 0;


		if(verifyScan(bb, world)) {
			for(int yCurr = (int) bb.minY; yCurr <= bb.maxY; yCurr++) {
				for(int xCurr = (int) bb.minX; xCurr <= bb.maxX; xCurr++) {
					for(int zCurr = (int) bb.minZ; zCurr <= bb.maxZ; zCurr++) {

						if(!world.isAirBlock(xCurr, yCurr, zCurr)) {
							numBlocks++;

						}
					}
				}
			}

			scanTotalBlocks = numBlocks;
		}*/

	}

	@Override
	public void writeToNBT(NBTTagCompound nbt) {
		super.writeToNBT(nbt);

		stats.writeToNBT(nbt);
		nbt.setInteger("scanTime", progress);
		nbt.setInteger("scanTotalBlocks", totalProgress);
		nbt.setBoolean("building", building);

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

	}

	@Override
	public void readFromNBT(NBTTagCompound nbt) {
		super.readFromNBT(nbt);

		stats.readFromNBT(nbt);

		prevProgress = progress = nbt.getInteger("scanTime");
		totalProgress = nbt.getInteger("scanTotalBlocks");

		building = nbt.getBoolean("building");
		if(nbt.hasKey("bb")) {

			NBTTagCompound tag = nbt.getCompoundTag("bb");
			bbCache = AxisAlignedBB.getBoundingBox(tag.getDouble("minX"), 
					tag.getDouble("minY"), tag.getDouble("minZ"),
					tag.getDouble("maxX"), tag.getDouble("maxY"), tag.getDouble("maxZ"));

		}
	}

	@Override
	public Packet getDescriptionPacket() {
		super.getDescriptionPacket();
		NBTTagCompound nbt = new NBTTagCompound();

		writeToNBT(nbt);

		return new S35PacketUpdateTileEntity(xCoord, yCoord, zCoord, 0, nbt);
	}

	@Override
	public void onDataPacket(NetworkManager net, S35PacketUpdateTileEntity pkt) {
		readFromNBT(pkt.func_148857_g());
	}

	//Creates the effects for building the rocket and changes state to build
	public void startBuild(int x, int y, int z) {

	}

	@Override
	public void writeDataToNetwork(ByteBuf out, byte id) {
		//Used to sync clinet/server
		if(id == 2) {
			out.writeInt(energy.getEnergyStored());
			out.writeInt(this.progress);
		}

	}

	@Override
	public void readDataFromNetwork(ByteBuf in, byte id,
			NBTTagCompound nbt) {

		if(id == 2) {
			nbt.setInteger("pwr", in.readInt());
			nbt.setInteger("tik", in.readInt());
		}

	}

	public boolean canScan() {
		return bbCache != null;
	}
	
	@Override
	public void useNetworkData(EntityPlayer player, Side side, byte id,
			NBTTagCompound nbt) {
		if(id == 0) {
			AxisAlignedBB bb = getRocketPadBounds(worldObj, xCoord, yCoord, zCoord);

			bbCache = bb;
			if(!canScan())
				return;

			totalProgress = (int) (Configuration.buildSpeedMultiplier*this.getVolume(worldObj, xCoord, yCoord, zCoord, bbCache)/10);
			this.markDirty();
			this.worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
		}
		else if(id == 1) {

			if(isScanning())
				return;

			building = true;
			AxisAlignedBB bb = getRocketPadBounds(worldObj, xCoord, yCoord, zCoord);

			bbCache = bb;
			if(!canScan())
				return;

			totalProgress =(int) (Configuration.buildSpeedMultiplier*this.getVolume(worldObj, xCoord, yCoord, zCoord,bbCache)/10);
			this.markDirty();
			this.worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);

		}
		else if(id == 2){
			energy.setEnergyStored(nbt.getInteger("pwr"));
			this.progress = nbt.getInteger("tik");
		}
	}

	protected void updateText() {
		thrustText.setText(isScanning() ? "Thrust: ???" :  String.format("Thrust: %dN",getThrust()));
		weightText.setText(isScanning() ? "Weight: ???"  : String.format("Weight: %dN",getWeight()));
		fuelText.setText(isScanning() ? "Fuel: ???" :  String.format("Fuel: %dmb/s", getRocketStats().getFuelRate(FuelType.LIQUID)));
		accelerationText.setText(isScanning() ? "Acc: ???" : String.format("Acc: %.2fm/s", getAcceleration()*20f));
		errorText.setText(getStatus().getErrorCode());
	}

	@Override
	public List<ModuleBase> getModules(int ID) {
		List<ModuleBase> modules = new LinkedList<ModuleBase>();

		modules.add(new ModulePower(160, 90, this));

		modules.add(new ModuleImage(4, 9, new IconResource(4, 9, 168, 74, backdrop)));

		modules.add(new ModuleProgress(89, 47, 0, horizontalProgressBar, this));
		modules.add(new ModuleProgress(89, 66, 1, horizontalProgressBar, this));

		modules.add(new ModuleProgress(149, 90, 2, verticalProgressBar, this));


		modules.add(new ModuleButton(5, 94, 0, "Scan", this, TextureResources.buttonScan));

		ModuleButton buttonBuild;
		modules.add(buttonBuild = new ModuleButton(5, 120, 1, "Build", this, TextureResources.buttonBuild));
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
			return (this.getAcceleration() > 0) ? MathHelper.clamp_float(0.5f + 0.5f*((this.getFuel() - this.getNeededFuel())/this.getNeededFuel()), 0f, 1f) : 0;
		case 1:
			return MathHelper.clamp_float(0.5f + this.getAcceleration()*10, 0f, 1f);
		case 2:
			return (float)this.getNormallizedProgress();
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
			getRocketStats().setFuelRate(FuelType.LIQUID, value);
			break;
		case 1:
			getRocketStats().setWeight(value);
			break;
		case 2:
			getRocketStats().setThrust(value);
			break;
		case 3:
			getRocketStats().setFuelCapacity(FuelType.LIQUID, value);
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
			return getRocketStats().getFuelRate(FuelType.LIQUID);
		case 1:
			return getRocketStats().getWeight();
		case 2:
			return getRocketStats().getThrust();
		case 3:
			return getRocketStats().getFuelCapacity(FuelType.LIQUID);
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
	public boolean canConnectEnergy(ForgeDirection arg0) {
		return true;
	}
}
