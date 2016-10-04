package zmaster587.advancedRocketry.cable;

import java.util.Iterator;
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
		Iterator<Entry<Integer, CableNetwork>> iter = networks.entrySet().iterator();
		while(iter.hasNext()) {
			iter.next().getValue().tick();
		}
	}
}
