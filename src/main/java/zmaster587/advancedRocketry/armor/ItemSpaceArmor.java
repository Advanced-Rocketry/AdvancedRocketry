package zmaster587.advancedRocketry.armor;

import java.util.LinkedList;
import java.util.List;

import com.mojang.realmsclient.gui.ChatFormatting;

import zmaster587.advancedRocketry.achievements.ARAchivements;
import zmaster587.advancedRocketry.api.AdvancedRocketryItems;
import zmaster587.advancedRocketry.api.IAtmosphere;
import zmaster587.advancedRocketry.api.armor.IFillableArmor;
import zmaster587.advancedRocketry.api.armor.IProtectiveArmor;
import zmaster587.advancedRocketry.api.capability.CapabilitySpaceArmor;
import zmaster587.advancedRocketry.atmosphere.AtmosphereType;
import zmaster587.advancedRocketry.client.render.armor.RenderJetPack;
import zmaster587.libVulpes.LibVulpes;
import zmaster587.libVulpes.api.IArmorComponent;
import zmaster587.libVulpes.api.IJetPack;
import zmaster587.libVulpes.api.IModularArmor;
import zmaster587.libVulpes.util.EmbeddedInventory;
import zmaster587.libVulpes.util.IconResource;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;
import net.minecraftforge.common.ISpecialArmor;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.Constants.NBT;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * Space Armor
 * Any class that extends this will gain the ability to store oxygen and will protect players from the vacuum atmosphere type
 *
 */

public class ItemSpaceArmor extends ItemArmor implements ISpecialArmor, ICapabilityProvider, IProtectiveArmor, IModularArmor {

	private final static String componentNBTName = "componentName";
	private int numModules;
	public ItemSpaceArmor(ArmorMaterial material, EntityEquipmentSlot component, int numModules) {
		super(material, 0, component);
		this.numModules = numModules;
	}

	@Override
	public boolean canBeExternallyModified(ItemStack armor, int slot) {
		return true;
	}

	@Override
	public void addInformation(ItemStack stack, EntityPlayer p_77624_2_,
			List list, boolean p_77624_4_) {
		super.addInformation(stack, p_77624_2_, list, p_77624_4_);

		list.add(LibVulpes.proxy.getLocalizedString("msg.modules"));

		for(ItemStack componentStack : getComponents(stack)) {
			list.add(ChatFormatting.DARK_GRAY + componentStack.getDisplayName());
		}
	}

	@Override
	@SideOnly(Side.CLIENT)
	public ModelBiped getArmorModel(EntityLivingBase entityLiving,
			ItemStack itemStack, EntityEquipmentSlot armorSlot,
			ModelBiped _default) {

		if(armorSlot == EntityEquipmentSlot.CHEST) {
			for(ItemStack stack : getComponents(itemStack)) {
				if(stack.getItem() instanceof IJetPack)
					return new RenderJetPack(_default);
			}
		}
		return super.getArmorModel(entityLiving, itemStack, armorSlot, _default);
	}

	public int getColor(ItemStack stack)
	{

		NBTTagCompound nbttagcompound = stack.getTagCompound();

		if (nbttagcompound != null)
		{
			NBTTagCompound nbttagcompound1 = nbttagcompound.getCompoundTag("display");

			if (nbttagcompound1 != null && nbttagcompound1.hasKey("color", 3))
			{
				return nbttagcompound1.getInteger("color");
			}
		}

		return 0xFFFFFF;

	}
	@Override
	public ArmorProperties getProperties(EntityLivingBase player,
			ItemStack armor, DamageSource source, double damage, int slot) {
		if(!source.isUnblockable())
			return new ArmorProperties(0, 5, 1);
		return new ArmorProperties(0, 0, 0);
	}

	private EmbeddedInventory loadEmbeddedInventory(ItemStack stack) {
		if(stack.hasTagCompound()) {
			EmbeddedInventory inv = new EmbeddedInventory(numModules);
			inv.readFromNBT(stack.getTagCompound());
			return inv;
		}
		return new EmbeddedInventory(numModules);
	}

	protected void saveEmbeddedInventory(ItemStack stack, EmbeddedInventory inv) {
		if(stack.hasTagCompound()) {
			inv.writeToNBT(stack.getTagCompound());
		}
		else {
			NBTTagCompound nbt = new NBTTagCompound();
			inv.writeToNBT(nbt);
			stack.setTagCompound(nbt);
		}
	}

	@Override
	public void onArmorTick(World world, EntityPlayer player,
			ItemStack armor) {
		super.onArmorTick(world, player, armor);

		if(armor.hasTagCompound()) {

			//Some upgrades modify player capabilities

			EmbeddedInventory inv = loadEmbeddedInventory(armor);
			for(int i = 0; i < inv.getSizeInventory(); i++ ) {
				ItemStack stack = inv.getStackInSlot(i);
				if(!stack.isEmpty()) {
					IArmorComponent component = (IArmorComponent)stack.getItem();
					component.onTick(world, player, armor, inv, stack);
				}
			}

			saveEmbeddedInventory(armor, inv);
		}

		ItemStack feet = player.getItemStackFromSlot(EntityEquipmentSlot.FEET);
		ItemStack leg = player.getItemStackFromSlot(EntityEquipmentSlot.LEGS);
		ItemStack chest = player.getItemStackFromSlot(EntityEquipmentSlot.CHEST);
		ItemStack helm = player.getItemStackFromSlot(EntityEquipmentSlot.HEAD);
		if(!feet.isEmpty() && feet.getItem() instanceof ItemSpaceArmor && !leg.isEmpty() && leg.getItem() instanceof ItemSpaceArmor && !chest.isEmpty() && chest.getItem() instanceof ItemSpaceArmor && !helm.isEmpty() && helm.getItem() instanceof ItemSpaceArmor)
			player.addStat(ARAchivements.suitedUp);
	}

	@Override
	public String getArmorTexture(ItemStack stack, Entity entity, EntityEquipmentSlot slot, String type) {

		if(type != null) {
			if(stack.getItem() == AdvancedRocketryItems.itemSpaceSuit_Leggings)
				return "advancedRocketry:textures/armor/spaceSuit_layer1_overlay.png";//super.getArmorTexture(stack, entity, slot, type);
			return "advancedRocketry:textures/armor/spaceSuit_layer2_overlay.png";
		}

		if(stack.getItem() == AdvancedRocketryItems.itemSpaceSuit_Leggings)
			return "advancedRocketry:textures/armor/spaceSuit_layer1.png";//super.getArmorTexture(stack, entity, slot, type);
		return "advancedRocketry:textures/armor/spaceSuit_layer2.png";
	}

	@Override
	public int getArmorDisplay(EntityPlayer player, ItemStack armor, int slot) {
		return 1;
	}

	@Override
	public void damageArmor(EntityLivingBase entity, ItemStack armor,
			DamageSource source, int damage, int slot) {
		// TODO Handle armor damage

		if(armor.hasTagCompound()) {

			EmbeddedInventory inv = loadEmbeddedInventory(armor);
			for(int i = 0; i < inv.getSizeInventory(); i++ ) {
				ItemStack stack = inv.getStackInSlot(i);
				if(!stack.isEmpty()) {
					IArmorComponent component = (IArmorComponent)stack.getItem();
					component.onArmorDamaged(entity, armor, stack, source, damage);
				}
			}

			saveEmbeddedInventory(armor, inv);
		}
	}

	@Override
	public void addArmorComponent(World world, ItemStack armor, ItemStack component, int slot) {

		EmbeddedInventory inv = loadEmbeddedInventory(armor);

		if(((IArmorComponent)component.getItem()).onComponentAdded(world, armor)) {
			inv.setInventorySlotContents(slot, component);

			saveEmbeddedInventory(armor, inv);
		}
	}



	public ItemStack removeComponent(World world, ItemStack armor, int index) {
		NBTTagCompound nbt;
		NBTTagList componentList;

		if(armor.hasTagCompound()) {
			nbt = armor.getTagCompound();
			componentList = nbt.getTagList(componentNBTName, NBT.TAG_COMPOUND);
		}
		else {
			return ItemStack.EMPTY;
		}

		EmbeddedInventory inv = loadEmbeddedInventory(armor);
		ItemStack stack = inv.getStackInSlot(index);
		inv.setInventorySlotContents(index, ItemStack.EMPTY);

		if(!stack.isEmpty()) {
			IArmorComponent component = (IArmorComponent) stack.getItem();
			component.onComponentRemoved(world, armor);
			saveEmbeddedInventory(armor, inv);
		}



		return stack;
	}

	public List<ItemStack> getComponents(ItemStack armor) {

		List<ItemStack> list = new LinkedList<ItemStack>();
		NBTTagCompound nbt;
		NBTTagList componentList;

		if(armor.hasTagCompound()) {
			EmbeddedInventory inv = loadEmbeddedInventory(armor);

			for(int i = 0; i < inv.getSizeInventory(); i++) {
				if(!inv.getStackInSlot(i).isEmpty())
					list.add(inv.getStackInSlot(i));
			}
		}

		return list;
	}

	@Override
	public boolean protectsFromSubstance(IAtmosphere atmosphere, ItemStack stack, boolean commitProtection) {
		return (atmosphere == AtmosphereType.HIGHPRESSURE || atmosphere == AtmosphereType.VACUUM || atmosphere == AtmosphereType.LOWOXYGEN) && (this != AdvancedRocketryItems.itemSpaceSuit_Chest || (this == AdvancedRocketryItems.itemSpaceSuit_Chest && 

				(commitProtection && ((IFillableArmor)AdvancedRocketryItems.itemSpaceSuit_Chest).decrementAir(stack, 1) > 0)) || (!commitProtection && ((IFillableArmor)AdvancedRocketryItems.itemSpaceSuit_Chest).getAirRemaining(stack) > 0));
	}

	@Override
	public int getNumSlots(ItemStack stack) {
		return loadEmbeddedInventory(stack).getSizeInventory();
	}

	@Override
	public ItemStack getComponentInSlot(ItemStack stack, int slot) {
		return loadEmbeddedInventory(stack).getStackInSlot(slot);
	}

	@Override
	public IInventory loadModuleInventory(ItemStack stack) {
		return loadEmbeddedInventory(stack);
	}

	@Override
	public void saveModuleInventory(ItemStack stack, IInventory inv) {
		saveEmbeddedInventory(stack, (EmbeddedInventory)inv);
	}

	@Override
	public boolean hasCapability(Capability<?> capability, EnumFacing facing) {

		return capability == CapabilitySpaceArmor.PROTECTIVEARMOR;
	}

	@Override
	public <T> T getCapability(Capability<T> capability, EnumFacing facing) {
		if(capability == CapabilitySpaceArmor.PROTECTIVEARMOR)
			return (T) this;
		return null;
	}

	public boolean isItemValidForSlot(ItemStack stack, int slot) {
		return true;	
	}

	@Override
	public IconResource getResourceForSlot(int slot) {
		return null;
	}
}
