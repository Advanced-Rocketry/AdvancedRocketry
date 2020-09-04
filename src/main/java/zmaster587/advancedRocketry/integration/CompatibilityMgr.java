package zmaster587.advancedRocketry.integration;

import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.ModLoader;
import zmaster587.advancedRocketry.integration.jei.ARPlugin;

public class CompatibilityMgr {

	public static boolean gregtechLoaded;
	public static boolean thermalExpansion;
	
	public CompatibilityMgr() {
		gregtechLoaded = false;
		thermalExpansion = false;
	}
	
	public static void getLoadedMods() {
		thermalExpansion = ModList.get().isLoaded("ThermalExpansion");
		gregtechLoaded = ModList.get().isLoaded("gregtech_addon");
	};
	
	public static void initCompatRecipies() {
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
