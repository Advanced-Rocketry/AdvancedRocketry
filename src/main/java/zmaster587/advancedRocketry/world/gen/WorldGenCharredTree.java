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

<<<<<<< HEAD
        for(int i = 0; i < p_230382_3_; ++i) {
           func_236911_a_(p_230382_1_, p_230382_2_, p_230382_4_.up(i), p_230382_5_, p_230382_6_, p_230382_7_);
=======
	@Override
	public boolean generate(World world, Random rand, BlockPos pos) {
		int l = rand.nextInt(3) + this.minTreeHeight;
        boolean flag = true;

        int x = pos.getX();
        int y = pos.getY();
        int z = pos.getZ();
        
        if (y >= 1 && y + l + 1 <= 256) {
            byte b0;
            int k1;
            Block block;

            for (int i1 = y; i1 <= y + 1 + l; ++i1) {
                b0 = 1;

                if (i1 == y) {
                    b0 = 0;
                }

                if (i1 >= y + 1 + l - 2) {
                    b0 = 2;
                }

                for (int j1 = x - b0; j1 <= x + b0 && flag; ++j1) {
                    for (k1 = z - b0; k1 <= z + b0 && flag; ++k1) {
                        if (i1 >= 0 && i1 < 256) {
                        	BlockPos pos2 = new BlockPos(j1, i1, k1);

                            if (!this.isReplaceable(world, pos2)) {
                                flag = false;
                            }
                        }
                        else {
                            flag = false;
                        }
                    }
                }
            }

            if (!flag) {
                return false;
            }
            else {
            	BlockPos pos3 = new BlockPos(x, y - 1, z);
            	IBlockState state2 = world.getBlockState(pos3);
                Block block2 = state2.getBlock();
                if (y < 256 - l - 1) {
                	
                    block2.onPlantGrow(state2, world, pos3, pos3.up());

                    for (k1 = 0; k1 < l; ++k1) {
                    	
                    	state2 = world.getBlockState(new BlockPos(x, y + k1, z));

                        if (world.isAirBlock(new BlockPos(x, y + k1, z)) || state2.getBlock().isLeaves(state2, world, new BlockPos(x, y + k1, z))) {
                            this.setBlockAndNotifyAdequately(world, new BlockPos(x, y + k1, z), AdvancedRocketryBlocks.blockCharcoalLog.getDefaultState().withProperty(BlockLog.LOG_AXIS, BlockLog.EnumAxis.Y));
                        }
                    }
 
                    return true;
                }
                else {
                    return false;
                }
            }
        }
        else {
            return false;
>>>>>>> origin/feature/nuclearthermalrockets
        }

        return new LinkedList<FoliagePlacer.Foliage>();
     }
    
    /*protected TrunkPlacerType<?> func_230381_a_() {
        return AdvancedRocktryTrees.CHARRED_TREE;
     }*/
}
