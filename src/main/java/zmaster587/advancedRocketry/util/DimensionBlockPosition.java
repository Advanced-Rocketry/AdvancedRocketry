package zmaster587.advancedRocketry.util;

import net.minecraft.util.ResourceLocation;
import zmaster587.libVulpes.util.HashedBlockPosition;

public class DimensionBlockPosition {
	public HashedBlockPosition pos;
	public ResourceLocation dimid;
	
	public DimensionBlockPosition(ResourceLocation dimid, HashedBlockPosition pos) {
		this.dimid = dimid;
		this.pos = pos;
	}
	
	@Override
	public int hashCode() {
		if(dimid == null)
			return 0;
		if(pos == null)
			return dimid.hashCode();
		return dimid.hashCode() + pos.hashCode();
	}
	
	@Override
	public boolean equals(Object arg0) {
		
		if(!(arg0 instanceof DimensionBlockPosition))
			return false;
		
		boolean flag = false;
		
		if(pos == null) {
			flag = ((DimensionBlockPosition)arg0).pos == null;
		}
		
		return dimid == ((DimensionBlockPosition)arg0).dimid && (flag || pos.equals(((DimensionBlockPosition)arg0).pos));
	}
	
	@Override
	public String toString() {
		return pos == null ? "Invalid position" :  "Dimension " + dimid + " Location: " + pos;
	}
}
