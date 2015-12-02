package zmaster587.advancedRocketry.api.dimension.solar;

import java.util.Iterator;
import java.util.List;

import zmaster587.advancedRocketry.api.satellite.SatelliteBase;

public class Planet {
	private int dimId;
	private StellarBody star;
	private List<Planet> moons;
	private List<SatelliteBase> satallites;
	private List<SatelliteBase> tickingSatallites;
	
	//Between 0 and 2pi
	private double orbit;
	
	public Planet(StellarBody star, int dimId) {
		this.star = star;
	}
	
	/**
	 * Adds a moon to this planet
	 * @param moon Planet type to add as the moon
	 */
	public void addMoon(Planet moon) {
		moons.add(moon);
	}
	
	/**
	 * Adds a satellite orbiting this body
	 * @param satallite
	 */
	public void addSatallite(SatelliteBase satallite) {
		satallites.add(satallite);
		
		if(satallite.canTick())
			tickingSatallites.add(satallite);
	}
	
	/**
	 * Removes a satellite orbiting this body
	 * @param satallite
	 * @return true if the satellite was removed, false if it doesn't exist
	 */
	public boolean removeSatallite(SatelliteBase satallite) {
		
		if(satallite.canTick())
			tickingSatallites.remove(satallite);
		
		return satallites.remove(satallite);
	}
	
	/**
	 * @return a list of satellites orbiting this body
	 */
	public List<SatelliteBase> getSatallites() {
		return satallites;
	}
	
	//TODO: multithreading
	/**
	 * If a satellite is registered to tick, then it is ticked in this method
	 */
	public void tick() {
		Iterator<SatelliteBase> iterator = tickingSatallites.iterator();
		
		while(iterator.hasNext()) {
			SatelliteBase satallite = iterator.next();
			satallite.tickEntity();
		}
	}
	
	/**
	 * @return The star the planet is orbiting
	 */
	public StellarBody getStar() {return star;}
	
	/**
	 * @return the registered Dimid for this planet dimension
	 */
	public int getDimensionId() {return dimId;}
	
	/**
	 * @return a list of planets registered as mooons orbiting this planet
	 */
	public List<Planet> getMoons() {
		return moons;
	}
}
