package zmaster587.advancedRocketry.item;

import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import zmaster587.advancedRocketry.api.Constants;
import zmaster587.advancedRocketry.api.ISatelliteIdItem;
import zmaster587.advancedRocketry.api.SatelliteRegistry;
import zmaster587.advancedRocketry.api.satellite.SatelliteBase;
import zmaster587.advancedRocketry.api.satellite.SatelliteProperties;
import zmaster587.libVulpes.LibVulpes;

import javax.annotation.Nonnull;
import java.util.List;

public class ItemSatelliteIdentificationChip extends Item implements ISatelliteIdItem {

	private static String name = "name";

	@Override
	public boolean isDamageable() {
		return false;
	}

	public long getSatelliteId(@Nonnull ItemStack stack) {
		if(stack.hasTagCompound()) {
			NBTTagCompound nbt = stack.getTagCompound();

			return nbt.getLong("satelliteId");
		}
		return -1;
	}

	public SatelliteBase getSatellite(@Nonnull ItemStack stack) {
		if(stack.hasTagCompound()) {
			NBTTagCompound nbt = stack.getTagCompound();

			long satId = nbt.getLong("satelliteId");

			SatelliteBase satellite = zmaster587.advancedRocketry.dimension.DimensionManager.getInstance().getSatellite(satId);

			if(satellite != null) {

				if(!nbt.hasKey("dimId") || nbt.getInteger("dimId") == Constants.INVALID_PLANET) {
					nbt.setInteger("dimId", satellite.getDimensionId());
				}

				if( zmaster587.advancedRocketry.dimension.DimensionManager.getInstance().getDimensionProperties(satellite.getDimensionId()) != null)
					nbt.setString(name, zmaster587.advancedRocketry.dimension.DimensionManager.getInstance().getDimensionProperties(satellite.getDimensionId()).getName());
			}


			return satellite;
		}
		return null;
	}

	public void setSatellite(@Nonnull ItemStack stack, SatelliteBase satellite) {
		NBTTagCompound nbt;
		if(stack.hasTagCompound())
			nbt = stack.getTagCompound();
		else 
			nbt = new NBTTagCompound();

		nbt.setString("satelliteName", satellite.getName());
		nbt.setInteger("dimId", satellite.getDimensionId());
		nbt.setLong("satelliteId", satellite.getId());
	}

	/**
	 * Note: this method does not modify dimension info
	 * @param stack itemStack
	 * @param satellite properties of satellite to set info with
	 */
	public void setSatellite(@Nonnull ItemStack stack, SatelliteProperties satellite) {
		erase(stack);
		SatelliteBase satellite2 = SatelliteRegistry.getSatellite(satellite.getSatelliteType());
		if(satellite2 != null) {
			NBTTagCompound nbt;
			if(stack.hasTagCompound())
				nbt = stack.getTagCompound();
			else 
				nbt = new NBTTagCompound();


			nbt.setString("satelliteName", satellite2.getName());
			nbt.setLong("satelliteId", satellite.getId());

			stack.setTagCompound(nbt);
		}
	}

	public void erase(@Nonnull ItemStack stack) {
		stack.setTagCompound(null);
	}

	public void setDim(@Nonnull ItemStack stack, int dimId) {
		NBTTagCompound nbt;
		if(stack.hasTagCompound())
			nbt = stack.getTagCompound();
		else 
			return;

		nbt.setInteger("dimId", dimId);
	}

	public String getSatelliteName(@Nonnull ItemStack stack) {
		if(stack.hasTagCompound()) {
			NBTTagCompound nbt = stack.getTagCompound();

			return nbt.getString("satelliteName");
		}
		return "";
	}

	public int getWorldId(@Nonnull ItemStack stack) {
		NBTTagCompound nbt;

		if(stack.hasTagCompound() && (nbt = stack.getTagCompound()).hasKey("dimId") ) {


			return nbt.getInteger("dimId");
		}
		return Constants.INVALID_PLANET; // Cant have a [strike]nether[/strike] satellite anyway...ofc you can
	}

	@Override
	public void addInformation(@Nonnull ItemStack stack, World player, List<String> list, ITooltipFlag bool) {
		int worldId = getWorldId(stack);
		long satId = getSatelliteId(stack);

		String satelliteName = getSatelliteName(stack);

		if(satId != -1) {

			if(worldId != Constants.INVALID_PLANET) {

				if(stack.getTagCompound().hasKey(name)) {

					list.add(LibVulpes.proxy.getLocalizedString("msg.itemsatchip.id") + satId);
					list.add(LibVulpes.proxy.getLocalizedString("msg.itemsatchip.planet") + stack.getTagCompound().getString(name));
					list.add(LibVulpes.proxy.getLocalizedString("msg.itemsatchip.sat") + satelliteName);
				}
				else {
					list.add(LibVulpes.proxy.getLocalizedString("msg.itemsatchip.planetunk"));
					list.add(LibVulpes.proxy.getLocalizedString("msg.itemsatchip.satlost")); //TODO: make satellite respond with name until
				}
			}
			else {
				list.add(LibVulpes.proxy.getLocalizedString("msg.itemsatchip.id") + satId);
				list.add(LibVulpes.proxy.getLocalizedString("msg.itemsatchip.planetunk"));
				list.add(LibVulpes.proxy.getLocalizedString("msg.itemsatchip.sat") + satelliteName);
			}
		}
		else
			list.add(LibVulpes.proxy.getLocalizedString("msg.unprogrammed"));
	}
}
