package zmaster587.advancedRocketry;


import zmaster587.advancedRocketry.block.BlockBasic;
import zmaster587.advancedRocketry.block.BlockLinkedHorizontalTexture;
import zmaster587.advancedRocketry.block.BlockrocketBuilder;
import zmaster587.advancedRocketry.tile.TileRocketBuilder;
import zmaster587.libVulpes.block.BlockMulti;
import zmaster587.libVulpes.block.RotatableBlock;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemReed;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.ForgeChunkManager;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.oredict.OreDictionary;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.registry.EntityRegistry;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.common.registry.LanguageRegistry;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.Mod.Instance;


@Mod(modid="advancedRocketry", name="Advanced Rocketry", version="0.0.1")
public class AdvancedRocketry {
	public static final String modId = "advancedRocketry";

	//@SidedProxy(clientSide="zmaster587.advancedRocketry.client.ClientProxy", serverSide="zmaster587.advancedRocketry.CommonProxy")
	//public static CommonProxy proxy;

	@Instance(value = modId)
	public static AdvancedRocketry instance;

	//public static CompatibilityMgr compat = new CompatibilityMgr();

	//public static BlockRemoteConnector blockRemoteConnector;
	/*public static BlockMissionControl blockMissionComp;
	public static BlockLightSource blockLightSource;
	public static BlockLaser blockSpaceLaser;
	public static BlockPrecisionAssembler blockPrecisionAssembler;
	public static BlockBlastFurnace blockBlastFurnace;
	public static BlockBlastBrick blockBlastBrick;
	public static BlockQuartzCrucible blockQuartzCrucible;
	public static BlockCrystallizer blockCrystallizer;
	public static BlockCuttingMachine blockCuttingMachine;
	public static Item quartzCrucible, oreScanner; 
	public static ItemIngredient itemIngot, itemBoule, itemNugget, itemWafer, itemCircuitPlate, itemIC;*/

	public static Block launchpad;
	public static Block structureTower;
	public static Block rocketBuilder;
	
	@EventHandler
	public void preInit(FMLPreInitializationEvent event)
	{
		Configuration config = new Configuration(event.getSuggestedConfigurationFile());
		config.load();
		
		launchpad = new BlockLinkedHorizontalTexture(Material.rock).setBlockName("pad").setCreativeTab(CreativeTabs.tabTransport).setBlockTextureName("advancedrocketry:rocketPad");//.setBlockTextureName("advancedrocketry:rocketLaunchPad");//new BlockMulti(Material.rock).setNames(new String[] {"concrete", "launchtower"});
		structureTower = new BlockBasic(Material.rock).setBlockName("structuretower").setCreativeTab(CreativeTabs.tabTransport).setBlockTextureName("advancedrocketry:structuretower");
		rocketBuilder = (BlockrocketBuilder) new BlockrocketBuilder(Material.rock).setBlockName("rockets").setCreativeTab(CreativeTabs.tabTransport);
		
		GameRegistry.registerBlock(launchpad, "launchpad");
		GameRegistry.registerBlock(rocketBuilder, "rocketBuilder");
		GameRegistry.registerBlock(structureTower, "structureTower");
		GameRegistry.registerTileEntity(TileRocketBuilder.class, "rocketBuilder");
		
		//blockRemoteConnector = new BlockRemoteConnector();
		/*blockMissionComp = new BlockMissionControl();
		blockLightSource = new BlockLightSource();
		blockSpaceLaser = new BlockLaser();
		blockPrecisionAssembler = new BlockPrecisionAssembler();
		blockBlastFurnace = new BlockBlastFurnace();
		blockBlastBrick = new BlockBlastBrick();
		blockQuartzCrucible = new BlockQuartzCrucible();
		blockCrystallizer = new BlockCrystallizer();
		blockCuttingMachine = (BlockCuttingMachine)new BlockCuttingMachine().setBlockName("cuttingMachine");
		quartzCrucible = (new ItemReed(blockQuartzCrucible)).setUnlocalizedName("qcrucible").setCreativeTab(CreativeTabs.tabTransport).setTextureName("advancedRocketry:qcrucible");
		itemIngot = (ItemIngredient) new ItemIngredient(2).setUnlocalizedName("ingot");
		itemBoule = (ItemIngredient) new ItemIngredient(1).setUnlocalizedName("boule");
		itemNugget = (ItemIngredient) new ItemIngredient(1).setUnlocalizedName("nugget");
		itemWafer = (ItemIngredient) new ItemIngredient(1).setUnlocalizedName("wafer");
		itemCircuitPlate = (ItemIngredient) new ItemIngredient(1).setUnlocalizedName("circuitplate");
		itemIC = (ItemIngredient) new ItemIngredient(1).setUnlocalizedName("circuitIC");
		oreScanner = new ItemOreScanner().setUnlocalizedName("OreScanner").setTextureName("advancedRocketry:oreScanner");
		config.save();

		//GameRegistry.registerTileEntity(TileRemoteConnector.class, "Remote Connector");
		GameRegistry.registerTileEntity(TileMissionController.class, "missionControlComp");
		GameRegistry.registerTileEntity(TileSpaceLaser.class, "spaceLaser");
		GameRegistry.registerTileEntity(TilePrecisionAssembler.class, "precisionAssembler");
		GameRegistry.registerTileEntity(TileEntityBlastFurnace.class, "blastFurnace");
		GameRegistry.registerTileEntity(TileCrystallizer.class, "crystallizer");
		GameRegistry.registerTileEntity(TileCuttingMachine.class, "cuttingMachine");
		//EntityRegistry.registerModEntity(EntityClientCameraMP.class, "Cam", 0, instance, 255, 1, false);
		EntityRegistry.registerModEntity(EntityLaserNode.class, "laserNode", 0, instance, 256, 1, false);

		OreDictionary.registerOre("ingotSilicon", new ItemStack(itemIngot,1,0));
		OreDictionary.registerOre("ingotSteel", new ItemStack(itemIngot,1,1));
		OreDictionary.registerOre("bouleSilicon", new ItemStack(itemBoule,1,0));
		OreDictionary.registerOre("nuggetSilicon", new ItemStack(itemNugget,1,0));
		OreDictionary.registerOre("waferSilicon", new ItemStack(itemWafer,1,0));
		
		
		//GameRegistry.registerBlock(blockRemoteConnector, "remoteConnector");
		GameRegistry.registerBlock(blockMissionComp, "controlComp");
		GameRegistry.registerBlock(blockSpaceLaser, "laserController");
		GameRegistry.registerBlock(blockPrecisionAssembler, "precisionAssembler");
		GameRegistry.registerBlock(blockBlastFurnace, "blastFurnace");
		GameRegistry.registerBlock(blockBlastBrick, "utilBlock");
		GameRegistry.registerBlock(blockQuartzCrucible, "quartzcrucible");
		GameRegistry.registerBlock(blockCrystallizer, "crystallizer");
		GameRegistry.registerBlock(blockCuttingMachine, "cuttingMachine");
		
		GameRegistry.registerItem(quartzCrucible, "iquartzcrucible");
		GameRegistry.registerItem(oreScanner, "oreScanner");
		itemIngot.registerItemStacks();*/

		//LanguageRegistry.addName(blockMissionComp, "Mission Control Computer");
		//LanguageRegistry.addName(blockSpaceLaser, "Laser Controller");
		//LanguageRegistry.addName(blockRemoteConnector, "Remote Connector");
		//LanguageRegistry.addName(blockPrecisionAssembler, "Precision Assembler");
		//LanguageRegistry.addName(blockBlastFurnace, "Blast Furnace Controller");
		//LanguageRegistry.addName(blockBlastBrick, "Blast Furnace Brick");
		//LanguageRegistry.addName(blockQuartzCrucible, "Quartz Crucible");
		///LanguageRegistry.addName(quartzCrucible, "Quartz Crucible");
		//LanguageRegistry.addName(blockCrystallizer, "Crystallizer");
	}

	@EventHandler
	public void load(FMLInitializationEvent event)
	{
		/*proxy.registerRenderers();
		
		GameRegistry.addShapelessRecipe(new ItemStack(blockBlastBrick,4), new ItemStack(Items.potionitem,1,8195), new ItemStack(Items.potionitem,1,8201), Blocks.brick_block, Blocks.brick_block, Blocks.brick_block, Blocks.brick_block);
		GameRegistry.addShapedRecipe(new ItemStack(blockBlastFurnace), "aba","bcb", "aba", Character.valueOf('a'), Items.brick, Character.valueOf('b'), new ItemStack(Items.dye,1,15), Character.valueOf('c'), blockBlastBrick);
		GameRegistry.addShapedRecipe(new ItemStack(quartzCrucible), " a ", "aba", " a ", Character.valueOf('a'), Items.quartz, Character.valueOf('b'), Items.cauldron);

		//MACHINES
		GameRegistry.addShapedRecipe(new ItemStack(blockPrecisionAssembler), " a ", "bcd", "ef ", 'a', Blocks.dropper, 'b', Items.repeater, 'c', Blocks.iron_block, 'd', Blocks.furnace, 'e', Blocks.heavy_weighted_pressure_plate, 'f', Items.diamond);
		GameRegistry.addShapedRecipe(new ItemStack(blockCrystallizer), "aba", "bcb","ada", 'a', Items.quartz, 'b', Items.repeater, 'c', Blocks.iron_block, 'd', Blocks.obsidian);
		GameRegistry.addShapedRecipe(new ItemStack(blockCuttingMachine), "aba", "cdc", "a a", 'a', Items.diamond, 'b', Blocks.torch, 'c', Blocks.obsidian, 'd', Blocks.iron_block);
		
		//TEMP RECIPES
		GameRegistry.addShapelessRecipe(new ItemStack(itemNugget,9,0), new ItemStack(itemIngot,1,0));
		
		//RecipesBlastFurnace.getInstance().addRecipe(new ItemStack(itemIngot,1,0), 120, Block.sand, 1);
		
		//Cutting Machine
		RecipesCuttingMachine.getInstance().addRecipe(new ItemStack(itemWafer, 6, 0), new ItemStack(itemBoule,1,0), 600, 1000);
		RecipesCuttingMachine.getInstance().addRecipe(new ItemStack(itemIC, 4, 0), new ItemStack(itemCircuitPlate,1,0), 300, 500);
		
		//Precision Assembler recipes
		RecipesPrecisionAssembler.getInstance().addRecipe(new ItemStack(itemCircuitPlate,1,0), Items.gold_ingot, Items.redstone, "waferSilicon", 900, 10000);
		
		//BlastFurnace
		RecipesBlastFurnace.getInstance().addFuel(Blocks.coal_block, 3000);
		RecipesBlastFurnace.getInstance().addRecipe(new ItemStack(itemIngot,1,0), 12000, Blocks.sand, 1);
		
		//Crystallizer
		RecipesCrystallizer.instance.addRecipe(new ItemStack(itemBoule,1,0), new ItemStack(itemNugget,1,0), new ItemStack(itemIngot,1,0), 300);
		
		NetworkRegistry.INSTANCE.registerGuiHandler(this, new GuiHandler());*/
	}

	@EventHandler
	public void postInit(FMLPostInitializationEvent event)
	{
		/*ForgeChunkManager.setForcedChunkLoadingCallback(instance, new WorldEvents());
		PacketHandler.init();
		proxy.registerKeyBinds();*/
	}
}
