package zmaster587.advancedRocketry.world;

import java.util.Arrays;
import java.util.List;

import zmaster587.advancedRocketry.api.AdvancedRocketryBiomes;
import zmaster587.advancedRocketry.world.gen.BiomeCacheExtended;
import zmaster587.advancedRocketry.world.gen.GenLayerHillsExtended;
import zmaster587.advancedRocketry.world.gen.GenLayerVoronoiExtended;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.util.ReportedException;
import net.minecraft.world.World;
import net.minecraft.world.WorldType;
import net.minecraft.world.biome.BiomeCache;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.biome.WorldChunkManager;
import net.minecraft.world.gen.layer.GenLayer;
import net.minecraft.world.gen.layer.GenLayerAddIsland;
import net.minecraft.world.gen.layer.GenLayerAddMushroomIsland;
import net.minecraft.world.gen.layer.GenLayerAddSnow;
import net.minecraft.world.gen.layer.GenLayerDeepOcean;
import net.minecraft.world.gen.layer.GenLayerEdge;
import net.minecraft.world.gen.layer.GenLayerFuzzyZoom;
import net.minecraft.world.gen.layer.GenLayerHills;
import net.minecraft.world.gen.layer.GenLayerIsland;
import net.minecraft.world.gen.layer.GenLayerRareBiome;
import net.minecraft.world.gen.layer.GenLayerRemoveTooMuchOcean;
import net.minecraft.world.gen.layer.GenLayerRiver;
import net.minecraft.world.gen.layer.GenLayerRiverInit;
import net.minecraft.world.gen.layer.GenLayerRiverMix;
import net.minecraft.world.gen.layer.GenLayerSmooth;
import net.minecraft.world.gen.layer.GenLayerVoronoiZoom;
import net.minecraft.world.gen.layer.GenLayerZoom;
import net.minecraft.world.gen.layer.IntCache;
import net.minecraftforge.common.BiomeManager.BiomeEntry;

public class ChunkManagerPlanet extends WorldChunkManager {

	/** A GenLayer containing the indices into BiomeGenBase.biomeList[] */
	private GenLayer biomeIndexLayer;

	private BiomeCacheExtended biomeCache;

	private GenLayer genBiomes;

	private List<BiomeEntry> biomes;

	
	public ChunkManagerPlanet(long seed, WorldTypePlanetGen default1) {
		super(seed, default1);

		this.biomeCache = new BiomeCacheExtended(this);
		GenLayer[] agenlayer = initializeAllBiomeGenerators(seed, default1);
		agenlayer = getModdedBiomeGenerators(default1, seed, agenlayer);
		this.genBiomes = agenlayer[0];
		this.biomeIndexLayer = agenlayer[1];
	}

	public ChunkManagerPlanet(World world)
	{
		this(world.getSeed(), (WorldTypePlanetGen)world.getWorldInfo().getTerrainType());
		//Note: world MUST BE REGISTERED WITH THE DIMENSION MANAGER
		//This is a f***ing mess!
		biomes = DimensionManager.getInstance().getDimensionProperties(world.provider.dimensionId).getBiomes(world.provider.dimensionId);
	}

	/**
	 * the first array item is a linked list of the bioms, the second is the zoom function, the third is the same as the
	 * first.
	 */
	public static GenLayer[] initializeAllBiomeGenerators(long p_75901_0_, WorldType p_75901_2_)
	{
		boolean flag = false;
		GenLayerIsland genlayerisland = new GenLayerIsland(1L);
		GenLayerFuzzyZoom genlayerfuzzyzoom = new GenLayerFuzzyZoom(2000L, genlayerisland);
		GenLayerAddIsland genlayeraddisland = new GenLayerAddIsland(1L, genlayerfuzzyzoom);
		GenLayerZoom genlayerzoom = new GenLayerZoom(2001L, genlayeraddisland);
		genlayeraddisland = new GenLayerAddIsland(2L, genlayerzoom);
		genlayeraddisland = new GenLayerAddIsland(50L, genlayeraddisland);
		genlayeraddisland = new GenLayerAddIsland(70L, genlayeraddisland);
		GenLayerRemoveTooMuchOcean genlayerremovetoomuchocean = new GenLayerRemoveTooMuchOcean(2L, genlayeraddisland);
		GenLayerAddSnow genlayeraddsnow = new GenLayerAddSnow(2L, genlayerremovetoomuchocean);
		genlayeraddisland = new GenLayerAddIsland(3L, genlayeraddsnow);
		GenLayerEdge genlayeredge = new GenLayerEdge(2L, genlayeraddisland, GenLayerEdge.Mode.COOL_WARM);
		genlayeredge = new GenLayerEdge(2L, genlayeredge, GenLayerEdge.Mode.HEAT_ICE);
		genlayeredge = new GenLayerEdge(3L, genlayeredge, GenLayerEdge.Mode.SPECIAL);
		genlayerzoom = new GenLayerZoom(2002L, genlayeredge);
		genlayerzoom = new GenLayerZoom(2003L, genlayerzoom);
		genlayeraddisland = new GenLayerAddIsland(4L, genlayerzoom);
		GenLayerAddMushroomIsland genlayeraddmushroomisland = new GenLayerAddMushroomIsland(5L, genlayeraddisland);
		GenLayerDeepOcean genlayerdeepocean = new GenLayerDeepOcean(4L, genlayeraddmushroomisland);
		GenLayer genlayer2 = GenLayerZoom.magnify(1000L, genlayerdeepocean, 0);
		byte b0 = 4;

		if (p_75901_2_ == WorldType.LARGE_BIOMES)
		{
			b0 = 6;
		}

		if (flag)
		{
			b0 = 4;
		}
		b0 = GenLayer.getModdedBiomeSize(p_75901_2_, b0);

		GenLayer genlayer = GenLayerZoom.magnify(1000L, genlayer2, 0);
		GenLayerRiverInit genlayerriverinit = new GenLayerRiverInit(100L, genlayer);
		Object object = p_75901_2_.getBiomeLayer(p_75901_0_, genlayer2);

		GenLayer genlayer1 = GenLayerZoom.magnify(1000L, genlayerriverinit, 2);
		GenLayerHillsExtended genlayerhills = new GenLayerHillsExtended(1000L, (GenLayer)object, genlayer1);
		genlayer = GenLayerZoom.magnify(1000L, genlayerriverinit, 2);
		genlayer = GenLayerZoom.magnify(1000L, genlayer, b0);
		GenLayerRiver genlayerriver = new GenLayerRiver(1L, genlayer);
		GenLayerSmooth genlayersmooth = new GenLayerSmooth(1000L, genlayerriver);
		object = new GenLayerRareBiome(1001L, genlayerhills);

		for (int j = 0; j < b0; ++j)
		{
			object = new GenLayerZoom((long)(1000 + j), (GenLayer)object);

			if (j == 0)
			{
				object = new GenLayerAddIsland(3L, (GenLayer)object);
			}

			if (j == 1)
			{
				object = new zmaster587.advancedRocketry.world.gen.GenLayerShoreExtended(1000L, (GenLayer)object);
			}
		}

		GenLayerSmooth genlayersmooth1 = new GenLayerSmooth(1000L, (GenLayer)object);
		GenLayerRiverMix genlayerrivermix = new GenLayerRiverMix(100L, genlayersmooth1, genlayersmooth);
		GenLayerVoronoiExtended genlayervoronoizoom = new GenLayerVoronoiExtended(10L, genlayerrivermix);
		genlayerrivermix.initWorldGenSeed(p_75901_0_);
		genlayervoronoizoom.initWorldGenSeed(p_75901_0_);
		
		return new GenLayer[] {genlayerrivermix, genlayervoronoizoom, genlayerrivermix};
	}


	//Overridden for MOAR BIOMES!
	/**
	 * Returns a list of rainfall values for the specified blocks. Args: listToReuse, x, z, width, length.
	 */
	public float[] getRainfall(float[] p_76936_1_, int p_76936_2_, int p_76936_3_, int p_76936_4_, int p_76936_5_)
	{
		IntCache.resetIntCache();

		if (p_76936_1_ == null || p_76936_1_.length < p_76936_4_ * p_76936_5_)
		{
			p_76936_1_ = new float[p_76936_4_ * p_76936_5_];
		}

		int[] aint = this.biomeIndexLayer.getInts(p_76936_2_, p_76936_3_, p_76936_4_, p_76936_5_);

		for (int i1 = 0; i1 < p_76936_4_ * p_76936_5_; ++i1)
		{
			try
			{
				BiomeGenBase biome = AdvancedRocketryBiomes.instance.getBiomeById(aint[i1]);

				//TODO: debug
				float f = (float)biome.getIntRainfall() / 65536.0F;

				if (f > 1.0F)
				{
					f = 1.0F;
				}

				p_76936_1_[i1] = f;
			}
			catch (Throwable throwable)
			{
				CrashReport crashreport = CrashReport.makeCrashReport(throwable, "Invalid Biome id");
				CrashReportCategory crashreportcategory = crashreport.makeCategory("DownfallBlock");
				crashreportcategory.addCrashSection("biome id", Integer.valueOf(i1));
				crashreportcategory.addCrashSection("downfalls[] size", Integer.valueOf(p_76936_1_.length));
				crashreportcategory.addCrashSection("x", Integer.valueOf(p_76936_2_));
				crashreportcategory.addCrashSection("z", Integer.valueOf(p_76936_3_));
				crashreportcategory.addCrashSection("w", Integer.valueOf(p_76936_4_));
				crashreportcategory.addCrashSection("h", Integer.valueOf(p_76936_5_));
				throw new ReportedException(crashreport);
			}
		}

		return p_76936_1_;
	}

	/**
	 * Returns an array of biomes for the location input.
	 */
	
    /**
     * Returns the BiomeGenBase related to the x, z position on the world.
     */
    public BiomeGenBase getBiomeGenAt(int p_76935_1_, int p_76935_2_)
    {
        return this.biomeCache.getBiomeGenAt(p_76935_1_, p_76935_2_);
    }

	public BiomeGenBase[] getBiomesForGeneration(BiomeGenBase[] p_76937_1_, int p_76937_2_, int p_76937_3_, int p_76937_4_, int p_76937_5_,  DimensionProperties properties)
	{
		if (p_76937_1_ == null || p_76937_1_.length < p_76937_4_ * p_76937_5_)
		{
			p_76937_1_ = new BiomeGenBase[p_76937_4_ * p_76937_5_];
		}

		GenLayerBiomePlanet.setupBiomesForUse(biomes);
		int[] aint = this.genBiomes.getInts(p_76937_2_, p_76937_3_, p_76937_4_, p_76937_5_);

		try
		{
			for (int i1 = 0; i1 < p_76937_4_ * p_76937_5_; ++i1)
			{
				p_76937_1_[i1] = AdvancedRocketryBiomes.instance.getBiomeById(aint[i1]);
			}

			return p_76937_1_;
		}
		catch (Throwable throwable)
		{
			CrashReport crashreport = CrashReport.makeCrashReport(throwable, "Invalid Biome id");
			CrashReportCategory crashreportcategory = crashreport.makeCategory("RawBiomeBlock");
			crashreportcategory.addCrashSection("biomes[] size", Integer.valueOf(p_76937_1_.length));
			crashreportcategory.addCrashSection("x", Integer.valueOf(p_76937_2_));
			crashreportcategory.addCrashSection("z", Integer.valueOf(p_76937_3_));
			crashreportcategory.addCrashSection("w", Integer.valueOf(p_76937_4_));
			crashreportcategory.addCrashSection("h", Integer.valueOf(p_76937_5_));
			throw new ReportedException(crashreport);
		}
	}

	public BiomeGenBase[] loadBlockGeneratorData(BiomeGenBase[] p_76933_1_, int p_76933_2_, int p_76933_3_, int p_76933_4_, int p_76933_5_)
	{
		return this.getBiomeGenAt(p_76933_1_, p_76933_2_, p_76933_3_, p_76933_4_, p_76933_5_, true);
	}

	/**
	 * Return a list of biomes for the specified blocks. Args: listToReuse, x, y, width, length, cacheFlag (if false,
	 * don't check biomeCache to avoid infinite loop in BiomeCacheBlock)
	 */
	@Override
	public BiomeGenBase[] getBiomeGenAt(BiomeGenBase[] p_76931_1_, int p_76931_2_, int p_76931_3_, int p_76931_4_, int p_76931_5_, boolean p_76931_6_ )
	{
		IntCache.resetIntCache();



		if (p_76931_1_ == null || p_76931_1_.length < p_76931_4_ * p_76931_5_)
		{
			p_76931_1_ = new BiomeGenBase[p_76931_4_ * p_76931_5_];
		}

		if (p_76931_6_ && p_76931_4_ == 16 && p_76931_5_ == 16 && (p_76931_2_ & 15) == 0 && (p_76931_3_ & 15) == 0)
		{
			BiomeGenBase[] abiomegenbase1 = this.biomeCache.getCachedBiomes(p_76931_2_, p_76931_3_);
			System.arraycopy(abiomegenbase1, 0, p_76931_1_, 0, p_76931_4_ * p_76931_5_);
			return p_76931_1_;
		}
		else
		{

			GenLayerBiomePlanet.setupBiomesForUse(biomes);
			int[] aint = this.biomeIndexLayer.getInts(p_76931_2_, p_76931_3_, p_76931_4_, p_76931_5_);

			for (int i1 = 0; i1 < p_76931_4_ * p_76931_5_; ++i1)
			{
				p_76931_1_[i1] = AdvancedRocketryBiomes.instance.getBiomeById(aint[i1]);
			}

			return p_76931_1_;
		}
	}
}
