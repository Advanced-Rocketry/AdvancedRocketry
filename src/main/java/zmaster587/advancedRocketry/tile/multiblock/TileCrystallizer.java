package zmaster587.advancedRocketry.tile.multiblock;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraftforge.common.util.ForgeDirection;
import zmaster587.advancedRocketry.Inventory.TextureResources;
import zmaster587.advancedRocketry.Inventory.modules.IModularInventory;
import zmaster587.advancedRocketry.Inventory.modules.ModuleBase;
import zmaster587.advancedRocketry.Inventory.modules.ModuleProgress;
import zmaster587.advancedRocketry.api.AdvancedRocketryBlocks;
import zmaster587.advancedRocketry.api.material.MaterialRegistry;
import zmaster587.advancedRocketry.api.material.MaterialRegistry.AllowedProducts;
import zmaster587.advancedRocketry.client.render.util.ProgressBarImage;
import zmaster587.advancedRocketry.recipe.RecipesMachine;
import zmaster587.advancedRocketry.tile.TileMaterial;
import zmaster587.libVulpes.block.BlockMeta;
import zmaster587.libVulpes.interfaces.IRecipe;

public class TileCrystallizer extends TileMultiblockMachine implements IModularInventory {

	
	
	public static final Object[][][] structure = { {{AdvancedRocketryBlocks.blockQuartzCrucible, AdvancedRocketryBlocks.blockQuartzCrucible, AdvancedRocketryBlocks.blockQuartzCrucible},
		{AdvancedRocketryBlocks.blockQuartzCrucible, AdvancedRocketryBlocks.blockQuartzCrucible, AdvancedRocketryBlocks.blockQuartzCrucible}},
		
		{{'O', 'c', 'I'}, 
			{new BlockMeta(Block.getBlockFromItem(MaterialRegistry.Materials.COPPER.getProduct(AllowedProducts.COIL).getItem()), MaterialRegistry.Materials.COPPER.getMeta()), 'P', new BlockMeta(Block.getBlockFromItem(MaterialRegistry.Materials.COPPER.getProduct(AllowedProducts.COIL).getItem()), MaterialRegistry.Materials.COPPER.getMeta())}},

	};

	MaterialRegistry.Materials coil[];
	
	public TileCrystallizer() {
		coil = new MaterialRegistry.Materials[2];
	}
	
	@Override
	public List<IRecipe> getMachineRecipeList() {
		return RecipesMachine.getInstance().getRecipes(this.getClass());
	}

	@Override
	protected void integrateTile(TileEntity tile) {
		super.integrateTile(tile);
	}
	
	@Override
	public boolean canProcessRecipe(IRecipe recipe) {
		return super.canProcessRecipe(recipe);
	}
	
	@Override
	public Object[][][] getStructure() {
		return structure;
	}

	@Override
	protected float getTimeMultiplierForRecipe(IRecipe recipe) {
		return super.getTimeMultiplierForRecipe(recipe);
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
		return "container.crystallizer";
	}
}
