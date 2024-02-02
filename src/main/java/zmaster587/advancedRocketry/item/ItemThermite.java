package zmaster587.advancedRocketry.item;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import javax.annotation.Nonnull;

public class ItemThermite extends Item {

    @Override
    public int getItemBurnTime(@Nonnull ItemStack itemStack) {
        return 6000;
    }

}
