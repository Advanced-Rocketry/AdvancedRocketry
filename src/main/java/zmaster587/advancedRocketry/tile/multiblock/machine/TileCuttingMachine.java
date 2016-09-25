package zmaster587.advancedRocketry.tile.multiblock.machine;

import java.util.List;
import zmaster587.advancedRocketry.api.AdvancedRocketryBlocks;
import zmaster587.advancedRocketry.inventory.TextureResources;
import zmaster587.libVulpes.block.BlockMeta;
import zmaster587.libVulpes.block.RotatableBlock;
import zmaster587.libVulpes.interfaces.IRecipe;
import zmaster587.libVulpes.inventory.modules.IModularInventory;
import zmaster587.libVulpes.inventory.modules.ModuleBase;
import zmaster587.libVulpes.inventory.modules.ModuleProgress;
import zmaster587.libVulpes.recipe.RecipesMachine;
import zmaster587.libVulpes.tile.multiblock.TileMultiblockMachine;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;

public class TileCuttingMachine extends TileMultiblockMachine implements IModularInventory {

	private static final Object[][][] structure = new Object[][][]{			
		{{'I', 'c', 'O'}, 
			{new BlockMeta(AdvancedRocketryBlocks.blockMotor, BlockMeta.WILDCARD), AdvancedRocketryBlocks.blockSawBlade, 'P'}}};

	@Override
	public List<IRecipe> getMachineRecipeList() {
		return RecipesMachine.getInstance().getRecipes(this.getClass());
	}

	@Override
	public Object[][][] getStructure() {
		return structure;
	}

	@Override
	public void update() {
		super.update();

		if(isRunning() && worldObj.getWorldTime() % 20 == 0) {
			EnumFacing back = RotatableBlock.getFront(worldObj.getBlockState(pos)).getOpposite();

			float xCoord = this.getPos().getX() + (0.5f*back.getFrontOffsetX()); 
			float zCoord = this.getPos().getZ() + (0.5f*back.getFrontOffsetZ());

			for(Object entity : worldObj.getEntitiesWithinAABB(EntityLivingBase.class, new AxisAlignedBB(xCoord, this.getPos().getY() + 1, zCoord, xCoord + 1, this.getPos().getY() + 1.5f, zCoord + 1))) {
				((EntityLivingBase)entity).attackEntityFrom(DamageSource.cactus, 1f);
			}
		}
	}

	@Override
	public AxisAlignedBB getRenderBoundingBox() {
		return new AxisAlignedBB(pos.add(-2,-2,-2), pos.add(2,2,2));
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