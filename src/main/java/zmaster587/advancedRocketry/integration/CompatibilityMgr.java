package zmaster587.advancedRocketry.integration;

import net.minecraftforge.fml.common.Loader;
import zmaster587.advancedRocketry.integration.jei.ARPlugin;

public class CompatibilityMgr {

	public static boolean gregtechLoaded;
	public static boolean thermalExpansion;
	
	public CompatibilityMgr() {
		gregtechLoaded = false;
		thermalExpansion = false;
	}
	
	public static void getLoadedMods() {
		thermalExpansion = Loader.isModLoaded("ThermalExpansion");
		gregtechLoaded = Loader.isModLoaded("gregtech_addon");
	}
	
	public static void initCompatRecipies() {
		if(gregtechLoaded) {
			
		}
	}
	
	public static void reloadRecipes() {
		try {
			Class<?> clazz = Class.forName("mezz.jei.api.BlankModPlugin");
			ARPlugin.reload();
		}
		catch(ClassNotFoundException e) {
			//Hush
		}
	}
}
