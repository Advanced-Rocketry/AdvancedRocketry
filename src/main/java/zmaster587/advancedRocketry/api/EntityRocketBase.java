package zmaster587.advancedRocketry.api;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import zmaster587.advancedRocketry.api.fuel.FuelRegistry;
import zmaster587.advancedRocketry.api.stations.ISpaceObject;
import zmaster587.advancedRocketry.dimension.DimensionManager;
import zmaster587.libVulpes.util.HashedBlockPosition;
import zmaster587.libVulpes.util.ZUtils;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
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
		this(AdvancedRocketryEntities.ENTITY_ROCKET, world);
	}

	public EntityRocketBase(EntityType<?> type, World world) {
		super(type, world);
	}

	@Override
	public void recalculateSize() {
		// don't recalculate size, it causes NPEs because the forge event returns null for size on the client
	}
	
	/**
	 * Unlinks the given infrastructure
	 * @param tile the tile to unlink
	 */
	public void unlinkInfrastructure(IInfrastructure tile) {
		connectedInfrastructure.remove(tile);
	}

	/**
	 * Links the supplied IInfrastructure with the rocket
	 * @param tile the tile to link
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
	 * @param fuelType the fuel type to use
	 * @return the amount of fuel stored in the rocket
	 */
	public abstract int getFuelAmount(@Nullable FuelRegistry.FuelType fuelType);

	/**
	 * Adds fuel and updates the datawatcher
	 * @param fuelType the fuel type to use
	 * @param amount amount of fuel to add
	 * @return the amount of fuel added
	 */
	public abstract int addFuelAmount(@Nonnull FuelRegistry.FuelType fuelType, int amount);

	/**
	 * Updates the data option
	 * @param fuelType the fuel type to use
	 * @param amt sets the amount of monopropellant fuel in the rocket
	 */
	public abstract void setFuelAmount(@Nonnull FuelRegistry.FuelType fuelType, int amt);

	/**
	 * @param fuelType sets the type of fuel to set a rate for
	 * @param rate sets the rate of fuel in the rocket
	 */
	public abstract void setFuelConsumptionRate(@Nonnull FuelRegistry.FuelType fuelType, int rate);

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
		
		if(DimensionManager.spaceId.equals(ZUtils.getDimensionIdentifier(this.world)) ) {
			ISpaceObject station = AdvancedRocketryAPI.spaceObjectManager.getSpaceStationFromBlockCoords(new BlockPos(this.getPositionVec()));

			if(station != null) {
				station.setPadStatus((int)Math.floor(this.getPosX()), (int)Math.floor(this.getPosZ()), false);
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
