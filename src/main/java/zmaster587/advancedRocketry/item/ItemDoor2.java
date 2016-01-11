package zmaster587.advancedRocketry.item;

import net.minecraft.init.Items;
import zmaster587.advancedRocketry.api.AdvancedRocketryBlocks;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

public class ItemDoor2 extends net.minecraft.item.ItemDoor {

	public ItemDoor2(Material material) {
		super(material);
		this.setMaxStackSize(Items.iron_door.getItemStackLimit());
	}


	/**
	 * Callback for item usage. If the item does something special on right clicking, he will have one of those. Return
	 * True if something happen and false if it don't. This is for ITEMS, not BLOCKS
	 */
	public boolean onItemUse(ItemStack itemstack, EntityPlayer player, World world, int x, int y, int z, int meta, float hitPosX, float hitPosY, float hitPosZ)
	{
		if (meta != 1)
		{
			return false;
		}
		else
		{
			++y;
			Block block;

			block = AdvancedRocketryBlocks.blockAirLock;

			if (player.canPlayerEdit(x, y, z, meta, itemstack) && player.canPlayerEdit(x, y + 1, z, meta, itemstack))
			{
				if (!block.canPlaceBlockAt(world, x, y, z))
				{
					return false;
				}
				else
				{
					int i1 = MathHelper.floor_double((double)((player.rotationYaw + 180.0F) * 4.0F / 360.0F) - 0.5D) & 3;
					placeDoorBlock(world, x, y, z, i1, block);
					--itemstack.stackSize;
					return true;
				}
			}
			else
			{
				return false;
			}
		}
	}
}
