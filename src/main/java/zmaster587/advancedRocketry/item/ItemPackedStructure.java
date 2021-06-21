package zmaster587.advancedRocketry.item;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import zmaster587.advancedRocketry.util.StorageChunk;

import javax.annotation.Nonnull;

public class ItemPackedStructure extends Item {

	public ItemPackedStructure(Properties props) {
		super(props);
	}
	
<<<<<<< HEAD
	public void setStructure(ItemStack stack, StorageChunk chunk) {
		CompoundNBT nbt;
		if(stack.hasTag())
			nbt = stack.getTag();
=======
	public void setStructure(@Nonnull ItemStack stack, StorageChunk chunk) {
		NBTTagCompound nbt;
		if(stack.hasTagCompound())
			nbt = stack.getTagCompound();
>>>>>>> origin/feature/nuclearthermalrockets
		else
			nbt = new CompoundNBT();

		CompoundNBT chunkNbt = new CompoundNBT();

		chunk.writeToNBT(chunkNbt);

		nbt.put("chunk", chunkNbt);
		stack.setTag(nbt);
	}

<<<<<<< HEAD
	public StorageChunk getStructure(ItemStack stack) {
		if(stack.hasTag()) {
			CompoundNBT nbt = stack.getTag();
=======
	public StorageChunk getStructure(@Nonnull ItemStack stack) {
		if(stack.hasTagCompound()) {
			NBTTagCompound nbt = stack.getTagCompound();
>>>>>>> origin/feature/nuclearthermalrockets
			StorageChunk chunk = new StorageChunk();
			
			chunk.readFromNBT(nbt.getCompound("chunk"));
			return chunk;
		}
		return null;
	}
}
