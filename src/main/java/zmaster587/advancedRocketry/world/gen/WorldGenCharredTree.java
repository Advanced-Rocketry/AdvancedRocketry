package zmaster587.advancedRocketry.world.gen;

import java.util.Random;

import zmaster587.advancedRocketry.api.AdvancedRocketryBlocks;
import net.minecraft.block.Block;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenAbstractTree;

public class WorldGenCharredTree extends WorldGenAbstractTree {
	
    /** The minimum height of a generated tree. */
    private final int minTreeHeight;
	
    public WorldGenCharredTree(boolean doNotify, int minHeight)
    {
        super(doNotify);
        this.minTreeHeight = minHeight;
    }

	
    public boolean generate(World world, Random rand, int x, int y, int z)
    {
        int l = rand.nextInt(3) + this.minTreeHeight;
        boolean flag = true;

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
                            block = world.getBlock(j1, i1, k1);

                            if (!this.isReplaceable(world, j1, i1, k1)) {
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
                Block block2 = world.getBlock(x, y - 1, z);
                if (y < 256 - l - 1) {
                    block2.onPlantGrow(world, x, y - 1, z, x, y, z);
                    b0 = 3;

                    for (k1 = 0; k1 < l; ++k1) {
                        block = world.getBlock(x, y + k1, z);

                        if (block.isAir(world, x, y + k1, z) || block.isLeaves(world, x, y + k1, z)) {
                            this.setBlockAndNotifyAdequately(world, x, y + k1, z, AdvancedRocketryBlocks.blockCharcoalLog, 0);
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
