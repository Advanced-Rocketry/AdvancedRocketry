package zmaster587.advancedRocketry.tile.multiblock;

import net.minecraft.block.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.Util;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.api.distmarker.Dist;
import zmaster587.advancedRocketry.api.AdvancedRocketryTileEntityType;
import zmaster587.advancedRocketry.api.Constants;
import zmaster587.advancedRocketry.dimension.DimensionManager;
import zmaster587.advancedRocketry.entity.EntityItemAbducted;
import zmaster587.advancedRocketry.util.AudioRegistry;
import zmaster587.advancedRocketry.util.PlanetaryTravelHelper;
import zmaster587.libVulpes.LibVulpes;
import zmaster587.libVulpes.api.LibVulpesBlocks;
import zmaster587.libVulpes.block.RotatableBlock;
import zmaster587.libVulpes.interfaces.ILinkableTile;
import zmaster587.libVulpes.inventory.modules.*;
import zmaster587.libVulpes.items.ItemLinker;
import zmaster587.libVulpes.network.PacketHandler;
import zmaster587.libVulpes.network.PacketMachine;
import zmaster587.libVulpes.tile.multiblock.TileMultiPowerConsumer;
import zmaster587.libVulpes.util.EmbeddedInventory;
import zmaster587.libVulpes.util.ZUtils;
import zmaster587.libVulpes.util.ZUtils.RedstoneState;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;

public class TileRailgun extends TileMultiPowerConsumer implements IInventory, ILinkableTile, IGuiCallback {
	private EmbeddedInventory inv;
	public long recoil;
	private int minStackTransferSize = 1;
	private ModuleNumericTextbox textBox;
	private RedstoneState state;
	private ModuleRedstoneOutputButton redstoneControl;

	static final Object[][][] structure = new Object[][][]
			{
					{
							{null,null,null,null,null,null,null,null,null},
							{null,null,null,null,null,null,null,null,null},
							{null,null,null,null,null,null,null,null,null},
							{null,null,null,null,new ResourceLocation("forge","coils/copper"),null,null,null,null},
							{null,null,null,new ResourceLocation("forge","coils/copper"),LibVulpesBlocks.blockMachineStructure,new ResourceLocation("forge","coils/copper"),null,null,null},
							{null,null,null,null,new ResourceLocation("forge","coils/copper"),null,null,null,null},
							{null,null,null,null,null,null,null,null,null},
							{null,null,null,null,null,null,null,null,null},
							{null,null,null,null,null,null,null,null,null}
					},
					{
							{null,null,null,null,null,null,null,null,null},
							{null,null,null,null,null,null,null,null,null},
							{null,null,null,null,null,null,null,null,null},
							{null,null,null,null,new ResourceLocation("forge","coils/copper"),null,null,null,null},
							{null,null,null,new ResourceLocation("forge","coils/copper"),LibVulpesBlocks.blockMachineStructure,new ResourceLocation("forge","coils/copper"),null,null,null},
							{null,null,null,null,new ResourceLocation("forge","coils/copper"),null,null,null,null},
							{null,null,null,null,null,null,null,null,null},
							{null,null,null,null,null,null,null,null,null},
							{null,null,null,null,null,null,null,null,null}
					},
					{
							{null,null,null,null,null,null,null,null,null},
							{null,null,null,null,null,null,null,null,null},
							{null,null,null,null,null,null,null,null,null},
							{null,null,null,null,new ResourceLocation("forge","coils/copper"),null,null,null,null},
							{null,null,null,new ResourceLocation("forge","coils/copper"),LibVulpesBlocks.blockMachineStructure,new ResourceLocation("forge","coils/copper"),null,null,null},
							{null,null,null,null,new ResourceLocation("forge","coils/copper"),null,null,null,null},
							{null,null,null,null,null,null,null,null,null},
							{null,null,null,null,null,null,null,null,null},
							{null,null,null,null,null,null,null,null,null}
					},
					{
							{null,null,null,null,null,null,null,null,null},
							{null,null,null,null,null,null,null,null,null},
							{null,null,null,null,null,null,null,null,null},
							{null,null,null,null,new ResourceLocation("forge","coils/copper"),null,null,null,null},
							{null,null,null,new ResourceLocation("forge","coils/copper"),LibVulpesBlocks.blockMachineStructure,new ResourceLocation("forge","coils/copper"),null,null,null},
							{null,null,null,null,new ResourceLocation("forge","coils/copper"),null,null,null,null},
							{null,null,null,null,null,null,null,null,null},
							{null,null,null,null,null,null,null,null,null},
							{null,null,null,null,null,null,null,null,null}
					},
					{
							{null,null,null,null,null,null,null,null,null},
							{null,null,null,null,null,null,null,null,null},
							{null,null,null,null,null,null,null,null,null},
							{null,null,null,null,new ResourceLocation("forge","coils/copper"),null,null,null,null},
							{null,null,null,new ResourceLocation("forge","coils/copper"),LibVulpesBlocks.blockMachineStructure,new ResourceLocation("forge","coils/copper"),null,null,null},
							{null,null,null,null,new ResourceLocation("forge","coils/copper"),null,null,null,null},
							{null,null,null,null,null,null,null,null,null},
							{null,null,null,null,null,null,null,null,null},
							{null,null,null,null,null,null,null,null,null}
					},
					{
							{null,null,null,null,null,null,null,null,null},
							{null,null,null,null,null,null,null,null,null},
							{null,null,null,null,null,null,null,null,null},
							{null,null,null,null,new ResourceLocation("forge","coils/copper"),null,null,null,null},
							{null,null,null,new ResourceLocation("forge","coils/copper"),LibVulpesBlocks.blockMachineStructure,new ResourceLocation("forge","coils/copper"),null,null,null},
							{null,null,null,null,new ResourceLocation("forge","coils/copper"),null,null,null,null},
							{null,null,null,null,null,null,null,null,null},
							{null,null,null,null,null,null,null,null,null},
							{null,null,null,null,null,null,null,null,null}
					},
					{
							{null,null,null,null,null,null,null,null,null},
							{null,null,null,null,null,null,null,null,null},
							{null,null,null,null,null,null,null,null,null},
							{null,null,null,null,new ResourceLocation("forge","coils/copper"),null,null,null,null},
							{null,null,null,new ResourceLocation("forge","coils/copper"),LibVulpesBlocks.blockMachineStructure,new ResourceLocation("forge","coils/copper"),null,null,null},
							{null,null,null,null,new ResourceLocation("forge","coils/copper"),null,null,null,null},
							{null,null,null,null,null,null,null,null,null},
							{null,null,null,null,null,null,null,null,null},
							{null,null,null,null,null,null,null,null,null}
					},
					{
							{null,null,null,null,null,null,null,null,null},
							{null,null,null,null,null,null,null,null,null},
							{null,null,null,null,null,null,null,null,null},
							{null,null,null,null,new ResourceLocation("forge","coils/copper"),null,null,null,null},
							{null,null,null,new ResourceLocation("forge","coils/copper"),LibVulpesBlocks.blockMachineStructure,new ResourceLocation("forge","coils/copper"),null,null,null},
							{null,null,null,null,new ResourceLocation("forge","coils/copper"),null,null,null,null},
							{null,null,null,null,null,null,null,null,null},
							{null,null,null,null,null,null,null,null,null},
							{null,null,null,null,null,null,null,null,null}
					},
					{
							{null,null,null,null,null,null,null,null,null},
							{null,null,null,null,null,null,null,null,null},
							{null,null,null,null,null,null,null,null,null},
							{null,null,null,null,new ResourceLocation("forge","coils/copper"),null,null,null,null},
							{null,null,null,new ResourceLocation("forge","coils/copper"),LibVulpesBlocks.blockMachineStructure,new ResourceLocation("forge","coils/copper"),null,null,null},
							{null,null,null,null,new ResourceLocation("forge","coils/copper"),null,null,null,null},
							{null,null,null,null,null,null,null,null,null},
							{null,null,null,null,null,null,null,null,null},
							{null,null,null,null,null,null,null,null,null}
					},
					{
							{null,null,null,null,null,null,null,null,null},
							{null,null,null,null,null,null,null,null,null},
							{null,null,null,null,new ResourceLocation("forge","storage_blocks/steel"),null,null,null,null},
							{null,null,null,LibVulpesBlocks.blockAdvancedMachineStructure,new ResourceLocation("forge","storage_blocks/titanium"),LibVulpesBlocks.blockAdvancedMachineStructure,null,null,null},
							{null,null,new ResourceLocation("forge","storage_blocks/steel"),new ResourceLocation("forge","storage_blocks/titanium"),new ResourceLocation("forge","storage_blocks/titanium"),new ResourceLocation("forge","storage_blocks/titanium"),new ResourceLocation("forge","storage_blocks/steel"),null,null},
							{null,null,null,LibVulpesBlocks.blockAdvancedMachineStructure,new ResourceLocation("forge","storage_blocks/titanium"),LibVulpesBlocks.blockAdvancedMachineStructure,null,null,null},
							{null,null,null,null,new ResourceLocation("forge","storage_blocks/steel"),null,null,null,null},
							{null,null,null,null,null,null,null,null,null},
							{null,null,null,null,null,null,null,null,null}
					},
					{
							{new ResourceLocation("forge","storage_blocks/steel"),null,null,new ResourceLocation("minecraft","slabs"),new ResourceLocation("minecraft","slabs"),new ResourceLocation("minecraft","slabs"),null,null,new ResourceLocation("forge","storage_blocks/steel")},
							{null,LibVulpesBlocks.blockAdvancedMachineStructure,new ResourceLocation("minecraft","slabs"),'I','c','O',new ResourceLocation("minecraft","slabs"),LibVulpesBlocks.blockAdvancedMachineStructure,null},
							{null,new ResourceLocation("minecraft","slabs"),LibVulpesBlocks.blockAdvancedMachineStructure,LibVulpesBlocks.blockAdvancedMachineStructure,LibVulpesBlocks.blockAdvancedMachineStructure,LibVulpesBlocks.blockAdvancedMachineStructure,LibVulpesBlocks.blockAdvancedMachineStructure,new ResourceLocation("minecraft","slabs"),null},
							{new ResourceLocation("minecraft","slabs"),new ResourceLocation("minecraft","slabs"),LibVulpesBlocks.blockAdvancedMachineStructure,LibVulpesBlocks.blockAdvancedMachineStructure,LibVulpesBlocks.blockAdvancedMachineStructure,LibVulpesBlocks.blockAdvancedMachineStructure,LibVulpesBlocks.blockAdvancedMachineStructure,new ResourceLocation("minecraft","slabs"),new ResourceLocation("minecraft","slabs")},
							{new ResourceLocation("minecraft","slabs"),new ResourceLocation("minecraft","slabs"),LibVulpesBlocks.blockAdvancedMachineStructure,LibVulpesBlocks.blockAdvancedMachineStructure,LibVulpesBlocks.motors,LibVulpesBlocks.blockAdvancedMachineStructure,LibVulpesBlocks.blockAdvancedMachineStructure,new ResourceLocation("minecraft","slabs"),new ResourceLocation("minecraft","slabs")},
							{new ResourceLocation("minecraft","slabs"),new ResourceLocation("minecraft","slabs"),LibVulpesBlocks.blockAdvancedMachineStructure,LibVulpesBlocks.blockAdvancedMachineStructure,LibVulpesBlocks.blockAdvancedMachineStructure,LibVulpesBlocks.blockAdvancedMachineStructure,LibVulpesBlocks.blockAdvancedMachineStructure,new ResourceLocation("minecraft","slabs"),new ResourceLocation("minecraft","slabs")},
							{null,new ResourceLocation("minecraft","slabs"),LibVulpesBlocks.blockAdvancedMachineStructure,LibVulpesBlocks.blockAdvancedMachineStructure,LibVulpesBlocks.blockAdvancedMachineStructure,LibVulpesBlocks.blockAdvancedMachineStructure,LibVulpesBlocks.blockAdvancedMachineStructure,new ResourceLocation("minecraft","slabs"),null},
							{null,LibVulpesBlocks.blockAdvancedMachineStructure,new ResourceLocation("minecraft","slabs"),'P','P','P',new ResourceLocation("minecraft","slabs"),LibVulpesBlocks.blockAdvancedMachineStructure,null},
							{new ResourceLocation("forge","storage_blocks/steel"),null,null,new ResourceLocation("minecraft","slabs"),new ResourceLocation("minecraft","slabs"),new ResourceLocation("minecraft","slabs"),null,null,new ResourceLocation("forge","storage_blocks/steel")}
					}
			};

	public TileRailgun() {
		super(AdvancedRocketryTileEntityType.TILE_RAILGUN);
		inv = new EmbeddedInventory(1);
		powerPerTick = 100000;
		redstoneControl = new ModuleRedstoneOutputButton(174, 4, "", this);
		state = RedstoneState.OFF;
		redstoneControl.setRedstoneState(state);
	}
	
	@Override
	protected int requiredPowerPerTick() {
		BlockPos pos = getDestPosition();
		if(pos != null) {
			int distance = (int)Math.sqrt(Math.pow(pos.getX() - this.pos.getX(),2) + Math.pow(pos.getZ() - this.pos.getZ(), 2));
			if(getDestDimId() == ZUtils.getDimensionIdentifier(world))
				distance = distance*10 + 50000;
			return Math.min(distance, super.requiredPowerPerTick());
		}
		return super.requiredPowerPerTick();
	}

	@Override
	public boolean shouldHideBlock(World world, BlockPos pos, BlockState tile) {
		return true;
	}
	
	/**
	 * @return the destionation DIMID or Constants.INVALID_PLANET if not valid
	 */
	private ResourceLocation getDestDimId() {
		ItemStack stack = inv.getStackInSlot(0);
		if(!stack.isEmpty() && stack.getItem() instanceof ItemLinker) {
			return ItemLinker.getDimId(stack);
		}
		return Constants.INVALID_PLANET;
	}
	
	/**
	 * @return the destionation DIMID or null if not valid
	 */
	private BlockPos getDestPosition() {
		ItemStack stack = inv.getStackInSlot(0);
		if(!stack.isEmpty() && stack.getItem() instanceof ItemLinker && ItemLinker.isSet(stack)) {
			return ItemLinker.getMasterCoords(stack);
		}
		return null;
	}

	@Override
	public Object[][][] getStructure() {
		return structure;
	}

	@Override
	public List<ModuleBase> getModules(int ID, PlayerEntity player) {
		List<ModuleBase> modules = super.getModules(ID, player);

		modules.add(new ModuleSlotArray(40, 40, this, 0, 1));
		if(world.isRemote) {
			//if(textBox == null) {
			textBox = new ModuleNumericTextbox(this, 80, 40, 32, 12, 2);
			//}
			textBox.setText(String.valueOf(minStackTransferSize));
			modules.add(new ModuleText(60, 25, LibVulpes.proxy.getLocalizedString("msg.railgun.transfermin"), 0x2b2b2b));
			modules.add(textBox);
		}

		modules.add(redstoneControl);

		return modules;
	}

	@Override
	public void remove() {
		super.remove();
		if(!this.world.isRemote)
		{
			ServerWorld serverworld = (ServerWorld)world;
			serverworld.forceChunk(new ChunkPos(getPos()).x, new ChunkPos(getPos()).z, false);
		}
	}

	@Override
	public void onInventoryUpdated() {
		//Needs completion
		if(itemInPorts.isEmpty()) {
			attemptCompleteStructure(world.getBlockState(pos));
		}
	}

	@Override
	public void onInventoryButtonPressed(ModuleButton buttonId) {
		super.onInventoryButtonPressed(buttonId);

		if(buttonId == redstoneControl) {
			state = redstoneControl.getState();
			PacketHandler.sendToServer(new PacketMachine(this, (byte)5));
		}
	}

	@Override
	protected void onRunningPoweredTick() {
		//Do nothing, or add charge effect
	}

	@Override
	public void useEnergy(int amt) {
		if(!world.isRemote && enabled && isRedstoneStateSatisfied() && attemptCargoTransfer())
			super.useEnergy(amt);
	}

	@Override
	public boolean isRunning() {
		return isComplete();
	}

	private boolean isRedstoneStateSatisfied() {
		if(state == RedstoneState.OFF)
			return true;

		boolean powered = world.getRedstonePowerFromNeighbors(pos) > 0;

		return (state == RedstoneState.ON && powered) || (!powered && state == RedstoneState.INVERTED);
	}

	private boolean attemptCargoTransfer() {
		if(world.isRemote)
			return false;

		ItemStack tfrStack = ItemStack.EMPTY;
		IInventory inv2 = null;
		int index = 0;
		//BlockPos invPos;

		out:
			for(IInventory inv : this.itemInPorts) {
				for(int i = inv.getSizeInventory() - 1; i >= 0 ; i--) {
					if(!(tfrStack = inv.getStackInSlot(i)).isEmpty() && inv.getStackInSlot(i).getCount() >= minStackTransferSize) {
						inv2 = inv;
						index = i;

						//invPos = ((TileEntity)inv).getPos();

						break out;
					}
					else tfrStack = ItemStack.EMPTY;
				}
			}

		if(!tfrStack.isEmpty()) {
			BlockPos pos = getDestPosition();
			if(pos != null) {
				ResourceLocation dimId;
				
				dimId = getDestDimId();

				if(!Constants.INVALID_PLANET.equals(dimId)) {
					World world = ZUtils.getWorld(dimId);
					TileEntity tile;

					if(world != null && (tile = world.getTileEntity(pos)) instanceof TileRailgun && ((TileRailgun)tile).canReceiveCargo(tfrStack) &&
							(PlanetaryTravelHelper.isTravelAnywhereInPlanetarySystem(ZUtils.getDimensionIdentifier(this.world),
									DimensionManager.getEffectiveDimId(ZUtils.getDimensionIdentifier(world), pos).getId()) ||
									DimensionManager.getEffectiveDimId(ZUtils.getDimensionIdentifier(world), pos).getId() == zmaster587.advancedRocketry.dimension.DimensionManager.getEffectiveDimId(ZUtils.getDimensionIdentifier(this.world), this.pos).getId()) ) {

						((TileRailgun)tile).onReceiveCargo(tfrStack);
						inv2.setInventorySlotContents(index, ItemStack.EMPTY);
						inv2.markDirty();
						world.notifyBlockUpdate(pos, world.getBlockState(pos),  world.getBlockState(pos), 2);

						Direction dir = RotatableBlock.getFront(world.getBlockState(pos));

						EntityItemAbducted ent = new EntityItemAbducted(this.world, this.pos.getX() - 2*dir.getXOffset() + 0.5f, this.pos.getY() + 5, this.pos.getZ() - 2*dir.getZOffset() + 0.5f, tfrStack);
						this.world.addEntity(ent);
						PacketHandler.sendToNearby(new PacketMachine(this, (byte) 3), this.world, this.pos.getX() - dir.getXOffset(), this.pos.getY() + 5, this.pos.getZ() - dir.getZOffset(),  64d);
						return true;
					}
				}
			}
		}
		return false;
	}

	public boolean canReceiveCargo(@Nonnull ItemStack stack) {
		for(IInventory inv : this.itemOutPorts) {
			if(ZUtils.numEmptySlots(inv) > 0)
				return true;
		}

		return false;
	}

	public void onReceiveCargo(@Nonnull ItemStack stack) {
		for(IInventory inv : this.itemOutPorts) {
			if(ZUtils.doesInvHaveRoom(stack, inv)) {
				ZUtils.mergeInventory(stack, inv);
				break;
			}
		}
	}

	@Override
	public String getMachineName() {
		return "block.advancedrocketry.railgun";
	}

	@Override
	@Nonnull
	public AxisAlignedBB getRenderBoundingBox() {
		return new AxisAlignedBB(this.pos.getX() -5, this.pos.getY(), this.pos.getZ() - 5, this.pos.getX() + 5, this.pos.getY() +10, this.pos.getZ() + 5);
	}

	@Override
	public int getSizeInventory() {
		return 1;
	}

	@Override
	@Nonnull
	public ItemStack getStackInSlot(int i) {
		return inv.getStackInSlot(i);
	}

	@Override
	@Nonnull
	public ItemStack decrStackSize(int i, int j) {
		return inv.decrStackSize(i, j);
	}


	@Override
	public void setInventorySlotContents(int i, @Nonnull ItemStack j) {
		inv.setInventorySlotContents(i, j);

	}

	@Override
	public int getInventoryStackLimit() {
		return 1;
	}

	@Override
	@ParametersAreNonnullByDefault
	public boolean isUsableByPlayer(PlayerEntity player) {
		return true;
	}
	
	@Override
	public boolean isEmpty() {
		return inv.isEmpty();
	}

	@Override
	@ParametersAreNonnullByDefault
	public void openInventory(PlayerEntity player) {

	}

	@Override
	@ParametersAreNonnullByDefault
	public void closeInventory(PlayerEntity player) {

	}

	@Override
	public boolean isItemValidForSlot(int i, @Nonnull ItemStack stack) {
		return stack.isEmpty() || stack.getItem() instanceof ItemLinker;
	}

	@Override
	@ParametersAreNonnullByDefault
	public boolean onLinkStart(ItemStack item, TileEntity entity, PlayerEntity player, World world) {
		ItemLinker.setMasterCoords(item, this.getPos());
		ItemLinker.setDimId(item, ZUtils.getDimensionIdentifier(world));
		if(!world.isRemote)
			player.sendMessage(new TranslationTextComponent("msg.linker.program"), Util.DUMMY_UUID);
		return true;
	}

	@Override
	@ParametersAreNonnullByDefault
	public boolean onLinkComplete(ItemStack item, TileEntity entity, PlayerEntity player, World world) {
		return false;
	}

	@Override
	public CompoundNBT write(CompoundNBT nbt) {
		super.write(nbt);
		inv.write(nbt);
		nbt.putInt("minTfrSize", minStackTransferSize);
		nbt.putByte("redstoneState", (byte) state.ordinal());
		return nbt;
	}

	@Override
	public void read(BlockState blkstate, CompoundNBT nbt) {
		super.read(blkstate, nbt);
		inv.readFromNBT(nbt);
		minStackTransferSize = nbt.getInt("minTfrSize");

		state = RedstoneState.values()[nbt.getByte("redstoneState")];
		redstoneControl.setRedstoneState(state);
	}

	@Override
	public void writeDataToNetwork(PacketBuffer out, byte id) {
		if(id == 4)
			out.writeInt(minStackTransferSize);
		else if(id == 5)
			out.writeByte(state.ordinal());
		else
			super.writeDataToNetwork(out, id);
	}

	@Override
	public void readDataFromNetwork(PacketBuffer in, byte packetId,
			CompoundNBT nbt) {
		if(packetId == 4)
			nbt.putInt("minTransferSize", in.readInt());
		else if(packetId == 5) 
			nbt.putByte("state", in.readByte());
		else
			super.readDataFromNetwork(in, packetId, nbt);
	}

	@Override
	public void useNetworkData(PlayerEntity player, Dist side, byte id,
			CompoundNBT nbt) {
		if(side.isClient()) {
			if(id == 3) {
				Direction dir = RotatableBlock.getFront(world.getBlockState(pos));
				LibVulpes.proxy.playSound(world, pos, AudioRegistry.railgunFire, SoundCategory.BLOCKS, Minecraft.getInstance().gameSettings.getSoundLevel(SoundCategory.BLOCKS), 0.975f + world.rand.nextFloat()*0.05f);
				recoil = world.getGameTime();
			}
		}
		else if(id == 4) {
			minStackTransferSize = nbt.getInt("minTransferSize");

		}			
		else if(id == 5) {
			state = RedstoneState.values()[nbt.getByte("state")];
		}
		else
			super.useNetworkData(player, side, id, nbt);
	}

	@Override
	public void onModuleUpdated(ModuleBase module) {
		if(module == textBox) {
			if(textBox.getText().isEmpty())
				minStackTransferSize = 1;
			else
				minStackTransferSize = MathHelper.clamp(Integer.parseInt(textBox.getText()),1, 64);
			PacketHandler.sendToServer(new PacketMachine(this, (byte)4));
		}
	}

	@Override
	protected void writeNetworkData(CompoundNBT nbt) {
		super.writeNetworkData(nbt);
		nbt.putByte("state", (byte)state.ordinal());
		nbt.putInt("minTfrSize", minStackTransferSize);
	}

	@Override
	protected void readNetworkData(CompoundNBT nbt) {
		super.readNetworkData(nbt);
		state = RedstoneState.values()[nbt.getByte("redstoneState")];
		redstoneControl.setRedstoneState(state);
		minStackTransferSize = nbt.getInt("minTfrSize");
	}

	@Override
	@Nonnull
	public ItemStack removeStackFromSlot(int index) {
		return inv.removeStackFromSlot(index);
	}

	@Override
	public void clear() {
		inv.clear();
	}
}
