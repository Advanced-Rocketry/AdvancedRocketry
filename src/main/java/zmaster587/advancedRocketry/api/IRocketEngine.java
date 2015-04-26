package zmaster587.advancedRocketry.api;

import net.minecraft.world.World;

public interface IRocketEngine {
	/*returns the amount of thrust per engine
	 * 1/100 meters per second per block
	 */
	public int getThrust(World world, int x, int y, int z);
	
	/*
	 * Returns base fuel consumption in mb/tick
	 */
	public int getFuelConsumptionRate(World world, int x, int y, int z);
}
