package zmaster587.advancedRocketry.util;

import net.minecraft.entity.Entity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import zmaster587.advancedRocketry.AdvancedRocketry;
import zmaster587.advancedRocketry.api.ARConfiguration;
import zmaster587.advancedRocketry.api.AdvancedRocketryAPI;
import zmaster587.advancedRocketry.api.IGravityManager;
import zmaster587.advancedRocketry.api.IPlanetaryProvider;
import zmaster587.advancedRocketry.dimension.DimensionManager;
import zmaster587.libVulpes.util.ZUtils;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.WeakHashMap;

public class GravityHandler implements IGravityManager {

	public static final float ENTITY_OFFSET = 0.0755f;
	public static final float ITEM_GRAV_OFFSET = 0.04f;
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
		if(entity.hasNoGravity()) return;
		if(!entity.isInWater() || entity instanceof ItemEntity) {
			if(!(entity instanceof PlayerEntity) || !((PlayerEntity)entity).abilities.isFlying) {
				Double d;
				if(entityMap.containsKey(entity) && (d = entityMap.get(entity)) != null)  {

					double multiplier = (entity instanceof ItemEntity) ? ITEM_GRAV_OFFSET*d : 0.075f*d;

					entity.setMotion(entity.getMotion().add(0, multiplier, 0));
					
				}
				else if(DimensionManager.getInstance().isDimensionCreated(ZUtils.getDimensionIdentifier(entity.world)) || ZUtils.getDimensionIdentifier(entity.world) == ARConfiguration.getCurrentConfig().spaceDimId.get()) {
					double gravMult;

					gravMult = DimensionManager.getInstance().getDimensionProperties(entity.world).gravitationalMultiplier;
					if(entity instanceof ItemEntity)
						entity.setMotion(entity.getMotion().add(0, -gravMult*ITEM_GRAV_OFFSET, 0));
					else//Not-Items are not ASMed, so they have to subtract the original gravity.
						entity.setMotion(entity.getMotion().add(0, -(gravMult*ENTITY_OFFSET - ENTITY_OFFSET), 0));
					return;
				}
				else {
					//GC handling
					/*if(gcWorldProvider != null && gcWorldProvider.isAssignableFrom(entity.world.provider.getClass())) {
						try {
							entity.getMotion().y -= 0.075f - (float)gcGetGravity.invoke(entity.world.provider);
						} catch (IllegalAccessException | IllegalArgumentException
								| InvocationTargetException e) {
							e.printStackTrace();
						}
					}
					else {*/
						if(entity instanceof ItemEntity)
							entity.setMotion(entity.getMotion().add(0, -ITEM_GRAV_OFFSET, 0));
						//else//Without the ASM, this added extra gravity in overworld on SMP
							//entity.getMotion().y -= 0.005d;
					//}
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
