package zmaster587.advancedRocketry.tile.oxygen;

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
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import zmaster587.advancedRocketry.api.AdvancedRocketryBlocks;
import zmaster587.advancedRocketry.api.AdvancedRocketryFluids;
import zmaster587.advancedRocketry.api.Configuration;
import zmaster587.advancedRocketry.api.util.IBlobHandler;
import zmaster587.advancedRocketry.atmosphere.AtmosphereHandler;
import zmaster587.advancedRocketry.atmosphere.AtmosphereType;
import zmaster587.advancedRocketry.dimension.DimensionManager;
import zmaster587.advancedRocketry.inventory.TextureResources;
import zmaster587.advancedRocketry.api.AreaBlob;
import zmaster587.libVulpes.LibVulpes;
import zmaster587.libVulpes.api.IToggleableMachine;
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
import zmaster587.libVulpes.util.BlockPosition;
import zmaster587.libVulpes.util.IAdjBlockUpdate;
import zmaster587.libVulpes.util.INetworkMachine;
import zmaster587.libVulpes.util.ZUtils.RedstoneState;
import io.netty.buffer.ByteBuf;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import cpw.mods.fml.relauncher.Side;

public class TileOxygenVent extends TileInventoriedRFConsumerTank implements INetworkMachine, IBlobHandler, IModularInventory, IAdjBlockUpdate, IToggleableMachine, IButtonInventory {

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
		return AtmosphereHandler.hasAtmosphereHandler(this.worldObj.provider.dimensionId);
	}

	@Override
	public void updateEntity() {

		if(canPerformFunction()) {
			if(hasEnoughEnergy(getPowerPerOperation())) {
				performFunction();
				if(!worldObj.isRemote && isSealed) this.energy.extractEnergy(getPowerPerOperation(), false);
			}
			else
				notEnoughEnergyForFunction();
		}
		
		if(!soundInit && worldObj.isRemote) {
			LibVulpes.proxy.playSound(new RepeatingSound(TextureResources.sndHiss, this));
		}
		soundInit = true;
	}

	@Override
	public Packet getDescriptionPacket() {
		NBTTagCompound nbt = new NBTTagCompound();
		nbt.setBoolean("sealed", isSealed);
		return new S35PacketUpdateTileEntity(xCoord, yCoord, zCoord, 0, nbt);
	}
	
	@Override
	public void onDataPacket(NetworkManager net, S35PacketUpdateTileEntity pkt) {
		isSealed = pkt.func_148857_g().getBoolean("sealed");
	}
	
	@Override
	public World getWorld() {
		return getWorldObj();
	}

	@Override
	public void onAdjacentBlockUpdated() {
		if(isSealed) 
			activateAdjblocks();

		scrubbers.clear();
		TileEntity[] tiles = new TileEntity[4];
		tiles[0] = worldObj.getTileEntity(this.xCoord + 1, this.yCoord, this.zCoord);
		tiles[1] = worldObj.getTileEntity(this.xCoord - 1, this.yCoord, this.zCoord);
		tiles[2] = worldObj.getTileEntity(this.xCoord, this.yCoord, this.zCoord + 1);
		tiles[3] = worldObj.getTileEntity(this.xCoord, this.yCoord, this.zCoord - 1);

		for(TileEntity tile : tiles) {
			if(tile instanceof TileCO2Scrubber && worldObj.getBlock(tile.xCoord, tile.yCoord, tile.zCoord) == AdvancedRocketryBlocks.blockOxygenScrubber)
				scrubbers.add((TileCO2Scrubber)tile);
		}


	}

	private void activateAdjblocks() {
		numScrubbers = 0;
		numScrubbers = toggleAdjBlock(this.xCoord + 1, this.yCoord, this.zCoord, true) ? numScrubbers + 1 : numScrubbers;
		numScrubbers = toggleAdjBlock(this.xCoord - 1, this.yCoord, this.zCoord, true) ? numScrubbers + 1 : numScrubbers;
		numScrubbers = toggleAdjBlock(this.xCoord, this.yCoord, this.zCoord + 1, true) ? numScrubbers + 1 : numScrubbers;
		numScrubbers = toggleAdjBlock(this.xCoord, this.yCoord, this.zCoord - 1, true) ? numScrubbers + 1 : numScrubbers;
		
		markDirty();
		worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
	}

	private void deactivateAdjblocks() {
		toggleAdjBlock(this.xCoord + 1, this.yCoord, this.zCoord, false);
		toggleAdjBlock(this.xCoord - 1, this.yCoord, this.zCoord, false);
		toggleAdjBlock(this.xCoord, this.yCoord, this.zCoord + 1, false);
		toggleAdjBlock(this.xCoord, this.yCoord, this.zCoord - 1, false);
		
		markDirty();
		worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
	}

	private boolean toggleAdjBlock(int x, int y, int z, boolean on) {
		Block block = this.worldObj.getBlock(x,y,z);
		if(block == AdvancedRocketryBlocks.blockOxygenScrubber) {
			int meta = worldObj.getBlockMetadata(x,y,z);
			if(on && (meta & 8) == 0)
				worldObj.setBlockMetadataWithNotify(x, y, z, 8, 2);
			else if(!on && (meta & 8) == 8)
				worldObj.setBlockMetadataWithNotify(x, y, z, 0, 2);

			return true;
		}
		return false;
	}

	@Override
	public void invalidate() {
		super.invalidate();

		AtmosphereHandler handler = AtmosphereHandler.getOxygenHandler(this.worldObj.provider.dimensionId);
		if(handler != null)
			AtmosphereHandler.getOxygenHandler(this.worldObj.provider.dimensionId).unregisterBlob(this);
		deactivateAdjblocks();
	}

	@Override
	public int getPowerPerOperation() {
		return isSealed ? numScrubbers*10 + 1 : 0;
	}

	@Override
	public boolean canFill(ForgeDirection from, Fluid fluid) {
		return fluid == AdvancedRocketryFluids.fluidOxygen && super.canFill(from, fluid);
	}
	
	public boolean getEquivilentPower() {
		if(state == RedstoneState.OFF)
			return true;

		
		boolean state2 = worldObj.isBlockIndirectlyGettingPowered(xCoord, yCoord, zCoord);

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
		if(firstRun) {
			if(!worldObj.isRemote) {
				AtmosphereHandler.getOxygenHandler(this.worldObj.provider.dimensionId).registerBlob(this, xCoord, yCoord, zCoord);

				onAdjacentBlockUpdated();
				//isSealed starts as true so we can accurately check for scrubbers, we now set it to false to force the tile to check for a seal on first run
				isSealed = false;
				
			}
			firstRun = false;
		}

		if(!worldObj.isRemote) {

			if(isSealed && !getEquivilentPower() ) {
				AtmosphereHandler.getOxygenHandler(this.worldObj.provider.dimensionId).clearBlob(this);

				deactivateAdjblocks();

				isSealed = false;
			}
			else if(!isSealed && getEquivilentPower() ) {
				AtmosphereHandler.getOxygenHandler(this.worldObj.provider.dimensionId).addBlock(this, new BlockPosition(this.xCoord, this.yCoord, this.zCoord));
				isSealed = true;

				activateAdjblocks();
			}

			if(isSealed) {
				//if(worldObj.getTotalWorldTime() % 30 == 0)
					//worldObj.playSoundEffect(xCoord, yCoord, zCoord, "advancedrocketry:airHissLoop", 0.3f,  0.975f + worldObj.rand.nextFloat()*0.05f);

				//If scrubbers exist and the config allows then use the cartridge
				if(Configuration.scrubberRequiresCartrige){
					//TODO: could be optimized
					if(worldObj.getTotalWorldTime() % 20 == 0) {
						numScrubbers = 0;
						for(TileCO2Scrubber scrubber : scrubbers) {
							numScrubbers =  scrubber.useCharge() ? numScrubbers + 1 : numScrubbers;
						}
					}

				}

				int amtToDrain = (int) (AtmosphereHandler.getOxygenHandler(this.worldObj.provider.dimensionId).getBlobSize(this)*getGasUsageMultiplier());
				FluidStack drainedFluid = this.drain(ForgeDirection.UNKNOWN, amtToDrain, false);

				if( (drainedFluid != null && drainedFluid.amount >= amtToDrain) || amtToDrain == 0) {
					this.drain(ForgeDirection.UNKNOWN, amtToDrain, true);
					if(!hasFluid) {
						hasFluid = true;

						activateAdjblocks();

						AtmosphereHandler.getOxygenHandler(this.worldObj.provider.dimensionId).setAtmosphereType(this, AtmosphereType.PRESSURIZEDAIR);
					}
				}
				else if(hasFluid){
					AtmosphereHandler.getOxygenHandler(this.worldObj.provider.dimensionId).setAtmosphereType(this, DimensionManager.getInstance().getDimensionProperties(this.worldObj.provider.dimensionId).getAtmosphere());

					deactivateAdjblocks();

					hasFluid = false;
				}
			}
		}
	}

	public float getGasUsageMultiplier() {
		return Math.max(0.05f - numScrubbers*0.025f,0);
	}

	@Override
	public void notEnoughEnergyForFunction() {
		if(!worldObj.isRemote) {
			AtmosphereHandler handler = AtmosphereHandler.getOxygenHandler(this.worldObj.provider.dimensionId);
			if(handler != null)
				handler.clearBlob(this);

			deactivateAdjblocks();

			isSealed = false;
		}
	}

	@Override
	public int[] getAccessibleSlotsFromSide(int p_94128_1_) {
		return new int[]{};
	}

	@Override
	public boolean isItemValidForSlot(int slot, ItemStack itemStack) {
		return false;
	}

	@Override
	public boolean canBlobsOverlap(BlockPosition blockPosition, AreaBlob blob) {
		return false;
	}

	@Override
	public int getMaxBlobRadius() {
		return Configuration.oxygenVentSize;
	}

	@Override
	public BlockPosition getRootPosition() {
		return new BlockPosition(this.xCoord, this.yCoord, this.zCoord);
	}

	@Override
	public List<ModuleBase> getModules(int ID, EntityPlayer player) {
		ArrayList<ModuleBase> modules = new ArrayList<ModuleBase>();

		modules.add(new ModulePower(18, 20, this));
		modules.add(new ModuleLiquidIndicator(32, 20, this));
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
		return getEquivilentPower() ;
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
	public void writeToNBT(NBTTagCompound nbt) {
		super.writeToNBT(nbt);
		nbt.setByte("redstoneState", (byte) state.ordinal());
	}
}