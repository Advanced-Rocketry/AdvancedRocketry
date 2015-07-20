package zmaster587.advancedRocketry.Inventory;

import zmaster587.libVulpes.api.IUniversalEnergy;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ICrafting;
import net.minecraftforge.common.util.ForgeDirection;

public class ContainerPowered extends Container {

	int currentId = 0;
	protected IUniversalEnergy tile;
	int prevRF;

	public ContainerPowered(InventoryPlayer playerInv, IUniversalEnergy tile2) {
		this.tile = tile2;
	}

	//Gets the next slot, used for updateProgressBar
	protected int nextSlot() {
		return currentId++;
	}
	
	@Override
	public void addCraftingToCrafters(ICrafting crafter) {
		super.addCraftingToCrafters(crafter);

		this.prevRF = tile.getEnergyStored(ForgeDirection.UNKNOWN);
		crafter.sendProgressBarUpdate(this, -1, this.prevRF);
	}

	@Override
	public void detectAndSendChanges() {
		super.detectAndSendChanges();

		if(this.prevRF != tile.getEnergyStored(ForgeDirection.UNKNOWN)) {
			this.prevRF = tile.getEnergyStored(ForgeDirection.UNKNOWN);
			
			for (int j = 0; j < this.crafters.size(); ++j) {
				((ICrafting)this.crafters.get(j)).sendProgressBarUpdate(this, -1, this.prevRF);
			}
		}
	}
	
	@Override
	public void updateProgressBar(int slot, int value) {
		super.updateProgressBar(slot, value);
		
		if(slot == -1) {
			tile.setEnergyStored(value);
		}
	}

	@Override
	public boolean canInteractWith(EntityPlayer player) {
		return true;
	}
}