package zmaster587.advancedRocketry.item;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;

public class ItemFluid extends ItemBlock {

	public ItemFluid(Block block) {
		super(block);
	}
	
	@SideOnly(Side.CLIENT)
	@Override
	public int getColorFromItemStack(ItemStack stack, int meta) {
		return Block.getBlockFromItem(stack.getItem()).getRenderColor(meta);
	}
}
