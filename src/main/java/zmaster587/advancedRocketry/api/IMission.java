package zmaster587.advancedRocketry.api;

import zmaster587.advancedRocketry.api.IInfrastructure;
import net.minecraft.world.World;

public interface IMission {
	
	/**
	 * Called when the misson is complete
	 */
	public void onMissionComplete();

	/**
	 * @return Normallized progress of the the mission
	 */
	public double getProgress(World world);
	
	/**
	 * @return Satellite ID of the mission
	 */
	public long getMissionId();
	
	/**
	 * @return the dimension the mission originated from
	 */
	public int getOriginatingDimention();

	public void unlinkInfrastructure(IInfrastructure tile);

	int getTimeRemainingInSeconds();
}
