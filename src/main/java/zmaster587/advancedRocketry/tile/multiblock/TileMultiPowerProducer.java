package zmaster587.advancedRocketry.tile.multiblock;

import java.util.LinkedList;
import java.util.List;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import cpw.mods.fml.relauncher.Side;
import zmaster587.advancedRocketry.inventory.TextureResources;
import zmaster587.advancedRocketry.inventory.modules.IModularInventory;
import zmaster587.advancedRocketry.inventory.modules.IToggleButton;
import zmaster587.advancedRocketry.inventory.modules.ModuleBase;
import zmaster587.advancedRocketry.inventory.modules.ModulePower;
import zmaster587.advancedRocketry.inventory.modules.ModuleToggleSwitch;
import zmaster587.advancedRocketry.network.PacketHandler;
import zmaster587.advancedRocketry.network.PacketMachine;
import zmaster587.advancedRocketry.tile.multiblock.TileMultiblockMachine.NetworkPackets;
import zmaster587.libVulpes.api.IUniversalEnergy;
import zmaster587.libVulpes.block.BlockMeta;
import zmaster587.libVulpes.util.INetworkMachine;
import zmaster587.libVulpes.util.MultiBattery;

public class TileMultiPowerProducer extends TileMultiBlock implements IToggleButton, IModularInventory, INetworkMachine {

	protected MultiBattery batteries = new MultiBattery();
	protected boolean enabled;
	private ModuleToggleSwitch toggleSwitch;

	public TileMultiPowerProducer() {
		super();
		enabled = false;
		toggleSwitch = new ModuleToggleSwitch(160, 5, 0, "", this, TextureResources.buttonToggleImage, 11, 26, getMachineEnabled());
	}

	public boolean getMachineEnabled() {
		return enabled;
	}

	public void setMachineEnabled(boolean enabled) {
		this.enabled = enabled;
		this.markDirty();
	}

	@Override
	public void useNetworkData(EntityPlayer player, Side side, byte id,
			NBTTagCompound nbt) {
		if (id == NetworkPackets.TOGGLE.ordinal()) {
			setMachineEnabled(nbt.getBoolean("enabled"));
			toggleSwitch.setToggleState(getMachineEnabled());

			//Last ditch effort to update the toggle switch when it's flipped
			if(!worldObj.isRemote)
				PacketHandler.sendToNearby(new PacketMachine(this, (byte)NetworkPackets.TOGGLE.ordinal()), worldObj.provider.dimensionId, xCoord, yCoord, zCoord, 64);
		}
	}
	
	@Override
	public void writeDataToNetwork(ByteBuf out, byte id) {
		if(id == NetworkPackets.TOGGLE.ordinal()) {
			out.writeBoolean(enabled);
		}
	}

	@Override
	public void readDataFromNetwork(ByteBuf in, byte packetId,
			NBTTagCompound nbt) {
		if(packetId == NetworkPackets.TOGGLE.ordinal()) {
			nbt.setBoolean("enabled", in.readBoolean());
		}
	}

	@Override
	public void onInventoryButtonPressed(int buttonId) {

		if(buttonId == 0) {
			this.setMachineEnabled(toggleSwitch.getState());
			PacketHandler.sendToServer(new PacketMachine(this,(byte)TileMultiblockMachine.NetworkPackets.TOGGLE.ordinal()));
		}
	}
	
	public MultiBattery getBatteries() {
		return batteries;
	}

	@Override
	public void stateUpdated(ModuleBase module) {
		if(module == toggleSwitch)
			setMachineEnabled(toggleSwitch.getState());
	}

	@Override
	public List<ModuleBase> getModules(int ID, EntityPlayer player) {
		LinkedList<ModuleBase> modules = new LinkedList<ModuleBase>();
		modules.add(new ModulePower(18, 20, getBatteries()));
		modules.add(toggleSwitch = new ModuleToggleSwitch(160, 5, 0, "", this, TextureResources.buttonToggleImage, 11, 26, getMachineEnabled()));

		return modules;
	}
	
	/**
	 * Handles distributing power production
	 */
	public void producePower(int amt) {
		batteries.acceptEnergy(amt, false);
	}
	
	@Override
	public String getModularInventoryName() {
		return getMachineName();
	}
	
	@Override
	public boolean canInteractWithContainer(EntityPlayer entity) {
		return isComplete();
	}
	
	public void resetCache() {
		batteries.clear();
		super.resetCache();
	}
	
	protected void integrateTile(TileEntity tile) {
		super.integrateTile(tile);

		for(BlockMeta block : getPowerOutputBlocks()) {
			if(block.getBlock() == worldObj.getBlock(tile.xCoord, tile.yCoord, tile.zCoord))
				batteries.addBattery((IUniversalEnergy) tile);
		}
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
	protected void writeNetworkData(NBTTagCompound nbt) {
		super.writeNetworkData(nbt);
		nbt.setBoolean("enabled", enabled);
	}
	
	@Override
	protected void readNetworkData(NBTTagCompound nbt) {
		super.readNetworkData(nbt);
		enabled = nbt.getBoolean("enabled");
	}
}
