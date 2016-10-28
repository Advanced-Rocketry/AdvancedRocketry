package zmaster587.advancedRocketry.tile.infrastructure;

import io.netty.buffer.ByteBuf;

import java.util.List;

import cpw.mods.fml.relauncher.Side;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ChatComponentText;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTankInfo;
import net.minecraftforge.fluids.IFluidHandler;
import zmaster587.advancedRocketry.api.AdvancedRocketryBlocks;
import zmaster587.advancedRocketry.api.EntityRocketBase;
import zmaster587.advancedRocketry.api.IInfrastructure;
import zmaster587.advancedRocketry.entity.EntityRocket;
import zmaster587.advancedRocketry.api.IMission;
import zmaster587.advancedRocketry.tile.TileGuidanceComputer;
import zmaster587.advancedRocketry.tile.TileRocketBuilder;
import zmaster587.libVulpes.block.multiblock.BlockHatch;
import zmaster587.libVulpes.inventory.modules.IButtonInventory;
import zmaster587.libVulpes.inventory.modules.ModuleBase;
import zmaster587.libVulpes.inventory.modules.ModuleRedstoneOutputButton;
import zmaster587.libVulpes.items.ItemLinker;
import zmaster587.libVulpes.network.PacketHandler;
import zmaster587.libVulpes.network.PacketMachine;
import zmaster587.libVulpes.tile.multiblock.hatch.TileFluidHatch;
import zmaster587.libVulpes.util.INetworkMachine;
import zmaster587.libVulpes.util.ZUtils.RedstoneState;

public class TileRocketFluidUnloader extends TileFluidHatch implements IInfrastructure, IButtonInventory, INetworkMachine {

	EntityRocket rocket;
	ModuleRedstoneOutputButton redstoneControl;
	RedstoneState state;

	public TileRocketFluidUnloader() {
		super();
		redstoneControl = new ModuleRedstoneOutputButton(174, 4, 0, "", this);
		state = RedstoneState.ON;
	}

	public TileRocketFluidUnloader(int size) {
		super(size);

		redstoneControl = new ModuleRedstoneOutputButton(174, 4, 0, "", this);
		state = RedstoneState.ON;
	}

	@Override
	public List<ModuleBase> getModules(int ID, EntityPlayer player) {
		List<ModuleBase> list = super.getModules(ID, player);
		list.add(redstoneControl);
		return list;
	}
	
	@Override
	public void invalidate() {
		super.invalidate();
		if(getMasterBlock() instanceof TileRocketBuilder)
			((TileRocketBuilder)getMasterBlock()).removeConnectedInfrastructure(this);
	}

	@Override
	public String getModularInventoryName() {
		return "tile.loader.3.name";
	}

	@Override
	public void updateEntity() {
		super.updateEntity();

		//Move a stack of items
		if(rocket != null ) {
			List<TileEntity> tiles = rocket.storage.getFluidTiles();
			boolean rocketContainsItems = false;
			//Function returns if something can be moved
			for(TileEntity tile : tiles) {
				IFluidHandler handler = (IFluidHandler)tile;

				if(handler.drain(ForgeDirection.DOWN, 1, false) != null)
					rocketContainsItems = true;
				
				FluidStack stack = fluidTank.getFluid();
				if(stack == null) {
					this.fill(ForgeDirection.UNKNOWN, handler.drain(ForgeDirection.DOWN, fluidTank.getCapacity(), true), true);
				}
				else {
					stack = stack.copy();
					stack.amount = fluidTank.getCapacity() - fluidTank.getFluidAmount();
					
					if(stack.amount != 0) {
						this.fill(ForgeDirection.UNKNOWN, handler.drain(ForgeDirection.DOWN, stack, true), true);
					}
				}
			}

			//Update redstone state
			setRedstoneState(!rocketContainsItems);

		}
	}


	private void setRedstoneState(boolean condition) {
		if(state == RedstoneState.INVERTED)
			condition = !condition;
		else if(state == RedstoneState.OFF)
			condition = false;
		((BlockHatch)AdvancedRocketryBlocks.blockLoader).setRedstoneState(worldObj, xCoord, yCoord, zCoord, condition);

	}
	
	@Override
	public boolean onLinkStart(ItemStack item, TileEntity entity,
			EntityPlayer player, World world) {

		ItemLinker.setMasterCoords(item, this.xCoord, this.yCoord, this.zCoord);

		if(this.rocket != null) {
			this.rocket.unlinkInfrastructure(this);
			this.unlinkRocket();
		}

		if(player.worldObj.isRemote)
			Minecraft.getMinecraft().ingameGUI.getChatGUI().printChatMessage((new ChatComponentText("You program the linker with the fluid unloader at: " + this.xCoord + " " + this.yCoord + " " + this.zCoord)));
		return true;
	}

	@Override
	public boolean onLinkComplete(ItemStack item, TileEntity entity,
			EntityPlayer player, World world) {
		if(player.worldObj.isRemote)
			Minecraft.getMinecraft().ingameGUI.getChatGUI().printChatMessage((new ChatComponentText("This must be the first machine to link!")));
		return false;
	}

	@Override
	public void unlinkRocket() {
		rocket = null;
		((BlockHatch)AdvancedRocketryBlocks.blockLoader).setRedstoneState(worldObj, xCoord, yCoord, zCoord, false);
		//On unlink prevent the tile from ticking anymore

		//if(!worldObj.isRemote)
		//worldObj.loadedTileEntityList.remove(this);
	}

	@Override
	public boolean disconnectOnLiftOff() {
		return true;
	}

	@Override
	public boolean linkRocket(EntityRocketBase rocket) {
		//On linked allow the tile to tick
		//if(!worldObj.isRemote)
		//worldObj.loadedTileEntityList.add(this);
		this.rocket = (EntityRocket) rocket;
		return true;
	}

	@Override
	public boolean canUpdate() {
		return true;
	}

	@Override
	public Packet getDescriptionPacket() {
		NBTTagCompound nbt = new NBTTagCompound();
		nbt.setByte("state", (byte)state.ordinal());
		return new S35PacketUpdateTileEntity(xCoord, yCoord, zCoord, 0, nbt);
	}

	@Override
	public void onDataPacket(NetworkManager net, S35PacketUpdateTileEntity pkt) {
		state = RedstoneState.values()[pkt.func_148857_g().getByte("state")];
		redstoneControl.setRedstoneState(state);
		super.onDataPacket(net, pkt);
	}
	
	@Override
	public boolean linkMission(IMission misson) {
		return false;
	}

	@Override
	public void unlinkMission() {

	}

	@Override
	public int getMaxLinkDistance() {
		return 32;
	}

	public boolean canRenderConnection() {
		return true;
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt) {
		super.readFromNBT(nbt);

		state = RedstoneState.values()[nbt.getByte("redstoneState")];
		redstoneControl.setRedstoneState(state);
	}

	@Override
	public void writeToNBT(NBTTagCompound nbt) {
		super.writeToNBT(nbt);
		nbt.setByte("redstoneState", (byte) state.ordinal());
	}

	@Override
	public void onInventoryButtonPressed(int buttonId) {
		state = redstoneControl.getState();
		PacketHandler.sendToServer(new PacketMachine(this, (byte)0));
	}

	@Override
	public void writeDataToNetwork(ByteBuf out, byte id) {
		out.writeByte(state.ordinal());
	}

	@Override
	public void readDataFromNetwork(ByteBuf in, byte packetId,
			NBTTagCompound nbt) {
		nbt.setByte("state", in.readByte());
	}

	@Override
	public void useNetworkData(EntityPlayer player, Side side, byte id,
			NBTTagCompound nbt) {
		state = RedstoneState.values()[nbt.getByte("state")];

		if(rocket == null)
			setRedstoneState(state == RedstoneState.INVERTED);
	}
}
