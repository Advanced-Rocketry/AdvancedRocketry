package zmaster587.advancedRocketry.block;

import net.minecraft.block.BlockState;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import zmaster587.libVulpes.block.BlockTile;
import zmaster587.libVulpes.inventory.GuiHandler;

public class BlockSuitWorkstation extends BlockTile {

	public BlockSuitWorkstation(Properties properties, GuiHandler.guiId guiId) {
		super(properties, guiId);
	}

	@Override
	public void onReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean isMoving) {
		TileEntity tile = world.getTileEntity(pos);

		//This code could use some optimization -Dark
		if (tile instanceof IInventory)
		{
			IInventory inventory = (IInventory)tile;
			int i1 = 0;
			ItemStack itemstack = inventory.getStackInSlot(i1);

			if (!itemstack.isEmpty())
			{
				float f = world.rand.nextFloat() * 0.8F + 0.1F;
				float f1 = world.rand.nextFloat() * 0.8F + 0.1F;
				ItemEntity entityitem;

				for (float f2 = world.rand.nextFloat() * 0.8F + 0.1F; itemstack.getCount() > 0; world.addEntity(entityitem))
				{
					int j1 = world.rand.nextInt(21) + 10;

					if (j1 > itemstack.getCount())
					{
						j1 = itemstack.getCount();
					}

					itemstack.setCount(itemstack.getCount() - j1 );
					ItemStack newStack = itemstack.copy();
					newStack.setCount(j1);
					entityitem = new ItemEntity(world, (float)pos.getX() + f, (float)pos.getY() + f1, (float)pos.getZ() + f2, newStack);
					float f3 = 0.05F;
					
					entityitem.setMotion((float)world.rand.nextGaussian() * f3,
							(float)world.rand.nextGaussian() * f3 + 0.2F,
							(float)world.rand.nextGaussian() * f3);

					if (itemstack.hasTag())
					{
						entityitem.getItem().setTag(itemstack.getTag().copy());
					}
				}
			}
		}

		world.removeTileEntity(pos);
	}
}
