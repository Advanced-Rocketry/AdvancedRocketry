package zmaster587.advancedRocketry.tile.atmosphere;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.fluid.Fluid;
import net.minecraft.inventory.container.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.api.distmarker.Dist;
import zmaster587.advancedRocketry.api.ARConfiguration;
import zmaster587.advancedRocketry.api.AdvancedRocketryBlocks;
import zmaster587.advancedRocketry.api.AdvancedRocketryFluids;
import zmaster587.advancedRocketry.api.AdvancedRocketryTileEntityType;
import zmaster587.advancedRocketry.api.AreaBlob;
import zmaster587.advancedRocketry.api.util.IBlobHandler;
import zmaster587.advancedRocketry.atmosphere.AtmosphereHandler;
import zmaster587.advancedRocketry.atmosphere.AtmosphereType;
import zmaster587.advancedRocketry.dimension.DimensionManager;
import zmaster587.advancedRocketry.inventory.TextureResources;
import zmaster587.advancedRocketry.util.AudioRegistry;
import zmaster587.libVulpes.LibVulpes;
import zmaster587.libVulpes.api.IToggleableMachine;
import zmaster587.libVulpes.api.LibvulpesGuiRegistry;
import zmaster587.libVulpes.block.BlockTile;
import zmaster587.libVulpes.client.RepeatingSound;
import zmaster587.libVulpes.inventory.ContainerModular;
import zmaster587.libVulpes.inventory.GuiHandler;
import zmaster587.libVulpes.inventory.GuiHandler.guiId;
import zmaster587.libVulpes.inventory.modules.*;
import zmaster587.libVulpes.network.PacketHandler;
import zmaster587.libVulpes.network.PacketMachine;
import zmaster587.libVulpes.tile.TileInventoriedFEConsumerTank;
import zmaster587.libVulpes.util.FluidUtils;
import zmaster587.libVulpes.util.HashedBlockPosition;
import zmaster587.libVulpes.util.IAdjBlockUpdate;
import zmaster587.libVulpes.util.INetworkMachine;
import zmaster587.libVulpes.util.ZUtils.RedstoneState;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class TileOxygenVent extends TileInventoriedFEConsumerTank implements IBlobHandler, IModularInventory, INetworkMachine, IAdjBlockUpdate, IToggleableMachine, IButtonInventory, IToggleButton {

	private boolean isSealed;
	private boolean firstRun;
	private boolean hasFluid;
	private boolean soundInit;
	private boolean allowTrace;
	private boolean lock;
	private int numScrubbers;
	private List<TileCO2Scrubber> scrubbers;
	private int radius = 0;
	
	private final static byte PACKET_REDSTONE_ID = 2;
	private final static byte PACKET_TRACE_ID = 3;
	
	private RedstoneState state;
	private ModuleRedstoneOutputButton redstoneControl;
	private ModuleToggleSwitch traceToggle;
	
	
	public TileOxygenVent() {
		super(AdvancedRocketryTileEntityType.TILE_OXYGEN_VENT, 1000, 2, 2000);
		isSealed = true;
		firstRun = true;
		hasFluid = true;
		soundInit = false;
		allowTrace = false;
		numScrubbers = 0;
		scrubbers = new LinkedList<>();
		state = RedstoneState.ON;
		redstoneControl = (ModuleRedstoneOutputButton) new ModuleRedstoneOutputButton(174, 4, "", this).setAdditionalData(PACKET_REDSTONE_ID);
		traceToggle = (ModuleToggleSwitch) new ModuleToggleSwitch(80, 20, LibVulpes.proxy.getLocalizedString("msg.vent.trace"), this, TextureResources.buttonGeneric, 80, 18, false).setAdditionalData(PACKET_TRACE_ID);
	}

	public TileOxygenVent(int energy, int invSize, int tankSize) {
		super(AdvancedRocketryTileEntityType.TILE_OXYGEN_VENT, energy, invSize, tankSize);
		isSealed = false;
		firstRun = false;
		hasFluid = true;
		soundInit = false;
		allowTrace = false;
		scrubbers = new LinkedList<>();
		state = RedstoneState.ON;
		redstoneControl = (ModuleRedstoneOutputButton) new ModuleRedstoneOutputButton(174, 4, "", this).setAdditionalData(0);
		traceToggle = (ModuleToggleSwitch) new ModuleToggleSwitch(80, 20, LibVulpes.proxy.getLocalizedString("msg.vent.trace"), this, TextureResources.buttonGeneric, 80, 18, false).setAdditionalData(5);
	}

	@Override
	public boolean canPerformFunction() {
		return AtmosphereHandler.hasAtmosphereHandler(this.world);
	}

	@Override
	public World getWorldObj() {
		return getWorld();
	}

	@Override
	public void onAdjacentBlockUpdated() {

		if(isSealed)
			activateAdjBlocks();
		scrubbers.clear();
		TileEntity[] tiles = new TileEntity[6];
		tiles[0] = world.getTileEntity(pos.add(1,0,0));
		tiles[1] = world.getTileEntity(pos.add(-1,0,0));
		tiles[2] = world.getTileEntity(pos.add(0,1,0));
		tiles[3] = world.getTileEntity(pos.add(0,-1,0));
		tiles[4] = world.getTileEntity(pos.add(0,0,1));
		tiles[5] = world.getTileEntity(pos.add(0,0,-1));

		lock = true;
		for(TileEntity tile : tiles) {
			if(tile instanceof TileCO2Scrubber && world.getBlockState(tile.getPos()).getBlock() == AdvancedRocketryBlocks.blockCO2Scrubber)
				scrubbers.add((TileCO2Scrubber)tile);
		}
	}

	private void activateAdjBlocks() {
		numScrubbers = 0;
		numScrubbers = toggleAdjBlock(pos.add(1,0,0), true) ? numScrubbers + 1 : numScrubbers;
		numScrubbers = toggleAdjBlock(pos.add(-1,0,0), true) ? numScrubbers + 1 : numScrubbers;
		numScrubbers = toggleAdjBlock(pos.add(0,1,0), true) ? numScrubbers + 1 : numScrubbers;
		numScrubbers = toggleAdjBlock(pos.add(0,-1,0), true) ? numScrubbers + 1 : numScrubbers;
		numScrubbers = toggleAdjBlock(pos.add(0,0,1), true) ? numScrubbers + 1 : numScrubbers;
		numScrubbers = toggleAdjBlock(pos.add(0,0,-1), true) ? numScrubbers + 1 : numScrubbers;
	}

	private void deactivateAdjBlocks() {
		toggleAdjBlock(pos.add(1,0,0), false);
		toggleAdjBlock(pos.add(-1,0,0), false);
		toggleAdjBlock(pos.add(0,1,0), false);
		toggleAdjBlock(pos.add(0,-1,0), false);
		toggleAdjBlock(pos.add(0,0,1), false);
		toggleAdjBlock(pos.add(0,0,-1), false);
	}

	private boolean toggleAdjBlock(BlockPos pos, boolean on) {
		BlockState state = this.world.getBlockState(pos);
		Block block = state.getBlock();
		if(block == AdvancedRocketryBlocks.blockCO2Scrubber) {
			((BlockTile)block).setBlockState(world, state, pos, on);

			return true;
		}
		return false;
	}

	@Override
	public void remove() {
		super.remove();

		AtmosphereHandler atmhandler = AtmosphereHandler.getOxygenHandler(this.world);
		if(atmhandler != null)
			atmhandler.unregisterBlob(this);
		deactivateAdjBlocks();
	}

	@Override
	public int getPowerPerOperation() {
		return (int)((numScrubbers * 10 + 1) * ARConfiguration.getCurrentConfig().oxygenVentPowerMultiplier.get());
	}

	@Override
	public boolean canFill(Fluid fluid) {
		return FluidUtils.areFluidsSameType(fluid, AdvancedRocketryFluids.oxygenStill.get()) && super.canFill( fluid);
	}

	public boolean isTurnedOn() {
		if(state == RedstoneState.OFF)
			return true;

		boolean state2 = world.getRedstonePowerFromNeighbors(pos) > 0;

		if(state == RedstoneState.INVERTED)
			state2 = !state2;
		return state2;
	}
	
	@Override
	public void performFunction() {

		/* NB: canPerformFunction returns false and must return true for performFunction to execute
		 * if there is no O2 handler, this is why we can safely call AtmosphereHandler.getOxygenHandler
		 * and not have to worry about an NPE being thrown
		 */

		//IF first tick then register the blob and check for scrubbers

		if(!world.isRemote) {
			AtmosphereHandler atmhandler = AtmosphereHandler.getOxygenHandler(this.world);
			if(atmhandler == null)
				return;

			if(firstRun) {
				atmhandler.registerBlob(this, pos);

				onAdjacentBlockUpdated();
				//isSealed starts as true so we can accurately check for scrubbers, we now set it to false to force the tile to check for a seal on first run
				setSealed(false);
				firstRun = false;
			}

			if(isSealed && atmhandler.getBlobSize(this) == 0) {
				deactivateAdjBlocks();
				setSealed(false);
			}

			if(isSealed && !isTurnedOn()) {
				atmhandler.clearBlob(this);

				deactivateAdjBlocks();

				setSealed(false);
			}
			else if(!isSealed && isTurnedOn() && hasEnoughEnergy(getPowerPerOperation())) {

				if(world.getGameTime() % 100 == 0)
					setSealed(atmhandler.addBlock(this, new HashedBlockPosition(pos)));

				if(isSealed) {
					activateAdjBlocks();
				}
				else if(world.getGameTime() % 10 == 0 && allowTrace) {
					radius++;
					if(radius > 128)
						radius = 0;
				}
			}

			//Lock the vent while the scrubbers do their block updates
			if(isSealed && !lock) {

				//If scrubbers exist and the config allows then use the cartridge
				if(ARConfiguration.getCurrentConfig().scrubberRequiresCartrige.get()){
					//TODO: could be optimized
					if(world.getGameTime() % 200 == 0) {
						numScrubbers = 0;
						for(TileCO2Scrubber scrubber : scrubbers) {
							numScrubbers = scrubber.useCharge() ? numScrubbers + 1 : numScrubbers;
						}
					}

				}

				int amtToDrain = (int)Math.ceil((atmhandler.getBlobSize(this) * getGasUsageMultiplier()));
				FluidStack drainedFluid = this.drain(amtToDrain, FluidAction.SIMULATE);

				if( (drainedFluid != null && drainedFluid.getAmount() >= amtToDrain) || amtToDrain == 0) {
					this.drain(amtToDrain, FluidAction.EXECUTE);
					if(!hasFluid) {
						hasFluid = true;

						activateAdjBlocks();

						atmhandler.setAtmosphereType(this, AtmosphereType.PRESSURIZEDAIR);
					}
				}
				else if(hasFluid){
					atmhandler.setAtmosphereType(this, DimensionManager.getInstance().getDimensionProperties(this.world).getAtmosphere());

					deactivateAdjBlocks();

					hasFluid = false;
				}
			} else
				lock = false;

		}
	}
	
	@Override
	public int getTraceDistance() {
		return allowTrace ? radius : -1;
	}

	@Override
	public void tick() {
		if(canPerformFunction()) {

			if(hasEnoughEnergy(getPowerPerOperation())) {
				performFunction();
				if(!world.isRemote && isSealed) this.energy.extractEnergy(getPowerPerOperation(), false);
			}
			else
				notEnoughEnergyForFunction();
		}
		else
			radius = -1;
		if(!soundInit && world.isRemote) {
			LibVulpes.proxy.playSound(new RepeatingSound(AudioRegistry.airHissLoop, SoundCategory.BLOCKS, this));
		}
		soundInit = true;
	}


	private void setSealed(boolean sealed) {
		boolean prevSealed = isSealed;
		if((prevSealed != sealed)) {
			markDirty();
			world.notifyBlockUpdate(pos, world.getBlockState(pos),  world.getBlockState(pos), 2);
			
			if(isSealed)
				radius = -1;
		}
		isSealed = sealed;
	}

	@Override
	public SUpdateTileEntityPacket getUpdatePacket() {
		return new SUpdateTileEntityPacket(pos,0, getUpdateTag());

	}

	@Override
	public void onDataPacket(NetworkManager net, SUpdateTileEntityPacket pkt) {
		handleUpdateTag(getBlockState(), pkt.getNbtCompound());
	}

	@Override
	public CompoundNBT getUpdateTag() {
		CompoundNBT tag = super.getUpdateTag();
		tag.putBoolean("isSealed", isSealed);
		
		return tag;
	}

	@Override
	public void handleUpdateTag(BlockState state, CompoundNBT tag) {
		super.handleUpdateTag(state, tag);
		isSealed = tag.getBoolean("isSealed");
		
		if(isSealed) {
			activateAdjBlocks();
		}
	}

	public float getGasUsageMultiplier() {
		return (float) (Math.max(0.01f - numScrubbers * 0.005f,0)*ARConfiguration.getCurrentConfig().oxygenVentConsumptionMult.get());
	}

	@Override
	public void notEnoughEnergyForFunction() {
		if(!world.isRemote) {
			AtmosphereHandler handler = AtmosphereHandler.getOxygenHandler(this.world);
			if(handler != null)
				handler.clearBlob(this);

			deactivateAdjBlocks();

			setSealed(false);
		}
	}

	
	
	@Override
	@Nonnull
	public int[] getSlotsForFace(@Nullable Direction side) {
		return new int[]{};
	}

	@Override
	public boolean isItemValidForSlot(int slot, @Nonnull ItemStack itemStack) {
		return false;
	}

	@Override
	public boolean canBlobsOverlap(HashedBlockPosition blockPosition, AreaBlob blob) {
		return false;
	}

	@Override
	public int getMaxBlobRadius() {
		return ARConfiguration.getCurrentConfig().oxygenVentSize.get();
	}

	@Override
	@Nonnull
	public HashedBlockPosition getRootPosition() {
		return new HashedBlockPosition(pos);
	}

	@Override
	public List<ModuleBase> getModules(int ID, PlayerEntity player) {
		ArrayList<ModuleBase> modules = new ArrayList<>();

		modules.add(new ModuleSlotArray(52, 20, this, 0, 1));
		modules.add(new ModuleSlotArray(52, 57, this, 1, 2));
		modules.add(new ModulePower(18, 20, this));
		modules.add(new ModuleLiquidIndicator(32, 20, this));
		modules.add(redstoneControl);
		modules.add(traceToggle);
		//modules.add(toggleSwitch = new ModuleToggleSwitch(160, 5, 0, "", this, TextureResources.buttonToggleImage, 11, 26, getMachineEnabled()));
		return modules;
	}
	
	@Override
	public void setInventorySlotContents(int slot, @Nonnull ItemStack stack) {
		super.setInventorySlotContents(slot, stack);
		
		while(FluidUtils.attemptDrainContainerIInv(inventory, this.tank, getStackInSlot(0), 0, 1));
	}

	@Override
	public String getModularInventoryName() {
		return "block.advancedrocketry.oxygenvent";
	}

	@Override
	public boolean canInteractWithContainer(PlayerEntity entity) {
		return true;
	}

	@Override
	public boolean canFormBlob() {
		return isTurnedOn();
	}
	
	@Override
	public boolean isRunning() {
		return isSealed;
	}

	@Override
	public void onInventoryButtonPressed(ModuleButton buttonId) {
		if(buttonId == redstoneControl) {
			state = redstoneControl.getState();
			PacketHandler.sendToServer(new PacketMachine(this, PACKET_REDSTONE_ID));
		}
		if(buttonId == traceToggle) {
			allowTrace = traceToggle.getState();
			PacketHandler.sendToServer(new PacketMachine(this, PACKET_TRACE_ID));
		}
	}
	
	@Override
	public void writeDataToNetwork(PacketBuffer out, byte id) {
		if(id == PACKET_REDSTONE_ID)
			out.writeByte(state.ordinal());
		else if(id == PACKET_TRACE_ID)
			out.writeBoolean(allowTrace);
	}

	@Override
	public void readDataFromNetwork(PacketBuffer in, byte packetId,
			CompoundNBT nbt) {
		if(packetId == PACKET_REDSTONE_ID)
			nbt.putByte("state", in.readByte());
		else if(packetId == PACKET_TRACE_ID)
			nbt.putBoolean("trace", in.readBoolean());
	}

	@Override
	public void useNetworkData(PlayerEntity player, Dist side, byte id,
			CompoundNBT nbt) {
		if(id == PACKET_REDSTONE_ID)
			state = RedstoneState.values()[nbt.getByte("state")];
		else if(id == PACKET_TRACE_ID) {
			allowTrace = nbt.getBoolean("trace");
			if(!allowTrace)
				radius = -1;
		}
	}
	
	@Override
	public void read(BlockState blkstate, CompoundNBT nbt) {
		super.read(blkstate, nbt);
		
		state = RedstoneState.values()[nbt.getByte("redstoneState")];
		redstoneControl.setRedstoneState(state);
		allowTrace = nbt.getBoolean("allowtrace");

	}
	
	@Override
	public CompoundNBT write(CompoundNBT nbt) {
		super.write(nbt);
		nbt.putByte("redstoneState", (byte) state.ordinal());
		nbt.putBoolean("allowtrace", allowTrace);
		return nbt;
	}

	@Override
	public boolean isEmpty() {
		return inventory.isEmpty();
	}

	@Override
	public void stateUpdated(ModuleBase module) {
		if(module.equals(traceToggle)) {
			allowTrace = ((ModuleToggleSwitch)module).getState();
			PacketHandler.sendToServer(new PacketMachine(this, PACKET_TRACE_ID));
		}
	}

	@Nonnull
	@Override
	public ITextComponent getDisplayName() {
		return new TranslationTextComponent(getModularInventoryName());
	}

	@Override
	@ParametersAreNonnullByDefault
	public Container createMenu(int id, PlayerInventory inv, PlayerEntity player) {
		return new ContainerModular(LibvulpesGuiRegistry.CONTAINER_MODULAR_TILE, id, player, getModules(getModularInvType().ordinal(), player), this, getModularInvType());
	}

	@Override
	public GuiHandler.guiId getModularInvType() {
		return guiId.MODULAR;
	}
}