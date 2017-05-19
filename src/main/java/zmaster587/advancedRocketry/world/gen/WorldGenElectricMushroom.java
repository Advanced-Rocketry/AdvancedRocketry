package zmaster587.advancedRocketry.world.gen;

import java.util.Random;

import zmaster587.advancedRocketry.block.BlockElectricMushroom;
import net.minecraft.block.Block;
import net.minecraft.block.BlockFlower;
import net.minecraft.block.BlockFlower.EnumFlowerType;
import net.minecraft.block.BlockMushroom;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenFlowers;
import net.minecraft.world.gen.feature.WorldGenerator;

public class WorldGenElectricMushroom extends WorldGenerator {

	IBlockState state;
	
	public WorldGenElectricMushroom(Block block) {
		state = block.getDefaultState();
	}
	//Stop changing my flowers Minecraft! >.<
	
    public boolean generate(World worldIn, Random rand, BlockPos position)
    {
        for (int i = 0; i < 64; ++i)
        {
            BlockPos blockpos = position.add(rand.nextInt(8) - rand.nextInt(8), rand.nextInt(4) - rand.nextInt(4), rand.nextInt(8) - rand.nextInt(8));

            if (worldIn.isAirBlock(blockpos) && (!worldIn.provider.hasNoSky() || blockpos.getY() < 255) && ((BlockElectricMushroom)this.state.getBlock()).canBlockStay(worldIn, blockpos, this.state))
            {
                worldIn.setBlockState(blockpos, state, 2);
            }
        }

        return true;
    }
}
