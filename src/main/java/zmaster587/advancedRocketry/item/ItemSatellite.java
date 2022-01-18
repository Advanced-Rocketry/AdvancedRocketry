package zmaster587.advancedRocketry.item;

import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import zmaster587.advancedRocketry.api.SatelliteRegistry;
import zmaster587.advancedRocketry.api.satellite.SatelliteBase;
import zmaster587.advancedRocketry.api.satellite.SatelliteProperties;
import zmaster587.libVulpes.LibVulpes;
import zmaster587.libVulpes.util.EmbeddedInventory;
import zmaster587.libVulpes.util.ZUtils;

import javax.annotation.Nonnull;
import java.util.List;

public class ItemSatellite extends ItemIdWithName {

	public ItemSatellite(Properties properties) {
		super(properties);
	}

	@Deprecated
	public static SatelliteProperties getSatellite(ItemStack stack) {

		//return getSatelliteProperties(stack);
		if(stack.hasTag()) {
			CompoundNBT nbt = stack.getTag();

			//TODO: check
			SatelliteProperties satellite = new SatelliteProperties();
			satellite.readFromNBT(nbt);

			return satellite;
		}
		return null;
	}

	public SatelliteProperties getSatelliteProperties(ItemStack stackIn) {
		if(stackIn.hasTag()) {
			int powerStorage = 0, powerGeneration = 0, maxData = 0;
			EmbeddedInventory inv = readInvFromNBT(stackIn);

			if(inv.getStackInSlot(0).isEmpty())
				return null;
			
			String satType = SatelliteRegistry.getSatelliteProperty(inv.getStackInSlot(0)).getSatelliteType();
			SatelliteBase sat = SatelliteRegistry.getSatellite(satType);

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

	public EmbeddedInventory readInvFromNBT(@Nonnull ItemStack stackIn) {
		EmbeddedInventory inv = new EmbeddedInventory(7);
		if(!stackIn.hasTag() || !stackIn.getTag().contains("inv"))
			return inv;

		inv.readFromNBT(stackIn.getTag().getCompound("inv"));
		return inv;
	}

	public void writeInvToNBT(@Nonnull ItemStack stackIn, EmbeddedInventory inv) {
		CompoundNBT nbt = new CompoundNBT();
		if(!stackIn.hasTag())
			stackIn.setTag(nbt);
		else
			nbt = stackIn.getTag();

		CompoundNBT tag = new CompoundNBT(); 
		inv.write(tag);
		nbt.put("inv", tag);
	}

	public void setSatellite(@Nonnull ItemStack stack, SatelliteProperties properties) {

		SatelliteBase testSatellite = SatelliteRegistry.getSatellite(properties.getSatelliteType());
		if(testSatellite != null) {
				
			CompoundNBT nbt;
			if(stack.hasTag())
				nbt = stack.getTag();
			else
				nbt = new CompoundNBT();
			//rod the properties into the NBT of the stack
			properties.writeToNBT(nbt);
			stack.setTag(nbt);

			setName(stack, testSatellite.getName());
		}
		else
			stack.setTag(null);

	}


	@Override
	public void addInformation(@Nonnull ItemStack stack, World player, List<ITextComponent> list, ITooltipFlag bool) {

		SatelliteProperties properties = getSatellite(stack);

		if(properties != null) {
			int dataStorage, powerGeneration, powerStorage;

			list.add(new StringTextComponent(getName(stack)));
			list.add(new StringTextComponent("ID: " + properties.getId()));

			if(SatelliteProperties.Property.BATTERY.isOfType(properties.getPropertyFlag())) {
				if( (powerStorage = properties.getPowerStorage()) > 0)
					list.add(new StringTextComponent(LibVulpes.proxy.getLocalizedString("msg.itemsatellite.pwr") + powerStorage));
				else
					list.add(new StringTextComponent(TextFormatting.RED + LibVulpes.proxy.getLocalizedString("msg.itemsatellite.nopwr")));
			}

			if(SatelliteProperties.Property.POWER_GEN.isOfType(properties.getPropertyFlag())) {
				if( ( powerGeneration=properties.getPowerGeneration() ) > 0)
					list.add(new StringTextComponent(LibVulpes.proxy.getLocalizedString("msg.itemsatellite.pwrgen") + powerGeneration));
				else
					list.add(new StringTextComponent(TextFormatting.RED + LibVulpes.proxy.getLocalizedString("msg.itemsatellite.nopwrgen")));
			}

			if(SatelliteProperties.Property.DATA.isOfType(properties.getPropertyFlag())) {
				if( (dataStorage = properties.getMaxDataStorage()) > 0 ) 
					list.add(new StringTextComponent(LibVulpes.proxy.getLocalizedString("msg.itemsatellite.data") + ZUtils.formatNumber(dataStorage)));
				else
					list.add(new StringTextComponent(TextFormatting.YELLOW + LibVulpes.proxy.getLocalizedString("msg.itemsatellite.nodata")));
			}
		}
		else {
			list.add(new StringTextComponent(TextFormatting.RED + LibVulpes.proxy.getLocalizedString("msg.itemsatellite.empty")));
		}
	}
}
