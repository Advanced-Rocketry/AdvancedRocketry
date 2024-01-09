package zmaster587.advancedRocketry.world.biome;

import net.minecraft.init.Blocks;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.ChunkPrimer;
import net.minecraft.world.gen.MapGenBase;
import net.minecraft.world.gen.feature.WorldGenerator;
import zmaster587.advancedRocketry.api.AdvancedRocketryBlocks;
import zmaster587.advancedRocketry.world.decoration.MapGenLargeCrystal;
import zmaster587.advancedRocketry.world.gen.WorldGenLargeCrystal;

import org.jetbrains.annotations.NotNull;
import java.util.Random;

public class BiomeGenCrystal extends Biome  {
	
	WorldGenerator crystalGenerator;
	MapGenBase crystalGenBase;
	
	public BiomeGenCrystal(BiomeProperties properties) {
		super(properties);
		
		topBlock = Blocks.SNOW.getDefaultState();
		fillerBlock = Blocks.PACKED_ICE.getDefaultState();
		this.spawnableMonsterList.clear();
		this.spawnableCreatureList.clear();
		this.decorator.generateFalls=false;
		this.decorator.flowersPerChunk=0;
		this.decorator.grassPerChunk=0;
		this.decorator.treesPerChunk=0;
		this.decorator.mushroomsPerChunk=0;
		
		crystalGenerator = new WorldGenLargeCrystal();
		crystalGenBase = new MapGenLargeCrystal(fillerBlock, AdvancedRocketryBlocks.blockCrystal.getDefaultState());
	}
	
	@Override
	public void genTerrainBlocks(World worldIn, Random rand,
								 @NotNull ChunkPrimer chunkPrimerIn, int x, int z, double noiseVal) {
		super.genTerrainBlocks(worldIn, rand, chunkPrimerIn, x, z, noiseVal);
		
		if(x % 16 == 0 && z % 16 == 0 )
			crystalGenBase.generate(worldIn, x >> 4, z >> 4, chunkPrimerIn);
	}
}
