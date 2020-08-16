package zmaster587.advancedRocketry.world.util;

import net.minecraft.entity.Entity;
import net.minecraft.world.Teleporter;
import net.minecraft.world.server.ServerWorld;

public class TeleporterNoPortalSeekBlock extends Teleporter {

	public TeleporterNoPortalSeekBlock(ServerWorld p_i1963_1_) {
		super(p_i1963_1_);
	}

	public void teleport(Entity entity, ServerWorld world) {

		if (entity.isEntityAlive()) {
			entity.setLocationAndAngles(entity.getPosX(), entity.posY, entity.getPosZ(), entity.rotationYaw, entity.rotationPitch);
			world.spawnEntity(entity);
			world.updateEntityWithOptionalForce(entity, false);
		}
		entity.setWorld(world);
	}

	@Override
	public boolean placeInExistingPortal(Entity entityIn, float rotationYaw) {
		
		double x, y, z;
		x = entityIn.posX;
		y = entityIn.posY;
		z = entityIn.posZ;
		MutableBlockPos pos = new MutableBlockPos();
		
		for(int yy = (int) y; yy < world.getHeight(); yy++) {
			pos.setPos(x, yy, z);
			if(world.isAirBlock(pos) && world.isAirBlock(pos.add(0,1,0))){
				y = yy;
				break;
			}
		}
        
	    if (entityIn instanceof ServerPlayerEntity)
	    {
	        ((ServerPlayerEntity)entityIn).connection.setPlayerLocation(x,y,z, entityIn.rotationYaw, entityIn.rotationPitch);
	    }
	    else
	    {
	        entityIn.setLocationAndAngles(x,y,z, entityIn.rotationYaw, entityIn.rotationPitch);
	    }
	    
	    return true;
	}

	@Override
	public void removeStalePortalLocations(long par1)
	{
	}

	
	@Override
	public boolean makePortal(Entity p_85188_1_) {
		return true;
	}
}
