package zmaster587.advancedRocketry.item;

import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import zmaster587.advancedRocketry.util.DimensionBlockPosition;
import zmaster587.advancedRocketry.util.NBTStorableListList;
import zmaster587.libVulpes.LibVulpes;

import javax.annotation.Nonnull;
import java.util.List;

public class ItemSpaceElevatorChip extends Item {

    public ItemSpaceElevatorChip() {

    }

    @Override
    public boolean isDamageable() {
        return false;
    }

    public List<DimensionBlockPosition> getBlockPositions(@Nonnull ItemStack stack) {
        NBTStorableListList list = new NBTStorableListList();

        if (stack.hasTagCompound()) {
            list.readFromNBT(stack.getTagCompound());
        }

        return list.getList();
    }

    public void setBlockPositions(@Nonnull ItemStack stack, List<DimensionBlockPosition> listToStore) {
        NBTStorableListList list = new NBTStorableListList(listToStore);

        if (stack.hasTagCompound()) {

            if (listToStore.isEmpty())
                stack.getTagCompound().removeTag("positions");
            else {
                list.writeToNBT(stack.getTagCompound());
            }
        } else if (!listToStore.isEmpty()) {
            NBTTagCompound nbt = new NBTTagCompound();
            list.writeToNBT(nbt);

            stack.setTagCompound(nbt);
        }
    }

    @Override
    public void addInformation(@Nonnull ItemStack stack, World player, List<String> list, ITooltipFlag bool) {

        int numPos = getBlockPositions(stack).size();

        if (numPos > 0)
            list.add("Contains " + numPos + " entries");
        else
            list.add(LibVulpes.proxy.getLocalizedString("msg.empty"));
    }

}
