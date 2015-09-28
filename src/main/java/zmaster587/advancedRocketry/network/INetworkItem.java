package zmaster587.advancedRocketry.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import cpw.mods.fml.relauncher.Side;

public interface INetworkItem {
	
	//Writes data to the network given an id of what type of packet to write
	public void writeDataToNetwork(ByteBuf out, byte id, ItemStack stack);
	
	//Reads data, stores read data to nbt to be passed to useNetworkData
	public void readDataFromNetwork(ByteBuf in, byte packetId, NBTTagCompound nbt, ItemStack stack);
	
	//Applies changes from network
	public void useNetworkData(EntityPlayer player, Side side, byte id, NBTTagCompound nbt, ItemStack stack);
}
