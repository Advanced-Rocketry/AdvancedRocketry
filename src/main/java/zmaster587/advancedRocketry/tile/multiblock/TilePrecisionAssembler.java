package zmaster587.advancedRocketry.tile.multiblock;

import java.util.HashSet;
import java.util.List;

import zmaster587.advancedRocketry.AdvancedRocketry;
import zmaster587.advancedRocketry.api.AdvRocketryBlocks;
import zmaster587.advancedRocketry.api.recipe.ITimedPoweredMachine;
import zmaster587.advancedRocketry.recipe.RecipesMachine;
import zmaster587.libVulpes.block.RotatableBlock;
import zmaster587.libVulpes.gui.IlimitedItemSlotEntity;
import zmaster587.libVulpes.interfaces.IRecipe;
import zmaster587.libVulpes.tile.TileEntityMachine;
import zmaster587.libVulpes.tile.TileEntityRFMachine;
import zmaster587.libVulpes.util.PoweredTimedItemStack;
import cofh.api.energy.EnergyStorage;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.AxisAlignedBB;
import net.minecraftforge.common.util.ForgeDirection;

public class TilePrecisionAssembler extends TileMultiBlockMachine {

	public static final Object structure[][][] = new Object[][][]{ {{Blocks.stone, Blocks.stone, Blocks.stone, Blocks.stone}, 
		{Blocks.stone, Blocks.stone, Blocks.stone, Blocks.stone},
		{Blocks.stone, Blocks.stone, Blocks.stone, Blocks.stone}},
	  
	  {{Blocks.stone, Blocks.glass, Blocks.glass, Blocks.stone},
		  {Blocks.stone, Blocks.glass, Blocks.glass, Blocks.stone},
    	  {Blocks.stone, Blocks.stone, Blocks.stone, Blocks.stone}},
	  
     {{'c', Blocks.stone, Blocks.stone, '*'},
	  {'*', Blocks.stone, Blocks.stone, '*'},
	  {'*', Blocks.stone, Blocks.stone, '*'}}};
	
	@Override
	public List<IRecipe> getMachineRecipeList() {
		return RecipesMachine.getInstance().getRecipes(this.getClass());
	}

	@Override
	public Object[][][] getStructure() {
		return structure;
	}
	
	@Override
	protected HashSet<Block> getAllowableWildCardBlocks() {
		HashSet<Block> set = super.getAllowableWildCardBlocks();
		
		set.add(AdvRocketryBlocks.blockStructureBlock);
		set.add(Blocks.stone);
		return set;
	}
	
	@Override
	public AxisAlignedBB getRenderBoundingBox() {
		return AxisAlignedBB.getBoundingBox(xCoord -4, yCoord -4, zCoord -4, xCoord + 4, yCoord + 4, zCoord + 4);
	}
	
	public boolean completeStructure() {
		boolean result;
		result = super.completeStructure();
		//result = false;
		if(result) {
			worldObj.setBlockMetadataWithNotify(xCoord, yCoord, zCoord, this.blockMetadata | 8, 2);
		}
		else
			worldObj.setBlockMetadataWithNotify(xCoord, yCoord, zCoord, this.blockMetadata & 7, 2);
		
		return result;
	}
	
	@Override
	public String getMachineName() {
		return "container.precisionassemblingmachine";
	}

}