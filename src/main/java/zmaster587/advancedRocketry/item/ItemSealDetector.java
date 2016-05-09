package zmaster587.advancedRocketry.item;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ChatComponentText;
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
    public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player)
    {
        //AtmosphereHandler.getOxygenHandler(this.worldObj.provider.dimensionId)
        //TODO test atmosphere
        return stack;
    }

    @Override
    public boolean onItemUse(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int side, float xx, float yy, float zz)
    {
        if (!world.isRemote)
        {
            if (SealableBlockHandler.INSTANCE.isBlockSealed(world, x, y, z))
            {
                player.addChatComponentMessage(new ChatComponentText("Should hold a nice seal."));
            }
            else
            {
                Block block = world.getBlock(x, y, z);
                Material mat = block.getMaterial();
                if (SealableBlockHandler.INSTANCE.isMaterialBanned(mat))
                {
                    player.addChatComponentMessage(new ChatComponentText("Material will not hold a seal."));
                }
                else if (SealableBlockHandler.INSTANCE.isBlockBanned(block))
                {
                    player.addChatComponentMessage(new ChatComponentText("Block will not hold a seal."));
                }
                else if (SealableBlockHandler.isFulBlock(block))
                {
                    player.addChatComponentMessage(new ChatComponentText("Air will pass around this block."));
                }
                else if (block instanceof IFluidBlock)
                {
                    player.addChatComponentMessage(new ChatComponentText("Air will bubble through this block"));
                }
                else
                {
                    player.addChatComponentMessage(new ChatComponentText("Air will leak through this block."));
                }
            }
        }
        return true;
    }
}
