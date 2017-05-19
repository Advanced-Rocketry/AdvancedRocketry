package zmaster587.advancedRocketry.item;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.World;
import net.minecraftforge.fluids.IFluidBlock;
import zmaster587.advancedRocketry.util.SealableBlockHandler;

/**
 * Detects if a block can be used to seal a room.
 * Created by Dark(DarkGuardsman, Robert) on 1/6/2016.
 */
public class ItemSealDetector extends Item
{
    //TODO make consume power?

	@Override
	public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn, EnumHand hand) {
		return new ActionResult<ItemStack>(EnumActionResult.SUCCESS, playerIn.getHeldItem(hand));
	}
	
	@Override
	public EnumActionResult onItemUse(EntityPlayer player,
			World world, BlockPos pos, EnumHand hand, EnumFacing facing,
			float hitX, float hitY, float hitZ) {
        if (!world.isRemote)
        {
            if (SealableBlockHandler.INSTANCE.isBlockSealed(world, pos))
            {
                player.sendMessage(new TextComponentString("Should hold a nice seal."));
            }
            else
            {
            	IBlockState state = world.getBlockState(pos);
                Material mat = state.getMaterial();
                if (SealableBlockHandler.INSTANCE.isMaterialBanned(mat))
                {
                    player.sendMessage(new TextComponentString("Material will not hold a seal."));
                }
                else if (SealableBlockHandler.INSTANCE.isBlockBanned(state.getBlock()))
                {
                    player.sendMessage(new TextComponentString("Block will not hold a seal."));
                }
                else if (SealableBlockHandler.isFulBlock(world, pos))
                {
                    player.sendMessage(new TextComponentString("Air will pass around this block."));
                }
                else if (state.getBlock() instanceof IFluidBlock)
                {
                    player.sendMessage(new TextComponentString("Air will bubble through this block"));
                }
                else
                {
                    player.sendMessage(new TextComponentString("Air will leak through this block."));
                }
            }
        }
        return EnumActionResult.SUCCESS;
	}
	
}
