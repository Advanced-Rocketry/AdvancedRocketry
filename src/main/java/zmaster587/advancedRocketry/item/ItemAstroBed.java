package zmaster587.advancedRocketry.item;

import zmaster587.advancedRocketry.api.AdvancedRocketryBlocks;
import net.minecraft.block.Block;
import net.minecraft.block.BlockBed;
import net.minecraft.block.SoundType;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

public class ItemAstroBed extends Item {
	
	 public EnumActionResult onItemUse(ItemStack stack, EntityPlayer playerIn, World worldIn, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ)
	    {
	        if (worldIn.isRemote)
	        {
	            return EnumActionResult.SUCCESS;
	        }
	        else if (facing != EnumFacing.UP)
	        {
	            return EnumActionResult.FAIL;
	        }
	        else
	        {
	            IBlockState iblockstate = worldIn.getBlockState(pos);
	            Block block = iblockstate.getBlock();
	            boolean flag = block.isReplaceable(worldIn, pos);

	            if (!flag)
	            {
	                pos = pos.up();
	            }

	            int i = MathHelper.floor((double)(playerIn.rotationYaw * 4.0F / 360.0F) + 0.5D) & 3;
	            EnumFacing enumfacing = EnumFacing.getHorizontal(i);
	            BlockPos blockpos = pos.offset(enumfacing);

	            if (playerIn.canPlayerEdit(pos, facing, stack) && playerIn.canPlayerEdit(blockpos, facing, stack))
	            {
	                boolean flag1 = worldIn.getBlockState(blockpos).getBlock().isReplaceable(worldIn, blockpos);
	                boolean flag2 = flag || worldIn.isAirBlock(pos);
	                boolean flag3 = flag1 || worldIn.isAirBlock(blockpos);

	                if (flag2 && flag3 && worldIn.getBlockState(pos.down()).isSideSolid(worldIn, pos.down(), EnumFacing.UP) && worldIn.getBlockState(blockpos.down()).isSideSolid(worldIn, blockpos.down(), EnumFacing.UP))
	                {
	                    IBlockState iblockstate1 = AdvancedRocketryBlocks.blockAstroBed.getDefaultState().withProperty(BlockBed.OCCUPIED, Boolean.valueOf(false)).withProperty(BlockBed.FACING, enumfacing).withProperty(BlockBed.PART, BlockBed.EnumPartType.FOOT);

	                    if (worldIn.setBlockState(pos, iblockstate1, 11))
	                    {
	                        IBlockState iblockstate2 = iblockstate1.withProperty(BlockBed.PART, BlockBed.EnumPartType.HEAD);
	                        worldIn.setBlockState(blockpos, iblockstate2, 11);
	                    }

	                    SoundType soundtype = iblockstate1.getBlock().getSoundType();
	                    worldIn.playSound((EntityPlayer)null, pos, soundtype.getPlaceSound(), SoundCategory.BLOCKS, (soundtype.getVolume() + 1.0F) / 2.0F, soundtype.getPitch() * 0.8F);
	                    stack.setCount(stack.getCount() - 1);
	                    return EnumActionResult.SUCCESS;
	                }
	                else
	                {
	                    return EnumActionResult.FAIL;
	                }
	            }
	            else
	            {
	                return EnumActionResult.FAIL;
	            }
	        }
	    }
}
