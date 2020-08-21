package zmaster587.advancedRocketry.api;

import net.minecraft.entity.Entity;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import zmaster587.advancedRocketry.api.stations.ISpaceObject;
import zmaster587.libVulpes.util.HashedBlockPosition;

import java.util.LinkedList;
import java.util.Set;



public abstract class EntityRocketBase extends Entity {

	//Linked list containing Objects implementing IInfrastructure
	protected LinkedList<IInfrastructure> connectedInfrastructure;
	
	//stores the coordinates of infrastructures, used for when the world loads/saves
	protected Set<HashedBlockPosition> infrastructureCoords;
	
	//Stores the blocks and tiles that make up the rocket
	//public StorageChunk storage;

	//Stores other info about the rocket such as fuel and acceleration properties
	public StatsRocket stats;
	
	public EntityRocketBase(World world) {
		super(world);
	}

	/**
	 * AttempTs to add amt fuel points to the rocket for monopropellants
	 * @param amt
	 * @return the amount of fuel actually added to the rocket
	 */
	public abstract int addFuelAmountMonopropellant(int amt);

	/**
	 * AttempTs to add amt fuel points to the rocket for bipropellants
	 * @param amt
	 * @return the amount of fuel actually added to the rocket
	 */
	public abstract int addFuelAmountBipropellant(int amt);

	/**
	 * AttempTs to add amt fuel points to the rocket for oxidizers
	 * @param amt
	 * @return the amount of fuel actually added to the rocket
	 */
	public abstract int addFuelAmountOxidizer(int amt);

	/**
	 * AttempTs to set the fuel rate for the rocket for monopropellants
	 * @param rate
	 * @return the rate of consumption for the rocket
	 */
	public abstract void setFuelRateMonopropellant(int rate);

	/**
	 * AttempTs to set the fuel rate for the rocket for bipropellants
	 * @param rate
	 * @return the rate of consumption for the rocket
	 */
	public abstract void setFuelRateBipropellant(int rate);

	/**
	 * AttempTs to set the fuel rate for the rocket for oxidizers
	 * @param rate
	 * @return the rate of consumption for the rocket
	 */
	public abstract void setFuelRateOxidizer(int rate);

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
		if(!connectedInfrastructure.contains(tile) && tile.linkRocket(this))
			connectedInfrastructure.add(tile);
	}
	
	/**
	 * Called when the player is sitting in the rocket and hits the launch key, this is where countdown begin/gui should be called from before launch
	 */
	public abstract void prepareLaunch();
	
	/**
	 * Handles actually launching the rocket
	 */
	public abstract void launch();

	/**
	 * @return the amount of fuel points in the rocket for monopropellants
	 */
	public abstract int getFuelAmountMonopropellant();

	/**
	 * @return the amount of fuel points in the rocket for bipropellants
	 */
	public abstract int getFuelAmountBipropellant();

	/**
	 * @return the amount of fuel points in the rocket for oxidizers
	 */
	public abstract int getFuelAmountOxidizer();

	/**
	 * @return the total fuel capacity of the rocket for monopropellants
	 */
	public abstract int getFuelCapacityMonopropellant();

	/**
	 * @return the total fuel capacity of the rocket for bipropellants
	 */
	public abstract int getFuelCapacityBipropellant();

	/**
	 * @return the total fuel capacity of the rocket for oxidizers
	 */
	public abstract int getFuelCapacityOxidizer();

	/**
	 * @return the rate of consumption for monopropellants
	 */
	public abstract int getFuelRateMonopropellant();

	/**
	 * @return the rate of consumption for bipropellants
	 */
	public abstract int getFuelRateBipropellant();

	/**
	 * @return the rate of consumption  for oxidizers
	 */
	public abstract int getFuelRateOxidizer();

	/**
	 * @return the location of the rocket in the world
	 */
	//Vector3F<Double> getLocation();
	
	/**
	 * @return the velocity of the rocket
	 */
	//Vector3F<Double> getVelocity();
	
	public String getTextOverlay() {
		return "";
	}
	
	/**
	 * @return the stats used to represent the rocket
	 */
	public abstract StatsRocket getRocketStats();
	
	/**Called when orbit is reached by a rocket*/
	public void onOrbitReached() {
		MinecraftForge.EVENT_BUS.post(new RocketEvent.RocketReachesOrbitEvent(this));
		
		if(this.world.provider.getDimension() == ARConfiguration.getCurrentConfig().spaceDimId) {
			ISpaceObject station = AdvancedRocketryAPI.spaceObjectManager.getSpaceStationFromBlockCoords(this.getPosition());
			
			if(station instanceof ISpaceObject) {
				((ISpaceObject)station).setPadStatus((int)Math.floor(this.posX), (int)Math.floor(this.posZ), false);
			}
		}
	}
	
	/**
	 * Deconstructs the rocket, replacing it with actual blocks
	 */
	public void deconstructRocket() {
		MinecraftForge.EVENT_BUS.post(new RocketEvent.RocketDismantleEvent(this));
	}
}
