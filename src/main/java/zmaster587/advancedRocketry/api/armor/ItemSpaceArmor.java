package zmaster587.advancedRocketry.api.armor;

import zmaster587.advancedRocketry.api.AdvancedRocketryItems;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.DamageSource;
import net.minecraftforge.common.ISpecialArmor;
/**
 * Space Armor
 * Any class that extends this will gain the ability to store oxygen and will protect players from the vacuum atmosphere type
 *
 */
public class ItemSpaceArmor extends ItemArmor implements ISpecialArmor {

	public ItemSpaceArmor(ArmorMaterial material, int component) {
		super(material, 0, component);
	}

	@Override
	public ArmorProperties getProperties(EntityLivingBase player,
			ItemStack armor, DamageSource source, double damage, int slot) {
		if(!source.isUnblockable())
			return new ArmorProperties(0, 5, 1);
		return new ArmorProperties(0, 0, 0);
	}

	@Override
	public String getArmorTexture(ItemStack stack, Entity entity, int slot,
			String type) {
		if(stack.getItem() == AdvancedRocketryItems.itemSpaceSuit_Leggings)
			return "advancedRocketry:textures/armor/spaceSuit_layer1.png";//super.getArmorTexture(stack, entity, slot, type);
		return "advancedRocketry:textures/armor/spaceSuit_layer2.png";
	}

	@Override
	public int getArmorDisplay(EntityPlayer player, ItemStack armor, int slot) {
		return 1;
	}

	@Override
	public void damageArmor(EntityLivingBase entity, ItemStack stack,
			DamageSource source, int damage, int slot) {
		// TODO Handle armor damage

	}
	
	/**
	 * gets the amount of air remaining in the suit.
	 * @param stack stack from which to get an amount of air
	 * @return the amount of air in the stack
	 */
	public int getAirRemaining(ItemStack stack) {
		if(stack.hasTagCompound()) {
			return stack.getTagCompound().getInteger("air");
		}
		else {
			NBTTagCompound nbt = new NBTTagCompound();
			nbt.setInteger("air", 0);
			stack.setTagCompound(nbt);
			return getMaxAir();
		}
	}
	
	/**
	 * Sets the amount of air remaining in the suit (WARNING: DOES NOT BOUNDS CHECK!)
	 * @param stack the stack to operate on
	 * @param amt amount of air to set the suit to
	 */
	public void setAirRemaining(ItemStack stack, int amt) {
		NBTTagCompound nbt;
		if(stack.hasTagCompound()) {
			nbt = stack.getTagCompound();
		}
		else {
			nbt = new NBTTagCompound();
		}
		nbt.setInteger("air", amt);
		stack.setTagCompound(nbt);
	}
	
	/**
	 * Decrements air in the suit by amt
	 * @param stack the item stack to operate on
	 * @param amt amount of air by which to decrement
	 * @return The amount of air extracted from the suit
	 */
	public int decrementAir(ItemStack stack, int amt) {
		NBTTagCompound nbt;
		if(stack.hasTagCompound()) {
			nbt = stack.getTagCompound();
		}
		else {
			nbt = new NBTTagCompound();
		}
		
		int prevAmt = nbt.getInteger("air");
		int newAmt = Math.max(prevAmt - amt,0);
		nbt.setInteger("air", newAmt);
		stack.setTagCompound(nbt);
		
		return prevAmt - newAmt;
	}
	
	/**
	 * Increments air in the suit by amt
	 * @param stack the item stack to operate on
	 * @param amt amount of air by which to decrement
	 * @return The amount of air inserted into the suit
	 */
	public int increment(ItemStack stack, int amt) {
		NBTTagCompound nbt;
		if(stack.hasTagCompound()) {
			nbt = stack.getTagCompound();
		}
		else {
			nbt = new NBTTagCompound();
		}
		
		int prevAmt = nbt.getInteger("air");
		int newAmt = Math.min(prevAmt + amt, getMaxAir());
		nbt.setInteger("air", newAmt);
		stack.setTagCompound(nbt);
		
		return newAmt - prevAmt;
	}
	
	/**
	 * @return the maximum amount of air allowed in this suit
	 */
	public static int getMaxAir() {
		return 36000; //30 minutes;
	}
}
