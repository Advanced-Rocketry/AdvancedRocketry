package zmaster587.advancedRocketry.cable;

public class NetworkRegistry {
	
	public static HandlerLiquidNetwork liquidNetwork;
	public static HandlerDataNetwork dataNetwork;
	public static HandlerEnergyNetwork energyNetwork;
	
	public static void registerLiquid() {
		liquidNetwork = new HandlerLiquidNetwork();
	}

	public static void registerEnergy() {
		energyNetwork = new HandlerEnergyNetwork();
	}

	public static void registerData() {
		dataNetwork = new HandlerDataNetwork();
	}
	
	public static void clearNetwork(HandlerCableNetwork network) {
		network.networks.clear();
	}
}
