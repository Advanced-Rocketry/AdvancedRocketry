package zmaster587.advancedRocketry.Inventory;

import zmaster587.advancedRocketry.tile.TileEntityFuelingStation;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Slot;

public class ContainerFuelingStation extends ContainerPoweredLiquid {

	
	TileEntityFuelingStation tile;

	public ContainerFuelingStation(InventoryPlayer playerInv, TileEntityFuelingStation tile) {
		super(playerInv, tile);
		this.tile = tile;
		
		addSlotToContainer(new Slot(tile, 0, 45, 18));
		addSlotToContainer(new Slot(tile, 1, 45, 54));
		
		// Player inventory
		for (int i1 = 0; i1 < 3; i1++) {
			for (int l1 = 0; l1 < 9; l1++) {
				addSlotToContainer(new Slot(playerInv, l1 + i1 * 9 + 9, 8 + l1 * 18, 89 + i1 * 18));
			}
		}

		// Player hotbar
		for (int j1 = 0; j1 < 9; j1++) {
			addSlotToContainer(new Slot(playerInv, j1, 8 + j1 * 18, 147));
		}
	}

	@Override
	public boolean canInteractWith(EntityPlayer player) {
		return player.getDistance(tile.xCoord, tile.yCoord, tile.zCoord) < 64;
	}

}
