package zmaster587.advancedRocketry.inventory;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IContainerListener;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import zmaster587.advancedRocketry.api.AdvancedRocketryItems;
import zmaster587.advancedRocketry.tile.multiblock.drill.TileOrbitalLaserDrill;
import zmaster587.libVulpes.gui.SlotSingleItem;

public class ContainerOrbitalLaserDrill extends Container {

	TileOrbitalLaserDrill laserTile;
	boolean finished, jammed;
	int prevEnergy = 0, prevLaserX = 0, prevLaserZ = 0, buildingX, buildingZ;
	TileOrbitalLaserDrill.MODE currMode;

	ContainerOrbitalLaserDrill(InventoryPlayer inventoryPlayer, TileOrbitalLaserDrill tile) {
		super();
		laserTile = tile;

		addSlotToContainer(new SlotSingleItem(tile,0,56,54, AdvancedRocketryItems.itemLens));

		// Player inventory
		for (int i1 = 0; i1 < 3; i1++) {
			for (int l1 = 0; l1 < 9; l1++) {
				addSlotToContainer(new Slot(inventoryPlayer, l1 + i1 * 9 + 9, 8 + l1 * 18, 89 + i1 * 18));
			}
		}
		// Player hotbar
		for (int j1 = 0; j1 < 9; j1++) {
			addSlotToContainer(new Slot(inventoryPlayer, j1, 8 + j1 * 18, 147));
		}

		currMode = laserTile.getMode();
		jammed = false;
		finished = false;
	}

	@Override
	public void detectAndSendChanges()
	{
		super.detectAndSendChanges();
		if(laserTile.getBatteries().getUniversalEnergyStored() != prevEnergy) {
			prevEnergy = laserTile.getBatteries().getUniversalEnergyStored() ;
			for (int j = 0; j < this.listeners.size(); ++j)
			{
				((IContainerListener)this.listeners.get(j)).sendWindowProperty(this, 0, prevEnergy/100);
			}
		}

		if(laserTile.laserX != prevLaserX) {
			prevLaserX = laserTile.laserX;


			for(int i = 0; i < this.listeners.size(); i++) {
				((IContainerListener)this.listeners.get(i)).sendWindowProperty(this, 1, prevLaserX & 65535);

				int j = prevLaserX >>> 16;
			//if(j != 0)
			((IContainerListener)this.listeners.get(i)).sendWindowProperty(this, 2, j);
			}
		}

		if(laserTile.laserZ != prevLaserZ) {
			prevLaserZ = laserTile.laserZ;

			for(int i = 0; i < this.listeners.size(); i++) {
				((IContainerListener)this.listeners.get(i)).sendWindowProperty(this, 3, prevLaserZ & 65535);

				int j = prevLaserZ >>> 16;
			//if(j != 0)
			((IContainerListener)this.listeners.get(i)).sendWindowProperty(this, 4, j);
			}
		}
		if(currMode.compareTo(laserTile.getMode()) != 0) {
			for(int i = 0; i < this.listeners.size(); i++) {
				((IContainerListener)this.listeners.get(i)).sendWindowProperty(this, 5, laserTile.getMode().ordinal());
			}
		}
		if(jammed != laserTile.isJammed()) {
			jammed = laserTile.isJammed();
			for(int i = 0; i < this.listeners.size(); i++) {
				((IContainerListener)this.listeners.get(i)).sendWindowProperty(this, 6, laserTile.isJammed() ? 1 : 0);
			}
		}
		if(finished != laserTile.isFinished()) {
			finished = laserTile.isFinished();
			for(int i = 0; i < this.listeners.size(); i++) {
				((IContainerListener)this.listeners.get(i)).sendWindowProperty(this, 7, laserTile.isFinished() ? 1 : 0);
			}
		}
	}

	@Override
	public void updateProgressBar(int id, int value) {
		if(id == 0) {
			laserTile.setEnergy(value*100);
		}
		else if(id == 1) {
			buildingX = value;
		}
		else if(id == 2) {
			buildingX |= value << 16;
			laserTile.laserX = buildingX;
			buildingX = 0;
		}
		else if(id == 3) {
			buildingZ = value;
		}
		else if(id == 4) {
			buildingZ |= value << 16;
			laserTile.laserZ = buildingZ;
			buildingZ = 0;
		}
		else if(id == 5) {
			laserTile.setMode(currMode.values()[value]);
		}
		else if(id == 6)
			laserTile.setJammed(value == 1 ? true : false);
		else if(id == 7)
			laserTile.setFinished(value == 1 ? true : false);
	}

	@Override
	public ItemStack transferStackInSlot(EntityPlayer player, int slot)
	{
		ItemStack stack = null;
		Slot slotObject = (Slot) inventorySlots.get(slot);
		//null checks and checks if the item can be stacked (maxStackSize > 1)
		if (slotObject != null && slotObject.getHasStack()) {

			ItemStack stackInSlot = slotObject.getStack();
			stack = stackInSlot.copy();

			//merges the item into player inventory since its in the tileEntity
			if (slot == 0) {
				if (!this.mergeItemStack(stackInSlot, 1, 35, true)) {
					return null;
				}
			}
			//placing it into the tileEntity is possible since its in the player inventory
			//check to make sure it's valid for the slot
			else if (!laserTile.isItemValidForSlot(0, stack) || !this.mergeItemStack(stackInSlot, 0, 1, false)) {
				return null;
			}

			if (stackInSlot.getCount() == 0) {
				slotObject.putStack(null);
			} else {
				slotObject.onSlotChanged();
			}

			if (stackInSlot.getCount() == stack.getCount()) {
				return null;
			}
			slotObject.onTake(player, stackInSlot);
		}

		return stack;
	}


	@Override
	public boolean canInteractWith(EntityPlayer entityplayer) {
		return laserTile.isUsableByPlayer(entityplayer);
	}
}
