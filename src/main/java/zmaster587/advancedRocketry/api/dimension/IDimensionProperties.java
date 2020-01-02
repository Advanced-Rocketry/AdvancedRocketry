package zmaster587.advancedRocketry.api.dimension;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import zmaster587.advancedRocketry.api.IAtmosphere;
import zmaster587.advancedRocketry.api.dimension.solar.StellarBody;
import zmaster587.advancedRocketry.api.satellite.SatelliteBase;
import zmaster587.advancedRocketry.util.SpacePosition;

import java.util.Set;

public interface IDimensionProperties {
	/**
	 * @return the DIMID of the planet
	 */
	public int getId();
	
	/**
	 * @return the color of the sun as an array of floats represented as  {r,g,b}
	 */
	public float[] getSunColor();
	
	/**
	 * @return the host star for this planet
	 */
	public StellarBody getStar();
	
	/**
	 * @return position in space of the planet
	 */
	public SpacePosition getSpacePosition();
	
	/**
	 * @return the name of the planet
	 */
	public String getName();
	
	/**
	 * @return the {@link DimensionProperties} of the parent planet
	 */
	public IDimensionProperties getParentProperties();
	
	/**
	 * Range 0 < value <= 200
	 * @return if the planet is a moon, then the distance from the host planet where the earth's moon is 100, higher is farther, if planet, distance from the star, 100 is earthlike, higher value is father
	 */
	public int getParentOrbitalDistance();
	
	/**
	 * @return if a planet, the same as getParentOrbitalDistance(), if a moon, the moon's distance from the host star
	 */
	public int getSolarOrbitalDistance();
	
	/**
	 * @return true if the planet has moons
	 */
	public boolean hasChildren();
	
	/**
	 * @return true if this DIM orbits another
	 */
	public boolean isMoon();
	
	/**
	 * @return the default atmosphere of this dimension
	 */
	public IAtmosphere getAtmosphere();
	
	/**
	 * @return true if the planet has an atmosphere
	 */
	public boolean hasAtmosphere();
	
	/**
	 * @return true if the planet has rings
	 */
	public boolean hasRings();
	
	/**
	 * @return float[3] array containing ring color
	 */
	public float[] getRingColor();
	
	/**
	 * @return float[3] array containing sky color
	 */
	public float[] getSkyColor();
	
	/**
	 * @return set of all moons orbiting this planet
	 */
	public Set<Integer> getChildPlanets();
	
	/**
	 * sets the gravity multiplier of the object
	 */
	public void setGravitationalMultiplier(float mult);
	
	/**
	 * gets the gravity multiplier of the object
	 */
	public float getGravitationalMultiplier();
	
	/**
	 * Adds a satellite to this DIM
	 * @param satellite satellite to add
	 * @param world world to add the satellite to
	 */
	public void addSatallite(SatelliteBase satellite, World world);
	public void addSatallite(SatelliteBase satellte);
	
	/**
	 * Returns the satellite with that ID
	 * @param lng satellite ID
	 * @return
	 */
	public SatelliteBase getSatellite(long lng);
	
	/**
	 * Removes the satellite from orbit around this world
	 * @param satalliteId ID # for this satellite
	 * @return reference to the satellite object
	 */
	public SatelliteBase removeSatellite(long id);

	public void writeToNBT(NBTTagCompound nbt);

	public void readFromNBT(NBTTagCompound nbt);

	public void setParentOrbitalDistance(int distance);

	/**
	 * @return true if the dimension is a gas giant
	 */
	public boolean isGasGiant();

	/**
	 * @param posY height
	 * @return density of the atmosphere
	 */
	public float getAtmosphereDensityAtHeight(double posY);

	/**
	 * @return the integer id of the star
	 */
	public int getStarId();

	/**
	 * @return density of the atmosphere in the range 0 to 200
	 */
	public int getAtmosphereDensity();

	/**
	 * @return true if terraforming activity has changed the planet properties
	 */
	public boolean isTerraformed();

	/**
	 * @return reource location of the planet
	 */
	public ResourceLocation getPlanetIcon();

	/**
	 * @return the location along the orbit in radians
	 */
	public double getOrbitTheta();

	/**
	 * @return distance of the planet from sun or moon in the range 0 to 200
	 */
	public int getOrbitalDist();

	
	/**
	 * @return depth in the tree planet is.  If the planet orbits the star directly, the result is 0, the moon 1, the moon of a moon 2, etc
	 */
	public int getPathLengthToStar();

	/**
	 * @return id of the parent planet
	 */
	public int getParentPlanet();

	/**
	 * Set the atmosphere like the terraformer does, can trigger terraform event
	 * @param i new atmosphere density
	 */
	public void setAtmosphereDensity(int i);

	// Client side only
	public ResourceLocation getPlanetIconLEO();
	
}
