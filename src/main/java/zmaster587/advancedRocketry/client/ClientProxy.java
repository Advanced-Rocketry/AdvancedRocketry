package zmaster587.advancedRocketry.client;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.RenderTypeLookup;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.item.IDyeableArmorItem;
import net.minecraft.particles.BasicParticleType;
import net.minecraft.profiler.IProfiler;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import zmaster587.advancedRocketry.AdvancedRocketry;
import zmaster587.advancedRocketry.api.AdvancedRocketryBiomes;
import zmaster587.advancedRocketry.api.AdvancedRocketryBlocks;
import zmaster587.advancedRocketry.api.AdvancedRocketryEntities;
import zmaster587.advancedRocketry.api.AdvancedRocketryItems;
import zmaster587.advancedRocketry.api.AdvancedRocketryParticleTypes;
import zmaster587.advancedRocketry.api.AdvancedRocketryTileEntityType;
import zmaster587.advancedRocketry.api.stations.ISpaceObject;
import zmaster587.advancedRocketry.client.render.*;
import zmaster587.advancedRocketry.client.render.entity.*;
import zmaster587.advancedRocketry.client.render.multiblocks.*;
import zmaster587.advancedRocketry.common.CommonProxy;
import zmaster587.advancedRocketry.entity.*;
import zmaster587.advancedRocketry.entity.fx.*;
import zmaster587.advancedRocketry.event.PlanetEventHandler;
import zmaster587.advancedRocketry.event.RocketEventHandler;
import zmaster587.advancedRocketry.stations.SpaceObjectManager;
import zmaster587.libVulpes.api.LibVulpesTileEntityTypes;
import zmaster587.libVulpes.entity.fx.FxErrorBlock;
import zmaster587.libVulpes.inventory.modules.ModuleContainerPan;

public class ClientProxy extends CommonProxy {

	private static zmaster587.advancedRocketry.dimension.DimensionManager dimensionManagerClient = new zmaster587.advancedRocketry.dimension.DimensionManager();

	public void initDeferredRegistries() {
		AdvancedRocketryParticleTypes.init();
	}

	@Override
	public void registerRenderers() {
		ClientRegistry.bindTileEntityRenderer(AdvancedRocketryTileEntityType.TILE_ROCKET_ASSEMBLER, RendererRocketAssemblingMachine::new);
		ClientRegistry.bindTileEntityRenderer(AdvancedRocketryTileEntityType.TILE_STATION_BUILDER, RendererRocketAssemblingMachine::new);
		ClientRegistry.bindTileEntityRenderer(AdvancedRocketryTileEntityType.TILE_PRECISION_ASSEMBLER, RendererPrecisionAssembler::new);
		ClientRegistry.bindTileEntityRenderer(AdvancedRocketryTileEntityType.TILE_CUTTING_MACHINE, RendererCuttingMachine::new);
		ClientRegistry.bindTileEntityRenderer(AdvancedRocketryTileEntityType.TILE_CRYSTALLIZER, RendererCrystallizer::new);
		ClientRegistry.bindTileEntityRenderer(AdvancedRocketryTileEntityType.TILE_OBSERVATORY, RendererObservatory::new);
		ClientRegistry.bindTileEntityRenderer(AdvancedRocketryTileEntityType.TILE_ASTROBODY_DATA_PROCESSOR, RenderAstrobodyDataProcessor::new);
		ClientRegistry.bindTileEntityRenderer(AdvancedRocketryTileEntityType.TILE_LATHE, RendererLathe::new);
		ClientRegistry.bindTileEntityRenderer(AdvancedRocketryTileEntityType.TILE_ROLLING, RendererRollingMachine::new);
		ClientRegistry.bindTileEntityRenderer(AdvancedRocketryTileEntityType.TILE_ELECTROLYZER, RendererElectrolyser::new);
		ClientRegistry.bindTileEntityRenderer(AdvancedRocketryTileEntityType.TILE_WARP_CORE, RendererWarpCore::new);
		ClientRegistry.bindTileEntityRenderer(AdvancedRocketryTileEntityType.TILE_CHEMICAL_REACTOR, RendererChemicalReactor::new);
		ClientRegistry.bindTileEntityRenderer(LibVulpesTileEntityTypes.TILE_SCHEMATIC, RendererPhantomBlock::new);
		//ClientRegistry.bindTileEntityRenderer(TileLiquidPipe.class, new RendererPipe(new ResourceLocation("AdvancedRocketry:textures/blocks/pipeLiquid.png")));
		//ClientRegistry.bindTileEntityRenderer(TileDataPipe.class, new RendererPipe(new ResourceLocation("AdvancedRocketry:textures/blocks/pipeData.png")));
		//ClientRegistry.bindTileEntityRenderer(TileEnergyPipe.class, new RendererPipe(new ResourceLocation("AdvancedRocketry:textures/blocks/pipeEnergy.png")));
		ClientRegistry.bindTileEntityRenderer(AdvancedRocketryTileEntityType.TILE_MICROWAVE_RECEIVER, RendererMicrowaveReciever::new);
		ClientRegistry.bindTileEntityRenderer(AdvancedRocketryTileEntityType.TILE_ORBITAL_LASER_DRILL, RenderOrbitalLaserDrill::new);
		ClientRegistry.bindTileEntityRenderer(AdvancedRocketryTileEntityType.TILE_BIOME_SCANNER, RenderBiomeScanner::new);
		ClientRegistry.bindTileEntityRenderer(AdvancedRocketryTileEntityType.TILE_BLACK_HOLE_GENERATOR, RenderBlackHoleGenerator::new);
		ClientRegistry.bindTileEntityRenderer(AdvancedRocketryTileEntityType.TILE_TERRAFORMER, RenderTerraformer::new);
		ClientRegistry.bindTileEntityRenderer(AdvancedRocketryTileEntityType.TILE_FLUID_TANK, RenderTank::new);
		ClientRegistry.bindTileEntityRenderer(AdvancedRocketryTileEntityType.TILE_RAILGUN, RendererRailgun::new);
		ClientRegistry.bindTileEntityRenderer(AdvancedRocketryTileEntityType.TILE_AREA_GRAVITY_CONTROLLER, RenderAreaGravityController::new);
		ClientRegistry.bindTileEntityRenderer(AdvancedRocketryTileEntityType.TILE_SPACE_ELEVATOR, RendererSpaceElevator::new);
		ClientRegistry.bindTileEntityRenderer(AdvancedRocketryTileEntityType.TILE_BEACON, RenderBeacon::new);
		ClientRegistry.bindTileEntityRenderer(AdvancedRocketryTileEntityType.TILE_CENTRIFUGE, RenderCentrifuge::new);
		ClientRegistry.bindTileEntityRenderer(AdvancedRocketryTileEntityType.TILE_PREC_LASER_ETCHER, RendererPrecisionLaserEtcher::new);
		ClientRegistry.bindTileEntityRenderer(AdvancedRocketryTileEntityType.TILE_SOLAR_ARRAY, RendererSolarArray::new);

		RenderingRegistry.registerEntityRenderingHandler(AdvancedRocketryEntities.ENTITY_ROCKET, new RendererRocket(null));
		RenderingRegistry.registerEntityRenderingHandler(AdvancedRocketryEntities.ENTITY_LASER_NODE, new RenderLaser(2.0, new float[] {1F, 0.25F, 0.25F, 0.2F}, new float[] {0.9F, 0.2F, 0.3F, 0.5F}));
		RenderingRegistry.registerEntityRenderingHandler(AdvancedRocketryEntities.ENTITY_ITEM_ABDUCTED, new RendererItem(Minecraft.getInstance().getRenderManager()));
		RenderingRegistry.registerEntityRenderingHandler(AdvancedRocketryEntities.ENTITY_UIPLANET, new RenderPlanetUIEntity(null));
		RenderingRegistry.registerEntityRenderingHandler(AdvancedRocketryEntities.ENTITY_UIBUTTON, new RenderButtonUIEntity(null));
		RenderingRegistry.registerEntityRenderingHandler(AdvancedRocketryEntities.ENTITY_UISTAR, new RenderStarUIEntity(null));
		RenderingRegistry.registerEntityRenderingHandler(AdvancedRocketryEntities.ENTITY_ELEVATOR_CAPSULE, new RenderElevatorCapsule(null));
		RenderingRegistry.registerEntityRenderingHandler(AdvancedRocketryEntities.ENTITY_HOVER_CRAFT, new RenderHoverCraft(null));
		RenderingRegistry.registerEntityRenderingHandler(AdvancedRocketryEntities.ENTITY_DUMMY, new RenderSeat(null));
	}

	@Override
	public void init() {
		//Colorizers
		Minecraft.getInstance().getItemColors().register((stack, tintIndex) -> tintIndex > 0 ? -1 : ((IDyeableArmorItem)stack.getItem()).getColor(stack), AdvancedRocketryItems.itemSpaceSuitBoots, AdvancedRocketryItems.itemSpaceSuitChestpiece, AdvancedRocketryItems.itemSpaceSuitHelmet, AdvancedRocketryItems.itemSpaceSuitLeggings);

		AdvancedRocketry.materialRegistry.init();

		//Blocks with special render layers
		RenderTypeLookup.setRenderLayer(AdvancedRocketryBlocks.blockStructureTower, RenderType.getTranslucent());
		RenderTypeLookup.setRenderLayer(AdvancedRocketryBlocks.blockSolarArrayPanel, RenderType.getCutoutMipped());
		RenderTypeLookup.setRenderLayer(AdvancedRocketryBlocks.blockSeat, RenderType.getCutoutMipped());
		RenderTypeLookup.setRenderLayer(AdvancedRocketryBlocks.blockUnlitTorch, RenderType.getCutoutMipped());
		RenderTypeLookup.setRenderLayer(AdvancedRocketryBlocks.blockUnlitTorchWall, RenderType.getCutoutMipped());
		RenderTypeLookup.setRenderLayer(AdvancedRocketryBlocks.blockThermiteTorch, RenderType.getCutoutMipped());
		RenderTypeLookup.setRenderLayer(AdvancedRocketryBlocks.blockThermiteTorchWall, RenderType.getCutoutMipped());
		RenderTypeLookup.setRenderLayer(AdvancedRocketryBlocks.blockLens, RenderType.getTranslucent());
		RenderTypeLookup.setRenderLayer(AdvancedRocketryBlocks.blockForceField, RenderType.getTranslucent());
		RenderTypeLookup.setRenderLayer(AdvancedRocketryBlocks.blockPressureTank, RenderType.getTranslucent());
		RenderTypeLookup.setRenderLayer(AdvancedRocketryBlocks.blockLightwoodSapling, RenderType.getCutoutMipped());
		RenderTypeLookup.setRenderLayer(AdvancedRocketryBlocks.blockElectricMushroom, RenderType.getCutoutMipped());
		RenderTypeLookup.setRenderLayer(AdvancedRocketryBlocks.blockMonopropellantFuelTank, RenderType.getTranslucent());
		RenderTypeLookup.setRenderLayer(AdvancedRocketryBlocks.blockBipropellantFuelTank, RenderType.getTranslucent());
		RenderTypeLookup.setRenderLayer(AdvancedRocketryBlocks.blockOxidizerFuelTank, RenderType.getTranslucent());
		RenderTypeLookup.setRenderLayer(AdvancedRocketryBlocks.blockNuclearWorkingFluidTank, RenderType.getTranslucent());
		for(Block block : AdvancedRocketryBlocks.crystalBlocks)
			RenderTypeLookup.setRenderLayer(block, RenderType.getTranslucent());
	}

	@Override
	public void preinit() {
		registerRenderers();
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
	public IProfiler getProfiler() {
		return Minecraft.getInstance().getProfiler();
	}

	@Override
	public void changeClientPlayerWorld(World world) {
		Minecraft.getInstance().player.world = world;
		//Minecraft.getInstance().world = (ClientWorld)world;
	}

	@Override
	public void spawnParticle(BasicParticleType particle, World world, double x, double y, double z, double motionX, double motionY, double motionZ) {
		if (particle == AdvancedRocketryParticleTypes.errorBox) {
			FxErrorBlock fx = new FxErrorBlock((ClientWorld) world, x, y, z);
			Minecraft.getInstance().particles.addEffect(fx);
		} else if (particle == AdvancedRocketryParticleTypes.laser) {
			FxSkyLaser fx =  new FxSkyLaser(world, x, y, z);
			Minecraft.getInstance().particles.addEffect(fx);
		}
		world.addParticle(particle, x, y, z, motionX, motionY, motionZ);
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
			return (float) SpaceObjectManager.getSpaceManager().getSpaceStationFromBlockCoords(new BlockPos(player.getPositionVec())).getRotation(Direction.EAST);
		} catch (NullPointerException e) {

			/*While waiting for network packets various variables required to continue with rendering may be null,
			 * it would be impractical to check them all
			 * This is kinda hacky but I cannot find a better solution for the time being
			 */
			return 0;
		}
	}

	@Override
	public long getWorldTimeUniversal() {
		try {
			return Minecraft.getInstance().world.getGameTime();
		} catch (NullPointerException e) {
			return 0;
		}
	}

	public void displayMessage(String msg, int time) {
		RocketEventHandler.setOverlay(Minecraft.getInstance().world.getGameTime() + time, msg);
	}

	public String getNameFromBiome(Biome biome) {
		return AdvancedRocketryBiomes.getBiomeResource(biome).toString();
	}

	@Override
	public zmaster587.advancedRocketry.dimension.DimensionManager getDimensionManager() {
		return dimensionManagerClient;
	}
}
