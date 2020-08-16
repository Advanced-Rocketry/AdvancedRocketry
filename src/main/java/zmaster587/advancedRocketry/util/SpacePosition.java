package zmaster587.advancedRocketry.util;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.vector.Vector3d;
import zmaster587.advancedRocketry.api.dimension.solar.StellarBody;
import zmaster587.advancedRocketry.dimension.DimensionManager;
import zmaster587.advancedRocketry.dimension.DimensionProperties;

public class SpacePosition {

	public double x,y,z, yaw, pitch, roll;
	public StellarBody star;
	public DimensionProperties world;
	public boolean isInInterplanetarySpace;
	
	public double distanceToSpacePosition2(SpacePosition s)
	{
		double xx, yy, zz;
		xx = x - s.x;
		yy = y - s.y;
		zz = z - s.z;
		
		return xx*xx + yy*yy + zz*zz;
	}
	
	public void writeToNBT(CompoundNBT nbt)
	{
		CompoundNBT subTag = new CompoundNBT();
		
		subTag.putDouble("x", x);
		subTag.putDouble("y", y);
		subTag.putDouble("z", z);
		subTag.putDouble("yaw", yaw);
		subTag.putDouble("pitch", pitch);
		subTag.putDouble("roll", roll);
		
		if(star != null)
			subTag.putString("star", star.getId().toString());
		if(world != null)
			subTag.putString("world", world.getId().toString());
		subTag.putBoolean("isInInterplanetarySpace", isInInterplanetarySpace);
		nbt.put("spacePosition", subTag);
	}
	
	public void readFromNBT(CompoundNBT nbt)
	{
		CompoundNBT subTag;
		if(!nbt.contains("spacePosition"))
			return;
		
		subTag = nbt.getCompound("spacePosition");
		
		x = subTag.getDouble("x");
		y = subTag.getDouble("y");
		z = subTag.getDouble("z");
		yaw = subTag.getDouble("yaw");
		pitch = subTag.getDouble("pitch");
		roll = subTag.getDouble("roll");
		subTag.putDouble("pitch", pitch);
		subTag.putDouble("roll", roll);
		
		if(subTag.contains("star"))
			star = DimensionManager.getInstance().getStar(new ResourceLocation(subTag.getString("star")));
		else
			star = null;
		
		if(subTag.contains("world"))
			world = DimensionManager.getInstance().getDimensionProperties(new ResourceLocation(subTag.getString("world")));
		else
			world = null;
		
		isInInterplanetarySpace = subTag.getBoolean("isInInterplanetarySpace");
	}
	
	public Vector3d getNormalVectorTo(SpacePosition other)
	{
		double x,y,z;
		
		double distance = Math.sqrt(this.distanceToSpacePosition2(other));
		
		x = (other.x - this.x )/distance;
		y = (other.y - this.y )/distance;
		z = (other.z - this.z )/distance;
		
		return new Vector3d(x,y,z);
	}
	
	public SpacePosition getFromSpherical(double radius, double theta)
	{
		SpacePosition returnPos = new SpacePosition();
		
		returnPos.world = world;
		returnPos.star = star;
		returnPos.isInInterplanetarySpace = isInInterplanetarySpace;
		
		returnPos.x = this.x + Math.cos(theta)*radius;
		returnPos.y = this.y;
		returnPos.z = this.z + Math.sin(theta)*radius;
		
		return returnPos;
	}
}
