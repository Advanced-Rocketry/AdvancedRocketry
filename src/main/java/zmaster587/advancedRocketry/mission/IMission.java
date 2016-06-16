package zmaster587.advancedRocketry.mission;

import net.minecraft.world.World;

public interface IMission {
	public void onMissionComplete();

	public double getProgress(World world);
}
