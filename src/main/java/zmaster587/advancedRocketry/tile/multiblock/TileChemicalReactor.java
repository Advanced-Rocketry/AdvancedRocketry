package zmaster587.advancedRocketry.tile.multiblock;

import java.util.List;

import net.minecraft.util.AxisAlignedBB;
import zmaster587.advancedRocketry.Inventory.TextureResources;
import zmaster587.advancedRocketry.Inventory.modules.ModuleBase;
import zmaster587.advancedRocketry.Inventory.modules.ModuleProgress;
import zmaster587.advancedRocketry.api.AdvancedRocketryBlocks;
import zmaster587.advancedRocketry.recipe.RecipesMachine;
import zmaster587.libVulpes.block.BlockMeta;
import zmaster587.libVulpes.interfaces.IRecipe;

public class TileChemicalReactor extends TileMultiblockMachine {
	public static final Object[][][] structure = { 
		{{'L', 'P','l'},
		{'L', new BlockMeta(AdvancedRocketryBlocks.blockStructureBlock),'l'}},
		
		{{'I', 'c', 'O'}, 
			{'I', new BlockMeta(AdvancedRocketryBlocks.blockStructureBlock), 'O'}},

	};
	
	@Override
	public List<IRecipe> getMachineRecipeList() {
		return RecipesMachine.getInstance().getRecipes(this.getClass());
	}
	
	@Override
	public Object[][][] getStructure() {
		return structure;
	}
	
	
	@Override
	public AxisAlignedBB getRenderBoundingBox() {
		return AxisAlignedBB.getBoundingBox(xCoord -2,yCoord -2, zCoord -2, xCoord + 2, yCoord + 2, zCoord + 2);
	}

	@Override
	public List<ModuleBase> getModules() {
		List<ModuleBase> modules = super.getModules();

		modules.add(new ModuleProgress(100, 4, 0, TextureResources.crystallizerProgressBar, this));
		return modules;
	}

	@Override
	public String getMachineName() {
		return "tile.chemreactor.name";
	}
}
