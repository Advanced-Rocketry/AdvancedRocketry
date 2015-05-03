package zmaster587.advancedRocketry.client;

import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.fml.client.registry.RenderingRegistry;
import zmaster587.advancedRocketry.client.render.RendererBlockRocketBuilder;
import zmaster587.advancedRocketry.client.render.RendererModelBlock;
import zmaster587.advancedRocketry.client.render.RendererRocket;
import zmaster587.advancedRocketry.common.CommonProxy;
import zmaster587.advancedRocketry.entity.EntityRocket;
import zmaster587.advancedRocketry.tile.TileModelRender;
import zmaster587.advancedRocketry.tile.TileRocketBuilder;

public class ClientProxy extends CommonProxy {


	public void registerRenderers() {
		ClientRegistry.bindTileEntitySpecialRenderer(TileRocketBuilder.class, new RendererBlockRocketBuilder());
		ClientRegistry.bindTileEntitySpecialRenderer(TileModelRender.class, new RendererModelBlock());
		
		RenderingRegistry.registerEntityRenderingHandler(EntityRocket.class, new RendererRocket());
	}
}
