package zmaster587.advancedRocketry.tile.cables;

import io.netty.buffer.ByteBuf;

import java.util.LinkedList;
import java.util.List;

import cpw.mods.fml.relauncher.Side;
import zmaster587.advancedRocketry.api.DataStorage;
import zmaster587.advancedRocketry.api.DataStorage.DataType;
import zmaster587.advancedRocketry.api.satellite.IDataHandler;
import zmaster587.advancedRocketry.cable.NetworkRegistry;
import zmaster587.advancedRocketry.inventory.TextureResources;
import zmaster587.advancedRocketry.world.util.MultiData;
import zmaster587.libVulpes.LibVulpes;
import zmaster587.libVulpes.block.RotatableBlock;
import zmaster587.libVulpes.interfaces.ILinkableTile;
import zmaster587.libVulpes.inventory.modules.IModularInventory;
import zmaster587.libVulpes.inventory.modules.IToggleButton;
import zmaster587.libVulpes.inventory.modules.ModuleBase;
import zmaster587.libVulpes.inventory.modules.ModuleToggleSwitch;
import zmaster587.libVulpes.items.ItemLinker;
import zmaster587.libVulpes.network.PacketHandler;
import zmaster587.libVulpes.network.PacketMachine;
import zmaster587.libVulpes.util.BlockPosition;
import zmaster587.libVulpes.util.INetworkMachine;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

public class TileWirelessTransciever extends TileEntity implements INetworkMachine, IModularInventory, ILinkableTile, IDataHandler, IToggleButton {


	boolean extractMode;
	boolean enabled;
	int networkID;
	MultiData data;
	ModuleToggleSwitch toggle;
	protected ModuleToggleSwitch toggleSwitch;

	public TileWirelessTransciever() {

		networkID = -1;
		data = new MultiData();
		data.setMaxData(100);
		toggle = new ModuleToggleSwitch(50, 50, 0, LibVulpes.proxy.getLocalizedString("msg.wirelessTransciever.extract"), this, TextureResources.buttonGeneric, 64, 18, false);
		toggleSwitch = new ModuleToggleSwitch(160, 5, 1, "", this,  zmaster587.libVulpes.inventory.TextureResources.buttonToggleImage, 11, 26, true);
	}


	@Override
	public boolean onLinkStart(ItemStack item, TileEntity entity, EntityPlayer player, World worldObj) {

		ItemLinker.setMasterCoords(item, this.xCoord, this.yCoord, this.zCoord);

		return true;
	}

	@Override
	public void onChunkUnload() {
		super.onChunkUnload();
		if(NetworkRegistry.dataNetwork.doesNetworkExist(networkID))
			NetworkRegistry.dataNetwork.getNetwork(networkID).removeFromAll(this);
	}

	@Override
	public boolean onLinkComplete(ItemStack item, TileEntity entity, EntityPlayer player, World worldObj) {
		BlockPosition pos = ItemLinker.getMasterCoords(item);

		TileEntity tile = worldObj.getTileEntity(pos.x, pos.y, pos.z);

		if(tile instanceof TileWirelessTransciever )
		{
			if(worldObj.isRemote)
				return true;

			int othernetworkid = ((TileWirelessTransciever)tile).networkID;

			if(networkID == -1 && othernetworkid == -1)
			{
				networkID = NetworkRegistry.dataNetwork.getNewNetworkID();
				((TileWirelessTransciever)tile).networkID = networkID;

			}
			else if(networkID == -1)
			{
				networkID = othernetworkid;
			}
			else if(othernetworkid == -1)
			{
				((TileWirelessTransciever)tile).networkID = networkID;
			}
			else
			{
				networkID = NetworkRegistry.dataNetwork.mergeNetworks(othernetworkid, networkID);
				((TileWirelessTransciever)tile).networkID = networkID;
			}
			addToNetwork();
			((TileWirelessTransciever)tile).addToNetwork();
			return true;
		}

		return false;
	}

	private void addToNetwork()
	{

		if(networkID == -1 || !enabled)
			return;
		else if(!NetworkRegistry.dataNetwork.doesNetworkExist(networkID))
			NetworkRegistry.dataNetwork.getNewNetworkID(networkID);

		if(extractMode)
		{
			NetworkRegistry.dataNetwork.getNetwork(networkID).addSource(this, ForgeDirection.UP);
		}
		else
		{
			NetworkRegistry.dataNetwork.getNetwork(networkID).addSink(this, ForgeDirection.UP);
		}
	}

	@Override
	public Packet getDescriptionPacket() {
		NBTTagCompound nbt = new NBTTagCompound();
		this.writeToNBT(nbt);
		return new S35PacketUpdateTileEntity(xCoord, yCoord, zCoord, 0, nbt);
	}
	
	@Override
	public void onDataPacket(NetworkManager net, S35PacketUpdateTileEntity pkt) {
		readFromNBT(pkt.func_148857_g());
	}
	

	public boolean canExtract(ForgeDirection dir, TileEntity e) {

		return e instanceof IDataHandler;
	}


	public boolean canInject(ForgeDirection dir, TileEntity e) {
		return e instanceof IDataHandler;
	}

	@Override
	public List<ModuleBase> getModules(int id, EntityPlayer player) {
		LinkedList list = new LinkedList<ModuleBase>();

		list.add(toggle);
		list.add(toggleSwitch);

		return list;
	}

	@Override
	public String getModularInventoryName() {
		return "tile.wirelessTransciever.name";
	}

	@Override
	public boolean canInteractWithContainer(EntityPlayer entity) {
		return true;
	}

	@Override
	public void writeDataToNetwork(ByteBuf out, byte id) {
		if(id == 0)
			out.writeBoolean(toggle.getState());
		else if(id == 1)
			out.writeBoolean(toggleSwitch.getState());
	}

	@Override
	public void readDataFromNetwork(ByteBuf in, byte packetId,
			NBTTagCompound nbt) {
		nbt.setBoolean("state", in.readBoolean());

	}

	@Override
	public void useNetworkData(EntityPlayer player, Side side, byte id,
			NBTTagCompound nbt) {

		if(side.isServer()) 
		{
			if(id == 0)
			{
				extractMode = nbt.getBoolean("state");
				if(NetworkRegistry.dataNetwork.doesNetworkExist(networkID))
				{
					NetworkRegistry.dataNetwork.getNetwork(networkID).removeFromAll(this);

					if(extractMode) 
						NetworkRegistry.dataNetwork.getNetwork(networkID).addSource(this, ForgeDirection.UP);
					else
						NetworkRegistry.dataNetwork.getNetwork(networkID).addSink(this, ForgeDirection.UP);
				}
			}
			else if(id == 1)
			{
				enabled = nbt.getBoolean("state");
				if(!enabled)
				{
					if(NetworkRegistry.dataNetwork.doesNetworkExist(networkID))
						NetworkRegistry.dataNetwork.getNetwork(networkID).removeFromAll(this);
				}
				else
					addToNetwork();
			}
		}
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt) {
		super.readFromNBT(nbt);

		extractMode = nbt.getBoolean("mode");
		enabled = nbt.getBoolean("enabled");
		networkID = nbt.getInteger("networkID");
		data.readFromNBT(nbt);
		addToNetwork();

		toggle.setToggleState(extractMode);
		toggleSwitch.setToggleState(enabled);
	}

	@Override
	public void writeToNBT(NBTTagCompound nbt) {
		super.writeToNBT(nbt);
		nbt.setBoolean("mode", extractMode);
		nbt.setInteger("networkID", networkID);
		data.writeToNBT(nbt);
	}

	@Override
	public int extractData(int maxAmount, DataType type, ForgeDirection dir,
			boolean commit) {
		return data.extractData(maxAmount, type, dir, commit);
	}

	@Override
	public int addData(int maxAmount, DataType type, ForgeDirection dir,
			boolean commit) {
		return data.addData(maxAmount, type, dir, commit);
	}


	@Override
	public boolean canUpdate() {
		return true;
	}
	
	@Override
	public void updateEntity() {

		int state = worldObj.getBlockMetadata(xCoord, yCoord, zCoord);
		Block block = worldObj.getBlock(xCoord, yCoord, zCoord);
		if (block instanceof RotatableBlock) {
			ForgeDirection facing = RotatableBlock.getFront(state).getOpposite();

			TileEntity tile = worldObj.getTileEntity(facing.offsetX + xCoord, facing.offsetY + yCoord, facing.offsetZ + zCoord);

			if( tile instanceof IDataHandler && !(tile instanceof TileWirelessTransciever))
			{
				for(DataType data : DataType.values())
				{

					if(data == DataStorage.DataType.UNDEFINED)
						continue;

					if(!extractMode) {
						int amt = ((IDataHandler)tile).addData(this.data.getDataAmount(data), data, facing.getOpposite(), true);
						this.data.extractData(amt, data, facing.getOpposite(), true);
					}
					else
					{
						int amt = ((IDataHandler)tile).extractData(this.data.getMaxData() - this.data.getDataAmount(data), data, facing.getOpposite(), true);
						this.data.addData(amt, data, facing.getOpposite(), true);
					}
				}
			}
		}
	}


	@Override
	public void onInventoryButtonPressed(int buttonId) {
		if(buttonId == 1)
			enabled = toggleSwitch.getState();
		else if(buttonId == 0)
			extractMode = toggle.getState();
		PacketHandler.sendToServer(new PacketMachine(this, (byte)buttonId));
	}


	@Override
	public void stateUpdated(ModuleBase module) {
		if(module == toggleSwitch)
			enabled = toggleSwitch.getState();
		else if(module == toggle)
			extractMode = toggle.getState();

		if(!worldObj.isRemote) {
			this.markDirty();
			worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
		}
	}

}
