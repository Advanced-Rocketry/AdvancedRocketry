package zmaster587.advancedRocketry.world.util;

import java.util.function.Function;

import net.minecraft.block.PortalInfo;
import net.minecraft.entity.Entity;
import net.minecraft.world.server.ServerWorld;

public class TeleporterNoPortal implements net.minecraftforge.common.util.ITeleporter  {
	PortalInfo portalinfo; 
	public TeleporterNoPortal(ServerWorld world, PortalInfo info) {
		portalinfo = info;
	}

	private Entity teleport(Entity entityold, ServerWorld world) {
		
        Entity entity = entityold.getType().create(world);
        if (entity != null) {
           entity.copyDataFromOld(entityold);
           entity.setLocationAndAngles(portalinfo.pos.x, portalinfo.pos.y, portalinfo.pos.z, portalinfo.rotationYaw, entity.rotationPitch);
           entity.setMotion(portalinfo.motion);
           world.addFromAnotherDimension(entity);
           entity.setUniqueId(entityold.getUniqueID());
        }
        return entity;
	}

    public Entity placeEntity(Entity entity, ServerWorld currentWorld, ServerWorld destWorld, float yaw, Function<Boolean, Entity> repositionEntity) {
        return teleport(entity, destWorld);
     }
}
