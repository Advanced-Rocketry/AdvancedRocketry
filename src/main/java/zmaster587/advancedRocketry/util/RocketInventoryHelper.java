package zmaster587.advancedRocketry.util;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;

import java.lang.ref.WeakReference;
import java.util.HashSet;
import java.util.WeakHashMap;

public class RocketInventoryHelper {
	
	//TODO: more robust way of inv checking
	//Has weak refs so if the player gets killed/logsout etc the entry doesnt stay trapped in RAM
<<<<<<< HEAD
	private static HashSet<WeakReference<PlayerEntity>> inventoryCheckPlayerBypassMap = new HashSet<WeakReference<PlayerEntity>>();
	private static WeakHashMap<PlayerEntity, Long> inventoryTimingMap = new WeakHashMap<PlayerEntity, Long>();
	private static WeakHashMap<PlayerEntity, BlockPos> inventoryDismapping = new WeakHashMap<PlayerEntity, BlockPos>();
=======
	private static HashSet<WeakReference<EntityPlayer>> inventoryCheckPlayerBypassMap = new HashSet<>();
	private static WeakHashMap<EntityPlayer, Long> inventoryTimingMap = new WeakHashMap<>();
	private static WeakHashMap<EntityPlayer, BlockPos> inventoryDismapping = new WeakHashMap<>();
>>>>>>> origin/feature/nuclearthermalrockets
	
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
	
<<<<<<< HEAD
	public static boolean canPlayerBypassInvChecks(PlayerEntity player) {
		Iterator<WeakReference<PlayerEntity>> iter = inventoryCheckPlayerBypassMap.iterator();
		while(iter.hasNext()) {
			WeakReference<PlayerEntity> player2 = iter.next();
			if(player2.get() == player)
=======
	public static boolean canPlayerBypassInvChecks(EntityPlayer player) {
		for (WeakReference<EntityPlayer> player2 : inventoryCheckPlayerBypassMap) {
			if (player2.get() == player)
>>>>>>> origin/feature/nuclearthermalrockets
				return true;
		}
		return false;
	}
	
<<<<<<< HEAD
	public static void removePlayerFromInventoryBypass(PlayerEntity player) {
		Iterator<WeakReference<PlayerEntity>> iter = inventoryCheckPlayerBypassMap.iterator();

		while(iter.hasNext()) {
			WeakReference<PlayerEntity> player2 = iter.next();
			if(player2.get() == player || player2.get() == null)
				iter.remove();
		}
=======
	public static void removePlayerFromInventoryBypass(EntityPlayer player) {

		inventoryCheckPlayerBypassMap.removeIf(player2 -> player2.get() == player || player2.get() == null);
>>>>>>> origin/feature/nuclearthermalrockets
	}

	public static void addPlayerToInventoryBypass(PlayerEntity player) {
		inventoryCheckPlayerBypassMap.add(new WeakReference<>(player));
	}

	public static void updateTime(PlayerEntity entity, long worldTime) {
		inventoryTimingMap.put(entity, worldTime);
		inventoryDismapping.put(entity, new BlockPos(entity.getPositionVec()));
	}
}
