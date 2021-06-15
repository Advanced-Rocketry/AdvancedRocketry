package zmaster587.advancedRocketry.api.dimension.solar;

import zmaster587.advancedRocketry.api.dimension.IDimensionProperties;
import zmaster587.advancedRocketry.api.satellite.SatelliteBase;

import java.util.Collection;

public interface IGalaxy {
	Collection<StellarBody> getStars();
	
	/**
	 * @return an Integer array of dimensions registered with this DimensionManager
	 */
	Integer[] getRegisteredDimensions();
	
	/**
	 * @param satId long id of the satellite
	 * @return a reference to the satellite object with the supplied ID
	 */
	SatelliteBase getSatellite(long satId);
	
	/**
	 * @param dimId dimension id to check
	 * @return true if it can be traveled to, in general if it has a surface
	 */
	boolean canTravelTo(int dimId);
	
	/**
	 * 
	 * @param dimId id of the dimension of which to get the properties
	 * @return DimensionProperties representing the dimId given
	 */
	IDimensionProperties getDimensionProperties(int dimId);
	
	/**
	 * @param id star id for which to get the object
	 * @return the {@link StellarBody} object
	 */
	StellarBody getStar(int id);
	
	/**
	 * @param dimId integer id of the dimension
	 * @return true if the dimension exists and is registered
	 */
	boolean isDimensionCreated(int dimId);
	
	/**
	 * 
	 * @param destinationDimId
	 * @param dimension
	 * @return true if the two dimensions are in the same planet/moon system
	 */
	boolean areDimensionsInSamePlanetMoonSystem(int destinationDimId,
												int dimension);
}
