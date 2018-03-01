package zmaster587.advancedRocketry.tile.cables;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import zmaster587.advancedRocketry.cable.HandlerCableNetwork;
import zmaster587.advancedRocketry.cable.NetworkRegistry;


public class TileLiquidPipe extends TilePipe {

	@Override
	public boolean canExtract(EnumFacing dir, TileEntity e) {
		return e.hasCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, dir);
	}

	@Override
	public boolean canInject(EnumFacing dir, TileEntity e) {
		return e.hasCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, dir);
	}
	
	public HandlerCableNetwork getNetworkHandler() {
		return NetworkRegistry.liquidNetwork;
	}
	
}
