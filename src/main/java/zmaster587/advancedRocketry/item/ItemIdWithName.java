package zmaster587.advancedRocketry.item;

import com.mojang.realmsclient.gui.ChatFormatting;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.List;

public class ItemIdWithName extends Item {
	
	public void setName(ItemStack stack, String name) {

		if(stack.hasTagCompound()) {
			NBTTagCompound nbt = stack.getTagCompound();
			nbt.setString("name", name);
			stack.setTagCompound(nbt);
		}
	}

	public String getName(ItemStack stack) {
		if(stack.hasTagCompound()) {
			NBTTagCompound nbt = stack.getTagCompound();
			return nbt.getString("name");
		}

		return "";
	}
	
	
	@Override
    @SideOnly(Side.CLIENT)
	public void addInformation(ItemStack stack, World player,
			List list, ITooltipFlag bool) {
		if(stack.getItemDamage() == -1) {
			list.add(ChatFormatting.GRAY + "Unprogrammed");
		}
		else {
			list.add(getName(stack));
		}
	}
}
