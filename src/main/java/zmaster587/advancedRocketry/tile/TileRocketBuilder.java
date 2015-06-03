/*
 * Purpose: validate the rocket structure as well as give feedback to the player as to what needs to be 
 * changed to complete the rocket structure
 * Also will be used to "build" the rocket components from the placed frames, control fuel flow etc
 */

package zmaster587.advancedRocketry.tile;

import cofh.api.energy.EnergyStorage;
import io.netty.buffer.ByteBuf;
import cpw.mods.fml.relauncher.Side;
import zmaster587.advancedRocketry.AdvancedRocketry;
import zmaster587.advancedRocketry.api.FuelRegistry.FuelType;
import zmaster587.advancedRocketry.api.IFuelTank;
import zmaster587.advancedRocketry.api.IRocketEngine;
import zmaster587.advancedRocketry.block.BlockSeat;
import zmaster587.advancedRocketry.entity.EntityRocket;
import zmaster587.advancedRocketry.network.PacketEntity;
import zmaster587.advancedRocketry.network.PacketHandler;
import zmaster587.advancedRocketry.network.PacketMachine;
import zmaster587.advancedRocketry.stats.StatsRocket;
import zmaster587.advancedRocketry.util.Configuration;
import zmaster587.advancedRocketry.util.StorageChunk;
import zmaster587.libVulpes.block.RotatableBlock;
import zmaster587.libVulpes.interfaces.INetworkEntity;
import zmaster587.libVulpes.tile.TileEntityRFConsumer;
import zmaster587.libVulpes.util.INetworkMachine;
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
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

public class TileRocketBuilder extends TileEntityRFConsumer implements INetworkMachine {

	private final int MAX_SIZE = 16;
	private final int MIN_SIZE = 3;
	private final int MAX_SIZE_Y = 64;
	private final int MIN_SIZE_Y = 4;
	private final int MAXSCANDELAY = 10;
	private final int ENERGYFOROP = 100;
	//private final int ENERGY = 100;


	private int scanTotalBlocks;
	private int scanTime; // How long until scan is finished from 0 -> num blocks
	private int scanTimeLast; // Used for client/server sync
	private boolean building; //True is rocket is being built, false if only scanning or otherwise

	private StatsRocket stats;
	private AxisAlignedBB bbCache;
	private ErrorCodes status;

	public enum ErrorCodes {
		SUCCESS("Clear for liftoff!"),
		NOFUEL("Not enough fuel!"),
		NOSEAT("Missing Seat!"),
		NOENGINES("You do not have enough thrust!"),
		UNSCANNED("Rocket unscanned.");

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
		scanTimeLast = 0;
	}

	public ErrorCodes getStatus() {
		return status;
	}
	public StatsRocket getRocketStats() { return stats; }

	public AxisAlignedBB getBBCache() { return bbCache;}

	public int getScanTotalBlocks() { return scanTotalBlocks; }
	public void setScanTotalBlocks(int scanTotalBlocks) { this.scanTotalBlocks = scanTotalBlocks; }

	public int getScanTime() { return scanTime; }
	public void setScanTime(int scanTime) { this.scanTime = scanTime; }

	public double getNormilizedScanTime() {

		return scanTime/(double)(scanTotalBlocks*MAXSCANDELAY);
	}

	public float getAcceleration() {
		return (getThrust() - getWeight());
	}

	public int getWeight()  { return stats.getWeight(); }

	public int getThrust() { return stats.getThrust(); }

	public float getNeededThrust() {return getWeight();}

	public float getNeededFuel() { return getAcceleration() > 0 ? stats.getFuelRate(FuelType.LIQUID)*MathHelper.sqrt_float((2*(Configuration.orbit-this.yCoord))/getAcceleration()) : 0; }

	public int getFuel() {return stats.getFuelCapacity(FuelType.LIQUID);}

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
		if(scanTime >= (scanTotalBlocks*MAXSCANDELAY)) {
			if(!worldObj.isRemote) {
				if(building)
					assembleRocket();
				else
					scanRocket(worldObj, xCoord, yCoord, zCoord, bbCache);
			}
			scanTotalBlocks = -1;
			scanTime = 0;
			scanTimeLast = 0;
			building = false; //Done building
		}

		scanTime++;

		if(!this.worldObj.isRemote && this.energy.getEnergyStored() < getPowerPerOperation() && scanTime - scanTimeLast > 0) {
			scanTimeLast = scanTime;
			PacketHandler.sentToNearby(new PacketMachine(this, (byte)2), this.worldObj.provider.dimensionId, this.xCoord, this.yCoord, this.zCoord, 32);
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

	public boolean isScanning() { return scanTotalBlocks > 0; }

	public void scanRocket(World world, int x, int y, int z, AxisAlignedBB bb) {

		int thrust = 0;
		int fuelUse = 0;
		int fuel = 0;
		int inventorySlots = 0;
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
								stats.setSeatLocation((int)(xCurr - actualMinX - ((actualMaxX - actualMinX)/2f)) , (int)(yCurr  -actualMinY), (int)(zCurr - actualMinZ - ((actualMaxZ - actualMinZ)/2f)));
							}

							TileEntity tile;
							if((tile = world.getTileEntity(xCurr, yCurr, zCurr)) instanceof IInventory) {
								inventorySlots += ((IInventory)tile).getSizeInventory();
							}
						}
					}
				}
			}
			stats.setFuelRate(FuelType.LIQUID,fuelUse);
			stats.setWeight(numBlocks + inventorySlots);
			stats.setThrust(thrust);
			stats.setFuelCapacity(FuelType.LIQUID,fuel);

			//Set status
			if(!stats.hasSeat()) 
				status = ErrorCodes.NOSEAT;
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

		EntityRocket rocket = new EntityRocket(worldObj, storageChunk, stats.copy(),bbCache.minX + (bbCache.maxX-bbCache.minX)/2f, yCoord , bbCache.minZ + (bbCache.maxZ-bbCache.minZ)/2f );

		worldObj.spawnEntityInWorld(rocket);
		NBTTagCompound nbtdata = new NBTTagCompound();

		rocket.writeToNBT(nbtdata);
		PacketHandler.sentToNearby(new PacketEntity((INetworkEntity)rocket, (byte)0, nbtdata), rocket.worldObj.provider.dimensionId, xCoord, yCoord, zCoord, 64);

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
		for(int i = xMin; i <= xMax; i++) {
			if(world.getBlock(i, yCurrent, zMin-1) == AdvancedRocketry.structureTower) {
				maxTowerSize = Math.max(maxTowerSize, ZUtils.getContinuousBlockLength(world, ForgeDirection.UP, i, yCurrent, zMin-1, MAX_SIZE_Y, AdvancedRocketry.structureTower));
			}

			if(world.getBlock(i, yCurrent, zMax+1) == AdvancedRocketry.structureTower) {
				maxTowerSize = Math.max(maxTowerSize, ZUtils.getContinuousBlockLength(world, ForgeDirection.UP, i, yCurrent, zMax+1, MAX_SIZE_Y, AdvancedRocketry.structureTower));
			}
		}

		for(int i = zMin; i <= zMax; i++) {
			if(world.getBlock(xMin-1, yCurrent, i) == AdvancedRocketry.structureTower) {
				maxTowerSize = Math.max(maxTowerSize, ZUtils.getContinuousBlockLength(world, ForgeDirection.UP, xMin-1, yCurrent, i, MAX_SIZE_Y, AdvancedRocketry.structureTower));
			}

			if(world.getBlock(xMax+1, yCurrent, i) == AdvancedRocketry.structureTower) {
				maxTowerSize = Math.max(maxTowerSize, ZUtils.getContinuousBlockLength(world, ForgeDirection.UP, xMax+1, yCurrent, i, MAX_SIZE_Y, AdvancedRocketry.structureTower));
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
					if(world.getBlock(xx, (int)bb.minY-1, zz) != AdvancedRocketry.launchpad) {
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
		nbt.setInteger("scanTime", scanTime);
		nbt.setInteger("scanTotalBlocks", scanTotalBlocks);
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

		scanTimeLast = scanTime = nbt.getInteger("scanTime");
		scanTotalBlocks = nbt.getInteger("scanTotalBlocks");

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
			out.writeInt(this.scanTime);
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

	@Override
	public void useNetworkData(EntityPlayer player, Side side, byte id,
			NBTTagCompound nbt) {
		if(id == 0) {
			AxisAlignedBB bb = getRocketPadBounds(worldObj, xCoord, yCoord, zCoord);

			bbCache = bb;
			if(bb == null)
				return;

			scanTotalBlocks = this.getVolume(worldObj, xCoord, yCoord, zCoord, bbCache)/10;
			this.markDirty();
			this.worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
		}
		else if(id == 1) {

			if(isScanning())
				return;

			building = true;
			AxisAlignedBB bb = getRocketPadBounds(worldObj, xCoord, yCoord, zCoord);

			bbCache = bb;
			if(bb == null)
				return;

			scanTotalBlocks =this.getVolume(worldObj, xCoord, yCoord, zCoord,bbCache)/10;
			this.markDirty();
			this.worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);

		}
		else if(id == 2){
			energy.setEnergyStored(nbt.getInteger("pwr"));
			this.scanTime = nbt.getInteger("tik");
		}
	}


}
