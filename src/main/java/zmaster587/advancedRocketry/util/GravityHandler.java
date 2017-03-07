package zmaster587.advancedRocketry.util;

import java.util.WeakHashMap;

import zmaster587.advancedRocketry.api.AdvancedRocketryAPI;
import zmaster587.advancedRocketry.api.IGravityManager;
import zmaster587.advancedRocketry.api.IPlanetaryProvider;
import zmaster587.advancedRocketry.dimension.DimensionManager;
import zmaster587.advancedRocketry.world.provider.WorldProviderSpace;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;

public class GravityHandler implements IGravityManager {
	
	static {
		AdvancedRocketryAPI.gravityManager = new GravityHandler();
	}

	private static WeakHashMap<Entity, Double> entityMap = new WeakHashMap<Entity, Double>();
	public static void applyGravity(Entity entity) {

		if(!entity.isInWater() || entity instanceof EntityItem) {
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
				else {
					if(entity instanceof EntityItem)
						entity.motionY -= 0.04f;
					else
						entity.motionY -= 0.08D;
				}
			}
		}

	}

	@Override
	public void setGravityMultiplier(Entity entity, double multiplier) {
		entityMap.put(entity, multiplier);
	}

	@Override
	public void clearGravityEffect(Entity entity) {
		entityMap.remove(entity);
	}
}
