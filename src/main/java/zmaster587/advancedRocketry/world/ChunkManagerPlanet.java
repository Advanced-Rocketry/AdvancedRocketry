package zmaster587.advancedRocketry.world;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;

import cpw.mods.fml.relauncher.ReflectionHelper;
import zmaster587.advancedRocketry.AdvancedRocketry;
import zmaster587.advancedRocketry.api.AdvancedRocketryBiomes;
import zmaster587.advancedRocketry.dimension.DimensionManager;
import zmaster587.advancedRocketry.dimension.DimensionProperties;
import zmaster587.advancedRocketry.world.gen.BiomeCacheExtended;
import zmaster587.advancedRocketry.world.gen.GenLayerHillsExtended;
import zmaster587.advancedRocketry.world.gen.GenLayerVoronoiExtended;
import zmaster587.advancedRocketry.world.type.WorldTypePlanetGen;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.util.LongHashMap;
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
	//TODO: make higher biome ids work
	/** A GenLayer containing the indices into BiomeGenBase.biomeList[] */
	private GenLayer biomeIndexLayer;

	private BiomeCache biomeCache;

	private GenLayer genBiomes;

	private List<BiomeEntry> biomes;
	
	private static Field fBiomeCacheMap;
	private static Field fBiomeCache;
	
	public ChunkManagerPlanet(long seed, WorldType default1, DimensionProperties properties) {

		this.biomeCache = new BiomeCache(this);//new BiomeCacheExtended(this);
		//TODO: more biomes
		//TODO: remove rivers
		GenLayer[] agenlayer = initializeAllBiomeGenerators(seed, default1, properties);//GenLayer.initializeAllBiomeGenerators(seed, default1); //;
		agenlayer = getModdedBiomeGenerators(default1, seed, agenlayer);
		this.genBiomes = agenlayer[0];
		this.biomeIndexLayer = agenlayer[1];
		
		fBiomeCache = ReflectionHelper.findField(BiomeCache.class, "cache", "field_76841_d");
		fBiomeCache.setAccessible(true);
		
		fBiomeCacheMap = ReflectionHelper.findField(BiomeCache.class, "cacheMap", "field_76843_c");
		fBiomeCacheMap.setAccessible(true);
	}

	
	public ChunkManagerPlanet(World world, List biomes)
	{
		this(world.getSeed(), (WorldTypePlanetGen)world.getWorldInfo().getTerrainType(), DimensionManager.getInstance().getDimensionProperties(world.provider.dimensionId));
		//Note: world MUST BE REGISTERED WITH THE DIMENSION MANAGER
		//This is a mess!
		this.biomes = biomes;
	}

	/**
	 * the first array item is a linked list of the bioms, the second is the zoom function, the third is the same as the
	 * first.
	 */
	public static GenLayer[] initializeAllBiomeGenerators(long seed, WorldType type, DimensionProperties properties)
	{
		boolean flag = false;
		boolean hasRivers = properties.hasRivers();

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

		if (type == WorldType.LARGE_BIOMES)
		{
			b0 = 6;
		}

		if (flag)
		{
			b0 = 4;
		}
		b0 = GenLayer.getModdedBiomeSize(type, b0);

		GenLayer genlayer = GenLayerZoom.magnify(1000L, genlayer2, 0);
		Object object = type.getBiomeLayer(seed, genlayer2);

		GenLayer genlayer1;


		if(hasRivers) {
			GenLayerRiverInit genlayerriverinit = new GenLayerRiverInit(100L, genlayer);
			genlayer1 = GenLayerZoom.magnify(1000L, genlayerriverinit, 2);
			genlayer = GenLayerZoom.magnify(1000L, genlayerriverinit, 2);
		}
		else
			genlayer1 = genlayer;

		GenLayerHillsExtended genlayerhills = new GenLayerHillsExtended(1000L, (GenLayer)object, genlayer1);

		genlayer = GenLayerZoom.magnify(1000L, genlayer, b0);

		GenLayerSmooth genlayersmooth;
		if(hasRivers) {
			GenLayerRiver genlayerriver = new GenLayerRiver(1L, genlayer);
			genlayersmooth = new GenLayerSmooth(1000L, genlayerriver);
		}
		else
			genlayersmooth = new GenLayerSmooth(1000L, genlayer);

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
		GenLayerVoronoiExtended genlayervoronoizoom;
		if(hasRivers) {
			GenLayerRiverMix genlayerrivermix = new GenLayerRiverMix(100L, genlayersmooth1, genlayersmooth);
			genlayervoronoizoom = new GenLayerVoronoiExtended(10L, genlayerrivermix);
			genlayerrivermix.initWorldGenSeed(seed);
			genlayer = genlayerrivermix;
		}
		else
			genlayer = genlayervoronoizoom = new GenLayerVoronoiExtended(10L, genlayersmooth1);
		
		genlayervoronoizoom.initWorldGenSeed(seed);

		return new GenLayer[] {genlayer, genlayervoronoizoom, genlayer};
	}


	//Overridden for MOAR BIOMES!
	/**
	 * Returns a list of rainfall values for the specified blocks. Args: listToReuse, x, z, width, length.
	 */
	@Override
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
				if(biome == null)
					System.out.println("shit");

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
	@Override
	public BiomeGenBase getBiomeGenAt(int p_76935_1_, int p_76935_2_)
	{
		return this.biomeCache.getBiomeGenAt(p_76935_1_, p_76935_2_);
	}
	
	
	public void resetCache() {
		
		try {
			fBiomeCacheMap.set(this.biomeCache, new LongHashMap());
			((List)fBiomeCache.get(this.biomeCache)).clear();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public BiomeGenBase[] getBiomesForGeneration(BiomeGenBase[] p_76937_1_, int p_76937_2_, int p_76937_3_, int p_76937_4_, int p_76937_5_,  DimensionProperties properties)
	{
		GenLayerBiomePlanet.setupBiomesForUse(biomes);
		//return super.getBiomesForGeneration(p_76937_1_, p_76937_2_, p_76937_3_, p_76937_4_, p_76937_5_);

		IntCache.resetIntCache();

		if (p_76937_1_ == null || p_76937_1_.length < p_76937_4_ * p_76937_5_)
		{
			p_76937_1_ = new BiomeGenBase[p_76937_4_ * p_76937_5_];
		}


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

	@Override
	public BiomeGenBase[] loadBlockGeneratorData(BiomeGenBase[] p_76933_1_, int p_76933_2_, int p_76933_3_, int p_76933_4_, int p_76933_5_)
	{
		return this.getBiomeGenAt(p_76933_1_, p_76933_2_, p_76933_3_, p_76933_4_, p_76933_5_, true);
	}


	//TODO: make it allow more biomes later
	/**
	 * Return a list of biomes for the specified blocks. Args: listToReuse, x, y, width, length, cacheFlag (if false,
	 * don't check biomeCache to avoid infinite loop in BiomeCacheBlock)
	 */
	@Override
	public BiomeGenBase[] getBiomeGenAt(BiomeGenBase[] biomeGenBase, int x, int y, int width, int length, boolean p_76931_6_ )
	{

		GenLayerBiomePlanet.setupBiomesForUse(biomes);
		//return super.getBiomeGenAt(biomeGenBase, x, y, width, length, p_76931_6_);

		IntCache.resetIntCache();

		if (biomeGenBase == null || biomeGenBase.length < width * length)
		{
			biomeGenBase = new BiomeGenBase[width * length];
		}

		if (p_76931_6_ && width == 16 && length == 16 && (x & 15) == 0 && (y & 15) == 0)
		{
			BiomeGenBase[] abiomegenbase1 = this.biomeCache.getCachedBiomes(x, y);
			System.arraycopy(abiomegenbase1, 0, biomeGenBase, 0, width * length);
			return biomeGenBase;
		}
		else
		{


			int[] aint = this.biomeIndexLayer.getInts(x, y, width, length);

			for (int i1 = 0; i1 < width * length; ++i1)
			{

				//TODO DEBUG:
				if(aint[i1] > 255) {
					System.out.println("s");
				}

				biomeGenBase[i1] = AdvancedRocketryBiomes.instance.getBiomeById(aint[i1]);
			}

			return biomeGenBase;
		}
	}

	@Override
	public void cleanupCache() {
		super.cleanupCache();
		this.biomeCache.cleanupCache();
	}
}
