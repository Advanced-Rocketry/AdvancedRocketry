package zmaster587.advancedRocketry.api;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public interface IMiningDrill {
	/**
	 * @return mining speed of the drill in blocks/tick
	 */
	public float getMiningSpeed(World world, BlockPos pos);
	
	/**
	 * @return power consumption in units/tick
	 */
	public int powerConsumption();
}
