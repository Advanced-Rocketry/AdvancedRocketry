package zmaster587.advancedRocketry;

import net.minecraft.block.Block;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.material.MaterialLiquid;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.init.Biomes;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.item.Item.ToolMaterial;
import net.minecraft.item.ItemArmor.ArmorMaterial;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemDoor;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraft.world.WorldType;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.common.BiomeDictionary;
import net.minecraftforge.common.ForgeChunkManager;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.*;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.registry.EntityRegistry;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.oredict.OreDictionary;
import net.minecraftforge.oredict.OreDictionary.OreRegisterEvent;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import zmaster587.advancedRocketry.advancements.ARAdvancements;
import zmaster587.advancedRocketry.api.*;
import zmaster587.advancedRocketry.api.capability.CapabilitySpaceArmor;
import zmaster587.advancedRocketry.api.satellite.SatelliteProperties;
import zmaster587.advancedRocketry.armor.ItemSpaceArmor;
import zmaster587.advancedRocketry.armor.ItemSpaceChest;
import zmaster587.advancedRocketry.block.*;
import zmaster587.advancedRocketry.block.multiblock.BlockARHatch;
import zmaster587.advancedRocketry.block.plant.BlockLightwoodLeaves;
import zmaster587.advancedRocketry.block.plant.BlockLightwoodPlanks;
import zmaster587.advancedRocketry.block.plant.BlockLightwoodSapling;
import zmaster587.advancedRocketry.block.plant.BlockLightwoodWood;
import zmaster587.advancedRocketry.capability.CapabilityProtectiveArmor;
import zmaster587.advancedRocketry.command.WorldCommand;
import zmaster587.advancedRocketry.common.CommonProxy;
import zmaster587.advancedRocketry.dimension.DimensionManager;
import zmaster587.advancedRocketry.dimension.DimensionProperties;
import zmaster587.advancedRocketry.dimension.DimensionProperties.AtmosphereTypes;
import zmaster587.advancedRocketry.dimension.DimensionProperties.Temps;
import zmaster587.advancedRocketry.enchant.EnchantmentSpaceBreathing;
import zmaster587.advancedRocketry.entity.*;
import zmaster587.advancedRocketry.event.CableTickHandler;
import zmaster587.advancedRocketry.event.PlanetEventHandler;
import zmaster587.advancedRocketry.event.WorldEvents;
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
import zmaster587.advancedRocketry.satellite.*;
import zmaster587.advancedRocketry.stations.SpaceStationObject;
import zmaster587.advancedRocketry.stations.SpaceObjectManager;
import zmaster587.advancedRocketry.tile.multiblock.orbitallaserdrill.TileOrbitalLaserDrill;
import zmaster587.advancedRocketry.tile.multiblock.energy.TileSolarArray;
import zmaster587.advancedRocketry.tile.atmosphere.*;
import zmaster587.advancedRocketry.tile.satellite.TileSatelliteTerminal;
import zmaster587.advancedRocketry.tile.satellite.TileSatelliteBuilder;
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
import zmaster587.advancedRocketry.tile.station.*;
import zmaster587.advancedRocketry.util.*;
import zmaster587.advancedRocketry.world.biome.*;
import zmaster587.advancedRocketry.world.decoration.MapGenLander;
import zmaster587.advancedRocketry.world.ore.OreGenerator;
import zmaster587.advancedRocketry.world.provider.WorldProviderPlanet;
import zmaster587.advancedRocketry.world.type.WorldTypePlanetGen;
import zmaster587.advancedRocketry.world.type.WorldTypeSpace;
import zmaster587.libVulpes.LibVulpes;
import zmaster587.libVulpes.api.LibVulpesBlocks;
import zmaster587.libVulpes.api.LibVulpesItems;
import zmaster587.libVulpes.api.material.AllowedProducts;
import zmaster587.libVulpes.api.material.MaterialRegistry;
import zmaster587.libVulpes.api.material.MixedMaterial;
import zmaster587.libVulpes.block.*;
import zmaster587.libVulpes.block.multiblock.BlockMultiBlockComponentVisible;
import zmaster587.libVulpes.block.multiblock.BlockMultiBlockComponentVisibleAlphaTexture;
import zmaster587.libVulpes.block.multiblock.BlockMultiblockMachine;
import zmaster587.libVulpes.inventory.GuiHandler;
import zmaster587.libVulpes.items.ItemBlockMeta;
import zmaster587.libVulpes.items.ItemIngredient;
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

import javax.annotation.Nonnull;
import java.io.*;
import java.util.*;
import java.util.Map.Entry;


@Mod(modid="advancedrocketry", name="Advanced Rocketry", version="@MAJOR@.@MINOR@.@REVIS@.@BUILD@", dependencies="required-after:libvulpes@[%LIBVULPESVERSION%,)")
public class AdvancedRocketry {


	@SidedProxy(clientSide="zmaster587.advancedRocketry.client.ClientProxy", serverSide="zmaster587.advancedRocketry.common.CommonProxy")
	public static CommonProxy proxy;

	public final static String version = "@MAJOR@.@MINOR@.@REVIS@@BUILD@";

	@Instance(value = Constants.modId)
	public static AdvancedRocketry instance;
	public static WorldType planetWorldType;
	public static WorldType spaceWorldType;
	public static final RecipeHandler machineRecipes = new RecipeHandler();

	public static CompatibilityMgr compat = new CompatibilityMgr();
	public static final Logger logger = LogManager.getLogger(Constants.modId);
	private static Configuration config;
	private boolean resetFromXml;
	
	static  {
		FluidRegistry.enableUniversalBucket(); // Must be called before preInit
	}

	public static MaterialRegistry materialRegistry = new MaterialRegistry(); 

	public static HashMap<AllowedProducts, HashSet<String>> modProducts = new HashMap<>();


	private static CreativeTabs tabAdvRocketry = new CreativeTabs("advancedRocketry") {
		@Override
		@Nonnull
		public ItemStack getTabIconItem() {
			return new ItemStack(AdvancedRocketryItems.itemSatelliteIdChip);
		}
	};
	
	//Biome registry.
	@SubscribeEvent
	public void register(RegistryEvent.Register<Biome> evt)
	{
	    System.out.println("REGISTERING BIOMES");
	    //Biome properties
        AdvancedRocketryBiomes.moonBiome = new BiomeGenMoon(new Biome.BiomeProperties("Regolith Highlands").setRainDisabled().setBaseHeight(1f).setHeightVariation(0.2f).setRainfall(0).setTemperature(0.3f));
        AdvancedRocketryBiomes.alienForest = new BiomeGenAlienForest(new Biome.BiomeProperties("Alien Forest").setWaterColor(0x8888FF));
        AdvancedRocketryBiomes.hotDryBiome = new BiomeGenHotDryRock(new Biome.BiomeProperties("Ferric Regolith Wasteland").setRainDisabled().setBaseHeight(1f).setHeightVariation(0.01f).setRainfall(0).setTemperature(0.9f));
        AdvancedRocketryBiomes.spaceBiome = new BiomeGenSpace(new Biome.BiomeProperties("Space").setRainDisabled().setBaseHeight(-2f).setHeightVariation(0f).setTemperature(1f));
        AdvancedRocketryBiomes.stormLandsBiome = new BiomeGenStormland(new Biome.BiomeProperties("Stormland").setBaseHeight(1f).setHeightVariation(0.1f).setRainfall(0.9f).setTemperature(0.9f));
        AdvancedRocketryBiomes.crystalChasms = new BiomeGenCrystal(new Biome.BiomeProperties("Crystal Chasms").setHeightVariation(0.1f).setBaseHeight(1f).setRainfall(0.2f).setTemperature(0.1f));
        AdvancedRocketryBiomes.swampDeepBiome = new BiomeGenDeepSwamp(new Biome.BiomeProperties("Deep Swamp").setBaseHeight(-0.1f).setHeightVariation(0.2f).setRainfall(0.9f).setTemperature(0.9f).setWaterColor(14745518));
        AdvancedRocketryBiomes.marsh = new BiomeGenMarsh(new Biome.BiomeProperties("Marsh").setBaseHeight(-0.4f).setHeightVariation(0f));
        AdvancedRocketryBiomes.oceanSpires = new BiomeGenOceanSpires(new Biome.BiomeProperties("Ocean Spires").setBaseHeight(-0.5f).setHeightVariation(0f));
        AdvancedRocketryBiomes.moonBiomeDark = new BiomeGenMoonDark(new Biome.BiomeProperties("Regolith Lowlands").setRainDisabled().setBaseHeight(0.5f).setHeightVariation(0.01f).setRainfall(0).setTemperature(0.3f));
        AdvancedRocketryBiomes.volcanic = new BiomeGenVolcanic(new Biome.BiomeProperties("Volcanic").setRainDisabled().setBaseHeight(0f).setHeightVariation(0.9f).setRainfall(0).setTemperature(1.0f));
        AdvancedRocketryBiomes.volcanicBarren = new BiomeGenBarrenVolcanic(new Biome.BiomeProperties("Volcanic Lowlands").setRainDisabled().setBaseHeight(0f).setHeightVariation(0.9f).setRainfall(0).setTemperature(1.0f));

        //Biome registry names outside of constructor
		AdvancedRocketryBiomes.moonBiome.setRegistryName(Constants.modId, "moon");
		AdvancedRocketryBiomes.alienForest.setRegistryName(Constants.modId, "alien_forest");
		AdvancedRocketryBiomes.hotDryBiome.setRegistryName(Constants.modId, "hotdryrock");
		AdvancedRocketryBiomes.spaceBiome.setRegistryName(Constants.modId, "space");
		AdvancedRocketryBiomes.stormLandsBiome.setRegistryName(Constants.modId, "stormland");
		AdvancedRocketryBiomes.crystalChasms.setRegistryName(Constants.modId, "crystalchasms");
		AdvancedRocketryBiomes.swampDeepBiome.setRegistryName(Constants.modId, "deepswamp");
		AdvancedRocketryBiomes.marsh.setRegistryName(Constants.modId, "marsh");
		AdvancedRocketryBiomes.oceanSpires.setRegistryName(Constants.modId, "oceanspires");
		AdvancedRocketryBiomes.moonBiomeDark.setRegistryName(Constants.modId, "moondark");
		AdvancedRocketryBiomes.volcanic.setRegistryName(Constants.modId, "volcanic");
		AdvancedRocketryBiomes.volcanicBarren.setRegistryName(Constants.modId, "volcanicbarren");

		//Actual registry
		AdvancedRocketryBiomes.instance.registerBiome(AdvancedRocketryBiomes.moonBiome, evt.getRegistry());
        AdvancedRocketryBiomes.instance.registerBiome(AdvancedRocketryBiomes.alienForest, evt.getRegistry());
        AdvancedRocketryBiomes.instance.registerBiome(AdvancedRocketryBiomes.hotDryBiome, evt.getRegistry());
        AdvancedRocketryBiomes.instance.registerBiome(AdvancedRocketryBiomes.spaceBiome, evt.getRegistry());
        AdvancedRocketryBiomes.instance.registerBiome(AdvancedRocketryBiomes.stormLandsBiome, evt.getRegistry());
        AdvancedRocketryBiomes.instance.registerBiome(AdvancedRocketryBiomes.crystalChasms, evt.getRegistry());
        AdvancedRocketryBiomes.instance.registerBiome(AdvancedRocketryBiomes.swampDeepBiome, evt.getRegistry());
        AdvancedRocketryBiomes.instance.registerBiome(AdvancedRocketryBiomes.marsh, evt.getRegistry());
        AdvancedRocketryBiomes.instance.registerBiome(AdvancedRocketryBiomes.oceanSpires, evt.getRegistry());
        AdvancedRocketryBiomes.instance.registerBiome(AdvancedRocketryBiomes.moonBiomeDark, evt.getRegistry());
        AdvancedRocketryBiomes.instance.registerBiome(AdvancedRocketryBiomes.volcanic, evt.getRegistry());
        AdvancedRocketryBiomes.instance.registerBiome(AdvancedRocketryBiomes.volcanicBarren, evt.getRegistry());
        
        BiomeDictionary.addTypes(AdvancedRocketryBiomes.moonBiome, 
        		BiomeDictionary.Type.WASTELAND,
        		BiomeDictionary.Type.DRY,
        		BiomeDictionary.Type.COLD
        		);
        BiomeDictionary.addTypes(AdvancedRocketryBiomes.moonBiomeDark, 
        		BiomeDictionary.Type.WASTELAND,
        		BiomeDictionary.Type.DRY,
        		BiomeDictionary.Type.COLD
        		);
        BiomeDictionary.addTypes(AdvancedRocketryBiomes.alienForest, 
        		BiomeDictionary.Type.MAGICAL,
        		BiomeDictionary.Type.FOREST
        		);
        BiomeDictionary.addTypes(AdvancedRocketryBiomes.hotDryBiome, 
        		BiomeDictionary.Type.WASTELAND,
        		BiomeDictionary.Type.DRY,
        		BiomeDictionary.Type.HOT
        		);
        BiomeDictionary.addTypes(AdvancedRocketryBiomes.volcanic, 
        		BiomeDictionary.Type.WASTELAND,
        		BiomeDictionary.Type.DRY,
        		BiomeDictionary.Type.HOT,
        		BiomeDictionary.Type.MOUNTAIN
        		);
        BiomeDictionary.addTypes(AdvancedRocketryBiomes.volcanicBarren, 
        		BiomeDictionary.Type.WASTELAND,
        		BiomeDictionary.Type.DRY,
        		BiomeDictionary.Type.HOT,
        		BiomeDictionary.Type.MOUNTAIN
        		);
        BiomeDictionary.addTypes(AdvancedRocketryBiomes.spaceBiome, BiomeDictionary.Type.VOID);
        BiomeDictionary.addTypes(AdvancedRocketryBiomes.stormLandsBiome, 
        		BiomeDictionary.Type.WASTELAND,
        		BiomeDictionary.Type.WET,
        		BiomeDictionary.Type.HOT
        		);
        
        BiomeDictionary.addTypes(AdvancedRocketryBiomes.swampDeepBiome,
        		BiomeDictionary.Type.WET,
        		BiomeDictionary.Type.HOT
        		);
        BiomeDictionary.addTypes(AdvancedRocketryBiomes.marsh,
        		BiomeDictionary.Type.WET,
        		BiomeDictionary.Type.HOT
        		);
        BiomeDictionary.addTypes(AdvancedRocketryBiomes.oceanSpires,
        		BiomeDictionary.Type.OCEAN
        		);
		BiomeDictionary.addTypes(AdvancedRocketryBiomes.crystalChasms,
				BiomeDictionary.Type.SNOWY,
				BiomeDictionary.Type.WASTELAND
		);
	}

	@EventHandler
	public void preInit(FMLPreInitializationEvent event)
	{
		//Init API
		DimensionManager.planetWorldProvider = WorldProviderPlanet.class;
		AdvancedRocketryAPI.atomsphereSealHandler = SealableBlockHandler.INSTANCE;
		((SealableBlockHandler)AdvancedRocketryAPI.atomsphereSealHandler).loadDefaultData();


		//Configuration  ---------------------------------------------------------------------------------------------

		config = new Configuration(new File(event.getModConfigurationDirectory(), "/" + zmaster587.advancedRocketry.api.ARConfiguration.configFolder + "/advancedRocketry.cfg"));
		zmaster587.advancedRocketry.api.ARConfiguration.getCurrentConfig().config = config;
		config.load();
		ARConfiguration.loadPreInit();
		resetFromXml = config.getBoolean("resetPlanetsFromXML", Configuration.CATEGORY_GENERAL, false, "setting this to true will force AR to read from the XML file in the config/advRocketry instead of the local data, intended for use pack developers to ensure updates are pushed through");
		
		//Load client and UI positioning stuff
		proxy.loadUILayout(config);

		config.save();

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
		SatelliteRegistry.registerSatellite("density", SatelliteDensity.class);
		SatelliteRegistry.registerSatellite("composition", SatelliteComposition.class);
		SatelliteRegistry.registerSatellite("mass", SatelliteMassScanner.class);
		SatelliteRegistry.registerSatellite("asteroidMiner", MissionOreMining.class);
		SatelliteRegistry.registerSatellite("gasMining", MissionGasCollection.class);
		SatelliteRegistry.registerSatellite("solarEnergy", SatelliteMicrowaveEnergy.class);
		SatelliteRegistry.registerSatellite("oreScanner", SatelliteOreMapping.class);
		SatelliteRegistry.registerSatellite("biomeChanger", SatelliteBiomeChanger.class);


		//Entity Registration ---------------------------------------------------------------------------------------------
		EntityRegistry.registerModEntity(new ResourceLocation(Constants.modId, "mountDummy"),EntityDummy.class, "mountDummy", 0, this, 16, 20, false);
		EntityRegistry.registerModEntity(new ResourceLocation(Constants.modId, "rocket") ,EntityRocket.class, "rocket", 1, this, 64, 3, true);
		EntityRegistry.registerModEntity(new ResourceLocation(Constants.modId, "laserNode"), EntityLaserNode.class, "laserNode", 2, instance, 256, 20, false);
		EntityRegistry.registerModEntity(new ResourceLocation(Constants.modId, "deployedRocket"), EntityStationDeployedRocket.class, "deployedRocket", 3, this, 256, 600, true);
		EntityRegistry.registerModEntity(new ResourceLocation(Constants.modId, "ARAbductedItem"), EntityItemAbducted.class, "ARAbductedItem", 4, this, 127, 600, false);
		EntityRegistry.registerModEntity(new ResourceLocation(Constants.modId, "ARPlanetUIItem"), EntityUIPlanet.class, "ARPlanetUIItem", 5, this, 64, 1, false);
		EntityRegistry.registerModEntity(new ResourceLocation(Constants.modId, "ARPlanetUIButton"), EntityUIButton.class, "ARPlanetUIButton", 6, this, 64, 20, false);
		EntityRegistry.registerModEntity(new ResourceLocation(Constants.modId, "ARStarUIButton"), EntityUIStar.class, "ARStarUIButton", 7, this, 64, 20, false);
		EntityRegistry.registerModEntity(new ResourceLocation(Constants.modId, "ARSpaceElevatorCapsule"),EntityElevatorCapsule.class, "ARSpaceElevatorCapsule", 8, this, 64, 20, true);
		EntityRegistry.registerModEntity(new ResourceLocation(Constants.modId, "ARHoverCraft") , EntityHoverCraft.class, "hovercraft", 9, this, 64, 3, true);
		
		//TileEntity Registration ---------------------------------------------------------------------------------------------
		GameRegistry.registerTileEntity(TileRocketAssemblingMachine.class, "ARrocketBuilder");
		GameRegistry.registerTileEntity(TileWarpCore.class, "ARwarpCore");
		//GameRegistry.registerTileEntity(TileModelRender.class, "ARmodelRenderer");
		GameRegistry.registerTileEntity(TileFuelingStation.class, "ARfuelingStation");
		GameRegistry.registerTileEntity(TileRocketMonitoringStation.class, "ARmonitoringStation");
		//GameRegistry.registerTileEntity(TileMissionController.class, "ARmissionControlComp");
		GameRegistry.registerTileEntity(TileOrbitalLaserDrill.class, "ARspaceLaser");
		GameRegistry.registerTileEntity(TilePrecisionAssembler.class, "ARprecisionAssembler");
		GameRegistry.registerTileEntity(TileObservatory.class, "ARobservatory");
		GameRegistry.registerTileEntity(TileCrystallizer.class, "ARcrystallizer");
		GameRegistry.registerTileEntity(TileCuttingMachine.class, "ARcuttingmachine");
		GameRegistry.registerTileEntity(TileDataBus.class, "ARdataBus");
		GameRegistry.registerTileEntity(TileSatelliteHatch.class, "ARsatelliteHatch");
		GameRegistry.registerTileEntity(TileGuidanceComputerAccessHatch.class, "ARguidanceComputerHatch");
		GameRegistry.registerTileEntity(TileSatelliteBuilder.class, "ARsatelliteBuilder");
		GameRegistry.registerTileEntity(TileSatelliteTerminal.class, "ARTileEntitySatelliteControlCenter");
		GameRegistry.registerTileEntity(TileAstrobodyDataProcessor.class, "ARplanetAnalyser");
		GameRegistry.registerTileEntity(TileGuidanceComputer.class, "ARguidanceComputer");
		GameRegistry.registerTileEntity(TileElectricArcFurnace.class, "ARelectricArcFurnace");
		GameRegistry.registerTileEntity(TilePlanetSelector.class, "ARTilePlanetSelector");
		//GameRegistry.registerTileEntity(TileModelRenderRotatable.class, "ARTileModelRenderRotatable");
		GameRegistry.registerTileEntity(TileMaterial.class, "ARTileMaterial");
		GameRegistry.registerTileEntity(TileLathe.class, "ARTileLathe");
		GameRegistry.registerTileEntity(TileRollingMachine.class, "ARTileMetalBender");
		GameRegistry.registerTileEntity(TileStationAssembler.class, "ARStationBuilder");
		GameRegistry.registerTileEntity(TileElectrolyser.class, "ARElectrolyser");
		GameRegistry.registerTileEntity(TileChemicalReactor.class, "ARChemicalReactor");
		GameRegistry.registerTileEntity(TileOxygenVent.class, "AROxygenVent");
		GameRegistry.registerTileEntity(TileGasChargePad.class, "AROxygenCharger");
		GameRegistry.registerTileEntity(TileCO2Scrubber.class, "ARCO2Scrubber");
		GameRegistry.registerTileEntity(TileWarpController.class, "ARStationMonitor");
		GameRegistry.registerTileEntity(TileAtmosphereDetector.class, "AROxygenDetector");
		GameRegistry.registerTileEntity(TileStationOrientationController.class, "AROrientationControl");
		GameRegistry.registerTileEntity(TileStationGravityController.class, "ARGravityControl");
		GameRegistry.registerTileEntity(TileLiquidPipe.class, "ARLiquidPipe");
		GameRegistry.registerTileEntity(TileDataPipe.class, "ARDataPipe");
		GameRegistry.registerTileEntity(TileEnergyPipe.class, "AREnergyPipe");
		GameRegistry.registerTileEntity(TileMicrowaveReciever.class, "ARMicrowaveReciever");
		GameRegistry.registerTileEntity(TileSuitWorkStation.class, "ARSuitWorkStation");
		GameRegistry.registerTileEntity(TileRocketLoader.class, "ARRocketLoader");
		GameRegistry.registerTileEntity(TileRocketUnloader.class, "ARRocketUnloader");
		GameRegistry.registerTileEntity(TileBiomeScanner.class, "ARBiomeScanner");
		GameRegistry.registerTileEntity(TileAtmosphereTerraformer.class, "ARAttTerraformer");
		GameRegistry.registerTileEntity(TileLandingPad.class, "ARLandingPad");
		GameRegistry.registerTileEntity(TileUnmannedVehicleAssembler.class, "ARStationDeployableRocketAssembler");
		GameRegistry.registerTileEntity(TileFluidTank.class, "ARFluidTank");
		GameRegistry.registerTileEntity(TileRocketFluidUnloader.class, "ARFluidUnloader");
		GameRegistry.registerTileEntity(TileRocketFluidLoader.class, "ARFluidLoader");
		GameRegistry.registerTileEntity(TileSolarPanel.class, "ARSolarGenerator");
		GameRegistry.registerTileEntity(TileDockingPort.class, "ARDockingPort");
		GameRegistry.registerTileEntity(TileStationAltitudeController.class, "ARStationAltitudeController");
		GameRegistry.registerTileEntity(TileRailgun.class, "ARRailgun");
		GameRegistry.registerTileEntity(TileHolographicPlanetSelector.class, "ARplanetHoloSelector");
		GameRegistry.registerTileEntity(TileForceFieldProjector.class, "ARForceFieldProjector");
		GameRegistry.registerTileEntity(TileSeal.class, "ARBlockSeal");
		GameRegistry.registerTileEntity(TileSpaceElevator.class, "ARSpaceElevator");
		GameRegistry.registerTileEntity(TileBeacon.class, "ARBeacon");
		GameRegistry.registerTileEntity(TileWirelessTransciever.class, "ARTransciever");
		GameRegistry.registerTileEntity(TileBlackHoleGenerator.class, "ARblackholegenerator");
		GameRegistry.registerTileEntity(TilePump.class, new ResourceLocation(Constants.modId, "ARpump"));
		GameRegistry.registerTileEntity(TileCentrifuge.class, new ResourceLocation(Constants.modId, "ARCentrifuge"));
		GameRegistry.registerTileEntity(TilePrecisionLaserEtcher.class, new ResourceLocation(Constants.modId, "ARPrecisionLaserEtcher"));
		GameRegistry.registerTileEntity(TileSolarArray.class, new ResourceLocation(Constants.modId, "ARSolarArray"));
		
		if(zmaster587.advancedRocketry.api.ARConfiguration.getCurrentConfig().enableGravityController)
			GameRegistry.registerTileEntity(TileAreaGravityController.class, "ARGravityMachine");


		//Register machine recipes
		LibVulpes.registerRecipeHandler(TileCuttingMachine.class, event.getModConfigurationDirectory().getAbsolutePath() + "/" + zmaster587.advancedRocketry.api.ARConfiguration.configFolder + "/CuttingMachine.xml");
		LibVulpes.registerRecipeHandler(TilePrecisionAssembler.class, event.getModConfigurationDirectory().getAbsolutePath() + "/" + zmaster587.advancedRocketry.api.ARConfiguration.configFolder + "/PrecisionAssembler.xml");
		LibVulpes.registerRecipeHandler(TileChemicalReactor.class, event.getModConfigurationDirectory().getAbsolutePath() + "/" + zmaster587.advancedRocketry.api.ARConfiguration.configFolder + "/ChemicalReactor.xml");
		LibVulpes.registerRecipeHandler(TileCrystallizer.class, event.getModConfigurationDirectory().getAbsolutePath() + "/" + zmaster587.advancedRocketry.api.ARConfiguration.configFolder + "/Crystallizer.xml");
		LibVulpes.registerRecipeHandler(TileElectrolyser.class, event.getModConfigurationDirectory().getAbsolutePath() + "/" + zmaster587.advancedRocketry.api.ARConfiguration.configFolder + "/Electrolyser.xml");
		LibVulpes.registerRecipeHandler(TileElectricArcFurnace.class, event.getModConfigurationDirectory().getAbsolutePath() + "/" + zmaster587.advancedRocketry.api.ARConfiguration.configFolder + "/ElectricArcFurnace.xml");
		LibVulpes.registerRecipeHandler(TileLathe.class, event.getModConfigurationDirectory().getAbsolutePath() + "/" + zmaster587.advancedRocketry.api.ARConfiguration.configFolder + "/Lathe.xml");
		LibVulpes.registerRecipeHandler(TileRollingMachine.class, event.getModConfigurationDirectory().getAbsolutePath() + "/" + zmaster587.advancedRocketry.api.ARConfiguration.configFolder + "/RollingMachine.xml");
		LibVulpes.registerRecipeHandler(BlockSmallPlatePress.class, event.getModConfigurationDirectory().getAbsolutePath() + "/" + zmaster587.advancedRocketry.api.ARConfiguration.configFolder + "/SmallPlatePress.xml");
		LibVulpes.registerRecipeHandler(TileCentrifuge.class, event.getModConfigurationDirectory().getAbsolutePath() + "/" + zmaster587.advancedRocketry.api.ARConfiguration.configFolder + "/Centrifuge.xml");
		LibVulpes.registerRecipeHandler(TilePrecisionLaserEtcher.class, event.getModConfigurationDirectory().getAbsolutePath() + "/" + zmaster587.advancedRocketry.api.ARConfiguration.configFolder + "/PrecisionLaserEtcher.xml");


		
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
		machineRecipes.registerMachine(TilePrecisionLaserEtcher.class);
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
		//Items -------------------------------------------------------------------------------------
		AdvancedRocketryItems.itemWafer = new ItemIngredient(1).setUnlocalizedName("advancedrocketry:wafer").setCreativeTab(tabAdvRocketry);
		AdvancedRocketryItems.itemCircuitPlate = new ItemIngredient(2).setUnlocalizedName("advancedrocketry:circuitplate").setCreativeTab(tabAdvRocketry);
		AdvancedRocketryItems.itemIC = new ItemIngredient(6).setUnlocalizedName("advancedrocketry:circuitIC").setCreativeTab(tabAdvRocketry);
		AdvancedRocketryItems.itemMisc = new ItemIngredient(2).setUnlocalizedName("advancedrocketry:miscpart").setCreativeTab(tabAdvRocketry);
		AdvancedRocketryItems.itemSawBlade = new ItemIngredient(1).setUnlocalizedName("advancedrocketry:sawBlade").setCreativeTab(tabAdvRocketry);
		AdvancedRocketryItems.itemSpaceStationChip = new ItemStationChip().setUnlocalizedName("stationChip").setCreativeTab(tabAdvRocketry);
		AdvancedRocketryItems.itemSpaceElevatorChip = new ItemSpaceElevatorChip().setUnlocalizedName("elevatorChip").setCreativeTab(tabAdvRocketry);
		AdvancedRocketryItems.itemAsteroidChip = new ItemAsteroidChip().setUnlocalizedName("asteroidChip").setCreativeTab(tabAdvRocketry);
		AdvancedRocketryItems.itemSpaceStation = new ItemPackedStructure().setUnlocalizedName("station");
		AdvancedRocketryItems.itemSmallAirlockDoor = new ItemDoor(AdvancedRocketryBlocks.blockAirLock).setUnlocalizedName("smallAirlock").setCreativeTab(tabAdvRocketry);
		//Short.MAX_VALUE is forge's wildcard, don't use it
		AdvancedRocketryItems.itemCarbonScrubberCartridge = new Item().setMaxDamage(Short.MAX_VALUE-1).setUnlocalizedName("carbonScrubberCartridge").setCreativeTab(tabAdvRocketry);
		AdvancedRocketryItems.itemLens = new ItemIngredient(1).setUnlocalizedName("advancedrocketry:lens").setCreativeTab(tabAdvRocketry);
		AdvancedRocketryItems.itemSatellitePowerSource = new ItemIngredient(2).setUnlocalizedName("advancedrocketry:satellitePowerSource").setCreativeTab(tabAdvRocketry);
		AdvancedRocketryItems.itemSatellitePrimaryFunction = new ItemIngredient(6).setUnlocalizedName("advancedrocketry:satellitePrimaryFunction").setCreativeTab(tabAdvRocketry);
		AdvancedRocketryItems.itemThermite = new ItemThermite().setUnlocalizedName("thermite").setCreativeTab(tabAdvRocketry);

		//TODO: move registration in the case we have more than one chip type
		AdvancedRocketryItems.itemDataUnit = new ItemData().setUnlocalizedName("advancedrocketry:dataUnit").setCreativeTab(tabAdvRocketry);
		AdvancedRocketryItems.itemOreScanner = new ItemOreScanner().setUnlocalizedName("OreScanner").setCreativeTab(tabAdvRocketry);
		AdvancedRocketryItems.itemQuartzCrucible = new ItemBlock(AdvancedRocketryBlocks.blockQuartzCrucible).setUnlocalizedName("qcrucible").setCreativeTab(tabAdvRocketry);
		AdvancedRocketryItems.itemSatellite = new ItemSatellite().setUnlocalizedName("satellite").setCreativeTab(tabAdvRocketry).setMaxStackSize(1);
		AdvancedRocketryItems.itemSatelliteIdChip = new ItemSatelliteIdentificationChip().setUnlocalizedName("satelliteIdChip").setCreativeTab(tabAdvRocketry);
		AdvancedRocketryItems.itemPlanetIdChip = new ItemPlanetIdentificationChip().setUnlocalizedName("planetIdChip").setCreativeTab(tabAdvRocketry);
		AdvancedRocketryItems.itemBiomeChanger = new ItemBiomeChanger().setUnlocalizedName("biomeChanger").setCreativeTab(tabAdvRocketry);
		AdvancedRocketryItems.itemBasicLaserGun = new ItemBasicLaserGun().setUnlocalizedName("basicLaserGun").setCreativeTab(tabAdvRocketry);
		AdvancedRocketryItems.itemHovercraft = new ItemHovercraft().setUnlocalizedName("hovercraft").setCreativeTab(tabAdvRocketry);
		
		//Fluids
		FluidRegistry.addBucketForFluid(AdvancedRocketryFluids.fluidHydrogen);
		FluidRegistry.addBucketForFluid(AdvancedRocketryFluids.fluidNitrogen);
		FluidRegistry.addBucketForFluid(AdvancedRocketryFluids.fluidOxygen);
		FluidRegistry.addBucketForFluid(AdvancedRocketryFluids.fluidRocketFuel);
		FluidRegistry.addBucketForFluid(AdvancedRocketryFluids.fluidEnrichedLava);

		//Suit Component Registration
		AdvancedRocketryItems.itemJetpack = new ItemJetpack().setCreativeTab(tabAdvRocketry).setUnlocalizedName("jetPack");
		AdvancedRocketryItems.itemPressureTank = new ItemPressureTank(4, (int)(1000 * ARConfiguration.getCurrentConfig().suitTankCapacity)).setCreativeTab(tabAdvRocketry).setUnlocalizedName("advancedrocketry:pressureTank");
		AdvancedRocketryItems.itemUpgrade = new ItemUpgrade(5).setCreativeTab(tabAdvRocketry).setUnlocalizedName("advancedrocketry:itemUpgrade");
		AdvancedRocketryItems.itemAtmAnalyser = new ItemAtmosphereAnalzer().setCreativeTab(tabAdvRocketry).setUnlocalizedName("atmAnalyser");
		AdvancedRocketryItems.itemBeaconFinder = new ItemBeaconFinder().setCreativeTab(tabAdvRocketry).setUnlocalizedName("beaconFinder");

		//Armor registration
		AdvancedRocketryItems.itemSpaceSuit_Helmet = new ItemSpaceArmor(ArmorMaterial.LEATHER, EntityEquipmentSlot.HEAD,4).setCreativeTab(tabAdvRocketry).setUnlocalizedName("spaceHelmet");
		AdvancedRocketryItems.itemSpaceSuit_Chest = new ItemSpaceChest(ArmorMaterial.LEATHER, EntityEquipmentSlot.CHEST,6).setCreativeTab(tabAdvRocketry).setUnlocalizedName("spaceChest");
		AdvancedRocketryItems.itemSpaceSuit_Leggings = new ItemSpaceArmor(ArmorMaterial.LEATHER, EntityEquipmentSlot.LEGS,4).setCreativeTab(tabAdvRocketry).setUnlocalizedName("spaceLeggings");
		AdvancedRocketryItems.itemSpaceSuit_Boots = new ItemSpaceArmor(ArmorMaterial.LEATHER, EntityEquipmentSlot.FEET,4).setCreativeTab(tabAdvRocketry).setUnlocalizedName("spaceBoots");
		AdvancedRocketryItems.itemSealDetector = new ItemSealDetector().setMaxStackSize(1).setCreativeTab(tabAdvRocketry).setUnlocalizedName("sealDetector");

		//Tools
		AdvancedRocketryItems.itemJackhammer = new ItemJackHammer(ToolMaterial.DIAMOND).setUnlocalizedName("jackhammer").setCreativeTab(tabAdvRocketry);
		AdvancedRocketryItems.itemJackhammer.setHarvestLevel("jackhammer", 3);
		AdvancedRocketryItems.itemJackhammer.setHarvestLevel("pickaxe", 3);

		//Register Satellite Properties
		SatelliteRegistry.registerSatelliteProperty(new ItemStack(AdvancedRocketryItems.itemSatellitePrimaryFunction, 1, 0), new SatelliteProperties().setSatelliteType(SatelliteRegistry.getKey(SatelliteOptical.class)));
		SatelliteRegistry.registerSatelliteProperty(new ItemStack(AdvancedRocketryItems.itemSatellitePrimaryFunction, 1, 1), new SatelliteProperties().setSatelliteType(SatelliteRegistry.getKey(SatelliteComposition.class)));
		SatelliteRegistry.registerSatelliteProperty(new ItemStack(AdvancedRocketryItems.itemSatellitePrimaryFunction, 1, 2), new SatelliteProperties().setSatelliteType(SatelliteRegistry.getKey(SatelliteMassScanner.class)));
		SatelliteRegistry.registerSatelliteProperty(new ItemStack(AdvancedRocketryItems.itemSatellitePrimaryFunction, 1, 3), new SatelliteProperties().setSatelliteType(SatelliteRegistry.getKey(SatelliteMicrowaveEnergy.class)));
		SatelliteRegistry.registerSatelliteProperty(new ItemStack(AdvancedRocketryItems.itemSatellitePrimaryFunction, 1, 4), new SatelliteProperties().setSatelliteType(SatelliteRegistry.getKey(SatelliteOreMapping.class)));
		SatelliteRegistry.registerSatelliteProperty(new ItemStack(AdvancedRocketryItems.itemSatellitePrimaryFunction, 1, 5), new SatelliteProperties().setSatelliteType(SatelliteRegistry.getKey(SatelliteBiomeChanger.class)));
		SatelliteRegistry.registerSatelliteProperty(new ItemStack(AdvancedRocketryItems.itemSatellitePowerSource,1,0), new SatelliteProperties().setPowerGeneration(4));
		SatelliteRegistry.registerSatelliteProperty(new ItemStack(AdvancedRocketryItems.itemSatellitePowerSource,1,1), new SatelliteProperties().setPowerGeneration(40));
		SatelliteRegistry.registerSatelliteProperty(new ItemStack(LibVulpesItems.itemBattery, 1, 0), new SatelliteProperties().setPowerStorage(10000));
		SatelliteRegistry.registerSatelliteProperty(new ItemStack(LibVulpesItems.itemBattery, 1, 1), new SatelliteProperties().setPowerStorage(40000));
		SatelliteRegistry.registerSatelliteProperty(new ItemStack(AdvancedRocketryItems.itemDataUnit, 1, 0), new SatelliteProperties().setMaxData(1000));


		//Item Registration
		//Circuit pieces
		LibVulpesBlocks.registerItem(AdvancedRocketryItems.itemWafer.setRegistryName("wafer"));
		LibVulpesBlocks.registerItem(AdvancedRocketryItems.itemCircuitPlate.setRegistryName("itemCircuitPlate"));
		LibVulpesBlocks.registerItem(AdvancedRocketryItems.itemIC.setRegistryName("ic"));
		//Chips
		LibVulpesBlocks.registerItem(AdvancedRocketryItems.itemSatelliteIdChip.setRegistryName("satelliteIdChip"));
		LibVulpesBlocks.registerItem(AdvancedRocketryItems.itemPlanetIdChip.setRegistryName("planetIdChip"));
		LibVulpesBlocks.registerItem(AdvancedRocketryItems.itemAsteroidChip.setRegistryName("asteroidChip"));
		LibVulpesBlocks.registerItem(AdvancedRocketryItems.itemSpaceElevatorChip.setRegistryName("elevatorChip"));
		LibVulpesBlocks.registerItem(AdvancedRocketryItems.itemSpaceStationChip.setRegistryName("spaceStationChip"));
		LibVulpesBlocks.registerItem(AdvancedRocketryItems.itemDataUnit.setRegistryName("dataUnit"));
		//Satellite bits
		LibVulpesBlocks.registerItem(AdvancedRocketryItems.itemSatellite.setRegistryName("satellite"));
		LibVulpesBlocks.registerItem(AdvancedRocketryItems.itemSatellitePowerSource.setRegistryName("satellitePowerSource"));
		LibVulpesBlocks.registerItem(AdvancedRocketryItems.itemSatellitePrimaryFunction.setRegistryName("satellitePrimaryFunction"));
		//Spacesuit
		LibVulpesBlocks.registerItem(AdvancedRocketryItems.itemSpaceSuit_Helmet.setRegistryName("spaceHelmet"));
		LibVulpesBlocks.registerItem(AdvancedRocketryItems.itemSpaceSuit_Chest.setRegistryName("spaceChestplate"));
		LibVulpesBlocks.registerItem(AdvancedRocketryItems.itemSpaceSuit_Leggings.setRegistryName("spaceLeggings"));
		LibVulpesBlocks.registerItem(AdvancedRocketryItems.itemSpaceSuit_Boots.setRegistryName("spaceBoots"));
		//Space suit modifiers
		LibVulpesBlocks.registerItem(AdvancedRocketryItems.itemPressureTank.setRegistryName("pressureTank"));
		LibVulpesBlocks.registerItem(AdvancedRocketryItems.itemUpgrade.setRegistryName("itemUpgrade"));
		LibVulpesBlocks.registerItem(AdvancedRocketryItems.itemBeaconFinder.setRegistryName("beaconFinder"));
		LibVulpesBlocks.registerItem(AdvancedRocketryItems.itemJetpack.setRegistryName("jetPack"));
		//Handheld tools
		LibVulpesBlocks.registerItem(AdvancedRocketryItems.itemAtmAnalyser.setRegistryName("atmAnalyser"));
		LibVulpesBlocks.registerItem(AdvancedRocketryItems.itemSealDetector.setRegistryName("sealDetector"));
		LibVulpesBlocks.registerItem(AdvancedRocketryItems.itemOreScanner.setRegistryName("oreScanner"));
		if(zmaster587.advancedRocketry.api.ARConfiguration.getCurrentConfig().enableTerraforming)
			LibVulpesBlocks.registerItem(AdvancedRocketryItems.itemBiomeChanger.setRegistryName("biomeChanger"));
		LibVulpesBlocks.registerItem(AdvancedRocketryItems.itemJackhammer.setRegistryName("jackHammer"));
		LibVulpesBlocks.registerItem(AdvancedRocketryItems.itemBasicLaserGun.setRegistryName("basicLaserGun"));
		//Misc
		LibVulpesBlocks.registerItem(AdvancedRocketryItems.itemMisc.setRegistryName("misc"));
		LibVulpesBlocks.registerItem(AdvancedRocketryItems.itemSawBlade.setRegistryName("sawBladeIron"));
		LibVulpesBlocks.registerItem(AdvancedRocketryItems.itemQuartzCrucible.setRegistryName("iquartzcrucible"));
		LibVulpesBlocks.registerItem(AdvancedRocketryItems.itemLens.setRegistryName("lens"));
		LibVulpesBlocks.registerItem(AdvancedRocketryItems.itemThermite.setRegistryName("thermite"));
		LibVulpesBlocks.registerItem(AdvancedRocketryItems.itemCarbonScrubberCartridge.setRegistryName("carbonScrubberCartridge"));
		LibVulpesBlocks.registerItem(AdvancedRocketryItems.itemSmallAirlockDoor.setRegistryName("smallAirlockDoor"));
		LibVulpesBlocks.registerItem(AdvancedRocketryItems.itemHovercraft.setRegistryName("hoverCraft"));
		LibVulpesBlocks.registerItem(AdvancedRocketryItems.itemSpaceStation.setRegistryName("spaceStation"));



		
        OreDictionary.registerOre("waferSilicon", new ItemStack(AdvancedRocketryItems.itemWafer,1,0));
        OreDictionary.registerOre("ingotCarbon", new ItemStack(AdvancedRocketryItems.itemMisc, 1, 1));
        OreDictionary.registerOre("itemLens", AdvancedRocketryItems.itemLens);
		OreDictionary.registerOre("lensPrecisionLaserEtcher", AdvancedRocketryItems.itemLens);
        OreDictionary.registerOre("itemSilicon", MaterialRegistry.getItemStackFromMaterialAndType("Silicon", AllowedProducts.getProductByName("INGOT")));
        OreDictionary.registerOre("dustThermite", new ItemStack(AdvancedRocketryItems.itemThermite));
        OreDictionary.registerOre("slab", new ItemStack(Blocks.STONE_SLAB));
		OreDictionary.registerOre("blockWarpCoreCore", new ItemStack(Blocks.GOLD_BLOCK));
		OreDictionary.registerOre("blockWarpCoreRim", MaterialRegistry.getMaterialFromName("Titanium").getProduct(AllowedProducts.getProductByName("BLOCK")));
	}
	
	@SubscribeEvent(priority=EventPriority.HIGH)
    public void registerBlocks(RegistryEvent.Register<Block> evt)
	{
		//Blocks -------------------------------------------------------------------------------------
		//Machines
		//Machine parts
		AdvancedRocketryBlocks.blockConcrete = new Block(Material.ROCK).setUnlocalizedName("concrete").setCreativeTab(tabAdvRocketry).setHardness(3f).setResistance(16f);
		AdvancedRocketryBlocks.blockBlastBrick = new BlockMultiBlockComponentVisible(Material.ROCK).setCreativeTab(tabAdvRocketry).setUnlocalizedName("blastBrick").setHardness(3F).setResistance(15F);
		AdvancedRocketryBlocks.blockStructureTower = new BlockAlphaTexture(Material.IRON).setUnlocalizedName("structuretower").setCreativeTab(tabAdvRocketry).setHardness(2f);
		AdvancedRocketryBlocks.blockLens = new BlockLens().setUnlocalizedName("lens").setCreativeTab(tabAdvRocketry).setHardness(0.3f);
		AdvancedRocketryBlocks.blockSolarPanel = new Block(Material.IRON).setUnlocalizedName("solarPanel").setCreativeTab(tabAdvRocketry).setHardness(3f);
		AdvancedRocketryBlocks.blockSolarArrayPanel = new BlockMultiBlockComponentVisibleAlphaTexture(Material.IRON).setUnlocalizedName("solararraypanel").setCreativeTab(tabAdvRocketry).setHardness(1).setResistance(1f);
		AdvancedRocketryBlocks.blockQuartzCrucible = new BlockQuartzCrucible().setUnlocalizedName("qcrucible").setCreativeTab(tabAdvRocketry);
		AdvancedRocketryBlocks.blockSawBlade = new BlockMotor(Material.IRON,1f).setCreativeTab(tabAdvRocketry).setUnlocalizedName("sawBlade").setHardness(2f);
		//Singleblock machines
		AdvancedRocketryBlocks.blockPlatePress = new BlockSmallPlatePress().setUnlocalizedName("blockHandPress").setCreativeTab(tabAdvRocketry).setHardness(2f);
		AdvancedRocketryBlocks.blockForceFieldProjector = new BlockForceFieldProjector(Material.IRON).setUnlocalizedName("forceFieldProjector").setCreativeTab(tabAdvRocketry).setHardness(3f);
		AdvancedRocketryBlocks.blockForceField = new BlockForceField(Material.BARRIER).setBlockUnbreakable().setResistance(6000000.0F).setUnlocalizedName("forceField");
		AdvancedRocketryBlocks.blockVacuumLaser = new BlockFullyRotatable(Material.IRON).setUnlocalizedName("vacuumLaser").setCreativeTab(tabAdvRocketry).setHardness(4f);
		AdvancedRocketryBlocks.blockPump = new BlockTile(TilePump.class, GuiHandler.guiId.MODULAR.ordinal()).setUnlocalizedName("pump").setCreativeTab(tabAdvRocketry).setHardness(3f);
		AdvancedRocketryBlocks.blockSuitWorkStation = new BlockSuitWorkstation(TileSuitWorkStation.class, GuiHandler.guiId.MODULAR.ordinal()).setUnlocalizedName("suitWorkStation").setCreativeTab(tabAdvRocketry).setHardness(3f);
		AdvancedRocketryBlocks.blockPressureTank = new BlockPressurizedFluidTank(Material.IRON).setUnlocalizedName("pressurizedTank").setCreativeTab(tabAdvRocketry).setHardness(3f);
		AdvancedRocketryBlocks.blockSolarGenerator = new BlockSolarGenerator(TileSolarPanel.class, GuiHandler.guiId.MODULAR.ordinal()).setCreativeTab(tabAdvRocketry).setHardness(3f).setUnlocalizedName("solarGenerator");
		AdvancedRocketryBlocks.blockTransciever = new BlockTransciever(TileWirelessTransciever.class, GuiHandler.guiId.MODULAR.ordinal()).setUnlocalizedName("wirelessTransciever").setCreativeTab(tabAdvRocketry).setHardness(3f);
		//Multiblock machines
        //T1 processing
		AdvancedRocketryBlocks.blockArcFurnace = new BlockMultiblockMachine(TileElectricArcFurnace.class, GuiHandler.guiId.MODULAR.ordinal()).setUnlocalizedName("electricArcFurnace").setCreativeTab(tabAdvRocketry).setHardness(3f);
		AdvancedRocketryBlocks.blockRollingMachine = new BlockMultiblockMachine(TileRollingMachine.class, GuiHandler.guiId.MODULAR.ordinal()).setUnlocalizedName("rollingMachine").setCreativeTab(tabAdvRocketry).setHardness(3f);
		AdvancedRocketryBlocks.blockLathe = new BlockMultiblockMachine(TileLathe.class, GuiHandler.guiId.MODULAR.ordinal()).setUnlocalizedName("lathe").setCreativeTab(tabAdvRocketry).setHardness(3f);
		AdvancedRocketryBlocks.blockCrystallizer = new BlockMultiblockMachine(TileCrystallizer.class, GuiHandler.guiId.MODULAR.ordinal()).setUnlocalizedName("Crystallizer").setCreativeTab(tabAdvRocketry).setHardness(3f);
		AdvancedRocketryBlocks.blockCuttingMachine = new BlockMultiblockMachine(TileCuttingMachine.class, GuiHandler.guiId.MODULAR.ordinal()).setUnlocalizedName("cuttingMachine").setCreativeTab(tabAdvRocketry).setHardness(3f);
		AdvancedRocketryBlocks.blockPrecisionAssembler = new BlockMultiblockMachine(TilePrecisionAssembler.class, GuiHandler.guiId.MODULAR.ordinal()).setUnlocalizedName("precisionAssemblingMachine").setCreativeTab(tabAdvRocketry).setHardness(3f);
		AdvancedRocketryBlocks.blockElectrolyser = new BlockMultiblockMachine(TileElectrolyser.class, GuiHandler.guiId.MODULAR.ordinal()).setUnlocalizedName("electrolyser").setCreativeTab(tabAdvRocketry).setHardness(3f);
		AdvancedRocketryBlocks.blockChemicalReactor = new BlockMultiblockMachine(TileChemicalReactor.class, GuiHandler.guiId.MODULAR.ordinal()).setUnlocalizedName("chemreactor").setCreativeTab(tabAdvRocketry).setHardness(3f);
		//T2 processing
		AdvancedRocketryBlocks.blockPrecisionLaserEngraver = new BlockMultiblockMachine(TilePrecisionLaserEtcher.class, GuiHandler.guiId.MODULAR.ordinal()).setUnlocalizedName("precisionlaseretcher").setCreativeTab(tabAdvRocketry).setHardness(3f);
		AdvancedRocketryBlocks.blockObservatory = new BlockMultiblockMachine(TileObservatory.class, GuiHandler.guiId.MODULARNOINV.ordinal()).setUnlocalizedName("observatory").setCreativeTab(tabAdvRocketry).setHardness(3f);
		AdvancedRocketryBlocks.blockPlanetAnalyser = new BlockMultiblockMachine(TileAstrobodyDataProcessor.class, GuiHandler.guiId.MODULARNOINV.ordinal()).setUnlocalizedName("planetanalyser").setCreativeTab(tabAdvRocketry).setHardness(3f);
		AdvancedRocketryBlocks.blockCentrifuge = new BlockMultiblockMachine(TileCentrifuge.class, GuiHandler.guiId.MODULAR.ordinal()).setCreativeTab(tabAdvRocketry).setHardness(3f).setUnlocalizedName("centrifuge");
		AdvancedRocketryBlocks.blockSatelliteBuilder = new BlockMultiblockMachine(TileSatelliteBuilder.class, GuiHandler.guiId.MODULAR.ordinal()).setCreativeTab(tabAdvRocketry).setHardness(3f).setUnlocalizedName("satelliteBuilder");
		//Energy
		AdvancedRocketryBlocks.blockBlackHoleGenerator = new BlockMultiblockMachine(TileBlackHoleGenerator.class, GuiHandler.guiId.MODULAR.ordinal()).setUnlocalizedName("blackholegenerator").setCreativeTab(tabAdvRocketry).setHardness(3f);
		AdvancedRocketryBlocks.blockMicrowaveReciever = new BlockMultiblockMachine(TileMicrowaveReciever.class, GuiHandler.guiId.MODULAR.ordinal()).setCreativeTab(tabAdvRocketry).setHardness(3f).setUnlocalizedName("microwaveReciever");
		AdvancedRocketryBlocks.blockSolarArray = new BlockMultiblockMachine(TileSolarArray.class, GuiHandler.guiId.MODULAR.ordinal()).setUnlocalizedName("solararray").setCreativeTab(tabAdvRocketry).setHardness(3f);
		//Aux/huge
		AdvancedRocketryBlocks.blockWarpCore = new BlockWarpCore(TileWarpCore.class, GuiHandler.guiId.MODULAR.ordinal()).setUnlocalizedName("warpCore").setCreativeTab(tabAdvRocketry).setHardness(3f);
		AdvancedRocketryBlocks.blockBeacon = new BlockBeacon(TileBeacon.class,  GuiHandler.guiId.MODULAR.ordinal()).setCreativeTab(tabAdvRocketry).setUnlocalizedName("beacon").setHardness(3f);
		AdvancedRocketryBlocks.blockBiomeScanner = new BlockMultiblockMachine(TileBiomeScanner.class,GuiHandler.guiId.MODULARNOINV.ordinal()).setUnlocalizedName("biomeScanner").setCreativeTab(tabAdvRocketry).setHardness(3f);
		AdvancedRocketryBlocks.blockRailgun = new BlockMultiblockMachine(TileRailgun.class, GuiHandler.guiId.MODULAR.ordinal()).setUnlocalizedName("railgun").setCreativeTab(tabAdvRocketry).setHardness(3f);
		AdvancedRocketryBlocks.blockSpaceElevatorController = new BlockMultiblockMachine(TileSpaceElevator.class,  GuiHandler.guiId.MODULAR.ordinal()).setCreativeTab(tabAdvRocketry).setUnlocalizedName("spaceElevatorController").setHardness(3f);
		//Configurable stuff
		if (ARConfiguration.getCurrentConfig().enableTerraforming)
		    AdvancedRocketryBlocks.blockAtmosphereTerraformer = new BlockMultiblockMachine(TileAtmosphereTerraformer.class, GuiHandler.guiId.MODULARNOINV.ordinal()).setUnlocalizedName("atmosphereTerraformer").setCreativeTab(tabAdvRocketry).setHardness(3f);
		if(ARConfiguration.getCurrentConfig().enableGravityController)
			AdvancedRocketryBlocks.blockGravityMachine = new BlockMultiblockMachine(TileAreaGravityController.class,GuiHandler.guiId.MODULARNOINV.ordinal()).setUnlocalizedName("gravityMachine").setCreativeTab(tabAdvRocketry).setHardness(3f);
		if(ARConfiguration.getCurrentConfig().enableLaserDrill)
			AdvancedRocketryBlocks.blockSpaceLaser = new BlockOrbitalLaserDrill().setHardness(2f).setCreativeTab(tabAdvRocketry);
		//Docking blocks
		AdvancedRocketryBlocks.blockLaunchpad = new BlockLinkedHorizontalTexture(Material.ROCK).setUnlocalizedName("pad").setCreativeTab(tabAdvRocketry).setHardness(2f).setResistance(10f);
		AdvancedRocketryBlocks.blockLandingPad = new BlockLandingPad(Material.ROCK).setUnlocalizedName("dockingPad").setHardness(3f).setCreativeTab(tabAdvRocketry);
		AdvancedRocketryBlocks.blockDockingPort = new BlockStationModuleDockingPort(Material.IRON).setUnlocalizedName("stationMarker").setCreativeTab(tabAdvRocketry).setHardness(3f);
		//Rocket blocks
		AdvancedRocketryBlocks.blockGenericSeat = new BlockSeat(Material.CLOTH).setUnlocalizedName("seat").setCreativeTab(tabAdvRocketry).setHardness(0.5f);
		AdvancedRocketryBlocks.blockEngine = new BlockRocketMotor(Material.IRON).setUnlocalizedName("rocket").setCreativeTab(tabAdvRocketry).setHardness(2f);
		AdvancedRocketryBlocks.blockBipropellantEngine = new BlockBipropellantRocketMotor(Material.IRON).setUnlocalizedName("bipropellantrocket").setCreativeTab(tabAdvRocketry).setHardness(2f);
		AdvancedRocketryBlocks.blockAdvEngine = new BlockAdvancedRocketMotor(Material.IRON).setUnlocalizedName("advRocket").setCreativeTab(tabAdvRocketry).setHardness(2f);
		AdvancedRocketryBlocks.blockAdvBipropellantEngine = new BlockAdvancedBipropellantRocketMotor(Material.IRON).setUnlocalizedName("advbipropellantRocket").setCreativeTab(tabAdvRocketry).setHardness(2f);
		AdvancedRocketryBlocks.blockNuclearEngine = new BlockNuclearRocketMotor(Material.IRON).setUnlocalizedName("nuclearrocket").setCreativeTab(tabAdvRocketry).setHardness(2f);
		AdvancedRocketryBlocks.blockFuelTank = new BlockFuelTank(Material.IRON).setUnlocalizedName("fuelTank").setCreativeTab(tabAdvRocketry).setHardness(2f);
		AdvancedRocketryBlocks.blockBipropellantFuelTank = new BlockBipropellantFuelTank(Material.IRON).setUnlocalizedName("bipropellantfueltank").setCreativeTab(tabAdvRocketry).setHardness(2f);
		AdvancedRocketryBlocks.blockOxidizerFuelTank = new BlockOxidizerFuelTank(Material.IRON).setUnlocalizedName("oxidizerfueltank").setCreativeTab(tabAdvRocketry).setHardness(2f);
		AdvancedRocketryBlocks.blockNuclearFuelTank = new BlockNuclearFuelTank(Material.IRON).setUnlocalizedName("nuclearfueltank").setCreativeTab(tabAdvRocketry).setHardness(2f);
		AdvancedRocketryBlocks.blockNuclearCore = new BlockNuclearCore(Material.IRON).setUnlocalizedName("nuclearcore").setCreativeTab(tabAdvRocketry).setHardness(2f);
		AdvancedRocketryBlocks.blockGuidanceComputer = new BlockTile(TileGuidanceComputer.class,GuiHandler.guiId.MODULAR.ordinal()).setUnlocalizedName("guidanceComputer").setCreativeTab(tabAdvRocketry).setHardness(3f);
		AdvancedRocketryBlocks.blockIntake = new BlockIntake(Material.IRON).setUnlocalizedName("gasIntake").setCreativeTab(tabAdvRocketry).setHardness(3f);
		AdvancedRocketryBlocks.blockDrill = new BlockMiningDrill().setUnlocalizedName("drill").setCreativeTab(tabAdvRocketry).setHardness(3f);
		AdvancedRocketryBlocks.blockLandingFloat = new Block(Material.IRON).setUnlocalizedName("landingfloat").setCreativeTab(tabAdvRocketry).setHardness(1).setResistance(1f);
		//Assembly machines
		AdvancedRocketryBlocks.blockRocketBuilder = new BlockTileWithMultitooltip(TileRocketAssemblingMachine.class, GuiHandler.guiId.MODULARNOINV.ordinal()).setUnlocalizedName("rocketAssembler").setCreativeTab(tabAdvRocketry).setHardness(3f);
		AdvancedRocketryBlocks.blockStationBuilder = new BlockTileWithMultitooltip(TileStationAssembler.class, GuiHandler.guiId.MODULAR.ordinal()).setUnlocalizedName("stationAssembler").setCreativeTab(tabAdvRocketry).setHardness(3f);
		AdvancedRocketryBlocks.blockDeployableRocketBuilder = new BlockTileWithMultitooltip(TileUnmannedVehicleAssembler.class, GuiHandler.guiId.MODULARNOINV.ordinal()).setUnlocalizedName("deployableRocketAssembler").setCreativeTab(tabAdvRocketry).setHardness(3f);
		//Infrastructure machines
		AdvancedRocketryBlocks.blockLoader = new BlockARHatch(Material.IRON).setUnlocalizedName("loader").setCreativeTab(tabAdvRocketry).setHardness(3f);
		AdvancedRocketryBlocks.blockFuelingStation = new BlockTileRedstoneEmitter(TileFuelingStation.class, GuiHandler.guiId.MODULAR.ordinal()).setUnlocalizedName("fuelStation").setCreativeTab(tabAdvRocketry).setHardness(3f);
		AdvancedRocketryBlocks.blockMonitoringStation = new BlockTileNeighborUpdate(TileRocketMonitoringStation.class, GuiHandler.guiId.MODULARNOINV.ordinal()).setCreativeTab(tabAdvRocketry).setHardness(3f).setUnlocalizedName("monitoringstation");
		AdvancedRocketryBlocks.blockSatelliteControlCenter = new BlockTile(TileSatelliteTerminal.class, GuiHandler.guiId.MODULAR.ordinal()).setCreativeTab(tabAdvRocketry).setHardness(3f).setUnlocalizedName("satelliteMonitor");
		//Station machines
		AdvancedRocketryBlocks.blockWarpShipMonitor = new BlockWarpController(TileWarpController.class, GuiHandler.guiId.MODULARNOINV.ordinal()).setCreativeTab(tabAdvRocketry).setHardness(3f).setUnlocalizedName("stationmonitor");
		AdvancedRocketryBlocks.blockOrientationController = new BlockTile(TileStationOrientationController.class,  GuiHandler.guiId.MODULAR.ordinal()).setCreativeTab(tabAdvRocketry).setUnlocalizedName("orientationControl").setHardness(3f);
		AdvancedRocketryBlocks.blockGravityController = new BlockTileComparatorOverride(TileStationGravityController.class,  GuiHandler.guiId.MODULAR.ordinal()).setCreativeTab(tabAdvRocketry).setUnlocalizedName("gravityControl").setHardness(3f);
		AdvancedRocketryBlocks.blockAltitudeController = new BlockTileComparatorOverride(TileStationAltitudeController.class,  GuiHandler.guiId.MODULAR.ordinal()).setCreativeTab(tabAdvRocketry).setUnlocalizedName("altitudeController").setHardness(3f);
		AdvancedRocketryBlocks.blockPlanetSelector = new BlockTile(TilePlanetSelector.class,GuiHandler.guiId.MODULARFULLSCREEN.ordinal()).setUnlocalizedName("planetSelector").setCreativeTab(tabAdvRocketry).setHardness(3f);
		AdvancedRocketryBlocks.blockPlanetHoloSelector = new BlockHalfTile(TileHolographicPlanetSelector.class,GuiHandler.guiId.MODULAR.ordinal()).setUnlocalizedName("planetHoloSelector").setCreativeTab(tabAdvRocketry).setHardness(3f);
		//Oxygen machines
		AdvancedRocketryBlocks.blockCO2Scrubber = new BlockTileComparatorOverride(TileCO2Scrubber.class, GuiHandler.guiId.MODULAR.ordinal()).setCreativeTab(tabAdvRocketry).setUnlocalizedName("scrubber").setHardness(3f);
		AdvancedRocketryBlocks.blockOxygenVent = new BlockTile(TileOxygenVent.class, GuiHandler.guiId.MODULAR.ordinal()).setUnlocalizedName("oxygenVent").setCreativeTab(tabAdvRocketry).setHardness(3f);
		AdvancedRocketryBlocks.blockOxygenCharger = new BlockHalfTile(TileGasChargePad.class, GuiHandler.guiId.MODULAR.ordinal()).setUnlocalizedName("oxygenCharger").setCreativeTab(tabAdvRocketry).setHardness(3f);
		AdvancedRocketryBlocks.blockOxygenDetection = new BlockRedstoneEmitter(Material.IRON,"advancedrocketry:atmosphereDetector_active").setUnlocalizedName("atmosphereDetector").setHardness(3f).setCreativeTab(tabAdvRocketry);
		AdvancedRocketryBlocks.blockPipeSealer = new BlockSeal(Material.IRON).setUnlocalizedName("pipeSeal").setCreativeTab(tabAdvRocketry).setHardness(0.5f);
		AdvancedRocketryBlocks.blockAirLock = new BlockDoor2(Material.IRON).setUnlocalizedName("smallAirlockDoor").setHardness(3f).setResistance(8f);
		//Light sources
		AdvancedRocketryBlocks.blockUnlitTorch = new BlockTorchUnlit().setHardness(0.0F).setUnlocalizedName("unlittorch");
		AdvancedRocketryBlocks.blockThermiteTorch = new BlockThermiteTorch().setUnlocalizedName("thermiteTorch").setCreativeTab(tabAdvRocketry).setHardness(0.1f).setLightLevel(1f);
		AdvancedRocketryBlocks.blockCircleLight = new Block(Material.IRON).setUnlocalizedName("circleLight").setCreativeTab(tabAdvRocketry).setHardness(2f).setLightLevel(1f);
		AdvancedRocketryBlocks.blockLightSource = new BlockLightSource();
		AdvancedRocketryBlocks.blockRocketFire = new BlockRocketFire();
		//Worldgen
		AdvancedRocketryBlocks.blockMoonTurf = new BlockRegolith().setMapColor(MapColor.SNOW).setHardness(0.5F).setUnlocalizedName("turf").setCreativeTab(tabAdvRocketry);
		AdvancedRocketryBlocks.blockMoonTurfDark = new BlockRegolith().setMapColor(MapColor.CLAY).setHardness(0.5F).setUnlocalizedName("turfDark").setCreativeTab(tabAdvRocketry);
		AdvancedRocketryBlocks.blockHotTurf = new BlockRegolith().setMapColor(MapColor.NETHERRACK).setHardness(0.5F).setUnlocalizedName("hotDryturf").setCreativeTab(tabAdvRocketry);
		AdvancedRocketryBlocks.blockBasalt = new Block(Material.ROCK).setUnlocalizedName("basalt").setCreativeTab(tabAdvRocketry).setHardness(5f).setResistance(15f);
		AdvancedRocketryBlocks.blocksGeode = new Block(MaterialGeode.geode).setUnlocalizedName("geode").setCreativeTab(tabAdvRocketry).setHardness(6f).setResistance(2000F);
		AdvancedRocketryBlocks.blocksGeode.setHarvestLevel("jackhammer", 2);
		AdvancedRocketryBlocks.blockCrystal = new BlockCrystal().setUnlocalizedName("crystal").setCreativeTab(tabAdvRocketry).setHardness(2f);
		AdvancedRocketryBlocks.blockVitrifiedSand = new Block(Material.SAND).setUnlocalizedName("vitrifiedSand").setCreativeTab(tabAdvRocketry).setHardness(0.5F);
		AdvancedRocketryBlocks.blockCharcoalLog = new BlockCharcoalLog().setUnlocalizedName("charcoallog").setCreativeTab(tabAdvRocketry);
		AdvancedRocketryBlocks.blockElectricMushroom = new BlockElectricMushroom().setUnlocalizedName("electricMushroom").setCreativeTab(tabAdvRocketry).setHardness(0.0F);
		AdvancedRocketryBlocks.blockLightwoodWood = new BlockLightwoodWood().setUnlocalizedName("lightwoodlog").setCreativeTab(tabAdvRocketry).setHardness(3f);
		AdvancedRocketryBlocks.sblockLightwoodLeaves = new BlockLightwoodLeaves().setUnlocalizedName("lightwoodleaves").setCreativeTab(tabAdvRocketry).setHardness(3f);
		AdvancedRocketryBlocks.blockLightwoodSapling = new BlockLightwoodSapling().setUnlocalizedName("lightwoodsapling").setCreativeTab(tabAdvRocketry).setHardness(3f);
		AdvancedRocketryBlocks.blockLightwoodPlanks = new BlockLightwoodPlanks().setUnlocalizedName("lightwoodplanks").setCreativeTab(tabAdvRocketry).setHardness(3f);



		//Fluid definitions
		final ResourceLocation notFlowing = new ResourceLocation("advancedrocketry:blocks/fluid/oxygen_still");
		final ResourceLocation flowing = new ResourceLocation("advancedrocketry:blocks/fluid/oxygen_flow");
		AdvancedRocketryFluids.fluidOxygen = new Fluid("oxygen", notFlowing, flowing).setUnlocalizedName("oxygen").setGaseous(false).setDensity(800).setViscosity(1500).setColor(0xFF8F94B9);
		AdvancedRocketryFluids.fluidHydrogen = new Fluid("hydrogen", notFlowing, flowing).setUnlocalizedName("hydrogen").setGaseous(false).setDensity(800).setViscosity(1500).setColor(0xFFDBC1C1);
		AdvancedRocketryFluids.fluidNitrogen = new Fluid("nitrogen",  notFlowing, flowing).setUnlocalizedName("nitrogen").setGaseous(false).setDensity(800).setViscosity(1500).setColor(0xFF97A7E7);
		AdvancedRocketryFluids.fluidRocketFuel = new Fluid("rocketFuel",  notFlowing, flowing).setUnlocalizedName("rocketFuel").setGaseous(false).setLuminosity(2).setDensity(800).setViscosity(1500).setColor(0xFFE5D884);
		AdvancedRocketryFluids.fluidEnrichedLava = new Fluid("enrichedLava",  new ResourceLocation("advancedrocketry:blocks/fluid/lava_still"), new ResourceLocation("advancedrocketry:blocks/fluid/lava_flow")).setUnlocalizedName("enrichedLava").setLuminosity(15).setDensity(3000).setViscosity(6000).setTemperature(1300).setColor(0xFFFFFFFF);

		//Fluid Registration
		if(!FluidRegistry.registerFluid(AdvancedRocketryFluids.fluidOxygen))
			AdvancedRocketryFluids.fluidOxygen = FluidRegistry.getFluid("oxygen");
		if(!FluidRegistry.registerFluid(AdvancedRocketryFluids.fluidHydrogen))
			AdvancedRocketryFluids.fluidHydrogen = FluidRegistry.getFluid("hydrogen");
		if(!FluidRegistry.registerFluid(AdvancedRocketryFluids.fluidNitrogen))
			AdvancedRocketryFluids.fluidNitrogen = FluidRegistry.getFluid("nitrogen");
		if(!FluidRegistry.registerFluid(AdvancedRocketryFluids.fluidRocketFuel))
			AdvancedRocketryFluids.fluidRocketFuel = FluidRegistry.getFluid("rocketFuel");
		if(!FluidRegistry.registerFluid(AdvancedRocketryFluids.fluidEnrichedLava))
			AdvancedRocketryFluids.fluidEnrichedLava = FluidRegistry.getFluid("enrichedLava");

		// For all intents and purposes, they're the same -- Mekanism compat
		FluidUtils.addFluidMapping(AdvancedRocketryFluids.fluidOxygen, "liquidoxygen");
		FluidUtils.addFluidMapping(AdvancedRocketryFluids.fluidHydrogen, "liquidhydrogen");

		AdvancedRocketryBlocks.blockOxygenFluid = new BlockFluid(AdvancedRocketryFluids.fluidOxygen, Material.WATER).setUnlocalizedName("oxygenFluidBlock").setCreativeTab(CreativeTabs.MISC);
		AdvancedRocketryBlocks.blockHydrogenFluid = new BlockFluid(AdvancedRocketryFluids.fluidHydrogen, Material.WATER).setUnlocalizedName("hydrogenFluidBlock").setCreativeTab(CreativeTabs.MISC);
		AdvancedRocketryBlocks.blockNitrogenFluid = new BlockFluid(AdvancedRocketryFluids.fluidNitrogen, Material.WATER).setUnlocalizedName("nitrogenFluidBlock").setCreativeTab(CreativeTabs.MISC);
		AdvancedRocketryBlocks.blockFuelFluid = new BlockFluid(AdvancedRocketryFluids.fluidRocketFuel, new MaterialLiquid(MapColor.YELLOW)).setUnlocalizedName("rocketFuelBlock").setCreativeTab(CreativeTabs.MISC);
		AdvancedRocketryBlocks.blockEnrichedLavaFluid = new BlockEnrichedLava(AdvancedRocketryFluids.fluidEnrichedLava, Material.LAVA).setUnlocalizedName("enrichedLavaBlock").setCreativeTab(CreativeTabs.MISC).setLightLevel(15);

		//Cables
		//TODO: add back after fixing the cable network
		//AdvancedRocketryBlocks.blockFluidPipe = new BlockLiquidPipe(Material.IRON).setUnlocalizedName("liquidPipe").setCreativeTab(tabAdvRocketry).setHardness(1f);
		//AdvancedRocketryBlocks.blockDataPipe = new BlockDataCable(Material.IRON).setUnlocalizedName("dataPipe").setCreativeTab(tabAdvRocketry).setHardness(1f);
		//AdvancedRocketryBlocks.blockEnergyPipe = new BlockEnergyCable(Material.IRON).setUnlocalizedName("energyPipe").setCreativeTab(tabAdvRocketry).setHardness(1f);
		//LibVulpesBlocks.registerBlock(AdvancedRocketryBlocks.blockDataPipe.setRegistryName("dataPipe"));
		//LibVulpesBlocks.registerBlock(AdvancedRocketryBlocks.blockEnergyPipe.setRegistryName("energyPipe"));
		//LibVulpesBlocks.registerBlock(AdvancedRocketryBlocks.blockFluidPipe.setRegistryName("liquidPipe"));



		//Machines
		//Machine parts
		LibVulpesBlocks.registerBlock(AdvancedRocketryBlocks.blockConcrete.setRegistryName("concrete"));
		LibVulpesBlocks.registerBlock(AdvancedRocketryBlocks.blockBlastBrick.setRegistryName("blastbrick"));
		LibVulpesBlocks.registerBlock(AdvancedRocketryBlocks.blockStructureTower.setRegistryName("structureTower"));
		LibVulpesBlocks.registerBlock(AdvancedRocketryBlocks.blockLens.setRegistryName("blockLens"));
		LibVulpesBlocks.registerBlock(AdvancedRocketryBlocks.blockSolarPanel.setRegistryName("solarPanel"));
		LibVulpesBlocks.registerBlock(AdvancedRocketryBlocks.blockSolarArrayPanel.setRegistryName("solararraypanel"));
		LibVulpesBlocks.registerBlock(AdvancedRocketryBlocks.blockQuartzCrucible.setRegistryName("quartzcrucible"));
		LibVulpesBlocks.registerBlock(AdvancedRocketryBlocks.blockSawBlade.setRegistryName("sawBlade"));
		//Singleblock machines
		LibVulpesBlocks.registerBlock(AdvancedRocketryBlocks.blockPlatePress.setRegistryName("platepress"));
		LibVulpesBlocks.registerBlock(AdvancedRocketryBlocks.blockForceFieldProjector.setRegistryName("forceFieldProjector"));
		LibVulpesBlocks.registerBlock(AdvancedRocketryBlocks.blockForceField.setRegistryName("forceField"));
		LibVulpesBlocks.registerBlock(AdvancedRocketryBlocks.blockVacuumLaser.setRegistryName("vacuumLaser"));
		LibVulpesBlocks.registerBlock(AdvancedRocketryBlocks.blockPump.setRegistryName("blockPump"));
		LibVulpesBlocks.registerBlock(AdvancedRocketryBlocks.blockSuitWorkStation.setRegistryName("suitWorkStation"));
		LibVulpesBlocks.registerBlock(AdvancedRocketryBlocks.blockPressureTank.setRegistryName("liquidTank"), ItemBlockFluidTank.class, true);
		LibVulpesBlocks.registerBlock(AdvancedRocketryBlocks.blockSolarGenerator.setRegistryName("solarGenerator"));
		LibVulpesBlocks.registerBlock(AdvancedRocketryBlocks.blockTransciever.setRegistryName("wirelessTransciever"));
		//Multiblock machines
		//T1 processing
		LibVulpesBlocks.registerBlock(AdvancedRocketryBlocks.blockArcFurnace.setRegistryName("arcfurnace"));
		LibVulpesBlocks.registerBlock(AdvancedRocketryBlocks.blockRollingMachine.setRegistryName("rollingMachine"));
		LibVulpesBlocks.registerBlock(AdvancedRocketryBlocks.blockLathe.setRegistryName("lathe"));
		LibVulpesBlocks.registerBlock(AdvancedRocketryBlocks.blockCrystallizer.setRegistryName("crystallizer"));
		LibVulpesBlocks.registerBlock(AdvancedRocketryBlocks.blockCuttingMachine.setRegistryName("cuttingMachine"));
		LibVulpesBlocks.registerBlock(AdvancedRocketryBlocks.blockPrecisionAssembler.setRegistryName("precisionassemblingmachine"));
		LibVulpesBlocks.registerBlock(AdvancedRocketryBlocks.blockElectrolyser.setRegistryName("electrolyser"));
		LibVulpesBlocks.registerBlock(AdvancedRocketryBlocks.blockChemicalReactor.setRegistryName("chemicalReactor"));
		//T2 processing
		LibVulpesBlocks.registerBlock(AdvancedRocketryBlocks.blockPrecisionLaserEngraver.setRegistryName("precisionlaseretcher"));
		LibVulpesBlocks.registerBlock(AdvancedRocketryBlocks.blockObservatory.setRegistryName("observatory"));
		LibVulpesBlocks.registerBlock(AdvancedRocketryBlocks.blockPlanetAnalyser.setRegistryName("planetAnalyser"));
		LibVulpesBlocks.registerBlock(AdvancedRocketryBlocks.blockCentrifuge.setRegistryName("centrifuge"));
		LibVulpesBlocks.registerBlock(AdvancedRocketryBlocks.blockSatelliteBuilder.setRegistryName("satelliteBuilder"));
		//Energy
		LibVulpesBlocks.registerBlock(AdvancedRocketryBlocks.blockBlackHoleGenerator.setRegistryName("blackholegenerator"));
		LibVulpesBlocks.registerBlock(AdvancedRocketryBlocks.blockMicrowaveReciever.setRegistryName("microwaveReciever"));
		LibVulpesBlocks.registerBlock(AdvancedRocketryBlocks.blockSolarArray.setRegistryName("solararray"));
		//Aux/huge
		LibVulpesBlocks.registerBlock(AdvancedRocketryBlocks.blockWarpCore.setRegistryName("warpCore"));
		LibVulpesBlocks.registerBlock(AdvancedRocketryBlocks.blockBeacon.setRegistryName("beacon"));
		LibVulpesBlocks.registerBlock(AdvancedRocketryBlocks.blockBiomeScanner.setRegistryName("biomeScanner"));
		LibVulpesBlocks.registerBlock(AdvancedRocketryBlocks.blockRailgun .setRegistryName("railgun"));
		LibVulpesBlocks.registerBlock(AdvancedRocketryBlocks.blockSpaceElevatorController.setRegistryName("spaceElevatorController"));
		//Configurable stuff
		if (ARConfiguration.getCurrentConfig().enableTerraforming)
			LibVulpesBlocks.registerBlock(AdvancedRocketryBlocks.blockAtmosphereTerraformer.setRegistryName("terraformer"));
		if(zmaster587.advancedRocketry.api.ARConfiguration.getCurrentConfig().enableGravityController)
			LibVulpesBlocks.registerBlock(AdvancedRocketryBlocks.blockGravityMachine.setRegistryName("gravityMachine"));
		if(zmaster587.advancedRocketry.api.ARConfiguration.getCurrentConfig().enableLaserDrill)
			LibVulpesBlocks.registerBlock(AdvancedRocketryBlocks.blockSpaceLaser.setRegistryName("spaceLaser"));
		//Docking blocks
		LibVulpesBlocks.registerBlock(AdvancedRocketryBlocks.blockLaunchpad.setRegistryName("launchpad"));
		LibVulpesBlocks.registerBlock(AdvancedRocketryBlocks.blockLandingPad.setRegistryName("landingPad"));
		LibVulpesBlocks.registerBlock(AdvancedRocketryBlocks.blockDockingPort.setRegistryName("stationMarker"));
		//Rocket blocks
		LibVulpesBlocks.registerBlock(AdvancedRocketryBlocks.blockGenericSeat.setRegistryName("seat"));
		LibVulpesBlocks.registerBlock(AdvancedRocketryBlocks.blockEngine.setRegistryName("rocketmotor"));
		LibVulpesBlocks.registerBlock(AdvancedRocketryBlocks.blockBipropellantEngine.setRegistryName("bipropellantrocketmotor"));
		LibVulpesBlocks.registerBlock(AdvancedRocketryBlocks.blockAdvEngine.setRegistryName("advRocketmotor"));
		LibVulpesBlocks.registerBlock(AdvancedRocketryBlocks.blockAdvBipropellantEngine.setRegistryName("advbipropellantRocketmotor"));
		LibVulpesBlocks.registerBlock(AdvancedRocketryBlocks.blockNuclearEngine.setRegistryName("nuclearrocketmotor"));
		LibVulpesBlocks.registerBlock(AdvancedRocketryBlocks.blockFuelTank.setRegistryName("fuelTank"));
		LibVulpesBlocks.registerBlock(AdvancedRocketryBlocks.blockBipropellantFuelTank.setRegistryName("bipropellantfueltank"));
		LibVulpesBlocks.registerBlock(AdvancedRocketryBlocks.blockOxidizerFuelTank.setRegistryName("oxidizerfueltank"));
		LibVulpesBlocks.registerBlock(AdvancedRocketryBlocks.blockNuclearFuelTank.setRegistryName("nuclearfueltank"));
		LibVulpesBlocks.registerBlock(AdvancedRocketryBlocks.blockNuclearCore.setRegistryName("nuclearcore"));
		LibVulpesBlocks.registerBlock(AdvancedRocketryBlocks.blockGuidanceComputer.setRegistryName("guidanceComputer"));
		LibVulpesBlocks.registerBlock(AdvancedRocketryBlocks.blockIntake.setRegistryName("intake"));
		LibVulpesBlocks.registerBlock(AdvancedRocketryBlocks.blockDrill.setRegistryName("drill"));
		LibVulpesBlocks.registerBlock(AdvancedRocketryBlocks.blockLandingFloat.setRegistryName("landingfloat"));
		//Assembly machines
		LibVulpesBlocks.registerBlock(AdvancedRocketryBlocks.blockRocketBuilder.setRegistryName("rocketBuilder"));
		LibVulpesBlocks.registerBlock(AdvancedRocketryBlocks.blockStationBuilder.setRegistryName("stationBuilder"));
		LibVulpesBlocks.registerBlock(AdvancedRocketryBlocks.blockDeployableRocketBuilder.setRegistryName("deployableRocketBuilder"));
		//Infrastructure machines
		LibVulpesBlocks.registerBlock(AdvancedRocketryBlocks.blockLoader.setRegistryName("loader"), ItemBlockMeta.class, false);
		LibVulpesBlocks.registerBlock(AdvancedRocketryBlocks.blockFuelingStation.setRegistryName("fuelingStation"));
		LibVulpesBlocks.registerBlock(AdvancedRocketryBlocks.blockMonitoringStation.setRegistryName("monitoringStation"));
		LibVulpesBlocks.registerBlock(AdvancedRocketryBlocks.blockSatelliteControlCenter.setRegistryName("satelliteControlCenter"));
		//Station machines
		LibVulpesBlocks.registerBlock(AdvancedRocketryBlocks.blockWarpShipMonitor.setRegistryName("warpMonitor"));
		LibVulpesBlocks.registerBlock(AdvancedRocketryBlocks.blockOrientationController.setRegistryName("orientationController"));
		LibVulpesBlocks.registerBlock(AdvancedRocketryBlocks.blockGravityController.setRegistryName("gravityController"));
		LibVulpesBlocks.registerBlock(AdvancedRocketryBlocks.blockAltitudeController.setRegistryName("altitudeController"));
		LibVulpesBlocks.registerBlock(AdvancedRocketryBlocks.blockPlanetSelector.setRegistryName("planetSelector"));
		LibVulpesBlocks.registerBlock(AdvancedRocketryBlocks.blockPlanetHoloSelector.setRegistryName("planetHoloSelector"));
		//Oxygen machines
		LibVulpesBlocks.registerBlock(AdvancedRocketryBlocks.blockCO2Scrubber.setRegistryName("oxygenScrubber"));
		LibVulpesBlocks.registerBlock(AdvancedRocketryBlocks.blockOxygenVent.setRegistryName("oxygenVent"));
		LibVulpesBlocks.registerBlock(AdvancedRocketryBlocks.blockOxygenCharger.setRegistryName("oxygenCharger"));
		LibVulpesBlocks.registerBlock(AdvancedRocketryBlocks.blockOxygenDetection.setRegistryName("oxygenDetection"));
		LibVulpesBlocks.registerBlock(AdvancedRocketryBlocks.blockPipeSealer.setRegistryName("pipeSealer"));
		LibVulpesBlocks.registerBlock(AdvancedRocketryBlocks.blockAirLock.setRegistryName("airlock_door"));
		//Light sources
		LibVulpesBlocks.registerBlock(AdvancedRocketryBlocks.blockUnlitTorch.setRegistryName("unlitTorch"));
		LibVulpesBlocks.registerBlock(AdvancedRocketryBlocks.blockThermiteTorch.setRegistryName("thermiteTorch"));
		LibVulpesBlocks.registerBlock(AdvancedRocketryBlocks.blockCircleLight.setRegistryName("circleLight"));
		LibVulpesBlocks.registerBlock(AdvancedRocketryBlocks.blockLightSource.setRegistryName("lightSource"));
		LibVulpesBlocks.registerBlock(AdvancedRocketryBlocks.blockRocketFire.setRegistryName("rocketfire"), null, false);
		//Worldgen
		LibVulpesBlocks.registerBlock(AdvancedRocketryBlocks.blockMoonTurf.setRegistryName("moonTurf"));
		LibVulpesBlocks.registerBlock(AdvancedRocketryBlocks.blockMoonTurfDark.setRegistryName("moonTurf_dark"));
		LibVulpesBlocks.registerBlock(AdvancedRocketryBlocks.blockHotTurf.setRegistryName("hotTurf"));
		LibVulpesBlocks.registerBlock(AdvancedRocketryBlocks.blockBasalt.setRegistryName("basalt"));
		LibVulpesBlocks.registerBlock(AdvancedRocketryBlocks.blocksGeode.setRegistryName("geode"));
		LibVulpesBlocks.registerBlock(AdvancedRocketryBlocks.blockCrystal.setRegistryName("crystal"), ItemBlockCrystal.class, true );
		LibVulpesBlocks.registerBlock(AdvancedRocketryBlocks.blockVitrifiedSand.setRegistryName("vitrifiedSand"));
		LibVulpesBlocks.registerBlock(AdvancedRocketryBlocks.blockCharcoalLog.setRegistryName("charcoalLog"));
		LibVulpesBlocks.registerBlock(AdvancedRocketryBlocks.blockElectricMushroom.setRegistryName("electricMushroom"));
		LibVulpesBlocks.registerBlock(AdvancedRocketryBlocks.blockLightwoodWood.setRegistryName("alienWood"));
		LibVulpesBlocks.registerBlock(AdvancedRocketryBlocks.sblockLightwoodLeaves.setRegistryName("alienLeaves"));
		LibVulpesBlocks.registerBlock(AdvancedRocketryBlocks.blockLightwoodSapling.setRegistryName("alienSapling"));
		LibVulpesBlocks.registerBlock(AdvancedRocketryBlocks.blockLightwoodPlanks.setRegistryName("planks"));
		//Fluids
		LibVulpesBlocks.registerBlock(AdvancedRocketryBlocks.blockOxygenFluid.setRegistryName("oxygenFluid"), null, false);
		LibVulpesBlocks.registerBlock(AdvancedRocketryBlocks.blockHydrogenFluid.setRegistryName("hydrogenFluid"), null, false);
		LibVulpesBlocks.registerBlock(AdvancedRocketryBlocks.blockNitrogenFluid.setRegistryName("nitrogenFluid"), null, false);
		LibVulpesBlocks.registerBlock(AdvancedRocketryBlocks.blockFuelFluid.setRegistryName("rocketFuel"), null, false);
		LibVulpesBlocks.registerBlock(AdvancedRocketryBlocks.blockEnrichedLavaFluid.setRegistryName("enrichedLavaFluid"), null, false);


		
		//Register Allowed Products
		materialRegistry.registerMaterial(new zmaster587.libVulpes.api.material.Material("TitaniumAluminide", "pickaxe", 1, 0xaec2de, AllowedProducts.getProductByName("PLATE").getFlagValue() | AllowedProducts.getProductByName("INGOT").getFlagValue() | AllowedProducts.getProductByName("NUGGET").getFlagValue() | AllowedProducts.getProductByName("DUST").getFlagValue() | AllowedProducts.getProductByName("STICK").getFlagValue() | AllowedProducts.getProductByName("BLOCK").getFlagValue() | AllowedProducts.getProductByName("GEAR").getFlagValue() | AllowedProducts.getProductByName("SHEET").getFlagValue(), false));
		materialRegistry.registerMaterial(new zmaster587.libVulpes.api.material.Material("TitaniumIridium", "pickaxe", 1, 0xd7dfe4, AllowedProducts.getProductByName("PLATE").getFlagValue() | AllowedProducts.getProductByName("INGOT").getFlagValue() | AllowedProducts.getProductByName("NUGGET").getFlagValue() | AllowedProducts.getProductByName("DUST").getFlagValue() | AllowedProducts.getProductByName("STICK").getFlagValue() | AllowedProducts.getProductByName("BLOCK").getFlagValue() | AllowedProducts.getProductByName("GEAR").getFlagValue() | AllowedProducts.getProductByName("SHEET").getFlagValue(), false));

		materialRegistry.registerOres(LibVulpes.tabLibVulpesOres);
		
        //OreDict stuff
        OreDictionary.registerOre("turfMoon", new ItemStack(AdvancedRocketryBlocks.blockMoonTurf));
        OreDictionary.registerOre("turfMoon", new ItemStack(AdvancedRocketryBlocks.blockMoonTurfDark));
        OreDictionary.registerOre("logWood", new ItemStack(AdvancedRocketryBlocks.blockLightwoodWood));
        OreDictionary.registerOre("plankWood", new ItemStack(AdvancedRocketryBlocks.blockLightwoodPlanks));
        OreDictionary.registerOre("treeLeaves", new ItemStack(AdvancedRocketryBlocks.sblockLightwoodLeaves));
        OreDictionary.registerOre("treeSapling", new ItemStack(AdvancedRocketryBlocks.blockLightwoodSapling));
        OreDictionary.registerOre("concrete", new ItemStack(AdvancedRocketryBlocks.blockConcrete));
		OreDictionary.registerOre("casingCentrifuge", new ItemStack(LibVulpesBlocks.blockAdvStructureBlock));
	}

	@SideOnly(Side.CLIENT)
	@SubscribeEvent
	public void registerModels(ModelRegistryEvent event) {
		proxy.preInitItems();
		proxy.preInitBlocks();
	}
	
    @SubscribeEvent
    public void registerRecipes(RegistryEvent<IRecipe> evt)
    {
        GameRegistry.addSmelting(MaterialRegistry.getMaterialFromName("Dilithium").getProduct(AllowedProducts.getProductByName("ORE")), MaterialRegistry.getMaterialFromName("Dilithium").getProduct(AllowedProducts.getProductByName("DUST")), 0);

        //Register the machine recipes
        machineRecipes.registerAllMachineRecipes();
    }

	@EventHandler
	public void load(FMLInitializationEvent event)
	{
		ARAdvancements.register();
		proxy.init();

		zmaster587.advancedRocketry.cable.NetworkRegistry.registerFluidNetwork();

		//Register Alloys
		MaterialRegistry.registerMixedMaterial(new MixedMaterial(TileElectricArcFurnace.class, "oreRutile", new ItemStack[] {MaterialRegistry.getMaterialFromName("Titanium").getProduct(AllowedProducts.getProductByName("INGOT"))}));


		NetworkRegistry.INSTANCE.registerGuiHandler(this, new GuiHandler());

		NetworkRegistry.INSTANCE.registerGuiHandler(this, new zmaster587.advancedRocketry.inventory.GuiHandler());
		planetWorldType = new WorldTypePlanetGen("PlanetCold");
		spaceWorldType = new WorldTypeSpace("Space");

		//Biomes --------------------------------------------------------------------------------------

		String[] biomeBlackList = config.getStringList("BlacklistedBiomes", "Planet", new String[] {Biomes.RIVER.getRegistryName().toString(), Biomes.SKY.getRegistryName().toString(), Biomes.HELL.getRegistryName().toString(), Biomes.VOID.getRegistryName().toString(), AdvancedRocketryBiomes.alienForest.getRegistryName().toString()}, "List of Biomes to be blacklisted from spawning as BiomeIds, default is: river, sky, hell, void, alienForest");
		String[] biomeHighPressure = config.getStringList("HighPressureBiomes", "Planet", new String[] { AdvancedRocketryBiomes.swampDeepBiome.getRegistryName().toString(), AdvancedRocketryBiomes.stormLandsBiome.getRegistryName().toString() }, "Biomes that only spawn on worlds with pressures over 125, will override blacklist.  Defaults: StormLands, DeepSwamp");
		String[] biomeSingle = config.getStringList("SingleBiomes", "Planet", new String[] { AdvancedRocketryBiomes.volcanicBarren.getRegistryName().toString(), AdvancedRocketryBiomes.swampDeepBiome.getRegistryName().toString(), AdvancedRocketryBiomes.crystalChasms.getRegistryName().toString(),  AdvancedRocketryBiomes.alienForest.getRegistryName().toString(), Biomes.DESERT_HILLS.getRegistryName().toString(), 
				Biomes.MUSHROOM_ISLAND.getRegistryName().toString(), Biomes.EXTREME_HILLS.getRegistryName().toString(), Biomes.ICE_PLAINS.getRegistryName().toString() }, "Some worlds have a chance of spawning single biomes contained in this list.  Defaults: deepSwamp, crystalChasms, alienForest, desert hills, mushroom island, extreme hills, ice plains");

		config.save();

		//Prevent these biomes from spawning normally
		AdvancedRocketryBiomes.instance.registerBlackListBiome(AdvancedRocketryBiomes.moonBiome);
		AdvancedRocketryBiomes.instance.registerBlackListBiome(AdvancedRocketryBiomes.moonBiomeDark);
		AdvancedRocketryBiomes.instance.registerBlackListBiome(AdvancedRocketryBiomes.hotDryBiome);
		AdvancedRocketryBiomes.instance.registerBlackListBiome(AdvancedRocketryBiomes.spaceBiome);
		AdvancedRocketryBiomes.instance.registerBlackListBiome(AdvancedRocketryBiomes.volcanic);

		//Read BlackList from config and register Blacklisted biomes
		for(String string : biomeBlackList) {
			try {
				Biome biome = AdvancedRocketryBiomes.getBiome(string);

				if(biome == null)
					logger.warn(String.format("Error blackListing biome  \"%s\", a biome with that ID does not exist!", string));
				else
					AdvancedRocketryBiomes.instance.registerBlackListBiome(biome);
			} catch (NumberFormatException e) {
				logger.warn("Error blackListing \"" + string + "\".  It is not a valid number or Biome ResourceLocation");
			}
		}

		if(zmaster587.advancedRocketry.api.ARConfiguration.getCurrentConfig().blackListAllVanillaBiomes) {
			AdvancedRocketryBiomes.instance.blackListVanillaBiomes();
		}


		//Read and Register High Pressure biomes from config
		for(String string : biomeHighPressure) {
			try {
				Biome biome = AdvancedRocketryBiomes.getBiome(string);

				if(biome == null)
					logger.warn(String.format("Error registering high pressure biome \"%s\", a biome with that ID does not exist!", string));
				else
					AdvancedRocketryBiomes.instance.registerHighPressureBiome(biome);
			} catch (NumberFormatException e) {
				logger.warn("Error registering high pressure biome \"" + string + "\".  It is not a valid number or Biome ResourceLocation");
			}
		}

		//Read and Register Single biomes from config
		for(String string : biomeSingle) {
			try {
				Biome biome = AdvancedRocketryBiomes.getBiome(string);

				if(biome == null)
					logger.warn(String.format("Error registering single biome \"%s\", a biome with that ID does not exist!", string));
				else
					AdvancedRocketryBiomes.instance.registerSingleBiome(biome);
			} catch (NumberFormatException e) {
				logger.warn("Error registering single biome \"" + string + "\".  It is not a valid number or Biome ResourceLocation");
			}
		}


		//Data mapping 'D'

		List<BlockMeta> list = new LinkedList<>();
		list.add(new BlockMeta(AdvancedRocketryBlocks.blockLoader, 0));
		list.add(new BlockMeta(AdvancedRocketryBlocks.blockLoader, 8));
		TileMultiBlock.addMapping('D', list);
		
		machineRecipes.createAutoGennedRecipes(modProducts);
	}



	@EventHandler
	public void postInit(FMLPostInitializationEvent event)
	{

		CapabilitySpaceArmor.register();
		//Need to raise the Max Entity Radius to allow player interaction with rockets
		World.MAX_ENTITY_RADIUS = 20;

		//Register multiblock items with the projector
		//Basic processing machines
		((ItemProjector)LibVulpesItems.itemHoloProjector).registerMachine(new TileElectricArcFurnace(), (BlockTile)AdvancedRocketryBlocks.blockArcFurnace);
		((ItemProjector)LibVulpesItems.itemHoloProjector).registerMachine(new TileRollingMachine(), (BlockTile)AdvancedRocketryBlocks.blockRollingMachine);
		((ItemProjector)LibVulpesItems.itemHoloProjector).registerMachine(new TileLathe(), (BlockTile)AdvancedRocketryBlocks.blockLathe);
		((ItemProjector)LibVulpesItems.itemHoloProjector).registerMachine(new TileCrystallizer(), (BlockTile)AdvancedRocketryBlocks.blockCrystallizer);
		((ItemProjector)LibVulpesItems.itemHoloProjector).registerMachine(new TileCuttingMachine(), (BlockTile)AdvancedRocketryBlocks.blockCuttingMachine);
		((ItemProjector)LibVulpesItems.itemHoloProjector).registerMachine(new TilePrecisionAssembler(), (BlockTile)AdvancedRocketryBlocks.blockPrecisionAssembler);
		((ItemProjector)LibVulpesItems.itemHoloProjector).registerMachine(new TileElectrolyser(), (BlockTile)AdvancedRocketryBlocks.blockElectrolyser);
		((ItemProjector)LibVulpesItems.itemHoloProjector).registerMachine(new TileChemicalReactor(), (BlockTile)AdvancedRocketryBlocks.blockChemicalReactor);
		//T2 processing machines
		((ItemProjector)LibVulpesItems.itemHoloProjector).registerMachine(new TilePrecisionLaserEtcher(), (BlockTile)AdvancedRocketryBlocks.blockPrecisionLaserEngraver);
		((ItemProjector)LibVulpesItems.itemHoloProjector).registerMachine(new TileObservatory(), (BlockTile)AdvancedRocketryBlocks.blockObservatory);
		((ItemProjector)LibVulpesItems.itemHoloProjector).registerMachine(new TileAstrobodyDataProcessor(), (BlockTile)AdvancedRocketryBlocks.blockPlanetAnalyser);
		((ItemProjector)LibVulpesItems.itemHoloProjector).registerMachine(new TileCentrifuge(), (BlockTile)AdvancedRocketryBlocks.blockCentrifuge);
		((ItemProjector)LibVulpesItems.itemHoloProjector).registerMachine(new TileSatelliteBuilder(), (BlockTile)AdvancedRocketryBlocks.blockSatelliteBuilder);
		//Power generation
		((ItemProjector)LibVulpesItems.itemHoloProjector).registerMachine(new TileBlackHoleGenerator(), (BlockTile)AdvancedRocketryBlocks.blockBlackHoleGenerator);
		((ItemProjector)LibVulpesItems.itemHoloProjector).registerMachine(new TileMicrowaveReciever(), (BlockTile)AdvancedRocketryBlocks.blockMicrowaveReciever);
		((ItemProjector)LibVulpesItems.itemHoloProjector).registerMachine(new TileSolarArray(), (BlockTile)AdvancedRocketryBlocks.blockSolarArray);
		//Auxillary machines
		((ItemProjector)LibVulpesItems.itemHoloProjector).registerMachine(new TileWarpCore(), (BlockTile)AdvancedRocketryBlocks.blockWarpCore);
		((ItemProjector)LibVulpesItems.itemHoloProjector).registerMachine(new TileBeacon(), (BlockTile)AdvancedRocketryBlocks.blockBeacon);
		((ItemProjector)LibVulpesItems.itemHoloProjector).registerMachine(new TileBiomeScanner(), (BlockTile)AdvancedRocketryBlocks.blockBiomeScanner);
		((ItemProjector)LibVulpesItems.itemHoloProjector).registerMachine(new TileRailgun(), (BlockTile)AdvancedRocketryBlocks.blockRailgun);
		((ItemProjector)LibVulpesItems.itemHoloProjector).registerMachine(new TileSpaceElevator(), (BlockTile)AdvancedRocketryBlocks.blockSpaceElevatorController);
		//Config-controlled machines
		if(ARConfiguration.getCurrentConfig().enableTerraforming)
		    ((ItemProjector)LibVulpesItems.itemHoloProjector).registerMachine(new TileAtmosphereTerraformer(), (BlockTile)AdvancedRocketryBlocks.blockAtmosphereTerraformer);
		if(zmaster587.advancedRocketry.api.ARConfiguration.getCurrentConfig().enableGravityController)
			((ItemProjector)LibVulpesItems.itemHoloProjector).registerMachine(new TileAreaGravityController(), (BlockTile)AdvancedRocketryBlocks.blockGravityMachine);
		if(zmaster587.advancedRocketry.api.ARConfiguration.getCurrentConfig().enableLaserDrill)
			((ItemProjector)LibVulpesItems.itemHoloProjector).registerMachine(new TileOrbitalLaserDrill(), (BlockTile)AdvancedRocketryBlocks.blockSpaceLaser);

		proxy.registerEventHandlers();
		proxy.registerKeyBindings();
		//TODO: debug
		//ClientCommandHandler.instance.registerCommand(new Debugger());

		PlanetEventHandler handle = new PlanetEventHandler();
		MinecraftForge.EVENT_BUS.register(handle);
		MinecraftForge.ORE_GEN_BUS.register(handle);

		CableTickHandler cable = new CableTickHandler();
		MinecraftForge.EVENT_BUS.register(cable);

		InputSyncHandler inputSync = new InputSyncHandler();
		MinecraftForge.EVENT_BUS.register(inputSync);

		MinecraftForge.EVENT_BUS.register(new MapGenLander());
		AdvancedRocketryAPI.gravityManager = new GravityHandler();

		// Compat stuff
		if(Loader.isModLoaded("galacticraftcore") && zmaster587.advancedRocketry.api.ARConfiguration.getCurrentConfig().overrideGCAir) {
			GalacticCraftHandler eventHandler = new GalacticCraftHandler();
			MinecraftForge.EVENT_BUS.register(eventHandler);
			if(event.getSide().isClient())
				FMLCommonHandler.instance().bus().register(eventHandler);
		}
		CompatibilityMgr.isSpongeInstalled = Loader.isModLoaded("sponge");
		// End compat stuff

		MinecraftForge.EVENT_BUS.register(SpaceObjectManager.getSpaceManager());

		PacketHandler.init();

		GameRegistry.registerWorldGenerator(new OreGenerator(), 100);

		ForgeChunkManager.setForcedChunkLoadingCallback(instance, new WorldEvents());

		
		//Register mixed material's recipes
		for(MixedMaterial material : MaterialRegistry.getMixedMaterialList()) {
			RecipesMachine.getInstance().addRecipe(material.getMachine(), material.getProducts(), 100, 10, material.getInput());
		}

		//Register space dimension
		net.minecraftforge.common.DimensionManager.registerDimension(zmaster587.advancedRocketry.api.ARConfiguration.getCurrentConfig().spaceDimId, DimensionManager.spaceDimensionType);

		ARConfiguration.loadPostInit();
		//TODO recipes?
		machineRecipes.registerXMLRecipes();

		//Add the overworld as a discovered planet
		zmaster587.advancedRocketry.api.ARConfiguration.getCurrentConfig().initiallyKnownPlanets.add(0);
	}

	@EventHandler
	public void serverStarted(FMLServerStartedEvent event) {
		for (int dimId : DimensionManager.getInstance().getLoadedDimensions()) {
			DimensionProperties properties = DimensionManager.getInstance().getDimensionProperties(dimId);
			if(!properties.isNativeDimension && properties.getId() == zmaster587.advancedRocketry.api.ARConfiguration.getCurrentConfig().MoonId && !Loader.isModLoaded("GalacticraftCore")) {
				properties.isNativeDimension = true;
			}
		}
	}

	@EventHandler
	public void serverStarting(FMLServerStartingEvent event) {
		event.registerServerCommand(new WorldCommand());

		
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
				stream.write("<Asteroids>"
						+ "\n\t<asteroid name=\"Small Asteroid\" distance=\"10\" mass=\"200\" massVariability=\"0.5\" minLevel=\"0\" probability=\"20\" richness=\"0.3\" richnessVariability=\"0.5\">"
						+ "\n\t\t<ore itemStack=\"minecraft:iron_ore\" chance=\"15\" />"
						+ "\n\t\t<ore itemStack=\"minecraft:gold_ore\" chance=\"10\" />"
						+ "\n\t\t<ore itemStack=\"minecraft:redstone_ore\" chance=\"10\" />"
						+ "\n\t</asteroid>"
						+ "\n\t<asteroid name=\"Light Asteroid\" distance=\"60\" mass=\"200\" massVariability=\"0.5\" minLevel=\"0\" probability=\"15\" richness=\"0.2\" richnessVariability=\"0.5\">"
						+ "\n\t\t<ore itemStack=\"libvulpes:ore0;9\" chance=\"20\" />"
						+ "\n\t\t<ore itemStack=\"libvulpes:ore0;8\" chance=\"10\" />"
						+ "\n\t\t<ore itemStack=\"minecraft:quartz_block\" chance=\"5\" />"
						+ "\n\t</asteroid>"
						+ "\n\t<asteroid name=\"Iridium Enriched asteroid\" distance=\"100\" mass=\"75\" massVariability=\"0.5\" minLevel=\"0\" probability=\"2\" richness=\"0.2\" richnessVariability=\"0.3\">"
						+ "\n\t\t<ore itemStack=\"minecraft:iron_ore\" chance=\"25\" />"
						+ "\n\t\t<ore itemStack=\"libvulpes:ore0 10\" chance=\"5\" />"
						+ "\n\t</asteroid>"
						+ "\n\t<asteroid name=\"Strange Asteroid\" distance=\"120\" mass=\"50\" massVariability=\"0.5\" minLevel=\"0\" probability=\"1\" richness=\"0.2\" richnessVariability=\"0.5\">"
						+ "\n\t\t<ore itemStack=\"libvulpes:ore0;0\" chance=\"20\" />"
						+ "\n\t\t<ore itemStack=\"minecraft:emerald_ore\" chance=\"5\" />"
						+ "\n\t</asteroid>"
						+ "\n</Asteroids>");
				stream.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		XMLAsteroidLoader load = new XMLAsteroidLoader();
		try {
			if(load.loadFile(file)) {
				for (Asteroid asteroid : load.loadPropertyFile()) {
					zmaster587.advancedRocketry.api.ARConfiguration.getCurrentConfig().asteroidTypes.put(asteroid.ID, asteroid);
				}
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
				if(oreLoader.loadFile(file)) {
					List<SingleEntry<HashedBlockPosition, OreGenProperties>> mapping = oreLoader.loadPropertyFile();

					for (Entry<HashedBlockPosition, OreGenProperties> entry : mapping) {
						int pressure = entry.getKey().x;
						int temp = entry.getKey().y;

						if (pressure == -1) {
							if (temp != -1) {
								OreGenProperties.setOresForTemperature(Temps.values()[temp], entry.getValue());
							}
						} else if (temp == -1) {
							OreGenProperties.setOresForPressure(AtmosphereTypes.values()[pressure], entry.getValue());
						} else {
							OreGenProperties.setOresForPressureAndTemp(AtmosphereTypes.values()[pressure], Temps.values()[temp], entry.getValue());
						}
					}
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		//End open and load ore files

		DimensionManager.getInstance().createAndLoadDimensions(resetFromXml);
	}


	@EventHandler
	public void serverStopped(FMLServerStoppedEvent event) {
		zmaster587.advancedRocketry.dimension.DimensionManager.getInstance().onServerStopped();
		//zmaster587.advancedRocketry.cable.NetworkRegistry.clearNetworks();
		SpaceObjectManager.getSpaceManager().onServerStopped();
		zmaster587.advancedRocketry.api.ARConfiguration.getCurrentConfig().MoonId = Constants.INVALID_PLANET;
		((BlockSeal)AdvancedRocketryBlocks.blockPipeSealer).clearMap();
		DimensionManager.dimOffset = config.getInt("minDimension", "Planet", 2, -127, 8000, "Dimensions including and after this number are allowed to be made into planets");
		zmaster587.advancedRocketry.api.ARConfiguration.getCurrentConfig().spaceDimId = config.get(Configuration.CATEGORY_GENERAL,"spaceStationId" , -2,"Dimension ID to use for space stations").getInt();
	}

	@SubscribeEvent
	public void registerOre(OreRegisterEvent event) {

		//Register ore products
		if(!zmaster587.advancedRocketry.api.ARConfiguration.getCurrentConfig().allowMakingItemsForOtherMods)
			return;

		for(AllowedProducts product : AllowedProducts.getAllAllowedProducts() ) {
			if(event.getName().startsWith(product.name().toLowerCase(Locale.ENGLISH))) {
				HashSet<String> list = modProducts.computeIfAbsent(product, k -> new HashSet<>());
				list.add(event.getName().substring(product.name().length()));
			}
		}
	}
}
