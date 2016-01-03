package zmaster587.advancedRocketry.entity;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;

import org.lwjgl.util.vector.Vector3f;

import io.netty.buffer.ByteBuf;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import zmaster587.advancedRocketry.AdvancedRocketry;
import zmaster587.advancedRocketry.Inventory.GuiHandler;
import zmaster587.advancedRocketry.Inventory.TextureResources;
import zmaster587.advancedRocketry.Inventory.modules.IButtonInventory;
import zmaster587.advancedRocketry.Inventory.modules.IModularInventory;
import zmaster587.advancedRocketry.Inventory.modules.IProgressBar;
import zmaster587.advancedRocketry.Inventory.modules.ModuleBase;
import zmaster587.advancedRocketry.Inventory.modules.ModuleButton;
import zmaster587.advancedRocketry.Inventory.modules.ModuleImage;
import zmaster587.advancedRocketry.Inventory.modules.ModuleProgress;
import zmaster587.advancedRocketry.Inventory.modules.ModuleSlotButton;
import zmaster587.advancedRocketry.Inventory.modules.ModuleText;
import zmaster587.advancedRocketry.api.AdvancedRocketryItems;
import zmaster587.advancedRocketry.api.Configuration;
import zmaster587.advancedRocketry.api.EntityRocketBase;
import zmaster587.advancedRocketry.api.IInfrastructure;
import zmaster587.advancedRocketry.api.RocketEvent;
import zmaster587.advancedRocketry.api.SatelliteRegistry;
import zmaster587.advancedRocketry.api.StatsRocket;
import zmaster587.advancedRocketry.api.RocketEvent.RocketLaunchEvent;
import zmaster587.advancedRocketry.api.dimension.DimensionManager;
import zmaster587.advancedRocketry.api.dimension.DimensionProperties;
import zmaster587.advancedRocketry.api.fuel.FuelRegistry;
import zmaster587.advancedRocketry.api.fuel.FuelRegistry.FuelType;
import zmaster587.advancedRocketry.api.network.PacketEntity;
import zmaster587.advancedRocketry.api.network.PacketHandler;
import zmaster587.advancedRocketry.api.satellite.SatelliteBase;
import zmaster587.advancedRocketry.api.stations.SpaceObject;
import zmaster587.advancedRocketry.client.render.util.ProgressBarImage;
import zmaster587.advancedRocketry.event.PlanetEventHandler;
import zmaster587.advancedRocketry.item.ItemPackedStructure;
import zmaster587.advancedRocketry.tile.TileGuidanceComputer;
import zmaster587.advancedRocketry.tile.Satellite.TileSatelliteHatch;
import zmaster587.advancedRocketry.util.StorageChunk;
import zmaster587.advancedRocketry.world.util.TeleporterNoPortal;
import zmaster587.libVulpes.gui.CommonResources;
import zmaster587.libVulpes.interfaces.INetworkEntity;
import zmaster587.libVulpes.item.ItemLinker;
import zmaster587.libVulpes.util.IconResource;
import zmaster587.libVulpes.util.Vector3F;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
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

public class EntityRocket extends EntityRocketBase implements INetworkEntity, IModularInventory, IProgressBar, IButtonInventory {


	//Stores the blocks and tiles that make up the rocket
	public StorageChunk storage;

	//Stores other info about the rocket such as fuel and acceleration properties
	public StatsRocket stats;

	//true if the rocket is on decent
	private boolean isInOrbit;
	//True if the rocket isn't on the ground
	private boolean isInFlight;

	//stores the coordinates of infrastructures, used for when the world loads/saves
	private LinkedList<Vector3F<Integer>> infrastructureCoords;

	private SatelliteBase satallite;
	private int destinationDimId;
	//Offset for buttons linking to the tileEntityGrid
	private int tilebuttonOffset = 3;

	//Cannot do setDead to avoid index out of bounds in worldMulti
	private boolean dieNextTick = false;

	public enum PacketType {
		RECIEVENBT,
		SENDINTERACT,
		REQUESTNBT,
		FORCEMOUNT,
		LAUNCH,
		DECONSTRUCT,
		OPENGUI,
		CHANGEWORLD,
		REVERTWORLD
	}

	public EntityRocket(World p_i1582_1_) {
		super(p_i1582_1_);
		isInOrbit = false;
		stats = new StatsRocket();
		isInFlight = false;
		connectedInfrastructure = new LinkedList<IInfrastructure>();
		infrastructureCoords = new LinkedList<Vector3F<Integer>>();
	}

	public EntityRocket(World world, StorageChunk storage, StatsRocket stats, double x, double y, double z) {
		this(world);
		this.stats = stats;
		this.setPosition(x, y, z);
		this.storage = storage;
		initFromBounds();
		isInFlight = false;
	}

	@Override
	public AxisAlignedBB getBoundingBox() {
		if(storage != null) {
			return this.boundingBox;
		}
		return null;
	}

	@Override
	public void setPositionAndRotation2(double x, double y,
			double z, float p_70056_7_, float p_70056_8_,
			int p_70056_9_) {
		
		if( !worldObj.isRemote || (y < 270 && this.isInFlight()))
			super.setPositionAndRotation2(x, y, z, p_70056_7_, p_70056_8_, p_70056_9_);
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
		return this.dataWatcher.getWatchableObjectByte(16) == 1;
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
	}

	//Set the size and position of the rocket from storage
	public void initFromBounds() {		
		if(storage != null) {
			this.setSize(Math.max(storage.getSizeX(), storage.getSizeZ()), storage.getSizeY());
			this.setPosition(this.posX, this.posY, this.posZ);
		}
	}

	private boolean interact(EntityPlayer player) {
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
						if(this.getDistance(ItemLinker.getMasterX(heldItem), this.posY, ItemLinker.getMasterZ(heldItem)) < infrastructure.getMaxLinkDistance() - Math.max(storage.getSizeX(), storage.getSizeZ())) {
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
			else if(FluidContainerRegistry.isFilledContainer(heldItem) && (fuelMult = FuelRegistry.instance.getMultiplier(FuelType.LIQUID, (fluidStack = FluidContainerRegistry.getFluidForFilledItem(heldItem)).getFluid()) ) > 0 ) { 


				int amountToAdd = (int) (fuelMult*fluidStack.amount);
				this.addFuelAmount(amountToAdd);

				//if the player is not in creative then try to use the fluid container
				if(!player.capabilities.isCreativeMode) {
					ItemStack emptyStack = FluidContainerRegistry.drainFluidContainer(player.getHeldItem());

					if(player.inventory.addItemStackToInventory(emptyStack)) {
						player.getHeldItem().splitStack(1);
						if(player.getHeldItem().stackSize == 0)
							player.inventory.setInventorySlotContents(player.inventory.currentItem, null); 
					}
				}

				return true;
			}
		}

		//If player is holding shift open GUI
		if(player.isSneaking()) {
			player.openGui(AdvancedRocketry.instance, GuiHandler.guiId.MODULAR.ordinal(), player.worldObj, this.getEntityId(), -1,0);

			//Only handle the bypass on the server
			if(!worldObj.isRemote)
				PlanetEventHandler.addPlayerToInventoryBypass(player);
		}
		else if(!worldObj.isRemote && stats.hasSeat())
			player.mountEntity(this);
		return true;
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
		return (getFuelAmount() > 0 || !Configuration.rocketRequireFuel) && (!(this.riddenByEntity instanceof EntityPlayer) || !isInOrbit || ((EntityPlayer)this.riddenByEntity).moveForward > 0);
	}

	@Override
	public void onUpdate() {
		super.onUpdate();

		if(dieNextTick)
			this.setDead();

		//TODO move
		World.MAX_ENTITY_RADIUS = 100;

		if(isInFlight()) {
			boolean burningFuel = isBurningFuel();

			if(burningFuel) {
				//Burn the rocket fuel
				if(!worldObj.isRemote)
					setFuelAmount(getFuelAmount() - stats.getFuelRate(FuelType.LIQUID));

				//Spawn in the particle effects for the engines
				if(worldObj.isRemote  && (this.motionY > 0 || (riddenByEntity instanceof EntityPlayer && ((EntityPlayer)riddenByEntity).moveForward > 0))) {
					for(Vector3F<Float> vec : stats.getEngineLocations()) {

						for(int i = 0; i < 4; i++) {
							AdvancedRocketry.proxy.spawnParticle("rocketFlame", worldObj, this.posX + vec.x, this.posY + vec.y - 0.75, this.posZ +vec.z,(this.rand.nextFloat() - 0.5f)/8f,-.75 + this.motionY,(this.rand.nextFloat() - 0.5f)/8f);
						}
					}
				}
			}

			if(this.riddenByEntity instanceof EntityPlayer) {
				EntityPlayer player = (EntityPlayer)this.riddenByEntity;
				player.fallDistance = 0;
				this.fallDistance = 0;

				//Hackish crap to make clients mount entities immediately after server transfer and fire events
				if(!worldObj.isRemote && this.isInFlight() && this.ticksExisted == 20) {
					//Deorbiting
					MinecraftForge.EVENT_BUS.post(new RocketEvent.RocketDeOrbitingEvent(this));

					if(player instanceof EntityPlayer)
						PacketHandler.sendToPlayer(new PacketEntity((INetworkEntity)this,(byte)PacketType.FORCEMOUNT.ordinal()), player);

				}

				//if the player holds the forward key then decelerate
				if(isInOrbit && burningFuel)
					this.motionY -= this.motionY*player.moveForward/50f;
				this.velocityChanged = true;

			}

			//If out of fuel or descending then accelerate downwards
			if(isInOrbit || !burningFuel) {
				this.motionY = Math.min(this.motionY - 0.001, 1);
			} else
				//this.motionY = Math.min(this.motionY + 0.001, 1);
				this.motionY += stats.getAcceleration();

			double lastPosY = this.posY;
			double prevMotion = this.motionY;
			this.moveEntity(0, prevMotion, 0);

			//Check to see if it's landed
			if((isInOrbit || !burningFuel) && isInFlight() && lastPosY + prevMotion != this.posY) {
				MinecraftForge.EVENT_BUS.post(new RocketEvent.RocketLandedEvent(this));
				this.setInFlight(false);
				this.isInOrbit = false;
			}
			if((this.posY > Configuration.orbit) && !this.worldObj.isRemote) {
				onOrbitReached();
			}


			//If the rocket falls out of the world while in orbit either fall back to earth or die
			if(!worldObj.isRemote && this.posY < 0) {
				int dimId = worldObj.provider.dimensionId;

				if(dimId == Configuration.spaceDimId) {
					Vector3F<Float> pos = storage.getGuidanceComputer().getLandingLocation(dimId);
					storage.getGuidanceComputer().setReturnPosition(new Vector3F<Float>((float)this.posX, (float)this.posY, (float)this.posZ));
					if(pos != null) {
						this.travelToDimension(destinationDimId, pos.x, pos.z);
					}
					else
						this.setDead();
				}
				else
					this.setDead();
			}
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
		MinecraftForge.EVENT_BUS.post(new RocketEvent.RocketReachesOrbitEvent(this));

		//TODO: support multiple riders and rider/satellite combo
		if(!stats.hasSeat()) {

			List<TileSatelliteHatch> satelliteHatches = storage.getSatelliteHatches();


			for(TileSatelliteHatch tile : storage.getSatelliteHatches()) {
				SatelliteBase satellite = tile.getSatellite();
				if(satellite == null) {
					ItemStack stack = tile.getStackInSlot(0);
					if(stack != null && stack.getItem() == AdvancedRocketryItems.itemSpaceStation) {
						StorageChunk storage = ((ItemPackedStructure)stack.getItem()).getStructure(stack);
						SpaceObject object = DimensionManager.getSpaceManager().getSpaceStation(stack.getItemDamage());

						DimensionManager.getSpaceManager().moveStationToBody(object, this.worldObj.provider.dimensionId);

						//Vector3F<Integer> spawn = object.getSpawnLocation();

						object.onFirstCreated(storage);

					}
				}
				else {
					satellite.setDimensionId(worldObj);
					DimensionProperties properties = DimensionManager.getInstance().getDimensionProperties(this.worldObj.provider.dimensionId);

					properties.addSatallite(satellite, this.worldObj);
				}
			}

			this.setDead();
			//TODO: satellite event?
		}
		else {
			//TODO: maybe add orbit dimension
			this.motionY = -this.motionY;
			isInOrbit = true;
			//If going to a station or something make sure to set coords accordingly
			//If in space land on the planet, if on the planet go to space
			if(destinationDimId == Configuration.spaceDimId || this.worldObj.provider.dimensionId == Configuration.spaceDimId) {
				Vector3F<Float> pos = storage.getGuidanceComputer().getLandingLocation(destinationDimId);
				storage.getGuidanceComputer().setReturnPosition(new Vector3F<Float>((float)this.posX, (float)this.posY, (float)this.posZ));
				if(pos != null) {

					if(Configuration.spaceDimId == destinationDimId) {
						this.travelToDimension(destinationDimId, pos.x, pos.z);
					}
					else {
						SpaceObject object = DimensionManager.getSpaceManager().getSpaceStationFromBlockCoords((int)this.posX, (int)this.posZ);
						if(object == null)
							this.travelToDimension(destinationDimId, pos.x, pos.z);
						else
							this.travelToDimension(object.getOrbitingPlanetId(), pos.x, pos.z);
					}

					return;
				}
			}


			this.travelToDimension(this.worldObj.provider.dimensionId == destinationDimId ? 0 : destinationDimId);
		}
	}

	public void launch() {

		//Get destination dimid and lock the computer
		//TODO: lock the computer
		if(stats.hasSeat()) {
			TileGuidanceComputer guidanceComputer = storage.getGuidanceComputer();
			destinationDimId = guidanceComputer.getDestinationDimId(worldObj.provider.dimensionId);
		}

		if(!stats.hasSeat() || ( destinationDimId != -1 && (DimensionManager.getInstance().isDimensionCreated(destinationDimId)) || destinationDimId == Configuration.spaceDimId || destinationDimId == 0) ) { //Abort if destination is invalid


			setInFlight(true);
			Iterator<IInfrastructure> connectedTiles = connectedInfrastructure.iterator();

			if(worldObj.isRemote)
				MinecraftForge.EVENT_BUS.post(new RocketLaunchEvent(this));

			//Disconnect things linked to the rocket on liftoff
			while(connectedTiles.hasNext()) {
				IInfrastructure i = connectedTiles.next();
				if(i.disconnectOnLiftOff()) {
					i.unlinkRocket();
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

	}


	//Relink connected tiles on load
	@Override
	public void onChunkLoad() {
		super.onChunkLoad();


		ListIterator<Vector3F<Integer>> itr = infrastructureCoords.listIterator();
		while(itr.hasNext()) {
			Vector3F<Integer> temp = itr.next();

			TileEntity tile = this.worldObj.getTileEntity(temp.x, temp.y, temp.z);
			if(tile instanceof IInfrastructure) {
				this.linkInfrastructure((IInfrastructure)tile);
				itr.remove();
			}
		}
	}


	@Override
	public void travelToDimension(int newDimId) {
		travelToDimension(newDimId, this.posX, this.posZ);
	}

	public void travelToDimension(int newDimId, double posX, double posZ)
	{
		if (!this.worldObj.isRemote && !this.isDead)
		{

			double x = posX, z = posZ;

			Entity rider = this.riddenByEntity;

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

				entity.setLocationAndAngles(x, Configuration.orbit, z, this.rotationYaw, this.rotationPitch);
				worldserver1.spawnEntityInWorld(entity);

				if(rider != null) {
					//Transfer the player if applicable
					minecraftserver.getConfigurationManager().transferPlayerToDimension((EntityPlayerMP)rider, newDimId, new TeleporterNoPortal(worldserver1));

					rider.setLocationAndAngles(x, Configuration.orbit, z, this.rotationYaw, this.rotationPitch);
					rider.mountEntity(entity);
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

		isInOrbit = nbt.getBoolean("orbit");
		stats.readFromNBT(nbt);
		setFuelAmount(stats.getFuelAmount(FuelType.LIQUID));

		setInFlight(nbt.getBoolean("flight"));

		if(nbt.hasKey("data"))
		{
			if(storage == null) 
				storage = new StorageChunk();

			storage.readFromNBT(nbt.getCompoundTag("data"));
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
				infrastructureCoords.add(new Vector3F<Integer>(coords[0], coords[1], coords[2]));
		}

		destinationDimId = nbt.getInteger("destinationDimId");

		//Satallite
		if(nbt.hasKey("satallite")) {
			NBTTagCompound satalliteNbt = nbt.getCompoundTag("satallite");

			satallite = SatelliteRegistry.createFromNBT(satalliteNbt);

		}

		if(nbt.hasKey("dieNextTick"))
			dieNextTick = nbt.getBoolean("dieNextTick");
	}

	protected void writeNetworkableNBT(NBTTagCompound nbt) {
		nbt.setBoolean("orbit", isInOrbit);
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

	@Override
	protected void writeEntityToNBT(NBTTagCompound nbt) {

		writeNetworkableNBT(nbt);
		if(storage != null) {
			NBTTagCompound blocks = new NBTTagCompound();
			storage.writeToNBT(blocks);
			nbt.setTag("data", blocks);
		}

		nbt.setBoolean("dieNextTick", dieNextTick);

		//TODO handle non tile Infrastructure

	}

	@Override
	public void readDataFromNetwork(ByteBuf in, byte packetId,
			NBTTagCompound nbt) {
		if(packetId == PacketType.RECIEVENBT.ordinal()) {
			storage = new StorageChunk();
			storage.readFromNetwork(in);
		}
	}

	@Override
	public void writeDataToNetwork(ByteBuf out, byte id) {

		if(id == PacketType.RECIEVENBT.ordinal()) {
			storage.writeToNetwork(out);
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
			this.launch();
		}
		else if(id == PacketType.CHANGEWORLD.ordinal()) {
			AdvancedRocketry.proxy.changeClientPlayerWorld(storage.world);
		}
		else if(id == PacketType.REVERTWORLD.ordinal()) {
			AdvancedRocketry.proxy.changeClientPlayerWorld(this.worldObj);
		}
		else if(id > 100) {
			TileEntity tile = storage.getUsableTiles().get(id - 100 - tilebuttonOffset);

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
				this.riddenByEntity.setPosition(this.posX  + stats.getSeatX(), this.posY + stats.getSeatY() + 1.5 , this.posZ + stats.getSeatZ() );
			}
			else
				this.riddenByEntity.setPosition(this.posX , this.posY , this.posZ );
		}
	}

	@Override
	public List<ModuleBase> getModules() {
		List<ModuleBase> modules;
		//If the rocket is flight don't load the interface
		modules = new LinkedList<ModuleBase>();

		//Backgrounds
		modules.add(new ModuleImage(173, 0, new IconResource(128, 0, 48, 86, CommonResources.genericBackground)));
		modules.add(new ModuleImage(173, 86, new IconResource(98, 0, 78, 83, CommonResources.genericBackground)));
		modules.add(new ModuleImage(173, 168, new IconResource(98, 168, 78, 3, CommonResources.genericBackground)));

		//Fuel
		modules.add(new ModuleProgress(192, 7, 0, new ProgressBarImage(2, 173, 12, 71, 17, 6, 3, 69, 1, 1, ForgeDirection.UP, TextureResources.rocketHud), this));

		//TODO DEBUG tiles!
		List<TileEntity> tiles = storage.getUsableTiles();
		for(int i = 0; i < tiles.size(); i++) {
			TileEntity tile  = tiles.get(i);
			modules.add(new ModuleSlotButton(8 + 18* (i % 9), 17 + 18*(i/9), i + tilebuttonOffset, this, new ItemStack(storage.getBlock(tile.xCoord, tile.yCoord, tile.zCoord), 1, storage.getBlockMetadata(tile.xCoord, tile.yCoord, tile.zCoord))));
		}

		//Add buttons
		modules.add(new ModuleButton(180, 140, 0, "Dissassemble", this, TextureResources.buttonBuild, 64, 20));

		//modules.add(new ModuleButton(180, 95, 1, "", this, TextureResources.buttonLeft, 10, 16));
		//modules.add(new ModuleButton(202, 95, 2, "", this, TextureResources.buttonRight, 10, 16));

		modules.add(new ModuleText(180, 114, "Inventories", 0x404040));

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
		default:
			PacketHandler.sendToServer(new PacketEntity(this, (byte)(buttonId + 100)));

			//Minecraft.getMinecraft().thePlayer.closeScreen();

			TileEntity tile = storage.getUsableTiles().get(buttonId - tilebuttonOffset);
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
}
