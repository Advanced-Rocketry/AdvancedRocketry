package zmaster587.advancedRocketry.entity;

import io.netty.buffer.ByteBuf;

import java.util.List;

import javax.annotation.Nullable;

import com.google.common.base.Predicate;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import zmaster587.advancedRocketry.AdvancedRocketry;
import zmaster587.advancedRocketry.api.Configuration;
import zmaster587.advancedRocketry.api.RocketEvent;
import zmaster587.advancedRocketry.entity.EntityRocket.PacketType;
import zmaster587.advancedRocketry.event.PlanetEventHandler;
import zmaster587.advancedRocketry.tile.multiblock.TileSpaceElevator;
import zmaster587.advancedRocketry.util.DimensionBlockPosition;
import zmaster587.advancedRocketry.util.TransitionEntity;
import zmaster587.advancedRocketry.world.util.TeleporterNoPortal;
import zmaster587.libVulpes.LibVulpes;
import zmaster587.libVulpes.interfaces.INetworkEntity;
import zmaster587.libVulpes.network.PacketEntity;
import zmaster587.libVulpes.network.PacketHandler;
import zmaster587.libVulpes.util.BlockPosition;
import zmaster587.libVulpes.util.BlockPosition;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.Teleporter;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.common.MinecraftForge;

public class EntityElevatorCapsule extends Entity implements INetworkEntity {

	public static final double MAX_HEIGHT = Configuration.orbit;
	public static final double MAX_STANDTIME = 200;
	byte motion;
	int standTime, idleTime;
	DimensionBlockPosition dstTilePos, srcTilePos;

	private static final byte PACKET_WRITE_DST_INFO = 0;
	private static final byte PACKET_RECIEVE_NBT = 1;
	private static final byte PACKET_LAUNCH_EVENT = 2;
	private static final byte PACKET_DEORBIT = 3;
	private static final byte PACKET_WRITE_SRC_INFO = 4;


	protected static final int motionDir =  2;
	protected static final int standTimeCounter = 3;

	public EntityElevatorCapsule(World worldIn) {
		super(worldIn);
		setSize(3, 3);
		motion = 0;
		ignoreFrustumCheck = true;
	}

	public boolean isAscending() {
		return dataWatcher.getWatchableObjectByte(motionDir) > 0;
	}

	public boolean isDescending() {
		return dataWatcher.getWatchableObjectByte(motionDir) < 0;
	}

	public boolean isInMotion() {
		return dataWatcher.getWatchableObjectByte(motionDir) != 0;
	}

	public void setCapsuleMotion(int motion) {
		this.dataWatcher.updateObject(motionDir, (byte)motion);
		this.motion = (byte)motion;
	}

	public int getStandTime() {
		return (standTime = this.dataWatcher.getWatchableObjectInt(standTimeCounter));
	}

	public void setStandTime(int time) {
		this.dataWatcher.updateObject(standTimeCounter, standTime);
	}

	public int decrStandTime() {

		this.dataWatcher.updateObject(standTimeCounter, (standTime = getStandTime()-1));
		return standTime;
	}

	@Override
	protected void entityInit() {
		this.dataWatcher.addObject(motionDir, motion);
		this.dataWatcher.addObject(standTimeCounter, standTime);
	}

	@Override
	protected void readEntityFromNBT(NBTTagCompound nbt) {
		setCapsuleMotion(nbt.getByte("motionDir"));

		if(nbt.hasKey("dstDimid")) {
			dstTilePos = new DimensionBlockPosition(-1, null);
			dstTilePos.dimid = nbt.getInteger("dstDimid");
			int[] loc = nbt.getIntArray("dstLoc");
			dstTilePos.pos = new BlockPosition(loc[0], loc[1], loc[2]);
		}
		else 
			dstTilePos = null;

		if(nbt.hasKey("srcDimid")) {
			srcTilePos = new DimensionBlockPosition(-1, null);
			srcTilePos.dimid = nbt.getInteger("srcDimid");
			int[] loc = nbt.getIntArray("srcLoc");
			srcTilePos.pos = new BlockPosition(loc[0], loc[1], loc[2]);
		}
		else 
			srcTilePos = null;
	}

	@Override
	public void setEntityId(int id){
		super.setEntityId(id);
		//Ask server for nbt data
		if(worldObj.isRemote) {
			PacketHandler.sendToServer(new PacketEntity(this, PACKET_RECIEVE_NBT));
		}
	}

	@Override
	protected void writeEntityToNBT(NBTTagCompound nbt) {
		nbt.setByte("motionDir", motion);
		if(dstTilePos != null) {
			nbt.setInteger("dstDimid", dstTilePos.dimid);
			nbt.setIntArray("dstLoc", new int[] { dstTilePos.pos.x, dstTilePos.pos.y, dstTilePos.pos.z });
		}

		if(srcTilePos != null) {
			nbt.setInteger("srcDimid", srcTilePos.dimid);
			nbt.setIntArray("srcLoc", new int[] { srcTilePos.pos.x, srcTilePos.pos.y, srcTilePos.pos.z });
		}
	}

	public boolean shouldRiderSit()
	{
		return false;
	}

	public void setDst(DimensionBlockPosition location) {
		this.dstTilePos = location;
		if(!worldObj.isRemote)
			PacketHandler.sendToPlayersTrackingEntity(new PacketEntity(this, PACKET_WRITE_DST_INFO), this);
	}

	public void setSourceTile(DimensionBlockPosition location) {
		this.srcTilePos = location;
		if(!worldObj.isRemote)
			PacketHandler.sendToPlayersTrackingEntity(new PacketEntity(this, PACKET_WRITE_SRC_INFO), this);
	}
	
	@Override
	public void travelToDimension(int newDimId) {
		travelToDimension(newDimId, this.posX, Configuration.orbit, this.posZ);
	}

	public void travelToDimension(int newDimId, double posX, double y, double posZ)
	{
		if (!this.worldObj.isRemote && !this.isDead)
		{
			
			double x = posX, z = posZ;

			Entity rider = this.riddenByEntity;
			if(rider != null)
				rider.mountEntity(null);

			this.worldObj.theProfiler.startSection("changeDimension");
			MinecraftServer minecraftserver = MinecraftServer.getServer();
			int j = this.dimension;
			WorldServer worldserver = minecraftserver.worldServerForDimension(j);
			WorldServer worldserver1 = minecraftserver.worldServerForDimension(newDimId);
			this.dimension = newDimId;

			//this.worldObj.removeEntity(this);
			this.isDead = false;
			this.worldObj.theProfiler.startSection("reposition");

			//transfer the rocket to the other dim without creating a nether portal
			minecraftserver.getConfigurationManager().transferEntityToWorld(this, j, worldserver, worldserver1,new TeleporterNoPortal(worldserver1));
			this.worldObj.theProfiler.endStartSection("reloading");
			Entity entity = EntityList.createEntityByName(EntityList.getEntityString(this), worldserver1);


			if (entity != null)
			{
				entity.copyDataFrom(this, true);

				entity.forceSpawn = true;

				entity.setLocationAndAngles(x, y, z, this.rotationYaw, this.rotationPitch);
				worldserver1.spawnEntityInWorld(entity);
				//worldserver1.updateEntityWithOptionalForce(entity, true);

				if(rider != null) {
					//Fix that darn random crash?
					//worldserver.resetUpdateEntityTick();
					//worldserver1.resetUpdateEntityTick();
					//Transfer the player if applicable

					PlanetEventHandler.addDelayedTransition(worldserver.getTotalWorldTime() + 1, new TransitionEntity(worldserver.getTotalWorldTime() + 1, rider, dimension, new BlockPosition((int)posX, Configuration.orbit, (int)posZ), entity));

					//minecraftserver.getConfigurationManager().transferPlayerToDimension((EntityPlayerMP)rider, newDimId, new TeleporterNoPortal(worldserver1));

					//rider.setLocationAndAngles(x, Configuration.orbit, z, this.rotationYaw, this.rotationPitch);
					//rider.mountEntity(entity);

				}
			}

			setDead();

			this.worldObj.theProfiler.endSection();
			worldserver.resetUpdateEntityTick();
			worldserver1.resetUpdateEntityTick();
			this.worldObj.theProfiler.endSection();
		}
	}



	@Override
	public void onEntityUpdate() {
		// TODO Auto-generated method stub
		super.onEntityUpdate();
		ignoreFrustumCheck = true;

		//Make sure to update client
		if(!worldObj.isRemote && this.ticksExisted == 5) {
			if(dstTilePos != null)
				setDst(dstTilePos);
			
			if(srcTilePos != null)
				setSourceTile(srcTilePos);
		}
		
		if(isAscending()) {

			if(this.posY > 255)
				this.motionY = 2.85;
			else
				this.motionY = 0.85;

			if(!worldObj.isRemote) {
				List<EntityPlayer> list = worldObj.getEntitiesWithinAABB(EntityPlayer.class, getBoundingBox().offset(0, 3, 0).expand(0, 1.5, 0));
				for(Entity ent : list) {
					if(this.riddenByEntity == null)
						ent.mountEntity(this);
				}

				if(this.posY > MAX_HEIGHT) {
					setCapsuleMotion(-1);
					double landingLocX, landingLocZ;
					World world;

					if((world = DimensionManager.getWorld(dstTilePos.dimid)) == null) {
						DimensionManager.initDimension(dstTilePos.dimid);
						world = DimensionManager.getWorld(dstTilePos.dimid);
					}

					if(world != null) {
						TileEntity tile = world.getTileEntity(dstTilePos.pos.x, dstTilePos.pos.y, dstTilePos.pos.z);

						if(tile instanceof TileSpaceElevator) {
							landingLocX = ((TileSpaceElevator)tile).getLandingLocationX();
							landingLocZ = ((TileSpaceElevator)tile).getLandingLocationZ();
						}
						else {
							setDead();
							return;
						}
					}
					else {
						dstTilePos = srcTilePos;
						world = this.worldObj;

						TileEntity tile = world.getTileEntity(dstTilePos.pos.x, dstTilePos.pos.y, dstTilePos.pos.z);

						if(tile instanceof TileSpaceElevator) {
							landingLocX = ((TileSpaceElevator)tile).getLandingLocationX();
							landingLocZ = ((TileSpaceElevator)tile).getLandingLocationZ();
						}
						else {
							setDead();
							return;
						}
					}

					travelToDimension(dstTilePos.dimid, landingLocX, Configuration.orbit, landingLocZ);

					MinecraftForge.EVENT_BUS.post(new RocketEvent.RocketDeOrbitingEvent(this));
				}
			}

			this.moveEntity(0, this.motionY, 0);
		}
		else if(isDescending()) {



			if(this.posY > 255)
				this.motionY = -2.85;
			else
				this.motionY = -0.85;

			if(!worldObj.isRemote) {

				//Send packet to player for deorbit a bit delayed
				if(this.ticksExisted == 20)
					PacketHandler.sendToPlayersTrackingEntity(new PacketEntity(this, PACKET_DEORBIT), this);

				List<EntityPlayer> list = worldObj.getEntitiesWithinAABB(EntityPlayer.class, getBoundingBox().offset(0, 3, 0).expand(0, 1.5, 0));
				for(Entity ent : list) {
					if(this.riddenByEntity == null)
						ent.mountEntity(this);
				}

				if(this.posY <= dstTilePos.pos.y) {
					setCapsuleMotion(0);


					setPosition(dstTilePos.pos.x, dstTilePos.pos.y, dstTilePos.pos.z);

					TileEntity e;

					if((e = worldObj.getTileEntity(dstTilePos.pos.x, dstTilePos.pos.y, dstTilePos.pos.z)) instanceof TileSpaceElevator) {
						((TileSpaceElevator)e).notifyLanded(this);
						standTime = 0;
					}
					else
						this.setDead();

					//Dismount rider after being put in final place
					if(this.riddenByEntity != null)
						this.riddenByEntity.mountEntity(null);
				}
				else
					this.moveEntity(0, this.motionY, 0);
			}
			else
				this.moveEntity(0, this.motionY, 0);
		}
		else {
			List<EntityPlayer> list = worldObj.getEntitiesWithinAABB(EntityPlayer.class, getBoundingBox().offset(0, 3, 0).expand(0, 1.5, 0));

			if(!worldObj.isRemote) {
				
				TileEntity srcTile = null;
				
				if(list.isEmpty())
					standTime = 0;
				else if(dstTilePos != null && dstTilePos.dimid != worldObj.provider.dimensionId && TileSpaceElevator.isDstValid(worldObj, dstTilePos, new BlockPosition((int)this.posX, (int)this.posY, (int)this.posZ)))
					standTime++;

				if(srcTilePos != null && srcTilePos.pos != null)
					srcTile = worldObj.getTileEntity(srcTilePos.pos.x,srcTilePos.pos.y, srcTilePos.pos.z);
				
				
				if( srcTile != null && srcTile instanceof TileSpaceElevator && !((TileSpaceElevator)srcTile).getMachineEnabled())
					standTime = 0;
				
				setStandTime(standTime);

				//Begin ascending
				if(standTime > MAX_STANDTIME) {

					if(srcTilePos != null && srcTilePos.pos != null) {
						srcTile = worldObj.getTileEntity(srcTilePos.pos.x,srcTilePos.pos.y, srcTilePos.pos.z);

						if(srcTile instanceof TileSpaceElevator && ((TileSpaceElevator)srcTile).attemptLaunch()) {

							setCapsuleMotion(1);
							//Make sure we mount player before takeoff
							List<EntityPlayer> list2 = worldObj.getEntitiesWithinAABB(EntityPlayer.class, getBoundingBox().offset(0, 3, 0).expand(0, 1.5, 0));

							for(Entity ent : list2) {
								if(this.riddenByEntity == null)
									ent.mountEntity(this);
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
					srcTile = worldObj.getTileEntity(srcTilePos.pos.x,srcTilePos.pos.y, srcTilePos.pos.z);
				
				
				if( srcTile != null && srcTile instanceof TileSpaceElevator && !((TileSpaceElevator)srcTile).getMachineEnabled())
					AdvancedRocketry.proxy.displayMessage(LibVulpes.proxy.getLocalizedString("msg.spaceElevator.turnedOff"),5);
				else if(dstTilePos != null) 
					AdvancedRocketry.proxy.displayMessage(LibVulpes.proxy.getLocalizedString("msg.spaceElevator.ascentReady") + ": " + (int)((MAX_STANDTIME - getStandTime())/20) + "\nDST " + dstTilePos,5);
				else 
					AdvancedRocketry.proxy.displayMessage(LibVulpes.proxy.getLocalizedString("msg.label.noneSelected"), 5);
			}			
		}

		//setDead();
	}

	@Override
	public double getMountedYOffset() {
		return 0.3;
	}
	
	@Override
	public AxisAlignedBB getBoundingBox() {
		return AxisAlignedBB.getBoundingBox(boundingBox.minX, boundingBox.minY-1, boundingBox.minZ, boundingBox.maxX, boundingBox.maxY-3, boundingBox.maxZ);//boundingBox;//super.getBoundingBox();//new AxisAlignedBB(-10,-20,-10, 10,10,10);
	}

	@Override
	public AxisAlignedBB getCollisionBox(Entity e) {
		//AxisAlignedBB aabb = AxisAlignedBB.getBoundingBox(getBoundingBox().minX, getBoundingBox().minY, getBoundingBox().minZ, getBoundingBox().maxX, getBoundingBox().maxY-3, getBoundingBox().maxZ);
		return null;
	}

	@SideOnly(Side.CLIENT)
	public boolean isInRangeToRenderDist(double par1)
	{
		//double d1 = this.boundingBox.getAverageEdgeLength();
		//d1 *= 4096.0D * this.renderDistanceWeight;
		return par1 < 16777216D;
	}

	@Override
	public void writeDataToNetwork(ByteBuf out, byte id) {
		if(id == PACKET_WRITE_DST_INFO) {
			out.writeBoolean(dstTilePos != null);

			if(dstTilePos != null) {
				out.writeInt(dstTilePos.dimid);
				out.writeInt(dstTilePos.pos.x);
				out.writeInt(dstTilePos.pos.y);
				out.writeInt(dstTilePos.pos.z);
			}
		}
		else if(id == PACKET_WRITE_SRC_INFO) {
			out.writeBoolean(dstTilePos != null);

			if(srcTilePos != null) {
				out.writeInt(srcTilePos.dimid);
				out.writeInt(srcTilePos.pos.x);
				out.writeInt(srcTilePos.pos.y);
				out.writeInt(srcTilePos.pos.z);
			}
		}
	}

	@Override
	public void readDataFromNetwork(ByteBuf in, byte packetId,
			NBTTagCompound nbt) {

		if(packetId == PACKET_WRITE_DST_INFO || packetId == PACKET_WRITE_SRC_INFO) {
			if(in.readBoolean()) {
				nbt.setInteger("dimid", in.readInt());
				nbt.setInteger("x", in.readInt());
				nbt.setInteger("y", in.readInt());
				nbt.setInteger("z", in.readInt());
			}
		}
	}

	@Override
	public void useNetworkData(EntityPlayer player, Side side, byte id,
			NBTTagCompound nbt) {
		if(id == PACKET_WRITE_DST_INFO && worldObj.isRemote) {
			if(nbt.hasKey("dimid")) {
				dstTilePos = new DimensionBlockPosition(nbt.getInteger("dimid"), new BlockPosition(nbt.getInteger("x"), nbt.getInteger("y"), nbt.getInteger("z")));
			}
			else dstTilePos = null;
		}
		else if(id == PACKET_WRITE_SRC_INFO && worldObj.isRemote) {
			if(nbt.hasKey("dimid")) {
				srcTilePos = new DimensionBlockPosition(nbt.getInteger("dimid"), new BlockPosition(nbt.getInteger("x"), nbt.getInteger("y"), nbt.getInteger("z")));
			}
			else srcTilePos = null;
		}
		else if(id == PACKET_RECIEVE_NBT) {
			PacketHandler.sendToPlayersTrackingEntity(new PacketEntity(this, PACKET_WRITE_DST_INFO), this);
		}
		else if(id == PACKET_LAUNCH_EVENT && worldObj.isRemote) {
			List<EntityPlayer> list = worldObj.getEntitiesWithinAABB(EntityPlayer.class, getBoundingBox());
			for(Entity ent : list) {
				if(this.riddenByEntity == null)
					ent.mountEntity(this);
			}

			MinecraftForge.EVENT_BUS.post(new RocketEvent.RocketLaunchEvent(this));
		}
		else if(id == PACKET_DEORBIT && worldObj.isRemote) {
			MinecraftForge.EVENT_BUS.post(new RocketEvent.RocketDeOrbitingEvent(this));
		}
	}
}
