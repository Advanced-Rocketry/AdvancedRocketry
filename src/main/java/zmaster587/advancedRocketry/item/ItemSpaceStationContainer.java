package zmaster587.advancedRocketry.item;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import zmaster587.advancedRocketry.util.StorageChunk;

public class ItemSpaceStationContainer extends Item {

	public ItemSpaceStationContainer(Properties props) {
		super(props);
	}

	public void setStructure(ItemStack stack, StorageChunk chunk) {
		CompoundNBT nbt;
		if(stack.hasTag())
			nbt = stack.getTag();
		else
			nbt = new CompoundNBT();

		CompoundNBT chunkNbt = new CompoundNBT();

		chunk.writeToNBT(chunkNbt);

		nbt.put("chunk", chunkNbt);
		stack.setTag(nbt);
	}

	public StorageChunk getStructure(ItemStack stack) {
		if(stack.hasTag()) {
			CompoundNBT nbt = stack.getTag();
			StorageChunk chunk = new StorageChunk();
			
			chunk.readFromNBT(nbt.getCompound("chunk"));
			return chunk;
		}
		return null;
	}
}
