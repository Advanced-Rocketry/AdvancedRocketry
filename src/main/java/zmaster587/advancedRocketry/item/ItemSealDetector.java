package zmaster587.advancedRocketry.item;

import net.minecraft.block.BlockState;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.fluids.IFluidBlock;
import zmaster587.advancedRocketry.util.SealableBlockHandler;
import zmaster587.libVulpes.LibVulpes;

/**
 * Detects if a block can be used to seal a room.
 * Created by Dark(DarkGuardsman, Robert) on 1/6/2016.
 */
public class ItemSealDetector extends Item
{
    //TODO make consume power?

	public ItemSealDetector(Properties properties) {
		super(properties);
	}

	@Override
	public ActionResult<ItemStack> onItemRightClick(World worldIn, PlayerEntity playerIn, Hand hand) {
		return new ActionResult<ItemStack>(ActionResultType.SUCCESS, playerIn.getHeldItem(hand));
	}
	
	@Override
	public ActionResultType onItemUse(ItemUseContext context) {
		World world = context.getWorld();
		BlockPos pos = context.getPos();
		PlayerEntity player = context.getPlayer();
        if (!world.isRemote)
        {
            if (SealableBlockHandler.INSTANCE.isBlockSealed(world, pos))
            {
                player.sendMessage(new TranslationTextComponent("msg.sealdetector.sealed"), Util.field_240973_b_);
            }
            else
            {
            	BlockState state = world.getBlockState(pos);
                Material mat = state.getMaterial();
                if (SealableBlockHandler.INSTANCE.isMaterialBanned(mat))
                {
                    player.sendMessage(new TranslationTextComponent("msg.sealdetector.notsealmat"), Util.field_240973_b_);
                }
                else if (SealableBlockHandler.INSTANCE.isBlockBanned(state.getBlock()))
                {
                    player.sendMessage(new TranslationTextComponent("msg.sealdetector.notsealblock"), Util.field_240973_b_);
                }
                else if (SealableBlockHandler.isFullBlock(world, pos))
                {
                    player.sendMessage(new TranslationTextComponent("msg.sealdetector.notfullblock"), Util.field_240973_b_);
                }
                else if (state.getBlock() instanceof IFluidBlock)
                {
                    player.sendMessage(new TranslationTextComponent("msg.sealdetector.fluid"), Util.field_240973_b_);
                }
                else
                {
                    player.sendMessage(new TranslationTextComponent("msg.sealdetector.other"), Util.field_240973_b_);
                }
            }
        }
        return ActionResultType.SUCCESS;
	}
	
}
