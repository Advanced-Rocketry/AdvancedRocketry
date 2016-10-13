package zmaster587.advancedRocketry.cable;

public class NetworkRegistry {
	
	public static HandlerLiquidNetwork liquidNetwork;
	public static HandlerDataNetwork dataNetwork;
	public static HandlerEnergyNetwork energyNetwork;
	
	public static void registerFluidNetwork() {
		liquidNetwork = new HandlerLiquidNetwork();
		energyNetwork = new HandlerEnergyNetwork();
		dataNetwork = new HandlerDataNetwork();
	}
	
	public static void clearNetworks() {
		energyNetwork.networks.clear();
		dataNetwork.networks.clear();
		liquidNetwork.networks.clear();
	}
}
