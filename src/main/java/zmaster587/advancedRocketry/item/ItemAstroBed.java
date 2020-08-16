package zmaster587.advancedRocketry.item;

import net.minecraft.block.BedBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.SoundType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BedItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.item.Items;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import zmaster587.advancedRocketry.api.AdvancedRocketryBlocks;

public class ItemAstroBed extends BedItem {

	public ItemAstroBed(Block blockIn, Properties properties) {
		super(blockIn, properties);
		// TODO Auto-generated constructor stub
	}


/*	@Override
	public ActionResultType onItemUse(ItemUseContext context)
	{
		World worldIn = context.getWorld();
		Direction facing = context.getFace();
		PlayerEntity playerIn = context.getPlayer();
		Hand hand = context.getHand();
		BlockPos pos = context.getPos();
		Items.BLACK_BED
		if (worldIn.isRemote)
		{
			return ActionResultType.SUCCESS;
		}
		else if (facing != Direction.UP)
		{
			return ActionResultType.FAIL;
		}
		else
		{
			ItemStack stack = playerIn.getHeldItem(hand);
			BlockState iblockstate = worldIn.getBlockState(pos);
			Block block = iblockstate.getBlock();
			boolean flag = block.isReplaceable(worldIn, pos);

			if (!flag)
			{
				pos = pos.up();
			}

			int i = MathHelper.floor((double)(playerIn.rotationYaw * 4.0F / 360.0F) + 0.5D) & 3;
			Direction enumfacing = Direction.byHorizontalIndex(i);
			BlockPos blockpos = pos.offset(enumfacing);

			if (playerIn.canPlayerEdit(pos, facing, stack) && playerIn.canPlayerEdit(blockpos, facing, stack))
			{
				boolean flag1 = worldIn.getBlockState(blockpos).getBlock().isReplaceable(worldIn, blockpos);
				boolean flag2 = flag || worldIn.isAirBlock(pos);
				boolean flag3 = flag1 || worldIn.isAirBlock(blockpos);

				if (flag2 && flag3 && worldIn.getBlockState(pos.down()).isSideSolid(worldIn, pos.down(), Direction.UP) && worldIn.getBlockState(blockpos.down()).isSideSolid(worldIn, blockpos.down(), Direction.UP))
				{
					BlockState iblockstate1 = AdvancedRocketryBlocks.blockAstroBed.getDefaultState().with(BedBlock.OCCUPIED, Boolean.valueOf(false)).with(BedBlock.FACING, enumfacing).with(BedBlock.PART, BedBlock.EnumPartType.FOOT);

					if (worldIn.setBlockState(pos, iblockstate1, 11))
					{
						BlockState iblockstate2 = iblockstate1.with(BedBlock.PART, BedBlock.EnumPartType.HEAD);
						worldIn.setBlockState(blockpos, iblockstate2, 11);
					}

					SoundType soundtype = iblockstate1.getBlock().getSoundType();
					worldIn.playSound((PlayerEntity)null, pos, soundtype.getPlaceSound(), SoundCategory.BLOCKS, (soundtype.getVolume() + 1.0F) / 2.0F, soundtype.getPitch() * 0.8F);
					stack.setCount(stack.getCount() - 1);
					return ActionResultType.SUCCESS;
				}
				else
				{
					return ActionResultType.FAIL;
				}
			}
			else
			{
				return ActionResultType.FAIL;
			}
		}
	}*/
}
