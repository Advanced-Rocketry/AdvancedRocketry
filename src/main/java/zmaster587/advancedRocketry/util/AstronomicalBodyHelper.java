package zmaster587.advancedRocketry.util;

import zmaster587.advancedRocketry.AdvancedRocketry;

public class AstronomicalBodyHelper {

	public static float getBodySizeMultiplier(float orbitalDistance) {
		//Returns size multiplier relative to Earth standard (1AU = 100 Distance)
		return 100f/orbitalDistance;
	}
	public static double getOrbitalTheta(int orbitalDistance, float solarSize) {
		//Gives output in MC Days, uses 40 for Orbital Mechanics G
		//One MC Year is 48 MC days (16 IRL Hours), one month is 8 MC Days
		double orbitalPeriod = 48D*Math.pow((Math.pow(orbitalDistance, 3)*Math.pow(Math.PI, 2)*10D)/solarSize, 0.5D);
		//Returns angle, relative to 0, of a planet at any given time
		return ((AdvancedRocketry.proxy.getWorldTimeUniversal(0) % 24000D*orbitalPeriod)/24000D*orbitalPeriod)*(2D*Math.PI);
	}
} 