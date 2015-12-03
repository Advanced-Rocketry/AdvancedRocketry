package zmaster587.advancedRocketry.tile;

import zmaster587.advancedRocketry.api.AdvancedRocketryBlocks;
import zmaster587.advancedRocketry.api.material.MaterialRegistry;
import zmaster587.libVulpes.tile.TilePointer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;

public class TileMaterial extends TilePointer {

	MaterialRegistry.Materials materialType;

	public TileMaterial() {
		super();
	}

	@Override
	public boolean canUpdate() {
		return false;
	}

	public MaterialRegistry.Materials getMaterial() {
		return materialType;
	}

	public void setMaterial(MaterialRegistry.Materials material) {
		materialType = material;
	}

	@Override
	public Packet getDescriptionPacket() {
		NBTTagCompound nbt = new NBTTagCompound();

		nbt.setInteger("material", materialType.ordinal());

		return new S35PacketUpdateTileEntity(xCoord, yCoord, zCoord, worldObj.provider.dimensionId, nbt);
	}

	@Override
	public void onDataPacket(NetworkManager net, S35PacketUpdateTileEntity pkt) {
		materialType = MaterialRegistry.Materials.values()[pkt.func_148857_g().getInteger("material")];
	}

	@Override
	public void writeToNBT(NBTTagCompound nbt) {
		super.writeToNBT(nbt);
		if(materialType != null)
			nbt.setInteger("material", materialType.ordinal());
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt) {
		super.readFromNBT(nbt);
		if(nbt.hasKey("material"))
			materialType = MaterialRegistry.Materials.values()[nbt.getInteger("material")];
	}
}
