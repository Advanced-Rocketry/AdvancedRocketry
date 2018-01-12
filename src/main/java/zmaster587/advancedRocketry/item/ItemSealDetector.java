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
import zmaster587.libVulpes.LibVulpes;
import zmaster587.libVulpes.util.BlockPosition;

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
                player.addChatComponentMessage(new ChatComponentText(LibVulpes.proxy.getLocalizedString("msg.sealdetector.sealed")));
            }
            else
            {
                Block block = world.getBlock(x, y, z);
                Material mat = block.getMaterial();
                if (SealableBlockHandler.INSTANCE.isMaterialBanned(mat))
                {
                    player.addChatComponentMessage(new ChatComponentText(LibVulpes.proxy.getLocalizedString("msg.sealdetector.notsealmat")));
                }
                else if (SealableBlockHandler.INSTANCE.isBlockBanned(block))
                {
                    player.addChatComponentMessage(new ChatComponentText(LibVulpes.proxy.getLocalizedString("msg.sealdetector.notsealblock")));
                }
                else if (SealableBlockHandler.isFulBlock(world, block, new BlockPosition(x, y, z)))
                {
                    player.addChatComponentMessage(new ChatComponentText(LibVulpes.proxy.getLocalizedString("msg.sealdetector.notfullblock")));
                }
                else if (block instanceof IFluidBlock)
                {
                    player.addChatComponentMessage(new ChatComponentText(LibVulpes.proxy.getLocalizedString("msg.sealdetector.fluid")));
                }
                else
                {
                    player.addChatComponentMessage(new ChatComponentText(LibVulpes.proxy.getLocalizedString("msg.sealdetector.other")));
                }
            }
        }
        return true;
    }
}
