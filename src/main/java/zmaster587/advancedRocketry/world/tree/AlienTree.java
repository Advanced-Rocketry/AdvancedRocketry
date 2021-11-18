package zmaster587.advancedRocketry.world.tree;

import java.util.Random;

import net.minecraft.block.trees.Tree;
import net.minecraft.world.gen.feature.BaseTreeFeatureConfig;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import zmaster587.advancedRocketry.api.AdvancedRocketryBiomes;

import javax.annotation.ParametersAreNonnullByDefault;

public class AlienTree extends Tree {

	@Override
	@ParametersAreNonnullByDefault
	protected ConfiguredFeature<BaseTreeFeatureConfig, ?> getTreeFeature(Random randomIn, boolean p_225546_2_) {
		return AdvancedRocketryBiomes.ALIEN_TREE;
	}

}
