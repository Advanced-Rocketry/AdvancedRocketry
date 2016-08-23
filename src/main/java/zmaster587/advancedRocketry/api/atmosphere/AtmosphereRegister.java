package zmaster587.advancedRocketry.api.atmosphere;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import zmaster587.advancedRocketry.api.IAtmosphere;

public class AtmosphereRegister {
	private static final AtmosphereRegister instance = new AtmosphereRegister();
	
	private AtmosphereRegister() {
		atmosphereRegistration = new HashMap<String, IAtmosphere>();
		atmosphereList = new LinkedList<IAtmosphere>();
	}
	
	
	public static final AtmosphereRegister getInstance() {
		return instance;
	}
	
	Map<String, IAtmosphere> atmosphereRegistration;
	List<IAtmosphere> atmosphereList;
	
	/**
	 * Registers the atmosphere with the mod
	 * @param atmosphere atmosphere to register
	 * @param name name to register the atmosphere.  using the unlocalized name is HIGHLY recommended
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
	
	
	
	/**
	 * @return list of all registered atmospheres
	 */
	public List<IAtmosphere> getAtmosphereList() {
		return atmosphereList;
	}
}
