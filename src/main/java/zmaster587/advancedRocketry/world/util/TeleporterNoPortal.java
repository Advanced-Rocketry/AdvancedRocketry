package zmaster587.advancedRocketry.world.util;

import java.util.Optional;

import net.minecraft.entity.Entity;
import net.minecraft.util.Direction;
import net.minecraft.util.TeleportationRepositioner;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Teleporter;
import net.minecraft.world.server.ServerWorld;

public class TeleporterNoPortal extends Teleporter {

	public TeleporterNoPortal(ServerWorld p_i1963_1_) {
		super(p_i1963_1_);
	}

	public void teleport(Entity entity, ServerWorld world) {

		if (entity.isAlive()) {
			entity.setLocationAndAngles(entity.getPosX(), entity.getPosY(), entity.getPosZ(), entity.rotationYaw, entity.rotationPitch);
			world.addEntity(entity);
		}
		entity.setWorld(world);
	}


	@Override
	public Optional<TeleportationRepositioner.Result> func_242956_a(BlockPos p_242956_1_, Direction.Axis p_242956_2_) {
		return Optional.empty();

	}

	@Override
	public Optional<TeleportationRepositioner.Result> func_242957_a(BlockPos p_242957_1_, boolean p_242957_2_) {
		return Optional.empty();
	}
}
