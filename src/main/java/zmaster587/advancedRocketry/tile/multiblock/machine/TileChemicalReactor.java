package zmaster587.advancedRocketry.tile.multiblock.machine;

import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidStack;
import zmaster587.advancedRocketry.api.AdvancedRocketryAPI;
import zmaster587.advancedRocketry.api.AdvancedRocketryFluids;
import zmaster587.advancedRocketry.api.AdvancedRocketryItems;
import zmaster587.advancedRocketry.inventory.TextureResources;
import zmaster587.libVulpes.api.LibVulpesBlocks;
import zmaster587.libVulpes.block.BlockMeta;
import zmaster587.libVulpes.inventory.modules.ModuleBase;
import zmaster587.libVulpes.inventory.modules.ModuleProgress;
import zmaster587.libVulpes.recipe.RecipesMachine;
import zmaster587.libVulpes.tile.multiblock.TileMultiBlock;
import zmaster587.libVulpes.tile.multiblock.TileMultiblockMachine;

public class TileChemicalReactor extends TileMultiblockMachine {
	public static final Object[][][] structure = { 
		{{null, 'c',null},
		{'L', 'I','L'}},
		{{'P',  LibVulpesBlocks.motors, 'P'},
			{'l', new BlockMeta(LibVulpesBlocks.blockStructureBlock), 'O'}},

	};

	@Override
	public boolean shouldHideBlock(World world, int x, int y, int z, Block tile) {
		TileEntity tileEntity = world.getTileEntity(x, y, z);
		
		return !TileMultiBlock.getMapping('P').contains(new BlockMeta(tile, BlockMeta.WILDCARD)) && tileEntity != null && !(tileEntity instanceof TileChemicalReactor);
	}
	
	@Override
	public void registerRecipes() {
		//Chemical Reactor
		RecipesMachine.getInstance().addRecipe(TileChemicalReactor.class, new Object[] {new ItemStack(AdvancedRocketryItems.itemCarbonScrubberCartridge,1, 0), new ItemStack(Items.coal, 1, 1)}, 40, 20, new ItemStack(AdvancedRocketryItems.itemCarbonScrubberCartridge, 1, AdvancedRocketryItems.itemCarbonScrubberCartridge.getMaxDamage()));
		RecipesMachine.getInstance().addRecipe(TileChemicalReactor.class, new ItemStack(Items.dye,5,0xF), 100, 1, Items.bone, new FluidStack(AdvancedRocketryFluids.fluidNitrogen, 10));
		RecipesMachine.getInstance().addRecipe(TileChemicalReactor.class, new FluidStack(AdvancedRocketryFluids.fluidRocketFuel, 20), 100, 10, new FluidStack(AdvancedRocketryFluids.fluidOxygen, 10), new FluidStack(AdvancedRocketryFluids.fluidHydrogen, 10));
	
		for(Object key : Item.itemRegistry.getKeys()) {
			Item item = (Item) Item.itemRegistry.getObject(key);
			
			if(item instanceof ItemArmor) {
				ItemStack enchanted = new ItemStack(item);
				enchanted.addEnchantment(AdvancedRocketryAPI.enchantmentSpaceProtection, 1);
				RecipesMachine.getInstance().addRecipe(TileChemicalReactor.class, enchanted, 100, 10, item);
			}
		}
	}
	
	@Override
	public Object[][][] getStructure() {
		return structure;
	}
	
	@Override
	public ResourceLocation getSound() {
		return TextureResources.sndRollingMachine;
	}

	@Override
	public int getSoundDuration() {
		return 30;
	}
	
	@Override
	public AxisAlignedBB getRenderBoundingBox() {
		return AxisAlignedBB.getBoundingBox(xCoord -2,yCoord -2, zCoord -2, xCoord + 2, yCoord + 2, zCoord + 2);
	}

	@Override
	public List<ModuleBase> getModules(int ID, EntityPlayer player) {
		List<ModuleBase> modules = super.getModules(ID, player);

		modules.add(new ModuleProgress(100, 4, 0, TextureResources.crystallizerProgressBar, this));
		return modules;
	}

	@Override
	public String getMachineName() {
		return "tile.chemreactor.name";
	}
}
