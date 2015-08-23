package zmaster587.advancedRocketry.tile;

import java.util.List;

import net.minecraft.item.ItemStack;
import zmaster587.advancedRocketry.Inventory.modules.IModularInventory;
import zmaster587.advancedRocketry.Inventory.modules.ModuleBase;
import zmaster587.advancedRocketry.item.ItemPlanetIdentificationChip;
import zmaster587.advancedRocketry.tile.multiblock.TileInventoryHatch;

public class TileGuidanceComputer extends TileInventoryHatch implements IModularInventory {

	public TileGuidanceComputer() {
		super(1);
	}
	@Override
	public List<ModuleBase> getModules() {
		return super.getModules();
	}

	@Override
	public int getInventoryStackLimit() {
		return 1;
	}

	/**
	 * Gets the dimension to travel to if applicable
	 * @return The dimension to travel to or -1 if not valid
	 */
	public int getDestinationDimId() {
		ItemStack stack = getStackInSlot(0);

		if(stack != null && stack.getItem() instanceof ItemPlanetIdentificationChip) {
			ItemPlanetIdentificationChip item = (ItemPlanetIdentificationChip)stack.getItem();

			return item.getDimensionId(stack);
		}
		return -1;
	}

	@Override
	public String getModularInventoryName() {
		return "tile.guidanceComputer.name";
	}
}
