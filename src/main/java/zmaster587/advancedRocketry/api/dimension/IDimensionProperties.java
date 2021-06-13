package zmaster587.advancedRocketry.api.dimension;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import zmaster587.advancedRocketry.api.IAtmosphere;
import zmaster587.advancedRocketry.api.dimension.solar.StellarBody;
import zmaster587.advancedRocketry.api.satellite.SatelliteBase;
import zmaster587.advancedRocketry.dimension.DimensionProperties;
import zmaster587.advancedRocketry.util.SpacePosition;

import java.util.Collection;
import java.util.Set;

public interface IDimensionProperties {
	/**
	 * @return the DIMID of the planet
	 */
	int getId();
	
	/**
	 * @return the color of the sun as an array of floats represented as  {r,g,b}
	 */
	float[] getSunColor();
	
	/**
	 * @return the host star for this planet
	 */
	StellarBody getStar();
	
	/**
	 * @return position in space of the planet
	 */
	SpacePosition getSpacePosition();
	
	/**
	 * @return the name of the planet
	 */
	String getName();
	
	/**
	 * @return the {@link DimensionProperties} of the parent planet
	 */
	IDimensionProperties getParentProperties();
	
	/**
	 * Range 0 < value <= 200
	 * @return if the planet is a moon, then the distance from the host planet where the earth's moon is 100, higher is farther, if planet, distance from the star, 100 is earthlike, higher value is father
	 */
	int getParentOrbitalDistance();
	
	/**
	 * @return if a planet, the same as getParentOrbitalDistance(), if a moon, the moon's distance from the host star
	 */
	int getSolarOrbitalDistance();
	
	/**
	 * @return true if the planet has moons
	 */
	boolean hasChildren();
	
	/**
	 * @return true if this DIM orbits another
	 */
	boolean isMoon();
	
	/**
	 * @return the default atmosphere of this dimension
	 */
	IAtmosphere getAtmosphere();
	
	/**
	 * @return true if the planet has an atmosphere
	 */
	boolean hasAtmosphere();
	
	/**
	 * @return the multiplier compared to Earth(1040W) for peak insolation of the body
	 */
	double getPeakInsolationMultiplier();

	/**
	 * @return the multiplier compared to Earth(1040W) for peak insolation of the body, disregarding atmosphere
	 */
	double getPeakInsolationMultiplierWithoutAtmosphere();
	
	/**
	 * @return true if the planet has rings
	 */
	boolean hasRings();
	
	/**
	 * @return float[3] array containing ring color
	 */
	float[] getRingColor();
	
	/**
	 * @return float[3] array containing sky color
	 */
	float[] getSkyColor();
	
	/**
	 * @return set of all moons orbiting this planet
	 */
	Set<Integer> getChildPlanets();
	
	/**
	 * sets the gravity multiplier of the object
	 */
	void setGravitationalMultiplier(float mult);
	
	/**
	 * gets the gravity multiplier of the object
	 */
	float getGravitationalMultiplier();
	
	/**
	 * Adds a satellite to this DIM
	 * @param satellite satellite to add
	 * @param world world to add the satellite to
	 */
	void addSatellite(SatelliteBase satellite, World world);
	void addSatellite(SatelliteBase satellte);
	
	/**
	 * Returns the satellite with that ID
	 * @param lng satellite ID
	 * @return
	 */
	SatelliteBase getSatellite(long lng);

	/**
	 * Returns all of a dimension's satellites
	 * @return a Collection containing all of a dimension's satellites
	 */
	Collection<SatelliteBase> getAllSatellites();
	
	/**
	 * Removes the satellite from orbit around this world
	 * @param id ID # for this satellite
	 * @return reference to the satellite object
	 */
	SatelliteBase removeSatellite(long id);

	void writeToNBT(NBTTagCompound nbt);

	void readFromNBT(NBTTagCompound nbt);

	void setParentOrbitalDistance(int distance);

	/**
	 * @return true if the dimension is a gas giant
	 */
	boolean isGasGiant();

	/**
	 * @param posY height
	 * @return density of the atmosphere
	 */
	float getAtmosphereDensityAtHeight(double posY);

	/**
	 * @return the integer id of the star
	 */
	int getStarId();

	/**
	 * @return density of the atmosphere in the range 0 to 200
	 */
	int getAtmosphereDensity();

	/**
	 * @return true if terraforming activity has changed the planet properties
	 */
	boolean isTerraformed();

	/**
	 * @return reource location of the planet
	 */
	ResourceLocation getPlanetIcon();

	/**
	 * @return the location along the orbit in radians
	 */
	double getOrbitTheta();

	/**
	 * @return distance of the planet from sun or moon in the range 0 to 200
	 */
	int getOrbitalDist();

	/**
	 * @return temperature of the planet in Kelvin
	 */
	int getAverageTemp();
	
	/**
	 * @return depth in the tree planet is.  If the planet orbits the star directly, the result is 0, the moon 1, the moon of a moon 2, etc
	 */
	int getPathLengthToStar();

	/**
	 * @return id of the parent planet
	 */
	int getParentPlanet();

	/**
	 * Set the atmosphere like the terraformer does, can trigger terraform event
	 * @param i new atmosphere density
	 */
	void setAtmosphereDensity(int i);

	// Client side only
	ResourceLocation getPlanetIconLEO();
	
	float getRenderSizePlanetView();
	
	float getRenderSizeSolarView();
	
}
