package zmaster587.advancedRocketry.world;

import java.util.List;
import java.util.Random;
import java.util.function.Supplier;

import com.mojang.serialization.Codec;

import zmaster587.advancedRocketry.dimension.DimensionManager;
import zmaster587.advancedRocketry.dimension.DimensionProperties;
import zmaster587.advancedRocketry.util.OreGenProperties;
import zmaster587.advancedRocketry.util.OreGenProperties.OreEntry;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.provider.BiomeProvider;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ChunkPrimer;
import net.minecraft.world.chunk.IChunk;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.DimensionSettings;
import net.minecraft.world.gen.Heightmap.Type;
import net.minecraft.world.gen.WorldGenRegion;
import net.minecraft.world.gen.feature.structure.StructureManager;
import net.minecraft.world.gen.settings.DimensionStructuresSettings;

public class ChunkProviderAsteroids extends ChunkGenerator {

	
	public ChunkProviderAsteroids(BiomeProvider p_i231887_1_, long p_i231887_4_,
			Supplier<DimensionSettings> settings,  DimensionProperties properties) {
		this(p_i231887_1_, p_i231887_1_, p_i231887_4_, settings, properties);
	}
	
	public ChunkProviderAsteroids(BiomeProvider p_i231887_1_, BiomeProvider p_i231887_2_, long p_i231887_4_,
			Supplier<DimensionSettings> settings,  DimensionProperties properties) {
		super(p_i231887_1_, p_i231887_2_, settings.get().func_236108_a_(), p_i231887_4_);
	}

	@Override
	protected Codec<? extends ChunkGenerator> func_230347_a_() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ChunkGenerator func_230349_a_(long p_230349_1_) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void generateSurface(WorldGenRegion p_225551_1_, IChunk p_225551_2_) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void func_230352_b_(IWorld p_230352_1_, StructureManager p_230352_2_, IChunk p_230352_3_) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public int func_222529_a(int p_222529_1_, int p_222529_2_, Type heightmapType) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public IBlockReader func_230348_a_(int p_230348_1_, int p_230348_2_) {
		// TODO Auto-generated method stub
		return null;
	}

}
