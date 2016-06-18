package zmaster587.advancedRocketry.mission;

import java.util.LinkedList;
import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.oredict.OreDictionary;
import zmaster587.advancedRocketry.AdvancedRocketry;
import zmaster587.advancedRocketry.api.Configuration;
import zmaster587.advancedRocketry.api.DataStorage.DataType;
import zmaster587.advancedRocketry.api.IInfrastructure;
import zmaster587.advancedRocketry.api.SatelliteRegistry;
import zmaster587.advancedRocketry.api.StatsRocket;
import zmaster587.advancedRocketry.api.satellite.SatelliteBase;
import zmaster587.advancedRocketry.entity.EntityRocket;
import zmaster587.advancedRocketry.item.ItemAsteroidChip;
import zmaster587.advancedRocketry.util.StorageChunk;
import zmaster587.libVulpes.util.BlockPosition;
import zmaster587.libVulpes.util.Vector3F;

public class MissionOreMining extends SatelliteBase implements IMission {

	long startWorldTime;
	double x,y,z;
	long duration;
	int launchDimension;
	StorageChunk rocketStorage;
	StatsRocket rocketStats;
	int worldId;

	//stores the coordinates of infrastructures, used for when the world loads/saves
	private LinkedList<BlockPosition> infrastructureCoords;

	public MissionOreMining(){
		infrastructureCoords = new LinkedList<BlockPosition>();
	}

	public MissionOreMining(long duration, EntityRocket entity, LinkedList<IInfrastructure> infrastructureCoords) {
		super();

		satelliteProperties.setId(zmaster587.advancedRocketry.dimension.DimensionManager.getInstance().getNextSatelliteId());

		startWorldTime = DimensionManager.getWorld(0).getTotalWorldTime();
		this.duration = duration;
		this.launchDimension = entity.worldObj.provider.dimensionId;
		rocketStorage = entity.storage;
		rocketStats = entity.stats;
		x = entity.posX;
		y = entity.posY;
		z = entity.posZ;
		worldId = entity.worldObj.provider.dimensionId;

		this.infrastructureCoords = new LinkedList<BlockPosition>();

		for(IInfrastructure tile : infrastructureCoords)
			this.infrastructureCoords.add(new BlockPosition(((TileEntity)tile).xCoord, ((TileEntity)tile).yCoord, ((TileEntity)tile).zCoord));
	}

	@Override
	public double getProgress(World world) {
		return (AdvancedRocketry.proxy.getWorldTimeUniversal(0) - startWorldTime) / (double)duration;
	}

	@Override
	public String getInfo(World world) {
		return null;
	}

	@Override
	public String getName() {
		return AdvancedRocketry.proxy.getLocalizedString("mission.asteroidmining.name");
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

		if(rocketStats.getDrillingPower() != 0f) {
			int distanceData, compositionData, massData, maxData;

			ItemStack stack = rocketStorage.getGuidanceComputer().getStackInSlot(0);

			if(stack != null) {

				distanceData = ((ItemAsteroidChip)stack.getItem()).getData(stack,DataType.DISTANCE);
				compositionData = ((ItemAsteroidChip)stack.getItem()).getData(stack,DataType.COMPOSITION);
				massData = ((ItemAsteroidChip)stack.getItem()).getData(stack,DataType.MASS);
				maxData = ((ItemAsteroidChip)stack.getItem()).getMaxData(stack);

				//fill the inventory of the rocket
				if(distanceData/(double)maxData > Math.random()) {
					int totalStacks = (int) (massData*Configuration.asteroidMiningMult*Math.random()*64);
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
			}
		}

		rocketStorage.getGuidanceComputer().setInventorySlotContents(0, null);
		EntityRocket rocket = new EntityRocket(DimensionManager.getWorld(launchDimension), rocketStorage, rocketStats, x, 999, z);

		World world = DimensionManager.getWorld(launchDimension);
		world.spawnEntityInWorld(rocket);
		rocket.setInOrbit(true);
		rocket.setInFlight(true);
		rocket.motionY = -1.0;

		for(BlockPosition i : infrastructureCoords) {
			TileEntity tile = world.getTileEntity(i.x, i.y, i.z);
			if(tile instanceof IInfrastructure) {
				((IInfrastructure)tile).unlinkMission();
				rocket.linkInfrastructure(((IInfrastructure)tile));
			}
		}
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
		nbt.setInteger("startDimid", worldId);

		NBTTagList itemList = new NBTTagList();
		for(int i = 0; i < infrastructureCoords.size(); i++)
		{
			BlockPosition inf = infrastructureCoords.get(i);

			NBTTagCompound tag = new NBTTagCompound();
			tag.setIntArray("loc", new int[] {inf.x, inf.y, inf.z});
			itemList.appendTag(tag);

		}
		nbt.setTag("infrastructure", itemList);


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
		worldId = nbt.getInteger("startDimid");

		NBTTagList tagList = nbt.getTagList("infrastructure", 10);
		infrastructureCoords.clear();

		for (int i = 0; i < tagList.tagCount(); i++) {
			int coords[] = tagList.getCompoundTagAt(i).getIntArray("loc");
			infrastructureCoords.add(new BlockPosition(coords[0], coords[1], coords[2]));
		}
	}

	@Override
	public long getMissionId() {
		return getId();
	}

	@Override
	public int getOriginatingDimention() {
		return worldId;
	}

	@Override
	public void unlinkInfrastructure(IInfrastructure tile) {

		BlockPosition pos = new BlockPosition(((TileEntity)tile).xCoord, ((TileEntity)tile).yCoord, ((TileEntity)tile).zCoord);
		infrastructureCoords.remove(pos);
	}
}
