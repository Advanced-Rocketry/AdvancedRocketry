package zmaster587.advancedRocketry.world;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.world.Blockreader;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.biome.provider.BiomeProvider;
import net.minecraft.world.chunk.IChunk;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.DimensionSettings;
import net.minecraft.world.gen.Heightmap.Type;
import net.minecraft.world.gen.WorldGenRegion;
import net.minecraft.world.gen.feature.structure.StructureManager;

import java.util.function.Supplier;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

public class ChunkProviderSpace extends ChunkGenerator {

	long seed;
	private final Supplier<DimensionSettings> dimensionSettings;

	public static final Codec<ChunkProviderSpace> planetCodec = RecordCodecBuilder.create((p_236091_0_) -> p_236091_0_.group(BiomeProvider.CODEC.fieldOf("biome_source").forGetter(ChunkGenerator::getBiomeProvider),
        Codec.LONG.fieldOf("seed").stable().forGetter((ChunkProviderSpace p_236093_0_) -> p_236093_0_.seed), DimensionSettings.DIMENSION_SETTINGS_CODEC.fieldOf("settings").forGetter((p_236090_0_) -> p_236090_0_.dimensionSettings)).apply(p_236091_0_, p_236091_0_.stable(ChunkProviderSpace::new)));

	public ChunkProviderSpace(BiomeProvider biomeProvider, long seed, Supplier<DimensionSettings> settings) {
		this( biomeProvider, settings);
	}
		
	public ChunkProviderSpace(BiomeProvider p_i231888_1_, Supplier< DimensionSettings> p_i231888_2_) {
		super(p_i231888_1_, p_i231888_2_.get().getStructures());
		dimensionSettings = p_i231888_2_;
	}
	
	@Nonnull
	@Override
	protected Codec<? extends ChunkGenerator> func_230347_a_() {
		return planetCodec;
	}

	@Nonnull
	@Override
	public ChunkGenerator func_230349_a_(long p_230349_1_) {
		return new ChunkProviderSpace(biomeProvider, dimensionSettings);
	}

	@Override
	@ParametersAreNonnullByDefault
	public void generateSurface(WorldGenRegion p_225551_1_, IChunk p_225551_2_) {}

	@Override
	@ParametersAreNonnullByDefault
	public void func_230352_b_(IWorld p_230352_1_, StructureManager p_230352_2_, IChunk p_230352_3_) {}

	@Override
	@ParametersAreNonnullByDefault
	public int getHeight(int x, int z, Type heightmapType) {
		return 0;
	}

	//@Override
	/*public int func_222529_a(int p_222529_1_, int p_222529_2_, Type heightmapType) {
		return 0;
	}*/

	@Nonnull
	@Override
	public IBlockReader func_230348_a_(int p_230348_1_, int p_230348_2_) {
		BlockState[] ablockstate = new BlockState[1];
		ablockstate[0] = Blocks.AIR.getDefaultState();
		return new Blockreader(ablockstate);
	}
}
