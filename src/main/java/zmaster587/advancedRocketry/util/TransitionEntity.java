package zmaster587.advancedRocketry.util;

import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.server.ServerWorld;

public class TransitionEntity {
	public long time;
	public Entity entity;
	public ServerWorld dimId;
	public BlockPos location;
	public Entity entity2; //the mount
	
	public TransitionEntity(long time, Entity entity, ServerWorld dimId, BlockPos location, Entity entity2 ) {
		this.time = time;
		this.entity = entity;
		this.dimId = dimId;
		this.location = location;
		this.entity2 = entity2;
	}
}
