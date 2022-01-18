package zmaster587.advancedRocketry.item;

import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import zmaster587.advancedRocketry.api.Constants;
import zmaster587.advancedRocketry.dimension.DimensionManager;
import zmaster587.advancedRocketry.dimension.DimensionProperties;
import zmaster587.libVulpes.LibVulpes;

import javax.annotation.Nonnull;
import java.util.List;

public class ItemPlanetChip extends ItemIdWithName {

	private static final String dimensionNameIdentifier = "DimensionName";
	private static final String dimensionIdIdentifier = "dimId";
	private static final String uuidIdentifier = "UUID";

	public ItemPlanetChip(Properties props) {
		super(props);
	}

	@Override
	public boolean isDamageable() {
		return false;
	}

	/**
	 * @param stack itemStack of this item-type
	 * @return the DimensionProperties of the dimId stored on the item or null if invalid
	 */
	public DimensionProperties getDimension(ItemStack stack) {
		if(stack.hasTag()) {
			return DimensionManager.getInstance().getDimensionProperties( new ResourceLocation( stack.getTag().getString(dimensionIdIdentifier)));
		}
		return null;
	}

	/**
	 * @param stack ItemStack to check against
	 * @return true of the dimension stored on the stack exists and is valid
	 */
	public boolean hasValidDimension(@Nonnull ItemStack stack) {

		if(stack.hasTag()) {
			ResourceLocation dimId = new ResourceLocation(stack.getTag().getString(dimensionIdIdentifier));
			return DimensionManager.getInstance().isDimensionCreated(dimId);
		}

		return false;
	}

	/**
	 * Removes any Information and reset the stack to a default state
	 * @param stack stack to erase
	 */
	public void erase(ItemStack stack) {
		stack.setTag(null);
	}

	/**
	 * Sets the dimId and adds the planet's name to the chip
	 * @param stack itemStack to operate on
	 * @param dimensionId dimension Id number
	 */
	public void setDimensionId(ItemStack stack, ResourceLocation dimensionId) {

		CompoundNBT nbt;
		if(Constants.INVALID_PLANET.equals(dimensionId)) {
			nbt = new CompoundNBT();
			nbt.putString(dimensionIdIdentifier, dimensionId.toString());
			return;
		}

		DimensionProperties properties = DimensionManager.getInstance().getDimensionProperties(dimensionId);
		if(properties == null) {
			erase(stack);
			return;
		}

		if(stack.hasTag())
			nbt = stack.getTag();
		else
			nbt = new CompoundNBT();

		nbt.putString(dimensionIdIdentifier, dimensionId.toString());
		nbt.putString(dimensionNameIdentifier, properties.getName());
		stack.setTag(nbt);
	}

	/**
	 * @param stack stack to get the dimId from
	 * @return id of the dimension stored or Constants.INVALID_PLANET if invalid
	 */
	public ResourceLocation getDimensionId(ItemStack stack) {
		if(stack.hasTag())
			return new ResourceLocation(stack.getTag().getString(dimensionIdIdentifier));
		return Constants.INVALID_PLANET;
	}

	/**
	 * Nicer way of getting dimension info from and itemStack
	 * @param stack stack to get the DimensionProperties object from
	 * @return DimensionProperties Object of the relevent dimension or null if invalid
	 */
	public DimensionProperties getDimensionProperties(ItemStack stack) {
		if(stack.hasTag())
			return DimensionManager.getInstance().getDimensionProperties(new ResourceLocation(stack.getTag().getString(dimensionIdIdentifier)));
		return null;
	}

	public Long getUUID(ItemStack stack) {
		if(stack.hasTag())
			return stack.getTag().getLong(uuidIdentifier);
		return null;
	}

	public void setUUID(ItemStack stack, long uuid) {
		CompoundNBT nbt;
		if(stack.hasTag())
			nbt = stack.getTag();
		else
			nbt = new CompoundNBT();

		nbt.putLong(uuidIdentifier,uuid);
		stack.setTag(nbt);
	}

	@Override
	public void addInformation(@Nonnull ItemStack stack, World player, List<ITextComponent> list, ITooltipFlag bool){

		if(!stack.hasTag()) {
			list.add(new StringTextComponent(LibVulpes.proxy.getLocalizedString("msg.unprogrammed")));
		}
		else if(!hasValidDimension(stack)) {
			list.add(new StringTextComponent(TextFormatting.RED + LibVulpes.proxy.getLocalizedString("msg.programfail")));
		}
		else {
			if(stack.getDamage()  == 0) {
				DimensionProperties props = DimensionManager.getInstance().getDimensionProperties(getDimensionId(stack));

				String unknown = TextFormatting.YELLOW + "???";
				String dimName = stack.getTag().getString(dimensionNameIdentifier);

				list.add(new StringTextComponent(LibVulpes.proxy.getLocalizedString("msg.itemplanetidchip.planetname") + TextFormatting.DARK_GREEN  + dimName));

				if( !props.getRequiredArtifacts().isEmpty()) {
					list.add(new StringTextComponent(LibVulpes.proxy.getLocalizedString("msg.itemplanetidchip.artifacts")));
					for(ItemStack stack2 : props.getRequiredArtifacts())
					{
						list.add(new StringTextComponent(TextFormatting.DARK_PURPLE + "    " + stack2.getDisplayName()));
					}
				}

				//super.addInformation(stack, player, list, bool);

				//list.add("Mass: " + unknown);
				//list.add("Atmosphere Density: " + unknown);
				//list.add("Distance From Star: " + unknown);

			}
			else { //Space station
				list.add(new StringTextComponent(LibVulpes.proxy.getLocalizedString("msg.itemplanetidchip.stationid") + TextFormatting.DARK_GREEN + stack.getTag().getString(dimensionNameIdentifier)));
			}
		}
	}
}
