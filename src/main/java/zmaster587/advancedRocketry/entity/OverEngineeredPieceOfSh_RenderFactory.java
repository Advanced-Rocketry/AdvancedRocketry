package zmaster587.advancedRocketry.entity;

import net.minecraft.entity.Entity;
import net.minecraftforge.fml.client.registry.IRenderFactory;
import zmaster587.advancedRocketry.client.render.RendererRocket;

public class OverEngineeredPieceOfSh_RenderFactory implements IRenderFactory<Entity> {

	@Override
	public EntityRenderer<? super Entity> createRenderFor(EntityRendererManager manager) {
		return new RendererRocket(manager);
	}

}
