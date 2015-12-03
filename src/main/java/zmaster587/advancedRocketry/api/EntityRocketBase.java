package zmaster587.advancedRocketry.api;

import java.util.LinkedList;

import net.minecraft.entity.Entity;
import net.minecraft.world.World;



public abstract class EntityRocketBase extends Entity {

	//Linked list containing Objects implementing IInfrastructure
	protected LinkedList<IInfrastructure> connectedInfrastructure;
	
	public EntityRocketBase(World world) {
		super(world);
	}

	/**
	 * AttempTs to add amt fuel points to the rocket
	 * @param amt
	 * @return the amount of fuel actually added to the rocket
	 */
	public abstract int addFuelAmount(int amt);

	/**
	 * Unlinks the given infrastructure
	 * @param infrastructure
	 */
	public void unlinkInfrastructure(IInfrastructure tile) {
		connectedInfrastructure.remove(tile);
	}

	/**
	 * Links the supplied IInfrastructure with the rocket
	 * @param tile
	 */
	public void linkInfrastructure(IInfrastructure tile) {
		if(tile.linkRocket(this));
		connectedInfrastructure.add(tile);
	}
	
	/**
	 * Launches the rocket
	 */
	public abstract void launch();

	/**
	 * @return the amount of fuel points in the rocket
	 */
	public abstract int getFuelAmount();

	/**
	 * @return the total fuel capacity of the rocket
	 */
	public abstract int getFuelCapacity();

	/**
	 * @return the location of the rocket in the world
	 */
	//Vector3F<Double> getLocation();
	
	/**
	 * @return the velocity of the rocket
	 */
	//Vector3F<Double> getVelocity();
	
	/**
	 * @return the stats used to represent the rocket
	 */
	public abstract StatsRocket getRocketStats();
	
	/**
	 * Deconstructs the rocket, replacing it with actual blocks
	 */
	public abstract void deconstructRocket();
}
