package zmaster587.advancedRocketry.api.fuel;

import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.Fluid;

import javax.annotation.Nonnull;
import java.util.HashSet;

public class FuelRegistry {

	public static final FuelRegistry instance = new FuelRegistry();
	
	public enum FuelType {
		LIQUID_MONOPROPELLANT,		//Used in ground to space rockets
		LIQUID_BIPROPELLANT,
		LIQUID_OXIDIZER,
		NUCLEAR,	//Used in ground to asteroid missions
		ION,		//Used in satellites
		WARP,		//Used in interstellar missions
		IMPULSE;	//Used in interplanetary missions
		
		//Stores a fuel entry for each type of fuel
		final HashSet<fuelEntry> fuels;
		
		FuelType() {
			fuels = new HashSet<>();
		}
		
		/**
		 * @param entry fuelEntry to add
		 * @return true if successfully added, false if already exists
		 */
		public boolean addFuel(fuelEntry entry) {
			entry.type = this;
			return !fuels.add(entry);
		}
		
		/**
		 * @param stack
		 * @return true if the itemStack is a fuel Source
		 */
		public boolean isFuel(@Nonnull ItemStack stack) {
			return isFuel((Object)stack);
		}
		
		/**
		 * @param stack
		 * @return true if the liquid is a fuelsource
		 */
		public boolean isFuel(Fluid stack) {
			return isFuel((Object)stack);
		}
		
		/**
		 * Called by helper functions and to avoid confusion
		 * @param obj
		 * @return true if the passed Itemstack or fluidstack is a fuel
		 */
		private boolean isFuel(Object obj) {

			for (fuelEntry fuel : fuels) {
				if (fuel.fuel == obj)
					return true;
			}
				return false;
		}
		
		//Returns the fuel if it exists otherwise null (Helper)

		public fuelEntry getFuel(@Nonnull ItemStack stack) {
			return getFuel((Object)stack);
		}
		
		//Returns the fuel if it exists otherwise null (Helper)
		public fuelEntry getFuel(Fluid fluid) {
			return getFuel((Object)fluid);
		}
		
		/**
		 * @param obj
		 * @return Fuel entry for the itemStack if it exists, null otherwise
		 */
		private fuelEntry getFuel(Object obj) {

			for (fuelEntry fuel : fuels) {
				fuelEntry entry;

				if ((entry = fuel).fuelMatches(obj))
					return entry;
			}
			return null;
		}
	}
	
	
	
	private static class fuelEntry {
		
		//Fuel: itemstack or liquid
		private Object fuel;
		//Type: as defined above
		private FuelType type;
		
		//Multiplier: basically how good is the fuel relative to the base value set in the config
		private float multiplier;
		
		/**
		 * @param fuel ItemStack or fluid to register as fuel
		 * @param multiplier how many fuel points one unit of this object is worth
		 */
		public fuelEntry(Object fuel, float multiplier) {
			this.fuel = fuel;
			this.multiplier = multiplier;
		}
		
		//Returns 
		/**
		 * @param obj object to check against
		 * @return true if the passed object is indeed the same fuel
		 */
		public boolean fuelMatches(Object obj) {
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
					return fuelMatches(cmp.fuel) && cmp.type == type;
				}
				return super.equals(obj);
			}
			return false;
		}
	}
	/**
	 * @param type {@link FuelType} to register with
	 * @param fluid fluid to register
	 * @param multiplier amount of fuel points 1mb is worth
	 * @return true if successfully added to the registry, false if it already exists
	 */
	public boolean registerFuel(FuelType type ,Fluid fluid, float multiplier) {
		fuelEntry entry = new fuelEntry(fluid, multiplier);
		
		return type.addFuel(entry);
	}
	
	/**
	 * Registers an ItemStack to be used as fuel for engines that use the given type of fuel
	 * @param type {@link FuelType} to register with
	 * @param item item to register
	 * @param multiplier amount of fuel points one item is worth
	 * @return true if successfully added to the registry, false if it already exists
	 */
	public boolean registerFuel(FuelType type, @Nonnull ItemStack item, float multiplier) {
		fuelEntry entry = new fuelEntry(item, multiplier);
		return type.addFuel(entry);
	}
	
	/**
	 * @param type {@link FuelType} registry to check in
	 * @param stack ItemStack to check
	 * @return true if the itemStack has been registered as {@link FuelType} fuel
	 */
	public boolean isFuel(FuelType type, @Nonnull ItemStack stack) {
		return isFuel(type, (Object)stack);
	}
	
	/**
	 * @param type {@link FuelType} registry to check in
	 * @param fluid Fluid to check
	 * @return true if the fluid has been registered as {@link FuelType} fuel
	 */
	public boolean isFuel(FuelType type, Fluid fluid) {
		return isFuel(type, (Object)fluid);
	}
	
	private boolean isFuel(FuelType type, Object obj) {
		return type.isFuel(obj);
	}
	
	/**
	 * @param type {@link FuelType} registry to check in
	 * @param stack itemStack to check against
	 * @return the amount of fuel points one item of this stack is worth
	 */
	public float getMultiplier(FuelType type, @Nonnull ItemStack stack) {
		return getMultiplier(type, (Object)stack);
	}
	
	/**
	 * @param type {@link FuelType} registry to check in
	 * @param fluid itemStack to check against
	 * @return the amount of fuel points one millibucket of this fluid is worth
	 */
	public float getMultiplier(FuelType type, Fluid fluid) {
		return getMultiplier(type, (Object)fluid);
	}
	
	private float getMultiplier(FuelType type, Object obj) {
		fuelEntry fuel = type.getFuel(obj);
		
		if(fuel == null)
			return 0f;
		return fuel.multiplier;
	}
}