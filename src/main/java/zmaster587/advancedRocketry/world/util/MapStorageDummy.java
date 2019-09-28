package zmaster587.advancedRocketry.world.util;

import javax.annotation.Nullable;

import net.minecraft.world.storage.ISaveHandler;
import net.minecraft.world.storage.MapStorage;
import net.minecraft.world.storage.WorldSavedData;

public class MapStorageDummy extends MapStorage {

	public MapStorageDummy(ISaveHandler saveHandlerIn) {
		super(saveHandlerIn);
	}


    /**
     * Saves nothing
     */
    public void saveAllData()
    {
    }
    
    public int getUniqueDataId(String key)
    {
        return 0;
    }
    
    public void setData(String dataIdentifier, WorldSavedData data)
    {
    }
    
    @Nullable
    public WorldSavedData getOrLoadData(Class <? extends WorldSavedData > clazz, String dataIdentifier)
    {
        return null;
    }
}
