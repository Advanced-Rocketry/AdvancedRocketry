package zmaster587.advancedRocketry.api;

import zmaster587.libVulpes.util.Vector3F;



public interface IEntityRocket {

	/**
	 * AttempTs to add amt fuel points to the rocket
	 * @param amt
	 * @return the amount of fuel actually added to the rocket
	 */
	int addFuelAmount(int amt);

	/**
	 * Unlinks the given infrastructure
	 * @param infrastructure
	 */
	void unlinkInfrastructure(IInfrastructure infrastructure);

	/**
	 * Launches the rocket
	 */
	void launch();

	/**
	 * @return the amount of fuel points in the rocket
	 */
	int getFuelAmount();

	/**
	 * @return the total fuel capacity of the rocket
	 */
	int getFuelCapacity();

	/**
	 * @return the location of the rocket in the world
	 */
	Vector3F<Double> getLocation();
	
	/**
	 * @return the velocity of the rocket
	 */
	Vector3F<Double> getVelocity();
	
	/**
	 * @return the stats used to represent the rocket
	 */
	StatsRocket getRocketStats();
	
	/**
	 * Deconstructs the rocket, replacing it with actual blocks
	 */
	public void deconstructRocket();
	/**
	 * Links the supplied IInfrastructure with the rocket
	 * @param tile
	 */
	void linkInfrastructure(IInfrastructure tile);
}
