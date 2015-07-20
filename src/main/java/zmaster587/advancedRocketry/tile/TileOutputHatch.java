package zmaster587.advancedRocketry.tile;

import net.minecraft.item.ItemStack;
import zmaster587.advancedRocketry.tile.multiblock.TileInventoryHatch;

public class TileOutputHatch extends TileInventoryHatch {
	
	public TileOutputHatch() {
		super();
	}
	
	public TileOutputHatch(int size) {
		super(size);
	}
	
	@Override
	public String getInventoryName() {
		return "Output Hatch";
	}
	
	@Override
	public boolean canInsertItem(int p_102007_1_, ItemStack p_102007_2_,
			int p_102007_3_) {
		return false;
	}
}
