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
