package zmaster587.advancedRocketry.advancements;

import net.minecraft.advancements.Advancement;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.server.ServerLifecycleHooks;

public class ARAdvancements {

	public static final ResourceLocation MOON_LANDING = new ResourceLocation("advancedrocketry", "normal/moonlanding");
	public static final ResourceLocation ONE_SMALL_STEP = new ResourceLocation("advancedrocketry", "normal/onesmallstep");
	public static final ResourceLocation BEER = new ResourceLocation("advancedrocketry", "normal/beer");
	public static final ResourceLocation WENT_TO_THE_MOON = new ResourceLocation("advancedrocketry", "normal/wenttothemoon");
	public static final ResourceLocation ALL_SHE_GOT = new ResourceLocation("advancedrocketry", "normal/givingitallshesgot");
	public static final ResourceLocation PHOENIX_FLIGHT = new ResourceLocation("advancedrocketry", "normal/flightofpheonix");
	
	public static void triggerAdvancement(ResourceLocation name, ServerPlayerEntity player) {
		Advancement advancement = ServerLifecycleHooks.getCurrentServer().getAdvancementManager().getAdvancement(name);
		System.out.println(true);
		if(advancement != null) {

			for (String str : advancement.getCriteria().keySet())
				player.getAdvancements().grantCriterion(advancement, str);
		}
	}
}
