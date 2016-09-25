package zmaster587.advancedRocketry.item;

import net.minecraft.block.Block;
import net.minecraft.item.ItemBlockWithMetadata;
import net.minecraft.item.ItemStack;
import zmaster587.libVulpes.block.INamedMetaBlock;
import zmaster587.libVulpes.items.ItemBlockMeta;

public class ItemCrystalBlock extends ItemBlockMeta {
	
	public ItemCrystalBlock(Block p_i45326_1_) {
		super(p_i45326_1_, p_i45326_1_);
	}

	@Override
	public String getUnlocalizedName(ItemStack stack) {
		return ((INamedMetaBlock)this.field_150939_a).getUnlocalizedName(stack.getItemDamage());
	}
}
