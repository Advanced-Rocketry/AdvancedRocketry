package zmaster587.advancedRocketry.api;

import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

/**
 * Implemented by a block that can contain fuel
 */
public interface IFuelTank {
	
	//Returns 
	/**
	 * @param world
	 * @param pos
	 * @param state
	 * @return the capacity for the container
	 */
	int getMaxFill(World world, BlockPos pos , BlockState state);
}
