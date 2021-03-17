package zmaster587.advancedRocketry.tile.multiblock.machine;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import zmaster587.advancedRocketry.api.AdvancedRocketryFluids;
import zmaster587.advancedRocketry.inventory.TextureResources;
import zmaster587.advancedRocketry.util.AudioRegistry;
import zmaster587.libVulpes.api.LibVulpesBlocks;
import zmaster587.libVulpes.api.material.Material;
import zmaster587.libVulpes.api.material.MaterialRegistry;
import zmaster587.libVulpes.block.BlockMeta;
import zmaster587.libVulpes.inventory.modules.ModuleBase;
import zmaster587.libVulpes.inventory.modules.ModuleProgress;
import zmaster587.libVulpes.recipe.RecipesMachine;
import zmaster587.libVulpes.tile.multiblock.TileMultiBlock;
import zmaster587.libVulpes.tile.multiblock.TileMultiblockMachine;

import java.util.List;

public class TileElectrolyser extends TileMultiblockMachine {
	public static final Object[][][] structure = { 
		{{null,null,null},
		{'P', "blockCoil",'P'}},
		
		{{'l', 'c', 'l'}, 
			{new BlockMeta(LibVulpesBlocks.blockStructureBlock), 'L', new BlockMeta(LibVulpesBlocks.blockStructureBlock)}},

	};
	
	@Override
	public Object[][][] getStructure() {
		return structure;
	}
	
	@Override
	public void registerRecipes() {
		//RecipesMachine.getInstance().addRecipe(TileElectrolyser.class, new Object[] {new FluidStack(AdvancedRocketryFluids.fluidOxygen, 100), new FluidStack(AdvancedRocketryFluids.fluidHydrogen, 100)}, 100, 20, new FluidStack(FluidRegistry.WATER, 10));
	}

	@Override
	public boolean shouldHideBlock(World world, BlockPos pos2, IBlockState tile) { return true; }
	
	
	@Override
	public AxisAlignedBB getRenderBoundingBox() {
		return new AxisAlignedBB(pos.add(-2,-2,-2), pos.add(2,2,2));
	}
	
	@Override
	public SoundEvent getSound() {
		// TODO Auto-generated method stub
		return AudioRegistry.electrolyser;
	}


	@Override
	public List<ModuleBase> getModules(int ID, EntityPlayer player) {
		List<ModuleBase> modules = super.getModules(ID, player);

		modules.add(new ModuleProgress(100, 4, 0, TextureResources.crystallizerProgressBar, this));
		return modules;
	}

	@Override
	public String getMachineName() {
		return "tile.electrolyser.name";
	}
}
