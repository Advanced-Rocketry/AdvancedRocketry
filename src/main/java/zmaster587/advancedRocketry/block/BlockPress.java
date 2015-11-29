package zmaster587.advancedRocketry.block;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import zmaster587.advancedRocketry.api.material.MaterialRegistry;
import zmaster587.advancedRocketry.api.material.MaterialRegistry.AllowedProducts;
import zmaster587.advancedRocketry.api.material.MaterialRegistry.Materials;
import zmaster587.advancedRocketry.recipe.RecipesMachine;
import zmaster587.libVulpes.interfaces.IRecipe;
import net.minecraft.block.Block;
import net.minecraft.block.BlockPistonBase;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class BlockPress extends BlockPistonBase {

	public BlockPress() {
		super(false);
	}

	@Override
	public void onBlockAdded(World world, int x, int y, int z)
	{
		if (!world.isRemote && world.getTileEntity(x, y, z) == null)
		{
			this.updatePistonState(world, x, y, z);
		}
	}

	@Override
	public void onNeighborBlockChange(World world, int x, int y, int z, Block block)
	{
		if (!world.isRemote)
		{
			this.updatePistonState(world, x, y, z);
		}
	}

	@Override
	public void onBlockPlacedBy(World world, int x, int y, int z, EntityLivingBase entity, ItemStack stack)
	{
		int l = 0;
		world.setBlockMetadataWithNotify(x, y, z, l, 2);

		if (!world.isRemote)
		{
			this.updatePistonState(world, x, y, z);
		}
	}

	protected boolean isIndirectlyPowered(World p_150072_1_, int p_150072_2_, int p_150072_3_, int p_150072_4_, int p_150072_5_)
	{
		return p_150072_5_ != 0 && p_150072_1_.getIndirectPowerOutput(p_150072_2_, p_150072_3_ - 1, p_150072_4_, 0) ? true : (p_150072_5_ != 1 && p_150072_1_.getIndirectPowerOutput(p_150072_2_, p_150072_3_ + 1, p_150072_4_, 1) ? true : (p_150072_5_ != 2 && p_150072_1_.getIndirectPowerOutput(p_150072_2_, p_150072_3_, p_150072_4_ - 1, 2) ? true : (p_150072_5_ != 3 && p_150072_1_.getIndirectPowerOutput(p_150072_2_, p_150072_3_, p_150072_4_ + 1, 3) ? true : (p_150072_5_ != 5 && p_150072_1_.getIndirectPowerOutput(p_150072_2_ + 1, p_150072_3_, p_150072_4_, 5) ? true : (p_150072_5_ != 4 && p_150072_1_.getIndirectPowerOutput(p_150072_2_ - 1, p_150072_3_, p_150072_4_, 4) ? true : (p_150072_1_.getIndirectPowerOutput(p_150072_2_, p_150072_3_, p_150072_4_, 0) ? true : (p_150072_1_.getIndirectPowerOutput(p_150072_2_, p_150072_3_ + 2, p_150072_4_, 1) ? true : (p_150072_1_.getIndirectPowerOutput(p_150072_2_, p_150072_3_ + 1, p_150072_4_ - 1, 2) ? true : (p_150072_1_.getIndirectPowerOutput(p_150072_2_, p_150072_3_ + 1, p_150072_4_ + 1, 3) ? true : (p_150072_1_.getIndirectPowerOutput(p_150072_2_ - 1, p_150072_3_ + 1, p_150072_4_, 4) ? true : p_150072_1_.getIndirectPowerOutput(p_150072_2_ + 1, p_150072_3_ + 1, p_150072_4_, 5)))))))))));
	}

	private ItemStack getRecipe(World world, int x, int y, int z, int meta) {
		if(world.isAirBlock(x, y-1, z))
			return null;
		Materials material = MaterialRegistry.getMaterialFromItemStack(new ItemStack(world.getBlock(x, y-1, z), 1, world.getBlockMetadata(x, y-1, z)));


		if(material == null)
			return null;

		List<IRecipe> recipes = RecipesMachine.getInstance().getRecipes(this.getClass());
		ItemStack stack = null;

		for(IRecipe recipe : recipes) {
			if(recipe.getIngredients().get(0).isItemEqual(material.getProduct(AllowedProducts.BLOCK))) {
				stack = recipe.getOutput().get(0);
				break;
			}
		}


		if(world.getBlock(x, y-2, z) == Blocks.obsidian)
			return stack;

		return null;
	}

	protected void updatePistonState(World world, int x, int y, int z) {
		int l = world.getBlockMetadata(x, y, z);
		int i1 = 0;

		if (i1 != 7)
		{

			if (!isExtended(l))
			{
				ItemStack output;
				if (this.isIndirectlyPowered(world, x, y, z, i1) && (output = getRecipe(world, x, y, z, i1)) != null)
				{
					world.setBlock(x, y-1, z,Blocks.air,0, 1);
					if(!world.isRemote) {
						world.spawnEntityInWorld(new EntityItem(world, x, y-0.5f, z, output));
					}

					world.addBlockEvent(x, y, z, this, 0, i1);
				}
			}
			else if (isExtended(l))
			{
				world.setBlockMetadataWithNotify(x, y, z, i1, 2);
			}
		}
	}

}
