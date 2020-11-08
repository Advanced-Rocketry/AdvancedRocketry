package zmaster587.advancedRocketry.world;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Maps;
import com.mojang.datafixers.util.Either;
import com.mojang.datafixers.util.Function3;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import it.unimi.dsi.fastutil.doubles.DoubleArrayList;
import it.unimi.dsi.fastutil.doubles.DoubleList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SharedSeedRandom;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryLookupCodec;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeRegistry;
import net.minecraft.world.biome.Biomes;
import net.minecraft.world.biome.provider.BiomeProvider;
import net.minecraft.world.gen.MaxMinNoiseMixer;
import net.minecraft.world.gen.feature.structure.Structure;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class CustomPlanetBiomeProvider extends BiomeProvider {

	private static final CustomPlanetBiomeProvider.Noise field_242596_g = new CustomPlanetBiomeProvider.Noise(-7, ImmutableList.of(1.0D, 1.0D));
	public static final MapCodec<CustomPlanetBiomeProvider> field_235262_e_ = RecordCodecBuilder.mapCodec((p_242602_0_) -> {
		return p_242602_0_.group(Codec.LONG.fieldOf("seed").forGetter((p_235286_0_) -> {
			return p_235286_0_.field_235270_m_;
		}), RecordCodecBuilder.<Pair<Biome.Attributes, Supplier<Biome>>>create((p_235282_0_) -> {
			return p_235282_0_.group(Biome.Attributes.CODEC.fieldOf("parameters").forGetter(Pair::getFirst), Biome.BIOME_CODEC.fieldOf("biome").forGetter(Pair::getSecond)).apply(p_235282_0_, Pair::of);
		}).listOf().fieldOf("biomes").forGetter((p_235284_0_) -> {
			return p_235284_0_.field_235268_k_;
		}), CustomPlanetBiomeProvider.Noise.field_242609_a.fieldOf("temperature_noise").forGetter((p_242608_0_) -> {
			return p_242608_0_.field_242597_h;
		}), CustomPlanetBiomeProvider.Noise.field_242609_a.fieldOf("humidity_noise").forGetter((p_242607_0_) -> {
			return p_242607_0_.field_242598_i;
		}), CustomPlanetBiomeProvider.Noise.field_242609_a.fieldOf("altitude_noise").forGetter((p_242606_0_) -> {
			return p_242606_0_.field_242599_j;
		}), CustomPlanetBiomeProvider.Noise.field_242609_a.fieldOf("weirdness_noise").forGetter((p_242604_0_) -> {
			return p_242604_0_.field_242600_k;
		})).apply(p_242602_0_, CustomPlanetBiomeProvider::new);
	});

	public static final Codec<CustomPlanetBiomeProvider> customPlanetCodec = Codec.mapEither(CustomPlanetBiomeProvider.DefaultBuilder.field_242624_a, field_235262_e_).xmap((p_235277_0_) -> {
		return p_235277_0_.map(CustomPlanetBiomeProvider.DefaultBuilder::func_242635_d, Function.identity());
	}, (p_235275_0_) -> {
		return p_235275_0_.func_242605_d().map(Either::<CustomPlanetBiomeProvider.DefaultBuilder, CustomPlanetBiomeProvider>left).orElseGet(() -> {
			return Either.right(p_235275_0_);
		});
	}).codec();
	private final CustomPlanetBiomeProvider.Noise field_242597_h;
	private final CustomPlanetBiomeProvider.Noise field_242598_i;
	private final CustomPlanetBiomeProvider.Noise field_242599_j;
	private final CustomPlanetBiomeProvider.Noise field_242600_k;
	private final MaxMinNoiseMixer field_235264_g_;
	private final MaxMinNoiseMixer field_235265_h_;
	private final MaxMinNoiseMixer field_235266_i_;
	private final MaxMinNoiseMixer field_235267_j_;
	private final List<Pair<Biome.Attributes, Supplier<Biome>>> field_235268_k_;
	private final boolean field_235269_l_;
	private final long field_235270_m_;
	private final Optional<Pair<Registry<Biome>, CustomPlanetBiomeProvider.Preset>> field_235271_n_;

	private CustomPlanetBiomeProvider(long p_i231640_1_, List<Pair<Biome.Attributes, Supplier<Biome>>> p_i231640_3_, Optional<Pair<Registry<Biome>, CustomPlanetBiomeProvider.Preset>> p_i231640_4_) {
		this(p_i231640_1_, p_i231640_3_, field_242596_g, field_242596_g, field_242596_g, field_242596_g, p_i231640_4_);
	}

	private CustomPlanetBiomeProvider(long p_i241951_1_, List<Pair<Biome.Attributes, Supplier<Biome>>> p_i241951_3_, CustomPlanetBiomeProvider.Noise p_i241951_4_, CustomPlanetBiomeProvider.Noise p_i241951_5_, CustomPlanetBiomeProvider.Noise p_i241951_6_, CustomPlanetBiomeProvider.Noise p_i241951_7_) {
		this(p_i241951_1_, p_i241951_3_, p_i241951_4_, p_i241951_5_, p_i241951_6_, p_i241951_7_, Optional.empty());
	}

	private CustomPlanetBiomeProvider(long p_i241952_1_, List<Pair<Biome.Attributes, Supplier<Biome>>> p_i241952_3_, CustomPlanetBiomeProvider.Noise p_i241952_4_, CustomPlanetBiomeProvider.Noise p_i241952_5_, CustomPlanetBiomeProvider.Noise p_i241952_6_, CustomPlanetBiomeProvider.Noise p_i241952_7_, Optional<Pair<Registry<Biome>, CustomPlanetBiomeProvider.Preset>> p_i241952_8_) {
		super(p_i241952_3_.stream().map(Pair::getSecond));
		this.field_235270_m_ = p_i241952_1_;
		this.field_235271_n_ = p_i241952_8_;
		this.field_242597_h = p_i241952_4_;
		this.field_242598_i = p_i241952_5_;
		this.field_242599_j = p_i241952_6_;
		this.field_242600_k = p_i241952_7_;
		this.field_235264_g_ = MaxMinNoiseMixer.func_242930_a(new SharedSeedRandom(p_i241952_1_), p_i241952_4_.func_242612_a(), p_i241952_4_.func_242614_b());
		this.field_235265_h_ = MaxMinNoiseMixer.func_242930_a(new SharedSeedRandom(p_i241952_1_ + 1L), p_i241952_5_.func_242612_a(), p_i241952_5_.func_242614_b());
		this.field_235266_i_ = MaxMinNoiseMixer.func_242930_a(new SharedSeedRandom(p_i241952_1_ + 2L), p_i241952_6_.func_242612_a(), p_i241952_6_.func_242614_b());
		this.field_235267_j_ = MaxMinNoiseMixer.func_242930_a(new SharedSeedRandom(p_i241952_1_ + 3L), p_i241952_7_.func_242612_a(), p_i241952_7_.func_242614_b());
		this.field_235268_k_ = p_i241952_3_;
		this.field_235269_l_ = false;
	}

	/*protected Codec<? extends BiomeProvider> func_230319_a_() {
		return customPlanetCodec;
	}*/

	@Override
	protected Codec<? extends BiomeProvider> getBiomeProviderCodec() {
		return customPlanetCodec;
	}

	@OnlyIn(Dist.CLIENT)
	public BiomeProvider getBiomeProvider(long p_230320_1_) {
		return new CustomPlanetBiomeProvider(p_230320_1_, this.field_235268_k_, this.field_242597_h, this.field_242598_i, this.field_242599_j, this.field_242600_k, this.field_235271_n_);
	}

	private Optional<CustomPlanetBiomeProvider.DefaultBuilder> func_242605_d() {
		return this.field_235271_n_.map((p_242601_1_) -> {
			return new CustomPlanetBiomeProvider.DefaultBuilder(p_242601_1_.getSecond(), p_242601_1_.getFirst(), this.field_235270_m_);
		});
	}

	public Biome getNoiseBiome(int x, int y, int z) {
		int i = this.field_235269_l_ ? y : 0;
		Biome.Attributes biome$attributes = new Biome.Attributes((float)this.field_235264_g_.func_237211_a_((double)x, (double)i, (double)z), (float)this.field_235265_h_.func_237211_a_((double)x, (double)i, (double)z), (float)this.field_235266_i_.func_237211_a_((double)x, (double)i, (double)z), (float)this.field_235267_j_.func_237211_a_((double)x, (double)i, (double)z), 0.0F);
		return this.field_235268_k_.stream().min(Comparator.comparing((p_235272_1_) -> {
			return p_235272_1_.getFirst().getAttributeDifference(biome$attributes);
		})).map(Pair::getSecond).map(Supplier::get).orElse(BiomeRegistry.THE_VOID);
	}

	public boolean func_235280_b_(long p_235280_1_) {
		return this.field_235270_m_ == p_235280_1_ && this.field_235271_n_.isPresent() && Objects.equals(this.field_235271_n_.get().getSecond(), CustomPlanetBiomeProvider.Preset.field_235288_b_);
	}

	static final class DefaultBuilder {
		public static final MapCodec<CustomPlanetBiomeProvider.DefaultBuilder> field_242624_a = RecordCodecBuilder.mapCodec((p_242630_0_) -> {
			return p_242630_0_.group(ResourceLocation.CODEC.flatXmap((p_242631_0_) -> {
				return Optional.ofNullable(CustomPlanetBiomeProvider.Preset.field_235289_c_.get(p_242631_0_)).map(DataResult::success).orElseGet(() -> {
					return DataResult.error("Unknown preset: " + p_242631_0_);
				});
			}, (p_242629_0_) -> {
				return DataResult.success(p_242629_0_.field_235290_d_);
			}).fieldOf("preset").stable().forGetter(CustomPlanetBiomeProvider.DefaultBuilder::func_242628_a), RegistryLookupCodec.getLookUpCodec(Registry.BIOME_KEY).forGetter(CustomPlanetBiomeProvider.DefaultBuilder::func_242632_b), Codec.LONG.fieldOf("seed").stable().forGetter(CustomPlanetBiomeProvider.DefaultBuilder::func_242634_c)).apply(p_242630_0_, p_242630_0_.stable(CustomPlanetBiomeProvider.DefaultBuilder::new));
		});
		private final CustomPlanetBiomeProvider.Preset field_242625_b;
		private final Registry<Biome> field_242626_c;
		private final long field_242627_d;

		private DefaultBuilder(CustomPlanetBiomeProvider.Preset p_i241956_1_, Registry<Biome> p_i241956_2_, long p_i241956_3_) {
			this.field_242625_b = p_i241956_1_;
			this.field_242626_c = p_i241956_2_;
			this.field_242627_d = p_i241956_3_;
		}

		public CustomPlanetBiomeProvider.Preset func_242628_a() {
			return this.field_242625_b;
		}

		public Registry<Biome> func_242632_b() {
			return this.field_242626_c;
		}

		public long func_242634_c() {
			return this.field_242627_d;
		}

		public CustomPlanetBiomeProvider func_242635_d() {
			return this.field_242625_b.func_242619_a(this.field_242626_c, this.field_242627_d);
		}
	}

	static class Noise {
		private final int field_242610_b;
		private final DoubleList field_242611_c;
		public static final Codec<CustomPlanetBiomeProvider.Noise> field_242609_a = RecordCodecBuilder.create((p_242613_0_) -> {
			return p_242613_0_.group(Codec.INT.fieldOf("firstOctave").forGetter(CustomPlanetBiomeProvider.Noise::func_242612_a), Codec.DOUBLE.listOf().fieldOf("amplitudes").forGetter(CustomPlanetBiomeProvider.Noise::func_242614_b)).apply(p_242613_0_, CustomPlanetBiomeProvider.Noise::new);
		});

		public Noise(int p_i241954_1_, List<Double> p_i241954_2_) {
			this.field_242610_b = p_i241954_1_;
			this.field_242611_c = new DoubleArrayList(p_i241954_2_);
		}

		public int func_242612_a() {
			return this.field_242610_b;
		}

		public DoubleList func_242614_b() {
			return this.field_242611_c;
		}
	}

	public boolean hasStructure(Structure<?> structureIn) {
		return true;
	}

	public static class Preset {
		private static final Map<ResourceLocation, CustomPlanetBiomeProvider.Preset> field_235289_c_ = Maps.newHashMap();
		public static final CustomPlanetBiomeProvider.Preset field_235288_b_ = new CustomPlanetBiomeProvider.Preset(new ResourceLocation("nether"), (p_242617_0_, p_242617_1_, p_242617_2_) -> {
			return new CustomPlanetBiomeProvider(p_242617_2_, ImmutableList.of(Pair.of(new Biome.Attributes(0.0F, 0.0F, 0.0F, 0.0F, 0.0F), () -> {
				return p_242617_1_.getOrThrow(Biomes.NETHER_WASTES);
			}), Pair.of(new Biome.Attributes(0.0F, -0.5F, 0.0F, 0.0F, 0.0F), () -> {
				return p_242617_1_.getOrThrow(Biomes.SOUL_SAND_VALLEY);
			}), Pair.of(new Biome.Attributes(0.4F, 0.0F, 0.0F, 0.0F, 0.0F), () -> {
				return p_242617_1_.getOrThrow(Biomes.CRIMSON_FOREST);
			}), Pair.of(new Biome.Attributes(0.0F, 0.5F, 0.0F, 0.0F, 0.375F), () -> {
				return p_242617_1_.getOrThrow(Biomes.WARPED_FOREST);
			}), Pair.of(new Biome.Attributes(-0.5F, 0.0F, 0.0F, 0.0F, 0.175F), () -> {
				return p_242617_1_.getOrThrow(Biomes.BASALT_DELTAS);
			})), Optional.of(Pair.of(p_242617_1_, p_242617_0_)));
		});
		private final ResourceLocation field_235290_d_;
		private final Function3<CustomPlanetBiomeProvider.Preset, Registry<Biome>, Long, CustomPlanetBiomeProvider> field_235291_e_;

		public Preset(ResourceLocation p_i241955_1_, Function3<CustomPlanetBiomeProvider.Preset, Registry<Biome>, Long, CustomPlanetBiomeProvider> p_i241955_2_) {
			this.field_235290_d_ = p_i241955_1_;
			this.field_235291_e_ = p_i241955_2_;
			field_235289_c_.put(p_i241955_1_, this);
		}

		public CustomPlanetBiomeProvider func_242619_a(Registry<Biome> p_242619_1_, long p_242619_2_) {
			return this.field_235291_e_.apply(this, p_242619_1_, p_242619_2_);
		}
	}
}
