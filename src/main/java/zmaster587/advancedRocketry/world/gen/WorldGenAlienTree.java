package zmaster587.advancedRocketry.world.gen;

import net.minecraft.block.RotatedPillarBlock;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MutableBoundingBox;
import net.minecraft.world.gen.IWorldGenerationReader;
import net.minecraft.world.gen.feature.BaseTreeFeatureConfig;
import net.minecraft.world.gen.foliageplacer.FoliagePlacer;
import net.minecraft.world.gen.trunkplacer.StraightTrunkPlacer;
import zmaster587.advancedRocketry.api.AdvancedRocketryBlocks;

import java.util.LinkedList;
import java.util.List;

import java.util.Random;
import java.util.Set;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public class WorldGenAlienTree extends StraightTrunkPlacer {
	
   public static final Codec<WorldGenAlienTree> codec = RecordCodecBuilder.create((p_236902_0_) -> getAbstractTrunkCodec(p_236902_0_).apply(p_236902_0_, WorldGenAlienTree::new));

	public WorldGenAlienTree(int i, int j, int k) {
		//3, 11, 0
		super(i, j, k);
	}
}
