package zmaster587.advancedRocketry.api;

import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import zmaster587.advancedRocketry.api.dimension.IDimensionProperties;
import zmaster587.advancedRocketry.dimension.DimensionProperties;

public interface IPlanetaryProvider {

    /**
     * 100 is earth-like, 0 is none
     *
     * @return multiplier of gravity
     */
    double getGravitationalMultiplier(BlockPos pos);

    /**
     * @return the dimension ID this one is in orbit around
     */
    int getOrbitingDimension(BlockPos pos);

    /**
     * @return array of dimension IDs orbiting this one
     */
    int[] getDimensionsInOrbit(BlockPos pos);

    /**
     * Earth is 100 (0 if no atmosphere)
     *
     * @return Density of atmosphere
     */
    float getAtmosphereDensity(BlockPos pos);

    /**
     * @return Temperature of the planet
     */
    int getAverageTemperature(BlockPos pos);

    /**
     * @return time in ticks for day/night cycle
     */
    int getRotationalPeriod(BlockPos pos);

    /**
     * earth is 50
     *
     * @return wetness of a planet
     */
    int getWetness();

    /**
     * Earth to moon is 100
     *
     * @return Distance to parent body, used in maps and fuel consumption calculations
     */
    int getOrbitalDistance(BlockPos pos);

    /**
     * @return if the dimension is a planet vs spacecraft etc
     */
    boolean isPlanet();

    /**
     * @return color of the sun in RGB
     */
    Vec3d getSunColor(BlockPos pos);

    /**
     * @param pos location in block coords
     * @return {@link DimensionProperties} of this dimension
     */
    IDimensionProperties getDimensionProperties(BlockPos pos);

    /**
     * @param y height
     * @return Absolute density of the atmosphere at the given height
     */
    float getAtmosphereDensityFromHeight(double y, BlockPos pos);

    /**
     * @param pos location in block coords
     * @return Atmosphere type
     */
    IAtmosphere getAtmosphere(BlockPos pos);

}
