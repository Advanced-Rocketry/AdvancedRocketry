package zmaster587.advancedRocketry.api;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

/**
 * Must be implemented by any block that is to be treated like a nuclear core
 */
public interface IRocketNuclearCore {
    /**
     * amount of thrust per core
     *
     * @return meters per tick per block
     */
    int getMaxThrust(World world, BlockPos pos);
}
