package zmaster587.advancedRocketry.tile.multiblock.machine;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import zmaster587.advancedRocketry.api.AdvancedRocketryItems;
import zmaster587.advancedRocketry.inventory.TextureResources;
import zmaster587.advancedRocketry.util.AudioRegistry;
import zmaster587.libVulpes.api.LibVulpesBlocks;
import zmaster587.libVulpes.api.material.AllowedProducts;
import zmaster587.libVulpes.api.material.Material;
import zmaster587.libVulpes.api.material.MaterialRegistry;
import zmaster587.libVulpes.interfaces.IRecipe;
import zmaster587.libVulpes.inventory.modules.ModuleBase;
import zmaster587.libVulpes.inventory.modules.ModuleProgress;
import zmaster587.libVulpes.recipe.NumberedOreDictStack;
import zmaster587.libVulpes.recipe.RecipesMachine;
import zmaster587.libVulpes.tile.multiblock.TileMultiblockMachine;

import java.util.List;

public class TileRollingMachine extends TileMultiblockMachine {

	public static final Object structure[][][] = new Object[][][] { 
		{   {Blocks.AIR, Blocks.AIR, Blocks.AIR, Blocks.AIR, Blocks.AIR},
			{LibVulpesBlocks.blockStructureBlock, LibVulpesBlocks.blockStructureBlock, LibVulpesBlocks.blockStructureBlock, LibVulpesBlocks.blockStructureBlock, LibVulpesBlocks.blockStructureBlock},
			{LibVulpesBlocks.blockStructureBlock, LibVulpesBlocks.blockStructureBlock, LibVulpesBlocks.blockStructureBlock, "blockSteel", LibVulpesBlocks.blockStructureBlock}},

			{{'P', 'c', 'I', Blocks.AIR, Blocks.AIR},
				{LibVulpesBlocks.blockStructureBlock, LibVulpesBlocks.motors, LibVulpesBlocks.motors, "blockSteel", LibVulpesBlocks.blockStructureBlock},
				{LibVulpesBlocks.blockStructureBlock, 'L', 'O', "blockSteel", LibVulpesBlocks.blockStructureBlock}}
	};

	@Override
	public float getTimeMultiplierForBlock(IBlockState state, TileEntity tile) {

		Material material = MaterialRegistry.getMaterialFromItemStack(new ItemStack(state.getBlock(),1, state.getBlock().getMetaFromState(state)));
		if(material == MaterialRegistry.getMaterialFromName("Gold"))
			return 0.9f;
		else if(material == MaterialRegistry.getMaterialFromName("Aluminum"))
			return 0.8f;
		else if(material == MaterialRegistry.getMaterialFromName("Titanium"))
			return 0.75f;
		else if(material == MaterialRegistry.getMaterialFromName("Iridium"))
			return 0.5f;

		return super.getTimeMultiplierForBlock(state, tile);
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
	public SoundEvent getSound() {
		return AudioRegistry.rollingMachine;
	}

	@Override
	public int getSoundDuration() {
		return 30;
	}
	
	@Override
	public boolean shouldHideBlock(World world, BlockPos pos, IBlockState tile) {
		return tile != Block.getBlockFromItem(MaterialRegistry.getMaterialFromName("Copper").getProduct(AllowedProducts.getProductByName("COIL")).getItem());
	}

	@Override
	public String getMachineName() {
		return "tile.rollingMachine.name";
	}

	@Override
	public AxisAlignedBB getRenderBoundingBox() {
		
		return new AxisAlignedBB(pos.add(-4,-4,-4), pos.add(4,4,4));
	}

}
