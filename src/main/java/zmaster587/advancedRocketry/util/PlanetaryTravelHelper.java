package zmaster587.advancedRocketry.util;

import zmaster587.advancedRocketry.api.ARConfiguration;
import zmaster587.advancedRocketry.api.dimension.IDimensionProperties;
import zmaster587.advancedRocketry.api.stations.ISpaceObject;
import zmaster587.advancedRocketry.dimension.DimensionManager;
import zmaster587.advancedRocketry.stations.SpaceObjectBase;
import zmaster587.advancedRocketry.stations.SpaceStationObject;

public class PlanetaryTravelHelper {
	public static boolean isTravelWithinPlanetarySystem(int currentDimensionID, int destinationDimensionID) {
		boolean isPlanetMoonSystem = false;
		IDimensionProperties launchworldProperties = DimensionManager.getInstance().getDimensionProperties(currentDimensionID);

		//If it's a moon, we need to check moon -> planet and moon -> moon
		//Otherwise, we need to check planet -> moon
		//Failing any of those means it's not within the bodies of the planetary system
		if (launchworldProperties.isMoon()) {
			isPlanetMoonSystem = (destinationDimensionID == launchworldProperties.getParentPlanet());
			for (int moonDimID : launchworldProperties.getParentProperties().getChildPlanets()) {
				if (destinationDimensionID == moonDimID) {
					isPlanetMoonSystem = true;
				}
			}
		} else {
			for (int moonDimID : launchworldProperties.getChildPlanets()) {
				if (destinationDimensionID == moonDimID) {
					isPlanetMoonSystem = true;
				}
			}
		}

		return isPlanetMoonSystem;
	}
	public static int getTransbodyInjectionBurn(int currentDimensionID, int destinationDimensionID, boolean toAsteroids) {
		int baseInjectionHeight = ARConfiguration.getCurrentConfig().transBodyInjection;
		//This is probably one of the worst ways to do this and I don't really care about realism, just tapering results.... if this turns out to be realistic well then, that's nice.
		//Not like the mod has an semblance of a concept of orbital mechanics anyway :P
		//This is vaugely a multiplier based on TLI burns, burning for 2x as long can get you 4x as far
		//This grabs the body distance multipier, then takes the square root of it, or if warp multiplies by the config option for that
		return (isTravelWithinPlanetarySystem(currentDimensionID, destinationDimensionID)) ? (int) (baseInjectionHeight * Math.pow(getBodyDistanceMultiplier(currentDimensionID, destinationDimensionID, toAsteroids), 0.5d)) : (int) ARConfiguration.getCurrentConfig().warpTBIBurnMult * baseInjectionHeight;
	}
	public static double getBodyDistanceMultiplier(int currentDimensionID, int destinationDimensionID, boolean toAsteroids) {
		//Check the orbital distance of the moon or planet we're going to
		//This gives us a ratio of how far it is compared to the default of 100
		double bodyDistanceMultiplier = 1.0d;
		IDimensionProperties destinationProperties = DimensionManager.getInstance().getDimensionProperties(destinationDimensionID);
		if (destinationProperties.isMoon()) {
			bodyDistanceMultiplier = destinationProperties.getOrbitalDist()/100d;
		} else {
			for (int moonDimID : destinationProperties.getChildPlanets()) {
				if (currentDimensionID == moonDimID) {
					bodyDistanceMultiplier = DimensionManager.getInstance().getDimensionProperties(moonDimID).getOrbitalDist()/100d;
				}
			}
		}
		//If it's asteroids, check the config for the multiplier there
		if (toAsteroids) {
			bodyDistanceMultiplier = ARConfiguration.getCurrentConfig().asteroidTBIBurnMult;
		}
		return bodyDistanceMultiplier;
	}
	public static boolean isTravelWithinOrbit(int currentDimensionID, int destinationDimensionID) {
		return (currentDimensionID == destinationDimensionID);
	}

	public static boolean isTravelWithinGeostationaryOrbit(SpaceStationObject spaceStation, int planetID) {
		//Returns true if the planet and the dimension (can be any!) are the same parent and if station is 36300 > x > 35500 km
		return spaceStation.getOrbitingPlanetId() == planetID && (spaceStation.getOrbitalDistance() >= 177.0f && 181.0f >= spaceStation.getOrbitalDistance());
	}
}
