package zmaster587.advancedRocketry.block;

import net.minecraft.block.AbstractBlock;
import net.minecraft.block.FlowingFluidBlock;
import net.minecraft.fluid.FlowingFluid;

public class BlockFluid extends FlowingFluidBlock {// implements IBlockColor {
	
	public BlockFluid(java.util.function.Supplier<? extends FlowingFluid> supplier, AbstractBlock.Properties properties) {
		
		super(supplier, properties);
	}
	

	/*@Override
	public int colorMultiplier(BlockState state, IBlockAccess worldIn,
			BlockPos pos, int tintIndex) {
		
		return ((BlockFluid)state.getBlock()).getFluid().getColor();
	}*/
	
	
   
}
