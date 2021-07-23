package zmaster587.advancedRocketry.util;

import net.minecraft.util.ResourceLocation;
import zmaster587.advancedRocketry.api.ARConfiguration;
import zmaster587.advancedRocketry.api.dimension.IDimensionProperties;
import zmaster587.advancedRocketry.dimension.DimensionManager;
import zmaster587.advancedRocketry.stations.SpaceStationObject;

public class PlanetaryTravelHelper {
	/**
	 * @param sourceDimensionID the dimension ID of the current planet
	 * @param destDimID the dimension ID of the destination planet
	 * @return boolean on whether the travel is between two bodies in a planetary systen
	 */
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
					break;
				}
			}
		} else {
			for (ResourceLocation moonDimID : launchworldProperties.getChildPlanets()) {
				if (sourceDimensionID.equals(moonDimID)) {
					isPlanetMoonSystem = true;
					break;
				}
			}
		}

		return isPlanetMoonSystem;
	}

	/**
	 * @param sourceDimensionID the dimension ID of the current planet
	 * @param destDimID the dimension ID of the destination planet
	 * @param toAsteroids whether the mission is to an asteroid
	 * @return integer for the number of extra blocks the rocket will have to burn for to complete its injection burn when orbit height has been reached
	 */
	public static int getTransbodyInjectionBurn(ResourceLocation sourceDimensionID, ResourceLocation destDimID, boolean toAsteroids) {
		int baseInjectionHeight = ARConfiguration.getCurrentConfig().transBodyInjection.get();
		//This is probably one of the worst ways to do this and I don't really care about realism, just tapering results.... if this turns out to be realistic well then, that's nice.
		//Not like the mod has an semblance of a concept of orbital mechanics anyway :P
		//This is vaugely a multiplier based on TLI burns, burning for 2x as long can get you 4x as far
		//This grabs the body distance multipier, then takes the square root of it, or if warp multiplies by the config option for that
		return (isTravelWithinPlanetarySystem(destDimID, sourceDimensionID)) ? (int) (baseInjectionHeight * Math.pow(getBodyDistanceMultiplier(destDimID, sourceDimensionID, toAsteroids), 0.5d)) : (int) (ARConfiguration.getCurrentConfig().warpTBIBurnMult.get() * baseInjectionHeight);
	}
			/**
			 * @param sourceDimensionID the dimension ID of the current planet
			 * @param destDimID the dimension ID of the destination planet
			 * @param toAsteroids whether the mission is to an asteroid
			 * @return double for the burn length needed to reach this particular destination
			 */
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

		/**
		 * @param sourceDimensionID the dimension ID of the current planet
		 * @param destDimID the dimension ID of the destination planet
		 * @return boolean for whether this is anywhere within the planetary system, not just between bodies
		 */
	public static boolean isTravelAnywhereInPlanetarySystem(ResourceLocation destDimID, ResourceLocation sourceDimensionID) {
		return isTravelWithinOrbit(destDimID, sourceDimensionID) || isTravelWithinPlanetarySystem(destDimID, sourceDimensionID);
	}

		/**
		 * @param sourceDimensionID the dimension ID of the current planet
		 * @param destDimID the dimension ID of the destination planet
		 * @return boolean for whether the destination is anywhere in the orbit of the current body, but not to another body
		 */
	public static boolean isTravelWithinOrbit(ResourceLocation destDimID, ResourceLocation sourceDimensionID) {

		return (destDimID.equals(sourceDimensionID));
	}

	/**
	 * @param spaceStation the space station within this pairing
	 * @param planetID the dimension ID of the planet we are either launching from or going to
	 * @return boolean for whether this trip is soley within geostationary orbit to/from the ground
	 */
	public static boolean isTravelWithinGeostationaryOrbit(SpaceStationObject spaceStation, ResourceLocation planetID) {
		//Returns true if the planet and the dimension (can be any!) are the same parent and if station is 35500 < x < 36300 km
		return spaceStation.getOrbitingPlanetId().equals(planetID) && (spaceStation.getOrbitalDistance() >= 177.0f && 181.0f >= spaceStation.getOrbitalDistance());
	}
}
