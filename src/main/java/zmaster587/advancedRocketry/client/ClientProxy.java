package zmaster587.advancedRocketry.client;

import java.util.LinkedList;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.color.IBlockColor;
import net.minecraft.client.renderer.color.IItemColor;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.model.ModelResourceLocation;
import net.minecraft.entity.Entity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.profiler.Profiler;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.client.event.ModelBakeEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.client.model.obj.OBJLoader;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fluids.IFluidBlock;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.client.registry.IRenderFactory;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import zmaster587.advancedRocketry.AdvancedRocketry;
import zmaster587.advancedRocketry.api.AdvancedRocketryBlocks;
import zmaster587.advancedRocketry.api.AdvancedRocketryEntities;
import zmaster587.advancedRocketry.api.AdvancedRocketryItems;
import zmaster587.advancedRocketry.api.AdvancedRocketryTileEntityType;
import zmaster587.advancedRocketry.api.stations.ISpaceObject;
import zmaster587.advancedRocketry.client.model.ModelRocket;
import zmaster587.advancedRocketry.client.render.*;
import zmaster587.advancedRocketry.client.render.RenderLaser;
import zmaster587.advancedRocketry.client.render.entity.*;
import zmaster587.advancedRocketry.client.render.multiblocks.*;
import zmaster587.advancedRocketry.common.CommonProxy;
import zmaster587.advancedRocketry.dimension.DimensionManager;
import zmaster587.advancedRocketry.entity.*;
import zmaster587.advancedRocketry.entity.fx.*;
import zmaster587.advancedRocketry.event.PlanetEventHandler;
import zmaster587.advancedRocketry.event.RocketEventHandler;
import zmaster587.advancedRocketry.stations.SpaceObjectManager;
import zmaster587.advancedRocketry.tile.TileFluidTank;
import zmaster587.advancedRocketry.tile.TileRocketBuilder;
import zmaster587.advancedRocketry.tile.cables.TileDataPipe;
import zmaster587.advancedRocketry.tile.cables.TileEnergyPipe;
import zmaster587.advancedRocketry.tile.cables.TileLiquidPipe;
import zmaster587.advancedRocketry.tile.multiblock.*;
import zmaster587.advancedRocketry.tile.multiblock.energy.TileBlackHoleGenerator;
import zmaster587.advancedRocketry.tile.multiblock.energy.TileMicrowaveReciever;
import zmaster587.advancedRocketry.tile.multiblock.machine.*;
import zmaster587.libVulpes.api.LibVulpesTileEntityTypes;
import zmaster587.libVulpes.entity.fx.FxErrorBlock;
import zmaster587.libVulpes.inventory.modules.ModuleContainerPan;
import zmaster587.libVulpes.tile.TileSchematic;

public class ClientProxy extends CommonProxy {

	private static zmaster587.advancedRocketry.dimension.DimensionManager dimensionManagerClient = new zmaster587.advancedRocketry.dimension.DimensionManager();
	
	@Override
	public void registerRenderers() {


		ClientRegistry.bindTileEntityRenderer(AdvancedRocketryTileEntityType.TILE_ROCKET_BUILDER, (dispatcher) -> {return new RendererRocketBuilder(dispatcher);} );
		//ClientRegistry.bindTileEntitySpecialRenderer(TileModelRender.class, modelBlock);
		ClientRegistry.bindTileEntityRenderer(AdvancedRocketryTileEntityType.TILE_PREC_ASS, (dispatcher) -> {return new RendererPrecisionAssembler(dispatcher);});
		ClientRegistry.bindTileEntityRenderer(AdvancedRocketryTileEntityType.TILE_CUTTING_MACHINE, (dispatcher) -> {return new RendererCuttingMachine(dispatcher);});
		ClientRegistry.bindTileEntityRenderer(AdvancedRocketryTileEntityType.TILE_CRYSTALLIZER, (dispatcher) -> {return new RendererCrystallizer(dispatcher);});
		ClientRegistry.bindTileEntityRenderer(AdvancedRocketryTileEntityType.TILE_OBSERVATORY, (dispatcher) -> {return new RendererObservatory(dispatcher);});
		ClientRegistry.bindTileEntityRenderer(AdvancedRocketryTileEntityType.TILE_ASTROBODY_DATA, (dispatcher) -> {return new RenderPlanetAnalyser(dispatcher);});
		ClientRegistry.bindTileEntityRenderer(AdvancedRocketryTileEntityType.TILE_LATHE, (dispatcher) -> {return new RendererLathe(dispatcher);});
		ClientRegistry.bindTileEntityRenderer(AdvancedRocketryTileEntityType.TILE_ROLLING, (dispatcher) -> {return new RendererRollingMachine(dispatcher);});
		ClientRegistry.bindTileEntityRenderer(AdvancedRocketryTileEntityType.TILE_ELECTROLYSER, (dispatcher) -> {return new RendererElectrolyser(dispatcher);});
		ClientRegistry.bindTileEntityRenderer(AdvancedRocketryTileEntityType.TILE_WARP_CORE, (dispatcher) -> {return new RendererWarpCore(dispatcher);});
		ClientRegistry.bindTileEntityRenderer(AdvancedRocketryTileEntityType.TILE_CHEMICAL_REACTOR, (dispatcher) -> {return new RendererChemicalReactor(dispatcher, "advancedrocketry:models/ChemicalReactor.obj", "advancedrocketry:textures/models/ChemicalReactor.png");});
		ClientRegistry.bindTileEntityRenderer(LibVulpesTileEntityTypes.TILE_SCHEMATIC, (dispatcher) -> {return new RendererPhantomBlock(dispatcher);});
		//ClientRegistry.bindTileEntitySpecialRenderer(TileDrill.class, new RendererDrill());
		//ClientRegistry.bindTileEntityRenderer(TileLiquidPipe.class, new RendererPipe(new ResourceLocation("AdvancedRocketry:textures/blocks/pipeLiquid.png")));
		//ClientRegistry.bindTileEntityRenderer(TileDataPipe.class, new RendererPipe(new ResourceLocation("AdvancedRocketry:textures/blocks/pipeData.png")));
		//ClientRegistry.bindTileEntityRenderer(TileEnergyPipe.class, new RendererPipe(new ResourceLocation("AdvancedRocketry:textures/blocks/pipeEnergy.png")));
		ClientRegistry.bindTileEntityRenderer(AdvancedRocketryTileEntityType.TILE_MICROWAVE_RECIEVER, (dispatcher) -> {return new RendererMicrowaveReciever(dispatcher);});
		ClientRegistry.bindTileEntityRenderer(AdvancedRocketryTileEntityType.TILE_SPACE_LASER, (dispatcher) -> {return new RenderLaserTile(dispatcher);});
		ClientRegistry.bindTileEntityRenderer(AdvancedRocketryTileEntityType.TILE_BIOME_SCANNER, (dispatcher) -> {return new RenderBiomeScanner(dispatcher);});
		ClientRegistry.bindTileEntityRenderer(AdvancedRocketryTileEntityType.TILE_BLACK_HOLE_GENERATOR, (dispatcher) -> {return new RenderBlackHoleEnergy(dispatcher);});
		ClientRegistry.bindTileEntityRenderer(AdvancedRocketryTileEntityType.TILE_TERRAFORMER, (dispatcher) -> {return new RenderTerraformerAtm(dispatcher);});
		ClientRegistry.bindTileEntityRenderer(AdvancedRocketryTileEntityType.TILE_FLUID_TANK, (dispatcher) -> {return new RenderTank(dispatcher);});
		ClientRegistry.bindTileEntityRenderer(AdvancedRocketryTileEntityType.TILE_RAILGUN, (dispatcher) -> {return new zmaster587.advancedRocketry.client.render.multiblocks.RendererRailgun(dispatcher);});
		ClientRegistry.bindTileEntityRenderer(AdvancedRocketryTileEntityType.TILE_GRAVITY_CONTROLLER, (dispatcher) -> {return new zmaster587.advancedRocketry.client.render.multiblocks.RenderGravityMachine(dispatcher);});
		ClientRegistry.bindTileEntityRenderer(AdvancedRocketryTileEntityType.TILE_SPACE_ELEVATOR, (dispatcher) -> {return new zmaster587.advancedRocketry.client.render.multiblocks.RendererSpaceElevator(dispatcher);});
		ClientRegistry.bindTileEntityRenderer(AdvancedRocketryTileEntityType.TILE_BEACON, (dispatcher) -> {return new zmaster587.advancedRocketry.client.render.multiblocks.RenderBeacon(dispatcher);});
		ClientRegistry.bindTileEntityRenderer(AdvancedRocketryTileEntityType.TILE_CENTRIFUGE, (dispatcher) -> {return new zmaster587.advancedRocketry.client.render.multiblocks.RenderCentrifuge(dispatcher);});

		//ClientRegistry.bindTileEntitySpecialRenderer(TileModelRenderRotatable.class, modelBlock);

		//RendererModelBlock blockRenderer = new RendererModelBlock();

		//RendererBucket bucket =  new RendererBucket();
		//MinecraftForgeClient.registerItemRenderer(AdvancedRocketryItems.itemBucketRocketFuel, bucket);
		//MinecraftForgeClient.registerItemRenderer(AdvancedRocketryItems.itemBucketNitrogen, bucket);
		//MinecraftForgeClient.registerItemRenderer(AdvancedRocketryItems.itemBucketHydrogen, bucket);
		//MinecraftForgeClient.registerItemRenderer(AdvancedRocketryItems.itemBucketOxygen, bucket);

		RenderingRegistry.registerEntityRenderingHandler(AdvancedRocketryEntities.ENTITY_ROCKET, (IRenderFactory<EntityRocket>)new RendererRocket(null));
		RenderingRegistry.registerEntityRenderingHandler(AdvancedRocketryEntities.ENTITY_LASER_NODE, (IRenderFactory<EntityLaserNode>)new RenderLaser(2.0, new float[] {1F, 0.25F, 0.25F, 0.2F}, new float[] {0.9F, 0.2F, 0.3F, 0.5F}));
		RenderingRegistry.registerEntityRenderingHandler(AdvancedRocketryEntities.ENTITY_ITEM_ABDUCTED, (IRenderFactory<EntityItemAbducted>)new RendererItem(Minecraft.getInstance().getRenderManager(), Minecraft.getInstance().getRenderItem()));
		RenderingRegistry.registerEntityRenderingHandler(AdvancedRocketryEntities.ENTITY_UIPLANET, (IRenderFactory<EntityUIPlanet>)new RenderPlanetUIEntity(null));
		RenderingRegistry.registerEntityRenderingHandler(AdvancedRocketryEntities.ENTITY_UIBUTTON, (IRenderFactory<EntityUIButton>)new RenderButtonUIEntity(null));
		RenderingRegistry.registerEntityRenderingHandler(AdvancedRocketryEntities.ENTITY_UISTAR, (IRenderFactory<EntityUIStar>)new RenderStarUIEntity(null));
		RenderingRegistry.registerEntityRenderingHandler(AdvancedRocketryEntities.ENTITY_ELEVATOR_CAPSULE, (IRenderFactory<EntityElevatorCapsule>)new RenderElevatorCapsule(null));
		RenderingRegistry.registerEntityRenderingHandler(AdvancedRocketryEntities.ENTITY_HOVER_CRAFT, (IRenderFactory<EntityHoverCraft>)new RenderHoverCraft(null));
	}

	@Override
	public void init() {

		//Colorizers
		Minecraft.getInstance().getItemColors().register(new IItemColor()
        {
			@Override
			public int getColor(ItemStack stack, int tintIndex) {
				return tintIndex > 0 ? -1 : ((ArmorItem)stack.getItem()).getColor(stack);
			}
        }, AdvancedRocketryItems.itemSpaceSuit_Boots, AdvancedRocketryItems.itemSpaceSuit_Chest, AdvancedRocketryItems.itemSpaceSuit_Helmet, AdvancedRocketryItems.itemSpaceSuit_Leggings);
		
		AdvancedRocketry.materialRegistry.init();
	}

	@Override
	public void preInitBlocks()
	{

		//TODO fluids
		registerFluidModel((IFluidBlock) AdvancedRocketryBlocks.blockOxygenFluid);
		registerFluidModel((IFluidBlock) AdvancedRocketryBlocks.blockNitrogenFluid);
		registerFluidModel((IFluidBlock) AdvancedRocketryBlocks.blockHydrogenFluid);
		registerFluidModel((IFluidBlock) AdvancedRocketryBlocks.blockFuelFluid);
		registerFluidModel((IFluidBlock) AdvancedRocketryBlocks.blockEnrichedLavaFluid);
	}
	
	@Override
	public void preInitItems()
	{
	}
	
	@Override
	public void preinit() {
		registerRenderers();
	}
	
	private void registerFluidModel(IFluidBlock fluidBlock) {
		Item item = Item.getItemFromBlock((Block) fluidBlock);

		ModelBakery.registerItemVariants(item);

		final ModelResourceLocation modelResourceLocation = new ModelResourceLocation("advancedrocketry:fluid", fluidBlock.getFluid().getName());
		
		//ModelLoader.setCustomMeshDefinition(item, MeshDefinitionFix.create(stack -> modelResourceLocation));

		
		StateMapperBase ignoreState = new FluidStateMapper(modelResourceLocation);
		ModelLoader.setCustomStateMapper((Block) fluidBlock, ignoreState);
		ModelLoader.setCustomMeshDefinition(item, new FluidItemMeshDefinition(modelResourceLocation));
		ModelBakery.registerItemVariants(item, modelResourceLocation);
	}
	
	private static class FluidStateMapper extends StateMapperBase {
		private final ModelResourceLocation fluidLocation;

		public FluidStateMapper(ModelResourceLocation fluidLocation) {
			this.fluidLocation = fluidLocation;
		}

		@Override
		protected ModelResourceLocation getModelResourceLocation(BlockState iBlockState) {
			return fluidLocation;
		}
	}

	private static class FluidItemMeshDefinition implements ItemMeshDefinition {
		private final ModelResourceLocation fluidLocation;

		public FluidItemMeshDefinition(ModelResourceLocation fluidLocation) {
			this.fluidLocation = fluidLocation;
		}

		@Override
		public ModelResourceLocation getModelLocation(ItemStack stack) {
			return fluidLocation;
		}
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
			PlanetEventHandler.runBurst(Minecraft.getInstance().world.getGameTime() + 20, 20);
		} catch (NullPointerException e) {}
	}

	@Override
	public void registerKeyBindings() {
		KeyBindings.init();
		MinecraftForge.EVENT_BUS.register(new KeyBindings());

	}

	@Override
	public Profiler getProfiler() {
		return Minecraft.getInstance().getProfiler();
	}

	@Override
	public void changeClientPlayerWorld(World world) {
		Minecraft.getInstance().player.world = world;
	}

	@Override
	public void spawnParticle(String particle, World world, double x, double y, double z, double motionX, double motionY, double motionZ) {
		//WTF how is == working?  Should be .equals
		if(particle == "rocketFlame") {
			RocketFx fx = new RocketFx(world, x, y, z, motionX, motionY, motionZ);
			Minecraft.getInstance().particles.addEffect(fx);
		}
		else if(particle == "smallRocketFlame") {
			RocketFx fx = new RocketFx(world, x, y, z, motionX, motionY, motionZ, 0.25f);
			Minecraft.getInstance().particles.addEffect(fx);
		}
		else if(particle == "rocketSmoke") {
			TrailFx fx = new TrailFx(world, x, y, z, motionX, motionY, motionZ);
			Minecraft.getInstance().particles.addEffect(fx);
		}
		else if(particle == "rocketSmokeInverse") {
			InverseTrailFx fx = new InverseTrailFx(world, x, y, z, motionX, motionY, motionZ);
			Minecraft.getInstance().particles.addEffect(fx);
		}
		else if(particle == "arc") {
			FxElectricArc fx = new FxElectricArc(world, x, y, z, motionX);
			Minecraft.getInstance().particles.addEffect(fx);
		}
		else if(particle == "smallLazer") {
			FxSkyLaser fx = new FxSkyLaser(world, x, y, z);
			Minecraft.getInstance().particles.addEffect(fx);
		}
		else if(particle == "errorBox") {
			FxErrorBlock fx = new FxErrorBlock(world, x, y, z);
			Minecraft.getInstance().particles.addEffect(fx);
		}
		else if(particle.equals("gravityEffect")) {
			FxGravityEffect fx = new FxGravityEffect(world, x, y, z, motionX, motionY, motionZ);
			Minecraft.getInstance().particles.addEffect(fx);
		}
		else
			world.spawnParticle(EnumParticleTypes.getByName(particle), x, y, z, motionX, motionY, motionZ);
	}

	@Override
	public void spawnLaser(Entity entity, Vector3d toPos) {
		FxLaser fx = new FxLaser(entity.world, toPos.x, toPos.y, toPos.z, entity);
		Minecraft.getInstance().particles.addEffect(fx);

		FxLaserHeat fx2 = new FxLaserHeat(entity.world,  toPos.x, toPos.y, toPos.z, 0.02f);
		Minecraft.getInstance().particles.addEffect(fx2);
		
		for(int i = 0; i < 4; i++) {
			FxLaserSpark fx3 = new FxLaserSpark(entity.world,  toPos.x, toPos.y, toPos.z, 
					.125 - entity.world.rand.nextFloat()/4f, .125 - entity.world.rand.nextFloat()/4f, .125 - entity.world.rand.nextFloat()/4f, .5f);
			Minecraft.getInstance().particles.addEffect(fx3);
		}
	}


	@Override
	public float calculateCelestialAngleSpaceStation() {
		Entity player = Minecraft.getInstance().player;
		try {
			return (float) SpaceObjectManager.getSpaceManager().getSpaceStationFromBlockCoords(player.getPosition()).getRotation(Direction.EAST);
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
			return Minecraft.getInstance().world.getGameTime();
		} catch (NullPointerException e) {
			return 0;
		}
	}
	
	@Override
	public void loadUILayout(Configuration config) {
		final String CLIENT = "Client";
		
		zmaster587.advancedRocketry.api.ARConfiguration.getCurrentConfig().lockUI = config.get(CLIENT, "lockUI", true, "If UI is not locked, the middle mouse can be used to drag certain AR UIs around the screen, positions are saved on hitting quit in the menu").getBoolean();
		
		config.addCustomCategoryComment(CLIENT, "UI locations can by set by clicking and dragging the middle mouse button ingame");
		RocketEventHandler.suitPanel.setRawX(config.get(CLIENT, "suitPanelX", 8).getInt());
		RocketEventHandler.suitPanel.setRawY(config.get(CLIENT, "suitPanelY", 8).getInt());
		RocketEventHandler.suitPanel.setSizeModeX(config.get(CLIENT, "suitPanelModeX", -1).getInt());
		RocketEventHandler.suitPanel.setSizeModeY(config.get(CLIENT, "suitPanelModeY", -1).getInt());
		
		RocketEventHandler.oxygenBar.setRawX(config.get(CLIENT, "oxygenBarX", -8).getInt());
		RocketEventHandler.oxygenBar.setRawY(config.get(CLIENT, "oxygenBarY", 57).getInt());
		RocketEventHandler.oxygenBar.setSizeModeX(config.get(CLIENT, "oxygenBarModeX", 0).getInt());
		RocketEventHandler.oxygenBar.setSizeModeY(config.get(CLIENT, "oxygenBarModeY", 1).getInt());
		
		RocketEventHandler.hydrogenBar.setRawX(config.get(CLIENT, "hydrogenBarX", -8).getInt());
		RocketEventHandler.hydrogenBar.setRawY(config.get(CLIENT, "hydrogenBarY", 74).getInt());
		RocketEventHandler.hydrogenBar.setSizeModeX(config.get(CLIENT, "hydrogenBarModeX", 0).getInt());
		RocketEventHandler.hydrogenBar.setSizeModeY(config.get(CLIENT, "hydrogenBarModeY", 1).getInt());
		
		RocketEventHandler.atmBar.setRawX(config.get(CLIENT, "atmBarX", 8).getInt());
		RocketEventHandler.atmBar.setRawY(config.get(CLIENT, "atmBarY", 27).getInt());
		RocketEventHandler.atmBar.setSizeModeX(config.get(CLIENT, "atmBarModeX", -1).getInt());
		RocketEventHandler.atmBar.setSizeModeY(config.get(CLIENT, "atmBarModeY", 1).getInt());
	}
	
	@Override
	public void saveUILayout(Configuration configuration) {
		final String CLIENT = "Client";
		configuration.get(CLIENT, "suitPanelX", 1).set(RocketEventHandler.suitPanel.getRawX());
		configuration.get(CLIENT, "suitPanelY", 1).set(RocketEventHandler.suitPanel.getRawY());
		configuration.get(CLIENT, "suitPanelModeX", 1).set(RocketEventHandler.suitPanel.getSizeModeX());
		configuration.get(CLIENT, "suitPanelModeY", 1).set(RocketEventHandler.suitPanel.getSizeModeY());
		
		configuration.get(CLIENT, "oxygenBarX", 1).set(RocketEventHandler.oxygenBar.getRawX());
		configuration.get(CLIENT, "oxygenBarY", 1).set(RocketEventHandler.oxygenBar.getRawY());
		configuration.get(CLIENT, "oxygenBarModeX", 1).set(RocketEventHandler.oxygenBar.getSizeModeX());
		configuration.get(CLIENT, "oxygenBarModeY", 1).set(RocketEventHandler.oxygenBar.getSizeModeY());
		
		configuration.get(CLIENT, "hydrogenBarX", 1).set(RocketEventHandler.hydrogenBar.getRawX());
		configuration.get(CLIENT, "hydrogenBarY", 1).set(RocketEventHandler.hydrogenBar.getRawY());
		configuration.get(CLIENT, "hydrogenBarModeX", 1).set(RocketEventHandler.hydrogenBar.getSizeModeX());
		configuration.get(CLIENT, "hydrogenBarModeY", 1).set(RocketEventHandler.hydrogenBar.getSizeModeY());
		
		configuration.get(CLIENT, "atmBarX", 1).set(RocketEventHandler.atmBar.getRawX());
		configuration.get(CLIENT, "atmBarY", 1).set(RocketEventHandler.atmBar.getRawY());
		configuration.get(CLIENT, "atmBarModeX", 1).set(RocketEventHandler.atmBar.getSizeModeX());
		configuration.get(CLIENT, "atmBarModeY", 1).set(RocketEventHandler.atmBar.getSizeModeY());
		configuration.save();
	}
	
	@Override
	public void displayMessage(String msg, int time) {
		RocketEventHandler.setOverlay(Minecraft.getInstance().world.getGameTime() + time, msg);
	}
	
	public String getNameFromBiome(Biome biome) {
		return biome.getBiomeName();
	}
	
	@Override
	public zmaster587.advancedRocketry.dimension.DimensionManager getDimensionManager() {
		return dimensionManagerClient;
	}
}
