package zmaster587.advancedRocketry.util;

import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

public class AudioRegistry {
	public static final SoundEvent electricShockSmall = createSoundEvent("ElectricShockSmall");
	public static final SoundEvent laserDrill = createSoundEvent("laserDrill");
	public static final SoundEvent airHissLoop = createSoundEvent("airHissLoop");
	public static final SoundEvent railgunFire = createSoundEvent("railgunBang");
	public static final SoundEvent machineLarge = createSoundEvent("MachineLarge");
	public static final SoundEvent rollingMachine = createSoundEvent("rollingMachine");
	public static final SoundEvent basicLaser = createSoundEvent("basicLaserGun");
	public static final SoundEvent combustionRocket = createSoundEvent("combustionRocket");
	public static final SoundEvent crystallizer = createSoundEvent("crystallizer");
	public static final SoundEvent lathe = createSoundEvent("lathe");
	public static final SoundEvent cuttingMachine = createSoundEvent("cuttingMachine");
	public static final SoundEvent electrolyser = createSoundEvent("electrolyser");
	public static final SoundEvent precAss = createSoundEvent("precAss");
	public static final SoundEvent electricArcFurnace = createSoundEvent("electricArcFurnace");
	public static final SoundEvent gravityOhhh = createSoundEvent("gravityOhhh");
	
	private static SoundEvent createSoundEvent(String name) {
		final ResourceLocation soundID = new ResourceLocation("advancedrocketry", name);
		return new SoundEvent(soundID).setRegistryName(soundID);
	}
	
	@Mod.EventBusSubscriber
	public static class RegistrationHandler {
		@SubscribeEvent
		public static void registerSoundEvents(RegistryEvent.Register<SoundEvent> event) {
			event.getRegistry().registerAll(electricShockSmall);
		}
	}
}
