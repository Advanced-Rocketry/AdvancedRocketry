package zmaster587.advancedRocketry;

import net.minecraft.block.Block;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.EntityType;
import net.minecraft.fluid.Fluid;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.particles.ParticleType;
import net.minecraft.tags.BlockTags;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.Dimension;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.carver.WorldCarver;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.structure.Structure;
import net.minecraftforge.client.event.ParticleFactoryRegisterEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.ModContainer;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLLoadCompleteEvent;
import net.minecraftforge.fml.event.server.FMLServerAboutToStartEvent;
import net.minecraftforge.fml.event.server.FMLServerStartedEvent;
import net.minecraftforge.fml.event.server.FMLServerStoppedEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import zmaster587.advancedRocketry.api.*;
import zmaster587.advancedRocketry.api.capability.CapabilitySpaceArmor;
import zmaster587.advancedRocketry.api.satellite.SatelliteProperties;
import zmaster587.advancedRocketry.atmosphere.AtmosphereType;
import zmaster587.advancedRocketry.block.*;
import zmaster587.advancedRocketry.block.multiblock.BlockSmallPlatePress;
import zmaster587.advancedRocketry.cable.NetworkRegistry;
import zmaster587.advancedRocketry.capability.CapabilityProtectiveArmor;
import zmaster587.advancedRocketry.common.CommonProxy;
import zmaster587.advancedRocketry.dimension.DimensionManager;
import zmaster587.advancedRocketry.dimension.DimensionProperties;
import zmaster587.advancedRocketry.dimension.DimensionProperties.AtmosphereTypes;
import zmaster587.advancedRocketry.dimension.DimensionProperties.Temps;
import zmaster587.advancedRocketry.enchant.EnchantmentSpaceBreathing;
import zmaster587.advancedRocketry.event.PlanetEventHandler;
import zmaster587.advancedRocketry.integration.CompatibilityMgr;
import zmaster587.advancedRocketry.mission.MissionGasCollection;
import zmaster587.advancedRocketry.mission.MissionOreMining;
import zmaster587.advancedRocketry.network.*;
import zmaster587.advancedRocketry.recipe.RecipeCentrifuge;
import zmaster587.advancedRocketry.recipe.RecipeChemicalReactor;
import zmaster587.advancedRocketry.recipe.RecipeCrystallizer;
import zmaster587.advancedRocketry.recipe.RecipeCuttingMachine;
import zmaster587.advancedRocketry.recipe.RecipeElectricArcFurnace;
import zmaster587.advancedRocketry.recipe.RecipeElectrolyzer;
import zmaster587.advancedRocketry.recipe.RecipeLathe;
import zmaster587.advancedRocketry.recipe.RecipePrecisionAssembler;
import zmaster587.advancedRocketry.recipe.RecipeRollingMachine;
import zmaster587.advancedRocketry.recipe.RecipeSmallPresser;
import zmaster587.advancedRocketry.satellite.*;
import zmaster587.advancedRocketry.stations.SpaceStationObject;
import zmaster587.advancedRocketry.stations.SpaceObjectManager;
import zmaster587.advancedRocketry.tile.multiblock.*;
import zmaster587.advancedRocketry.tile.multiblock.energy.TileBlackHoleGenerator;
import zmaster587.advancedRocketry.tile.multiblock.energy.TileMicrowaveReciever;
import zmaster587.advancedRocketry.tile.multiblock.energy.TileSolarArray;
import zmaster587.advancedRocketry.tile.multiblock.machine.*;
import zmaster587.advancedRocketry.tile.multiblock.orbitallaserdrill.TileOrbitalLaserDrill;
import zmaster587.advancedRocketry.tile.satellite.TileSatelliteAssembler;
import zmaster587.advancedRocketry.util.*;
import zmaster587.advancedRocketry.world.decoration.MapGenLander;
import zmaster587.advancedRocketry.world.decoration.StructurePieceGeode;
import zmaster587.libVulpes.LibVulpes;
import zmaster587.libVulpes.api.LibVulpesBlocks;
import zmaster587.libVulpes.api.LibVulpesItems;
import zmaster587.libVulpes.api.material.AllowedProducts;
import zmaster587.libVulpes.api.material.MaterialRegistry;
import zmaster587.libVulpes.api.material.MixedMaterial;
import zmaster587.libVulpes.block.BlockMeta;
import zmaster587.libVulpes.block.BlockTile;
import zmaster587.libVulpes.items.ItemProjector;
import zmaster587.libVulpes.network.PacketHandler;
import zmaster587.libVulpes.network.PacketItemModifcation;
import zmaster587.libVulpes.tile.multiblock.TileMultiBlock;
import zmaster587.libVulpes.util.HashedBlockPosition;
import zmaster587.libVulpes.util.InputSyncHandler;
import zmaster587.libVulpes.util.SingleEntry;
import zmaster587.advancedRocketry.client.ClientProxy;
import zmaster587.advancedRocketry.command.PlanetCommand;

import javax.annotation.Nonnull;
import java.io.*;
import java.util.*;
import java.util.Map.Entry;

// @Mod(modid="advancedrocketry", name="Advanced Rocketry", version="@MAJOR@.@MINOR@.@REVIS@.@BUILD@", dependencies="required-after:libvulpes@[%LIBVULPESVERSION%,)")
@Mod(value=Constants.modId)
public class AdvancedRocketry {

	public static final CommonProxy proxy = DistExecutor.runForDist(() -> ClientProxy::new, () -> CommonProxy::new);

	public final static String version = "@MAJOR@.@MINOR@.@REVIS@@BUILD@";

	public static final ARRecipeHandler machineRecipes = new ARRecipeHandler();

	public static ModContainer MOD_CONTAINER;
	public static final Logger logger = LogManager.getLogger(Constants.modId);
	private boolean resetFromXml;

	public static final MaterialRegistry materialRegistry = new MaterialRegistry();

	public static final ItemGroup tabAdvRocketry = new ItemGroup("advancedrocketry") {
		@Override
		@Nonnull
		public ItemStack createIcon() {
			return new ItemStack(AdvancedRocketryItems.itemSatelliteChip);
		}
	};
	
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, Constants.modId);
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, Constants.modId);
    public static final DeferredRegister<Fluid> FLUIDS = DeferredRegister.create(ForgeRegistries.FLUIDS, Constants.modId);
	
	public AdvancedRocketry() {
		MOD_CONTAINER = ModLoadingContext.get().getActiveContainer();
		FMLJavaModLoadingContext.get().getModEventBus().register(this);
		MinecraftForge.EVENT_BUS.addListener(this::registerCommands);
		MinecraftForge.EVENT_BUS.addListener(this::serverStarted);
		MinecraftForge.EVENT_BUS.addListener(this::serverStarting);
		MinecraftForge.EVENT_BUS.addListener(this::serverStopped);
		
		IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
		
		BLOCKS.register(modEventBus);
		ITEMS.register(modEventBus);
		FLUIDS.register(modEventBus);
		ARConfiguration.register();
		AdvancedRocketryFluids.init();
		proxy.initDeferredRegistries();
	}

	//@SubscribeEvent
	public void preInit() {
		//Init API
		AdvancedRocketryAPI.atomsphereSealHandler = SealableBlockHandler.INSTANCE;
		((SealableBlockHandler)AdvancedRocketryAPI.atomsphereSealHandler).loadDefaultData();

		//Configuration  ---------------------------------------------------------------------------------------------
		resetFromXml = ARConfiguration.getCurrentConfig().resetFromXML.get();

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
		SatelliteRegistry.registerSatellite("composition", SatelliteComposition.class);
		SatelliteRegistry.registerSatellite("mass", SatelliteMassScanner.class);
		SatelliteRegistry.registerSatellite("asteroidMiner", MissionOreMining.class);
		SatelliteRegistry.registerSatellite("gasMining", MissionGasCollection.class);
		SatelliteRegistry.registerSatellite("solarEnergy", SatelliteMicrowaveEnergy.class);
		SatelliteRegistry.registerSatellite("oreScanner", SatelliteOreMapping.class);
		
		//Register Satellite Properties
		SatelliteRegistry.registerSatelliteProperty(new ItemStack(AdvancedRocketryItems.itemOpticalSensor, 1), new SatelliteProperties().setSatelliteType(SatelliteRegistry.getKey(SatelliteOptical.class)));
		SatelliteRegistry.registerSatelliteProperty(new ItemStack(AdvancedRocketryItems.itemCompositionSensor, 1), new SatelliteProperties().setSatelliteType(SatelliteRegistry.getKey(SatelliteComposition.class)));
		SatelliteRegistry.registerSatelliteProperty(new ItemStack(AdvancedRocketryItems.itemMassSensor, 1), new SatelliteProperties().setSatelliteType(SatelliteRegistry.getKey(SatelliteMassScanner.class)));
		SatelliteRegistry.registerSatelliteProperty(new ItemStack(AdvancedRocketryItems.itemMicrowaveTransmitter, 1), new SatelliteProperties().setSatelliteType(SatelliteRegistry.getKey(SatelliteMicrowaveEnergy.class)));
		SatelliteRegistry.registerSatelliteProperty(new ItemStack(AdvancedRocketryItems.itemOreSensor, 1), new SatelliteProperties().setSatelliteType(SatelliteRegistry.getKey(SatelliteOreMapping.class)));
		SatelliteRegistry.registerSatelliteProperty(new ItemStack(AdvancedRocketryItems.itemSolarPanel,1), new SatelliteProperties().setPowerGeneration(4));
		SatelliteRegistry.registerSatelliteProperty(new ItemStack(AdvancedRocketryItems.itemLargeSolarPanel,1), new SatelliteProperties().setPowerGeneration(40));
		SatelliteRegistry.registerSatelliteProperty(new ItemStack(LibVulpesItems.itemBattery, 1), new SatelliteProperties().setPowerStorage(10000));
		SatelliteRegistry.registerSatelliteProperty(new ItemStack(LibVulpesItems.itemBatteryPack, 1), new SatelliteProperties().setPowerStorage(40000));
		SatelliteRegistry.registerSatelliteProperty(new ItemStack(AdvancedRocketryItems.itemDataUnit, 1), new SatelliteProperties().setMaxData(1000));
		
		//TileEntity Registration ---------------------------------------------------------------------------------------------

		//Register machine recipes
		File dir = new File( "./config/" + zmaster587.advancedRocketry.api.ARConfiguration.configFolder);
		if(!dir.exists())
			dir.mkdir();

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
        machineRecipes.registerMachine(BlockSmallPlatePress.class);
        machineRecipes.registerMachine(TilePrecisionLaserEtcher.class);
	}
	
	@SubscribeEvent
	public void registerRecipeTypes(RegistryEvent.Register<IRecipeSerializer<?>> event) {
		event.getRegistry().register(RecipeLathe.INSTANCE.setRegistryName("lathe"));
		event.getRegistry().register(RecipeRollingMachine.INSTANCE.setRegistryName("rollingmachine"));
		event.getRegistry().register(RecipeCrystallizer.INSTANCE.setRegistryName("crystallizer"));
		event.getRegistry().register(RecipeSmallPresser.INSTANCE.setRegistryName("smallplatepress"));
		event.getRegistry().register(RecipeCuttingMachine.INSTANCE.setRegistryName("cuttingmachine"));
		event.getRegistry().register(RecipePrecisionAssembler.INSTANCE.setRegistryName("precisionassembler"));
		event.getRegistry().register(RecipeElectricArcFurnace.INSTANCE.setRegistryName("electricarcfurnace"));
		event.getRegistry().register(RecipeChemicalReactor.INSTANCE.setRegistryName("chemicalreactor"));
		event.getRegistry().register(RecipeCentrifuge.INSTANCE.setRegistryName("centrifuge"));
		event.getRegistry().register(RecipeElectrolyzer.INSTANCE.setRegistryName("electrolyzer"));
	}
	
	@SubscribeEvent
	public void registerEntities(RegistryEvent.Register<EntityType<?>> event) {
		//Entity Registration ---------------------------------------------------------------------------------------------
		AdvancedRocketryEntities.registerEntities(event);
	}
	
	@SubscribeEvent
	public void registerTileEntities(RegistryEvent.Register<TileEntityType<?>> evt) {
		AdvancedRocketryTileEntityType.registerTileEntities(evt);
	}
	
	@SubscribeEvent
	public void registerStructures(RegistryEvent.Register<Structure<?>> evt) {
		AdvancedRocketryBiomes.registerStructures(evt);
	}
	
	@SubscribeEvent
	public void registerCarvers(RegistryEvent.Register<WorldCarver<?>> evt) {
		AdvancedRocketryBiomes.registerCarvers(evt);	
	}
	
	@SubscribeEvent
	public void registerFeature(RegistryEvent.Register<Feature<?>> evt)
	{
		AdvancedRocketryBiomes.registerFeature(evt);
	}
	
	@SubscribeEvent
	public void registerBiomes(RegistryEvent.Register<Biome> evt)
	{
		AdvancedRocketryBiomes.registerBiomes(evt);
	}
	
	@SubscribeEvent(priority=EventPriority.HIGH)
	public void registerEnchants(RegistryEvent.Register<Enchantment> evt) {
		//Enchantments
		AdvancedRocketryAPI.enchantmentSpaceProtection = new EnchantmentSpaceBreathing().setRegistryName("spacebreathing");
		evt.getRegistry().register(AdvancedRocketryAPI.enchantmentSpaceProtection);
	}

	@SubscribeEvent
	public void registerParticles(ParticleFactoryRegisterEvent evt) {
		AdvancedRocketryParticleTypes.registerParticles(evt);
	}

	@SubscribeEvent
	public void registerParticles(RegistryEvent.Register<ParticleType<?>> evt) {
		AdvancedRocketryParticleTypes.registerParticles(evt);
	}
	
	@SubscribeEvent(priority=EventPriority.HIGH)
    public void registerItems(RegistryEvent.Register<Item> evt)
	{
		AdvancedRocketryItems.registerItems(evt);
	}
	
	@SubscribeEvent(priority=EventPriority.HIGH)
    public void registerBlocks(RegistryEvent.Register<Block> evt) {
		AdvancedRocketryBlocks.registerBlocks(evt);

		//Register Allowed Products
		materialRegistry.registerMaterial(new zmaster587.libVulpes.api.material.Material("titaniumaluminide", "pickaxe", 1, 0xaec2de, AllowedProducts.getProductByName("PLATE").getFlagValue() | AllowedProducts.getProductByName("INGOT").getFlagValue() | AllowedProducts.getProductByName("NUGGET").getFlagValue() | AllowedProducts.getProductByName("DUST").getFlagValue() | AllowedProducts.getProductByName("ROD").getFlagValue() | AllowedProducts.getProductByName("BLOCK").getFlagValue() | AllowedProducts.getProductByName("GEAR").getFlagValue() | AllowedProducts.getProductByName("SHEET").getFlagValue(), false));
		materialRegistry.registerMaterial(new zmaster587.libVulpes.api.material.Material("titaniumiridium", "pickaxe", 1, 0xd7dfe4, AllowedProducts.getProductByName("PLATE").getFlagValue() | AllowedProducts.getProductByName("INGOT").getFlagValue() | AllowedProducts.getProductByName("NUGGET").getFlagValue() | AllowedProducts.getProductByName("DUST").getFlagValue() | AllowedProducts.getProductByName("ROD").getFlagValue() | AllowedProducts.getProductByName("BLOCK").getFlagValue() | AllowedProducts.getProductByName("GEAR").getFlagValue() | AllowedProducts.getProductByName("SHEET").getFlagValue(), false));

		materialRegistry.registerOres(LibVulpes.tabLibVulpesOres);
		
        //OreDict stuff
		BlockTags.getCollection().getTagByID(new ResourceLocation("forge", "turfmoon")).contains(AdvancedRocketryBlocks.blockMoonTurf);
		BlockTags.getCollection().getTagByID(new ResourceLocation("forge", "turfmoon")).contains(AdvancedRocketryBlocks.blockMoonTurfDark);
		BlockTags.getCollection().getTagByID(new ResourceLocation("minecraft", "logs")).contains(AdvancedRocketryBlocks.blockLightwoodLog);
		BlockTags.getCollection().getTagByID(new ResourceLocation("minecraft", "planks")).contains(AdvancedRocketryBlocks.blockLightwoodPlanks);
		BlockTags.getCollection().getTagByID(new ResourceLocation("minecraft", "leaves")).contains(AdvancedRocketryBlocks.blockLightwoodLeaves);
		BlockTags.getCollection().getTagByID(new ResourceLocation("minecraft", "saplings")).contains(AdvancedRocketryBlocks.blockLightwoodSapling);
		BlockTags.getCollection().getTagByID(new ResourceLocation("forge", "concrete")).contains(AdvancedRocketryBlocks.blockConcrete);
		BlockTags.getCollection().getTagByID(new ResourceLocation("advancedrocketry", "casingcentrifuge")).contains(LibVulpesBlocks.blockAdvancedMachineStructure);
	}
	
    @SubscribeEvent
	public void load(FMLCommonSetupEvent event) {
		preInit();
		proxy.init();

		//Register Alloys
		MaterialRegistry.registerMixedMaterial(new MixedMaterial(TileElectricArcFurnace.class, "orerutile", new ItemStack[] {MaterialRegistry.getMaterialFromName("Titanium").getProduct(AllowedProducts.getProductByName("INGOT"))}));

		//Data mapping 'D'
		List<BlockMeta> list = new LinkedList<>();
		list.add(new BlockMeta(AdvancedRocketryBlocks.blockDataBus, true));
		TileMultiBlock.addMapping('D', list);
		
        //Register the machine recipes
        TileChemicalReactor.registerRecipes();
	}

	@SubscribeEvent
	public void postInit(FMLLoadCompleteEvent event) {
		AtmosphereType.registerAtmosphere();
		List<? extends CharSequence> biomeBlackList = ARConfiguration.biomeBlackList.get();
		List<? extends CharSequence> biomeHighPressure = ARConfiguration.biomeHighPressure.get();
		List<? extends CharSequence> biomeSingle = ARConfiguration.biomeSingle.get();
		
		//Prevent these biomes from spawning normally
		AdvancedRocketryBiomes.instance.registerBlackListBiome(AdvancedRocketryBiomes.moonBiome);
		AdvancedRocketryBiomes.instance.registerBlackListBiome(AdvancedRocketryBiomes.moonBiomeDark);
		AdvancedRocketryBiomes.instance.registerBlackListBiome(AdvancedRocketryBiomes.hotDryBiome);
		AdvancedRocketryBiomes.instance.registerBlackListBiome(AdvancedRocketryBiomes.spaceBiome);
		AdvancedRocketryBiomes.instance.registerBlackListBiome(AdvancedRocketryBiomes.volcanic);

		//Read BlackList from config and register Blacklisted biomes
		for(CharSequence string : biomeBlackList) {
			try {
				Biome biome = AdvancedRocketryBiomes.getBiome((String)string);

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
		for(CharSequence string : biomeHighPressure) {
			try {
				Biome biome = AdvancedRocketryBiomes.getBiome((String)string);

				if(biome == null)
					logger.warn(String.format("Error registering high pressure biome \"%s\", a biome with that ID does not exist!", string));
				else
					AdvancedRocketryBiomes.instance.registerHighPressureBiome(biome);
			} catch (NumberFormatException e) {
				logger.warn("Error registering high pressure biome \"" + string + "\".  It is not a valid number or Biome ResourceLocation");
			}
		}

		//Read and Register Single biomes from config
		for(CharSequence string : biomeSingle) {
			try {
				Biome biome = AdvancedRocketryBiomes.getBiome((String)string);

				if(biome == null)
					logger.warn(String.format("Error registering single biome \"%s\", a biome with that ID does not exist!", string));
				else
					AdvancedRocketryBiomes.instance.registerSingleBiome(biome);
			} catch (NumberFormatException e) {
				logger.warn("Error registering single biome \"" + string + "\".  It is not a valid number or Biome ResourceLocation");
			}
		}

		CapabilitySpaceArmor.register();

		//Register multiblock items with the projector
		//Item processing machines
		((ItemProjector)LibVulpesItems.itemHoloProjector).registerMachine(new TileElectricArcFurnace(), (BlockTile)AdvancedRocketryBlocks.blockArcFurnace);
		((ItemProjector)LibVulpesItems.itemHoloProjector).registerMachine(new TileRollingMachine(), (BlockTile)AdvancedRocketryBlocks.blockRollingMachine);
		((ItemProjector)LibVulpesItems.itemHoloProjector).registerMachine(new TileLathe(), (BlockTile)AdvancedRocketryBlocks.blockLathe);
		((ItemProjector)LibVulpesItems.itemHoloProjector).registerMachine(new TileCrystallizer(), (BlockTile)AdvancedRocketryBlocks.blockCrystallizer);
		((ItemProjector)LibVulpesItems.itemHoloProjector).registerMachine(new TileCuttingMachine(), (BlockTile)AdvancedRocketryBlocks.blockCuttingMachine);
		((ItemProjector)LibVulpesItems.itemHoloProjector).registerMachine(new TilePrecisionAssembler(), (BlockTile)AdvancedRocketryBlocks.blockPrecisionAssembler);
		((ItemProjector)LibVulpesItems.itemHoloProjector).registerMachine(new TilePrecisionLaserEtcher(), (BlockTile)AdvancedRocketryBlocks.blockPrecisionLaserEtcher);
		//Fluid processing machines
		((ItemProjector)LibVulpesItems.itemHoloProjector).registerMachine(new TileElectrolyser(), (BlockTile)AdvancedRocketryBlocks.blockElectrolyzer);
		((ItemProjector)LibVulpesItems.itemHoloProjector).registerMachine(new TileChemicalReactor(), (BlockTile)AdvancedRocketryBlocks.blockChemicalReactor);
		((ItemProjector)LibVulpesItems.itemHoloProjector).registerMachine(new TileCentrifuge(), (BlockTile)AdvancedRocketryBlocks.blockCentrifuge);
		//Data processing machines
		((ItemProjector)LibVulpesItems.itemHoloProjector).registerMachine(new TileSatelliteAssembler(), (BlockTile)AdvancedRocketryBlocks.blockSatelliteAssembler);
		((ItemProjector)LibVulpesItems.itemHoloProjector).registerMachine(new TileObservatory(), (BlockTile)AdvancedRocketryBlocks.blockObservatory);
		((ItemProjector)LibVulpesItems.itemHoloProjector).registerMachine(new TileAstrobodyDataProcessor(), (BlockTile)AdvancedRocketryBlocks.blockAstrobodyDataProcessor);
		//Power generation
		((ItemProjector)LibVulpesItems.itemHoloProjector).registerMachine(new TileSolarArray(), (BlockTile)AdvancedRocketryBlocks.blockSolarArray);
		((ItemProjector)LibVulpesItems.itemHoloProjector).registerMachine(new TileMicrowaveReciever(), (BlockTile)AdvancedRocketryBlocks.blockMicrowaveReceiver);
		((ItemProjector)LibVulpesItems.itemHoloProjector).registerMachine(new TileBlackHoleGenerator(), (BlockTile)AdvancedRocketryBlocks.blockBlackHoleGenerator);
		//Station multiblocks
		((ItemProjector)LibVulpesItems.itemHoloProjector).registerMachine(new TileBiomeScanner(), (BlockTile)AdvancedRocketryBlocks.blockBiomeScanner);
		((ItemProjector)LibVulpesItems.itemHoloProjector).registerMachine(new TileRailgun(), (BlockTile)AdvancedRocketryBlocks.blockRailgun);
		((ItemProjector)LibVulpesItems.itemHoloProjector).registerMachine(new TileSpaceElevator(), (BlockTile)AdvancedRocketryBlocks.blockSpaceElevator);
		((ItemProjector)LibVulpesItems.itemHoloProjector).registerMachine(new TileBeacon(), (BlockTile)AdvancedRocketryBlocks.blockBeacon);
		//Extremely high teir machines
		((ItemProjector)LibVulpesItems.itemHoloProjector).registerMachine(new TileOrbitalLaserDrill(), (BlockTile)AdvancedRocketryBlocks.blockOrbitalLaserDrill);
		((ItemProjector)LibVulpesItems.itemHoloProjector).registerMachine(new TileWarpCore(), (BlockTile)AdvancedRocketryBlocks.blockWarpCore);
		((ItemProjector)LibVulpesItems.itemHoloProjector).registerMachine(new TileAreaGravityController(), (BlockTile)AdvancedRocketryBlocks.blockAreaGravityController);
		((ItemProjector)LibVulpesItems.itemHoloProjector).registerMachine(new TileAtmosphereTerraformer(), (BlockTile)AdvancedRocketryBlocks.blockTerraformer);

		proxy.registerEventHandlers();
		proxy.registerKeyBindings();
		//TODO: debug
		//ClientCommandHandler.instance.registerCommand(new Debugger());

		PlanetEventHandler handle = new PlanetEventHandler();
		MinecraftForge.EVENT_BUS.register(handle);

		//Wireless transceievers
		NetworkRegistry.registerData();

		InputSyncHandler inputSync = new InputSyncHandler();
		MinecraftForge.EVENT_BUS.register(inputSync);

		MinecraftForge.EVENT_BUS.register(new MapGenLander());
		AdvancedRocketryAPI.gravityManager = new GravityHandler();

		CompatibilityMgr.isSpongeInstalled = ModList.get().isLoaded("sponge");
		// End compat stuff

		MinecraftForge.EVENT_BUS.register(SpaceObjectManager.getSpaceManager());

		//Register space dimension
		DimensionManager.getInstance().registerSpaceDimension(DimensionManager.spaceId);

		ARConfiguration.loadPostInit();
		
		// Post init mapgen
		StructurePieceGeode.init();

		//Add the overworld as a discovered planet
		zmaster587.advancedRocketry.api.ARConfiguration.getCurrentConfig().initiallyKnownPlanets.add(Dimension.OVERWORLD.getLocation());
	}

	public void serverStarted(FMLServerStartedEvent event) {
		for (ResourceLocation dimId : DimensionManager.getInstance().getLoadedDimensions()) {
			DimensionProperties properties = DimensionManager.getInstance().getDimensionProperties(dimId);
			if(!properties.isNativeDimension && properties.getId() == zmaster587.advancedRocketry.api.ARConfiguration.getCurrentConfig().MoonId) {
				properties.isNativeDimension = true;
			}
		}
	}
	
	
	public void registerCommands(RegisterCommandsEvent event)
	{
		PlanetCommand.register(event.getDispatcher());
	}

	
	public void serverStarting(FMLServerAboutToStartEvent event) {
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


	
	public void serverStopped(FMLServerStoppedEvent event) {
		zmaster587.advancedRocketry.dimension.DimensionManager.getInstance().onServerStopped();
		//zmaster587.advancedRocketry.cable.NetworkRegistry.clearNetworks();
		SpaceObjectManager.getSpaceManager().onServerStopped();
		zmaster587.advancedRocketry.api.ARConfiguration.getCurrentConfig().MoonId = Constants.INVALID_PLANET;
		((BlockSeal)AdvancedRocketryBlocks.blockSeal).clearMap();
	}
}
