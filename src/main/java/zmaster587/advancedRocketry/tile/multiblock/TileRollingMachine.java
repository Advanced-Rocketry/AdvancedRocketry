package zmaster587.advancedRocketry.tile.multiblock;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.World;
import zmaster587.advancedRocketry.Inventory.TextureResources;
import zmaster587.advancedRocketry.Inventory.modules.ModuleBase;
import zmaster587.advancedRocketry.Inventory.modules.ModuleProgress;
import zmaster587.advancedRocketry.api.AdvancedRocketryBlocks;
import zmaster587.advancedRocketry.api.MaterialRegistry;
import zmaster587.advancedRocketry.api.MaterialRegistry.AllowedProducts;
import zmaster587.advancedRocketry.recipe.RecipesMachine;
import zmaster587.libVulpes.block.BlockMeta;
import zmaster587.libVulpes.interfaces.IRecipe;

public class TileRollingMachine extends TileMultiblockMachine {

	public static final Object structure[][][] = new Object[][][] { 
		 {   {'c', Blocks.air, Blocks.air, Blocks.air},
			 {'I', Blocks.air, AdvancedRocketryBlocks.blockStructureBlock, Blocks.air},
			 {'I', Blocks.air, AdvancedRocketryBlocks.blockStructureBlock, Blocks.air}},
		 
		{{AdvancedRocketryBlocks.blockRFBattery, AdvancedRocketryBlocks.blockFuelTank, AdvancedRocketryBlocks.blockStructureBlock, Blocks.air},
		 {new BlockMeta(Block.getBlockFromItem(MaterialRegistry.Materials.COPPER.getProduct(AllowedProducts.COIL).getItem()), MaterialRegistry.Materials.COPPER.getMeta()), AdvancedRocketryBlocks.blockStructureBlock, AdvancedRocketryBlocks.blockStructureBlock, 'O'},
		 {new BlockMeta(Block.getBlockFromItem(MaterialRegistry.Materials.COPPER.getProduct(AllowedProducts.COIL).getItem()), MaterialRegistry.Materials.COPPER.getMeta()), AdvancedRocketryBlocks.blockMotor, AdvancedRocketryBlocks.blockStructureBlock, 'O'}}
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
	public List<ModuleBase> getModules() {
		List<ModuleBase> modules = super.getModules();

		modules.add(new ModuleProgress(70, 20, 0, TextureResources.rollingMachineProgressBar, this));
		return modules;
	}
	
	@Override
	public boolean shouldHideBlock(World world, int x, int y, int z, Block tile) {
		return tile != Block.getBlockFromItem(MaterialRegistry.Materials.COPPER.getProduct(AllowedProducts.COIL).getItem());
	}
	
	@Override
	public String getMachineName() {
		return "tile.rollingMachine.name";
	}
	
	@Override
	public AxisAlignedBB getRenderBoundingBox() {
		return AxisAlignedBB.getBoundingBox(xCoord -4, yCoord -4, zCoord -4, xCoord + 4, yCoord + 4, zCoord + 4);
	}

}
