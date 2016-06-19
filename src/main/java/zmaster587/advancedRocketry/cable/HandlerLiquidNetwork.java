package zmaster587.advancedRocketry.cable;

import java.util.Iterator;
import java.util.Random;
import java.util.Map.Entry;

public class HandlerLiquidNetwork extends HandlerCableNetwork {
	
	@Override
	public int getNewNetworkID() {
		LiquidNetwork net = LiquidNetwork.initNetwork();

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
