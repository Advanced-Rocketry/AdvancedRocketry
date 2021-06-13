package zmaster587.advancedRocketry.cable;

import java.util.Map.Entry;

public class HandlerEnergyNetwork extends HandlerCableNetwork {
	@Override
	public int getNewNetworkID() {
		EnergyNetwork net = EnergyNetwork.initNetwork();

		networks.put(net.networkID, net);

		return net.networkID;
	}


	@Override
	public void tickAllNetworks() {
		for (Entry<Integer, CableNetwork> integerCableNetworkEntry : networks.entrySet()) {
			integerCableNetworkEntry.getValue().tick();
		}
	}
}
