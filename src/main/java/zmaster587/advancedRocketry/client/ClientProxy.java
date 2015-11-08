package zmaster587.advancedRocketry.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.Item;
import net.minecraft.profiler.Profiler;
import net.minecraft.world.World;
import net.minecraftforge.client.MinecraftForgeClient;
import net.minecraftforge.common.MinecraftForge;
import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.fml.client.registry.RenderingRegistry;
import cpw.mods.fml.common.FMLCommonHandler;
import zmaster587.advancedRocketry.Inventory.modules.ModuleContainerPan;
import zmaster587.advancedRocketry.api.AdvancedRocketryBlocks;
import zmaster587.advancedRocketry.api.AdvancedRocketryItems;
import zmaster587.advancedRocketry.client.render.RendererPhantomBlock;
import zmaster587.advancedRocketry.client.render.RendererRocketBuilder;
import zmaster587.advancedRocketry.client.render.RendererModelBlock;
import zmaster587.advancedRocketry.client.render.RendererRocket;
import zmaster587.advancedRocketry.client.render.multiblocks.RenderPlanetAnalyser;
import zmaster587.advancedRocketry.client.render.multiblocks.RendererRollingMachine;
import zmaster587.advancedRocketry.client.render.multiblocks.RendererCrystallizer;
import zmaster587.advancedRocketry.client.render.multiblocks.RendererCuttingMachine;
import zmaster587.advancedRocketry.client.render.multiblocks.RendererLathe;
import zmaster587.advancedRocketry.client.render.multiblocks.RendererObservatory;
import zmaster587.advancedRocketry.client.render.multiblocks.RendererPrecisionAssembler;
import zmaster587.advancedRocketry.common.CommonProxy;
import zmaster587.advancedRocketry.entity.EntityRocket;
import zmaster587.advancedRocketry.entity.fx.RocketFx;
import zmaster587.advancedRocketry.event.PlanetEventHandlerClient;
import zmaster587.advancedRocketry.event.RocketEventHandler;
import zmaster587.advancedRocketry.tile.TileModelRender;
import zmaster587.advancedRocketry.tile.TileRocketBuilder;
import zmaster587.advancedRocketry.tile.TileSchematic;
import zmaster587.advancedRocketry.tile.multiblock.TileCrystallizer;
import zmaster587.advancedRocketry.tile.multiblock.TileCuttingMachine;
import zmaster587.advancedRocketry.tile.multiblock.TileLathe;
import zmaster587.advancedRocketry.tile.multiblock.TilePlaceholder;
import zmaster587.advancedRocketry.tile.multiblock.TileRollingMachine;
import zmaster587.advancedRocketry.tile.multiblock.TileObservatory;
import zmaster587.advancedRocketry.tile.multiblock.TilePlanetAnalyser;
import zmaster587.advancedRocketry.tile.multiblock.TilePrecisionAssembler;

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
		ClientRegistry.bindTileEntitySpecialRenderer(TileSchematic.class, new RendererPhantomBlock());
		
		RendererModelBlock blockRenderer = new RendererModelBlock();
		
		MinecraftForgeClient.registerItemRenderer(Item.getItemFromBlock(AdvancedRocketryBlocks.blockSawBlade), blockRenderer);
		MinecraftForgeClient.registerItemRenderer(Item.getItemFromBlock(AdvancedRocketryBlocks.blockEngine), blockRenderer);
		MinecraftForgeClient.registerItemRenderer(Item.getItemFromBlock(AdvancedRocketryBlocks.blockFuelTank), blockRenderer);
		MinecraftForgeClient.registerItemRenderer(Item.getItemFromBlock(AdvancedRocketryBlocks.blockMotor), blockRenderer);
		
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
	}
}
