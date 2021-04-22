package zmaster587.advancedRocketry.util;

import net.minecraft.util.ResourceLocation;
import zmaster587.advancedRocketry.api.ARConfiguration;
import zmaster587.advancedRocketry.api.dimension.IDimensionProperties;
import zmaster587.advancedRocketry.dimension.DimensionManager;
import zmaster587.advancedRocketry.stations.SpaceStationObject;

public class PlanetaryTravelHelper {
	public static boolean isTravelWithinPlanetarySystem(ResourceLocation destDimID, ResourceLocation sourceDimensionID) {

		boolean isPlanetMoonSystem = false;
		IDimensionProperties launchworldProperties = DimensionManager.getInstance().getDimensionProperties(destDimID);

		//If it's a moon, we need to check moon -> planet and moon -> moon
		//Otherwise, we need to check planet -> moon
		//Failing any of those means it's not within the bodies of the planetary system
		if (launchworldProperties.isMoon()) {
			isPlanetMoonSystem = (sourceDimensionID.equals(launchworldProperties.getParentPlanet()));
			for (ResourceLocation moonDimID : launchworldProperties.getParentProperties().getChildPlanets()) {
				if (sourceDimensionID.equals(moonDimID)) {
					isPlanetMoonSystem = true;
				}
			}
		} else {
			for (ResourceLocation moonDimID : launchworldProperties.getChildPlanets()) {
				if (sourceDimensionID.equals(moonDimID)) {
					isPlanetMoonSystem = true;
				}
			}
		}

		return isPlanetMoonSystem;
	}
	public static int getTransbodyInjectionBurn(ResourceLocation destDimID, ResourceLocation sourceDimensionID, boolean toAsteroids) {
		int baseInjectionHeight = ARConfiguration.getCurrentConfig().transBodyInjection.get();
		//This is probably one of the worst ways to do this and I don't really care about realism, just tapering results.... if this turns out to be realistic well then, that's nice.
		//Not like the mod has an semblance of a concept of orbital mechanics anyway :P
		//This is vaugely a multiplier based on TLI burns, burning for 2x as long can get you 4x as far
		//This grabs the body distance multipier, then takes the square root of it, or if warp multiplies by the config option for that
		return (isTravelWithinPlanetarySystem(destDimID, sourceDimensionID)) ? (int) (baseInjectionHeight * Math.pow(getBodyDistanceMultiplier(destDimID, sourceDimensionID, toAsteroids), 0.5d)) : (int) (ARConfiguration.getCurrentConfig().warpTBIBurnMult.get() * baseInjectionHeight);
	}
	public static double getBodyDistanceMultiplier(ResourceLocation destDimID, ResourceLocation sourceDimensionID, boolean toAsteroids) {
		//Check the orbital distance of the moon or planet we're going to
		//This gives us a ratio of how far it is compared to the default of 100
		double bodyDistanceMultiplier = 1.0d;
		IDimensionProperties destinationProperties = DimensionManager.getInstance().getDimensionProperties(sourceDimensionID);
		if (destinationProperties.isMoon()) {
			bodyDistanceMultiplier = destinationProperties.getOrbitalDist()/100d;
		} else {
			for (ResourceLocation moonDimID : destinationProperties.getChildPlanets()) {
				if (destDimID.equals(moonDimID)) {
					bodyDistanceMultiplier = DimensionManager.getInstance().getDimensionProperties(moonDimID).getOrbitalDist()/100d;
				}
			}
		}
		//If it's asteroids, check the config for the multiplier there
		if (toAsteroids) {
			bodyDistanceMultiplier = ARConfiguration.getCurrentConfig().asteroidTBIBurnMult.get();
		}
		return bodyDistanceMultiplier;
	}
	
	public static boolean isTravelAnywhereInPlanetarySystem(ResourceLocation destDimID, ResourceLocation sourceDimensionID) {
		return isTravelWithinOrbit(destDimID, sourceDimensionID) || isTravelWithinPlanetarySystem(destDimID, sourceDimensionID);
	}
	
	public static boolean isTravelWithinOrbit(ResourceLocation destDimID, ResourceLocation sourceDimensionID) {

		return (destDimID.equals(sourceDimensionID));
	}

	public static boolean isTravelWithinGeostationaryOrbit(SpaceStationObject spaceStation, ResourceLocation planetID) {
		//Returns true if the planet and the dimension (can be any!) are the same parent and if station is 35500 < x < 36300 km
		return spaceStation.getOrbitingPlanetId().equals(planetID) && (spaceStation.getOrbitalDistance() >= 177.0f && 181.0f >= spaceStation.getOrbitalDistance());
	}
}
