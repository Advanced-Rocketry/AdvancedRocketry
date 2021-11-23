package zmaster587.advancedRocketry.util;

import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import zmaster587.advancedRocketry.api.Constants;

public class AudioRegistry {
	public static final SoundEvent electricShockSmall = createSoundEvent("electricshocksmall");
	public static final SoundEvent laserDrill = createSoundEvent("laserdrill");
	public static final SoundEvent airHissLoop = createSoundEvent("airhissloop");
	public static final SoundEvent railgunFire = createSoundEvent("railgunbang");
	public static final SoundEvent machineLarge = createSoundEvent("machinelarge");
	public static final SoundEvent rollingMachine = createSoundEvent("rollingmachine");
	public static final SoundEvent basicLaser = createSoundEvent("basiclasergun");
	public static final SoundEvent combustionRocket = createSoundEvent("combustionrocket");
	public static final SoundEvent crystallizer = createSoundEvent("crystallizer");
	public static final SoundEvent lathe = createSoundEvent("lathe");
	public static final SoundEvent cuttingMachine = createSoundEvent("cuttingmachine");
	public static final SoundEvent electrolyser = createSoundEvent("electrolyser");
	public static final SoundEvent precAss = createSoundEvent("precass");
	public static final SoundEvent electricArcFurnace = createSoundEvent("electricarcfurnace");
	public static final SoundEvent gravityOhhh = createSoundEvent("gravityohhh");
	
	private static SoundEvent createSoundEvent(String name) {
		final ResourceLocation soundID = new ResourceLocation(Constants.modId, name);
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
