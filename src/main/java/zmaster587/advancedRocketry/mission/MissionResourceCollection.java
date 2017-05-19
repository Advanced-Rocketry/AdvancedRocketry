package zmaster587.advancedRocketry.mission;


import java.util.LinkedList;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.DimensionManager;
import zmaster587.advancedRocketry.AdvancedRocketry;
import zmaster587.advancedRocketry.api.IInfrastructure;
import zmaster587.advancedRocketry.api.IMission;
import zmaster587.advancedRocketry.api.StatsRocket;
import zmaster587.advancedRocketry.api.satellite.SatelliteBase;
import zmaster587.advancedRocketry.entity.EntityRocket;
import zmaster587.advancedRocketry.util.StorageChunk;
import zmaster587.libVulpes.LibVulpes;
import zmaster587.libVulpes.util.HashedBlockPosition;


public abstract class MissionResourceCollection extends SatelliteBase implements IMission {


	long startWorldTime;
	double x,y,z;
	long duration;
	int launchDimension;
	StorageChunk rocketStorage;
	StatsRocket rocketStats;
	int worldId;
	NBTTagCompound missionPersistantNBT;

	//stores the coordinates of infrastructures, used for when the world loads/saves
	protected LinkedList<HashedBlockPosition> infrastructureCoords;

	public MissionResourceCollection(){
		infrastructureCoords = new LinkedList<HashedBlockPosition>();
	}

	public MissionResourceCollection(long duration, EntityRocket entity, LinkedList<IInfrastructure> infrastructureCoords) {
		super();
		missionPersistantNBT = new NBTTagCompound();
		entity.writeMissionPersistantNBT(missionPersistantNBT);
		
		satelliteProperties.setId(zmaster587.advancedRocketry.dimension.DimensionManager.getInstance().getNextSatelliteId());

		startWorldTime = DimensionManager.getWorld(0).getTotalWorldTime();
		this.duration = duration;
		this.launchDimension = entity.world.provider.getDimension();
		rocketStorage = entity.storage;
		rocketStats = entity.stats;
		x = entity.posX;
		y = entity.posY;
		z = entity.posZ;
		worldId = entity.world.provider.getDimension();

		this.infrastructureCoords = new LinkedList<HashedBlockPosition>();

		for(IInfrastructure tile : infrastructureCoords)
			this.infrastructureCoords.add(new HashedBlockPosition(((TileEntity)tile).getPos()));
	}

	@Override
	public double getProgress(World world) {
		return (AdvancedRocketry.proxy.getWorldTimeUniversal(0) - startWorldTime) / (double)duration;
	}
	
	@Override
	public int getTimeRemainingInSeconds() {
		return (int)(( duration -AdvancedRocketry.proxy.getWorldTimeUniversal(0) + startWorldTime)/20);
	}

	@Override
	public String getInfo(World world) {
		return null;
	}

	@Override
	public String getName() {
		return LibVulpes.proxy.getLocalizedString("mission.asteroidmining.name");
	}

	@Override
	public boolean performAction(EntityPlayer player, World world, BlockPos pos) {
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
	public abstract void onMissionComplete();

	@Override
	public void tickEntity() {
		if(getProgress(DimensionManager.getWorld(getDimensionId())) >= 1 && !DimensionManager.getWorld(0).isRemote) {
			setDead();
			onMissionComplete();
		}
	}

	public void writeToNBT(NBTTagCompound nbt) {
		super.writeToNBT(nbt);
		
		nbt.setTag("persist", missionPersistantNBT);
		
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
		nbt.setInteger("launchDim", launchDimension);

		NBTTagList itemList = new NBTTagList();
		for(int i = 0; i < infrastructureCoords.size(); i++)
		{
			HashedBlockPosition inf = infrastructureCoords.get(i);

			NBTTagCompound tag = new NBTTagCompound();
			tag.setIntArray("loc", new int[] {inf.x, inf.y, inf.z});
			itemList.appendTag(tag);

		}
		nbt.setTag("infrastructure", itemList);
	}

	public void readFromNBT(NBTTagCompound nbt) {
		super.readFromNBT(nbt);
		
		missionPersistantNBT = nbt.getCompoundTag("persist" );

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
		launchDimension = nbt.getInteger("launchDim");

		NBTTagList tagList = nbt.getTagList("infrastructure", 10);
		infrastructureCoords.clear();

		for (int i = 0; i < tagList.tagCount(); i++) {
			int coords[] = tagList.getCompoundTagAt(i).getIntArray("loc");
			infrastructureCoords.add(new HashedBlockPosition(coords[0], coords[1], coords[2]));
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
		HashedBlockPosition pos = new HashedBlockPosition(((TileEntity)tile).getPos());
		infrastructureCoords.remove(pos);
	}

}
