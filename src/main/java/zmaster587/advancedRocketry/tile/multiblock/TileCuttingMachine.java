package zmaster587.advancedRocketry.tile.multiblock;

import java.util.HashSet;
import java.util.List;

import zmaster587.advancedRocketry.Inventory.TextureResources;
import zmaster587.advancedRocketry.Inventory.modules.IModularInventory;
import zmaster587.advancedRocketry.Inventory.modules.ModuleBase;
import zmaster587.advancedRocketry.Inventory.modules.ModuleProgress;
import zmaster587.advancedRocketry.api.AdvRocketryBlocks;
import zmaster587.advancedRocketry.block.BlockRotatableModel;
import zmaster587.advancedRocketry.client.render.util.ProgressBarImage;
import zmaster587.advancedRocketry.recipe.RecipesMachine;
import zmaster587.libVulpes.block.BlockMeta;
import zmaster587.libVulpes.block.RotatableBlock;
import zmaster587.libVulpes.interfaces.IRecipe;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Blocks;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.DamageSource;
import net.minecraftforge.common.util.ForgeDirection;

public class TileCuttingMachine extends TileMultiblockMachine implements IModularInventory {

	private static final Object[][][] structure = new Object[][][]{			
		    	 			 {{'O', 'c', 'I'}, 
							  {'*', AdvRocketryBlocks.blockSawBlade, '*'}}};
	
	@Override
	public List<IRecipe> getMachineRecipeList() {
		return RecipesMachine.getInstance().getRecipes(this.getClass());
	}

	@Override
	public Object[][][] getStructure() {
		return structure;
	}
	
	@Override
	public void updateEntity() {
		super.updateEntity();
		
		if(isRunning() && worldObj.getWorldTime() % 20 == 0) {
			ForgeDirection back = RotatableBlock.getFront(this.getBlockMetadata()).getOpposite();
			
			float xCoord = this.xCoord + (0.5f*back.offsetX); 
			float zCoord = this.zCoord + (0.5f*back.offsetZ);
			
			for(Object entity : worldObj.getEntitiesWithinAABB(EntityLivingBase.class, AxisAlignedBB.getBoundingBox(xCoord, yCoord + 1, zCoord, xCoord + 1, yCoord + 1.5f, zCoord + 1))) {
				((EntityLivingBase)entity).attackEntityFrom(DamageSource.cactus, 1f);
			}
		}
	}
	
	@Override
	public AxisAlignedBB getRenderBoundingBox() {
		return AxisAlignedBB.getBoundingBox(xCoord -2,yCoord -2, zCoord -2, xCoord + 2, yCoord + 2, zCoord + 2);
	}
	
	@Override
	public List<BlockMeta> getAllowableWildCardBlocks() {
		List<BlockMeta> list = super.getAllowableWildCardBlocks();
		
		list.add(new BlockMeta(AdvRocketryBlocks.blockMotor, BlockMeta.WILDCARD));
		return list;
	}
	
	public boolean completeStructure() {
		boolean result = super.completeStructure();
		
		return result;
	}
	
	@Override
	public String getMachineName() {
		return "container.cuttingmachine";
	}

	@Override
	public List<ModuleBase> getModules() {
		List<ModuleBase> modules = super.getModules();
		modules.add(new ModuleProgress(100, 20, 0, TextureResources.cuttingMachineProgressBar, this));
		
		return modules;
	}
	
}