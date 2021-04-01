package zmaster587.advancedRocketry.stations;

import net.minecraft.block.Block;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants.NBT;
import net.minecraftforge.registries.ForgeRegistries;
import zmaster587.advancedRocketry.api.ARConfiguration;
import zmaster587.advancedRocketry.api.Constants;
import zmaster587.advancedRocketry.api.DataStorage.DataType;
import zmaster587.advancedRocketry.api.dimension.IDimensionProperties;
import zmaster587.advancedRocketry.api.satellite.IDataHandler;
import zmaster587.advancedRocketry.world.util.MultiData;
import zmaster587.libVulpes.util.ZUtils;

import java.util.HashMap;
import java.util.Map.Entry;

public class SpaceObjectAsteroid extends SpaceObjectBase implements IDataHandler {
	HashMap<Block, Integer> compositionMapping;
	int numberOfBlocks;
	long uuid;
	MultiData data;
	
	public SpaceObjectAsteroid() {
		data = new MultiData();
		data.setMaxData(5000);
		setId(Constants.INVALID_PLANET);
	}
	
	public SpaceObjectAsteroid(HashMap<Block, Integer> compositionMapping, long uuid, int numBlocks) {
		this();
		this.numberOfBlocks = numBlocks;
		this.compositionMapping = compositionMapping;
		this.uuid = uuid;
	}
	
	public boolean registered() {
		return getId() != Constants.INVALID_PLANET;
	}
	
	public long getAsteroidId() {
		return uuid;
	}
	
	public void setAsteroidId(long id) {
		uuid = id;
	}
	
	public HashMap<Block, Integer> getCompositionMapping() {
		return compositionMapping;
	}
	
	public int getNumberOfBlocks() {
		return numberOfBlocks;
	}
	
	public void registerWithSpaceObjectManager() {
		
		SpaceObjectManager.getSpaceManager().registerTemporarySpaceObject(this, Constants.INVALID_PLANET, ZUtils.getWorld(ARConfiguration.GetSpaceDimId()).getGameTime() + 100000);
	}
	
	public static void generateAsteroid(World world, int x, int y, int z) {
		
	}
	
	@Override
	public void writeToNbt(CompoundNBT nbt) {
		super.writeToNbt(nbt);
		
		ListNBT list = new ListNBT();
		for(Entry<Block, Integer> entry : compositionMapping.entrySet()) {
			CompoundNBT tag = new CompoundNBT();
			tag.putString("id", entry.getKey().getRegistryName().toString());
			tag.putInt("amt", entry.getValue());
			list.add(tag);
		}
		nbt.put("composition", list);
		nbt.putInt("numBlocks", numberOfBlocks);
		nbt.putLong("uuid",uuid);
		data.writeToNBT(nbt);
	}
	@Override
	public void readFromNbt(CompoundNBT nbt) {
		super.readFromNbt(nbt);
		
		ListNBT list = nbt.getList("composition", NBT.TAG_COMPOUND);
		compositionMapping.clear();
		for(int i = 0; i < list.size(); i++) {
			CompoundNBT tag = list.getCompound(i);
			ResourceLocation blockId = new ResourceLocation(tag.getString("id"));
			int rarity = tag.getInt("amt");
			compositionMapping.put(ForgeRegistries.BLOCKS.getValue(blockId), rarity);
		}
		
		numberOfBlocks = nbt.getInt("numBlocks");
		uuid = nbt.getLong("uuid");
		data.readFromNBT(nbt);
	}

	@Override
	public double getInsolationMultiplier() {
		return 0;
	}

	@Override
	public int extractData(int maxAmount, DataType type, Direction dir, boolean commit) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int addData(int maxAmount, DataType type, Direction dir, boolean commit) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void setProperties(IDimensionProperties properties) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public float getOrbitalDistance() {
		return getProperties().getParentOrbitalDistance();
	}

	@Override
	public void setOrbitalDistance(float finalVel) {
	}
}
