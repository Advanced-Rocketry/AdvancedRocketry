package zmaster587.advancedRocketry.api;

import net.minecraft.world.World;

/**
 * Implemented by a block that can contain fuel
 */
public interface IFuelTank {
	
	//Returns the capacity for the container
	public int getMaxFill(World world, int x, int y, int z , int meta);
}
