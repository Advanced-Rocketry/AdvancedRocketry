package zmaster587.advancedRocketry.util;

import zmaster587.libVulpes.util.BlockPosition;

public class StationLandingLocation {
	private BlockPosition pos;
	private String name;
	private boolean occupied;
	
	public StationLandingLocation(BlockPosition pos, String name) {
		this.pos = pos;
		this.name = name;
	}
	
	public StationLandingLocation(BlockPosition pos) {
		this(pos,"");
	}
	
	public BlockPosition getPos() {
		return pos;
	}
	
	public void setPos(BlockPosition pos) {
		this.pos = pos;
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public boolean getOccupied() {
		return occupied;
	}
	
	public void setOccupied(boolean occupied) {
		this.occupied = occupied;
	}
	
	@Override
	public boolean equals(Object object) {
		if(object instanceof StationLandingLocation)
			return pos.equals(((StationLandingLocation) object).getPos());
		else if(object instanceof BlockPosition)
			return pos.equals(object);
		return super.equals(object);
	}
	
	@Override
	public int hashCode() {
		return pos.hashCode();
	}
	
	@Override
	public String toString() {
		return name == "" || name.isEmpty() ? pos.toString() : name;
	}
}
