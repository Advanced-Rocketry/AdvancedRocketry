package zmaster587.advancedRocketry.api.armor;

import net.minecraft.item.ItemStack;
import zmaster587.advancedRocketry.api.IAtmosphere;

public interface IProtectiveArmor {

	boolean protectsFromSubstance(IAtmosphere atmosphere, ItemStack stack,
			boolean commitProtection);
}
