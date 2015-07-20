package zmaster587.advancedRocketry;


import zmaster587.advancedRocketry.Inventory.GuiHandler;
import zmaster587.advancedRocketry.api.AdvRocketryBlocks;
import zmaster587.advancedRocketry.api.AdvancedRocketryBiomes;
import zmaster587.advancedRocketry.api.AdvancedRocketryItems;
import zmaster587.advancedRocketry.api.FuelRegistry;
import zmaster587.advancedRocketry.api.FuelRegistry.FuelType;
import zmaster587.advancedRocketry.block.*;
import zmaster587.advancedRocketry.block.multiblock.BlockHatch;
import zmaster587.advancedRocketry.block.multiblock.BlockMultiblockMachine;
import zmaster587.advancedRocketry.block.multiblock.BlockMultiblockPlaceHolder;
import zmaster587.advancedRocketry.block.multiblock.BlockRFBattery;
import zmaster587.advancedRocketry.block.plant.BlockAlienWood;
import zmaster587.advancedRocketry.command.WorldCommand;
import zmaster587.advancedRocketry.common.CommonProxy;
import zmaster587.advancedRocketry.entity.EntityDummy;
import zmaster587.advancedRocketry.entity.EntityLaserNode;
import zmaster587.advancedRocketry.entity.EntityRocket;
import zmaster587.advancedRocketry.event.PlanetEventHandler;
import zmaster587.advancedRocketry.integration.CompatibilityMgr;
import zmaster587.advancedRocketry.item.ItemBlockMeta;
import zmaster587.advancedRocketry.item.ItemIngredient;
import zmaster587.advancedRocketry.item.ItemOreScanner;
import zmaster587.advancedRocketry.network.PacketHandler;
import zmaster587.advancedRocketry.recipe.RecipesMachine;
import zmaster587.advancedRocketry.recipe.RecipesBlastFurnace;
import zmaster587.advancedRocketry.tile.*;
import zmaster587.advancedRocketry.tile.multiblock.TileCrystallizer;
import zmaster587.advancedRocketry.tile.multiblock.TilePrecisionAssembler;
import zmaster587.advancedRocketry.tile.multiblock.TileCuttingMachine;
import zmaster587.advancedRocketry.tile.multiblock.TilePlaceholder;
import zmaster587.advancedRocketry.world.ProviderPlanet;
import zmaster587.advancedRocketry.world.biome.BiomeGenAlienForest;
import zmaster587.advancedRocketry.world.biome.BiomeGenMoon;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemReed;
import net.minecraft.item.ItemStack;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.oredict.OreDictionary;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerAboutToStartEvent;
import cpw.mods.fml.common.event.FMLServerStartedEvent;
import cpw.mods.fml.common.event.FMLServerStartingEvent;
import cpw.mods.fml.common.event.FMLServerStoppedEvent;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.registry.EntityRegistry;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.relauncher.Side;


@Mod(modid="advancedRocketry", name="Advanced Rocketry", version="0.0.1", dependencies="required-after:libVulpes")
public class AdvancedRocketry {
	public static final String modId = "advancedRocketry";

	@SidedProxy(clientSide="zmaster587.advancedRocketry.client.ClientProxy", serverSide="zmaster587.advancedRocketry.common.CommonProxy")
	public static CommonProxy proxy;

	@Instance(value = modId)
	public static AdvancedRocketry instance;

	public static CompatibilityMgr compat = new CompatibilityMgr();

	@EventHandler
	public void preInit(FMLPreInitializationEvent event)
	{
		Configuration config = new Configuration(event.getSuggestedConfigurationFile());
		config.load();

		AdvRocketryBlocks.initBlocks();
		AdvancedRocketryItems.initItems();
		AdvancedRocketryBiomes.init();

		EntityRegistry.registerModEntity(EntityDummy.class, "mountDummy", 0, this, 16, 20, false);
		EntityRegistry.registerModEntity(EntityRocket.class, "rocket", 1, this, 64, 20, true);

		GameRegistry.registerTileEntity(TileRocketBuilder.class, "rocketBuilder");
		GameRegistry.registerTileEntity(TileModelRender.class, "modelRenderer");
		GameRegistry.registerTileEntity(TileEntityFuelingStation.class, "fuelingStation");
		GameRegistry.registerTileEntity(TilePlaceholder.class, "placeHolder");


		
		config.save();

		GameRegistry.registerTileEntity(TileMissionController.class, "missionControlComp");
		GameRegistry.registerTileEntity(TileSpaceLaser.class, "spaceLaser");
		GameRegistry.registerTileEntity(TilePrecisionAssembler.class, "precisionAssembler");
		GameRegistry.registerTileEntity(TileEntityBlastFurnace.class, "blastFurnace");
		GameRegistry.registerTileEntity(zmaster587.advancedRocketry.tile.multiblock.TileCrystallizer.class, "crystallizer");
		GameRegistry.registerTileEntity(TileOutputHatch.class, "outputHatch");
		GameRegistry.registerTileEntity(TileInputHatch.class, "inputHatch");
		GameRegistry.registerTileEntity(TileRFBattery.class, "rfBattery");
		GameRegistry.registerTileEntity(TileCuttingMachine.class, "cuttingmachine");
		EntityRegistry.registerModEntity(EntityLaserNode.class, "laserNode", 0, instance, 256, 1, false);


	}

	@EventHandler
	public void load(FMLInitializationEvent event)
	{
		proxy.registerRenderers();
		NetworkRegistry.INSTANCE.registerGuiHandler(this, new GuiHandler());

		/*DimensionManager.registerProviderType(zmaster587.advancedRocketry.util.Configuration.MoonId, ProviderMoon.class, true);
		DimensionManager.registerDimension(zmaster587.advancedRocketry.util.Configuration.MoonId, zmaster587.advancedRocketry.util.Configuration.MoonId);


		//TEMPWORLD
		DimensionManager.registerProviderType(3, ProviderMoon.class, true);
		DimensionManager.registerDimension(3, 3);*/





		GameRegistry.addShapelessRecipe(new ItemStack(AdvRocketryBlocks.blockBlastBrick,4), new ItemStack(Items.potionitem,1,8195), new ItemStack(Items.potionitem,1,8201), Blocks.brick_block, Blocks.brick_block, Blocks.brick_block, Blocks.brick_block);
		GameRegistry.addShapedRecipe(new ItemStack(AdvRocketryBlocks.blockBlastFurnace), "aba","bcb", "aba", Character.valueOf('a'), Items.brick, Character.valueOf('b'), new ItemStack(Items.dye,1,15), Character.valueOf('c'), AdvRocketryBlocks.blockBlastBrick);
		GameRegistry.addShapedRecipe(new ItemStack(AdvancedRocketryItems.quartzCrucible), " a ", "aba", " a ", Character.valueOf('a'), Items.quartz, Character.valueOf('b'), Items.cauldron);

		//MACHINES
		GameRegistry.addShapedRecipe(new ItemStack(AdvRocketryBlocks.blockPrecisionAssembler), " a ", "bcd", "ef ", 'a', Blocks.dropper, 'b', Items.repeater, 'c', Blocks.iron_block, 'd', Blocks.furnace, 'e', Blocks.heavy_weighted_pressure_plate, 'f', Items.diamond);
		GameRegistry.addShapedRecipe(new ItemStack(AdvRocketryBlocks.blockCrystallizer), "aba", "bcb","ada", 'a', Items.quartz, 'b', Items.repeater, 'c', Blocks.iron_block, 'd', Blocks.obsidian);
		GameRegistry.addShapedRecipe(new ItemStack(AdvRocketryBlocks.blockCuttingMachine), "aba", "cdc", "a a", 'a', Items.diamond, 'b', Blocks.torch, 'c', Blocks.obsidian, 'd', Blocks.iron_block);

		//TEMP RECIPES
		GameRegistry.addShapelessRecipe(new ItemStack(AdvancedRocketryItems.itemNugget,9,0), new ItemStack(AdvancedRocketryItems.itemIngot,1,0));

		//Cutting Machine
		RecipesMachine.getInstance().addRecipe(TileCuttingMachine.class, new ItemStack(AdvancedRocketryItems.itemWafer, 6, 0), 600, 1000, new ItemStack(AdvancedRocketryItems.itemBoule,1,0));
		RecipesMachine.getInstance().addRecipe(TileCuttingMachine.class, new ItemStack(AdvancedRocketryItems.itemIC, 4, 0), 300, 500, new ItemStack(AdvancedRocketryItems.itemCircuitPlate,1,0));

		//Precision Assembler recipes
		RecipesMachine.getInstance().addRecipe(TilePrecisionAssembler.class, new ItemStack(AdvancedRocketryItems.itemCircuitPlate,1,0), 900, 10000, Items.gold_ingot, Items.redstone, "waferSilicon");


		//BlastFurnace
		RecipesBlastFurnace.getInstance().addFuel(Blocks.coal_block, 3000);
		RecipesBlastFurnace.getInstance().addRecipe(new ItemStack(AdvancedRocketryItems.itemIngot,1,0), 12000, Blocks.sand, 1);

		//Crystallizer
		RecipesMachine.getInstance().addRecipe(TileCrystallizer.class, new ItemStack(AdvancedRocketryItems.itemBoule,1,0), 300, 200, new ItemStack(AdvancedRocketryItems.itemNugget,1,0), new ItemStack(AdvancedRocketryItems.itemIngot,1,0));


		NetworkRegistry.INSTANCE.registerGuiHandler(this, new GuiHandler());
	}

	@EventHandler
	public void postInit(FMLPostInitializationEvent event)
	{
		proxy.registerEventHandlers();

		PlanetEventHandler handle = new PlanetEventHandler();
		FMLCommonHandler.instance().bus().register(handle);
		MinecraftForge.EVENT_BUS.register(handle);

		PacketHandler.init();
		FuelRegistry.instance.registerFuel(FuelType.LIQUID, FluidRegistry.WATER, 100);
		/*ForgeChunkManager.setForcedChunkLoadingCallback(instance, new WorldEvents());

		proxy.registerKeyBinds();*/
	}
	
	@EventHandler
	public void serverStarted(FMLServerStartingEvent event) {
		event.registerServerCommand(new WorldCommand());
		zmaster587.advancedRocketry.world.DimensionManager.getInstance().loadDimensions(zmaster587.advancedRocketry.world.DimensionManager.filePath);
	}
	
	@EventHandler
	public void serverStopped(FMLServerStoppedEvent event) {
		zmaster587.advancedRocketry.world.DimensionManager.getInstance().unregisterAllDimensions();
	}
}
