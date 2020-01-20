package zmaster587.advancedRocketry.util;

import net.minecraft.entity.Entity;
import zmaster587.advancedRocketry.api.dimension.IDimensionProperties;
import zmaster587.advancedRocketry.dimension.DimensionProperties;

public class SpaceTravelInteraction {

	public static void tick(SpacePosition spacePosition, Entity entity)
	{
		//Check if close to a world
		if(spacePosition.world == null && spacePosition.star != null)
		{
			for(IDimensionProperties properties : spacePosition.star.getPlanets())
			{
				SpacePosition worldSpacePosition = properties.getSpacePosition();
				double distanceSq = spacePosition.distanceToSpacePosition2(worldSpacePosition);
			
				if(distanceSq < 200)
				{
					spacePosition.world = (DimensionProperties) properties;
					
					
					//Radius to put the player
					double radius = -480;
					//Assume planet centered at 0
					SpacePosition planetPosition = new SpacePosition();
					double theta = Math.atan2(entity.motionZ, entity.motionX);
					
					spacePosition.x = planetPosition.x + Math.cos(theta)*radius;
					spacePosition.y = planetPosition.y;
					spacePosition.z = planetPosition.z + Math.sin(theta)*radius;
					
					break;
				}
			}
		}
		if(spacePosition.world != null)
		{
			double distanceSq = spacePosition.distanceToSpacePosition2(new SpacePosition());
			
			// transition to solar navigation
			if(distanceSq > 40000*8)
			{
				//Radius to put the player
				double radius = 12;
				
				SpacePosition planetPosition = spacePosition.world.getSpacePosition();
				spacePosition.world = null;
				
				double theta = Math.atan2(entity.motionZ, entity.motionX);
				
				spacePosition.x = planetPosition.x + Math.cos(theta)*radius;
				spacePosition.y = planetPosition.y;
				spacePosition.z = planetPosition.z + Math.sin(theta)*radius;
			}
		}
	}
	
	public static boolean isCloseToWorld(SpacePosition spacePosition, IDimensionProperties properties)
	{
		if(spacePosition.world == null && spacePosition.star != null)
		{
			SpacePosition worldSpacePosition = properties.getSpacePosition();
			double distanceSq = spacePosition.distanceToSpacePosition2(worldSpacePosition);
		
			if(distanceSq < 200)
			{
				return true;
			}
		}
		
		return false;
	}
	
	public static boolean isExitingWorld(SpacePosition spacePosition, IDimensionProperties properties)
	{
		if(spacePosition.world != null)
		{
			double distanceSq = spacePosition.distanceToSpacePosition2(new SpacePosition());
			if(distanceSq > 40000*8)
				return true;
		}
		
		return false;
	}
}
