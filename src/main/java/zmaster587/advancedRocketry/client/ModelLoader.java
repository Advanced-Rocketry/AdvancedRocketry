package zmaster587.advancedRocketry.client;

import net.minecraft.client.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.ICustomModelLoader;
import net.minecraftforge.client.model.IModel;
import net.minecraftforge.client.model.ModelLoaderRegistry;
import zmaster587.advancedRocketry.client.model.ModelRocket;

import org.jetbrains.annotations.NotNull;

public class ModelLoader implements ICustomModelLoader
{
	public final String SMART_MODEL_RESOURCE_LOCATION = "models/";
	private IResourceManager resourceManager;

	// return true if our Model Loader accepts this ModelResourceLocation
	@Override
	public boolean accepts(ResourceLocation resourceLocation) {
		return resourceLocation.getNamespace().equals("advancedrocketry")
				&& resourceLocation.getPath().contains("rocketmotor");
	}

	// When called for our Block3DWeb's ModelResourceLocation, return our WebModel.
	@Override
	@NotNull
	public IModel loadModel(ResourceLocation resourceLocation) {
		/*if (!resourcePath.startsWith(SMART_MODEL_RESOURCE_LOCATION)) {
			assert false : "loadModel expected " + SMART_MODEL_RESOURCE_LOCATION + " but found " + resourcePath;
		}*/

        if (resourceLocation.getPath().contains("rocketmotor")) {
			return new ModelRocket();
		} else {
			try {
				return ModelLoaderRegistry.getModel(new ResourceLocation(resourceLocation.getPath()));
			} catch (Exception e) {
				return ModelLoaderRegistry.getMissingModel();
			}// ModelLoaderRegistry.getMissingModel();
		}
	}

	// don't need it for this example; you might.  We have to implement it anyway.
	@Override
	public void onResourceManagerReload(@NotNull IResourceManager resourceManager) {
        this.resourceManager = resourceManager;
	}

}