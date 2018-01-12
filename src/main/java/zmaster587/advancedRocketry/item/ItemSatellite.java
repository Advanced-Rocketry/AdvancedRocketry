package zmaster587.advancedRocketry.item;

import java.util.List;

import com.mojang.realmsclient.gui.ChatFormatting;

import zmaster587.advancedRocketry.api.SatelliteRegistry;
import zmaster587.advancedRocketry.api.satellite.SatelliteBase;
import zmaster587.advancedRocketry.api.satellite.SatelliteProperties;
import zmaster587.libVulpes.LibVulpes;
import zmaster587.libVulpes.util.EmbeddedInventory;
import zmaster587.libVulpes.util.ZUtils;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

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

	public SatelliteProperties getSatelliteProperties(ItemStack stackIn) {
		if(stackIn.hasTagCompound()) {
			int powerStorage = 0, powerGeneration = 0, maxData = 0;
			EmbeddedInventory inv = readInvFromNBT(stackIn);

			if(inv.getStackInSlot(0).isEmpty())
				return null;
			
			String satType = SatelliteRegistry.getSatelliteProperty(inv.getStackInSlot(0)).getSatelliteType();
			SatelliteBase sat = SatelliteRegistry.getSatallite(satType);

			for(int i = 0; i < inv.getSizeInventory(); i++) {
				ItemStack stack = inv.getStackInSlot(i);
				if(!stack.isEmpty()) {
					SatelliteProperties properties = SatelliteRegistry.getSatelliteProperty(stack);

					if(!sat.acceptsItemInConstruction(stack))
						continue;

					powerStorage += properties.getPowerStorage();
					powerGeneration += properties.getPowerGeneration();
					maxData += properties.getMaxDataStorage();
				}
			}

			return new SatelliteProperties(powerGeneration, powerStorage, satType, maxData);
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

	public void setSatellite(ItemStack stack, SatelliteProperties satellite) {

		SatelliteBase satellite2 = SatelliteRegistry.getSatallite(satellite.getSatelliteType());
		if(satellite2 != null) {
				
			NBTTagCompound nbt;
			if(stack.hasTagCompound())
				nbt = stack.getTagCompound();
			else
				nbt = new NBTTagCompound();

			SatelliteProperties internalProps = getSatelliteProperties(stack);
			if(internalProps != null) {
				satellite.setMaxData(internalProps.getMaxDataStorage());
				satellite.setPowerGeneration(internalProps.getPowerGeneration());
				satellite.setPowerStorage(internalProps.getPowerGeneration());
			}
			
			satellite.writeToNBT(nbt);
			stack.setTagCompound(nbt);

			setName(stack, satellite2.getName());
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
					list.add(LibVulpes.proxy.getLocalizedString("msg.itemsatellite.pwr") + powerGeneration);
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
