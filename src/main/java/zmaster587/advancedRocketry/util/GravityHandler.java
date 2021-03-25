package zmaster587.advancedRocketry.util;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityFlying;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import zmaster587.advancedRocketry.AdvancedRocketry;
import zmaster587.advancedRocketry.api.AdvancedRocketryAPI;
import zmaster587.advancedRocketry.api.IGravityManager;
import zmaster587.advancedRocketry.api.IPlanetaryProvider;
import zmaster587.advancedRocketry.dimension.DimensionManager;
import zmaster587.advancedRocketry.world.provider.WorldProviderSpace;

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
		if(!entity.isInWater() || entity instanceof EntityItem) {
			//Because working gravity on elytra-flying players can cause..... severe problems at lower gravity, it is my utter delight to announce to you elytra are now magic!
			//This totally isn't because Mojang decided for some godforsaken @#@#@#% reason to make ALL WAYS TO SET ELYTRA FLIGHT _protected_
			//With no set methods
			//So I cannot, without much more effort than it's worth, set elytra flight. Therefore, they're magic.
			if((!(entity instanceof EntityPlayer) && !(entity instanceof EntityFlying)) || (!(entity instanceof EntityFlying) && !(((EntityPlayer)entity).capabilities.isFlying || ((EntityLivingBase)entity).isElytraFlying()))) {
				Double d;
				if(entityMap.containsKey(entity) && (d = entityMap.get(entity)) != null)  {

					double multiplier = (entity instanceof EntityItem) ? ITEM_GRAV_OFFSET*d : 0.075f*d;

					entity.motionY += multiplier;
					
				}
				else if(DimensionManager.getInstance().isDimensionCreated(entity.world.provider.getDimension()) || entity.world.provider instanceof WorldProviderSpace) {
					double gravMult;

					if(entity.world.provider instanceof IPlanetaryProvider)
						gravMult = ((IPlanetaryProvider)entity.world.provider).getGravitationalMultiplier(entity.getPosition());
					else
						gravMult = DimensionManager.getInstance().getDimensionProperties(entity.world.provider.getDimension()).gravitationalMultiplier;
					if(entity instanceof EntityItem)
						entity.motionY -= gravMult*ITEM_GRAV_OFFSET;
					else//Not-Items are not ASMed, so they have to subtract the original gravity.
						entity.motionY -= (gravMult*ENTITY_OFFSET - ENTITY_OFFSET);
					return;
				}
				else {
					//GC handling
					if(gcWorldProvider != null && gcWorldProvider.isAssignableFrom(entity.world.provider.getClass())) {
						try {
							entity.motionY -= 0.075f - (float)gcGetGravity.invoke(entity.world.provider);
						} catch (IllegalAccessException | IllegalArgumentException
								| InvocationTargetException e) {
							e.printStackTrace();
						}
					}
					else {
						if(entity instanceof EntityItem)
							entity.motionY -= ITEM_GRAV_OFFSET;
						//else//Without the ASM, this added extra gravity in overworld on SMP
							//entity.motionY -= 0.005d;
					}
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
