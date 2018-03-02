package zmaster587.advancedRocketry.client.model;

import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.IModel;
import net.minecraftforge.client.model.ModelLoaderRegistry;
import net.minecraftforge.common.model.IModelPart;
import net.minecraftforge.common.model.IModelState;
import net.minecraftforge.common.model.TRSRTransformation;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

public class ModelRocket implements IModel {

	public static ModelResourceLocation resource = new ModelResourceLocation("advancedrocketry:rocket.obj");
	
	@Override
	public Collection<ResourceLocation> getDependencies() {
		return new LinkedList<ResourceLocation>();
	}

	@Override
	public Collection<ResourceLocation> getTextures() {
		List<ResourceLocation> textures = new LinkedList<ResourceLocation>();
		textures.add(new ResourceLocation("advancedrocketry:models/combustion.png"));
		return textures;
	}

	@Override
	public IBakedModel bake(IModelState state, VertexFormat format,
			Function<ResourceLocation, TextureAtlasSprite> bakedTextureGetter) {
		
		IModel subComponent;
		try {
			subComponent = ModelLoaderRegistry.getModel(resource);
			IBakedModel bakedModelCore = subComponent.bake(state, format, bakedTextureGetter);
			return bakedModelCore;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public IModelState getDefaultState() {
		return State.myState;
	}

	
	private final static class State implements IModelState {

		static State myState = new State();
		
		@Override
		public Optional<TRSRTransformation> apply(
				Optional<? extends IModelPart> part) {
			return Optional.empty();
		}
		
	}
}
