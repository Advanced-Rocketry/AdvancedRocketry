package zmaster587.advancedRocketry.world.gen;

import java.util.Random;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenAbstractTree;

public class WorldGenNoTree extends WorldGenAbstractTree {

	public WorldGenNoTree(boolean p_i45448_1_) {
		super(p_i45448_1_);
	}

	@Override
	public boolean generate(World worldIn, Random rand, BlockPos position) {
		// TODO Auto-generated method stub
		return false;
	}
}
