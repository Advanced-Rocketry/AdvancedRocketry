package zmaster587.advancedRocketry.armor;

import net.minecraft.client.renderer.entity.model.BipedModel;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.ArmorMaterial;
import net.minecraft.item.IDyeableArmorItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.util.Direction;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.common.util.Constants.NBT;
import zmaster587.advancedRocketry.api.AdvancedRocketryItems;
import zmaster587.advancedRocketry.api.IAtmosphere;
import zmaster587.advancedRocketry.api.armor.IProtectiveArmor;
import zmaster587.advancedRocketry.api.capability.CapabilitySpaceArmor;
import zmaster587.advancedRocketry.atmosphere.AtmosphereType;
import zmaster587.advancedRocketry.client.render.armor.RenderJetPack;
import zmaster587.libVulpes.api.IArmorComponent;
import zmaster587.libVulpes.api.IJetPack;
import zmaster587.libVulpes.api.IModularArmor;
import zmaster587.libVulpes.util.EmbeddedInventory;
import zmaster587.libVulpes.util.IconResource;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Consumer;
/**
 * Space Armor
 * Any class that extends this will gain the ability to store oxygen and will protect players from the vacuum atmosphere type
 *
 */

public class ItemSpaceArmor extends ArmorItem implements ICapabilityProvider, IProtectiveArmor, IModularArmor, IDyeableArmorItem {

	private final static String componentNBTName = "componentName";
	private int numModules;
	public ItemSpaceArmor(Item.Properties props, ArmorMaterial material, EquipmentSlotType component, int numModules) {
		super(material, component, props);
		this.numModules = numModules;
	}

	@Override
	public boolean canBeExternallyModified(@Nonnull ItemStack armor, int slot) {
		return true;
	}

	@Override
	@ParametersAreNonnullByDefault
	public void addInformation(@Nonnull ItemStack stack, @Nullable World world, List<ITextComponent> list, ITooltipFlag p_77624_4_) {
		super.addInformation(stack, world, list, p_77624_4_);

		list.add(new TranslationTextComponent("msg.modules"));

		for(ItemStack componentStack : getComponents(stack)) {
			list.add(componentStack.getDisplayName());
		}
	}

	@Override
	public boolean isDamageable() {
		return false;
	}
	@Override
	public void setDamage(@Nonnull ItemStack stack, int damage) {
		//Dummy out
	}
	
	
	@Override
	@OnlyIn(value=Dist.CLIENT)
	public <A extends BipedModel<?>> A getArmorModel(LivingEntity entityLiving, ItemStack itemStack,
			EquipmentSlotType armorSlot, A _default) {

		if(armorSlot == EquipmentSlotType.CHEST) {
			for(ItemStack stack : getComponents(itemStack)) {
				if(stack.getItem() instanceof IJetPack)
					return (A) new RenderJetPack(_default);
			}
		}
		return super.getArmorModel(entityLiving, itemStack, armorSlot, _default);
	}

	public int getColor(@Nonnull ItemStack stack)
	{

		CompoundNBT nbttagcompound = stack.getTag();

		if (nbttagcompound != null)
		{
			CompoundNBT nbttagcompound1 = nbttagcompound.getCompound("display");

			if (nbttagcompound1.contains("color", 3))
			{
				return nbttagcompound1.getInt("color");
			}
		}

		return 0xFFFFFF;

	}
	
	/*@Override
	public ArmorProperties getProperties(LivingEntity player,
			ItemStack armor, DamageSource source, double damage, int slot) {
		if(!source.isUnblockable())
			return new ArmorProperties(0, 0, 1);
		return new ArmorProperties(0, 0, 0);
	}*/

	private EmbeddedInventory loadEmbeddedInventory(ItemStack stack) {
		if(stack.hasTag()) {
			EmbeddedInventory inv = new EmbeddedInventory(numModules);
			inv.readFromNBT(stack.getTag());
			return inv;
		}
		return new EmbeddedInventory(numModules);
	}

	protected void saveEmbeddedInventory(@Nonnull ItemStack stack, EmbeddedInventory inv) {
		if(stack.hasTag()) {
			inv.write(stack.getTag());
		}
		else {
			CompoundNBT nbt = new CompoundNBT();
			inv.write(nbt);
			stack.setTag(nbt);
		}
	}
	
	@Override
	public void onArmorTick(@Nonnull  ItemStack armor, World world, PlayerEntity player) {
		super.onArmorTick(armor, world, player);

		if(armor.hasTag()) {

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
	}

	@Override
	public String getArmorTexture(@Nonnull  ItemStack stack, Entity entity, EquipmentSlotType slot, String type) {

		if(type != null) {
			if(stack.getItem() == AdvancedRocketryItems.itemSpaceSuitLeggings)
				return "advancedrocketry:textures/armor/spacesuit_layer1_overlay.png";//super.getArmorTexture(stack, entity, slot, type);
			return "advancedrocketry:textures/armor/spacesuit_layer2_overlay.png";
		}

		if(stack.getItem() == AdvancedRocketryItems.itemSpaceSuitLeggings)
			return "advancedrocketry:textures/armor/spacesuit_layer1.png";//super.getArmorTexture(stack, entity, slot, type);
		return "advancedrocketry:textures/armor/spacesuit_layer2.png";
	}

	@Override
	public <T extends LivingEntity> int damageItem(ItemStack stack, int amount, T entity, Consumer<T> onBroken) {
		// TODO Auto-generated method stub
		return super.damageItem(stack, amount, entity, onBroken);
	}
	
	/*@Override
	public void damageArmor(LivingEntity entity, ItemStack armor,
			DamageSource source, int damage, int slot) {
		// TODO Handle armor damage

		if(armor.hasTag()) {

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
	}*/

	@Override
	public void addArmorComponent(World world, @Nonnull ItemStack armor, @Nonnull ItemStack component, int slot) {

		EmbeddedInventory inv = loadEmbeddedInventory(armor);

		if(((IArmorComponent)component.getItem()).onComponentAdded(world, armor)) {
			inv.setInventorySlotContents(slot, component);

			saveEmbeddedInventory(armor, inv);
		}
	}



	@Nonnull
	public ItemStack removeComponent(World world, ItemStack armor, int index) {
		CompoundNBT nbt;
		ListNBT componentList;

		if(armor.hasTag()) {
			nbt = armor.getTag();
			componentList = nbt.getList(componentNBTName, NBT.TAG_COMPOUND);
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

	public List<ItemStack> getComponents(@Nonnull ItemStack armor) {

		List<ItemStack> list = new LinkedList<>();
		CompoundNBT nbt;
		ListNBT componentList;

		if(armor.hasTag()) {
			EmbeddedInventory inv = loadEmbeddedInventory(armor);

			for(int i = 0; i < inv.getSizeInventory(); i++) {
				if(!inv.getStackInSlot(i).isEmpty())
					list.add(inv.getStackInSlot(i));
			}
		}

		return list;
	}

	@Override
	public boolean protectsFromSubstance(IAtmosphere atmosphere, @Nonnull ItemStack stack, boolean commitProtection) {
		return (atmosphere == AtmosphereType.SUPERHIGHPRESSURE || atmosphere == AtmosphereType.HIGHPRESSURE || atmosphere == AtmosphereType.VACUUM || atmosphere == AtmosphereType.VERYHOT || atmosphere == AtmosphereType.SUPERHEATED || atmosphere == AtmosphereType.LOWOXYGEN || atmosphere == AtmosphereType.SUPERHIGHPRESSURENOO2 || atmosphere == AtmosphereType.HIGHPRESSURENOO2 || atmosphere == AtmosphereType.VERYHOTNOO2|| atmosphere == AtmosphereType.SUPERHEATEDNOO2  || atmosphere == AtmosphereType.NOO2);
	}

	@Override
	public int getNumSlots(@Nonnull ItemStack stack) {
		return loadEmbeddedInventory(stack).getSizeInventory();
	}

	@Override
	@Nonnull
	public ItemStack getComponentInSlot(@Nonnull ItemStack stack, int slot) {
		return loadEmbeddedInventory(stack).getStackInSlot(slot);
	}

	@Override
	public IInventory loadModuleInventory(@Nonnull ItemStack stack) {
		return loadEmbeddedInventory(stack);
	}

	@Override
	public void saveModuleInventory(@Nonnull ItemStack stack, IInventory inv) {
		saveEmbeddedInventory(stack, (EmbeddedInventory)inv);
	}

	@Nonnull
	@Override
	public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> capability, Direction facing) {
		if(capability == CapabilitySpaceArmor.PROTECTIVEARMOR)
			return LazyOptional.of(() -> this).cast();
		return LazyOptional.empty();
	}

	public boolean isItemValidForSlot(@Nonnull ItemStack stack, int slot) {
		return true;	
	}

	@Override
	public IconResource getResourceForSlot(int slot) {
		return null;
	}
}
