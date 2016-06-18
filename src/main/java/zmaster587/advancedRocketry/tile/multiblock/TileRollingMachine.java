package zmaster587.advancedRocketry.tile.multiblock;

import java.util.List;
import java.util.Set;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.IFluidBlock;
import net.minecraftforge.fluids.IFluidHandler;
import zmaster587.advancedRocketry.api.AdvancedRocketryBlocks;
import zmaster587.advancedRocketry.api.material.MaterialRegistry;
import zmaster587.advancedRocketry.api.material.MaterialRegistry.AllowedProducts;
import zmaster587.advancedRocketry.inventory.TextureResources;
import zmaster587.advancedRocketry.inventory.modules.ModuleBase;
import zmaster587.advancedRocketry.inventory.modules.ModuleProgress;
import zmaster587.advancedRocketry.recipe.RecipesMachine;
import zmaster587.libVulpes.block.BlockMeta;
import zmaster587.libVulpes.interfaces.IRecipe;

public class TileRollingMachine extends TileMultiblockMachine {

	public static final Object structure[][][] = new Object[][][] { 
		{   {'c', null, Blocks.air, Blocks.air},
			{'I', Blocks.air, AdvancedRocketryBlocks.blockStructureBlock, Blocks.air},
			{'I', Blocks.air, AdvancedRocketryBlocks.blockStructureBlock, Blocks.air}},

			{{AdvancedRocketryBlocks.blockRFBattery, 'L', AdvancedRocketryBlocks.blockStructureBlock, null},
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
	public List<ModuleBase> getModules(int ID, EntityPlayer player) {
		List<ModuleBase> modules = super.getModules(ID, player);

		modules.add(new ModuleProgress(70, 20, 0, TextureResources.rollingMachineProgressBar, this));
		return modules;
	}

	@Override
	public boolean canProcessRecipe(IRecipe recipe) {
		if(!fluidInPorts.isEmpty()) {
			IFluidHandler fluidHandler = fluidInPorts.get(0);
			FluidStack fluid;
			if(fluidHandler == null || (fluid = fluidHandler.drain(ForgeDirection.UNKNOWN, new FluidStack(FluidRegistry.WATER, 100), false)) == null 
					|| fluid.amount != 100)
				return false;
		}

		return super.canProcessRecipe(recipe);
	}


	@Override
	public void consumeItems(IRecipe recipe) {
		super.consumeItems(recipe);
		IFluidHandler fluidHandler = fluidInPorts.get(0);
		fluidHandler.drain(ForgeDirection.UNKNOWN, new FluidStack(FluidRegistry.WATER, 100), true);
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
