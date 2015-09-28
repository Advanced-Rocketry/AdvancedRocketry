package zmaster587.advancedRocketry.tile.multiblock;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import zmaster587.advancedRocketry.Inventory.TextureResources;
import zmaster587.advancedRocketry.Inventory.modules.IModularInventory;
import zmaster587.advancedRocketry.Inventory.modules.ModuleBase;
import zmaster587.advancedRocketry.Inventory.modules.ModuleProgress;
import zmaster587.advancedRocketry.api.AdvRocketryBlocks;
import zmaster587.advancedRocketry.client.render.util.ProgressBarImage;
import zmaster587.advancedRocketry.recipe.RecipesMachine;
import zmaster587.advancedRocketry.tile.TileInputHatch;
import zmaster587.advancedRocketry.tile.TileOutputHatch;
import zmaster587.libVulpes.block.BlockMeta;
import zmaster587.libVulpes.interfaces.IRecipe;
import zmaster587.libVulpes.tile.IMultiblock;

public class TileElectricArcFurnace extends TileMultiblockMachine implements IModularInventory {

	public static final Object[][][] structure = { 
		{	{Blocks.air,Blocks.air,Blocks.air,Blocks.air,Blocks.air},
			{Blocks.air,'P',AdvRocketryBlocks.blockBlastBrick,'P',Blocks.air},
			{Blocks.air,AdvRocketryBlocks.blockBlastBrick,AdvRocketryBlocks.blockBlastBrick,AdvRocketryBlocks.blockBlastBrick,Blocks.air},
			{Blocks.air,AdvRocketryBlocks.blockBlastBrick,'P',AdvRocketryBlocks.blockBlastBrick,Blocks.air},
			{Blocks.air,Blocks.air,Blocks.air,Blocks.air,Blocks.air},
		},

		{	{Blocks.air,AdvRocketryBlocks.blockBlastBrick,AdvRocketryBlocks.blockBlastBrick,AdvRocketryBlocks.blockBlastBrick,Blocks.air},
			{AdvRocketryBlocks.blockBlastBrick,Blocks.gold_block,Blocks.air,Blocks.gold_block,AdvRocketryBlocks.blockBlastBrick},
			{AdvRocketryBlocks.blockBlastBrick,Blocks.air,Blocks.air,Blocks.air,AdvRocketryBlocks.blockBlastBrick},
			{AdvRocketryBlocks.blockBlastBrick,Blocks.air,Blocks.gold_block,Blocks.air,AdvRocketryBlocks.blockBlastBrick},
			{Blocks.air,AdvRocketryBlocks.blockBlastBrick,AdvRocketryBlocks.blockBlastBrick,AdvRocketryBlocks.blockBlastBrick,Blocks.air},
		},

		{	{AdvRocketryBlocks.blockBlastBrick,AdvRocketryBlocks.blockBlastBrick,AdvRocketryBlocks.blockBlastBrick,AdvRocketryBlocks.blockBlastBrick,AdvRocketryBlocks.blockBlastBrick},
			{AdvRocketryBlocks.blockBlastBrick,Blocks.air,Blocks.air,Blocks.air,AdvRocketryBlocks.blockBlastBrick},
			{AdvRocketryBlocks.blockBlastBrick,Blocks.air,Blocks.air,Blocks.air,AdvRocketryBlocks.blockBlastBrick},
			{AdvRocketryBlocks.blockBlastBrick,Blocks.air,Blocks.air,Blocks.air,AdvRocketryBlocks.blockBlastBrick},
			{AdvRocketryBlocks.blockBlastBrick,AdvRocketryBlocks.blockBlastBrick,AdvRocketryBlocks.blockBlastBrick,AdvRocketryBlocks.blockBlastBrick,AdvRocketryBlocks.blockBlastBrick},
		},

		{	{AdvRocketryBlocks.blockBlastBrick,'*','c','*',AdvRocketryBlocks.blockBlastBrick},
			{'*',Blocks.air,Blocks.air,Blocks.air,'*'},
			{'*',Blocks.air,Blocks.air,Blocks.air, '*'},
			{'*',Blocks.air, Blocks.air,Blocks.air, '*'},
			{AdvRocketryBlocks.blockBlastBrick,'*','*','*',AdvRocketryBlocks.blockBlastBrick},
		},

		{	{AdvRocketryBlocks.blockBlastBrick,AdvRocketryBlocks.blockBlastBrick,AdvRocketryBlocks.blockBlastBrick,AdvRocketryBlocks.blockBlastBrick,AdvRocketryBlocks.blockBlastBrick},
			{AdvRocketryBlocks.blockBlastBrick,AdvRocketryBlocks.blockBlastBrick,AdvRocketryBlocks.blockBlastBrick,AdvRocketryBlocks.blockBlastBrick,AdvRocketryBlocks.blockBlastBrick},
			{AdvRocketryBlocks.blockBlastBrick,AdvRocketryBlocks.blockBlastBrick,AdvRocketryBlocks.blockBlastBrick,AdvRocketryBlocks.blockBlastBrick,AdvRocketryBlocks.blockBlastBrick},
			{AdvRocketryBlocks.blockBlastBrick,AdvRocketryBlocks.blockBlastBrick,AdvRocketryBlocks.blockBlastBrick,AdvRocketryBlocks.blockBlastBrick,AdvRocketryBlocks.blockBlastBrick},
			{AdvRocketryBlocks.blockBlastBrick,AdvRocketryBlocks.blockBlastBrick,AdvRocketryBlocks.blockBlastBrick,AdvRocketryBlocks.blockBlastBrick,AdvRocketryBlocks.blockBlastBrick},
		}

	};
	
	@Override
	public List<BlockMeta> getAllowableWildCardBlocks() {
		List<BlockMeta> list = super.getAllowableWildCardBlocks();
		list.add(new BlockMeta(AdvRocketryBlocks.blockHatch, 0));
		list.add(new BlockMeta(AdvRocketryBlocks.blockHatch, 1));
		list.add(new BlockMeta(AdvRocketryBlocks.blockBlastBrick, -1));
		return list;
	}

	@Override
	protected void integrateTile(TileEntity tile) {
		super.integrateTile(tile);
	}
	
	//Since this doesn't have a render just set the meta and masterBlock
	@Override
	protected void replaceStandardBlock(int xCoord, int yCoord, int zCoord,	Block block, TileEntity tile) {

		if(block == AdvRocketryBlocks.blockBlastBrick) {
			worldObj.setBlockMetadataWithNotify(xCoord, yCoord, zCoord, 1, 2);

			TileEntity tileEntity = worldObj.getTileEntity(xCoord, yCoord, zCoord);

			if(tileEntity instanceof IMultiblock) {
				IMultiblock multiblock = (IMultiblock)tileEntity;
				multiblock.setComplete(this.xCoord, this.yCoord, this.zCoord);
			}	
		}
	}
		
	@Override
	protected void destroyBlockAt(int x, int y, int z, Block block,	TileEntity tile) {
		if(block == AdvRocketryBlocks.blockBlastBrick) {
			worldObj.setBlockMetadataWithNotify(x, y, z, 0, 2);
		}
		else
			super.destroyBlockAt(x, y, z, block, tile);
	}

	@Override
	public Object[][][] getStructure() {
		return structure;
	}

	@Override
	public List<IRecipe> getMachineRecipeList() {
		return RecipesMachine.getInstance().getRecipes(this.getClass());
	}

	@Override
	public String getMachineName() {
		return "tile.electricArcFurnace.name";
	}
	
	@Override
	public List<ModuleBase> getModules() {
		// TODO Auto-generated method stub
		List<ModuleBase> modules = super.getModules();
		
		modules.add(new ModuleProgress(100, 20, 0, TextureResources.arcFurnaceProgressBar, this));
		return modules;
	}
}
