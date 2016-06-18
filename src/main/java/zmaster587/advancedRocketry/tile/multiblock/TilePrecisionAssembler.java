package zmaster587.advancedRocketry.tile.multiblock;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import zmaster587.advancedRocketry.api.AdvancedRocketryBlocks;
import zmaster587.advancedRocketry.api.material.MaterialRegistry;
import zmaster587.advancedRocketry.api.material.MaterialRegistry.AllowedProducts;
import zmaster587.advancedRocketry.client.render.util.ProgressBarImage;
import zmaster587.advancedRocketry.inventory.TextureResources;
import zmaster587.advancedRocketry.inventory.modules.IModularInventory;
import zmaster587.advancedRocketry.inventory.modules.IProgressBar;
import zmaster587.advancedRocketry.inventory.modules.ModuleBase;
import zmaster587.advancedRocketry.inventory.modules.ModuleImage;
import zmaster587.advancedRocketry.inventory.modules.ModuleProgress;
import zmaster587.advancedRocketry.recipe.RecipesMachine;
import zmaster587.libVulpes.block.BlockMeta;
import zmaster587.libVulpes.interfaces.IRecipe;
import zmaster587.libVulpes.util.IconResource;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.util.AxisAlignedBB;
import net.minecraftforge.common.util.ForgeDirection;

public class TilePrecisionAssembler extends TileMultiblockMachine implements IModularInventory, IProgressBar {

	public static final Object structure[][][] = new Object[][][]{ {{AdvancedRocketryBlocks.blockStructureBlock, AdvancedRocketryBlocks.blockStructureBlock, AdvancedRocketryBlocks.blockStructureBlock, AdvancedRocketryBlocks.blockStructureBlock}, 
		{AdvancedRocketryBlocks.blockStructureBlock, AdvancedRocketryBlocks.blockStructureBlock, AdvancedRocketryBlocks.blockStructureBlock, AdvancedRocketryBlocks.blockStructureBlock},
		{AdvancedRocketryBlocks.blockStructureBlock, AdvancedRocketryBlocks.blockStructureBlock, AdvancedRocketryBlocks.blockStructureBlock, AdvancedRocketryBlocks.blockStructureBlock}},

		{{AdvancedRocketryBlocks.blockStructureBlock, Blocks.glass, Blocks.glass, AdvancedRocketryBlocks.blockStructureBlock},
			{AdvancedRocketryBlocks.blockStructureBlock, Blocks.air, Blocks.air, AdvancedRocketryBlocks.blockStructureBlock},
			{AdvancedRocketryBlocks.blockStructureBlock, AdvancedRocketryBlocks.blockStructureBlock, AdvancedRocketryBlocks.blockStructureBlock, AdvancedRocketryBlocks.blockStructureBlock}},

			{{'c', '*', '*', '*'},
				{'*', new BlockMeta(Block.getBlockFromItem(MaterialRegistry.Materials.COPPER.getProduct(AllowedProducts.COIL).getItem()), MaterialRegistry.Materials.COPPER.getMeta()), new BlockMeta(Block.getBlockFromItem(MaterialRegistry.Materials.COPPER.getProduct(AllowedProducts.COIL).getItem()), MaterialRegistry.Materials.COPPER.getMeta()), '*'},
				{'*', AdvancedRocketryBlocks.blockMotor, AdvancedRocketryBlocks.blockMotor, '*'}}};

	@Override
	public List<IRecipe> getMachineRecipeList() {
		return RecipesMachine.getInstance().getRecipes(this.getClass());
	}

	@Override
	public Object[][][] getStructure() {
		return structure;
	}

	@Override
	public List<BlockMeta> getAllowableWildCardBlocks() {
		List<BlockMeta> list = super.getAllowableWildCardBlocks();

		list.add(new BlockMeta(AdvancedRocketryBlocks.blockStructureBlock, BlockMeta.WILDCARD));
		list.add(new BlockMeta(AdvancedRocketryBlocks.blockRFBattery, BlockMeta.WILDCARD));
		list.addAll(getAllowableOutputBlocks());
		list.addAll(getInputs());
		list.addAll(getPowerInputBlocks());

		return list;
	}

	@Override
	public AxisAlignedBB getRenderBoundingBox() {
		return AxisAlignedBB.getBoundingBox(xCoord -4, yCoord -4, zCoord -4, xCoord + 4, yCoord + 4, zCoord + 4);
	}

	@Override
	public List<ModuleBase> getModules(int ID, EntityPlayer player) {
		List<ModuleBase> modules = super.getModules(ID, player);

		int yOffset = 16;
		int xOffset = 65;

		if(worldObj.isRemote)
			modules.add(new ModuleImage(xOffset, yOffset, new IconResource(132, 0, 53, 66, TextureResources.progressBars)));
		modules.add(new ModuleProgress(xOffset + 35, yOffset + 22, 1, new ProgressBarImage(167, 22, 13, 15, 54, 42, 13, 15, ForgeDirection.DOWN, TextureResources.progressBars), this));
		modules.add(new ModuleProgress(xOffset + 36, yOffset + 41, 2, new ProgressBarImage(168, 41, 11, 15, 67, 42, 11, 15, ForgeDirection.DOWN, TextureResources.progressBars), this));
		modules.add(new ModuleProgress(xOffset + 31, yOffset + 62, 3, new ProgressBarImage(163, 62, 21, 3, 90, 42, 21,  3, ForgeDirection.EAST, TextureResources.progressBars), this));

		return modules;
	}

	@Override
	public int getProgress(int id) {
		if(id == 0) {
			return super.getProgress(id);
		}
		if(id == 1) {
			return Math.min(currentTime, completionTime/3);
		}
		else if(id == 2) {
			int relativeTime = currentTime - (completionTime/3);
			return relativeTime >= 0 ? Math.min(relativeTime, completionTime/3) : 0;
		}

		int relativeTime = currentTime - (2*completionTime/3);

		return relativeTime >= 0 ? Math.min(relativeTime, completionTime/3) : 0; 
	}

	@Override
	public int getTotalProgress(int id) {
		if(id == 0)
			return super.getTotalProgress(id);
		return completionTime/3;
	}

	@Override
	public void setTotalProgress(int id, int progress) {
		if(id == 0)
			super.setTotalProgress(id, progress);
	}

	@Override
	public void setProgress(int id, int progress) {
		if(id == 0)
			super.setProgress(id, progress);
	}

	@Override
	public float getNormallizedProgress(int id) {
		return getProgress(id)/(float)getTotalProgress(id);
	}

	@Override
	public String getMachineName() {
		return "container.precisionassemblingmachine";
	}
}