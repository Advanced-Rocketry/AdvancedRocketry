package zmaster587.advancedRocketry.util;

import zmaster587.libVulpes.util.BlockPosition;

public class DimensionBlockPosition {
	public BlockPosition pos;
	public int dimid;
	
	public DimensionBlockPosition(int dimid, BlockPosition pos) {
		this.dimid = dimid;
		this.pos = pos;
	}
	
	@Override
	public int hashCode() {
		if(pos == null)
			return dimid;
		return dimid + pos.hashCode();
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
