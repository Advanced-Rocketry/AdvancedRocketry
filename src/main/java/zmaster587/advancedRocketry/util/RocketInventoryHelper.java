package zmaster587.advancedRocketry.util;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;

import java.lang.ref.WeakReference;
import java.util.HashSet;
import java.util.Iterator;
import java.util.WeakHashMap;

public class RocketInventoryHelper {
	
	//TODO: more robust way of inv checking
	//Has weak refs so if the player gets killed/logsout etc the entry doesnt stay trapped in RAM
	private static HashSet<WeakReference<PlayerEntity>> inventoryCheckPlayerBypassMap = new HashSet<WeakReference<PlayerEntity>>();
	private static WeakHashMap<PlayerEntity, Long> inventoryTimingMap = new WeakHashMap<PlayerEntity, Long>();
	private static WeakHashMap<PlayerEntity, BlockPos> inventoryDismapping = new WeakHashMap<PlayerEntity, BlockPos>();
	
	//TODO: check for rocket
	public static boolean allowAccess(Object tile) {
		PlayerEntity player = (PlayerEntity)tile;
		
		
		//If a small amount of time is passed since interfacing with the rocket and the player has moved then assume the player is no longer accessing the rocket
		//and possibly trying to abuse AR to circumvent inv checks
		if(inventoryTimingMap.containsKey(player)) {
			if(inventoryTimingMap.get(player) + 10 < player.world.getGameTime() && 
					!inventoryDismapping.get(player).withinDistance(player.getPositionVec(),3) )
				removePlayerFromInventoryBypass(player);
		}
		//else
		//	removePlayerFromInventoryBypass(player);
		
		//return !player.worldObj.getEntitiesWithinAABB(EntityRocketBase.class, new AxisAlignedBB(player.getPosition().add(-64,-64,-64), player.getPosition().add(64,64,64))).isEmpty();
		
		return !canPlayerBypassInvChecks((PlayerEntity)tile);
	}
	
	public static boolean canPlayerBypassInvChecks(PlayerEntity player) {
		Iterator<WeakReference<PlayerEntity>> iter = inventoryCheckPlayerBypassMap.iterator();
		while(iter.hasNext()) {
			WeakReference<PlayerEntity> player2 = iter.next();
			if(player2.get() == player)
				return true;
		}
		return false;
	}
	
	public static void removePlayerFromInventoryBypass(PlayerEntity player) {
		Iterator<WeakReference<PlayerEntity>> iter = inventoryCheckPlayerBypassMap.iterator();

		while(iter.hasNext()) {
			WeakReference<PlayerEntity> player2 = iter.next();
			if(player2.get() == player || player2.get() == null)
				iter.remove();
		}
	}

	public static void addPlayerToInventoryBypass(PlayerEntity player) {
		inventoryCheckPlayerBypassMap.add(new WeakReference<>(player));
	}

	public static void updateTime(PlayerEntity entity, long worldTime) {
		inventoryTimingMap.put(entity, worldTime);
		inventoryDismapping.put(entity, new BlockPos(entity.getPositionVec()));
	}
}
