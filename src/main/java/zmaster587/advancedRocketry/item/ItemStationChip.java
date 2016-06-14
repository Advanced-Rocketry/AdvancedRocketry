package zmaster587.advancedRocketry.item;

import java.util.List;

import zmaster587.libVulpes.util.Vector3F;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumChatFormatting;

/**
 * MetaData corresponds to the id
 */
public class ItemStationChip extends ItemIdWithName {
	
	private static final String uuidIdentifier = "UUID";
	
	public ItemStationChip() {
		//setMaxDamage(Integer.MAX_VALUE);
		setHasSubtypes(true);
	}

	public void setTakeoffCoords(ItemStack stack, Vector3F<Float> pos) {
		setTakeoffCoords(stack, pos.x, pos.y, pos.z);
	}

	public void setTakeoffCoords(ItemStack stack, float x, float y, float z) {
		NBTTagCompound nbt;

		if(stack.hasTagCompound()) 
			nbt = stack.getTagCompound();
		else 
			nbt = new NBTTagCompound();

		nbt.setFloat("x", x);
		nbt.setFloat("y", y);
		nbt.setFloat("z", z);

		stack.setTagCompound(nbt);
	}

	/**
	 * @param stack
	 * @return Vector3F containing the takeoff coords or null if there is none
	 */
	public Vector3F<Float> getTakeoffCoords(ItemStack stack) {
		if(stack.hasTagCompound()) {
			NBTTagCompound nbt = stack.getTagCompound();
			if(nbt.hasKey("x")) {
				return new Vector3F<Float>(nbt.getFloat("x"), nbt.getFloat("y"),nbt.getFloat("z"));
			}
		}
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
	public void addInformation(ItemStack stack, EntityPlayer player, List list,
			boolean bool) {
		if(stack.getItemDamage() == 0)
			list.add(EnumChatFormatting.GRAY + "Unprogrammed");
		else {
			list.add(EnumChatFormatting.GREEN + "Station " + stack.getItemDamage());
			super.addInformation(stack, player, list, bool);
		}
	}
}
