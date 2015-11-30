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
	
	public void addMoon(Planet moon) {
		moons.add(moon);
	}
	
	public void addSatallite(SatelliteBase satallite) {
		satallites.add(satallite);
		
		if(satallite.canTick())
			tickingSatallites.add(satallite);
	}
	
	public boolean removeSatallite(SatelliteBase satallite) {
		
		if(satallite.canTick())
			tickingSatallites.remove(satallite);
		
		return satallites.remove(satallite);
	}
	
	public List<SatelliteBase> getSatallites() {
		return satallites;
	}
	
	//TODO: multithreading
	public void tick() {
		Iterator<SatelliteBase> iterator = tickingSatallites.iterator();
		
		while(iterator.hasNext()) {
			SatelliteBase satallite = iterator.next();
			satallite.tickEntity();
		}
	}
	
	public StellarBody getStar() {return star;}
	
	public int getDimensionId() {return dimId;}
	
	public List<Planet> getMoons() {
		return moons;
	}
}
