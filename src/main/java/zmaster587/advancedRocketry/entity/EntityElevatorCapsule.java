package zmaster587.advancedRocketry.entity;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.MoverType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.IPacket;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.network.play.server.SSpawnObjectPacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.Teleporter;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.ITeleporter;
import zmaster587.advancedRocketry.AdvancedRocketry;
import zmaster587.advancedRocketry.api.ARConfiguration;
import zmaster587.advancedRocketry.api.AdvancedRocketryEntities;
import zmaster587.advancedRocketry.api.Constants;
import zmaster587.advancedRocketry.api.RocketEvent;
import zmaster587.advancedRocketry.event.PlanetEventHandler;
import zmaster587.advancedRocketry.tile.multiblock.TileSpaceElevator;
import zmaster587.advancedRocketry.util.DimensionBlockPosition;
import zmaster587.advancedRocketry.util.TransitionEntity;
import zmaster587.advancedRocketry.world.util.TeleporterNoPortal;
import zmaster587.libVulpes.LibVulpes;
import zmaster587.libVulpes.interfaces.INetworkEntity;
import zmaster587.libVulpes.network.PacketEntity;
import zmaster587.libVulpes.network.PacketHandler;
import zmaster587.libVulpes.util.HashedBlockPosition;
import zmaster587.libVulpes.util.ZUtils;

import javax.annotation.Nullable;
import java.util.List;

public class EntityElevatorCapsule extends Entity implements INetworkEntity {

	public static final double MAX_HEIGHT = ARConfiguration.getCurrentConfig().orbit.get();
	public static final double MAX_STANDTIME = 200;
	byte motion;
	int standTime, idleTime;
	DimensionBlockPosition dstTilePos, srcTilePos;

	private static final byte PACKET_WRITE_DST_INFO = 0;
	private static final byte PACKET_RECIEVE_NBT = 1;
	private static final byte PACKET_LAUNCH_EVENT = 2;
	private static final byte PACKET_DEORBIT = 3;
	private static final byte PACKET_WRITE_SRC_INFO = 4;


	protected static final DataParameter<Byte> motionDir =  EntityDataManager.<Byte>createKey(EntityElevatorCapsule.class, DataSerializers.BYTE);
	protected static final DataParameter<Integer> standTimeCounter =  EntityDataManager.<Integer>createKey(EntityElevatorCapsule.class, DataSerializers.VARINT);

	public EntityElevatorCapsule( World worldIn) {
		super(AdvancedRocketryEntities.ENTITY_ELEVATOR_CAPSULE, worldIn);
		motion = 0;
		ignoreFrustumCheck = true;
	}
	
	public EntityElevatorCapsule(EntityType<? extends EntityElevatorCapsule> type,  World worldIn) {
		this(worldIn);
	}
	
	@Override
	protected void registerData() {
		this.dataManager.register(motionDir, motion);
		this.dataManager.register(standTimeCounter, standTime);
	}

	public boolean isAscending() {
		return dataManager.get(motionDir) > 0;
	}

	public boolean isDescending() {
		return dataManager.get(motionDir) < 0;
	}

	public boolean isInMotion() {
		return dataManager.get(motionDir) != 0;
	}

	public void setCapsuleMotion(int motion) {
		this.dataManager.set(motionDir, (byte)motion);
		this.motion = (byte)motion;
	}

	public int getStandTime() {
		return (standTime = this.dataManager.get(standTimeCounter));
	}

	public void setStandTime(int time) {
		this.dataManager.set(standTimeCounter, standTime);
	}

	public int decrStandTime() {

		this.dataManager.set(standTimeCounter, (standTime = getStandTime()-1));
		return standTime;
	}
	
	@Override
	protected void readAdditional(CompoundNBT nbt) {
		setCapsuleMotion(nbt.getByte("motionDir"));

		if(nbt.contains("dstDimid")) {
			dstTilePos = new DimensionBlockPosition(Constants.INVALID_PLANET, null);
			dstTilePos.dimid = new ResourceLocation(nbt.getString("dstDimid"));
			int[] loc = nbt.getIntArray("dstLoc");
			dstTilePos.pos = new HashedBlockPosition(loc[0], loc[1], loc[2]);
		}
		else 
			dstTilePos = null;

		if(nbt.contains("srcDimid")) {
			srcTilePos = new DimensionBlockPosition(Constants.INVALID_PLANET, null);
			srcTilePos.dimid = new ResourceLocation(nbt.getString("srcDimid"));
			int[] loc = nbt.getIntArray("srcLoc");
			srcTilePos.pos = new HashedBlockPosition(loc[0], loc[1], loc[2]);
		}
		else 
			srcTilePos = null;
	}

	@Override
	public void setEntityId(int id){
		super.setEntityId(id);
		//Ask server for nbt data
		if(world.isRemote) {
			PacketHandler.sendToServer(new PacketEntity(this, PACKET_RECIEVE_NBT));
		}
	}

	@Override
	protected void writeAdditional(CompoundNBT nbt) {
		nbt.putByte("motionDir", motion);
		if(dstTilePos != null) {
			nbt.putString("dstDimid", dstTilePos.dimid.toString());
			nbt.putIntArray("dstLoc", new int[] { dstTilePos.pos.x, dstTilePos.pos.y, dstTilePos.pos.z });
		}

		if(srcTilePos != null) {
			nbt.putString("srcDimid", srcTilePos.dimid.toString());
			nbt.putIntArray("srcLoc", new int[] { srcTilePos.pos.x, srcTilePos.pos.y, srcTilePos.pos.z });
		}
	}

	public boolean shouldRiderSit()
	{
		return false;
	}

	public void setDst(DimensionBlockPosition location) {
		this.dstTilePos = location;
		if(!world.isRemote)
			PacketHandler.sendToPlayersTrackingEntity(new PacketEntity(this, PACKET_WRITE_DST_INFO), this);
	}

	public void setSourceTile(DimensionBlockPosition location) {
		this.srcTilePos = location;
		if(!world.isRemote)
			PacketHandler.sendToPlayersTrackingEntity(new PacketEntity(this, PACKET_WRITE_SRC_INFO), this);
	}

	public Entity changeDimension(ServerWorld newDimId) {
		return changeDimension(newDimId, this.getPosX(), (double)ARConfiguration.getCurrentConfig().orbit.get(), this.getPosZ());
	}

	@Override
	public void copyDataFromOld(Entity entityIn)
	{
		super.copyDataFromOld(entityIn);
		this.getPassengers().clear();
	}

	@Override
	public Entity changeDimension(ServerWorld world, ITeleporter teleporter) {
		return super.changeDimension(world, teleporter);
	}
	
	@Nullable
	public Entity changeDimension(ServerWorld dimensionIn, double posX, double y, double posZ)
	{
		if (!this.world.isRemote && this.isAlive())
		{
			float yaw = this.rotationYaw;
			float pitch = this.rotationPitch;

			List<Entity> passengers = getPassengers();
			MinecraftServer minecraftserver = this.getServer();
			ServerWorld worldserver =  (ServerWorld) this.getEntityWorld();
			ServerWorld worldserver1 = dimensionIn;
			this.setPosition(posX, y, posZ);
			
			Teleporter teleporter = new TeleporterNoPortal(worldserver1);
			Entity entity = changeDimension(dimensionIn, teleporter);

			if(entity == null)
				return null;
			
			entity.setPositionAndRotation(posX, y, posZ, yaw, pitch);
			
			int timeOffset = 1;
			for(Entity e : passengers) {
				PlanetEventHandler.addDelayedTransition(new TransitionEntity(worldserver.getGameTime() + ++timeOffset, e, dimensionIn, new BlockPos(posX, y, posZ), entity));
			}
			return entity;
		}
		return null;
	}

	@Override
	public AxisAlignedBB getRenderBoundingBox() {
		return getBoundingBox().grow(getPosX(), 2000, getPosZ());
	}
	
	@Override
	public void tick() {
		// TODO Auto-generated method stub
		super.tick();

		//Make sure to update client
		if(!world.isRemote && this.ticksExisted == 5) {
			if(dstTilePos != null)
				setDst(dstTilePos);
			
			if(srcTilePos != null)
				setSourceTile(srcTilePos);
		}
		
		if(isAscending()) {

			if(this.getPosY() > 255)
				this.setMotion( new Vector3d(getMotion().x, 2.85, getMotion().z) );
			else
				this.setMotion( new Vector3d(getMotion().x, 0.85, getMotion().z) );

			if(!world.isRemote) {
				List<PlayerEntity> list = world.getEntitiesWithinAABB(PlayerEntity.class, getBoundingBox());
				for(Entity ent : list) {
					if(this.getRidingEntity() == null)
						ent.startRiding(this);
				}

				if(this.getPosY() > MAX_HEIGHT) {
					setCapsuleMotion(-1);
					double landingLocX, landingLocZ;
					World world = ZUtils.getWorld(dstTilePos.dimid);

					if(world != null) {
						TileEntity tile = world.getTileEntity(dstTilePos.pos.getBlockPos());

						if(tile instanceof TileSpaceElevator) {
							landingLocX = ((TileSpaceElevator)tile).getLandingLocationX();
							landingLocZ = ((TileSpaceElevator)tile).getLandingLocationZ();
						}
						else {
							remove();
							return;
						}
					}
					else {
						dstTilePos = srcTilePos;
						world = this.getEntityWorld();

						TileEntity tile = world.getTileEntity(dstTilePos.pos.getBlockPos());

						if(tile instanceof TileSpaceElevator) {
							landingLocX = ((TileSpaceElevator)tile).getLandingLocationX();
							landingLocZ = ((TileSpaceElevator)tile).getLandingLocationZ();
						}
						else {
							remove();
							return;
						}
					}

					changeDimension(ZUtils.getWorld(dstTilePos.dimid), landingLocX, ARConfiguration.getCurrentConfig().orbit.get(), landingLocZ);

					MinecraftForge.EVENT_BUS.post(new RocketEvent.RocketDeOrbitingEvent(this));
				}
			}

			this.move(MoverType.SELF, this.getMotion());
		}
		else if(isDescending()) {
			
			this.onGround = false;
			
			if(this.getPosY() > 255)
				this.setMotion( new Vector3d(getMotion().x, -2.85, getMotion().z) );
			else
				this.setMotion( new Vector3d(getMotion().x, -0.85, getMotion().z) );

			if(!world.isRemote) {

				//Send packet to player for deorbit a bit delayed
				if(this.ticksExisted == 20)
					PacketHandler.sendToPlayersTrackingEntity(new PacketEntity(this, PACKET_DEORBIT), this);

				List<PlayerEntity> list = world.getEntitiesWithinAABB(PlayerEntity.class, getBoundingBox());
				for(Entity ent : list) {
					if(this.getRidingEntity() == null)
						ent.startRiding(this);
				}

				if(this.getPosY() <= dstTilePos.pos.y) {
					setCapsuleMotion(0);


					setPosition(dstTilePos.pos.x, dstTilePos.pos.y, dstTilePos.pos.z);

					TileEntity e;

					if((e = world.getTileEntity(dstTilePos.pos.getBlockPos())) instanceof TileSpaceElevator) {
						((TileSpaceElevator)e).notifyLanded(this);
						standTime = 0;
					}
					else
						this.remove();

					//Dismount rider after being put in final place
					for(Entity ent : this.getPassengers()) {
						ent.stopRiding();
					}
				}
				else
					this.move(MoverType.SELF, getMotion());
			}
			else
				this.move(MoverType.SELF, getMotion());
		}
		else {
			List<PlayerEntity> list = world.getEntitiesWithinAABB(PlayerEntity.class, getBoundingBox());

			if(!world.isRemote) {
				
				TileEntity srcTile = null;
				if(list.isEmpty())
					standTime = 0;
				else if(dstTilePos != null && dstTilePos.dimid != ZUtils.getDimensionIdentifier(world) && TileSpaceElevator.isDstValid(getEntityWorld(), dstTilePos, new HashedBlockPosition(getPositionVec())))
					standTime++;

				if(srcTilePos != null && srcTilePos.pos != null)
					srcTile = world.getTileEntity(srcTilePos.pos.getBlockPos());
				
				
				if( srcTile != null && srcTile instanceof TileSpaceElevator && !((TileSpaceElevator)srcTile).getMachineEnabled())
					standTime = 0;
				
				setStandTime(standTime);

				//Begin ascending
				if(standTime > MAX_STANDTIME) {

					if(srcTilePos != null && srcTilePos.pos != null) {
						srcTile = world.getTileEntity(srcTilePos.pos.getBlockPos());

						if(srcTile instanceof TileSpaceElevator && ((TileSpaceElevator)srcTile).attemptLaunch()) {

							setCapsuleMotion(1);
							//Make sure we mount player before takeoff
							List<PlayerEntity> list2 = world.getEntitiesWithinAABB(PlayerEntity.class, getBoundingBox());

							for(Entity ent : list2) {
								if(this.getRidingEntity() == null)
									ent.startRiding(this);
							}
							MinecraftForge.EVENT_BUS.post(new RocketEvent.RocketLaunchEvent(this));
							PacketHandler.sendToPlayersTrackingEntity(new PacketEntity(this, PACKET_LAUNCH_EVENT), this);
						}
					}
				}
			}
			else if(!list.isEmpty()) {
				TileEntity srcTile = null;
				if(srcTilePos != null && srcTilePos.pos != null)
					srcTile = world.getTileEntity(srcTilePos.pos.getBlockPos());
				
				
				if( srcTile != null && srcTile instanceof TileSpaceElevator && !((TileSpaceElevator)srcTile).getMachineEnabled())
					AdvancedRocketry.proxy.displayMessage(LibVulpes.proxy.getLocalizedString("msg.spaceElevator.turnedOff"),5);
				else if(dstTilePos != null) 
					AdvancedRocketry.proxy.displayMessage(LibVulpes.proxy.getLocalizedString("msg.spaceElevator.ascentReady") + ": " + (int)((MAX_STANDTIME - getStandTime())/20) + "\nDST " + dstTilePos,5);
				else 
					AdvancedRocketry.proxy.displayMessage(LibVulpes.proxy.getLocalizedString("msg.label.noneSelected"), 5);
			}			
		}

		//remove();
	}

	@Override
	public double getMountedYOffset() {
		return 0.3;
	}

	@Override
	public AxisAlignedBB getBoundingBox() {
		AxisAlignedBB aabb = new AxisAlignedBB(super.getBoundingBox().minX, super.getBoundingBox().minY, super.getBoundingBox().minZ, super.getBoundingBox().maxX, super.getBoundingBox().maxY-3, super.getBoundingBox().maxZ);
		return isAscending() || isDescending() ? null : aabb;
	}
	

	@OnlyIn(value=Dist.CLIENT)
	@Override
	public boolean isInRangeToRenderDist(double par1)
	{
		//double d1 = this.boundingBox.getAverageEdgeLength();
		//d1 *= 4096.0D * this.renderDistanceWeight;
		return par1 < 16777216D;
	}

	@Override
	public void writeDataToNetwork(PacketBuffer out, byte id) {
		if(id == PACKET_WRITE_DST_INFO) {
			out.writeBoolean(dstTilePos != null);

			if(dstTilePos != null) {
				out.writeResourceLocation(dstTilePos.dimid);
				out.writeInt(dstTilePos.pos.x);
				out.writeInt(dstTilePos.pos.y);
				out.writeInt(dstTilePos.pos.z);
			}
		}
		else if(id == PACKET_WRITE_SRC_INFO) {
			out.writeBoolean(dstTilePos != null);

			if(srcTilePos != null) {
				out.writeResourceLocation(srcTilePos.dimid);
				out.writeInt(srcTilePos.pos.x);
				out.writeInt(srcTilePos.pos.y);
				out.writeInt(srcTilePos.pos.z);
			}
		}
	}

	@Override
	public void readDataFromNetwork(PacketBuffer in, byte packetId,
			CompoundNBT nbt) {

		if(packetId == PACKET_WRITE_DST_INFO || packetId == PACKET_WRITE_SRC_INFO) {
			if(in.readBoolean()) {
				nbt.putString("dimid", in.readResourceLocation().toString());
				nbt.putInt("x", in.readInt());
				nbt.putInt("y", in.readInt());
				nbt.putInt("z", in.readInt());
			}
		}
	}

	@Override
	public void useNetworkData(PlayerEntity player, Dist side, byte id,
			CompoundNBT nbt) {
		if(id == PACKET_WRITE_DST_INFO && world.isRemote) {
			if(nbt.contains("dimid")) {
				dstTilePos = new DimensionBlockPosition(new ResourceLocation(nbt.getString("dimid")), new HashedBlockPosition(nbt.getInt("x"), nbt.getInt("y"), nbt.getInt("z")));
			}
			else dstTilePos = null;
		}
		else if(id == PACKET_WRITE_SRC_INFO && world.isRemote) {
			if(nbt.contains("dimid")) {
				srcTilePos = new DimensionBlockPosition(new ResourceLocation(nbt.getString("dimid")), new HashedBlockPosition(nbt.getInt("x"), nbt.getInt("y"), nbt.getInt("z")));
			}
			else srcTilePos = null;
		}
		else if(id == PACKET_RECIEVE_NBT) {
			PacketHandler.sendToPlayersTrackingEntity(new PacketEntity(this, PACKET_WRITE_DST_INFO), this);
		}
		else if(id == PACKET_LAUNCH_EVENT && world.isRemote) {
			List<PlayerEntity> list = world.getEntitiesWithinAABB(PlayerEntity.class, getBoundingBox());
			for(Entity ent : list) {
				if(this.getRidingEntity() == null)
					ent.startRiding(this);
			}

			MinecraftForge.EVENT_BUS.post(new RocketEvent.RocketLaunchEvent(this));
		}
		else if(id == PACKET_DEORBIT && world.isRemote) {
			MinecraftForge.EVENT_BUS.post(new RocketEvent.RocketDeOrbitingEvent(this));
		}
	}

	@Override
	public IPacket<?> createSpawnPacket() {
		return new SSpawnObjectPacket(this);
	}
}
