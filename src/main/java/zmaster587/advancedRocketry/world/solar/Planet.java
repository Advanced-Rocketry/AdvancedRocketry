package zmaster587.advancedRocketry.world.solar;

import java.util.List;

public class Planet {
	private int dimId;
	private StellarBody star;
	private List<Planet> moons;
	
	public Planet(StellarBody star, int dimId) {
		this.star = star;
	}
	
	public void addMoon(Planet moon) {
		moons.add(moon);
	}
	
	public StellarBody getStar() {return star;}
	
	public int getDimensionId() {return dimId;}
	
	public List<Planet> getMoons() {
		return moons;
	}
}
