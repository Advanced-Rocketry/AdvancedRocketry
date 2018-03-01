package zmaster587.advancedRocketry.world.gen;

import net.minecraft.block.Block;
import net.minecraft.block.BlockLog;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenAbstractTree;
import zmaster587.advancedRocketry.api.AdvancedRocketryBlocks;

import java.util.Random;

public class WorldGenCharredTree extends WorldGenAbstractTree {
	
    /** The minimum height of a generated tree. */
    private final int minTreeHeight;
	
    public WorldGenCharredTree(boolean doNotify, int minHeight)
    {
        super(doNotify);
        this.minTreeHeight = minHeight;
    }

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
                            block = world.getBlockState(pos2).getBlock();

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
                    b0 = 3;

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
        }
	}
}
