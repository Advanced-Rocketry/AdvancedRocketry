package zmaster587.advancedRocketry.item;

import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Direction;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import zmaster587.advancedRocketry.api.DataStorage;
import zmaster587.advancedRocketry.world.util.MultiData;

import javax.annotation.Nonnull;
import java.util.List;

public class ItemMultiData extends Item {

	public ItemMultiData(Properties props) {
		super(props);
	}

	public void setMaxData(@Nonnull ItemStack stack, int amount) {
		MultiData data = getDataStorage(stack);
		data.setMaxData(amount);

		CompoundNBT nbt;

		if(!stack.hasTag()) {
			nbt= new CompoundNBT();
		}
		else
			nbt = stack.getTag();
		data.writeToNBT(nbt);
		stack.setTag(nbt);
	}

	public int getData(@Nonnull ItemStack stack, DataStorage.DataType type) {
		return getDataStorage(stack).getDataAmount(type);
	}
	
	public int getMaxData(@Nonnull ItemStack stack) {
		return getDataStorage(stack).getMaxData();
	}

	private MultiData getDataStorage(@Nonnull ItemStack item) {

		MultiData data = new MultiData();

		if(!item.hasTag()) {
			CompoundNBT nbt = new CompoundNBT();
			data.writeToNBT(nbt);
		}
		else
			data.readFromNBT(item.getTag());

		return data;
	}

	public boolean isFull(@Nonnull ItemStack item,  DataStorage.DataType dataType) {
		return getDataStorage(item).getMaxData() == getData(item, dataType);
		
	}
	
	public int addData(@Nonnull ItemStack item, int amount, DataStorage.DataType dataType) {
		MultiData data = getDataStorage(item);

		int amt = data.addData(amount, dataType, Direction.DOWN,true);

		CompoundNBT nbt;
		if(item.hasTag())
			nbt = item.getTag();
		else
			nbt = new CompoundNBT();
		
		data.writeToNBT(nbt);
		item.setTag(nbt);

		return amt;
	}

	public int removeData(@Nonnull ItemStack item, int amount, DataStorage.DataType dataType) {
		MultiData data = getDataStorage(item);

		int amt = data.extractData(amount, dataType, Direction.DOWN, true);
		
		CompoundNBT nbt;
		if(item.hasTag())
			nbt = item.getTag();
		else
			nbt = new CompoundNBT();
		
		data.writeToNBT(nbt);
		item.setTag(nbt);

		return amt;
	}

	public void setData(@Nonnull ItemStack item, int amount, DataStorage.DataType dataType) {
		MultiData data = getDataStorage(item);

		data.setDataAmount(amount, dataType);

		CompoundNBT nbt;
		if(item.hasTag())
			nbt = item.getTag();
		else
			nbt = new CompoundNBT();
		
		data.writeToNBT(nbt);
		item.setTag(nbt);
	}

	@Override
	@OnlyIn(value=Dist.CLIENT)
	public void addInformation(ItemStack stack, World player, List<ITextComponent> list, ITooltipFlag bool) {
		super.addInformation(stack, player, list, bool);

		MultiData data = getDataStorage(stack);

		for(DataStorage.DataType type : DataStorage.DataType.values()) {
			if(type != DataStorage.DataType.UNDEFINED)
				list.add(new StringTextComponent(data.getDataAmount(type) + " / " + data.getMaxData() + " " + I18n.format(type.toString(), new Object[0]) + " Data"));
		}
	}
}
