package zmaster587.advancedRocketry.armor;

import java.util.LinkedList;
import java.util.List;

import cpw.mods.fml.common.ObfuscationReflectionHelper;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import zmaster587.advancedRocketry.achievements.ARAchivements;
import zmaster587.advancedRocketry.api.AdvancedRocketryFluids;
import zmaster587.advancedRocketry.api.AdvancedRocketryItems;
import zmaster587.advancedRocketry.api.Configuration;
import zmaster587.advancedRocketry.api.IAtmosphere;
import zmaster587.advancedRocketry.api.armor.IFillableArmor;
import zmaster587.advancedRocketry.api.armor.IProtectiveArmor;
import zmaster587.advancedRocketry.atmosphere.AtmosphereType;
import zmaster587.advancedRocketry.client.render.armor.RenderJetPack;
import zmaster587.advancedRocketry.inventory.TextureResources;
import zmaster587.libVulpes.api.IArmorComponent;
import zmaster587.libVulpes.api.IModularArmor;
import zmaster587.libVulpes.util.EmbeddedInventory;
import zmaster587.libVulpes.util.IconResource;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;
import net.minecraftforge.common.ISpecialArmor;
import net.minecraftforge.common.util.Constants.NBT;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.IFluidContainerItem;
import net.minecraftforge.fluids.IFluidHandler;
/**
 * Space Armor
 * Any class that extends this will gain the ability to store oxygen and will protect players from the vacuum atmosphere type
 *
 */
public class ItemSpaceArmor extends ItemArmor implements ISpecialArmor, IProtectiveArmor, IModularArmor {

	private final static String componentNBTName = "componentName";
	private IIcon overlayIcon;
	private int numModules;

	public ItemSpaceArmor(ArmorMaterial material, int component, int numModules) {
		super(material, 0, component);
		this.numModules = numModules;
	}

	@SideOnly(Side.CLIENT)
	public void registerIcons(IIconRegister p_94581_1_)
	{
		super.registerIcons(p_94581_1_);
		this.overlayIcon = p_94581_1_.registerIcon(getIconString() + "_overlay");
	}

	@Override
	public boolean canBeExternallyModified(ItemStack armor, int slot) {
		return true;
	}
	
	/**
	 * Return the color for the specified armor ItemStack.
	 */
	@Override
	public int getColor(ItemStack p_82814_1_)
	{
		if (this.getArmorMaterial() != ItemArmor.ArmorMaterial.CLOTH)
		{
			return -1;
		}
		else
		{
			NBTTagCompound nbttagcompound = p_82814_1_.getTagCompound();

			if (nbttagcompound == null)
			{
				return 0xFFFFFF;
			}
			else
			{
				NBTTagCompound nbttagcompound1 = nbttagcompound.getCompoundTag("display");
				return nbttagcompound1 == null ? 10511680 : (nbttagcompound1.hasKey("color", 3) ? nbttagcompound1.getInteger("color") : 10511680);
			}
		}
	}

	/**
	 * Gets an icon index based on an item's damage value and the given render pass
	 */
	@SideOnly(Side.CLIENT)
	public IIcon getIconFromDamageForRenderPass(int p_77618_1_, int p_77618_2_)
	{
		return p_77618_2_ == 1 ? this.overlayIcon : super.getIconFromDamageForRenderPass(p_77618_1_, p_77618_2_);
	}

	@Override
	public void addInformation(ItemStack stack, EntityPlayer p_77624_2_,
			List list, boolean p_77624_4_) {
		super.addInformation(stack, p_77624_2_, list, p_77624_4_);

		list.add("Modules:");

		for(ItemStack componentStack : getComponents(stack)) {
			list.add(EnumChatFormatting.DARK_GRAY + componentStack.getDisplayName());
		}
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

	@Override
	public ModelBiped getArmorModel(EntityLivingBase entityLiving,
			ItemStack itemStack, int armorSlot) {
		//if(armorSlot == 1)
		//return new RenderJetPack();

		return super.getArmorModel(entityLiving, itemStack, armorSlot);
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
				if(stack != null) {
					IArmorComponent component = (IArmorComponent)stack.getItem();
					component.onTick(world, player, armor, inv, stack);
				}
			}

			saveEmbeddedInventory(armor, inv);
		}
		ItemStack feet = player.getCurrentArmor(0);
		ItemStack leg = player.getCurrentArmor(1);
		ItemStack chest = player.getCurrentArmor(2);
		ItemStack helm = player.getCurrentArmor(3);
		if(feet != null && feet.getItem() instanceof ItemSpaceArmor && leg != null && leg.getItem() instanceof ItemSpaceArmor && chest != null && chest.getItem() instanceof ItemSpaceArmor && helm != null && helm.getItem() instanceof ItemSpaceArmor)
			player.triggerAchievement(ARAchivements.suitedUp);

	}

	@Override
	public String getArmorTexture(ItemStack stack, Entity entity,
			int slot, String type) {

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
				if(stack != null) {
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
			return null;
		}

		EmbeddedInventory inv = loadEmbeddedInventory(armor);
		ItemStack stack = inv.getStackInSlot(index);
		inv.setInventorySlotContents(index, null);

		if(stack != null) {
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
				if(inv.getStackInSlot(i) != null)
					list.add(inv.getStackInSlot(i));
			}
		}

		return list;
	}

	@Override
	public boolean protectsFromSubstance(IAtmosphere atmosphere, ItemStack stack, boolean commitProtection) {

		return (atmosphere == AtmosphereType.VACUUM || atmosphere == AtmosphereType.LOWOXYGEN) && (this != AdvancedRocketryItems.itemSpaceSuit_Chest || (this == AdvancedRocketryItems.itemSpaceSuit_Chest && 
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
	public boolean isItemValidForSlot(ItemStack stack, int slot) {
		return true;	
	}

	@Override
	public IconResource getResourceForSlot(int slot) {
		return null;
	}
}
