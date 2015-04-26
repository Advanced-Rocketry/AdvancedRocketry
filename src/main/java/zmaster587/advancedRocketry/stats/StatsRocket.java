package zmaster587.advancedRocketry.stats;

import net.minecraft.nbt.NBTTagCompound;

public class StatsRocket {
	
	private int thrust;
	private int fuelRate;
	private int weight;
	private int fuel;
	
	private static final String TAGNAME = "rocketStats";
	
	public StatsRocket() {
		thrust = 0;
		fuelRate = 0;
		weight = 0;
		fuel = 0;
	}
	
	public StatsRocket(int thrust, int weight, int fuelRate, int fuel) {
		this.thrust = thrust;
		this.weight = weight;
		this.fuelRate = fuelRate;
		this.fuel = fuel;
	}
	
	public int getThrust() {return thrust;}
	public int getFuelRate() {return fuelRate;}
	public int getWeight() {return weight;}
	public int getFuel() {return fuel;}
	
	public void setThrust(int thrust) { this.thrust = thrust; }
	public void setFuelRate(int fuel) { this.fuelRate = fuel; }
	public void setWeight(int weight) { this.weight = weight; }
	public void setFuel(int fuel) {this.fuel = fuel;}
	
	
	public static StatsRocket createFromNBT(NBTTagCompound nbt) { 
		if(nbt.hasKey(TAGNAME)) {
			NBTTagCompound stats = nbt.getCompoundTag(TAGNAME);
			return new StatsRocket(stats.getInteger("thrust"), stats.getInteger("weight"), stats.getInteger("fuelRate"),stats.getInteger("fuel"));
		}
		
		return new StatsRocket();
	}
	
	public void writeToNBT(NBTTagCompound nbt) {
		NBTTagCompound stats = new NBTTagCompound();
		
		stats.setInteger("thrust", this.thrust);
		stats.setInteger("fuelRate", this.fuelRate);
		stats.setInteger("weight", this.weight);
		stats.setInteger("fuel", this.fuel);
		
		nbt.setTag(TAGNAME, stats);
	}
	
	public void readFromNBT(NBTTagCompound nbt) {
		
		if(nbt.hasKey(TAGNAME)) {
			NBTTagCompound stats = nbt.getCompoundTag(TAGNAME);
			this.fuelRate = stats.getInteger("fuelRate");
			this.thrust = stats.getInteger("thrust");
			this.weight = stats.getInteger("weight");
			this.fuel = stats.getInteger("fuel");
		}
		
	}
}
