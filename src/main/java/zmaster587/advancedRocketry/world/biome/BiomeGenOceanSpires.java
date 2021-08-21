package zmaster587.advancedRocketry.world.biome;

import net.minecraft.init.Blocks;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.ChunkPrimer;
import net.minecraft.world.gen.MapGenBase;
import net.minecraft.world.gen.feature.WorldGenAbstractTree;
import net.minecraft.world.gen.feature.WorldGenShrub;
import zmaster587.advancedRocketry.world.decoration.MapGenInvertedPillar;

import java.util.Random;

public class BiomeGenOceanSpires extends Biome {

	MapGenBase oceanSpire;
	
	public BiomeGenOceanSpires(BiomeProperties properties) {
		super(properties);
		
		this.decorator.clayPerChunk = 0;
		this.decorator.flowersPerChunk = 0;
		this.decorator.mushroomsPerChunk = 0;
		this.decorator.treesPerChunk = 0;
		this.decorator.grassPerChunk = 7;
		this.decorator.waterlilyPerChunk = 0;
		this.decorator.sandPatchesPerChunk = 0;
		this.spawnableCreatureList.clear();
		this.topBlock = GRAVEL;
		this.fillerBlock = GRAVEL;
		
		oceanSpire = new MapGenInvertedPillar(4, Blocks.MOSSY_COBBLESTONE.getDefaultState(), Blocks.COBBLESTONE.getDefaultState(), Blocks.DIRT.getDefaultState());
	}

	@Override
	public void genTerrainBlocks(World worldIn, Random rand,
			ChunkPrimer chunkPrimerIn, int x, int z, double noiseVal) {
		super.genTerrainBlocks(worldIn, rand, chunkPrimerIn, x, z, noiseVal);
		
		if(x % 16 == 0 && z % 16 == 0 )
			oceanSpire.generate(worldIn, x/16, z/16, chunkPrimerIn);
	}
	
	@Override
	public WorldGenAbstractTree getRandomTreeFeature(Random rand) {
		return new WorldGenShrub(Blocks.LOG.getDefaultState(), Blocks.LEAVES.getDefaultState());
	}
}
