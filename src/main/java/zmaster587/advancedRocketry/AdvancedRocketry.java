package zmaster587.advancedRocketry;

import com.google.common.collect.Lists;

import net.minecraft.block.Block;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.material.MaterialLiquid;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.item.EntityArmorStand;
import net.minecraft.init.Biomes;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
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
import net.minecraft.world.biome.Biome.SpawnListEntry;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.common.BiomeDictionary;
import net.minecraftforge.common.ForgeChunkManager;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.event.RegistryEvent.MissingMappings.Action;
import net.minecraftforge.event.RegistryEvent.MissingMappings.Mapping;
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
import net.minecraftforge.oredict.ShapedOreRecipe;
import net.minecraftforge.oredict.ShapelessOreRecipe;
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
import zmaster587.advancedRocketry.block.plant.BlockAlienPlank;
import zmaster587.advancedRocketry.block.plant.BlockAlienSapling;
import zmaster587.advancedRocketry.block.plant.BlockAlienWood;
import zmaster587.advancedRocketry.capability.CapabilityProtectiveArmor;
import zmaster587.advancedRocketry.command.WorldCommand;
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
import zmaster587.libVulpes.block.BlockAlphaTexture;
import zmaster587.libVulpes.block.BlockMeta;
import zmaster587.libVulpes.block.BlockMotor;
import zmaster587.libVulpes.block.BlockTile;
import zmaster587.libVulpes.block.multiblock.BlockMultiBlockComponentVisible;
import zmaster587.libVulpes.block.multiblock.BlockMultiblockMachine;
import zmaster587.libVulpes.event.BucketHandler;
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

import java.io.*;
import java.util.*;
import java.util.Map.Entry;


@Mod(modid="advancedrocketry", name="Advanced Rocketry", version= AdvancedRocketry.MODVERSION, dependencies="required-after:libvulpes@[%LIBVULPESVERSION%,)")
public class AdvancedRocketry {


	@SidedProxy(clientSide="zmaster587.advancedRocketry.client.ClientProxy", serverSide="zmaster587.advancedRocketry.common.CommonProxy")
	public static CommonProxy proxy;

	public final static String MODVERSION = "MAJOR + MINOR + REVIS + BUILD";
        public static String MAJOR = "@MAJOR@.";
        public static String MINOR = "@MINOR@.";
        public static String REVIS = "@REVIS@.";
        public static String BUILD = "@BUILD@";

	@Instance(value = Constants.modId)
	public static AdvancedRocketry instance;
	public static WorldType planetWorldType;
	public static WorldType spaceWorldType;
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

	public static CompatibilityMgr compat = new CompatibilityMgr();
	public static Logger logger = LogManager.getLogger(Constants.modId);
	private static Configuration config;
	private static final String BIOMECATETORY = "Biomes";
	private boolean resetFromXml;
	
	//static {
	//	FluidRegistry.enableUniversalBucket(); // Must be called before preInit
	//}

	public static MaterialRegistry materialRegistry = new MaterialRegistry(); 

	public static HashMap<AllowedProducts, HashSet<String>> modProducts = new HashMap<AllowedProducts, HashSet<String>>();


	private static CreativeTabs tabAdvRocketry = new CreativeTabs("advancedRocketry") {
		@Override
		public ItemStack getTabIconItem() {
			return new ItemStack(AdvancedRocketryItems.itemSatelliteIdChip);
		}
	};
	
	//Biome registry.
	@SubscribeEvent
	public void register(RegistryEvent.Register<Biome> evt)
	{
	    System.out.println("REGISTERING BIOMES");
        AdvancedRocketryBiomes.moonBiome = new BiomeGenMoon();
        AdvancedRocketryBiomes.alienForest = new BiomeGenAlienForest();
        AdvancedRocketryBiomes.hotDryBiome = new BiomeGenHotDryRock();
        AdvancedRocketryBiomes.spaceBiome = new BiomeGenSpace();
        AdvancedRocketryBiomes.stormLandsBiome = new BiomeGenStormland();
        AdvancedRocketryBiomes.crystalChasms = new BiomeGenCrystal();
        AdvancedRocketryBiomes.swampDeepBiome = new BiomeGenDeepSwamp();
        AdvancedRocketryBiomes.marsh = new BiomeGenMarsh();
        AdvancedRocketryBiomes.oceanSpires = new BiomeGenOceanSpires();
        AdvancedRocketryBiomes.moonBiomeDark = new BiomeGenMoonDark();
        AdvancedRocketryBiomes.volcanic = new BiomeGenVolcanic();
        AdvancedRocketryBiomes.volcanicBarren = new BiomeGenBarrenVolcanic();

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
		SatelliteRegistry.registerSatellite("solar", SatelliteEnergy.class);
		SatelliteRegistry.registerSatellite("density", SatelliteDensity.class);
		SatelliteRegistry.registerSatellite("composition", SatelliteComposition.class);
		SatelliteRegistry.registerSatellite("mass", SatelliteMassScanner.class);
		SatelliteRegistry.registerSatellite("asteroidMiner", MissionOreMining.class);
		SatelliteRegistry.registerSatellite("gasMining", MissionGasCollection.class);
		SatelliteRegistry.registerSatellite("solarEnergy", SatelliteEnergy.class);
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
		GameRegistry.registerTileEntity(TileRocketBuilder.class, "ARrocketBuilder");
		GameRegistry.registerTileEntity(TileWarpCore.class, "ARwarpCore");
		//GameRegistry.registerTileEntity(TileModelRender.class, "ARmodelRenderer");
		GameRegistry.registerTileEntity(TileEntityFuelingStation.class, "ARfuelingStation");
		GameRegistry.registerTileEntity(TileEntityMoniteringStation.class, "ARmonitoringStation");
		//GameRegistry.registerTileEntity(TileMissionController.class, "ARmissionControlComp");
		GameRegistry.registerTileEntity(TileSpaceLaser.class, "ARspaceLaser");
		GameRegistry.registerTileEntity(TilePrecisionAssembler.class, "ARprecisionAssembler");
		GameRegistry.registerTileEntity(TileObservatory.class, "ARobservatory");
		GameRegistry.registerTileEntity(TileCrystallizer.class, "ARcrystallizer");
		GameRegistry.registerTileEntity(TileCuttingMachine.class, "ARcuttingmachine");
		GameRegistry.registerTileEntity(TileDataBus.class, "ARdataBus");
		GameRegistry.registerTileEntity(TileSatelliteHatch.class, "ARsatelliteHatch");
		GameRegistry.registerTileEntity(TileGuidanceComputerHatch.class, "ARguidanceComputerHatch");
		GameRegistry.registerTileEntity(TileSatelliteBuilder.class, "ARsatelliteBuilder");
		GameRegistry.registerTileEntity(TileEntitySatelliteControlCenter.class, "ARTileEntitySatelliteControlCenter");
		GameRegistry.registerTileEntity(TileAstrobodyDataProcessor.class, "ARplanetAnalyser");
		GameRegistry.registerTileEntity(TileGuidanceComputer.class, "ARguidanceComputer");
		GameRegistry.registerTileEntity(TileElectricArcFurnace.class, "ARelectricArcFurnace");
		GameRegistry.registerTileEntity(TilePlanetSelector.class, "ARTilePlanetSelector");
		//GameRegistry.registerTileEntity(TileModelRenderRotatable.class, "ARTileModelRenderRotatable");
		GameRegistry.registerTileEntity(TileMaterial.class, "ARTileMaterial");
		GameRegistry.registerTileEntity(TileLathe.class, "ARTileLathe");
		GameRegistry.registerTileEntity(TileRollingMachine.class, "ARTileMetalBender");
		GameRegistry.registerTileEntity(TileStationBuilder.class, "ARStationBuilder");
		GameRegistry.registerTileEntity(TileElectrolyser.class, "ARElectrolyser");
		GameRegistry.registerTileEntity(TileChemicalReactor.class, "ARChemicalReactor");
		GameRegistry.registerTileEntity(TileOxygenVent.class, "AROxygenVent");
		GameRegistry.registerTileEntity(TileOxygenCharger.class, "AROxygenCharger");
		GameRegistry.registerTileEntity(TileCO2Scrubber.class, "ARCO2Scrubber");
		GameRegistry.registerTileEntity(TileWarpShipMonitor.class, "ARStationMonitor");
		GameRegistry.registerTileEntity(TileAtmosphereDetector.class, "AROxygenDetector");
		GameRegistry.registerTileEntity(TileStationOrientationControl.class, "AROrientationControl");
		GameRegistry.registerTileEntity(TileStationGravityController.class, "ARGravityControl");
		GameRegistry.registerTileEntity(TileLiquidPipe.class, "ARLiquidPipe");
		GameRegistry.registerTileEntity(TileDataPipe.class, "ARDataPipe");
		GameRegistry.registerTileEntity(TileEnergyPipe.class, "AREnergyPipe");
		GameRegistry.registerTileEntity(TileDrill.class, "ARDrill");
		GameRegistry.registerTileEntity(TileMicrowaveReciever.class, "ARMicrowaveReciever");
		GameRegistry.registerTileEntity(TileSuitWorkStation.class, "ARSuitWorkStation");
		GameRegistry.registerTileEntity(TileRocketLoader.class, "ARRocketLoader");
		GameRegistry.registerTileEntity(TileRocketUnloader.class, "ARRocketUnloader");
		GameRegistry.registerTileEntity(TileBiomeScanner.class, "ARBiomeScanner");
		GameRegistry.registerTileEntity(TileAtmosphereTerraformer.class, "ARAttTerraformer");
		GameRegistry.registerTileEntity(TileLandingPad.class, "ARLandingPad");
		GameRegistry.registerTileEntity(TileStationDeployedAssembler.class, "ARStationDeployableRocketAssembler");
		GameRegistry.registerTileEntity(TileFluidTank.class, "ARFluidTank");
		GameRegistry.registerTileEntity(TileRocketFluidUnloader.class, "ARFluidUnloader");
		GameRegistry.registerTileEntity(TileRocketFluidLoader.class, "ARFluidLoader");
		GameRegistry.registerTileEntity(TileSolarPanel.class, "ARSolarGenerator");
		GameRegistry.registerTileEntity(TileDockingPort.class, "ARDockingPort");
		GameRegistry.registerTileEntity(TileStationAltitudeController.class, "ARStationAltitudeController");
		GameRegistry.registerTileEntity(TileRailgun.class, "ARRailgun");
		GameRegistry.registerTileEntity(TilePlanetaryHologram.class, "ARplanetHoloSelector");
		GameRegistry.registerTileEntity(TileForceFieldProjector.class, "ARForceFieldProjector");
		GameRegistry.registerTileEntity(TileSeal.class, "ARBlockSeal");
		GameRegistry.registerTileEntity(TileSpaceElevator.class, "ARSpaceElevator");
		GameRegistry.registerTileEntity(TileBeacon.class, "ARBeacon");
		GameRegistry.registerTileEntity(TileWirelessTransciever.class, "ARTransciever");
		GameRegistry.registerTileEntity(TileBlackHoleGenerator.class, "ARblackholegenerator");
		GameRegistry.registerTileEntity(TilePump.class, new ResourceLocation(Constants.modId, "ARpump"));
		GameRegistry.registerTileEntity(TileCentrifuge.class, new ResourceLocation(Constants.modId, "ARCentrifuge"));
		
		if(zmaster587.advancedRocketry.api.ARConfiguration.getCurrentConfig().enableGravityController)
			GameRegistry.registerTileEntity(TileGravityController.class, "ARGravityMachine");


		//Register machine recipes
		LibVulpes.registerRecipeHandler(TileCuttingMachine.class, event.getModConfigurationDirectory().getAbsolutePath() + "/" + zmaster587.advancedRocketry.api.ARConfiguration.configFolder + "/CuttingMachine.xml");
		LibVulpes.registerRecipeHandler(TilePrecisionAssembler.class, event.getModConfigurationDirectory().getAbsolutePath() + "/" + zmaster587.advancedRocketry.api.ARConfiguration.configFolder + "/PrecisionAssembler.xml");
		LibVulpes.registerRecipeHandler(TileChemicalReactor.class, event.getModConfigurationDirectory().getAbsolutePath() + "/" + zmaster587.advancedRocketry.api.ARConfiguration.configFolder + "/ChemicalReactor.xml");
		LibVulpes.registerRecipeHandler(TileCrystallizer.class, event.getModConfigurationDirectory().getAbsolutePath() + "/" + zmaster587.advancedRocketry.api.ARConfiguration.configFolder + "/Crystallizer.xml");
		LibVulpes.registerRecipeHandler(TileElectrolyser.class, event.getModConfigurationDirectory().getAbsolutePath() + "/" + zmaster587.advancedRocketry.api.ARConfiguration.configFolder + "/Electrolyser.xml");
		LibVulpes.registerRecipeHandler(TileElectricArcFurnace.class, event.getModConfigurationDirectory().getAbsolutePath() + "/" + zmaster587.advancedRocketry.api.ARConfiguration.configFolder + "/ElectricArcFurnace.xml");
		LibVulpes.registerRecipeHandler(TileLathe.class, event.getModConfigurationDirectory().getAbsolutePath() + "/" + zmaster587.advancedRocketry.api.ARConfiguration.configFolder + "/Lathe.xml");
		LibVulpes.registerRecipeHandler(TileRollingMachine.class, event.getModConfigurationDirectory().getAbsolutePath() + "/" + zmaster587.advancedRocketry.api.ARConfiguration.configFolder + "/RollingMachine.xml");
		LibVulpes.registerRecipeHandler(BlockPress.class, event.getModConfigurationDirectory().getAbsolutePath() + "/" + zmaster587.advancedRocketry.api.ARConfiguration.configFolder + "/SmallPlatePress.xml");
		LibVulpes.registerRecipeHandler(TileCentrifuge.class, event.getModConfigurationDirectory().getAbsolutePath() + "/" + zmaster587.advancedRocketry.api.ARConfiguration.configFolder + "/Centrifuge.xml");


		
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
		AdvancedRocketryItems.itemBucketRocketFuel = new ItemARBucket(AdvancedRocketryFluids.fluidRocketFuel).setCreativeTab(LibVulpes.tabLibVulpesOres).setUnlocalizedName("bucketRocketFuel").setContainerItem(Items.BUCKET);
		AdvancedRocketryItems.itemBucketNitrogen = new ItemARBucket(AdvancedRocketryFluids.fluidNitrogen).setCreativeTab(LibVulpes.tabLibVulpesOres).setUnlocalizedName("bucketNitrogen").setContainerItem(Items.BUCKET);
		AdvancedRocketryItems.itemBucketHydrogen = new ItemARBucket(AdvancedRocketryFluids.fluidHydrogen).setCreativeTab(LibVulpes.tabLibVulpesOres).setUnlocalizedName("bucketHydrogen").setContainerItem(Items.BUCKET);
		AdvancedRocketryItems.itemBucketOxygen = new ItemARBucket(AdvancedRocketryFluids.fluidOxygen).setCreativeTab(LibVulpes.tabLibVulpesOres).setUnlocalizedName("bucketOxygen").setContainerItem(Items.BUCKET);
		AdvancedRocketryItems.itemBucketEnrichedLava = new ItemARBucket(AdvancedRocketryFluids.fluidEnrichedLava).setCreativeTab(LibVulpes.tabLibVulpesOres).setUnlocalizedName("bucketEnrichedLava").setContainerItem(Items.BUCKET);
		//FluidRegistry.addBucketForFluid(AdvancedRocketryFluids.fluidHydrogen);
		//FluidRegistry.addBucketForFluid(AdvancedRocketryFluids.fluidNitrogen);
		//FluidRegistry.addBucketForFluid(AdvancedRocketryFluids.fluidOxygen);
		//FluidRegistry.addBucketForFluid(AdvancedRocketryFluids.fluidRocketFuel);

		//Suit Component Registration
		AdvancedRocketryItems.itemJetpack = new ItemJetpack().setCreativeTab(tabAdvRocketry).setUnlocalizedName("jetPack");
		AdvancedRocketryItems.itemPressureTank = new ItemPressureTank(4, 1000).setCreativeTab(tabAdvRocketry).setUnlocalizedName("advancedrocketry:pressureTank");
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

		//Note: not registered
		AdvancedRocketryItems.itemAstroBed = new ItemAstroBed();

		//Register Satellite Properties
		SatelliteRegistry.registerSatelliteProperty(new ItemStack(AdvancedRocketryItems.itemSatellitePrimaryFunction, 1, 0), new SatelliteProperties().setSatelliteType(SatelliteRegistry.getKey(SatelliteOptical.class)));
		SatelliteRegistry.registerSatelliteProperty(new ItemStack(AdvancedRocketryItems.itemSatellitePrimaryFunction, 1, 1), new SatelliteProperties().setSatelliteType(SatelliteRegistry.getKey(SatelliteComposition.class)));
		SatelliteRegistry.registerSatelliteProperty(new ItemStack(AdvancedRocketryItems.itemSatellitePrimaryFunction, 1, 2), new SatelliteProperties().setSatelliteType(SatelliteRegistry.getKey(SatelliteMassScanner.class)));
		SatelliteRegistry.registerSatelliteProperty(new ItemStack(AdvancedRocketryItems.itemSatellitePrimaryFunction, 1, 3), new SatelliteProperties().setSatelliteType(SatelliteRegistry.getKey(SatelliteEnergy.class)));
		SatelliteRegistry.registerSatelliteProperty(new ItemStack(AdvancedRocketryItems.itemSatellitePrimaryFunction, 1, 4), new SatelliteProperties().setSatelliteType(SatelliteRegistry.getKey(SatelliteOreMapping.class)));
		SatelliteRegistry.registerSatelliteProperty(new ItemStack(AdvancedRocketryItems.itemSatellitePrimaryFunction, 1, 5), new SatelliteProperties().setSatelliteType(SatelliteRegistry.getKey(SatelliteBiomeChanger.class)));
		SatelliteRegistry.registerSatelliteProperty(new ItemStack(AdvancedRocketryItems.itemSatellitePowerSource,1,0), new SatelliteProperties().setPowerGeneration(1));
		SatelliteRegistry.registerSatelliteProperty(new ItemStack(AdvancedRocketryItems.itemSatellitePowerSource,1,1), new SatelliteProperties().setPowerGeneration(10));
		SatelliteRegistry.registerSatelliteProperty(new ItemStack(LibVulpesItems.itemBattery, 1, 0), new SatelliteProperties().setPowerStorage(100));
		SatelliteRegistry.registerSatelliteProperty(new ItemStack(LibVulpesItems.itemBattery, 1, 1), new SatelliteProperties().setPowerStorage(400));
		SatelliteRegistry.registerSatelliteProperty(new ItemStack(AdvancedRocketryItems.itemDataUnit, 1, 0), new SatelliteProperties().setMaxData(1000));


		//Item Registration
		LibVulpesBlocks.registerItem(AdvancedRocketryItems.itemQuartzCrucible.setRegistryName("iquartzcrucible"));
		LibVulpesBlocks.registerItem(AdvancedRocketryItems.itemOreScanner.setRegistryName("oreScanner"));
		LibVulpesBlocks.registerItem(AdvancedRocketryItems.itemSatellitePowerSource.setRegistryName("satellitePowerSource"));
		LibVulpesBlocks.registerItem(AdvancedRocketryItems.itemSatellitePrimaryFunction.setRegistryName("satellitePrimaryFunction"));
		LibVulpesBlocks.registerItem(AdvancedRocketryItems.itemCircuitPlate.setRegistryName("itemCircuitPlate"));
		LibVulpesBlocks.registerItem(AdvancedRocketryItems.itemIC.setRegistryName("ic"));
		LibVulpesBlocks.registerItem(AdvancedRocketryItems.itemWafer.setRegistryName("wafer"));
		LibVulpesBlocks.registerItem(AdvancedRocketryItems.itemDataUnit.setRegistryName("dataUnit"));
		LibVulpesBlocks.registerItem(AdvancedRocketryItems.itemSatellite.setRegistryName("satellite"));
		LibVulpesBlocks.registerItem(AdvancedRocketryItems.itemSatelliteIdChip.setRegistryName("satelliteIdChip"));
		LibVulpesBlocks.registerItem(AdvancedRocketryItems.itemPlanetIdChip.setRegistryName("planetIdChip"));
		LibVulpesBlocks.registerItem(AdvancedRocketryItems.itemMisc.setRegistryName("misc"));
		LibVulpesBlocks.registerItem(AdvancedRocketryItems.itemSawBlade.setRegistryName("sawBladeIron"));
		LibVulpesBlocks.registerItem(AdvancedRocketryItems.itemSpaceStationChip.setRegistryName("spaceStationChip"));
		LibVulpesBlocks.registerItem(AdvancedRocketryItems.itemSpaceStation.setRegistryName("spaceStation"));
		LibVulpesBlocks.registerItem(AdvancedRocketryItems.itemSpaceSuit_Helmet.setRegistryName("spaceHelmet"));
		LibVulpesBlocks.registerItem(AdvancedRocketryItems.itemSpaceSuit_Boots.setRegistryName("spaceBoots"));
		LibVulpesBlocks.registerItem(AdvancedRocketryItems.itemSpaceSuit_Chest.setRegistryName("spaceChestplate"));
		LibVulpesBlocks.registerItem(AdvancedRocketryItems.itemSpaceSuit_Leggings.setRegistryName("spaceLeggings"));
		LibVulpesBlocks.registerItem(AdvancedRocketryItems.itemBucketRocketFuel.setRegistryName("bucketRocketFuel"));
		LibVulpesBlocks.registerItem(AdvancedRocketryItems.itemBucketNitrogen.setRegistryName("bucketNitrogen"));
		LibVulpesBlocks.registerItem(AdvancedRocketryItems.itemBucketHydrogen.setRegistryName("bucketHydrogen"));
		LibVulpesBlocks.registerItem(AdvancedRocketryItems.itemBucketOxygen.setRegistryName("bucketOxygen"));
		LibVulpesBlocks.registerItem(AdvancedRocketryItems.itemBucketEnrichedLava.setRegistryName("bucketEnrichedLava"));
		LibVulpesBlocks.registerItem(AdvancedRocketryItems.itemSmallAirlockDoor.setRegistryName("smallAirlockDoor"));
		LibVulpesBlocks.registerItem(AdvancedRocketryItems.itemCarbonScrubberCartridge.setRegistryName("carbonScrubberCartridge"));
		LibVulpesBlocks.registerItem(AdvancedRocketryItems.itemSealDetector.setRegistryName("sealDetector"));
		LibVulpesBlocks.registerItem(AdvancedRocketryItems.itemJackhammer.setRegistryName("jackHammer"));
		LibVulpesBlocks.registerItem(AdvancedRocketryItems.itemAsteroidChip.setRegistryName("asteroidChip"));
		LibVulpesBlocks.registerItem(AdvancedRocketryItems.itemSpaceElevatorChip.setRegistryName("elevatorChip"));
		LibVulpesBlocks.registerItem(AdvancedRocketryItems.itemLens.setRegistryName("lens"));
		LibVulpesBlocks.registerItem(AdvancedRocketryItems.itemJetpack.setRegistryName("jetPack"));
		LibVulpesBlocks.registerItem(AdvancedRocketryItems.itemPressureTank.setRegistryName("pressureTank"));
		LibVulpesBlocks.registerItem(AdvancedRocketryItems.itemUpgrade.setRegistryName("itemUpgrade"));
		LibVulpesBlocks.registerItem(AdvancedRocketryItems.itemAtmAnalyser.setRegistryName("atmAnalyser"));
		LibVulpesBlocks.registerItem(AdvancedRocketryItems.itemBasicLaserGun.setRegistryName("basicLaserGun"));
		LibVulpesBlocks.registerItem(AdvancedRocketryItems.itemBeaconFinder.setRegistryName("beaconFinder"));
		LibVulpesBlocks.registerItem(AdvancedRocketryItems.itemThermite.setRegistryName("thermite"));
		LibVulpesBlocks.registerItem(AdvancedRocketryItems.itemHovercraft.setRegistryName("hoverCraft"));

		if(zmaster587.advancedRocketry.api.ARConfiguration.getCurrentConfig().enableTerraforming)
			LibVulpesBlocks.registerItem(AdvancedRocketryItems.itemBiomeChanger.setRegistryName("biomeChanger"));
		
        OreDictionary.registerOre("waferSilicon", new ItemStack(AdvancedRocketryItems.itemWafer,1,0));
        OreDictionary.registerOre("ingotCarbon", new ItemStack(AdvancedRocketryItems.itemMisc, 1, 1));
        OreDictionary.registerOre("itemLens", AdvancedRocketryItems.itemLens);
        OreDictionary.registerOre("itemSilicon", MaterialRegistry.getItemStackFromMaterialAndType("Silicon", AllowedProducts.getProductByName("INGOT")));
        OreDictionary.registerOre("dustThermite", new ItemStack(AdvancedRocketryItems.itemThermite));
	}
	
	@SubscribeEvent(priority=EventPriority.HIGH)
    public void registerBlocks(RegistryEvent.Register<Block> evt)
	{
		//Blocks -------------------------------------------------------------------------------------
		AdvancedRocketryBlocks.blocksGeode = new Block(MaterialGeode.geode).setUnlocalizedName("geode").setCreativeTab(LibVulpes.tabLibVulpesOres).setHardness(6f).setResistance(2000F);
		AdvancedRocketryBlocks.blocksGeode.setHarvestLevel("jackhammer", 2);
		AdvancedRocketryBlocks.blockLaunchpad = new BlockLinkedHorizontalTexture(Material.ROCK).setUnlocalizedName("pad").setCreativeTab(tabAdvRocketry).setHardness(2f).setResistance(10f);
		AdvancedRocketryBlocks.blockStructureTower = new BlockAlphaTexture(Material.ROCK).setUnlocalizedName("structuretower").setCreativeTab(tabAdvRocketry).setHardness(2f);
		AdvancedRocketryBlocks.blockGenericSeat = new BlockSeat(Material.CLOTH).setUnlocalizedName("seat").setCreativeTab(tabAdvRocketry).setHardness(0.5f);
		AdvancedRocketryBlocks.blockEngine = new BlockRocketMotor(Material.ROCK).setUnlocalizedName("rocket").setCreativeTab(tabAdvRocketry).setHardness(2f);
		AdvancedRocketryBlocks.blockAdvEngine = new BlockAdvancedRocketMotor(Material.ROCK).setUnlocalizedName("advRocket").setCreativeTab(tabAdvRocketry).setHardness(2f);
		AdvancedRocketryBlocks.blockFuelTank = new BlockFuelTank(Material.ROCK).setUnlocalizedName("fuelTank").setCreativeTab(tabAdvRocketry).setHardness(2f);
		AdvancedRocketryBlocks.blockSawBlade = new BlockMotor(Material.ROCK,1f).setCreativeTab(tabAdvRocketry).setUnlocalizedName("sawBlade").setHardness(2f);

		AdvancedRocketryBlocks.blockConcrete = new Block(Material.ROCK).setUnlocalizedName("concrete").setCreativeTab(tabAdvRocketry).setHardness(3f).setResistance(16f);
		AdvancedRocketryBlocks.blockPlatePress = new BlockPress().setUnlocalizedName("blockHandPress").setCreativeTab(tabAdvRocketry).setHardness(2f);
		AdvancedRocketryBlocks.blockAirLock = new BlockDoor2(Material.ROCK).setUnlocalizedName("smallAirlockDoor").setHardness(3f).setResistance(8f);
		AdvancedRocketryBlocks.blockLandingPad = new BlockLandingPad(Material.ROCK).setUnlocalizedName("dockingPad").setHardness(3f).setCreativeTab(tabAdvRocketry);
		AdvancedRocketryBlocks.blockOxygenDetection = new BlockRedstoneEmitter(Material.ROCK,"advancedrocketry:atmosphereDetector_active").setUnlocalizedName("atmosphereDetector").setHardness(3f).setCreativeTab(tabAdvRocketry);
		AdvancedRocketryBlocks.blockOxygenScrubber = new BlockTile(TileCO2Scrubber.class, GuiHandler.guiId.MODULAR.ordinal()).setCreativeTab(tabAdvRocketry).setUnlocalizedName("scrubber").setHardness(3f);
		AdvancedRocketryBlocks.blockUnlitTorch = new BlockTorchUnlit().setHardness(0.0F).setUnlocalizedName("unlittorch");
		AdvancedRocketryBlocks.blockVitrifiedSand = new Block(Material.SAND).setUnlocalizedName("vitrifiedSand").setCreativeTab(CreativeTabs.BUILDING_BLOCKS).setHardness(0.5F);
		AdvancedRocketryBlocks.blockCharcoalLog = new BlockCharcoalLog().setUnlocalizedName("charcoallog").setCreativeTab(CreativeTabs.BUILDING_BLOCKS);
		AdvancedRocketryBlocks.blockElectricMushroom = new BlockElectricMushroom().setUnlocalizedName("electricMushroom").setCreativeTab(tabAdvRocketry).setHardness(0.0F);
		AdvancedRocketryBlocks.blockCrystal = new BlockCrystal().setUnlocalizedName("crystal").setCreativeTab(LibVulpes.tabLibVulpesOres).setHardness(2f);
		AdvancedRocketryBlocks.blockOrientationController = new BlockTile(TileStationOrientationControl.class,  GuiHandler.guiId.MODULAR.ordinal()).setCreativeTab(tabAdvRocketry).setUnlocalizedName("orientationControl").setHardness(3f);
		AdvancedRocketryBlocks.blockGravityController = new BlockTile(TileStationGravityController.class,  GuiHandler.guiId.MODULAR.ordinal()).setCreativeTab(tabAdvRocketry).setUnlocalizedName("gravityControl").setHardness(3f);
		AdvancedRocketryBlocks.blockAltitudeController = new BlockTile(TileStationAltitudeController.class,  GuiHandler.guiId.MODULAR.ordinal()).setCreativeTab(tabAdvRocketry).setUnlocalizedName("altitudeController").setHardness(3f);
		AdvancedRocketryBlocks.blockOxygenCharger = new BlockHalfTile(TileOxygenCharger.class, GuiHandler.guiId.MODULAR.ordinal()).setUnlocalizedName("oxygenCharger").setCreativeTab(tabAdvRocketry).setHardness(3f);
		AdvancedRocketryBlocks.blockOxygenVent = new BlockTile(TileOxygenVent.class, GuiHandler.guiId.MODULAR.ordinal()).setUnlocalizedName("oxygenVent").setCreativeTab(tabAdvRocketry).setHardness(3f);
		AdvancedRocketryBlocks.blockCircleLight = new Block(Material.IRON).setUnlocalizedName("circleLight").setCreativeTab(tabAdvRocketry).setHardness(2f).setLightLevel(1f);
		AdvancedRocketryBlocks.blockLens = new BlockLens().setUnlocalizedName("lens").setCreativeTab(tabAdvRocketry).setHardness(0.3f);
		AdvancedRocketryBlocks.blockRocketBuilder = new BlockTileWithMultitooltip(TileRocketBuilder.class, GuiHandler.guiId.MODULARNOINV.ordinal()).setUnlocalizedName("rocketAssembler").setCreativeTab(tabAdvRocketry).setHardness(3f);
		AdvancedRocketryBlocks.blockForceField = new BlockForceField(Material.ROCK).setBlockUnbreakable().setResistance(6000000.0F).setUnlocalizedName("forceField");
		AdvancedRocketryBlocks.blockForceFieldProjector = new BlockForceFieldProjector(Material.ROCK).setUnlocalizedName("forceFieldProjector").setCreativeTab(tabAdvRocketry).setHardness(3f);

		AdvancedRocketryBlocks.blockDeployableRocketBuilder = new BlockTileWithMultitooltip(TileStationDeployedAssembler.class, GuiHandler.guiId.MODULARNOINV.ordinal()).setUnlocalizedName("deployableRocketAssembler").setCreativeTab(tabAdvRocketry).setHardness(3f);

		AdvancedRocketryBlocks.blockStationBuilder = new BlockTileWithMultitooltip(TileStationBuilder.class, GuiHandler.guiId.MODULAR.ordinal()).setUnlocalizedName("stationAssembler").setCreativeTab(tabAdvRocketry).setHardness(3f);

		AdvancedRocketryBlocks.blockFuelingStation = new BlockTileRedstoneEmitter(TileEntityFuelingStation.class, GuiHandler.guiId.MODULAR.ordinal()).setUnlocalizedName("fuelStation").setCreativeTab(tabAdvRocketry).setHardness(3f);

		AdvancedRocketryBlocks.blockMonitoringStation = new BlockTileNeighborUpdate(TileEntityMoniteringStation.class, GuiHandler.guiId.MODULARNOINV.ordinal()).setCreativeTab(tabAdvRocketry).setHardness(3f);
		AdvancedRocketryBlocks.blockMonitoringStation.setUnlocalizedName("monitoringstation");

		AdvancedRocketryBlocks.blockWarpShipMonitor = new BlockWarpShipMonitor(TileWarpShipMonitor.class, GuiHandler.guiId.MODULARNOINV.ordinal()).setCreativeTab(tabAdvRocketry).setHardness(3f);
		AdvancedRocketryBlocks.blockWarpShipMonitor.setUnlocalizedName("stationmonitor");

		AdvancedRocketryBlocks.blockSatelliteBuilder = new BlockMultiblockMachine(TileSatelliteBuilder.class, GuiHandler.guiId.MODULAR.ordinal()).setCreativeTab(tabAdvRocketry).setHardness(3f);
		AdvancedRocketryBlocks.blockSatelliteBuilder.setUnlocalizedName("satelliteBuilder");

		AdvancedRocketryBlocks.blockSatelliteControlCenter = new BlockTile(TileEntitySatelliteControlCenter.class, GuiHandler.guiId.MODULAR.ordinal()).setCreativeTab(tabAdvRocketry).setHardness(3f);
		AdvancedRocketryBlocks.blockSatelliteControlCenter.setUnlocalizedName("satelliteMonitor");

		AdvancedRocketryBlocks.blockMicrowaveReciever = new BlockMultiblockMachine(TileMicrowaveReciever.class, GuiHandler.guiId.MODULAR.ordinal()).setCreativeTab(tabAdvRocketry).setHardness(3f);
		AdvancedRocketryBlocks.blockMicrowaveReciever.setUnlocalizedName("microwaveReciever");
		
		AdvancedRocketryBlocks.blockCentrifuge = new BlockMultiblockMachine(TileCentrifuge.class, GuiHandler.guiId.MODULAR.ordinal()).setCreativeTab(tabAdvRocketry).setHardness(3f);
		AdvancedRocketryBlocks.blockCentrifuge.setUnlocalizedName("centrifuge");

		//Arcfurnace
		AdvancedRocketryBlocks.blockArcFurnace = new BlockMultiblockMachine(TileElectricArcFurnace.class, GuiHandler.guiId.MODULAR.ordinal()).setUnlocalizedName("electricArcFurnace").setCreativeTab(tabAdvRocketry).setHardness(3f);

		AdvancedRocketryBlocks.blockMoonTurf = new BlockPlanetSoil().setMapColor(MapColor.SNOW).setHardness(0.5F).setUnlocalizedName("turf").setCreativeTab(tabAdvRocketry);
		AdvancedRocketryBlocks.blockMoonTurfDark = new BlockPlanetSoil().setMapColor(MapColor.SNOW).setHardness(0.5F).setUnlocalizedName("turfDark").setCreativeTab(tabAdvRocketry);
		AdvancedRocketryBlocks.blockHotTurf = new BlockPlanetSoil().setMapColor(MapColor.NETHERRACK).setHardness(0.5F).setUnlocalizedName("hotDryturf").setCreativeTab(tabAdvRocketry);

		AdvancedRocketryBlocks.blockLoader = new BlockARHatch(Material.ROCK).setUnlocalizedName("loader").setCreativeTab(tabAdvRocketry).setHardness(3f);

		AdvancedRocketryBlocks.blockAlienWood = new BlockAlienWood().setUnlocalizedName("log").setCreativeTab(tabAdvRocketry).setHardness(3f);
		AdvancedRocketryBlocks.blockAlienLeaves = new BlockAlienLeaves().setUnlocalizedName("leaves2").setCreativeTab(tabAdvRocketry).setHardness(3f);
		AdvancedRocketryBlocks.blockAlienSapling = new BlockAlienSapling().setUnlocalizedName("sapling").setCreativeTab(tabAdvRocketry).setHardness(3f);
		AdvancedRocketryBlocks.blockAlienPlanks = new BlockAlienPlank().setUnlocalizedName("planks").setCreativeTab(tabAdvRocketry).setHardness(3f);
		
		AdvancedRocketryBlocks.blockLightSource = new BlockLightSource();

		AdvancedRocketryBlocks.blockBlastBrick = new BlockMultiBlockComponentVisible(Material.ROCK).setCreativeTab(tabAdvRocketry).setUnlocalizedName("blastBrick").setHardness(3F).setResistance(15F);
		AdvancedRocketryBlocks.blockQuartzCrucible = new BlockQuartzCrucible().setUnlocalizedName("qcrucible").setCreativeTab(tabAdvRocketry);
		AdvancedRocketryBlocks.blockAstroBed = new BlockAstroBed().setHardness(0.2F).setUnlocalizedName("astroBed");

		AdvancedRocketryBlocks.blockPrecisionAssembler = new BlockMultiblockMachine(TilePrecisionAssembler.class, GuiHandler.guiId.MODULAR.ordinal()).setUnlocalizedName("precisionAssemblingMachine").setCreativeTab(tabAdvRocketry).setHardness(3f);
		AdvancedRocketryBlocks.blockCuttingMachine = new BlockMultiblockMachine(TileCuttingMachine.class, GuiHandler.guiId.MODULAR.ordinal()).setUnlocalizedName("cuttingMachine").setCreativeTab(tabAdvRocketry).setHardness(3f);
		AdvancedRocketryBlocks.blockCrystallizer = new BlockMultiblockMachine(TileCrystallizer.class, GuiHandler.guiId.MODULAR.ordinal()).setUnlocalizedName("Crystallizer").setCreativeTab(tabAdvRocketry).setHardness(3f);
		AdvancedRocketryBlocks.blockWarpCore = new BlockWarpCore(TileWarpCore.class, GuiHandler.guiId.MODULAR.ordinal()).setUnlocalizedName("warpCore").setCreativeTab(tabAdvRocketry).setHardness(3f);
		AdvancedRocketryBlocks.blockChemicalReactor = new BlockMultiblockMachine(TileChemicalReactor.class, GuiHandler.guiId.MODULAR.ordinal()).setUnlocalizedName("chemreactor").setCreativeTab(tabAdvRocketry).setHardness(3f);
		AdvancedRocketryBlocks.blockLathe = new BlockMultiblockMachine(TileLathe.class, GuiHandler.guiId.MODULAR.ordinal()).setUnlocalizedName("lathe").setCreativeTab(tabAdvRocketry).setHardness(3f);
		AdvancedRocketryBlocks.blockRollingMachine = new BlockMultiblockMachine(TileRollingMachine.class, GuiHandler.guiId.MODULAR.ordinal()).setUnlocalizedName("rollingMachine").setCreativeTab(tabAdvRocketry).setHardness(3f);
		AdvancedRocketryBlocks.blockElectrolyser = new BlockMultiblockMachine(TileElectrolyser.class, GuiHandler.guiId.MODULAR.ordinal()).setUnlocalizedName("electrolyser").setCreativeTab(tabAdvRocketry).setHardness(3f);
		AdvancedRocketryBlocks.blockAtmosphereTerraformer = new BlockMultiblockMachine(TileAtmosphereTerraformer.class, GuiHandler.guiId.MODULARNOINV.ordinal()).setUnlocalizedName("atmosphereTerraformer").setCreativeTab(tabAdvRocketry).setHardness(3f);
		AdvancedRocketryBlocks.blockPlanetAnalyser = new BlockMultiblockMachine(TileAstrobodyDataProcessor.class, GuiHandler.guiId.MODULARNOINV.ordinal()).setUnlocalizedName("planetanalyser").setCreativeTab(tabAdvRocketry).setHardness(3f);
		AdvancedRocketryBlocks.blockObservatory = new BlockMultiblockMachine(TileObservatory.class, GuiHandler.guiId.MODULARNOINV.ordinal()).setUnlocalizedName("observatory").setCreativeTab(tabAdvRocketry).setHardness(3f);
		AdvancedRocketryBlocks.blockBlackHoleGenerator = new BlockMultiblockMachine(TileBlackHoleGenerator.class, GuiHandler.guiId.MODULAR.ordinal()).setUnlocalizedName("blackholegenerator").setCreativeTab(tabAdvRocketry).setHardness(3f);
		AdvancedRocketryBlocks.blockPump = new BlockTile(TilePump.class, GuiHandler.guiId.MODULAR.ordinal()).setUnlocalizedName("pump").setCreativeTab(tabAdvRocketry).setHardness(3f);
		
		AdvancedRocketryBlocks.blockGuidanceComputer = new BlockTile(TileGuidanceComputer.class,GuiHandler.guiId.MODULAR.ordinal()).setUnlocalizedName("guidanceComputer").setCreativeTab(tabAdvRocketry).setHardness(3f);
		AdvancedRocketryBlocks.blockPlanetSelector = new BlockTile(TilePlanetSelector.class,GuiHandler.guiId.MODULARFULLSCREEN.ordinal()).setUnlocalizedName("planetSelector").setCreativeTab(tabAdvRocketry).setHardness(3f);
		AdvancedRocketryBlocks.blockPlanetHoloSelector = new BlockHalfTile(TilePlanetaryHologram.class,GuiHandler.guiId.MODULAR.ordinal()).setUnlocalizedName("planetHoloSelector").setCreativeTab(tabAdvRocketry).setHardness(3f);
		AdvancedRocketryBlocks.blockBiomeScanner = new BlockMultiblockMachine(TileBiomeScanner.class,GuiHandler.guiId.MODULARNOINV.ordinal()).setUnlocalizedName("biomeScanner").setCreativeTab(tabAdvRocketry).setHardness(3f);
		AdvancedRocketryBlocks.blockDrill = new BlockMiningDrill().setUnlocalizedName("drill").setCreativeTab(tabAdvRocketry).setHardness(3f);
		AdvancedRocketryBlocks.blockSuitWorkStation = new BlockSuitWorkstation(TileSuitWorkStation.class, GuiHandler.guiId.MODULAR.ordinal()).setUnlocalizedName("suitWorkStation").setCreativeTab(tabAdvRocketry).setHardness(3f);
		AdvancedRocketryBlocks.blockRailgun = new BlockMultiblockMachine(TileRailgun.class, GuiHandler.guiId.MODULAR.ordinal()).setUnlocalizedName("railgun").setCreativeTab(tabAdvRocketry).setHardness(3f);
		AdvancedRocketryBlocks.blockSpaceElevatorController = new BlockMultiblockMachine(TileSpaceElevator.class,  GuiHandler.guiId.MODULAR.ordinal()).setCreativeTab(tabAdvRocketry).setUnlocalizedName("spaceElevatorController").setHardness(3f);
		AdvancedRocketryBlocks.blockBeacon = new BlockBeacon(TileBeacon.class,  GuiHandler.guiId.MODULAR.ordinal()).setCreativeTab(tabAdvRocketry).setUnlocalizedName("beacon").setHardness(3f);
		AdvancedRocketryBlocks.blockIntake = new BlockIntake(Material.IRON).setUnlocalizedName("gasIntake").setCreativeTab(tabAdvRocketry).setHardness(3f);
		AdvancedRocketryBlocks.blockPressureTank = new BlockPressurizedFluidTank(Material.IRON).setUnlocalizedName("pressurizedTank").setCreativeTab(tabAdvRocketry).setHardness(3f);
		AdvancedRocketryBlocks.blockSolarPanel = new Block(Material.IRON).setUnlocalizedName("solarPanel").setCreativeTab(tabAdvRocketry).setHardness(3f);
		AdvancedRocketryBlocks.blockSolarGenerator = new BlockSolarGenerator(TileSolarPanel.class, GuiHandler.guiId.MODULAR.ordinal()).setCreativeTab(tabAdvRocketry).setHardness(3f).setUnlocalizedName("solarGenerator");
		AdvancedRocketryBlocks.blockDockingPort = new BlockStationModuleDockingPort(Material.IRON).setUnlocalizedName("stationMarker").setCreativeTab(tabAdvRocketry).setHardness(3f);
		AdvancedRocketryBlocks.blockPipeSealer = new BlockSeal(Material.IRON).setUnlocalizedName("pipeSeal").setCreativeTab(tabAdvRocketry).setHardness(0.5f);
		AdvancedRocketryBlocks.blockThermiteTorch = new BlockThermiteTorch().setUnlocalizedName("thermiteTorch").setCreativeTab(tabAdvRocketry).setHardness(0.1f).setLightLevel(1f);
		AdvancedRocketryBlocks.blockBasalt = new Block(Material.ROCK).setUnlocalizedName("basalt").setCreativeTab(tabAdvRocketry).setHardness(5f).setResistance(15f);
		AdvancedRocketryBlocks.blockLandingFloat = new Block(Material.ROCK).setUnlocalizedName("landingfloat").setCreativeTab(tabAdvRocketry).setHardness(1).setResistance(1f);
		AdvancedRocketryBlocks.blockTransciever = new BlockTransciever(TileWirelessTransciever.class, GuiHandler.guiId.MODULAR.ordinal()).setUnlocalizedName("wirelessTransciever").setCreativeTab(tabAdvRocketry).setHardness(3f);
		
		//Configurable stuff
		if(zmaster587.advancedRocketry.api.ARConfiguration.getCurrentConfig().enableGravityController)
			AdvancedRocketryBlocks.blockGravityMachine = new BlockMultiblockMachine(TileGravityController.class,GuiHandler.guiId.MODULARNOINV.ordinal()).setUnlocalizedName("gravityMachine").setCreativeTab(tabAdvRocketry).setHardness(3f);


		if(zmaster587.advancedRocketry.api.ARConfiguration.getCurrentConfig().enableLaserDrill) {
			AdvancedRocketryBlocks.blockSpaceLaser = new BlockLaser().setHardness(2f);
			AdvancedRocketryBlocks.blockSpaceLaser.setCreativeTab(tabAdvRocketry);
		}


		//Fluid Registration
		AdvancedRocketryFluids.fluidOxygen = new FluidColored("oxygen",0xFF8f94b9).setUnlocalizedName("oxygen").setGaseous(false).setLuminosity(10).setDensity(800).setViscosity(1500);
		if(!FluidRegistry.registerFluid(AdvancedRocketryFluids.fluidOxygen))
		{
			AdvancedRocketryFluids.fluidOxygen = FluidRegistry.getFluid("oxygen");
		}
		// For all intents and purposes, it's the same
		FluidUtils.addFluidMapping(AdvancedRocketryFluids.fluidOxygen, "liquidoxygen");

		AdvancedRocketryFluids.fluidHydrogen = new FluidColored("hydrogen",0xFFdbc1c1).setUnlocalizedName("hydrogen").setGaseous(false).setLuminosity(10).setDensity(800).setViscosity(1500);
		if(!FluidRegistry.registerFluid(AdvancedRocketryFluids.fluidHydrogen))
		{
			AdvancedRocketryFluids.fluidHydrogen = FluidRegistry.getFluid("hydrogen");
		}
		FluidUtils.addFluidMapping(AdvancedRocketryFluids.fluidHydrogen, "liquidhydrogen");

		AdvancedRocketryFluids.fluidRocketFuel = new FluidColored("rocketFuel", 0xFFe5d884).setUnlocalizedName("rocketFuel").setGaseous(false).setLuminosity(10).setDensity(800).setViscosity(1500);
		if(!FluidRegistry.registerFluid(AdvancedRocketryFluids.fluidRocketFuel))
		{
			AdvancedRocketryFluids.fluidRocketFuel = FluidRegistry.getFluid("rocketFuel");
		}

		AdvancedRocketryFluids.fluidNitrogen = new FluidColored("nitrogen", 0xFF97a7e7).setUnlocalizedName("nitrogen").setGaseous(false).setLuminosity(10).setDensity(800).setViscosity(1500);
		if(!FluidRegistry.registerFluid(AdvancedRocketryFluids.fluidNitrogen))
		{
			AdvancedRocketryFluids.fluidNitrogen = FluidRegistry.getFluid("nitrogen");
		}		

		AdvancedRocketryFluids.fluidEnrichedLava = new FluidEnrichedLava("enrichedLava", 0xFFFFFFFF).setUnlocalizedName("enrichedLava").setLuminosity(15).setDensity(3000).setViscosity(6000).setTemperature(1300);
		if(!FluidRegistry.registerFluid(AdvancedRocketryFluids.fluidEnrichedLava))
		{
			AdvancedRocketryFluids.fluidEnrichedLava = FluidRegistry.getFluid("enrichedLava");
		}

		AtmosphereRegister.getInstance().registerHarvestableFluid(AdvancedRocketryFluids.fluidNitrogen);
		AtmosphereRegister.getInstance().registerHarvestableFluid(AdvancedRocketryFluids.fluidHydrogen);
		AtmosphereRegister.getInstance().registerHarvestableFluid(AdvancedRocketryFluids.fluidOxygen);

		AdvancedRocketryBlocks.blockOxygenFluid = new BlockFluid(AdvancedRocketryFluids.fluidOxygen, Material.WATER).setUnlocalizedName("oxygenFluidBlock").setCreativeTab(CreativeTabs.MISC);
		AdvancedRocketryBlocks.blockHydrogenFluid = new BlockFluid(AdvancedRocketryFluids.fluidHydrogen, Material.WATER).setUnlocalizedName("hydrogenFluidBlock").setCreativeTab(CreativeTabs.MISC);
		AdvancedRocketryBlocks.blockFuelFluid = new BlockFluid(AdvancedRocketryFluids.fluidRocketFuel, new MaterialLiquid(MapColor.YELLOW)).setUnlocalizedName("rocketFuelBlock").setCreativeTab(CreativeTabs.MISC);
		AdvancedRocketryBlocks.blockNitrogenFluid = new BlockFluid(AdvancedRocketryFluids.fluidNitrogen, Material.WATER).setUnlocalizedName("nitrogenFluidBlock").setCreativeTab(CreativeTabs.MISC);
		AdvancedRocketryBlocks.blockEnrichedLavaFluid = new BlockEnrichedLava(AdvancedRocketryFluids.fluidEnrichedLava, Material.LAVA).setUnlocalizedName("enrichedLavaBlock").setCreativeTab(CreativeTabs.MISC).setLightLevel(15);

		//Cables
		//TODO: add back after fixing the cable network
		//AdvancedRocketryBlocks.blockFluidPipe = new BlockLiquidPipe(Material.IRON).setUnlocalizedName("liquidPipe").setCreativeTab(tabAdvRocketry).setHardness(1f);
		//AdvancedRocketryBlocks.blockDataPipe = new BlockDataCable(Material.IRON).setUnlocalizedName("dataPipe").setCreativeTab(tabAdvRocketry).setHardness(1f);
		//AdvancedRocketryBlocks.blockEnergyPipe = new BlockEnergyCable(Material.IRON).setUnlocalizedName("energyPipe").setCreativeTab(tabAdvRocketry).setHardness(1f);

		//LibVulpesBlocks.registerBlock(AdvancedRocketryBlocks.blockDataPipe.setRegistryName("dataPipe"));
		//LibVulpesBlocks.registerBlock(AdvancedRocketryBlocks.blockEnergyPipe.setRegistryName("energyPipe"));
		//LibVulpesBlocks.registerBlock(AdvancedRocketryBlocks.blockFluidPipe.setRegistryName("liquidPipe"));
		LibVulpesBlocks.registerBlock(AdvancedRocketryBlocks.blockLaunchpad.setRegistryName("launchpad"));
		LibVulpesBlocks.registerBlock(AdvancedRocketryBlocks.blockRocketBuilder.setRegistryName("rocketBuilder"));
		LibVulpesBlocks.registerBlock(AdvancedRocketryBlocks.blockStructureTower.setRegistryName("structureTower"));
		LibVulpesBlocks.registerBlock(AdvancedRocketryBlocks.blockGenericSeat.setRegistryName("seat"));
		LibVulpesBlocks.registerBlock(AdvancedRocketryBlocks.blockEngine.setRegistryName("rocketmotor"));
		LibVulpesBlocks.registerBlock(AdvancedRocketryBlocks.blockAdvEngine.setRegistryName("advRocketmotor"));
		LibVulpesBlocks.registerBlock(AdvancedRocketryBlocks.blockFuelTank.setRegistryName("fuelTank"));
		LibVulpesBlocks.registerBlock(AdvancedRocketryBlocks.blockFuelingStation.setRegistryName("fuelingStation"));
		LibVulpesBlocks.registerBlock(AdvancedRocketryBlocks.blockMonitoringStation.setRegistryName("monitoringStation"));
		LibVulpesBlocks.registerBlock(AdvancedRocketryBlocks.blockSatelliteBuilder.setRegistryName("satelliteBuilder"));
		LibVulpesBlocks.registerBlock(AdvancedRocketryBlocks.blockMoonTurf.setRegistryName("moonTurf"));
		LibVulpesBlocks.registerBlock(AdvancedRocketryBlocks.blockMoonTurfDark.setRegistryName("moonTurf_dark"));
		LibVulpesBlocks.registerBlock(AdvancedRocketryBlocks.blockHotTurf.setRegistryName("hotTurf"));
		LibVulpesBlocks.registerBlock(AdvancedRocketryBlocks.blockLoader.setRegistryName("loader"), ItemBlockMeta.class, false);
		LibVulpesBlocks.registerBlock(AdvancedRocketryBlocks.blockPrecisionAssembler.setRegistryName("precisionassemblingmachine"));
		LibVulpesBlocks.registerBlock(AdvancedRocketryBlocks.blockBlastBrick.setRegistryName("blastbrick"));
		LibVulpesBlocks.registerBlock(AdvancedRocketryBlocks.blockQuartzCrucible.setRegistryName("quartzcrucible"));
		LibVulpesBlocks.registerBlock(AdvancedRocketryBlocks.blockCrystallizer.setRegistryName("crystallizer"));
		LibVulpesBlocks.registerBlock(AdvancedRocketryBlocks.blockCuttingMachine.setRegistryName("cuttingMachine"));
		LibVulpesBlocks.registerBlock(AdvancedRocketryBlocks.blockAlienWood.setRegistryName("alienWood"));
		LibVulpesBlocks.registerBlock(AdvancedRocketryBlocks.blockAlienLeaves.setRegistryName("alienLeaves"));
		LibVulpesBlocks.registerBlock(AdvancedRocketryBlocks.blockAlienSapling.setRegistryName("alienSapling"));
		LibVulpesBlocks.registerBlock(AdvancedRocketryBlocks.blockObservatory.setRegistryName("observatory"));
		LibVulpesBlocks.registerBlock(AdvancedRocketryBlocks.blockBlackHoleGenerator.setRegistryName("blackholegenerator"));
		LibVulpesBlocks.registerBlock(AdvancedRocketryBlocks.blockConcrete.setRegistryName("concrete"));
		LibVulpesBlocks.registerBlock(AdvancedRocketryBlocks.blockPlanetSelector.setRegistryName("planetSelector"));
		LibVulpesBlocks.registerBlock(AdvancedRocketryBlocks.blockSatelliteControlCenter.setRegistryName("satelliteControlCenter"));
		LibVulpesBlocks.registerBlock(AdvancedRocketryBlocks.blockPlanetAnalyser.setRegistryName("planetAnalyser"));
		LibVulpesBlocks.registerBlock(AdvancedRocketryBlocks.blockGuidanceComputer.setRegistryName("guidanceComputer"));
		LibVulpesBlocks.registerBlock(AdvancedRocketryBlocks.blockArcFurnace.setRegistryName("arcfurnace"));
		LibVulpesBlocks.registerBlock(AdvancedRocketryBlocks.blockSawBlade.setRegistryName("sawBlade"));
		LibVulpesBlocks.registerBlock(AdvancedRocketryBlocks.blockLathe.setRegistryName("lathe"));
		LibVulpesBlocks.registerBlock(AdvancedRocketryBlocks.blockRollingMachine.setRegistryName("rollingMachine"));
		LibVulpesBlocks.registerBlock(AdvancedRocketryBlocks.blockPlatePress.setRegistryName("platepress"));
		LibVulpesBlocks.registerBlock(AdvancedRocketryBlocks.blockStationBuilder.setRegistryName("stationBuilder"));
		LibVulpesBlocks.registerBlock(AdvancedRocketryBlocks.blockElectrolyser.setRegistryName("electrolyser"));
		LibVulpesBlocks.registerBlock(AdvancedRocketryBlocks.blockChemicalReactor.setRegistryName("chemicalReactor"));
		LibVulpesBlocks.registerBlock(AdvancedRocketryBlocks.blockOxygenScrubber.setRegistryName("oxygenScrubber"));
		LibVulpesBlocks.registerBlock(AdvancedRocketryBlocks.blockOxygenVent.setRegistryName("oxygenVent"));
		LibVulpesBlocks.registerBlock(AdvancedRocketryBlocks.blockOxygenCharger.setRegistryName("oxygenCharger"));
		LibVulpesBlocks.registerBlock(AdvancedRocketryBlocks.blockAirLock.setRegistryName("airlock_door"));
		LibVulpesBlocks.registerBlock(AdvancedRocketryBlocks.blockLandingPad.setRegistryName("landingPad"));
		LibVulpesBlocks.registerBlock(AdvancedRocketryBlocks.blockWarpCore.setRegistryName("warpCore"));
		LibVulpesBlocks.registerBlock(AdvancedRocketryBlocks.blockWarpShipMonitor.setRegistryName("warpMonitor"));
		LibVulpesBlocks.registerBlock(AdvancedRocketryBlocks.blockOxygenDetection.setRegistryName("oxygenDetection"));
		LibVulpesBlocks.registerBlock(AdvancedRocketryBlocks.blockUnlitTorch.setRegistryName("unlitTorch"));
		LibVulpesBlocks.registerBlock(AdvancedRocketryBlocks.blocksGeode.setRegistryName("geode"));
		LibVulpesBlocks.registerBlock(AdvancedRocketryBlocks.blockOxygenFluid.setRegistryName("oxygenFluid"));
		LibVulpesBlocks.registerBlock(AdvancedRocketryBlocks.blockHydrogenFluid.setRegistryName("hydrogenFluid"));
		LibVulpesBlocks.registerBlock(AdvancedRocketryBlocks.blockFuelFluid.setRegistryName("rocketFuel"));
		LibVulpesBlocks.registerBlock(AdvancedRocketryBlocks.blockNitrogenFluid.setRegistryName("nitrogenFluid"));
		LibVulpesBlocks.registerBlock(AdvancedRocketryBlocks.blockEnrichedLavaFluid.setRegistryName("enrichedLavaFluid"));
		LibVulpesBlocks.registerBlock(AdvancedRocketryBlocks.blockVitrifiedSand.setRegistryName("vitrifiedSand"));
		LibVulpesBlocks.registerBlock(AdvancedRocketryBlocks.blockCharcoalLog.setRegistryName("charcoalLog"));
		LibVulpesBlocks.registerBlock(AdvancedRocketryBlocks.blockElectricMushroom.setRegistryName("electricMushroom"));
		LibVulpesBlocks.registerBlock(AdvancedRocketryBlocks.blockCrystal.setRegistryName("crystal"), ItemBlockCrystal.class, true );
		LibVulpesBlocks.registerBlock(AdvancedRocketryBlocks.blockOrientationController.setRegistryName("orientationController"));
		LibVulpesBlocks.registerBlock(AdvancedRocketryBlocks.blockGravityController.setRegistryName("gravityController"));
		LibVulpesBlocks.registerBlock(AdvancedRocketryBlocks.blockDrill.setRegistryName("drill"));
		LibVulpesBlocks.registerBlock(AdvancedRocketryBlocks.blockMicrowaveReciever.setRegistryName("microwaveReciever"));
		LibVulpesBlocks.registerBlock(AdvancedRocketryBlocks.blockLightSource.setRegistryName("lightSource"));
		LibVulpesBlocks.registerBlock(AdvancedRocketryBlocks.blockSolarPanel.setRegistryName("solarPanel"));
		LibVulpesBlocks.registerBlock(AdvancedRocketryBlocks.blockSuitWorkStation.setRegistryName("suitWorkStation"));
		LibVulpesBlocks.registerBlock(AdvancedRocketryBlocks.blockBiomeScanner.setRegistryName("biomeScanner"));
		LibVulpesBlocks.registerBlock(AdvancedRocketryBlocks.blockAtmosphereTerraformer.setRegistryName("terraformer"));
		LibVulpesBlocks.registerBlock(AdvancedRocketryBlocks.blockDeployableRocketBuilder.setRegistryName("deployableRocketBuilder"));
		LibVulpesBlocks.registerBlock(AdvancedRocketryBlocks.blockPressureTank.setRegistryName("liquidTank"), ItemBlockFluidTank.class, true);
		LibVulpesBlocks.registerBlock(AdvancedRocketryBlocks.blockIntake.setRegistryName("intake"));
		LibVulpesBlocks.registerBlock(AdvancedRocketryBlocks.blockCircleLight.setRegistryName("circleLight"));
		LibVulpesBlocks.registerBlock(AdvancedRocketryBlocks.blockSolarGenerator.setRegistryName("solarGenerator"));
		LibVulpesBlocks.registerBlock(AdvancedRocketryBlocks.blockDockingPort.setRegistryName("stationMarker"));
		LibVulpesBlocks.registerBlock(AdvancedRocketryBlocks.blockAltitudeController.setRegistryName("altitudeController"));
		LibVulpesBlocks.registerBlock(AdvancedRocketryBlocks.blockRailgun .setRegistryName("railgun"));
		LibVulpesBlocks.registerBlock(AdvancedRocketryBlocks.blockAstroBed .setRegistryName("astroBed"));
		LibVulpesBlocks.registerBlock(AdvancedRocketryBlocks.blockPlanetHoloSelector.setRegistryName("planetHoloSelector"));
		LibVulpesBlocks.registerBlock(AdvancedRocketryBlocks.blockLens.setRegistryName("blockLens"));
		LibVulpesBlocks.registerBlock(AdvancedRocketryBlocks.blockForceField.setRegistryName("forceField"));
		LibVulpesBlocks.registerBlock(AdvancedRocketryBlocks.blockForceFieldProjector.setRegistryName("forceFieldProjector"));
		LibVulpesBlocks.registerBlock(AdvancedRocketryBlocks.blockPipeSealer.setRegistryName("pipeSealer"));
		LibVulpesBlocks.registerBlock(AdvancedRocketryBlocks.blockSpaceElevatorController.setRegistryName("spaceElevatorController"));
		LibVulpesBlocks.registerBlock(AdvancedRocketryBlocks.blockBeacon.setRegistryName("beacon"));
		LibVulpesBlocks.registerBlock(AdvancedRocketryBlocks.blockAlienPlanks.setRegistryName("planks"));
		LibVulpesBlocks.registerBlock(AdvancedRocketryBlocks.blockThermiteTorch.setRegistryName("thermiteTorch"));
		LibVulpesBlocks.registerBlock(AdvancedRocketryBlocks.blockTransciever.setRegistryName("wirelessTransciever"));
		LibVulpesBlocks.registerBlock(AdvancedRocketryBlocks.blockPump.setRegistryName("blockPump"));
		LibVulpesBlocks.registerBlock(AdvancedRocketryBlocks.blockCentrifuge.setRegistryName("centrifuge"));
		LibVulpesBlocks.registerBlock(AdvancedRocketryBlocks.blockBasalt.setRegistryName("basalt"));
		LibVulpesBlocks.registerBlock(AdvancedRocketryBlocks.blockLandingFloat.setRegistryName("landingfloat"));
		
		if(zmaster587.advancedRocketry.api.ARConfiguration.getCurrentConfig().enableGravityController)
			LibVulpesBlocks.registerBlock(AdvancedRocketryBlocks.blockGravityMachine.setRegistryName("gravityMachine"));

		//TODO, use different mechanism to enable/disable drill
		if(zmaster587.advancedRocketry.api.ARConfiguration.getCurrentConfig().enableLaserDrill)
			LibVulpesBlocks.registerBlock(AdvancedRocketryBlocks.blockSpaceLaser.setRegistryName("spaceLaser"));

		
		//Register Allowed Products
		materialRegistry.registerMaterial(new zmaster587.libVulpes.api.material.Material("TitaniumAluminide", "pickaxe", 1, 0xaec2de, AllowedProducts.getProductByName("PLATE").getFlagValue() | AllowedProducts.getProductByName("INGOT").getFlagValue() | AllowedProducts.getProductByName("NUGGET").getFlagValue() | AllowedProducts.getProductByName("DUST").getFlagValue() | AllowedProducts.getProductByName("STICK").getFlagValue() | AllowedProducts.getProductByName("BLOCK").getFlagValue() | AllowedProducts.getProductByName("GEAR").getFlagValue() | AllowedProducts.getProductByName("SHEET").getFlagValue(), false));
		materialRegistry.registerMaterial(new zmaster587.libVulpes.api.material.Material("TitaniumIridium", "pickaxe", 1, 0xd7dfe4, AllowedProducts.getProductByName("PLATE").getFlagValue() | AllowedProducts.getProductByName("INGOT").getFlagValue() | AllowedProducts.getProductByName("NUGGET").getFlagValue() | AllowedProducts.getProductByName("DUST").getFlagValue() | AllowedProducts.getProductByName("STICK").getFlagValue() | AllowedProducts.getProductByName("BLOCK").getFlagValue() | AllowedProducts.getProductByName("GEAR").getFlagValue() | AllowedProducts.getProductByName("SHEET").getFlagValue(), false));

		materialRegistry.registerOres(LibVulpes.tabLibVulpesOres);
		
        //OreDict stuff
        OreDictionary.registerOre("turfMoon", new ItemStack(AdvancedRocketryBlocks.blockMoonTurf));
        OreDictionary.registerOre("turfMoon", new ItemStack(AdvancedRocketryBlocks.blockMoonTurfDark));
        OreDictionary.registerOre("logWood", new ItemStack(AdvancedRocketryBlocks.blockAlienWood));
        OreDictionary.registerOre("plankWood", new ItemStack(AdvancedRocketryBlocks.blockAlienPlanks));
        OreDictionary.registerOre("treeLeaves", new ItemStack(AdvancedRocketryBlocks.blockAlienLeaves));
        OreDictionary.registerOre("treeSapling", new ItemStack(AdvancedRocketryBlocks.blockAlienSapling));
        OreDictionary.registerOre("concrete", new ItemStack(AdvancedRocketryBlocks.blockConcrete));
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

		//TODO: move to proxy
		//Minecraft.getMinecraft().getBlockColors().registerBlockColorHandler((IBlockColor) AdvancedRocketryBlocks.blockFuelFluid, new Block[] {AdvancedRocketryBlocks.blockFuelFluid});
        
		ARAchivements.register();
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

		List<BlockMeta> list = new LinkedList<BlockMeta>();
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

		if(zmaster587.advancedRocketry.api.ARConfiguration.getCurrentConfig().enableGravityController)
			((ItemProjector)LibVulpesItems.itemHoloProjector).registerMachine(new TileGravityController(), (BlockTile)AdvancedRocketryBlocks.blockGravityMachine);

		if(zmaster587.advancedRocketry.api.ARConfiguration.getCurrentConfig().enableLaserDrill)
			((ItemProjector)LibVulpesItems.itemHoloProjector).registerMachine(new TileSpaceLaser(), (BlockTile)AdvancedRocketryBlocks.blockSpaceLaser);

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
		if(isGalacticraftLoaded() && zmaster587.advancedRocketry.api.ARConfiguration.getCurrentConfig().overrideGCAir) {
			GalacticCraftHandler eventHandler = new GalacticCraftHandler();
			MinecraftForge.EVENT_BUS.register(eventHandler);
			if(event.getSide().isClient())
				FMLCommonHandler.instance().bus().register(eventHandler);
		}
		
		// Handler checks is Galacticraft is loaded
		//Simplify?
		if(zmaster587.advancedRocketry.api.ARConfiguration.getCurrentConfig().overrideGCAir) {
			MinecraftForge.EVENT_BUS.register(new GalacticCraftHandler());
		}

		Compat.isSpongeInstalled = Loader.isModLoaded("sponge");
		// End compat stuff

		MinecraftForge.EVENT_BUS.register(SpaceObjectManager.getSpaceManager());

		PacketHandler.init();

		GameRegistry.registerWorldGenerator(new OreGenerator(), 100);

		ForgeChunkManager.setForcedChunkLoadingCallback(instance, new WorldEvents());

		

		//Register buckets
		BucketHandler.INSTANCE.registerBucket(AdvancedRocketryBlocks.blockFuelFluid, AdvancedRocketryItems.itemBucketRocketFuel, AdvancedRocketryFluids.fluidRocketFuel);
		BucketHandler.INSTANCE.registerBucket(AdvancedRocketryBlocks.blockHydrogenFluid, AdvancedRocketryItems.itemBucketHydrogen, AdvancedRocketryFluids.fluidHydrogen);
		BucketHandler.INSTANCE.registerBucket(AdvancedRocketryBlocks.blockOxygenFluid, AdvancedRocketryItems.itemBucketOxygen, AdvancedRocketryFluids.fluidOxygen);
		BucketHandler.INSTANCE.registerBucket(AdvancedRocketryBlocks.blockNitrogenFluid, AdvancedRocketryItems.itemBucketNitrogen, AdvancedRocketryFluids.fluidNitrogen);
		BucketHandler.INSTANCE.registerBucket(AdvancedRocketryBlocks.blockEnrichedLavaFluid, AdvancedRocketryItems.itemBucketEnrichedLava, AdvancedRocketryFluids.fluidEnrichedLava);
		
		//register gasses
		AdvancedRocketryFluids.registerGasGiantGas(AdvancedRocketryFluids.fluidHydrogen);
		AdvancedRocketryFluids.registerGasGiantGas(AdvancedRocketryFluids.fluidNitrogen);
		AdvancedRocketryFluids.registerGasGiantGas(AdvancedRocketryFluids.fluidOxygen);
		
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
			if(!properties.isNativeDimension && properties.getId() == zmaster587.advancedRocketry.api.ARConfiguration.getCurrentConfig().MoonId && !isGalacticraftLoaded()) {
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


	@EventHandler
	public void serverStopped(FMLServerStoppedEvent event) {
		zmaster587.advancedRocketry.dimension.DimensionManager.getInstance().onServerStopped();
		zmaster587.advancedRocketry.cable.NetworkRegistry.clearNetworks();
		SpaceObjectManager.getSpaceManager().onServerStopped();
		zmaster587.advancedRocketry.api.ARConfiguration.getCurrentConfig().MoonId = Constants.INVALID_PLANET;
		((BlockSeal)AdvancedRocketryBlocks.blockPipeSealer).clearMap();
		DimensionManager.dimOffset = config.getInt("minDimension", PLANET, 2, -127, 8000, "Dimensions including and after this number are allowed to be made into planets");
		zmaster587.advancedRocketry.api.ARConfiguration.getCurrentConfig().spaceDimId = config.get(Configuration.CATEGORY_GENERAL,"spaceStationId" , -2,"Dimension ID to use for space stations").getInt();
		
		if(!zmaster587.advancedRocketry.api.ARConfiguration.getCurrentConfig().lockUI)
			proxy.saveUILayout(config);
	}

	@SubscribeEvent
	public void registerOre(OreRegisterEvent event) {

		//Register ore products
		if(!zmaster587.advancedRocketry.api.ARConfiguration.getCurrentConfig().allowMakingItemsForOtherMods)
			return;

		for(AllowedProducts product : AllowedProducts.getAllAllowedProducts() ) {
			if(event.getName().startsWith(product.name().toLowerCase(Locale.ENGLISH))) {
				HashSet<String> list = modProducts.get(product);
				if(list == null) {
					list = new HashSet<String>();
					modProducts.put(product, list);
				}
				list.add(event.getName().substring(product.name().length()));
			}
		}

		//GT uses stick instead of Rod
		if(event.getName().startsWith("rod")) {
			HashSet<String> list = modProducts.get(AllowedProducts.getProductByName("STICK"));
			if(list == null) {
				list = new HashSet<String>();
				modProducts.put(AllowedProducts.getProductByName("STICK"), list);
			}

			list.add(event.getName().substring("rod".length()));
		}
	}
	
	/**
	 * Checks if galacticraft loaded.
	 *
	 * @return true, if is galacticraft loaded
	 */
	public static boolean isGalacticraftLoaded() {
		return Loader.isModLoaded("galacticraftcore");
	}
}
