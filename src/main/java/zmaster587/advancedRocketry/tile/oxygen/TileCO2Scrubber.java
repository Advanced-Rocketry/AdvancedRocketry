package zmaster587.advancedRocketry.tile.oxygen;

import net.minecraft.item.ItemStack;
import zmaster587.advancedRocketry.api.AdvancedRocketryItems;
import zmaster587.libVulpes.tile.multiblock.hatch.TileInventoryHatch;

public class TileCO2Scrubber extends TileInventoryHatch {
	public TileCO2Scrubber() {
		super(1);
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
		if(stack != null && stack.getItem() == AdvancedRocketryItems.itemCarbonScrubberCartridge) {
			
			if(stack.getItemDamage() != stack.getMaxDamage()) {
				stack.setItemDamage(stack.getItemDamage() + 1);
				//this.markDirty();
				return true;
			}
		}
		return false;
	}
}
