package zmaster587.advancedRocketry.api;

import zmaster587.advancedRocketry.api.dimension.IDimensionProperties;
import net.minecraft.util.Vec3;

public interface IPlanetaryProvider {
	
	/**
	 * 100 is earth-like, 0 is none
	 * @return multiplier of gravity
	 */
	public double getGravitationalMultiplier(int x, int z);
	
	/**
	 * @return the dimension ID this one is in orbit around
	 */
	public int getOrbitingDimension(int x, int z);
	
	/**
	 * @return array of dimension IDs orbiting this one
	 */
	public int[] getDimensionsInOrbit(int x, int z);
	
	/**
	 * Earth is 100 (0 if no atmosphere)
	 * @return Density of atmosphere
	 */
	public float getAtmosphereDensity(int x, int z);
	
	/**
	 * @return Temperature of the planet
	 */
	public int getAverageTemperature(int x, int z);

	/**
	 * @return time in ticks for day/night cycle
	 */
	public int getRotationalPeriod(int x, int z);
	
	/**
	 * earth is 50
	 * @return wetness of a planet
	 */
	public int getWetness();
	
	/**
	 * Earth to moon is 100
	 * @return Distance to parent body, used in maps and fuel consumption calculations
	 */
	public int getOrbitalDistance(int x, int z);
	
	/**
	 * @return if the dimension is a planet vs spacecraft etc
	 */
	public boolean isPlanet();
	
	/**
	 * @return color of the sun in RGB
	 */
	public Vec3  getSunColor(int x, int z);
	
	/**
	 * @param x location in block coords
	 * @param z location in block coords
	 * @return {@link DimensionProperties} of this dimension
	 */
	public IDimensionProperties getDimensionProperties(int x, int z);

	/**
	 * @param y height
	 * @return Absolute density of the atmosphere at the given height
	 */
	public float getAtmosphereDensityFromHeight(double y, int x, int z);

}
