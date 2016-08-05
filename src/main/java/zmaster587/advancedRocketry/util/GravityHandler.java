package zmaster587.advancedRocketry.util;

import zmaster587.advancedRocketry.api.IPlanetaryProvider;
import zmaster587.advancedRocketry.dimension.DimensionManager;
import zmaster587.advancedRocketry.world.provider.WorldProviderSpace;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;

public class GravityHandler {
	public static void applyGravity(Entity entity) {

		if(!entity.isInWater()) {
			if(!(entity instanceof EntityPlayer) || !((EntityPlayer)entity).capabilities.isFlying) {
				if(DimensionManager.getInstance().isDimensionCreated(entity.worldObj.provider.dimensionId) || entity.worldObj.provider instanceof WorldProviderSpace) {
					double gravMult;
					if(entity.worldObj.provider instanceof IPlanetaryProvider)
						gravMult = ((IPlanetaryProvider)entity.worldObj.provider).getGravitationalMultiplier((int)entity.posX, (int)entity.posZ);
					else
						gravMult = DimensionManager.getInstance().getDimensionProperties(entity.worldObj.provider.dimensionId).gravitationalMultiplier;

					if(entity instanceof EntityItem)
						entity.motionY -= gravMult*0.04f;
					else
						entity.motionY -= gravMult*0.075f;
					return;
				}
				else
					entity.motionY -= 0.08D;
			}		
			
		}

	}
}
