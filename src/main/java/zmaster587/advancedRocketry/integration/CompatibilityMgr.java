package zmaster587.advancedRocketry.integration;

import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.ModLoader;
import zmaster587.advancedRocketry.integration.jei.ARPlugin;

public class CompatibilityMgr {

	public static boolean gregtechLoaded;
	public static boolean thermalExpansionLoaded;
	public static boolean isSpongeInstalled;

	public CompatibilityMgr() {
		gregtechLoaded = false;
		thermalExpansionLoaded = false;
	}
	
	public static void getLoadedMods() {
		thermalExpansionLoaded = ModList.get().isLoaded("ThermalExpansion");
		gregtechLoaded = ModList.get().isLoaded("gregtech_addon");
	}
	
	public static void initCompatRecipes() {
		if(gregtechLoaded) {
			
		}
	}
	
	public static void reloadRecipes() {
		try {
			Class<?> clazz = Class.forName("mezz.jei.api.BlankModPlugin");
			//ARPlugin.reload();
		}
		catch(ClassNotFoundException e) {
			//Hush
		}
	}
}
