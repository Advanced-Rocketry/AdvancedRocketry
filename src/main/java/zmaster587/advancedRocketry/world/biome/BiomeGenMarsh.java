package zmaster587.advancedRocketry.world.biome;

import net.minecraft.init.Blocks;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.ChunkPrimer;
import net.minecraft.world.gen.feature.WorldGenAbstractTree;
import net.minecraft.world.gen.feature.WorldGenShrub;

import javax.annotation.Nonnull;
import java.util.Random;

public class BiomeGenMarsh extends Biome {

    public BiomeGenMarsh(BiomeProperties properties) {
        super(properties);

        this.decorator.clayPerChunk = 10;
        this.decorator.flowersPerChunk = 0;
        this.decorator.mushroomsPerChunk = 0;
        this.decorator.treesPerChunk = 0;
        this.decorator.grassPerChunk = 0;
        this.decorator.waterlilyPerChunk = 10;
        this.decorator.sandPatchesPerChunk = 0;

        this.spawnableCreatureList.clear();
    }

    @Override
    public void genTerrainBlocks(World worldIn, Random rand,
                                 ChunkPrimer chunkPrimerIn, int x, int z, double noiseVal) {
        super.genTerrainBlocks(worldIn, rand, chunkPrimerIn, x, z, noiseVal);

        double d1 = GRASS_COLOR_NOISE.getValue((double) x * 0.25D, (double) z * 0.25D);
        x = Math.abs(x % 16);
        z = Math.abs(z % 16);
        if (d1 > 0.2D) {
            chunkPrimerIn.setBlockState(x, 62, z, Blocks.GRASS.getDefaultState());
            for (int y = 61; y > 1; y--) {

                if (!chunkPrimerIn.getBlockState(x, y, z).isOpaqueCube())
                    chunkPrimerIn.setBlockState(x, y, z, Blocks.GRASS.getDefaultState());
                else
                    break;
            }
        }
    }

    @Override
    @Nonnull
    public WorldGenAbstractTree getRandomTreeFeature(Random rand) {
        return new WorldGenShrub(Blocks.LOG.getDefaultState(), Blocks.LEAVES.getDefaultState());
    }
}
