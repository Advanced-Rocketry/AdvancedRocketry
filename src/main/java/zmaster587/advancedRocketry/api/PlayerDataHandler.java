package zmaster587.advancedRocketry.api;

import java.util.HashMap;
import java.util.Map.Entry;

import zmaster587.advancedRocketry.api.DataStorage.DataType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraftforge.common.IExtendedEntityProperties;
/**
 * Currently unused
 *
 */
public class PlayerDataHandler implements IExtendedEntityProperties {
	
	public final static String IDENTIFIER = "planetdata";
	
	private HashMap<Integer, MultiData> dataMap;
	
	public PlayerDataHandler() {
		dataMap = new HashMap<Integer, MultiData>();
	}
	
	public static void addData(EntityPlayer player, int planetId, int amount, DataType dataType) {
		PlayerDataHandler handler = (PlayerDataHandler)player.getExtendedProperties(IDENTIFIER);
		
		MultiData planetData = handler.dataMap.get(planetId);
		
		if(planetData == null) {
			planetData = new MultiData();
			handler.dataMap.put(planetId, planetData);
		}
		
		planetData.addData(amount, dataType);
	}

	public static int getData(EntityPlayer player, int planetId, DataType type) {
		PlayerDataHandler handler = (PlayerDataHandler)player.getExtendedProperties(IDENTIFIER);
		MultiData planetData = handler.dataMap.get(planetId);
		
		if(planetData == null)
			return 0;
		
		return planetData.getDataAmount(type);
	}
	
	@Override
	public void saveNBTData(NBTTagCompound nbt) {
		NBTTagCompound tag = new NBTTagCompound();
		for(Entry<Integer, MultiData> dataEntry : dataMap.entrySet()) {
			NBTTagCompound nbtData = new NBTTagCompound();
			dataEntry.getValue().writeToNBT(nbtData);
			
			tag.setTag(dataEntry.getKey().toString(), nbtData);
		}
		
		nbt.setTag("advancedRocketry", tag);
	}

	@Override
	public void loadNBTData(NBTTagCompound nbt) {
		
		if(nbt.hasKey("advancedRocketry")) {
		
			NBTTagCompound tag = nbt.getCompoundTag("advancedRocketry");
		for(Object key : tag.func_150296_c()) {
			String keyString = (String)key;
			
			MultiData data = new MultiData();
			data.readFromNBT(tag.getCompoundTag(keyString));
			dataMap.put(Integer.decode(keyString), data);
		}
	}
	}

	@Override
	public void init(Entity entity, World world) {		
	}
}
