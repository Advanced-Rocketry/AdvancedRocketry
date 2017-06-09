package zmaster587.advancedRocketry.tile.infrastructure;

import io.netty.buffer.ByteBuf;

import java.util.List;

import cpw.mods.fml.relauncher.Side;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.IFluidHandler;
import zmaster587.advancedRocketry.api.AdvancedRocketryBlocks;
import zmaster587.advancedRocketry.api.EntityRocketBase;
import zmaster587.advancedRocketry.api.IInfrastructure;
import zmaster587.advancedRocketry.entity.EntityRocket;
import zmaster587.advancedRocketry.api.IMission;
import zmaster587.advancedRocketry.block.multiblock.BlockARHatch;
import zmaster587.advancedRocketry.tile.TileRocketBuilder;
import zmaster587.libVulpes.block.multiblock.BlockHatch;
import zmaster587.libVulpes.inventory.modules.IButtonInventory;
import zmaster587.libVulpes.inventory.modules.IGuiCallback;
import zmaster587.libVulpes.inventory.modules.ModuleBase;
import zmaster587.libVulpes.inventory.modules.ModuleBlockSideSelector;
import zmaster587.libVulpes.inventory.modules.ModuleRedstoneOutputButton;
import zmaster587.libVulpes.items.ItemLinker;
import zmaster587.libVulpes.network.PacketHandler;
import zmaster587.libVulpes.network.PacketMachine;
import zmaster587.libVulpes.tile.multiblock.hatch.TileFluidHatch;
import zmaster587.libVulpes.util.INetworkMachine;
import zmaster587.libVulpes.util.ZUtils.RedstoneState;

public class TileRocketFluidLoader extends TileFluidHatch  implements IInfrastructure, IButtonInventory, INetworkMachine, IGuiCallback {

	EntityRocket rocket;
	ModuleRedstoneOutputButton redstoneControl;
	RedstoneState state;
	ModuleRedstoneOutputButton inputRedstoneControl;
	RedstoneState inputstate;
	ModuleBlockSideSelector sideSelectorModule;
	
	private static int ALLOW_REDSTONEOUT = 2;

	public TileRocketFluidLoader() {
		super();
		redstoneControl = new ModuleRedstoneOutputButton(174, 4, 0, "", this, "Loading State: ");
		state = RedstoneState.ON;
		inputRedstoneControl = new ModuleRedstoneOutputButton(174, 32, 1, "", this, "Allow Loading: ");
		inputstate = RedstoneState.OFF;
		inputRedstoneControl.setRedstoneState(inputstate);
		sideSelectorModule = new ModuleBlockSideSelector(90, 15, this, new String[] {"None", "Allow redstone output", "Allow redstone input"});
	}

	public TileRocketFluidLoader(int size) {
		super(size);
		redstoneControl = new ModuleRedstoneOutputButton(174, 4, 0, "", this, "Loading State: ");
		state = RedstoneState.ON;
		inputRedstoneControl = new ModuleRedstoneOutputButton(174, 32, 1, "", this, "Allow Loading: ");
		inputstate = RedstoneState.OFF;
		inputRedstoneControl.setRedstoneState(inputstate);
		sideSelectorModule = new ModuleBlockSideSelector(90, 15, this, new String[] {"None", "Allow redstone output", "Allow redstone input"});
	}
	
	@Override
	public List<ModuleBase> getModules(int ID, EntityPlayer player) {
		List<ModuleBase> list = super.getModules(ID, player);
		list.add(redstoneControl);
		list.add(inputRedstoneControl);
		list.add(sideSelectorModule);
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
		return "tile.loader.5.name";
	}

	protected void setRedstoneState(boolean condition) {
		condition = isStateActive(state, condition);
		((BlockARHatch)AdvancedRocketryBlocks.blockLoader).setRedstoneState(worldObj, xCoord,yCoord,zCoord, condition);

	}
	
	protected boolean isStateActive(RedstoneState state, boolean condition) {
		if(state == RedstoneState.INVERTED)
			return !condition;
		else if(state == RedstoneState.OFF)
			return false;
		return condition;
	}

	protected boolean getStrongPowerForSides(World world, int x, int y , int z) {
		for(int i = 0; i < 6; i++) {
			ForgeDirection dir = ForgeDirection.getOrientation(i);
			if(sideSelectorModule.getStateForSide(i) == ALLOW_REDSTONEOUT && world.getIndirectPowerLevelTo(x + dir.offsetX, y + dir.offsetY, z + dir.offsetZ, i) > 0)
				return true;
		}
		return false;
	}
	
	@Override
	public void updateEntity() {
		super.updateEntity();

		//Move a stack of items
		if(rocket != null ) {
			boolean isAllowedToOperate = (inputstate == RedstoneState.OFF || isStateActive(inputstate, getStrongPowerForSides(worldObj, xCoord,yCoord,zCoord)));
			List<TileEntity> tiles = rocket.storage.getFluidTiles();
			boolean rocketContainsItems = false;

			//Function returns if something can be moved
			for(TileEntity tile : tiles) {
				IFluidHandler handler = (IFluidHandler)tile;

				//See if we have anything to fill because redstone output
				FluidStack stack = handler.drain(ForgeDirection.DOWN, 1, false);
				if(stack == null || handler.fill(ForgeDirection.UP, stack, false) > 0)
					rocketContainsItems = true;

				if(isAllowedToOperate) {
				stack = fluidTank.drain(fluidTank.getCapacity(), false);
				if(stack != null && stack.amount > 0)
					fluidTank.drain(handler.fill(ForgeDirection.UP, stack, true), true);
				}
			}

			//Update redstone state
			setRedstoneState(!rocketContainsItems);
		}
	}

	@Override
	public Packet getDescriptionPacket() {
		NBTTagCompound nbt = new NBTTagCompound();
		nbt.setByte("state", (byte)state.ordinal());
		nbt.setByte("inputRedstoneState", (byte) inputstate.ordinal());
		sideSelectorModule.writeToNBT(nbt);
		return new S35PacketUpdateTileEntity(xCoord, yCoord, zCoord, 0, nbt);
	}

	@Override
	public void onDataPacket(NetworkManager net, S35PacketUpdateTileEntity pkt) {
		NBTTagCompound nbt = pkt.func_148857_g();
		state = RedstoneState.values()[nbt.getByte("state")];
		redstoneControl.setRedstoneState(state);
		
		inputstate = RedstoneState.values()[nbt.getByte("inputRedstoneState")];
		inputRedstoneControl.setRedstoneState(inputstate);

		sideSelectorModule.readFromNBT(nbt);
		super.onDataPacket(net, pkt);
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
			Minecraft.getMinecraft().ingameGUI.getChatGUI().printChatMessage((new ChatComponentText("You program the linker with the fluid loader at: " + this.xCoord + " " + this.yCoord + " " + this.zCoord)));
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
		((BlockARHatch)AdvancedRocketryBlocks.blockLoader).setRedstoneState(worldObj, xCoord, yCoord, zCoord, false);
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
	public boolean allowRedstoneOutputOnSide(ForgeDirection facing) {
		return sideSelectorModule.getStateForSide(facing.getOpposite()) == 1;
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt) {
		super.readFromNBT(nbt);

		state = RedstoneState.values()[nbt.getByte("redstoneState")];
		redstoneControl.setRedstoneState(state);
		
		inputstate = RedstoneState.values()[nbt.getByte("inputRedstoneState")];
		inputRedstoneControl.setRedstoneState(inputstate);

		sideSelectorModule.readFromNBT(nbt);
	}

	@Override
	public void writeToNBT(NBTTagCompound nbt) {
		super.writeToNBT(nbt);
		nbt.setByte("redstoneState", (byte) state.ordinal());
		nbt.setByte("inputRedstoneState", (byte) inputstate.ordinal());
		sideSelectorModule.writeToNBT(nbt);
	}

	@Override
	public void onInventoryButtonPressed(int buttonId) {
		if(buttonId == 0)
			state = redstoneControl.getState();
		if(buttonId == 1)
			inputstate = inputRedstoneControl.getState();
		PacketHandler.sendToServer(new PacketMachine(this, (byte)0));
	}

	@Override
	public void writeDataToNetwork(ByteBuf out, byte id) {
		out.writeByte(state.ordinal());
		out.writeByte(inputstate.ordinal());
		for(int i = 0; i < 6; i++)
			out.writeByte(sideSelectorModule.getStateForSide(i));
	}

	@Override
	public void readDataFromNetwork(ByteBuf in, byte packetId,
			NBTTagCompound nbt) {
		nbt.setByte("state", in.readByte());
		nbt.setByte("inputstate", in.readByte());

		byte bytes[] = new byte[6];
		for(int i = 0; i < 6; i++)
			bytes[i] = in.readByte();
		nbt.setByteArray("bytes", bytes);
	}

	@Override
	public void useNetworkData(EntityPlayer player, Side side, byte id,
			NBTTagCompound nbt) {
		state = RedstoneState.values()[nbt.getByte("state")];
		inputstate = RedstoneState.values()[nbt.getByte("inputstate")];

		byte bytes[] = nbt.getByteArray("bytes");
		for(int i = 0; i < 6; i++)
			sideSelectorModule.setStateForSide(i, bytes[i]);

		if(rocket == null)
			setRedstoneState(state == RedstoneState.INVERTED);
		
		
		markDirty();
		worldObj.notifyBlockChange(xCoord, yCoord, zCoord, worldObj.getBlock(xCoord, yCoord, zCoord));
	}
	
	@Override
	public void onModuleUpdated(ModuleBase module) {
		PacketHandler.sendToServer(new PacketMachine(this, (byte)0));
	}
}
