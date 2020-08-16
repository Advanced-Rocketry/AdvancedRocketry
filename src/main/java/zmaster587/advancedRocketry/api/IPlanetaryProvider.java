package zmaster587.advancedRocketry.api;

import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import zmaster587.advancedRocketry.api.dimension.IDimensionProperties;

public interface IPlanetaryProvider {
	
	/**
	 * 100 is earth-like, 0 is none
	 * @return multiplier of gravity
	 */
	public double getGravitationalMultiplier(BlockPos pos);
	
	/**
	 * @return the dimension ID this one is in orbit around
	 */
	public int getOrbitingDimension(BlockPos pos);
	
	/**
	 * @return array of dimension IDs orbiting this one
	 */
	public int[] getDimensionsInOrbit(BlockPos pos);
	
	/**
	 * Earth is 100 (0 if no atmosphere)
	 * @return Density of atmosphere
	 */
	public float getAtmosphereDensity(BlockPos pos);
	
	/**
	 * @return Temperature of the planet
	 */
	public int getAverageTemperature(BlockPos pos);

	/**
	 * @return time in ticks for day/night cycle
	 */
	public int getRotationalPeriod(BlockPos pos);
	
	/**
	 * earth is 50
	 * @return wetness of a planet
	 */
	public int getWetness();
	
	/**
	 * Earth to moon is 100
	 * @return Distance to parent body, used in maps and fuel consumption calculations
	 */
	public int getOrbitalDistance(BlockPos pos);
	
	/**
	 * @return if the dimension is a planet vs spacecraft etc
	 */
	public boolean isPlanet();
	
	/**
	 * @return color of the sun in RGB
	 */
	public Vector3d getSunColor(BlockPos pos);
	
	/**
	 * @param pos location in block coords
	 * @return {@link DimensionProperties} of this dimension
	 */
	public IDimensionProperties getDimensionProperties(BlockPos pos);

	/**
	 * @param y height
	 * @return Absolute density of the atmosphere at the given height
	 */
	public float getAtmosphereDensityFromHeight(double y, BlockPos pos);

	/**
	 * 
	 * @param pos location in block coords
	 * @return Atmosphere type
	 */
	public IAtmosphere getAtmosphere(BlockPos pos);

}
