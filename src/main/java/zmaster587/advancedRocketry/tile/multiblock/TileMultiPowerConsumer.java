package zmaster587.advancedRocketry.tile.multiblock;

import java.util.LinkedList;
import java.util.List;

import io.netty.buffer.ByteBuf;
import cpw.mods.fml.relauncher.Side;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import zmaster587.advancedRocketry.inventory.TextureResources;
import zmaster587.advancedRocketry.inventory.modules.IModularInventory;
import zmaster587.advancedRocketry.inventory.modules.IProgressBar;
import zmaster587.advancedRocketry.inventory.modules.IToggleButton;
import zmaster587.advancedRocketry.inventory.modules.ModuleBase;
import zmaster587.advancedRocketry.inventory.modules.ModulePower;
import zmaster587.advancedRocketry.inventory.modules.ModuleToggleSwitch;
import zmaster587.advancedRocketry.network.PacketHandler;
import zmaster587.advancedRocketry.network.PacketMachine;
import zmaster587.advancedRocketry.tile.TileRFPlug;
import zmaster587.advancedRocketry.tile.multiblock.TileMultiblockMachine.NetworkPackets;
import zmaster587.libVulpes.api.IUniversalEnergy;
import zmaster587.libVulpes.block.BlockMeta;
import zmaster587.libVulpes.util.INetworkMachine;
import zmaster587.libVulpes.util.MultiBattery;

public class TileMultiPowerConsumer extends TileMultiBlock implements INetworkMachine, IModularInventory, IProgressBar, IToggleButton {

	protected MultiBattery batteries = new MultiBattery();

	protected int completionTime, currentTime;
	protected int powerPerTick;
	protected boolean enabled;
	private ModuleToggleSwitch toggleSwitch;
	//On server determines change in power state, on client determines last power state on server
	boolean hadPowerLastTick = true;

	public TileMultiPowerConsumer() {
		super();
		enabled = false;
		completionTime = -1;
		currentTime = -1;
		hadPowerLastTick = true;
		toggleSwitch = new ModuleToggleSwitch(160, 5, 0, "", this, TextureResources.buttonToggleImage, 11, 26, getMachineEnabled());
	}

	//Needed for GUI stuff
	public MultiBattery getBatteries() {
		return batteries;
	}

	@Override
	public int getProgress(int id) {
		return currentTime;
	}

	@Override
	public int getTotalProgress(int id) {
		return completionTime;
	}

	@Override
	public void setProgress(int id, int progress) {
		currentTime = progress;
	}

	@Override
	public void setTotalProgress(int id, int progress) {
		completionTime = progress;
	}

	@Override
	public float getNormallizedProgress(int id) {

		return completionTime > 0 ? currentTime/(float)completionTime : 0f;
	}

	@Override
	public boolean canUpdate() {
		return true;
	}

	@Override
	public void updateEntity() {
		super.updateEntity();

		//Freaky jenky crap to make sure the multiblock loads on chunkload etc
		if(timeAlive == 0 && !worldObj.isRemote) {
			if(isComplete())
				canRender = completeStructure = completeStructure();
			timeAlive = 0x1;
		}
		
		if(!worldObj.isRemote && worldObj.getTotalWorldTime() % 1000L == 0 && !isComplete()) {
			attemptCompleteStructure();
			markDirty();
			worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
		}
		
		if(isRunning()) {
			if(hasEnergy(powerPerTick) || (worldObj.isRemote && hadPowerLastTick)) {

				onRunningPoweredTick();

				//If server then check to see if we need to update the client, use power and process output if applicable
				if(!worldObj.isRemote) {

					if(!hadPowerLastTick) {
						hadPowerLastTick = true;
						markDirty();
						worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
					}

					useEnergy(powerPerTick);
				}
			}
			else if(!worldObj.isRemote && hadPowerLastTick) { //If server and out of power check to see if client needs update
				hadPowerLastTick = false;
				markDirty();
				worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
			}
		}
	}

	protected void onRunningPoweredTick() {
		//Increment for both client and server
		currentTime++;

		if(currentTime == completionTime)
			processComplete();
	}

	public void setMachineEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	public boolean getMachineEnabled() {
		return enabled;
	}

	public void resetCache() {
		batteries.clear();
	}

	/**
	 * @param world world
	 * @param destroyedX x coord of destroyed block
	 * @param destroyedY y coord of destroyed block
	 * @param destroyedZ z coord of destroyed block
	 * @param blockBroken set true if the block is being broken, otherwise some other means is being used to disassemble the machine
	 */
	public void deconstructMultiBlock(World world, int destroyedX, int destroyedY, int destroyedZ, boolean blockBroken) {
		resetCache();
		completionTime = 0;
		currentTime = 0;
		enabled = false;

		super.deconstructMultiBlock(world, destroyedX, destroyedY, destroyedZ, blockBroken);
	}

	protected void processComplete() {
		completionTime = 0;
		currentTime = 0;

		this.markDirty();
		worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
	}

	/**
	 * True if the machine is running
	 * @return true if the machine is currently processing something, or more formally, if completionTime > 0
	 */
	public boolean isRunning() {
		return completionTime > 0 && isComplete();
	}

	public void useEnergy(int amt) {
		batteries.extractEnergy(amt, false);
	}

	public boolean hasEnergy(int amt) {
		return batteries.getEnergyStored() >= amt;
	}

	public void setMachineRunning(boolean running) {
		if(running && this.getBlockMetadata() < 8) {
			this.blockMetadata = getBlockMetadata() | 8;
			worldObj.setBlockMetadataWithNotify(xCoord, yCoord, zCoord, this.blockMetadata, 2);
		}
		else if(!running && this.blockMetadata >= 8) {
			this.blockMetadata = getBlockMetadata() & 7;
			worldObj.setBlockMetadataWithNotify(xCoord, yCoord, zCoord, this.blockMetadata, 2); //Turn off machine
		}
	}

	@Override
	protected void integrateTile(TileEntity tile) {
		super.integrateTile(tile);

		for(BlockMeta block :getPowerInputBlocks()) {
			if(block.getBlock() == worldObj.getBlock(tile.xCoord, tile.yCoord, tile.zCoord))
				batteries.addBattery((IUniversalEnergy) tile);
		}
	}

	@Override
	protected void writeNetworkData(NBTTagCompound nbt) {
		super.writeNetworkData(nbt);
		nbt.setInteger("completionTime", this.completionTime);
		nbt.setInteger("currentTime", this.currentTime);
		nbt.setInteger("powerPerTick", this.powerPerTick);
		nbt.setBoolean("enabled", enabled);
	}
	
	@Override
	protected void readNetworkData(NBTTagCompound nbt) {
		super.readNetworkData(nbt);
		completionTime = nbt.getInteger("completionTime");
		currentTime = nbt.getInteger("currentTime");
		powerPerTick = nbt.getInteger("powerPerTick");
		enabled = nbt.getBoolean("enabled");
	}

	@Override
	public void writeDataToNetwork(ByteBuf out, byte id) {

		if(id == NetworkPackets.POWERERROR.ordinal()) {
			out.writeBoolean(hadPowerLastTick);
		}
		else if(id == NetworkPackets.TOGGLE.ordinal()) {
			out.writeBoolean(enabled);
		}
	}

	@Override
	public void readDataFromNetwork(ByteBuf in, byte packetId,
			NBTTagCompound nbt) {
		if(packetId == NetworkPackets.POWERERROR.ordinal()) {
			nbt.setBoolean("hadPowerLastTick", in.readBoolean());
		}
		else if(packetId == NetworkPackets.TOGGLE.ordinal()) {
			nbt.setBoolean("enabled", in.readBoolean());
		}
	}

	@Override
	public void useNetworkData(EntityPlayer player, Side side, byte id,
			NBTTagCompound nbt) {

		if(id == NetworkPackets.POWERERROR.ordinal()) {
			hadPowerLastTick = nbt.getBoolean("hadPowerLastTick");
		} else if (id == NetworkPackets.TOGGLE.ordinal()) {
			setMachineEnabled(nbt.getBoolean("enabled"));
			toggleSwitch.setToggleState(getMachineEnabled());

			//Last ditch effort to update the toggle switch when it's flipped
			if(!worldObj.isRemote)
				PacketHandler.sendToNearby(new PacketMachine(this, (byte)NetworkPackets.TOGGLE.ordinal()), worldObj.provider.dimensionId, xCoord, yCoord, zCoord, 64);
		}
	}

	@Override
	public List<ModuleBase> getModules() {
		LinkedList<ModuleBase> modules = new LinkedList<ModuleBase>();
		modules.add(new ModulePower(18, 20, getBatteries()));
		modules.add(toggleSwitch = new ModuleToggleSwitch(160, 5, 0, "", this, TextureResources.buttonToggleImage, 11, 26, getMachineEnabled()));

		return modules;
	}

	@Override
	public String getModularInventoryName() {
		return getMachineName();
	}

	@Override
	public void onInventoryButtonPressed(int buttonId) {

		if(buttonId == 0) {
			this.setMachineEnabled(toggleSwitch.getState());
			PacketHandler.sendToServer(new PacketMachine(this,(byte)TileMultiblockMachine.NetworkPackets.TOGGLE.ordinal()));
		}
	}

	@Override
	public void stateUpdated(ModuleBase module) {
		if(module == toggleSwitch)
			setMachineEnabled(toggleSwitch.getState());
	}

	@Override
	public boolean canInteractWithContainer(EntityPlayer entity) {
		return isComplete();
	}
}
