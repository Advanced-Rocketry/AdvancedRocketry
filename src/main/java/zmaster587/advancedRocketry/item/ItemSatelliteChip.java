package zmaster587.advancedRocketry.item;

import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.World;
import zmaster587.advancedRocketry.api.Constants;
import zmaster587.advancedRocketry.api.ISatelliteIdItem;
import zmaster587.advancedRocketry.api.SatelliteRegistry;
import zmaster587.advancedRocketry.api.satellite.SatelliteBase;
import zmaster587.advancedRocketry.api.satellite.SatelliteProperties;
import zmaster587.libVulpes.LibVulpes;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;

public class ItemSatelliteChip extends Item implements ISatelliteIdItem {

	public ItemSatelliteChip(Properties properties) {
		super(properties);
		// TODO Auto-generated constructor stub
	}

	private static String name = "name";

	@Override
	public boolean isDamageable() {
		return false;
	}

	public static long getSatelliteId(@Nonnull ItemStack stack) {
		if(stack.hasTag()) {
			CompoundNBT nbt = stack.getTag();

			if(nbt != null)
			    return nbt.getLong("satelliteId");
		}
		return -1;
	}

	public SatelliteBase getSatellite(@Nonnull ItemStack stack) {
		if(stack.hasTag()) {
			CompoundNBT nbt = stack.getTag();

            if(nbt == null)
                return null;

            long satId = nbt.getLong("satelliteId");

			SatelliteBase satellite = zmaster587.advancedRocketry.dimension.DimensionManager.getInstance().getSatellite(satId);

			if(satellite != null) {

				if(!nbt.contains("dimId") || Constants.INVALID_PLANET.equals(new ResourceLocation(nbt.getString("dimId")))) {
					nbt.putString("dimId", satellite.getDimensionId().get().toString());
				}

				if( zmaster587.advancedRocketry.dimension.DimensionManager.getInstance().getDimensionProperties(satellite.getDimensionId().get()) != null)
					nbt.putString(name, zmaster587.advancedRocketry.dimension.DimensionManager.getInstance().getDimensionProperties(satellite.getDimensionId().get()).getName());
			}


			return satellite;
		}
		return null;
	}

	public void setSatellite(@Nonnull ItemStack stack, SatelliteBase satellite) {
		CompoundNBT nbt;
		if(stack.hasTag())
			nbt = stack.getTag();
		else 
			nbt = new CompoundNBT();

		nbt.putString("satelliteName", satellite.getName());
		nbt.putString("dimId", satellite.getDimensionId().toString());
		nbt.putLong("satelliteId", satellite.getId());
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
			CompoundNBT nbt;
			if(stack.hasTag())
				nbt = stack.getTag();
			else 
				nbt = new CompoundNBT();


			nbt.putString("satelliteName", satellite2.getName());
			nbt.putLong("satelliteId", satellite.getId());

			stack.setTag(nbt);
		}
	}

	public void erase(@Nonnull ItemStack stack) {
		stack.setTag(null);
	}

	public void setDim(@Nonnull ItemStack stack, int dimId) {
		CompoundNBT nbt;
		if(stack.hasTag())
			nbt = stack.getTag();
		else 
			return;

		nbt.putInt("dimId", dimId);
	}

	public String getSatelliteName(@Nonnull ItemStack stack) {
		if(stack.hasTag()) {
			CompoundNBT nbt = stack.getTag();

			return nbt.getString("satelliteName");
		}
		return "";
	}

	public ResourceLocation getWorldId(@Nonnull ItemStack stack) {
		CompoundNBT nbt;

		if(stack.hasTag() && (nbt = stack.getTag()).contains("dimId") ) {


			return new ResourceLocation( nbt.getString("dimId"));
		}
		return Constants.INVALID_PLANET; // Cant have a [strike]nether[/strike] satellite anyway...ofc you can
	}

	@Override
	@ParametersAreNonnullByDefault
	public void addInformation(@Nonnull ItemStack stack, @Nullable World world, List<ITextComponent> list, ITooltipFlag bool) {
		ResourceLocation worldId = getWorldId(stack);
		long satId = getSatelliteId(stack);

		String satelliteName = getSatelliteName(stack);

		if(satId != -1) {

			if(!Constants.INVALID_PLANET.equals(worldId)) {

				if(stack.getTag().contains(name)) {

					list.add(new StringTextComponent(LibVulpes.proxy.getLocalizedString("msg.itemsatchip.id") + satId));
					list.add(new StringTextComponent(LibVulpes.proxy.getLocalizedString("msg.itemsatchip.planet") + stack.getTag().getString(name)));
					list.add(new StringTextComponent(LibVulpes.proxy.getLocalizedString("msg.itemsatchip.sat") + satelliteName));
				}
				else {
					list.add(new StringTextComponent(LibVulpes.proxy.getLocalizedString("msg.itemsatchip.planetunk")));
					list.add(new StringTextComponent(LibVulpes.proxy.getLocalizedString("msg.itemsatchip.satlost"))); //TODO: make satellite respond with name until
				}
			}
			else {
				list.add(new StringTextComponent(LibVulpes.proxy.getLocalizedString("msg.itemsatchip.id") + satId));
				list.add(new StringTextComponent(LibVulpes.proxy.getLocalizedString("msg.itemsatchip.planetunk")));
				list.add(new StringTextComponent(LibVulpes.proxy.getLocalizedString("msg.itemsatchip.sat") + satelliteName));
			}
		}
		else
			list.add(new StringTextComponent(LibVulpes.proxy.getLocalizedString("msg.unprogrammed")));
	}
}
