package zmaster587.advancedRocketry.world;

import java.lang.reflect.Field;
import java.util.List;

import javax.annotation.Nullable;

import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.init.Biomes;
import net.minecraft.util.ReportedException;
import net.minecraft.world.World;
import net.minecraft.world.WorldType;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeCache;
import net.minecraft.world.biome.BiomeProvider;
import net.minecraft.world.gen.ChunkGeneratorSettings;
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
import net.minecraft.world.gen.layer.GenLayerShore;
import net.minecraft.world.gen.layer.GenLayerSmooth;
import net.minecraft.world.gen.layer.GenLayerVoronoiZoom;
import net.minecraft.world.gen.layer.GenLayerZoom;
import net.minecraft.world.gen.layer.IntCache;
import net.minecraftforge.common.BiomeManager.BiomeEntry;
import net.minecraftforge.fml.relauncher.ReflectionHelper;
import zmaster587.advancedRocketry.AdvancedRocketry;
import zmaster587.advancedRocketry.dimension.DimensionManager;
import zmaster587.advancedRocketry.dimension.DimensionProperties;
import zmaster587.advancedRocketry.world.type.WorldTypePlanetGen;

public class ChunkManagerPlanet extends BiomeProvider {
	//TODO: make higher biome ids work
	/** A GenLayer containing the indices into BiomeGenBase.biomeList[] */
	private GenLayer biomeIndexLayer;

	private BiomeCache biomeCache;

	private GenLayer genBiomes;

	private List<BiomeEntry> biomes;

	private static Field fBiomeCacheMap;
	private static Field fBiomeCache;

	public ChunkManagerPlanet(long seed, WorldType default1, String str, DimensionProperties properties) {

		this.biomeCache = new BiomeCache(this);//new BiomeCacheExtended(this);
		//TODO: more biomes
		//TODO: remove rivers
		GenLayer[] agenlayer = initializeAllBiomeGenerators(seed, default1, str, properties);//GenLayer.initializeAllBiomeGenerators(seed, default1); //;
		agenlayer = getModdedBiomeGenerators(default1, seed, agenlayer);
		this.genBiomes = agenlayer[0];
		this.biomeIndexLayer = agenlayer[1];

		ReflectionHelper.setPrivateValue(BiomeProvider.class, this, this.genBiomes, "genBiomes", "field_76944_d");
		ReflectionHelper.setPrivateValue(BiomeProvider.class, this, this.biomeIndexLayer, "biomeIndexLayer", "field_76945_e");
		
		fBiomeCache = ReflectionHelper.findField(BiomeCache.class, "cache", "field_76841_d");
		fBiomeCache.setAccessible(true);

		fBiomeCacheMap = ReflectionHelper.findField(BiomeCache.class, "cacheMap", "field_76843_c");
		fBiomeCacheMap.setAccessible(true);
	}


	public ChunkManagerPlanet(World world, String str, List biomes)
	{
		this(world.getSeed(), (WorldTypePlanetGen)AdvancedRocketry.planetWorldType, str, DimensionManager.getInstance().getDimensionProperties(world.provider.getDimension()));
		//Note: world MUST BE REGISTERED WITH THE DIMENSION MANAGER
		//This is a mess!
		this.biomes = biomes;
	}

	/**
	 * the first array item is a linked list of the bioms, the second is the zoom function, the third is the same as the
	 * first.
	 */
	public static GenLayer[] initializeAllBiomeGenerators(long seed, WorldType p_180781_2_, String p_180781_3_ ,  DimensionProperties properties)
	{
		boolean hasRivers = properties.hasRivers();

		GenLayer genlayer = new GenLayerIsland(1L);
		genlayer = new GenLayerFuzzyZoom(2000L, genlayer);
		GenLayerAddIsland genlayeraddisland = new GenLayerAddIsland(1L, genlayer);
		GenLayerZoom genlayerzoom = new GenLayerZoom(2001L, genlayeraddisland);
		GenLayerAddIsland genlayeraddisland1 = new GenLayerAddIsland(2L, genlayerzoom);
		genlayeraddisland1 = new GenLayerAddIsland(50L, genlayeraddisland1);
		genlayeraddisland1 = new GenLayerAddIsland(70L, genlayeraddisland1);
		GenLayerRemoveTooMuchOcean genlayerremovetoomuchocean = new GenLayerRemoveTooMuchOcean(2L, genlayeraddisland1);
		GenLayerAddSnow genlayeraddsnow = new GenLayerAddSnow(2L, genlayerremovetoomuchocean);
		GenLayerAddIsland genlayeraddisland2 = new GenLayerAddIsland(3L, genlayeraddsnow);
		GenLayerEdge genlayeredge = new GenLayerEdge(2L, genlayeraddisland2, GenLayerEdge.Mode.COOL_WARM);
		genlayeredge = new GenLayerEdge(2L, genlayeredge, GenLayerEdge.Mode.HEAT_ICE);
		genlayeredge = new GenLayerEdge(3L, genlayeredge, GenLayerEdge.Mode.SPECIAL);
		GenLayerZoom genlayerzoom1 = new GenLayerZoom(2002L, genlayeredge);
		genlayerzoom1 = new GenLayerZoom(2003L, genlayerzoom1);
		GenLayerAddIsland genlayeraddisland3 = new GenLayerAddIsland(4L, genlayerzoom1);
		GenLayerAddMushroomIsland genlayeraddmushroomisland = new GenLayerAddMushroomIsland(5L, genlayeraddisland3);
		GenLayerDeepOcean genlayerdeepocean = new GenLayerDeepOcean(4L, genlayeraddmushroomisland);
		GenLayer genlayer4 = GenLayerZoom.magnify(1000L, genlayerdeepocean, 0);
		int i = 4;
		int j = i;
		ChunkGeneratorSettings chunkprovidersettings = null;
		
		
		if(!p_180781_3_.isEmpty()) {
			chunkprovidersettings = ChunkGeneratorSettings.Factory.jsonToFactory(p_180781_3_).build();
		}

		if (p_180781_2_ == WorldType.CUSTOMIZED && !p_180781_3_.isEmpty())
		{
			i = chunkprovidersettings.biomeSize;
			j = chunkprovidersettings.riverSize;
		}

		if (p_180781_2_ == WorldType.LARGE_BIOMES)
		{
			i = 6;
		}

		i = getModdedBiomeSize(p_180781_2_, i);

		GenLayer lvt_8_1_ = GenLayerZoom.magnify(1000L, genlayer4, 0);
		GenLayer genlayer5;
		GenLayer genlayerhills;

		//if(hasRivers) {
		GenLayerRiverInit genlayerriverinit = new GenLayerRiverInit(100L, lvt_8_1_);
		GenLayer lvt_10_1_ = GenLayerZoom.magnify(1000L, genlayerriverinit, 2);
		GenLayer genlayerbiomeedge = p_180781_2_.getBiomeLayer(seed, genlayer4, chunkprovidersettings);
		genlayerhills = new GenLayerHills(1000L, genlayerbiomeedge, lvt_10_1_);
		genlayer5 = GenLayerZoom.magnify(1000L, genlayerriverinit, 2);
		//}
		//else {
		//GenLayer genlayerbiomeedge = p_180781_2_.getBiomeLayer(seed, genlayer4, p_180781_3_);
		//genlayerhills = new GenLayerHills(1000L, genlayerbiomeedge, lvt_8_1_);
		//genlayer5 = GenLayerZoom.magnify(1000L, lvt_8_1_, 2);
		//}

		genlayer5 = GenLayerZoom.magnify(1000L, genlayer5, j);
		GenLayerSmooth genlayersmooth;

		//if(hasRivers) {
		GenLayerRiver genlayerriver = new GenLayerRiver(1L, genlayer5);
		genlayersmooth = new GenLayerSmooth(1000L, genlayerriver);
		//}
		//else
		genlayersmooth = new GenLayerSmooth(1000L, genlayer);

		genlayerhills = new GenLayerRareBiome(1001L, genlayerhills);

		for (int k = 0; k < i; ++k)
		{
			genlayerhills = new GenLayerZoom((long)(1000 + k), genlayerhills);

			if (k == 0)
			{
				genlayerhills = new GenLayerAddIsland(3L, genlayerhills);
			}

			if (k == 1 || i == 1)
			{
				genlayerhills = new GenLayerShore(1000L, genlayerhills);
			}
		}

		GenLayerSmooth genlayersmooth1 = new GenLayerSmooth(1000L, genlayerhills);
		GenLayer genlayer3;
		GenLayer finalOut;
		GenLayer genlayerrivermix;
		if(hasRivers) 
			genlayerrivermix = new GenLayerRiverMix(100L, genlayersmooth1, genlayersmooth);
		else {
			genlayerrivermix = new GenLayerRiverMix(100L, genlayersmooth1, genlayersmooth1);
		}
		
		genlayer3 = new GenLayerVoronoiZoom(10L, genlayerrivermix);
		genlayerrivermix.initWorldGenSeed(seed);
		genlayer3.initWorldGenSeed(seed);
		finalOut = genlayerrivermix;

		return new GenLayer[] {finalOut, genlayer3, finalOut};
	}


	public static int getModdedBiomeSize(WorldType worldType, int original)
	{
		net.minecraftforge.event.terraingen.WorldTypeEvent.BiomeSize event = new net.minecraftforge.event.terraingen.WorldTypeEvent.BiomeSize(worldType, original);
		net.minecraftforge.common.MinecraftForge.TERRAIN_GEN_BUS.post(event);
		return event.getNewSize();
	}


	public void resetCache() {

		try {
			fBiomeCacheMap.set(this.biomeCache, new Long2ObjectOpenHashMap(4096));
			((List)fBiomeCache.get(this.biomeCache)).clear();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public Biome[] getBiomesForGeneration(Biome[] biomes, int x, int z, int width, int height)
	{
		GenLayerBiomePlanet.setupBiomesForUse(this.biomes);
		//return super.getBiomesForGeneration(p_76937_1_, p_76937_2_, p_76937_3_, p_76937_4_, p_76937_5_);

		IntCache.resetIntCache();

		if (biomes == null || biomes.length < width * height)
		{
			biomes = new Biome[width * height];
		}

		int[] aint = this.genBiomes.getInts(x, z, width, height);

		try
		{
			for (int i1 = 0; i1 < width * height; ++i1)
			{
				biomes[i1] = Biome.getBiome(aint[i1], Biomes.OCEAN);//AdvancedRocketryBiomes.instance.getBiomeById(aint[i1]);
			}

			return biomes;
		}
		catch (Throwable throwable)
		{
			CrashReport crashreport = CrashReport.makeCrashReport(throwable, "Invalid Biome id");
			CrashReportCategory crashreportcategory = crashreport.makeCategory("RawBiomeBlock");
			crashreportcategory.addCrashSection("biomes[] size", Integer.valueOf(biomes.length));
			crashreportcategory.addCrashSection("x", Integer.valueOf(x));
			crashreportcategory.addCrashSection("z", Integer.valueOf(z));
			crashreportcategory.addCrashSection("w", Integer.valueOf(width));
			crashreportcategory.addCrashSection("h", Integer.valueOf(height));
			throw new ReportedException(crashreport);
		}
	}

	/**
	 * Gets biomes to use for the blocks and loads the other data like temperature and humidity onto the
	 * WorldChunkManager.
	 */
	public Biome[] loadBlockGeneratorData(@Nullable Biome[] oldBiomeList, int x, int z, int width, int depth)
	{
		return this.getBiomes(oldBiomeList, x, z, width, depth, true);
	}


	public Biome getBiomeGenAt(int x, int z)
	{
		return this.biomeCache.getBiome(x, z, Biomes.OCEAN);
	}
	//TODO: make it allow more biomes later
	/**
	 * Return a list of biomes for the specified blocks. Args: listToReuse, x, y, width, length, cacheFlag (if false,
	 * don't check biomeCache to avoid infinite loop in BiomeCacheBlock)
	 */
	@Override
	public Biome[] getBiomes(@Nullable Biome[] listToReuse, int x, int z, int width, int length, boolean cacheFlag) {

		GenLayerBiomePlanet.setupBiomesForUse(biomes);
		//return super.getBiomeGenAt(biomeGenBase, x, y, width, length, p_76931_6_);

		IntCache.resetIntCache();

		if (listToReuse == null || listToReuse.length < width * length)
		{
			listToReuse = new Biome[width * length];
		}

		if (cacheFlag && width == 16 && length == 16 && (x & 15) == 0 && (z & 15) == 0)
		{
			Biome[] abiome = this.biomeCache.getCachedBiomes(x, z);
			System.arraycopy(abiome, 0, listToReuse, 0, width * length);
			return listToReuse;
		}
		else
		{
			int[] aint = this.biomeIndexLayer.getInts(x, z, width, length);

			for (int i = 0; i < width * length; ++i)
			{
				listToReuse[i] = Biome.getBiome(aint[i], Biomes.DEFAULT);
			}

			return listToReuse;
		}
	}

	@Override
	public void cleanupCache() {
		super.cleanupCache();
		this.biomeCache.cleanupCache();
	}
}
