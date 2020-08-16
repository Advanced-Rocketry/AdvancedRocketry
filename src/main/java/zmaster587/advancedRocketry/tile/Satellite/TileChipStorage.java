package zmaster587.advancedRocketry.tile.Satellite;

import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.api.distmarker.Dist;
import zmaster587.advancedRocketry.api.AdvancedRocketryTileEntityType;
import zmaster587.advancedRocketry.inventory.TextureResources;
import zmaster587.advancedRocketry.item.ItemSatelliteIdentificationChip;
import zmaster587.libVulpes.client.util.ProgressBarImage;
import zmaster587.libVulpes.inventory.modules.*;
import zmaster587.libVulpes.network.PacketHandler;
import zmaster587.libVulpes.network.PacketMachine;
import zmaster587.libVulpes.tile.multiblock.TileMultiPowerConsumer;

import java.util.LinkedList;
import java.util.List;

public class TileChipStorage extends TileMultiPowerConsumer  implements IModularInventory, IInventory, IButtonInventory {

	public static final Object[][][] structure = new Object[][][] {
		{{'c'}},
		{{'*'}}
	};

	public TileChipStorage() {
		super(AdvancedRocketryTileEntityType.TILE_CHIP_STORAGE);
	}
	
	ItemStack inventory[];

	@Override
	protected void processComplete() {
		super.processComplete();
		
		ItemStack stack0 = getStackInSlot(0);
		ItemStack stack1 = getStackInSlot(1);
		
		stack1.setTag(stack0.getTag());
	}

	@Override
	public void useNetworkData(PlayerEntity player, Dist side, byte id,
			CompoundNBT nbt) {
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

		return !isRunning() && stack0 != null && stack0.getItem() instanceof ItemSatelliteIdentificationChip && stack0.hasTag() && stack1 != null && stack1.getItem() instanceof ItemSatelliteIdentificationChip;
	}

	@Override
	public List<ModuleBase> getModules(int ID, PlayerEntity player) {
		List<ModuleBase> modules = new LinkedList<ModuleBase>();

		modules.add(new ModulePower(18, 20, getBatteries()));
		modules.add(new ModuleTexturedSlotArray(58, 16, this, 0, 1, TextureResources.idChip));   // Output
		modules.add(new ModuleTexturedSlotArray(58, 36, this, 1, 2, TextureResources.idChip)); 	// Id chip
		modules.add(new ModuleProgress(75, 36, 0, new ProgressBarImage(217,0, 17, 17, 234, 0, Direction.DOWN, TextureResources.progressBars), this));
		modules.add(new ModuleButton(40, 56, "Copy", this,  zmaster587.libVulpes.inventory.TextureResources.buttonBuild).setAdditionalData(0));
		return modules;
	}

	@Override
	public void onInventoryButtonPressed(ModuleButton buttonId) {
		if(buttonId.getAdditionalData() != null && (int)buttonId.getAdditionalData() == 0)
			PacketHandler.sendToServer(new PacketMachine(this, (byte)(100)) );
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
			ItemStack stack = inventory[slot].split(amt);

			if(inventory[slot].getCount() == 0)
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
	public int getInventoryStackLimit() {
		return 64;
	}

	@Override
	public boolean isUsableByPlayer(PlayerEntity player) {
		return pos.distanceSq(new BlockPos(player.getPositionVec())) < 4096;
	}

	@Override
	public void openInventory(PlayerEntity entity) {}

	@Override
	public void closeInventory(PlayerEntity entity) {}

	@Override
	public boolean isItemValidForSlot(int slot, ItemStack stack) {
		return true;
	}

	@Override
	public CompoundNBT write(CompoundNBT nbt) {
		super.write(nbt);

		ListNBT list = new ListNBT();
		for(int i = 0; i < inventory.length; i++)
		{
			ItemStack stack = inventory[i];

			if(stack != null) {
				CompoundNBT tag = new CompoundNBT();
				tag.putByte("Slot", (byte)(i));
				stack.write(tag);
				list.add(tag);
			}
		}

		nbt.put("outputItems", list);
		return nbt;
	}

	@Override
	public void func_230337_a_(BlockState state, CompoundNBT nbt) {
		super.func_230337_a_(state, nbt);

		ListNBT list = nbt.getList("outputItems", 10);

		for (int i = 0; i < list.size(); i++) {
			CompoundNBT tag = (CompoundNBT) list.getCompound(i);
			byte slot = tag.getByte("Slot");
			if (slot >= 0 && slot < inventory.length) {
				inventory[slot] = ItemStack.read(tag);
			}
		}
	}
	@Override
	public ItemStack removeStackFromSlot(int index) {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public void clear() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean isEmpty() {
		return false;
	}
}
