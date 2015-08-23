package zmaster587.advancedRocketry.item;

import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import zmaster587.advancedRocketry.AdvancedRocketry;
import zmaster587.advancedRocketry.api.satellite.SatelliteBase;
import zmaster587.advancedRocketry.satellite.OreMappingSatellite;

public class ItemOreScanner extends Item {


	@Override
	public void addInformation(ItemStack stack, EntityPlayer player,
			List list, boolean arg5) {
		if(!stack.hasTagCompound() || !stack.getTagCompound().hasKey("connected") || (stack.getTagCompound().getBoolean("connected")))
			list.add("Not Connected");
		else
			list.add("Connected");

			super.addInformation(stack, player, list, arg5);
	}

	@Override
	public ItemStack onItemRightClick(ItemStack stack, World world,
			EntityPlayer player) {

		if(!player.worldObj.isRemote)
			player.openGui(AdvancedRocketry.instance, 100, world, (int)player.posX, (int)player.posY, (int)player.posZ);

		return super.onItemRightClick(stack, world, player);
	}

	@Override
	public boolean onItemUse(ItemStack stack, EntityPlayer player,World world, int x, int y, int z,int p_77648_7_, float p_77648_8_, float p_77648_9_, float p_77648_10_) {

		if(!player.worldObj.isRemote)
			player.openGui(AdvancedRocketry.instance, 100, world, x, y, z);


		return super.onItemUse(stack, player, world, x, y,z, p_77648_7_, p_77648_8_, p_77648_9_, p_77648_10_);
	}


	public void interactSatellite(SatelliteBase satellite,EntityPlayer player, World world, int x, int y, int z) {
		satellite.performAction(player, world,x,y,z);
	}

}
