package zmaster587.advancedRocketry.tile;

import zmaster587.advancedRocketry.block.BlockRotatableModel;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraftforge.common.util.ForgeDirection;

public class TileModelRenderRotatable extends TileModelRender {
	
	ForgeDirection rotation;
	
	public TileModelRenderRotatable() {
		super();
		rotation = ForgeDirection.SOUTH;
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
}
