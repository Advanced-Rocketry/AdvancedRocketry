package zmaster587.advancedRocketry.tile.cables;

import zmaster587.advancedRocketry.cable.HandlerCableNetwork;
import zmaster587.advancedRocketry.cable.NetworkRegistry;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidTankInfo;
import net.minecraftforge.fluids.IFluidHandler;

public class TileLiquidPipe extends TilePipe {

	@Override
	public boolean canExtract(ForgeDirection dir, TileEntity e) {
		return e instanceof IFluidHandler;
	}

	@Override
	public boolean canInject(ForgeDirection dir, TileEntity e) {
		return  e instanceof IFluidHandler;
	}
	
	public HandlerCableNetwork getNetworkHandler() {
		return NetworkRegistry.liquidNetwork;
	}
	
}
