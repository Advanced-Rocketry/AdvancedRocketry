package zmaster587.advancedRocketry.api;

import net.minecraft.world.IWorldAccess;

public interface IMiningDrill {
	/**
	 * @return mining speed of the drill in blocks/tick
	 */
	public float getMiningSpeed(IWorldAccess world, int x, int y, int z);
	
	/**
	 * @return power consumption in units/tick
	 */
	public int powerConsumption();
}
