package zmaster587.advancedRocketry.inventory.slot;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class SlotArmor extends Slot {
	
	EntityPlayer player;
	int armorType;
	public SlotArmor(IInventory inv, int slot, int x, int y, EntityPlayer player, int armorType) {
		super(inv, slot, x, y);
		this.player = player;
		this.armorType = armorType;
		// TODO Auto-generated constructor stub
	}
	
	/**
	 * Returns the maximum stack size for a given slot (usually the same as getInventoryStackLimit(), but 1
	 * in the case of armor slots)
	 */
	public int getSlotStackLimit()
	{
		return 1;
	}
	/**
	 * Check if the stack is a valid item for this slot. Always true beside for the armor slots.
	 */
	public boolean isItemValid(ItemStack p_75214_1_)
	{
		if (p_75214_1_ == null) return false;
		return p_75214_1_.getItem().isValidArmor(p_75214_1_, armorType, player);
	}
	/**
	 * Returns the icon index on items.png that is used as background image of the slot.
	 */
	@SideOnly(Side.CLIENT)
	public IIcon getBackgroundIconIndex()
	{
		return ItemArmor.func_94602_b(armorType);
	}
}
