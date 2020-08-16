package zmaster587.advancedRocketry.client;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.obj.OBJLoader;
import net.minecraftforge.client.model.obj.OBJModel;
import net.minecraftforge.client.model.obj.OBJModel.ModelSettings;

public class ModelRegistry {
	
	public static OBJModel elevatorModel;
	
	public static void init()
	{
		elevatorModel = OBJLoader.INSTANCE.loadModel(new ModelSettings(new ResourceLocation("advancedrocketry:models/spaceElevator.obj"), false, true, false, false, "advancedRocketry:textures/models/spaceElevatorCapsule.png"));
		
	}

}
