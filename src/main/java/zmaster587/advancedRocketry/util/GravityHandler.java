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
import net.minecraft.util.EnumFacing;

public class GravityHandler implements IGravityManager {

	public static final float ENTITY_OFFSET = 0.075f;
	public static final float ITEM_GRAV_OFFSET = 0.04f;
	private static WeakHashMap<Entity, Double> entityMap = new WeakHashMap<Entity, Double>();

	public static void applyGravity(Entity entity) {
		if(!entity.isInWater() || entity instanceof EntityItem) {
			if(!(entity instanceof EntityPlayer) || !((EntityPlayer)entity).capabilities.isFlying) {
				Double d;
				if(entityMap.containsKey(entity) && (d = entityMap.get(entity)) != null)  {

					double multiplier = (entity instanceof EntityItem) ? ITEM_GRAV_OFFSET*d : 0.075f*d;

					entity.motionY += multiplier;
					
				}
				else if(DimensionManager.getInstance().isDimensionCreated(entity.worldObj.provider.getDimension()) || entity.worldObj.provider instanceof WorldProviderSpace) {
					double gravMult;


					if(entity.worldObj.provider instanceof IPlanetaryProvider)
						gravMult = ((IPlanetaryProvider)entity.worldObj.provider).getGravitationalMultiplier(entity.getPosition());
					else
						gravMult = DimensionManager.getInstance().getDimensionProperties(entity.worldObj.provider.getDimension()).gravitationalMultiplier;

					if(entity instanceof EntityItem)
						entity.motionY -= gravMult*ITEM_GRAV_OFFSET;
					else
						entity.motionY -= gravMult*ENTITY_OFFSET;
					return;
				}
				else {
					if(entity instanceof EntityItem)
						entity.motionY -= ITEM_GRAV_OFFSET;
					else
						entity.motionY -= ENTITY_OFFSET + 0.005f;
				}
			}		
		}
	}

	@Override
	public void setGravityMultiplier(Entity entity, double multiplier) {
		//TODO: packet handling
		entityMap.put(entity, multiplier);
	}

	@Override
	public void clearGravityEffect(Entity entity) {
		entityMap.remove(entity);
	}
}
