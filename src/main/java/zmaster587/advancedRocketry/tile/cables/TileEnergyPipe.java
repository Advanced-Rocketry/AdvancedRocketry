package zmaster587.advancedRocketry.tile.cables;

import cofh.api.energy.IEnergyHandler;
import cofh.api.energy.IEnergyProvider;
import cofh.api.energy.IEnergyReceiver;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.ForgeDirection;
import zmaster587.advancedRocketry.cable.HandlerCableNetwork;
import zmaster587.advancedRocketry.cable.NetworkRegistry;

public class TileEnergyPipe extends TilePipe {
	
	@Override
	public boolean canExtract(ForgeDirection dir, TileEntity e) {
		
		return e instanceof IEnergyProvider;
	}

	@Override
	public boolean canInject(ForgeDirection dir, TileEntity e) {
		return e instanceof IEnergyReceiver;
	}
	
	public HandlerCableNetwork getNetworkHandler() {
		return NetworkRegistry.energyNetwork;
	}
}
