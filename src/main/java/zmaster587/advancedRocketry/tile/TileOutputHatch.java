package zmaster587.advancedRocketry.tile;

import java.util.LinkedList;
import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import zmaster587.advancedRocketry.inventory.modules.ModuleBase;
import zmaster587.advancedRocketry.inventory.modules.ModuleOutputSlotArray;
import zmaster587.advancedRocketry.tile.multiblock.TileInventoryHatch;

public class TileOutputHatch extends TileInventoryHatch {
	
	public TileOutputHatch() {
		super();
	}
	
	public TileOutputHatch(int size) {
		super(size);
	}
	
	@Override
	public String getModularInventoryName() {
		return "tile.hatch.1.name";
	}
	
	@Override
	public boolean canInsertItem(int p_102007_1_, ItemStack p_102007_2_,
			int p_102007_3_) {
		return false;
	}
	
	@Override
	public List<ModuleBase> getModules(int ID, EntityPlayer player) {
		LinkedList<ModuleBase> modules = new LinkedList<ModuleBase>();
		modules.add(new ModuleOutputSlotArray(8, 18, this, 0, this.getSizeInventory()));
		return modules;
	}
}
