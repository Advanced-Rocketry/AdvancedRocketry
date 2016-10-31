package zmaster587.advancedRocketry.block;

import net.minecraft.block.material.Material;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class BlockAdvancedRocketMotor extends BlockRocketMotor {

	public BlockAdvancedRocketMotor(Material mat) {
		super(mat);
	}

	@Override
	public int getThrust(World world, BlockPos pos) {
		return 50;
	}
}
