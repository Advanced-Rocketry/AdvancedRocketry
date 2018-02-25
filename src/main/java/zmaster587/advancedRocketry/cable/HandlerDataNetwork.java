package zmaster587.advancedRocketry.cable;

import java.util.Iterator;
import java.util.Map.Entry;

public class HandlerDataNetwork extends HandlerCableNetwork {
	@Override
	public int getNewNetworkID() {
		DataNetwork net = DataNetwork.initNetwork();

		networks.put(net.networkID, net);

		return net.networkID;
	}
	
	public int getNewNetworkID(int id) {
		DataNetwork net = new DataNetwork();

		net.networkID = id;
		networks.put(net.networkID, net);

		return net.networkID;
	}


	public void tickAllNetworks() {
		Iterator<Entry<Integer, CableNetwork>> iter = networks.entrySet().iterator();
		while(iter.hasNext()) {
			iter.next().getValue().tick();
		}
	}
}
