package zmaster587.advancedRocketry.tile.multiblock.machine;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
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
import zmaster587.libVulpes.recipe.RecipesMachine;
import zmaster587.libVulpes.tile.multiblock.TileMultiblockMachine;

public class TileRollingMachine extends TileMultiblockMachine {

	public static final Object structure[][][] = new Object[][][] { 
		{   {'c', null, Blocks.AIR, Blocks.AIR},
			{'I', Blocks.AIR, LibVulpesBlocks.blockStructureBlock, Blocks.AIR},
			{'I', Blocks.AIR, LibVulpesBlocks.blockStructureBlock, Blocks.AIR}},

			{{'P', 'L', LibVulpesBlocks.blockStructureBlock, null},
				{"blockCoil", LibVulpesBlocks.blockStructureBlock, LibVulpesBlocks.blockStructureBlock, 'O'},
				{"blockCoil", LibVulpesBlocks.motors, LibVulpesBlocks.blockStructureBlock, 'O'}}
	};

	@Override
	public float getTimeMultiplierForBlock(IBlockState state, TileEntity tile) {

		Material material = MaterialRegistry.getMaterialFromItemStack(new ItemStack(state.getBlock(),1, state.getBlock().getMetaFromState(state)));
		if(material == MaterialRegistry.getMaterialFromName("Gold"))
			return 0.9f;
		else if(material == MaterialRegistry.getMaterialFromName("Aluiminum"))
			return 0.8f;
		else if(material == MaterialRegistry.getMaterialFromName("Titanium"))
			return 0.75f;
		else if(material == MaterialRegistry.getMaterialFromName("Iriudium"))
			return 0.5f;

		return super.getTimeMultiplierForBlock(state, tile);
	}

	@Override
	public void registerRecipes() {
		FluidStack water = new FluidStack(FluidRegistry.WATER, 100);
        Item sheet = Item.getByNameOrId("libvulpes:productsheet");
        Item plate = Item.getByNameOrId("libvulpes:productplate");
        Item sheet2 = Item.getByNameOrId("advancedrocketry:productsheet");
        Item plate2 = Item.getByNameOrId("advancedrocketry:productplate");
		//Tanks
        RecipesMachine.getInstance().addRecipe(TileRollingMachine.class, new ItemStack(AdvancedRocketryItems.itemPressureTank, 1, 0), 100, 1, "sheetIron", "sheetIron", water);
        RecipesMachine.getInstance().addRecipe(TileRollingMachine.class, new ItemStack(AdvancedRocketryItems.itemPressureTank, 1, 1), 200, 2, "sheetSteel", "sheetSteel", water);
        RecipesMachine.getInstance().addRecipe(TileRollingMachine.class, new ItemStack(AdvancedRocketryItems.itemPressureTank, 1, 2), 100, 1, "sheetAluminum", "sheetAluminum", water);
        RecipesMachine.getInstance().addRecipe(TileRollingMachine.class, new ItemStack(AdvancedRocketryItems.itemPressureTank, 1, 3), 1000, 8, "sheetTitanium", "sheetTitanium", water);
        
        //Plates
        RecipesMachine.getInstance().addRecipe(TileRollingMachine.class, new ItemStack(plate, 1, 1), 100, 20, "ingotIron", water);
        RecipesMachine.getInstance().addRecipe(TileRollingMachine.class, new ItemStack(plate, 1, 2), 100, 20, "ingotGold", water);
        RecipesMachine.getInstance().addRecipe(TileRollingMachine.class, new ItemStack(plate, 1, 3), 100, 20, "ingotSilicon", water);
        RecipesMachine.getInstance().addRecipe(TileRollingMachine.class, new ItemStack(plate, 1, 4), 100, 20, "ingotCopper", water);
        RecipesMachine.getInstance().addRecipe(TileRollingMachine.class, new ItemStack(plate, 1, 5), 100, 20, "ingotTin", water);
        RecipesMachine.getInstance().addRecipe(TileRollingMachine.class, new ItemStack(plate, 1, 6), 100, 20, "ingotSteel", water);
        RecipesMachine.getInstance().addRecipe(TileRollingMachine.class, new ItemStack(plate, 1, 7), 100, 20, "ingotTitanium", water);
        RecipesMachine.getInstance().addRecipe(TileRollingMachine.class, new ItemStack(plate, 1, 9), 100, 20, "ingotAluminum", water);
        RecipesMachine.getInstance().addRecipe(TileRollingMachine.class, new ItemStack(plate, 1, 10), 100, 20, "ingotIridium", water);
        RecipesMachine.getInstance().addRecipe(TileRollingMachine.class, new ItemStack(plate2, 1, 0), 100, 20, "ingotTitaniumAluminide", water);
        RecipesMachine.getInstance().addRecipe(TileRollingMachine.class, new ItemStack(plate2, 1, 1), 100, 20, "ingotTitaniumIridium", water);
        
        //Sheets
        RecipesMachine.getInstance().addRecipe(TileRollingMachine.class, new ItemStack(sheet, 1, 1), 100, 200, "plateIron", water);
        RecipesMachine.getInstance().addRecipe(TileRollingMachine.class, new ItemStack(sheet, 1, 4), 100, 200, "plateCopper", water);
        RecipesMachine.getInstance().addRecipe(TileRollingMachine.class, new ItemStack(sheet, 1, 6), 100, 200, "plateSteel", water);
        RecipesMachine.getInstance().addRecipe(TileRollingMachine.class, new ItemStack(sheet, 1, 7), 100, 200, "plateTitanium", water);
        RecipesMachine.getInstance().addRecipe(TileRollingMachine.class, new ItemStack(sheet, 1, 9), 100, 200, "plateAluminum", water);
        RecipesMachine.getInstance().addRecipe(TileRollingMachine.class, new ItemStack(sheet2, 1, 0), 100, 200, "plateTitaniumAluminide", water);
        RecipesMachine.getInstance().addRecipe(TileRollingMachine.class, new ItemStack(sheet2, 1, 1), 100, 200, "plateTitaniumIridium", water);
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
			if(fluidHandler == null || (fluid = fluidHandler.drain(new FluidStack(FluidRegistry.WATER, 100), false)) == null 
					|| fluid.amount != 100)
				return false;
		}

		return super.canProcessRecipe(recipe);
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
