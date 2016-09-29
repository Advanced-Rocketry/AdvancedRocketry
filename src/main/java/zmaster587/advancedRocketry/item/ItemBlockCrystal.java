package zmaster587.advancedRocketry.item;

import zmaster587.libVulpes.block.INamedMetaBlock;
import zmaster587.libVulpes.items.ItemBlockMeta;
import net.minecraft.block.Block;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;

public class ItemBlockCrystal extends ItemBlockMeta {

	public ItemBlockCrystal(Block p_i45326_1_) {
		super(p_i45326_1_);
	}

	@Override
	public String getUnlocalizedName(ItemStack stack) {
		return((INamedMetaBlock)Block.getBlockFromItem(stack.getItem())).getUnlocalizedName(stack.getItemDamage());
	}
}
