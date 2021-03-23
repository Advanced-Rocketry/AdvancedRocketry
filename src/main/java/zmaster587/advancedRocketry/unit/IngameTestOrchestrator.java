package zmaster587.advancedRocketry.unit;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import zmaster587.advancedRocketry.AdvancedRocketry;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

public class IngameTestOrchestrator {
	
	static final Map<Long, PlayerMapping> eventScheduler = new HashMap<Long, PlayerMapping>();
	public static String name;
	public static boolean registered = false;
	public static IngameTestOrchestrator instance = new IngameTestOrchestrator();
	
	@SubscribeEvent
	public void serverTickEvent(TickEvent.WorldTickEvent event) {
		Iterator<Entry<Long, PlayerMapping>> itr = eventScheduler.entrySet().iterator();
		while (itr.hasNext())
		{
			Entry<Long, PlayerMapping> e = itr.next();
			if(event.world.getTotalWorldTime() >= e.getKey())
			{
				itr.remove();
				BaseTest test = e.getValue().test;
				try {
					e.getValue().func.invoke(test, e.getValue().world, getPlayerFromAnywhere());
				} catch (AssertionError e1) {
					AdvancedRocketry.logger.error("Test Failed!!!");
					AdvancedRocketry.logger.catching(e1);
					getPlayerFromAnywhere().sendMessage(new TextComponentString(test.getName() + " Failed!"));
				} catch (Exception e2) {
					e2.printStackTrace();
				}
				
				if(test.passed())
				{
					getPlayerFromAnywhere().sendMessage(new TextComponentString(test.getName() + " Passed!"));
				}
			}
		}
	}
	
	public static boolean runTests(World world, EntityPlayer player)
	{
		name = player.getName();
		BuildRocketTest buildRocketTest = new BuildRocketTest();
		try {
			IngameTestOrchestrator.scheduleEvent(world, 1, BuildRocketTest.class.getDeclaredMethod("Phase1", World.class, EntityPlayer.class), buildRocketTest);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return true;
	}
	
	public static void scheduleEvent(World world, long numTicks, Method function, BaseTest test)
	{
		eventScheduler.put(world.getTotalWorldTime() + numTicks, new PlayerMapping(world, function, test));
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
	
	public static EntityPlayer getPlayerFromAnywhere() {
		return getPlayerByName(name);
	}
	
	private static EntityPlayer getPlayerByName(String name) {
		EntityPlayer player = null;
		for(World world : net.minecraftforge.common.DimensionManager.getWorlds()) {
			player = world.getPlayerEntityByName(name);
			if ( player != null) break;
		}

		return player;
	}
}
