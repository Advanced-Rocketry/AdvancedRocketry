package zmaster587.advancedRocketry.item;

import java.util.List;

import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.common.registry.LanguageRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import zmaster587.advancedRocketry.entity.EntityLaserNode;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.IIcon;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;

public class ItemIngredient extends Item {
	
	private int numIngots;
	
	public ItemIngredient(int num) {
		super();
		this.setCreativeTab(CreativeTabs.tabTransport);
		numIngots = num;
		setHasSubtypes(true);
		setMaxDamage(0);
	}

	private IIcon[] icons;

	@SuppressWarnings({"rawtypes", "unchecked"})
	@Override
	@SideOnly(Side.CLIENT)
	public void getSubItems(Item par1, CreativeTabs par2CreativeTabs, List itemList) {
		for (int i = 0; i < numIngots; i++) {
				itemList.add(new ItemStack(this, 1, i));
		}
	}
	
	@Override
	public String getUnlocalizedName(ItemStack itemstack) {
		return (new StringBuilder()).append(super.getUnlocalizedName()).append(".").append(itemstack.getItemDamage()).toString();
	}
	
	@Override
	public IIcon getIconFromDamage(int i) {
		return i < icons.length ? icons[i] : null;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerIcons(IIconRegister par1IconRegister) {
		icons = new IIcon[numIngots];
		for (int i = 0; i < numIngots; i++) {
			icons[i] = par1IconRegister.registerIcon("advancedrocketry:" + getUnlocalizedName().replaceFirst("item\\.", "") + i);
		}
	}

	public void registerItemStacks() {
		for(int i = 0; i < numIngots; i++) {
			ItemStack stack = new ItemStack(this, 1, i);
			GameRegistry.registerCustomItemStack(getUnlocalizedName(stack), stack);
		}
	}
}
