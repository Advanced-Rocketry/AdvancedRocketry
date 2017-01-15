package zmaster587.advancedRocketry.tile.multiblock.machine;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import zmaster587.advancedRocketry.api.AdvancedRocketryBlocks;
import zmaster587.advancedRocketry.block.BlockRotatableModel;
import zmaster587.advancedRocketry.inventory.TextureResources;
import zmaster587.libVulpes.api.LibVulpesBlocks;
import zmaster587.libVulpes.block.BlockMeta;
import zmaster587.libVulpes.block.RotatableBlock;
import zmaster587.libVulpes.client.util.ProgressBarImage;
import zmaster587.libVulpes.interfaces.IRecipe;
import zmaster587.libVulpes.inventory.modules.IModularInventory;
import zmaster587.libVulpes.inventory.modules.ModuleBase;
import zmaster587.libVulpes.inventory.modules.ModuleProgress;
import zmaster587.libVulpes.recipe.RecipesMachine;
import zmaster587.libVulpes.tile.multiblock.TileMultiblockMachine;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.DamageSource;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.util.ForgeDirection;

public class TileCuttingMachine extends TileMultiblockMachine implements IModularInventory {

	private static final Object[][][] structure = new Object[][][]{			
		    	 			 {{'I', 'c', 'O'}, 
							  {new BlockMeta(AdvancedRocketryBlocks.blockMotor, BlockMeta.WILDCARD), AdvancedRocketryBlocks.blockSawBlade, new BlockMeta(LibVulpesBlocks.blockRFBattery, 0)}}};
	

	@Override
	public Object[][][] getStructure() {
		return structure;
	}
	
	@Override
	public void updateEntity() {
		super.updateEntity();
		
		if(isRunning() && worldObj.getTotalWorldTime() % 20 == 0) {
			ForgeDirection back = RotatableBlock.getFront(this.getBlockMetadata()).getOpposite();
			
			float xCoord = this.xCoord + (0.5f*back.offsetX); 
			float zCoord = this.zCoord + (0.5f*back.offsetZ);
			
			for(Object entity : worldObj.getEntitiesWithinAABB(EntityLivingBase.class, AxisAlignedBB.getBoundingBox(xCoord, yCoord + 1, zCoord, xCoord + 1, yCoord + 1.5f, zCoord + 1))) {
				((EntityLivingBase)entity).attackEntityFrom(DamageSource.cactus, 1f);
			}
		}
	}
	
	@Override
	public ResourceLocation getSound() {
		return TextureResources.sndCuttingMachine;
	}
	
	@Override
	public AxisAlignedBB getRenderBoundingBox() {
		return AxisAlignedBB.getBoundingBox(xCoord -2,yCoord -2, zCoord -2, xCoord + 2, yCoord + 2, zCoord + 2);
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
	public List<ModuleBase> getModules(int ID, EntityPlayer player) {
		List<ModuleBase> modules = super.getModules(ID, player);
		modules.add(new ModuleProgress(100, 20, 0, TextureResources.cuttingMachineProgressBar, this));
		
		return modules;
	}
	
}