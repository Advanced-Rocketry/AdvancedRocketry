package zmaster587.advancedRocketry.item;

import com.mojang.realmsclient.gui.ChatFormatting;

import zmaster587.advancedRocketry.dimension.DimensionManager;
import zmaster587.advancedRocketry.dimension.DimensionProperties;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

public class ItemPlanetIdentificationChip extends ItemIdWithName {

	private static final String dimensionNameIdentifier = "DimensionName";
	private static final String dimensionIdIdentifier = "dimId";
	private static final String uuidIdentifier = "UUID";

	public ItemPlanetIdentificationChip() {
	}

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
			int dimId = stack.getTagCompound().getInteger(dimensionIdIdentifier);
			return dimId == 0 || DimensionManager.getInstance().isDimensionCreated(dimId);
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

		NBTTagCompound nbt;
		if(dimensionId == -1) {
			nbt = new NBTTagCompound();
			nbt.setInteger(dimensionIdIdentifier, dimensionId);
			return;
		}

		DimensionProperties properties = DimensionManager.getInstance().getDimensionProperties(dimensionId);
		if(properties == null) {
			erase(stack);
			return;
		}

		if(stack.hasTagCompound())
			nbt = stack.getTagCompound();
		else
			nbt = new NBTTagCompound();

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
	
	public Long getUUID(ItemStack stack) {
		if(stack.hasTagCompound())
			return stack.getTagCompound().getLong(uuidIdentifier);
		return null;
	}

	public void setUUID(ItemStack stack, long uuid) {
		NBTTagCompound nbt;
		if(stack.hasTagCompound())
			nbt = stack.getTagCompound();
		else
			nbt = new NBTTagCompound();

		nbt.setLong(uuidIdentifier,uuid);
		stack.setTagCompound(nbt);
	}
	
	@Override
	public void addInformation(ItemStack stack, net.minecraft.entity.player.EntityPlayer player, java.util.List list, boolean bool) {

		if(!stack.hasTagCompound()) {
			list.add("Unprogrammed");
		}
		else if(!hasValidDimension(stack)) {
			list.add(ChatFormatting.RED + "Programming Failed");
		}
		else {
			if(stack.getItemDamage()  == 0) {
				String unknown = ChatFormatting.YELLOW + "???";
				String dimName = stack.getTagCompound().getString(dimensionNameIdentifier);

				list.add("Planet Name: " + ChatFormatting.DARK_GREEN  + dimName);

				super.addInformation(stack, player, list, bool);

				//list.add("Mass: " + unknown);
				//list.add("Atmosphere Density: " + unknown);
				//list.add("Distance From Star: " + unknown);

			}
			else { //Space station
				list.add("Station Id: " + ChatFormatting.DARK_GREEN + stack.getTagCompound().getString(dimensionNameIdentifier));
			}
		}
	}
}
