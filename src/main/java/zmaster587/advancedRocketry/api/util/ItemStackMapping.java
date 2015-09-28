package zmaster587.advancedRocketry.api.util;

import net.minecraft.item.ItemStack;

public class ItemStackMapping {
	ItemStack stack;
	public ItemStackMapping(ItemStack stack) {
		this.stack = stack;
	}

	@Override
	public String toString() {
		return stack.toString();
	}

	@Override
	public boolean equals(Object obj) {
		if(obj instanceof ItemStack)
			return stack.isItemEqual((ItemStack) obj);
		return super.equals(obj);
	}
}