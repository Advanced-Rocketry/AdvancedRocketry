package zmaster587.advancedRocketry.util;

import net.minecraft.nbt.NBTTagCompound;
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
	
	public void writeToNBT(NBTTagCompound nbt)
	{
		NBTTagCompound subTag = new NBTTagCompound();
		
		subTag.setDouble("x", x);
		subTag.setDouble("y", y);
		subTag.setDouble("z", z);
		subTag.setDouble("yaw", yaw);
		subTag.setDouble("pitch", pitch);
		subTag.setDouble("roll", roll);
		
		if(star != null)
			subTag.setInteger("star", star.getId());
		if(world != null)
			subTag.setInteger("world", world.getId());
		subTag.setBoolean("isInInterplanetarySpace", isInInterplanetarySpace);
		nbt.setTag("spacePosition", subTag);
	}
	
	public void readFromNBT(NBTTagCompound nbt)
	{
		NBTTagCompound subTag;
		if(!nbt.hasKey("spacePosition"))
			return;
		
		subTag = nbt.getCompoundTag("spacePosition");
		
		x = subTag.getDouble("x");
		y = subTag.getDouble("y");
		z = subTag.getDouble("z");
		yaw = subTag.getDouble("yaw");
		pitch = subTag.getDouble("pitch");
		roll = subTag.getDouble("roll");
		subTag.setDouble("pitch", pitch);
		subTag.setDouble("roll", roll);
		
		if(subTag.hasKey("star"))
			star = DimensionManager.getInstance().getStar(subTag.getInteger("star"));
		else
			star = null;
		
		if(subTag.hasKey("world"))
			world = DimensionManager.getInstance().getDimensionProperties(subTag.getInteger("world"));
		else
			world = null;
		
		isInInterplanetarySpace = subTag.getBoolean("isInInterplanetarySpace");
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
