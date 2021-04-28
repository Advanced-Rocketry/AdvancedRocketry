package zmaster587.advancedRocketry.achievements;

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
	public static final CustomTrigger FLIGHT_OF_PHEONIX = new CustomTrigger("flightofpheonix");
	
	public static final CustomTrigger[] TRIGGER_ARRAY = new CustomTrigger[] {
		MOON_LANDING,
		ONE_SMALL_STEP,
		BEER,
		WENT_TO_THE_MOON,
		ALL_SHE_GOT,
		FLIGHT_OF_PHEONIX
	};

	/*	public static final Advancement moonLanding = new Advancement("Advancement.AR.moonLanding", "moonLanding", -5, 1, AdvancedRocketryBlocks.blockMoonTurf, null).initIndependentStat().registerStat();
	public static final Advancement oneSmallStep = new Advancement("Advancement.AR.oneSmallStep", "oneSmallStep", -4, -1, AdvancedRocketryBlocks.blockMoonTurf, moonLanding).setSpecial().registerStat();

	public static final Advancement dilithiumCrystals = new Advancement("Advancement.AR.dilithium", "dilithium", -2, 5, LibVulpes.materialRegistry.getItemStackFromMaterialAndType("Dilithium", AllowedProducts.getProductByName("CRYSTAL")), null).initIndependentStat().registerStat();
	public static final Advancement warp = new Advancement("Advancement.AR.warp", "warp", -2, 3, AdvancedRocketryBlocks.blockWarpCore, dilithiumCrystals).registerStat();
	//public static final Advancement spaceStation = new Advancement("Advancement.AR.spaceStation", "spaceStation", -2, -5, AdvancedRocketryItems.itemSpaceStation, dilithiumCrystals).registerStat();

	public static final Advancement beerOnTheSun = new Advancement("Advancement.beerOnTheSun", "beerOnTheSun", -4, 1, Blocks.TNT, null).initIndependentStat().registerStat();
	public static final Advancement weReallyWentToTheMoon = new Advancement("Advancement.weReallyWentToTheMoon", "weReallyWentToTheMoon", -6, -1, AdvancedRocketryItems.itemSpaceSuit_Boots, moonLanding).registerStat().setSpecial();
	public static final Advancement suitedUp = new Advancement("Advancement.AR.suitedUp", "suitedUp", 0, 5, AdvancedRocketryItems.itemSpaceSuit_Helmet, null).initIndependentStat().registerStat();

	public static final Advancement givingItAllShesGot = new Advancement("Advancement.AR.givingItAllShesGot", "givingItAllShesGot", -2, 1, AdvancedRocketryBlocks.blockWarpCore, dilithiumCrystals).registerStat();
	public static final Advancement flightOfThePhoenix = new Advancement("Advancement.AR.flightOfThePhoenix", "flightOfThePhoenix", -2, -1, AdvancedRocketryBlocks.blockWarpCore, givingItAllShesGot).setSpecial().registerStat();

	public static final Advancement blockPresser = new Advancement("Advancement.AR.flattening", "flattening", 1, -2, Blocks.PISTON, null).registerStat().initIndependentStat();
	public static final Advancement holographic = new Advancement("Advancement.holographic", "holographic", 3, -2, LibVulpesItems.itemHoloProjector, blockPresser).registerStat();
	public static final Advancement crystalline = new Advancement("Advancement.AR.crystalline", "crystalline", 5, 0, AdvancedRocketryBlocks.blockCrystallizer, holographic).registerStat();
	public static final Advancement rollin = new Advancement("Advancement.AR.rollin", "rollin", 5, 2, AdvancedRocketryBlocks.blockRollingMachine, holographic).registerStat();
	public static final Advancement spinDoctor = new Advancement("Advancement.AR.spinDoctor", "spinDoctor", 5, 4, AdvancedRocketryBlocks.blockRollingMachine, holographic).registerStat();
	public static final Advancement feelTheHeat = new Advancement("Advancement.AR.feelTheHeat", "feelTheHeat", 5, 6, AdvancedRocketryBlocks.blockArcFurnace, holographic).registerStat();
	public static final Advancement electrifying = new Advancement("Advancement.AR.electrifying", "electrifying", 5, 8, AdvancedRocketryBlocks.blockElectrolyser, holographic).registerStat();



	//public static final Advancement gottaGoFast = new Advancement("achiement.gottaGoFast", "gottaGoFast", 0, -2, new ItemStack(AdvancedRocketryItems.itemUpgrade,1,2), suitedUp).registerStat();


	public static void register() {
		AdvancementPage.registerAdvancementPage(new AdvancementPage("Advanced Rocketry", moonLanding,
				dilithiumCrystals,
				beerOnTheSun,
				weReallyWentToTheMoon,
				suitedUp,
				givingItAllShesGot,
				crystalline,
				rollin,
				warp,
				oneSmallStep,
				holographic,
				flightOfThePhoenix,
				spinDoctor,
				feelTheHeat,
				electrifying,
				blockPresser));
	}*/

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
