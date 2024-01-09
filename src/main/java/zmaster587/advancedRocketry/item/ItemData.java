package zmaster587.advancedRocketry.item;

import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import zmaster587.advancedRocketry.api.DataStorage;
import zmaster587.libVulpes.items.ItemIngredient;

import org.jetbrains.annotations.NotNull;
import java.util.List;

public class ItemData extends ItemIngredient {

	int maxData;

	public ItemData() {
		super(1);
		setMaxStackSize(1);
	}

	public int getMaxData(int damage) {
		return damage == 0 ? 1000 : 0;
	}

	@Override
	public int getItemStackLimit(@NotNull ItemStack stack) {
		return getData(stack) == 0 ? super.getItemStackLimit(stack) : 1;
	}
	
	public int getData(@NotNull ItemStack stack) {
		return getDataStorage(stack).getData();
	}
	
	public DataStorage.DataType getDataType(@NotNull ItemStack stack) {
		return getDataStorage(stack).getDataType();
	}
	
	public DataStorage getDataStorage(@NotNull ItemStack item) {

		DataStorage data = new DataStorage();

		if(!item.hasTagCompound()) {
			data.setMaxData(getMaxData(item.getItemDamage()));
			NBTTagCompound nbt = new NBTTagCompound();
			data.writeToNBT(nbt);
		}
		else
			data.readFromNBT(item.getTagCompound());

		return data;
	}

	public int addData(@NotNull ItemStack item, int amount, DataStorage.DataType dataType) {
		DataStorage data = getDataStorage(item);

		int amt = data.addData(amount, dataType, true);

		NBTTagCompound nbt = new NBTTagCompound();
		data.writeToNBT(nbt);
		item.setTagCompound(nbt);

		return amt;
	}

	public int removeData(@NotNull ItemStack item, int amount, DataStorage.DataType dataType) {
		DataStorage data = getDataStorage(item);

		int amt = data.removeData(amount, true);

		NBTTagCompound nbt = new NBTTagCompound();
		data.writeToNBT(nbt);
		item.setTagCompound(nbt);

		return amt;
	}

	public void setData(@NotNull ItemStack item, int amount, DataStorage.DataType dataType) {
		DataStorage data = getDataStorage(item);

		data.setData(amount, dataType);

		NBTTagCompound nbt = new NBTTagCompound();
		data.writeToNBT(nbt);
		item.setTagCompound(nbt);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void addInformation(@NotNull ItemStack stack, World player, List<String> list, ITooltipFlag bool) {
		super.addInformation(stack, player, list, bool);

		DataStorage data = getDataStorage(stack);

		list.add(data.getData() + " / " + data.getMaxData() + " Data");
		list.add(I18n.format(data.getDataType().toString()));

	}

}
