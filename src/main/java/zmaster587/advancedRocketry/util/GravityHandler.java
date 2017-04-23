package zmaster587.advancedRocketry.util;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.WeakHashMap;

import zmaster587.advancedRocketry.AdvancedRocketry;
import zmaster587.advancedRocketry.api.AdvancedRocketryAPI;
import zmaster587.advancedRocketry.api.IGravityManager;
import zmaster587.advancedRocketry.api.IPlanetaryProvider;
import zmaster587.advancedRocketry.dimension.DimensionManager;
import zmaster587.advancedRocketry.world.provider.WorldProviderSpace;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;

public class GravityHandler implements IGravityManager {

	static Class gcWorldProvider;
	static Method gcGetGravity;
	
	static {
		AdvancedRocketryAPI.gravityManager = new GravityHandler();
		
		
		try {
			gcWorldProvider = Class.forName("micdoodle8.mods.galacticraft.api.world.IGalacticraftWorldProvider");
			AdvancedRocketry.logger.info("GC IGalacticraftWorldProvider  found");
			gcGetGravity = gcWorldProvider.getMethod("getGravity");
		} catch(ClassNotFoundException e){
			gcWorldProvider = null;
			AdvancedRocketry.logger.info("GC IGalacticraftWorldProvider not found");
		}
		catch(NoSuchMethodException e){
			gcWorldProvider = null;
			AdvancedRocketry.logger.info("GC IGalacticraftWorldProvider not found");
		}
	}
	
	private static WeakHashMap<Entity, Double> entityMap = new WeakHashMap<Entity, Double>();
	public static void applyGravity(Entity entity) {
		
		
		if(!entity.isInWater() || entity instanceof EntityItem) {
			if(!(entity instanceof EntityPlayer) || !((EntityPlayer)entity).capabilities.isFlying) {
				
				if(DimensionManager.getInstance().isDimensionCreated(entity.worldObj.provider.getDimension()) || entity.worldObj.provider instanceof WorldProviderSpace) {
					double gravMult;
					
					if(entityMap.containsKey(entity)) {
						gravMult = entityMap.get(entity);
					}else {
						if(entity.worldObj.provider instanceof IPlanetaryProvider)
							gravMult = ((IPlanetaryProvider)entity.worldObj.provider).getGravitationalMultiplier(entity.getPosition());
						else
							gravMult = DimensionManager.getInstance().getDimensionProperties(entity.worldObj.provider.getDimension()).gravitationalMultiplier;
					}
					if(entity instanceof EntityItem)
						entity.motionY -= gravMult*0.04f;
					else
						entity.motionY -= gravMult*0.075f;
					return;
				}
				else {
					//GC handling
					if(gcWorldProvider != null && gcWorldProvider.isAssignableFrom(entity.worldObj.provider.getClass())) {
						try {
							entity.motionY -= 0.075f - (float)gcGetGravity.invoke(entity.worldObj.provider);
						} catch (IllegalAccessException | IllegalArgumentException
								| InvocationTargetException e) {
							e.printStackTrace();
						}
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
