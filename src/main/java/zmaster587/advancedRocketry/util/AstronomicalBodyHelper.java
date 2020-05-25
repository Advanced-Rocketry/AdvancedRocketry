package zmaster587.advancedRocketry.util;

import zmaster587.advancedRocketry.AdvancedRocketry;
import zmaster587.advancedRocketry.api.dimension.solar.StellarBody;

public class AstronomicalBodyHelper {
	public static float getBodySizeMultiplier(float orbitalDistance) {
		//Returns size multiplier relative to Earth standard (1AU = 100 Distance)
		return 100f/orbitalDistance;
	}
	public static double getOrbitalPeriod(int orbitalDistance, float solarSize) {
		//Gives output in MC Days, uses 40 for Orbital Mechanics G
		//One MC Year is 48 MC days (16 IRL Hours), one month is 8 MC Days
		return 48d*Math.pow((Math.pow((orbitalDistance/100d), 3)*Math.pow(Math.PI, 2))/(Math.pow(solarSize, 3)*10d), 0.5d);
	}
	public static double getOrbitalTheta(int orbitalDistance, float solarSize) {
		double orbitalPeriod = getOrbitalPeriod(orbitalDistance, solarSize);
		//Returns angle, relative to 0, of a planet at any given time
		return ((AdvancedRocketry.proxy.getWorldTimeUniversal(0) % (24000d*orbitalPeriod))/(24000d*orbitalPeriod))*(2d*Math.PI);
	}
	public static int getAverageTemperature(StellarBody star, int orbitalDistance, int atmPressure) {
		int starSurfaceTemperature = 58 * star.getTemperature();
		float starRadius = star.getSize()/215f;
		//Gives output in AU
		float planetaryOrbitalRadius = orbitalDistance/100f;
		//Albedo is 0.3f hardcoded because of inability to easily calculate
		double averageWithoutAtmosphere = starSurfaceTemperature * Math.pow(starRadius/(2* planetaryOrbitalRadius), 0.5) * Math.pow((1f-0.3f), 0.25);
		//Slightly kludgey solution that works out mostly for Venus and well for Earth, without being overly complex
		//Output is in Kelvin
		return (int)(averageWithoutAtmosphere * Math.max(1, (1.125d * Math.pow((atmPressure/100), 0.25))));
	}
	public static double getStellarBrightness(StellarBody star, int orbitalDistance) {
		//Normal stars are 1.0 times this value, black holes with accretion discs emit less and so modify it
		float lightMultiplier = 1.0f;
		//Make all values ratios of Earth normal to get ratio compared to Earth
		float normalizedStarTemperature = star.getTemperature()/100;
		float planetaryOrbitalRadius = orbitalDistance/100f;
		//Check to see if the star is a black hole
		boolean blackHole = star.isBlackHole();
		for(StellarBody star2 : star.getSubStars())
			if(!star2.isBlackHole()) {
				blackHole = false;
				break;
			}
		//There's no real easy way to get the light emitted by an accretion disc, so this substitutes
		if(blackHole)
			lightMultiplier  *=0.25;
		//Returns ratio compared to Earth, because it'll only matter light level <15 and so we want a multiplier due to how MC handles light
		return (lightMultiplier * ((Math.pow(star.getSize(), 2) * Math.pow(normalizedStarTemperature, 4))/Math.pow(planetaryOrbitalRadius, 2)));
	}
}
