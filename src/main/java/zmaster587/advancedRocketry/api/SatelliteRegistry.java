package zmaster587.advancedRocketry.api;

import java.util.HashMap;
import java.util.Map.Entry;
import java.util.logging.Logger;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import zmaster587.advancedRocketry.AdvancedRocketry;
import zmaster587.advancedRocketry.api.satellite.SatelliteBase;
import zmaster587.advancedRocketry.api.satellite.SatelliteProperties;
import zmaster587.advancedRocketry.satellite.SatelliteDefunct;

public class SatelliteRegistry {
	static HashMap<String, Class<? extends SatelliteBase>> registry = new HashMap<String, Class<? extends SatelliteBase>>();

	static HashMap<ItemStack, SatelliteProperties> itemPropertiesRegistry = new HashMap<ItemStack, SatelliteProperties>();

	/**
	 * Registers an itemStack with a satellite property, this is used in the Satellite Builder to determine the effect of a component
	 * @param stack stack to register, stacksize insensitive
	 * @param properties Satellite Properties to register the ItemStack with
	 */
	public static void registerSatelliteProperty(ItemStack stack, SatelliteProperties properties) {
		if(stack == null) {
			Logger.getLogger(AdvancedRocketry.modId).warning("null satellite property being registered!");
		}
		else if(!itemPropertiesRegistry.containsKey(stack))
			itemPropertiesRegistry.put(stack, properties);
		else
			Logger.getLogger(AdvancedRocketry.modId).warning("Duplicate satellite property being registered for " + stack.toString());
	}

	/**
	 * @param stack ItemStack to get the SatelliteProperties of, stacksize insensative 
	 * @return the registered SatelliteProperties of the stack, or null if not registered
	 */
	public static SatelliteProperties getSatelliteProperty(ItemStack stack) {
		
		for(ItemStack keyStack : itemPropertiesRegistry.keySet()) {
			if(keyStack.getItem() == stack.getItem() && ( !keyStack.getHasSubtypes() || keyStack.getItemDamage() == stack.getItemDamage()) ) {
				return itemPropertiesRegistry.get(keyStack);
			}
		}
		
		return null;
	}

	/**
	 * Registers a satellite class with a string ID, used for loading and saving satellites
	 * @param name String id to register the satellite class to
	 * @param clazz class to register
	 */
	public static void registerSatellite(String name, Class<? extends SatelliteBase> clazz) {
		registry.put(name, clazz);
	}

	/**
	 * @param clazz Satellite Class to get the String identifier for 
	 * @return String identifier for clazz
	 */
	public static String getKey(Class<? extends SatelliteBase> clazz) {

		for(Entry<String, Class<? extends SatelliteBase>> entrySet : registry.entrySet()) {
			if(entrySet.getValue() == clazz)
				return entrySet.getKey();
		}
		//TODO: throw exception
		return "poo";
	}

	/**
	 * Handles loading a satellite from nbt, does NOT add it to list of functioning satellites
	 * @param nbt NBT to create a satellite Object from
	 * @return Satellite constructed from the passed NBT
	 */
	public static SatelliteBase createFromNBT(NBTTagCompound nbt) {
		SatelliteBase satellite = getSatallite(nbt.getString("dataType"));

		satellite.readFromNBT(nbt);

		return satellite;
	}

	/**
	 * @param name String identifier for a satellite
	 * @return new satellite registered to the String identifier, SatelliteDefunct otherwise
	 */
	public static SatelliteBase getSatallite(String name) {
		Class<? extends SatelliteBase> clazz = registry.get(name);

		if(clazz == null) {
			return new SatelliteDefunct();
		}
		else
			try {
				return clazz.newInstance();
			} catch( Exception e)  {
				return new SatelliteDefunct();
			}
	}
}
