package zmaster587.advancedRocketry.tile.infrastructure;

import net.minecraft.block.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.api.distmarker.Dist;
import zmaster587.advancedRocketry.api.AdvancedRocketryTileEntityType;
import zmaster587.advancedRocketry.api.EntityRocketBase;
import zmaster587.advancedRocketry.api.IInfrastructure;
import zmaster587.advancedRocketry.api.IMission;
import zmaster587.advancedRocketry.block.multiblock.BlockARHatch;
import zmaster587.advancedRocketry.entity.EntityRocket;
import zmaster587.advancedRocketry.tile.TileRocketAssembler;
import zmaster587.libVulpes.LibVulpes;
import zmaster587.libVulpes.inventory.modules.*;
import zmaster587.libVulpes.items.ItemLinker;
import zmaster587.libVulpes.network.PacketHandler;
import zmaster587.libVulpes.network.PacketMachine;
import zmaster587.libVulpes.tile.ISidedRedstoneTile;
import zmaster587.libVulpes.tile.multiblock.hatch.TileFluidHatch;
import zmaster587.libVulpes.util.INetworkMachine;
import zmaster587.libVulpes.util.ZUtils.RedstoneState;

import java.util.List;

public class TileRocketFluidLoader extends TileFluidHatch implements IInfrastructure, ITickableTileEntity, IButtonInventory, INetworkMachine, IGuiCallback, ISidedRedstoneTile {

	EntityRocket rocket;
	ModuleRedstoneOutputButton redstoneControl;
	RedstoneState state;
	ModuleRedstoneOutputButton inputRedstoneControl;
	RedstoneState inputstate;
	ModuleBlockSideSelector sideSelectorModule;

	private static int ALLOW_REDSTONEOUT = 2;

	public TileRocketFluidLoader(TileEntityType<?> type) {
		super(type);
		redstoneControl = new ModuleRedstoneOutputButton(174, 4, "", this, LibVulpes.proxy.getLocalizedString("msg.fluidloader.loadingstate"));
		state = RedstoneState.ON;
		inputRedstoneControl = new ModuleRedstoneOutputButton(174, 32, "", this, LibVulpes.proxy.getLocalizedString("msg.fluidloader.allowloading"));
		inputstate = RedstoneState.OFF;
		inputRedstoneControl.setRedstoneState(inputstate);
		sideSelectorModule = new ModuleBlockSideSelector(90, 15, this, LibVulpes.proxy.getLocalizedString("msg.fluidloader.none"), LibVulpes.proxy.getLocalizedString("msg.fluidLoader.allowredstoneoutput"), LibVulpes.proxy.getLocalizedString("msg.fluidLoader.allowredstoneinput"));
	}

	public TileRocketFluidLoader(TileEntityType<?> type, int size) {
		super(type, size);
		redstoneControl = new ModuleRedstoneOutputButton(174, 4, "", this, LibVulpes.proxy.getLocalizedString("msg.fluidLoader.loadingstate"));
		state = RedstoneState.ON;
		inputRedstoneControl = new ModuleRedstoneOutputButton(174, 32, "", this, LibVulpes.proxy.getLocalizedString("msg.fluidLoader.allowloading"));
		inputstate = RedstoneState.OFF;
		inputRedstoneControl.setRedstoneState(inputstate);
		sideSelectorModule = new ModuleBlockSideSelector(90, 15, this, LibVulpes.proxy.getLocalizedString("msg.fluidloader.none"), LibVulpes.proxy.getLocalizedString("msg.fluidLoader.allowredstoneoutput"), LibVulpes.proxy.getLocalizedString("msg.fluidLoader.allowredstoneinput"));
	}
	
	public TileRocketFluidLoader()
	{
		this(AdvancedRocketryTileEntityType.TILE_FLUID_LOADER);
	}
	
	public TileRocketFluidLoader(int size)
	{
		this(AdvancedRocketryTileEntityType.TILE_FLUID_LOADER, size);
	}

	@Override
	public void remove() {
		super.remove();
		if(getMasterBlock() instanceof TileRocketAssembler)
			((TileRocketAssembler)getMasterBlock()).removeConnectedInfrastructure(this);
	}

	@Override
	public String getModularInventoryName() {
		return "block.advancedrocketry.rocketfluidloader";
	}

	@Override
	public boolean allowRedstoneOutputOnSide(Direction facing) {
		return sideSelectorModule.getStateForSide(facing.getOpposite()) == 1;
	}

	@Override
	public List<ModuleBase> getModules(int ID, PlayerEntity player) {
		List<ModuleBase> list = super.getModules(ID, player);
		list.add(redstoneControl);
		list.add(inputRedstoneControl);
		list.add(sideSelectorModule);
		return list;
	}

	protected boolean getStrongPowerForSides(World world, BlockPos pos) {
		for(int i = 0; i < 6; i++) {
			if(sideSelectorModule.getStateForSide(i) == ALLOW_REDSTONEOUT && world.getRedstonePower(pos.offset(Direction.values()[i]), Direction.values()[i]) > 0)
				return true;
		}
		return false;
	}
	
	@Override
	public void tick() {
		//Move a stack of items
		if(!world.isRemote && rocket != null) {

			boolean isAllowToOperate = (inputstate == RedstoneState.OFF || isStateActive(inputstate, getStrongPowerForSides(world, getPos())));

			List<TileEntity> tiles = rocket.storage.getFluidTiles();
			boolean rocketFluidFull = false;

			//Function returns if something can be moved
			for(TileEntity tile : tiles) {
				IFluidHandler handler = tile.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, null).resolve().get();

				//See if we have anything to fill because redstone output
				FluidStack rocketFluid = handler.drain(1, FluidAction.SIMULATE);
				if(handler.fill(rocketFluid, FluidAction.SIMULATE) > 0)
					rocketFluidFull = true;

				if(isAllowToOperate) {
					rocketFluid = fluidTank.drain(fluidTank.getCapacity(), FluidAction.SIMULATE);
					if(!rocketFluid.isEmpty() && rocketFluid.getAmount() > 0)
						fluidTank.drain(handler.fill(rocketFluid, FluidAction.EXECUTE), FluidAction.EXECUTE);
				}
			}

			//Update redstone state
			setRedstoneState(!rocketFluidFull);

		}
	}


	@Override
	public SUpdateTileEntityPacket getUpdatePacket() {
		return new SUpdateTileEntityPacket(pos, 0, getUpdateTag());
	}

	@Override
	public void onDataPacket(NetworkManager net, SUpdateTileEntityPacket pkt) {
		handleUpdateTag(getBlockState(), pkt.getNbtCompound());
	}

	@Override
	public CompoundNBT getUpdateTag() {
		return write(new CompoundNBT());
	}

	protected void setRedstoneState(boolean condition) {
		condition = isStateActive(state, condition);
		((BlockARHatch)world.getBlockState(pos).getBlock()).setRedstoneState(world,world.getBlockState(pos), pos, condition);

	}

	protected boolean isStateActive(RedstoneState state, boolean condition) {
		if(state == RedstoneState.INVERTED)
			return !condition;
		else if(state == RedstoneState.OFF)
			return false;
		return condition;
	}

	@Override
	public boolean onLinkStart(ItemStack item, TileEntity entity,
			PlayerEntity player, World world) {

		ItemLinker.setMasterCoords(item, this.getPos());

		if(this.rocket != null) {
			this.rocket.unlinkInfrastructure(this);
			this.unlinkRocket();
		}

		if(player.world.isRemote)
			Minecraft.getInstance().ingameGUI.getChatGUI().printChatMessage(new TranslationTextComponent("%s %s",new TranslationTextComponent("msg.fluidloader.link"), ": " + getPos().getX() + " " + getPos().getY() + " " + getPos().getZ()));
		return true;
	}

	@Override
	public boolean onLinkComplete(ItemStack item, TileEntity entity,
			PlayerEntity player, World world) {
		if(player.world.isRemote)
			Minecraft.getInstance().ingameGUI.getChatGUI().printChatMessage(new TranslationTextComponent("msg.linker.error.firstmachine"));
		return false;
	}

	@Override
	public void unlinkRocket() {
		rocket = null;
		((BlockARHatch)world.getBlockState(pos).getBlock()).setRedstoneState(world, world.getBlockState(pos), pos, false);
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
	public boolean linkMission(IMission mission) {
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
	public void read(BlockState blkstate, CompoundNBT nbt) {
		super.read(blkstate, nbt);

		state = RedstoneState.values()[nbt.getByte("redstoneState")];
		redstoneControl.setRedstoneState(state);


		inputstate = RedstoneState.values()[nbt.getByte("inputRedstoneState")];
		inputRedstoneControl.setRedstoneState(inputstate);

		sideSelectorModule.readFromNBT(nbt);
	}

	@Override
	public CompoundNBT write(CompoundNBT nbt) {
		super.write(nbt);
		nbt.putByte("redstoneState", (byte) state.ordinal());
		nbt.putByte("inputRedstoneState", (byte) inputstate.ordinal());
		sideSelectorModule.write(nbt);
		return nbt;
	}

	@Override
	public void onInventoryButtonPressed(ModuleButton buttonId) {
		if(buttonId == redstoneControl)
			state = redstoneControl.getState();
		if(buttonId == inputRedstoneControl)
			inputstate = inputRedstoneControl.getState();
		PacketHandler.sendToServer(new PacketMachine(this, (byte)0));
	}

	@Override
	public void writeDataToNetwork(PacketBuffer out, byte id) {
		out.writeByte(state.ordinal());
		out.writeByte(inputstate.ordinal());
		for(int i = 0; i < 6; i++)
			out.writeByte(sideSelectorModule.getStateForSide(i));
	}

	@Override
	public void readDataFromNetwork(PacketBuffer in, byte packetId,
			CompoundNBT nbt) {
		nbt.putByte("state", in.readByte());
		nbt.putByte("inputstate", in.readByte());

		byte[] bytes = new byte[6];
		for(int i = 0; i < 6; i++)
			bytes[i] = in.readByte();
		nbt.putByteArray("bytes", bytes);
	}

	@Override
	public void useNetworkData(PlayerEntity player, Dist side, byte id,
			CompoundNBT nbt) {
		state = RedstoneState.values()[nbt.getByte("state")];
		inputstate = RedstoneState.values()[nbt.getByte("inputstate")];

		byte[] bytes = nbt.getByteArray("bytes");
		for(int i = 0; i < 6; i++)
			sideSelectorModule.setStateForSide(i, bytes[i]);

		if(rocket == null)
			setRedstoneState(state == RedstoneState.INVERTED);
		
		
		markDirty();
		world.markChunkDirty(getPos(), this);
	}

	@Override
	public void onModuleUpdated(ModuleBase module) {
		PacketHandler.sendToServer(new PacketMachine(this, (byte)0));
	}
}
