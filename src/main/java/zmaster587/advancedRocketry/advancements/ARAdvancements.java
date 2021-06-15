package zmaster587.advancedRocketry.advancements;

import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.advancements.ICriterionTrigger;
import net.minecraftforge.fml.relauncher.ReflectionHelper;

import java.lang.reflect.Method;

public class ARAdvancements {

	private static Method CriterionRegister;

	public static final CustomTrigger MOON_LANDING = new CustomTrigger("moonlanding");
	public static final CustomTrigger ONE_SMALL_STEP = new CustomTrigger("onesmallstep");
	public static final CustomTrigger BEER = new CustomTrigger("beer");
	public static final CustomTrigger WENT_TO_THE_MOON = new CustomTrigger("wenttothemoon");
	public static final CustomTrigger ALL_SHE_GOT = new CustomTrigger("givingitallshesgot");
	public static final CustomTrigger FLIGHT_OF_PHOENIX = new CustomTrigger("flightofpheonix");
	
	public static final CustomTrigger[] TRIGGER_ARRAY = new CustomTrigger[] {
		MOON_LANDING,
		ONE_SMALL_STEP,
		BEER,
		WENT_TO_THE_MOON,
		ALL_SHE_GOT,
		FLIGHT_OF_PHOENIX
	};

	public static void register() {
		Method method;
		try {
			method = ReflectionHelper.findMethod(CriteriaTriggers.class, "register", "func_192118_a", ICriterionTrigger.class);
			method.setAccessible(true);
			for (int i = 0; i < ARAdvancements.TRIGGER_ARRAY.length; i++) {
				method.invoke(null, ARAdvancements.TRIGGER_ARRAY[i]);
			} 
		} catch (Exception e) {
			
		}
	}
}
