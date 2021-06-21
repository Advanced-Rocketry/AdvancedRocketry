package zmaster587.advancedRocketry.unit;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import com.google.common.base.Predicate;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.Util;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.server.ServerLifecycleHooks;
import zmaster587.advancedRocketry.AdvancedRocketry;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

public class IngameTestOrchestrator {
	
	static final Map<Long, PlayerMapping> eventScheduler = new HashMap<>();
	public static String name;
	public static boolean registered = false;
	public static IngameTestOrchestrator instance = new IngameTestOrchestrator();
	
	@SubscribeEvent
	public void serverTickEvent(TickEvent.WorldTickEvent event) {
		Iterator<Entry<Long, PlayerMapping>> itr = eventScheduler.entrySet().iterator();
		while (itr.hasNext())
		{
			Entry<Long, PlayerMapping> e = itr.next();
			if(event.world.getGameTime() >= e.getKey())
			{
				itr.remove();
				BaseTest test = e.getValue().test;
				try {
					e.getValue().func.invoke(test, e.getValue().world, getPlayerFromAnywhere());
				} catch (AssertionError e1) {
					AdvancedRocketry.logger.error("Test Failed!!!");
					AdvancedRocketry.logger.catching(e1);
					getPlayerFromAnywhere().sendMessage(new StringTextComponent(test.getName() + " Failed!"), Util.DUMMY_UUID);
				} catch (Exception e2) {
					e2.printStackTrace();
				}
				
				if(test.passed())
				{
					getPlayerFromAnywhere().sendMessage(new StringTextComponent(test.getName() + " Passed!"), Util.DUMMY_UUID);
				}
			}
		}
	}
	
	public static boolean runTests(World world, PlayerEntity player)
	{
		name = player.getName().getString();
		BuildRocketTest buildRocketTest = new BuildRocketTest();
		try {
			IngameTestOrchestrator.scheduleEvent(world, 1, BuildRocketTest.class.getDeclaredMethod("Phase1", World.class, PlayerEntity.class), buildRocketTest);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return true;
	}
	
	public static void scheduleEvent(World world, long numTicks, Method function, BaseTest test)
	{
		eventScheduler.put(world.getGameTime() + numTicks, new PlayerMapping(world, function, test));
	}
	
	private static class PlayerMapping
	{
		PlayerMapping(World world, Method func, BaseTest test )
		{
			this.func = func;
			this.world = world;
			this.test = test;
		}
		public Method func;
		public World world;
		public BaseTest test;
	}
	
	public static PlayerEntity getPlayerFromAnywhere() {
		return getPlayerByName(name);
	}
	
	private static PlayerEntity getPlayerByName(String name) {
		PlayerEntity player = null;
		for(ServerWorld world : ServerLifecycleHooks.getCurrentServer().getWorlds()) {
			player = (PlayerEntity) world.getPlayers(new Predicate<ServerPlayerEntity>() {
				public boolean apply(ServerPlayerEntity input) 
				{
					return input.getName().toString().equals(name);
				};
			});
			if ( player != null) break;
		}

		return player;
	}
}
