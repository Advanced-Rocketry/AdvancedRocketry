package zmaster587.advancedRocketry.world.gen;

import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MutableBoundingBox;
import net.minecraft.world.gen.IWorldGenerationReader;
import net.minecraft.world.gen.feature.BaseTreeFeatureConfig;
import net.minecraft.world.gen.foliageplacer.FoliagePlacer;
import net.minecraft.world.gen.trunkplacer.StraightTrunkPlacer;

import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.Set;

public class WorldGenCharredTree extends StraightTrunkPlacer {
	
    /** The minimum height of a generated tree. */
    private final int minTreeHeight;
	
    public WorldGenCharredTree(int i, int j, int k, int minHeight) {
        super(i,j,k);
        this.minTreeHeight = minHeight;
    }
}
