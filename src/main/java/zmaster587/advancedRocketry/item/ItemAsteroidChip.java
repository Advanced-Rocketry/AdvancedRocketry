package zmaster587.advancedRocketry.item;

import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import zmaster587.libVulpes.LibVulpes;

import javax.annotation.Nonnull;
import java.util.List;

public class ItemAsteroidChip  extends ItemMultiData {

	private static final String uuidIdentifier = "UUID";
	private static final String astType = "astype";
	public ItemAsteroidChip(Properties props) {
		super(props);
	}

	@Override
	public boolean isDamageable() {
		return false;
	}


	/**
	 * Removes any Information and reset the stack to a default state
	 * @param stack stack to erase
	 */
<<<<<<< HEAD
	public void erase(ItemStack stack) {
		stack.setTag(null);
	}

	public Long getUUID(ItemStack stack) {
		if(stack.hasTag())
			return stack.getTag().getLong(uuidIdentifier);
		return null;
	}

	public void setUUID(ItemStack stack, long uuid) {
		CompoundNBT nbt;
		if(stack.hasTag())
			nbt = stack.getTag();
=======
	public void erase(@Nonnull ItemStack stack) {
		stack.setTagCompound(null);
	}

	public Long getUUID(@Nonnull ItemStack stack) {
		if(stack.hasTagCompound())
			return stack.getTagCompound().getLong(uuidIdentifier);
		return null;
	}

	public void setUUID(@Nonnull ItemStack stack, long uuid) {
		NBTTagCompound nbt;
		if(stack.hasTagCompound())
			nbt = stack.getTagCompound();
>>>>>>> origin/feature/nuclearthermalrockets
		else
			nbt = new CompoundNBT();

		nbt.putLong(uuidIdentifier,uuid);
		stack.setTag(nbt);
	}
	
<<<<<<< HEAD
	public String getType(ItemStack stack) {
		if(stack.hasTag())
			return stack.getTag().getString(astType);
		return null;
	}

	public void setType(ItemStack stack, String type) {
		CompoundNBT nbt;
		if(stack.hasTag())
			nbt = stack.getTag();
=======
	public String getType(@Nonnull ItemStack stack) {
		if(stack.hasTagCompound())
			return stack.getTagCompound().getString(astType);
		return null;
	}

	public void setType(@Nonnull ItemStack stack, String type) {
		NBTTagCompound nbt;
		if(stack.hasTagCompound())
			nbt = stack.getTagCompound();
>>>>>>> origin/feature/nuclearthermalrockets
		else
			nbt = new CompoundNBT();

		nbt.putString(astType,type);
		stack.setTag(nbt);
	}

	@Override
	public void addInformation(@Nonnull ItemStack stack, World player, List<String> list, ITooltipFlag bool) {

		if(!stack.hasTag()) {
			list.add(new TranslationTextComponent("msg.unprogrammed"));
		}
		else {
			if(stack.getDamage()  == 0) {

				list.add(new StringTextComponent(LibVulpes.proxy.getLocalizedString("msg.asteroidchip.asteroid") + "-" + TextFormatting.DARK_GREEN  + getUUID(stack)));

				super.addInformation(stack, player, list, bool);

				//list.add("Mass: " + unknown);
				//list.add("Atmosphere Density: " + unknown);
				//list.add("Distance From Star: " + unknown);

			}
		}
	}

}
