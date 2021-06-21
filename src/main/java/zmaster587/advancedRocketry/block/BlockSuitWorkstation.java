package zmaster587.advancedRocketry.block;

import net.minecraft.block.BlockState;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
<<<<<<< HEAD
import net.minecraft.nbt.CompoundNBT;
=======
>>>>>>> origin/feature/nuclearthermalrockets
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
<<<<<<< HEAD
					ItemStack newStack = itemstack.copy();
					newStack.setCount(j1);
					entityitem = new ItemEntity(world, (double)((float)pos.getX() + f), (double)((float)pos.getY() + f1), (double)((float)pos.getZ() + f2), newStack);
					float f3 = 0.05F;
					
					entityitem.setMotion((double)((float)world.rand.nextGaussian() * f3),
						(double)((float)world.rand.nextGaussian() * f3 + 0.2F),
						(double)((float)world.rand.nextGaussian() * f3));
=======
					entityitem = new EntityItem(world, (float)pos.getX() + f, (float)pos.getY() + f1, (float)pos.getZ() + f2, new ItemStack(itemstack.getItem(), j1, itemstack.getItemDamage()));
					float f3 = 0.05F;
					entityitem.motionX = (float)world.rand.nextGaussian() * f3;
					entityitem.motionY = (float)world.rand.nextGaussian() * f3 + 0.2F;
					entityitem.motionZ = (float)world.rand.nextGaussian() * f3;
>>>>>>> origin/feature/nuclearthermalrockets

					if (itemstack.hasTag())
					{
<<<<<<< HEAD
						entityitem.getItem().setTag((CompoundNBT)itemstack.getTag().copy());
=======
						entityitem.getItem().setTagCompound(itemstack.getTagCompound().copy());
>>>>>>> origin/feature/nuclearthermalrockets
					}
				}
			}
		}

		world.removeTileEntity(pos);
	}
}
