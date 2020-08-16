package zmaster587.advancedRocketry.tile.multiblock.machine;

import zmaster587.libVulpes.interfaces.IRecipe;
import zmaster587.libVulpes.tile.multiblock.TileMultiblockMachine;

import java.util.List;

import net.minecraft.tileentity.TileEntityType;

public class TileDistillationTower extends TileMultiblockMachine {

	public TileDistillationTower(TileEntityType<?> type) {
		super(type);
	}

	@Override
	public List<IRecipe> getMachineRecipeList() {
		return null;
	}
}
