package zmaster587.advancedRocketry.entity;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.MoverType;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeChunkManager;
import net.minecraftforge.common.ForgeChunkManager.Ticket;
import net.minecraftforge.common.ForgeChunkManager.Type;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import zmaster587.advancedRocketry.AdvancedRocketry;
import zmaster587.advancedRocketry.api.Configuration;
import zmaster587.advancedRocketry.api.IInfrastructure;
import zmaster587.advancedRocketry.api.RocketEvent;
import zmaster587.advancedRocketry.api.RocketEvent.RocketLaunchEvent;
import zmaster587.advancedRocketry.api.StatsRocket;
import zmaster587.advancedRocketry.api.atmosphere.AtmosphereRegister;
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
import zmaster587.libVulpes.util.HashedBlockPosition;
import zmaster587.libVulpes.util.Vector3F;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;

public class EntityStationDeployedRocket extends EntityRocket {

	public EnumFacing launchDirection;
	public EnumFacing forwardDirection;
	public HashedBlockPosition launchLocation;
	private ModuleText atmText;
	private short gasId;
	boolean coastMode;
	private Ticket ticket;

	public EntityStationDeployedRocket(World world) {
		super(world);
		launchDirection = EnumFacing.DOWN;
		launchLocation = new HashedBlockPosition(0,0,0);
		atmText = new ModuleText(182, 114, "", 0x2d2d2d);
		gasId = 0;
		ticket = null;
	}

	public EntityStationDeployedRocket(World world, StorageChunk storage, StatsRocket stats, double x, double y, double z) {
		super(world, storage, stats, x,y,z);
		launchLocation = new HashedBlockPosition((int)x,(int)y,(int)z);
		launchDirection = EnumFacing.DOWN;
		stats.setSeatLocation(-1, -1, -1); //No seats
		atmText = new ModuleText(182, 114, "", 0x2d2d2d);
		gasId = 0;
	}

	//Use as a way of checking when chunk is unloaded
	@Override
	public void setDead() {
		super.setDead();
		if(ticket != null)
			ForgeChunkManager.releaseTicket(ticket);
	}

	@Override
	public void launch() {

		if(isInFlight())
			return;


		if(isInOrbit()) {
			setInFlight(true);
			return;
		}
		if(getFuelAmount() < getFuelCapacity())
			return;

		ISpaceObject spaceObj;
		if( world.provider.getDimension() == Configuration.spaceDimId && (spaceObj = SpaceObjectManager.getSpaceManager().getSpaceStationFromBlockCoords(getPosition())) != null && ((DimensionProperties)spaceObj.getProperties().getParentProperties()).isGasGiant() ) { //Abort if destination is invalid


			setInFlight(true);
			launchLocation.x = (int) Math.floor(this.posX);
			launchLocation.y = (short) this.posY;
			launchLocation.z = (int) Math.floor(this.posZ);
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
	public void onUpdate() {
		lastWorldTickTicked = world.getTotalWorldTime();

		if(this.ticksExisted == 20) {
			//problems with loading on other world then where the infrastructure was set?
			ListIterator<HashedBlockPosition> itr = (new LinkedList<HashedBlockPosition>(infrastructureCoords)).listIterator();
			while(itr.hasNext()) {
				HashedBlockPosition temp = itr.next();

				TileEntity tile = this.world.getTileEntity(new BlockPos(temp.x, temp.y, temp.z));
				if(tile instanceof IInfrastructure) {
					this.linkInfrastructure((IInfrastructure)tile);
				}
			}

			if(world.isRemote)
				LibVulpes.proxy.playSound(new SoundRocketEngine( AudioRegistry.combustionRocket, SoundCategory.NEUTRAL,this));
		}

		if(isInFlight()) {

			boolean burningFuel = isBurningFuel();

			if(launchLocation == null || storage == null)
				return;
			
			//Grab a ticket when we take off
			if(!world.isRemote && ticket == null) {
				ticket = ForgeChunkManager.requestTicket(AdvancedRocketry.instance, world, Type.ENTITY);
				if(ticket != null) {
					ticket.bindEntity(this);
					for(int i = 0; i < 9; i++)
						ForgeChunkManager.forceChunk(ticket, new ChunkPos(forwardDirection.getFrontOffsetX()*i + (launchLocation.x >> 4), forwardDirection.getFrontOffsetZ()*i + (launchLocation.z >> 4)));
				}
			}
			
			boolean isCoasting = Math.abs(this.posX - launchLocation.x) < 4*storage.getSizeX() && Math.abs(this.posY - launchLocation.y) < 4*storage.getSizeY() && Math.abs(this.posZ - launchLocation.z) < 4*storage.getSizeZ();

			if(!isCoasting) {
				//Burn the rocket fuel

				//Spawn in the particle effects for the engines
				if(world.isRemote && Minecraft.getMinecraft().gameSettings.particleSetting < 2) {
					for(Vector3F<Float> vec : stats.getEngineLocations()) {

						float xMult = Math.abs(forwardDirection.getFrontOffsetX());
						float zMult = Math.abs(forwardDirection.getFrontOffsetZ());
						float xVel, zVel;

						for(int i = 0; i < 4; i++) {
							xVel = (1-xMult)*((this.rand.nextFloat() - 0.5f)/8f) + xMult*-.15f;
							zVel = (1-zMult)*((this.rand.nextFloat() - 0.5f)/8f) + zMult*-.15f;

							AdvancedRocketry.proxy.spawnParticle("rocketFlame", world, this.posX + vec.x + motionX, this.posY + vec.y, this.posZ +vec.z, xVel,(this.rand.nextFloat() - 0.5f)/8f, zVel +  motionZ);

						}
					}
				}
			}


			if(forwardDirection == null)
				return;

			//Returning
			if(isInOrbit()) { //For unmanned rockets
				EnumFacing dir;
				isCoasting = Math.abs(this.posX - launchLocation.x - (storage.getSizeX() % 2 == 0 ? 0 : 0.5f)) < 0.01 && Math.abs(this.posZ - launchLocation.z - (storage.getSizeZ() % 2 == 0 ? 0 : 0.5f)) < .01;

				if(isCoasting) {
					dir = launchDirection.getOpposite();
					float speed = 0.075f;
					motionX = speed*dir.getFrontOffsetX();
					motionY = speed*dir.getFrontOffsetY();
					motionZ = speed*dir.getFrontOffsetZ();
				}
				else {
					dir = forwardDirection.getOpposite();

					float acc = 0.01f;

					motionX = acc*(launchLocation.x - this.posX + (storage.getSizeX() % 2 == 0 ? 0 : 0.5f)) + 0.01*dir.getFrontOffsetX();
					motionY = 0;//acc*(launchLocation.y - this.posY) + 0.01*dir.offsetY;
					motionZ = acc*(launchLocation.z - this.posZ + (storage.getSizeZ() % 2 == 0 ? 0 : 0.5f)) + 0.01*dir.getFrontOffsetZ();

				}

				if(this.posY > launchLocation.y ) {
					if(!world.isRemote) {
						this.setInFlight(false);
						this.setInOrbit(false);
						MinecraftForge.EVENT_BUS.post(new RocketEvent.RocketLandedEvent(this));
						
						//Release ticket on landing
						if(ticket != null) {
							ForgeChunkManager.releaseTicket(ticket);
							ticket = null;
						}
						
						//PacketHandler.sendToNearby(new PacketEntity(this, (byte)PacketType.ROCKETLANDEVENT.ordinal()), world.provider.dimensionId, (int)posX, (int)posY, (int)posZ, 64);
						//PacketHandler.sendToPlayersTrackingEntity(new PacketEntity(this, (byte)PacketType.ROCKETLANDEVENT.ordinal()), this);
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
					motionX = speed*launchDirection.getFrontOffsetX() * ( 2.1*storage.getSizeX() - Math.abs(2*storage.getSizeX() - Math.abs(this.posX - launchLocation.x)) + 0.05);
					motionY = speed*launchDirection.getFrontOffsetY() * ( 2.1*storage.getSizeY() - Math.abs(2*storage.getSizeY() - Math.abs(this.posY - launchLocation.y)) + 0.05);
					motionZ = speed*launchDirection.getFrontOffsetZ() * ( 2.1*storage.getSizeZ() - Math.abs(2*storage.getSizeZ() - Math.abs(this.posZ - launchLocation.z)) + 0.05);
				}
				else {
					float acc = 0.01f;
					motionX += acc*forwardDirection.getFrontOffsetX();
					motionY += acc*forwardDirection.getFrontOffsetY();
					motionZ += acc*forwardDirection.getFrontOffsetZ();

				}
				if(!world.isRemote && this.getDistance(launchLocation.x, launchLocation.y, launchLocation.z) > 128) {
					
					
					//Release ticket on landing
					if(ticket != null) {
						ForgeChunkManager.releaseTicket(ticket);
						ticket = null;
					}
					onOrbitReached();
					return;
				}
			}


			this.move(MoverType.SELF, motionX, motionY, motionZ);
		}
	}

	@Override
	public List<ModuleBase> getModules(int ID, EntityPlayer player) {
		List<ModuleBase> modules;
		//If the rocket is flight don't load the interface
		modules = super.getModules(ID, player);

		Iterator<ModuleBase> itr = modules.iterator();
		while(itr.hasNext()) {
			ModuleBase module = itr.next();
			if(module instanceof ModuleButton && ((ModuleButton)module).getText().equalsIgnoreCase("Select Dst")) {
				itr.remove();
				break;
			}
		}


		DimensionProperties props = DimensionManager.getEffectiveDimId(world, this.getPosition());
		if(props.isGasGiant()) {
			try {
				atmText.setText(props.getHarvestableGasses().get(gasId).getLocalizedName(new FluidStack(props.getHarvestableGasses().get(gasId), 1)));
			} catch (IndexOutOfBoundsException e) {
				gasId = 0;
				atmText.setText(props.getHarvestableGasses().get(gasId).getLocalizedName(new FluidStack(props.getHarvestableGasses().get(gasId), 1)));
			}
		}
		else {
			atmText.setText(LibVulpes.proxy.getLocalizedString("msg.entityDeployedRocket.notGasGiant"));
		}
		modules.add(new ModuleButton(170, 114, 1, "", this, zmaster587.libVulpes.inventory.TextureResources.buttonLeft, 5, 8));
		modules.add(atmText);
		modules.add(new ModuleButton(240, 114, 2, "", this, zmaster587.libVulpes.inventory.TextureResources.buttonRight,  5, 8));

		return modules;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void onInventoryButtonPressed(int buttonId) {
		DimensionProperties props;
		switch(buttonId) {
		case 0:
			PacketHandler.sendToServer(new PacketEntity(this, (byte)EntityRocket.PacketType.DECONSTRUCT.ordinal()));
			break;
		case 1:
			props = DimensionManager.getEffectiveDimId(world, this.getPosition());
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
			props = DimensionManager.getEffectiveDimId(world, this.getPosition());
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
			super.onInventoryButtonPressed(buttonId);
		}
	}


	/**
	 * Called when the rocket reaches orbit
	 */
	public void onOrbitReached() {
		//make it 30 minutes with one drill

		if(this.isDead)
			return;

		//Check again to make sure we are around a gas giant
		ISpaceObject spaceObj = null;
		setInOrbit(true);
		if( world.provider.getDimension() == Configuration.spaceDimId && ((spaceObj = SpaceObjectManager.getSpaceManager().getSpaceStationFromBlockCoords(this.getPosition())) != null && ((DimensionProperties)spaceObj.getProperties().getParentProperties()).isGasGiant() )) { //Abort if destination is invalid
			this.setPosition(forwardDirection.getFrontOffsetX()*64d + this.launchLocation.x + (storage.getSizeX() % 2 == 0 ? 0 : 0.5d), posY, forwardDirection.getFrontOffsetZ()*64d + this.launchLocation.z + (storage.getSizeZ() % 2 == 0 ? 0 : 0.5d));	
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
		properties.addSatallite(miningMission);

		if(!world.isRemote)
			PacketHandler.sendToAll(new PacketSatellite(miningMission));

		for(IInfrastructure i : connectedInfrastructure) {
			i.linkMission(miningMission);
		}

		this.setDead();
	}


	@Override
	protected void writeNetworkableNBT(NBTTagCompound nbt) {
		super.writeNetworkableNBT(nbt);

	}

	@Override
	protected void readEntityFromNBT(NBTTagCompound nbt) {
		super.readEntityFromNBT(nbt);

	}
	@Override
	public void writeDataToNetwork(ByteBuf out, byte id) {
		super.writeDataToNetwork(out, id);

		if(id == PacketType.MENU_CHANGE.ordinal()) {
			out.writeShort(gasId);
		}
		else
			super.writeDataToNetwork(out, id);
	}

	@Override
	public void readDataFromNetwork(ByteBuf in, byte packetId,
			NBTTagCompound nbt) {


		if(packetId == PacketType.MENU_CHANGE.ordinal()) {
			nbt.setShort("gas", in.readShort());
		}
		else
			super.readDataFromNetwork(in, packetId, nbt);
	}

	@Override
	public void useNetworkData(EntityPlayer player, Side side, byte id,
			NBTTagCompound nbt) {


		if(id == PacketType.MENU_CHANGE.ordinal()) {

			DimensionProperties props = DimensionManager.getEffectiveDimId(world, this.getPosition());
			if(props.isGasGiant()) {

				gasId = nbt.getShort("gas");
				if(gasId < 0)
					gasId = (short)(props.getHarvestableGasses().size() - 1);
				else if(gasId > props.getHarvestableGasses().size() - 1)
					gasId = 0;

				if(!world.isRemote)
					PacketHandler.sendToNearby(new PacketEntity(this, (byte) PacketType.MENU_CHANGE.ordinal()), world.provider.getDimension(), (int)posX, (int)posY, (int)posZ, 64d);
				else
					atmText.setText(props.getHarvestableGasses().get(gasId).getLocalizedName(new FluidStack(AtmosphereRegister.getInstance().getHarvestableGasses().get(gasId),1)));
			}
		}
		else
			super.useNetworkData(player, side, id, nbt);
	}


	@Override
	public void writeMissionPersistantNBT(NBTTagCompound nbt) {
		// TODO Auto-generated method stub
		super.writeMissionPersistantNBT(nbt);
		nbt.setInteger("fwd", forwardDirection.ordinal());

		nbt.setInteger("launchX", launchLocation.x);
		nbt.setInteger("launchY", launchLocation.y);
		nbt.setInteger("launchZ", launchLocation.z);

		nbt.setShort("gas", gasId);
	}

	@Override
	public void readMissionPersistantNBT(NBTTagCompound nbt) {
		super.readMissionPersistantNBT(nbt);
		forwardDirection = EnumFacing.values()[nbt.getInteger("fwd")];

		launchLocation.x = nbt.getInteger("launchX");
		launchLocation.y = (short)nbt.getInteger("launchY");
		launchLocation.z = nbt.getInteger("launchZ");
		gasId = nbt.getShort("gas");
	}
}
