package zmaster587.advancedRocketry.world.util;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.Teleporter;
import net.minecraft.world.WorldServer;

public class TeleporterNoPortalSeekBlock extends Teleporter {
	private final WorldServer world;
	
	public TeleporterNoPortalSeekBlock(WorldServer p_i1963_1_) {
		super(p_i1963_1_);
		world = p_i1963_1_;
	}

	public void teleport(Entity entity, WorldServer world) {

		if (entity.isEntityAlive()) {
			entity.setLocationAndAngles(entity.posX, entity.posY, entity.posZ, entity.rotationYaw, entity.rotationPitch);
			world.spawnEntityInWorld(entity);
			world.updateEntityWithOptionalForce(entity, false);
		}
		entity.setWorld(world);
	}

	@Override
	public boolean placeInExistingPortal(Entity entityIn, double par2, double par4, double par6, float par8) {
		
		int x, y, z;
		x = (int) entityIn.posX;
		y = (int) entityIn.posY;
		z = (int) entityIn.posZ;
		
		for(int yy = (int) y; yy < world.getHeight(); yy++) {
			if(world.isAirBlock(x,yy,z) && world.isAirBlock(x,yy+1,z)){
				y = yy;
				break;
			}
		}
       
	    
	    entityIn.setLocationAndAngles(x,y,z, entityIn.rotationYaw, entityIn.rotationPitch);
	    
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
