package zmaster587.advancedRocketry.tile;

import net.minecraft.item.ItemStack;
import zmaster587.advancedRocketry.tile.multiblock.TileInventoryHatch;

public class TileInputHatch extends TileInventoryHatch {
	
	public TileInputHatch() {
		super();
	}
	
	public TileInputHatch(int size) {
		super(size);
	}
	
	@Override
	public String getInventoryName() {
		return "Input Hatch";
	}
	
	@Override
	public boolean canExtractItem(int p_102008_1_, ItemStack p_102008_2_,
			int p_102008_3_) {
		return false;
	}
}
