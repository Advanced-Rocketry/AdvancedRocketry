package zmaster587.advancedRocketry.entity;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.IPacket;
import net.minecraft.network.PacketBuffer;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.common.registry.IEntityAdditionalSpawnData;
import net.minecraftforge.fml.network.NetworkHooks;
import zmaster587.advancedRocketry.api.AdvancedRocketryEntities;
import zmaster587.libVulpes.network.PacketSpawnEntity;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

public class EntityDummy extends Entity implements IEntityAdditionalSpawnData {

	
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
	@ParametersAreNonnullByDefault
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
	@ParametersAreNonnullByDefault
	public void read(CompoundNBT nbt) {
		
	}
	
	@Nonnull
	@Override
	@ParametersAreNonnullByDefault
	public CompoundNBT writeWithoutTypeId(CompoundNBT nbt) {
		return new CompoundNBT();
	}

	@Override
	@ParametersAreNonnullByDefault
	public boolean writeUnlessPassenger(CompoundNBT compound) {
		// TODO Auto-generated method stub
		return super.writeUnlessPassenger(compound);
	}
	
	@Override
	protected void registerData() {
		
	}

	@Override
	@ParametersAreNonnullByDefault
	protected void readAdditional(CompoundNBT compound) {
		// TODO Auto-generated method stub
		
	}

	@Override
	@ParametersAreNonnullByDefault
	protected void writeAdditional(CompoundNBT compound) {
		// TODO Auto-generated method stub
		
	}

	@Nonnull
	@Override
	public IPacket<?> createSpawnPacket() {
		return NetworkHooks.getEntitySpawningPacket(this);
	}

	@Override
	public void writeSpawnData(PacketBuffer buffer) {
		new PacketSpawnEntity(this).write(buffer);	
	}

	@Override
	public void readSpawnData(PacketBuffer additionalData) {
		PacketSpawnEntity packet = new PacketSpawnEntity();
		packet.read(additionalData);
		packet.execute(this);
	}

	
}
