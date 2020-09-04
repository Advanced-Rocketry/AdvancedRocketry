package zmaster587.advancedRocketry.util;

import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.ForgeFlowingFluid;
import net.minecraftforge.fluids.ForgeFlowingFluid.Properties;

public class FluidColored extends ForgeFlowingFluid {

	private static final ResourceLocation notFlowing = new ResourceLocation("advancedrocketry:blocks/fluid/oxygen_still");
	private static final ResourceLocation flowing = new ResourceLocation("advancedrocketry:blocks/fluid/oxygen_flow");
	
	public FluidColored(ForgeFlowingFluid.Properties properties) {
		super(properties);
		//this.setColor(color);
	}

	@Override
	public boolean isSource(FluidState state) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public int getLevel(FluidState p_207192_1_) {
		// TODO Auto-generated method stub
		return 0;
	}

}
