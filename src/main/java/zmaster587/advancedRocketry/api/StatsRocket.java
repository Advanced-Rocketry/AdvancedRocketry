package zmaster587.advancedRocketry.api;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.FloatNBT;
import net.minecraft.nbt.IntNBT;
import net.minecraft.util.ResourceLocation;
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
	private ResourceLocation fuelFluid;
	private ResourceLocation oxidizerFluid;

	//Used for orbital height calculations
	public int orbitHeight;
	public float injectionBurnLenghtMult;

	private int fuelMonopropellant;
	private int fuelNuclear;
	private int fuelBipropellant;
	private int fuelOxidizer;
	private int fuelIon;
	private int fuelWarp;
	private int fuelImpulse;

	private int fuelCapacityMonopropellant;
	private int fuelCapacityBipropellant;
	private int fuelCapacityOxidizer;

	private int fuelCapacityNuclear;
	private int fuelCapacityIon;
	private int fuelCapacityWarp;
	private int fuelCapacityImpulse;

	private int fuelRateMonopropellant;
	private int fuelRateBipropellant;
	private int fuelRateOxidizer;
	private int fuelRateNuclear;
	private int fuelRateIon;
	private int fuelRateWarp;
	private int fuelRateImpulse;

	private float fuelBaseRateMonopropellant;
	private float fuelBaseRateBipropellant;
	private float fuelBaseRateOxidizer;
	private float fuelBaseRateNuclear;
	private float fuelBaseRateIon;
	private float fuelBaseRateWarp;
	private float fuelBaseRateImpulse;


	HashedBlockPosition pilotSeatPos;
	private final List<HashedBlockPosition> passengerSeats = new ArrayList<HashedBlockPosition>();
	private List<Vector3F<Float>> engineLoc;

	private static final String TAGNAME = "rocketStats";
	private HashMap<String, Object> statTags;
	
	private static final int INVALID_SEAT = Integer.MIN_VALUE;

	public StatsRocket() {
		thrust = 0;
		weight = 0;
		fuelFluid = null;
		oxidizerFluid = null;
		fuelMonopropellant = 0;
		fuelBipropellant = 0;
		fuelOxidizer = 0;
		fuelRateMonopropellant = 0;
		fuelRateBipropellant = 0;
		fuelRateOxidizer = 0;
		drillingPower = 0f;
		orbitHeight = ARConfiguration.getCurrentConfig().orbit.get();
		injectionBurnLenghtMult = 1;
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

	public int getThrust() {return (int) (thrust*ARConfiguration.getCurrentConfig().rocketThrustMultiplier.get());}
	public int getWeight() {return weight;}
	public ResourceLocation getFuelFluid() {return fuelFluid;}
	public ResourceLocation getOxidizerFluid() {return oxidizerFluid;}
	public float getDrillingPower() {return drillingPower;}
	public void setDrillingPower(float power) {drillingPower = power;}
	public float getAcceleration(float gravitationalMultiplier) { return (getThrust() - (weight * ((ARConfiguration.getCurrentConfig().gravityAffectsFuel.get()) ? gravitationalMultiplier : 1)))/10000f; }
	public List<Vector3F<Float>> getEngineLocations() { return engineLoc; }

	public void setThrust(int thrust) { this.thrust = thrust; }
	public void setWeight(int weight) { this.weight = weight; }
	public void setFuelFluid(ResourceLocation fuelFluid) { this.fuelFluid = fuelFluid; }
	public void setOxidizerFluid(ResourceLocation oxidizerFluid) { this.oxidizerFluid = oxidizerFluid; }

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
		stat.fuelFluid = this.fuelFluid;
		stat.oxidizerFluid = this.oxidizerFluid;
		stat.drillingPower = this.drillingPower;

		for(FuelType type : FuelType.values()) {
			stat.setFuelAmount(type, this.getFuelAmount(type));
			stat.setFuelRate(type, this.getFuelRate(type));
			stat.setFuelCapacity(type, this.getFuelCapacity(type));
			stat.setBaseFuelRate(type, this.getBaseFuelRate(type));
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
		case LIQUID_MONOPROPELLANT:
			return fuelMonopropellant;
		case LIQUID_BIPROPELLANT:
			return fuelBipropellant;
		case LIQUID_OXIDIZER:
			return fuelOxidizer;
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
		case LIQUID_MONOPROPELLANT:
			return fuelCapacityMonopropellant;
		case LIQUID_BIPROPELLANT:
			return fuelCapacityBipropellant;
		case LIQUID_OXIDIZER:
			return fuelCapacityOxidizer;
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

		if(!ARConfiguration.getCurrentConfig().rocketRequireFuel.get())
			return 0;

		switch(type) {
		case WARP:
			return fuelRateWarp;
		case IMPULSE:
			return fuelRateImpulse;
		case ION:
			return fuelRateIon;
		case LIQUID_MONOPROPELLANT:
			return fuelRateMonopropellant;
		case LIQUID_BIPROPELLANT:
			return fuelRateBipropellant;
		case LIQUID_OXIDIZER:
			return fuelRateOxidizer;
		case NUCLEAR:
			return fuelRateNuclear;
		}
		return 0;
	}

	/**
	 * @param type
	 * @return the base engine consumption rate of the fuel per tick
	 */
	public float getBaseFuelRate(FuelRegistry.FuelType type) {

		if(!ARConfiguration.getCurrentConfig().rocketRequireFuel.get())
			return 0;

		switch(type) {
			case WARP:
				return fuelBaseRateWarp;
			case IMPULSE:
				return fuelBaseRateImpulse;
			case ION:
				return fuelBaseRateIon;
			case LIQUID_MONOPROPELLANT:
				return fuelBaseRateMonopropellant;
			case LIQUID_BIPROPELLANT:
				return fuelBaseRateBipropellant;
			case LIQUID_OXIDIZER:
				return fuelBaseRateOxidizer;
			case NUCLEAR:
				return fuelBaseRateNuclear;
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
		case LIQUID_MONOPROPELLANT:
			fuelMonopropellant = amt;
			break;
		case LIQUID_BIPROPELLANT:
			fuelBipropellant = amt;
			break;
			case LIQUID_OXIDIZER:
			fuelOxidizer = amt;
			break;
		case NUCLEAR:
			fuelNuclear = amt;
		}
	}

	/**
	 * Sets the fuel consumption rate per tick of the stat
	 * @param type
	 * @param rate
	 */
	public void setFuelRate(FuelRegistry.FuelType type, int rate) {
		switch(type) {
		case WARP:
			fuelRateWarp = rate;
			break;
		case IMPULSE:
			fuelRateImpulse = rate;
			break;
		case ION:
			fuelRateIon = rate;
			break;
		case LIQUID_MONOPROPELLANT:
			fuelRateMonopropellant = rate;
			break;
		case LIQUID_BIPROPELLANT:
			fuelRateBipropellant = rate;
			break;
		case LIQUID_OXIDIZER:
			fuelRateOxidizer = rate;
			break;
		case NUCLEAR:
			fuelRateNuclear = rate;
		}
	}

	/**
	 * Sets the engine consumption rate per tick of the stat
	 * @param type
	 * @param rate
	 */
	public void setBaseFuelRate(FuelRegistry.FuelType type, float rate) {
		switch(type) {
			case WARP:
				fuelBaseRateWarp = rate;
				break;
			case IMPULSE:
				fuelBaseRateImpulse = rate;
				break;
			case ION:
				fuelBaseRateIon = rate;
				break;
			case LIQUID_MONOPROPELLANT:
				fuelBaseRateMonopropellant = rate;
				break;
			case LIQUID_BIPROPELLANT:
				fuelBaseRateBipropellant = rate;
				break;
			case LIQUID_OXIDIZER:
				fuelBaseRateOxidizer = rate;
				break;
			case NUCLEAR:
				fuelBaseRateNuclear = rate;
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
		case LIQUID_MONOPROPELLANT:
			fuelCapacityMonopropellant = amt;
			break;
		case LIQUID_BIPROPELLANT:
			fuelCapacityBipropellant = amt;
			break;
		case LIQUID_OXIDIZER:
			fuelCapacityOxidizer = amt;
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
		case LIQUID_MONOPROPELLANT:
			int maxAddMono = fuelCapacityMonopropellant - fuelMonopropellant;
			int amountToAddMono = Math.min(amt, maxAddMono);
			fuelMonopropellant += amountToAddMono;
			return amountToAddMono;
		case LIQUID_BIPROPELLANT:
			int maxAddBi = fuelCapacityBipropellant - fuelBipropellant;
			int amountToAddBi = Math.min(amt, maxAddBi);
			fuelBipropellant += amountToAddBi;
			return amountToAddBi;
		case LIQUID_OXIDIZER:
			int maxAddOxi = fuelCapacityOxidizer - fuelOxidizer;
			int amountToAddOxi = Math.min(amt, maxAddOxi);
			fuelOxidizer += amountToAddOxi;
			return amountToAddOxi;
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
		fuelFluid = null;
		oxidizerFluid = null;
		drillingPower = 0f;

		for(FuelType type : FuelType.values()) {
			setFuelAmount(type, 0);
			setFuelRate(type, 0);
			setFuelCapacity(type, 0);
		}

		fuelMonopropellant = 0;
		fuelBipropellant = 0;
		fuelOxidizer = 0;
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

	public static StatsRocket createFromNBT(CompoundNBT nbt) { 
		if(nbt.contains(TAGNAME)) {
			CompoundNBT stats = nbt.getCompound(TAGNAME);
			StatsRocket statsRocket = new StatsRocket();
			statsRocket.readFromNBT(stats);
			return statsRocket;
		}

		return new StatsRocket();
	}

	public void writeToNBT(CompoundNBT nbt) {
		CompoundNBT stats = new CompoundNBT();

		stats.putInt("thrust", this.thrust);
		stats.putInt("weight", this.weight);
		if(this.fuelFluid != null)
			stats.putString("fuelFluid", this.fuelFluid.toString());
		if(this.oxidizerFluid != null)
			stats.putString("oxidizerFluid", this.oxidizerFluid.toString());
		stats.putFloat("drillingPower", this.drillingPower);

		stats.putInt("fuelMonopropellant", this.fuelMonopropellant);
		stats.putInt("fuelBipropellant", this.fuelBipropellant);
		stats.putInt("fuelOxidizer", this.fuelOxidizer);
		stats.putInt("fuelImpulse", this.fuelImpulse);
		stats.putInt("fuelIon", this.fuelIon);
		stats.putInt("fuelNuclear", this.fuelNuclear);
		stats.putInt("fuelWarp", this.fuelWarp);

		stats.putInt("fuelCapacityMonopropellant", this.fuelCapacityMonopropellant);
		stats.putInt("fuelCapacityBipropellant", this.fuelCapacityBipropellant);
		stats.putInt("fuelCapacityOxidizer", this.fuelCapacityOxidizer);
		stats.putInt("fuelCapacityImpulse", this.fuelCapacityImpulse);
		stats.putInt("fuelCapacityIon", this.fuelCapacityIon);
		stats.putInt("fuelCapacityNuclear", this.fuelCapacityNuclear);
		stats.putInt("fuelCapacityWarp", this.fuelCapacityWarp);

		stats.putInt("fuelRateMonopropellant", this.fuelRateMonopropellant);
		stats.putInt("fuelRateBipropellant", this.fuelRateBipropellant);
		stats.putInt("fuelRateOxidizer", this.fuelRateOxidizer);
		stats.putInt("fuelRateImpulse", this.fuelRateImpulse);
		stats.putInt("fuelRateIon", this.fuelRateIon);
		stats.putInt("fuelRateNuclear", this.fuelRateNuclear);
		stats.putInt("fuelRateWarp", this.fuelRateWarp);
		
		stats.putFloat("fuelBaseRateMonopropellant", this.fuelBaseRateMonopropellant);
		stats.putFloat("fuelBaseRateBipropellant", this.fuelBaseRateBipropellant);
		stats.putFloat("fuelBaseRateOxidizer", this.fuelBaseRateOxidizer);
		stats.putFloat("fuelBaseRateImpulse", this.fuelBaseRateImpulse);
		stats.putFloat("fuelBaseRateIon", this.fuelBaseRateIon);
		stats.putFloat("fuelBaseRateNuclear", this.fuelBaseRateNuclear);
		stats.putFloat("fuelBaseRateWarp", this.fuelBaseRateWarp);

		CompoundNBT dynStats = new CompoundNBT();
		for(String key : statTags.keySet()) {
			Object obj = statTags.get(key);

			if(obj instanceof Float)
				dynStats.putFloat(key, (float)obj);
			else if(obj instanceof Integer)
				dynStats.putInt(key, (int)obj);
		}
		if(!dynStats.isEmpty())
			stats.put("dynStats", dynStats);

		stats.putInt("playerXPos", pilotSeatPos.x);
		stats.putInt("playerYPos", pilotSeatPos.y);
		stats.putInt("playerZPos", pilotSeatPos.z);

		if(!engineLoc.isEmpty()) {
			int locs[] = new int[engineLoc.size()*3];

			for(int i=0 ; (i/3) < engineLoc.size(); i+=3) {
				Vector3F<Float> vec = engineLoc.get(i/3);
				locs[i] = vec.x.intValue();
				locs[i + 1] = vec.y.intValue();
				locs[i + 2] = vec.z.intValue();
			}
			stats.putIntArray("engineLoc", locs);
		}

		if(!passengerSeats.isEmpty()) {
			int locs[] = new int[passengerSeats.size()*3];

			for(int i=0 ; (i/3) < passengerSeats.size(); i+=3) {
				HashedBlockPosition vec = passengerSeats.get(i/3);
				locs[i] = vec.x;
				locs[i + 1] = vec.y;
				locs[i + 2] = vec.z;

			}
			stats.putIntArray("passengerSeats", locs);
		}

		nbt.put(TAGNAME, stats);
	}

	public void readFromNBT(CompoundNBT nbt) {
		if(nbt.contains(TAGNAME)) {
			CompoundNBT stats = nbt.getCompound(TAGNAME);
			this.thrust = stats.getInt("thrust");
			this.weight = stats.getInt("weight");
			if(nbt.contains("fuelFluid"))
				this.fuelFluid = ResourceLocation.tryCreate(stats.getString("fuelFluid"));
			else
				this.fuelFluid = null;
			
			if(nbt.contains("oxidizerFluid"))
				this.oxidizerFluid = ResourceLocation.tryCreate(stats.getString("oxidizerFluid"));
			else
				this.oxidizerFluid = null;
			this.drillingPower = stats.getFloat("drillingPower");

			this.fuelMonopropellant = stats.getInt("fuelMonopropellant");
			this.fuelBipropellant = stats.getInt("fuelBipropellant");
			this.fuelOxidizer = stats.getInt("fuelOxidizer");
			this.fuelImpulse = stats.getInt("fuelImpulse");
			this.fuelIon = stats.getInt("fuelIon");
			this.fuelNuclear = stats.getInt("fuelNuclear");
			this.fuelWarp = stats.getInt("fuelWarp");

			this.fuelCapacityMonopropellant = stats.getInt("fuelCapacityMonopropellant");
			this.fuelCapacityBipropellant = stats.getInt("fuelCapacityBipropellant");
			this.fuelCapacityOxidizer = stats.getInt("fuelCapacityOxidizer");
			this.fuelCapacityImpulse = stats.getInt("fuelCapacityImpulse");
			this.fuelCapacityIon = stats.getInt("fuelCapacityIon");
			this.fuelCapacityNuclear = stats.getInt("fuelCapacityNuclear");
			this.fuelCapacityWarp = stats.getInt("fuelCapacityWarp");

			this.fuelRateMonopropellant = stats.getInt("fuelRateMonopropellant");
			this.fuelRateBipropellant = stats.getInt("fuelRateBipropellant");
			this.fuelRateOxidizer = stats.getInt("fuelRateOxidizer");
			this.fuelRateImpulse = stats.getInt("fuelRateImpulse");
			this.fuelRateIon = stats.getInt("fuelRateIon");
			this.fuelRateNuclear = stats.getInt("fuelRateNuclear");
			this.fuelRateWarp = stats.getInt("fuelRateWarp");
			
			this.fuelBaseRateMonopropellant = stats.getInt("fuelBaseRateMonopropellant");
			this.fuelBaseRateBipropellant = stats.getInt("fuelBaseRateBipropellant");
			this.fuelBaseRateOxidizer = stats.getInt("fuelBaseRateOxidizer");
			this.fuelBaseRateImpulse = stats.getInt("fuelBaseRateImpulse");
			this.fuelBaseRateIon = stats.getInt("fuelBaseRateIon");
			this.fuelBaseRateNuclear = stats.getInt("fuelBaseRateNuclear");
			this.fuelBaseRateWarp = stats.getInt("fuelBaseRateWarp");
			

			if(stats.contains("dynStats")) {
				CompoundNBT dynStats = stats.getCompound("dynStats");


				for(Object key : dynStats.keySet()) {
					Object obj = dynStats.get((String)key);

					if(obj instanceof FloatNBT)
						setStatTag((String)key, dynStats.getFloat((String)key));
					else if(obj instanceof IntNBT)
						setStatTag((String)key, dynStats.getInt((String)key));
				}
			}

			pilotSeatPos.x = stats.getInt("playerXPos");
			pilotSeatPos.y = (short)stats.getInt("playerYPos");
			pilotSeatPos.z = stats.getInt("playerZPos");

			if(stats.contains("engineLoc")) {
				int locations[] = stats.getIntArray("engineLoc");

				for(int i=0 ; i < locations.length; i+=3) {

					this.addEngineLocation(locations[i], locations[i+1], locations[i+2]);
				}
			}

			if(stats.contains("passengerSeats")) {
				int locations[] = stats.getIntArray("passengerSeats");

				for(int i=0 ; i < locations.length; i+=3) {

					this.addPassengerSeat(locations[i], locations[i+1], locations[i+2]);
				}
			}
		}
	}
}