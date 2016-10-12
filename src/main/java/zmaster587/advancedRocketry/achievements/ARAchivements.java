package zmaster587.advancedRocketry.achievements;

import zmaster587.advancedRocketry.api.AdvancedRocketryBlocks;
import zmaster587.advancedRocketry.api.AdvancedRocketryItems;
import zmaster587.libVulpes.LibVulpes;
import zmaster587.libVulpes.api.LibVulpesItems;
import zmaster587.libVulpes.api.material.AllowedProducts;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.stats.Achievement;
import net.minecraftforge.common.AchievementPage;

public class ARAchivements  {

	public static final Achievement moonLanding = new Achievement("achievment.moonLanding", "moonLanding", -5, 1, AdvancedRocketryBlocks.blockMoonTurf, null).initIndependentStat().registerStat();
	public static final Achievement oneSmallStep = new Achievement("achievment.oneSmallStep", "oneSmallStep", -4, -1, AdvancedRocketryBlocks.blockMoonTurf, moonLanding).setSpecial().registerStat();
	
	public static final Achievement dilithiumCrystals = new Achievement("achievment.dilithium", "dilithium", -2, 5, LibVulpes.materialRegistry.getItemStackFromMaterialAndType("Dilithium", AllowedProducts.getProductByName("CRYSTAL")), null).initIndependentStat().registerStat();
	public static final Achievement warp = new Achievement("achievment.warp", "warp", -2, 3, AdvancedRocketryBlocks.blockWarpCore, dilithiumCrystals).registerStat();
	//public static final Achievement spaceStation = new Achievement("achievment.spaceStation", "spaceStation", -2, -5, AdvancedRocketryItems.itemSpaceStation, dilithiumCrystals).registerStat();
	
	public static final Achievement beerOnTheSun = new Achievement("achievement.beerOnTheSun", "beerOnTheSun", -4, 1, Blocks.tnt, null).initIndependentStat().registerStat();
	public static final Achievement weReallyWentToTheMoon = new Achievement("achievement.weReallyWentToTheMoon", "weReallyWentToTheMoon", -6, -1, AdvancedRocketryItems.itemSpaceSuit_Boots, moonLanding).registerStat().setSpecial();
	public static final Achievement suitedUp = new Achievement("achievment.suitedUp", "suitedUp", 0, 5, AdvancedRocketryItems.itemSpaceSuit_Helmet, null).initIndependentStat().registerStat();
	
	public static final Achievement givingItAllShesGot = new Achievement("achievment.givingItAllShesGot", "givingItAllShesGot", -2, 1, AdvancedRocketryBlocks.blockWarpCore, dilithiumCrystals).registerStat();
	public static final Achievement flightOfThePhoenix = new Achievement("achievment.flightOfThePhoenix", "flightOfThePhoenix", -2, -1, AdvancedRocketryBlocks.blockWarpCore, givingItAllShesGot).setSpecial().registerStat();
	
	public static final Achievement blockPresser = new Achievement("achievment.flattening", "flattening", 1, -2, Blocks.piston, null).registerStat().initIndependentStat();
	public static final Achievement holographic = new Achievement("achievement.holographic", "holographic", 3, -2, LibVulpesItems.itemHoloProjector, blockPresser).registerStat();
	public static final Achievement crystalline = new Achievement("achievment.crystalline", "crystalline", 5, 0, AdvancedRocketryBlocks.blockCrystallizer, holographic).registerStat();
	public static final Achievement rollin = new Achievement("achievment.rollin", "rollin", 5, 2, AdvancedRocketryBlocks.blockRollingMachine, holographic).registerStat();
	public static final Achievement spinDoctor = new Achievement("achievment.spinDoctor", "spinDoctor", 5, 4, AdvancedRocketryBlocks.blockRollingMachine, holographic).registerStat();
	public static final Achievement feelTheHeat = new Achievement("achievment.feelTheHeat", "feelTheHeat", 5, 6, AdvancedRocketryBlocks.blockArcFurnace, holographic).registerStat();
	public static final Achievement electrifying = new Achievement("achievment.electrifying", "electrifying", 5, 8, AdvancedRocketryBlocks.blockElectrolyser, holographic).registerStat();
	
	
	
	//public static final Achievement gottaGoFast = new Achievement("achiement.gottaGoFast", "gottaGoFast", 0, -2, new ItemStack(AdvancedRocketryItems.itemUpgrade,1,2), suitedUp).registerStat();
	
	public static void register() {
		AchievementPage.registerAchievementPage(new AchievementPage("ARPage", moonLanding,
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
	}
}
