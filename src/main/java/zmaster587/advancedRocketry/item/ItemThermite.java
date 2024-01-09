package zmaster587.advancedRocketry.item;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import org.jetbrains.annotations.NotNull;

public class ItemThermite extends Item {

	@Override
	public int getItemBurnTime(@NotNull ItemStack itemStack) {
		return 6000;
	}
	
}
