package zmaster587.advancedRocketry.tile.multiblock;

import java.util.HashSet;
import java.util.List;

import zmaster587.advancedRocketry.Inventory.TextureResources;
import zmaster587.advancedRocketry.Inventory.modules.IModularInventory;
import zmaster587.advancedRocketry.Inventory.modules.ModuleBase;
import zmaster587.advancedRocketry.Inventory.modules.ModuleProgress;
import zmaster587.advancedRocketry.api.AdvRocketryBlocks;
import zmaster587.advancedRocketry.client.render.util.ProgressBarImage;
import zmaster587.advancedRocketry.recipe.RecipesMachine;
import zmaster587.libVulpes.interfaces.IRecipe;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.util.AxisAlignedBB;
import net.minecraftforge.common.util.ForgeDirection;

public class TileCuttingMachine extends TileMultiBlockMachine implements IModularInventory {

	private static final Object[][][] structure = new Object[][][]{
			{{Blocks.stone, Blocks.stone, Blocks.stone},
		    	 {Blocks.stone, Blocks.stone, Blocks.stone}},
			
		    	 			 {{'*', 'c', '*'}, 
							  {'*', '*', '*'}}};
	
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
	protected HashSet<Block> getAllowableWildCardBlocks() {
		HashSet<Block> set = super.getAllowableWildCardBlocks();
		
		set.add(AdvRocketryBlocks.blockStructureBlock);
		return set;
	}
	
	public boolean completeStructure() {
		boolean result = super.completeStructure();
		
		return result;
	}
	
	@Override
	public String getMachineName() {
		return "container.cuttingmachine";
	}

	@Override
	public List<ModuleBase> getModules() {
		List<ModuleBase> modules = super.getModules();
		modules.add(new ModuleProgress(100, 20, 0, new ProgressBarImage(54, 0, 42, 42,96, 0, 36, 36, 3, 3, ForgeDirection.EAST, TextureResources.progressBars), this));
		
		return modules;
	}
	
}