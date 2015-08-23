package zmaster587.advancedRocketry.tile.multiblock;

import java.util.HashSet;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.util.AxisAlignedBB;
import net.minecraftforge.common.util.ForgeDirection;
import zmaster587.advancedRocketry.Inventory.TextureResources;
import zmaster587.advancedRocketry.Inventory.modules.IModularInventory;
import zmaster587.advancedRocketry.Inventory.modules.ModuleBase;
import zmaster587.advancedRocketry.Inventory.modules.ModuleProgress;
import zmaster587.advancedRocketry.api.AdvRocketryBlocks;
import zmaster587.advancedRocketry.client.render.util.ProgressBarImage;
import zmaster587.advancedRocketry.recipe.RecipesMachine;
import zmaster587.libVulpes.interfaces.IRecipe;

public class TileCrystallizer extends TileMultiBlockMachine implements IModularInventory {
	@Override
	public List<IRecipe> getMachineRecipeList() {
		return RecipesMachine.getInstance().getRecipes(this.getClass());
	}

	@Override
	public Object[][][] getStructure() {
		return new Object[][][]{ {{AdvRocketryBlocks.blockQuartzCrucible, AdvRocketryBlocks.blockQuartzCrucible, AdvRocketryBlocks.blockQuartzCrucible},
								{AdvRocketryBlocks.blockQuartzCrucible, AdvRocketryBlocks.blockQuartzCrucible, AdvRocketryBlocks.blockQuartzCrucible}},
				
								  {{'*', 'c', '*'}, 
								  {'*', '*', '*'}},
								  
							     };
	}
	
	
	@Override
	public AxisAlignedBB getRenderBoundingBox() {
		return AxisAlignedBB.getBoundingBox(xCoord -2,yCoord -2, zCoord -2, xCoord + 2, yCoord + 2, zCoord + 2);
	}
	
	
	@Override
	protected HashSet<Block> getAllowableWildCardBlocks() {
		HashSet<Block> set = super.getAllowableWildCardBlocks();
		
		set.add(AdvRocketryBlocks.blockStructureBlock);
		return set;
	}
	
	@Override
	public boolean completeStructure() {
		return super.completeStructure();
	}
	
	@Override
	public List<ModuleBase> getModules() {
		List<ModuleBase> modules = super.getModules();
		
		modules.add(new ModuleProgress(100, 4, 0, new ProgressBarImage(0, 0, 31, 66, 31, 0, 23, 49, 4, 17, ForgeDirection.UP, TextureResources.progressBars), this));
		return modules;
	}
	
	@Override
	public String getMachineName() {
		return "container.crystallizer";
	}
}
