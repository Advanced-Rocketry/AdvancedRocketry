package zmaster587.advancedRocketry.tile.cables;

import zmaster587.advancedRocketry.cable.HandlerCableNetwork;
import zmaster587.advancedRocketry.cable.NetworkRegistry;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidTankInfo;
import net.minecraftforge.fluids.IFluidHandler;


public class TileLiquidPipe extends TilePipe {

	@Override
	public boolean canExtract(EnumFacing dir, TileEntity e) {
		
		return e instanceof IFluidHandler;
	}

	@Override
	public boolean canInject(EnumFacing dir, TileEntity e) {
		return  e instanceof IFluidHandler;
	}
	
	public HandlerCableNetwork getNetworkHandler() {
		return NetworkRegistry.liquidNetwork;
	}
	
}
