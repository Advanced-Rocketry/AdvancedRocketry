package zmaster587.advancedRocketry.block;

import net.minecraft.block.AbstractBlock;
import net.minecraft.block.material.Material;
import net.minecraft.fluid.FlowingFluid;
import net.minecraft.fluid.Fluid;

public class BlockEnrichedLava extends BlockFluid {

	public BlockEnrichedLava(java.util.function.Supplier<? extends FlowingFluid> supplier, AbstractBlock.Properties properties) {
		super(supplier, properties);
	}
	
	//TODO: add eyecandy

}
