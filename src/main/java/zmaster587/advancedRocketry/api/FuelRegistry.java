package zmaster587.advancedRocketry.api;

import java.util.HashSet;
import java.util.Iterator;

import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.Fluid;

public class FuelRegistry {

	public static final FuelRegistry instance = new FuelRegistry();
	
	public enum FuelType {
		LIQUID,		//Used in ground to space rockets
		NUCLEAR,	//Used in ground to asteroid missions
		ION,		//Used in satalites
		WARP,		//Used in intersteller missions
		IMPULSE;	//Used in interplanetary missions
		
		//Stores a fuel entry for each type of fuel
		final HashSet<fuelEntry> fuels;
		
		private FuelType() {
			fuels = new HashSet<FuelRegistry.fuelEntry>();
		}
		
		//Returns true if successfully added, false if already exists
		public boolean addFuel(fuelEntry entry) {
			entry.type = this;
			return !fuels.add(entry);
		}
		
		public boolean isFuel(ItemStack stack) {
			return isFuel((Object)stack);
		}
		
		public boolean isFuel(Fluid stack) {
			return isFuel((Object)stack);
		}
		
		//Returns true if the passed Itemstack or fluidstack is a fuel
		private boolean isFuel(Object obj) {
				Iterator<fuelEntry> currFuel = fuels.iterator();
				
				while(currFuel.hasNext()) {
					if(currFuel.next().fuel == obj)
						return true;
				}
				return false;
		}
		
		//Returns the fuel if it exists otherwise null (Helper)
		public fuelEntry getFuel(ItemStack stack) {
			return getFuel((Object)stack);
		}
		
		//Returns the fuel if it exists otherwise null (Helper)
		public fuelEntry getFuel(Fluid fluid) {
			return getFuel((Object)fluid);
		}
		
		private fuelEntry getFuel(Object obj) {
			Iterator<fuelEntry> currFuel = fuels.iterator();
			
			while(currFuel.hasNext()) {
				fuelEntry entry;

				if((entry = currFuel.next()).fuelMaches(obj))
					return entry;
			}
			return null;
		}
	}
	
	
	
	private class fuelEntry {
		
		//Fuel: itemstack or liquid
		private Object fuel;
		//Type: as defined above
		private FuelType type;
		
		//Multiplier: basically how good is the fuel relative to the base value set in the config
		private float multiplier;
		
		
		public fuelEntry(Object fuel, float multiplier) {
			this.fuel = fuel;
			this.multiplier = multiplier;
		}
		
		//Returns true if the passed object is indeed the same fuel
		public boolean fuelMaches(Object obj) {
			if(fuel.getClass() != obj.getClass())
				return false;
			
			if(fuel instanceof ItemStack) {
				return ItemStack.areItemStacksEqual((ItemStack)fuel, (ItemStack)obj);
			}
			
			if(fuel instanceof Fluid) {
				return fuel.equals(obj);
			}
			return false;
		}
		
		//Override equals(Object), each the itemstack or fluid determines the entry
		@Override
		public boolean equals(Object obj) {
			if(obj != null) {
				if(obj instanceof fuelEntry) {
					fuelEntry cmp = ((fuelEntry)obj);
					return fuelMaches(cmp.fuel) && cmp.type == cmp.type;
				}
				return super.equals(obj);
			}
			return false;
		}
		
		public Object getFuel() {
			return fuel;
		}
	}
	
	//Registers the fluid with the fueltype type with multiplier multiplier
	public boolean registerFuel(FuelType type ,Fluid fluid, float multiplier) {
		fuelEntry entry = new fuelEntry(fluid, multiplier);
		
		return type.addFuel(entry);
	}
	
	//Registers the itemstack with the fueltype type with multiplier multiplier
	public boolean registerFuel(FuelType type, ItemStack item, float multiplier) {
		fuelEntry entry = new fuelEntry(item, multiplier);
		return type.addFuel(entry);
	}
	
	//Returns true if the itemStack obj is a fuel of the fueltype type
	public boolean isFuel(FuelType type, ItemStack obj) {
		return isFuel(type, (Object)obj);
	}
	
	//Returns true if the fluid obj is a fuel of the fueltype type
	public boolean isFuel(FuelType type, Fluid obj) {
		return isFuel(type, (Object)obj);
	}
	
	private boolean isFuel(FuelType type, Object obj) {
		return type.isFuel(obj);
	}
	
	//Returns true if the itemStack obj is a fuel
	public boolean isFuel(ItemStack obj) {
		return isFuel((Object)obj);
	}
	
	//Returns true if the fluid obj is a fuel
	public boolean isFuel(Fluid obj) {
		return isFuel((Object)obj);
	}
	
	
	private boolean isFuel(Object obj) {
		
		for(FuelType type : FuelType.values())
			if(type.isFuel(obj)) return true;
		
		return false;
	}
	
	// returns the multiplier of the object for the itemstack
	public float getMultiplier(FuelType type, ItemStack obj) {
		return getMultiplier(type, (Object)obj);
	}
	
	// returns the multiplier of the object for the fluid
	public float getMultiplier(FuelType type, Fluid obj) {
		return getMultiplier(type, (Object)obj);
	}
	
	private float getMultiplier(FuelType type, Object obj) {
		fuelEntry fuel = type.getFuel(obj);
		
		if(fuel == null)
			return 0f;
		return fuel.multiplier;
	}
}