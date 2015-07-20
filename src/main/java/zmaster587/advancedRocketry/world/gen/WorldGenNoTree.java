package zmaster587.advancedRocketry.world.gen;

import java.util.Random;

import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenAbstractTree;

public class WorldGenNoTree extends WorldGenAbstractTree {

	public WorldGenNoTree(boolean p_i45448_1_) {
		super(p_i45448_1_);
	}

	@Override
	public boolean generate(World p_76484_1_, Random p_76484_2_,
			int p_76484_3_, int p_76484_4_, int p_76484_5_) {
		return false;
	}

}
