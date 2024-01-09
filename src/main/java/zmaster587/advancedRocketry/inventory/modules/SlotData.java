package zmaster587.advancedRocketry.inventory.modules;

import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import zmaster587.advancedRocketry.item.ItemData;

import org.jetbrains.annotations.NotNull;

public class SlotData extends Slot {

	public SlotData(IInventory p_i1824_1_, int p_i1824_2_, int p_i1824_3_,
			int p_i1824_4_) {
		super(p_i1824_1_, p_i1824_2_, p_i1824_3_, p_i1824_4_);

	}

	@Override
	public boolean isItemValid(@NotNull ItemStack stack) {
		if(stack.isEmpty() || stack.getItem() instanceof ItemData)
			return super.isItemValid(stack);
		return false;
	}

}
