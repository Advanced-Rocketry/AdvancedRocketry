package zmaster587.advancedRocketry.api.dimension.solar;

import zmaster587.advancedRocketry.api.dimension.IDimensionProperties;
import zmaster587.advancedRocketry.api.satellite.SatelliteBase;

import java.util.Collection;

import net.minecraft.util.ResourceLocation;

public interface IGalaxy {
	Collection<StellarBody> getStars();
	
	/**
	 * @return an Integer array of dimensions registered with this DimensionManager
	 */
<<<<<<< HEAD
	public ResourceLocation[] getRegisteredDimensions();
=======
	Integer[] getRegisteredDimensions();
>>>>>>> origin/feature/nuclearthermalrockets
	
	/**
	 * @param satId long id of the satellite
	 * @return a reference to the satellite object with the supplied ID
	 */
	SatelliteBase getSatellite(long satId);
	
	/**
	 * @param dimId dimension id to check
	 * @return true if it can be traveled to, in general if it has a surface
	 */
<<<<<<< HEAD
	public boolean canTravelTo(ResourceLocation dimId);
=======
	boolean canTravelTo(int dimId);
>>>>>>> origin/feature/nuclearthermalrockets
	
	/**
	 * 
	 * @param dimId id of the dimension of which to get the properties
	 * @return DimensionProperties representing the dimId given
	 */
<<<<<<< HEAD
	public IDimensionProperties getDimensionProperties(ResourceLocation dimId);
=======
	IDimensionProperties getDimensionProperties(int dimId);
>>>>>>> origin/feature/nuclearthermalrockets
	
	/**
	 * @param id star id for which to get the object
	 * @return the {@link StellarBody} object
	 */
<<<<<<< HEAD
	public StellarBody getStar(ResourceLocation id);
=======
	StellarBody getStar(int id);
>>>>>>> origin/feature/nuclearthermalrockets
	
	/**
	 * @param dimId integer id of the dimension
	 * @return true if the dimension exists and is registered
	 */
<<<<<<< HEAD
	public boolean isDimensionCreated( ResourceLocation dimId );
=======
	boolean isDimensionCreated(int dimId);
>>>>>>> origin/feature/nuclearthermalrockets
	
	/**
	 * 
	 * @param destinationDimId
	 * @param dimension
	 * @return true if the two dimensions are in the same planet/moon system
	 */
<<<<<<< HEAD
	public boolean areDimensionsInSamePlanetMoonSystem(ResourceLocation destinationDimId,
			ResourceLocation dimension);
=======
	boolean areDimensionsInSamePlanetMoonSystem(int destinationDimId,
												int dimension);
>>>>>>> origin/feature/nuclearthermalrockets
}
