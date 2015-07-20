package zmaster587.advancedRocketry.tile.multiblock;

import java.util.HashSet;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.util.AxisAlignedBB;
import zmaster587.advancedRocketry.AdvancedRocketry;
import zmaster587.advancedRocketry.api.AdvRocketryBlocks;
import zmaster587.advancedRocketry.recipe.RecipesMachine;
import zmaster587.libVulpes.interfaces.IRecipe;

public class TileCrystallizer extends TileMultiBlockMachine {
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
	
	public boolean completeStructure() {
		boolean result = super.completeStructure();
		if(result) {
			worldObj.setBlockMetadataWithNotify(xCoord, yCoord, zCoord, this.blockMetadata | 8, 2);
		}
		else
			worldObj.setBlockMetadataWithNotify(xCoord, yCoord, zCoord, this.blockMetadata & 7, 2);
		
		return result;
	}
	
	@Override
	public String getMachineName() {
		return "container.crystallizer";
	}
}
