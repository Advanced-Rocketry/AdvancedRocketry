package zmaster587.advancedRocketry.cable;

public class NetworkRegistry {
	
	public static HandlerLiquidNetwork liquidNetwork;
	public static HandlerDataNetwork dataNetwork;
	
	public static void registerFluidNetwork() {
		//liquidNetwork = new HandlerLiquidNetwork();
		dataNetwork = new HandlerDataNetwork();
	}
}
