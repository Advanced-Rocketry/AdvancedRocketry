package zmaster587.advancedRocketry.world;

import net.minecraft.block.BlockFalling;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.init.Biomes;
import net.minecraft.init.Blocks;
import net.minecraft.util.IProgressUpdate;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.WorldEntitySpawner;
import net.minecraft.world.WorldType;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.Biome.SpawnListEntry;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ChunkPrimer;
import net.minecraft.world.gen.*;
import net.minecraft.world.gen.feature.WorldGenDungeons;
import net.minecraft.world.gen.feature.WorldGenLakes;
import net.minecraft.world.gen.structure.*;
import net.minecraftforge.event.terraingen.TerrainGen;
import zmaster587.advancedRocketry.api.ARConfiguration;
import zmaster587.advancedRocketry.dimension.DimensionManager;
import zmaster587.advancedRocketry.dimension.DimensionProperties;
import zmaster587.advancedRocketry.dimension.DimensionProperties.Temps;
import zmaster587.advancedRocketry.event.PlanetEventHandler;
import zmaster587.advancedRocketry.util.OreGenProperties;
import zmaster587.advancedRocketry.util.OreGenProperties.OreEntry;
import zmaster587.advancedRocketry.world.decoration.MapGenCaveExt;
import zmaster587.advancedRocketry.world.decoration.MapGenCrater;
import zmaster587.advancedRocketry.world.decoration.MapGenGeode;
import zmaster587.advancedRocketry.world.decoration.MapGenRavineExt;
import zmaster587.advancedRocketry.world.decoration.MapGenSpaceVillage;
import zmaster587.advancedRocketry.world.decoration.MapGenVolcano;
import zmaster587.advancedRocketry.world.ore.CustomizableOreGen;
import zmaster587.advancedRocketry.world.provider.WorldProviderPlanet;

import java.util.List;
import java.util.Random;

import javax.annotation.Nullable;

import static net.minecraftforge.event.terraingen.InitMapGenEvent.EventType.*;

public class ChunkProviderPlanet implements IChunkGenerator {
	/** RNG. */
	protected static final IBlockState STONE = Blocks.STONE.getDefaultState();
	private final Random rand;
	private NoiseGeneratorOctaves minLimitPerlinNoise;
	private NoiseGeneratorOctaves maxLimitPerlinNoise;
	private NoiseGeneratorOctaves mainPerlinNoise;
	private NoiseGeneratorPerlin surfaceNoise;
	public NoiseGeneratorOctaves scaleNoise;
	public NoiseGeneratorOctaves depthNoise;
	public NoiseGeneratorOctaves forestNoise;
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
	double[] mainNoiseRegion;
	double[] minLimitRegion;
	double[] maxLimitRegion;
	double[] depthRegion;
	protected int heightmapOffset = 0;
	protected float heightmapMult = 1f;
	protected boolean habitable = false;


	private MapGenCrater craterGenerator;
	private MapGenGeode geodeGenerator;
	private MapGenVolcano volcanoGenerator;

	{
		caveGenerator = TerrainGen.getModdedMapGen(caveGenerator, CAVE);
		strongholdGenerator = (MapGenStronghold) TerrainGen.getModdedMapGen(strongholdGenerator, STRONGHOLD);
		villageGenerator = (MapGenVillage) TerrainGen.getModdedMapGen(villageGenerator, VILLAGE);
		mineshaftGenerator = (MapGenMineshaft) TerrainGen.getModdedMapGen(mineshaftGenerator, MINESHAFT);
		scatteredFeatureGenerator = (MapGenScatteredFeature) TerrainGen.getModdedMapGen(scatteredFeatureGenerator, SCATTERED_FEATURE);
		ravineGenerator = TerrainGen.getModdedMapGen(ravineGenerator, RAVINE);
	}

	public ChunkProviderPlanet(World worldIn, long seed, boolean mapFeaturesEnabledIn, String p_i46668_5_)
	{
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
		
        for (int i = -2; i <= 2; ++i)
        {
            for (int j = -2; j <= 2; ++j)
            {
                float f = 10.0F / MathHelper.sqrt((float)(i * i + j * j) + 0.2F);
                this.biomeWeights[i + 2 + (j + 2) * 5] = f;
            }
        }
        
        if (p_i46668_5_ != null)
        {
            this.settings = ChunkGeneratorSettings.Factory.jsonToFactory(p_i46668_5_).build();
            this.oceanBlock = this.settings.useLavaOceans ? Blocks.LAVA.getDefaultState() : Blocks.WATER.getDefaultState();
            worldIn.setSeaLevel(dimProps.getSeaLevel());
        }
        
        IBlockState oceanBlock = dimProps.getOceanBlock();
        if(oceanBlock != null)
        {
        	this.oceanBlock = oceanBlock;
        	if(caveGenerator instanceof MapGenCaveExt)
        		((MapGenCaveExt)caveGenerator).setOceanBlock(this.oceanBlock);
        	if(ravineGenerator instanceof MapGenRavineExt)
        		((MapGenRavineExt)ravineGenerator).setOceanBlock(this.oceanBlock);
        }
        
        IBlockState fillBlock = dimProps.getStoneBlock();
        if(fillBlock != null)
        {
        	this.fillblock = fillBlock;
        	if(caveGenerator instanceof MapGenCaveExt)
        		((MapGenCaveExt)caveGenerator).setFillerBlock(this.fillblock);
        	if(ravineGenerator instanceof MapGenRavineExt)
        		((MapGenRavineExt)ravineGenerator).setFillerBlock(this.fillblock);
        }
        

		net.minecraftforge.event.terraingen.InitNoiseGensEvent.ContextOverworld ctx =
				new net.minecraftforge.event.terraingen.InitNoiseGensEvent.ContextOverworld(minLimitPerlinNoise, maxLimitPerlinNoise, mainPerlinNoise, surfaceNoise, scaleNoise, depthNoise, forestNoise);
		ctx = net.minecraftforge.event.terraingen.TerrainGen.getModdedNoiseGenerators(worldIn, this.rand, ctx);
		this.minLimitPerlinNoise = ctx.getLPerlin1();
		this.maxLimitPerlinNoise = ctx.getLPerlin2();
		this.mainPerlinNoise = ctx.getPerlin();
		this.surfaceNoise = ctx.getHeight();
		this.scaleNoise = ctx.getScale();
		this.depthNoise = ctx.getDepth();
		this.forestNoise = ctx.getForest();
		
		this.mapFeaturesEnabled = dimProps.canGenerateStructures() && ARConfiguration.getCurrentConfig().generateVanillaStructures;

		//TODO: may break on little planets
		float atmDensity = ((WorldProviderPlanet)worldObj.provider).getAtmosphereDensity(new BlockPos(0,0,0));
		habitable = ((WorldProviderPlanet)worldObj.provider).getAtmosphere(new BlockPos(0,0,0)).isBreathable();
		

		if(ARConfiguration.getCurrentConfig().generateCraters && dimProps.canGenerateCraters())
			craterGenerator = new MapGenCrater( (int)((10 +  (26*(1-atmDensity)) )*dimProps.getCraterMultiplier()));
		else 
			craterGenerator = null;

		if(dimProps.canGenerateGeodes() && ARConfiguration.getCurrentConfig().generateGeodes) {
			geodeGenerator = new MapGenGeode((int)(800 * dimProps.getGeodeMultiplier()));
		}
		else
			geodeGenerator = null;
		
		if(dimProps.canGenerateVolcanos() && ARConfiguration.getCurrentConfig().generateVolcanos) {
			volcanoGenerator = new MapGenVolcano((int)(800 * dimProps.getVolcanoMultiplier()));
		}
		else
			volcanoGenerator = null;
	}

	public void setBlocksInChunk(int x, int z, ChunkPrimer primer)
	{
		byte b0 = 63;
		//TODO: may break for little planets
		this.biomesForGeneration = this.worldObj.getBiomeProvider().getBiomesForGeneration(this.biomesForGeneration, x * 4 - 2, z * 4 - 2, 10, 10);
        this.generateHeightmap(x * 4, 0, z * 4);

        for (int i = 0; i < 4; ++i)
        {
            int j = i * 5;
            int k = (i + 1) * 5;

            for (int l = 0; l < 4; ++l)
            {
                int i1 = (j + l) * 33;
                int j1 = (j + l + 1) * 33;
                int k1 = (k + l) * 33;
                int l1 = (k + l + 1) * 33;

                for (int i2 = 0; i2 < 32; ++i2)
                {
                    double d0 = 0.125D;
                    double d1 = this.heightMap[i1 + i2];
                    double d2 = this.heightMap[j1 + i2];
                    double d3 = this.heightMap[k1 + i2];
                    double d4 = this.heightMap[l1 + i2];
                    double d5 = (this.heightMap[i1 + i2 + 1] - d1) * 0.125D;
                    double d6 = (this.heightMap[j1 + i2 + 1] - d2) * 0.125D;
                    double d7 = (this.heightMap[k1 + i2 + 1] - d3) * 0.125D;
                    double d8 = (this.heightMap[l1 + i2 + 1] - d4) * 0.125D;

                    for (int j2 = 0; j2 < 8; ++j2)
                    {
                        double d9 = 0.25D;
                        double d10 = d1;
                        double d11 = d2;
                        double d12 = (d3 - d1) * 0.25D;
                        double d13 = (d4 - d2) * 0.25D;

                        for (int k2 = 0; k2 < 4; ++k2)
                        {
                            double d14 = 0.25D;
                            double d16 = (d11 - d10) * 0.25D;
                            double lvt_45_1_ = d10 - d16;

                            for (int l2 = 0; l2 < 4; ++l2)
                            {
                                if ((lvt_45_1_ += d16) > 0.0D)
                                {
                                    primer.setBlockState(i * 4 + k2, heightmapOffset + i2 * 8 + j2, l * 4 + l2, this.fillblock);
                                }
                                else if (i2 * 8 + j2 < worldObj.getSeaLevel())
                                {
                                    primer.setBlockState(i * 4 + k2, heightmapOffset + i2 * 8 + j2, l * 4 + l2, this.oceanBlock);
                                }
                            }

                            d10 += d12;
                            d11 += d13;
                        }

                        d1 += d5;
                        d2 += d6;
                        d3 += d7;
                        d4 += d8;
                    }
                }
            }
        }
	}

	public void replaceBiomeBlocks(int x, int z, ChunkPrimer primer, Biome[] biomesIn)
	{
		if (!net.minecraftforge.event.ForgeEventFactory.onReplaceBiomeBlocks(this, x, z, primer, this.worldObj)) return;
		double d0 = 0.03125D;
		this.depthBuffer = this.surfaceNoise.getRegion(this.depthBuffer, (double)(x * 16), (double)(z * 16), 16, 16, 0.0625D, 0.0625D, 1.0D);

		for (int i = 0; i < 16; ++i)
		{
			for (int j = 0; j < 16; ++j)
			{
				Biome biome = biomesIn[j + i * 16];
				biome.genTerrainBlocks(this.worldObj, this.rand, primer, x * 16 + i, z * 16 + j, this.depthBuffer[j + i * 16]);
			}
		}
	}

	protected ChunkPrimer getChunkPrimer(int x, int z) 
	{
		ChunkPrimer chunkprimer = new ChunkPrimer();
		this.setBlocksInChunk(x, z, chunkprimer);
		this.biomesForGeneration = this.worldObj.getBiomeProvider().getBiomesForGeneration(this.biomesForGeneration, x * 16, z * 16, 16, 16);
		this.replaceBiomeBlocks(x, z, chunkprimer, this.biomesForGeneration);

		if (this.settings.useCaves)
		{
			this.caveGenerator.generate(this.worldObj, x, z, chunkprimer);
		}

		if (this.settings.useRavines)
		{
			this.ravineGenerator.generate(this.worldObj, x, z, chunkprimer);
		}

		if(this.craterGenerator != null)
			this.craterGenerator.generate(this.worldObj, x, z, chunkprimer);
		
		if(this.volcanoGenerator != null)
			this.volcanoGenerator.generate(this.worldObj, x, z, chunkprimer);

		if(this.geodeGenerator != null)
			this.geodeGenerator.generate(this.worldObj, x, z, chunkprimer);

		if (this.mapFeaturesEnabled && habitable)
		{
            if (this.settings.useMineShafts)
            {
                this.mineshaftGenerator.generate(this.worldObj, x, z, chunkprimer);
            }

            if (this.settings.useVillages)
            {
                this.villageGenerator.generate(this.worldObj, x, z, chunkprimer);
            }

            if (this.settings.useStrongholds)
            {
                this.strongholdGenerator.generate(this.worldObj, x, z, chunkprimer);
            }

            if (this.settings.useTemples)
            {
                this.scatteredFeatureGenerator.generate(this.worldObj, x, z, chunkprimer);
            }

            if (this.settings.useMonuments)
            {
                this.oceanMonumentGenerator.generate(this.worldObj, x, z, chunkprimer);
            }
		}
		
		return chunkprimer;
	}
	
	/**
	 * Will return back a chunk, if it doesn't exist and its not a MP client it will generates all the blocks for the
	 * specified chunk from the map seed and chunk seed
	 */
	@Override
	public Chunk generateChunk(int x, int z)
	{
		this.rand.setSeed((long)x * 341873128712L + (long)z * 132897987541L);


		//ChunkExtendedBiome
		Chunk chunk = new Chunk(this.worldObj, getChunkPrimer(x, z), x, z);
		byte[] abyte = chunk.getBiomeArray();

		for (int i = 0; i < abyte.length; ++i)
		{
			abyte[i] = (byte)Biome.getIdForBiome(this.biomesForGeneration[i]);
		}

		chunk.generateSkylightMap();
		return chunk;
	}

	private void generateHeightmap(int p_185978_1_, int p_185978_2_, int p_185978_3_)
	{
		this.depthRegion = this.depthNoise.generateNoiseOctaves(this.depthRegion, p_185978_1_, p_185978_3_, 5, 5, (double)this.settings.depthNoiseScaleX, (double)this.settings.depthNoiseScaleZ, (double)this.settings.depthNoiseScaleExponent);
		float f = this.settings.coordinateScale;
		float f1 = this.settings.heightScale * heightmapMult;
		this.mainNoiseRegion = this.mainPerlinNoise.generateNoiseOctaves(this.mainNoiseRegion, p_185978_1_, p_185978_2_, p_185978_3_, 5, 33, 5, (double)(f / this.settings.mainNoiseScaleX), (double)(f1 / this.settings.mainNoiseScaleY), (double)(f / this.settings.mainNoiseScaleZ));
		this.minLimitRegion = this.minLimitPerlinNoise.generateNoiseOctaves(this.minLimitRegion, p_185978_1_, p_185978_2_, p_185978_3_, 5, 33, 5, (double)f, (double)f1, (double)f);
		this.maxLimitRegion = this.maxLimitPerlinNoise.generateNoiseOctaves(this.maxLimitRegion, p_185978_1_, p_185978_2_, p_185978_3_, 5, 33, 5, (double)f, (double)f1, (double)f);
		int i = 0;
		int j = 0;

		for (int k = 0; k < 5; ++k)
		{
			for (int l = 0; l < 5; ++l)
			{
				float f2 = 0.0F;
				float f3 = 0.0F;
				float f4 = 0.0F;
				int i1 = 2;
				Biome biome = this.biomesForGeneration[k + 2 + (l + 2) * 10];

				for (int j1 = -2; j1 <= 2; ++j1)
				{
					for (int k1 = -2; k1 <= 2; ++k1)
					{
						Biome biome1 = this.biomesForGeneration[k + j1 + 2 + (l + k1 + 2) * 10];
						float f5 = this.settings.biomeDepthOffSet + biome1.getBaseHeight() * this.settings.biomeDepthWeight;
						float f6 = this.settings.biomeScaleOffset + biome1.getHeightVariation() * this.settings.biomeScaleWeight;

						if (this.terrainType == WorldType.AMPLIFIED && f5 > 0.0F)
						{
							f5 = 1.0F + f5 * 2.0F;
							f6 = 1.0F + f6 * 4.0F;
						}

						float f7 = this.biomeWeights[j1 + 2 + (k1 + 2) * 5] / (f5 + 2.0F);

						if (biome1.getBaseHeight() > biome.getBaseHeight())
						{
							f7 /= 2.0F;
						}

						f2 += f6 * f7;
						f3 += f5 * f7;
						f4 += f7;
					}
				}

				f2 = f2 / f4;
				f3 = f3 / f4;
				f2 = f2 * 0.9F + 0.1F;
				f3 = (f3 * 4.0F - 1.0F) / 8.0F;
				double d7 = this.depthRegion[j] / 8000.0D;

				if (d7 < 0.0D)
				{
					d7 = -d7 * 0.3D;
				}

				d7 = d7 * 3.0D - 2.0D;

				if (d7 < 0.0D)
				{
					d7 = d7 / 2.0D;

					if (d7 < -1.0D)
					{
						d7 = -1.0D;
					}

					d7 = d7 / 1.4D;
					d7 = d7 / 2.0D;
				}
				else
				{
					if (d7 > 1.0D)
					{
						d7 = 1.0D;
					}

					d7 = d7 / 8.0D;
				}

				++j;
				double d8 = (double)f3;
				double d9 = (double)f2;
				d8 = d8 + d7 * 0.2D;
				d8 = d8 * (double)this.settings.baseSize / 8.0D;
				double d0 = (double)this.settings.baseSize + d8 * 4.0D;

				for (int l1 = 0; l1 < 33; ++l1)
				{
					double d1 = ((double)l1 - d0) * (double)this.settings.stretchY * 128.0D / 256.0D / d9;

					if (d1 < 0.0D)
					{
						d1 *= 4.0D;
					}

					double d2 = this.minLimitRegion[i] / (double)this.settings.lowerLimitScale;
					double d3 = this.maxLimitRegion[i] / (double)this.settings.upperLimitScale;
					double d4 = (this.mainNoiseRegion[i] / 10.0D + 1.0D) / 2.0D;
					double d5 = MathHelper.clamp(d2, d3, d4) - d1;

					if (l1 > 29)
					{
						double d6 = (double)((float)(l1 - 29) / 3.0F);
						d5 = d5 * (1.0D - d6) + -10.0D * d6;
					}

					this.heightMap[i] = d5;
					++i;
				}
			}
		}
	}

	/**
	 * Checks to see if a chunk exists at x, y
	 */
	public boolean chunkExists(int p_73149_1_, int p_73149_2_)
	{
		return true;
	}

	/**
	 * Populates chunk with ores etc etc
	 */
	@Override
	public void populate(int x, int z)
	{
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

		net.minecraftforge.event.ForgeEventFactory.onChunkPopulate(true, this, this.worldObj, this.rand, x, z, flag);

		if (this.mapFeaturesEnabled && habitable)
		{
			if (this.settings.useMineShafts)
			{
				this.mineshaftGenerator.generateStructure(this.worldObj, this.rand, chunkpos);
			}

			if (this.settings.useVillages)
			{
				flag = this.villageGenerator.generateStructure(this.worldObj, this.rand, chunkpos);
			}

			if (this.settings.useStrongholds)
			{
				this.strongholdGenerator.generateStructure(this.worldObj, this.rand, chunkpos);
			}

			if (this.settings.useTemples)
			{
				this.scatteredFeatureGenerator.generateStructure(this.worldObj, this.rand, chunkpos);
			}

			if (this.settings.useMonuments)
			{
				this.oceanMonumentGenerator.generateStructure(this.worldObj, this.rand, chunkpos);
			}
		}

		if (false && biome != Biomes.DESERT && biome != Biomes.DESERT_HILLS && this.settings.useWaterLakes && !flag && this.rand.nextInt(this.settings.waterLakeChance) == 0)
			if (net.minecraftforge.event.terraingen.TerrainGen.populate(this, this.worldObj, this.rand, x, z, flag, net.minecraftforge.event.terraingen.PopulateChunkEvent.Populate.EventType.LAKE))
			{
				int i1 = this.rand.nextInt(16) + 8;
				int j1 = this.rand.nextInt(256);
				int k1 = this.rand.nextInt(16) + 8;
				(new WorldGenLakes(Blocks.WATER)).generate(this.worldObj, this.rand, blockpos.add(i1, j1, k1));
			}

		if (false && !flag && this.rand.nextInt(this.settings.lavaLakeChance / 10) == 0 && this.settings.useLavaLakes)
			if (net.minecraftforge.event.terraingen.TerrainGen.populate(this, this.worldObj, this.rand, x, z, flag, net.minecraftforge.event.terraingen.PopulateChunkEvent.Populate.EventType.LAVA))
			{
				int i2 = this.rand.nextInt(16) + 8;
				int l2 = this.rand.nextInt(this.rand.nextInt(248) + 8);
				int k3 = this.rand.nextInt(16) + 8;

				if (l2 < this.worldObj.getSeaLevel() || this.rand.nextInt(this.settings.lavaLakeChance / 8) == 0)
				{
					(new WorldGenLakes(Blocks.LAVA)).generate(this.worldObj, this.rand, blockpos.add(i2, l2, k3));
				}
			}

		if (habitable && this.settings.useDungeons)
			if (net.minecraftforge.event.terraingen.TerrainGen.populate(this, this.worldObj, this.rand, x, z, flag, net.minecraftforge.event.terraingen.PopulateChunkEvent.Populate.EventType.DUNGEON))
			{
				for (int j2 = 0; j2 < this.settings.dungeonChance; ++j2)
				{
					int i3 = this.rand.nextInt(16) + 8;
					int l3 = this.rand.nextInt(256);
					int l1 = this.rand.nextInt(16) + 8;
					(new WorldGenDungeons()).generate(this.worldObj, this.rand, blockpos.add(i3, l3, l1));
				}
			}

		biome.decorate(this.worldObj, this.rand, new BlockPos(i, 0, j));
		if (net.minecraftforge.event.terraingen.TerrainGen.populate(this, this.worldObj, this.rand, x, z, flag, net.minecraftforge.event.terraingen.PopulateChunkEvent.Populate.EventType.ANIMALS))
			WorldEntitySpawner.performWorldGenSpawning(this.worldObj, biome, i + 8, j + 8, 16, 16, this.rand);
		blockpos = blockpos.add(8, 0, 8);

		if (net.minecraftforge.event.terraingen.TerrainGen.populate(this, this.worldObj, this.rand, x, z, flag, net.minecraftforge.event.terraingen.PopulateChunkEvent.Populate.EventType.ICE))
		{
			for (int k2 = 0; k2 < 16; ++k2)
			{
				for (int j3 = 0; j3 < 16; ++j3)
				{
					BlockPos blockpos1 = this.worldObj.getPrecipitationHeight(blockpos.add(k2, 0, j3));
					BlockPos blockpos2 = blockpos1.down();

					if (this.worldObj.canBlockFreezeWater(blockpos2))
					{
						this.worldObj.setBlockState(blockpos2, Blocks.ICE.getDefaultState(), 2);
					}

					if (this.worldObj.canSnowAt(blockpos1, true))
					{
						this.worldObj.setBlockState(blockpos1, Blocks.SNOW_LAYER.getDefaultState(), 2);
					}
				}
			}
		}//Forge: End ICE


		//If a planet is terraformed chenge upper blocks
		if(zmaster587.advancedRocketry.api.ARConfiguration.getCurrentConfig().allowTerraforming && worldObj.provider.getClass() == WorldProviderPlanet.class) {

			if(DimensionManager.getInstance().getDimensionProperties(worldObj.provider.getDimension()).isTerraformed()) {
				Chunk chunk = worldObj.getChunkFromChunkCoords(x, z);
				PlanetEventHandler.modifyChunk(worldObj, (WorldProviderPlanet)worldObj.provider, chunk);
			}
		}

		net.minecraftforge.event.ForgeEventFactory.onChunkPopulate(false, this, this.worldObj, this.rand, x, z, flag);

		OreGenProperties oreGenProperties = DimensionManager.getInstance().getDimensionProperties(this.worldObj.provider.getDimension()).getOreGenProperties(this.worldObj);
		
		if(oreGenProperties != null) {
			for(OreEntry entry : oreGenProperties.getOreEntries()) {
				new CustomizableOreGen(entry).generate(rand, x, z, this.worldObj, this, this.worldObj.getChunkProvider());
			}
		}
		
		BlockFalling.fallInstantly = false;
	}

	/**
	 * Two modes of operation: if passed true, save all Chunks in one go.  If passed false, save up to two chunks.
	 * Return true if all chunks have been saved.
	 */
	public boolean saveChunks(boolean p_73151_1_, IProgressUpdate p_73151_2_)
	{
		return true;
	}

	/**
	 * Save extra data not associated with any Chunk.  Not saved during autosave, only during world unload.  Currently
	 * unimplemented.
	 */
	public void saveExtraData() {}

	/**
	 * Unloads chunks that are marked to be unloaded. This is not guaranteed to unload every such chunk.
	 */
	public boolean unloadQueuedChunks()
	{
		return false;
	}

	/**
	 * Returns if the IChunkProvider supports saving.
	 */
	public boolean canSave()
	{
		return true;
	}

	/**
	 * Converts the instance data to a readable string.
	 */
	public String makeString()
	{
		return "RandomLevelSource";
	}

	/**
	 * Returns a list of creatures of the specified type that can spawn at the given location.
	 */
	@Override
	public List<SpawnListEntry> getPossibleCreatures(
			EnumCreatureType creatureType, BlockPos pos) {
		Biome biome = this.worldObj.getBiome(pos);

        return biome.getSpawnableList(creatureType);
	}

	@Override
	public boolean generateStructures(Chunk chunkIn, int x, int z) {
		return false;
	}

	@Override
    public void recreateStructures(Chunk chunkIn, int x, int z)
    {
        if (this.mapFeaturesEnabled || !habitable)
        {
            if (this.settings.useMineShafts)
            {
                this.mineshaftGenerator.generate(this.worldObj, x, z, (ChunkPrimer)null);
            }

            if (this.settings.useVillages)
            {
                this.villageGenerator.generate(this.worldObj, x, z, (ChunkPrimer)null);
            }

            if (this.settings.useStrongholds)
            {
                this.strongholdGenerator.generate(this.worldObj, x, z, (ChunkPrimer)null);
            }

            if (this.settings.useTemples)
            {
                this.scatteredFeatureGenerator.generate(this.worldObj, x, z, (ChunkPrimer)null);
            }

            if (this.settings.useMonuments)
            {
                this.oceanMonumentGenerator.generate(this.worldObj, x, z, (ChunkPrimer)null);
            }
        }
    }

    @Override
    @Nullable
    public BlockPos getNearestStructurePos(World worldIn, String structureName, BlockPos position, boolean findUnexplored)
    {
        if (!this.mapFeaturesEnabled || !habitable)
        {
            return null;
        }
        else if ("Stronghold".equals(structureName) && this.strongholdGenerator != null)
        {
            return this.strongholdGenerator.getNearestStructurePos(worldIn, position, findUnexplored);
        }
        else if ("Monument".equals(structureName) && this.oceanMonumentGenerator != null)
        {
            return this.oceanMonumentGenerator.getNearestStructurePos(worldIn, position, findUnexplored);
        }
        else if ("Village".equals(structureName) && this.villageGenerator != null)
        {
            return this.villageGenerator.getNearestStructurePos(worldIn, position, findUnexplored);
        }
        else if ("Mineshaft".equals(structureName) && this.mineshaftGenerator != null)
        {
            return this.mineshaftGenerator.getNearestStructurePos(worldIn, position, findUnexplored);
        }
        else
        {
            return "Temple".equals(structureName) && this.scatteredFeatureGenerator != null ? this.scatteredFeatureGenerator.getNearestStructurePos(worldIn, position, findUnexplored) : null;
        }
    }

    @Override
    public boolean isInsideStructure(World worldIn, String structureName, BlockPos pos)
    {
        if (!this.mapFeaturesEnabled || !habitable)
        {
            return false;
        }
        else if ("Stronghold".equals(structureName) && this.strongholdGenerator != null)
        {
            return this.strongholdGenerator.isInsideStructure(pos);
        }
        else if ("Monument".equals(structureName) && this.oceanMonumentGenerator != null)
        {
            return this.oceanMonumentGenerator.isInsideStructure(pos);
        }
        else if ("Village".equals(structureName) && this.villageGenerator != null)
        {
            return this.villageGenerator.isInsideStructure(pos);
        }
        else if ("Mineshaft".equals(structureName) && this.mineshaftGenerator != null)
        {
            return this.mineshaftGenerator.isInsideStructure(pos);
        }
        else
        {
            return "Temple".equals(structureName) && this.scatteredFeatureGenerator != null ? this.scatteredFeatureGenerator.isInsideStructure(pos) : false;
        }
    }
}
