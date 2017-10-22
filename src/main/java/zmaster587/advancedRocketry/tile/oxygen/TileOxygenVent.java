package zmaster587.advancedRocketry.tile.oxygen;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.relauncher.Side;
import zmaster587.advancedRocketry.api.AdvancedRocketryBlocks;
import zmaster587.advancedRocketry.api.AdvancedRocketryFluids;
import zmaster587.advancedRocketry.api.Configuration;
import zmaster587.advancedRocketry.api.util.IBlobHandler;
import zmaster587.advancedRocketry.atmosphere.AtmosphereHandler;
import zmaster587.advancedRocketry.atmosphere.AtmosphereType;
import zmaster587.advancedRocketry.dimension.DimensionManager;
import zmaster587.advancedRocketry.inventory.TextureResources;
import zmaster587.advancedRocketry.util.AudioRegistry;
import zmaster587.libVulpes.util.FluidUtils;
import zmaster587.advancedRocketry.api.AreaBlob;
import zmaster587.libVulpes.LibVulpes;
import zmaster587.libVulpes.api.IToggleableMachine;
import zmaster587.libVulpes.block.BlockTile;
import zmaster587.libVulpes.client.RepeatingSound;
import zmaster587.libVulpes.inventory.modules.IButtonInventory;
import zmaster587.libVulpes.inventory.modules.IModularInventory;
import zmaster587.libVulpes.inventory.modules.ModuleBase;
import zmaster587.libVulpes.inventory.modules.ModuleLiquidIndicator;
import zmaster587.libVulpes.inventory.modules.ModulePower;
import zmaster587.libVulpes.inventory.modules.ModuleRedstoneOutputButton;
import zmaster587.libVulpes.network.PacketHandler;
import zmaster587.libVulpes.network.PacketMachine;
import zmaster587.libVulpes.tile.TileInventoriedRFConsumerTank;
import zmaster587.libVulpes.util.HashedBlockPosition;
import zmaster587.libVulpes.util.IAdjBlockUpdate;
import zmaster587.libVulpes.util.INetworkMachine;
import zmaster587.libVulpes.util.ZUtils.RedstoneState;
import io.netty.buffer.ByteBuf;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class TileOxygenVent extends TileInventoriedRFConsumerTank implements IBlobHandler, IModularInventory, IAdjBlockUpdate, IToggleableMachine, IButtonInventory, INetworkMachine {

	boolean isSealed;
	boolean firstRun;
	boolean hasFluid;
	boolean soundInit;
	int numScrubbers;
	List<TileCO2Scrubber> scrubbers;
	
	RedstoneState state;
	ModuleRedstoneOutputButton redstoneControl;
	
	
	public TileOxygenVent() {
		super(1000,2, 1000);
		isSealed = true;
		firstRun = true;
		hasFluid = true;
		soundInit = false;
		numScrubbers = 0;
		scrubbers = new LinkedList<TileCO2Scrubber>();
		state = RedstoneState.ON;
		redstoneControl = new ModuleRedstoneOutputButton(174, 4, 0, "", this);
	}

	public TileOxygenVent(int energy, int invSize, int tankSize) {
		super(energy, invSize, tankSize);
		isSealed = false;
		firstRun = false;
		hasFluid = true;
		soundInit = false;
		scrubbers = new LinkedList<TileCO2Scrubber>();
		state = RedstoneState.ON;
		redstoneControl = new ModuleRedstoneOutputButton(174, 4, 0, "", this);
	}

	@Override
	public boolean canPerformFunction() {
		return AtmosphereHandler.hasAtmosphereHandler(this.worldObj.provider.getDimension());
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
		tiles[0] = worldObj.getTileEntity(pos.add(1,0,0));
		tiles[1] = worldObj.getTileEntity(pos.add(-1,0,0));
		tiles[2] = worldObj.getTileEntity(pos.add(0,0,1));
		tiles[3] = worldObj.getTileEntity(pos.add(0,0,-1));

		for(TileEntity tile : tiles) {
			if(tile instanceof TileCO2Scrubber && worldObj.getBlockState(tile.getPos()).getBlock() == AdvancedRocketryBlocks.blockOxygenScrubber)
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
		IBlockState state = this.worldObj.getBlockState(pos);
		Block block = state.getBlock();
		if(block == AdvancedRocketryBlocks.blockOxygenScrubber) {
			((BlockTile)block).setBlockState(worldObj, state, pos, on);

			return true;
		}
		return false;
	}

	@Override
	public void invalidate() {
		super.invalidate();

		AtmosphereHandler handler = AtmosphereHandler.getOxygenHandler(this.worldObj.provider.getDimension());
		if(handler != null)
			AtmosphereHandler.getOxygenHandler(this.worldObj.provider.getDimension()).unregisterBlob(this);
		deactivateAdjblocks();
	}

	@Override
	public int getPowerPerOperation() {
		return numScrubbers*10 + 1;
	}

	@Override
	public boolean canFill( Fluid fluid) {
		return FluidUtils.areFluidsSameType(fluid, AdvancedRocketryFluids.fluidOxygen) && super.canFill( fluid);
	}

	public boolean getEquivilentPower() {
		if(state == RedstoneState.OFF)
			return true;

		boolean state2 = worldObj.isBlockIndirectlyGettingPowered(pos) > 0;

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

		if(!worldObj.isRemote) {
			if(firstRun) {
				AtmosphereHandler.getOxygenHandler(this.worldObj.provider.getDimension()).registerBlob(this, pos);

				onAdjacentBlockUpdated();
				//isSealed starts as true so we can accurately check for scrubbers, we now set it to false to force the tile to check for a seal on first run
				setSealed(false);
				firstRun = false;
			}

			if(isSealed && !getEquivilentPower()) {
				AtmosphereHandler.getOxygenHandler(this.worldObj.provider.getDimension()).clearBlob(this);

				deactivateAdjblocks();

				setSealed(false);
			}
			else if(!isSealed && getEquivilentPower() && hasEnoughEnergy(getPowerPerOperation())) {
				setSealed(AtmosphereHandler.getOxygenHandler(this.worldObj.provider.getDimension()).addBlock(this, new HashedBlockPosition(pos)));


				if(isSealed)
					activateAdjblocks();
			}

			if(isSealed) {

				//If scrubbers exist and the config allows then use the cartridge
				if(Configuration.scrubberRequiresCartrige){
					//TODO: could be optimized
					if(worldObj.getTotalWorldTime() % 200 == 0) {
						numScrubbers = 0;
						for(TileCO2Scrubber scrubber : scrubbers) {
							numScrubbers = scrubber.useCharge() ? numScrubbers + 1 : numScrubbers;
						}
					}

				}

				int amtToDrain = (int) (AtmosphereHandler.getOxygenHandler(this.worldObj.provider.getDimension()).getBlobSize(this)*getGasUsageMultiplier());
				FluidStack drainedFluid = this.drain(amtToDrain, false);

				if( (drainedFluid != null && drainedFluid.amount >= amtToDrain) || amtToDrain == 0) {
					this.drain(amtToDrain, true);
					if(!hasFluid) {
						hasFluid = true;

						activateAdjblocks();

						AtmosphereHandler.getOxygenHandler(this.worldObj.provider.getDimension()).setAtmosphereType(this, AtmosphereType.PRESSURIZEDAIR);
					}
				}
				else if(hasFluid){
					AtmosphereHandler.getOxygenHandler(this.worldObj.provider.getDimension()).setAtmosphereType(this, DimensionManager.getInstance().getDimensionProperties(this.worldObj.provider.getDimension()).getAtmosphere());

					deactivateAdjblocks();

					hasFluid = false;
				}
			}
		}
	}

	@Override
	public void update() {

		if(canPerformFunction()) {

			if(hasEnoughEnergy(getPowerPerOperation())) {
				performFunction();
				if(!worldObj.isRemote && isSealed) this.energy.extractEnergy(getPowerPerOperation(), false);
			}
			else
				notEnoughEnergyForFunction();
		}
		if(!soundInit && worldObj.isRemote) {
			LibVulpes.proxy.playSound(new RepeatingSound(AudioRegistry.airHissLoop, SoundCategory.BLOCKS, this));
		}
		soundInit = true;
	}


	private void setSealed(boolean sealed) {
		boolean prevSealed = isSealed;
		if((prevSealed != sealed)) {
			markDirty();
			worldObj.notifyBlockUpdate(pos, worldObj.getBlockState(pos),  worldObj.getBlockState(pos), 2);
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
		if(!worldObj.isRemote) {
			AtmosphereHandler handler = AtmosphereHandler.getOxygenHandler(this.worldObj.provider.getDimension());
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
			state = redstoneControl.getState();
			PacketHandler.sendToServer(new PacketMachine(this, (byte)2));
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
	}
	
	@Override
	public void readFromNBT(NBTTagCompound nbt) {
		super.readFromNBT(nbt);
		
		state = RedstoneState.values()[nbt.getByte("redstoneState")];
		redstoneControl.setRedstoneState(state);

	}
	
	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound nbt) {
		super.writeToNBT(nbt);
		nbt.setByte("redstoneState", (byte) state.ordinal());
		return nbt;
	}
}