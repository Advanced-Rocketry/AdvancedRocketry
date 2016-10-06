package zmaster587.advancedRocketry.integration;

import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.Loader;

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
	};
	
	public static void initCompatRecipies() {
		if(gregtechLoaded) {
			
		}
	}
}
