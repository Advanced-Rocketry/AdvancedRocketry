package zmaster587.advancedRocketry.client;

import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;

public class ClientRenderHelper {

	static int oldRenderDistance;
	static boolean renderDistanceOverriden = false;
	static final int INTERPLANETARY_RENDER_DISTANCE = 16;


	public static void setOverridenRenderDistance(int distance)
	{
		if(!renderDistanceOverriden)
			oldRenderDistance = Minecraft.getMinecraft().gameSettings.renderDistanceChunks;
		Minecraft.getMinecraft().gameSettings.renderDistanceChunks = distance;
		try {
			ObfuscationReflectionHelper.setPrivateValue(net.minecraft.client.renderer.RenderGlobal.class, Minecraft.getMinecraft().renderGlobal, distance, "renderDistanceChunks");
		}
		catch(Exception e)
		{
			
		}
		
		
		renderDistanceOverriden = true;

	}

	public static void RestoreRenderDistance()
	{
		if(renderDistanceOverriden)
		{
			renderDistanceOverriden = false;
			Minecraft.getMinecraft().gameSettings.renderDistanceChunks = oldRenderDistance;
		}
	}

}
