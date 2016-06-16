package zmaster587.advancedRocketry.mission;

import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.oredict.OreDictionary;
import zmaster587.advancedRocketry.api.Configuration;
import zmaster587.advancedRocketry.api.DataStorage.DataType;
import zmaster587.advancedRocketry.api.SatelliteRegistry;
import zmaster587.advancedRocketry.api.StatsRocket;
import zmaster587.advancedRocketry.api.satellite.SatelliteBase;
import zmaster587.advancedRocketry.entity.EntityRocket;
import zmaster587.advancedRocketry.item.ItemAsteroidChip;
import zmaster587.advancedRocketry.util.StorageChunk;

public class MissionOreMining extends SatelliteBase implements IMission {

	long startWorldTime;
	double x,y,z;
	long duration;
	int launchDimension;
	StorageChunk rocketStorage;
	StatsRocket rocketStats;
	World worldObj;
	
	public MissionOreMining(){}
	
	public MissionOreMining(long duration, EntityRocket entity) {
		super();
		satelliteProperties.setSatelliteType(SatelliteRegistry.getKey(this.getClass()));
		
		satelliteProperties.setId(zmaster587.advancedRocketry.dimension.DimensionManager.getInstance().getNextSatelliteId());
		
		startWorldTime = DimensionManager.getWorld(0).getTotalWorldTime();
		this.duration = duration;
		this.launchDimension = entity.worldObj.provider.dimensionId;
		rocketStorage = entity.storage;
		rocketStats = entity.stats;
		x = entity.posX;
		y = entity.posY;
		z = entity.posZ;
		worldObj = entity.worldObj;
	}
	
	@Override
	public double getProgress(World world) {
		return (DimensionManager.getWorld(0).getTotalWorldTime() - startWorldTime) / (double)duration;
	}
	
	@Override
	public String getInfo(World world) {
		return null;
	}

	@Override
	public String getName() {
		return "";
	}

	@Override
	public boolean performAction(EntityPlayer player, World world, int x,
			int y, int z) {
		return false;
	}

	@Override
	public double failureChance() {
		return 0;
	}
	
	@Override
	public boolean canTick() {
		return true;
	}
	
	@Override
	public void onMissionComplete() {
		int distanceData, compositionData, massData, maxData;
		if(rocketStorage.getGuidanceComputer() == null)
			return;
		ItemStack stack = rocketStorage.getGuidanceComputer().getStackInSlot(0);
		distanceData = ((ItemAsteroidChip)stack.getItem()).getDataStorage(stack).getDataAmount(DataType.DISTANCE);
		compositionData = ((ItemAsteroidChip)stack.getItem()).getDataStorage(stack).getDataAmount(DataType.COMPOSITION);
		massData = ((ItemAsteroidChip)stack.getItem()).getDataStorage(stack).getDataAmount(DataType.MASS);
		maxData = ((ItemAsteroidChip)stack.getItem()).getDataStorage(stack).getMaxData();
		
		//fill the inventory of the rocket
		if(distanceData/(double)maxData > Math.random()) {
			int totalStacks = (int) (massData*Configuration.asteroidMiningMult*Math.random()*128);
			ItemStack[] stacks = new ItemStack[totalStacks];
			for (int i = 0; i < totalStacks; i++) {
				if((compositionData/(double)maxData)*0.9d > Math.random()) {
					String oreDictName = Configuration.standardAsteroidOres[(int)(Math.random()*Configuration.standardAsteroidOres.length)];
					List<ItemStack> ores = OreDictionary.getOres(oreDictName);
					if(ores != null && !ores.isEmpty()) {
						stacks[i] = ores.get(0).copy();
						stacks[i].stackSize = 64;
						continue;
					}
				}
				
				stacks[i] = new ItemStack(Blocks.stone,64);
			}
			
			for(int i = 0,  g = 0; i < rocketStorage.getUsableTiles().size(); i++) {
				IInventory tile = (IInventory) rocketStorage.getUsableTiles().get(i);
				
				
				for(int offset = 0; offset < tile.getSizeInventory() && g < totalStacks; offset++, g++) {
					if(tile.getStackInSlot(offset) == null)
						tile.setInventorySlotContents(offset, stacks[g]);
				}
			}
		}
		

		
		EntityRocket rocket = new EntityRocket(DimensionManager.getWorld(launchDimension), rocketStorage, rocketStats, x, y-1, z);
		
		DimensionManager.getWorld(launchDimension).spawnEntityInWorld(rocket);
		rocket.setInOrbit(true);
		rocket.setInFlight(true);
		rocket.setVelocity(0, -1, 0);
	}
	
	@Override
	public void tickEntity() {
		if(getProgress(DimensionManager.getWorld(getDimensionId())) >= 1 && !DimensionManager.getWorld(0).isRemote) {
			zmaster587.advancedRocketry.dimension.DimensionManager.getInstance().getDimensionProperties(getDimensionId()).removeSatellite(getId());
			onMissionComplete();
		}
	}
	
	public void writeToNBT(NBTTagCompound nbt) {
		super.writeToNBT(nbt);
		NBTTagCompound nbt2 = new NBTTagCompound();
		rocketStats.writeToNBT(nbt2);
		nbt.setTag("rocketStats", nbt2);
		
		nbt2 = new NBTTagCompound();
		rocketStorage.writeToNBT(nbt2);
		nbt.setTag("rocketStorage", nbt2);
		
		nbt.setDouble("launchPosX", x);
		nbt.setDouble("launchPosY", y);
		nbt.setDouble("launchPosZ", z);
		
		nbt.setLong("startWorldTime", startWorldTime);
		nbt.setLong("duration", duration);
	}
	
	public void readFromNBT(NBTTagCompound nbt) {
		super.readFromNBT(nbt);
		
		rocketStats = new StatsRocket();
		rocketStats.readFromNBT(nbt.getCompoundTag("rocketStats"));
		
		rocketStorage = new StorageChunk();
		rocketStorage.readFromNBT(nbt.getCompoundTag("rocketStorage"));
		
		x = nbt.getDouble("launchPosX");
		y = nbt.getDouble("launchPosY");
		z = nbt.getDouble("launchPosZ");
		
		startWorldTime = nbt.getLong("startWorldTime");
		duration = nbt.getLong("duration");
	}
}
