package zmaster587.advancedRocketry;

import com.google.common.collect.Lists;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.fluid.FlowingFluid;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.Fluids;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.tags.BlockTags;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.Dimension;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.Biomes;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.event.RegistryEvent.MissingMappings.Action;
import net.minecraftforge.event.RegistryEvent.MissingMappings.Mapping;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fluids.FluidAttributes;
import net.minecraftforge.fluids.ForgeFlowingFluid;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.ModContainer;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLLoadCompleteEvent;
import net.minecraftforge.fml.event.server.FMLServerStartedEvent;
import net.minecraftforge.fml.event.server.FMLServerStartingEvent;
import net.minecraftforge.fml.event.server.FMLServerStoppedEvent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.GameData;
import net.minecraftforge.registries.IForgeRegistryEntry;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import zmaster587.advancedRocketry.achievements.ARAchivements;
import zmaster587.advancedRocketry.api.*;
import zmaster587.advancedRocketry.api.atmosphere.AtmosphereRegister;
import zmaster587.advancedRocketry.api.capability.CapabilitySpaceArmor;
import zmaster587.advancedRocketry.api.dimension.solar.StellarBody;
import zmaster587.advancedRocketry.api.fuel.FuelRegistry;
import zmaster587.advancedRocketry.api.fuel.FuelRegistry.FuelType;
import zmaster587.advancedRocketry.api.satellite.SatelliteProperties;
import zmaster587.advancedRocketry.api.stations.ISpaceObject;
import zmaster587.advancedRocketry.armor.ItemSpaceArmor;
import zmaster587.advancedRocketry.armor.ItemSpaceChest;
import zmaster587.advancedRocketry.atmosphere.AtmosphereVacuum;
import zmaster587.advancedRocketry.backwardCompat.VersionCompat;
import zmaster587.advancedRocketry.block.*;
import zmaster587.advancedRocketry.block.cable.BlockDataCable;
import zmaster587.advancedRocketry.block.cable.BlockEnergyCable;
import zmaster587.advancedRocketry.block.cable.BlockLiquidPipe;
import zmaster587.advancedRocketry.block.multiblock.BlockARHatch;
import zmaster587.advancedRocketry.block.plant.BlockAlienLeaves;
import zmaster587.advancedRocketry.capability.CapabilityProtectiveArmor;
import zmaster587.advancedRocketry.common.CommonProxy;
import zmaster587.advancedRocketry.compat.Compat;
import zmaster587.advancedRocketry.dimension.DimensionManager;
import zmaster587.advancedRocketry.dimension.DimensionProperties;
import zmaster587.advancedRocketry.dimension.DimensionProperties.AtmosphereTypes;
import zmaster587.advancedRocketry.dimension.DimensionProperties.Temps;
import zmaster587.advancedRocketry.enchant.EnchantmentSpaceBreathing;
import zmaster587.advancedRocketry.entity.*;
import zmaster587.advancedRocketry.event.CableTickHandler;
import zmaster587.advancedRocketry.event.PlanetEventHandler;
import zmaster587.advancedRocketry.integration.CompatibilityMgr;
import zmaster587.advancedRocketry.integration.GalacticCraftHandler;
import zmaster587.advancedRocketry.item.*;
import zmaster587.advancedRocketry.item.components.ItemJetpack;
import zmaster587.advancedRocketry.item.components.ItemPressureTank;
import zmaster587.advancedRocketry.item.components.ItemUpgrade;
import zmaster587.advancedRocketry.item.tools.ItemBasicLaserGun;
import zmaster587.advancedRocketry.mission.MissionGasCollection;
import zmaster587.advancedRocketry.mission.MissionOreMining;
import zmaster587.advancedRocketry.network.*;
import zmaster587.advancedRocketry.recipe.RecipeElectricArcFurnace;
import zmaster587.advancedRocketry.satellite.*;
import zmaster587.advancedRocketry.stations.SpaceStationObject;
import zmaster587.advancedRocketry.stations.SpaceObjectManager;
import zmaster587.advancedRocketry.tile.Satellite.TileEntitySatelliteControlCenter;
import zmaster587.advancedRocketry.tile.Satellite.TileSatelliteBuilder;
import zmaster587.advancedRocketry.tile.*;
import zmaster587.advancedRocketry.tile.cables.TileDataPipe;
import zmaster587.advancedRocketry.tile.cables.TileEnergyPipe;
import zmaster587.advancedRocketry.tile.cables.TileLiquidPipe;
import zmaster587.advancedRocketry.tile.cables.TileWirelessTransciever;
import zmaster587.advancedRocketry.tile.hatch.TileDataBus;
import zmaster587.advancedRocketry.tile.hatch.TileSatelliteHatch;
import zmaster587.advancedRocketry.tile.infrastructure.*;
import zmaster587.advancedRocketry.tile.multiblock.*;
import zmaster587.advancedRocketry.tile.multiblock.energy.TileBlackHoleGenerator;
import zmaster587.advancedRocketry.tile.multiblock.energy.TileMicrowaveReciever;
import zmaster587.advancedRocketry.tile.multiblock.machine.*;
import zmaster587.advancedRocketry.tile.oxygen.TileCO2Scrubber;
import zmaster587.advancedRocketry.tile.oxygen.TileOxygenCharger;
import zmaster587.advancedRocketry.tile.oxygen.TileOxygenVent;
import zmaster587.advancedRocketry.tile.station.*;
import zmaster587.advancedRocketry.util.*;
import zmaster587.advancedRocketry.util.XMLPlanetLoader.DimensionPropertyCoupling;
import zmaster587.advancedRocketry.world.decoration.MapGenLander;
import zmaster587.libVulpes.LibVulpes;
import zmaster587.libVulpes.api.LibVulpesBlocks;
import zmaster587.libVulpes.api.LibVulpesItems;
import zmaster587.libVulpes.api.material.AllowedProducts;
import zmaster587.libVulpes.api.material.MaterialRegistry;
import zmaster587.libVulpes.api.material.MixedMaterial;
import zmaster587.libVulpes.block.BlockAlphaTexture;
import zmaster587.libVulpes.block.BlockMeta;
import zmaster587.libVulpes.block.BlockMotor;
import zmaster587.libVulpes.block.BlockTile;
import zmaster587.libVulpes.block.multiblock.BlockMultiBlockComponentVisible;
import zmaster587.libVulpes.block.multiblock.BlockMultiblockMachine;
import zmaster587.libVulpes.event.BucketHandler;
import zmaster587.libVulpes.inventory.GuiHandler;
import zmaster587.libVulpes.items.ItemBlockMeta;
import zmaster587.libVulpes.items.ItemProjector;
import zmaster587.libVulpes.network.PacketHandler;
import zmaster587.libVulpes.network.PacketItemModifcation;
import zmaster587.libVulpes.recipe.RecipesMachine;
import zmaster587.libVulpes.tile.TileMaterial;
import zmaster587.libVulpes.tile.multiblock.TileMultiBlock;
import zmaster587.libVulpes.util.FluidUtils;
import zmaster587.libVulpes.util.HashedBlockPosition;
import zmaster587.libVulpes.util.InputSyncHandler;
import zmaster587.libVulpes.util.SingleEntry;
import zmaster587.advancedRocketry.client.ClientProxy;
import zmaster587.advancedRocketry.command.PlanetCommand;

import java.io.*;
import java.util.*;
import java.util.Map.Entry;

// @Mod(modid="advancedrocketry", name="Advanced Rocketry", version="@MAJOR@.@MINOR@.@REVIS@.@BUILD@", dependencies="required-after:libvulpes@[%LIBVULPESVERSION%,)")
@Mod(value=Constants.modId)
public class AdvancedRocketry {


	public static CommonProxy proxy = (CommonProxy) DistExecutor.runForDist(() -> ClientProxy::new, () -> CommonProxy::new);

	public final static String version = "@MAJOR@.@MINOR@.@REVIS@@BUILD@";

	public static AdvancedRocketry instance;
	public static final RecipeHandler machineRecipes = new RecipeHandler();

	final String oreGen = "Ore Generation";
	final String ROCKET = "Rockets";
	final String MOD_INTERACTION = "Mod Interaction";
	final String PLANET = "Planet";
	final String ASTEROID = "Asteroid";
	final String BLACK_HOLE = "Black_hole_generator";
	final String GAS_MINING = "GasMining";
	final String PERFORMANCE = "Performance";
	final String CLIENT = "Client";

	public static ModContainer MOD_CONTAINER;
	public static CompatibilityMgr compat = new CompatibilityMgr();
	public static Logger logger = LogManager.getLogger(Constants.modId);
	private static final String BIOMECATETORY = "Biomes";
	private boolean resetFromXml;
	
	//static {
	//	FluidRegistry.enableUniversalBucket(); // Must be called before preInit
	//}

	public static MaterialRegistry materialRegistry = new MaterialRegistry(); 

	public static HashMap<AllowedProducts, HashSet<String>> modProducts = new HashMap<AllowedProducts, HashSet<String>>();


	public static ItemGroup tabAdvRocketry = new ItemGroup("advancedRocketry") {
		@Override
		public ItemStack createIcon() {
			return new ItemStack(AdvancedRocketryItems.itemSatelliteIdChip);
		}
	};
	
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, Constants.modId);
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, Constants.modId);
    public static final DeferredRegister<Fluid> FLUIDS = DeferredRegister.create(ForgeRegistries.FLUIDS, Constants.modId);
	
	public AdvancedRocketry() {
		MOD_CONTAINER = ModLoadingContext.get().getActiveContainer();
		preInit();
		registerBiomes();
	}
	
	//Biome registry.
	@SubscribeEvent
	public void registerBiomes(/*RegistryEvent.Register<Biome> evt*/)
	{
	    System.out.println("REGISTERING BIOMES");
	    AdvancedRocketryBiomes.registerBiomes();
	}

	@SubscribeEvent
	public void preInit()
	{
		//Init API
		AdvancedRocketryAPI.atomsphereSealHandler = SealableBlockHandler.INSTANCE;
		((SealableBlockHandler)AdvancedRocketryAPI.atomsphereSealHandler).loadDefaultData();


		//Configuration  ---------------------------------------------------------------------------------------------
		resetFromXml = ARConfiguration.getCurrentConfig().resetFromXML.get();
		
		//Load client and UI positioning stuff
		proxy.loadUILayout(ARConfiguration.getCurrentConfig());

		//Register cap events
		MinecraftForge.EVENT_BUS.register(new CapabilityProtectiveArmor());

		//Register Packets
		PacketHandler.INSTANCE.addDiscriminator(PacketDimInfo.class);
		PacketHandler.INSTANCE.addDiscriminator(PacketSatellite.class);
		PacketHandler.INSTANCE.addDiscriminator(PacketStellarInfo.class);
		PacketHandler.INSTANCE.addDiscriminator(PacketItemModifcation.class);
		PacketHandler.INSTANCE.addDiscriminator(PacketOxygenState.class);
		PacketHandler.INSTANCE.addDiscriminator(PacketStationUpdate.class);
		PacketHandler.INSTANCE.addDiscriminator(PacketSpaceStationInfo.class);
		PacketHandler.INSTANCE.addDiscriminator(PacketAtmSync.class);
		PacketHandler.INSTANCE.addDiscriminator(PacketBiomeIDChange.class);
		PacketHandler.INSTANCE.addDiscriminator(PacketStorageTileUpdate.class);
		PacketHandler.INSTANCE.addDiscriminator(PacketLaserGun.class);
		PacketHandler.INSTANCE.addDiscriminator(PacketAsteroidInfo.class);
		PacketHandler.INSTANCE.addDiscriminator(PacketAirParticle.class);
		PacketHandler.INSTANCE.addDiscriminator(PacketInvalidLocationNotify.class);
		PacketHandler.INSTANCE.addDiscriminator(PacketConfigSync.class);
		PacketHandler.INSTANCE.addDiscriminator(PacketFluidParticle.class);
		
		
		//if(zmaster587.advancedRocketry.api.Configuration.allowMakingItemsForOtherMods)
		MinecraftForge.EVENT_BUS.register(this);

		//Satellites ---------------------------------------------------------------------------------------------
		SatelliteRegistry.registerSatellite("optical", SatelliteOptical.class);
		SatelliteRegistry.registerSatellite("solar", SatelliteEnergy.class);
		SatelliteRegistry.registerSatellite("density", SatelliteDensity.class);
		SatelliteRegistry.registerSatellite("composition", SatelliteComposition.class);
		SatelliteRegistry.registerSatellite("mass", SatelliteMassScanner.class);
		SatelliteRegistry.registerSatellite("asteroidMiner", MissionOreMining.class);
		SatelliteRegistry.registerSatellite("gasMining", MissionGasCollection.class);
		SatelliteRegistry.registerSatellite("solarEnergy", SatelliteEnergy.class);
		SatelliteRegistry.registerSatellite("oreScanner", SatelliteOreMapping.class);
		SatelliteRegistry.registerSatellite("biomeChanger", SatelliteBiomeChanger.class);
		
		//TileEntity Registration ---------------------------------------------------------------------------------------------

		//Register machine recipes
		LibVulpes.registerRecipeHandler(TileCuttingMachine.class, "./config/" + zmaster587.advancedRocketry.api.ARConfiguration.configFolder + "/CuttingMachine.xml");
		LibVulpes.registerRecipeHandler(TilePrecisionAssembler.class, "./config/" + zmaster587.advancedRocketry.api.ARConfiguration.configFolder + "/PrecisionAssembler.xml");
		LibVulpes.registerRecipeHandler(TileChemicalReactor.class, "./config/" + zmaster587.advancedRocketry.api.ARConfiguration.configFolder + "/ChemicalReactor.xml");
		LibVulpes.registerRecipeHandler(TileCrystallizer.class, "./config/" + zmaster587.advancedRocketry.api.ARConfiguration.configFolder + "/Crystallizer.xml");
		LibVulpes.registerRecipeHandler(TileElectrolyser.class, "./config/" + zmaster587.advancedRocketry.api.ARConfiguration.configFolder + "/Electrolyser.xml");
		LibVulpes.registerRecipeHandler(TileElectricArcFurnace.class, "./config/" + zmaster587.advancedRocketry.api.ARConfiguration.configFolder + "/ElectricArcFurnace.xml");
		LibVulpes.registerRecipeHandler(TileLathe.class, "./config/" + zmaster587.advancedRocketry.api.ARConfiguration.configFolder + "/Lathe.xml");
		LibVulpes.registerRecipeHandler(TileRollingMachine.class, "./config/" + zmaster587.advancedRocketry.api.ARConfiguration.configFolder + "/RollingMachine.xml");
		LibVulpes.registerRecipeHandler(BlockPress.class, "./config/" + zmaster587.advancedRocketry.api.ARConfiguration.configFolder + "/SmallPlatePress.xml");
		LibVulpes.registerRecipeHandler(TileCentrifuge.class, "./config/" + zmaster587.advancedRocketry.api.ARConfiguration.configFolder + "/Centrifuge.xml");


		
		//AUDIO

		//MOD-SPECIFIC ENTRIES --------------------------------------------------------------------------------------------------------------------------


		//Register Space Objects
		SpaceObjectManager.getSpaceManager().registerSpaceObjectType("genericObject", SpaceStationObject.class);



		//Register item/block crap
		proxy.preinit();
		
        //Register machines
        machineRecipes.registerMachine(TileElectrolyser.class);
        machineRecipes.registerMachine(TileCuttingMachine.class);
        machineRecipes.registerMachine(TileLathe.class);
        machineRecipes.registerMachine(TilePrecisionAssembler.class);
        machineRecipes.registerMachine(TileElectricArcFurnace.class);
        machineRecipes.registerMachine(TileChemicalReactor.class);
        machineRecipes.registerMachine(TileRollingMachine.class);
        machineRecipes.registerMachine(TileCrystallizer.class);
        machineRecipes.registerMachine(TileCentrifuge.class);
	}
	
	@SubscribeEvent
	public void registerEntities(RegistryEvent.Register<EntityType<?>> event)
	{
		//Entity Registration ---------------------------------------------------------------------------------------------
		AdvancedRocketryEntities.registerEntities(event);
	}
	
	@SubscribeEvent
	public void registerTileEntities(RegistryEvent.Register<TileEntityType<?>> evt)
	{
		AdvancedRocketryTileEntityType.registerTileEntities(evt);
	}
	
	
	@SubscribeEvent(priority=EventPriority.HIGH)
	public void registerEnchants(RegistryEvent.Register<Enchantment> evt)
	{
		//Enchantments
		AdvancedRocketryAPI.enchantmentSpaceProtection = new EnchantmentSpaceBreathing();
		AdvancedRocketryAPI.enchantmentSpaceProtection.setRegistryName(new ResourceLocation("advancedrocketry:spacebreathing"));
		evt.getRegistry().register(AdvancedRocketryAPI.enchantmentSpaceProtection);
	}
	
	@SubscribeEvent(priority=EventPriority.HIGH)
    public void registerItems(RegistryEvent.Register<Item> evt)
	{
		AdvancedRocketryItems.registerItems(evt);

		//Register Satellite Properties
		SatelliteRegistry.registerSatelliteProperty(new ItemStack(AdvancedRocketryItems.itemSensorOptical, 1), new SatelliteProperties().setSatelliteType(SatelliteRegistry.getKey(SatelliteOptical.class)));
		SatelliteRegistry.registerSatelliteProperty(new ItemStack(AdvancedRocketryItems.itemSensorComposition, 1), new SatelliteProperties().setSatelliteType(SatelliteRegistry.getKey(SatelliteComposition.class)));
		SatelliteRegistry.registerSatelliteProperty(new ItemStack(AdvancedRocketryItems.itemSensorMass, 1), new SatelliteProperties().setSatelliteType(SatelliteRegistry.getKey(SatelliteMassScanner.class)));
		SatelliteRegistry.registerSatelliteProperty(new ItemStack(AdvancedRocketryItems.itemSensorMicrowave, 1), new SatelliteProperties().setSatelliteType(SatelliteRegistry.getKey(SatelliteEnergy.class)));
		SatelliteRegistry.registerSatelliteProperty(new ItemStack(AdvancedRocketryItems.itemSensorOreMapper, 1), new SatelliteProperties().setSatelliteType(SatelliteRegistry.getKey(SatelliteOreMapping.class)));
		SatelliteRegistry.registerSatelliteProperty(new ItemStack(AdvancedRocketryItems.itemSensorBiomeChanger, 1), new SatelliteProperties().setSatelliteType(SatelliteRegistry.getKey(SatelliteBiomeChanger.class)));
		SatelliteRegistry.registerSatelliteProperty(new ItemStack(AdvancedRocketryItems.itemSatellitePowerSourceBasic,1), new SatelliteProperties().setPowerGeneration(1));
		SatelliteRegistry.registerSatelliteProperty(new ItemStack(AdvancedRocketryItems.itemSatellitePowerSourceAdvanced,1), new SatelliteProperties().setPowerGeneration(10));
		SatelliteRegistry.registerSatelliteProperty(new ItemStack(LibVulpesItems.itemBattery, 1), new SatelliteProperties().setPowerStorage(100));
		SatelliteRegistry.registerSatelliteProperty(new ItemStack(LibVulpesItems.itemBatteryx2, 1), new SatelliteProperties().setPowerStorage(400));
		SatelliteRegistry.registerSatelliteProperty(new ItemStack(AdvancedRocketryItems.itemDataUnit, 1), new SatelliteProperties().setMaxData(1000));
	}
	
	@SubscribeEvent(priority=EventPriority.HIGH)
    public void registerBlocks(RegistryEvent.Register<Block> evt)
	{
		AdvancedRocketryBlocks.registerBlocks(evt);

		//Register Allowed Products
		materialRegistry.registerMaterial(new zmaster587.libVulpes.api.material.Material("TitaniumAluminide", "pickaxe", 1, 0xaec2de, AllowedProducts.getProductByName("PLATE").getFlagValue() | AllowedProducts.getProductByName("INGOT").getFlagValue() | AllowedProducts.getProductByName("NUGGET").getFlagValue() | AllowedProducts.getProductByName("DUST").getFlagValue() | AllowedProducts.getProductByName("STICK").getFlagValue() | AllowedProducts.getProductByName("BLOCK").getFlagValue() | AllowedProducts.getProductByName("GEAR").getFlagValue() | AllowedProducts.getProductByName("SHEET").getFlagValue(), false));
		materialRegistry.registerMaterial(new zmaster587.libVulpes.api.material.Material("TitaniumIridium", "pickaxe", 1, 0xd7dfe4, AllowedProducts.getProductByName("PLATE").getFlagValue() | AllowedProducts.getProductByName("INGOT").getFlagValue() | AllowedProducts.getProductByName("NUGGET").getFlagValue() | AllowedProducts.getProductByName("DUST").getFlagValue() | AllowedProducts.getProductByName("STICK").getFlagValue() | AllowedProducts.getProductByName("BLOCK").getFlagValue() | AllowedProducts.getProductByName("GEAR").getFlagValue() | AllowedProducts.getProductByName("SHEET").getFlagValue(), false));

		materialRegistry.registerOres(LibVulpes.tabLibVulpesOres);
		
        //OreDict stuff
        /*OreDictionary.registerOre("turfMoon", new ItemStack(AdvancedRocketryBlocks.blockMoonTurf));
        OreDictionary.registerOre("turfMoon", new ItemStack(AdvancedRocketryBlocks.blockMoonTurfDark));
        OreDictionary.registerOre("logWood", new ItemStack(AdvancedRocketryBlocks.blockAlienWood));
        OreDictionary.registerOre("plankWood", new ItemStack(AdvancedRocketryBlocks.blockAlienPlanks));
        OreDictionary.registerOre("treeLeaves", new ItemStack(AdvancedRocketryBlocks.blockAlienLeaves));
        OreDictionary.registerOre("treeSapling", new ItemStack(AdvancedRocketryBlocks.blockAlienSapling));
        OreDictionary.registerOre("concrete", new ItemStack(AdvancedRocketryBlocks.blockConcrete));*/
	}

	@OnlyIn(value=Dist.CLIENT)
	@SubscribeEvent
	public void registerModels(ModelRegistryEvent event) {
		proxy.preInitItems();
		proxy.preInitBlocks();
	}
	

    @SubscribeEvent
	public void load(FMLCommonSetupEvent event)
	{
		proxy.init();

		zmaster587.advancedRocketry.cable.NetworkRegistry.registerFluidNetwork();

		//Register Alloys
		MaterialRegistry.registerMixedMaterial(new MixedMaterial(TileElectricArcFurnace.class, "oreRutile", new ItemStack[] {MaterialRegistry.getMaterialFromName("Titanium").getProduct(AllowedProducts.getProductByName("INGOT"))}));

		//Biomes --------------------------------------------------------------------------------------

		List<? extends ResourceLocation> biomeBlackList = ARConfiguration.getCurrentConfig().biomeBlackList.get();
		List<? extends ResourceLocation> biomeHighPressure = ARConfiguration.getCurrentConfig().biomeHighPressure.get();
		List<? extends ResourceLocation> biomeSingle = ARConfiguration.getCurrentConfig().biomeSingle.get();
		
		//Prevent these biomes from spawning normally
		AdvancedRocketryBiomes.instance.registerBlackListBiome(AdvancedRocketryBiomes.moonBiome);
		AdvancedRocketryBiomes.instance.registerBlackListBiome(AdvancedRocketryBiomes.moonBiomeDark);
		AdvancedRocketryBiomes.instance.registerBlackListBiome(AdvancedRocketryBiomes.hotDryBiome);
		AdvancedRocketryBiomes.instance.registerBlackListBiome(AdvancedRocketryBiomes.spaceBiome);
		AdvancedRocketryBiomes.instance.registerBlackListBiome(AdvancedRocketryBiomes.volcanic);

		//Read BlackList from config and register Blacklisted biomes
		for(ResourceLocation string : biomeBlackList) {
			try {
				Biome biome = AdvancedRocketryBiomes.getBiomeFromResourceLocation(string);

				if(biome == null)
					logger.warn(String.format("Error blackListing biome  \"%s\", a biome with that ID does not exist!", string));
				else
					AdvancedRocketryBiomes.instance.registerBlackListBiome(biome);
			} catch (NumberFormatException e) {
				logger.warn("Error blackListing \"" + string + "\".  It is not a valid number or Biome ResourceLocation");
			}
		}

		if(zmaster587.advancedRocketry.api.ARConfiguration.getCurrentConfig().blackListAllVanillaBiomes.get()) {
			AdvancedRocketryBiomes.instance.blackListVanillaBiomes();
		}


		//Read and Register High Pressure biomes from config
		for(ResourceLocation string : biomeHighPressure) {
			try {
				Biome biome = AdvancedRocketryBiomes.getBiomeFromResourceLocation(string);

				if(biome == null)
					logger.warn(String.format("Error registering high pressure biome \"%s\", a biome with that ID does not exist!", string));
				else
					AdvancedRocketryBiomes.instance.registerHighPressureBiome(biome);
			} catch (NumberFormatException e) {
				logger.warn("Error registering high pressure biome \"" + string + "\".  It is not a valid number or Biome ResourceLocation");
			}
		}

		//Read and Register Single biomes from config
		for(ResourceLocation string : biomeSingle) {
			try {
				Biome biome = AdvancedRocketryBiomes.getBiomeFromResourceLocation(string);

				if(biome == null)
					logger.warn(String.format("Error registering single biome \"%s\", a biome with that ID does not exist!", string));
				else
					AdvancedRocketryBiomes.instance.registerSingleBiome(biome);
			} catch (NumberFormatException e) {
				logger.warn("Error registering single biome \"" + string + "\".  It is not a valid number or Biome ResourceLocation");
			}
		}


		//Data mapping 'D'

		List<BlockMeta> list = new LinkedList<BlockMeta>();
		list.add(new BlockMeta(AdvancedRocketryBlocks.blockDataBus, true));
		TileMultiBlock.addMapping('D', list);
		
		machineRecipes.createAutoGennedRecipes(modProducts);
		
        //Register the machine recipes
        machineRecipes.registerAllMachineRecipes();
	}


	@SubscribeEvent
	public void postInit(FMLLoadCompleteEvent event)
	{
		
		// For all intents and purposes, it's the same
		//FluidUtils.addFluidMapping(AdvancedRocketryFluids.oxygenStill.get(), "liquidoxygen");
		//FluidUtils.addFluidMapping(AdvancedRocketryFluids.fluidHydrogen, "liquidhydrogen");

		AtmosphereRegister.getInstance().registerHarvestableFluid(AdvancedRocketryFluids.nitrogenStill.get());
		AtmosphereRegister.getInstance().registerHarvestableFluid(AdvancedRocketryFluids.hydrogenStill.get());
		AtmosphereRegister.getInstance().registerHarvestableFluid(AdvancedRocketryFluids.oxygenStill.get());
		

		CapabilitySpaceArmor.register();

		//Register multiblock items with the projector
		((ItemProjector)LibVulpesItems.itemHoloProjector).registerMachine(new TileCuttingMachine(), (BlockTile)AdvancedRocketryBlocks.blockCuttingMachine);
		((ItemProjector)LibVulpesItems.itemHoloProjector).registerMachine(new TileLathe(), (BlockTile)AdvancedRocketryBlocks.blockLathe);
		((ItemProjector)LibVulpesItems.itemHoloProjector).registerMachine(new TileCrystallizer(), (BlockTile)AdvancedRocketryBlocks.blockCrystallizer);
		((ItemProjector)LibVulpesItems.itemHoloProjector).registerMachine(new TilePrecisionAssembler(), (BlockTile)AdvancedRocketryBlocks.blockPrecisionAssembler);
		((ItemProjector)LibVulpesItems.itemHoloProjector).registerMachine(new TileObservatory(), (BlockTile)AdvancedRocketryBlocks.blockObservatory);
		((ItemProjector)LibVulpesItems.itemHoloProjector).registerMachine(new TileAstrobodyDataProcessor(), (BlockTile)AdvancedRocketryBlocks.blockPlanetAnalyser);
		((ItemProjector)LibVulpesItems.itemHoloProjector).registerMachine(new TileRollingMachine(), (BlockTile)AdvancedRocketryBlocks.blockRollingMachine);
		((ItemProjector)LibVulpesItems.itemHoloProjector).registerMachine(new TileElectricArcFurnace(), (BlockTile)AdvancedRocketryBlocks.blockArcFurnace);
		((ItemProjector)LibVulpesItems.itemHoloProjector).registerMachine(new TileElectrolyser(), (BlockTile)AdvancedRocketryBlocks.blockElectrolyser);
		((ItemProjector)LibVulpesItems.itemHoloProjector).registerMachine(new TileChemicalReactor(), (BlockTile)AdvancedRocketryBlocks.blockChemicalReactor);
		((ItemProjector)LibVulpesItems.itemHoloProjector).registerMachine(new TileWarpCore(), (BlockTile)AdvancedRocketryBlocks.blockWarpCore);
		((ItemProjector)LibVulpesItems.itemHoloProjector).registerMachine(new TileMicrowaveReciever(), (BlockTile)AdvancedRocketryBlocks.blockMicrowaveReciever);
		((ItemProjector)LibVulpesItems.itemHoloProjector).registerMachine(new TileBiomeScanner(), (BlockTile)AdvancedRocketryBlocks.blockBiomeScanner);
		((ItemProjector)LibVulpesItems.itemHoloProjector).registerMachine(new TileAtmosphereTerraformer(), (BlockTile)AdvancedRocketryBlocks.blockAtmosphereTerraformer);
		((ItemProjector)LibVulpesItems.itemHoloProjector).registerMachine(new TileRailgun(), (BlockTile)AdvancedRocketryBlocks.blockRailgun);
		((ItemProjector)LibVulpesItems.itemHoloProjector).registerMachine(new TileSpaceElevator(), (BlockTile)AdvancedRocketryBlocks.blockSpaceElevatorController);
		((ItemProjector)LibVulpesItems.itemHoloProjector).registerMachine(new TileBeacon(), (BlockTile)AdvancedRocketryBlocks.blockBeacon);
		((ItemProjector)LibVulpesItems.itemHoloProjector).registerMachine(new TileBlackHoleGenerator(), (BlockTile)AdvancedRocketryBlocks.blockBlackHoleGenerator);
		((ItemProjector)LibVulpesItems.itemHoloProjector).registerMachine(new TileCentrifuge(), (BlockTile)AdvancedRocketryBlocks.blockCentrifuge);
		((ItemProjector)LibVulpesItems.itemHoloProjector).registerMachine(new TileGravityController(), (BlockTile)AdvancedRocketryBlocks.blockGravityMachine);
		((ItemProjector)LibVulpesItems.itemHoloProjector).registerMachine(new TileSpaceLaser(), (BlockTile)AdvancedRocketryBlocks.blockSpaceLaser);

		proxy.registerEventHandlers();
		proxy.registerKeyBindings();
		//TODO: debug
		//ClientCommandHandler.instance.registerCommand(new Debugger());

		PlanetEventHandler handle = new PlanetEventHandler();
		MinecraftForge.EVENT_BUS.register(handle);

		CableTickHandler cable = new CableTickHandler();
		MinecraftForge.EVENT_BUS.register(cable);

		InputSyncHandler inputSync = new InputSyncHandler();
		MinecraftForge.EVENT_BUS.register(inputSync);

		MinecraftForge.EVENT_BUS.register(new MapGenLander());
		AdvancedRocketryAPI.gravityManager = new GravityHandler();

		// Compat stuff
		if(ModList.get().isLoaded("galacticraftcore") && zmaster587.advancedRocketry.api.ARConfiguration.getCurrentConfig().overrideGCAir.get()) {
			GalacticCraftHandler eventHandler = new GalacticCraftHandler();
			MinecraftForge.EVENT_BUS.register(eventHandler);
			DistExecutor.runWhenOn(Dist.CLIENT, () -> () -> {MinecraftForge.EVENT_BUS.register(eventHandler);} );
				
		}
		Compat.isSpongeInstalled = ModList.get().isLoaded("sponge");
		// End compat stuff

		MinecraftForge.EVENT_BUS.register(SpaceObjectManager.getSpaceManager());

		//register gasses
		AdvancedRocketryFluids.registerGasGiantGas(AdvancedRocketryFluids.hydrogenStill.get());
		AdvancedRocketryFluids.registerGasGiantGas(AdvancedRocketryFluids.nitrogenStill.get());
		AdvancedRocketryFluids.registerGasGiantGas(AdvancedRocketryFluids.oxygenStill.get());
		
		//Register mixed material's recipes
		int mixedMetalCount = 0;
		for(MixedMaterial material : MaterialRegistry.getMixedMaterialList()) {
			RecipesMachine.getInstance().addRecipe(new ResourceLocation("mixed_metal_auto_" + mixedMetalCount), RecipeElectricArcFurnace.INSTANCE,  material.getMachine(), material.getProducts(), 100, 10, material.getInput());
		}

		//Register space dimension
		DimensionManager.getInstance().registerSpaceDimension(ARConfiguration.getCurrentConfig().spaceDimId.get());

		ARConfiguration.loadPostInit();
		//TODO recipes?
		machineRecipes.registerXMLRecipes();

		//Add the overworld as a discovered planet
		zmaster587.advancedRocketry.api.ARConfiguration.getCurrentConfig().initiallyKnownPlanets.add(Dimension.field_236053_b_.getRegistryName());
	}

	@SubscribeEvent
	public void serverStarted(FMLServerStartedEvent event) {
		for (ResourceLocation dimId : DimensionManager.getInstance().getLoadedDimensions()) {
			DimensionProperties properties = DimensionManager.getInstance().getDimensionProperties(dimId);
			if(!properties.isNativeDimension && properties.getId() == zmaster587.advancedRocketry.api.ARConfiguration.getCurrentConfig().MoonId && !ModList.get().isLoaded("galacticraftcore")) {
				properties.isNativeDimension = true;
			}
		}
	}

	@SubscribeEvent
	public void serverStarting(FMLServerStartingEvent event) {
		PlanetCommand.register(event.getServer().getCommandManager().getDispatcher());
		//Open ore files

		
		//Load Asteroids from XML
		File file = new File("./config/" + zmaster587.advancedRocketry.api.ARConfiguration.configFolder + "/asteroidConfig.xml");
		logger.info("Checking for asteroid config at " + file.getAbsolutePath());
		if(!file.exists()) {
			logger.info(file.getAbsolutePath() + " not found, generating");
			try {

				file.createNewFile();
				BufferedWriter stream;
				stream = new BufferedWriter(new FileWriter(file));
				stream.write("<Asteroids>\n\t<asteroid name=\"Small Asteroid\" distance=\"10\" mass=\"100\" massVariability=\"0.5\" minLevel=\"0\" probability=\"10\" richness=\"0.2\" richnessVariability=\"0.5\">"
						+ "\n\t\t<ore itemStack=\"minecraft:iron_ore\" chance=\"15\" />"
						+ "\n\t\t<ore itemStack=\"minecraft:gold_ore\" chance=\"10\" />"
						+ "\n\t\t<ore itemStack=\"minecraft:redstone_ore\" chance=\"10\" />"
						+ "\n\t</asteroid>"
						+ "\n\t<asteroid name=\"Iridium Enriched asteroid\" distance=\"100\" mass=\"25\" massVariability=\"0.5\" minLevel=\"0\" probability=\"0.75\" richness=\"0.2\" richnessVariability=\"0.3\">"
						+ "\n\t\t<ore itemStack=\"minecraft:iron_ore\" chance=\"25\" />"
						+ "\n\t\t<ore itemStack=\"libvulpes:ore0 10\" chance=\"5\" />"
						+ "\n\t</asteroid>"
						+ "\n</Asteroids>");
				stream.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		XMLAsteroidLoader load = new XMLAsteroidLoader();
		try {
			load.loadFile(file);
			for(AsteroidSmall asteroid : load.loadPropertyFile()) {
				zmaster587.advancedRocketry.api.ARConfiguration.getCurrentConfig().asteroidTypes.put(asteroid.ID, asteroid);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		// End load asteroids from XML
		
		
		file = new File("./config/" + zmaster587.advancedRocketry.api.ARConfiguration.configFolder + "/oreConfig.xml");
		logger.info("Checking for ore config at " + file.getAbsolutePath());
		if(!file.exists()) {
			logger.info(file.getAbsolutePath() + " not found, generating");
			try {

				file.createNewFile();
				BufferedWriter stream;
				stream = new BufferedWriter(new FileWriter(file));
				stream.write("<OreConfig>\n</OreConfig>");
				stream.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		else {
			XMLOreLoader oreLoader = new XMLOreLoader();
			try {
				oreLoader.loadFile(file);

				List<SingleEntry<HashedBlockPosition, OreGenProperties>> mapping = oreLoader.loadPropertyFile();

				for(Entry<HashedBlockPosition, OreGenProperties> entry : mapping) {
					int pressure = entry.getKey().x;
					int temp = entry.getKey().y;

					if(pressure == -1) {
						if(temp != -1) {
							OreGenProperties.setOresForTemperature(Temps.values()[temp], entry.getValue());
						}
					}
					else if(temp == -1) {
						if(pressure != -1) {
							OreGenProperties.setOresForPressure(AtmosphereTypes.values()[pressure], entry.getValue());
						}
					}
					else {
						OreGenProperties.setOresForPressureAndTemp(AtmosphereTypes.values()[pressure], Temps.values()[temp], entry.getValue());
					}
				}

			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		//End open and load ore files

		DimensionManager.getInstance().createAndLoadDimensions(resetFromXml);
		
	}


	@SubscribeEvent
	public void serverStopped(FMLServerStoppedEvent event) {
		zmaster587.advancedRocketry.dimension.DimensionManager.getInstance().onServerStopped();
		zmaster587.advancedRocketry.cable.NetworkRegistry.clearNetworks();
		SpaceObjectManager.getSpaceManager().onServerStopped();
		zmaster587.advancedRocketry.api.ARConfiguration.getCurrentConfig().MoonId = Constants.INVALID_PLANET;
		((BlockSeal)AdvancedRocketryBlocks.blockPipeSealer).clearMap();
		
		if(!zmaster587.advancedRocketry.api.ARConfiguration.getCurrentConfig().lockUI.get())
			proxy.saveUILayout(ARConfiguration.getCurrentConfig());
	}
}
