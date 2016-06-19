package zmaster587.advancedRocketry.item;

import java.util.List;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import zmaster587.advancedRocketry.api.DataStorage;
import net.minecraft.client.resources.I18n;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

public class ItemData extends ItemIngredient {

	int maxData;

	public ItemData() {
		super(1);
		setMaxStackSize(16);
	}

	public int getMaxData(int damage) {
		switch(damage) {
		case 0:
			return 1000;
		}
		return 0;
	}

	public int getData(ItemStack stack) {
		return getDataStorage(stack).getData();
	}
	
	public DataStorage.DataType getDataType(ItemStack stack) {
		return getDataStorage(stack).getDataType();
	}
	
	public DataStorage getDataStorage(ItemStack item) {

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

	public int addData(ItemStack item, int amount, DataStorage.DataType dataType) {
		DataStorage data = getDataStorage(item);

		int amt = data.addData(amount, dataType, true);

		NBTTagCompound nbt = new NBTTagCompound();
		data.writeToNBT(nbt);
		item.setTagCompound(nbt);

		return amt;
	}

	public int removeData(ItemStack item, int amount, DataStorage.DataType dataType) {
		DataStorage data = getDataStorage(item);

		int amt = data.removeData(amount, true);

		NBTTagCompound nbt = new NBTTagCompound();
		data.writeToNBT(nbt);
		item.setTagCompound(nbt);

		return amt;
	}

	public void setData(ItemStack item, int amount, DataStorage.DataType dataType) {
		DataStorage data = getDataStorage(item);

		data.setData(amount, dataType);

		NBTTagCompound nbt = new NBTTagCompound();
		data.writeToNBT(nbt);
		item.setTagCompound(nbt);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void addInformation(ItemStack stack, EntityPlayer player,
			List list, boolean bool) {
		super.addInformation(stack, player, list, bool);

		DataStorage data = getDataStorage(stack);

		list.add(data.getData() + " / " + data.getMaxData() + " Data");
		list.add(I18n.format(data.getDataType().toString(), new Object[0]));

	}

}
