package zmaster587.advancedRocketry.item;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import zmaster587.advancedRocketry.util.StorageChunk;

public class ItemPackedStructure extends Item {

	public ItemPackedStructure() {
		setHasSubtypes(true);
	}
	
	public void setStructure(ItemStack stack, StorageChunk chunk) {
		NBTTagCompound nbt;
		if(stack.hasTagCompound())
			nbt = stack.getTagCompound();
		else
			nbt = new NBTTagCompound();

		NBTTagCompound chunkNbt = new NBTTagCompound();

		chunk.writeToNBT(chunkNbt);

		nbt.setTag("chunk", chunkNbt);
		stack.setTagCompound(nbt);
	}

	public StorageChunk getStructure(ItemStack stack) {
		if(stack.hasTagCompound()) {
			NBTTagCompound nbt = stack.getTagCompound();
			StorageChunk chunk = new StorageChunk();
			
			chunk.readFromNBT(nbt.getCompoundTag("chunk"));
			return chunk;
		}
		return null;
	}
}
