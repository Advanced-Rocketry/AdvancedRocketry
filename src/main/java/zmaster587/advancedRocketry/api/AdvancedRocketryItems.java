package zmaster587.advancedRocketry.api;

import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemReed;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;
import zmaster587.advancedRocketry.item.ItemIngredient;
import zmaster587.advancedRocketry.item.ItemOreScanner;

public class AdvancedRocketryItems {

	public static ItemIngredient itemIngot, itemBoule, itemNugget, itemWafer, itemCircuitPlate, itemIC;
	
	public static Item oreScanner,quartzCrucible, saplingBlue;
	
	public static void initItems() {
		itemIngot = (ItemIngredient) new ItemIngredient(2).setUnlocalizedName("ingot");
		itemBoule = (ItemIngredient) new ItemIngredient(1).setUnlocalizedName("boule");
		itemNugget = (ItemIngredient) new ItemIngredient(1).setUnlocalizedName("nugget");
		itemWafer = (ItemIngredient) new ItemIngredient(1).setUnlocalizedName("wafer");
		itemCircuitPlate = (ItemIngredient) new ItemIngredient(1).setUnlocalizedName("circuitplate");
		itemIC = (ItemIngredient) new ItemIngredient(1).setUnlocalizedName("circuitIC");
		oreScanner = new ItemOreScanner().setUnlocalizedName("OreScanner").setTextureName("advancedRocketry:oreScanner");
		quartzCrucible = (new ItemReed(AdvRocketryBlocks.blockQuartzCrucible)).setUnlocalizedName("qcrucible").setCreativeTab(CreativeTabs.tabTransport).setTextureName("advancedRocketry:qcrucible");

		
		//OreDict stuff
		OreDictionary.registerOre("ingotSilicon", new ItemStack(itemIngot,1,0));
		OreDictionary.registerOre("ingotSteel", new ItemStack(itemIngot,1,1));
		OreDictionary.registerOre("bouleSilicon", new ItemStack(itemBoule,1,0));
		OreDictionary.registerOre("nuggetSilicon", new ItemStack(itemNugget,1,0));
		OreDictionary.registerOre("waferSilicon", new ItemStack(itemWafer,1,0));
		
		//Item Registration
		GameRegistry.registerItem(quartzCrucible, "iquartzcrucible");
		GameRegistry.registerItem(oreScanner, "oreScanner");
		GameRegistry.registerItem(itemIngot, itemIngot.getUnlocalizedName());
		GameRegistry.registerItem(itemBoule, itemBoule.getUnlocalizedName());
		GameRegistry.registerItem(itemCircuitPlate, itemCircuitPlate.getUnlocalizedName());
		GameRegistry.registerItem(itemIC, itemIC.getUnlocalizedName());
		GameRegistry.registerItem(itemWafer, itemWafer.getUnlocalizedName());
		GameRegistry.registerItem(itemNugget, itemNugget.getUnlocalizedName());
	}
}
