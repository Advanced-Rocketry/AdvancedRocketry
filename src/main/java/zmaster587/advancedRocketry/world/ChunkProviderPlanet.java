// Unfortunately since NoiseChunkGenerator is final, we end up with a class that's basically a copy plus a few tweaks :/

package zmaster587.advancedRocketry.world;

<<<<<<< HEAD
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.EntityClassification;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SharedSeedRandom;
import net.minecraft.util.Util;
=======
import net.minecraft.block.BlockFalling;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.init.Biomes;
import net.minecraft.init.Blocks;
>>>>>>> origin/feature/nuclearthermalrockets
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.MutableBoundingBox;
import net.minecraft.util.math.SectionPos;
import net.minecraft.util.registry.DynamicRegistries;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.Blockreader;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeManager;
import net.minecraft.world.biome.MobSpawnInfo;
import net.minecraft.world.biome.provider.BiomeProvider;
import net.minecraft.world.biome.provider.EndBiomeProvider;
import net.minecraft.world.chunk.ChunkPrimer;
import net.minecraft.world.chunk.ChunkSection;
import net.minecraft.world.chunk.IChunk;
import net.minecraft.world.gen.*;
import net.minecraft.world.gen.feature.StructureFeature;
import net.minecraft.world.gen.feature.jigsaw.JigsawJunction;
import net.minecraft.world.gen.feature.jigsaw.JigsawPattern;
import net.minecraft.world.gen.feature.structure.AbstractVillagePiece;
import net.minecraft.world.gen.feature.structure.Structure;
import net.minecraft.world.gen.feature.structure.StructureFeatures;
import net.minecraft.world.gen.feature.structure.StructureManager;
import net.minecraft.world.gen.feature.structure.StructurePiece;
import net.minecraft.world.gen.feature.structure.StructureStart;
import net.minecraft.world.gen.feature.template.TemplateManager;
import net.minecraft.world.gen.settings.DimensionStructuresSettings;
import net.minecraft.world.gen.settings.NoiseSettings;
import net.minecraft.world.gen.settings.StructureSeparationSettings;
import net.minecraft.world.spawner.WorldEntitySpawner;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import zmaster587.advancedRocketry.dimension.DimensionManager;
import zmaster587.advancedRocketry.dimension.DimensionProperties;
<<<<<<< HEAD
=======
import zmaster587.advancedRocketry.event.PlanetEventHandler;
import zmaster587.advancedRocketry.util.OreGenProperties;
import zmaster587.advancedRocketry.util.OreGenProperties.OreEntry;
import zmaster587.advancedRocketry.world.decoration.*;
import zmaster587.advancedRocketry.world.ore.CustomizableOreGen;
import zmaster587.advancedRocketry.world.provider.WorldProviderPlanet;

>>>>>>> origin/feature/nuclearthermalrockets
import java.util.List;
import java.util.Random;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.IntStream;

import javax.annotation.Nullable;

<<<<<<< HEAD
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectList;
import it.unimi.dsi.fastutil.objects.ObjectListIterator;

public class ChunkProviderPlanet extends ChunkGenerator {
	public static final Logger logger = LogManager.getLogger();
	public static final Codec<ChunkProviderPlanet> planetCodec = RecordCodecBuilder.create((p_236091_0_) -> {
		return p_236091_0_.group(BiomeProvider.CODEC.fieldOf("biome_source").forGetter((p_236096_0_) -> {
			return p_236096_0_.biomeProvider;
		}), Codec.LONG.fieldOf("seed").stable().forGetter((p_236093_0_) -> {
			return p_236093_0_.seed;
		}), DimensionSettings.field_236098_b_.fieldOf("settings").forGetter((p_236090_0_) -> {
			return p_236090_0_.dimensionSettings;
		}),
				StructureFeature.field_242770_c.promotePartial(Util.func_240982_a_("Structure start: ", logger::error)).fieldOf("starts").forGetter((p_242488_0_) -> {
					return p_242488_0_.starts;
				}),
				Codec.STRING.fieldOf("dimension_props").forGetter((p_236090_0_) -> {
					return p_236090_0_.dimensionId.toString();
				})).apply(p_236091_0_, p_236091_0_.stable(ChunkProviderPlanet::new));
	});
	private static final float[] field_222561_h = Util.make(new float[13824], (p_236094_0_) -> {
		for(int i = 0; i < 24; ++i) {
			for(int j = 0; j < 24; ++j) {
				for(int k = 0; k < 24; ++k) {
					p_236094_0_[i * 24 * 24 + j * 24 + k] = (float)func_222554_b(j - 12, k - 12, i - 12);
				}
			}
		}
	});
	private static final float[] field_236081_j_ = Util.make(new float[25], (p_236092_0_) -> {
		for(int i = -2; i <= 2; ++i) {
			for(int j = -2; j <= 2; ++j) {
=======
import static net.minecraftforge.event.terraingen.InitMapGenEvent.EventType.*;

public class ChunkProviderPlanet implements IChunkGenerator {
	/** RNG. */
	protected static final IBlockState STONE = Blocks.STONE.getDefaultState();
	private final Random rand;
	private NoiseGeneratorOctaves minLimitPerlinNoise;
	private NoiseGeneratorOctaves maxLimitPerlinNoise;
	private NoiseGeneratorOctaves mainPerlinNoise;
	private NoiseGeneratorPerlin surfaceNoise;
	private NoiseGeneratorOctaves scaleNoise;
	private NoiseGeneratorOctaves depthNoise;
	private NoiseGeneratorOctaves forestNoise;
	private final World worldObj;
	private final boolean mapFeaturesEnabled;
	private final WorldType terrainType;
	private final double[] heightMap;
	private final float[] biomeWeights;
	private ChunkGeneratorSettings settings;
	private IBlockState oceanBlock = Blocks.WATER.getDefaultState();
	private IBlockState fillblock = Blocks.STONE.getDefaultState();
	private double[] depthBuffer = new double[256];
	private MapGenBase caveGenerator = new MapGenCaveExt();
	private MapGenStronghold strongholdGenerator = new MapGenStronghold();
	private MapGenVillage villageGenerator = new MapGenSpaceVillage();
	private MapGenMineshaft mineshaftGenerator = new MapGenMineshaft();
	private MapGenScatteredFeature scatteredFeatureGenerator = new MapGenScatteredFeature();
	private MapGenBase ravineGenerator = new MapGenRavineExt();
	private StructureOceanMonument oceanMonumentGenerator = new StructureOceanMonument();
	private Biome[] biomesForGeneration;
	private double[] mainNoiseRegion;
	private double[] minLimitRegion;
	private double[] maxLimitRegion;
	private double[] depthRegion;
	protected int heightmapOffset = 0;
	protected float heightmapMult = 1f;
	private boolean habitable;


	private MapGenCraterSmall craterGeneratorSmall;
	private MapGenCrater craterGenerator;
	private MapGenCraterHuge craterGeneratorHuge;
	private MapGenGeode geodeGenerator;
	private MapGenVolcano volcanoGenerator;
	private MapGenSwampTree swampTreeGenerator;

	{
		caveGenerator = TerrainGen.getModdedMapGen(caveGenerator, CAVE);
		strongholdGenerator = (MapGenStronghold) TerrainGen.getModdedMapGen(strongholdGenerator, STRONGHOLD);
		villageGenerator = (MapGenVillage) TerrainGen.getModdedMapGen(villageGenerator, VILLAGE);
		mineshaftGenerator = (MapGenMineshaft) TerrainGen.getModdedMapGen(mineshaftGenerator, MINESHAFT);
		scatteredFeatureGenerator = (MapGenScatteredFeature) TerrainGen.getModdedMapGen(scatteredFeatureGenerator, SCATTERED_FEATURE);
		ravineGenerator = TerrainGen.getModdedMapGen(ravineGenerator, RAVINE);
	}

	public ChunkProviderPlanet(World worldIn, long seed, boolean mapFeaturesEnabledIn, String p_i46668_5_) {
		this.worldObj = worldIn;
		this.terrainType = worldIn.getWorldInfo().getTerrainType();
		this.rand = new Random(seed);
		this.minLimitPerlinNoise = new NoiseGeneratorOctaves(this.rand, 16);
		this.maxLimitPerlinNoise = new NoiseGeneratorOctaves(this.rand, 16);
		this.mainPerlinNoise = new NoiseGeneratorOctaves(this.rand, 8);
		this.surfaceNoise = new NoiseGeneratorPerlin(this.rand, 4);
		this.scaleNoise = new NoiseGeneratorOctaves(this.rand, 10);
		this.depthNoise = new NoiseGeneratorOctaves(this.rand, 16);
		this.forestNoise = new NoiseGeneratorOctaves(this.rand, 8);
		this.heightMap = new double[825];
		this.biomeWeights = new float[25];

		DimensionProperties dimProps = DimensionManager.getInstance().getDimensionProperties(worldIn.provider.getDimension());

		for (int i = -2; i <= 2; ++i) {
			for (int j = -2; j <= 2; ++j) {
>>>>>>> origin/feature/nuclearthermalrockets
				float f = 10.0F / MathHelper.sqrt((float)(i * i + j * j) + 0.2F);
				p_236092_0_[i + 2 + (j + 2) * 5] = f;
			}
		}

<<<<<<< HEAD
	});
	private static final BlockState AIR = Blocks.AIR.getDefaultState();
	private final int verticalNoiseGranularity;
	private final int horizontalNoiseGranularity;
	private final int noiseSizeX;
	private final int noiseSizeY;
	private final int noiseSizeZ;
	protected final SharedSeedRandom randomSeed;
	private final OctavesNoiseGenerator field_222568_o;
	private final OctavesNoiseGenerator field_222569_p;
	private final OctavesNoiseGenerator field_222570_q;
	private final INoiseGenerator surfaceDepthNoise;
	private final OctavesNoiseGenerator field_236082_u_;
	@Nullable
	private final SimplexNoiseGenerator field_236083_v_;
	protected final BlockState defaultBlock;
	protected final BlockState defaultFluid;
	private final long seed;
	protected final Supplier<DimensionSettings> dimensionSettings;
	private final int field_236085_x_;
	DimensionProperties cachedDimensionProps;
	ResourceLocation dimensionId;
	private final DimensionStructuresSettings settings;
	private final List<Supplier<StructureFeature<?, ?>>> starts;

	public ChunkProviderPlanet(BiomeProvider biomeProvider, long seed, Supplier<DimensionSettings> settings, List<Supplier<StructureFeature<?, ?>>> starts, ResourceLocation dimensionProps) {
		this(biomeProvider, biomeProvider, seed, settings, starts, dimensionProps);
	}

	public ChunkProviderPlanet(BiomeProvider biomeProvider, long seed, Supplier<DimensionSettings> settings, List<Supplier<StructureFeature<?, ?>>> starts, String dimensionProps) {
		this(biomeProvider, biomeProvider, seed, settings, starts, new ResourceLocation(dimensionProps));
	}

	private ChunkProviderPlanet(BiomeProvider biomeProvider, BiomeProvider biomeProvider2, long seed, Supplier<DimensionSettings> settings, List<Supplier<StructureFeature<?, ?>>> starts, ResourceLocation dimensionProps) {
		super(biomeProvider, biomeProvider2, settings.get().getStructures(), seed);
		this.seed = seed;
		this.settings = settings.get().getStructures();
		dimensionId = dimensionProps;
		DimensionSettings dimensionsettings = settings.get();
		this.dimensionSettings = settings;
		NoiseSettings noisesettings = dimensionsettings.getNoise();
		this.field_236085_x_ = noisesettings.func_236169_a_();
		this.verticalNoiseGranularity = noisesettings.func_236175_f_() * 4;
		this.horizontalNoiseGranularity = noisesettings.func_236174_e_() * 4;
		//TODO: ASM this
		this.defaultBlock = dimensionsettings.getDefaultBlock();
		this.defaultFluid = dimensionsettings.getDefaultFluid();
		this.noiseSizeX = 16 / this.horizontalNoiseGranularity;
		this.noiseSizeY = noisesettings.func_236169_a_() / this.verticalNoiseGranularity;
		this.noiseSizeZ = 16 / this.horizontalNoiseGranularity;
		this.randomSeed = new SharedSeedRandom(seed);
		this.field_222568_o = new OctavesNoiseGenerator(this.randomSeed, IntStream.rangeClosed(-15, 0));
		this.field_222569_p = new OctavesNoiseGenerator(this.randomSeed, IntStream.rangeClosed(-15, 0));
		this.field_222570_q = new OctavesNoiseGenerator(this.randomSeed, IntStream.rangeClosed(-7, 0));
		this.surfaceDepthNoise = (INoiseGenerator)(noisesettings.func_236178_i_() ? new PerlinNoiseGenerator(this.randomSeed, IntStream.rangeClosed(-3, 0)) : new OctavesNoiseGenerator(this.randomSeed, IntStream.rangeClosed(-3, 0)));
		this.randomSeed.skip(2620);
		this.field_236082_u_ = new OctavesNoiseGenerator(this.randomSeed, IntStream.rangeClosed(-15, 0));
		if (noisesettings.func_236180_k_()) {
			SharedSeedRandom sharedseedrandom = new SharedSeedRandom(seed);
			sharedseedrandom.skip(17292);
			this.field_236083_v_ = new SimplexNoiseGenerator(sharedseedrandom);
		} else {
			this.field_236083_v_ = null;
=======
		if (p_i46668_5_ != null) {
			this.settings = ChunkGeneratorSettings.Factory.jsonToFactory(p_i46668_5_).build();
			this.oceanBlock = this.settings.useLavaOceans ? Blocks.LAVA.getDefaultState() : Blocks.WATER.getDefaultState();
			worldIn.setSeaLevel(dimProps.getSeaLevel());
		}

		IBlockState oceanBlock = dimProps.getOceanBlock();
		if(oceanBlock != null) {
			this.oceanBlock = oceanBlock;
			if(caveGenerator instanceof MapGenCaveExt)
				((MapGenCaveExt)caveGenerator).setOceanBlock(this.oceanBlock);
			if(ravineGenerator instanceof MapGenRavineExt)
				((MapGenRavineExt)ravineGenerator).setOceanBlock(this.oceanBlock);
		}

		IBlockState fillBlock = dimProps.getStoneBlock();
		if(fillBlock != null) {
			this.fillblock = fillBlock;
			if(caveGenerator instanceof MapGenCaveExt)
				((MapGenCaveExt)caveGenerator).setFillerBlock(this.fillblock);
			if(ravineGenerator instanceof MapGenRavineExt)
				((MapGenRavineExt)ravineGenerator).setFillerBlock(this.fillblock);
>>>>>>> origin/feature/nuclearthermalrockets
		}

		this.starts = starts;
	}

	protected Codec<? extends ChunkGenerator> func_230347_a_() {
		return planetCodec;
	}

<<<<<<< HEAD

	private DimensionProperties getDimensionProperties()
	{
		if(cachedDimensionProps == null || !cachedDimensionProps.getId().equals(dimensionId))
			cachedDimensionProps = DimensionManager.getInstance().getDimensionProperties(dimensionId);

		return cachedDimensionProps;
	}

	@OnlyIn(Dist.CLIENT)
	public ChunkGenerator func_230349_a_(long p_230349_1_) {
		return new ChunkProviderPlanet(this.biomeProvider.getBiomeProvider(p_230349_1_), p_230349_1_, this.dimensionSettings, this.starts, dimensionId);
	}
=======
		if(ARConfiguration.getCurrentConfig().generateCraters && dimProps.canGenerateCraters() && atmDensity <= 0.05)
			craterGeneratorSmall = new MapGenCraterSmall( (int)((16 +  (8*(1-atmDensity)) )*dimProps.getCraterMultiplier()));
		else
			craterGeneratorSmall = null;

		if(ARConfiguration.getCurrentConfig().generateCraters && dimProps.canGenerateCraters())
			craterGenerator = new MapGenCrater( (int)((250 + (175*(1-atmDensity)) )*dimProps.getCraterMultiplier()), atmDensity < 0.05);
		else
			craterGenerator = null;

		if(ARConfiguration.getCurrentConfig().generateCraters && dimProps.canGenerateCraters() && atmDensity == 0)
			craterGeneratorHuge = new MapGenCraterHuge((int)(200 * dimProps.getCraterMultiplier()));
		else
			craterGeneratorHuge = null;

		if(dimProps.canGenerateGeodes() && ARConfiguration.getCurrentConfig().generateGeodes) {
			geodeGenerator = new MapGenGeode((int)(800 * dimProps.getGeodeMultiplier()));
		} else
			geodeGenerator = null;

		if(dimProps.canGenerateVolcanos() && ARConfiguration.getCurrentConfig().generateVolcanos) {
			volcanoGenerator = new MapGenVolcano((int)(800 * dimProps.getVolcanoMultiplier()));
		} else
			volcanoGenerator = null;

		if(!dimProps.canGenerateCaves()) {
			caveGenerator = null;
            ravineGenerator = null;
		}

		//Yes, the trees shouldn't be here. This, however, makes them NOT MAKE WALLS. So they're here
		swampTreeGenerator = new MapGenSwampTree(10);
>>>>>>> origin/feature/nuclearthermalrockets

	public boolean func_236088_a_(long p_236088_1_, RegistryKey<DimensionSettings> p_236088_3_) {
		return this.seed == p_236088_1_ && this.dimensionSettings.get().func_242744_a(p_236088_3_);
	}

<<<<<<< HEAD
	public void func_242707_a(DynamicRegistries p_242707_1_, StructureManager p_242707_2_, IChunk p_242707_3_, TemplateManager p_242707_4_, long p_242707_5_) {
		ChunkPos chunkpos = p_242707_3_.getPos();
		Biome biome = this.biomeProvider.getNoiseBiome((chunkpos.x << 2) + 2, 0, (chunkpos.z << 2) + 2);
		this.func_242705_a(StructureFeatures.STRONGHOLD, p_242707_1_, p_242707_2_, p_242707_3_, p_242707_4_, p_242707_5_, chunkpos, biome);
=======
	public void setBlocksInChunk(int x, int z, ChunkPrimer primer) {
		byte b0 = 63;
		//TODO: may break for little planets
		this.biomesForGeneration = this.worldObj.getBiomeProvider().getBiomesForGeneration(this.biomesForGeneration, x * 4 - 2, z * 4 - 2, 10, 10);
		this.generateHeightmap(x * 4, 0, z * 4);

		for (int i = 0; i < 4; ++i) {
			int j = i * 5;
			int k = (i + 1) * 5;

			for (int l = 0; l < 4; ++l) {
				int i1 = (j + l) * 33;
				int j1 = (j + l + 1) * 33;
				int k1 = (k + l) * 33;
				int l1 = (k + l + 1) * 33;

				for (int i2 = 0; i2 < 32; ++i2) {
					double d0 = 0.125D;
					double d1 = this.heightMap[i1 + i2];
					double d2 = this.heightMap[j1 + i2];
					double d3 = this.heightMap[k1 + i2];
					double d4 = this.heightMap[l1 + i2];
					double d5 = (this.heightMap[i1 + i2 + 1] - d1) * 0.125D;
					double d6 = (this.heightMap[j1 + i2 + 1] - d2) * 0.125D;
					double d7 = (this.heightMap[k1 + i2 + 1] - d3) * 0.125D;
					double d8 = (this.heightMap[l1 + i2 + 1] - d4) * 0.125D;

					for (int j2 = 0; j2 < 8; ++j2) {
						double d9 = 0.25D;
						double d10 = d1;
						double d11 = d2;
						double d12 = (d3 - d1) * 0.25D;
						double d13 = (d4 - d2) * 0.25D;

						for (int k2 = 0; k2 < 4; ++k2) {
							double d14 = 0.25D;
							double d16 = (d11 - d10) * 0.25D;
							double lvt_45_1_ = d10 - d16;

							for (int l2 = 0; l2 < 4; ++l2) {
								if ((lvt_45_1_ += d16) > 0.0D) {
									primer.setBlockState(i * 4 + k2, heightmapOffset + i2 * 8 + j2, l * 4 + l2, this.fillblock);
								}
								else if (i2 * 8 + j2 < worldObj.getSeaLevel()) {
									primer.setBlockState(i * 4 + k2, heightmapOffset + i2 * 8 + j2, l * 4 + l2, this.oceanBlock);
								}
							}
>>>>>>> origin/feature/nuclearthermalrockets

		for(Supplier<StructureFeature<?, ?>> supplier : biome.getGenerationSettings().getStructures()) {
			this.func_242705_a(supplier.get(), p_242707_1_, p_242707_2_, p_242707_3_, p_242707_4_, p_242707_5_, chunkpos, biome);
		}

		for(Supplier<StructureFeature<?, ?>> supplier : starts) {
			this.func_242705_a(supplier.get(), p_242707_1_, p_242707_2_, p_242707_3_, p_242707_4_, p_242707_5_, chunkpos, biome);
		}
	}

<<<<<<< HEAD
	private void func_242705_a(StructureFeature<?, ?> p_242705_1_, DynamicRegistries p_242705_2_, StructureManager p_242705_3_, IChunk p_242705_4_, TemplateManager p_242705_5_, long p_242705_6_, ChunkPos p_242705_8_, Biome p_242705_9_) {
		StructureStart<?> structurestart = p_242705_3_.getStructureStart(SectionPos.from(p_242705_4_.getPos(), 0), p_242705_1_.field_236268_b_, p_242705_4_);
		int i = structurestart != null ? structurestart.getRefCount() : 0;
		StructureSeparationSettings structureseparationsettings = this.settings.func_236197_a_(p_242705_1_.field_236268_b_);
		if (structureseparationsettings != null) {
			StructureStart<?> structurestart1 = p_242705_1_.func_242771_a(p_242705_2_, this, this.biomeProvider, p_242705_5_, p_242705_6_, p_242705_8_, p_242705_9_, i, structureseparationsettings);
			p_242705_3_.addStructureStart(SectionPos.from(p_242705_4_.getPos(), 0), p_242705_1_.field_236268_b_, structurestart1, p_242705_4_);
=======
	public void replaceBiomeBlocks(int x, int z, ChunkPrimer primer, Biome[] biomesIn) {
		if (!net.minecraftforge.event.ForgeEventFactory.onReplaceBiomeBlocks(this, x, z, primer, this.worldObj)) return;
		double d0 = 0.03125D;
		this.depthBuffer = this.surfaceNoise.getRegion(this.depthBuffer, x * 16, z * 16, 16, 16, 0.0625D, 0.0625D, 1.0D);

		for (int i = 0; i < 16; ++i) {
			for (int j = 0; j < 16; ++j) {
				Biome biome = biomesIn[j + i * 16];
				biome.genTerrainBlocks(this.worldObj, this.rand, primer, x * 16 + i, z * 16 + j, this.depthBuffer[j + i * 16]);
			}
>>>>>>> origin/feature/nuclearthermalrockets
		}

	}

<<<<<<< HEAD
	private double func_222552_a(int p_222552_1_, int p_222552_2_, int p_222552_3_, double p_222552_4_, double p_222552_6_, double p_222552_8_, double p_222552_10_) {
		double d0 = 0.0D;
		double d1 = 0.0D;
		double d2 = 0.0D;
		boolean flag = true;
		double d3 = 1.0D;

		for(int i = 0; i < 16; ++i) {
			double d4 = OctavesNoiseGenerator.maintainPrecision((double)p_222552_1_ * p_222552_4_ * d3);
			double d5 = OctavesNoiseGenerator.maintainPrecision((double)p_222552_2_ * p_222552_6_ * d3);
			double d6 = OctavesNoiseGenerator.maintainPrecision((double)p_222552_3_ * p_222552_4_ * d3);
			double d7 = p_222552_6_ * d3;
			ImprovedNoiseGenerator improvednoisegenerator = this.field_222568_o.getOctave(i);
			if (improvednoisegenerator != null) {
				d0 += improvednoisegenerator.func_215456_a(d4, d5, d6, d7, (double)p_222552_2_ * d7) / d3;
			}
=======
	protected ChunkPrimer getChunkPrimer(int x, int z) {
>>>>>>> origin/feature/nuclearthermalrockets

			ImprovedNoiseGenerator improvednoisegenerator1 = this.field_222569_p.getOctave(i);
			if (improvednoisegenerator1 != null) {
				d1 += improvednoisegenerator1.func_215456_a(d4, d5, d6, d7, (double)p_222552_2_ * d7) / d3;
			}

<<<<<<< HEAD
			if (i < 8) {
				ImprovedNoiseGenerator improvednoisegenerator2 = this.field_222570_q.getOctave(i);
				if (improvednoisegenerator2 != null) {
					d2 += improvednoisegenerator2.func_215456_a(OctavesNoiseGenerator.maintainPrecision((double)p_222552_1_ * p_222552_8_ * d3), OctavesNoiseGenerator.maintainPrecision((double)p_222552_2_ * p_222552_10_ * d3), OctavesNoiseGenerator.maintainPrecision((double)p_222552_3_ * p_222552_8_ * d3), p_222552_10_ * d3, (double)p_222552_2_ * p_222552_10_ * d3) / d3;
				}
			}

			d3 /= 2.0D;
		}

		return MathHelper.clampedLerp(d0 / 512.0D, d1 / 512.0D, (d2 / 10.0D + 1.0D) / 2.0D);
	}
=======
		if (this.settings.useCaves && caveGenerator != null) {
			this.caveGenerator.generate(this.worldObj, x, z, chunkprimer);
		}

		if (this.settings.useRavines && ravineGenerator != null) {
			this.ravineGenerator.generate(this.worldObj, x, z, chunkprimer);
		}

		if(this.craterGeneratorSmall != null)
			this.craterGeneratorSmall.generate(this.worldObj, x, z, chunkprimer);

		if(this.craterGenerator != null)
			this.craterGenerator.generate(this.worldObj, x, z, chunkprimer);

		if(this.craterGeneratorHuge != null)
			this.craterGeneratorHuge.generate(this.worldObj, x, z, chunkprimer);
		
		if(this.volcanoGenerator != null)
			this.volcanoGenerator.generate(this.worldObj, x, z, chunkprimer);

		if(this.geodeGenerator != null)
			this.geodeGenerator.generate(this.worldObj, x, z, chunkprimer);
>>>>>>> origin/feature/nuclearthermalrockets

	private double[] func_222547_b(int p_222547_1_, int p_222547_2_) {
		double[] adouble = new double[this.noiseSizeY + 1];
		this.fillNoiseColumn(adouble, p_222547_1_, p_222547_2_);
		return adouble;
	}

<<<<<<< HEAD
	private void fillNoiseColumn(double[] noiseColumn, int noiseX, int noiseZ) {
		NoiseSettings noisesettings = this.dimensionSettings.get().getNoise();
		double d0;
		double d1;
		if (this.field_236083_v_ != null) {
			d0 = (double)(EndBiomeProvider.getRandomNoise(this.field_236083_v_, noiseX, noiseZ) - 8.0F);
			if (d0 > 0.0D) {
				d1 = 0.25D;
			} else {
				d1 = 1.0D;
=======
		if (this.mapFeaturesEnabled && habitable) {
			if (this.settings.useMineShafts) {
				this.mineshaftGenerator.generate(this.worldObj, x, z, chunkprimer);
>>>>>>> origin/feature/nuclearthermalrockets
			}
		} else {
			float f = 0.0F;
			float f1 = 0.0F;
			float f2 = 0.0F;
			int i = 2;
			int j = this.func_230356_f_();
			float f3 = this.biomeProvider.getNoiseBiome(noiseX, j, noiseZ).getDepth();

			for(int k = -2; k <= 2; ++k) {
				for(int l = -2; l <= 2; ++l) {
					Biome biome = this.biomeProvider.getNoiseBiome(noiseX + k, j, noiseZ + l);
					float f4 = biome.getDepth();
					float f5 = biome.getScale();
					float f6;
					float f7;
					if (noisesettings.func_236181_l_() && f4 > 0.0F) {
						f6 = 1.0F + f4 * 2.0F;
						f7 = 1.0F + f5 * 4.0F;
					} else {
						f6 = f4;
						f7 = f5;
					}

<<<<<<< HEAD
					float f8 = f4 > f3 ? 0.5F : 1.0F;
					float f9 = f8 * field_236081_j_[k + 2 + (l + 2) * 5] / (f6 + 2.0F);
					f += f7 * f9;
					f1 += f6 * f9;
					f2 += f9;
				}
			}

			float f10 = f1 / f2;
			float f11 = f / f2;
			double d16 = (double)(f10 * 0.5F - 0.125F);
			double d18 = (double)(f11 * 0.9F + 0.1F);
			d0 = d16 * 0.265625D;
			d1 = 96.0D / d18;
		}

		double d12 = 684.412D * noisesettings.func_236171_b_().func_236151_a_();
		double d13 = 684.412D * noisesettings.func_236171_b_().func_236153_b_();
		double d14 = d12 / noisesettings.func_236171_b_().func_236154_c_();
		double d15 = d13 / noisesettings.func_236171_b_().func_236155_d_();
		double d17 = (double)noisesettings.func_236172_c_().func_236186_a_();
		double d19 = (double)noisesettings.func_236172_c_().func_236188_b_();
		double d20 = (double)noisesettings.func_236172_c_().func_236189_c_();
		double d21 = (double)noisesettings.func_236173_d_().func_236186_a_();
		double d2 = (double)noisesettings.func_236173_d_().func_236188_b_();
		double d3 = (double)noisesettings.func_236173_d_().func_236189_c_();
		double d4 = noisesettings.func_236179_j_() ? this.func_236095_c_(noiseX, noiseZ) : 0.0D;
		double d5 = noisesettings.func_236176_g_();
		double d6 = noisesettings.func_236177_h_();

		for(int i1 = 0; i1 <= this.noiseSizeY; ++i1) {
			double d7 = this.func_222552_a(noiseX, i1, noiseZ, d12, d13, d14, d15);
			double d8 = 1.0D - (double)i1 * 2.0D / (double)this.noiseSizeY + d4;
			double d9 = d8 * d5 + d6;
			double d10 = (d9 + d0) * d1;
			if (d10 > 0.0D) {
				d7 = d7 + d10 * 4.0D;
			} else {
				d7 = d7 + d10;
			}

			if (d19 > 0.0D) {
				double d11 = ((double)(this.noiseSizeY - i1) - d20) / d19;
				d7 = MathHelper.clampedLerp(d17, d7, d11);
			}

			if (d2 > 0.0D) {
				double d22 = ((double)i1 - d3) / d2;
				d7 = MathHelper.clampedLerp(d21, d7, d22);
=======
			if (this.settings.useVillages) {
				this.villageGenerator.generate(this.worldObj, x, z, chunkprimer);
			}

			if (this.settings.useStrongholds) {
				this.strongholdGenerator.generate(this.worldObj, x, z, chunkprimer);
			}

			if (this.settings.useTemples) {
				this.scatteredFeatureGenerator.generate(this.worldObj, x, z, chunkprimer);
			}

			if (this.settings.useMonuments) {
				this.oceanMonumentGenerator.generate(this.worldObj, x, z, chunkprimer);
>>>>>>> origin/feature/nuclearthermalrockets
			}

			noiseColumn[i1] = d7;
		}

	}

<<<<<<< HEAD
	private double func_236095_c_(int p_236095_1_, int p_236095_2_) {
		double d0 = this.field_236082_u_.getValue((double)(p_236095_1_ * 200), 10.0D, (double)(p_236095_2_ * 200), 1.0D, 0.0D, true);
		double d1;
		if (d0 < 0.0D) {
			d1 = -d0 * 0.3D;
		} else {
			d1 = d0;
=======
	/**
	 * Generates the chunk at the specified position, from scratch
	 */
	public Chunk generateChunk(int x, int z) {
		ChunkPrimer chunkprimer = getChunkPrimer(x,z);
		Chunk chunk = new Chunk(this.worldObj, chunkprimer, x, z);
		byte[] abyte = chunk.getBiomeArray();

		for (int i = 0; i < abyte.length; ++i) {
			abyte[i] = (byte)Biome.getIdForBiome(this.biomesForGeneration[i]);
>>>>>>> origin/feature/nuclearthermalrockets
		}

		double d2 = d1 * 24.575625D - 2.0D;
		return d2 < 0.0D ? d2 * 0.009486607142857142D : Math.min(d2, 1.0D) * 0.006640625D;
	}

	/*public int func_222529_a(int p_222529_1_, int p_222529_2_, Heightmap.Type heightmapType) {
		return this.func_236087_a_(p_222529_1_, p_222529_2_, (BlockState[])null, heightmapType.getHeightLimitPredicate());
	}*/

<<<<<<< HEAD
	public IBlockReader func_230348_a_(int p_230348_1_, int p_230348_2_) {
		BlockState[] ablockstate = new BlockState[this.noiseSizeY * this.verticalNoiseGranularity];
		this.func_236087_a_(p_230348_1_, p_230348_2_, ablockstate, (Predicate<BlockState>)null);
		return new Blockreader(ablockstate);
	}

	public void func_230350_a_(long p_230350_1_, BiomeManager p_230350_3_, IChunk p_230350_4_, GenerationStage.Carving p_230350_5_) {
		super.func_230350_a_(p_230350_1_, p_230350_3_, p_230350_4_, p_230350_5_);

		//TODO: add planet specific carving
	}
=======
	//@Override
	public Chunk generateChunk2(int x, int z) {
		ChunkPrimer chunkPrimer = getChunkPrimer(x, z);
		Chunk chunk = new Chunk(this.worldObj, chunkPrimer, x, z);
		byte[] abyte = chunk.getBiomeArray();

		for (int i = 0; i < abyte.length; ++i) {
			abyte[i] = (byte)Biome.getIdForBiome(this.biomesForGeneration[i]);
		}

		chunk.generateSkylightMap();
		return chunk;
	}

	private void generateHeightmap(int p_185978_1_, int p_185978_2_, int p_185978_3_) {
		this.depthRegion = this.depthNoise.generateNoiseOctaves(this.depthRegion, p_185978_1_, p_185978_3_, 5, 5, this.settings.depthNoiseScaleX, this.settings.depthNoiseScaleZ, this.settings.depthNoiseScaleExponent);
		float f = this.settings.coordinateScale;
		float f1 = this.settings.heightScale * heightmapMult;
		this.mainNoiseRegion = this.mainPerlinNoise.generateNoiseOctaves(this.mainNoiseRegion, p_185978_1_, p_185978_2_, p_185978_3_, 5, 33, 5, f / this.settings.mainNoiseScaleX, f1 / this.settings.mainNoiseScaleY, f / this.settings.mainNoiseScaleZ);
		this.minLimitRegion = this.minLimitPerlinNoise.generateNoiseOctaves(this.minLimitRegion, p_185978_1_, p_185978_2_, p_185978_3_, 5, 33, 5, f, f1, f);
		this.maxLimitRegion = this.maxLimitPerlinNoise.generateNoiseOctaves(this.maxLimitRegion, p_185978_1_, p_185978_2_, p_185978_3_, 5, 33, 5, f, f1, f);
		int i = 0;
		int j = 0;

		for (int k = 0; k < 5; ++k) {
			for (int l = 0; l < 5; ++l) {
				float f2 = 0.0F;
				float f3 = 0.0F;
				float f4 = 0.0F;
				int i1 = 2;
				Biome biome = this.biomesForGeneration[k + 2 + (l + 2) * 10];

				for (int j1 = -2; j1 <= 2; ++j1) {
					for (int k1 = -2; k1 <= 2; ++k1) {
						Biome biome1 = this.biomesForGeneration[k + j1 + 2 + (l + k1 + 2) * 10];
						float f5 = this.settings.biomeDepthOffSet + biome1.getBaseHeight() * this.settings.biomeDepthWeight;
						float f6 = this.settings.biomeScaleOffset + biome1.getHeightVariation() * this.settings.biomeScaleWeight;

						if (this.terrainType == WorldType.AMPLIFIED && f5 > 0.0F) {
							f5 = 1.0F + f5 * 2.0F;
							f6 = 1.0F + f6 * 4.0F;
						}

						float f7 = this.biomeWeights[j1 + 2 + (k1 + 2) * 5] / (f5 + 2.0F);

						if (biome1.getBaseHeight() > biome.getBaseHeight()) {
							f7 /= 2.0F;
						}
>>>>>>> origin/feature/nuclearthermalrockets

	private int func_236087_a_(int p_236087_1_, int p_236087_2_, @Nullable BlockState[] p_236087_3_, @Nullable Predicate<BlockState> p_236087_4_) {
		int i = Math.floorDiv(p_236087_1_, this.horizontalNoiseGranularity);
		int j = Math.floorDiv(p_236087_2_, this.horizontalNoiseGranularity);
		int k = Math.floorMod(p_236087_1_, this.horizontalNoiseGranularity);
		int l = Math.floorMod(p_236087_2_, this.horizontalNoiseGranularity);
		double d0 = (double)k / (double)this.horizontalNoiseGranularity;
		double d1 = (double)l / (double)this.horizontalNoiseGranularity;
		double[][] adouble = new double[][]{this.func_222547_b(i, j), this.func_222547_b(i, j + 1), this.func_222547_b(i + 1, j), this.func_222547_b(i + 1, j + 1)};

		for(int i1 = this.noiseSizeY - 1; i1 >= 0; --i1) {
			double d2 = adouble[0][i1];
			double d3 = adouble[1][i1];
			double d4 = adouble[2][i1];
			double d5 = adouble[3][i1];
			double d6 = adouble[0][i1 + 1];
			double d7 = adouble[1][i1 + 1];
			double d8 = adouble[2][i1 + 1];
			double d9 = adouble[3][i1 + 1];

			for(int j1 = this.verticalNoiseGranularity - 1; j1 >= 0; --j1) {
				double d10 = (double)j1 / (double)this.verticalNoiseGranularity;
				double d11 = MathHelper.lerp3(d10, d0, d1, d2, d6, d4, d8, d3, d7, d5, d9);
				int k1 = i1 * this.verticalNoiseGranularity + j1;
				BlockState blockstate = this.func_236086_a_(d11, k1);
				if (p_236087_3_ != null) {
					p_236087_3_[k1] = blockstate;
				}

<<<<<<< HEAD
				if (p_236087_4_ != null && p_236087_4_.test(blockstate)) {
					return k1 + 1;
=======
				f2 = f2 / f4;
				f3 = f3 / f4;
				f2 = f2 * 0.9F + 0.1F;
				f3 = (f3 * 4.0F - 1.0F) / 8.0F;
				double d7 = this.depthRegion[j] / 8000.0D;

				if (d7 < 0.0D) {
					d7 = -d7 * 0.3D;
>>>>>>> origin/feature/nuclearthermalrockets
				}
			}
		}

<<<<<<< HEAD
		return 0;
	}

	protected BlockState func_236086_a_(double p_236086_1_, int p_236086_3_) {
		BlockState blockstate;
		if (p_236086_1_ > 0.0D) {
			blockstate = this.defaultBlock;
		} else if (p_236086_3_ < this.func_230356_f_()) {
			blockstate = this.defaultFluid;
		} else {
			blockstate = AIR;
		}
=======
				d7 = d7 * 3.0D - 2.0D;

				if (d7 < 0.0D) {
					d7 = d7 / 2.0D;

					if (d7 < -1.0D) {
						d7 = -1.0D;
					}

					d7 = d7 / 1.4D;
					d7 = d7 / 2.0D;
				} else {
					if (d7 > 1.0D) {
						d7 = 1.0D;
					}
>>>>>>> origin/feature/nuclearthermalrockets

		return blockstate;
	}

<<<<<<< HEAD
	/**
	 * Generate the SURFACE part of a chunk
	 */
	public void generateSurface(WorldGenRegion p_225551_1_, IChunk p_225551_2_) {
		ChunkPos chunkpos = p_225551_2_.getPos();
		int i = chunkpos.x;
		int j = chunkpos.z;
		SharedSeedRandom sharedseedrandom = new SharedSeedRandom();
		sharedseedrandom.setBaseChunkSeed(i, j);
		ChunkPos chunkpos1 = p_225551_2_.getPos();
		int k = chunkpos1.getXStart();
		int l = chunkpos1.getZStart();
		double d0 = 0.0625D;
		BlockPos.Mutable blockpos$mutable = new BlockPos.Mutable();

		for(int i1 = 0; i1 < 16; ++i1) {
			for(int j1 = 0; j1 < 16; ++j1) {
				int k1 = k + i1;
				int l1 = l + j1;
				int i2 = p_225551_2_.getTopBlockY(Heightmap.Type.WORLD_SURFACE_WG, i1, j1) + 1;
				double d1 = this.surfaceDepthNoise.noiseAt((double)k1 * 0.0625D, (double)l1 * 0.0625D, 0.0625D, (double)i1 * 0.0625D) * 15.0D;
				p_225551_1_.getBiome(blockpos$mutable.setPos(k + i1, i2, l + j1)).buildSurface(sharedseedrandom, p_225551_2_, k1, l1, i2, d1, this.defaultBlock, this.defaultFluid, this.func_230356_f_(), p_225551_1_.getSeed());
			}
		}

		this.makeBedrock(p_225551_2_, sharedseedrandom);
	}

	private void makeBedrock(IChunk chunkIn, Random rand) {
		BlockPos.Mutable blockpos$mutable = new BlockPos.Mutable();
		int i = chunkIn.getPos().getXStart();
		int j = chunkIn.getPos().getZStart();
		DimensionSettings dimensionsettings = this.dimensionSettings.get();
		int k = dimensionsettings.func_236118_f_();
		int l = this.field_236085_x_ - 1 - dimensionsettings.func_236117_e_();
		int i1 = 5;
		boolean flag = l + 4 >= 0 && l < this.field_236085_x_;
		boolean flag1 = k + 4 >= 0 && k < this.field_236085_x_;
		if (flag || flag1) {
			for(BlockPos blockpos : BlockPos.getAllInBoxMutable(i, 0, j, i + 15, 0, j + 15)) {
				if (flag) {
					for(int j1 = 0; j1 < 5; ++j1) {
						if (j1 <= rand.nextInt(5)) {
							chunkIn.setBlockState(blockpos$mutable.setPos(blockpos.getX(), l - j1, blockpos.getZ()), Blocks.BEDROCK.getDefaultState(), false);
						}
=======
				++j;
				double d8 = f3;
				double d9 = f2;
				d8 = d8 + d7 * 0.2D;
				d8 = d8 * (double)this.settings.baseSize / 8.0D;
				double d0 = (double)this.settings.baseSize + d8 * 4.0D;

				for (int l1 = 0; l1 < 33; ++l1) {
					double d1 = ((double)l1 - d0) * (double)this.settings.stretchY * 128.0D / 256.0D / d9;

					if (d1 < 0.0D) {
						d1 *= 4.0D;
>>>>>>> origin/feature/nuclearthermalrockets
					}
				}

<<<<<<< HEAD
				if (flag1) {
					for(int k1 = 4; k1 >= 0; --k1) {
						if (k1 <= rand.nextInt(5)) {
							chunkIn.setBlockState(blockpos$mutable.setPos(blockpos.getX(), k + k1, blockpos.getZ()), Blocks.BEDROCK.getDefaultState(), false);
						}
=======
					double d2 = this.minLimitRegion[i] / (double)this.settings.lowerLimitScale;
					double d3 = this.maxLimitRegion[i] / (double)this.settings.upperLimitScale;
					double d4 = (this.mainNoiseRegion[i] / 10.0D + 1.0D) / 2.0D;
					double d5 = MathHelper.clamp(d2, d3, d4) - d1;

					if (l1 > 29) {
						double d6 = (float)(l1 - 29) / 3.0F;
						d5 = d5 * (1.0D - d6) + -10.0D * d6;
>>>>>>> origin/feature/nuclearthermalrockets
					}
				}
			}

		}
	}

<<<<<<< HEAD
	public void func_230352_b_(IWorld p_230352_1_, StructureManager p_230352_2_, IChunk p_230352_3_) {
		ObjectList<StructurePiece> objectlist = new ObjectArrayList<>(10);
		ObjectList<JigsawJunction> objectlist1 = new ObjectArrayList<>(32);
		ChunkPos chunkpos = p_230352_3_.getPos();
		int i = chunkpos.x;
		int j = chunkpos.z;
		int k = i << 4;
		int l = j << 4;

		for(Structure<?> structure : Structure.field_236384_t_) {
			p_230352_2_.func_235011_a_(SectionPos.from(chunkpos, 0), structure).forEach((p_236089_5_) -> {
				for(StructurePiece structurepiece1 : p_236089_5_.getComponents()) {
					if (structurepiece1.func_214810_a(chunkpos, 12)) {
						if (structurepiece1 instanceof AbstractVillagePiece) {
							AbstractVillagePiece abstractvillagepiece = (AbstractVillagePiece)structurepiece1;
							JigsawPattern.PlacementBehaviour jigsawpattern$placementbehaviour = abstractvillagepiece.getJigsawPiece().getPlacementBehaviour();
							if (jigsawpattern$placementbehaviour == JigsawPattern.PlacementBehaviour.RIGID) {
								objectlist.add(abstractvillagepiece);
							}

							for(JigsawJunction jigsawjunction1 : abstractvillagepiece.getJunctions()) {
								int l5 = jigsawjunction1.getSourceX();
								int i6 = jigsawjunction1.getSourceZ();
								if (l5 > k - 12 && i6 > l - 12 && l5 < k + 15 + 12 && i6 < l + 15 + 12) {
									objectlist1.add(jigsawjunction1);
								}
							}
						} else {
							objectlist.add(structurepiece1);
						}
					}
				}

			});
		}

		double[][][] adouble = new double[2][this.noiseSizeZ + 1][this.noiseSizeY + 1];

		for(int i5 = 0; i5 < this.noiseSizeZ + 1; ++i5) {
			adouble[0][i5] = new double[this.noiseSizeY + 1];
			this.fillNoiseColumn(adouble[0][i5], i * this.noiseSizeX, j * this.noiseSizeZ + i5);
			adouble[1][i5] = new double[this.noiseSizeY + 1];
		}

		ChunkPrimer chunkprimer = (ChunkPrimer)p_230352_3_;
		Heightmap heightmap = chunkprimer.getHeightmap(Heightmap.Type.OCEAN_FLOOR_WG);
		Heightmap heightmap1 = chunkprimer.getHeightmap(Heightmap.Type.WORLD_SURFACE_WG);
		BlockPos.Mutable blockpos$mutable = new BlockPos.Mutable();
		ObjectListIterator<StructurePiece> objectlistiterator = objectlist.iterator();
		ObjectListIterator<JigsawJunction> objectlistiterator1 = objectlist1.iterator();

		for(int i1 = 0; i1 < this.noiseSizeX; ++i1) {
			for(int j1 = 0; j1 < this.noiseSizeZ + 1; ++j1) {
				this.fillNoiseColumn(adouble[1][j1], i * this.noiseSizeX + i1 + 1, j * this.noiseSizeZ + j1);
			}

			for(int j5 = 0; j5 < this.noiseSizeZ; ++j5) {
				ChunkSection chunksection = chunkprimer.getSection(15);
				chunksection.lock();

				for(int k1 = this.noiseSizeY - 1; k1 >= 0; --k1) {
					double d0 = adouble[0][j5][k1];
					double d1 = adouble[0][j5 + 1][k1];
					double d2 = adouble[1][j5][k1];
					double d3 = adouble[1][j5 + 1][k1];
					double d4 = adouble[0][j5][k1 + 1];
					double d5 = adouble[0][j5 + 1][k1 + 1];
					double d6 = adouble[1][j5][k1 + 1];
					double d7 = adouble[1][j5 + 1][k1 + 1];

					for(int l1 = this.verticalNoiseGranularity - 1; l1 >= 0; --l1) {
						int i2 = k1 * this.verticalNoiseGranularity + l1;
						int j2 = i2 & 15;
						int k2 = i2 >> 4;
		if (chunksection.getYLocation() >> 4 != k2) {
			chunksection.unlock();
			chunksection = chunkprimer.getSection(k2);
			chunksection.lock();
		}

		double d8 = (double)l1 / (double)this.verticalNoiseGranularity;
		double d9 = MathHelper.lerp(d8, d0, d4);
		double d10 = MathHelper.lerp(d8, d2, d6);
		double d11 = MathHelper.lerp(d8, d1, d5);
		double d12 = MathHelper.lerp(d8, d3, d7);

		for(int l2 = 0; l2 < this.horizontalNoiseGranularity; ++l2) {
			int i3 = k + i1 * this.horizontalNoiseGranularity + l2;
			int j3 = i3 & 15;
			double d13 = (double)l2 / (double)this.horizontalNoiseGranularity;
			double d14 = MathHelper.lerp(d13, d9, d10);
			double d15 = MathHelper.lerp(d13, d11, d12);

			for(int k3 = 0; k3 < this.horizontalNoiseGranularity; ++k3) {
				int l3 = l + j5 * this.horizontalNoiseGranularity + k3;
				int i4 = l3 & 15;
				double d16 = (double)k3 / (double)this.horizontalNoiseGranularity;
				double d17 = MathHelper.lerp(d16, d14, d15);
				double d18 = MathHelper.clamp(d17 / 200.0D, -1.0D, 1.0D);

				int j4;
				int k4;
				int l4;
				for(d18 = d18 / 2.0D - d18 * d18 * d18 / 24.0D; objectlistiterator.hasNext(); d18 += func_222556_a(j4, k4, l4) * 0.8D) {
					StructurePiece structurepiece = objectlistiterator.next();
					MutableBoundingBox mutableboundingbox = structurepiece.getBoundingBox();
					j4 = Math.max(0, Math.max(mutableboundingbox.minX - i3, i3 - mutableboundingbox.maxX));
					k4 = i2 - (mutableboundingbox.minY + (structurepiece instanceof AbstractVillagePiece ? ((AbstractVillagePiece)structurepiece).getGroundLevelDelta() : 0));
					l4 = Math.max(0, Math.max(mutableboundingbox.minZ - l3, l3 - mutableboundingbox.maxZ));
=======
	/**
	 * Populates chunk with ores etc etc
	 */
	@Override
	public void populate(int x, int z) {
		BlockFalling.fallInstantly = true;
		int i = x * 16;
		int j = z * 16;
		BlockPos blockpos = new BlockPos(i, 0, j);
		Biome biome = this.worldObj.getBiome(blockpos.add(16, 0, 16));
		this.rand.setSeed(this.worldObj.getSeed());
		long k = this.rand.nextLong() / 2L * 2L + 1L;
		long l = this.rand.nextLong() / 2L * 2L + 1L;
		this.rand.setSeed((long)x * k + (long)z * l ^ this.worldObj.getSeed());
		boolean flag = false;
		ChunkPos chunkpos = new ChunkPos(x, z);

		//noinspection ConstantConditions
		net.minecraftforge.event.ForgeEventFactory.onChunkPopulate(true, this, this.worldObj, this.rand, x, z, flag);

		if (this.mapFeaturesEnabled && habitable) {
			if (this.settings.useMineShafts) {
				this.mineshaftGenerator.generateStructure(this.worldObj, this.rand, chunkpos);
			}

			if (this.settings.useVillages) {
				flag = this.villageGenerator.generateStructure(this.worldObj, this.rand, chunkpos);
			}

			if (this.settings.useStrongholds) {
				this.strongholdGenerator.generateStructure(this.worldObj, this.rand, chunkpos);
			}

			if (this.settings.useTemples) {
				this.scatteredFeatureGenerator.generateStructure(this.worldObj, this.rand, chunkpos);
			}

			if (this.settings.useMonuments) {
				this.oceanMonumentGenerator.generateStructure(this.worldObj, this.rand, chunkpos);
			}
		}

		if (false && biome != Biomes.DESERT && biome != Biomes.DESERT_HILLS && this.settings.useWaterLakes && !flag && this.rand.nextInt(this.settings.waterLakeChance) == 0)
			if (net.minecraftforge.event.terraingen.TerrainGen.populate(this, this.worldObj, this.rand, x, z, flag, net.minecraftforge.event.terraingen.PopulateChunkEvent.Populate.EventType.LAKE)) {
				int i1 = this.rand.nextInt(16) + 8;
				int j1 = this.rand.nextInt(256);
				int k1 = this.rand.nextInt(16) + 8;
				(new WorldGenLakes(Blocks.WATER)).generate(this.worldObj, this.rand, blockpos.add(i1, j1, k1));
			}

		if (false && !flag && this.rand.nextInt(this.settings.lavaLakeChance / 10) == 0 && this.settings.useLavaLakes)
			if (net.minecraftforge.event.terraingen.TerrainGen.populate(this, this.worldObj, this.rand, x, z, flag, net.minecraftforge.event.terraingen.PopulateChunkEvent.Populate.EventType.LAVA)) {
				int i2 = this.rand.nextInt(16) + 8;
				int l2 = this.rand.nextInt(this.rand.nextInt(248) + 8);
				int k3 = this.rand.nextInt(16) + 8;

				if (l2 < this.worldObj.getSeaLevel() || this.rand.nextInt(this.settings.lavaLakeChance / 8) == 0) {
					(new WorldGenLakes(Blocks.LAVA)).generate(this.worldObj, this.rand, blockpos.add(i2, l2, k3));
>>>>>>> origin/feature/nuclearthermalrockets
				}

<<<<<<< HEAD
				objectlistiterator.back(objectlist.size());

				while(objectlistiterator1.hasNext()) {
					JigsawJunction jigsawjunction = objectlistiterator1.next();
					int k5 = i3 - jigsawjunction.getSourceX();
					j4 = i2 - jigsawjunction.getSourceGroundY();
					k4 = l3 - jigsawjunction.getSourceZ();
					d18 += func_222556_a(k5, j4, k4) * 0.4D;
=======
		if (habitable && this.settings.useDungeons)
			if (net.minecraftforge.event.terraingen.TerrainGen.populate(this, this.worldObj, this.rand, x, z, flag, net.minecraftforge.event.terraingen.PopulateChunkEvent.Populate.EventType.DUNGEON)) {
				for (int j2 = 0; j2 < this.settings.dungeonChance; ++j2) {
					int i3 = this.rand.nextInt(16) + 8;
					int l3 = this.rand.nextInt(256);
					int l1 = this.rand.nextInt(16) + 8;
					(new WorldGenDungeons()).generate(this.worldObj, this.rand, blockpos.add(i3, l3, l1));
>>>>>>> origin/feature/nuclearthermalrockets
				}

<<<<<<< HEAD
				objectlistiterator1.back(objectlist1.size());
				BlockState blockstate = this.func_236086_a_(d18, i2);
				if (blockstate != AIR) {
					blockpos$mutable.setPos(i3, i2, l3);
					if (blockstate.getLightValue(chunkprimer, blockpos$mutable) != 0) {
						chunkprimer.addLightPosition(blockpos$mutable);
					}

					chunksection.setBlockState(j3, j2, i4, blockstate, false);
					heightmap.update(j3, i2, i4, blockstate);
					heightmap1.update(j3, i2, i4, blockstate);
=======
		biome.decorate(this.worldObj, this.rand, new BlockPos(i, 0, j));
		if (net.minecraftforge.event.terraingen.TerrainGen.populate(this, this.worldObj, this.rand, x, z, flag, net.minecraftforge.event.terraingen.PopulateChunkEvent.Populate.EventType.ANIMALS))
			WorldEntitySpawner.performWorldGenSpawning(this.worldObj, biome, i + 8, j + 8, 16, 16, this.rand);
		blockpos = blockpos.add(8, 0, 8);

		if (net.minecraftforge.event.terraingen.TerrainGen.populate(this, this.worldObj, this.rand, x, z, flag, net.minecraftforge.event.terraingen.PopulateChunkEvent.Populate.EventType.ICE)) {
			for (int k2 = 0; k2 < 16; ++k2) {
				for (int j3 = 0; j3 < 16; ++j3) {
					BlockPos blockpos1 = this.worldObj.getPrecipitationHeight(blockpos.add(k2, 0, j3));
					BlockPos blockpos2 = blockpos1.down();

					if (this.worldObj.canBlockFreezeWater(blockpos2)) {
						this.worldObj.setBlockState(blockpos2, Blocks.ICE.getDefaultState(), 2);
					}

					if (this.worldObj.canSnowAt(blockpos1, true)) {
						this.worldObj.setBlockState(blockpos1, Blocks.SNOW_LAYER.getDefaultState(), 2);
					}
>>>>>>> origin/feature/nuclearthermalrockets
				}
			}
		}
					}
				}

				chunksection.unlock();
			}

			double[][] adouble1 = adouble[0];
			adouble[0] = adouble[1];
			adouble[1] = adouble1;
		}

	}

	@Override
	public int getHeight(int x, int z, Heightmap.Type heightmapType) {
		return 0;
	}

	private static double func_222556_a(int p_222556_0_, int p_222556_1_, int p_222556_2_) {
		int i = p_222556_0_ + 12;
		int j = p_222556_1_ + 12;
		int k = p_222556_2_ + 12;
		if (i >= 0 && i < 24) {
			if (j >= 0 && j < 24) {
				return k >= 0 && k < 24 ? (double)field_222561_h[k * 24 * 24 + i * 24 + j] : 0.0D;
			} else {
				return 0.0D;
			}
		} else {
			return 0.0D;
		}
	}

	private static double func_222554_b(int p_222554_0_, int p_222554_1_, int p_222554_2_) {
		double d0 = (double)(p_222554_0_ * p_222554_0_ + p_222554_2_ * p_222554_2_);
		double d1 = (double)p_222554_1_ + 0.5D;
		double d2 = d1 * d1;
		double d3 = Math.pow(Math.E, -(d2 / 16.0D + d0 / 16.0D));
		double d4 = -d1 * MathHelper.fastInvSqrt(d2 / 2.0D + d0 / 2.0D) / 2.0D;
		return d4 * d3;
	}

	public int func_230355_e_() {
		return this.field_236085_x_;
	}

	public int func_230356_f_() {
		return getDimensionProperties().getSeaLevel();
	}

<<<<<<< HEAD
	public List<MobSpawnInfo.Spawners> func_230353_a_(Biome p_230353_1_, StructureManager p_230353_2_, EntityClassification p_230353_3_, BlockPos p_230353_4_) {
		if (p_230353_2_.getStructureStart(p_230353_4_, true, Structure.SWAMP_HUT).isValid()) {
			if (p_230353_3_ == EntityClassification.MONSTER) {
				return Structure.SWAMP_HUT.getSpawnList();
			}

			if (p_230353_3_ == EntityClassification.CREATURE) {
				return Structure.SWAMP_HUT.getCreatureSpawnList();
=======
	@Override
	public void recreateStructures(Chunk chunkIn, int x, int z) {
		if (this.mapFeaturesEnabled || !habitable) {
			if (this.settings.useMineShafts) {
				this.mineshaftGenerator.generate(this.worldObj, x, z, null);
			}

			if (this.settings.useVillages) {
				this.villageGenerator.generate(this.worldObj, x, z, null);
>>>>>>> origin/feature/nuclearthermalrockets
			}
		}

<<<<<<< HEAD
		if (p_230353_3_ == EntityClassification.MONSTER) {
			if (p_230353_2_.getStructureStart(p_230353_4_, false, Structure.PILLAGER_OUTPOST).isValid()) {
				return Structure.PILLAGER_OUTPOST.getSpawnList();
			}

			if (p_230353_2_.getStructureStart(p_230353_4_, false, Structure.MONUMENT).isValid()) {
				return Structure.MONUMENT.getSpawnList();
			}

			if (p_230353_2_.getStructureStart(p_230353_4_, true, Structure.FORTRESS).isValid()) {
				return Structure.FORTRESS.getSpawnList();
=======
			if (this.settings.useStrongholds) {
				this.strongholdGenerator.generate(this.worldObj, x, z, null);
			}

			if (this.settings.useTemples) {
				this.scatteredFeatureGenerator.generate(this.worldObj, x, z, null);
			}

			if (this.settings.useMonuments) {
				this.oceanMonumentGenerator.generate(this.worldObj, x, z, null);
>>>>>>> origin/feature/nuclearthermalrockets
			}
		}

<<<<<<< HEAD
		return super.func_230353_a_(p_230353_1_, p_230353_2_, p_230353_3_, p_230353_4_);
	}

	public void func_230354_a_(WorldGenRegion p_230354_1_) {
		//TODO: asm this
		if (getDimensionProperties().isHabitable()) {
			int i = p_230354_1_.getMainChunkX();
			int j = p_230354_1_.getMainChunkZ();
			Biome biome = p_230354_1_.getBiome((new ChunkPos(i, j)).asBlockPos());
			SharedSeedRandom sharedseedrandom = new SharedSeedRandom();
			sharedseedrandom.setDecorationSeed(p_230354_1_.getSeed(), i << 4, j << 4);
			WorldEntitySpawner.performWorldGenSpawning(p_230354_1_, biome, i, j, sharedseedrandom);
=======
	@Override
	@Nullable
	public BlockPos getNearestStructurePos(World worldIn, String structureName, BlockPos position, boolean findUnexplored) {
		if (!this.mapFeaturesEnabled || !habitable) {
			return null;
		} else if ("Stronghold".equals(structureName) && this.strongholdGenerator != null) {
			return this.strongholdGenerator.getNearestStructurePos(worldIn, position, findUnexplored);
		} else if ("Monument".equals(structureName) && this.oceanMonumentGenerator != null) {
			return this.oceanMonumentGenerator.getNearestStructurePos(worldIn, position, findUnexplored);
		} else if ("Village".equals(structureName) && this.villageGenerator != null) {
			return this.villageGenerator.getNearestStructurePos(worldIn, position, findUnexplored);
		} else if ("Mineshaft".equals(structureName) && this.mineshaftGenerator != null) {
			return this.mineshaftGenerator.getNearestStructurePos(worldIn, position, findUnexplored);
		} else {
			return "Temple".equals(structureName) && this.scatteredFeatureGenerator != null ? this.scatteredFeatureGenerator.getNearestStructurePos(worldIn, position, findUnexplored) : null;
		}
	}

	@Override
	public boolean isInsideStructure(World worldIn, String structureName, BlockPos pos) {
		if (!this.mapFeaturesEnabled || !habitable) {
			return false;
		} else if ("Stronghold".equals(structureName) && this.strongholdGenerator != null) {
			return this.strongholdGenerator.isInsideStructure(pos);
		} else if ("Monument".equals(structureName) && this.oceanMonumentGenerator != null) {
			return this.oceanMonumentGenerator.isInsideStructure(pos);
		} else if ("Village".equals(structureName) && this.villageGenerator != null) {
			return this.villageGenerator.isInsideStructure(pos);
		} else if ("Mineshaft".equals(structureName) && this.mineshaftGenerator != null) {
			return this.mineshaftGenerator.isInsideStructure(pos);
		}	else {
			return "Temple".equals(structureName) && this.scatteredFeatureGenerator != null && this.scatteredFeatureGenerator.isInsideStructure(pos);
>>>>>>> origin/feature/nuclearthermalrockets
		}
	}
}
