package zmaster587.advancedRocketry.tile.cables;

import io.netty.buffer.ByteBuf;

import java.util.LinkedList;
import java.util.List;

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
import zmaster587.libVulpes.util.INetworkMachine;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;

public class TileWirelessTransciever extends TileEntity implements INetworkMachine, IModularInventory, ILinkableTile, IDataHandler, ITickable, IToggleButton {


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
	public boolean onLinkStart(ItemStack item, TileEntity entity, EntityPlayer player, World world) {

		ItemLinker.setMasterCoords(item, getPos());
		
		if(!world.isRemote)
			player.sendMessage(new TextComponentString(LibVulpes.proxy.getLocalizedString("msg.linker.program")));

		return true;
	}

	@Override
	public void onChunkUnload() {
		super.onChunkUnload();
		if(NetworkRegistry.dataNetwork.doesNetworkExist(networkID))
			NetworkRegistry.dataNetwork.getNetwork(networkID).removeFromAll(this);
	}

	@Override
	public boolean onLinkComplete(ItemStack item, TileEntity entity, EntityPlayer player, World world) {
		BlockPos pos = ItemLinker.getMasterCoords(item);

		TileEntity tile = world.getTileEntity(pos);

		if(tile instanceof TileWirelessTransciever )
		{
			if(world.isRemote)
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
			
			player.sendMessage(new TextComponentString(LibVulpes.proxy.getLocalizedString("msg.linker.success")));
			
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
			NetworkRegistry.dataNetwork.getNetwork(networkID).addSource(this, EnumFacing.UP);
		}
		else
		{
			NetworkRegistry.dataNetwork.getNetwork(networkID).addSink(this, EnumFacing.UP);
		}
	}

	@Override
	public SPacketUpdateTileEntity getUpdatePacket() {
		NBTTagCompound nbt = new NBTTagCompound();
		this.writeToNBT(nbt);

		return new SPacketUpdateTileEntity(this.pos, 0, nbt);
	}

	@Override 
	public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity pkt) {
		this.readFromNBT(pkt.getNbtCompound());
	}

	@Override
	public NBTTagCompound getUpdateTag() {
		return writeToNBT(new NBTTagCompound());
	}

	public boolean canExtract(EnumFacing dir, TileEntity e) {

		return e instanceof IDataHandler;
	}


	public boolean canInject(EnumFacing dir, TileEntity e) {
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
						NetworkRegistry.dataNetwork.getNetwork(networkID).addSource(this, EnumFacing.UP);
					else
						NetworkRegistry.dataNetwork.getNetwork(networkID).addSink(this, EnumFacing.UP);
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
	public NBTTagCompound writeToNBT(NBTTagCompound nbt) {
		nbt.setBoolean("mode", extractMode);
		nbt.setInteger("networkID", networkID);
		data.writeToNBT(nbt);
		return super.writeToNBT(nbt);
	}

	@Override
	public int extractData(int maxAmount, DataType type, EnumFacing dir,
			boolean commit) {
		return data.extractData(maxAmount, type, dir, commit);
	}

	@Override
	public int addData(int maxAmount, DataType type, EnumFacing dir,
			boolean commit) {
		return data.addData(maxAmount, type, dir, commit);
	}


	@Override
	public void update() {

		IBlockState state = world.getBlockState(getPos());
		if (state.getBlock() instanceof RotatableBlock) {
			EnumFacing facing = RotatableBlock.getFront(state).getOpposite();

			TileEntity tile = world.getTileEntity(getPos().add(facing.getFrontOffsetX(),facing.getFrontOffsetY(),facing.getFrontOffsetZ()));

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

		if(!world.isRemote) {
			this.markDirty();
			world.notifyBlockUpdate(pos, world.getBlockState(pos),  world.getBlockState(pos), 3);
		}
	}

}
