package zmaster587.advancedRocketry.world.util;

import net.minecraft.entity.Entity;
import net.minecraft.world.Teleporter;
import net.minecraft.world.WorldServer;

public class TeleporterNoPortal extends Teleporter {

	public TeleporterNoPortal(WorldServer p_i1963_1_) {
		super(p_i1963_1_);
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
	public boolean placeInExistingPortal(Entity entityIn, float rotationYaw) {
		return false;
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
