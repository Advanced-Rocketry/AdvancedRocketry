package zmaster587.advancedRocketry.entity;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.monster.DrownedEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.IPacket;
import net.minecraft.network.play.server.SSpawnObjectPacket;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import zmaster587.advancedRocketry.api.AdvancedRocketryEntities;

public class EntityDummy extends Entity {

	
	//Just a dummy so a player can sit on a chair
	public EntityDummy(EntityType<? extends EntityDummy> type, World world) {
		super(AdvancedRocketryEntities.ENTITY_DUMMY, world);
		this.noClip=true;
		
	}

	public EntityDummy(World world, double x, double y, double z) {
		this(AdvancedRocketryEntities.ENTITY_DUMMY, world);
		setPosition(x, y, z);
	}
	
	@Override
	public boolean isInvisible() {
		return true;
	}
	@Override
	public boolean isInvisibleToPlayer(PlayerEntity player) {
		return true;
	}

	/**
	 * Checks if the entity is in range to render by using the past in distance and comparing it to its average edge
	 * length * 64 * renderDistanceWeight Args: distance
	 */
	@OnlyIn(value=Dist.CLIENT)
	@Override
	public boolean isInRangeToRenderDist(double p_70112_1_)
	{
		return false;
	}

	@Override
	public boolean shouldRiderSit() {
		return true;
	}
	
	@Override
	public void read(CompoundNBT p_70037_1_) {
		
	}
	
	@Override
	public CompoundNBT writeWithoutTypeId(CompoundNBT p_70014_1_) {
		return new CompoundNBT();
	}

	@Override
	public boolean writeUnlessPassenger(CompoundNBT compound) {
		// TODO Auto-generated method stub
		return super.writeUnlessPassenger(compound);
	}
	
	@Override
	protected void registerData() {
		
	}

	@Override
	protected void readAdditional(CompoundNBT compound) {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void writeAdditional(CompoundNBT compound) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public IPacket<?> createSpawnPacket() {
		return new SSpawnObjectPacket(this);
	}

	
}
