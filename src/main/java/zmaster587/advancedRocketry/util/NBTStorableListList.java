package zmaster587.advancedRocketry.util;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraftforge.common.util.Constants.NBT;
import zmaster587.libVulpes.util.HashedBlockPosition;

import java.util.LinkedList;
import java.util.List;

public class NBTStorableListList {

	List<DimensionBlockPosition> pos;
	
	public NBTStorableListList() {
		pos = new LinkedList<DimensionBlockPosition>();
	}
	
	public NBTStorableListList(List<DimensionBlockPosition> list) {
		pos = list;
	}
	
	public void writeToNBT(NBTTagCompound nbt) {
		NBTTagList list = new NBTTagList();
		for(DimensionBlockPosition pos : this.pos) {
			NBTTagCompound tag = new NBTTagCompound();
			tag.setIntArray("loc",new int[] { pos.pos.x, pos.pos.y, pos.pos.z } );
			tag.setInteger("dim", pos.dimid);
			list.appendTag(tag);
		}
		nbt.setTag("list", list);
		
	}
	
	public void readFromNBT(NBTTagCompound nbt) {
		
		NBTTagList list = nbt.getTagList("list", NBT.TAG_COMPOUND);
		pos.clear();
		for(int i = 0; i < list.tagCount(); i++) {
			NBTTagCompound nbttag = list.getCompoundTagAt(i);
			int[] tag = nbttag.getIntArray("loc");
			int dimid = nbttag.getInteger("dim");
			
			pos.add(new DimensionBlockPosition(dimid, new HashedBlockPosition(tag[0], tag[1], tag[2])));
		}
	}
	
	public List<DimensionBlockPosition> getList() {
		return pos;
	}
}
