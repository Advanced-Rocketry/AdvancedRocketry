package zmaster587.advancedRocketry.item;

import java.util.List;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
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
	
	@Override
	public void addInformation(ItemStack p_77624_1_, EntityPlayer p_77624_2_,
			List p_77624_3_, boolean p_77624_4_) {
		
		super.addInformation(p_77624_1_, p_77624_2_, p_77624_3_, p_77624_4_);
	}
}
