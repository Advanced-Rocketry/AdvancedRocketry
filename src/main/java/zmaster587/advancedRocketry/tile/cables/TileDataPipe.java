package zmaster587.advancedRocketry.tile.cables;

import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.IFluidHandler;
import zmaster587.advancedRocketry.api.satellite.IDataHandler;
import zmaster587.advancedRocketry.cable.HandlerCableNetwork;
import zmaster587.advancedRocketry.cable.NetworkRegistry;

public class TileDataPipe extends TilePipe {
	
	@Override
	public boolean canExtract(ForgeDirection dir, TileEntity e) {
		
		return e instanceof IDataHandler;
	}

	@Override
	public boolean canInject(ForgeDirection dir, TileEntity e) {
		return e instanceof IDataHandler;
	}
	
	public HandlerCableNetwork getNetworkHandler() {
		return NetworkRegistry.dataNetwork;
	}
}
