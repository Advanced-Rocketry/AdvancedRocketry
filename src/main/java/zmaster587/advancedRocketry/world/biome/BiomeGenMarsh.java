package zmaster587.advancedRocketry.world.biome;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.gen.feature.WorldGenAbstractTree;
import net.minecraft.world.gen.feature.WorldGenShrub;

public class BiomeGenMarsh extends BiomeGenBase {

	public BiomeGenMarsh(int id, boolean b) {
		super(id, b);

		this.biomeName = "Marsh";
		this.rootHeight=-0.2f;
		this.heightVariation=0.0f;
		this.theBiomeDecorator.clayPerChunk = 10;
		this.theBiomeDecorator.flowersPerChunk = 0;
		this.theBiomeDecorator.mushroomsPerChunk = 0;
		this.theBiomeDecorator.treesPerChunk = 0;
		this.theBiomeDecorator.grassPerChunk = 0;
		this.theBiomeDecorator.waterlilyPerChunk = 10;
		this.theBiomeDecorator.sandPerChunk = 0;
		this.theBiomeDecorator.sandPerChunk2 = 0;
		
		this.spawnableCreatureList.clear();
	}

	@Override
	public void genTerrainBlocks(World world, Random rand,
			Block[] block, byte[] abyte, int x,
			int z, double noise) {
		super.genTerrainBlocks(world, rand, block, abyte, x, z, noise);

		double d1 = plantNoise.func_151601_a((double)x * 0.25D, (double)z * 0.25D);

		if (d1 > 0.3D)
		{
			int index = (Math.abs(x % 16) * 16 + Math.abs(z % 16)) * 256 + 62;
			block[index] = Blocks.grass;
			for(int y = (int)(61); y > 1; y--) {
				index = (Math.abs(x % 16) * 16 + Math.abs(z % 16)) * 256 + y;
				if(block[index] == null || !block[index].isOpaqueCube())
					block[index] = Blocks.dirt;
				else
					break;
			}
		}
	}
	
    public WorldGenAbstractTree func_150567_a(Random p_150567_1_)
    {
        return new WorldGenShrub(3, 0);
    }


	public BiomeGenBase.TempCategory getTempCategory()
	{
		return BiomeGenBase.TempCategory.OCEAN;
	}
}
