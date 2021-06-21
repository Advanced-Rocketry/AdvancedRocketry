package zmaster587.advancedRocketry.item;

import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nonnull;
import java.util.List;

public class ItemIdWithName extends Item {
	
<<<<<<< HEAD
	public ItemIdWithName(Properties properties) {
		super(properties);
	}

	public void setName(ItemStack stack, String name) {
=======
	public void setName(@Nonnull ItemStack stack, String name) {
>>>>>>> origin/feature/nuclearthermalrockets

		if(stack.hasTag()) {
			CompoundNBT nbt = stack.getTag();
			nbt.putString("name", name);
			stack.setTag(nbt);
		}
	}

<<<<<<< HEAD
	public String getName(ItemStack stack) {
		if(stack.hasTag()) {
			CompoundNBT nbt = stack.getTag();
=======
	public String getName(@Nonnull ItemStack stack) {
		if(stack.hasTagCompound()) {
			NBTTagCompound nbt = stack.getTagCompound();
>>>>>>> origin/feature/nuclearthermalrockets
			return nbt.getString("name");
		}

		return "";
	}
	
	
	@Override
<<<<<<< HEAD
    @OnlyIn(value=Dist.CLIENT)
	public void addInformation(ItemStack stack, World player,
			List list, ITooltipFlag bool) {
		if(stack.getDamage() == -1) {
			list.add(new StringTextComponent("Unprogrammed"));
=======
    @SideOnly(Side.CLIENT)
	public void addInformation(@Nonnull ItemStack stack, World player, List<String> list, ITooltipFlag bool) {
		if(stack.getItemDamage() == -1) {
			list.add(ChatFormatting.GRAY + "Unprogrammed");
>>>>>>> origin/feature/nuclearthermalrockets
		}
		else {
			list.add(new StringTextComponent(getName(stack)));
		}
	}
}
