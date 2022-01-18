package zmaster587.advancedRocketry.api;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import zmaster587.advancedRocketry.api.fuel.FuelRegistry.FuelType;

/**
 * Must be implemented by any block that is to be treated like a rocket engine
 *
 */
public interface IRocketEngine {
	/**
	 * amount of thrust per engine
	 * @return meters per tick per block
	 */
	int getThrust(World world, BlockPos pos);

	/**
	 * fuel type of the block
	 * @return fuel type
	 */
	FuelType getFuelType(World world, BlockPos pos);
	
	/**
	 * @return base fuel consumption in mb/tick
	 */
	int getFuelConsumptionRate(World world, int x, int y, int z);
}
