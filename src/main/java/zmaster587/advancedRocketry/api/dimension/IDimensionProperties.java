package zmaster587.advancedRocketry.api.dimension;

import java.util.Set;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import zmaster587.advancedRocketry.api.IAtmosphere;
import zmaster587.advancedRocketry.api.dimension.solar.StellarBody;
import zmaster587.advancedRocketry.api.satellite.SatelliteBase;

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

	public void writeToNBT(NBTTagCompound nbt);

	public void readFromNBT(NBTTagCompound nbt);
	
}
