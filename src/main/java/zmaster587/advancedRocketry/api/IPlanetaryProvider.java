package zmaster587.advancedRocketry.api;

import zmaster587.advancedRocketry.world.DimensionProperties;
import net.minecraft.util.Vec3;

public interface IPlanetaryProvider {
	
	/**
	 * @return multiplier for gravity
	 */
	public double getGraviationalMultiplyer();
	
	public int getOrbitingDimension();
	
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
	 * 
	 * @return if the dimension is a planet vs spacecraft etc
	 */
	public boolean isPlanet();
	
	public Vec3 getSunColor();
	
	public DimensionProperties getDimensionProperties();

	float getAtmosphereDensityFromHeight(double y);
}
