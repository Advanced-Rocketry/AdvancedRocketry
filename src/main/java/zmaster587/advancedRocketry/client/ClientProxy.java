package zmaster587.advancedRocketry.client;

import net.minecraft.block.Block;
import net.minecraft.block.BlockColored;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.block.statemap.StateMapperBase;
import net.minecraft.client.renderer.color.IBlockColor;
import net.minecraft.client.renderer.color.IItemColor;
import net.minecraft.entity.Entity;
import net.minecraft.item.Item;
import net.minecraft.profiler.Profiler;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.client.MinecraftForgeClient;
import net.minecraftforge.client.event.ModelBakeEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.client.model.ModelLoaderRegistry;
import net.minecraftforge.client.model.obj.OBJLoader;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.client.registry.IRenderFactory;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import zmaster587.advancedRocketry.AdvancedRocketry;
import zmaster587.advancedRocketry.api.AdvancedRocketryBlocks;
import zmaster587.advancedRocketry.api.AdvancedRocketryItems;
import zmaster587.advancedRocketry.api.stations.ISpaceObject;
import zmaster587.advancedRocketry.block.BlockCrystal;
import zmaster587.advancedRocketry.block.CrystalColorizer;
import zmaster587.advancedRocketry.client.model.ModelRocket;
import zmaster587.advancedRocketry.client.render.RenderComponents;
import zmaster587.advancedRocketry.client.render.RenderLaser;
import zmaster587.advancedRocketry.client.render.RenderLaserTile;
import zmaster587.advancedRocketry.client.render.RenderTank;
import zmaster587.advancedRocketry.client.render.RendererPhantomBlock;
import zmaster587.advancedRocketry.client.render.RendererRocketBuilder;
import zmaster587.advancedRocketry.client.render.RendererRocket;
import zmaster587.advancedRocketry.client.render.RendererPipe;
import zmaster587.advancedRocketry.client.render.entity.RendererItem;
import zmaster587.advancedRocketry.client.render.multiblocks.RenderBiomeScanner;
import zmaster587.advancedRocketry.client.render.multiblocks.RenderPlanetAnalyser;
import zmaster587.advancedRocketry.client.render.multiblocks.RenderTerraformerAtm;
import zmaster587.advancedRocketry.client.render.multiblocks.RendererChemicalReactor;
import zmaster587.advancedRocketry.client.render.multiblocks.RendererElectrolyser;
import zmaster587.advancedRocketry.client.render.multiblocks.RendererMicrowaveReciever;
import zmaster587.advancedRocketry.client.render.multiblocks.RendererRollingMachine;
import zmaster587.advancedRocketry.client.render.multiblocks.RendererCrystallizer;
import zmaster587.advancedRocketry.client.render.multiblocks.RendererCuttingMachine;
import zmaster587.advancedRocketry.client.render.multiblocks.RendererLathe;
import zmaster587.advancedRocketry.client.render.multiblocks.RendererObservatory;
import zmaster587.advancedRocketry.client.render.multiblocks.RendererPrecisionAssembler;
import zmaster587.advancedRocketry.client.render.multiblocks.RendererWarpCore;
import zmaster587.advancedRocketry.common.CommonProxy;
import zmaster587.advancedRocketry.entity.EntityItemAbducted;
import zmaster587.advancedRocketry.entity.EntityLaserNode;
import zmaster587.advancedRocketry.entity.EntityRocket;
import zmaster587.advancedRocketry.entity.FxSkyLaser;
import zmaster587.advancedRocketry.entity.fx.FxElectricArc;
import zmaster587.advancedRocketry.entity.fx.FxLaser;
import zmaster587.advancedRocketry.entity.fx.FxLaserHeat;
import zmaster587.advancedRocketry.entity.fx.FxLaserSpark;
import zmaster587.advancedRocketry.entity.fx.InverseTrailFx;
import zmaster587.advancedRocketry.entity.fx.RocketFx;
import zmaster587.advancedRocketry.entity.fx.TrailFx;
import zmaster587.advancedRocketry.event.PlanetEventHandler;
import zmaster587.advancedRocketry.event.RocketEventHandler;
import zmaster587.advancedRocketry.stations.SpaceObjectManager;
import zmaster587.advancedRocketry.tile.TileDrill;
import zmaster587.advancedRocketry.tile.TileFluidTank;
import zmaster587.advancedRocketry.tile.TileRocketBuilder;
import zmaster587.advancedRocketry.tile.multiblock.TileSpaceLaser;
import zmaster587.advancedRocketry.tile.cables.TileDataPipe;
import zmaster587.advancedRocketry.tile.cables.TileEnergyPipe;
import zmaster587.advancedRocketry.tile.cables.TileLiquidPipe;
import zmaster587.advancedRocketry.tile.multiblock.TileAtmosphereTerraformer;
import zmaster587.advancedRocketry.tile.multiblock.TileBiomeScanner;
import zmaster587.advancedRocketry.tile.multiblock.TileObservatory;
import zmaster587.advancedRocketry.tile.multiblock.TileAstrobodyDataProcessor;
import zmaster587.advancedRocketry.tile.multiblock.TileWarpCore;
import zmaster587.advancedRocketry.tile.multiblock.energy.TileMicrowaveReciever;
import zmaster587.advancedRocketry.tile.multiblock.machine.TileChemicalReactor;
import zmaster587.advancedRocketry.tile.multiblock.machine.TileCrystallizer;
import zmaster587.advancedRocketry.tile.multiblock.machine.TileCuttingMachine;
import zmaster587.advancedRocketry.tile.multiblock.machine.TileElectrolyser;
import zmaster587.advancedRocketry.tile.multiblock.machine.TileLathe;
import zmaster587.advancedRocketry.tile.multiblock.machine.TilePrecisionAssembler;
import zmaster587.advancedRocketry.tile.multiblock.machine.TileRollingMachine;
import zmaster587.libVulpes.entity.fx.FxErrorBlock;
import zmaster587.libVulpes.inventory.modules.ModuleContainerPan;
import zmaster587.libVulpes.tile.TileSchematic;

public class ClientProxy extends CommonProxy {

	@Override
	public void registerRenderers() {


		ClientRegistry.bindTileEntitySpecialRenderer(TileRocketBuilder.class, new RendererRocketBuilder());
		//ClientRegistry.bindTileEntitySpecialRenderer(TileModelRender.class, modelBlock);
		ClientRegistry.bindTileEntitySpecialRenderer(TilePrecisionAssembler.class, new RendererPrecisionAssembler());
		ClientRegistry.bindTileEntitySpecialRenderer(TileCuttingMachine.class, new RendererCuttingMachine());
		ClientRegistry.bindTileEntitySpecialRenderer(TileCrystallizer.class, new RendererCrystallizer());
		ClientRegistry.bindTileEntitySpecialRenderer(TileObservatory.class, new RendererObservatory());
		ClientRegistry.bindTileEntitySpecialRenderer(TileAstrobodyDataProcessor.class, new RenderPlanetAnalyser());
		ClientRegistry.bindTileEntitySpecialRenderer(TileLathe.class, new RendererLathe());
		ClientRegistry.bindTileEntitySpecialRenderer(TileRollingMachine.class, new RendererRollingMachine());
		ClientRegistry.bindTileEntitySpecialRenderer(TileElectrolyser.class, new RendererElectrolyser());
		ClientRegistry.bindTileEntitySpecialRenderer(TileWarpCore.class, new RendererWarpCore());
		ClientRegistry.bindTileEntitySpecialRenderer(TileChemicalReactor.class, new RendererChemicalReactor("advancedrocketry:models/ChemicalReactor.obj", "advancedrocketry:textures/models/ChemicalReactor.png"));
		ClientRegistry.bindTileEntitySpecialRenderer(TileSchematic.class, new RendererPhantomBlock());
		//ClientRegistry.bindTileEntitySpecialRenderer(TileDrill.class, new RendererDrill());
		ClientRegistry.bindTileEntitySpecialRenderer(TileLiquidPipe.class, new RendererPipe(new ResourceLocation("AdvancedRocketry:textures/blocks/pipeLiquid.png")));
		ClientRegistry.bindTileEntitySpecialRenderer(TileDataPipe.class, new RendererPipe(new ResourceLocation("AdvancedRocketry:textures/blocks/pipeData.png")));
		ClientRegistry.bindTileEntitySpecialRenderer(TileEnergyPipe.class, new RendererPipe(new ResourceLocation("AdvancedRocketry:textures/blocks/pipeEnergy.png")));
		ClientRegistry.bindTileEntitySpecialRenderer(TileMicrowaveReciever.class, new RendererMicrowaveReciever());
		ClientRegistry.bindTileEntitySpecialRenderer(TileSpaceLaser.class, new RenderLaserTile());
		ClientRegistry.bindTileEntitySpecialRenderer(TileBiomeScanner.class, new RenderBiomeScanner());
		ClientRegistry.bindTileEntitySpecialRenderer(TileAtmosphereTerraformer.class, new RenderTerraformerAtm());
		ClientRegistry.bindTileEntitySpecialRenderer(TileFluidTank.class, new RenderTank());
		ClientRegistry.bindTileEntitySpecialRenderer(zmaster587.advancedRocketry.tile.multiblock.TileSpaceLaser.class, new zmaster587.advancedRocketry.client.render.multiblocks.RenderLaser());
		ClientRegistry.bindTileEntitySpecialRenderer(zmaster587.advancedRocketry.tile.multiblock.TileRailgun.class, new zmaster587.advancedRocketry.client.render.multiblocks.RendererRailgun());

		//ClientRegistry.bindTileEntitySpecialRenderer(TileModelRenderRotatable.class, modelBlock);

		//RendererModelBlock blockRenderer = new RendererModelBlock();

		//MinecraftForgeClient.registerItemRenderer(Item.getItemFromBlock(AdvancedRocketryBlocks.blockSawBlade), blockRenderer);
		//MinecraftForgeClient.registerItemRenderer(Item.getItemFromBlock(AdvancedRocketryBlocks.blockEngine), blockRenderer);
		//MinecraftForgeClient.registerItemRenderer(Item.getItemFromBlock(AdvancedRocketryBlocks.blockFuelTank), blockRenderer);
		//MinecraftForgeClient.registerItemRenderer(Item.getItemFromBlock(AdvancedRocketryBlocks.blockMotor), blockRenderer);
		//RendererBucket bucket =  new RendererBucket();
		//MinecraftForgeClient.registerItemRenderer(AdvancedRocketryItems.itemBucketRocketFuel, bucket);
		//MinecraftForgeClient.registerItemRenderer(AdvancedRocketryItems.itemBucketNitrogen, bucket);
		//MinecraftForgeClient.registerItemRenderer(AdvancedRocketryItems.itemBucketHydrogen, bucket);
		//MinecraftForgeClient.registerItemRenderer(AdvancedRocketryItems.itemBucketOxygen, bucket);

		RenderingRegistry.registerEntityRenderingHandler(EntityRocket.class, (IRenderFactory<EntityRocket>)new RendererRocket(null));
		RenderingRegistry.registerEntityRenderingHandler(EntityLaserNode.class, (IRenderFactory<Entity>)new RenderLaser(2.0, new float[] {1F, 0.25F, 0.25F, 0.2F}, new float[] {0.9F, 0.2F, 0.3F, 0.5F}));
		RenderingRegistry.registerEntityRenderingHandler(EntityItemAbducted.class, (IRenderFactory<EntityItemAbducted>)new RendererItem(Minecraft.getMinecraft().getRenderManager(), Minecraft.getMinecraft().getRenderItem()));
	}

	@Override
	public void init() {

		//Colorizers
		CrystalColorizer colorizer = new CrystalColorizer();
		Minecraft.getMinecraft().getBlockColors().registerBlockColorHandler((IBlockColor)colorizer, new Block[] {AdvancedRocketryBlocks.blockCrystal});
		Minecraft.getMinecraft().getItemColors().registerItemColorHandler((IItemColor)colorizer,  Item.getItemFromBlock(AdvancedRocketryBlocks.blockCrystal));

		AdvancedRocketry.materialRegistry.init();
	}

	@Override
	public void preinit() {
		OBJLoader.INSTANCE.addDomain("advancedrocketry");
		registerRenderers();
		//Register Block models
		Item blockItem = Item.getItemFromBlock(AdvancedRocketryBlocks.blockLoader);
		ModelLoader.setCustomModelResourceLocation(blockItem, 0, new ModelResourceLocation("advancedrocketry:databus", "inventory"));
		ModelLoader.setCustomModelResourceLocation(blockItem, 1, new ModelResourceLocation("advancedrocketry:satelliteHatch", "inventory"));
		ModelLoader.setCustomModelResourceLocation(blockItem, 2, new ModelResourceLocation("libvulpes:inputHatch", "inventory"));
		ModelLoader.setCustomModelResourceLocation(blockItem, 3, new ModelResourceLocation("libvulpes:outputHatch", "inventory"));
		ModelLoader.setCustomModelResourceLocation(blockItem, 4, new ModelResourceLocation("libvulpes:fluidInputHatch", "inventory"));
		ModelLoader.setCustomModelResourceLocation(blockItem, 5, new ModelResourceLocation("libvulpes:fluidOutputHatch", "inventory"));

		blockItem = Item.getItemFromBlock(AdvancedRocketryBlocks.blockCrystal);
		for(int i = 0; i < BlockCrystal.numMetas; i++)
			ModelLoader.setCustomModelResourceLocation(blockItem, i, new ModelResourceLocation("advancedrocketry:crystal", "inventory"));


		blockItem = Item.getItemFromBlock(AdvancedRocketryBlocks.blockAirLock);

		blockItem = Item.getItemFromBlock(AdvancedRocketryBlocks.blockLaunchpad);
		ModelLoader.setCustomModelResourceLocation(blockItem, 0, new ModelResourceLocation("advancedrocketry:launchpad_all", "inventory"));

		blockItem = Item.getItemFromBlock(AdvancedRocketryBlocks.blockPlatePress);
		ModelLoader.setCustomModelResourceLocation(blockItem, 0, new ModelResourceLocation("advancedrocketry:platePress", "inventory"));

		//Register Item models
		ModelLoader.setCustomModelResourceLocation(AdvancedRocketryItems.itemSatellitePrimaryFunction, 0, new ModelResourceLocation("advancedrocketry:opticalSensor", "inventory"));
		ModelLoader.setCustomModelResourceLocation(AdvancedRocketryItems.itemSatellitePrimaryFunction, 1, new ModelResourceLocation("advancedrocketry:compositionSensor", "inventory"));
		ModelLoader.setCustomModelResourceLocation(AdvancedRocketryItems.itemSatellitePrimaryFunction, 2, new ModelResourceLocation("advancedrocketry:massDetector", "inventory"));
		ModelLoader.setCustomModelResourceLocation(AdvancedRocketryItems.itemSatellitePrimaryFunction, 3, new ModelResourceLocation("advancedrocketry:microwaveTransmitter", "inventory"));
		ModelLoader.setCustomModelResourceLocation(AdvancedRocketryItems.itemSatellitePrimaryFunction, 4, new ModelResourceLocation("advancedrocketry:oreMapper", "inventory"));
		ModelLoader.setCustomModelResourceLocation(AdvancedRocketryItems.itemSatellitePrimaryFunction, 5, new ModelResourceLocation("advancedrocketry:biomeChangerSat", "inventory"));

		ModelLoader.setCustomModelResourceLocation(AdvancedRocketryItems.itemIC, 0, new ModelResourceLocation("advancedrocketry:basicCircuit", "inventory"));
		ModelLoader.setCustomModelResourceLocation(AdvancedRocketryItems.itemIC, 1, new ModelResourceLocation("advancedrocketry:trackingCircuit", "inventory"));
		ModelLoader.setCustomModelResourceLocation(AdvancedRocketryItems.itemIC, 2, new ModelResourceLocation("advancedrocketry:advancedCircuit", "inventory"));
		ModelLoader.setCustomModelResourceLocation(AdvancedRocketryItems.itemIC, 3, new ModelResourceLocation("advancedrocketry:controlIOCircuit", "inventory"));
		ModelLoader.setCustomModelResourceLocation(AdvancedRocketryItems.itemIC, 4, new ModelResourceLocation("advancedrocketry:itemIOCircuit", "inventory"));
		ModelLoader.setCustomModelResourceLocation(AdvancedRocketryItems.itemIC, 5, new ModelResourceLocation("advancedrocketry:liquidIOCircuit", "inventory"));

		ModelLoader.setCustomModelResourceLocation(AdvancedRocketryItems.itemMisc, 0, new ModelResourceLocation("advancedrocketry:userInterface", "inventory"));
		ModelLoader.setCustomModelResourceLocation(AdvancedRocketryItems.itemMisc, 1, new ModelResourceLocation("advancedrocketry:miscpart1", "inventory"));

		ModelLoader.setCustomModelResourceLocation(AdvancedRocketryItems.itemUpgrade, 0, new ModelResourceLocation("advancedrocketry:hoverUpgrade", "inventory"));
		ModelLoader.setCustomModelResourceLocation(AdvancedRocketryItems.itemUpgrade, 1, new ModelResourceLocation("advancedrocketry:flightSpeedUpgrade", "inventory"));
		ModelLoader.setCustomModelResourceLocation(AdvancedRocketryItems.itemUpgrade, 2, new ModelResourceLocation("advancedrocketry:bionicLegs", "inventory"));
		ModelLoader.setCustomModelResourceLocation(AdvancedRocketryItems.itemUpgrade, 3, new ModelResourceLocation("advancedrocketry:landingBoots", "inventory"));
		ModelLoader.setCustomModelResourceLocation(AdvancedRocketryItems.itemUpgrade, 4, new ModelResourceLocation("advancedrocketry:antiFogVisor", "inventory"));

		ModelLoader.setCustomModelResourceLocation(AdvancedRocketryItems.itemSatellitePowerSource, 0, new ModelResourceLocation("advancedrocketry:basicSolarPanel", "inventory"));
		ModelLoader.setCustomModelResourceLocation(AdvancedRocketryItems.itemSatellitePowerSource, 1, new ModelResourceLocation("advancedrocketry:advancedSolarPanel", "inventory"));

		ModelLoader.setCustomModelResourceLocation(AdvancedRocketryItems.itemLens, 0, new ModelResourceLocation("advancedrocketry:basicLens", "inventory"));

		ModelLoader.setCustomModelResourceLocation(AdvancedRocketryItems.itemWafer, 0, new ModelResourceLocation("advancedrocketry:siliconWafer", "inventory"));

		ModelLoader.setCustomModelResourceLocation(AdvancedRocketryItems.itemSpaceStation, 0, new ModelResourceLocation("advancedrocketry:spaceStation", "inventory"));

		ModelLoader.setCustomModelResourceLocation(AdvancedRocketryItems.itemBucketHydrogen, 0, new ModelResourceLocation("advancedrocketry:bucketHydrogen", "inventory"));
		ModelLoader.setCustomModelResourceLocation(AdvancedRocketryItems.itemBucketOxygen, 0, new ModelResourceLocation("advancedrocketry:bucketOxygen", "inventory"));
		ModelLoader.setCustomModelResourceLocation(AdvancedRocketryItems.itemBucketNitrogen, 0, new ModelResourceLocation("advancedrocketry:bucketNitrogen", "inventory"));
		ModelLoader.setCustomModelResourceLocation(AdvancedRocketryItems.itemBucketRocketFuel, 0, new ModelResourceLocation("advancedrocketry:bucketRocketFuel", "inventory"));


		ModelLoader.setCustomModelResourceLocation(AdvancedRocketryItems.itemSpaceSuit_Chest, 0, new ModelResourceLocation("advancedrocketry:spaceChestplate", "inventory"));
		ModelLoader.setCustomModelResourceLocation(AdvancedRocketryItems.itemSpaceSuit_Helmet, 0, new ModelResourceLocation("advancedrocketry:spaceHelmet", "inventory"));
		ModelLoader.setCustomModelResourceLocation(AdvancedRocketryItems.itemSpaceSuit_Boots, 0, new ModelResourceLocation("advancedrocketry:spaceBoots", "inventory"));
		ModelLoader.setCustomModelResourceLocation(AdvancedRocketryItems.itemSpaceSuit_Leggings, 0, new ModelResourceLocation("advancedrocketry:spaceLeggings", "inventory"));
		ModelLoader.setCustomModelResourceLocation(AdvancedRocketryItems.itemQuartzCrucible, 0, new ModelResourceLocation("advancedrocketry:iquartzCrucible", "inventory"));
		ModelLoader.setCustomModelResourceLocation(AdvancedRocketryItems.itemDataUnit, 0, new ModelResourceLocation("advancedrocketry:dataStorageUnit", "inventory"));
		ModelLoader.setCustomModelResourceLocation(AdvancedRocketryItems.itemSatelliteIdChip, 0, new ModelResourceLocation("advancedrocketry:satelliteIdChip", "inventory"));
		ModelLoader.setCustomModelResourceLocation(AdvancedRocketryItems.itemPlanetIdChip, 0, new ModelResourceLocation("advancedrocketry:planetIdChip", "inventory"));
		ModelLoader.setCustomModelResourceLocation(AdvancedRocketryItems.itemSpaceStationChip, 0, new ModelResourceLocation("advancedrocketry:asteroidChip", "inventory"));
		ModelLoader.setCustomModelResourceLocation(AdvancedRocketryItems.itemSawBlade, 0, new ModelResourceLocation("advancedrocketry:sawBladeIron", "inventory"));
		ModelLoader.setCustomModelResourceLocation(AdvancedRocketryItems.itemOreScanner, 0, new ModelResourceLocation("advancedrocketry:oreScanner", "inventory"));
		ModelLoader.setCustomModelResourceLocation(AdvancedRocketryItems.itemSatellite, 0, new ModelResourceLocation("advancedrocketry:satellite", "inventory"));

		ModelLoader.setCustomModelResourceLocation(AdvancedRocketryItems.itemCarbonScrubberCartridge, 0, new ModelResourceLocation("advancedrocketry:carbonCartridge", "inventory"));
		ModelLoader.setCustomModelResourceLocation(AdvancedRocketryItems.itemSealDetector, 0, new ModelResourceLocation("advancedrocketry:sealDetector", "inventory"));
		ModelLoader.setCustomModelResourceLocation(AdvancedRocketryItems.itemJackhammer, 0, new ModelResourceLocation("advancedrocketry:jackHammer", "inventory"));
		ModelLoader.setCustomModelResourceLocation(AdvancedRocketryItems.itemAsteroidChip, 0, new ModelResourceLocation("advancedrocketry:asteroidChip", "inventory"));
		ModelLoader.setCustomModelResourceLocation(AdvancedRocketryItems.itemJetpack, 0, new ModelResourceLocation("advancedrocketry:jetPack", "inventory"));
		ModelLoader.setCustomModelResourceLocation(AdvancedRocketryItems.itemAtmAnalyser, 0, new ModelResourceLocation("advancedrocketry:atmAnalyser", "inventory"));
		ModelLoader.setCustomModelResourceLocation(AdvancedRocketryItems.itemBiomeChanger, 0, new ModelResourceLocation("advancedrocketry:biomeChanger", "inventory"));
		ModelLoader.setCustomModelResourceLocation(AdvancedRocketryItems.itemSmallAirlockDoor, 0, new ModelResourceLocation("advancedrocketry:smallAirlockDoor", "inventory"));

		ModelLoader.setCustomModelResourceLocation(AdvancedRocketryItems.itemCircuitPlate, 0, new ModelResourceLocation("advancedrocketry:basicCircuitPlate", "inventory"));
		ModelLoader.setCustomModelResourceLocation(AdvancedRocketryItems.itemCircuitPlate, 1, new ModelResourceLocation("advancedrocketry:advancedCircuitPlate", "inventory"));

		ModelLoader.setCustomModelResourceLocation(AdvancedRocketryItems.itemPressureTank, 0, new ModelResourceLocation("advancedrocketry:pressureTank0", "inventory"));
		ModelLoader.setCustomModelResourceLocation(AdvancedRocketryItems.itemPressureTank, 1, new ModelResourceLocation("advancedrocketry:pressureTank1", "inventory"));
		ModelLoader.setCustomModelResourceLocation(AdvancedRocketryItems.itemPressureTank, 2, new ModelResourceLocation("advancedrocketry:pressureTank2", "inventory"));
		ModelLoader.setCustomModelResourceLocation(AdvancedRocketryItems.itemPressureTank, 3, new ModelResourceLocation("advancedrocketry:pressureTank3", "inventory"));

		ModelLoader.setCustomModelResourceLocation(AdvancedRocketryItems.itemBasicLaserGun, 0, new ModelResourceLocation("advancedrocketry:basicLaserGun", "inventory"));


		//TODO fluids
		/*ModelLoader.setCustomMeshDefinition(Item, new ItemMeshDefinition() {

		@Override
		public ModelResourceLocation getModelLocation(ItemStack stack) {
			return new ModelResourceLocation("advancedRocketry:fluid", "rocketFuel");
		}
	});*/


		/*Item item = Item.getItemFromBlock((Block) AdvancedRocketryBlocks.blockFuelFluid);
	ModelBakery.registerItemVariants(item, );
	ModelResourceLocation modeEgylResourceLocation = new ModelResourceLocation(FLUID_MODEL_PATH, fluidBlock.getFluid().getName());
	ModelLoader.setCustomMeshDefinition(AdvancedRocketryItems.itemBucketRocketFuel, new ItemMeshDefinition() {
		@Override
		public ModelResourceLocation getModelLocation(ItemStack stack) {
			FluidStack fluidStack = AdvancedRocketryItems.itemBucketRocketFuel.getFluid(stack);
			return fluidStack != null ? new ModelResourceLocation("advancedrocketry:bucket/" + fluidStack.getFluid().getName(), "inventory") : null;
		}
	});
	//Register Fluid Block
	ModelLoader.setCustomStateMapper(AdvancedRocketryBlocks.blockFuelFluid, new StateMapperBase() {

		@Override
		protected ModelResourceLocation getModelResourceLocation(IBlockState state) {
			return new ModelResourceLocation("advancedrocketry:fluid");
		}
	});*/
	}

	@SubscribeEvent
	public void modelBakeEvent(ModelBakeEvent event) {
		Object object =  event.getModelRegistry().getObject(ModelRocket.resource);
		if (object instanceof IBakedModel) {
			IBakedModel existingModel = (IBakedModel)object;
			ModelRocket customModel = new ModelRocket();
			event.getModelRegistry().putObject(ModelRocket.resource, existingModel);
		}
	}

	@Override
	public void registerEventHandlers() {
		super.registerEventHandlers();
		MinecraftForge.EVENT_BUS.register(new RocketEventHandler());

		MinecraftForge.EVENT_BUS.register(ModuleContainerPan.class);
		MinecraftForge.EVENT_BUS.register(new RenderComponents());
	}

	@Override
	public void fireFogBurst(ISpaceObject station) {
		try {
			PlanetEventHandler.runBurst(Minecraft.getMinecraft().theWorld.getTotalWorldTime() + 20, 20);
		} catch (NullPointerException e) {}
	}

	@Override
	public void registerKeyBindings() {
		KeyBindings.init();
		MinecraftForge.EVENT_BUS.register(new KeyBindings());

	}

	@Override
	public Profiler getProfiler() {
		return Minecraft.getMinecraft().mcProfiler;
	}

	@Override
	public void changeClientPlayerWorld(World world) {
		Minecraft.getMinecraft().thePlayer.worldObj = world;
	}

	@Override
	public void spawnParticle(String particle, World world, double x, double y, double z, double motionX, double motionY, double motionZ) {
		if(particle == "rocketFlame") {
			RocketFx fx = new RocketFx(world, x, y, z, motionX, motionY, motionZ);
			Minecraft.getMinecraft().effectRenderer.addEffect(fx);
		}
		else if(particle == "smallRocketFlame") {
			RocketFx fx = new RocketFx(world, x, y, z, motionX, motionY, motionZ, 0.25f);
			Minecraft.getMinecraft().effectRenderer.addEffect(fx);
		}
		else if(particle == "rocketSmoke") {
			TrailFx fx = new TrailFx(world, x, y, z, motionX, motionY, motionZ);
			Minecraft.getMinecraft().effectRenderer.addEffect(fx);
		}
		else if(particle == "rocketSmokeInverse") {
			InverseTrailFx fx = new InverseTrailFx(world, x, y, z, motionX, motionY, motionZ);
			Minecraft.getMinecraft().effectRenderer.addEffect(fx);
		}
		else if(particle == "arc") {
			FxElectricArc fx = new FxElectricArc(world, x, y, z, motionX);
			Minecraft.getMinecraft().effectRenderer.addEffect(fx);
		}
		else if(particle == "smallLazer") {
			FxSkyLaser fx = new FxSkyLaser(world, x, y, z);
			Minecraft.getMinecraft().effectRenderer.addEffect(fx);
		}
		else if(particle == "errorBox") {
			FxErrorBlock fx = new FxErrorBlock(world, x, y, z);
			Minecraft.getMinecraft().effectRenderer.addEffect(fx);
		}
	}

	@Override
	public void spawnLaser(Entity entity, Vec3d toPos) {
		FxLaser fx = new FxLaser(entity.worldObj, toPos.xCoord, toPos.yCoord, toPos.zCoord, entity);
		Minecraft.getMinecraft().effectRenderer.addEffect(fx);

		FxLaserHeat fx2 = new FxLaserHeat(entity.worldObj,  toPos.xCoord, toPos.yCoord, toPos.zCoord, 0.02f);
		Minecraft.getMinecraft().effectRenderer.addEffect(fx2);
		
		for(int i = 0; i < 4; i++) {
			FxLaserSpark fx3 = new FxLaserSpark(entity.worldObj,  toPos.xCoord, toPos.yCoord, toPos.zCoord, 
					.125 - entity.worldObj.rand.nextFloat()/4f, .125 - entity.worldObj.rand.nextFloat()/4f, .125 - entity.worldObj.rand.nextFloat()/4f, .5f);
			Minecraft.getMinecraft().effectRenderer.addEffect(fx3);
		}
	}


	@Override
	public float calculateCelestialAngleSpaceStation() {
		Entity player = Minecraft.getMinecraft().thePlayer;
		try {
			return (float) SpaceObjectManager.getSpaceManager().getSpaceStationFromBlockCoords(player.getPosition()).getRotation();
		} catch (NullPointerException e) {

			/*While waiting for network packets various variables required to continue with rendering may be null,
			 * it would be impractical to check them all
			 * This is kinda hacky but I cannot find a better solution for the time being
			 */
			return 0;
		}
	}

	@Override
	public long getWorldTimeUniversal(int id) {
		try {
			return Minecraft.getMinecraft().theWorld.getTotalWorldTime();
		} catch (NullPointerException e) {
			return 0;
		}
	}
}
