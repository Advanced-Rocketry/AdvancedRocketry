package zmaster587.advancedRocketry.api;

import net.minecraft.entity.Entity;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import zmaster587.advancedRocketry.api.fuel.FuelRegistry;
import zmaster587.advancedRocketry.api.stations.ISpaceObject;
import zmaster587.libVulpes.util.HashedBlockPosition;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
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
	 * Unlinks the given infrastructure
	 * @param tile
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
	 * @param fuelType
	 * @return the amount of fuel stored in the rocket
	 */
	public abstract int getFuelAmount(@Nullable FuelRegistry.FuelType fuelType);

	/**
	 * Adds fuel and updates the datawatcher
	 * @param fuelType
	 * @param amount amount of fuel to add
	 * @return the amount of fuel added
	 */
	public abstract int addFuelAmount(@NotNull FuelRegistry.FuelType fuelType, int amount);

	/**
	 * Updates the data option
	 * @param fuelType
	 * @param amt sets the amount of monopropellant fuel in the rocket
	 */
	public abstract void setFuelAmount(@NotNull FuelRegistry.FuelType fuelType, int amt);

	/**
	 * @param fuelType sets the type of fuel to set a rate for
	 * @param rate sets the rate of fuel in the rocket
	 */
	public abstract void setFuelConsumptionRate(@NotNull FuelRegistry.FuelType fuelType, int rate);

	/**
	 * @param fuelType is the fuel type to get
	 * @return gets the fuel capacity of the rocket
	 */
	public abstract int getFuelCapacity(@Nullable FuelRegistry.FuelType fuelType);

	/**
	 * @param fuelType is the fuel type to get
	 * @return the rate of fuel consumption for the rocket
	 */
	public abstract int getFuelConsumptionRate(@Nullable FuelRegistry.FuelType fuelType);

	/**
	 * @return the fuel type that this rocket uses, null if the rocket does not use any
	 */
	@Nullable
	public abstract FuelRegistry.FuelType getRocketFuelType();

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
			
			if(station != null) {
				station.setPadStatus((int)Math.floor(this.posX), (int)Math.floor(this.posZ), false);
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
