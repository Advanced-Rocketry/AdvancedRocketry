package zmaster587.advancedRocketry.network;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Logger;

import zmaster587.advancedRocketry.dimension.DimensionManager;
import zmaster587.advancedRocketry.dimension.DimensionProperties;
import zmaster587.libVulpes.network.BasePacket;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.PacketBuffer;

public class PacketDimInfo extends BasePacket {

	DimensionProperties dimProperties;
	NBTTagCompound dimNBT;
	int dimNumber;
	boolean deleteDim;
	List<ItemStack> artifacts;

	public PacketDimInfo() {
		artifacts = new LinkedList<ItemStack>();
	}

	public PacketDimInfo(int dimNumber,DimensionProperties dimProperties) {
		this();
		this.dimProperties = dimProperties;
		this.dimNumber = dimNumber;
	}

	@Override
	public void write(ByteBuf out) {
		NBTTagCompound nbt = new NBTTagCompound();
		out.writeInt(dimNumber);
		boolean flag = dimProperties == null;
		
		if(!flag) {
			
			//Try to send the nbt data of the dimension to the client, if it fails(probably due to non existent Biome ids) then remove the dimension
			PacketBuffer packetBuffer = new PacketBuffer(out);
			try {
				dimProperties.writeToNBT(nbt);
				out.writeBoolean(false);
				packetBuffer.writeNBTTagCompoundToBuffer(nbt);
				
				out.writeShort(dimProperties.getRequiredArtifacts().size());
				for(ItemStack i : dimProperties.getRequiredArtifacts()) {
					NBTTagCompound nbt2 = new NBTTagCompound(); 
					i.writeToNBT(nbt2);
					packetBuffer.writeNBTTagCompoundToBuffer(nbt2);
				}
				
			} catch(NullPointerException e) {
				out.writeBoolean(true);
				e.printStackTrace();
				Logger.getLogger("advancedRocketry").warning("Dimension " + dimNumber + " has thrown an exception trying to write NBT, deleting!");
				DimensionManager.getInstance().deleteDimension(dimNumber);
			}
			
			if(!dimProperties.customIcon.isEmpty())
			{
				packetBuffer.writeShort(dimProperties.customIcon.length());
				packetBuffer.writeString(dimProperties.customIcon);
			}
			else
				packetBuffer.writeShort(0);

		}
		else
			out.writeBoolean(flag);

	}

	@Override
	public void readClient(ByteBuf in) {
		PacketBuffer packetBuffer = new PacketBuffer(in);
		NBTTagCompound nbt;
		dimNumber = in.readInt();
		

		deleteDim = in.readBoolean();
		
		if(!deleteDim) {
			//TODO: error handling
			try {
				dimNBT = nbt = packetBuffer.readNBTTagCompoundFromBuffer();

				int number = packetBuffer.readShort();
				for(int i = 0; i < number; i++) {
					NBTTagCompound nbt2 = packetBuffer.readNBTTagCompoundFromBuffer();
					artifacts.add(ItemStack.loadItemStackFromNBT(nbt2));
				}
				
			} catch (IOException e) {
				e.printStackTrace();
				return;
			}
			dimProperties = new DimensionProperties(dimNumber);
			dimProperties.readFromNBT(nbt);
			
			short strLen = packetBuffer.readShort();
			if(strLen > 0)
			{
				dimProperties.customIcon = packetBuffer.readStringFromBuffer(strLen);
			}
		}
	}

	@Override
	public void read(ByteBuf in) {
		//Should never be read on the server!
	}

	@Override
	public void executeClient(EntityPlayer thePlayer) {
		if(deleteDim) {
			if(DimensionManager.getInstance().isDimensionCreated(dimNumber)) {
				DimensionManager.getInstance().deleteDimension(dimNumber);
			}
		}
		else if(dimProperties != null)
		{
			if(dimNumber == 0) {
				DimensionManager.overworldProperties = dimProperties;
			}
			else if( DimensionManager.getInstance().isDimensionCreated(dimNumber) ) {
				dimProperties.oreProperties = DimensionManager.getInstance().getDimensionProperties(dimNumber).oreProperties;
				dimProperties.getRequiredArtifacts().clear();
				dimProperties.getRequiredArtifacts().addAll(artifacts);
				dimProperties.customIcon = DimensionManager.getInstance().getDimensionProperties(dimNumber).customIcon;
				
				DimensionManager.getInstance().setDimProperties(dimNumber, dimProperties);
			} else {
				dimProperties = new DimensionProperties(dimNumber);
				dimProperties.readFromNBT(dimNBT);
				DimensionManager.getInstance().registerDimNoUpdate(dimProperties, true);
			}
		}
		
	}

	@Override
	public void executeServer(EntityPlayerMP player) {}

}
