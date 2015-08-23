package zmaster587.advancedRocketry.api;

import net.minecraft.world.World;

public interface IRocketEngine {
	/**
	 * TODO: adjust.<br>  
	 * amount of thrust per engine
	 * @return meters per tick per block
	 */
	public int getThrust(World world, int x, int y, int z);
	
	/**
	 * @return base fuel consumption in mb/tick
	 */
	public int getFuelConsumptionRate(World world, int x, int y, int z);
}
