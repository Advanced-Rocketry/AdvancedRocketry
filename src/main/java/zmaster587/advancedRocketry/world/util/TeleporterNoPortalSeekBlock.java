package zmaster587.advancedRocketry.world.util;

import net.minecraft.block.PortalInfo;
import net.minecraft.world.server.ServerWorld;

public class TeleporterNoPortalSeekBlock extends TeleporterNoPortal {

	public TeleporterNoPortalSeekBlock(ServerWorld p_i1963_1_, PortalInfo info) {
		super(p_i1963_1_, info);
	}


	/*@Override
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
	}*/
}
