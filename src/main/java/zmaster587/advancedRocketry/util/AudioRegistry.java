package zmaster587.advancedRocketry.util;

import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class AudioRegistry {
	public static final SoundEvent electricShockSmall = createSoundEvent("ElectricShockSmall");
	public static final SoundEvent laserDrill = createSoundEvent("laserDrill");
	public static final SoundEvent airHissLoop = createSoundEvent("airHissLoop");
	public static final SoundEvent machineLarge = createSoundEvent("MachineLarge");
	public static final SoundEvent rollingMachine = createSoundEvent("rollingMachine"); 
	
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
