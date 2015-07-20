package zmaster587.advancedRocketry.client;

import net.minecraft.client.Minecraft;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.fml.client.registry.RenderingRegistry;
import zmaster587.advancedRocketry.client.render.RendererRocketBuilder;
import zmaster587.advancedRocketry.client.render.RendererModelBlock;
import zmaster587.advancedRocketry.client.render.RendererRocket;
import zmaster587.advancedRocketry.client.render.multiblocks.RendererCrystallizer;
import zmaster587.advancedRocketry.client.render.multiblocks.RendererCuttingMachine;
import zmaster587.advancedRocketry.client.render.multiblocks.RendererPrecisionAssembler;
import zmaster587.advancedRocketry.common.CommonProxy;
import zmaster587.advancedRocketry.entity.EntityRocket;
import zmaster587.advancedRocketry.entity.fx.RocketFx;
import zmaster587.advancedRocketry.event.RocketEventHandler;
import zmaster587.advancedRocketry.tile.TileModelRender;
import zmaster587.advancedRocketry.tile.TileRocketBuilder;
import zmaster587.advancedRocketry.tile.multiblock.TileCrystallizer;
import zmaster587.advancedRocketry.tile.multiblock.TileCuttingMachine;
import zmaster587.advancedRocketry.tile.multiblock.TilePrecisionAssembler;

public class ClientProxy extends CommonProxy {

	@Override
	public void registerRenderers() {
		ClientRegistry.bindTileEntitySpecialRenderer(TileRocketBuilder.class, new RendererRocketBuilder());
		ClientRegistry.bindTileEntitySpecialRenderer(TileModelRender.class, new RendererModelBlock());
		ClientRegistry.bindTileEntitySpecialRenderer(TilePrecisionAssembler.class, new RendererPrecisionAssembler());
		ClientRegistry.bindTileEntitySpecialRenderer(TileCuttingMachine.class, new RendererCuttingMachine());
		ClientRegistry.bindTileEntitySpecialRenderer(TileCrystallizer.class, new RendererCrystallizer());

		RenderingRegistry.registerEntityRenderingHandler(EntityRocket.class, new RendererRocket());
	}

	@Override
	public void registerEventHandlers() {
		MinecraftForge.EVENT_BUS.register(new RocketEventHandler());
	}

	@Override
	public void spawnParticle(String particle, World world, double x, double y, double z, double motionX, double motionY, double motionZ) {
		if(particle == "rocketFlame") {
			RocketFx fx = new RocketFx(world, x, y, z, motionX, motionY, motionZ);
			Minecraft.getMinecraft().effectRenderer.addEffect(fx);
		}
	}
}
