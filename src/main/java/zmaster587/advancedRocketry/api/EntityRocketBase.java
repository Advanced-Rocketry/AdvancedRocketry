package zmaster587.advancedRocketry.api;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import zmaster587.advancedRocketry.api.stations.ISpaceObject;
import zmaster587.libVulpes.util.HashedBlockPosition;
import zmaster587.libVulpes.util.ZUtils;

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
	
	public EntityRocketBase(EntityType<?> type, World world) {
		super(type, world);
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
		
		if(ZUtils.getDimensionIdentifier(this.world) == ARConfiguration.GetSpaceDimId()) {
			ISpaceObject station = AdvancedRocketryAPI.spaceObjectManager.getSpaceStationFromBlockCoords(new BlockPos(this.getPositionVec()));
			
			if(station instanceof ISpaceObject) {
				((ISpaceObject)station).setPadStatus((int)Math.floor(this.getPosX()), (int)Math.floor(this.getPosZ()), false);
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
