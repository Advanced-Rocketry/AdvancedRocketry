package zmaster587.advancedRocketry.mission;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.common.DimensionManager;
import zmaster587.advancedRocketry.api.satellite.SatelliteBase;
import zmaster587.advancedRocketry.entity.EntityRocket;

public class OreMiningMission extends SatelliteBase {

	long startWorldTime;
	long duration;
	public OreMiningMission(long duration, EntityRocket entity) {
		startWorldTime = DimensionManager.getWorld(0).getTotalWorldTime();
		this.duration = duration;
	}
	
	public double getProgress() {
		return (DimensionManager.getWorld(0).getTotalWorldTime() - startWorldTime) / duration;
	}
	
	@Override
	public String getInfo(World world) {
		return null;
	}

	@Override
	public String getName() {
		return "";
	}

	@Override
	public boolean performAction(EntityPlayer player, World world, int x,
			int y, int z) {
		return false;
	}

	@Override
	public double failureChance() {
		return 0;
	}
	
	@Override
	public boolean canTick() {
		return true;
	}
	
	@Override
	public void tickEntity() {
		
	}
}
