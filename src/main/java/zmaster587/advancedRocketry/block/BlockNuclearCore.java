package zmaster587.advancedRocketry.block;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import zmaster587.advancedRocketry.api.ARConfiguration;
import zmaster587.advancedRocketry.api.IRocketNuclearCore;

public class BlockNuclearCore extends Block implements IRocketNuclearCore {

	public BlockNuclearCore(Properties properties) {
		super(properties);
	}

	@Override
	public int getMaxThrust(World world, BlockPos pos) { return (int)(1000 * ARConfiguration.getCurrentConfig().nuclearCoreThrustRatio.get()); }


}
