package zmaster587.advancedRocketry.item;

import zmaster587.advancedRocketry.block.BlockOre;
import zmaster587.advancedRocketry.block.INamedMetaBlock;
import net.minecraft.block.Block;
import net.minecraft.item.ItemBlockWithMetadata;
import net.minecraft.item.ItemStack;
import net.minecraft.util.StatCollector;

public class ItemOre extends ItemBlockWithMetadata {

	public ItemOre(Block p_i45326_1_) {
		super(p_i45326_1_, p_i45326_1_);
	}

	@Override
	public String getUnlocalizedName(ItemStack stack) {
		return ((INamedMetaBlock)this.field_150939_a).getUnlocalizedName(stack.getItemDamage());
	}
	
	@Override
    public String getItemStackDisplayName(ItemStack stack)
    {
        return ("" + StatCollector.translateToLocal(this.getUnlocalizedNameInefficiently(stack) + ".name") + " " + StatCollector.translateToLocal("type." + ((BlockOre)this.field_150939_a).getProduct().toString().toLowerCase() + ".name")).trim();
    }
}
