package zmaster587.advancedRocketry.tile.multiblock;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.World;
import zmaster587.advancedRocketry.Inventory.TextureResources;
import zmaster587.advancedRocketry.Inventory.modules.ModuleBase;
import zmaster587.advancedRocketry.Inventory.modules.ModuleProgress;
import zmaster587.advancedRocketry.api.AdvancedRocketryBlocks;
import zmaster587.advancedRocketry.api.MaterialRegistry;
import zmaster587.advancedRocketry.recipe.RecipesMachine;
import zmaster587.advancedRocketry.tile.TileRFBattery;
import zmaster587.libVulpes.block.BlockMeta;
import zmaster587.libVulpes.interfaces.IRecipe;

public class TileElectrolyser extends TileMultiblockMachine {
	public static final Object[][][] structure = { 
		{{Blocks.air, Blocks.air,Blocks.air},
		{'P', new BlockMeta(AdvancedRocketryBlocks.blockStructureBlock),'P'}},
		
		{{'l', 'c', 'l'}, 
			{new BlockMeta(AdvancedRocketryBlocks.blockStructureBlock), 'L', new BlockMeta(AdvancedRocketryBlocks.blockStructureBlock)}},

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
	public boolean shouldHideBlock(World world, int x, int y, int z, Block tile) {
		TileEntity tileEntity = world.getTileEntity(x, y, z);
		return tileEntity != null && !(tileEntity instanceof TileRFBattery || tileEntity instanceof TileElectrolyser);
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
		return "tile.electrolyser.name";
	}
}
