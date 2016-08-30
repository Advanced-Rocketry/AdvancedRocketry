package zmaster587.advancedRocketry.entity;

import java.util.Iterator;

import zmaster587.advancedRocketry.AdvancedRocketry;
import zmaster587.advancedRocketry.api.Configuration;
import zmaster587.advancedRocketry.api.IInfrastructure;
import zmaster587.advancedRocketry.api.StatsRocket;
import zmaster587.advancedRocketry.api.RocketEvent.RocketLaunchEvent;
import zmaster587.advancedRocketry.api.fuel.FuelRegistry.FuelType;
import zmaster587.advancedRocketry.api.stations.ISpaceObject;
import zmaster587.advancedRocketry.api.stations.SpaceObjectManager;
import zmaster587.advancedRocketry.dimension.DimensionManager;
import zmaster587.advancedRocketry.dimension.DimensionProperties;
import zmaster587.advancedRocketry.mission.MissionGasCollection;
import zmaster587.advancedRocketry.util.StorageChunk;
import zmaster587.libVulpes.util.BlockPosition;
import zmaster587.libVulpes.util.Vector3F;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.ForgeDirection;

public class EntityStationDeployedRocket extends EntityRocket {

	public ForgeDirection launchDirection;
	public ForgeDirection forwardDirection;
	public BlockPosition launchLocation;
	boolean coastMode;

	public EntityStationDeployedRocket(World world) {
		super(world);
		launchDirection = ForgeDirection.DOWN;
		launchLocation = new BlockPosition(0,0,0);
	}

	public EntityStationDeployedRocket(World world, StorageChunk storage, StatsRocket stats, double x, double y, double z) {
		super(world, storage, stats, x,y,z);
		launchLocation = new BlockPosition((int)x,(int)y,(int)z);
		launchDirection = ForgeDirection.DOWN;
		stats.setSeatLocation(-1, -1, -1); //No seats
	}

	//Use as a way of checking when chunk is unloaded
	@Override
	public void setDead() {
		super.setDead();
	}

	@Override
	public void launch() {

		if(isInFlight())
			return;


		if(isInOrbit()) {
			setInFlight(true);
			return;
		}

		ISpaceObject spaceObj;
		if( worldObj.provider.dimensionId == Configuration.spaceDimId && (spaceObj = SpaceObjectManager.getSpaceManager().getSpaceStationFromBlockCoords((int)posX, (int)posZ)) != null && ((DimensionProperties)spaceObj.getProperties().getParentProperties()).isGasGiant() ) { //Abort if destination is invalid


			setInFlight(true);
			launchLocation.x = (int) Math.floor(this.posX);
			launchLocation.y = (short) this.posY;
			launchLocation.z = (int) Math.floor(this.posZ);
			Iterator<IInfrastructure> connectedTiles = connectedInfrastructure.iterator();

			if(worldObj.isRemote)
				MinecraftForge.EVENT_BUS.post(new RocketLaunchEvent(this));

			//Disconnect things linked to the rocket on liftoff
			while(connectedTiles.hasNext()) {
				IInfrastructure i = connectedTiles.next();
				if(i.disconnectOnLiftOff()) {
					i.unlinkRocket();
					infrastructureCoords.remove(new BlockPosition(((TileEntity)i).xCoord, ((TileEntity)i).yCoord, ((TileEntity)i).zCoord));
					connectedTiles.remove();
				}
			}
		}
	}
	
	@Override
	public void onUpdate() {
		lastWorldTickTicked = worldObj.getTotalWorldTime();

		if(isInFlight()) {
			boolean burningFuel = isBurningFuel();

			if(launchLocation == null || storage == null)
				return;
			boolean isCoasting = Math.abs(this.posX - launchLocation.x) < 4*storage.getSizeX() && Math.abs(this.posY - launchLocation.y) < 4*storage.getSizeY() && Math.abs(this.posZ - launchLocation.z) < 4*storage.getSizeZ();

			if(!isCoasting) {
				//Burn the rocket fuel
				if(!worldObj.isRemote)
					setFuelAmount(getFuelAmount() - stats.getFuelRate(FuelType.LIQUID));

				//Spawn in the particle effects for the engines
				if(worldObj.isRemote && Minecraft.getMinecraft().gameSettings.particleSetting < 2) {
					for(Vector3F<Float> vec : stats.getEngineLocations()) {

						float xMult = Math.abs(forwardDirection.offsetX);
						float zMult = Math.abs(forwardDirection.offsetZ);
						float xVel, zVel;

						for(int i = 0; i < 4; i++) {
							xVel = (1-xMult)*((this.rand.nextFloat() - 0.5f)/8f) + xMult*-.75f;
							zVel = (1-zMult)*((this.rand.nextFloat() - 0.5f)/8f) + zMult*-.75f;

							AdvancedRocketry.proxy.spawnParticle("rocketFlame", worldObj, this.posX + vec.x, this.posY + vec.y, this.posZ +vec.z, xVel,(this.rand.nextFloat() - 0.5f)/8f, zVel);

						}
					}
				}
			}


			if(forwardDirection == null)
				return;

			//Returning
			if(isInOrbit()) { //For unmanned rockets
				ForgeDirection dir;
				isCoasting = Math.abs(this.posX - launchLocation.x - (storage.getSizeX() % 2 == 0 ? 0 : 0.5f)) < 0.01 && Math.abs(this.posZ - launchLocation.z - (storage.getSizeZ() % 2 == 0 ? 0 : 0.5f)) < .01;

				if(isCoasting) {
					dir = launchDirection.getOpposite();
					float speed = 0.075f;
					motionX = speed*dir.offsetX;
					motionY = speed*dir.offsetY;
					motionZ = speed*dir.offsetZ;
				}
				else {
					dir = forwardDirection.getOpposite();

					float acc = 0.01f;
					
					motionX = acc*(launchLocation.x - this.posX + (storage.getSizeX() % 2 == 0 ? 0 : 0.5f)) + 0.01*dir.offsetX;
					motionY = 0;//acc*(launchLocation.y - this.posY) + 0.01*dir.offsetY;
					motionZ = acc*(launchLocation.z - this.posZ + (storage.getSizeZ() % 2 == 0 ? 0 : 0.5f)) + 0.01*dir.offsetZ;
					
				}

				if(this.posY > launchLocation.y ) {
					if(!worldObj.isRemote) {
						this.setInFlight(false);
						this.setInOrbit(false);
					}
					this.motionY = 0;
					this.setPosition(launchLocation.x + (storage.getSizeX() % 2 == 0 ? 0 : 0.5f), launchLocation.y, launchLocation.z  + (storage.getSizeZ() % 2 == 0 ? 0 : 0.5f));
				}
			}
			else {
				//Move out 4x the size of the rocket
				//Coast away from the station
				if(isCoasting) {
					float speed = 0.01f;//(float)Math.min(0.2f, Math.abs(motionY) + 0.0001f);
					motionX = speed*launchDirection.offsetX * ( 2.1*storage.getSizeX() - Math.abs(2*storage.getSizeX() - Math.abs(this.posX - launchLocation.x)) + 0.05);
					motionY = speed*launchDirection.offsetY * ( 2.1*storage.getSizeY() - Math.abs(2*storage.getSizeY() - Math.abs(this.posY - launchLocation.y)) + 0.05);
					motionZ = speed*launchDirection.offsetZ * ( 2.1*storage.getSizeZ() - Math.abs(2*storage.getSizeZ() - Math.abs(this.posZ - launchLocation.z)) + 0.05);
				}
				else {
					float acc = 0.01f;
					motionX += acc*forwardDirection.offsetX;
					motionY += acc*forwardDirection.offsetY;
					motionZ += acc*forwardDirection.offsetZ;

				}

			}
			

			this.moveEntity(motionX, motionY, motionZ);
		}
	}

	/**
	 * Called when the rocket reaches orbit
	 */
	public void onOrbitReached() {
		//TODO: support multiple riders and rider/satellite combo
		//make it 30 minutes with one drill

		if(this.isDead)
			return;

		MissionGasCollection miningMission = new MissionGasCollection((long)(360), this, connectedInfrastructure);
		DimensionProperties properties = DimensionManager.getInstance().getDimensionProperties(worldObj.provider.dimensionId).getParentProperties();

		properties.addSatallite(miningMission);

		for(IInfrastructure i : connectedInfrastructure) {
			i.linkMission(miningMission);
		}

		this.setDead();
	}

	@Override
	protected void writeEntityToNBT(NBTTagCompound nbt) {
		if(!worldObj.isRemote && isInFlight() && !isInOrbit() && this.getDistance(launchLocation.x, launchLocation.y, launchLocation.z) > 64) {
			onOrbitReached();
			return;
		}
		super.writeEntityToNBT(nbt);
	}

	@Override
	public void writeToNBT(NBTTagCompound p_70109_1_) {
		if(!worldObj.isRemote && isInFlight() && !isInOrbit() && this.getDistance(launchLocation.x, launchLocation.y, launchLocation.z) > 64) {
			onOrbitReached();
			return;
		}
		super.writeToNBT(p_70109_1_);
	}

	@Override
	public boolean writeToNBTOptional(NBTTagCompound p_70039_1_) {
		if(!worldObj.isRemote && isInFlight() && !isInOrbit() && this.getDistance(launchLocation.x, launchLocation.y, launchLocation.z) > 64) {
			onOrbitReached();
			return false;
		}
		return super.writeToNBTOptional(p_70039_1_);
	}


	@Override
	public void writeMissionPersistantNBT(NBTTagCompound nbt) {
		// TODO Auto-generated method stub
		super.writeMissionPersistantNBT(nbt);
		nbt.setInteger("fwd", forwardDirection.ordinal());

		nbt.setInteger("launchX", launchLocation.x);
		nbt.setInteger("launchY", launchLocation.y);
		nbt.setInteger("launchZ", launchLocation.z);
	}

	@Override
	public void readMissionPersistantNBT(NBTTagCompound nbt) {
		super.readMissionPersistantNBT(nbt);
		forwardDirection = ForgeDirection.values()[nbt.getInteger("fwd")];

		launchLocation.x = nbt.getInteger("launchX");
		launchLocation.y = (short)nbt.getInteger("launchY");
		launchLocation.z = nbt.getInteger("launchZ");
	}
}
