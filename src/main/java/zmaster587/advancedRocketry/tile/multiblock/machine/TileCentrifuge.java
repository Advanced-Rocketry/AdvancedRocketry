package zmaster587.advancedRocketry.tile.multiblock.machine;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.oredict.OreDictionary;
import zmaster587.advancedRocketry.AdvancedRocketry;
import zmaster587.advancedRocketry.api.ARConfiguration;
import zmaster587.advancedRocketry.api.AdvancedRocketryFluids;
import zmaster587.advancedRocketry.inventory.TextureResources;
import zmaster587.advancedRocketry.util.AudioRegistry;
import zmaster587.libVulpes.api.LibVulpesBlocks;
import zmaster587.libVulpes.block.BlockMeta;
import zmaster587.libVulpes.inventory.modules.ModuleBase;
import zmaster587.libVulpes.inventory.modules.ModuleProgress;
import zmaster587.libVulpes.recipe.RecipesMachine;
import zmaster587.libVulpes.recipe.RecipesMachine.ChanceFluidStack;
import zmaster587.libVulpes.tile.multiblock.TileMultiblockMachine;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

public class TileCentrifuge extends TileMultiblockMachine {
	public static final Object[][][] structure = {

			{{Blocks.AIR, new BlockMeta(LibVulpesBlocks.blockStructureBlock), 'l'},
					{"casingCentrifuge", "casingCentrifuge", new BlockMeta(LibVulpesBlocks.blockStructureBlock)},
					{"casingCentrifuge", "casingCentrifuge", null}},

			{{Blocks.AIR, new BlockMeta(LibVulpesBlocks.blockStructureBlock), 'l'},
					{"casingCentrifuge", "casingCentrifuge", new BlockMeta(LibVulpesBlocks.blockStructureBlock)},
					{"casingCentrifuge", "casingCentrifuge", 'l'}},

			{{'c', new BlockMeta(LibVulpesBlocks.blockStructureBlock), 'l'},
					{"casingCentrifuge", "casingCentrifuge", new BlockMeta(LibVulpesBlocks.blockStructureBlock)},
					{"casingCentrifuge", "casingCentrifuge", 'l'}},

			{   {'P','L', 'l'},
				{LibVulpesBlocks.motors,'O', new BlockMeta(LibVulpesBlocks.blockStructureBlock)},
			  {new BlockMeta(LibVulpesBlocks.blockStructureBlock), new BlockMeta(LibVulpesBlocks.blockStructureBlock), 'l'}},

		};
		
		@Override
		public Object[][][] getStructure() {
			return structure;
		}

		
		@Override
		public boolean shouldHideBlock(World world, BlockPos pos2, IBlockState tile) {
			
			return true;
		}
		
		@Override
		public AxisAlignedBB getRenderBoundingBox() {
			return new AxisAlignedBB(pos.add(-2,-2,-2), pos.add(2,2,2));
		}
		
		@Override
		public void registerRecipes() {
            // Nuggets for centrifuge
            List<RecipesMachine.ChanceItemStack> nuggetList = new LinkedList<>();
            
            for(String entry : ARConfiguration.getCurrentConfig().lavaCentrifugeOutputs)
            {
            	try
            	{
	            	String[] split = entry.split(":");
	            	String chance = split[split.length-1];
	            	String item = split[0];
	            	
	            	if(split.length != 2)
	            		throw new ArrayIndexOutOfBoundsException();
	            	
	            	float floatChance = Float.parseFloat(chance);
	            	
	        		List<ItemStack> list2 = OreDictionary.getOres(item);
	        		if(!list2.isEmpty())
	        			nuggetList.add(new RecipesMachine.ChanceItemStack(list2.get(0), floatChance ));
            	}
            	catch(NumberFormatException e)
            	{
            		AdvancedRocketry.logger.warn("Unable to parse the weight for '" + entry + "' in lavaCentrifugeOutputs.  Remember, it should end with colon followed by a number with no spaces");
            	}
            	catch(ArrayIndexOutOfBoundsException e)
            	{
            		AdvancedRocketry.logger.warn("Unable to parse the entry for '" + entry + "' in lavaCentrifugeOutputs.  Remember, there should be only an 'ore_dictionary_entry:chance' in the entry.  "
            				+ "Items are not yet supported");
            	}
            }
            
            List<List<ItemStack>> inputItems = new LinkedList<>();
            List<FluidStack> inputFluid = new LinkedList<>();
            inputFluid.add(new FluidStack(AdvancedRocketryFluids.fluidEnrichedLava, 1000));
			List<ChanceFluidStack> outputFluid = new LinkedList<>();
			outputFluid.add(new ChanceFluidStack(new FluidStack(FluidRegistry.getFluid("lava"), 1000), 1.0f));
            RecipesMachine.Recipe rec =  new RecipesMachine.Recipe(nuggetList, inputItems, outputFluid,inputFluid, 200, 10, new HashMap<>());
            rec.setMaxOutputSize(4);
            RecipesMachine.getInstance().getRecipes(TileCentrifuge.class).add(rec);
		}
		
		@Override
		public SoundEvent getSound() {
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
			return "tile.centrifuge.name";
		}
}
