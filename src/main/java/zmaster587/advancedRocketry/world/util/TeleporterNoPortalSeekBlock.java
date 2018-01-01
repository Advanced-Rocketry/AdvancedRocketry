package zmaster587.advancedRocketry.world.util;

import net.minecraft.block.state.pattern.BlockPattern;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.BlockPos.MutableBlockPos;
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
        
	    if (entityIn instanceof EntityPlayerMP)
	    {
	        ((EntityPlayerMP)entityIn).connection.setPlayerLocation(x,y,z, entityIn.rotationYaw, entityIn.rotationPitch);
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
