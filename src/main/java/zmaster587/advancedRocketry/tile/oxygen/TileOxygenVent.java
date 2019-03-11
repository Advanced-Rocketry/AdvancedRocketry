package zmaster587.advancedRocketry.tile.oxygen;

import io.netty.buffer.ByteBuf;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.relauncher.Side;
import zmaster587.advancedRocketry.api.AdvancedRocketryBlocks;
import zmaster587.advancedRocketry.api.AdvancedRocketryFluids;
import zmaster587.advancedRocketry.api.AreaBlob;
import zmaster587.advancedRocketry.api.Configuration;
import zmaster587.advancedRocketry.api.util.IBlobHandler;
import zmaster587.advancedRocketry.atmosphere.AtmosphereHandler;
import zmaster587.advancedRocketry.atmosphere.AtmosphereType;
import zmaster587.advancedRocketry.dimension.DimensionManager;
import zmaster587.advancedRocketry.inventory.TextureResources;
import zmaster587.advancedRocketry.util.AudioRegistry;
import zmaster587.libVulpes.LibVulpes;
import zmaster587.libVulpes.api.IToggleableMachine;
import zmaster587.libVulpes.block.BlockTile;
import zmaster587.libVulpes.client.RepeatingSound;
import zmaster587.libVulpes.inventory.modules.*;
import zmaster587.libVulpes.network.PacketHandler;
import zmaster587.libVulpes.network.PacketMachine;
import zmaster587.libVulpes.tile.TileInventoriedRFConsumerTank;
import zmaster587.libVulpes.util.FluidUtils;
import zmaster587.libVulpes.util.HashedBlockPosition;
import zmaster587.libVulpes.util.IAdjBlockUpdate;
import zmaster587.libVulpes.util.INetworkMachine;
import zmaster587.libVulpes.util.ZUtils.RedstoneState;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class TileOxygenVent extends TileInventoriedRFConsumerTank implements IBlobHandler, IModularInventory, INetworkMachine, IAdjBlockUpdate, IToggleableMachine, IButtonInventory, IToggleButton {

	boolean isSealed;
	boolean firstRun;
	boolean hasFluid;
	boolean soundInit;
	boolean allowTrace;
	int numScrubbers;
	List<TileCO2Scrubber> scrubbers;
	int radius = 0;
	
	final static byte PACKET_REDSTONE_ID = 2;
	final static byte PACKET_TRACE_ID = 3;
	
	RedstoneState state;
	ModuleRedstoneOutputButton redstoneControl;
	ModuleToggleSwitch traceToggle;
	
	
	public TileOxygenVent() {
		super(1000,2, 1000);
		isSealed = true;
		firstRun = true;
		hasFluid = true;
		soundInit = false;
		allowTrace = false;
		numScrubbers = 0;
		scrubbers = new LinkedList<TileCO2Scrubber>();
		state = RedstoneState.ON;
		redstoneControl = new ModuleRedstoneOutputButton(174, 4, PACKET_REDSTONE_ID, "", this);
		traceToggle = new ModuleToggleSwitch(80, 20, PACKET_TRACE_ID, LibVulpes.proxy.getLocalizedString("msg.vent.trace"), this, TextureResources.buttonGeneric, 80, 18, false);
	}

	public TileOxygenVent(int energy, int invSize, int tankSize) {
		super(energy, invSize, tankSize);
		isSealed = false;
		firstRun = false;
		hasFluid = true;
		soundInit = false;
		allowTrace = false;
		scrubbers = new LinkedList<TileCO2Scrubber>();
		state = RedstoneState.ON;
		redstoneControl = new ModuleRedstoneOutputButton(174, 4, 0, "", this);
		traceToggle = new ModuleToggleSwitch(80, 20, 5, LibVulpes.proxy.getLocalizedString("msg.vent.trace"), this, TextureResources.buttonGeneric, 80, 18, false);
	}

	@Override
	public boolean canPerformFunction() {
		return AtmosphereHandler.hasAtmosphereHandler(this.world.provider.getDimension());
	}

	@Override
	public World getWorldObj() {
		return getWorld();
	}

	@Override
	public void onAdjacentBlockUpdated() {

		if(isSealed)
			activateAdjblocks();
		scrubbers.clear();
		TileEntity[] tiles = new TileEntity[4];
		tiles[0] = world.getTileEntity(pos.add(1,0,0));
		tiles[1] = world.getTileEntity(pos.add(-1,0,0));
		tiles[2] = world.getTileEntity(pos.add(0,0,1));
		tiles[3] = world.getTileEntity(pos.add(0,0,-1));

		for(TileEntity tile : tiles) {
			if(tile instanceof TileCO2Scrubber && world.getBlockState(tile.getPos()).getBlock() == AdvancedRocketryBlocks.blockOxygenScrubber)
				scrubbers.add((TileCO2Scrubber)tile);
		}
	}

	private void activateAdjblocks() {
		numScrubbers = 0;
		numScrubbers = toggleAdjBlock(pos.add(1,0,0), true) ? numScrubbers + 1 : numScrubbers;
		numScrubbers = toggleAdjBlock(pos.add(-1,0,0), true) ? numScrubbers + 1 : numScrubbers;
		numScrubbers = toggleAdjBlock(pos.add(0,0,1), true) ? numScrubbers + 1 : numScrubbers;
		numScrubbers = toggleAdjBlock(pos.add(0,0,-1), true) ? numScrubbers + 1 : numScrubbers;
	}

	private void deactivateAdjblocks() {
		toggleAdjBlock(pos.add(1,0,0), false);
		toggleAdjBlock(pos.add(-1,0,0), false);
		toggleAdjBlock(pos.add(0,0,1), false);
		toggleAdjBlock(pos.add(0,0,-1), false);
	}

	private boolean toggleAdjBlock(BlockPos pos, boolean on) {
		IBlockState state = this.world.getBlockState(pos);
		Block block = state.getBlock();
		if(block == AdvancedRocketryBlocks.blockOxygenScrubber) {
			((BlockTile)block).setBlockState(world, state, pos, on);

			return true;
		}
		return false;
	}

	@Override
	public void invalidate() {
		super.invalidate();

		AtmosphereHandler handler = AtmosphereHandler.getOxygenHandler(this.world.provider.getDimension());
		if(handler != null)
			AtmosphereHandler.getOxygenHandler(this.world.provider.getDimension()).unregisterBlob(this);
		deactivateAdjblocks();
	}

	@Override
	public int getPowerPerOperation() {
		return (int)((numScrubbers*10 + 1)*Configuration.oxygenVentPowerMultiplier);
	}

	@Override
	public boolean canFill( Fluid fluid) {
		return FluidUtils.areFluidsSameType(fluid, AdvancedRocketryFluids.fluidOxygen) && super.canFill( fluid);
	}

	public boolean getEquivilentPower() {
		if(state == RedstoneState.OFF)
			return true;

		boolean state2 = world.isBlockIndirectlyGettingPowered(pos) > 0;

		if(state == RedstoneState.INVERTED)
			state2 = !state2;
		return state2;
	}
	
	@Override
	public void performFunction() {

		/*NB: canPerformFunction returns false and must return true for perform function to execute
		 *  if there is no O2 handler, this is why we can safely call AtmosphereHandler.getOxygenHandler
		 * And not have to worry about an NPE being thrown
		 */

		//IF first tick then register the blob and check for scrubbers

		if(!world.isRemote) {
			if(firstRun) {
				AtmosphereHandler.getOxygenHandler(this.world.provider.getDimension()).registerBlob(this, pos);

				onAdjacentBlockUpdated();
				//isSealed starts as true so we can accurately check for scrubbers, we now set it to false to force the tile to check for a seal on first run
				setSealed(false);
				firstRun = false;
			}
			
			if(isSealed && AtmosphereHandler.getOxygenHandler(this.world.provider.getDimension()).getBlobSize(this) == 0) {
				deactivateAdjblocks();
				setSealed(false);
			}

			if(isSealed && !getEquivilentPower()) {
				AtmosphereHandler.getOxygenHandler(this.world.provider.getDimension()).clearBlob(this);

				deactivateAdjblocks();

				setSealed(false);
			}
			else if(!isSealed && getEquivilentPower() && hasEnoughEnergy(getPowerPerOperation())) {
				
				if(world.getTotalWorldTime() % 100 == 0)
					setSealed(AtmosphereHandler.getOxygenHandler(this.world.provider.getDimension()).addBlock(this, new HashedBlockPosition(pos)));

				if(isSealed) {
					activateAdjblocks();
				}
				else if(world.getTotalWorldTime() % 10 == 0 && allowTrace) {
					radius++;
					if(radius > 128)
						radius = 0;
				}
			}

			if(isSealed) {

				//If scrubbers exist and the config allows then use the cartridge
				if(Configuration.scrubberRequiresCartrige){
					//TODO: could be optimized
					if(world.getTotalWorldTime() % 200 == 0) {
						numScrubbers = 0;
						for(TileCO2Scrubber scrubber : scrubbers) {
							numScrubbers = scrubber.useCharge() ? numScrubbers + 1 : numScrubbers;
						}
					}

				}

				int amtToDrain = (int)Math.ceil((AtmosphereHandler.getOxygenHandler(this.world.provider.getDimension()).getBlobSize(this)*getGasUsageMultiplier()));
				FluidStack drainedFluid = this.drain((int)amtToDrain, false);

				if( (drainedFluid != null && drainedFluid.amount >= amtToDrain) || amtToDrain == 0) {
					this.drain((int)amtToDrain, true);
					if(!hasFluid) {
						hasFluid = true;

						activateAdjblocks();

						AtmosphereHandler.getOxygenHandler(this.world.provider.getDimension()).setAtmosphereType(this, AtmosphereType.PRESSURIZEDAIR);
					}
				}
				else if(hasFluid){
					AtmosphereHandler.getOxygenHandler(this.world.provider.getDimension()).setAtmosphereType(this, DimensionManager.getInstance().getDimensionProperties(this.world.provider.getDimension()).getAtmosphere());

					deactivateAdjblocks();

					hasFluid = false;
				}
			}
		}
	}
	
	@Override
	public int getTraceDistance() {
		return allowTrace ? radius : -1;
	}

	@Override
	public void update() {
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
	public SPacketUpdateTileEntity getUpdatePacket() {
		return new SPacketUpdateTileEntity(pos,getBlockMetadata(), getUpdateTag());

	}

	@Override
	public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity pkt) {
		handleUpdateTag(pkt.getNbtCompound());
	}

	@Override
	public NBTTagCompound getUpdateTag() {
		NBTTagCompound tag = super.getUpdateTag();
		tag.setBoolean("isSealed", isSealed);
		
		return tag;
	}

	@Override
	public void handleUpdateTag(NBTTagCompound tag) {
		super.handleUpdateTag(tag);
		isSealed = tag.getBoolean("isSealed");
		
		if(isSealed) {
			activateAdjblocks();
		}
	}

	public float getGasUsageMultiplier() {
		return (float) (Math.max(0.01f - numScrubbers*0.005f,0)*Configuration.oxygenVentConsumptionMult);
	}

	@Override
	public void notEnoughEnergyForFunction() {
		if(!world.isRemote) {
			AtmosphereHandler handler = AtmosphereHandler.getOxygenHandler(this.world.provider.getDimension());
			if(handler != null)
				handler.clearBlob(this);

			deactivateAdjblocks();

			setSealed(false);
		}
	}

	
	
	@Override
	public int[] getSlotsForFace(EnumFacing side) {
		return new int[]{};
	}

	@Override
	public boolean isItemValidForSlot(int slot, ItemStack itemStack) {
		return false;
	}

	@Override
	public boolean canBlobsOverlap(HashedBlockPosition blockPosition, AreaBlob blob) {
		return false;
	}

	@Override
	public int getMaxBlobRadius() {
		return Configuration.oxygenVentSize;
	}

	@Override
	public HashedBlockPosition getRootPosition() {
		return new HashedBlockPosition(pos);
	}

	@Override
	public List<ModuleBase> getModules(int ID, EntityPlayer player) {
		ArrayList<ModuleBase> modules = new ArrayList<ModuleBase>();

		modules.add(new ModulePower(18, 20, this));
		modules.add(new ModuleLiquidIndicator(32, 20, this));
		modules.add(redstoneControl);
		modules.add(traceToggle);
		//modules.add(toggleSwitch = new ModuleToggleSwitch(160, 5, 0, "", this, TextureResources.buttonToggleImage, 11, 26, getMachineEnabled()));
		//TODO add itemStack slots for liqiuid
		return modules;
	}

	@Override
	public String getModularInventoryName() {
		return "OxygenVent";
	}

	@Override
	public boolean canInteractWithContainer(EntityPlayer entity) {
		return true;
	}

	@Override
	public boolean canFormBlob() {
		return getEquivilentPower();
	}
	
	@Override
	public boolean isRunning() {
		return isSealed;
	}

	@Override
	public void onInventoryButtonPressed(int buttonId) {
		if(buttonId == PACKET_REDSTONE_ID) {
			state = redstoneControl.getState();
			PacketHandler.sendToServer(new PacketMachine(this, PACKET_REDSTONE_ID));
		}
		if(buttonId == PACKET_TRACE_ID) {
			allowTrace = traceToggle.getState();
			PacketHandler.sendToServer(new PacketMachine(this, PACKET_TRACE_ID));
		}
	}
	
	@Override
	public void writeDataToNetwork(ByteBuf out, byte id) {
		if(id == PACKET_REDSTONE_ID)
			out.writeByte(state.ordinal());
		else if(id == PACKET_TRACE_ID)
			out.writeBoolean(allowTrace);
	}

	@Override
	public void readDataFromNetwork(ByteBuf in, byte packetId,
			NBTTagCompound nbt) {
		if(packetId == PACKET_REDSTONE_ID)
			nbt.setByte("state", in.readByte());
		else if(packetId == PACKET_TRACE_ID)
			nbt.setBoolean("trace", in.readBoolean());
	}

	@Override
	public void useNetworkData(EntityPlayer player, Side side, byte id,
			NBTTagCompound nbt) {
		if(id == PACKET_REDSTONE_ID)
			state = RedstoneState.values()[nbt.getByte("state")];
		else if(id == PACKET_TRACE_ID) {
			allowTrace = nbt.getBoolean("trace");
			if(!allowTrace)
				radius = -1;
		}
	}
	
	@Override
	public void readFromNBT(NBTTagCompound nbt) {
		super.readFromNBT(nbt);
		
		state = RedstoneState.values()[nbt.getByte("redstoneState")];
		redstoneControl.setRedstoneState(state);
		allowTrace = nbt.getBoolean("allowtrace");

	}
	
	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound nbt) {
		super.writeToNBT(nbt);
		nbt.setByte("redstoneState", (byte) state.ordinal());
		nbt.setBoolean("allowtrace", allowTrace);
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
}