package zmaster587.advancedRocketry.world;

import java.util.function.LongFunction;

import it.unimi.dsi.fastutil.ints.Int2IntMap;
import it.unimi.dsi.fastutil.ints.Int2IntOpenHashMap;
import net.minecraft.util.Util;
import net.minecraft.world.gen.IExtendedNoiseRandom;
import net.minecraft.world.gen.LazyAreaLayerContext;
import net.minecraft.world.gen.area.IArea;
import net.minecraft.world.gen.area.IAreaFactory;
import net.minecraft.world.gen.area.LazyArea;
import net.minecraft.world.gen.layer.AddBambooForestLayer;
import net.minecraft.world.gen.layer.AddIslandLayer;
import net.minecraft.world.gen.layer.AddMushroomIslandLayer;
import net.minecraft.world.gen.layer.AddSnowLayer;
import net.minecraft.world.gen.layer.BiomeLayer;
import net.minecraft.world.gen.layer.DeepOceanLayer;
import net.minecraft.world.gen.layer.EdgeBiomeLayer;
import net.minecraft.world.gen.layer.EdgeLayer;
import net.minecraft.world.gen.layer.HillsLayer;
import net.minecraft.world.gen.layer.IslandLayer;
import net.minecraft.world.gen.layer.Layer;
import net.minecraft.world.gen.layer.MixOceansLayer;
import net.minecraft.world.gen.layer.MixRiverLayer;
import net.minecraft.world.gen.layer.OceanLayer;
import net.minecraft.world.gen.layer.RareBiomeLayer;
import net.minecraft.world.gen.layer.RemoveTooMuchOceanLayer;
import net.minecraft.world.gen.layer.RiverLayer;
import net.minecraft.world.gen.layer.ShoreLayer;
import net.minecraft.world.gen.layer.SmoothLayer;
import net.minecraft.world.gen.layer.StartRiverLayer;
import net.minecraft.world.gen.layer.ZoomLayer;
import net.minecraft.world.gen.layer.traits.IAreaTransformer1;
import zmaster587.advancedRocketry.dimension.DimensionProperties;

public class CustomLayerUtil {
	private static final Int2IntMap field_242937_a = Util.make(new Int2IntOpenHashMap(), (p_242938_0_) -> {
		func_242939_a(p_242938_0_, CustomLayerUtil.Type.BEACH, 16);
		func_242939_a(p_242938_0_, CustomLayerUtil.Type.BEACH, 26);
		func_242939_a(p_242938_0_, CustomLayerUtil.Type.DESERT, 2);
		func_242939_a(p_242938_0_, CustomLayerUtil.Type.DESERT, 17);
		func_242939_a(p_242938_0_, CustomLayerUtil.Type.DESERT, 130);
		func_242939_a(p_242938_0_, CustomLayerUtil.Type.EXTREME_HILLS, 131);
		func_242939_a(p_242938_0_, CustomLayerUtil.Type.EXTREME_HILLS, 162);
		func_242939_a(p_242938_0_, CustomLayerUtil.Type.EXTREME_HILLS, 20);
		func_242939_a(p_242938_0_, CustomLayerUtil.Type.EXTREME_HILLS, 3);
		func_242939_a(p_242938_0_, CustomLayerUtil.Type.EXTREME_HILLS, 34);
		func_242939_a(p_242938_0_, CustomLayerUtil.Type.FOREST, 27);
		func_242939_a(p_242938_0_, CustomLayerUtil.Type.FOREST, 28);
		func_242939_a(p_242938_0_, CustomLayerUtil.Type.FOREST, 29);
		func_242939_a(p_242938_0_, CustomLayerUtil.Type.FOREST, 157);
		func_242939_a(p_242938_0_, CustomLayerUtil.Type.FOREST, 132);
		func_242939_a(p_242938_0_, CustomLayerUtil.Type.FOREST, 4);
		func_242939_a(p_242938_0_, CustomLayerUtil.Type.FOREST, 155);
		func_242939_a(p_242938_0_, CustomLayerUtil.Type.FOREST, 156);
		func_242939_a(p_242938_0_, CustomLayerUtil.Type.FOREST, 18);
		func_242939_a(p_242938_0_, CustomLayerUtil.Type.ICY, 140);
		func_242939_a(p_242938_0_, CustomLayerUtil.Type.ICY, 13);
		func_242939_a(p_242938_0_, CustomLayerUtil.Type.ICY, 12);
		func_242939_a(p_242938_0_, CustomLayerUtil.Type.JUNGLE, 168);
		func_242939_a(p_242938_0_, CustomLayerUtil.Type.JUNGLE, 169);
		func_242939_a(p_242938_0_, CustomLayerUtil.Type.JUNGLE, 21);
		func_242939_a(p_242938_0_, CustomLayerUtil.Type.JUNGLE, 23);
		func_242939_a(p_242938_0_, CustomLayerUtil.Type.JUNGLE, 22);
		func_242939_a(p_242938_0_, CustomLayerUtil.Type.JUNGLE, 149);
		func_242939_a(p_242938_0_, CustomLayerUtil.Type.JUNGLE, 151);
		func_242939_a(p_242938_0_, CustomLayerUtil.Type.MESA, 37);
		func_242939_a(p_242938_0_, CustomLayerUtil.Type.MESA, 165);
		func_242939_a(p_242938_0_, CustomLayerUtil.Type.MESA, 167);
		func_242939_a(p_242938_0_, CustomLayerUtil.Type.MESA, 166);
		func_242939_a(p_242938_0_, CustomLayerUtil.Type.BADLANDS_PLATEAU, 39);
		func_242939_a(p_242938_0_, CustomLayerUtil.Type.BADLANDS_PLATEAU, 38);
		func_242939_a(p_242938_0_, CustomLayerUtil.Type.MUSHROOM, 14);
		func_242939_a(p_242938_0_, CustomLayerUtil.Type.MUSHROOM, 15);
		func_242939_a(p_242938_0_, CustomLayerUtil.Type.NONE, 25);
		func_242939_a(p_242938_0_, CustomLayerUtil.Type.OCEAN, 46);
		func_242939_a(p_242938_0_, CustomLayerUtil.Type.OCEAN, 49);
		func_242939_a(p_242938_0_, CustomLayerUtil.Type.OCEAN, 50);
		func_242939_a(p_242938_0_, CustomLayerUtil.Type.OCEAN, 48);
		func_242939_a(p_242938_0_, CustomLayerUtil.Type.OCEAN, 24);
		func_242939_a(p_242938_0_, CustomLayerUtil.Type.OCEAN, 47);
		func_242939_a(p_242938_0_, CustomLayerUtil.Type.OCEAN, 10);
		func_242939_a(p_242938_0_, CustomLayerUtil.Type.OCEAN, 45);
		func_242939_a(p_242938_0_, CustomLayerUtil.Type.OCEAN, 0);
		func_242939_a(p_242938_0_, CustomLayerUtil.Type.OCEAN, 44);
		func_242939_a(p_242938_0_, CustomLayerUtil.Type.PLAINS, 1);
		func_242939_a(p_242938_0_, CustomLayerUtil.Type.PLAINS, 129);
		func_242939_a(p_242938_0_, CustomLayerUtil.Type.RIVER, 11);
		func_242939_a(p_242938_0_, CustomLayerUtil.Type.RIVER, 7);
		func_242939_a(p_242938_0_, CustomLayerUtil.Type.SAVANNA, 35);
		func_242939_a(p_242938_0_, CustomLayerUtil.Type.SAVANNA, 36);
		func_242939_a(p_242938_0_, CustomLayerUtil.Type.SAVANNA, 163);
		func_242939_a(p_242938_0_, CustomLayerUtil.Type.SAVANNA, 164);
		func_242939_a(p_242938_0_, CustomLayerUtil.Type.SWAMP, 6);
		func_242939_a(p_242938_0_, CustomLayerUtil.Type.SWAMP, 134);
		func_242939_a(p_242938_0_, CustomLayerUtil.Type.TAIGA, 160);
		func_242939_a(p_242938_0_, CustomLayerUtil.Type.TAIGA, 161);
		func_242939_a(p_242938_0_, CustomLayerUtil.Type.TAIGA, 32);
		func_242939_a(p_242938_0_, CustomLayerUtil.Type.TAIGA, 33);
		func_242939_a(p_242938_0_, CustomLayerUtil.Type.TAIGA, 30);
		func_242939_a(p_242938_0_, CustomLayerUtil.Type.TAIGA, 31);
		func_242939_a(p_242938_0_, CustomLayerUtil.Type.TAIGA, 158);
		func_242939_a(p_242938_0_, CustomLayerUtil.Type.TAIGA, 5);
		func_242939_a(p_242938_0_, CustomLayerUtil.Type.TAIGA, 19);
		func_242939_a(p_242938_0_, CustomLayerUtil.Type.TAIGA, 133);
	});

	public static <T extends IArea, C extends IExtendedNoiseRandom<T>> IAreaFactory<T> repeat(long seed, IAreaTransformer1 parent, IAreaFactory<T> p_202829_3_, int count, LongFunction<C> contextFactory) {
		IAreaFactory<T> iareafactory = p_202829_3_;

		for(int i = 0; i < count; ++i) {
			iareafactory = parent.apply(contextFactory.apply(seed + (long)i), iareafactory);
		}

		return iareafactory;
	}

	private static <T extends IArea, C extends IExtendedNoiseRandom<T>> IAreaFactory<T> func_237216_a_(boolean p_237216_0_, int p_237216_1_, int p_237216_2_, LongFunction<C> p_237216_3_, DimensionProperties properties) {
		boolean hasRivers = properties.hasRivers();
		IAreaFactory<T> iareafactory = IslandLayer.INSTANCE.apply(p_237216_3_.apply(1L));
		iareafactory = ZoomLayer.FUZZY.apply(p_237216_3_.apply(2000L), iareafactory);
		iareafactory = AddIslandLayer.INSTANCE.apply(p_237216_3_.apply(1L), iareafactory);
		iareafactory = ZoomLayer.NORMAL.apply(p_237216_3_.apply(2001L), iareafactory);
		iareafactory = AddIslandLayer.INSTANCE.apply(p_237216_3_.apply(2L), iareafactory);
		iareafactory = AddIslandLayer.INSTANCE.apply(p_237216_3_.apply(50L), iareafactory);
		iareafactory = AddIslandLayer.INSTANCE.apply(p_237216_3_.apply(70L), iareafactory);
		iareafactory = RemoveTooMuchOceanLayer.INSTANCE.apply(p_237216_3_.apply(2L), iareafactory);
		IAreaFactory<T> iareafactory1 = OceanLayer.INSTANCE.apply(p_237216_3_.apply(2L));
		iareafactory1 = repeat(2001L, ZoomLayer.NORMAL, iareafactory1, 6, p_237216_3_);
		iareafactory = AddSnowLayer.INSTANCE.apply(p_237216_3_.apply(2L), iareafactory);
		iareafactory = AddIslandLayer.INSTANCE.apply(p_237216_3_.apply(3L), iareafactory);
		iareafactory = EdgeLayer.CoolWarm.INSTANCE.apply(p_237216_3_.apply(2L), iareafactory);
		iareafactory = EdgeLayer.HeatIce.INSTANCE.apply(p_237216_3_.apply(2L), iareafactory);
		iareafactory = EdgeLayer.Special.INSTANCE.apply(p_237216_3_.apply(3L), iareafactory);
		iareafactory = ZoomLayer.NORMAL.apply(p_237216_3_.apply(2002L), iareafactory);
		iareafactory = ZoomLayer.NORMAL.apply(p_237216_3_.apply(2003L), iareafactory);
		iareafactory = AddIslandLayer.INSTANCE.apply(p_237216_3_.apply(4L), iareafactory);
		iareafactory = AddMushroomIslandLayer.INSTANCE.apply(p_237216_3_.apply(5L), iareafactory);
		iareafactory = DeepOceanLayer.INSTANCE.apply(p_237216_3_.apply(4L), iareafactory);
		iareafactory = repeat(1000L, ZoomLayer.NORMAL, iareafactory, 0, p_237216_3_);
		IAreaFactory<T> lvt_6_1_ = repeat(1000L, ZoomLayer.NORMAL, iareafactory, 0, p_237216_3_);
		lvt_6_1_ = StartRiverLayer.INSTANCE.apply(p_237216_3_.apply(100L), lvt_6_1_);
		IAreaFactory<T> lvt_7_1_ = (new GenLayerBiomePlanet(properties.getBiomes())).apply(p_237216_3_.apply(200L), iareafactory);
		lvt_7_1_ = AddBambooForestLayer.INSTANCE.apply(p_237216_3_.apply(1001L), lvt_7_1_);
		lvt_7_1_ = repeat(1000L, ZoomLayer.NORMAL, lvt_7_1_, 2, p_237216_3_);
		lvt_7_1_ = EdgeBiomeLayer.INSTANCE.apply(p_237216_3_.apply(1000L), lvt_7_1_);
		IAreaFactory<T> lvt_8_1_ = repeat(1000L, ZoomLayer.NORMAL, lvt_6_1_, 2, p_237216_3_);
		lvt_7_1_ = HillsLayer.INSTANCE.apply(p_237216_3_.apply(1000L), lvt_7_1_, lvt_8_1_);
		lvt_6_1_ = repeat(1000L, ZoomLayer.NORMAL, lvt_6_1_, 2, p_237216_3_);
		lvt_6_1_ = repeat(1000L, ZoomLayer.NORMAL, lvt_6_1_, p_237216_2_, p_237216_3_);
		lvt_6_1_ = RiverLayer.INSTANCE.apply(p_237216_3_.apply(1L), lvt_6_1_);
		lvt_6_1_ = SmoothLayer.INSTANCE.apply(p_237216_3_.apply(1000L), lvt_6_1_);
		lvt_7_1_ = RareBiomeLayer.INSTANCE.apply(p_237216_3_.apply(1001L), lvt_7_1_);

		for(int i = 0; i < p_237216_1_; ++i) {
			lvt_7_1_ = ZoomLayer.NORMAL.apply(p_237216_3_.apply((long)(1000 + i)), lvt_7_1_);
			if (i == 0) {
				lvt_7_1_ = AddIslandLayer.INSTANCE.apply(p_237216_3_.apply(3L), lvt_7_1_);
			}

			if (i == 1 || p_237216_1_ == 1) {
				lvt_7_1_ = ShoreLayer.INSTANCE.apply(p_237216_3_.apply(1000L), lvt_7_1_);
			}
		}

		lvt_7_1_ = SmoothLayer.INSTANCE.apply(p_237216_3_.apply(1000L), lvt_7_1_);
		if(hasRivers)
			lvt_7_1_ = MixRiverLayer.INSTANCE.apply(p_237216_3_.apply(100L), lvt_7_1_, lvt_6_1_);
		
		return MixOceansLayer.INSTANCE.apply(p_237216_3_.apply(100L), lvt_7_1_, iareafactory1);
	}

	public static Layer func_237215_a_(long p_237215_0_, boolean p_237215_2_, int p_237215_3_, int p_237215_4_, DimensionProperties dimensionProperties) {
		int i = 25;
		IAreaFactory<LazyArea> iareafactory = func_237216_a_(p_237215_2_, p_237215_3_, p_237215_4_, (p_227473_2_) -> {
			return new LazyAreaLayerContext(25, p_237215_0_, p_227473_2_);
		}, dimensionProperties);
		return new Layer(iareafactory);
	}

	public static boolean areBiomesSimilar(int p_202826_0_, int p_202826_1_) {
		if (p_202826_0_ == p_202826_1_) {
			return true;
		} else {
			return field_242937_a.get(p_202826_0_) == field_242937_a.get(p_202826_1_);
		}
	}

	private static void func_242939_a(Int2IntOpenHashMap p_242939_0_, CustomLayerUtil.Type p_242939_1_, int p_242939_2_) {
		p_242939_0_.put(p_242939_2_, p_242939_1_.ordinal());
	}

	protected static boolean isOcean(int biomeIn) {
		return biomeIn == 44 || biomeIn == 45 || biomeIn == 0 || biomeIn == 46 || biomeIn == 10 || biomeIn == 47 || biomeIn == 48 || biomeIn == 24 || biomeIn == 49 || biomeIn == 50;
	}

	protected static boolean isShallowOcean(int biomeIn) {
		return biomeIn == 44 || biomeIn == 45 || biomeIn == 0 || biomeIn == 46 || biomeIn == 10;
	}

	static enum Type {
		NONE,
		TAIGA,
		EXTREME_HILLS,
		JUNGLE,
		MESA,
		BADLANDS_PLATEAU,
		PLAINS,
		SAVANNA,
		ICY,
		BEACH,
		FOREST,
		OCEAN,
		DESERT,
		RIVER,
		SWAMP,
		MUSHROOM;
	}
}
