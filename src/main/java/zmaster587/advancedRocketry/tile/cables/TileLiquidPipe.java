package zmaster587.advancedRocketry.tile.cables;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import zmaster587.advancedRocketry.api.AdvancedRocketryTileEntityType;
import zmaster587.advancedRocketry.cable.HandlerCableNetwork;
import zmaster587.advancedRocketry.cable.NetworkRegistry;


public class TileLiquidPipe extends TilePipe {

	public TileLiquidPipe() {
		super(AdvancedRocketryTileEntityType.TILE_FLUID_PIPE);
	}
	
	@Override
	public boolean canExtract(Direction dir, TileEntity e) {
		return e.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, dir).isPresent();
	}

	@Override
	public boolean canInject(Direction dir, TileEntity e) {
		return e.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, dir).isPresent();
	}
	
	public HandlerCableNetwork getNetworkHandler() {
		return NetworkRegistry.liquidNetwork;
	}
	
}
