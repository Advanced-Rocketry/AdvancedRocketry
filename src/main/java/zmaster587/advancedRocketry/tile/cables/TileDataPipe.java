package zmaster587.advancedRocketry.tile.cables;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.fluids.IFluidHandler;
import zmaster587.advancedRocketry.api.satellite.IDataHandler;
import zmaster587.advancedRocketry.cable.HandlerCableNetwork;
import zmaster587.advancedRocketry.cable.NetworkRegistry;

public class TileDataPipe extends TilePipe {
	
	@Override
	public boolean canExtract(EnumFacing dir, TileEntity e) {
		
		return e instanceof IDataHandler;
	}

	@Override
	public boolean canInject(EnumFacing dir, TileEntity e) {
		return e instanceof IDataHandler;
	}
	
	public HandlerCableNetwork getNetworkHandler() {
		return NetworkRegistry.dataNetwork;
	}
}
