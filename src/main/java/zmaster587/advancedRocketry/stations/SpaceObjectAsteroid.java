package zmaster587.advancedRocketry.stations;

import java.util.HashMap;
import java.util.Map.Entry;

import zmaster587.advancedRocketry.api.Configuration;
import zmaster587.advancedRocketry.api.DataStorage.DataType;
import zmaster587.advancedRocketry.api.dimension.IDimensionProperties;
import zmaster587.advancedRocketry.api.satellite.IDataHandler;
import zmaster587.advancedRocketry.dimension.DimensionManager;
import zmaster587.advancedRocketry.world.util.MultiData;
import net.minecraft.block.Block;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants.NBT;

public class SpaceObjectAsteroid extends SpaceObjectBase implements IDataHandler {
	HashMap<Block, Integer> compositionMapping;
	int numberOfBlocks;
	long uuid;
	MultiData data;
	
	public SpaceObjectAsteroid() {
		data = new MultiData();
		data.setMaxData(5000);
		setId(-1);
	}
	
	public SpaceObjectAsteroid(HashMap<Block, Integer> compositionMapping, long uuid, int numBlocks) {
		this();
		this.numberOfBlocks = numBlocks;
		this.compositionMapping = compositionMapping;
		this.uuid = uuid;
	}
	
	public boolean registered() {
		return getId() != -1;
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
		
		SpaceObjectManager.getSpaceManager().registerTemporarySpaceObject(this, -1,net.minecraftforge.common.DimensionManager.getWorld(Configuration.spaceDimId).getTotalWorldTime() + 100000);
	}
	
	public static void generateAsteroid(World world, int x, int y, int z) {
		
	}
	
	@Override
	public void writeToNbt(NBTTagCompound nbt) {
		super.writeToNbt(nbt);
		
		NBTTagList list = new NBTTagList();
		for(Entry<Block, Integer> entry : compositionMapping.entrySet()) {
			NBTTagCompound tag = new NBTTagCompound();
			tag.setInteger("id", Block.getIdFromBlock(entry.getKey()));
			tag.setInteger("amt", entry.getValue());
			list.appendTag(tag);
		}
		nbt.setTag("composition", list);
		nbt.setInteger("numBlocks", numberOfBlocks);
		nbt.setLong("uuid",uuid);
		data.writeToNBT(nbt);
	}
	@Override
	public void readFromNbt(NBTTagCompound nbt) {
		super.readFromNbt(nbt);
		
		NBTTagList list = nbt.getTagList("composition", NBT.TAG_COMPOUND);
		compositionMapping.clear();
		for(int i = 0; i < list.tagCount(); i++) {
			NBTTagCompound tag = list.getCompoundTagAt(i);
			int blockId = tag.getInteger("id");
			int rarity = tag.getInteger("amt");
			compositionMapping.put(Block.getBlockById(blockId), rarity);
		}
		
		numberOfBlocks = nbt.getInteger("numBlocks");
		uuid = nbt.getLong("uuid");
		data.readFromNBT(nbt);
	}

	@Override
	public int extractData(int maxAmount, DataType type, EnumFacing dir, boolean commit) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int addData(int maxAmount, DataType type, EnumFacing dir, boolean commit) {
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
