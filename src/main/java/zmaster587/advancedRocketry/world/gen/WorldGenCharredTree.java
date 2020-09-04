package zmaster587.advancedRocketry.world.gen;

import net.minecraft.block.Block;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MutableBoundingBox;
import net.minecraft.world.World;
import net.minecraft.world.gen.IWorldGenerationReader;
import net.minecraft.world.gen.feature.BaseTreeFeatureConfig;
import net.minecraft.world.gen.foliageplacer.FoliagePlacer;
import net.minecraft.world.gen.trunkplacer.StraightTrunkPlacer;
import net.minecraft.world.gen.trunkplacer.TrunkPlacerType;
import zmaster587.advancedRocketry.api.AdvancedRocketryBlocks;
import zmaster587.advancedRocketry.api.AdvancedRocktryTrees;

import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.Set;

import com.google.common.collect.ImmutableList;

public class WorldGenCharredTree extends StraightTrunkPlacer {
	
    /** The minimum height of a generated tree. */
    private final int minTreeHeight;
	
    public WorldGenCharredTree(int i, int j, int k, int minHeight)
    {
        super(i,j,k);
        this.minTreeHeight = minHeight;
    }
    
    public List<FoliagePlacer.Foliage> func_230382_a_(IWorldGenerationReader p_230382_1_, Random p_230382_2_, int p_230382_3_, BlockPos p_230382_4_, Set<BlockPos> p_230382_5_, MutableBoundingBox p_230382_6_, BaseTreeFeatureConfig p_230382_7_) {
        func_236909_a_(p_230382_1_, p_230382_4_.down());

        for(int i = 0; i < p_230382_3_; ++i) {
           func_236911_a_(p_230382_1_, p_230382_2_, p_230382_4_.up(i), p_230382_5_, p_230382_6_, p_230382_7_);
        }

        return new LinkedList<FoliagePlacer.Foliage>();
     }
    
    protected TrunkPlacerType<?> func_230381_a_() {
        return AdvancedRocktryTrees.CHARRED_TREE;
     }
}
