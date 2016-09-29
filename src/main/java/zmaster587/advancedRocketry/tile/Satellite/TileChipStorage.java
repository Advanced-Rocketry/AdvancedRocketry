package zmaster587.advancedRocketry.tile.Satellite;

import java.util.LinkedList;
import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.fml.relauncher.Side;
import zmaster587.advancedRocketry.api.AdvancedRocketryItems;
import zmaster587.advancedRocketry.api.SatelliteRegistry;
import zmaster587.advancedRocketry.api.satellite.SatelliteProperties;
import zmaster587.advancedRocketry.dimension.DimensionManager;
import zmaster587.advancedRocketry.inventory.TextureResources;
import zmaster587.advancedRocketry.item.ItemSatellite;
import zmaster587.advancedRocketry.item.ItemSatelliteIdentificationChip;
import zmaster587.libVulpes.client.util.ProgressBarImage;
import zmaster587.libVulpes.inventory.modules.IButtonInventory;
import zmaster587.libVulpes.inventory.modules.IModularInventory;
import zmaster587.libVulpes.inventory.modules.ModuleBase;
import zmaster587.libVulpes.inventory.modules.ModuleButton;
import zmaster587.libVulpes.inventory.modules.ModuleOutputSlotArray;
import zmaster587.libVulpes.inventory.modules.ModulePower;
import zmaster587.libVulpes.inventory.modules.ModuleProgress;
import zmaster587.libVulpes.inventory.modules.ModuleTexturedSlotArray;
import zmaster587.libVulpes.network.PacketHandler;
import zmaster587.libVulpes.network.PacketMachine;
import zmaster587.libVulpes.tile.multiblock.TileMultiPowerConsumer;

public class TileChipStorage extends TileMultiPowerConsumer  implements IModularInventory, IInventory, IButtonInventory {

	public static final Object[][][] structure = new Object[][][] {
		{{'c'}},
		{{'*'}}
	};

	
	ItemStack inventory[];

	@Override
	protected void processComplete() {
		super.processComplete();
		
		ItemStack stack0 = getStackInSlot(0);
		ItemStack stack1 = getStackInSlot(1);
		
		stack1.setTagCompound(stack0.getTagCompound());
	}

	@Override
	public void useNetworkData(EntityPlayer player, Side side, byte id,
			NBTTagCompound nbt) {
		super.useNetworkData(player, side, id, nbt);

		if(id == 100) {
			if(canCopy())
				beginCopy();
		}
	}

	private void beginCopy() {
		completionTime = 100;
	}
	
	private boolean canCopy() {
		ItemStack stack0 = getStackInSlot(0);
		ItemStack stack1 = getStackInSlot(1);

		return !isRunning() && stack0 != null && stack0.getItem() instanceof ItemSatelliteIdentificationChip && stack0.hasTagCompound() && stack1 != null && stack1.getItem() instanceof ItemSatelliteIdentificationChip;
	}

	@Override
	public List<ModuleBase> getModules(int ID, EntityPlayer player) {
		List<ModuleBase> modules = new LinkedList<ModuleBase>();

		modules.add(new ModulePower(18, 20, getBatteries()));
		modules.add(new ModuleTexturedSlotArray(58, 16, this, 0, 1, TextureResources.idChip));   // Output
		modules.add(new ModuleTexturedSlotArray(58, 36, this, 1, 2, TextureResources.idChip)); 	// Id chip
		modules.add(new ModuleProgress(75, 36, 0, new ProgressBarImage(217,0, 17, 17, 234, 0, EnumFacing.DOWN, TextureResources.progressBars), this));
		modules.add(new ModuleButton(40, 56, 0, "Copy", this,  zmaster587.libVulpes.inventory.TextureResources.buttonBuild));
		return modules;
	}

	@Override
	public void onInventoryButtonPressed(int buttonId) {
		PacketHandler.sendToServer(new PacketMachine(this, (byte)(buttonId + 100)) );
	}

	@Override
	public String getMachineName() {
		return "tile.satelliteBuilder.name";
	}

	@Override
	public int getSizeInventory() {
		return inventory.length - 1;
	}

	@Override
	public ItemStack getStackInSlot(int slot) {
		return inventory[slot];
	}

	@Override
	public ItemStack decrStackSize(int slot, int amt) {
		if(inventory[slot] != null) {
			ItemStack stack = inventory[slot].splitStack(amt);

			if(inventory[slot].stackSize == 0)
				inventory[slot] = null;
			return stack;
		}
		return null;
	}

	@Override
	public void setInventorySlotContents(int slot, ItemStack stack) {
		inventory[slot] = stack;

		if(isRunning() && slot > 2) {
			super.processComplete();
		}
	}

	@Override
	public boolean hasCustomName() {
		return false;
	}

	@Override
	public int getInventoryStackLimit() {
		return 64;
	}

	@Override
	public boolean isUseableByPlayer(EntityPlayer player) {
		return player.getDistanceSq(pos) < 4092;
	}

	@Override
	public void openInventory(EntityPlayer entity) {}

	@Override
	public void closeInventory(EntityPlayer entity) {}

	@Override
	public boolean isItemValidForSlot(int slot, ItemStack stack) {
		return true;
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound nbt) {
		super.writeToNBT(nbt);

		NBTTagList list = new NBTTagList();
		for(int i = 0; i < inventory.length; i++)
		{
			ItemStack stack = inventory[i];

			if(stack != null) {
				NBTTagCompound tag = new NBTTagCompound();
				tag.setByte("Slot", (byte)(i));
				stack.writeToNBT(tag);
				list.appendTag(tag);
			}
		}

		nbt.setTag("outputItems", list);
		return nbt;
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt) {
		super.readFromNBT(nbt);

		NBTTagList list = nbt.getTagList("outputItems", 10);

		for (int i = 0; i < list.tagCount(); i++) {
			NBTTagCompound tag = (NBTTagCompound) list.getCompoundTagAt(i);
			byte slot = tag.getByte("Slot");
			if (slot >= 0 && slot < inventory.length) {
				inventory[slot] = ItemStack.loadItemStackFromNBT(tag);
			}
		}
	}

	@Override
	public String getName() {
		return null;
	}

	@Override
	public ItemStack removeStackFromSlot(int index) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int getField(int id) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void setField(int id, int value) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public int getFieldCount() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void clear() {
		// TODO Auto-generated method stub
		
	}
}
