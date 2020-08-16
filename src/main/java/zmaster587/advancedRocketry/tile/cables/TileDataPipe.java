package zmaster587.advancedRocketry.tile.cables;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import zmaster587.advancedRocketry.api.AdvancedRocketryTileEntityType;
import zmaster587.advancedRocketry.api.satellite.IDataHandler;
import zmaster587.advancedRocketry.cable.HandlerCableNetwork;
import zmaster587.advancedRocketry.cable.NetworkRegistry;

public class TileDataPipe extends TilePipe {
	
	public TileDataPipe() {
		super(AdvancedRocketryTileEntityType.TILE_DATA_PIPE);
	}

	@Override
	public boolean canExtract(Direction dir, TileEntity e) {
		
		return e instanceof IDataHandler;
	}

	@Override
	public boolean canInject(Direction dir, TileEntity e) {
		return e instanceof IDataHandler;
	}
	
	public HandlerCableNetwork getNetworkHandler() {
		return NetworkRegistry.dataNetwork;
	}
}
