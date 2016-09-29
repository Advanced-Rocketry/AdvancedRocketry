package zmaster587.advancedRocketry.block;

import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.color.IBlockColor;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.fluids.BlockFluidClassic;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;

public class BlockFluid extends BlockFluidClassic implements IBlockColor {
	
	public BlockFluid(Fluid fluid, Material material) {
		super(fluid, material);
	}
	

	@Override
	public int colorMultiplier(IBlockState state, IBlockAccess worldIn,
			BlockPos pos, int tintIndex) {
		
		return ((BlockFluid)state.getBlock()).getFluid().getColor();
	}
	
	
   
}
