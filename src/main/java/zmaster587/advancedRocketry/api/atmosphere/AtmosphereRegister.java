package zmaster587.advancedRocketry.api.atmosphere;

import zmaster587.advancedRocketry.api.IAtmosphere;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import net.minecraft.fluid.Fluid;

public class AtmosphereRegister {
	private static final AtmosphereRegister instance = new AtmosphereRegister();
	
	private AtmosphereRegister() {
		atmosphereRegistration = new HashMap<>();
		atmosphereList = new LinkedList<>();
		harvestableAtmosphere = new LinkedList<>();
	}
	
	
	public static AtmosphereRegister getInstance() {
		return instance;
	}
	
	private Map<String, IAtmosphere> atmosphereRegistration;
	private List<Fluid> harvestableAtmosphere;
	private List<IAtmosphere> atmosphereList;
	
	/**
	 * Registers the atmosphere with the mod
	 * @param atmosphere atmosphere to register
	 */
	public void registerAtmosphere(IAtmosphere atmosphere) {
		atmosphereRegistration.put(atmosphere.getUnlocalizedName(), atmosphere);
		atmosphereList.add(atmosphere);
	}
	
	/**
	 * You should be using unlocalized names for the atmosphere here!
	 * @param identifier registered name of the atmosphere 
	 * @return atmosphere  or AIR if not in the list
	 */
	public IAtmosphere getAtmosphere(String identifier) {
		IAtmosphere atm = atmosphereRegistration.get(identifier);
		return atm == null ? getAtmosphere("air") : atm;
	}
	
	public void registerHarvestableFluid(Fluid fluid) {
		harvestableAtmosphere.add(fluid);
	}
	
	public List<Fluid> getHarvestableGasses() {
		return harvestableAtmosphere;
	}
	
	/**
	 * @return list of all registered atmospheres
	 */
	public List<IAtmosphere> getAtmosphereList() {
		return atmosphereList;
	}
}
