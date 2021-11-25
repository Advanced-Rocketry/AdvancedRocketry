package zmaster587.advancedRocketry.entity;

import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.ParticleStatus;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.MoverType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.IPacket;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraft.world.server.Ticket;
import net.minecraft.world.server.TicketType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.network.NetworkHooks;
import zmaster587.advancedRocketry.AdvancedRocketry;
import zmaster587.advancedRocketry.api.*;
import zmaster587.advancedRocketry.api.RocketEvent.RocketLaunchEvent;
import zmaster587.advancedRocketry.api.RocketEvent.RocketPreLaunchEvent;
import zmaster587.advancedRocketry.api.stations.ISpaceObject;
import zmaster587.advancedRocketry.client.SoundRocketEngine;
import zmaster587.advancedRocketry.dimension.DimensionManager;
import zmaster587.advancedRocketry.dimension.DimensionProperties;
import zmaster587.advancedRocketry.mission.MissionGasCollection;
import zmaster587.advancedRocketry.network.PacketSatellite;
import zmaster587.advancedRocketry.stations.SpaceObjectManager;
import zmaster587.advancedRocketry.util.AudioRegistry;
import zmaster587.advancedRocketry.util.StorageChunk;
import zmaster587.libVulpes.LibVulpes;
import zmaster587.libVulpes.inventory.modules.ModuleBase;
import zmaster587.libVulpes.inventory.modules.ModuleButton;
import zmaster587.libVulpes.inventory.modules.ModuleText;
import zmaster587.libVulpes.network.PacketEntity;
import zmaster587.libVulpes.network.PacketHandler;
import zmaster587.libVulpes.network.PacketSpawnEntity;
import zmaster587.libVulpes.util.HashedBlockPosition;
import zmaster587.libVulpes.util.Vector3F;
import zmaster587.libVulpes.util.ZUtils;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public class EntityStationDeployedRocket extends EntityRocket {

	public Direction launchDirection;
	public Direction forwardDirection;
	public HashedBlockPosition launchLocation;
	private ModuleText atmText;
	private short gasId;
	boolean coastMode;
	private Ticket<Entity> ticket;

	public EntityStationDeployedRocket(EntityType<?> type, World world) {
		super(type, world);
		launchDirection = Direction.DOWN;
		launchLocation = new HashedBlockPosition(0,0,0);
		atmText = new ModuleText(182, 114, "", 0x2d2d2d);
		gasId = 0;
		ticket = null;
	}

	public EntityStationDeployedRocket(World world, StorageChunk storage, StatsRocket stats, double x, double y, double z) {
		super(world, storage, stats, x,y,z);
		launchLocation = new HashedBlockPosition((int)x,(int)y,(int)z);
		launchDirection = Direction.DOWN;
		stats.setSeatLocation(-1, -1, -1); //No seats
		atmText = new ModuleText(182, 114, "", 0x2d2d2d);
		gasId = 0;
	}

	/**
	 * Called immediately before launch
	 */
	@Override
	public void prepareLaunch() {
		
		RocketPreLaunchEvent event = new RocketEvent.RocketPreLaunchEvent(this);
		MinecraftForge.EVENT_BUS.post(event);

		if(!event.isCanceled()) {
			if(world.isRemote)
				PacketHandler.sendToServer(new PacketEntity(this, (byte)EntityRocket.PacketType.LAUNCH.ordinal()));
			launch();
		}
	}
	
	@Override
	public void launch() {

		if(isInFlight())
			return;


		if(isInOrbit()) {
			setInFlight(true);
			return;
		}
		if(getFuelAmount(getRocketFuelType()) < getFuelCapacity(getRocketFuelType()))
			return;

		ISpaceObject spaceObj;
		if( DimensionManager.spaceId.equals(ZUtils.getDimensionIdentifier(world) ) && (spaceObj = SpaceObjectManager.getSpaceManager().getSpaceStationFromBlockCoords(new BlockPos(getPositionVec()))) != null && spaceObj.getProperties().getParentProperties().isGasGiant() ) { //Abort if destination is invalid


			setInFlight(true);
			launchLocation.x = (int) Math.floor(this.getPosX());
			launchLocation.y = (short) this.getPosY();
			launchLocation.z = (int) Math.floor(this.getPosZ());
			Iterator<IInfrastructure> connectedTiles = connectedInfrastructure.iterator();

			MinecraftForge.EVENT_BUS.post(new RocketLaunchEvent(this));

			//Disconnect things linked to the rocket on liftoff
			while(connectedTiles.hasNext()) {
				IInfrastructure i = connectedTiles.next();
				if(i.disconnectOnLiftOff()) {
					disconnectInfrastructure(i);
					connectedTiles.remove();
				}
			}
		}
	}

	@Override
	public void tick() {
		lastWorldTickTicked = world.getGameTime();
		if(this.ticksExisted == 20) {
			//problems with loading on other world then where the infrastructure was set?
			for (HashedBlockPosition temp : new LinkedList<>(infrastructureCoords)) {
				TileEntity tile = this.world.getTileEntity(new BlockPos(temp.x, temp.y, temp.z));
				if (tile instanceof IInfrastructure) {
					this.linkInfrastructure((IInfrastructure) tile);
				}
			}

			if(world.isRemote)
				LibVulpes.proxy.playSound(new SoundRocketEngine( AudioRegistry.combustionRocket, SoundCategory.NEUTRAL,this));
		}

		if(isInFlight()) {

			if(launchLocation == null || storage == null)
				return;
			
			//Grab a ticket when we take off
			if(!world.isRemote) {
				((ServerWorld)this.world).getChunkProvider().registerTicket(TicketType.UNKNOWN, new ChunkPos(getPositionUnderneath()), 3, new ChunkPos(getPositionUnderneath()));
			}
			
			boolean isCoasting = Math.abs(this.getPosX() - launchLocation.x) < 4*storage.getSizeX() && Math.abs(this.getPosY() - launchLocation.y) < 4*storage.getSizeY() && Math.abs(this.getPosZ() - launchLocation.z) < 4*storage.getSizeZ();

			if(!isCoasting) {
				//Burn the rocket fuel

				//Spawn in the particle effects for the engines
				if(world.isRemote && Minecraft.getInstance().gameSettings.particles != ParticleStatus.MINIMAL) {
					for(Vector3F<Float> vec : stats.getEngineLocations()) {

						float xMult = Math.abs(forwardDirection.getXOffset());
						float zMult = Math.abs(forwardDirection.getZOffset());
						float xVel, zVel;

						for(int i = 0; i < 4; i++) {
							xVel = (1-xMult)*((this.rand.nextFloat() - 0.5f)/8f) + xMult*-.15f;
							zVel = (1-zMult)*((this.rand.nextFloat() - 0.5f)/8f) + zMult*-.15f;

							AdvancedRocketry.proxy.spawnParticle(AdvancedRocketryParticleTypes.rocketFx, world, this.getPosX() + vec.x + getMotion().x, this.getPosY() + vec.y, this.getPosZ() +vec.z, xVel,(this.rand.nextFloat() - 0.5f)/8f, zVel + getMotion().z);

						}
					}
				}
			}


			if(forwardDirection == null)
				return;

			//Returning
			if(isInOrbit()) { //For unmanned rockets
				Direction dir;
				isCoasting = Math.abs(this.getPosX() - launchLocation.x - (storage.getSizeX() % 2 == 0 ? 0 : 0.5f)) < 0.01 && Math.abs(this.getPosZ() - launchLocation.z - (storage.getSizeZ() % 2 == 0 ? 0 : 0.5f)) < .01;

				if(isCoasting) {
					dir = launchDirection.getOpposite();
					float speed = 0.075f;
					this.setMotion(speed*dir.getXOffset(),
					speed*dir.getYOffset(),
					speed*dir.getZOffset());
				}
				else {
					dir = forwardDirection.getOpposite();

					float acc = 0.01f;
					this.setMotion(
					acc*(launchLocation.x - this.getPosX() + (storage.getSizeX() % 2 == 0 ? 0 : 0.5f)) + 0.01*dir.getXOffset(),
					0,//acc*(launchLocation.y - this.posY) + 0.01*dir.offsetY;
					acc*(launchLocation.z - this.getPosZ() + (storage.getSizeZ() % 2 == 0 ? 0 : 0.5f)) + 0.01*dir.getZOffset());

				}

				if(this.getPosY() > launchLocation.y ) {
					if(!world.isRemote) {
						this.setInFlight(false);
						this.setInOrbit(false);
						MinecraftForge.EVENT_BUS.post(new RocketEvent.RocketLandedEvent(this));
						
						//PacketHandler.sendToNearby(new PacketEntity(this, (byte)PacketType.ROCKETLANDEVENT.ordinal()), world.provider.dimensionId, (int)posX, (int)posY, (int)posZ, 64);
						//PacketHandler.sendToPlayersTrackingEntity(new PacketEntity(this, (byte)PacketType.ROCKETLANDEVENT.ordinal()), this);
					}

					this.setMotion(getMotion().x, 0, getMotion().z);
					this.setPosition(launchLocation.x + (storage.getSizeX() % 2 == 0 ? 0 : 0.5f), launchLocation.y, launchLocation.z  + (storage.getSizeZ() % 2 == 0 ? 0 : 0.5f));
				}
			}
			else {
				//Move out 4x the size of the rocket
				//Coast away from the station
				if(isCoasting) {
					float speed = 0.01f;//(float)Math.min(0.2f, Math.abs(motionY) + 0.0001f);
					
					double motionX = getMotion().x, motionY = getMotion().y, motionZ = getMotion().z;
					
					this.setMotion(
					motionX + speed*launchDirection.getXOffset() * ( 2.1*storage.getSizeX() - Math.abs(2*storage.getSizeX() - Math.abs(this.getPosX() - launchLocation.x)) + 0.05),
					motionY + speed*launchDirection.getYOffset() * ( 2.1*storage.getSizeY() - Math.abs(2*storage.getSizeY() - Math.abs(this.getPosY() - launchLocation.y)) + 0.05),
					motionZ + speed*launchDirection.getZOffset() * ( 2.1*storage.getSizeZ() - Math.abs(2*storage.getSizeZ() - Math.abs(this.getPosZ() - launchLocation.z)) + 0.05));
				}
				else {
					float acc = 0.01f;
					double motionX = getMotion().x, motionY = getMotion().y, motionZ = getMotion().z;
					this.setMotion(
					motionX + acc*forwardDirection.getXOffset(),
					motionY + acc*forwardDirection.getYOffset(),
					motionZ + acc*forwardDirection.getZOffset());

				}
				if(!world.isRemote && this.getDistanceSq(launchLocation.x, launchLocation.y, launchLocation.z) > 128*128) {
					onOrbitReached();
					return;
				}
			}


			this.move(MoverType.SELF, getMotion());
		}
	}

	@Override
	public List<ModuleBase> getModules(int ID, PlayerEntity player) {
		List<ModuleBase> modules;
		//If the rocket is flight don't load the interface
		modules = super.getModules(ID, player);

		Iterator<ModuleBase> itr = modules.iterator();
		while(itr.hasNext()) {
			ModuleBase module = itr.next();
			if(module instanceof ModuleButton && ((ModuleButton)module).getAdditionalData().equals("unmannedremove")) {
				itr.remove();
				break;
			}
		}


		DimensionProperties props = DimensionManager.getEffectiveDimId(world, this.getPositionVec());
		if(props.isGasGiant()) {
			try {
				atmText.setText(props.getHarvestableGasses().get(gasId).getRegistryName().toString());
			} catch (IndexOutOfBoundsException e) {
				gasId = 0;
				atmText.setText(props.getHarvestableGasses().get(gasId).getRegistryName().toString());
			}
		}
		else {
			atmText.setText(LibVulpes.proxy.getLocalizedString("msg.entityDeployedRocket.notgasgiant"));
		}
		modules.add(new ModuleButton(170, 114, "", this, zmaster587.libVulpes.inventory.TextureResources.buttonLeft, 5, 8).setAdditionalData(1));
		modules.add(atmText);
		modules.add(new ModuleButton(240, 114, "", this, zmaster587.libVulpes.inventory.TextureResources.buttonRight,  5, 8).setAdditionalData(2));

		return modules;
	}

	@Override
	@OnlyIn(value=Dist.CLIENT)
	public void onInventoryButtonPressed(ModuleButton button) {
		
		int buttonId = (int)button.getAdditionalData();
		
		DimensionProperties props;
		switch(buttonId) {
		case 0:
			PacketHandler.sendToServer(new PacketEntity(this, (byte)EntityRocket.PacketType.DECONSTRUCT.ordinal()));
			break;
		case 1:
			props = DimensionManager.getEffectiveDimId(world, this.getPositionVec());
			if(props.isGasGiant()) {
				gasId++;
				if(gasId < 0)
					gasId = (short)(props.getHarvestableGasses().size() - 1);
				else if(gasId > props.getHarvestableGasses().size() - 1)
					gasId = 0;
				PacketHandler.sendToServer(new PacketEntity(this, (byte)EntityRocket.PacketType.MENU_CHANGE.ordinal()));
			}
			break;
		case 2:
			props = DimensionManager.getEffectiveDimId(world, this.getPositionVec());
			if(props.isGasGiant()) {
				gasId--;
				if(gasId < 0)
					gasId = (short)(props.getHarvestableGasses().size() - 1);
				else if(gasId > props.getHarvestableGasses().size() - 1)
					gasId = 0;
				PacketHandler.sendToServer(new PacketEntity(this, (byte)EntityRocket.PacketType.MENU_CHANGE.ordinal()));
			}
			break;
		default:
			super.onInventoryButtonPressed(button);
		}
	}


	/**
	 * Called when the rocket reaches orbit
	 */
	public void onOrbitReached() {
		//make it 30 minutes with one drill

		if(!this.isAlive())
			return;

		//Check again to make sure we are around a gas giant
		ISpaceObject spaceObj;
		setInOrbit(true);
		if( DimensionManager.spaceId.equals(ZUtils.getDimensionIdentifier(world)) && ((spaceObj = SpaceObjectManager.getSpaceManager().getSpaceStationFromBlockCoords(new BlockPos(this.getPositionVec()))) != null && spaceObj.getProperties().getParentProperties().isGasGiant() )) { //Abort if destination is invalid
			this.setPosition(forwardDirection.getXOffset()*64d + this.launchLocation.x + (storage.getSizeX() % 2 == 0 ? 0 : 0.5d), getPosY(), forwardDirection.getZOffset()*64d + this.launchLocation.z + (storage.getSizeZ() % 2 == 0 ? 0 : 0.5d));
		}
		else {
			setInOrbit(true);
			return;
		}


		DimensionProperties properties = (DimensionProperties)spaceObj.getProperties().getParentProperties();
		
		//Make sure gas id is valid, or abort
		if(gasId >= properties.getHarvestableGasses().size() || gasId < 0)
		{
			setInOrbit(true);
			return;
		}
		
		//one intake with a 1 bucket tank should take 100 seconds
		float intakePower = (Integer)stats.getStatTag("intakePower");
		
		MissionGasCollection miningMission = new MissionGasCollection(intakePower == 0 ? 360 : (long)(2*((int)stats.getStatTag("liquidCapacity")/intakePower)), this, connectedInfrastructure, properties.getHarvestableGasses().get(gasId));

		miningMission.setDimensionId(properties.getId());
		properties.addSatellite(miningMission);

		if(!world.isRemote)
			PacketHandler.sendToAll(new PacketSatellite(miningMission));

		for(IInfrastructure i : connectedInfrastructure) {
			i.linkMission(miningMission);
		}

		this.remove();
	}


	@Override
	protected void writeNetworkableNBT(CompoundNBT nbt) {
		super.writeNetworkableNBT(nbt);

	}

	@Override
	protected void readAdditional(CompoundNBT nbt) {
		super.readAdditional(nbt);

	}
	@Override
	public void writeDataToNetwork(PacketBuffer out, byte id) {
		super.writeDataToNetwork(out, id);

		if(id == PacketType.MENU_CHANGE.ordinal()) {
			out.writeShort(gasId);
		}
		else
			super.writeDataToNetwork(out, id);
	}

	@Override
	public void readDataFromNetwork(PacketBuffer in, byte packetId,
			CompoundNBT nbt) {


		if(packetId == PacketType.MENU_CHANGE.ordinal()) {
			nbt.putShort("gas", in.readShort());
		}
		else
			super.readDataFromNetwork(in, packetId, nbt);
	}

	@Override
	public void useNetworkData(PlayerEntity player, Dist side, byte id,
			CompoundNBT nbt) {


		if(id == PacketType.MENU_CHANGE.ordinal()) {

			DimensionProperties props = DimensionManager.getEffectiveDimId(world, this.getPositionVec());
			if(props.isGasGiant()) {

				gasId = nbt.getShort("gas");
				if(gasId < 0)
					gasId = (short)(props.getHarvestableGasses().size() - 1);
				else if(gasId > props.getHarvestableGasses().size() - 1)
					gasId = 0;

				if(!world.isRemote)
					PacketHandler.sendToNearby(new PacketEntity(this, (byte) PacketType.MENU_CHANGE.ordinal()), world, (int)getPosX(), (int)getPosY(), (int)getPosZ(), 64d);
				else
					atmText.setText(props.getHarvestableGasses().get(gasId).getRegistryName().toString());
			}
		}
		else
			super.useNetworkData(player, side, id, nbt);
	}


	@Override
	public void writeMissionPersistentNBT(CompoundNBT nbt) {
		super.writeMissionPersistentNBT(nbt);
		nbt.putInt("fwd", forwardDirection.ordinal());

		nbt.putInt("launchX", launchLocation.x);
		nbt.putInt("launchY", launchLocation.y);
		nbt.putInt("launchZ", launchLocation.z);

		nbt.putShort("gas", gasId);
	}

	@Override
	public void readMissionPersistentNBT(CompoundNBT nbt) {
		super.readMissionPersistentNBT(nbt);
		forwardDirection = Direction.values()[nbt.getInt("fwd")];

		launchLocation.x = nbt.getInt("launchX");
		launchLocation.y = (short)nbt.getInt("launchY");
		launchLocation.z = nbt.getInt("launchZ");
		gasId = nbt.getShort("gas");
	}
	
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
