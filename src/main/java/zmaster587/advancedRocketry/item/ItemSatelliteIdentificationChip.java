package zmaster587.advancedRocketry.item;

import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.StringTextComponent;
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

	public ItemSatelliteIdentificationChip(Properties properties) {
		super(properties);
		// TODO Auto-generated constructor stub
	}

	private static String name = "name";

	@Override
	public boolean isDamageable() {
		return false;
	}

<<<<<<< HEAD
	public long getSatelliteId(ItemStack stack) {
		if(stack.hasTag()) {
			CompoundNBT nbt = stack.getTag();
=======
	public long getSatelliteId(@Nonnull ItemStack stack) {
		if(stack.hasTagCompound()) {
			NBTTagCompound nbt = stack.getTagCompound();
>>>>>>> origin/feature/nuclearthermalrockets

			return nbt.getLong("satelliteId");
		}
		return -1;
	}

<<<<<<< HEAD
	public SatelliteBase getSatellite(ItemStack stack) {
		if(stack.hasTag()) {
			CompoundNBT nbt = stack.getTag();
=======
	public SatelliteBase getSatellite(@Nonnull ItemStack stack) {
		if(stack.hasTagCompound()) {
			NBTTagCompound nbt = stack.getTagCompound();
>>>>>>> origin/feature/nuclearthermalrockets

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

<<<<<<< HEAD
	public void setSatellite(ItemStack stack, SatelliteBase satellite) {
		CompoundNBT nbt;
		if(stack.hasTag())
			nbt = stack.getTag();
=======
	public void setSatellite(@Nonnull ItemStack stack, SatelliteBase satellite) {
		NBTTagCompound nbt;
		if(stack.hasTagCompound())
			nbt = stack.getTagCompound();
>>>>>>> origin/feature/nuclearthermalrockets
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

<<<<<<< HEAD
	public void erase(ItemStack stack) {
		stack.setTag(null);
	}

	public void setDim(ItemStack stack, int dimId) {
		CompoundNBT nbt;
		if(stack.hasTag())
			nbt = stack.getTag();
=======
	public void erase(@Nonnull ItemStack stack) {
		stack.setTagCompound(null);
	}

	public void setDim(@Nonnull ItemStack stack, int dimId) {
		NBTTagCompound nbt;
		if(stack.hasTagCompound())
			nbt = stack.getTagCompound();
>>>>>>> origin/feature/nuclearthermalrockets
		else 
			return;

		nbt.putInt("dimId", dimId);
	}

<<<<<<< HEAD
	public String getSatelliteName(ItemStack stack) {
		if(stack.hasTag()) {
			CompoundNBT nbt = stack.getTag();
=======
	public String getSatelliteName(@Nonnull ItemStack stack) {
		if(stack.hasTagCompound()) {
			NBTTagCompound nbt = stack.getTagCompound();
>>>>>>> origin/feature/nuclearthermalrockets

			return nbt.getString("satelliteName");
		}
		return "";
	}

<<<<<<< HEAD
	public ResourceLocation getWorldId(ItemStack stack) {
		CompoundNBT nbt;
=======
	public int getWorldId(@Nonnull ItemStack stack) {
		NBTTagCompound nbt;
>>>>>>> origin/feature/nuclearthermalrockets

		if(stack.hasTag() && (nbt = stack.getTag()).contains("dimId") ) {


			return new ResourceLocation( nbt.getString("dimId"));
		}
		return Constants.INVALID_PLANET; // Cant have a [strike]nether[/strike] satellite anyway...ofc you can
	}

	@Override
<<<<<<< HEAD
	public void addInformation(ItemStack stack, World player,
			List list, ITooltipFlag bool) {
		ResourceLocation worldId = getWorldId(stack);
=======
	public void addInformation(@Nonnull ItemStack stack, World player, List<String> list, ITooltipFlag bool) {
		int worldId = getWorldId(stack);
>>>>>>> origin/feature/nuclearthermalrockets
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
