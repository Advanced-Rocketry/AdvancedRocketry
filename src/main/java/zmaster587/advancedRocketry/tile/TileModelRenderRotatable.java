package zmaster587.advancedRocketry.tile;

import zmaster587.advancedRocketry.block.BlockRotatableModel;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraftforge.common.util.ForgeDirection;

public class TileModelRenderRotatable extends TileModelRender {
	
	public ForgeDirection rotation;
	
	public TileModelRenderRotatable() {
		super();
		rotation = ForgeDirection.DOWN;
	}
	
	public TileModelRenderRotatable(int type, ForgeDirection rotation) {
		super(type);
		this.rotation = rotation;
	}
	
	@Override
	public void onDataPacket(NetworkManager net, S35PacketUpdateTileEntity pkt) {
		super.onDataPacket(net, pkt);
		this.rotation = BlockRotatableModel.getFront(this.getBlockMetadata());
	}
	
	@Override
	public ForgeDirection getRotation() {
		return rotation;
	}
	
	@Override
	public void writeToNBT(NBTTagCompound nbt) {
		super.writeToNBT(nbt);
		nbt.setInteger("dir", rotation.ordinal());
	}
	
	@Override
	public void readFromNBT(NBTTagCompound nbt) {
		super.readFromNBT(nbt);
		
		rotation = ForgeDirection.values()[nbt.getInteger("dir")];
		
	}
}
