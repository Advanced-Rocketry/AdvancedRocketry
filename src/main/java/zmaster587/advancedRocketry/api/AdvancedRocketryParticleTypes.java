package zmaster587.advancedRocketry.api;

import net.minecraft.client.Minecraft;
import net.minecraft.particles.BasicParticleType;
import net.minecraft.particles.ParticleType;
import net.minecraftforge.client.event.ParticleFactoryRegisterEvent;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import zmaster587.advancedRocketry.entity.fx.FxElectricArc;
import zmaster587.advancedRocketry.entity.fx.FxGravityEffect;
import zmaster587.advancedRocketry.entity.fx.InverseTrailFluid;
import zmaster587.advancedRocketry.entity.fx.InverseTrailFx;
import zmaster587.advancedRocketry.entity.fx.OxygenCloudFX;
import zmaster587.advancedRocketry.entity.fx.OxygenTraceFX;
import zmaster587.advancedRocketry.entity.fx.RocketFx;
import zmaster587.advancedRocketry.entity.fx.TrailFx;

public class AdvancedRocketryParticleTypes {

	
	
	private static final DeferredRegister<ParticleType<?>> PARTICLE_TYPES = DeferredRegister.create(ForgeRegistries.PARTICLE_TYPES, Constants.modId);
	
	
	public static void init()
	{
		PARTICLE_TYPES.register(FMLJavaModLoadingContext.get().getModEventBus());
	}
	
	public static RegistryObject<BasicParticleType> fxElectricArc = PARTICLE_TYPES.register("electricarc", () -> new BasicParticleType(true));
	public static RegistryObject<BasicParticleType> fxGravityEffect = PARTICLE_TYPES.register("gravityeffect", () -> new BasicParticleType(true));
	//public static RegistryObject<BasicParticleType> fxLaser = PARTICLE_TYPES.register("fxlaser", () -> new BasicParticleType(true));
	//public static RegistryObject<BasicParticleType> fxLaserHeat = PARTICLE_TYPES.register("fxlaserheat", () -> new BasicParticleType(true));
	//public static RegistryObject<BasicParticleType> fxLaserSpark = PARTICLE_TYPES.register("fxlaserspark", () -> new BasicParticleType(true));
	public static RegistryObject<BasicParticleType> inverseTrailFluid = PARTICLE_TYPES.register("inversetrailfluid", () -> new BasicParticleType(true));
	public static RegistryObject<BasicParticleType> inverseTrailFx = PARTICLE_TYPES.register("inversetrailfx", () -> new BasicParticleType(true));
	public static RegistryObject<BasicParticleType> oxygenCloudFx = PARTICLE_TYPES.register("oxygencloudfx", () -> new BasicParticleType(true));
	public static RegistryObject<BasicParticleType> oxygenlTraceFx = PARTICLE_TYPES.register("oxygentracefx", () -> new BasicParticleType(true));
	public static RegistryObject<BasicParticleType> rocketFx = PARTICLE_TYPES.register("rocketfx", () -> new BasicParticleType(true));
	public static RegistryObject<BasicParticleType> trailFx = PARTICLE_TYPES.register("trailfx", () -> new BasicParticleType(true));

	public static void registerParticles(RegistryEvent.Register<ParticleType<?>> evt) {
		/*evt.getRegistry().registerAll(
				fxElectricArc.setRegistryName("electricarc"),
				fxGravityEffect.setRegistryName("gravityeffect"),
				fxLaser.setRegistryName("fxlaser"), 
				fxLaserHeat.setRegistryName("fxlaserheat"),
				fxLaserSpark.setRegistryName("fxlaserspark"),
				inverseTrailFluid.setRegistryName("inversetrailfluid"),
				inverseTrailFx.setRegistryName("inversetrailfx"),
				oxygenCloudFx.setRegistryName("oxygencloudfx"),
				oxygenlTraceFx.setRegistryName("oxygentracefx"),
				rocketFx.setRegistryName("rocketfx"),
				trailFx.setRegistryName("trailfx")
				);*/
	}
	
	public static void registerParticles(ParticleFactoryRegisterEvent evt)
	{
		
		Minecraft.getInstance().particles.registerFactory(fxElectricArc.get(), FxElectricArc.Factory::new);
		Minecraft.getInstance().particles.registerFactory(fxGravityEffect.get(), FxGravityEffect.Factory::new);
		//Minecraft.getInstance().particles.registerFactory(fxLaser.get(), FxLaser.Factory::new);
		//Minecraft.getInstance().particles.registerFactory(fxLaserHeat.get(), FxLaserHeat.Factory::new);
		//Minecraft.getInstance().particles.registerFactory(fxLaserSpark.get(), FxLaserSpark.Factory::new);
		Minecraft.getInstance().particles.registerFactory(inverseTrailFluid.get(), InverseTrailFluid.Factory::new);
		Minecraft.getInstance().particles.registerFactory(inverseTrailFx.get(), InverseTrailFx.Factory::new);
		Minecraft.getInstance().particles.registerFactory(oxygenCloudFx.get(), OxygenCloudFX.Factory::new);
		Minecraft.getInstance().particles.registerFactory(oxygenlTraceFx.get(), OxygenTraceFX.Factory::new);
		Minecraft.getInstance().particles.registerFactory(rocketFx.get(), RocketFx.Factory::new);
		Minecraft.getInstance().particles.registerFactory(trailFx.get(), TrailFx.Factory::new);
		

	}
}
