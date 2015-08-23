package zmaster587.advancedRocketry.item;

import com.mojang.realmsclient.gui.ChatFormatting;

import zmaster587.advancedRocketry.world.DimensionManager;
import zmaster587.advancedRocketry.world.DimensionProperties;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

public class ItemPlanetIdentificationChip extends Item {

	private static final String dimensionNameIdentifier = "DimensionName";
	private static final String dimensionIdIdentifier = "dimId";
	
	@Override
	public boolean isDamageable() {
		return false;
	}

	/**
	 * @param stack itemStack of this item-type
	 * @return the DimensionProperties of the dimId stored on the item or null if invalid
	 */
	public DimensionProperties getDimension(ItemStack stack) {
		if(stack.hasTagCompound()) {
			return DimensionManager.getInstance().getDimensionProperties(stack.getTagCompound().getInteger(dimensionIdIdentifier));
		}
		return null;
	}

	/**
	 * @param stack ItemStack to check against
	 * @return true of the dimension stored on the stack exists and is valid
	 */
	public boolean hasValidDimension(ItemStack stack) {
		if(stack.hasTagCompound()) {
			return DimensionManager.getInstance().isDimensionCreated(stack.getTagCompound().getInteger(dimensionIdIdentifier));
		}

		return false;
	}

	/**
	 * Removes any Information and reset the stack to a default state
	 * @param stack stack to erase
	 */
	public void erase(ItemStack stack) {
		stack.setTagCompound(null);
	}

	/**
	 * Sets the dimId and adds the planet's name to the chip
	 * @param stack itemStack to operate on
	 * @param dimensionId dimension Id number
	 */
	public void setDimensionId(ItemStack stack, int dimensionId) {

		DimensionProperties properties = DimensionManager.getInstance().getDimensionProperties(dimensionId);
		if(properties == null) {
			erase(stack);
			return;
		}

		NBTTagCompound nbt = new NBTTagCompound();
		nbt.setInteger(dimensionIdIdentifier, dimensionId);
		nbt.setString(dimensionNameIdentifier, properties.getName());
		stack.setTagCompound(nbt);
	}

	/**
	 * @param stack stack to get the dimId from
	 * @return id of the dimension stored or -1 if invalid
	 */
	public int getDimensionId(ItemStack stack) {
		if(stack.hasTagCompound())
			return stack.getTagCompound().getInteger(dimensionIdIdentifier);
		return -1;
	}

	/**
	 * Nicer way of getting dimension info from and itemStack
	 * @param stack stack to get the DimensionProperties object from
	 * @return DimensionProperties Object of the relevent dimension or null if invalid
	 */
	public DimensionProperties getDimensionProperties(ItemStack stack) {
		if(stack.hasTagCompound())
			return DimensionManager.getInstance().getDimensionProperties(stack.getTagCompound().getInteger(dimensionIdIdentifier));
		return null;
	}

	@Override
	public void addInformation(ItemStack stack, net.minecraft.entity.player.EntityPlayer player, java.util.List list, boolean bool) {

		if(hasValidDimension(stack)) {
			
			String unknown = ChatFormatting.YELLOW + "???";
			String dimName = stack.getTagCompound().getString(dimensionNameIdentifier);
			
			list.add("Planet Name: " + ChatFormatting.DARK_GREEN  + dimName);
			list.add("Mass: " + unknown);
			list.add("Atmosphere Density: " + unknown);
			list.add("Distance From Star: " + unknown);
		}
		else {
			list.add("Unprogrammed");
		}
	}
}
