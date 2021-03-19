package zmaster587.advancedRocketry.item;

import com.mojang.realmsclient.gui.ChatFormatting;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import zmaster587.advancedRocketry.api.SatelliteRegistry;
import zmaster587.advancedRocketry.api.satellite.SatelliteBase;
import zmaster587.advancedRocketry.api.satellite.SatelliteProperties;
import zmaster587.libVulpes.LibVulpes;
import zmaster587.libVulpes.util.EmbeddedInventory;
import zmaster587.libVulpes.util.ZUtils;

import java.util.List;

public class ItemSatellite extends ItemIdWithName {

	@Deprecated
	public SatelliteProperties getSatellite(ItemStack stack) {

		//return getSatelliteProperties(stack);
		if(stack.hasTagCompound()) {
			NBTTagCompound nbt = stack.getTagCompound();

			//TODO: check
			SatelliteProperties satellite = new SatelliteProperties();
			satellite.readFromNBT(nbt);

			return satellite;
		}
		return null;
	}

	public EmbeddedInventory readInvFromNBT(ItemStack stackIn) {
		EmbeddedInventory inv = new EmbeddedInventory(7);
		if(!stackIn.hasTagCompound() || !stackIn.getTagCompound().hasKey("inv"))
			return inv;

		inv.readFromNBT(stackIn.getTagCompound().getCompoundTag("inv"));
		return inv;
	}

	public void writeInvToNBT(ItemStack stackIn, EmbeddedInventory inv) {
		NBTTagCompound nbt = new NBTTagCompound();
		if(!stackIn.hasTagCompound())
			stackIn.setTagCompound(nbt);
		else
			nbt = stackIn.getTagCompound();

		NBTTagCompound tag = new NBTTagCompound(); 
		inv.writeToNBT(tag);
		nbt.setTag("inv", tag);
	}

	public void setSatellite(ItemStack stack, SatelliteProperties properties) {

		SatelliteBase testSatellite = SatelliteRegistry.getSatellite(properties.getSatelliteType());
		if(testSatellite != null) {
			//Check to see if we have some NBT already, if so, add to it
			NBTTagCompound nbt;
			if(stack.hasTagCompound())
				nbt = stack.getTagCompound();
			else
				nbt = new NBTTagCompound();

			//Stick the properties into the NBT of the stack
			properties.writeToNBT(nbt);
			stack.setTagCompound(nbt);

			setName(stack, testSatellite.getName());
		}
		else
			stack.setTagCompound(null);

	}


	@Override
	public void addInformation(ItemStack stack, World player, List list,
            ITooltipFlag bool) {

		SatelliteProperties properties = getSatellite(stack);

		if(properties != null) {
			int dataStorage, powerGeneration, powerStorage;

			list.add(getName(stack));
			list.add("ID: " + properties.getId());

			if(SatelliteProperties.Property.BATTERY.isOfType(properties.getPropertyFlag())) {
				if( (powerStorage = properties.getPowerStorage()) > 0)
					list.add(LibVulpes.proxy.getLocalizedString("msg.itemsatellite.pwr") + powerStorage);
				else
					list.add(ChatFormatting.RED + LibVulpes.proxy.getLocalizedString("msg.itemsatellite.nopwr"));
			}

			if(SatelliteProperties.Property.POWER_GEN.isOfType(properties.getPropertyFlag())) {
				if( ( powerGeneration=properties.getPowerGeneration() ) > 0)
					list.add(LibVulpes.proxy.getLocalizedString("msg.itemsatellite.pwrgen") + powerGeneration);
				else
					list.add(ChatFormatting.RED + LibVulpes.proxy.getLocalizedString("msg.itemsatellite.nopwrgen"));
			}

			if(SatelliteProperties.Property.DATA.isOfType(properties.getPropertyFlag())) {
				if( (dataStorage = properties.getMaxDataStorage()) > 0 ) 
					list.add(LibVulpes.proxy.getLocalizedString("msg.itemsatellite.data") + ZUtils.formatNumber(dataStorage));
				else
					list.add(ChatFormatting.YELLOW + LibVulpes.proxy.getLocalizedString("msg.itemsatellite.nodata"));
			}
		}
		else {
			list.add(ChatFormatting.RED + LibVulpes.proxy.getLocalizedString("msg.itemsatellite.empty"));
		}
	}
}
