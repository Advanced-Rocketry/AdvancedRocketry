package zmaster587.advancedRocketry.cable;

import java.util.Map.Entry;

public class HandlerLiquidNetwork extends HandlerCableNetwork {

    @Override
    public int getNewNetworkID() {
        LiquidNetwork net = LiquidNetwork.initNetwork();

        networks.put(net.networkID, net);

        return net.networkID;
    }


    public void tickAllNetworks() {
        for (Entry<Integer, CableNetwork> integerCableNetworkEntry : networks.entrySet()) {
            integerCableNetworkEntry.getValue().tick();
        }
    }
}
