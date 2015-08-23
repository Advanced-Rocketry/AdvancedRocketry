package zmaster587.advancedRocketry.api;

import zmaster587.advancedRocketry.world.DimensionProperties;
import net.minecraft.util.Vec3;

public interface IPlanetaryProvider {
	
	/**
	 * 100 is earth-like, 0 is none
	 * @return multiplier of gravity
	 */
	public double getGravitationalMultiplier();
	
	/**
	 * @return the dimension ID this one is in orbit around
	 */
	public int getOrbitingDimension();
	
	/**
	 * @return array of dimension IDs orbiting this one
	 */
	public int[] getDimensionsInOrbit();
	
	/**
	 * Earth is 100 (0 if no atmosphere)
	 * @return Density of atmosphere
	 */
	public float getAtmosphereDensity();
	
	/**
	 * @return Temperature of the planet
	 */
	public int getAverageTemperature();

	/**
	 * @return time in ticks for day/night cycle
	 */
	public int getRotationalPeriod();
	
	/**
	 * earth is 50
	 * @return wetness of a planet
	 */
	public int getWetness();
	
	/**
	 * Earth to moon is 100
	 * @return Distance to parent body, used in maps and fuel consumption calculations
	 */
	public int getOrbitalDistance();
	
	/**
	 * @return if the dimension is a planet vs spacecraft etc
	 */
	public boolean isPlanet();
	
	/**
	 * @return color of the sun in RGB
	 */
	public Vec3 getSunColor();
	
	/**
	 * @return {@link DimensionProperties} of this dimension
	 */
	public DimensionProperties getDimensionProperties();

	/**
	 * @param y height
	 * @return Absolute density of the atmosphere at the given height
	 */
	float getAtmosphereDensityFromHeight(double y);
}
