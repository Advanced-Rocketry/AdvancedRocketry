package zmaster587.advancedRocketry.util;

import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;

public class TransitionEntity {
	long time;
	public Entity entity;
	public int dimId;
	public BlockPos location;
	public Entity entity2; //the mount
	
	public TransitionEntity(long time, Entity entity, int dimId, BlockPos location, Entity entity2 ) {
		this.time = time;
		this.entity = entity;
		this.dimId = dimId;
		this.location = location;
		this.entity2 = entity2;
	}
}
