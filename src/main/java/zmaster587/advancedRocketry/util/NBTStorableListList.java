package zmaster587.advancedRocketry.util;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.util.ResourceLocation;
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
	
	public void writeToNBT(CompoundNBT nbt) {
		ListNBT list = new ListNBT();
		for(DimensionBlockPosition pos : this.pos) {
			CompoundNBT tag = new CompoundNBT();
			tag.putIntArray("loc",new int[] { pos.pos.x, pos.pos.y, pos.pos.z } );
			tag.putString("dim", pos.dimid.toString());
			list.add(tag);
		}
		nbt.put("list", list);
		
	}
	
	public void readFromNBT(CompoundNBT nbt) {
		
		ListNBT list = nbt.getList("list", NBT.TAG_COMPOUND);
		pos.clear();
		for(int i = 0; i < list.size(); i++) {
			CompoundNBT nbttag = list.getCompound(i);
			int[] tag = nbttag.getIntArray("loc");
			ResourceLocation dimid = new ResourceLocation(nbttag.getString("dim"));
			
			pos.add(new DimensionBlockPosition(dimid, new HashedBlockPosition(tag[0], tag[1], tag[2])));
		}
	}
	
	public List<DimensionBlockPosition> getList() {
		return pos;
	}
}
