package zmaster587.advancedRocketry.util;

import zmaster587.advancedRocketry.api.IPlanetaryProvider;
import zmaster587.advancedRocketry.dimension.DimensionManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;

public class GravityHandler {
	public static void applyGravity(Entity entity) {

		if(entity.worldObj.provider instanceof IPlanetaryProvider && !entity.isInWater()) {
			IPlanetaryProvider planet = (IPlanetaryProvider)entity.worldObj.provider;
			if(!(entity instanceof EntityPlayer) || !((EntityPlayer)entity).capabilities.isFlying) {

				if(entity instanceof EntityItem)
					entity.motionY -= planet.getGravitationalMultiplier((int)entity.posX, (int)entity.posZ)*0.04f;
				else
					entity.motionY -= planet.getGravitationalMultiplier((int)entity.posX, (int)entity.posZ)*0.075f;
			}
		}
		else if(entity.worldObj.provider.dimensionId == 0) {
			if(!(entity instanceof EntityPlayer) || !((EntityPlayer)entity).capabilities.isFlying) {
				if(entity instanceof EntityItem)
					entity.motionY -= DimensionManager.overworldProperties.gravitationalMultiplier*0.04f;
				else
					entity.motionY -= DimensionManager.overworldProperties.gravitationalMultiplier*0.075f;
			}
		}
		else
			entity.motionY -= 0.08D;
	}
}
