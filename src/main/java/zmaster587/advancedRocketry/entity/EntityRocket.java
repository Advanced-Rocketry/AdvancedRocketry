package zmaster587.advancedRocketry.entity;

import io.netty.buffer.ByteBuf;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GLAllocation;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.ChatComponentText;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.FluidContainerRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.IFluidContainerItem;
import zmaster587.advancedRocketry.AdvancedRocketry;
import zmaster587.advancedRocketry.achievements.ARAchivements;
import zmaster587.advancedRocketry.api.AdvancedRocketryItems;
import zmaster587.advancedRocketry.api.Configuration;
import zmaster587.advancedRocketry.api.EntityRocketBase;
import zmaster587.advancedRocketry.api.IInfrastructure;
import zmaster587.advancedRocketry.api.RocketEvent;
import zmaster587.advancedRocketry.api.RocketEvent.RocketLaunchEvent;
import zmaster587.advancedRocketry.api.RocketEvent.RocketPreLaunchEvent;
import zmaster587.advancedRocketry.api.SatelliteRegistry;
import zmaster587.advancedRocketry.api.StatsRocket;
import zmaster587.advancedRocketry.api.fuel.FuelRegistry;
import zmaster587.advancedRocketry.api.fuel.FuelRegistry.FuelType;
import zmaster587.advancedRocketry.api.satellite.SatelliteBase;
import zmaster587.advancedRocketry.api.stations.ISpaceObject;
import zmaster587.advancedRocketry.atmosphere.AtmosphereHandler;
import zmaster587.advancedRocketry.client.SoundRocketEngine;
import zmaster587.advancedRocketry.dimension.DimensionManager;
import zmaster587.advancedRocketry.dimension.DimensionProperties;
import zmaster587.advancedRocketry.event.PlanetEventHandler;
import zmaster587.advancedRocketry.inventory.TextureResources;
import zmaster587.advancedRocketry.inventory.modules.ModulePlanetSelector;
import zmaster587.advancedRocketry.item.ItemAsteroidChip;
import zmaster587.advancedRocketry.item.ItemPackedStructure;
import zmaster587.advancedRocketry.item.ItemPlanetIdentificationChip;
import zmaster587.advancedRocketry.item.ItemStationChip;
import zmaster587.advancedRocketry.mission.MissionOreMining;
import zmaster587.advancedRocketry.network.PacketSatellite;
import zmaster587.advancedRocketry.stations.SpaceObjectManager;
import zmaster587.advancedRocketry.tile.TileGuidanceComputer;
import zmaster587.advancedRocketry.tile.hatch.TileSatelliteHatch;
import zmaster587.advancedRocketry.util.StorageChunk;
import zmaster587.advancedRocketry.util.TransitionEntity;
import zmaster587.advancedRocketry.world.util.TeleporterNoPortal;
import zmaster587.libVulpes.LibVulpes;
import zmaster587.libVulpes.api.IDismountHandler;
import zmaster587.libVulpes.client.util.ProgressBarImage;
import zmaster587.libVulpes.gui.CommonResources;
import zmaster587.libVulpes.interfaces.INetworkEntity;
import zmaster587.libVulpes.inventory.GuiHandler;
import zmaster587.libVulpes.inventory.modules.IButtonInventory;
import zmaster587.libVulpes.inventory.modules.IModularInventory;
import zmaster587.libVulpes.inventory.modules.IProgressBar;
import zmaster587.libVulpes.inventory.modules.ISelectionNotify;
import zmaster587.libVulpes.inventory.modules.ModuleBase;
import zmaster587.libVulpes.inventory.modules.ModuleButton;
import zmaster587.libVulpes.inventory.modules.ModuleImage;
import zmaster587.libVulpes.inventory.modules.ModuleProgress;
import zmaster587.libVulpes.inventory.modules.ModuleSlotButton;
import zmaster587.libVulpes.items.ItemLinker;
import zmaster587.libVulpes.network.PacketEntity;
import zmaster587.libVulpes.network.PacketHandler;
import zmaster587.libVulpes.util.BlockPosition;
import zmaster587.libVulpes.util.IconResource;
import zmaster587.libVulpes.util.Vector3F;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class EntityRocket extends EntityRocketBase implements INetworkEntity, IDismountHandler, IModularInventory, IProgressBar, IButtonInventory, ISelectionNotify {

	//true if the rocket is on decent
	private boolean isInOrbit;
	//True if the rocket isn't on the ground
	private boolean isInFlight;
	//used in the rare case a player goes to a non-existant space station
	private int lastDimensionFrom = 0;
	
	public StorageChunk storage;
	private String errorStr;
	private long lastErrorTime = Long.MIN_VALUE;
	private static long ERROR_DISPLAY_TIME = 100;
	private static int DESCENT_TIMER = 500;
	protected long lastWorldTickTicked;

	private SatelliteBase satallite;
	protected int destinationDimId;
	//Offset for buttons linking to the tileEntityGrid
	private int tilebuttonOffset = 3;
	private int autoDescendTimer;
	private WeakReference<Entity>[] mountedEntities;
	protected ModulePlanetSelector container;

	public static enum PacketType {
		RECIEVENBT,
		SENDINTERACT,
		REQUESTNBT,
		FORCEMOUNT,
		LAUNCH,
		DECONSTRUCT,
		OPENGUI,
		CHANGEWORLD,
		REVERTWORLD,
		OPENPLANETSELECTION,
		SENDPLANETDATA,
		DISCONNECTINFRASTRUCTURE,
		CONNECTINFRASTRUCTURE,
		ROCKETLANDEVENT,
		MENU_CHANGE,
		UPDATE_ATM,
		UPDATE_ORBIT,
		UPDATE_FLIGHT
	}

	public EntityRocket(World p_i1582_1_) {
		super(p_i1582_1_);
		isInOrbit = false;
		stats = new StatsRocket();
		isInFlight = false;
		connectedInfrastructure = new LinkedList<IInfrastructure>();
		infrastructureCoords = new LinkedList<BlockPosition>();
		mountedEntities = new WeakReference[stats.getNumPassengerSeats()];

		lastWorldTickTicked = p_i1582_1_.getTotalWorldTime();
		autoDescendTimer = 5000;
	}

	public EntityRocket(World world, StorageChunk storage, StatsRocket stats, double x, double y, double z) {
		this(world);
		this.stats = stats;
		this.setPosition(x, y, z);
		this.storage = storage;
		this.storage.setEntity(this);
		initFromBounds();
		isInFlight = false;
		mountedEntities = new WeakReference[stats.getNumPassengerSeats()];
		lastWorldTickTicked = world.getTotalWorldTime();
		autoDescendTimer = 5000;
	}

	@Override
	public AxisAlignedBB getBoundingBox() {
		if(storage != null) {
			return this.boundingBox;
		}
		return null;
	}

	/**
	 * @return the amount of fuel stored in the rocket
	 */
	public int getFuelAmount() {
		int amount = dataWatcher.getWatchableObjectInt(17);
		stats.setFuelAmount(FuelType.LIQUID,amount);
		return amount;
	}

	/**
	 * Adds fuel and updates the datawatcher
	 * @param amount amount of fuel to add
	 * @return the amount of fuel added
	 */
	public int addFuelAmount(int amount) {
		int ret = stats.addFuelAmount(FuelType.LIQUID, amount);

		setFuelAmount(stats.getFuelAmount(FuelType.LIQUID));

		return ret;
	}

	public void disconnectInfrastructure(IInfrastructure infrastructure){
		infrastructure.unlinkRocket();
		infrastructureCoords.remove(new BlockPosition(((TileEntity)infrastructure).xCoord, ((TileEntity)infrastructure).yCoord, ((TileEntity)infrastructure).zCoord));

		if(!worldObj.isRemote) {
			int pos[] = {((TileEntity)infrastructure).xCoord, ((TileEntity)infrastructure).yCoord, ((TileEntity)infrastructure).zCoord};

			NBTTagCompound nbt = new NBTTagCompound();
			nbt.setIntArray("pos", pos);
			//PacketHandler.sendToPlayersTrackingEntity(new PacketEntity(this, (byte)PacketType.DISCONNECTINFRASTRUCTURE.ordinal(), nbt), this);
		}
	}

	@Override
	
	public void setPosition(double x, double y,
			double z) {
		super.setPosition(x, y, z);

		if(storage != null) {
			float sizeX = storage.getSizeX()/2.0f;
			float sizeY = storage.getSizeY();
			float sizeZ = storage.getSizeZ()/2.0f;
			this.boundingBox.setBounds(x - sizeX, y - (double)this.yOffset + this.ySize, z - sizeZ, x + sizeX, y + sizeY - (double)this.yOffset + this.ySize, z + sizeZ);
		}
	}

	/**
	 * Updates the data option
	 * @param amt sets the amount of fuel in the rocket
	 */
	public void setFuelAmount(int amt) {
		dataWatcher.updateObject(17, amt);
	}

	/**
	 * @return gets the fuel capacity of the rocket
	 */
	public int getFuelCapacity() {
		return stats.getFuelCapacity(FuelType.LIQUID);
	}

	@Override
	public void setEntityId(int id){
		super.setEntityId(id);
		//Ask server for nbt data
		if(worldObj.isRemote) {
			PacketHandler.sendToServer(new PacketEntity(this, (byte)PacketType.REQUESTNBT.ordinal()));
		}
	}

	@Override
	public boolean canBeCollidedWith() {
		return true;
	}


	/**
	 * If the rocket is in flight, ie the rocket has taken off and has not touched the ground
	 * @return true if in flight
	 */
	public boolean isInFlight() {
		if(!worldObj.isRemote) {
			return isInFlight;
		}
		return this.dataWatcher.getWatchableObjectByte(16) == 1;
	}

	/**
	 * Sets the the status of flight of the rocket and updates the datawatcher
	 * @param inflight status of flight
	 */
	public void setInOrbit(boolean inOrbit) {
		this.isInOrbit = inOrbit;
		this.dataWatcher.updateObject(18, new Byte(isInOrbit ? (byte)1 : (byte)0));
	}

	/**
	 * If the rocket is in flight, ie the rocket has taken off and has not touched the ground
	 * @return true if in flight
	 */
	public boolean isInOrbit() {
		if(!worldObj.isRemote) {
			return isInOrbit;
		}
		return this.dataWatcher.getWatchableObjectByte(18) == 1;
	}

	/**
	 * Sets the the status of flight of the rocket and updates the datawatcher
	 * @param inflight status of flight
	 */
	public void setInFlight(boolean inflight) {
		this.isInFlight = inflight;
		this.dataWatcher.updateObject(16, new Byte(isInFlight ? (byte)1 : (byte)0));
	}

	@Override
	protected void entityInit() {
		this.dataWatcher.addObject(16, new Byte(isInFlight ? (byte)1 : (byte)0));
		this.dataWatcher.addObject(17, new Integer(0));
		this.dataWatcher.addObject(18, new Byte(isInOrbit ? (byte)1 : (byte)0));
	}

	//Set the size and position of the rocket from storage
	public void initFromBounds() {		
		if(storage != null) {
			this.setSize(Math.max(storage.getSizeX(), storage.getSizeZ()), storage.getSizeY());
			this.setPosition(this.posX, this.posY, this.posZ);
		}
	}

	protected boolean interact(EntityPlayer player) {
		//Actual interact code needs to be moved to a packet receive on the server

		ItemStack heldItem = player.getHeldItem();

		//Handle linkers and right-click with fuel
		if(heldItem != null) {
			float fuelMult;
			FluidStack fluidStack;

			if(heldItem.getItem() instanceof ItemLinker) {
				if(ItemLinker.isSet(heldItem)) {

					TileEntity tile = this.worldObj.getTileEntity(ItemLinker.getMasterX(heldItem), ItemLinker.getMasterY(heldItem), ItemLinker.getMasterZ(heldItem));

					if(tile instanceof IInfrastructure) {
						IInfrastructure infrastructure = (IInfrastructure)tile;
						if(this.getDistance(ItemLinker.getMasterX(heldItem), this.posY, ItemLinker.getMasterZ(heldItem)) < infrastructure.getMaxLinkDistance() + Math.max(storage.getSizeX(), storage.getSizeZ())) {
							if(!connectedInfrastructure.contains(tile)) {

								linkInfrastructure(infrastructure);
								if(!worldObj.isRemote) {
									player.addChatMessage(new ChatComponentText("Linked Sucessfully"));
								}
								ItemLinker.resetPosition(heldItem);

								return true;
							}
							else if(!worldObj.isRemote)
								player.addChatMessage(new ChatComponentText("Already linked!"));
						}
						else if(!worldObj.isRemote)
							player.addChatMessage(new ChatComponentText("The object you are trying to link is too far away"));
					}
					else if(!worldObj.isRemote)
						player.addChatMessage(new ChatComponentText("This cannot be linked to a rocket!"));
				}
				else if(!worldObj.isRemote)
					player.addChatMessage(new ChatComponentText("Nothing to be linked"));
				return false;
			}
			else if((FluidContainerRegistry.isFilledContainer(heldItem) && (fuelMult = FuelRegistry.instance.getMultiplier(FuelType.LIQUID, (fluidStack = FluidContainerRegistry.getFluidForFilledItem(heldItem)).getFluid()) ) > 0 )
					|| ( heldItem.getItem() instanceof IFluidContainerItem && ((IFluidContainerItem) heldItem.getItem()).getFluid(heldItem) != null && 
					((IFluidContainerItem) heldItem.getItem()).getFluid(heldItem).amount >= FluidContainerRegistry.BUCKET_VOLUME
					&& (fuelMult = FuelRegistry.instance.getMultiplier(FuelType.LIQUID, (fluidStack = ((IFluidContainerItem) heldItem.getItem()).getFluid(heldItem)).getFluid())) > 0 )) { 


				int amountToAdd = (int) (fuelMult*fluidStack.amount);
				this.addFuelAmount(amountToAdd);

				//if the player is not in creative then try to use the fluid container
				if(!player.capabilities.isCreativeMode) {
					if(heldItem.getItem() instanceof IFluidContainerItem) {
						((IFluidContainerItem) heldItem.getItem()).drain(heldItem, FluidContainerRegistry.BUCKET_VOLUME, true);
					}
					else {
						ItemStack emptyStack = FluidContainerRegistry.drainFluidContainer(player.getHeldItem());

						if(player.inventory.addItemStackToInventory(emptyStack)) {
							player.getHeldItem().splitStack(1);
							if(player.getHeldItem().stackSize == 0)
								player.inventory.setInventorySlotContents(player.inventory.currentItem, null); 
						}
					}
				}

				return true;
			}
		}

		//If player is holding shift open GUI
		if(player.isSneaking()) {
			openGui(player);
		}
		else if(stats.hasSeat()) { //If pilot seat is open mount entity there
			if(stats.hasSeat() && this.riddenByEntity == null) {
				if(!worldObj.isRemote)
					player.mountEntity(this);
			}
			/*else if(stats.getNumPassengerSeats() > 0) { //If a passenger seat exists and one is empty, mount the player to it
				for(int i = 0; i < stats.getNumPassengerSeats(); i++) {
					if(this.mountedEntities[i] == null || this.mountedEntities[i].get() == null) {
						player.ridingEntity = this;
						this.mountedEntities[i] = new WeakReference<Entity>(player);
						break;
					}
				}
			}*/
		}
		return true;
	}


	public void openGui(EntityPlayer player) {
		player.openGui(LibVulpes.instance, GuiHandler.guiId.MODULAR.ordinal(), player.worldObj, this.getEntityId(), -1,0);

		//Only handle the bypass on the server
		if(!worldObj.isRemote)
			PlanetEventHandler.addPlayerToInventoryBypass(player);
	}


	@Override
	public boolean interactFirst(EntityPlayer player) {
		if(worldObj.isRemote) {
			//Due to forge's rigid handling of entities (NetHanlderPlayServer:866) needs to be handled differently for large rockets
			PacketHandler.sendToServer(new PacketEntity(this, (byte)PacketType.SENDINTERACT.ordinal()));
			return interact(player);
		}
		return true;

	}


	public boolean isBurningFuel() {
		return (getFuelAmount() > 0 || !Configuration.rocketRequireFuel) && (!(this.riddenByEntity instanceof EntityPlayer) || !isInOrbit() || ((EntityPlayer)this.riddenByEntity).moveForward > 0);
	}

	public boolean isDescentPhase() {
		return Configuration.automaticRetroRockets && isInOrbit() && this.posY < 300 && (this.motionY < -0.4f || worldObj.isRemote);
	}

	public boolean areEnginesRunning() {
		return (this.motionY > 0 || isDescentPhase() || (riddenByEntity != null && ((EntityPlayer)riddenByEntity).moveForward > 0));
	}


	@Override
	public void onUpdate() {
		super.onUpdate();

		long deltaTime = worldObj.getTotalWorldTime() - lastWorldTickTicked;
		lastWorldTickTicked = worldObj.getTotalWorldTime();

		if(this.ticksExisted == 1 && worldObj.isRemote) {

			LibVulpes.proxy.playSound(new SoundRocketEngine( TextureResources.sndCombustionRocket,this));
		}
		
		if(this.ticksExisted > DESCENT_TIMER && isInOrbit() && !isInFlight())
			setInFlight(true);

		//Hackish crap to make clients mount entities immediately after server transfer and fire events
		if(!worldObj.isRemote && (this.isInFlight() || this.isInOrbit()) && this.ticksExisted == 20) {
			if(this.riddenByEntity instanceof EntityPlayer) {
				EntityPlayer player = (EntityPlayer)this.riddenByEntity;
				//Deorbiting
				MinecraftForge.EVENT_BUS.post(new RocketEvent.RocketDeOrbitingEvent(this));
				PacketHandler.sendToNearby(new PacketEntity(this, (byte)PacketType.ROCKETLANDEVENT.ordinal()), worldObj.provider.dimensionId, (int)posX, (int)posY, (int)posZ, 64);

				if(player instanceof EntityPlayer)
					PacketHandler.sendToPlayer(new PacketEntity((INetworkEntity)this,(byte)PacketType.FORCEMOUNT.ordinal()), player);
			}
		}


		if(isInFlight()) {
			boolean burningFuel = isBurningFuel();

			boolean descentPhase = isDescentPhase();

			if(burningFuel || descentPhase) {
				//Burn the rocket fuel
				if(!worldObj.isRemote && !descentPhase)
					setFuelAmount(getFuelAmount() - stats.getFuelRate(FuelType.LIQUID));

				//Spawn in the particle effects for the engines
				if(worldObj.isRemote && Minecraft.getMinecraft().gameSettings.particleSetting < 2 && areEnginesRunning()) {
					int engineNum = 0;
					for(Vector3F<Float> vec : stats.getEngineLocations()) {

						AtmosphereHandler handler;
						//Cycle through engines outputting smoke, increases performance with craft with large number of engines
						if(worldObj.getTotalWorldTime() % 10 == 0 && (engineNum < 8 || ((worldObj.getTotalWorldTime()/10) % Math.max((stats.getEngineLocations().size()/8),1)) == (engineNum/8)) && ( (handler = AtmosphereHandler.getOxygenHandler(worldObj.provider.dimensionId)) == null || handler.getAtmosphereType(this) == null || handler.getAtmosphereType(this).allowsCombustion()) )
							AdvancedRocketry.proxy.spawnParticle("rocketSmoke", worldObj, this.posX + vec.x, this.posY + vec.y - 0.75, this.posZ +vec.z,0,0,0);

						for(int i = 0; i < 4; i++) {
							AdvancedRocketry.proxy.spawnParticle("rocketFlame", worldObj, this.posX + vec.x, this.posY + vec.y - 0.75, this.posZ +vec.z,(this.rand.nextFloat() - 0.5f)/8f,-.75 ,(this.rand.nextFloat() - 0.5f)/8f);

						}

						engineNum++;
					}
				}
			}

			if(this.riddenByEntity instanceof EntityPlayer) {
				EntityPlayer player = (EntityPlayer)this.riddenByEntity;
				player.fallDistance = 0;
				this.fallDistance = 0;

				//if the player holds the forward key then decelerate
				if(isInOrbit() && (burningFuel || descentPhase)) {
					float vel = descentPhase ? 1f : player.moveForward;
					this.motionY -= this.motionY*vel/50f;
				}
				this.velocityChanged = true;

			}
			else if(isInOrbit() && descentPhase) { //For unmanned rockets
				this.motionY -= this.motionY/50f;
				this.velocityChanged = true;
			}

			if(!worldObj.isRemote) {
				//If out of fuel or descending then accelerate downwards
				if(isInOrbit() || !burningFuel) {
					this.motionY = Math.min(this.motionY - 0.001, 1);
				} else
					//this.motionY = Math.min(this.motionY + 0.001, 1);
					this.motionY += stats.getAcceleration() * deltaTime;


				double lastPosY = this.posY;
				double prevMotion = this.motionY;
				this.moveEntity(0, prevMotion*deltaTime, 0);

				//Check to see if it's landed
				if((isInOrbit() || !burningFuel) && isInFlight() && lastPosY + prevMotion != this.posY && this.posY < 256) {
					MinecraftForge.EVENT_BUS.post(new RocketEvent.RocketLandedEvent(this));
					//PacketHandler.sendToPlayersTrackingEntity(new PacketEntity(this, (byte)PacketType.ROCKETLANDEVENT.ordinal()), this);
					this.setInFlight(false);
					this.setInOrbit(false);
				}
				if(!isInOrbit() && (this.posY > Configuration.orbit)) {
					onOrbitReached();
				}


				//If the rocket falls out of the world while in orbit either fall back to earth or die
				if(this.posY < 0) {
					int dimId = worldObj.provider.dimensionId;

					if(dimId == Configuration.spaceDimId) {

						ISpaceObject obj = SpaceObjectManager.getSpaceManager().getSpaceStationFromBlockCoords((int)this.posX, (int)this.posZ);

						if(obj != null) {
							int targetDimID = obj.getOrbitingPlanetId();

							Vector3F<Float> pos = storage.getDestinationCoordinates(targetDimID, true);
							if(pos != null) {
								setInOrbit(true);
								setInFlight(false);
								this.travelToDimension(destinationDimId, pos.x, Configuration.orbit, pos.z);
							}
							else 
								this.setDead();
						}
						else {
							Vector3F<Float> pos = storage.getDestinationCoordinates(lastDimensionFrom, true);
							if(pos != null) {
								setInOrbit(true);
								setInFlight(false);
								this.travelToDimension(destinationDimId, pos.x, Configuration.orbit, pos.z);
							}
							else 
								this.setDead();
						}
					}
					else
						this.setDead();
				}
			}
			else
				this.moveEntity(0, this.motionY, 0);
		}
	}


	/**
	 * @return a list of satellites stores in this rocket
	 */
	public List<SatelliteBase> getSatellites() {	
		List<SatelliteBase> satellites = new ArrayList<SatelliteBase>();
		for(TileSatelliteHatch tile : storage.getSatelliteHatches()) {
			SatelliteBase satellite = tile.getSatellite();
			if(satellite != null)
				satellites.add(satellite);
		}
		return satellites;
	}

	/**
	 * Called when the rocket reaches orbit
	 */
	public void onOrbitReached() {
		super.onOrbitReached();

		//TODO: support multiple riders and rider/satellite combo
		if(!stats.hasSeat()) {

			TileGuidanceComputer computer = storage.getGuidanceComputer();
			if(computer != null && computer.getStackInSlot(0) != null &&
					computer.getStackInSlot(0).getItem() instanceof ItemAsteroidChip) {
				//make it 30 minutes with one drill
				float drillingPower = stats.getDrillingPower();
				MissionOreMining miningMission = new MissionOreMining((long)(Configuration.asteroidMiningTimeMult*(drillingPower == 0f ? 36000 : 360/stats.getDrillingPower())), this, connectedInfrastructure);
				DimensionProperties properties = DimensionManager.getInstance().getDimensionProperties(worldObj.provider.dimensionId);

				miningMission.setDimensionId(worldObj);
				properties.addSatallite(miningMission, worldObj);

				if(!worldObj.isRemote)
					PacketHandler.sendToAll(new PacketSatellite(miningMission));

				for(IInfrastructure i : connectedInfrastructure) {
					i.linkMission(miningMission);
				}

				this.setDead();
				//TODO: Move tracking stations over to the mission handler
			}
			else {
				unpackSatellites();
			}

			destinationDimId = storage.getDestinationDimId(this.worldObj.provider.dimensionId, (int)this.posX, (int)this.posZ);
			if(DimensionManager.getInstance().canTravelTo(destinationDimId)) {
				Vector3F<Float> pos = storage.getDestinationCoordinates(destinationDimId,true);
				storage.setDestinationCoordinates(new Vector3F<Float>((float)this.posX, (float)this.posY, (float)this.posZ), this.worldObj.provider.dimensionId);
				if(pos != null) {
					this.setInOrbit(true);
					this.motionY = -this.motionY;
					this.travelToDimension(destinationDimId, pos.x, Configuration.orbit, pos.z);
					return;
				}
			}
			else
				this.setDead();
			//TODO: satellite event?
		}
		else {

			unpackSatellites();

			//TODO: maybe add orbit dimension
			this.motionY = -this.motionY;
			setInOrbit(true);
			//If going to a station or something make sure to set coords accordingly
			//If in space land on the planet, if on the planet go to space
			if(destinationDimId == Configuration.spaceDimId || this.worldObj.provider.dimensionId == Configuration.spaceDimId) {
				Vector3F<Float> pos = storage.getDestinationCoordinates(destinationDimId, true);
				storage.setDestinationCoordinates(new Vector3F<Float>((float)this.posX, (float)this.posY, (float)this.posZ), this.worldObj.provider.dimensionId);
				if(pos != null) {

					//Make player confirm deorbit if a player is riding the rocket
					if(this.riddenByEntity != null) {
						setInFlight(false);
						pos.y = (float) Configuration.orbit;
					}

					this.travelToDimension(destinationDimId, pos.x, pos.y, pos.z);
					return;
				}
			}
			//Make player confirm deorbit if a player is riding the rocket
			if(this.riddenByEntity != null) {
				setInFlight(false);

				if(DimensionManager.getInstance().getDimensionProperties(destinationDimId).getName().equals("Luna")) {

					if(this.riddenByEntity instanceof EntityPlayer) {
						((EntityPlayer)this.riddenByEntity).triggerAchievement(ARAchivements.moonLanding);
						if(!DimensionManager.hasReachedMoon)
							((EntityPlayer)this.riddenByEntity).triggerAchievement(ARAchivements.oneSmallStep);
					}

					DimensionManager.hasReachedMoon = true;
				}
			}
			else
				setPosition(posX, Configuration.orbit, posZ);

			if(destinationDimId != this.worldObj.provider.dimensionId)
				this.travelToDimension(this.worldObj.provider.dimensionId == destinationDimId ? 0 : destinationDimId);
		}
	}

	private void unpackSatellites() {
		List<TileSatelliteHatch> satelliteHatches = storage.getSatelliteHatches();

		for(TileSatelliteHatch tile : satelliteHatches) {
			SatelliteBase satellite = tile.getSatellite();
			if(satellite == null) {
				ItemStack stack = tile.getStackInSlot(0);
				if(stack != null && stack.getItem() == AdvancedRocketryItems.itemSpaceStation) {
					StorageChunk storage = ((ItemPackedStructure)stack.getItem()).getStructure(stack);
					ISpaceObject object = SpaceObjectManager.getSpaceManager().getSpaceStation((int)ItemStationChip.getUUID(stack));

					SpaceObjectManager.getSpaceManager().moveStationToBody(object, this.worldObj.provider.dimensionId);

					//Vector3F<Integer> spawn = object.getSpawnLocation();

					object.onModuleUnpack(storage);

					tile.setInventorySlotContents(0, null);
				}
			}
			else {
				
				DimensionProperties properties = DimensionManager.getEffectiveDimId(worldObj, (int)this.posX, (int)this.posZ);
				World world = net.minecraftforge.common.DimensionManager.getWorld(properties.getId());
				
				properties.addSatallite(satellite, world);
				tile.setInventorySlotContents(0, null);
			}
		}
	}

	@Override
	/**
	 * Called immediately before launch
	 */
	public void prepareLaunch() {
		RocketPreLaunchEvent event = new RocketEvent.RocketPreLaunchEvent(this);
		MinecraftForge.EVENT_BUS.post(event);

		if(!event.isCanceled()) {
			if(worldObj.isRemote)
				PacketHandler.sendToServer(new PacketEntity(this, (byte)EntityRocket.PacketType.LAUNCH.ordinal()));
			launch();
		}
	}

	@Override
	public String getTextOverlay() {

		if(this.worldObj.getTotalWorldTime() < this.lastErrorTime + ERROR_DISPLAY_TIME)
			return errorStr;

		//Get destination string
		String displayStr = "N/A";
		if(storage != null) {
			int dimid = storage.getDestinationDimId(this.worldObj.provider.dimensionId, (int)posX, (int)posZ);

			if(dimid == Configuration.spaceDimId) {
				Vector3F<Float> vec = storage.getDestinationCoordinates(dimid, false);
				if(vec != null) {

					ISpaceObject obj = SpaceObjectManager.getSpaceManager().getSpaceStationFromBlockCoords((int)((float)vec.x),(int)((float)vec.x));

					if(obj != null) {
						displayStr = "Station " + obj.getId();
					}
				}
			}
			else if(dimid != -1 && dimid != SpaceObjectManager.WARPDIMID) {
				displayStr = DimensionManager.getInstance().getDimensionProperties(dimid).getName();
			}
		}

		if(isInOrbit() && !isInFlight())
			return "Press Space to descend!\n  Auto descend in " + ((DESCENT_TIMER - this.ticksExisted)/20);
		else if(!isInFlight())
			return "Press Space to take off!\nDest: " + displayStr;

		return super.getTextOverlay();
	}

	private void setError(String error) {
		this.errorStr = error;
		this.lastErrorTime = this.worldObj.getTotalWorldTime();
	}

	@Override
	public void launch() {

		if(isInFlight())
			return;

		if(isInOrbit()) {
			setInFlight(true);
			return;
		}

		//Get destination dimid and lock the computer
		//TODO: lock the computer
		destinationDimId = storage.getDestinationDimId(worldObj.provider.dimensionId, (int)this.posX, (int)this.posZ);

		//TODO: make sure this doesn't break asteroid mining
		if(!(DimensionManager.getInstance().canTravelTo(destinationDimId) || (destinationDimId == -1 && storage.getSatelliteHatches().size() != 0))) {
			setError(LibVulpes.proxy.getLocalizedString("error.rocket.cannotGetThere"));
			return;
		}

		int finalDest = destinationDimId;
		if(destinationDimId == Configuration.spaceDimId) {
			ISpaceObject obj = null;
			Vector3F<Float> vec = storage.getDestinationCoordinates(destinationDimId,false);
			if(vec != null)
				obj = SpaceObjectManager.getSpaceManager().getSpaceStationFromBlockCoords((int)(float)vec.x, (int)(float)vec.z);

			if(obj != null)
				finalDest = obj.getOrbitingPlanetId();
			else { 
				setError(LibVulpes.proxy.getLocalizedString("error.rocket.destinationNotExist"));
				return;
			}
		}


		int thisDimId = this.worldObj.provider.dimensionId;
		if(this.worldObj.provider.dimensionId == Configuration.spaceDimId) {
			ISpaceObject object = SpaceObjectManager.getSpaceManager().getSpaceStationFromBlockCoords((int)this.posX, (int)this.posZ);
			if(object != null)
				thisDimId = object.getProperties().getParentProperties().getId();
		}

		if(finalDest != -1 && !DimensionManager.getInstance().areDimensionsInSamePlanetMoonSystem(finalDest, thisDimId)) {
			setError(LibVulpes.proxy.getLocalizedString("error.rocket.notSameSystem"));
			return;
		}

		//TODO: Clean this logic a bit?
		if(!stats.hasSeat() || ((DimensionManager.getInstance().isDimensionCreated(destinationDimId)) || destinationDimId == Configuration.spaceDimId || destinationDimId == 0) ) { //Abort if destination is invalid


			setInFlight(true);
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

	/**
	 * Called when the rocket is to be deconstructed
	 */
	@Override
	public void deconstructRocket() {
		super.deconstructRocket();

		for(IInfrastructure infrastructure : connectedInfrastructure) {
			infrastructure.unlinkRocket();
		}


		//paste the rocket into the world as blocks
		storage.pasteInWorld(this.worldObj, (int)(this.posX - storage.getSizeX()/2f), (int)this.posY, (int)(this.posZ - storage.getSizeZ()/2f));
		this.setDead();
	}

	@Override
	public void setDead() {
		super.setDead();

		//unlink any connected tiles
		Iterator<IInfrastructure> connectedTiles = connectedInfrastructure.iterator();
		while(connectedTiles.hasNext()) {
			connectedTiles.next().unlinkRocket();
			connectedTiles.remove();
		}

		if(worldObj.isRemote && storage != null && storage.world.glListID != -1) {
			GLAllocation.deleteDisplayLists(storage.world.glListID);
		}
	}


	//Relink connected tiles on load
	@Override
	public void onChunkLoad() {
		super.onChunkLoad();

		//problems with loading on other world then where the infrastructure was set?
		ListIterator<BlockPosition> itr = infrastructureCoords.listIterator();
		while(itr.hasNext()) {
			BlockPosition temp = itr.next();

			TileEntity tile = this.worldObj.getTileEntity(temp.x, temp.y, temp.z);
			if(tile instanceof IInfrastructure) {
				this.linkInfrastructure((IInfrastructure)tile);
				itr.remove();
			}
		}
	}


	public void setOverriddenCoords(int dimId, float x, float y, float z) {
		TileGuidanceComputer tile = storage.getGuidanceComputer();
		if(tile != null) {
			tile.setFallbackDestination(dimId, new Vector3F<Float>(x, y, z));
		}
	}

	@Override
	public void travelToDimension(int newDimId) {
		travelToDimension(newDimId, this.posX, Configuration.orbit, this.posZ);
	}

	public void travelToDimension(int newDimId, double posX, double y, double posZ)
	{
		if (!this.worldObj.isRemote && !this.isDead)
		{

			if(!DimensionManager.getInstance().canTravelTo(newDimId)) {
				AdvancedRocketry.logger.warn("Rocket trying to travel from Dim" + this.worldObj.provider.dimensionId + " to Dim " + newDimId + ".  target not accessible by rocket from launch dim");
				return;
			}

			lastDimensionFrom = this.worldObj.provider.dimensionId;
			
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

	protected void readNetworkableNBT(NBTTagCompound nbt) {
		//Normal function checks for the existance of the data anyway
		readEntityFromNBT(nbt);
	}

	@Override
	protected void readEntityFromNBT(NBTTagCompound nbt) {

		setInOrbit(isInOrbit = nbt.getBoolean("orbit"));
		stats.readFromNBT(nbt);

		mountedEntities = new WeakReference[stats.getNumPassengerSeats()];

		setFuelAmount(stats.getFuelAmount(FuelType.LIQUID));

		setInFlight(isInFlight = nbt.getBoolean("flight"));

		readMissionPersistantNBT(nbt);
		if(nbt.hasKey("data"))
		{
			if(storage == null) 
				storage = new StorageChunk();

			storage.readFromNBT(nbt.getCompoundTag("data"));
			storage.setEntity(this);
			this.setSize(Math.max(storage.getSizeX(), storage.getSizeZ()), storage.getSizeY());
		}

		NBTTagList tagList = nbt.getTagList("infrastructure", 10);
		for (int i = 0; i < tagList.tagCount(); i++) {
			int coords[] = tagList.getCompoundTagAt(i).getIntArray("loc");

			//If called on server causes recursive loop, use hackish workaround with tempcoords and onChunkLoad if on server
			if(worldObj.isRemote) {

				TileEntity tile = this.worldObj.getTileEntity(coords[0], coords[1], coords[2]);
				if(tile instanceof IInfrastructure)
					this.linkInfrastructure((IInfrastructure)tile);

			}
			else
				infrastructureCoords.add(new BlockPosition(coords[0], coords[1], coords[2]));
		}

		destinationDimId = nbt.getInteger("destinationDimId");
		
		lastDimensionFrom = nbt.getInteger("lastDimensionFrom");

		//Satallite
		if(nbt.hasKey("satallite")) {
			NBTTagCompound satalliteNbt = nbt.getCompoundTag("satallite");
			satallite = SatelliteRegistry.createFromNBT(satalliteNbt);
		}
	}

	protected void writeNetworkableNBT(NBTTagCompound nbt) {
		writeMissionPersistantNBT(nbt);
		nbt.setBoolean("orbit", isInOrbit());
		nbt.setBoolean("flight", isInFlight());
		stats.writeToNBT(nbt);

		NBTTagList itemList = new NBTTagList();
		for(int i = 0; i < connectedInfrastructure.size(); i++)
		{
			IInfrastructure inf = connectedInfrastructure.get(i);

			if(inf instanceof TileEntity) {
				TileEntity ent = (TileEntity)inf;
				NBTTagCompound tag = new NBTTagCompound();
				tag.setIntArray("loc", new int[] {ent.xCoord, ent.yCoord, ent.zCoord});
				itemList.appendTag(tag);
			}
		}
		nbt.setTag("infrastructure", itemList);
		nbt.setInteger("destinationDimId", destinationDimId);

		//Satallite
		if(satallite != null) {
			NBTTagCompound satalliteNbt = new NBTTagCompound();
			satallite.writeToNBT(satalliteNbt);
			satalliteNbt.setString("DataType",SatelliteRegistry.getKey(satallite.getClass()));

			nbt.setTag("satallite", satalliteNbt);
		}
	}

	public void writeMissionPersistantNBT(NBTTagCompound nbt) {

	}

	public void readMissionPersistantNBT(NBTTagCompound nbt) {

	}

	@Override
	protected void writeEntityToNBT(NBTTagCompound nbt) {

		writeNetworkableNBT(nbt);
		if(storage != null) {
			NBTTagCompound blocks = new NBTTagCompound();
			storage.writeToNBT(blocks);
			nbt.setTag("data", blocks);
		}
		
		nbt.setInteger("lastDimensionFrom", lastDimensionFrom);

		//TODO handle non tile Infrastructure

	}

	@Override
	public void readDataFromNetwork(ByteBuf in, byte packetId,
			NBTTagCompound nbt) {
		if(packetId == PacketType.RECIEVENBT.ordinal()) {
			storage = new StorageChunk();
			storage.setEntity(this);
			storage.readFromNetwork(in);
		}
		else if(packetId == PacketType.SENDPLANETDATA.ordinal()) {
			nbt.setInteger("selection", in.readInt());
		}
	}

	@Override
	public void writeDataToNetwork(ByteBuf out, byte id) {

		if(id == PacketType.RECIEVENBT.ordinal()) {
			storage.writeToNetwork(out);
		}
		else if(id == PacketType.SENDPLANETDATA.ordinal()) {
			if(worldObj.isRemote)
				out.writeInt(container.getSelectedSystem());
			else {
				if(storage.getGuidanceComputer() != null) {
					ItemStack stack = storage.getGuidanceComputer().getStackInSlot(0);
					if(stack != null && stack.getItem() == AdvancedRocketryItems.itemPlanetIdChip) {
						out.writeInt(((ItemPlanetIdentificationChip)AdvancedRocketryItems.itemPlanetIdChip).getDimensionId(stack));
					}
				}
			}
		}
	}

	@Override
	public void useNetworkData(EntityPlayer player, Side side, byte id,
			NBTTagCompound nbt) {


		if(id == PacketType.RECIEVENBT.ordinal()) {
			this.readEntityFromNBT(nbt);
			initFromBounds();
		}
		else if(id == PacketType.DECONSTRUCT.ordinal()) {
			deconstructRocket();
		}
		else if(id == PacketType.SENDINTERACT.ordinal()) {
			interact(player);
		}
		else if(id == PacketType.OPENGUI.ordinal()) { //Used in key handler
			if(player.ridingEntity == this) //Prevent cheating
				openGui(player);
		}
		else if(id == PacketType.REQUESTNBT.ordinal()) {
			if(storage != null) {
				NBTTagCompound nbtdata = new NBTTagCompound();

				this.writeNetworkableNBT(nbtdata);
				PacketHandler.sendToPlayer(new PacketEntity((INetworkEntity)this, (byte)PacketType.RECIEVENBT.ordinal(), nbtdata), player);

			}
		}
		else if(id == PacketType.FORCEMOUNT.ordinal()) { //Used for pesky dimension transfers
			//When dimensions are transferred make sure to remount the player on the client
			player.mountEntity(this);
			MinecraftForge.EVENT_BUS.post(new RocketEvent.RocketDeOrbitingEvent(this));
		}
		else if(id == PacketType.LAUNCH.ordinal()) {
			if(player.equals(this.riddenByEntity))
				this.prepareLaunch();
		}
		else if(id == PacketType.CHANGEWORLD.ordinal()) {
			AdvancedRocketry.proxy.changeClientPlayerWorld(storage.world);
		}
		else if(id == PacketType.REVERTWORLD.ordinal()) {
			AdvancedRocketry.proxy.changeClientPlayerWorld(this.worldObj);
		}
		else if(id == PacketType.OPENPLANETSELECTION.ordinal()) {
			player.openGui(LibVulpes.instance, GuiHandler.guiId.MODULARFULLSCREEN.ordinal(), player.worldObj, this.getEntityId(), -1,0);
		}
		else if(id == PacketType.SENDPLANETDATA.ordinal()) {
			ItemStack stack = storage.getGuidanceComputer().getStackInSlot(0);
			if(stack != null && stack.getItem() == AdvancedRocketryItems.itemPlanetIdChip) {
				((ItemPlanetIdentificationChip)AdvancedRocketryItems.itemPlanetIdChip).setDimensionId(stack, nbt.getInteger("selection"));

				//Send data back to sync destination dims
				if(!worldObj.isRemote) {
					PacketHandler.sendToPlayersTrackingEntity(new PacketEntity(this, (byte)PacketType.SENDPLANETDATA.ordinal()), this);
				}
			}
		}
		else if(id == PacketType.DISCONNECTINFRASTRUCTURE.ordinal()) {
			int pos[] = nbt.getIntArray("pos");

			connectedInfrastructure.remove(new BlockPosition(pos[0], pos[1], pos[2]));

			TileEntity tile = worldObj.getTileEntity(pos[0], pos[1], pos[2]);
			if(tile instanceof IInfrastructure) {
				((IInfrastructure)tile).unlinkRocket();
				connectedInfrastructure.remove(tile);
			}
		}
		else if(id == PacketType.ROCKETLANDEVENT.ordinal() && worldObj.isRemote) {
			MinecraftForge.EVENT_BUS.post(new RocketEvent.RocketLandedEvent(this));
		}
		else if(id > 100) {
			TileEntity tile = storage.getGUItiles().get(id - 100 - tilebuttonOffset);

			//Welcome to super hack time with packets
			//Due to the fact the client uses the player's current world to open the gui, we have to move the client between worlds for a bit
			PacketHandler.sendToPlayer(new PacketEntity(this, (byte)PacketType.CHANGEWORLD.ordinal()), player);
			storage.getBlock(tile.xCoord, tile.yCoord, tile.zCoord).onBlockActivated(storage.world, tile.xCoord, tile.yCoord,  tile.zCoord, player, 0, 0, 0, 0);
			PacketHandler.sendToPlayer(new PacketEntity(this, (byte)PacketType.REVERTWORLD.ordinal()), player);
		}
	}

	@Override
	public void updateRiderPosition()
	{
		if (this.riddenByEntity != null )
		{
			//Bind player to the seat
			if(this.storage != null) {
				//Conditional b/c for some reason client/server positions do not match
				float xOffset = this.storage.getSizeX() % 2 == 0 ? 0.5f : 0f;
				float zOffset = this.storage.getSizeZ() % 2 == 0 ? 0.5f : 0f;
				this.riddenByEntity.setPosition(this.posX  + stats.getSeatX() + xOffset, this.posY + stats.getSeatY() + (worldObj.isRemote && this.riddenByEntity.equals(Minecraft.getMinecraft().thePlayer) ? 1.5 : -0.25), this.posZ + stats.getSeatZ() + zOffset );
			}
			else
				this.riddenByEntity.setPosition(this.posX , this.posY , this.posZ );
		}

		for(int i = 0; i < this.stats.getNumPassengerSeats(); i++) {
			BlockPosition pos = this.stats.getPassengerSeat(i);
			if(mountedEntities[i] != null && mountedEntities[i].get() != null) {
				mountedEntities[i].get().setPosition(this.posX + pos.x, this.posY + pos.y, this.posZ + pos.z); 
				System.out.println("Additional: " + mountedEntities[i].get());
			}
		}
	}

	@Override
	public List<ModuleBase> getModules(int ID, EntityPlayer player) {
		List<ModuleBase> modules;
		//If the rocket is flight don't load the interface
		modules = new LinkedList<ModuleBase>();

		if(ID == GuiHandler.guiId.MODULAR.ordinal()) {
			//Backgrounds
			if(worldObj.isRemote) {
				modules.add(new ModuleImage(173, 0, new IconResource(128, 0, 48, 86, CommonResources.genericBackground)));
				modules.add(new ModuleImage(173, 86, new IconResource(98, 0, 78, 83, CommonResources.genericBackground)));
				modules.add(new ModuleImage(173, 168, new IconResource(98, 168, 78, 3, CommonResources.genericBackground)));
			}

			//Fuel
			modules.add(new ModuleProgress(192, 7, 0, new ProgressBarImage(2, 173, 12, 71, 17, 6, 3, 69, 1, 1, ForgeDirection.UP, TextureResources.rocketHud), this));

			//TODO DEBUG tiles!
			List<TileEntity> tiles = storage.getGUItiles();
			for(int i = 0; i < tiles.size(); i++) {
				TileEntity tile  = tiles.get(i);
				try {
					modules.add(new ModuleSlotButton(8 + 18* (i % 9), 17 + 18*(i/9), i + tilebuttonOffset, this, new ItemStack(storage.getBlock(tile.xCoord, tile.yCoord, tile.zCoord), 1, storage.getBlockMetadata(tile.xCoord, tile.yCoord, tile.zCoord))));
				} catch(NullPointerException e) {
					//Fail silently, seems to happen with odd blocks once in a while, see #207
				}
			}

			//Add buttons
			modules.add(new ModuleButton(180, 140, 0, "Dissassemble", this, zmaster587.libVulpes.inventory.TextureResources.buttonBuild, 64, 20));

			//modules.add(new ModuleButton(180, 95, 1, "", this, TextureResources.buttonLeft, 10, 16));
			//modules.add(new ModuleButton(202, 95, 2, "", this, TextureResources.buttonRight, 10, 16));

			modules.add(new ModuleButton(180, 114, 1, "Select Dst", this,  zmaster587.libVulpes.inventory.TextureResources.buttonBuild, 64,20));
			//modules.add(new ModuleText(180, 114, "Inventories", 0x404040));
		}
		else {

			DimensionProperties properties = DimensionManager.getEffectiveDimId(worldObj, (int)this.posX, (int)this.posZ);
			while(properties.getParentProperties() != null) properties = properties.getParentProperties();

			container = new ModulePlanetSelector(properties.getId(), zmaster587.libVulpes.inventory.TextureResources.starryBG, this, false);
			container.setOffset(1000, 1000);
			modules.add(container);
		}
		return modules;
	}

	@Override
	public String getModularInventoryName() {
		return "Rocket";
	}

	@Override
	public float getNormallizedProgress(int id) {
		if(id == 0)
			return getFuelAmount()/(float)getFuelCapacity();
		return 0;
	}

	@Override
	public void setProgress(int id, int progress) {

	}

	@Override
	public int getProgress(int id) {
		return 0;
	}

	@Override
	public int getTotalProgress(int id) {
		return 0;
	}

	@Override
	public void setTotalProgress(int id, int progress) {}

	@Override
	@SideOnly(Side.CLIENT)
	public void onInventoryButtonPressed(int buttonId) {
		switch(buttonId) {
		case 0:
			PacketHandler.sendToServer(new PacketEntity(this, (byte)EntityRocket.PacketType.DECONSTRUCT.ordinal()));
			break;
		case 1:
			PacketHandler.sendToServer(new PacketEntity(this, (byte)EntityRocket.PacketType.OPENPLANETSELECTION.ordinal()));
			break;
		default:
			PacketHandler.sendToServer(new PacketEntity(this, (byte)(buttonId + 100)));

			//Minecraft.getMinecraft().thePlayer.closeScreen();

			TileEntity tile = storage.getGUItiles().get(buttonId - tilebuttonOffset);
			storage.getBlock(tile.xCoord, tile.yCoord, tile.zCoord).onBlockActivated(storage.world, tile.xCoord, tile.yCoord,  tile.zCoord, Minecraft.getMinecraft().thePlayer, 0, 0, 0, 0);
		}
	}

	@Override
	public boolean canInteractWithContainer(EntityPlayer entity) {
		boolean ret = !this.isDead && this.getDistanceToEntity(entity) < 64;
		if(!ret)
			PlanetEventHandler.removePlayerFromInventoryBypass(entity);

		return ret;
	}

	@Override
	public StatsRocket getRocketStats() {
		return stats;
	}

	@Override
	public void handleDismount(Entity entity) {

		//Attempt to dismount passengers first, else dismount pilot
		for(int i = 0; i < mountedEntities.length; i++) {

			if(mountedEntities[i] != null && mountedEntities[i].equals(entity)) {
				mountedEntities[i] = null;
				break;
			}
		}

		entity.ridingEntity = null;
		this.riddenByEntity = null;
	}

	@Override
	public void onSelected(Object sender) {

	}

	@Override
	public void onSelectionConfirmed(Object sender) {
		PacketHandler.sendToServer(new PacketEntity(this, (byte)PacketType.SENDPLANETDATA.ordinal()));
	}

	@Override
	public void onSystemFocusChanged(Object sender) {
		// TODO Auto-generated method stub

	}

	public LinkedList<IInfrastructure> getConnectedInfrastructure() {
		return connectedInfrastructure;
	}
}
