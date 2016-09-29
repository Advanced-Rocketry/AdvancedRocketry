package zmaster587.advancedRocketry.block;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.color.IBlockColor;
import net.minecraft.client.renderer.color.IItemColor;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;

public class CrystalColorizer implements IBlockColor, IItemColor {

	@Override
	public int getColorFromItemstack(ItemStack stack, int tintIndex) {
		return BlockCrystal.EnumCrystal.values()[stack.getItemDamage()].getColor();
		
	}

	@Override
	public int colorMultiplier(IBlockState state, IBlockAccess worldIn,
			BlockPos pos, int tintIndex) {
		return state.getValue(BlockCrystal.CRYSTALPROPERTY).getColor();
	}

}
