package zmaster587.advancedRocketry.client;

import zmaster587.advancedRocketry.client.model.ModelRocket;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.ICustomModelLoader;
import net.minecraftforge.client.model.IModel;
import net.minecraftforge.client.model.ModelLoaderRegistry;

public class ModelLoader implements ICustomModelLoader
{
	public final String SMART_MODEL_RESOURCE_LOCATION = "models/";

	// return true if our Model Loader accepts this ModelResourceLocation
	@Override
	public boolean accepts(ResourceLocation resourceLocation) {
		return resourceLocation.getResourceDomain().equals("advancedrocketry")
				&& resourceLocation.getResourcePath().contains("rocketmotor");
	}

	// When called for our Block3DWeb's ModelResourceLocation, return our WebModel.
	@Override
	public IModel loadModel(ResourceLocation resourceLocation) {
		String resourcePath = resourceLocation.getResourcePath();
		/*if (!resourcePath.startsWith(SMART_MODEL_RESOURCE_LOCATION)) {
			assert false : "loadModel expected " + SMART_MODEL_RESOURCE_LOCATION + " but found " + resourcePath;
		}*/
		String modelName = resourcePath;//.substring(SMART_MODEL_RESOURCE_LOCATION.length());

		if (modelName.contains("rocketmotor")) {
			return new ModelRocket();
		} else {
			try {
				return ModelLoaderRegistry.getModel(new ResourceLocation(modelName));
			} catch (Exception e) {
				return ModelLoaderRegistry.getMissingModel();
			}// ModelLoaderRegistry.getMissingModel();
		}
	}

	// don't need it for this example; you might.  We have to implement it anyway.
	@Override
	public void onResourceManagerReload(IResourceManager resourceManager) {
		this.resourceManager = resourceManager;
	}

	private IResourceManager resourceManager;
}