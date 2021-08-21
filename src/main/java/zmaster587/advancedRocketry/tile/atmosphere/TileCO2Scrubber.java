package zmaster587.advancedRocketry.tile.atmosphere;

import net.minecraft.item.ItemStack;
import zmaster587.advancedRocketry.api.AdvancedRocketryItems;
import zmaster587.libVulpes.tile.IComparatorOverride;
import zmaster587.libVulpes.tile.multiblock.hatch.TileInventoryHatch;

public class TileCO2Scrubber extends TileInventoryHatch implements IComparatorOverride {
	public TileCO2Scrubber() {
		super(1);
		inventory.setCanInsertSlot(0, true);
		inventory.setCanExtractSlot(0, true);
	}
	
	@Override
	public String getModularInventoryName() {
		return "tile.scrubber.name";
	}
	
	@Override
	public int getInventoryStackLimit() {
		return 1;
	}
	
	public boolean useCharge() {
		ItemStack stack = getStackInSlot(0);
		if(!stack.isEmpty() && stack.getItem() == AdvancedRocketryItems.itemCarbonScrubberCartridge) {
			
			if(stack.getItemDamage() != stack.getMaxDamage()) {
				stack.setItemDamage(stack.getItemDamage() + 1);
				if ((32766 - stack.getItemDamage() + 2184)/2185 != (32766 - stack.getItemDamage() + 1 + 2184)/2185)
				    this.markDirty();
				return true;
			}
		}
		return false;
	}

	@Override
	public int getComparatorOverride() {
		ItemStack stack = getStackInSlot(0);
		if (!stack.isEmpty()) {
			return (32766 - stack.getItemDamage() + 2184)/2185;
		}
		return 0;
	}
}
