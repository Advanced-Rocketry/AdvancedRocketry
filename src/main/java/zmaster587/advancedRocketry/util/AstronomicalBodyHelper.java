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
	public static double getMoonOrbitalPeriod(float orbitalDistance, float planetaryMass) {
		//The same a the function for planets, but since gravity is directly correlated with mass uses the gravity of the plant for mass
		return 48d*Math.pow((Math.pow((orbitalDistance/100d), 3)*Math.pow(Math.PI, 2))/(planetaryMass*10d), 0.5d);
	}
	public static double getOrbitalTheta(int orbitalDistance, float solarSize) {
		double orbitalPeriod = getOrbitalPeriod(orbitalDistance, solarSize);
		//Returns angle, relative to 0, of a planet at any given time
		return ((AdvancedRocketry.proxy.getWorldTimeUniversal(0) % (24000d*orbitalPeriod))/(24000d*orbitalPeriod))*(2d*Math.PI);
	}
	public static double getMoonOrbitalTheta(int orbitalDistance, float parentGravitationalMultiplier) {
		//Because the function is still in AU and solar mass, some correctional factors to convert to those units
		double orbitalPeriod = getMoonOrbitalPeriod(orbitalDistance * 0.0025f, parentGravitationalMultiplier * 0.000003f);
		//Returns angle, relative to 0, of a moon at any given time
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
		return (int)(averageWithoutAtmosphere * Math.max(1, (1.125d * Math.pow((atmPressure/100d), 0.25))));
	}
	public static double getStellarBrightness(StellarBody star, int orbitalDistance) {
		//Normal stars are 1.0 times this value, black holes with accretion discs emit less and so modify it
		float lightMultiplier = 1.0f;
		//Make all values ratios of Earth normal to get ratio compared to Earth
		float normalizedStarTemperature = star.getTemperature()/100f;
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
		//Returns ratio compared to a planet at 1 AU for Sol, because the other values in AR are normalized,
		//and this works fairly well for hooking into with other mod's solar panels & such
		return (lightMultiplier * (
				(Math.pow(star.getSize(), 2) * Math.pow(normalizedStarTemperature, 4))
						/Math.pow(planetaryOrbitalRadius, 2)));
	}
	public static double getPlanetaryLightLevelMultiplier(double stellarBrightnessMultiplier) {
		double log2Multiplier = (Math.log10(stellarBrightnessMultiplier)/Math.log10(2.0));
		//Returns the brightness visible to the eye, compared to the actual flux - this is a factor of ~1.5x for every 2x increase in luminosity
        //This is used for planetary light levels, as those would be eyesight based unlike the stellar brightness or similar
		return Math.pow(1.5, log2Multiplier);
	}
}
