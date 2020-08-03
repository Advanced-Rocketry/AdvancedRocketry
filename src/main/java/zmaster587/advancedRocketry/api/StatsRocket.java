package zmaster587.advancedRocketry.api;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagFloat;
import net.minecraft.nbt.NBTTagInt;
import zmaster587.advancedRocketry.api.fuel.FuelRegistry;
import zmaster587.advancedRocketry.api.fuel.FuelRegistry.FuelType;
import zmaster587.libVulpes.util.HashedBlockPosition;
import zmaster587.libVulpes.util.Vector3F;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class StatsRocket {

	private int thrust;
	private int weight;
	private float drillingPower;

	private int fuelLiquid;
	private int fuelNuclear;
	private int fuelIon;
	private int fuelWarp;
	private int fuelImpulse;

	//Used for orbital height calculations
	public boolean lowOrbitOnly;
	public boolean lowOrbitAndTBI;
	public boolean stationClearanceAndTBI;
	public boolean stationClearanceOnly;

	private int fuelCapacityLiquid;
	private int fuelCapacityNuclear;
	private int fuelCapacityIon;
	private int fuelCapacityWarp;
	private int fuelCapacityImpulse;

	private int fuelRateLiquid;
	private int fuelRateNuclear;
	private int fuelRateIon;
	private int fuelRateWarp;
	private int fuelRateImpulse;

	HashedBlockPosition pilotSeatPos;
	private final List<HashedBlockPosition> passengerSeats = new ArrayList<HashedBlockPosition>();
	private List<Vector3F<Float>> engineLoc;

	private static final String TAGNAME = "rocketStats";
	private HashMap<String, Object> statTags;
	
	private static final int INVALID_SEAT = Integer.MIN_VALUE;

	public StatsRocket() {
		thrust = 0;
		weight = 0;
		fuelLiquid = 0;
		drillingPower = 0f;
		lowOrbitOnly = false;
		lowOrbitAndTBI = false;
		stationClearanceAndTBI = false;
		stationClearanceOnly = false;
		pilotSeatPos = new HashedBlockPosition(0,0,0);
		pilotSeatPos.x = INVALID_SEAT;
		engineLoc = new ArrayList<Vector3F<Float>>();
		statTags = new HashMap<String, Object>();
	}

	/*public StatsRocket(int thrust, int weight, int fuelRate, int fuel) {
		this.thrust = thrust;
		this.weight = weight;
		this.fuelLiquid = fuel;
		lastSeatX = -1;
		engineLoc = new ArrayList<Vector3F>();
	}*/



	public int getSeatX() { return pilotSeatPos.x; }
	public int getSeatY() { return pilotSeatPos.y; }
	public int getSeatZ() { return pilotSeatPos.z; }

	public HashedBlockPosition getPassengerSeat(int index) {
		return passengerSeats.get(index);
	}

	public int getNumPassengerSeats() {
		return passengerSeats.size();
	}

	public int getThrust() {return (int) (thrust*ARConfiguration.getCurrentConfig().rocketThrustMultiplier);}
	public int getWeight() {return weight;}
	public float getDrillingPower() {return drillingPower;}
	public void setDrillingPower(float power) {drillingPower = power;}
	public float getAcceleration() { return (getThrust() - weight)/10000f; }
	public List<Vector3F<Float>> getEngineLocations() { return engineLoc; }

	public void setThrust(int thrust) { this.thrust = thrust; }
	public void setWeight(int weight) { this.weight = weight; }

	public void setSeatLocation(int x, int y, int z) {
		pilotSeatPos.x = x;
		pilotSeatPos.y = (short)y;
		pilotSeatPos.z = z;
	}

	public void addPassengerSeat(int x, int y, int z) {
		if(!hasSeat())
			setSeatLocation(x, y, z);
		passengerSeats.add(new HashedBlockPosition(x, y, z));
	}

	/**
	 * Adds an engine location to the given coordinates
	 * the engine location is only currently used to track the location for spawning particle effects
	 * @param x
	 * @param y
	 * @param z
	 */
	public void addEngineLocation(float x, float y, float z) {
		//We want to be in the center of the block
		engineLoc.add(new Vector3F<Float>(x , y, z));
	}

	/**
	 * Removes all engine locations
	 */
	public void clearEngineLocations() {
		engineLoc.clear();
	}

	/**
	 * @return a duplicate of the rocket stats
	 */
	public StatsRocket copy() {
		StatsRocket stat = new StatsRocket();

		stat.thrust = this.thrust;
		stat.weight = this.weight;
		stat.drillingPower = this.drillingPower;

		for(FuelType type : FuelType.values()) {
			stat.setFuelAmount(type, this.getFuelAmount(type));
			stat.setFuelRate(type, this.getFuelRate(type));
			stat.setFuelCapacity(type, this.getFuelCapacity(type));
		}

		stat.pilotSeatPos = new HashedBlockPosition(this.pilotSeatPos.x, this.pilotSeatPos.y, this.pilotSeatPos.z);
		stat.passengerSeats.addAll(passengerSeats);
		stat.engineLoc = new ArrayList<Vector3F<Float>>(engineLoc);
		stat.statTags = new HashMap<String, Object>(statTags);
		return stat;
	}

	/**
	 * 
	 * @param type type of fuel to check
	 * @return the amount of fuel of the type currently contained in the stat
	 */
	public int getFuelAmount(FuelRegistry.FuelType type) {
		switch(type) {
		case WARP:
			return fuelWarp;
		case IMPULSE:
			return fuelImpulse;
		case ION:
			return fuelIon;
		case LIQUID:
			return fuelLiquid;
		case NUCLEAR:
			return fuelNuclear;
		}
		return 0;
	}

	/**
	 * 
	 * @param type
	 * @return the largest amount of fuel of the type that can be stored in the stat
	 */
	public int getFuelCapacity(FuelRegistry.FuelType type) {
		switch(type) {
		case WARP:
			return fuelCapacityWarp;
		case IMPULSE:
			return fuelCapacityImpulse;
		case ION:
			return fuelCapacityIon;
		case LIQUID:
			return fuelCapacityLiquid;
		case NUCLEAR:
			return fuelCapacityNuclear;
		}
		return 0;
	}

	/**
	 * @param type
	 * @return the consumption rate of the fuel per tick
	 */
	public int getFuelRate(FuelRegistry.FuelType type) {

		if(!ARConfiguration.getCurrentConfig().rocketRequireFuel)
			return 0;

		switch(type) {
		case WARP:
			return fuelRateWarp;
		case IMPULSE:
			return fuelRateImpulse;
		case ION:
			return fuelRateIon;
		case LIQUID:
			return fuelRateLiquid;
		case NUCLEAR:
			return fuelRateNuclear;
		}
		return 0;
	}

	/**
	 * Sets the amount of a given fuel type in the stat
	 * @param type
	 * @param amt
	 */
	public void setFuelAmount(FuelRegistry.FuelType type, int amt) {
		switch(type) {
		case WARP:
			fuelWarp = amt;
			break;
		case IMPULSE:
			fuelImpulse = amt;
			break;
		case ION:
			fuelIon = amt;
			break;
		case LIQUID:
			fuelLiquid = amt;
			break;
		case NUCLEAR:
			fuelNuclear = amt;
		}
	}

	/**
	 * Sets the fuel consumption rate per tick of the stat
	 * @param type
	 * @param amt
	 */
	public void setFuelRate(FuelRegistry.FuelType type, int amt) {
		switch(type) {
		case WARP:
			fuelRateWarp = amt;
			break;
		case IMPULSE:
			fuelRateImpulse = amt;
			break;
		case ION:
			fuelRateIon = amt;
			break;
		case LIQUID:
			fuelRateLiquid = amt;
			break;
		case NUCLEAR:
			fuelRateNuclear = amt;
		}
	}

	/**
	 * Sets the fuel capacity of the fuel type in this stat
	 * @param type
	 * @param amt
	 */
	public void setFuelCapacity(FuelRegistry.FuelType type, int amt) {
		switch(type) {
		case WARP:
			fuelCapacityWarp = amt;
			break;
		case IMPULSE:
			fuelCapacityImpulse = amt;
			break;
		case ION:
			fuelCapacityIon = amt;
			break;
		case LIQUID:
			fuelCapacityLiquid = amt;
			break;
		case NUCLEAR:
			fuelCapacityNuclear = amt;
		}
	}

	/**
	 * 
	 * @param type type of fuel
	 * @param amt amount of fuel to add
	 * @return amount of fuel added
	 */
	public int addFuelAmount(FuelRegistry.FuelType type, int amt) {
		//TODO: finish other ones
		switch(type) {
		case WARP:
			fuelWarp += amt;
			return fuelWarp;
		case IMPULSE:
			fuelImpulse += amt;
			return fuelImpulse;
		case ION:
			fuelIon += amt;
			return fuelIon;
		case LIQUID:
			int maxAdd = fuelCapacityLiquid - fuelLiquid;
			int amountToAdd = Math.min(amt, maxAdd);
			fuelLiquid += amountToAdd;
			return amountToAdd;
		case NUCLEAR:
			fuelNuclear += amt;
			return fuelNuclear;
		}
		return 0;
	}

	/**
	 * @return true if a seat exists on this stat
	 */
	public boolean hasSeat() {
		return pilotSeatPos.x != INVALID_SEAT;
	}

	/**
	 * resets all values to default
	 */
	public void reset() {
		thrust = 0;
		weight = 0;
		drillingPower = 0f;

		for(FuelType type : FuelType.values()) {
			setFuelAmount(type, 0);
			setFuelRate(type, 0);
			setFuelCapacity(type, 0);
		}

		fuelLiquid = 0;
		pilotSeatPos.x = INVALID_SEAT;
		clearEngineLocations();
		passengerSeats.clear();
		statTags.clear();
	}

	public void setStatTag(String str, float value) {
		statTags.put(str, new Float(value));
	}

	public void setStatTag(String str, int value) {
		statTags.put(str, new Integer(value));
	}

	/**
	 * 
	 * @param str name of the tag to get
	 * @return the value of the tag as float or int, or 0 if tag does not exist
	 */
	public Object getStatTag(String str) {
		Object obj = statTags.get(str);
		return obj == null ? 0 : obj;
	}

	public static StatsRocket createFromNBT(NBTTagCompound nbt) { 
		if(nbt.hasKey(TAGNAME)) {
			NBTTagCompound stats = nbt.getCompoundTag(TAGNAME);
			StatsRocket statsRocket = new StatsRocket();
			statsRocket.readFromNBT(stats);
			return statsRocket;
		}

		return new StatsRocket();
	}

	public void writeToNBT(NBTTagCompound nbt) {
		NBTTagCompound stats = new NBTTagCompound();

		stats.setInteger("thrust", this.thrust);
		stats.setInteger("weight", this.weight);
		stats.setFloat("drillingPower", this.drillingPower);

		stats.setInteger("fuelLiquid", this.fuelLiquid);
		stats.setInteger("fuelImpulse", this.fuelImpulse);
		stats.setInteger("fuelIon", this.fuelIon);
		stats.setInteger("fuelNuclear", this.fuelNuclear);
		stats.setInteger("fuelWarp", this.fuelWarp);

		stats.setInteger("fuelCapacityLiquid", this.fuelCapacityLiquid);
		stats.setInteger("fuelCapacityImpulse", this.fuelCapacityImpulse);
		stats.setInteger("fuelCapacityIon", this.fuelCapacityIon);
		stats.setInteger("fuelCapacityNuclear", this.fuelCapacityNuclear);
		stats.setInteger("fuelCapacityWarp", this.fuelCapacityWarp);

		stats.setInteger("fuelRateLiquid", this.fuelRateLiquid);
		stats.setInteger("fuelRateImpulse", this.fuelRateImpulse);
		stats.setInteger("fuelRateIon", this.fuelRateIon);
		stats.setInteger("fuelRateNuclear", this.fuelRateNuclear);
		stats.setInteger("fuelRateWarp", this.fuelRateWarp);

		NBTTagCompound dynStats = new NBTTagCompound();
		for(String key : statTags.keySet()) {
			Object obj = statTags.get(key);

			if(obj instanceof Float)
				dynStats.setFloat(key, (float)obj);
			else if(obj instanceof Integer)
				dynStats.setInteger(key, (int)obj);
		}
		if(!dynStats.hasNoTags())
			stats.setTag("dynStats", dynStats);

		stats.setInteger("playerXPos", pilotSeatPos.x);
		stats.setInteger("playerYPos", pilotSeatPos.y);
		stats.setInteger("playerZPos", pilotSeatPos.z);

		if(!engineLoc.isEmpty()) {
			int locs[] = new int[engineLoc.size()*3];

			for(int i=0 ; (i/3) < engineLoc.size(); i+=3) {
				Vector3F<Float> vec = engineLoc.get(i/3);
				locs[i] = vec.x.intValue();
				locs[i + 1] = vec.y.intValue();
				locs[i + 2] = vec.z.intValue();
			}
			stats.setIntArray("engineLoc", locs);
		}

		if(!passengerSeats.isEmpty()) {
			int locs[] = new int[passengerSeats.size()*3];

			for(int i=0 ; (i/3) < passengerSeats.size(); i+=3) {
				HashedBlockPosition vec = passengerSeats.get(i/3);
				locs[i] = vec.x;
				locs[i + 1] = vec.y;
				locs[i + 2] = vec.z;

			}
			stats.setIntArray("passengerSeats", locs);
		}

		nbt.setTag(TAGNAME, stats);
	}

	public void readFromNBT(NBTTagCompound nbt) {

		if(nbt.hasKey(TAGNAME)) {
			NBTTagCompound stats = nbt.getCompoundTag(TAGNAME);
			this.thrust = stats.getInteger("thrust");
			this.weight = stats.getInteger("weight");
			this.drillingPower = stats.getFloat("drillingPower");

			this.fuelLiquid = stats.getInteger("fuelLiquid");
			this.fuelImpulse = stats.getInteger("fuelImpulse");
			this.fuelIon = stats.getInteger("fuelIon");
			this.fuelNuclear = stats.getInteger("fuelNuclear");
			this.fuelWarp = stats.getInteger("fuelWarp");

			this.fuelCapacityLiquid = stats.getInteger("fuelCapacityLiquid");
			this.fuelCapacityImpulse = stats.getInteger("fuelCapacityImpulse");
			this.fuelCapacityIon = stats.getInteger("fuelCapacityIon");
			this.fuelCapacityNuclear = stats.getInteger("fuelCapacityNuclear");
			this.fuelCapacityWarp = stats.getInteger("fuelCapacityWarp");

			this.fuelRateLiquid = stats.getInteger("fuelRateLiquid");
			this.fuelRateImpulse = stats.getInteger("fuelRateImpulse");
			this.fuelRateIon = stats.getInteger("fuelRateIon");
			this.fuelRateNuclear = stats.getInteger("fuelRateNuclear");
			this.fuelRateWarp = stats.getInteger("fuelRateWarp");

			if(stats.hasKey("dynStats")) {
				NBTTagCompound dynStats = stats.getCompoundTag("dynStats");


				for(Object key : dynStats.getKeySet()) {
					Object obj = dynStats.getTag((String)key);

					if(obj instanceof NBTTagFloat)
						setStatTag((String)key, dynStats.getFloat((String)key));
					else if(obj instanceof NBTTagInt)
						setStatTag((String)key, dynStats.getInteger((String)key));
				}
			}

			pilotSeatPos.x = stats.getInteger("playerXPos");
			pilotSeatPos.y = (short)stats.getInteger("playerYPos");
			pilotSeatPos.z = stats.getInteger("playerZPos");

			if(stats.hasKey("engineLoc")) {
				int locations[] = stats.getIntArray("engineLoc");

				for(int i=0 ; i < locations.length; i+=3) {

					this.addEngineLocation(locations[i], locations[i+1], locations[i+2]);
				}
			}

			if(stats.hasKey("passengerSeats")) {
				int locations[] = stats.getIntArray("passengerSeats");

				for(int i=0 ; i < locations.length; i+=3) {

					this.addPassengerSeat(locations[i], locations[i+1], locations[i+2]);
				}
			}
		}
	}
}