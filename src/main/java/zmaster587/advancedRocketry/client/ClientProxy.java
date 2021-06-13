package zmaster587.advancedRocketry.client;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ItemMeshDefinition;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ModelBakery;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.block.statemap.StateMapperBase;
import net.minecraft.client.renderer.color.IItemColor;
import net.minecraft.entity.Entity;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.profiler.Profiler;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.client.event.ModelBakeEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.client.model.obj.OBJLoader;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fluids.IFluidBlock;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.client.registry.IRenderFactory;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import zmaster587.advancedRocketry.AdvancedRocketry;
import zmaster587.advancedRocketry.api.AdvancedRocketryBlocks;
import zmaster587.advancedRocketry.api.AdvancedRocketryItems;
import zmaster587.advancedRocketry.api.stations.ISpaceObject;
import zmaster587.advancedRocketry.block.BlockCrystal;
import zmaster587.advancedRocketry.block.CrystalColorizer;
import zmaster587.advancedRocketry.client.model.ModelRocket;
import zmaster587.advancedRocketry.client.render.*;
import zmaster587.advancedRocketry.client.render.entity.*;
import zmaster587.advancedRocketry.client.render.multiblocks.*;
import zmaster587.advancedRocketry.common.CommonProxy;
import zmaster587.advancedRocketry.entity.*;
import zmaster587.advancedRocketry.entity.fx.*;
import zmaster587.advancedRocketry.event.PlanetEventHandler;
import zmaster587.advancedRocketry.event.RocketEventHandler;
import zmaster587.advancedRocketry.stations.SpaceObjectManager;
import zmaster587.advancedRocketry.tile.TileFluidTank;
import zmaster587.advancedRocketry.tile.TileRocketAssemblingMachine;
import zmaster587.advancedRocketry.tile.cables.TileDataPipe;
import zmaster587.advancedRocketry.tile.cables.TileEnergyPipe;
import zmaster587.advancedRocketry.tile.cables.TileLiquidPipe;
import zmaster587.advancedRocketry.tile.multiblock.*;
import zmaster587.advancedRocketry.tile.multiblock.energy.TileSolarArray;
import zmaster587.advancedRocketry.tile.multiblock.orbitallaserdrill.TileOrbitalLaserDrill;
import zmaster587.advancedRocketry.tile.multiblock.energy.TileBlackHoleGenerator;
import zmaster587.advancedRocketry.tile.multiblock.energy.TileMicrowaveReciever;
import zmaster587.advancedRocketry.tile.multiblock.machine.*;
import zmaster587.libVulpes.entity.fx.FxErrorBlock;
import zmaster587.libVulpes.inventory.modules.ModuleContainerPan;
import zmaster587.libVulpes.tile.TileSchematic;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Objects;

public class ClientProxy extends CommonProxy {

	private static zmaster587.advancedRocketry.dimension.DimensionManager dimensionManagerClient = new zmaster587.advancedRocketry.dimension.DimensionManager();
	
	@Override
	public void registerRenderers() {


		ClientRegistry.bindTileEntitySpecialRenderer(TileRocketAssemblingMachine.class, new RendererRocketAssemblingMachine());
		//ClientRegistry.bindTileEntitySpecialRenderer(TileModelRender.class, modelBlock);
		ClientRegistry.bindTileEntitySpecialRenderer(TilePrecisionAssembler.class, new RendererPrecisionAssembler());
		ClientRegistry.bindTileEntitySpecialRenderer(TileCuttingMachine.class, new RendererCuttingMachine());
		ClientRegistry.bindTileEntitySpecialRenderer(TileCrystallizer.class, new RendererCrystallizer());
		ClientRegistry.bindTileEntitySpecialRenderer(TileObservatory.class, new RendererObservatory());
		ClientRegistry.bindTileEntitySpecialRenderer(TileAstrobodyDataProcessor.class, new RenderAstrobodyDataProcessor());
		ClientRegistry.bindTileEntitySpecialRenderer(TileLathe.class, new RendererLathe());
		ClientRegistry.bindTileEntitySpecialRenderer(TileRollingMachine.class, new RendererRollingMachine());
		ClientRegistry.bindTileEntitySpecialRenderer(TileElectrolyser.class, new RendererElectrolyser());
		ClientRegistry.bindTileEntitySpecialRenderer(TileWarpCore.class, new RendererWarpCore());
		ClientRegistry.bindTileEntitySpecialRenderer(TileChemicalReactor.class, new RendererChemicalReactor("advancedrocketry:models/chemicalreactor.obj", "advancedrocketry:textures/models/chemicalreactor.png"));
		ClientRegistry.bindTileEntitySpecialRenderer(TileSchematic.class, new RendererPhantomBlock());
		//ClientRegistry.bindTileEntitySpecialRenderer(TileDrill.class, new RendererDrill());
		ClientRegistry.bindTileEntitySpecialRenderer(TileLiquidPipe.class, new RendererPipe(new ResourceLocation("AdvancedRocketry:textures/blocks/pipeLiquid.png")));
		ClientRegistry.bindTileEntitySpecialRenderer(TileDataPipe.class, new RendererPipe(new ResourceLocation("AdvancedRocketry:textures/blocks/pipeData.png")));
		ClientRegistry.bindTileEntitySpecialRenderer(TileEnergyPipe.class, new RendererPipe(new ResourceLocation("AdvancedRocketry:textures/blocks/pipeEnergy.png")));
		ClientRegistry.bindTileEntitySpecialRenderer(TileMicrowaveReciever.class, new RendererMicrowaveReciever());
		ClientRegistry.bindTileEntitySpecialRenderer(TileOrbitalLaserDrill.class, new RenderOrbitalLaserDrillTile());
		ClientRegistry.bindTileEntitySpecialRenderer(TileBiomeScanner.class, new RenderBiomeScanner());
		ClientRegistry.bindTileEntitySpecialRenderer(TileBlackHoleGenerator.class, new RenderBlackHoleGenerator());
		ClientRegistry.bindTileEntitySpecialRenderer(TileAtmosphereTerraformer.class, new RenderTerraformerAtm());
		ClientRegistry.bindTileEntitySpecialRenderer(TileFluidTank.class, new RenderTank());
		ClientRegistry.bindTileEntitySpecialRenderer(TileOrbitalLaserDrill.class, new RenderOrbitalLaserDrill());
		ClientRegistry.bindTileEntitySpecialRenderer(zmaster587.advancedRocketry.tile.multiblock.TileRailgun.class, new zmaster587.advancedRocketry.client.render.multiblocks.RendererRailgun());
		ClientRegistry.bindTileEntitySpecialRenderer(TileAreaGravityController.class, new RenderAreaGravityController());
		ClientRegistry.bindTileEntitySpecialRenderer(zmaster587.advancedRocketry.tile.multiblock.TileSpaceElevator.class, new zmaster587.advancedRocketry.client.render.multiblocks.RendererSpaceElevator());
		ClientRegistry.bindTileEntitySpecialRenderer(zmaster587.advancedRocketry.tile.multiblock.TileBeacon.class, new zmaster587.advancedRocketry.client.render.multiblocks.RenderBeacon());
		ClientRegistry.bindTileEntitySpecialRenderer(zmaster587.advancedRocketry.tile.multiblock.machine.TileCentrifuge.class, new zmaster587.advancedRocketry.client.render.multiblocks.RenderCentrifuge());
		ClientRegistry.bindTileEntitySpecialRenderer(TilePrecisionLaserEtcher.class, new RendererPrecisionLaserEtcher());
		ClientRegistry.bindTileEntitySpecialRenderer(TileSolarArray.class, new RendererSolarArray());

		//ClientRegistry.bindTileEntitySpecialRenderer(TileModelRenderRotatable.class, modelBlock);

		//RendererModelBlock blockRenderer = new RendererModelBlock();

		RenderingRegistry.registerEntityRenderingHandler(EntityRocket.class, (IRenderFactory<EntityRocket>)new RendererRocket(null));
		RenderingRegistry.registerEntityRenderingHandler(EntityLaserNode.class, (IRenderFactory<EntityLaserNode>)new RenderLaser(2.0, new float[] {1F, 0.25F, 0.25F, 0.2F}, new float[] {0.9F, 0.2F, 0.3F, 0.5F}));
		RenderingRegistry.registerEntityRenderingHandler(EntityItemAbducted.class, (IRenderFactory<EntityItemAbducted>)new RendererItem(Minecraft.getMinecraft().getRenderManager(), Minecraft.getMinecraft().getRenderItem()));
		RenderingRegistry.registerEntityRenderingHandler(EntityUIPlanet.class, (IRenderFactory<EntityUIPlanet>)new RenderPlanetUIEntity(null));
		RenderingRegistry.registerEntityRenderingHandler(EntityUIButton.class, (IRenderFactory<EntityUIButton>)new RenderButtonUIEntity(null));
		RenderingRegistry.registerEntityRenderingHandler(EntityUIStar.class, (IRenderFactory<EntityUIStar>)new RenderStarUIEntity(null));
		RenderingRegistry.registerEntityRenderingHandler(EntityElevatorCapsule.class, (IRenderFactory<EntityElevatorCapsule>)new RenderElevatorCapsule(null));
		RenderingRegistry.registerEntityRenderingHandler(EntityHoverCraft.class, (IRenderFactory<EntityHoverCraft>)new RenderHoverCraft(null));
	}

	@Override
	public void init() {

		//Colorizers
		CrystalColorizer colorizer = new CrystalColorizer();
		Minecraft.getMinecraft().getBlockColors().registerBlockColorHandler(colorizer, AdvancedRocketryBlocks.blockCrystal);
		Minecraft.getMinecraft().getItemColors().registerItemColorHandler(colorizer,  Item.getItemFromBlock(AdvancedRocketryBlocks.blockCrystal));

		Minecraft.getMinecraft().getItemColors().registerItemColorHandler(new IItemColor()
        {
            public int getColorFromItemstack(@Nonnull ItemStack stack, int tintIndex)
            {
                return tintIndex > 0 ? -1 : ((ItemArmor)stack.getItem()).getColor(stack);
            }
        }, AdvancedRocketryItems.itemSpaceSuit_Boots, AdvancedRocketryItems.itemSpaceSuit_Chest, AdvancedRocketryItems.itemSpaceSuit_Helmet, AdvancedRocketryItems.itemSpaceSuit_Leggings);
		
		AdvancedRocketry.materialRegistry.init();
	}

	@Override
	public void preInitBlocks()
	{
		//Register Block models
		Item blockItem = Item.getItemFromBlock(AdvancedRocketryBlocks.blockLoader);
		ModelLoader.setCustomModelResourceLocation(blockItem, 0, new ModelResourceLocation("advancedrocketry:databus", "inventory"));
		ModelLoader.setCustomModelResourceLocation(blockItem, 1, new ModelResourceLocation("advancedrocketry:satelliteHatch", "inventory"));
		ModelLoader.setCustomModelResourceLocation(blockItem, 2, new ModelResourceLocation("libvulpes:outputHatch", "inventory"));
		ModelLoader.setCustomModelResourceLocation(blockItem, 3, new ModelResourceLocation("libvulpes:inputHatch", "inventory"));
		ModelLoader.setCustomModelResourceLocation(blockItem, 4, new ModelResourceLocation("libvulpes:fluidOutputHatch", "inventory"));
		ModelLoader.setCustomModelResourceLocation(blockItem, 5, new ModelResourceLocation("libvulpes:fluidInputHatch", "inventory"));
		ModelLoader.setCustomModelResourceLocation(blockItem, 6, new ModelResourceLocation("advancedrocketry:guidancecomputeraccesshatch", "inventory"));

		blockItem = Item.getItemFromBlock(AdvancedRocketryBlocks.blockCrystal);
		for(int i = 0; i < BlockCrystal.numMetas; i++)
			ModelLoader.setCustomModelResourceLocation(blockItem, i, new ModelResourceLocation("advancedrocketry:crystal", "inventory"));

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
				ModelLoader.setCustomModelResourceLocation(AdvancedRocketryItems.itemBeaconFinder, 0, new ModelResourceLocation("advancedrocketry:beaconFinder", "inventory"));

				ModelLoader.setCustomModelResourceLocation(AdvancedRocketryItems.itemWafer, 0, new ModelResourceLocation("advancedrocketry:siliconWafer", "inventory"));

				ModelLoader.setCustomModelResourceLocation(AdvancedRocketryItems.itemSpaceStation, 0, new ModelResourceLocation("advancedrocketry:spaceStation", "inventory"));


				ModelLoader.setCustomModelResourceLocation(AdvancedRocketryItems.itemSpaceSuit_Chest, 0, new ModelResourceLocation("advancedrocketry:spaceChestplate", "inventory"));
				ModelLoader.setCustomModelResourceLocation(AdvancedRocketryItems.itemSpaceSuit_Helmet, 0, new ModelResourceLocation("advancedrocketry:spaceHelmet", "inventory"));
				ModelLoader.setCustomModelResourceLocation(AdvancedRocketryItems.itemSpaceSuit_Boots, 0, new ModelResourceLocation("advancedrocketry:spaceBoots", "inventory"));
				ModelLoader.setCustomModelResourceLocation(AdvancedRocketryItems.itemSpaceSuit_Leggings, 0, new ModelResourceLocation("advancedrocketry:spaceLeggings", "inventory"));
				ModelLoader.setCustomModelResourceLocation(AdvancedRocketryItems.itemQuartzCrucible, 0, new ModelResourceLocation("advancedrocketry:iquartzCrucible", "inventory"));
				ModelLoader.setCustomModelResourceLocation(AdvancedRocketryItems.itemDataUnit, 0, new ModelResourceLocation("advancedrocketry:dataStorageUnit", "inventory"));
				ModelLoader.setCustomModelResourceLocation(AdvancedRocketryItems.itemSatelliteIdChip, 0, new ModelResourceLocation("advancedrocketry:satelliteIdChip", "inventory"));
				ModelLoader.setCustomModelResourceLocation(AdvancedRocketryItems.itemPlanetIdChip, 0, new ModelResourceLocation("advancedrocketry:planetIdChip", "inventory"));
				ModelLoader.setCustomModelResourceLocation(AdvancedRocketryItems.itemSpaceStationChip, 0, new ModelResourceLocation("advancedrocketry:stationidchip", "inventory"));
				ModelLoader.setCustomModelResourceLocation(AdvancedRocketryItems.itemSpaceElevatorChip, 0, new ModelResourceLocation("advancedrocketry:elevatorChip", "inventory"));
				
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
				ModelLoader.setCustomModelResourceLocation(AdvancedRocketryItems.itemThermite, 0, new ModelResourceLocation("advancedrocketry:thermite", "inventory"));
				ModelLoader.setCustomModelResourceLocation(AdvancedRocketryItems.itemHovercraft, 0, new ModelResourceLocation("advancedrocketry:hoverCraft", "inventory"));
	}
	
	@Override
	public void preinit() {
		OBJLoader.INSTANCE.addDomain("advancedrocketry");
		registerRenderers();


		
	}

	private void registerFluidModel(IFluidBlock fluidBlock) {
		Item item = Item.getItemFromBlock((Block) fluidBlock);

		final ModelResourceLocation modelResourceLocation = new ModelResourceLocation("advancedrocketry:fluid", fluidBlock.getFluid().getName());

		if (item != Items.AIR) {
			ModelLoader.registerItemVariants(item);
			ModelLoader.setCustomMeshDefinition(item, new FluidItemMeshDefinition(modelResourceLocation));
		}
		FluidStateMapper ignoreState = new FluidStateMapper(modelResourceLocation);
		ModelLoader.setCustomStateMapper((Block) fluidBlock, ignoreState);
		ModelBakery.registerItemVariants(item, modelResourceLocation);

	}

	private static class FluidStateMapper extends StateMapperBase {
		private final ModelResourceLocation location;

		public FluidStateMapper(ModelResourceLocation fluidLocation) {
			this.location = fluidLocation;
		}

		@Override
		protected ModelResourceLocation getModelResourceLocation(@Nullable IBlockState iBlockState) {
			return location;
		}
	}

	private static class FluidItemMeshDefinition implements ItemMeshDefinition {
		private final ModelResourceLocation location;

		public FluidItemMeshDefinition(ModelResourceLocation fluidLocation) {
			this.location = fluidLocation;
		}

		@Override
		public ModelResourceLocation getModelLocation(@Nonnull ItemStack stack) {
			return location;
		}
}

	@SubscribeEvent
	public void modelBakeEvent(ModelBakeEvent event) {
		IBakedModel bakedModel =  event.getModelRegistry().getObject(ModelRocket.resource);
		if (bakedModel != null) {
			ModelRocket customModel = new ModelRocket();
			event.getModelRegistry().putObject(ModelRocket.resource, bakedModel);
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
			PlanetEventHandler.runBurst(Minecraft.getMinecraft().world.getTotalWorldTime() + 20, 20);
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
		Minecraft.getMinecraft().player.world = world;
	}

	@Override
	public void spawnParticle(String particle, World world, double x, double y, double z, double motionX, double motionY, double motionZ) {
		switch (particle) {
			case "rocketFlame": {
				RocketFx fx = new RocketFx(world, x, y, z, motionX, motionY, motionZ);
				Minecraft.getMinecraft().effectRenderer.addEffect(fx);
				break;
			}
			case "smallRocketFlame": {
				RocketFx fx = new RocketFx(world, x, y, z, motionX, motionY, motionZ, 0.25f);
				Minecraft.getMinecraft().effectRenderer.addEffect(fx);
				break;
			}
			case "rocketSmoke": {
				TrailFx fx = new TrailFx(world, x, y, z, motionX, motionY, motionZ);
				Minecraft.getMinecraft().effectRenderer.addEffect(fx);
				break;
			}
			case "rocketSmokeInverse": {
				InverseTrailFx fx = new InverseTrailFx(world, x, y, z, motionX, motionY, motionZ);
				Minecraft.getMinecraft().effectRenderer.addEffect(fx);
				break;
			}
			case "arc": {
				FxElectricArc fx = new FxElectricArc(world, x, y, z, motionX);
				Minecraft.getMinecraft().effectRenderer.addEffect(fx);
				break;
			}
			case "smallLazer": {
				FxSkyLaser fx = new FxSkyLaser(world, x, y, z);
				Minecraft.getMinecraft().effectRenderer.addEffect(fx);
				break;
			}
			case "errorBox": {
				FxErrorBlock fx = new FxErrorBlock(world, x, y, z);
				Minecraft.getMinecraft().effectRenderer.addEffect(fx);
				break;
			}
			case "gravityEffect": {
				FxGravityEffect fx = new FxGravityEffect(world, x, y, z, motionX, motionY, motionZ);
				Minecraft.getMinecraft().effectRenderer.addEffect(fx);
				break;
			}
			default:
				world.spawnParticle(Objects.requireNonNull(EnumParticleTypes.getByName(particle)), x, y, z, motionX, motionY, motionZ);
				break;
		}
	}

	@Override
	public void spawnLaser(Entity entity, Vec3d toPos) {
		FxLaser fx = new FxLaser(entity.world, toPos.x, toPos.y, toPos.z, entity);
		Minecraft.getMinecraft().effectRenderer.addEffect(fx);

		FxLaserHeat fx2 = new FxLaserHeat(entity.world,  toPos.x, toPos.y, toPos.z, 0.02f);
		Minecraft.getMinecraft().effectRenderer.addEffect(fx2);
		
		for(int i = 0; i < 4; i++) {
			FxLaserSpark fx3 = new FxLaserSpark(entity.world,  toPos.x, toPos.y, toPos.z, 
					.125 - entity.world.rand.nextFloat()/4f, .125 - entity.world.rand.nextFloat()/4f, .125 - entity.world.rand.nextFloat()/4f, .5f);
			Minecraft.getMinecraft().effectRenderer.addEffect(fx3);
		}
	}


	@Override
	public float calculateCelestialAngleSpaceStation() {
		Entity player = Minecraft.getMinecraft().player;
		try {
			return (float) SpaceObjectManager.getSpaceManager().getSpaceStationFromBlockCoords(player.getPosition()).getRotation(EnumFacing.EAST);
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
			return Minecraft.getMinecraft().world.getTotalWorldTime();
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
		RocketEventHandler.setOverlay(Minecraft.getMinecraft().world.getTotalWorldTime() + time, msg);
	}
	
	public String getNameFromBiome(Biome biome) {
		return biome.getBiomeName();
	}
	
	@Override
	public zmaster587.advancedRocketry.dimension.DimensionManager getDimensionManager() {
		return dimensionManagerClient;
	}
}
