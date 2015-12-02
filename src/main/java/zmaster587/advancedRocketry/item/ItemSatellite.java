package zmaster587.advancedRocketry.item;

import java.util.List;

import zmaster587.advancedRocketry.api.SatelliteRegistry;
import zmaster587.advancedRocketry.api.satellite.SatelliteBase;
import zmaster587.advancedRocketry.api.satellite.SatelliteProperties;
import zmaster587.libVulpes.util.ZUtils;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumChatFormatting;

public class ItemSatellite extends ItemIdWithName {

	public SatelliteProperties getSatellite(ItemStack stack) {

		if(stack.hasTagCompound()) {
			NBTTagCompound nbt = stack.getTagCompound();

			//TODO: check
			SatelliteProperties satellite = new SatelliteProperties();
			satellite.readFromNBT(nbt);

			return satellite;
		}
		return null;
	}

	public void setSatellite(ItemStack stack, SatelliteProperties satellite) {

		SatelliteBase satellite2 = SatelliteRegistry.getSatallite(satellite.getSatelliteType());
		if(satellite2 != null) {
			NBTTagCompound nbt = new NBTTagCompound();

			satellite.writeToNBT(nbt);
			stack.setTagCompound(nbt);

			setName(stack, satellite2.getName());
		}
		else
			stack.setTagCompound(null);

	}


	@Override
	public void addInformation(ItemStack stack, EntityPlayer player,
			List list, boolean bool) {

		SatelliteProperties properties = getSatellite(stack);

		if(properties != null) {
			int dataStorage, powerGeneration, powerStorage;

			list.add(getName(stack));
			list.add("ID: " + properties.getId());

			if( (powerStorage = properties.getPowerStorage()) > 0)
				list.add("Power Storage: " + powerStorage);
			else
				list.add(EnumChatFormatting.RED + "No Power Storage");

			if( ( powerGeneration=properties.getPowerGeneration() ) > 0)
				list.add("Power Generation: " + powerGeneration);
			else
				list.add(EnumChatFormatting.RED + "No Power Generation!");

			if( (dataStorage = properties.getMaxDataStorage()) > 0 ) 
				list.add("Data Storage: " + ZUtils.formatNumber(dataStorage));
			else
				list.add(EnumChatFormatting.YELLOW + "No Data Storage!");

		}
	}
}
