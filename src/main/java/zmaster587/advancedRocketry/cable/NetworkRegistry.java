package zmaster587.advancedRocketry.cable;

public class NetworkRegistry {
	
	public static HandlerLiquidNetwork liquidNetwork;
	public static HandlerDataNetwork dataNetwork;
	public static HandlerCableNetwork energyNetwork;
	
	public static void registerFluidNetwork() {
		liquidNetwork = new HandlerLiquidNetwork();
		dataNetwork = new HandlerDataNetwork();
		energyNetwork = new HandlerEnergyNetwork();
	}
	
	public static void clearNetworks() {
		energyNetwork.networks.clear();
		dataNetwork.networks.clear();
		liquidNetwork.networks.clear();
	}
}
