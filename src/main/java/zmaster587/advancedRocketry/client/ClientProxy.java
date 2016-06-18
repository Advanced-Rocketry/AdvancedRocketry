package zmaster587.advancedRocketry.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.Entity;
import net.minecraft.item.Item;
import net.minecraft.profiler.Profiler;
import net.minecraft.world.World;
import net.minecraftforge.client.MinecraftForgeClient;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.common.MinecraftForge;
import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.fml.client.registry.RenderingRegistry;
import cpw.mods.fml.common.FMLCommonHandler;
import zmaster587.advancedRocketry.api.AdvancedRocketryBlocks;
import zmaster587.advancedRocketry.api.AdvancedRocketryItems;
import zmaster587.advancedRocketry.api.stations.ISpaceObject;
import zmaster587.advancedRocketry.api.stations.SpaceObjectManager;
import zmaster587.advancedRocketry.client.render.RendererDrill;
import zmaster587.advancedRocketry.client.render.RendererPhantomBlock;
import zmaster587.advancedRocketry.client.render.RendererRocketBuilder;
import zmaster587.advancedRocketry.client.render.RendererModelBlock;
import zmaster587.advancedRocketry.client.render.RendererRocket;
import zmaster587.advancedRocketry.client.render.item.RendererBucket;
import zmaster587.advancedRocketry.client.render.multiblocks.RenderPlanetAnalyser;
import zmaster587.advancedRocketry.client.render.multiblocks.RendererChemicalReactor;
import zmaster587.advancedRocketry.client.render.multiblocks.RendererElectrolyser;
import zmaster587.advancedRocketry.client.render.multiblocks.RendererGenericMachineModel;
import zmaster587.advancedRocketry.client.render.multiblocks.RendererRollingMachine;
import zmaster587.advancedRocketry.client.render.multiblocks.RendererCrystallizer;
import zmaster587.advancedRocketry.client.render.multiblocks.RendererCuttingMachine;
import zmaster587.advancedRocketry.client.render.multiblocks.RendererLathe;
import zmaster587.advancedRocketry.client.render.multiblocks.RendererObservatory;
import zmaster587.advancedRocketry.client.render.multiblocks.RendererPrecisionAssembler;
import zmaster587.advancedRocketry.client.render.multiblocks.RendererWarpCore;
import zmaster587.advancedRocketry.common.CommonProxy;
import zmaster587.advancedRocketry.entity.EntityRocket;
import zmaster587.advancedRocketry.entity.fx.FxElectricArc;
import zmaster587.advancedRocketry.entity.fx.RocketFx;
import zmaster587.advancedRocketry.entity.fx.TrailFx;
import zmaster587.advancedRocketry.event.PlanetEventHandler;
import zmaster587.advancedRocketry.event.PlanetEventHandlerClient;
import zmaster587.advancedRocketry.event.RocketEventHandler;
import zmaster587.advancedRocketry.inventory.modules.ModuleContainerPan;
import zmaster587.advancedRocketry.tile.TileDrill;
import zmaster587.advancedRocketry.tile.TileModelRender;
import zmaster587.advancedRocketry.tile.TileRocketBuilder;
import zmaster587.advancedRocketry.tile.TileSchematic;
import zmaster587.advancedRocketry.tile.multiblock.TileChemicalReactor;
import zmaster587.advancedRocketry.tile.multiblock.TileCrystallizer;
import zmaster587.advancedRocketry.tile.multiblock.TileCuttingMachine;
import zmaster587.advancedRocketry.tile.multiblock.TileElectrolyser;
import zmaster587.advancedRocketry.tile.multiblock.TileLathe;
import zmaster587.advancedRocketry.tile.multiblock.TilePlaceholder;
import zmaster587.advancedRocketry.tile.multiblock.TileRollingMachine;
import zmaster587.advancedRocketry.tile.multiblock.TileObservatory;
import zmaster587.advancedRocketry.tile.multiblock.TilePlanetAnalyser;
import zmaster587.advancedRocketry.tile.multiblock.TilePrecisionAssembler;
import zmaster587.advancedRocketry.tile.multiblock.TileWarpCore;

public class ClientProxy extends CommonProxy {

	@Override
	public void registerRenderers() {
		ClientRegistry.bindTileEntitySpecialRenderer(TileRocketBuilder.class, new RendererRocketBuilder());
		ClientRegistry.bindTileEntitySpecialRenderer(TileModelRender.class, new RendererModelBlock());
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

		RendererModelBlock blockRenderer = new RendererModelBlock();

		MinecraftForgeClient.registerItemRenderer(Item.getItemFromBlock(AdvancedRocketryBlocks.blockSawBlade), blockRenderer);
		MinecraftForgeClient.registerItemRenderer(Item.getItemFromBlock(AdvancedRocketryBlocks.blockEngine), blockRenderer);
		MinecraftForgeClient.registerItemRenderer(Item.getItemFromBlock(AdvancedRocketryBlocks.blockFuelTank), blockRenderer);
		MinecraftForgeClient.registerItemRenderer(Item.getItemFromBlock(AdvancedRocketryBlocks.blockMotor), blockRenderer);
		MinecraftForgeClient.registerItemRenderer(AdvancedRocketryItems.itemBucketRocketFuel, new RendererBucket());

		RenderingRegistry.registerEntityRenderingHandler(EntityRocket.class, new RendererRocket());
	}

	@Override
	public void registerEventHandlers() {
		super.registerEventHandlers();
		MinecraftForge.EVENT_BUS.register(new RocketEventHandler());
		MinecraftForge.EVENT_BUS.register(AdvancedRocketryItems.itemHoloProjector);
		MinecraftForge.EVENT_BUS.register(ModuleContainerPan.class);

		FMLCommonHandler.instance().bus().register(new PlanetEventHandlerClient());
	}

	@Override
	public void fireFogBurst(ISpaceObject station) {
		PlanetEventHandler.runBurst(Minecraft.getMinecraft().theWorld.getTotalWorldTime() + 20, 20);
	}

	@Override
	public void registerKeyBindings() {
		//KeyBindings.init();
		//FMLCommonHandler.instance().bus().register(new KeyBindings());

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
	public String getLocalizedString(String str) {
		return I18n.format(str);
	}

	@Override
	public void spawnParticle(String particle, World world, double x, double y, double z, double motionX, double motionY, double motionZ) {
		if(particle == "rocketFlame") {
			RocketFx fx = new RocketFx(world, x, y, z, motionX, motionY, motionZ);
			Minecraft.getMinecraft().effectRenderer.addEffect(fx);
		}
		else if(particle == "rocketSmoke") {
			TrailFx fx = new TrailFx(world, x, y, z, motionX, motionY, motionZ);
			Minecraft.getMinecraft().effectRenderer.addEffect(fx);
		}
		else if(particle == "arc") {
			FxElectricArc fx = new FxElectricArc(world, x, y, z, motionX);
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
		return Minecraft.getMinecraft().theWorld.getTotalWorldTime();
	}
}
