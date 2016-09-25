package zmaster587.advancedRocketry.world.biome;

import java.util.Random;

import zmaster587.advancedRocketry.world.decoration.MapGenInvertedPillar;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.ChunkPrimer;
import net.minecraft.world.gen.MapGenBase;
import net.minecraft.world.gen.feature.WorldGenAbstractTree;
import net.minecraft.world.gen.feature.WorldGenShrub;

public class BiomeGenOceanSpires extends Biome {

	MapGenBase oceanSpire;
	
	public BiomeGenOceanSpires(int id, boolean register) {
		super(new BiomeProperties("OceanSpires").setBaseHeight(-0.5f).setHeightVariation(0f));
		
		registerBiome(id, "OceanSpires", this);
		
		this.theBiomeDecorator.clayPerChunk = 0;
		this.theBiomeDecorator.flowersPerChunk = 0;
		this.theBiomeDecorator.mushroomsPerChunk = 0;
		this.theBiomeDecorator.treesPerChunk = 0;
		this.theBiomeDecorator.grassPerChunk = 7;
		this.theBiomeDecorator.waterlilyPerChunk = 0;
		this.theBiomeDecorator.sandPerChunk = 0;
		this.theBiomeDecorator.sandPerChunk2 = 0;
		this.spawnableCreatureList.clear();
		this.topBlock = GRAVEL;
		this.fillerBlock = GRAVEL;
		
		oceanSpire = new MapGenInvertedPillar(4, Blocks.MOSSY_COBBLESTONE.getDefaultState(), Blocks.COBBLESTONE.getDefaultState(), Blocks.DIRT.getDefaultState());
	}

	@Override
	public void genTerrainBlocks(World worldIn, Random rand,
			ChunkPrimer chunkPrimerIn, int x, int z, double noiseVal) {
		// TODO Auto-generated method stub
		super.genTerrainBlocks(worldIn, rand, chunkPrimerIn, x, z, noiseVal);
		
		if(x % 16 == 0 && z % 16 == 0 )
			oceanSpire.generate(worldIn, x/16, z/16, chunkPrimerIn);
	}
	
	@Override
	public WorldGenAbstractTree genBigTreeChance(Random rand) {
		return new WorldGenShrub(Blocks.LOG.getDefaultState(), Blocks.LEAVES.getDefaultState());
	}
}
