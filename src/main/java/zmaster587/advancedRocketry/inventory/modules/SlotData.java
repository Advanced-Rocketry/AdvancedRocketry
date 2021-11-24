package zmaster587.advancedRocketry.inventory.modules;

import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import zmaster587.advancedRocketry.item.ItemDataChip;

import javax.annotation.Nonnull;

public class SlotData extends Slot {

	public SlotData(IInventory p_i1824_1_, int p_i1824_2_, int p_i1824_3_,
			int p_i1824_4_) {
		super(p_i1824_1_, p_i1824_2_, p_i1824_3_, p_i1824_4_);

	}

	@Override
	public boolean isItemValid(@Nonnull ItemStack stack) {
		if(stack.isEmpty() || stack.getItem() instanceof ItemDataChip)
			return super.isItemValid(stack);
		return false;
	}

}
