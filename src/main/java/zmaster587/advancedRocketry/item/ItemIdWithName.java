package zmaster587.advancedRocketry.item;

import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.List;

public class ItemIdWithName extends Item {
	
	public ItemIdWithName(Properties properties) {
		super(properties);
	}

	public void setName(ItemStack stack, String name) {

		if(stack.hasTag()) {
			CompoundNBT nbt = stack.getTag();
			nbt.putString("name", name);
			stack.setTag(nbt);
		}
	}

	public String getName(ItemStack stack) {
		if(stack.hasTag()) {
			CompoundNBT nbt = stack.getTag();
			return nbt.getString("name");
		}

		return "";
	}
	
	
	@Override
    @OnlyIn(value=Dist.CLIENT)
	public void addInformation(ItemStack stack, World player,
			List list, ITooltipFlag bool) {
		if(stack.getDamage() == -1) {
			list.add(new StringTextComponent("Unprogrammed"));
		}
		else {
			list.add(new StringTextComponent(getName(stack)));
		}
	}
}
