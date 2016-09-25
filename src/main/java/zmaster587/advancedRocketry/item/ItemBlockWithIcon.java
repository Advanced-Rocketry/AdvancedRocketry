package zmaster587.advancedRocketry.item;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class ItemBlockWithIcon extends Item {
	private Block block;

    public ItemBlockWithIcon(Block p_i45329_1_)
    {
        this.block = p_i45329_1_;
    }

    
    /**
     * Callback for item usage. If the item does something special on right clicking, he will have one of those. Return
     * True if something happen and false if it don't. This is for ITEMS, not BLOCKS
     */
    @Override
    public EnumActionResult onItemUse(ItemStack stack, EntityPlayer player,
    		World world, BlockPos pos, EnumHand hand, EnumFacing facing,
    		float hitX, float hitY, float hitZ) {
        IBlockState block = world.getBlockState(pos);

        if (block == Blocks.SNOW_LAYER.getDefaultState())
        {
            facing = EnumFacing.UP;
        }
        else if (!block.getBlock().isReplaceable(world, pos))
        {
            if (p_77648_7_ == 0)
            {
                --y;
            }

            if (p_77648_7_ == 1)
            {
                ++y;
            }

            if (p_77648_7_ == 2)
            {
                --z;
            }

            if (p_77648_7_ == 3)
            {
                ++z;
            }

            if (p_77648_7_ == 4)
            {
                --x;
            }

            if (p_77648_7_ == 5)
            {
                ++x;
            }
        }

        if (!player.canPlayerEdit(x, y, z, p_77648_7_, stack))
        {
            return false;
        }
        else if (stack.stackSize == 0)
        {
            return false;
        }
        else
        {
            if (world.canPlaceEntityOnSide(this.block, x, y, z, false, p_77648_7_, (Entity)null, stack))
            {
                int i1 = this.block.onBlockPlaced(world, x, y, z, p_77648_7_, p_77648_8_, p_77648_9_, p_77648_10_, 0);

                if (world.setBlock(x, y, z, this.block, i1, 3))
                {
                    if (world.getBlock(x, y, z) == this.block)
                    {
                        this.block.onBlockPlacedBy(world, x, y, z, player, stack);
                        this.block.onPostBlockPlaced(world, x, y, z, i1);
                    }

                    world.playSoundEffect((double)((float)x + 0.5F), (double)((float)y + 0.5F), (double)((float)z + 0.5F), this.block.stepSound.func_150496_b(), (this.block.stepSound.getVolume() + 1.0F) / 2.0F, this.block.stepSound.getPitch() * 0.8F);
                    --stack.stackSize;
                }
            }

            return true;
        }
    }
   
}
