package zmaster587.advancedRocketry.api;

import net.minecraft.world.World;

/**
 * Implemented by a block that can contain fuel
 */
public interface IFuelTank {
	
	//Returns 
	/**
	 * @param world
	 * @param x
	 * @param y
	 * @param z
	 * @param meta
	 * @return the capacity for the container
	 */
	public int getMaxFill(World world, int x, int y, int z , int meta);
}
