package zmaster587.advancedRocketry.api.dimension.solar;

import zmaster587.advancedRocketry.api.satellite.SatelliteBase;

import java.util.Iterator;
import java.util.List;

public class Planet {
	private int dimId;
	private StellarBody star;
	private List<Planet> moons;
	private List<SatelliteBase> satellites;
	private List<SatelliteBase> tickingSatellites;
	
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
	 * @param satellite
	 */
	public void addSatellite(SatelliteBase satellite) {
		satellites.add(satellite);
		
		if(satellite.canTick())
			tickingSatellites.add(satellite);
	}
	
	/**
	 * Removes a satellite orbiting this body
	 * @param satellite
	 * @return true if the satellite was removed, false if it doesn't exist
	 */
	public boolean removeSatellite(SatelliteBase satellite) {
		
		if(satellite.canTick())
			tickingSatellites.remove(satellite);
		
		return satellites.remove(satellite);
	}
	
	/**
	 * @return a list of satellites orbiting this body
	 */
	public List<SatelliteBase> getSatellites() {
		return satellites;
	}
	
	//TODO: multithreading
	/**
	 * If a satellite is registered to tick, then it is ticked in this method
	 */
	public void tick() {
		Iterator<SatelliteBase> iterator = tickingSatellites.iterator();
		
		while(iterator.hasNext()) {
			SatelliteBase satellite = iterator.next();
			satellite.tickEntity();
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
