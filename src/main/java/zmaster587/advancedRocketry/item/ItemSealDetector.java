package zmaster587.advancedRocketry.item;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ChatComponentText;
import net.minecraft.world.World;
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
        if(!world.isRemote)
        {
            if(SealableBlockHandler.INSTANCE.isBlockSealed(world, x, y, z))
            {
                player.addChatComponentMessage(new ChatComponentText("Block is sealed."));
            }
            else
            {
                player.addChatComponentMessage(new ChatComponentText("Air will leak threw this block"));
            }
        }
        return true;
    }
}
