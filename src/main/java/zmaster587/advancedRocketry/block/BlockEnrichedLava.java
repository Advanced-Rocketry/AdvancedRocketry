package zmaster587.advancedRocketry.block;

import java.util.function.Supplier;

import net.minecraft.block.AbstractBlock;
import net.minecraft.block.FlowingFluidBlock;
import net.minecraft.block.material.Material;
import net.minecraft.fluid.FlowingFluid;
import net.minecraft.fluid.Fluid;

public class BlockEnrichedLava extends FlowingFluidBlock {

	public BlockEnrichedLava(Supplier<FlowingFluid> fluidEnrichedLava, AbstractBlock.Properties properties) {
		super(fluidEnrichedLava, properties);
	}
	
	//TODO: add eyecandy

}
