package zmaster587.advancedRocketry.entity;

import io.netty.buffer.ByteBuf;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GLAllocation;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.MoverType;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.Teleporter;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandlerItem;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import zmaster587.advancedRocketry.AdvancedRocketry;
import zmaster587.advancedRocketry.achievements.ARAchivements;
import zmaster587.advancedRocketry.api.*;
import zmaster587.advancedRocketry.api.RocketEvent.RocketLaunchEvent;
import zmaster587.advancedRocketry.api.RocketEvent.RocketPreLaunchEvent;
import zmaster587.advancedRocketry.api.dimension.IDimensionProperties;
import zmaster587.advancedRocketry.api.dimension.solar.StellarBody;
import zmaster587.advancedRocketry.api.fuel.FuelRegistry;
import zmaster587.advancedRocketry.api.fuel.FuelRegistry.FuelType;
import zmaster587.advancedRocketry.api.satellite.SatelliteBase;
import zmaster587.advancedRocketry.api.stations.ISpaceObject;
import zmaster587.advancedRocketry.atmosphere.AtmosphereHandler;
import zmaster587.advancedRocketry.client.SoundRocketEngine;
import zmaster587.advancedRocketry.dimension.DimensionManager;
import zmaster587.advancedRocketry.dimension.DimensionProperties;
import zmaster587.advancedRocketry.event.PlanetEventHandler;
import zmaster587.advancedRocketry.inventory.IPlanetDefiner;
import zmaster587.advancedRocketry.inventory.TextureResources;
import zmaster587.advancedRocketry.inventory.modules.ModulePlanetSelector;
import zmaster587.advancedRocketry.inventory.modules.ModuleStellarBackground;
import zmaster587.advancedRocketry.item.ItemAsteroidChip;
import zmaster587.advancedRocketry.item.ItemPackedStructure;
import zmaster587.advancedRocketry.item.ItemPlanetIdentificationChip;
import zmaster587.advancedRocketry.item.ItemStationChip;
import zmaster587.advancedRocketry.mission.MissionOreMining;
import zmaster587.advancedRocketry.network.PacketSatellite;
import zmaster587.advancedRocketry.stations.SpaceObject;
import zmaster587.advancedRocketry.stations.SpaceObjectManager;
import zmaster587.advancedRocketry.tile.TileGuidanceComputer;
import zmaster587.advancedRocketry.tile.hatch.TileSatelliteHatch;
import zmaster587.advancedRocketry.util.*;
import zmaster587.advancedRocketry.world.util.TeleporterNoPortal;
import zmaster587.libVulpes.LibVulpes;
import zmaster587.libVulpes.client.util.ProgressBarImage;
import zmaster587.libVulpes.gui.CommonResources;
import zmaster587.libVulpes.interfaces.INetworkEntity;
import zmaster587.libVulpes.inventory.GuiHandler;
import zmaster587.libVulpes.inventory.modules.*;
import zmaster587.libVulpes.items.ItemLinker;
import zmaster587.libVulpes.network.PacketEntity;
import zmaster587.libVulpes.network.PacketHandler;
import zmaster587.libVulpes.util.FluidUtils;
import zmaster587.libVulpes.util.HashedBlockPosition;
import zmaster587.libVulpes.util.IconResource;
import zmaster587.libVulpes.util.InputSyncHandler;
import zmaster587.libVulpes.util.Vector3F;

import javax.annotation.Nullable;
import java.lang.ref.WeakReference;
import java.util.*;

public class EntityRocket extends EntityRocketBase implements INetworkEntity, IModularInventory, IProgressBar, IButtonInventory, ISelectionNotify, IPlanetDefiner {

	//true if the rocket is on decent
	private boolean isInOrbit;
	//True if the rocket isn't on the ground
	private boolean isInFlight;
	//used in the rare case a player goes to a non-existant space station
	private int lastDimensionFrom = 0;
	private boolean turningLeft, turningRight, turningUp, turningDownforWhat;
	public StorageChunk storage;
	private String errorStr;
	private long lastErrorTime = Long.MIN_VALUE;
	private static long ERROR_DISPLAY_TIME = 100;
	private static int DESCENT_TIMER = 500;
	private static int BUTTON_ID_OFFSET = 25;
	private static final int STATION_LOC_OFFSET = 50;
	private ModuleText landingPadDisplayText;


	protected long lastWorldTickTicked;

	private SatelliteBase satallite;
	protected int destinationDimId;
	//Offset for buttons linking to the tileEntityGrid
	private int tilebuttonOffset = 3;
	private int autoDescendTimer;
	protected ModulePlanetSelector container;
	boolean acceptedPacket = false;
	
	//0 to 100, 100 is fully rotated and ready to go, 0 is normal mode
	private int rcs_mode_counter = 0;
	//Used to most of the logic, determining if in RCS mode or not
	private boolean rcs_mode = false;

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
		UPDATE_FLIGHT,
		DISMOUNTCLIENT,
		TOGGLE_RCS,
		TURNUPDATE
	}

	private static final DataParameter<Integer> fuelLevel =  EntityDataManager.<Integer>createKey(EntityRocket.class, DataSerializers.VARINT);
	private static final DataParameter<Boolean> INFLIGHT =  EntityDataManager.<Boolean>createKey(EntityRocket.class, DataSerializers.BOOLEAN);
	private static final DataParameter<Boolean> INORBIT =  EntityDataManager.<Boolean>createKey(EntityRocket.class, DataSerializers.BOOLEAN);
	private static final DataParameter<Boolean> RCS_MODE =  EntityDataManager.<Boolean>createKey(EntityRocket.class, DataSerializers.BOOLEAN);

	public EntityRocket(World p_i1582_1_) {
		super(p_i1582_1_);
		isInOrbit = false;
		stats = new StatsRocket();
		isInFlight = false;
		connectedInfrastructure = new LinkedList<IInfrastructure>();
		infrastructureCoords = new HashSet<HashedBlockPosition>();

		lastWorldTickTicked = p_i1582_1_.getTotalWorldTime();
		autoDescendTimer = 5000;
		landingPadDisplayText = new ModuleText(256, 16, "", 0x00FF00, 2f);
		landingPadDisplayText.setColor(0x00ff00);
	}

	public EntityRocket(World world, StorageChunk storage, StatsRocket stats, double x, double y, double z) {
		this(world);
		this.stats = stats;
		this.setPosition(x, y, z);
		this.storage = storage;
		this.storage.setEntity(this);
		initFromBounds();
		isInFlight = false;
		lastWorldTickTicked = world.getTotalWorldTime();
		autoDescendTimer = 5000;
		landingPadDisplayText = new ModuleText(256, 16, "", 0x00FF00, 2f);
		landingPadDisplayText.setColor(0x00ff00);
	}

	
	public void toggleRCS() {
		if(DimensionManager.getInstance().getDimensionProperties(this.world.provider.getDimension()).isAsteroid()) {
			rcs_mode = !rcs_mode;
			setRCS(rcs_mode);
			setPosition(this.posX, this.posY, this.posZ);
		}
		else
		{
			rcs_mode = false;
			setRCS(rcs_mode);
		}
		
	}
	
	private void setRCS(boolean status) {
		dataManager.set(RCS_MODE, status);
	}
	
	public boolean getRCS() {
		return dataManager.get(RCS_MODE);
	}
	
	public int getRCSRotateProgress() {
		return rcs_mode_counter;
	}
	
	@Override
	public AxisAlignedBB getEntityBoundingBox() {
		if(storage != null) {
			//MobileAABB aabb = new MobileAABB(super.getEntityBoundingBox());
			//aabb.setStorageChunk(storage);
			//aabb.setRemote(worldObj.isRemote);
			//return aabb;
			return super.getEntityBoundingBox();
		}
		return new AxisAlignedBB(0,0,0,1,1,1);
	}

	@Override
	public void setEntityBoundingBox(AxisAlignedBB bb) {
		//if(storage != null)
		//	super.setEntityBoundingBox(bb.offset(0, storage.getSizeY(),0));
		//else
		super.setEntityBoundingBox(bb);
	}

	@Override
	public AxisAlignedBB getCollisionBoundingBox() {
		// TODO Auto-generated method stub
		return getEntityBoundingBox();
	}

	/**
	 * @return the amount of fuel stored in the rocket
	 */
	public int getFuelAmount() {
		int amount = dataManager.get(fuelLevel);
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
		infrastructureCoords.remove(new HashedBlockPosition(((TileEntity)infrastructure).getPos()));

		if(!world.isRemote) {
			int pos[] = {((TileEntity)infrastructure).getPos().getX(), ((TileEntity)infrastructure).getPos().getY(), ((TileEntity)infrastructure).getPos().getZ()};

			NBTTagCompound nbt = new NBTTagCompound();
			nbt.setIntArray("pos", pos);
			//PacketHandler.sendToPlayersTrackingEntity(new PacketEntity(this, (byte)PacketType.DISCONNECTINFRASTRUCTURE.ordinal(), nbt), this);
		}
	}

	@Override
	public void linkInfrastructure(IInfrastructure tile) {
		super.linkInfrastructure(tile);
		if(tile instanceof TileEntity)
			infrastructureCoords.add(new HashedBlockPosition(((TileEntity)tile).getPos()));
	}
	
	@Override
	public String getTextOverlay() {

		if(this.world.getTotalWorldTime() < this.lastErrorTime + ERROR_DISPLAY_TIME)
			return errorStr;

		//Get destination string
		String displayStr = LibVulpes.proxy.getLocalizedString("msg.na");
		if(storage != null) {
			int dimid = storage.getDestinationDimId(this.world.provider.getDimension(), (int)posX, (int)posZ);

			if(dimid == Configuration.spaceDimId) {
				Vector3F<Float> vec = storage.getDestinationCoordinates(dimid, false);
				if(vec != null) {

					ISpaceObject obj = SpaceObjectManager.getSpaceManager().getSpaceStationFromBlockCoords(new BlockPos(vec.x,vec.y,vec.z));

					if(obj != null) {
						displayStr =  LibVulpes.proxy.getLocalizedString("msg.entity.rocket.station") + obj.getId();

						StationLandingLocation location = storage.getGuidanceComputer().getLandingLocation(obj.getId());

						if(location != null) {
							displayStr = displayStr + "\n" + LibVulpes.proxy.getLocalizedString("msg.entity.rocket.pad") + location;
						}
					}
				}
			}
			else if(dimid != Constants.INVALID_PLANET && dimid != SpaceObjectManager.WARPDIMID) {
				displayStr = DimensionManager.getInstance().getDimensionProperties(dimid).getName();
			}
		}

		if(DimensionManager.getInstance().getDimensionProperties(this.world.provider.getDimension()).isAsteroid()) {
			if(getRCS())
				return LibVulpes.proxy.getLocalizedString("msg.entity.rocket.rcs") + ": " + getRCS();
			else
				displayStr += "\n" + LibVulpes.proxy.getLocalizedString("msg.entity.rocket.rcs") + ": " + getRCS();
		}
		
		if(isInOrbit() && !isInFlight())
			return LibVulpes.proxy.getLocalizedString("msg.entity.rocket.descend.1") + "\n" + LibVulpes.proxy.getLocalizedString("msg.entity.rocket.descend.2") + ((DESCENT_TIMER - this.ticksExisted)/20);
		else if(!isInFlight())
			return LibVulpes.proxy.getLocalizedString("msg.entity.rocket.ascend.1") + "\n" + LibVulpes.proxy.getLocalizedString("msg.entity.rocket.ascend.2") + displayStr;

		return super.getTextOverlay();
	}

	private void setError(String error) {
		this.errorStr = error;
		this.lastErrorTime = this.world.getTotalWorldTime();
	}

	@Override
	public void setPosition(double x, double y,
			double z) {
		super.setPosition(x, y, z);

		if(storage != null) {
			if(getRCS())
			{
				float sizeX = storage.getSizeX()/2.0f;
				float sizeY = storage.getSizeY()/2.0f;
				float sizeZ = storage.getSizeZ();
				setEntityBoundingBox(new AxisAlignedBB(x - sizeX, y - (double)this.getYOffset() + sizeZ*0.5 + 0.5, z - sizeY, x + sizeX, y + sizeZ*1.5 + .5 - (double)this.getYOffset(), z + sizeY));
			}
			else {
				float sizeX = storage.getSizeX()/2.0f;
				float sizeY = storage.getSizeY();
				float sizeZ = storage.getSizeZ()/2.0f;
				setEntityBoundingBox(new AxisAlignedBB(x - sizeX, y - (double)this.getYOffset(), z - sizeZ, x + sizeX, y + sizeY - (double)this.getYOffset(), z + sizeZ));
			}
		}
	}

	@Override
    public void resetPositionToBB()
    {
        AxisAlignedBB axisalignedbb = this.getEntityBoundingBox();
		if(storage!= null && getRCS())
		{
			float sizeX = storage.getSizeX()/2.0f;
			float sizeY = storage.getSizeY()/2.0f;
			float sizeZ = storage.getSizeZ();
			//setEntityBoundingBox(new AxisAlignedBB(x - sizeX, y - (double)this.getYOffset() + sizeZ*0.5 + 0.5, z - sizeY, x + sizeX, y + sizeZ*1.5 + .5 - (double)this.getYOffset(), z + sizeY));
			
	        this.posX = (axisalignedbb.minX + axisalignedbb.maxX) / 2.0D;
	        this.posY = axisalignedbb.minY - sizeZ*0.5 - 0.5;
	        this.posZ = (axisalignedbb.minZ + axisalignedbb.maxZ) / 2.0D;
		}
		else {
			super.resetPositionToBB();
		}
    }
	
	/**
	 * Updates the data option
	 * @param amt sets the amount of fuel in the rocket
	 */
	public void setFuelAmount(int amt) {
		dataManager.set(fuelLevel, amt);
		dataManager.setDirty(fuelLevel);
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
		if(world.isRemote) {
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
		if(!world.isRemote) {
			return isInFlight;
		}
		return this.dataManager.get(INFLIGHT);
	}

	/**
	 * Sets the the status of flight of the rocket and updates the datawatcher
	 * @param inflight status of flight
	 */
	public void setInOrbit(boolean inOrbit) {
		this.isInOrbit = inOrbit;
		this.dataManager.set(INORBIT, inOrbit);
		this.dataManager.setDirty(INORBIT);
	}

	/**
	 * If the rocket is in flight, ie the rocket has taken off and has not touched the ground
	 * @return true if in flight
	 */
	public boolean isInOrbit() {
		if(!world.isRemote) {
			return isInOrbit;
		}
		return this.dataManager.get(INORBIT);
	}

	/**
	 * Sets the the status of flight of the rocket and updates the datawatcher
	 * @param inflight status of flight
	 */
	public void setInFlight(boolean inflight) {
		this.isInFlight = inflight;
		this.dataManager.set(INFLIGHT, inflight);
		this.dataManager.setDirty(INFLIGHT);
	}

	@Override
	protected void entityInit() {
		this.dataManager.register(INFLIGHT, false);
		this.dataManager.register(fuelLevel, 0);
		this.dataManager.register(INORBIT, false);
		this.dataManager.register(RCS_MODE, false);
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

		ItemStack heldItem = player.getHeldItem(EnumHand.MAIN_HAND);

		//Handle linkers and right-click with fuel
		if(heldItem != null) {
			float fuelMult;
			FluidStack fluidStack;

			if(heldItem.getItem() instanceof ItemLinker) {
				if(ItemLinker.isSet(heldItem)) {


					TileEntity tile = this.world.getTileEntity(ItemLinker.getMasterCoords(heldItem));

					if(tile instanceof IInfrastructure) {
						IInfrastructure infrastructure = (IInfrastructure)tile;
						if(this.getDistance(ItemLinker.getMasterX(heldItem), this.posY, ItemLinker.getMasterZ(heldItem)) < infrastructure.getMaxLinkDistance() + Math.max(storage.getSizeX(), storage.getSizeZ())) {
							if(!connectedInfrastructure.contains(tile)) {

								linkInfrastructure(infrastructure);
								if(!world.isRemote) {
									player.sendMessage(new TextComponentString("Linked Sucessfully"));
								}
								ItemLinker.resetPosition(heldItem);

								return true;
							}
							else if(!world.isRemote)
								player.sendMessage(new TextComponentString("Already linked!"));
						}
						else if(!world.isRemote)
							player.sendMessage(new TextComponentString("The object you are trying to link is too far away"));
					}
					else if(!world.isRemote)
						player.sendMessage(new TextComponentString("This cannot be linked to a rocket!"));
				}
				else if(!world.isRemote)
					player.sendMessage(new TextComponentString("Nothing to be linked"));
				return false;
			}

			else if((FluidUtils.containsFluid(heldItem) && (fluidStack = FluidUtils.getFluidForItem(heldItem)) != null && (fuelMult = FuelRegistry.instance.getMultiplier(FuelType.LIQUID, fluidStack.getFluid())) > 0 )) { 


				int amountToAdd = (int) (fuelMult*fluidStack.amount);
				this.addFuelAmount(amountToAdd);

				//if the player is not in creative then try to use the fluid container
				if(!player.capabilities.isCreativeMode) {
					heldItem = heldItem.copy();
					heldItem.setCount(1);
					IFluidHandlerItem handler = FluidUtils.getFluidHandler(heldItem);
					handler.drain(fluidStack.amount, true);
					ItemStack emptyStack = handler.getContainer();

					if(player.inventory.addItemStackToInventory(emptyStack)) {
						player.getHeldItem(EnumHand.MAIN_HAND).splitStack(1);
						if(player.getHeldItem(EnumHand.MAIN_HAND).isEmpty())
							player.inventory.setInventorySlotContents(player.inventory.currentItem, ItemStack.EMPTY); 
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
			if(this.getPassengers().size() < stats.getNumPassengerSeats()) {
				if(!world.isRemote)
					player.startRiding(this);
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

    protected boolean canFitPassenger(Entity passenger)
    {
        return this.getPassengers().size() < stats.getNumPassengerSeats();
    }

	public void openGui(EntityPlayer player) {
		player.openGui(LibVulpes.instance, GuiHandler.guiId.MODULAR.ordinal(), player.world, this.getEntityId(), -1,0);

		//Only handle the bypass on the server
		if(!world.isRemote)
			RocketInventoryHelper.addPlayerToInventoryBypass(player);
	}

	@Override
	public boolean processInitialInteract(EntityPlayer player,  EnumHand hand){
		if(world.isRemote) {
			//Due to forge's rigid handling of entities (NetHanlderPlayServer:866) needs to be handled differently for large rockets
			PacketHandler.sendToServer(new PacketEntity(this, (byte)PacketType.SENDINTERACT.ordinal()));
			return interact(player);
		}
		return true;

	}


	public boolean isBurningFuel() {
		return (getFuelAmount() > 0 || !Configuration.rocketRequireFuel) && ((!this.getPassengers().isEmpty() && getPassengerMovingForward() > 0) || !isInOrbit());
	}

	public float getPassengerMovingForward() {

		for(Entity entity : this.getPassengers()) {
			if(entity instanceof EntityPlayer) {
				return ((EntityPlayer) entity).moveForward;
			}
		}
		return 0f;
	}

	private boolean hasHumanPassenger() {

		for(Entity entity : this.getPassengers()) {
			if(entity instanceof EntityPlayer) {
				return true;
			}
		}
		return false;
	}

	public boolean isDescentPhase() {
		return Configuration.automaticRetroRockets && isInOrbit() && this.posY < 300 && (this.motionY < -0.4f || world.isRemote);
	}

	public boolean areEnginesRunning() {
		return (this.motionY > 0 || isDescentPhase() || (getPassengerMovingForward() > 0));
	}

	@Override
	public void onUpdate() {
		super.onUpdate();

		long deltaTime = world.getTotalWorldTime() - lastWorldTickTicked;
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

		if(this.ticksExisted > DESCENT_TIMER && isInOrbit() && !isInFlight())
			setInFlight(true);

		//Hackish crap to make clients mount entities immediately after server transfer and fire events
		//Known race condition... screw me...
		if(!world.isRemote && (this.isInFlight() || this.isInOrbit()) && this.ticksExisted  == 20) {
			//Deorbiting
			MinecraftForge.EVENT_BUS.post(new RocketEvent.RocketDeOrbitingEvent(this));
			PacketHandler.sendToNearby(new PacketEntity(this, (byte)PacketType.ROCKETLANDEVENT.ordinal()), world.provider.getDimension(), (int)posX, (int)posY, (int)posZ, 64);

			for(Entity riddenByEntity : getPassengers()) {
				if(riddenByEntity instanceof EntityPlayer) {
					EntityPlayer player = (EntityPlayer)riddenByEntity;


					if(player instanceof EntityPlayer)
						PacketHandler.sendToPlayer(new PacketEntity((INetworkEntity)this,(byte)PacketType.FORCEMOUNT.ordinal()), player);
				}
			}
		}

		//Update RCS mode
		if(getRCS() && rcs_mode_counter < 100)
			rcs_mode_counter++;
		else if(!getRCS() && rcs_mode_counter > 0)
		{
			rcs_mode_counter--;
			this.rotationYaw = 0;
		}

		if(isInFlight()) {
			boolean burningFuel = isBurningFuel();

			boolean descentPhase = isDescentPhase();

			if(burningFuel || descentPhase) {
				//Burn the rocket fuel
				if(!world.isRemote && !descentPhase)
					setFuelAmount((int) (getFuelAmount() - stats.getFuelRate(FuelType.LIQUID)*(Configuration.gravityAffectsFuel ? DimensionManager.getInstance().getDimensionProperties(world.provider.getDimension()).getGravitationalMultiplier() : 1f)));

				//Spawn in the particle effects for the engines
				int engineNum = 0;
				if(world.isRemote && Minecraft.getMinecraft().gameSettings.particleSetting < 2 && areEnginesRunning()) {
					for(Vector3F<Float> vec : stats.getEngineLocations()) {

						AtmosphereHandler handler;
						if(Minecraft.getMinecraft().gameSettings.particleSetting < 1 && world.getTotalWorldTime() % 10 == 0 && (engineNum < 8 || ((world.getTotalWorldTime()/10) % Math.max((stats.getEngineLocations().size()/8),1)) == (engineNum/8)) && ( (handler = AtmosphereHandler.getOxygenHandler(world.provider.getDimension())) == null || (handler.getAtmosphereType(this) != null && handler.getAtmosphereType(this).allowsCombustion())) )
							AdvancedRocketry.proxy.spawnParticle("rocketSmoke", world, this.posX + vec.x, this.posY + vec.y - 0.75, this.posZ +vec.z,0,0,0);

						for(int i = 0; i < 4; i++) {
							AdvancedRocketry.proxy.spawnParticle("rocketFlame", world, this.posX + vec.x, this.posY + vec.y - 0.75, this.posZ +vec.z,(this.rand.nextFloat() - 0.5f)/8f,-.75 ,(this.rand.nextFloat() - 0.5f)/8f);

						}
					}
				}
			}

			if(!this.getPassengers().isEmpty()) {

				for(Entity entity : this.getPassengers()) {
					entity.fallDistance = 0;
					this.fallDistance = 0;
				}

				//if the player holds the forward key then decelerate
				if(isInOrbit() && (burningFuel || descentPhase)) {
					float vel = descentPhase ? 1f : getPassengerMovingForward();
					this.motionY -= this.motionY*vel/50f;
				}
				this.velocityChanged = true;

			}
			else if(isInOrbit() && descentPhase) { //For unmanned rockets
				this.motionY -= this.motionY/50f;
				this.velocityChanged = true;
			}

			if(!world.isRemote) {
				//If out of fuel or descending then accelerate downwards
				if(isInOrbit() || !burningFuel) {
					this.motionY = Math.min(this.motionY - 0.001, 1);
				} else
					//this.motionY = Math.min(this.motionY + 0.001, 1);
					this.motionY += stats.getAcceleration() * deltaTime;


				double lastPosY = this.posY;
				double prevMotion = this.motionY;
				this.move(MoverType.SELF , 0, prevMotion*deltaTime, 0);

				
				boolean landedInSpace = DimensionManager.getInstance().getDimensionProperties(this.world.provider.getDimension()).isAsteroid() && this.posY < 64;
				boolean landedOnGround = lastPosY + prevMotion != this.posY && this.posY < 256;
				//Check to see if it's landed
				if((isInOrbit() || !burningFuel) && isInFlight() && (landedOnGround || landedInSpace)) {
					//Did  sending this packet cause problems?
					PacketHandler.sendToPlayersTrackingEntity(new PacketEntity(this, (byte)PacketType.ROCKETLANDEVENT.ordinal()), this);
					MinecraftForge.EVENT_BUS.post(new RocketEvent.RocketLandedEvent(this));
					this.motionY = 0;
					this.setInFlight(false);
					this.setInOrbit(false);
				}
				if(!isInOrbit() && (this.posY > Configuration.orbit)) {
					onOrbitReached();
				}


				//If the rocket falls out of the world while in orbit either fall back to earth or die
				if(this.posY < 0) {
					int dimId = world.provider.getDimension();

					if(dimId == Configuration.spaceDimId) {

						ISpaceObject obj = SpaceObjectManager.getSpaceManager().getSpaceStationFromBlockCoords(getPosition());

						if(obj != null) {
							int targetDimID = obj.getOrbitingPlanetId();

							Vector3F<Float> pos = storage.getDestinationCoordinates(targetDimID, true);
							if(pos != null) {
								setInOrbit(true);
								setInFlight(false);
								this.changeDimension(targetDimID, pos.x, Configuration.orbit, pos.z);
							}
							else 
								this.setDead();
						}
						else {
							Vector3F<Float> pos = storage.getDestinationCoordinates(0, true);
							if(pos != null) {
								setInOrbit(true);
								setInFlight(false);
								this.changeDimension(lastDimensionFrom, pos.x, Configuration.orbit, pos.z);
							}
							else 
								this.setDead();
						}
					}
					else
						this.setDead();
				}
			}
			else {
				this.move(MoverType.SELF , 0, this.motionY, 0);
			}
		}
		else if(DimensionManager.getInstance().getDimensionProperties(this.world.provider.getDimension()).isAsteroid() && getRCS()) {
		
			this.rotationYaw += (turningRight ? 5 : 0) - (turningLeft ? 5 : 0);
			double acc = this.getPassengerMovingForward()*.02;
			//RCS mode, steer like boat
			float yawAngle = (float)(this.rotationYaw*Math.PI/180f);
			this.motionX += acc*MathHelper.sin(-yawAngle);
			this.motionY += (turningUp ? 0.02 : 0) - (turningDownforWhat ? 0.02 : 0);
			this.motionZ += acc*MathHelper.cos(-yawAngle);
			this.motionX *= 0.9;
			this.motionY *= 0.9;
			this.motionZ *= 0.9;
			
			this.move(MoverType.SELF , this.motionX, this.motionY, this.motionZ);
		}
	}

	public void onTurnRight(boolean state) {
		turningRight = state;
		PacketHandler.sendToServer(new PacketEntity((INetworkEntity) this, (byte)EntityRocket.PacketType.TURNUPDATE.ordinal()));
	}
	
	public void onTurnLeft(boolean state) {
		turningLeft = state;
		PacketHandler.sendToServer(new PacketEntity((INetworkEntity) this, (byte)EntityRocket.PacketType.TURNUPDATE.ordinal()));
	}
	
	public void onUp(boolean state) {
		turningUp = state;
		PacketHandler.sendToServer(new PacketEntity((INetworkEntity) this, (byte)EntityRocket.PacketType.TURNUPDATE.ordinal()));
	}
	
	public void onDown(boolean state) {
		turningDownforWhat = state;
		PacketHandler.sendToServer(new PacketEntity((INetworkEntity) this, (byte)EntityRocket.PacketType.TURNUPDATE.ordinal()));
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
		long targetSatellite;
		if(storage.getGuidanceComputer() != null && (targetSatellite = storage.getGuidanceComputer().getTargetSatellite()) != -1L) {
			SatelliteBase sat = DimensionManager.getInstance().getSatellite(targetSatellite);
			for(TileEntity tile : storage.getTileEntityList()) {
				if(tile instanceof TileSatelliteHatch && !((IInventory)tile).getStackInSlot(0).isEmpty()) {
					((IInventory)tile).setInventorySlotContents(0, sat.getItemStackFromSatellite());
					DimensionManager.getInstance().getDimensionProperties(sat.getDimensionId()).removeSatellite(targetSatellite);
					break;
				}
			}
			this.motionY = -this.motionY;
			setInOrbit(true);
			
		}
		else if(!stats.hasSeat()) {

			TileGuidanceComputer computer = storage.getGuidanceComputer();
			if(computer != null && computer.getStackInSlot(0) != null &&
					computer.getStackInSlot(0).getItem() instanceof ItemAsteroidChip) {
				//make it 30 minutes with one drill
				float drillingPower = stats.getDrillingPower();
				
				float asteroidDrillingMult = 1f;
				
				ItemStack stack = storage.getGuidanceComputer().getStackInSlot(0);

				AsteroidSmall asteroid = Configuration.asteroidTypes.get(((ItemAsteroidChip)stack.getItem()).getType(stack));

				if(asteroid != null) {
					asteroidDrillingMult = asteroid.timeMultiplier;
				}
				
				MissionOreMining miningMission = new MissionOreMining((long)(asteroidDrillingMult*Configuration.asteroidMiningTimeMult*(drillingPower == 0f ? 36000 : 360/stats.getDrillingPower())), this, connectedInfrastructure);
				DimensionProperties properties = DimensionManager.getEffectiveDimId(world, getPosition());

				miningMission.setDimensionId(world);
				properties.addSatallite(miningMission, world);

				if(!world.isRemote)
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

			destinationDimId = storage.getDestinationDimId(this.world.provider.getDimension(), (int)this.posX, (int)this.posZ);
			if(destinationDimId == this.world.provider.getDimension()) {
				Vector3F<Float> pos = storage.getDestinationCoordinates(destinationDimId, true);
				storage.setDestinationCoordinates(new Vector3F<Float>((float)this.posX, (float)this.posY, (float)this.posZ), this.world.provider.getDimension());
				if(pos != null) {
					this.setInOrbit(true);
					this.motionY = -this.motionY;
					
					//unlink any connected tiles
					Iterator<IInfrastructure> connectedTiles = connectedInfrastructure.iterator();
					while(connectedTiles.hasNext()) {
						connectedTiles.next().unlinkRocket();
						connectedTiles.remove();
					}
					
					this.setPositionAndUpdate(pos.x, Configuration.orbit, pos.z);
					return;
				}
				else {

					//Make player confirm deorbit if a player is riding the rocket
					if(hasHumanPassenger()) {
						setInFlight(false);
						pos.y = (float) Configuration.orbit;

					}
					this.setInOrbit(true);
					this.motionY = -this.motionY;
					//unlink any connected tiles
					
					Iterator<IInfrastructure> connectedTiles = connectedInfrastructure.iterator();
					while(connectedTiles.hasNext()) {
						connectedTiles.next().unlinkRocket();
						connectedTiles.remove();
					}
					
					this.setPositionAndUpdate(this.posX, Configuration.orbit, this.posZ);
					return;
				}
				
			}
			else if(DimensionManager.getInstance().canTravelTo(destinationDimId)) {
				Vector3F<Float> pos = storage.getDestinationCoordinates(destinationDimId, true);
				storage.setDestinationCoordinates(new Vector3F<Float>((float)this.posX, (float)this.posY, (float)this.posZ), this.world.provider.getDimension());
				if(pos != null) {
					this.setInOrbit(true);
					this.motionY = -this.motionY;
					this.changeDimension(destinationDimId, pos.x, Configuration.orbit, pos.z);
					return;
				}
				else {

					//Make player confirm deorbit if a player is riding the rocket
					if(hasHumanPassenger()) {
						setInFlight(false);
						pos.y = (float) Configuration.orbit;

					}
					this.setInOrbit(true);
					this.motionY = -this.motionY;
					
					this.changeDimension(destinationDimId, this.posX, Configuration.orbit, this.posZ);
					return;
				}
			}
			else {
				//Make rocket return semi nearby
				int offX = (world.rand.nextInt() % 256) - 128;
				int offZ = (world.rand.nextInt() % 256) - 128;
				this.setInOrbit(true);
				this.motionY = -this.motionY;
				this.setPosition(posX + offX, posY, posZ + offZ);
				
				//unlink any connected tiles
				Iterator<IInfrastructure> connectedTiles = connectedInfrastructure.iterator();
				while(connectedTiles.hasNext()) {
					connectedTiles.next().unlinkRocket();
					connectedTiles.remove();
				}

				//this.setDead();
				//TODO: satellite event?
			}
		}
		else {
			unpackSatellites();
			//TODO: maybe add orbit dimension
			this.motionY = -this.motionY;
			setInOrbit(true);
			//If going to a station or something make sure to set coords accordingly
			//If in space land on the planet, if on the planet go to space
			if((destinationDimId == Configuration.spaceDimId || this.world.provider.getDimension() == Configuration.spaceDimId) && this.world.provider.getDimension() != destinationDimId) {
				Vector3F<Float> pos = storage.getDestinationCoordinates(destinationDimId, true);
				storage.setDestinationCoordinates(new Vector3F<Float>((float)this.posX, (float)this.posY, (float)this.posZ), this.world.provider.getDimension());
				if(pos != null) {

					//Make player confirm deorbit if a player is riding the rocket
					if(hasHumanPassenger()) {
						setInFlight(false);
						pos.y = (float) Configuration.orbit;

					}

					this.changeDimension(destinationDimId, pos.x, pos.y, pos.z);
					return;
				}
			}
			
			
			//if coordinates are overridden, make sure we grab them
			Vector3F<Float> destPos = storage.getDestinationCoordinates(destinationDimId, true);
			if(destPos == null)
				destPos = new Vector3F<Float>((float)posX, (float)Configuration.orbit, (float)posZ);
			
			if(hasHumanPassenger()) {
				//Make player confirm deorbit if a player is riding the rocket
				setInFlight(false);

				if(DimensionManager.getInstance().getDimensionProperties(destinationDimId).getName().equals("Luna")) {
					for(Entity player : this.getPassengers()) {
						if(player instanceof EntityPlayer) {
							ARAchivements.MOON_LANDING.trigger((EntityPlayerMP) player);
							if(!DimensionManager.hasReachedMoon)
								ARAchivements.ONE_SMALL_STEP.trigger((EntityPlayerMP) player);
						}
					}
					DimensionManager.hasReachedMoon = true;
				}
			}
			
			//Reset override coords
			setOverriddenCoords(-1, 0, 0, 0);
			
			if(destinationDimId != this.world.provider.getDimension())
				this.changeDimension(!DimensionManager.getInstance().isDimensionCreated(this.world.provider.getDimension()) ? 0 : destinationDimId, destPos.x, Configuration.orbit, destPos.z);
			else
			{
				List<Entity> eList = this.getPassengers();
				for(Entity e : eList) {
					e.dismountRidingEntity();
					e.setPositionAndUpdate(destPos.x, Configuration.orbit, destPos.z);
				}
				this.setPositionAndUpdate(destPos.x, Configuration.orbit, destPos.z);
				this.ticksExisted = 0;
				((WorldServer)world).resetUpdateEntityTick();
				for(Entity e : eList) {
					e.startRiding(this, true);
				}
			}
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

					//in case of no NBT data or the like
					if(object == null) {
						tile.setInventorySlotContents(0, ItemStack.EMPTY);
						continue;
					}
					
					SpaceObjectManager.getSpaceManager().moveStationToBody(object, 
									DimensionManager.getEffectiveDimId(this.world.provider.getDimension(), getPosition()).getId() );

					//Vector3F<Integer> spawn = object.getSpawnLocation();

					object.onModuleUnpack(storage);
					tile.setInventorySlotContents(0, ItemStack.EMPTY);
				}
			}
			else {
				int destinationId = storage.getDestinationDimId(world.provider.getDimension(), (int)posX, (int)posZ);
				DimensionProperties properties = DimensionManager.getEffectiveDimId(world, this.getPosition());
				int world2;
				if(destinationId == Configuration.spaceDimId || destinationId == Constants.INVALID_PLANET)
					world2 = properties.getId();
				else
					world2 = destinationId;
				
				properties.addSatallite(satellite, world2, world.isRemote);
				tile.setInventorySlotContents(0, ItemStack.EMPTY);
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

		//Get destination dimid and lock the computer
		//TODO: lock the computer
		destinationDimId = storage.getDestinationDimId(world.provider.getDimension(), (int)this.posX, (int)this.posZ);

		//TODO: make sure this doesn't break asteriod mining
		if(!(DimensionManager.getInstance().canTravelTo(destinationDimId) || (destinationDimId == Constants.INVALID_PLANET && storage.getSatelliteHatches().size() != 0))) {
			setError(LibVulpes.proxy.getLocalizedString("error.rocket.cannotGetThere"));
			return;
		}

		int finalDest = destinationDimId;
		if(destinationDimId == Configuration.spaceDimId) {
			ISpaceObject obj = null;
			Vector3F<Float> vec = storage.getDestinationCoordinates(destinationDimId,false);

			if(vec != null)
				obj = SpaceObjectManager.getSpaceManager().getSpaceStationFromBlockCoords(new BlockPos(vec.x, vec.y, vec.z));

			if( obj != null)
				finalDest = obj.getOrbitingPlanetId();
			else { 
				setError(LibVulpes.proxy.getLocalizedString("error.rocket.destinationNotExist"));
				return;
			}
		}

		//If we're on a space station get the id of the planet, not the station
		int thisDimId = this.world.provider.getDimension();
		if(this.world.provider.getDimension() == Configuration.spaceDimId) {
			ISpaceObject object = SpaceObjectManager.getSpaceManager().getSpaceStationFromBlockCoords(this.getPosition());
			if(object != null)
				thisDimId = object.getProperties().getParentProperties().getId();
		}

		//Check to see if it's possible to reach
		if(finalDest != Constants.INVALID_PLANET && (!storage.hasWarpCore() || DimensionManager.getInstance().getDimensionProperties(finalDest).getStarId() != DimensionManager.getInstance().getDimensionProperties(thisDimId).getStarId()) && !DimensionManager.getInstance().areDimensionsInSamePlanetMoonSystem(finalDest, thisDimId)) {
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
		storage.pasteInWorld(this.world, (int)(this.posX - storage.getSizeX()/2f), (int)this.posY, (int)(this.posZ - storage.getSizeZ()/2f));
		this.setDead();
	}

	@Override
	public void setDead() {
		super.setDead();

		if(storage != null && storage.world.displayListIndex != -1)
			GLAllocation.deleteDisplayLists(storage.world.displayListIndex);

		//unlink any connected tiles
		Iterator<IInfrastructure> connectedTiles = connectedInfrastructure.iterator();
		while(connectedTiles.hasNext()) {
			connectedTiles.next().unlinkRocket();
			connectedTiles.remove();
		}

	}

	public void setOverriddenCoords(int dimId, float x, float y, float z) {
		TileGuidanceComputer tile = storage.getGuidanceComputer();
		if(tile != null) {
			tile.setFallbackDestination(dimId, new Vector3F<Float>(x, y, z));
		}
	}



	@Override
	public Entity changeDimension(int newDimId) {
		return changeDimension(newDimId, this.posX, (double)Configuration.orbit, this.posZ);
	}

	@Nullable
	public Entity changeDimension(int dimensionIn, double posX, double y, double posZ)
	{
		if (!this.world.isRemote && !this.isDead)
		{			

			if(!DimensionManager.getInstance().canTravelTo(dimensionIn)) {
				AdvancedRocketry.logger.warn("Rocket trying to travel from Dim" + this.world.provider.getDimension() + " to Dim " + dimensionIn + ".  target not accessible by rocket from launch dim");
				return null;
			}

			lastDimensionFrom = this.world.provider.getDimension();

			List<Entity> passengers = getPassengers();

			if (!net.minecraftforge.common.ForgeHooks.onTravelToDimension(this, dimensionIn)) return null;
			this.world.profiler.startSection("changeDimension");
			MinecraftServer minecraftserver = this.getServer();
			int i = this.dimension;
			WorldServer worldserver = minecraftserver.getWorld(i);
			WorldServer worldserver1 = minecraftserver.getWorld(dimensionIn);
			this.dimension = dimensionIn;

			if (i == 1 && dimensionIn == 1)
			{
				worldserver1 = minecraftserver.getWorld(0);
				this.dimension = 0;
			}

			this.world.removeEntity(this);
			this.isDead = false;
			this.world.profiler.startSection("reposition");
			BlockPos blockpos;


			double d0 = this.posX;
			double d1 = this.posZ;
			double d2 = 8.0D;


			d0 = MathHelper.clamp(d0 * 8.0D, worldserver1.getWorldBorder().minX() + 16.0D, worldserver1.getWorldBorder().maxX() - 16.0D);
			d1 = MathHelper.clamp(d1 * 8.0D, worldserver1.getWorldBorder().minZ() + 16.0D, worldserver1.getWorldBorder().maxZ() - 16.0D);


			d0 = (double)MathHelper.clamp((int)d0, -29999872, 29999872);
			d1 = (double)MathHelper.clamp((int)d1, -29999872, 29999872);
			float f = this.rotationYaw;
			this.setLocationAndAngles(d0, this.posY, d1, 90.0F, 0.0F);
			Teleporter teleporter = new TeleporterNoPortal(worldserver1);
			teleporter.placeInExistingPortal(this, f);


			worldserver.updateEntityWithOptionalForce(this, false);
			this.world.profiler.endStartSection("reloading");
			Entity entity = EntityList.newEntity(this.getClass(), worldserver1);

			if (entity != null)
			{

				this.moveToBlockPosAndAngles(new BlockPos(posX, y, posZ), entity.rotationYaw, entity.rotationPitch);
				((EntityRocket)entity).copyDataFromOld(this);

				entity.forceSpawn = true;
				worldserver1.spawnEntity(entity);
				worldserver1.updateEntityWithOptionalForce(entity, true);

				int timeOffset = 0;
				for(Entity e : passengers) {
					//Fix that darn random crash?
					worldserver.resetUpdateEntityTick();
					worldserver1.resetUpdateEntityTick();
					//Transfer the player if applicable

					//Need to handle our own removal to avoid race condition where player is mounted on client on the old entity but is already mounted to the new one on server
					//PacketHandler.sendToPlayer(new PacketEntity(this, (byte)PacketType.DISMOUNTCLIENT.ordinal()), (EntityPlayer) e);

					PlanetEventHandler.addDelayedTransition(new TransitionEntity(worldserver.getTotalWorldTime(), e, dimensionIn, new BlockPos(posX + 16, y, posZ), entity));

					//minecraftserver.getPlayerList().transferPlayerToDimension((EntityPlayerMP)e, dimensionIn, teleporter);

					//e.setLocationAndAngles(posX, Configuration.orbit, posZ, this.rotationYaw, this.rotationPitch);

					//e.startRiding(entity);


					//e.playerNetServerHandler.sendPacket(new SPacketRespawn(e.dimension, e.worldObj.getDifficulty(), worldserver1.getWorldInfo().getTerrainType(), ((EntityPlayerMP)e).interactionManager.getGameType()));
					//((WorldServer)startWorld).getPlayerManager().removePlayer(player);

				}
			}

			this.isDead = true;
			this.world.profiler.endSection();
			worldserver.resetUpdateEntityTick();
			worldserver1.resetUpdateEntityTick();
			this.world.profiler.endSection();
			return entity;
		}
		else
		{
			return null;
		}
	}

	/**
	 * Prepares this entity in new dimension by copying NBT data from entity in old dimension
	 */
	public void copyDataFromOld(Entity entityIn)
	{
		NBTTagCompound nbttagcompound = entityIn.writeToNBT(new NBTTagCompound());
		nbttagcompound.removeTag("Dimension");
		nbttagcompound.removeTag("Passengers");
		this.readFromNBT(nbttagcompound);
		this.timeUntilPortal = entityIn.timeUntilPortal;
	}

	protected void readNetworkableNBT(NBTTagCompound nbt) {
		//Normal function checks for the existance of the data anyway
		readEntityFromNBT(nbt);
	}

	@Override
	protected void readEntityFromNBT(NBTTagCompound nbt) {

		setInOrbit(isInOrbit = nbt.getBoolean("orbit"));
		rcs_mode = nbt.getBoolean("rcs_mode");
		setRCS(rcs_mode);
		rcs_mode_counter = nbt.getInteger("rcs_mode_cnt");
		
		stats.readFromNBT(nbt);

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

		if(nbt.hasKey("infrastructure")) {
			NBTTagList tagList = nbt.getTagList("infrastructure", 10);
			for (int i = 0; i < tagList.tagCount(); i++) {
				int coords[] = tagList.getCompoundTagAt(i).getIntArray("loc");
				
				infrastructureCoords.add(new HashedBlockPosition(coords[0], coords[1], coords[2]));
				
			}
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
		nbt.setBoolean("rcs_mode", rcs_mode);
		nbt.setInteger("rcs_mode_cnt", rcs_mode_counter);
		stats.writeToNBT(nbt);

		if(!infrastructureCoords.isEmpty()) {
			NBTTagList itemList = new NBTTagList();
			for(HashedBlockPosition inf : infrastructureCoords)
			{

				NBTTagCompound tag = new NBTTagCompound();
				tag.setIntArray("loc", new int[] {inf.x, inf.y, inf.z});
				itemList.appendTag(tag);

			}
			nbt.setTag("infrastructure", itemList);
		}

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

		//TODO handle non tile Infrastructure


		nbt.setInteger("lastDimensionFrom", lastDimensionFrom);
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
		else if(packetId == PacketType.TURNUPDATE.ordinal()) {
			nbt.setBoolean("left", in.readBoolean());
			nbt.setBoolean("right", in.readBoolean());
			nbt.setBoolean("up", in.readBoolean());
			nbt.setBoolean("down", in.readBoolean());
		}
	}

	@Override
	public void writeDataToNetwork(ByteBuf out, byte id) {

		if(id == PacketType.RECIEVENBT.ordinal()) {
			storage.writeToNetwork(out);
		}
		else if(id == PacketType.SENDPLANETDATA.ordinal()) {
			if(world.isRemote)
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
		else if(id == PacketType.TURNUPDATE.ordinal()) {
			out.writeBoolean(turningLeft);
			out.writeBoolean(turningRight);
			out.writeBoolean(turningUp);
			out.writeBoolean(turningDownforWhat);
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
			if(player.getRidingEntity() == this) //Prevent cheating
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
			if(!acceptedPacket) {
				acceptedPacket = true;
				player.setPositionAndRotation(this.posX, this.posY, this.posZ, player.rotationYaw, player.rotationPitch);
				player.startRiding(this);
				MinecraftForge.EVENT_BUS.post(new RocketEvent.RocketDeOrbitingEvent(this));
			}
		}
		else if(id == PacketType.LAUNCH.ordinal()) {

			if(this.getPassengers().contains(player))
				this.prepareLaunch();
		}
		else if(id == PacketType.CHANGEWORLD.ordinal()) {
			AdvancedRocketry.proxy.changeClientPlayerWorld(storage.world);
		}
		else if(id == PacketType.REVERTWORLD.ordinal()) {
			AdvancedRocketry.proxy.changeClientPlayerWorld(this.world);
		}
		else if(id == PacketType.OPENPLANETSELECTION.ordinal()) {
			player.openGui(LibVulpes.instance, GuiHandler.guiId.MODULARFULLSCREEN.ordinal(), player.world, this.getEntityId(), -1,0);
		}
		else if(id == PacketType.SENDPLANETDATA.ordinal()) {
			ItemStack stack = storage.getGuidanceComputer().getStackInSlot(0);
			if(stack != null && stack.getItem() == AdvancedRocketryItems.itemPlanetIdChip) {
				((ItemPlanetIdentificationChip)AdvancedRocketryItems.itemPlanetIdChip).setDimensionId(stack, nbt.getInteger("selection"));

				//Send data back to sync destination dims
				if(!world.isRemote) {
					PacketHandler.sendToPlayersTrackingEntity(new PacketEntity(this, (byte)PacketType.SENDPLANETDATA.ordinal()), this);
				}
			}
		}
		else if(id == PacketType.DISCONNECTINFRASTRUCTURE.ordinal()) {
			int pos[] = nbt.getIntArray("pos");

			connectedInfrastructure.remove(new HashedBlockPosition(pos[0], pos[1], pos[2]));

			TileEntity tile = world.getTileEntity(new BlockPos(pos[0], pos[1], pos[2]));
			if(tile instanceof IInfrastructure) {
				((IInfrastructure)tile).unlinkRocket();
				connectedInfrastructure.remove(tile);
			}
		}
		else if(id == PacketType.ROCKETLANDEVENT.ordinal() && world.isRemote) {
			MinecraftForge.EVENT_BUS.post(new RocketEvent.RocketLandedEvent(this));
		}
		else if(id == PacketType.DISMOUNTCLIENT.ordinal() && world.isRemote) {
			player.dismountRidingEntity();
			//this.removePassenger(player);
		}
		else if(id == PacketType.TOGGLE_RCS.ordinal() && !world.isRemote) {
			this.toggleRCS();
		}
		else if(id == PacketType.TURNUPDATE.ordinal()) {
			this.turningLeft = nbt.getBoolean("left");
			this.turningRight = nbt.getBoolean("right");
			this.turningUp = nbt.getBoolean("up");
			this.turningDownforWhat = nbt.getBoolean("down");
		}
		else if(id >= STATION_LOC_OFFSET + BUTTON_ID_OFFSET) {
			int id2 = id - (STATION_LOC_OFFSET + BUTTON_ID_OFFSET) - 1;
			setDestLandingPad(id2);

			//propagate change back to the clients
			if(!world.isRemote)
				PacketHandler.sendToPlayersTrackingEntity(new PacketEntity(this, id), this);
		}
		else if(id > BUTTON_ID_OFFSET) {
			TileEntity tile = storage.getGUItiles().get(id - BUTTON_ID_OFFSET - tilebuttonOffset);

			//Welcome to super hack time with packets
			//Due to the fact the client uses the player's current world to open the gui, we have to move the client between worlds for a bit
			PacketHandler.sendToPlayer(new PacketEntity(this, (byte)PacketType.CHANGEWORLD.ordinal()), player);
			storage.getBlockState(tile.getPos()).getBlock().onBlockActivated(storage.world, tile.getPos(), storage.getBlockState(tile.getPos()), player, EnumHand.MAIN_HAND, EnumFacing.DOWN, 0, 0, 0);
			PacketHandler.sendToPlayer(new PacketEntity(this, (byte)PacketType.REVERTWORLD.ordinal()), player);
		}
	}

	private void setDestLandingPad(int padIndex) {
		ItemStack slot0 = storage.getGuidanceComputer().getStackInSlot(0);
		int uuid;
		//Station location select
		if( slot0 != null && slot0.getItem() instanceof ItemStationChip && (uuid = ItemStationChip.getUUID(slot0)) != 0) {
			ISpaceObject obj = SpaceObjectManager.getSpaceManager().getSpaceStation(uuid);

			if(obj instanceof SpaceObject) {

				if(padIndex == -1) {
					storage.getGuidanceComputer().setLandingLocation(uuid, null);
				}
				else {

					StationLandingLocation location = ((SpaceObject) obj).getLandingPads().get(padIndex);
					if(location != null && !location.getOccupied())
						storage.getGuidanceComputer().setLandingLocation(uuid, location);
				}
			}

			StationLandingLocation location = storage.getGuidanceComputer().getLandingLocation(uuid);
			landingPadDisplayText.setText(location != null ? location.toString() : "None Selected");
		}
	}

	@Override
	public void updatePassenger(Entity entity)
	{
		if (entity != null )
		{
			//Bind player to the seat
			if(this.storage != null) {
				try {
					HashedBlockPosition seatPos = stats.getPassengerSeat(this.getPassengers().indexOf(entity));
					//Conditional b/c for some reason client/server positions do not match
					float xOffset = this.storage.getSizeX() % 2 == 0 ? 0.5f : 0f;
					float zOffset = this.storage.getSizeZ() % 2 == 0 ? 0.5f : 0f;
					float halfy = storage.getSizeY()/2f;
					
					double xPos = seatPos.x + xOffset;
					double yPos = seatPos.y  - 0.5f - halfy;
					double zPos = seatPos.z + zOffset ;
					float angle = (float)(getRCSRotateProgress()*0.9f*Math.PI/180f);
					
					double yNew = (yPos)*MathHelper.cos(angle) + (-zPos - 0.5)*MathHelper.sin(angle);
					double zNew = zPos*MathHelper.cos(angle) + (yPos + 1)*MathHelper.sin(angle);
					yPos = yNew + this.posY + halfy;
					zPos = zNew;
					
					//Now do yaw
					float yawAngle = (float)(this.rotationYaw*Math.PI/180f);
					double xNew = (xPos)*MathHelper.cos(-yawAngle) + (zPos)*MathHelper.sin(-yawAngle);
					zNew = zPos*MathHelper.cos(yawAngle) + (xPos)*MathHelper.sin(yawAngle);
					xPos = this.posX + xNew ;
					zPos = this.posZ + zNew;
					
					
					entity.setPosition(xPos, yPos, zPos );
				} catch (IndexOutOfBoundsException e) {
					entity.setPosition(this.posX , this.posY , this.posZ );
				}
			}
			else
				entity.setPosition(this.posX , this.posY , this.posZ );
		}
	}

	@Override
	public List<ModuleBase> getModules(int ID, EntityPlayer player) {
		List<ModuleBase> modules;
		//If the rocket is flight don't load the interface
		modules = new LinkedList<ModuleBase>();

		if(ID == GuiHandler.guiId.MODULAR.ordinal()) {
			//Backgrounds
			if(world.isRemote) {
				modules.add(new ModuleImage(173, 0, new IconResource(128, 0, 48, 86, CommonResources.genericBackground)));
				modules.add(new ModuleImage(173, 86, new IconResource(98, 0, 78, 83, CommonResources.genericBackground)));
				modules.add(new ModuleImage(173, 168, new IconResource(98, 168, 78, 3, CommonResources.genericBackground)));
			}

			//Fuel
			modules.add(new ModuleProgress(192, 7, 0, new ProgressBarImage(2, 173, 12, 71, 17, 6, 3, 69, 1, 1, EnumFacing.UP, TextureResources.rocketHud), this));

			//TODO DEBUG tiles!
			List<TileEntity> tiles = storage.getGUItiles();
			for(int i = 0; i < tiles.size(); i++) {
				TileEntity tile  = tiles.get(i);
				IBlockState state = storage.getBlockState(tile.getPos());
				try {
					modules.add(new ModuleSlotButton(8 + 18* (i % 9), 17 + 18*(i/9), i + tilebuttonOffset, this, new ItemStack(state.getBlock(), 1, state.getBlock().getMetaFromState(state)), world));
				} catch (NullPointerException e) {

				}
			}

			//Add buttons
			modules.add(new ModuleButton(180, 140, 0, LibVulpes.proxy.getLocalizedString("msg.entity.rocket.disass"), this, zmaster587.libVulpes.inventory.TextureResources.buttonBuild, 64, 20));

			//modules.add(new ModuleButton(180, 95, 1, "", this, TextureResources.buttonLeft, 10, 16));
			//modules.add(new ModuleButton(202, 95, 2, "", this, TextureResources.buttonRight, 10, 16));

			modules.add(new ModuleButton(180, 114, 1, LibVulpes.proxy.getLocalizedString("msg.entity.rocket.seldst"), this,  zmaster587.libVulpes.inventory.TextureResources.buttonBuild, 64,20));
			//modules.add(new ModuleText(180, 114, "Inventories", 0x404040));
		}
		else {
			ItemStack slot0 = storage.getGuidanceComputer().getStackInSlot(0);
			int uuid;
			//Station location select
			if( slot0 != null && slot0.getItem() instanceof ItemStationChip && (uuid = ItemStationChip.getUUID(slot0)) != 0) {
				ISpaceObject obj = SpaceObjectManager.getSpaceManager().getSpaceStation(uuid);

				modules.add(new ModuleStellarBackground(0, 0, zmaster587.libVulpes.inventory.TextureResources.starryBG));
				//modules.add(new ModuleImage(0, 0, icon));

				if(obj == null)
					return modules;

				List<ModuleBase> list2 = new LinkedList<ModuleBase>();
				ModuleButton button = new ModuleButton(0, 0, STATION_LOC_OFFSET, LibVulpes.proxy.getLocalizedString("msg.entity.rocket.clear"), this, TextureResources.buttonGeneric, 72, 18);
				list2.add(button);

				int i = 1;
				for( StationLandingLocation pos : ((SpaceObject)obj).getLandingPads()) 
				{
					button = new ModuleButton(0, i*18, i + STATION_LOC_OFFSET, pos.toString(), this, TextureResources.buttonGeneric, 72, 18);
					list2.add(button);

					if(pos.getOccupied())
						button.setColor(0xFF0000);

					i++;
				}

				ModuleContainerPan pan = new ModuleContainerPan(25, 25, list2, new LinkedList<ModuleBase>(), null, 256, 256, 0, -48, 258, 256);
				modules.add(pan);

				StationLandingLocation location = storage.getGuidanceComputer().getLandingLocation(uuid);

				landingPadDisplayText.setText(location != null ? location.toString() : LibVulpes.proxy.getLocalizedString("msg.entity.rocket.none"));
				modules.add(landingPadDisplayText);
			}
			else {
				DimensionProperties properties = DimensionManager.getEffectiveDimId(world, this.getPosition());
				while(properties.getParentProperties() != null) properties = properties.getParentProperties();

				if(storage.hasWarpCore())
					container = new ModulePlanetSelector(properties.getStarId(), zmaster587.libVulpes.inventory.TextureResources.starryBG, this, this, true);
				else
					container = new ModulePlanetSelector(properties.getId(), zmaster587.libVulpes.inventory.TextureResources.starryBG, this, false);
				container.setOffset(1000, 1000);
				modules.add(container);
			}
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
	public boolean startRiding(Entity entityIn, boolean force) {
		// TODO Auto-generated method stub
		return super.startRiding(entityIn, force);
	}

	@Override
	public boolean startRiding(Entity entityIn) {
		// TODO Auto-generated method stub
		return super.startRiding(entityIn);
	}

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
			PacketHandler.sendToServer(new PacketEntity(this, (byte)(buttonId + BUTTON_ID_OFFSET)));
			//Minecraft.getMinecraft().thePlayer.closeScreen();

			if(buttonId < STATION_LOC_OFFSET) {
				TileEntity tile = storage.getGUItiles().get(buttonId - tilebuttonOffset);
				storage.getBlockState(tile.getPos()).getBlock().onBlockActivated(storage.world, tile.getPos(), storage.getBlockState(tile.getPos()), Minecraft.getMinecraft().player, EnumHand.MAIN_HAND, EnumFacing.DOWN, 0, 0, 0);
			}
		}
	}

	@Override
	public boolean canInteractWithContainer(EntityPlayer entity) {
		boolean ret = !this.isDead && this.getDistanceToEntity(entity) < 64;
		if(!ret)
			RocketInventoryHelper.removePlayerFromInventoryBypass(entity);

		RocketInventoryHelper.updateTime(entity, world.getWorldTime());

		return ret;
	}

	@Override
	public StatsRocket getRocketStats() {
		return stats;
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

	@Override
	public boolean isPlanetKnown(IDimensionProperties properties) {
		return !Configuration.planetsMustBeDiscovered || DimensionManager.getInstance().knownPlanets.contains(properties.getId());
	}

	@Override
	public boolean isStarKnown(StellarBody body) {
		return true;
	}
}
