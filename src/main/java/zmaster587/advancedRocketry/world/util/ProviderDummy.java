package zmaster587.advancedRocketry.world.util;

import net.minecraft.world.DimensionType;
import net.minecraft.world.WorldProvider;

public class ProviderDummy extends WorldProvider {


	@Override
	public DimensionType getDimensionType() {
		return DimensionType.NETHER;
	}
	
	@Override
	public float[] getLightBrightnessTable() {
		return super.getLightBrightnessTable();
	}

}
