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
import zmaster587.advancedRocketry.api.AdvRocketryBlocks;
import zmaster587.advancedRocketry.api.MaterialRegistry;
import zmaster587.advancedRocketry.api.MaterialRegistry.AllowedProducts;
import zmaster587.advancedRocketry.client.render.util.ProgressBarImage;
import zmaster587.advancedRocketry.recipe.RecipesMachine;
import zmaster587.advancedRocketry.tile.TileMaterial;
import zmaster587.libVulpes.interfaces.IRecipe;

public class TileCrystallizer extends TileMultiblockMachine implements IModularInventory {

	
	
	public static final Object[][][] structure = { {{AdvRocketryBlocks.blockQuartzCrucible, AdvRocketryBlocks.blockQuartzCrucible, AdvRocketryBlocks.blockQuartzCrucible},
		{AdvRocketryBlocks.blockQuartzCrucible, AdvRocketryBlocks.blockQuartzCrucible, AdvRocketryBlocks.blockQuartzCrucible}},

		{{'O', 'c', 'I'}, 
			{Block.getBlockFromItem(MaterialRegistry.Materials.COPPER.getProduct(AllowedProducts.COIL).getItem()), 'P', Block.getBlockFromItem(MaterialRegistry.Materials.COPPER.getProduct(AllowedProducts.COIL).getItem())}},

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
		
		if(tile instanceof TileMaterial) {
			if(coil[0] == null)
				coil[0] = ((TileMaterial)tile).getMaterial();
			else
				coil[1] = ((TileMaterial)tile).getMaterial();
		}
	}
	
	@Override
	public boolean canProcessRecipe(IRecipe recipe) {
		return super.canProcessRecipe(recipe) && coil[0] == MaterialRegistry.Materials.COPPER && coil[1] == MaterialRegistry.Materials.COPPER;
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
