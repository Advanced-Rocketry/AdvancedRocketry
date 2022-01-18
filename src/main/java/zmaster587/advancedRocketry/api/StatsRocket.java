package zmaster587.advancedRocketry.api;

import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.Fluids;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.FloatNBT;
import net.minecraft.nbt.IntNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.IFluidTank;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.templates.FluidTank;
import net.minecraftforge.registries.ForgeRegistries;
import zmaster587.advancedRocketry.api.fuel.FuelRegistry;
import zmaster587.advancedRocketry.api.fuel.FuelRegistry.FuelType;
import zmaster587.advancedRocketry.util.RocketFluidTank;
import zmaster587.libVulpes.util.HashedBlockPosition;
import zmaster587.libVulpes.util.Vector3F;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class StatsRocket {

	private int thrust;
	private int weight;
	private float drillingPower;

	//Used for orbital height calculations
	public int orbitHeight;
	public float injectionBurnLengthMult;

	private RocketFluidTank fuelTank;
	private RocketFluidTank oxidizerTank;
	private RocketFluidTank workingFluidTank;

	HashedBlockPosition pilotSeatPos;
	private final List<HashedBlockPosition> passengerSeats = new ArrayList<>();
	private List<Vector3F<Float>> engineLoc;

	public static final String TAGNAME = "rocketstats";
	private HashMap<String, Object> statTags;
	
	private static final int INVALID_SEAT = Integer.MIN_VALUE;

	public StatsRocket() {
		//Basic rocket stats
		thrust = 0;
		weight = 0;
		//Fuel handling stuff
		fuelTank = new RocketFluidTank(0, 0, FuelType.LIQUID_BIPROPELLANT);
		oxidizerTank = new RocketFluidTank(0, 0, FuelType.LIQUID_OXIDIZER);
		workingFluidTank = new RocketFluidTank(0, 0, FuelType.NUCLEAR_WORKING_FLUID);
		//Non-fuel stuff
		drillingPower = 0f;
		orbitHeight = ARConfiguration.getCurrentConfig().orbit.get();
		injectionBurnLengthMult = 1;
		pilotSeatPos = new HashedBlockPosition(0,0,0);
		pilotSeatPos.x = INVALID_SEAT;
		engineLoc = new ArrayList<>();
		statTags = new HashMap<>();
	}

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
	public float getDrillingPower() {return drillingPower;}
	public void setDrillingPower(float power) {drillingPower = power;}
	public float getAcceleration(float gravitationalMultiplier) { return (getThrust() - (weight * ((ARConfiguration.getCurrentConfig().gravityAffectsFuel.get()) ? gravitationalMultiplier : 1)))/10000f; }
	public float getAcceleration() { return getThrust()/10000f; }
	public List<Vector3F<Float>> getEngineLocations() { return engineLoc; }
	public boolean isNuclear() {return workingFluidTank.getBaseFuelRate() > 0;}

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
		engineLoc.add(new Vector3F<>(x, y, z));
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

		stat.fuelTank = this.fuelTank;
		stat.oxidizerTank = this.oxidizerTank;
		stat.workingFluidTank = this.workingFluidTank;

		stat.pilotSeatPos = new HashedBlockPosition(this.pilotSeatPos.x, this.pilotSeatPos.y, this.pilotSeatPos.z);
		stat.passengerSeats.addAll(passengerSeats);
		stat.engineLoc = new ArrayList<>(engineLoc);
		stat.statTags = new HashMap<>(statTags);
		return stat;
	}

	/**
	 * Sets the fuel capacity of the fuel type in this stat
	 * @param type
	 * @param amt
	 */
	public void setFluidTank(@Nonnull FuelRegistry.FuelType type, int amt, int baseFuelRate) {
		switch(type) {
		case LIQUID_MONOPROPELLANT:
		case LIQUID_BIPROPELLANT:
			fuelTank = new RocketFluidTank(amt, baseFuelRate, type);
			break;
		case LIQUID_OXIDIZER:
			oxidizerTank = new RocketFluidTank(amt, baseFuelRate, type);
			break;
		case NUCLEAR_WORKING_FLUID:
			workingFluidTank = new RocketFluidTank(amt, baseFuelRate, type);
		}
	}

	/**
	 * Gets the fuel tank of this rocket to access
	 * @param type
	 * @return the IFluidTank representation of the tank in the rocket
	 */
	public RocketFluidTank getFluidTank(@Nonnull FuelRegistry.FuelType type) {
		switch(type) {
			case LIQUID_MONOPROPELLANT:
			case LIQUID_BIPROPELLANT:
				return fuelTank;
			case LIQUID_OXIDIZER:
				return oxidizerTank;
			case NUCLEAR_WORKING_FLUID:
				return workingFluidTank;
		}
		return new RocketFluidTank(0, 0, FuelType.LIQUID_BIPROPELLANT);
	}

	/**
	 * Gets whether the rocket has any fuel to burn
	 * @return boolean on whetehr the rocket can still burn fuel
	 *  */
	public boolean hasNonZeroFuel() {
		return getFluidTank(FuelType.LIQUID_MONOPROPELLANT).getFluidAmount() > 0 || getFluidTank(FuelType.NUCLEAR_WORKING_FLUID).getFluidAmount() > 0;
	}

	public float getFuelFillPercentage(@Nonnull FuelRegistry.FuelType type) {
		switch(type) {
			case LIQUID_MONOPROPELLANT:
			case LIQUID_BIPROPELLANT:
				return fuelTank.getFluidAmount()/(float)fuelTank.getCapacity();
			case LIQUID_OXIDIZER:
				return oxidizerTank.getFluidAmount()/(float)oxidizerTank.getCapacity();
			case NUCLEAR_WORKING_FLUID:
				return workingFluidTank.getFluidAmount()/(float)workingFluidTank.getCapacity();
		}
		return 0;
	}

	/**
	 * Gets the fuel tank of this rocket that the inserted fluid can fit into
	 * @param fluid
	 * @return the IFluidTank representation of the tank in the rocket
	 */
	public RocketFluidTank getFluidTank(@Nonnull FluidStack fluid) {
		if (fuelTank.fill(fluid, IFluidHandler.FluidAction.SIMULATE) > 0 ) return fuelTank;
		else if (oxidizerTank.fill(fluid, IFluidHandler.FluidAction.SIMULATE) > 0 ) return oxidizerTank;
		else if (workingFluidTank.fill(fluid, IFluidHandler.FluidAction.SIMULATE) > 0 ) return workingFluidTank;
		return new RocketFluidTank(0, 0, FuelType.LIQUID_BIPROPELLANT);
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

		fuelTank = new RocketFluidTank(0, 0, FuelType.LIQUID_BIPROPELLANT);
		oxidizerTank = new RocketFluidTank(0, 0, FuelType.LIQUID_OXIDIZER);
		workingFluidTank = new RocketFluidTank(0, 0, FuelType.NUCLEAR_WORKING_FLUID);

		pilotSeatPos.x = INVALID_SEAT;
		clearEngineLocations();
		passengerSeats.clear();
		statTags.clear();
	}

	public void setStatTag(String str, float value) {
		statTags.put(str, value);
	}

	public void setStatTag(String str, int value) {
		statTags.put(str, value);
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
		stats.putFloat("drillingPower", this.drillingPower);

		CompoundNBT fuel = new CompoundNBT();
		CompoundNBT oxidizer = new CompoundNBT();
		CompoundNBT workingfluid = new CompoundNBT();
		fuelTank.writeToNBT(fuel);
		oxidizerTank.writeToNBT(oxidizer);
		workingFluidTank.writeToNBT(workingfluid);
		stats.put("fuel", fuel);
		stats.put("oxidizer", oxidizer);
		stats.put("workingfluid", workingfluid);

		CompoundNBT dynStats = new CompoundNBT();
		for(String key : statTags.keySet()) {
			Object obj = statTags.get(key);

			if(obj instanceof Float)
				dynStats.putFloat(key, (float)obj);
			else if(obj instanceof Integer)
				dynStats.putInt(key, (int)obj);
		}
		if(!dynStats.isEmpty())
			stats.put("dynamicstats", dynStats);

		stats.putInt("playerXPos", pilotSeatPos.x);
		stats.putInt("playerYPos", pilotSeatPos.y);
		stats.putInt("playerZPos", pilotSeatPos.z);

		if(!engineLoc.isEmpty()) {
			int[] locs = new int[engineLoc.size()*3];

			for(int i=0 ; (i/3) < engineLoc.size(); i+=3) {
				Vector3F<Float> vec = engineLoc.get(i/3);
				locs[i] = vec.x.intValue();
				locs[i + 1] = vec.y.intValue();
				locs[i + 2] = vec.z.intValue();
			}
			stats.putIntArray("enginelocations", locs);
		}

		if(!passengerSeats.isEmpty()) {
			int[] locs = new int[passengerSeats.size()*3];

			for(int i=0 ; (i/3) < passengerSeats.size(); i+=3) {
				HashedBlockPosition vec = passengerSeats.get(i/3);
				locs[i] = vec.x;
				locs[i + 1] = vec.y;
				locs[i + 2] = vec.z;

			}
			stats.putIntArray("passengerseats", locs);
		}

		nbt.put(TAGNAME, stats);
	}

	public void readFromNBT(CompoundNBT nbt) {
		if(nbt.contains(TAGNAME)) {
			CompoundNBT stats = nbt.getCompound(TAGNAME);
			this.thrust = stats.getInt("thrust");
			this.weight = stats.getInt("weight");
			this.drillingPower = stats.getFloat("drillingPower");

			CompoundNBT fuel = stats.getCompound("fuel");
			CompoundNBT oxidizer = stats.getCompound("oxidizer");
			CompoundNBT workingfluid = stats.getCompound("workingfluid");
			fuelTank = fuelTank.readFromNBT(fuel);
			oxidizerTank = oxidizerTank.readFromNBT(oxidizer);
			workingFluidTank = workingFluidTank.readFromNBT(workingfluid);

			if(stats.contains("dynamicstats")) {
				CompoundNBT dynStats = stats.getCompound("dynamicstats");


				for(String key : dynStats.keySet()) {
					Object obj = dynStats.get(key);

					if(obj instanceof FloatNBT)
						setStatTag(key, dynStats.getFloat(key));
					else if(obj instanceof IntNBT)
						setStatTag(key, dynStats.getInt(key));
				}
			}

			pilotSeatPos.x = stats.getInt("playerXPos");
			pilotSeatPos.y = (short)stats.getInt("playerYPos");
			pilotSeatPos.z = stats.getInt("playerZPos");

			if(stats.contains("enginelocations")) {
				int[] locations = stats.getIntArray("enginelocations");

				for(int i=0 ; i < locations.length; i+=3) {

					this.addEngineLocation(locations[i], locations[i+1], locations[i+2]);
				}
			}

			if(stats.contains("passengerseats")) {
				int[] locations = stats.getIntArray("passengerseats");

				for(int i=0 ; i < locations.length; i+=3) {

					this.addPassengerSeat(locations[i], locations[i+1], locations[i+2]);
				}
			}
		}
	}
}