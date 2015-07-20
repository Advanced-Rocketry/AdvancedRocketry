package zmaster587.advancedRocketry.item;

import net.minecraft.block.Block;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemBlockWithMetadata;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class ItemBlockMeta extends  ItemBlockWithMetadata {

	public ItemBlockMeta(Block p_i45326_1_) {
		super(p_i45326_1_, p_i45326_1_);
	}

	@Override
	public String getUnlocalizedName(ItemStack stack) {
		return super.getUnlocalizedName(stack) + "." + stack.getItemDamage();
	}

}
