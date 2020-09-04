package zmaster587.advancedRocketry.mission;


import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.thread.EffectiveSide;
import zmaster587.advancedRocketry.AdvancedRocketry;
import zmaster587.advancedRocketry.api.IInfrastructure;
import zmaster587.advancedRocketry.api.IMission;
import zmaster587.advancedRocketry.api.StatsRocket;
import zmaster587.advancedRocketry.api.satellite.SatelliteBase;
import zmaster587.advancedRocketry.entity.EntityRocket;
import zmaster587.advancedRocketry.util.StorageChunk;
import zmaster587.libVulpes.LibVulpes;
import zmaster587.libVulpes.util.HashedBlockPosition;
import zmaster587.libVulpes.util.ZUtils;

import java.util.LinkedList;


public abstract class MissionResourceCollection extends SatelliteBase implements IMission {


	long startWorldTime;
	double x,y,z;
	long duration;
	ResourceLocation launchDimension;
	StorageChunk rocketStorage;
	StatsRocket rocketStats;
	ResourceLocation worldId;
	CompoundNBT missionPersistantNBT;

	//stores the coordinates of infrastructures, used for when the world loads/saves
	protected LinkedList<HashedBlockPosition> infrastructureCoords;

	public MissionResourceCollection(){
		infrastructureCoords = new LinkedList<HashedBlockPosition>();
	}

	public MissionResourceCollection(long duration, EntityRocket entity, LinkedList<IInfrastructure> infrastructureCoords) {
		super();
		missionPersistantNBT = new CompoundNBT();
		entity.writeMissionPersistantNBT(missionPersistantNBT);
		
		satelliteProperties.setId(zmaster587.advancedRocketry.dimension.DimensionManager.getInstance().getNextSatelliteId());

		startWorldTime = entity.world.getServer().getServerTime();
		this.duration = duration;
		this.launchDimension = ZUtils.getDimensionIdentifier(entity.world);
		rocketStorage = entity.storage;
		rocketStats = entity.stats;
		x = entity.getPosX();
		y = entity.getPosY();
		z = entity.getPosZ();
		worldId = ZUtils.getDimensionIdentifier(entity.world);

		this.infrastructureCoords = new LinkedList<HashedBlockPosition>();

		for(IInfrastructure tile : infrastructureCoords)
			this.infrastructureCoords.add(new HashedBlockPosition(((TileEntity)tile).getPos()));
	}

	@Override
	public double getProgress(World world) {
		return (AdvancedRocketry.proxy.getWorldTimeUniversal() - startWorldTime) / (double)duration;
	}
	
	@Override
	public int getTimeRemainingInSeconds() {
		return (int)(( duration -AdvancedRocketry.proxy.getWorldTimeUniversal() + startWorldTime)/20);
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
	public boolean performAction(PlayerEntity player, World world, BlockPos pos) {
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
		if(EffectiveSide.get().isServer() && getProgress(ZUtils.getWorld(getDimensionId().get())) >= 1) {
			remove();
			onMissionComplete();
		}
	}

	public void writeToNBT(CompoundNBT nbt) {
		super.writeToNBT(nbt);
		
		nbt.put("persist", missionPersistantNBT);
		
		CompoundNBT nbt2 = new CompoundNBT();
		rocketStats.writeToNBT(nbt2);
		nbt.put("rocketStats", nbt2);

		nbt2 = new CompoundNBT();
		rocketStorage.writeToNBT(nbt2);
		nbt.put("rocketStorage", nbt2);

		nbt.putDouble("launchPosX", x);
		nbt.putDouble("launchPosY", y);
		nbt.putDouble("launchPosZ", z);

		nbt.putLong("startWorldTime", startWorldTime);
		nbt.putLong("duration", duration);
		nbt.putString("startDimid", worldId.toString());
		nbt.putString("launchDim", launchDimension.toString());

		ListNBT itemList = new ListNBT();
		for(int i = 0; i < infrastructureCoords.size(); i++)
		{
			HashedBlockPosition inf = infrastructureCoords.get(i);

			CompoundNBT tag = new CompoundNBT();
			tag.putIntArray("loc", new int[] {inf.x, inf.y, inf.z});
			itemList.add(tag);

		}
		nbt.put("infrastructure", itemList);
	}

	public void readFromNBT(CompoundNBT nbt) {
		super.readFromNBT(nbt);
		
		missionPersistantNBT = nbt.getCompound("persist" );

		rocketStats = new StatsRocket();
		rocketStats.readFromNBT(nbt.getCompound("rocketStats"));

		rocketStorage = new StorageChunk();
		rocketStorage.readFromNBT(nbt.getCompound("rocketStorage"));

		x = nbt.getDouble("launchPosX");
		y = nbt.getDouble("launchPosY");
		z = nbt.getDouble("launchPosZ");

		startWorldTime = nbt.getLong("startWorldTime");
		duration = nbt.getLong("duration");
		worldId = new ResourceLocation(nbt.getString("startDimid"));
		launchDimension = new ResourceLocation(nbt.getString("launchDim"));

		ListNBT tagList = nbt.getList("infrastructure", 10);
		infrastructureCoords.clear();

		for (int i = 0; i < tagList.size(); i++) {
			int coords[] = tagList.getCompound(i).getIntArray("loc");
			infrastructureCoords.add(new HashedBlockPosition(coords[0], coords[1], coords[2]));
		}
	}

	@Override
	public long getMissionId() {
		return getId();
	}

	@Override
	public ResourceLocation getOriginatingDimention() {
		return worldId;
	}

	@Override
	public void unlinkInfrastructure(IInfrastructure tile) {
		HashedBlockPosition pos = new HashedBlockPosition(((TileEntity)tile).getPos());
		infrastructureCoords.remove(pos);
	}

}
