package zmaster587.advancedRocketry.client;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.item.Item;
import net.minecraft.profiler.Profiler;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.client.MinecraftForgeClient;
import net.minecraftforge.common.MinecraftForge;
import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.fml.client.registry.RenderingRegistry;
import cpw.mods.fml.common.FMLCommonHandler;
import zmaster587.advancedRocketry.api.AdvancedRocketryBlocks;
import zmaster587.advancedRocketry.api.AdvancedRocketryItems;
import zmaster587.advancedRocketry.api.stations.ISpaceObject;
import zmaster587.advancedRocketry.client.render.RenderComponents;
import zmaster587.advancedRocketry.client.render.RenderLaser;
import zmaster587.advancedRocketry.client.render.RenderLaserTile;
import zmaster587.advancedRocketry.client.render.RenderTank;
import zmaster587.advancedRocketry.client.render.RendererDrill;
import zmaster587.advancedRocketry.client.render.RendererPhantomBlock;
import zmaster587.advancedRocketry.client.render.RendererRocketBuilder;
import zmaster587.advancedRocketry.client.render.RendererModelBlock;
import zmaster587.advancedRocketry.client.render.RendererRocket;
import zmaster587.advancedRocketry.client.render.RendererPipe;
import zmaster587.advancedRocketry.client.render.item.RendererBucket;
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
import zmaster587.advancedRocketry.entity.EntityLaserNode;
import zmaster587.advancedRocketry.entity.EntityRocket;
import zmaster587.advancedRocketry.entity.FxSkyLaser;
import zmaster587.advancedRocketry.entity.fx.FxElectricArc;
import zmaster587.advancedRocketry.entity.fx.InverseTrailFx;
import zmaster587.advancedRocketry.entity.fx.RocketFx;
import zmaster587.advancedRocketry.entity.fx.TrailFx;
import zmaster587.advancedRocketry.event.PlanetEventHandler;
import zmaster587.advancedRocketry.event.RocketEventHandler;
import zmaster587.advancedRocketry.stations.SpaceObjectManager;
import zmaster587.advancedRocketry.tile.TileDrill;
import zmaster587.advancedRocketry.tile.TileFluidTank;
import zmaster587.advancedRocketry.tile.TileModelRender;
import zmaster587.advancedRocketry.tile.TileModelRenderRotatable;
import zmaster587.advancedRocketry.tile.TileRocketBuilder;
import zmaster587.advancedRocketry.tile.TileSpaceLaser;
import zmaster587.advancedRocketry.tile.cables.TileDataPipe;
import zmaster587.advancedRocketry.tile.cables.TileLiquidPipe;
import zmaster587.advancedRocketry.tile.multiblock.TileAtmosphereTerraformer;
import zmaster587.advancedRocketry.tile.multiblock.TileBiomeScanner;
import zmaster587.advancedRocketry.tile.multiblock.TileObservatory;
import zmaster587.advancedRocketry.tile.multiblock.TilePlanetAnalyser;
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
		
		RendererModelBlock modelBlock = new RendererModelBlock();
		
		ClientRegistry.bindTileEntitySpecialRenderer(TileRocketBuilder.class, new RendererRocketBuilder());
		ClientRegistry.bindTileEntitySpecialRenderer(TileModelRender.class, modelBlock);
		ClientRegistry.bindTileEntitySpecialRenderer(TilePrecisionAssembler.class, new RendererPrecisionAssembler());
		ClientRegistry.bindTileEntitySpecialRenderer(TileCuttingMachine.class, new RendererCuttingMachine());
		ClientRegistry.bindTileEntitySpecialRenderer(TileCrystallizer.class, new RendererCrystallizer());
		ClientRegistry.bindTileEntitySpecialRenderer(TileObservatory.class, new RendererObservatory());
		ClientRegistry.bindTileEntitySpecialRenderer(TilePlanetAnalyser.class, new RenderPlanetAnalyser());
		ClientRegistry.bindTileEntitySpecialRenderer(TileLathe.class, new RendererLathe());
		ClientRegistry.bindTileEntitySpecialRenderer(TileRollingMachine.class, new RendererRollingMachine());
		ClientRegistry.bindTileEntitySpecialRenderer(TileElectrolyser.class, new RendererElectrolyser());
		ClientRegistry.bindTileEntitySpecialRenderer(TileWarpCore.class, new RendererWarpCore());
		ClientRegistry.bindTileEntitySpecialRenderer(TileChemicalReactor.class, new RendererChemicalReactor("advancedrocketry:models/ChemicalReactor.obj", "advancedrocketry:textures/models/ChemicalReactor.png"));
		ClientRegistry.bindTileEntitySpecialRenderer(TileSchematic.class, new RendererPhantomBlock());
		ClientRegistry.bindTileEntitySpecialRenderer(TileDrill.class, new RendererDrill());
		ClientRegistry.bindTileEntitySpecialRenderer(TileLiquidPipe.class, new RendererPipe(new ResourceLocation("AdvancedRocketry:textures/blocks/pipeLiquid.png")));
		ClientRegistry.bindTileEntitySpecialRenderer(TileDataPipe.class, new RendererPipe(new ResourceLocation("AdvancedRocketry:textures/blocks/pipeData.png")));
		ClientRegistry.bindTileEntitySpecialRenderer(TileMicrowaveReciever.class, new RendererMicrowaveReciever());
		ClientRegistry.bindTileEntitySpecialRenderer(TileSpaceLaser.class, new RenderLaserTile());
		ClientRegistry.bindTileEntitySpecialRenderer(TileBiomeScanner.class, new RenderBiomeScanner());
		ClientRegistry.bindTileEntitySpecialRenderer(TileAtmosphereTerraformer.class, new RenderTerraformerAtm());
		ClientRegistry.bindTileEntitySpecialRenderer(TileFluidTank.class, new RenderTank());
		ClientRegistry.bindTileEntitySpecialRenderer(TileModelRenderRotatable.class, modelBlock);

		RendererModelBlock blockRenderer = new RendererModelBlock();

		MinecraftForgeClient.registerItemRenderer(Item.getItemFromBlock(AdvancedRocketryBlocks.blockSawBlade), blockRenderer);
		MinecraftForgeClient.registerItemRenderer(Item.getItemFromBlock(AdvancedRocketryBlocks.blockEngine), blockRenderer);
		MinecraftForgeClient.registerItemRenderer(Item.getItemFromBlock(AdvancedRocketryBlocks.blockFuelTank), blockRenderer);
		MinecraftForgeClient.registerItemRenderer(Item.getItemFromBlock(AdvancedRocketryBlocks.blockMotor), blockRenderer);
		RendererBucket bucket =  new RendererBucket();
		MinecraftForgeClient.registerItemRenderer(AdvancedRocketryItems.itemBucketRocketFuel, bucket);
		MinecraftForgeClient.registerItemRenderer(AdvancedRocketryItems.itemBucketNitrogen, bucket);
		MinecraftForgeClient.registerItemRenderer(AdvancedRocketryItems.itemBucketHydrogen, bucket);
		MinecraftForgeClient.registerItemRenderer(AdvancedRocketryItems.itemBucketOxygen, bucket);
		
		RenderingRegistry.registerEntityRenderingHandler(EntityRocket.class, new RendererRocket());
		RenderingRegistry.registerEntityRenderingHandler(EntityLaserNode.class, new RenderLaser(2.0, new float[] {1F, 0.25F, 0.25F, 0.2F}, new float[] {0.9F, 0.2F, 0.3F, 0.5F}));
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
		FMLCommonHandler.instance().bus().register(new KeyBindings());

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
	public float calculateCelestialAngleSpaceStation() {
		Entity player = Minecraft.getMinecraft().thePlayer;
		try {
			return (float) SpaceObjectManager.getSpaceManager().getSpaceStationFromBlockCoords((int)player.posX, (int)player.posZ).getRotation();
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
