package zmaster587.advancedRocketry.entity;

import io.netty.buffer.ByteBuf;
<<<<<<< HEAD
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.PortalInfo;
import net.minecraft.block.SandBlock;
=======
import net.minecraft.block.BlockFire;
import net.minecraft.block.BlockSand;
>>>>>>> origin/feature/nuclearthermalrockets
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GLAllocation;
import net.minecraft.client.settings.ParticleStatus;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntitySize;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.MoverType;
import net.minecraft.entity.Pose;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.network.IPacket;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.network.play.server.SSpawnObjectPacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tags.BlockTags;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.Util;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.Teleporter;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.gen.Heightmap.Type;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.Tags;
import net.minecraftforge.common.util.ITeleporter;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fluids.capability.IFluidHandler.FluidAction;
import net.minecraftforge.fml.common.registry.IEntityAdditionalSpawnData;
import net.minecraftforge.fml.network.NetworkHooks;
import net.minecraftforge.fluids.capability.IFluidHandlerItem;
import net.minecraftforge.fluids.capability.templates.FluidTank;
import zmaster587.advancedRocketry.AdvancedRocketry;
import zmaster587.advancedRocketry.advancements.ARAdvancements;
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
import zmaster587.advancedRocketry.stations.SpaceStationObject;
import zmaster587.advancedRocketry.stations.SpaceObjectManager;
import zmaster587.advancedRocketry.tile.TileGuidanceComputer;
import zmaster587.advancedRocketry.tile.hatch.TileSatelliteHatch;
import zmaster587.advancedRocketry.util.*;
import zmaster587.advancedRocketry.world.util.TeleporterNoPortal;
import zmaster587.libVulpes.LibVulpes;
import zmaster587.libVulpes.api.LibvulpesGuiRegistry;
import zmaster587.libVulpes.client.util.ProgressBarImage;
import zmaster587.libVulpes.gui.CommonResources;
import zmaster587.libVulpes.interfaces.INetworkEntity;
import zmaster587.libVulpes.inventory.ContainerModular;
import zmaster587.libVulpes.inventory.GuiHandler;
import zmaster587.libVulpes.inventory.GuiHandler.guiId;
import zmaster587.libVulpes.inventory.modules.*;
import zmaster587.libVulpes.items.ItemLinker;
import zmaster587.libVulpes.network.IEntitySpawnNBT;
import zmaster587.libVulpes.network.PacketEntity;
import zmaster587.libVulpes.network.PacketHandler;
import zmaster587.libVulpes.network.PacketSpawnEntity;
import zmaster587.libVulpes.util.FluidUtils;
import zmaster587.libVulpes.util.HashedBlockPosition;
import zmaster587.libVulpes.util.IconResource;
import zmaster587.libVulpes.util.Vector3F;
import zmaster587.libVulpes.util.ZUtils;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;

public class EntityRocket extends EntityRocketBase implements INetworkEntity, IEntityAdditionalSpawnData, IModularInventory, IProgressBar, IButtonInventory, ISelectionNotify, IPlanetDefiner, INamedContainerProvider, IEntitySpawnNBT {

	//true if the rocket is on decent
	private boolean isInOrbit;
	//True if the rocket isn't on the ground
	private boolean isInFlight;
	//used in the rare case a player goes to a non-existant space station
	private ResourceLocation lastDimensionFrom = Constants.INVALID_PLANET;
	private boolean turningLeft, turningRight, turningUp, turningDownforWhat;
	public StorageChunk storage;
	private String errorStr;
	private long lastErrorTime = Long.MIN_VALUE;
	private static long ERROR_DISPLAY_TIME = 100;
	private static int DESCENT_TIMER = 500;
	private static int BUTTON_ID_OFFSET = 25;
	private static final int STATION_LOC_OFFSET = 50;
	private static final int ENGINE_IGNITION_CNT = 100;
	private ModuleText landingPadDisplayText;


	protected long lastWorldTickTicked;

	private SatelliteBase satellite;
	protected ResourceLocation destinationDimId;
	//Offset for buttons linking to the tileEntityGrid
	private int tilebuttonOffset = 3;
	private int autoDescendTimer;
	protected ModulePlanetSelector container;
	boolean acceptedPacket = false;
	SpacePosition spacePosition;

	//0 to 100, 100 is fully rotated and ready to go, 0 is normal mode
	private int rcs_mode_counter = 0;
	//Used to most of the logic, determining if in RCS mode or not
	private boolean rcs_mode = false;
	private EntitySize mySize;

	public enum PacketType {
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
		TURNUPDATE,
		ABORTLAUNCH,
		SENDSPACEPOS
	}

	private static final DataParameter<Integer> fuelLevelMonopropellant =  EntityDataManager.createKey(EntityRocket.class, DataSerializers.VARINT);
	private static final DataParameter<Integer> fuelLevelBipropellant =  EntityDataManager.createKey(EntityRocket.class, DataSerializers.VARINT);
	private static final DataParameter<Integer> fuelLevelOxidizer =  EntityDataManager.createKey(EntityRocket.class, DataSerializers.VARINT);
	private static final DataParameter<Integer> fuelLevelNuclearWorkingFluid =  EntityDataManager.createKey(EntityRocket.class, DataSerializers.VARINT);
	private static final DataParameter<Boolean> INFLIGHT =  EntityDataManager.createKey(EntityRocket.class, DataSerializers.BOOLEAN);
	private static final DataParameter<Boolean> INORBIT =  EntityDataManager.createKey(EntityRocket.class, DataSerializers.BOOLEAN);
	private static final DataParameter<Boolean> INSPACEFLIGHT =  EntityDataManager.createKey(EntityRocket.class, DataSerializers.BOOLEAN);
	private static final DataParameter<Boolean> RCS_MODE =  EntityDataManager.createKey(EntityRocket.class, DataSerializers.BOOLEAN);
	private static final DataParameter<Integer> LAUNCH_COUNTER =  EntityDataManager.createKey(EntityRocket.class, DataSerializers.VARINT);

	public EntityRocket(EntityType<?> type, World p_i1582_1_) {
		super(type, p_i1582_1_);
		isInOrbit = false;
		stats = new StatsRocket();
		isInFlight = false;
		connectedInfrastructure = new LinkedList<>();
		infrastructureCoords = new HashSet<>();

		lastWorldTickTicked = p_i1582_1_.getGameTime();
		autoDescendTimer = 5000;
		landingPadDisplayText = new ModuleText(256, 16, "", 0x00FF00, 2f);
		landingPadDisplayText.setColor(0x00ff00);

		spacePosition = new SpacePosition();
		spacePosition.star = DimensionManager.getInstance().getSol();
	}


	public EntityRocket(EntityType<?> type, World world, StorageChunk storage, StatsRocket stats, double x, double y, double z) {
		this(AdvancedRocketryEntities.ENTITY_ROCKET, world);
		this.stats = stats;
		this.setPosition(x, y, z);
		this.storage = storage;
		this.storage.setEntity(this);
		initFromBounds();
		isInFlight = false;
		lastWorldTickTicked = world.getGameTime();
		autoDescendTimer = 5000;
		landingPadDisplayText = new ModuleText(256, 16, "", 0x00FF00, 2f);
		landingPadDisplayText.setColor(0x00ff00);
	}
	
	public EntityRocket(World world, StorageChunk storage, StatsRocket stats, double x, double y, double z) {
		this(AdvancedRocketryEntities.ENTITY_ROCKET, world, storage, stats, x,y,z);
	}


	public void toggleRCS() {
		if(DimensionManager.getInstance().getDimensionProperties(this.world).isAsteroid()) {
			rcs_mode = !rcs_mode;
			setRCS(rcs_mode);
			setPosition(this.getPosX(), this.getPosY(), this.getPosZ());
		}
		else
		{
			rcs_mode = false;
			setRCS(false);
		}

	}

	private void setRCS(boolean status) {
		dataManager.set(RCS_MODE, status);
	}

	public boolean getRCS() {
		return dataManager.get(RCS_MODE);
	}

	private void setInSpaceFlight(boolean status) {
		dataManager.set(INSPACEFLIGHT, status);
	}

	public boolean getInSpaceFlight() {
		return dataManager.get(INSPACEFLIGHT);
	}

	public int getRCSRotateProgress() {
		return rcs_mode_counter;
	}

	@Override
	public AxisAlignedBB getBoundingBox() {
		if(storage != null) {
			//MobileAABB aabb = new MobileAABB(super.getBoundingBox());
			//aabb.setStorageChunk(storage);
			//aabb.setRemote(worldObj.isRemote);
			//return aabb;
			return super.getBoundingBox();
		}
		return new AxisAlignedBB(0,0,0,1,1,1);
	}

	@Override
	public void setBoundingBox(AxisAlignedBB bb) {
		//if(storage != null)
		//	super.setEntityBoundingBox(bb.offset(0, storage.getSizeY(),0));
		//else
		super.setBoundingBox(bb);
	}

	public SpacePosition getSpacePosition()
	{
		SpacePosition planetPosition;
		return spacePosition;
	}

	public void disconnectInfrastructure(IInfrastructure infrastructure){
		infrastructure.unlinkRocket();
		infrastructureCoords.remove(new HashedBlockPosition(((TileEntity)infrastructure).getPos()));

		if(!world.isRemote) {
			int[] pos = {((TileEntity)infrastructure).getPos().getX(), ((TileEntity)infrastructure).getPos().getY(), ((TileEntity)infrastructure).getPos().getZ()};

			CompoundNBT nbt = new CompoundNBT();
			nbt.putIntArray("pos", pos);
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

<<<<<<< HEAD
		if(this.world.getGameTime() < this.lastErrorTime + ERROR_DISPLAY_TIME)
=======
		ERROR_DISPLAY_TIME = 100;
		if(this.world.getTotalWorldTime() < this.lastErrorTime + ERROR_DISPLAY_TIME)
>>>>>>> origin/feature/nuclearthermalrockets
			return errorStr;

		//Get destination string
		String displayStr = LibVulpes.proxy.getLocalizedString("msg.na");
		if(storage != null) {
			ResourceLocation dimid = storage.getDestinationDimId(ZUtils.getDimensionIdentifier(this.world), (int)getPosX(), (int)getPosZ());

			if(dimid.equals(ARConfiguration.GetSpaceDimId())) {
				Vector3F<Float> vec = storage.getDestinationCoordinates(dimid, false);
				if(vec != null) {

					ISpaceObject spaceObject = SpaceObjectManager.getSpaceManager().getSpaceStationFromBlockCoords(new BlockPos(vec.x,vec.y,vec.z));

<<<<<<< HEAD
					if(obj != null) {
						displayStr = " " +  LibVulpes.proxy.getLocalizedString("msg.entity.rocket.station") + " " + obj.getId();
=======
					if(spaceObject != null) {
						displayStr =  LibVulpes.proxy.getLocalizedString("msg.entity.rocket.station") + spaceObject.getId();
>>>>>>> origin/feature/nuclearthermalrockets

						StationLandingLocation location = storage.getGuidanceComputer().getLandingLocation(spaceObject.getId());

						if(location != null) {
							displayStr = displayStr + "\n" + LibVulpes.proxy.getLocalizedString("msg.entity.rocket.pad") + location;
						}
					}
				}
			}
			else if(!Constants.INVALID_PLANET.equals(dimid) && !SpaceObjectManager.WARPDIMID.equals(dimid)) {

<<<<<<< HEAD
				boolean goingToOrbit = ARConfiguration.getCurrentConfig().experimentalSpaceFlight.get() && storage.getGuidanceComputer().isEmpty() && !Constants.INVALID_PLANET.equals(dimid);
=======
				boolean goingToOrbit = ARConfiguration.getCurrentConfig().experimentalSpaceFlight && storage.getGuidanceComputer().isEmpty();
>>>>>>> origin/feature/nuclearthermalrockets

				if(goingToOrbit)
					displayStr = " Orbit";
				else {
					displayStr = " " + DimensionManager.getInstance().getDimensionProperties(dimid).getName();
					Vector3F<Float> loc = storage.getDestinationCoordinates(dimid, false);
					if(loc != null)
					{
						String name = storage.getDestinationName(dimid);
						if(!name.isEmpty())
							displayStr += String.format("\n%s: %s", LibVulpes.proxy.getLocalizedString("msg.label.destname"), name);
						displayStr += String.format("\n%s: %.0f, %.0f", LibVulpes.proxy.getLocalizedString("msg.label.coords"), loc.x, loc.z);
					}
					else
					{
						displayStr += "\nCoords: ???, ???";
					}
				}
			}
		}

		if(dataManager.get(LAUNCH_COUNTER) >= 0) {
			return LibVulpes.proxy.getLocalizedString("msg.entity.rocket.launch") +  " " + (dataManager.get(LAUNCH_COUNTER)/20) + "\n" +
					LibVulpes.proxy.getLocalizedString("msg.entity.rocket.launch2");
		}

		if(DimensionManager.getInstance().getDimensionProperties(this.world).isAsteroid()) {
			if(getRCS())
				return LibVulpes.proxy.getLocalizedString("msg.entity.rocket.rcs") + ": " + getRCS();
			else
				displayStr += "\n" + LibVulpes.proxy.getLocalizedString("msg.entity.rocket.rcs") + ": " + getRCS();
		}

		if(isInOrbit() && !isInFlight())
			return LibVulpes.proxy.getLocalizedString("msg.entity.rocket.descend.1") + "\n" + LibVulpes.proxy.getLocalizedString("msg.entity.rocket.descend.2") + " " + ((DESCENT_TIMER - this.ticksExisted)/20);
		else if(!isInFlight())
			return LibVulpes.proxy.getLocalizedString("msg.entity.rocket.ascend.1") + "\n" + LibVulpes.proxy.getLocalizedString("msg.entity.rocket.ascend.2") + displayStr;

		return super.getTextOverlay();
	}

	private void setError(String error) {
		this.errorStr = error;
		this.lastErrorTime = this.world.getGameTime();
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
<<<<<<< HEAD
				setBoundingBox(new AxisAlignedBB(x - sizeX, y - (double)this.getYOffset() + sizeZ*0.5 + 0.5, z - sizeY, x + sizeX, y + sizeZ*1.5 + .5 - (double)this.getYOffset(), z + sizeY));
=======
				setEntityBoundingBox(new AxisAlignedBB(x - sizeX, y - this.getYOffset() + sizeZ*0.5 + 0.5, z - sizeY, x + sizeX, y + sizeZ*1.5 + .5 - this.getYOffset(), z + sizeY));
>>>>>>> origin/feature/nuclearthermalrockets
			}
			else {
				float sizeX = storage.getSizeX()/2.0f;
				float sizeY = storage.getSizeY();
				float sizeZ = storage.getSizeZ()/2.0f;
<<<<<<< HEAD
				setBoundingBox(new AxisAlignedBB(x - sizeX, y - (double)this.getYOffset(), z - sizeZ, x + sizeX, y + sizeY - (double)this.getYOffset(), z + sizeZ));
=======
				setEntityBoundingBox(new AxisAlignedBB(x - sizeX, y - this.getYOffset(), z - sizeZ, x + sizeX, y + sizeY - this.getYOffset(), z + sizeZ));
>>>>>>> origin/feature/nuclearthermalrockets
			}
		}
	}

	@Override
	public void resetPositionToBB()
	{
		AxisAlignedBB axisalignedbb = this.getBoundingBox();
		if(storage!= null && getRCS())
		{
			float sizeX = storage.getSizeX()/2.0f;
			float sizeY = storage.getSizeY()/2.0f;
			float sizeZ = storage.getSizeZ();
			//setEntityBoundingBox(new AxisAlignedBB(x - sizeX, y - (double)this.getYOffset() + sizeZ*0.5 + 0.5, z - sizeY, x + sizeX, y + sizeZ*1.5 + .5 - (double)this.getYOffset(), z + sizeY));


			this.setPosition((axisalignedbb.minX + axisalignedbb.maxX) / 2.0D, axisalignedbb.minY - sizeZ*0.5 - 0.5, (axisalignedbb.minZ + axisalignedbb.maxZ) / 2.0D);
		}
		else {
			super.resetPositionToBB();
		}
	}

	/**
<<<<<<< HEAD
	 * Updates the data option
	 * @param amt sets the amount of monopropellant fuel in the rocket
	 */
	public void setFuelAmountMonoproellant(int amt) {
		dataManager.set(fuelLevelMonopropellant, amt);
	}

	/**
	 * Updates the data option
	 * @param amt sets the amount of bipropellant fuel in the rocket
	 */
	public void setFuelAmountBipropellant(int amt) {
		dataManager.set(fuelLevelBipropellant, amt);
	}

	/**
	 * Updates the data option
	 * @param amt sets the amount of oxidizer fuel in the rocket
	 */
	public void setFuelAmountOxidizer(int amt) {
		dataManager.set(fuelLevelOxidizer, amt);
	}

	/**
	 * @param rate sets the amount of monopropellant fuel in the rocket
	 */
	public void setFuelRateMonopropellant(int rate) { stats.setFuelRate(FuelType.LIQUID_MONOPROPELLANT, rate); }
=======
	 * @param fuelType
	 * @return the amount of fuel stored in the rocket
	 */
	public int getFuelAmount(@Nullable FuelType fuelType) {
		if(fuelType != null) {
			int amount;

			switch(fuelType) {
				case LIQUID_MONOPROPELLANT:
					amount = dataManager.get(fuelLevelMonopropellant);
					break;
				case LIQUID_BIPROPELLANT:
					amount = dataManager.get(fuelLevelBipropellant);
					break;
				case LIQUID_OXIDIZER:
					amount = dataManager.get(fuelLevelOxidizer);
					break;
				case NUCLEAR_WORKING_FLUID:
					amount = dataManager.get(fuelLevelNuclearWorkingFluid);
					break;
				default:
					return 0;
			}
>>>>>>> origin/feature/nuclearthermalrockets

			stats.setFuelAmount(fuelType, amount);
			return amount;
		}

		return 0;
	}

	/**
	 * Adds fuel and updates the datawatcher
	 * @param fuelType
	 * @param amount amount of fuel to add
	 * @return the amount of fuel added
	 */
	public int addFuelAmount(@Nonnull FuelType fuelType, int amount) {
		int ret = stats.addFuelAmount(fuelType, amount);
		setFuelAmount(fuelType, stats.getFuelAmount(fuelType));
		return ret;
	}

	/**
	 * Updates the data option
	 * @param fuelType
	 * @param amt sets the amount of monopropellant fuel in the rocket
	 */
	public void setFuelAmount(@Nonnull FuelType fuelType, int amt) {
		if (fuelType == FuelType.LIQUID_MONOPROPELLANT) {
			dataManager.set(fuelLevelMonopropellant, amt);
			dataManager.setDirty(fuelLevelMonopropellant);
		} else if (fuelType == FuelType.LIQUID_BIPROPELLANT) {
			dataManager.set(fuelLevelBipropellant, amt);
			dataManager.setDirty(fuelLevelBipropellant);
		} else if (fuelType == FuelType.LIQUID_OXIDIZER) {
			dataManager.set(fuelLevelOxidizer, amt);
			dataManager.setDirty(fuelLevelOxidizer);
		} else if (fuelType == FuelType.NUCLEAR_WORKING_FLUID) {
			dataManager.set(fuelLevelNuclearWorkingFluid, amt);
			dataManager.setDirty(fuelLevelNuclearWorkingFluid);
		}
	}

	/**
	 * @param fuelType sets the type of fuel to set a rate for
	 * @param rate sets the rate of fuel in the rocket
	 */
	public void setFuelConsumptionRate(@Nonnull FuelType fuelType, int rate) { stats.setFuelRate(fuelType, rate); }

	/**
	 * @param fuelType is the fuel type to get
	 * @return gets the fuel capacity of the rocket
	 */
	public int getFuelCapacity(@Nullable FuelType fuelType) {
		return stats.getFuelCapacity(fuelType);
	}

	/**
	 * @param fuelType is the fuel type to get
	 * @return the rate of fuel consumption for the rocket
	 */
	public int getFuelConsumptionRate(@Nullable FuelType fuelType) {
		return stats.getFuelRate(fuelType);
	}

	/**
	 * @return the fuel type that this rocket uses, null if the rocket does not use any
	 */
	@Nullable
	public FuelType getRocketFuelType() {
		if (getFuelCapacity(FuelType.LIQUID_MONOPROPELLANT) > 0)
			return FuelType.LIQUID_MONOPROPELLANT;
		else if (getFuelCapacity(FuelType.LIQUID_BIPROPELLANT) > 0)
			return FuelType.LIQUID_BIPROPELLANT;
		else if (getFuelCapacity(FuelType.NUCLEAR_WORKING_FLUID) > 0)
		    return FuelType.NUCLEAR_WORKING_FLUID;
		return null;
	}

	@Override
	public void setEntityId(int id){
		super.setEntityId(id);
		//Ask server for nbt data
		if(world.isRemote) {
			//PacketHandler.sendToServer(new PacketEntity(this, (byte)PacketType.REQUESTNBT.ordinal()));
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
	 * Sets the status of orbit of the rocket and updates the datawatcher
	 * @param inOrbit status of orbit
	 */
	public void setInOrbit(boolean inOrbit) {
		this.isInOrbit = inOrbit;
		this.dataManager.set(INORBIT, inOrbit);
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
	 * @param inFlight status of flight
	 */
<<<<<<< HEAD
	public void setInFlight(boolean inflight) {
		this.isInFlight = inflight;
		this.dataManager.set(INFLIGHT, inflight);
=======
	public void setInFlight(boolean inFlight) {
		this.isInFlight = inFlight;
		this.dataManager.set(INFLIGHT, inFlight);
		this.dataManager.setDirty(INFLIGHT);
>>>>>>> origin/feature/nuclearthermalrockets
	}

	@Override
	protected void registerData() {
		this.dataManager.register(INFLIGHT, false);
		this.dataManager.register(fuelLevelMonopropellant, 0);
		this.dataManager.register(fuelLevelBipropellant, 0);
		this.dataManager.register(fuelLevelOxidizer, 0);
		this.dataManager.register(fuelLevelNuclearWorkingFluid, 0);
		this.dataManager.register(INORBIT, false);
		this.dataManager.register(RCS_MODE, false);
		this.dataManager.register(LAUNCH_COUNTER, -1);
		this.dataManager.register(INSPACEFLIGHT, false);
	}

	//Set the size and position of the rocket from storage
	public void initFromBounds() {		
		if(storage != null) {
			this.setSize(Math.max(storage.getSizeX(), storage.getSizeZ()), storage.getSizeY());
			this.setPosition(this.getPosX(), this.getPosY(), this.getPosZ());
		}
	}

<<<<<<< HEAD
	protected ActionResultType interact(PlayerEntity player) {
=======
	protected boolean interact(@Nonnull EntityPlayer player) {
>>>>>>> origin/feature/nuclearthermalrockets
		//Actual interact code needs to be moved to a packet receive on the server

		ItemStack heldItem = player.getHeldItem(Hand.MAIN_HAND);
		boolean isHoldingFluidItemOrLinker = false;
<<<<<<< HEAD
		//Handle linkers and right-click with fuel
		if(!heldItem.isEmpty()) {
			
			float fuelMult;
=======
		if(!heldItem.isEmpty()) {
>>>>>>> origin/feature/nuclearthermalrockets
			FluidStack fluidStack;

			if(heldItem.getItem() instanceof ItemLinker) {
				isHoldingFluidItemOrLinker = true;

				if(ItemLinker.isSet(heldItem)) {
					TileEntity tile = this.world.getTileEntity(ItemLinker.getMasterCoords(heldItem));

					if(tile instanceof IInfrastructure) {
						IInfrastructure infrastructure = (IInfrastructure)tile;
<<<<<<< HEAD
						if(Math.sqrt(this.getDistanceSq(ItemLinker.getMasterX(heldItem), this.getPosY(), ItemLinker.getMasterZ(heldItem))) < infrastructure.getMaxLinkDistance() + Math.max(storage.getSizeX(), storage.getSizeZ())) {
=======

						if(this.getDistance(ItemLinker.getMasterX(heldItem), this.posY, ItemLinker.getMasterZ(heldItem)) < infrastructure.getMaxLinkDistance() + Math.max(storage.getSizeX(), storage.getSizeZ())) {
>>>>>>> origin/feature/nuclearthermalrockets
							if(!connectedInfrastructure.contains(tile)) {
								linkInfrastructure(infrastructure);

								if(!world.isRemote) {
<<<<<<< HEAD
									player.sendMessage(new StringTextComponent("Linked Sucessfully"), Util.DUMMY_UUID);
=======
									player.sendMessage(new TextComponentString("Linked successfully"));
>>>>>>> origin/feature/nuclearthermalrockets
								}
								ItemLinker.resetPosition(heldItem);

								return ActionResultType.SUCCESS;
							}
							else if(!world.isRemote)
								player.sendMessage(new StringTextComponent("Already linked!"), Util.DUMMY_UUID);
						}
						else if(!world.isRemote)
							player.sendMessage(new StringTextComponent("The object you are trying to link is too far away"), Util.DUMMY_UUID);
					}
					else if(!world.isRemote)
						player.sendMessage(new StringTextComponent("This cannot be linked to a rocket!"), Util.DUMMY_UUID);
				}
				else if(!world.isRemote)
<<<<<<< HEAD
					player.sendMessage(new StringTextComponent("Nothing to be linked"), Util.DUMMY_UUID);
				return ActionResultType.PASS;
			}
			else if((FluidUtils.containsFluid(heldItem) && FluidUtils.getFluidForItem(heldItem) != null) && ARConfiguration.getCurrentConfig().canBeFueledByHand.get()) {
				isHoldingFluidItemOrLinker = true;
=======
					player.sendMessage(new TextComponentString("Nothing to be linked"));

				return false;
			} //End of if(heldItem.getItem() instanceof ItemLinker)
			else if((FluidUtils.containsFluid(heldItem) && FluidUtils.getFluidForItem(heldItem) != null) && ARConfiguration.getCurrentConfig().canBeFueledByHand) {
>>>>>>> origin/feature/nuclearthermalrockets
				fluidStack = FluidUtils.getFluidForItem(heldItem);

				if ((canRocketFitFluid(fluidStack))) {
					isHoldingFluidItemOrLinker = true;

<<<<<<< HEAD
					if (FuelRegistry.instance.isFuel(FuelType.LIQUID_MONOPROPELLANT, fluidStack.getFluid())) {
						stats.setFuelRate(FuelType.LIQUID_MONOPROPELLANT, (int)(stats.getBaseFuelRate(FuelType.LIQUID_MONOPROPELLANT) * FuelRegistry.instance.getMultiplier(FuelType.LIQUID_MONOPROPELLANT, fluidStack.getFluid())));
						FluidTank rocketFakeTank = new FluidTank(getFuelCapacityMonopropellant() - getFuelAmountMonopropellant());
						FluidUtil.interactWithFluidHandler(player, Hand.MAIN_HAND, rocketFakeTank);
						this.addFuelAmountMonopropellant(rocketFakeTank.getFluidAmount());
					} else if (FuelRegistry.instance.isFuel(FuelType.LIQUID_BIPROPELLANT, fluidStack.getFluid())) {
						stats.setFuelRate(FuelType.LIQUID_BIPROPELLANT, (int)(stats.getBaseFuelRate(FuelType.LIQUID_BIPROPELLANT) * FuelRegistry.instance.getMultiplier(FuelType.LIQUID_BIPROPELLANT, fluidStack.getFluid())));
						FluidTank rocketFakeTank = new FluidTank(getFuelCapacityBipropellant() - getFuelAmountBipropellant());
						FluidUtil.interactWithFluidHandler(player, Hand.MAIN_HAND, rocketFakeTank);
						this.addFuelAmountBipropellant(rocketFakeTank.getFluidAmount());
					} else if (FuelRegistry.instance.isFuel(FuelType.LIQUID_OXIDIZER, fluidStack.getFluid())) {
						stats.setFuelRate(FuelType.LIQUID_OXIDIZER, (int)(stats.getBaseFuelRate(FuelType.LIQUID_OXIDIZER) * FuelRegistry.instance.getMultiplier(FuelType.LIQUID_OXIDIZER, fluidStack.getFluid())));
						FluidTank rocketFakeTank = new FluidTank(getFuelCapacityBipropellant() - getFuelAmountBipropellant());
						FluidUtil.interactWithFluidHandler(player, Hand.MAIN_HAND, rocketFakeTank);
						this.addFuelAmountOxidizer(rocketFakeTank.getFluidAmount());
					}
				}
				return ActionResultType.SUCCESS;
=======
					FuelType type = getRocketFuelType();
					if(type == null)
						return false;

					if (getRocketFuelType() == FuelType.LIQUID_BIPROPELLANT && FuelRegistry.instance.isFuel(FuelType.LIQUID_OXIDIZER, fluidStack.getFluid()))
						type = FuelType.LIQUID_OXIDIZER;

					stats.setFuelRate(type, (int) (stats.getBaseFuelRate(type) * FuelRegistry.instance.getMultiplier(type, fluidStack.getFluid())));
					FluidTank rocketFakeTank = new FluidTank(getFuelCapacity(type) - getFuelAmount(type));
					FluidUtil.interactWithFluidHandler(player, EnumHand.MAIN_HAND, rocketFakeTank);
					this.addFuelAmount(type, rocketFakeTank.getFluidAmount());
				}
>>>>>>> origin/feature/nuclearthermalrockets
			}
		}

		//If player is holding shift open GUI
		if(player.isSneaking() || (!stats.hasSeat() && !isHoldingFluidItemOrLinker)) {
			if(!world.isRemote)
				openGui(player);
		}
		else if(stats.hasSeat()) { //If pilot seat is open mount entity there
			if(this.getPassengers().size() < stats.getNumPassengerSeats()) {
				if(!world.isRemote)
					player.startRiding(this);
			}
		}
		return ActionResultType.SUCCESS;
	}

	protected boolean canFitPassenger(Entity passenger) {
		return this.getPassengers().size() < stats.getNumPassengerSeats();
	}

	/**
	 * @param fluidStack the stack to check whether the rocket can fit
	 * @return boolean on whether said fluid stack can fit into the rocket's internal fuel point storage
	 */
	public boolean canRocketFitFluid(FluidStack fluidStack) {
		if (FuelRegistry.instance.isFuel(FuelType.LIQUID_MONOPROPELLANT, fluidStack.getFluid())) {
<<<<<<< HEAD
			boolean isCorrectFluid = (stats.getFuelFluid() == null) ? true : FluidUtils.areFluidsSameType(stats.getFuelFluid(), fluidStack.getFluid());
			if (stats.getFuelFluid() == null && isCorrectFluid)
				stats.setFuelFluid(fluidStack.getFluid().getRegistryName());
			return isCorrectFluid;
		} else if (FuelRegistry.instance.isFuel(FuelType.LIQUID_BIPROPELLANT, fluidStack.getFluid())) {
			boolean isCorrectFluid = (stats.getFuelFluid() == null) ? true : FluidUtils.areFluidsSameType(stats.getFuelFluid(), fluidStack.getFluid());
			if (stats.getFuelFluid() == null && isCorrectFluid)
				stats.setFuelFluid(fluidStack.getFluid().getRegistryName());
			return isCorrectFluid;
		} else if (FuelRegistry.instance.isFuel(FuelType.LIQUID_OXIDIZER, fluidStack.getFluid())) {
			boolean isCorrectFluid = (stats.getOxidizerFluid() == null) ? true : FluidUtils.areFluidsSameType(stats.getOxidizerFluid(), fluidStack.getFluid());
			if (stats.getOxidizerFluid() == null && isCorrectFluid)
				stats.setOxidizerFluid(fluidStack.getFluid().getRegistryName());
=======
			boolean isCorrectFluid = stats.getFuelFluid().equals("null") || fluidStack.getFluid() == FluidRegistry.getFluid(stats.getFuelFluid());
			if (stats.getFuelFluid().equals("null") && isCorrectFluid)
				stats.setFuelFluid(fluidStack.getFluid().getName());
			return isCorrectFluid;
		} else if (FuelRegistry.instance.isFuel(FuelType.LIQUID_BIPROPELLANT, fluidStack.getFluid())) {
			boolean isCorrectFluid = stats.getFuelFluid().equals("null") || fluidStack.getFluid() == FluidRegistry.getFluid(stats.getFuelFluid());
			if (stats.getFuelFluid().equals("null") && isCorrectFluid)
				stats.setFuelFluid(fluidStack.getFluid().getName());
			return isCorrectFluid;
		} else if (FuelRegistry.instance.isFuel(FuelType.LIQUID_OXIDIZER, fluidStack.getFluid())) {
			boolean isCorrectFluid = stats.getOxidizerFluid().equals("null") || fluidStack.getFluid() == FluidRegistry.getFluid(stats.getOxidizerFluid());
			if (stats.getOxidizerFluid().equals("null") && isCorrectFluid)
				stats.setOxidizerFluid(fluidStack.getFluid().getName());
>>>>>>> origin/feature/nuclearthermalrockets
			return isCorrectFluid;
		}
		return false;
	}

	public void openGui(PlayerEntity player) {
		NetworkHooks.openGui((ServerPlayerEntity)player, (INamedContainerProvider)this, packetBuffer -> {packetBuffer.writeInt(getModularInvType().ordinal()); packetBuffer.writeInt(this.getEntityId());});

		//Only handle the bypass on the server
		if(!world.isRemote)
			RocketInventoryHelper.addPlayerToInventoryBypass(player);
	}

	@Override
	public ActionResultType processInitialInteract(PlayerEntity player,  Hand hand){
		if(world.isRemote) {
			//Due to forge's rigid handling of entities (NetHanlderPlayServer:866) needs to be handled differently for large rockets
			PacketHandler.sendToServer(new PacketEntity(this, (byte)PacketType.SENDINTERACT.ordinal()));
			return interact(player);
		}
		return ActionResultType.PASS;

	}


	/**
	 * @return boolean on whether the rocket is burning any type of fuel at the current moment, including all fuel types
	 */
	public boolean isBurningFuel() {
<<<<<<< HEAD
		return ((getFuelAmountMonopropellant() > 0 || (getFuelAmountBipropellant() > 0 && getFuelAmountOxidizer() > 0)) || !ARConfiguration.getCurrentConfig().rocketRequireFuel.get()) && ((!this.getPassengers().isEmpty() && getPassengerMovingForward() > 0) || !isInOrbit());
=======
		FuelType fuelType = getRocketFuelType();
		return (((fuelType == FuelType.LIQUID_BIPROPELLANT ) ? getFuelAmount(fuelType) > 0 && getFuelAmount(FuelType.LIQUID_OXIDIZER) > 0 : getFuelAmount(fuelType) > 0)  || !ARConfiguration.getCurrentConfig().rocketRequireFuel) && ((!this.getPassengers().isEmpty() && getPassengerMovingForward() > 0) || !isInOrbit());
>>>>>>> origin/feature/nuclearthermalrockets
	}

	public float getPassengerMovingForward() {

		for(Entity entity : this.getPassengers()) {
			if(entity instanceof PlayerEntity) {
				return ((PlayerEntity) entity).moveForward;
			}
		}
		return 0f;
	}

	private boolean hasHumanPassenger() {

		for(Entity entity : this.getPassengers()) {
			if(entity instanceof PlayerEntity) {
				return true;
			}
		}
		return false;
	}

	public boolean isDescentPhase() {
		return ARConfiguration.getCurrentConfig().automaticRetroRockets.get() && isInOrbit() && this.getPosY() < 300 && (this.getMotion().y < -0.4f || world.isRemote);
	}

	public boolean isStartupPhase() {
		return this.dataManager.get(LAUNCH_COUNTER) < ENGINE_IGNITION_CNT && this.dataManager.get(LAUNCH_COUNTER) != -1;
	}

	public float getEnginePower() {
		float mult = 1;
		int countdown = this.dataManager.get(LAUNCH_COUNTER);
		if(countdown > -1 && isStartupPhase()) {
			mult = (ENGINE_IGNITION_CNT - countdown)/(float)ENGINE_IGNITION_CNT;
		}

		if(this.areEnginesRunning())
			return mult*Math.max(DimensionManager.getInstance().getDimensionProperties(this.world).getAtmosphereDensityAtHeight(this.getPosY()), 0.05f);
		else
			return 0;
	}

	public boolean areEnginesRunning() {
		return this.getMotion().y > 0 || isDescentPhase() || (getPassengerMovingForward() > 0) || isStartupPhase();
	}

	private void runEngines() {
		//Spawn in the particle effects for the engines
		int engineNum = 0;
		if(world.isRemote && Minecraft.getInstance().gameSettings.particles != ParticleStatus.MINIMAL && areEnginesRunning()) {
			for(Vector3F<Float> vec : stats.getEngineLocations()) {

<<<<<<< HEAD
				AtmosphereHandler handler;
				if(Minecraft.getInstance().gameSettings.particles  == ParticleStatus.ALL && world.getGameTime() % 10 == 0 && (engineNum < 8 || ((world.getGameTime()/10) % Math.max((stats.getEngineLocations().size()/8),1)) == (engineNum/8)) && ( (handler = AtmosphereHandler.getOxygenHandler(world)) == null || (handler.getAtmosphereType(this) != null && handler.getAtmosphereType(this).allowsCombustion())) )
					AdvancedRocketry.proxy.spawnParticle("rocketSmoke", world, this.getPosX() + vec.x, this.getPosY() + vec.y - 0.75, this.getPosZ() +vec.z,0,0,0);
=======
				AtmosphereHandler handler = AtmosphereHandler.getOxygenHandler(world.provider.getDimension());
				IAtmosphere atmosphere = null;

				if(handler != null)
					atmosphere = handler.getAtmosphereType(this);

				if(Minecraft.getMinecraft().gameSettings.particleSetting < 1 && world.getTotalWorldTime() % 10 == 0 && (engineNum < 8 || ((world.getTotalWorldTime()/10) % Math.max((stats.getEngineLocations().size()/8),1)) == (engineNum/8)) && ( handler == null || (atmosphere != null && atmosphere.allowsCombustion())) )
					AdvancedRocketry.proxy.spawnParticle("rocketSmoke", world, this.posX + vec.x, this.posY + vec.y - 0.75, this.posZ +vec.z,0,0,0);
>>>>>>> origin/feature/nuclearthermalrockets

				for(int i = 0; i < 4; i++) {
					AdvancedRocketry.proxy.spawnParticle("rocketFlame", world, this.getPosX() + vec.x, this.getPosY() + vec.y - 0.75, this.getPosZ() +vec.z,(this.rand.nextFloat() - 0.5f)/8f,-.75 ,(this.rand.nextFloat() - 0.5f)/8f);

				}
			}
		}
	}

	private BlockPos getTopBlock(BlockPos pos) {
		//Yeah... because minecraft's World.getTopSolidOrLiquidBlock does not actually check for liquids like lava
		Chunk chunk = world.getChunkAt(pos);
		BlockPos blockpos;
		BlockPos blockpos1;

		for (blockpos = new BlockPos(pos.getX(), chunk.getTopFilledSegment() + 16, pos.getZ()); blockpos.getY() >= 0; blockpos = blockpos1)
		{
			blockpos1 = blockpos.down();

			if (!world.isAirBlock(blockpos))
			{
				break;
			}
		}
		return blockpos;
	}

<<<<<<< HEAD
	private Vector3d calculatePullFromPlanets()
	{
=======
	private Vec3d calculatePullFromPlanets() {
>>>>>>> origin/feature/nuclearthermalrockets
		double x = 0;
		double y = 0;
		double z = 0;
		double gravityMultiplier = 0.01;
		if(this.spacePosition.world != null) {
			//Sun 
			// This is totally cheesed because none of the input is in real values anyway
			SpacePosition planetSpacePosition = new SpacePosition();
			double acceleration = 100*gravityMultiplier;
			double distanceSq = planetSpacePosition.distanceToSpacePosition2(this.spacePosition);

			double shipAcceleration = acceleration/distanceSq;

			Vector3d vector = this.spacePosition.getNormalVectorTo(planetSpacePosition);

			if(distanceSq > 0)
			{
				x += shipAcceleration*vector.x;
				y += shipAcceleration*vector.y;
				z += shipAcceleration*vector.z;
			}
		}
		else if(this.spacePosition.star != null) {
			for(IDimensionProperties planet : this.spacePosition.star.getPlanets()) {
				// This is totally cheesed because none of the input is in real values anyway
				SpacePosition planetSpacePosition = planet.getSpacePosition();
				double acceleration = planet.getGravitationalMultiplier()*9.81f*gravityMultiplier;
				double distanceSq = planet.getSpacePosition().distanceToSpacePosition2(this.spacePosition);

				double shipAcceleration = acceleration/distanceSq;

				Vector3d vector = this.spacePosition.getNormalVectorTo(planetSpacePosition);

				x += shipAcceleration*vector.x;
				y += shipAcceleration*vector.y;
				z += shipAcceleration*vector.z;

			}

			//Sun 
			// This is totally cheesed because none of the input is in real values anyway
			SpacePosition planetSpacePosition = new SpacePosition();
			double acceleration = 100*gravityMultiplier;
			double distanceSq = planetSpacePosition.distanceToSpacePosition2(this.spacePosition);

			double shipAcceleration = acceleration/distanceSq;

			Vector3d vector = this.spacePosition.getNormalVectorTo(planetSpacePosition);

			if(distanceSq > 0)
			{
				x += shipAcceleration*vector.x;
				y += shipAcceleration*vector.y;
				z += shipAcceleration*vector.z;
			}
		}

		return new Vector3d(x,y,z);
	}

	@Override
	public void setFire(int seconds) {}

	@Override
	public void tick() {
		super.tick();
		long deltaTime = world.getGameTime() - lastWorldTickTicked;
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
			else {
				int rocketSizeX = storage.getSizeX()/2+1;
				int rocketSizeZ = storage.getSizeZ()/2+1;
				final int bufferSize = 3;

				// Create float if needed

				//First check to see if anything at all will catch the rocket
				boolean safeLanding = false;
				for(int x = ((int)getPosX()-rocketSizeX); x < (getPosX()+rocketSizeX) && !safeLanding; x++)
				{
					for(int z = ((int)getPosZ()-rocketSizeZ); z < (getPosZ()+rocketSizeZ) && !safeLanding; z++)
					{
						BlockPos pos = new BlockPos(x, getPosY(), z);
						pos = getTopBlock(pos);

						safeLanding = !world.getBlockState(pos).getMaterial().isLiquid() || world.getBlockState(pos).getBlock() == Blocks.WATER || world.getBlockState(pos).getBlock() == AdvancedRocketryBlocks.blockRocketFire;
					}
				}

				// If nothing will catch the rocket, and the material isn't water, then create a float
				// If anyone asks, the dev thinks underwater rocket launch platforms are cool and players can swim anyway
				if(!safeLanding)
				{
					for(int x = ((int)getPosX()-rocketSizeX - bufferSize); x < (getPosX()+rocketSizeX + bufferSize); x++)
					{
						for(int z = ((int)getPosZ()-rocketSizeZ - bufferSize); z < (getPosZ()+rocketSizeZ + bufferSize); z++)
						{
							BlockPos pos = new BlockPos(x, getPosY(), z);
							pos = getTopBlock(pos);
							world.setBlockState(pos, AdvancedRocketryBlocks.blockLandingFloat.getDefaultState());
						}
					}
				}
			}
		}

		if(this.ticksExisted > DESCENT_TIMER && isInOrbit() && !isInFlight())
			setInFlight(true);

		//Hackish crap to make clients mount entities immediately after server transfer and fire events
		//Known race condition... screw me...
		if(!world.isRemote && (this.isInFlight() || this.isInOrbit()) && this.ticksExisted  == 20) {
			//Deorbiting
			MinecraftForge.EVENT_BUS.post(new RocketEvent.RocketDeOrbitingEvent(this));
			PacketHandler.sendToNearby(new PacketEntity(this, (byte)PacketType.ROCKETLANDEVENT.ordinal()), world, (int)getPosX(), (int)getPosY(), (int)getPosZ(), 64);
			for(Entity riddenByEntity : getPassengers()) {
				if(riddenByEntity instanceof PlayerEntity) {
					PlayerEntity player = (PlayerEntity)riddenByEntity;


<<<<<<< HEAD
					if(player instanceof PlayerEntity)
					{
						PacketHandler.sendToPlayer(new PacketEntity((INetworkEntity)this,(byte)PacketType.FORCEMOUNT.ordinal()), player);
					}
=======

					PacketHandler.sendToPlayer(new PacketEntity(this,(byte)PacketType.FORCEMOUNT.ordinal()), player);
>>>>>>> origin/feature/nuclearthermalrockets
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
		//Count down
		int launchCount = this.dataManager.get(LAUNCH_COUNTER);
		if(launchCount >= 0) {
			if(launchCount == 0) 
				launch();
			launchCount--;
			this.dataManager.set(LAUNCH_COUNTER, launchCount);
			//Just before launch, damage the ground. We'll do it again on the tick that we launch
<<<<<<< HEAD
			if (launchCount == 20 && ARConfiguration.getCurrentConfig().launchingDestroysBlocks.get())
				damageGroundBelowRocket(world, (int)this.getPosX(), (int)this.getPosY(), (int)this.getPosZ(), (int)Math.pow(stats.getThrust(), 0.3333));
=======
			if (ARConfiguration.getCurrentConfig().launchingDestroysBlocks && launchCount <= 100 && launchCount != 0 && this.getFuelCapacity(getRocketFuelType()) > 0)
				damageGroundBelowRocket(world, (int)this.posX, (int)this.posY, (int)this.posZ, (int)Math.pow(stats.getThrust(), 0.4));
>>>>>>> origin/feature/nuclearthermalrockets
		}

		// When flying around in space
		if(getInSpaceFlight())
		{
			double distanceFromPlanetToLeaveOrbitMult = 16.0;

			double motionX = getMotion().x, motionY  = getMotion().y, motionZ  = getMotion().z;

			this.rotationYaw += (turningRight ? 5 : 0) - (turningLeft ? 5 : 0);
			double acc = 10*this.getPassengerMovingForward()*0.2;
			//RCS mode, steer like boat
			float yawAngle = (float)(this.rotationYaw*Math.PI/180f);
			Vector3d planetPull = Vector3d.ZERO; //calculatePullFromPlanets();
			motionX += acc*MathHelper.sin(-yawAngle) + planetPull.x;
			motionY += (turningUp ? 0.02 : 0) - (turningDownforWhat ? 0.02 : 0) + planetPull.y;
			motionZ += acc*MathHelper.cos(-yawAngle)  + planetPull.z;

			if(acc == 0)
			{
				motionX *= 0.98;
				motionY *= 0.98;
				motionZ *= 0.98;
			}


			this.setMotion(motionX, motionY, motionZ);
			spacePosition.x += motionX;
			spacePosition.y += motionY;
			spacePosition.z += motionZ;

			//Check if close to a world
			if(this.spacePosition.world == null && this.spacePosition.star != null)
			{
				for(IDimensionProperties properties : this.spacePosition.star.getPlanets())
				{
					SpacePosition worldSpacePosition = properties.getSpacePosition();
					double distanceSq = this.spacePosition.distanceToSpacePosition2(worldSpacePosition);

					if(distanceSq < properties.getRenderSizeSolarView()*properties.getRenderSizeSolarView()*8)
					{
						this.spacePosition.world = (DimensionProperties) properties;


						//Radius to put the player
						double radius = -properties.getRenderSizePlanetView()*16;
						//Assume planet centered at 0
						SpacePosition planetPosition = new SpacePosition();
						double theta = Math.atan2(this.getMotion().z, this.getMotion().x);

						this.spacePosition.x = planetPosition.x + Math.cos(theta)*radius;
						this.spacePosition.y = planetPosition.y;
						this.spacePosition.z = planetPosition.z + Math.sin(theta)*radius;
						PacketHandler.sendToServer(new PacketEntity(this,(byte)PacketType.SENDSPACEPOS.ordinal()));
						break;
					}
				}
			}
			else if(this.spacePosition.world != null)
			{
				double distanceSq = this.spacePosition.distanceToSpacePosition2(new SpacePosition());
				//Land, only handle on server
				if(!world.isRemote)
				{
					if(distanceSq < 0.5f*spacePosition.world.getRenderSizePlanetView()*spacePosition.world.getRenderSizePlanetView())
					{
						this.destinationDimId = spacePosition.world.getId();
						this.setRCS(false);
						this.setMotion(0,1,0); // y=+1 because it gets inverted later
						this.rotationYaw = 0;
						rcs_mode = false;
						reachSpaceManned();
						this.setInSpaceFlight(false);
					}
					else
					{
						// Land on moons?
						for(ResourceLocation subId : spacePosition.world.getChildPlanets())
						{
							DimensionProperties subPlanetProperties = DimensionManager.getInstance().getDimensionProperties(subId);

							distanceSq = this.spacePosition.distanceToSpacePosition2(subPlanetProperties.getSpacePosition());
							if(distanceSq < 0.5f*subPlanetProperties.getRenderSizePlanetView()*subPlanetProperties.getRenderSizePlanetView())
							{
								this.destinationDimId = subPlanetProperties.getId();
								this.setRCS(false);
								rcs_mode = false;
								this.rotationYaw = 0;
								reachSpaceManned();
								this.setInSpaceFlight(false);
							}

							//What about space stations?
							List<ISpaceObject> stations = SpaceObjectManager.getSpaceManager().getSpaceStationsOrbitingPlanet(subId);

							if(stations != null)
							{
								for(ISpaceObject station : stations)
								{
									distanceSq = this.spacePosition.distanceToSpacePosition2(((SpaceStationObject)station).getSpacePosition());
									if(distanceSq < 100*100)
									{
										this.destinationDimId = ARConfiguration.GetSpaceDimId();
										this.storage.getGuidanceComputer().overrideLandingStation(station);
										this.setRCS(false);
										this.rotationYaw = 0;
										rcs_mode = false;
										reachSpaceManned();
										this.setInSpaceFlight(false);
									}
								}
							}
						}
					}

					// Station orbiting main world?
					List<ISpaceObject> stations = SpaceObjectManager.getSpaceManager().getSpaceStationsOrbitingPlanet(this.spacePosition.world.getId());

					if(stations != null)
					{
						for(ISpaceObject station : stations)
						{
							distanceSq = this.spacePosition.distanceToSpacePosition2(((SpaceStationObject)station).getSpacePosition());
							if(distanceSq < 100*100)
							{
								this.destinationDimId = ARConfiguration.GetSpaceDimId();
								this.storage.getGuidanceComputer().overrideLandingStation(station);
								this.setRCS(false);
								rcs_mode = false;
								reachSpaceManned();
								this.setInSpaceFlight(false);
							}
						}
					}
				}
				// transition to solar navigation, this comes after, prevent NPE, client only
				else if(distanceSq > this.spacePosition.world.getRenderSizePlanetView()*this.spacePosition.world.getRenderSizePlanetView()*distanceFromPlanetToLeaveOrbitMult*distanceFromPlanetToLeaveOrbitMult)
				{
					//Radius to put the player
					double radius = this.spacePosition.world.getRenderSizeSolarView()*10;

					SpacePosition planetPosition = this.spacePosition.world.getSpacePosition();
					this.spacePosition.world = null;

					double theta = Math.atan2(this.getMotion().z, this.getMotion().x);

					this.spacePosition.x = planetPosition.x + Math.cos(theta)*radius;
					this.spacePosition.y = planetPosition.y;
					this.spacePosition.z = planetPosition.z + Math.sin(theta)*radius;

					this.setMotion(0,0,0);
				}
			}
			// Update server of location
<<<<<<< HEAD
			if(this.world.isRemote && this.world.getGameTime() % 20 == 0)
				PacketHandler.sendToServer(new PacketEntity((INetworkEntity)this,(byte)PacketType.SENDSPACEPOS.ordinal()));
=======
			if(this.world.isRemote && this.world.getTotalWorldTime() % 20 == 0)
				PacketHandler.sendToServer(new PacketEntity(this,(byte)PacketType.SENDSPACEPOS.ordinal()));
>>>>>>> origin/feature/nuclearthermalrockets
		}
		else if(isInFlight()) {
			boolean burningFuel = isBurningFuel();

			boolean descentPhase = isDescentPhase();

			double motionX = getMotion().x, motionY  = getMotion().y, motionZ  = getMotion().z;

			if(burningFuel || descentPhase) {
				//Burn the rocket fuel
				if(!world.isRemote && !descentPhase) {
<<<<<<< HEAD
					setFuelAmountBipropellant((int) (getFuelAmountBipropellant() - stats.getFuelRate(FuelType.LIQUID_BIPROPELLANT)));
					setFuelAmountOxidizer((int) (getFuelAmountOxidizer() - stats.getFuelRate(FuelType.LIQUID_OXIDIZER)));
					setFuelAmountMonoproellant((int) (getFuelAmountMonopropellant() - stats.getFuelRate(FuelType.LIQUID_MONOPROPELLANT)));
				    if (getFuelAmountBipropellant() == 0 && getFuelAmountMonopropellant() == 0) {
				    	stats.setFuelFluid(null);
					}
					if (getFuelAmountOxidizer() == 0) {
						stats.setOxidizerFluid(null);
=======
					setFuelAmount(getRocketFuelType(), getFuelAmount(getRocketFuelType()) - getFuelConsumptionRate(getRocketFuelType()));
					if (getRocketFuelType() == FuelType.LIQUID_BIPROPELLANT)
						setFuelAmount(FuelType.LIQUID_OXIDIZER, getFuelAmount(FuelType.LIQUID_OXIDIZER) - getFuelConsumptionRate(FuelType.LIQUID_OXIDIZER));

				    if (getFuelAmount(getRocketFuelType()) == 0) {
				    	stats.setFuelFluid("null");
						stats.setWorkingFluid("null");
					}
					if (getRocketFuelType() == FuelType.LIQUID_BIPROPELLANT && getFuelAmount(FuelType.LIQUID_OXIDIZER) == 0) {
						stats.setOxidizerFluid("null");
>>>>>>> origin/feature/nuclearthermalrockets
					}
				}

				runEngines();
			}

			if(!this.getPassengers().isEmpty()) {

				for(Entity entity : this.getPassengers()) {
					entity.fallDistance = 0;
					this.fallDistance = 0;
				}

				//if the player holds the forward key then decelerate
				if(isInOrbit() && (burningFuel || descentPhase)) {
					float vel = descentPhase ? 1f : getPassengerMovingForward();
					motionY -= motionY*vel/50f;
				}
				this.velocityChanged = true;

			}
			else if(isInOrbit() && descentPhase) { //For unmanned rockets
				motionY -= motionY/50f;
				this.velocityChanged = true;
			}

			if(!world.isRemote) {
				//If out of fuel or descending then accelerate downwards
				if(isInOrbit() || !burningFuel) {
					motionY = Math.min(motionY - 0.001, 1);
				} else
					motionY += stats.getAcceleration(DimensionManager.getInstance().getDimensionProperties(this.world).getGravitationalMultiplier()) * deltaTime;


				double lastPosY = this.getPosY();
				double prevMotion = motionY;

				this.move(MoverType.SELF , new Vector3d(0, prevMotion*deltaTime, 0));


				boolean landedInSpace = DimensionManager.getInstance().getDimensionProperties(this.world).isAsteroid() && this.getPosY() < 64;
				boolean landedOnGround = lastPosY + prevMotion != this.getPosY() && this.getPosY() < 256;
				//Check to see if it's landed
				if((isInOrbit() || !burningFuel) && isInFlight() && (landedOnGround || landedInSpace)) {
					//Did  sending this packet cause problems?
					PacketHandler.sendToPlayersTrackingEntity(new PacketEntity(this, (byte)PacketType.ROCKETLANDEVENT.ordinal()), this);
					MinecraftForge.EVENT_BUS.post(new RocketEvent.RocketLandedEvent(this));
					motionY = 0;
					this.setInFlight(false);
					this.setInOrbit(false);
				}

				//Checks heights to see how high the rocket should go
				//I cannot believe I am doing this but it's not like orbital mechanics exists anyway.... here, have an approximation for it being harder to get to farther moons
				if(!isInOrbit() && (this.getPosY() > stats.orbitHeight)) {
					onOrbitReached();
				}

				this.setMotion(motionX, motionY, motionZ);

				//If the rocket falls out of the world while in orbit either fall back to earth or die
				if(this.getPosY() < 0) {
					ResourceLocation dimId = ZUtils.getDimensionIdentifier(world);

					if(ARConfiguration.GetSpaceDimId().equals(dimId)) {

<<<<<<< HEAD
						ISpaceObject obj = SpaceObjectManager.getSpaceManager().getSpaceStationFromBlockCoords(new BlockPos(getPositionVec()));

						if(obj != null) {
							ResourceLocation targetDimID = obj.getOrbitingPlanetId();
=======
						ISpaceObject spaceObject = SpaceObjectManager.getSpaceManager().getSpaceStationFromBlockCoords(getPosition());

						if(spaceObject != null) {
							int targetDimID = spaceObject.getOrbitingPlanetId();
>>>>>>> origin/feature/nuclearthermalrockets

							Vector3F<Float> pos = storage.getDestinationCoordinates(targetDimID, true);
							if(pos != null) {
								setInOrbit(true);
								setInFlight(false);
								this.changeDimension(ZUtils.getWorld(targetDimID), pos.x, getEntryHeight(targetDimID), pos.z);
							}
							else 
								this.remove();
						}
						else {
							Vector3F<Float> pos = storage.getDestinationCoordinates(Constants.INVALID_PLANET, true);
							if(pos != null) {
								setInOrbit(true);
								setInFlight(false);
								this.changeDimension(ZUtils.getWorld(lastDimensionFrom), pos.x, ARConfiguration.getCurrentConfig().orbit.get(), pos.z);
							}
							else 
								this.remove();
						}
					}
					else
						this.remove();
				}
			}
			else {
				this.move(MoverType.SELF , new Vector3d(0, this.getMotion().y, 0));
			}
		}
		else if(DimensionManager.getInstance().getDimensionProperties(this.world).isAsteroid() && getRCS()) {
			double motionX = getMotion().x, motionY  = getMotion().y, motionZ  = getMotion().z;
			this.rotationYaw += (turningRight ? 5 : 0) - (turningLeft ? 5 : 0);
			double acc = this.getPassengerMovingForward()*.02;
			//RCS mode, steer like boat
			float yawAngle = (float)(this.rotationYaw*Math.PI/180f);
			motionX += acc*MathHelper.sin(-yawAngle);
			motionY += (turningUp ? 0.02 : 0) - (turningDownforWhat ? 0.02 : 0);
			motionZ += acc*MathHelper.cos(-yawAngle);
			motionX *= 0.9;
			motionY *= 0.9;
			motionZ *= 0.9;

			this.setMotion(motionX, motionY, motionZ);

			this.move(MoverType.SELF , this.getMotion());
		}
		else if(isStartupPhase())
			runEngines();

		//When we're landing, we should also destroy the blocks below the rocket if they are valid to be destroyed - but overall we do it fewer times than on launch (once instead of twice)
<<<<<<< HEAD
		if((int)this.getPosY() == world.getHeight(Type.WORLD_SURFACE, getPosition()).getY() + 2 && ARConfiguration.getCurrentConfig().launchingDestroysBlocks.get() && this.isDescentPhase()) {
			damageGroundBelowRocket(world, (int)this.getPosX(), (int)this.getPosY() -1, (int)this.getPosZ(), (int)Math.pow(stats.getThrust(), 0.3333));
=======
		if(this.posY < getTopBlock(getPosition()).getY() + 5 && this.posX > getTopBlock(getPosition()).getY() && ARConfiguration.getCurrentConfig().launchingDestroysBlocks && motionY < -0.1) {
			damageGroundBelowRocket(world, (int)this.posX, (int)this.posY -1, (int)this.posZ, (int)Math.pow(stats.getThrust(), 0.4));
>>>>>>> origin/feature/nuclearthermalrockets
		}
	}

	public void onTurnRight(boolean state) {
		turningRight = state;
		PacketHandler.sendToServer(new PacketEntity(this, (byte)EntityRocket.PacketType.TURNUPDATE.ordinal()));
	}

	public void onTurnLeft(boolean state) {
		turningLeft = state;
		PacketHandler.sendToServer(new PacketEntity(this, (byte)EntityRocket.PacketType.TURNUPDATE.ordinal()));
	}

	public void onUp(boolean state) {
		turningUp = state;
		PacketHandler.sendToServer(new PacketEntity(this, (byte)EntityRocket.PacketType.TURNUPDATE.ordinal()));
	}

	public void onDown(boolean state) {
		turningDownforWhat = state;
		PacketHandler.sendToServer(new PacketEntity(this, (byte)EntityRocket.PacketType.TURNUPDATE.ordinal()));
	}

	/**
	 * @return a list of satellites stores in this rocket
	 */
	public List<SatelliteBase> getSatellites() {	
		List<SatelliteBase> satellites = new ArrayList<>();
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

		long targetSatellite;
		if(storage.getGuidanceComputer() != null && (targetSatellite = storage.getGuidanceComputer().getTargetSatellite()) != -1) {
			SatelliteBase sat = DimensionManager.getInstance().getSatellite(targetSatellite);
			for(TileEntity tile : storage.getTileEntityList()) {
				if(tile instanceof TileSatelliteHatch && ((IInventory)tile).getStackInSlot(0).isEmpty()) {
					((IInventory)tile).setInventorySlotContents(0, sat.getItemStackFromSatellite());
					DimensionManager.getInstance().getDimensionProperties(sat.getDimensionId().get()).removeSatellite(targetSatellite);
					break;
				}
			}
			this.setMotion(this.getMotion().x, -this.getMotion().y, this.getMotion().z);
			setInOrbit(true);
		}
		else if(!stats.hasSeat()) {
			reachSpaceUnmanned();
		}
		else {
			reachSpaceManned();
		}
	}

<<<<<<< HEAD
	private int getEntryHeight(ResourceLocation entryLocationDimID){
		if (entryLocationDimID.getPath() == ARConfiguration.getCurrentConfig().spaceDimId.get()) {
			return ARConfiguration.getCurrentConfig().stationClearanceHeight.get();
=======
	/**
	 * @param entryLocationDimID the dimension ID for the dimension the rocket is entering
	 * @return integer for world height in blocks the rocket will spawn in at when it reaches the dimension
	 */
	private int getEntryHeight(int entryLocationDimID){
		if (entryLocationDimID == ARConfiguration.getCurrentConfig().spaceDimId) {
			return ARConfiguration.getCurrentConfig().stationClearanceHeight;
>>>>>>> origin/feature/nuclearthermalrockets
		} else {
			return ARConfiguration.getCurrentConfig().orbit.get();
		}
	}

	private void reachSpaceUnmanned() {
		TileGuidanceComputer computer = storage.getGuidanceComputer();
		if(computer != null && computer.getStackInSlot(0) != null &&
				computer.getStackInSlot(0).getItem() instanceof ItemAsteroidChip) {
			//make it 30 minutes with one drill
			float drillingPower = stats.getDrillingPower();

			float asteroidDrillingMult = 1f;

			ItemStack stack = storage.getGuidanceComputer().getStackInSlot(0);

			Asteroid asteroid = ARConfiguration.getCurrentConfig().asteroidTypes.get(((ItemAsteroidChip)stack.getItem()).getType(stack));

			if(asteroid != null) {
				asteroidDrillingMult = asteroid.timeMultiplier;
			}

			MissionOreMining miningMission = new MissionOreMining((long)(asteroidDrillingMult*ARConfiguration.getCurrentConfig().asteroidMiningTimeMult.get()*(drillingPower == 0f ? 36000 : 360/stats.getDrillingPower())), this, connectedInfrastructure);
			DimensionProperties properties = DimensionManager.getEffectiveDimId(world, new BlockPos(getPositionVec()));

			miningMission.setDimensionId(world);
			properties.addSatellite(miningMission, world);

			if(!world.isRemote)
				PacketHandler.sendToAll(new PacketSatellite(miningMission));

			for(IInfrastructure i : connectedInfrastructure) {
				i.linkMission(miningMission);
			}

			this.remove();
			return;
			//TODO: Move tracking stations over to the mission handler
		}
		else {
			unpackSatellites();
		}

		destinationDimId = storage.getDestinationDimId(this.world, (int)this.getPosX(), (int)this.getPosZ());
		if(destinationDimId == ZUtils.getDimensionIdentifier(this.world)) {
			Vector3F<Float> pos = storage.getDestinationCoordinates(destinationDimId, true);
<<<<<<< HEAD
			storage.setDestinationCoordinates(new Vector3F<Float>((float)this.getPosX(), (float)this.getPosY(), (float)this.getPosZ()), ZUtils.getDimensionIdentifier(this.world));
=======
			storage.setDestinationCoordinates(new Vector3F<>((float) this.posX, (float) this.posY, (float) this.posZ), this.world.provider.getDimension());
>>>>>>> origin/feature/nuclearthermalrockets
			if(pos != null) {
				this.setInOrbit(true);
				this.setMotion(this.getMotion().x, -this.getMotion().y, this.getMotion().z);

				//unlink any connected tiles
				Iterator<IInfrastructure> connectedTiles = connectedInfrastructure.iterator();
				while(connectedTiles.hasNext()) {
					connectedTiles.next().unlinkRocket();
					connectedTiles.remove();
				}
				this.setPositionAndUpdate(pos.x, getEntryHeight(destinationDimId), pos.z);
			}
			else {

				//Make player confirm deorbit if a player is riding the rocket
				if(hasHumanPassenger()) {
					setInFlight(false);
<<<<<<< HEAD
					pos.y = (float) getEntryHeight(destinationDimId);
=======
					this.setPositionAndUpdate(this.posX, getEntryHeight(destinationDimId), this.posZ);

>>>>>>> origin/feature/nuclearthermalrockets
				}
				this.setInOrbit(true);
				this.setMotion(this.getMotion().x, -this.getMotion().y, this.getMotion().z);
				//unlink any connected tiles

				Iterator<IInfrastructure> connectedTiles = connectedInfrastructure.iterator();
				while(connectedTiles.hasNext()) {
					connectedTiles.next().unlinkRocket();
					connectedTiles.remove();
				}

<<<<<<< HEAD
				this.setPositionAndUpdate(this.getPosX(), getEntryHeight(destinationDimId), this.getPosZ());
				return;
=======
				this.setPositionAndUpdate(this.posX, getEntryHeight(destinationDimId), this.posZ);
>>>>>>> origin/feature/nuclearthermalrockets
			}

		}
		else if(DimensionManager.getInstance().canTravelTo(destinationDimId)) {
			Vector3F<Float> pos = storage.getDestinationCoordinates(destinationDimId, true);
<<<<<<< HEAD
			storage.setDestinationCoordinates(new Vector3F<Float>((float)this.getPosX(), (float)this.getPosY(), (float)this.getPosZ()), ZUtils.getDimensionIdentifier(this.world));
			if(pos != null) {
				this.setInOrbit(true);
				this.setMotion(this.getMotion().x, -this.getMotion().y, this.getMotion().z);
				this.changeDimension(ZUtils.getWorld(destinationDimId), pos.x, getEntryHeight(destinationDimId), pos.z);
				return;
=======
			storage.setDestinationCoordinates(new Vector3F<>((float) this.posX, (float) this.posY, (float) this.posZ), this.world.provider.getDimension());
			if(pos != null) {
				this.setInOrbit(true);
				this.motionY = -this.motionY;
				this.changeDimension(destinationDimId, pos.x, getEntryHeight(destinationDimId), pos.z);
>>>>>>> origin/feature/nuclearthermalrockets
			}
			else {

				//Make player confirm deorbit if a player is riding the rocket
				if(hasHumanPassenger()) {
					setInFlight(false);
					this.setPositionAndUpdate(this.posX, getEntryHeight(destinationDimId), this.posZ);

				}
				this.setInOrbit(true);
<<<<<<< HEAD
				this.setMotion(this.getMotion().x, -this.getMotion().y, this.getMotion().z);
				this.changeDimension(ZUtils.getWorld(destinationDimId), this.getPosX(), getEntryHeight(destinationDimId), this.getPosZ());
				return;
=======
				this.motionY = -this.motionY;

				this.changeDimension(destinationDimId, this.posX, getEntryHeight(destinationDimId), this.posZ);
>>>>>>> origin/feature/nuclearthermalrockets
			}
		}
		else {
			//Make rocket return semi nearby
			int offX = (world.rand.nextInt() % 256) - 128;
			int offZ = (world.rand.nextInt() % 256) - 128;
			this.setInOrbit(true);
			this.setMotion(this.getMotion().x, -this.getMotion().y, this.getMotion().z);
			this.setPosition(getPosX() + offX, getPosY(), getPosZ() + offZ);

			//unlink any connected tiles
			Iterator<IInfrastructure> connectedTiles = connectedInfrastructure.iterator();
			while(connectedTiles.hasNext()) {
				connectedTiles.next().unlinkRocket();
				connectedTiles.remove();
			}

			//this.remove();
			//TODO: satellite event?
		}
	}

	private void reachSpaceManned() {
		unpackSatellites();
		Vector3F<Float> destPos = new Vector3F<>(0f, 0f, 0f);

		// Update space position
		if(ARConfiguration.getCurrentConfig().experimentalSpaceFlight.get() && storage.getGuidanceComputer().isEmpty() && hasHumanPassenger() && !getInSpaceFlight())
		{
			DimensionProperties currentDim = DimensionManager.getEffectiveDimId(world, new BlockPos(getPositionVec()));

			// Get top level planet
			while(currentDim.isMoon()) currentDim = currentDim.getParentProperties();

			SpacePosition planetSpacePos = currentDim.getSpacePosition();

			SpacePosition modifiedPosition = new SpacePosition().getFromSpherical(currentDim.getRenderSizePlanetView()*1.1, 0);

			spacePosition.x = modifiedPosition.x;
			spacePosition.y = modifiedPosition.y;
			spacePosition.z = modifiedPosition.z;
			spacePosition.star = planetSpacePos.star;

			spacePosition.world = planetSpacePos.world;
			setInSpaceFlight(true);
			setRCS(true);
			setInOrbit(true);
			this.setMotion(0,0,0);

			destinationDimId = ARConfiguration.GetSpaceDimId();
			destPos.x = 0f;
			destPos.y = (float) getEntryHeight(destinationDimId);
			destPos.z = 0f;

			for(Entity e : getPassengers())
			{
				if(e instanceof PlayerEntity)
				{
					PacketHandler.sendToPlayer(new PacketEntity(this, (byte)PacketType.SENDSPACEPOS.ordinal()), (PlayerEntity) e);
				}
			}

		}
<<<<<<< HEAD
		else
		{
			//TODO: maybe add orbit dimension
			this.setMotion(this.getMotion().x, -this.getMotion().y, this.getMotion().z);
=======
		else {
			this.motionY = -this.motionY;
>>>>>>> origin/feature/nuclearthermalrockets
			setInOrbit(true);
			//If going to a station or something make sure to set coords accordingly
			//If in space land on the planet, if on the planet go to space
			if((ARConfiguration.GetSpaceDimId().equals(destinationDimId) || (ARConfiguration.GetSpaceDimId().equals(ZUtils.getDimensionIdentifier(this.world)) && !getInSpaceFlight())) && ZUtils.getDimensionIdentifier(this.world) != destinationDimId) {
				Vector3F<Float> pos = storage.getDestinationCoordinates(destinationDimId, true);
<<<<<<< HEAD
				storage.setDestinationCoordinates(new Vector3F<Float>((float)this.getPosX(), (float)this.getPosY(), (float)this.getPosZ()), ZUtils.getDimensionIdentifier(this.world));
=======
				storage.setDestinationCoordinates(new Vector3F<>((float) this.posX, (float) this.posY, (float) this.posZ), this.world.provider.getDimension());
>>>>>>> origin/feature/nuclearthermalrockets
				if(pos != null) {

					//Make player confirm deorbit if a player is riding the rocket
					if(hasHumanPassenger()) {
						setInFlight(false);
						pos.y = (float) getEntryHeight(destinationDimId);

					}

					this.changeDimension(ZUtils.getWorld(destinationDimId), pos.x, pos.y, pos.z);
					return;
				}
			}


			//if coordinates are overridden, make sure we grab them
			destPos = storage.getDestinationCoordinates(destinationDimId, true);
			if(destPos == null)
<<<<<<< HEAD
				destPos = new Vector3F<Float>((float)getPosX(), (float)getEntryHeight(destinationDimId), (float)getPosZ());
=======
				destPos = new Vector3F<>((float) posX, (float) getEntryHeight(destinationDimId), (float) posZ);
>>>>>>> origin/feature/nuclearthermalrockets

			if(hasHumanPassenger()) {
				//Make player confirm deorbit if a player is riding the rocket
				setInFlight(false);

				if(DimensionManager.getInstance().getDimensionProperties(destinationDimId).getName().equals("Luna")) {
					for(Entity player : this.getPassengers()) {
						if(player instanceof PlayerEntity) {
							ARAdvancements.triggerAchievement(ARAdvancements.MOON_LANDING, (ServerPlayerEntity) player);
							if(!DimensionManager.hasReachedMoon)
								ARAdvancements.triggerAchievement(ARAdvancements.ONE_SMALL_STEP, (ServerPlayerEntity) player);
						}
					}
					DimensionManager.hasReachedMoon = true;
				}
			}
			destPos.y = (float) getEntryHeight(destinationDimId);
		}

		//Reset override coords
		setOverriddenCoords(Constants.INVALID_PLANET, 0, 0, 0);

		if(destinationDimId != ZUtils.getDimensionIdentifier(this.world))
			this.changeDimension(!DimensionManager.getInstance().isDimensionCreated(ZUtils.getDimensionIdentifier(this.world)) ? ZUtils.getWorld(DimensionManager.defaultSpaceDimensionProperties.getId()) : ZUtils.getWorld(destinationDimId), destPos.x, getEntryHeight(destinationDimId), destPos.z);
		else
		{
			List<Entity> eList = this.getPassengers();
			for(Entity e : eList) {
				e.stopRiding();
				e.setPositionAndUpdate(destPos.x, destPos.y, destPos.z);
			}
			this.setPositionAndUpdate(destPos.x, destPos.y, destPos.z);
			this.ticksExisted = 0;
			((ServerWorld)world).resetUpdateEntityTick();
			for(Entity e : eList) {
				e.startRiding(this, true);
			}
		}
	}

	private void unpackSatellites() {
		List<TileSatelliteHatch> satelliteHatches = storage.getSatelliteHatches();

		for(TileSatelliteHatch tile : satelliteHatches) {
			SatelliteBase satellite = tile.getSatellite();
			if(satellite == null) {
				ItemStack stack = tile.getStackInSlot(0);
				if(!stack.isEmpty() && stack.getItem() == AdvancedRocketryItems.itemSpaceStation) {
					StorageChunk storage = ((ItemPackedStructure)stack.getItem()).getStructure(stack);
<<<<<<< HEAD
					ISpaceObject object = SpaceObjectManager.getSpaceManager().getSpaceStation(ItemStationChip.getUUID(stack));
=======
					ISpaceObject spaceObject = SpaceObjectManager.getSpaceManager().getSpaceStation(ItemStationChip.getUUID(stack));
>>>>>>> origin/feature/nuclearthermalrockets

					//in case of no NBT data or the like
					if(spaceObject == null) {
						tile.setInventorySlotContents(0, ItemStack.EMPTY);
						continue;
					}

<<<<<<< HEAD
					SpaceObjectManager.getSpaceManager().moveStationToBody(object, 
							DimensionManager.getEffectiveDimId(ZUtils.getDimensionIdentifier(this.world), new BlockPos(getPositionVec())).getId() );
=======
					SpaceObjectManager.getSpaceManager().moveStationToBody(spaceObject,
							DimensionManager.getEffectiveDimId(this.world.provider.getDimension(), getPosition()).getId() );
>>>>>>> origin/feature/nuclearthermalrockets

					//Vector3F<Integer> spawn = spaceObject.getSpawnLocation();

					spaceObject.onModuleUnpack(storage);
					tile.setInventorySlotContents(0, ItemStack.EMPTY);
				}
			}
			else {
				ResourceLocation destinationId = storage.getDestinationDimId(ZUtils.getDimensionIdentifier(this.world), (int)getPosX(), (int)getPosZ());
				DimensionProperties properties = DimensionManager.getEffectiveDimId(ZUtils.getDimensionIdentifier(this.world), new BlockPos(getPositionVec()));
				ResourceLocation world2;
				if(ARConfiguration.GetSpaceDimId().equals(destinationId) || Constants.INVALID_PLANET.equals(destinationId))
					world2 = properties.getId();
				else
					world2 = destinationId;

				properties.addSatellite(satellite, world2, world.isRemote);
				tile.setInventorySlotContents(0, ItemStack.EMPTY);
			}
		}
	}

	/**
	 * Called immediately before launch
	 */
	@Override
	public void prepareLaunch() {

		if(this.dataManager.get(LAUNCH_COUNTER) > 0) {
			this.dataManager.set(LAUNCH_COUNTER, -1);
			if(world.isRemote)
				PacketHandler.sendToServer(new PacketEntity(this, (byte)EntityRocket.PacketType.ABORTLAUNCH.ordinal()));
			return;
		}

		if(isInOrbit()) {
			setInFlight(true);
			return;
		}

		RocketPreLaunchEvent event = new RocketEvent.RocketPreLaunchEvent(this);
		MinecraftForge.EVENT_BUS.post(event);

		if(!event.isCanceled()) {
			if(world.isRemote)
				PacketHandler.sendToServer(new PacketEntity(this, (byte)EntityRocket.PacketType.LAUNCH.ordinal()));
			this.dataManager.set(LAUNCH_COUNTER, 200);
		}
	}

	/**
	 * Launches the rocket post determining its height, checking whether it can launch to the selected planet and whether it can exist,
	 * among other factors. Also handles orbital height calculations
	 */
	@Override
	public void launch() {

		if(isInFlight())
			return;

		boolean allowLaunch = false;


<<<<<<< HEAD
		if(!(ARConfiguration.getCurrentConfig().experimentalSpaceFlight.get() && storage.getGuidanceComputer() != null && storage.getGuidanceComputer().isEmpty()))
		{
=======
		if(ARConfiguration.getCurrentConfig().experimentalSpaceFlight && storage.getGuidanceComputer() != null && storage.getGuidanceComputer().isEmpty()) {
			allowLaunch = true;
		}
		else {
>>>>>>> origin/feature/nuclearthermalrockets

			//Get destination dimid and lock the computer
			//TODO: lock the computer
			destinationDimId = storage.getDestinationDimId(world, (int)this.getPosX(), (int)this.getPosZ());

<<<<<<< HEAD
			//TODO: make sure this doesn't break asteriod mining
			if(!(DimensionManager.getInstance().canTravelTo(destinationDimId) || (Constants.INVALID_PLANET.equals(destinationDimId) && storage.getSatelliteHatches().size() != 0))) {
				setError(LibVulpes.proxy.getLocalizedString("error.rocket.cannotgetthere"));
				return;
			}

			ResourceLocation finalDest = destinationDimId;
			if(ARConfiguration.GetSpaceDimId().equals(destinationDimId)) {
				ISpaceObject obj = null;
=======
			if(!(DimensionManager.getInstance().canTravelTo(destinationDimId) || (destinationDimId == Constants.INVALID_PLANET && storage.getSatelliteHatches().size() != 0))) {
				setError(LibVulpes.proxy.getLocalizedString("error.rocket.cannotGetThere"));
				return;
			}

			int finalDest = destinationDimId;
			if(destinationDimId == ARConfiguration.getCurrentConfig().spaceDimId) {
				ISpaceObject spaceObject = null;
>>>>>>> origin/feature/nuclearthermalrockets
				Vector3F<Float> vec = storage.getDestinationCoordinates(destinationDimId,false);

				if(vec != null)
					spaceObject = SpaceObjectManager.getSpaceManager().getSpaceStationFromBlockCoords(new BlockPos(vec.x, vec.y, vec.z));

				if( spaceObject != null)
					finalDest = spaceObject.getOrbitingPlanetId();
				else { 
					setError(LibVulpes.proxy.getLocalizedString("error.rocket.destinationnotexist"));
					return;
				}
			}


			//If we're on a space station get the id of the planet, not the station
<<<<<<< HEAD
			ResourceLocation thisDimId = ZUtils.getDimensionIdentifier(this.world);
			if(ARConfiguration.GetSpaceDimId().equals(thisDimId)) {
				ISpaceObject object = SpaceObjectManager.getSpaceManager().getSpaceStationFromBlockCoords(new BlockPos(this.getPositionVec()));
				if(object != null)
					thisDimId = object.getProperties().getParentProperties().getId();
			}

			//Check to see if it's possible to reach
			if(!Constants.INVALID_PLANET.equals(finalDest) && (!storage.hasWarpCore() || !DimensionManager.getInstance().getDimensionProperties(thisDimId).getStarId().equals(DimensionManager.getInstance().getDimensionProperties(finalDest).getStarId()) ) && !PlanetaryTravelHelper.isTravelAnywhereInPlanetarySystem(finalDest, thisDimId)) {
				setError(LibVulpes.proxy.getLocalizedString("error.rocket.notsamesystem"));
=======
			int thisDimId = this.world.provider.getDimension();
			if(this.world.provider.getDimension() == ARConfiguration.getCurrentConfig().spaceDimId) {
				ISpaceObject spaceObject = SpaceObjectManager.getSpaceManager().getSpaceStationFromBlockCoords(this.getPosition());
				if(spaceObject != null)
					thisDimId = spaceObject.getProperties().getParentProperties().getId();
			}

			//Check to see if it's possible to reach
			if(finalDest != Constants.INVALID_PLANET && (!stats.isNuclear() || DimensionManager.getInstance().getDimensionProperties(finalDest).getStarId() != DimensionManager.getInstance().getDimensionProperties(thisDimId).getStarId()) && !PlanetaryTravelHelper.isTravelAnywhereInPlanetarySystem(finalDest, thisDimId)) {
				setError(LibVulpes.proxy.getLocalizedString("error.rocket.notSameSystem"));
>>>>>>> origin/feature/nuclearthermalrockets
				return;
			}
		}


		//Check to see what place we should be going to
		//This is bad but it works and is mostly intelligible so it's here for now
		stats.orbitHeight = (storage.getGuidanceComputer() == null) ? getEntryHeight(ZUtils.getDimensionIdentifier(this.world)) : storage.getGuidanceComputer().getLaunchSequence(ZUtils.getDimensionIdentifier(this.world), this.getPosition());


		//TODO: Clean this logic a bit?
		if(allowLaunch || !stats.hasSeat() || ((DimensionManager.getInstance().isDimensionCreated(destinationDimId)) || ARConfiguration.GetSpaceDimId().equals(destinationDimId) || Constants.INVALID_PLANET.equals(destinationDimId)) ) { //Abort if destination is invalid


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
<<<<<<< HEAD
		//When we launch, we want to damage blocks a second time if possible
		if (ARConfiguration.getCurrentConfig().launchingDestroysBlocks.get())
		    damageGroundBelowRocket(world, (int)getPosX(), (int)getPosY(), (int)getPosZ(), (int)Math.pow(stats.getThrust(), 0.3333));
=======
>>>>>>> origin/feature/nuclearthermalrockets
	}

	/**
	 * Damages the ground beneath the rocket, depending on block type
	 */
	private void damageGroundBelowRocket(World world, int x, int y, int z, int radius) {
<<<<<<< HEAD
		//Actually, we affect the blocks that are one lower
		y--;
		for (int i = 0; i <= radius; i++) {
			for (int j = 0; j <= radius; j++) {
				for (int k = 1; k >= -2; k--) {
					//Set blocks to their damaged variants, the if statements make sure we don't set cardinal directions twice the times we should be
					setDamagedBlock(getDamagedBlock(world.getBlockState(new BlockPos(x + i, y + k, z + j))), world, new BlockPos(x + i, y + k, z + j));
					if (j != 0)
					    setDamagedBlock(getDamagedBlock(world.getBlockState(new BlockPos(x + i, y + k, z - j))), world, new BlockPos(x + i, y + k, z - j));
					if (i != 0) {
						setDamagedBlock(getDamagedBlock(world.getBlockState(new BlockPos(x - i, y + k, z + j))), world, new BlockPos(x - i, y + k, z + j));
						if (j != 0)
						    setDamagedBlock(getDamagedBlock(world.getBlockState(new BlockPos(x - i, y + k, z - j))), world, new BlockPos(x - i, y + k, z - j));
					}
					//Set fire above that
					BlockPos blocksAbove = new BlockPos(x + i, y + k, z + j);
					if (world.getBlockState(blocksAbove).getMaterial().isReplaceable() || world.getBlockState(blocksAbove).getBlock() == Blocks.AIR) {
						world.setBlockState(blocksAbove, Blocks.FIRE.getDefaultState());
=======
		//Start on the same level as the bottom of the rocket
		BlockPos center = new BlockPos(x - 1, y, z);
		for (int i = -radius; i <= radius; i++) {
			for (int j = -radius; j <= radius; j++) {
				for (int k = -3; k < radius/12; k++) {
					//Check for a circle, not a square
					BlockPos position = center.add(i, k, j);
					if (center.distanceSq(position) <= radius * radius) {
						//Set blocks to their damaged variants
						if (rand.nextInt(80) == 0 && getDamagedBlock(world.getBlockState(position)) != null) {
							world.setBlockState(position, getDamagedBlock(world.getBlockState(position)));
						}
						//Always set fire above that
						BlockPos blockAbove = position.add(0, 1, 0);
						if ( world.getBlockState(blockAbove).getBlock().isAir(world.getBlockState(blockAbove), world, blockAbove)) {
							world.setBlockState(blockAbove, AdvancedRocketryBlocks.blockRocketFire.getDefaultState());
						}
>>>>>>> origin/feature/nuclearthermalrockets
					}
				}
			}
		}
	}

<<<<<<< HEAD
	private static BlockState getDamagedBlock(BlockState blockState) {
		boolean isRegolith = (blockState.getBlock() == AdvancedRocketryBlocks.blockMoonTurf || blockState.getBlock() == AdvancedRocketryBlocks.blockMoonTurfDark);
		Material material = blockState.getMaterial();
		
		if (ZUtils.isBlockTag(blockState.getBlock(), new ResourceLocation("minecraft:stone"))) {
=======
	/**
	 * @param blockState the blockstate to damage
	 * @return the blockstate that the input blockstate turns into
	 */
	private static IBlockState getDamagedBlock(IBlockState blockState) {
		ItemStack stack = new ItemStack(blockState.getBlock(), 1, blockState.getBlock().getMetaFromState(blockState));
		if (ZUtils.isItemInOreDict(stack, "stone") || blockState.getBlock() == Blocks.STONEBRICK || ZUtils.isItemInOreDict(stack, "bricksStone")) {
>>>>>>> origin/feature/nuclearthermalrockets
			return Blocks.COBBLESTONE.getDefaultState();
		} else if (ZUtils.isBlockTag(blockState.getBlock(), new ResourceLocation("minecraft:cobblestone")) || ZUtils.isBlockTag(blockState.getBlock(), new ResourceLocation("minecraft:stone"))) {
			return AdvancedRocketryBlocks.blockBasalt.getDefaultState();
		} else if (blockState.getBlock() == AdvancedRocketryBlocks.blockBasalt) {
			return Blocks.MAGMA_BLOCK.getDefaultState();
		} else if (blockState.getBlock() == Blocks.NETHERRACK) {
			return Blocks.MAGMA_BLOCK.getDefaultState();
		} else if (blockState.getBlock() == Blocks.MAGMA_BLOCK) {
			return Blocks.LAVA.getDefaultState();
<<<<<<< HEAD
		} else if (blockState.getBlock() == Blocks.GRASS_BLOCK) {
			return Blocks.DIRT.getDefaultState();
		} else if (material == Material.EARTH && !isRegolith) {
			return Blocks.SAND.getDefaultState();
		} else if (blockState.getBlock() instanceof SandBlock || ZUtils.isBlockTag(blockState.getBlock(), new ResourceLocation("advancedrocketry:regolith"))) {
			return Blocks.GLASS.getDefaultState();
		} else if (material == Material.ICE || material == Material.PACKED_ICE || ((material == Material.SNOW || material == Material.SNOW_BLOCK) && blockState.getBlock() != Blocks.SNOW )) {
			return Blocks.WATER.getDefaultState();
		} else if (material == Material.WATER || blockState.getBlock() == Blocks.SNOW) {
			return Blocks.AIR.getDefaultState();
		} else if (material == Material.WOOD || material == Material.LEAVES || material == Material.PLANTS || material == Material.GOURD || material == Material.WEB ||material == Material.WOOL || material == Material.CARPET || material == Material.CACTUS || material == Material.SPONGE) {
			return Blocks.FIRE.getDefaultState();
		}
		return blockState;
	}

	private static void setDamagedBlock(BlockState blockState, World world, BlockPos position) {
		if (blockState != world.getBlockState(position)) {
			world.setBlockState(position, blockState);
		}
=======
		} else if (blockState.getMaterial() == Material.GRASS) {
			return Blocks.DIRT.getDefaultState();
		} else if (blockState.getMaterial() == Material.GROUND && !(blockState.getBlock() instanceof BlockRegolith)) {
			return Blocks.SAND.getDefaultState();
		} else if (blockState.getBlock() instanceof BlockSand || blockState.getBlock() instanceof BlockRegolith || ZUtils.isItemInOreDict(stack, "regolith") || ZUtils.isItemInOreDict(stack, "sandstone")) {
			return Blocks.GLASS.getDefaultState();
		} else if (blockState.getMaterial() == Material.ICE || blockState.getMaterial() == Material.PACKED_ICE || ((blockState.getMaterial() == Material.SNOW || blockState.getMaterial() == Material.CRAFTED_SNOW) && blockState.getBlock() != Blocks.SNOW_LAYER )) {
			return Blocks.WATER.getDefaultState();
		} else if (blockState.getMaterial() == Material.WATER || blockState.getBlock() == Blocks.SNOW_LAYER) {
			return Blocks.AIR.getDefaultState();
		} else if (blockState.getMaterial() == Material.WOOD || blockState.getMaterial() == Material.LEAVES || blockState.getMaterial() == Material.PLANTS || blockState.getMaterial() == Material.GOURD || blockState.getMaterial() == Material.WEB || blockState.getMaterial() == Material.CLOTH || blockState.getMaterial() == Material.CARPET || blockState.getMaterial() == Material.CACTUS || blockState.getMaterial() == Material.SPONGE) {
			return Blocks.FIRE.getDefaultState();
		}
		return null;
>>>>>>> origin/feature/nuclearthermalrockets
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
		storage.pasteInWorld(this.world, (int)(this.getPosX() - storage.getSizeX()/2f), (int)this.getPosY(), (int)(this.getPosZ() - storage.getSizeZ()/2f));
		this.remove();
	}

	@Override
	public void remove() {
		super.remove();

		/*if(storage != null && storage.world.displayListIndex != -1)
			GLAllocation.deleteDisplayLists(storage.world.displayListIndex);*/

		//unlink any connected tiles
		Iterator<IInfrastructure> connectedTiles = connectedInfrastructure.iterator();
		while(connectedTiles.hasNext()) {
			connectedTiles.next().unlinkRocket();
			connectedTiles.remove();
		}

	}

	public void setOverriddenCoords(ResourceLocation dimId, float x, float y, float z) {
		TileGuidanceComputer tile = storage.getGuidanceComputer();
		if(tile != null) {
			tile.setFallbackDestination(dimId, new Vector3F<>(x, y, z));
		}
	}



<<<<<<< HEAD
	public Entity changeDimension(ServerWorld newDimId) {
		return changeDimension(newDimId, this.getPosX(), (double)getEntryHeight(ZUtils.getDimensionIdentifier(newDimId)), this.getPosZ());
=======
	@Override
	public Entity changeDimension(int newDimId) {
		return changeDimension(newDimId, this.posX, getEntryHeight(newDimId), this.posZ);
>>>>>>> origin/feature/nuclearthermalrockets
	}

	@Nullable
	public Entity changeDimension(ServerWorld dimensionIn, double x, double y, double z)
	{
		if (!this.world.isRemote && this.isAlive())
		{			

			if(!DimensionManager.getInstance().canTravelTo(dimensionIn)) {
				AdvancedRocketry.logger.warn("Rocket trying to travel from Dim" + ZUtils.getDimensionIdentifier(this.world) + " to Dim " + dimensionIn + ".  target not accessible by rocket from launch dim");
				return null;
			}

			lastDimensionFrom = ZUtils.getDimensionIdentifier(this.world);

			List<Entity> passengers = getPassengers();
			ServerWorld worldserver = (ServerWorld) this.world;
			ServerWorld worldserver1 = dimensionIn;

			
			PortalInfo info = new PortalInfo(new Vector3d(x, y, z), this.getMotion(), this.rotationYaw, this.rotationPitch);
			ITeleporter teleporter = new TeleporterNoPortal(worldserver1, info);
			Entity entity = changeDimension(dimensionIn, teleporter);

			if(entity == null)
				return null;

			int timeOffset = 1;
			for(Entity e : passengers) {
				PlanetEventHandler.addDelayedTransition(new TransitionEntity(worldserver.getGameTime() + ++timeOffset, e, dimensionIn, new BlockPos(x, y, z), entity));
			}
			return entity;
		}
		return null;
	}

	/**
	 * Prepares this entity in new dimension by copying NBT data from entity in old dimension
	 */
	public void copyDataFromOld(Entity entityIn)
	{
		super.copyDataFromOld(entityIn);
		this.getPassengers().clear();
	}

<<<<<<< HEAD
	protected void readNetworkableNBT(CompoundNBT nbt) {
		//Normal function checks for the existance of the data anyway
		readAdditional(nbt);
	}

	public EntitySize getSize(Pose poseIn) {
		return mySize;
	}
	
	private void setSize(float x, float z)
	{
		mySize = new EntitySize(x,z, false);
=======
	protected void readNetworkableNBT(NBTTagCompound nbt) {
		//Normal function checks for the existence of the data anyway
		readEntityFromNBT(nbt);
>>>>>>> origin/feature/nuclearthermalrockets
	}

	@Override
	protected void readAdditional(CompoundNBT nbt) {
		setInOrbit(isInOrbit = nbt.getBoolean("orbit"));
		rcs_mode_counter = nbt.getInt("rcs_mode_cnt");
		setInSpaceFlight(nbt.getBoolean("inSpaceFlight"));
		rcs_mode = nbt.getBoolean("rcs_mode") || getInSpaceFlight();
		setRCS(rcs_mode);
		stats.readFromNBT(nbt);

		FuelType fuelType = getRocketFuelType();
		if(fuelType != null) {
			setFuelAmount(fuelType, stats.getFuelAmount(fuelType));
			if (getRocketFuelType() == FuelType.LIQUID_BIPROPELLANT)
				setFuelAmount(FuelType.LIQUID_OXIDIZER, stats.getFuelAmount(FuelType.LIQUID_OXIDIZER));
		}

		setInFlight(isInFlight = nbt.getBoolean("flight"));

<<<<<<< HEAD
		readMissionPersistantNBT(nbt);
		if(nbt.contains("data"))
=======
		readMissionPersistentNBT(nbt);
		if(nbt.hasKey("data"))
>>>>>>> origin/feature/nuclearthermalrockets
		{
			if(storage == null) 
				storage = new StorageChunk();

			storage.readFromNBT(nbt.getCompound("data"));
			storage.setEntity(this);
			this.setSize(Math.max(storage.getSizeX(), storage.getSizeZ()), storage.getSizeY());
		}

<<<<<<< HEAD
		if(nbt.contains("infrastructure")) {
			ListNBT tagList = nbt.getList("infrastructure", 10);
			for (int i = 0; i < tagList.size(); i++) {
				int coords[] = tagList.getCompound(i).getIntArray("loc");
=======
		if(nbt.hasKey("infrastructure")) {
			NBTTagList tagList = nbt.getTagList("infrastructure", 10);
			for (int i = 0; i < tagList.tagCount(); i++) {
				int[] coords = tagList.getCompoundTagAt(i).getIntArray("loc");
>>>>>>> origin/feature/nuclearthermalrockets

				infrastructureCoords.add(new HashedBlockPosition(coords[0], coords[1], coords[2]));

			}
		}
		
		if(nbt.contains("destinationDimId"))
			destinationDimId = new ResourceLocation(nbt.getString("destinationDimId"));

		if(nbt.contains("lastDimensionFrom"))
			lastDimensionFrom = new ResourceLocation(nbt.getString("lastDimensionFrom"));
		//Satellite
		if(nbt.contains("satellite")) {
			CompoundNBT satalliteNbt = nbt.getCompound("satellite");
			satellite = SatelliteRegistry.createFromNBT(satalliteNbt);
		}

		spacePosition.readFromNBT(nbt);
	}

<<<<<<< HEAD
	protected void writeNetworkableNBT(CompoundNBT nbt) {
		writeMissionPersistantNBT(nbt);
		nbt.putBoolean("orbit", isInOrbit());
		nbt.putBoolean("flight", isInFlight());
		nbt.putBoolean("rcs_mode", rcs_mode);
		nbt.putInt("rcs_mode_cnt", rcs_mode_counter);
		nbt.putBoolean("inSpaceFlight", getInSpaceFlight());
=======
	protected void writeNetworkableNBT(NBTTagCompound nbt) {
		writeMissionPersistentNBT(nbt);
		nbt.setBoolean("orbit", isInOrbit());
		nbt.setBoolean("flight", isInFlight());
		nbt.setBoolean("rcs_mode", rcs_mode);
		nbt.setInteger("rcs_mode_cnt", rcs_mode_counter);
		nbt.setBoolean("inSpaceFlight", getInSpaceFlight());
>>>>>>> origin/feature/nuclearthermalrockets
		stats.writeToNBT(nbt);

		if(!infrastructureCoords.isEmpty()) {
			ListNBT itemList = new ListNBT();
			for(HashedBlockPosition inf : infrastructureCoords)
			{

				CompoundNBT tag = new CompoundNBT();
				tag.putIntArray("loc", new int[] {inf.x, inf.y, inf.z});
				itemList.add(tag);

			}
			nbt.put("infrastructure", itemList);
		}

		if(destinationDimId != null)
			nbt.putString("destinationDimId", destinationDimId.toString());

		//Satellite
		if(satellite != null) {
			CompoundNBT satalliteNbt = new CompoundNBT();
			satellite.writeToNBT(satalliteNbt);
			satalliteNbt.putString("DataType",SatelliteRegistry.getKey(satellite.getClass()));

			nbt.put("satellite", satalliteNbt);
		}
		spacePosition.writeToNBT(nbt);
	}

<<<<<<< HEAD
	public void writeMissionPersistantNBT(CompoundNBT nbt) {

	}

	public void readMissionPersistantNBT(CompoundNBT nbt) {
=======
	public void writeMissionPersistentNBT(NBTTagCompound nbt) {

	}

	public void readMissionPersistentNBT(NBTTagCompound nbt) {
>>>>>>> origin/feature/nuclearthermalrockets

	}

	@Override
<<<<<<< HEAD
	protected void writeAdditional(CompoundNBT nbt) {
=======
	protected void writeEntityToNBT(@Nonnull NBTTagCompound nbt) {
>>>>>>> origin/feature/nuclearthermalrockets

		writeNetworkableNBT(nbt);
		if(storage != null) {
			CompoundNBT blocks = new CompoundNBT();
			storage.writeToNBT(blocks);
			nbt.put("data", blocks);
		}

		//TODO handle non tile Infrastructure
		
		if(lastDimensionFrom != null)
			nbt.putString("lastDimensionFrom", lastDimensionFrom.toString());
	}

	@Override
	public void readDataFromNetwork(PacketBuffer in, byte packetId,
			CompoundNBT nbt) {
		if(packetId == PacketType.RECIEVENBT.ordinal()) {
			storage = new StorageChunk();
			storage.setEntity(this);
			storage.readFromNetwork(in);
		}
		else if(packetId == PacketType.SENDPLANETDATA.ordinal()) {
			nbt.putString("selection", in.readString());
		}
		else if(packetId == PacketType.TURNUPDATE.ordinal()) {
			nbt.putBoolean("left", in.readBoolean());
			nbt.putBoolean("right", in.readBoolean());
			nbt.putBoolean("up", in.readBoolean());
			nbt.putBoolean("down", in.readBoolean());
		}
		else if(packetId == PacketType.SENDSPACEPOS.ordinal()) {
			SpacePosition position = new SpacePosition();
			position.x = in.readDouble();
			position.y = in.readDouble();
			position.z = in.readDouble();

			boolean hasWorld = in.readBoolean();
			if(hasWorld)
				position.world = DimensionManager.getInstance().getDimensionProperties(new ResourceLocation(in.readString()));

			boolean hasStar = in.readBoolean();
			if(hasStar)
				position.star = DimensionManager.getInstance().getStar(new ResourceLocation(in.readString()));

			position.writeToNBT(nbt);
		}
	}

	@Override
	public void writeDataToNetwork(PacketBuffer out, byte id) {

		if(id == PacketType.RECIEVENBT.ordinal()) {
			storage.writeToNetwork(out);
		}
		else if(id == PacketType.SENDPLANETDATA.ordinal()) {
			if(world.isRemote)
				out.writeString(container.getSelectedSystem().toString());
			else {
				if(storage.getGuidanceComputer() != null) {
					ItemStack stack = storage.getGuidanceComputer().getStackInSlot(0);
<<<<<<< HEAD
					if(stack != null && stack.getItem() == AdvancedRocketryItems.itemPlanetIdChip) {
						out.writeString(((ItemPlanetIdentificationChip)AdvancedRocketryItems.itemPlanetIdChip).getDimensionId(stack).toString());
=======
					if(!stack.isEmpty() && stack.getItem() == AdvancedRocketryItems.itemPlanetIdChip) {
						out.writeInt(((ItemPlanetIdentificationChip)AdvancedRocketryItems.itemPlanetIdChip).getDimensionId(stack));
>>>>>>> origin/feature/nuclearthermalrockets
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
		else if (id == PacketType.SENDSPACEPOS.ordinal()) {
			out.writeDouble(this.spacePosition.x);
			out.writeDouble(this.spacePosition.y);
			out.writeDouble(this.spacePosition.z);
			boolean hasWorld = this.spacePosition.world != null;
			boolean hasStar = this.spacePosition.star != null;

			out.writeBoolean(hasWorld);
			if(hasWorld)
				out.writeString(spacePosition.world.getId().toString());
			out.writeBoolean(hasStar);
			if(hasStar)
				out.writeString(spacePosition.star.getId().toString());
		}
	}

	@Override
	public void useNetworkData(PlayerEntity player, Dist side, byte id,
			CompoundNBT nbt) {


		if(id == PacketType.RECIEVENBT.ordinal()) {
			this.readAdditional(nbt);
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
				CompoundNBT nbtdata = new CompoundNBT();

				this.writeNetworkableNBT(nbtdata);
				PacketHandler.sendToPlayer(new PacketEntity(this, (byte)PacketType.RECIEVENBT.ordinal(), nbtdata), player);

			}
		}
		else if(id == PacketType.FORCEMOUNT.ordinal()) { //Used for pesky dimension transfers
			//When dimensions are transferred make sure to remount the player on the client
			if(!acceptedPacket) {
				acceptedPacket = true;
				player.setPositionAndRotation(this.getPosX(), this.getPosY(), this.getPosZ(), player.rotationYaw, player.rotationPitch);
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
			NetworkHooks.openGui((ServerPlayerEntity)player, (INamedContainerProvider)this, packetBuffer -> {packetBuffer.writeInt(GuiHandler.guiId.MODULARFULLSCREEN.ordinal());packetBuffer.writeInt(this.getEntityId()); });
		}
		else if(id == PacketType.SENDPLANETDATA.ordinal()) {
			ItemStack stack = storage.getGuidanceComputer().getStackInSlot(0);
<<<<<<< HEAD
			if(stack != null && stack.getItem() == AdvancedRocketryItems.itemPlanetIdChip) {
				((ItemPlanetIdentificationChip)AdvancedRocketryItems.itemPlanetIdChip).setDimensionId(stack, new ResourceLocation(nbt.getString("selection")));
=======
			if(!stack.isEmpty() && stack.getItem() == AdvancedRocketryItems.itemPlanetIdChip) {
				((ItemPlanetIdentificationChip)AdvancedRocketryItems.itemPlanetIdChip).setDimensionId(stack, nbt.getInteger("selection"));
>>>>>>> origin/feature/nuclearthermalrockets

				//Send data back to sync destination dims
				if(!world.isRemote) {
					PacketHandler.sendToPlayersTrackingEntity(new PacketEntity(this, (byte)PacketType.SENDPLANETDATA.ordinal()), this);
				}
			}
		}
		else if(id == PacketType.DISCONNECTINFRASTRUCTURE.ordinal()) {
			int[] pos = nbt.getIntArray("pos");

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
			player.stopRiding();
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
		else if(id == PacketType.ABORTLAUNCH.ordinal()) {
			this.dataManager.set(LAUNCH_COUNTER, -1);
		}
		else if(id == PacketType.SENDSPACEPOS.ordinal()) {
			this.spacePosition.readFromNBT(nbt);
		}
		else if(id >= STATION_LOC_OFFSET + BUTTON_ID_OFFSET) {
			int id2 = id - (STATION_LOC_OFFSET + BUTTON_ID_OFFSET) - 1;
			setDestLandingPad(id2);

			//propagate change back to the clients
			if(!world.isRemote)
				PacketHandler.sendToPlayersTrackingEntity(new PacketEntity(this, id), this);
		}
		else if(id > BUTTON_ID_OFFSET) {
			TileEntity tile = storage.getGUITiles().get(id - BUTTON_ID_OFFSET - tilebuttonOffset);

			//Welcome to super hack time with packets
			//Due to the fact the client uses the player's current world to open the gui, we have to move the client between worlds for a bit
			PacketHandler.sendToPlayer(new PacketEntity(this, (byte)PacketType.CHANGEWORLD.ordinal()), player);
			storage.getBlockState(tile.getPos()).getBlock().onBlockActivated(storage.getBlockState(tile.getPos()), storage.world, tile.getPos(), player, Hand.MAIN_HAND, new BlockRayTraceResult(new Vector3d(0,0,0), Direction.DOWN, tile.getPos(), false));
			PacketHandler.sendToPlayer(new PacketEntity(this, (byte)PacketType.REVERTWORLD.ordinal()), player);
		}
	}

	private void setDestLandingPad(int padIndex) {
		ItemStack slot0 = storage.getGuidanceComputer().getStackInSlot(0);
		ResourceLocation uuid;
		//Station location select
<<<<<<< HEAD
		if( slot0 != null && slot0.getItem() instanceof ItemStationChip && !Constants.INVALID_PLANET.equals((uuid = ItemStationChip.getUUID(slot0)))) {
			ISpaceObject obj = SpaceObjectManager.getSpaceManager().getSpaceStation(uuid);
=======
		if(!slot0.isEmpty() && slot0.getItem() instanceof ItemStationChip && (uuid = ItemStationChip.getUUID(slot0)) != 0) {
			ISpaceObject spaceObject = SpaceObjectManager.getSpaceManager().getSpaceStation(uuid);
>>>>>>> origin/feature/nuclearthermalrockets

			if(spaceObject instanceof SpaceStationObject) {

				if(padIndex == -1) {
					storage.getGuidanceComputer().setLandingLocation(uuid, null);
				}
				else {

					StationLandingLocation location = ((SpaceStationObject) spaceObject).getLandingPads().get(padIndex);
					if(location != null && !location.getOccupied())
						storage.getGuidanceComputer().setLandingLocation(uuid, location);
				}
			}

			StationLandingLocation location = storage.getGuidanceComputer().getLandingLocation(uuid);
			landingPadDisplayText.setText(location != null ? location.toString() : "None Selected");
		}
	}

	@Override
	public void updatePassenger(@Nonnull Entity entity)
	{
<<<<<<< HEAD
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
					yPos = yNew + this.getPosY() + halfy;
					zPos = zNew;

					//Now do yaw
					float yawAngle = (float)(this.rotationYaw*Math.PI/180f);
					double xNew = (xPos)*MathHelper.cos(-yawAngle) + (zPos)*MathHelper.sin(-yawAngle);
					zNew = zPos*MathHelper.cos(yawAngle) + (xPos)*MathHelper.sin(yawAngle);
					xPos = this.getPosX() + xNew ;
					zPos = this.getPosZ() + zNew;


					entity.setPosition(xPos, yPos, zPos );
				} catch (IndexOutOfBoundsException e) {
					entity.setPosition(this.getPosX() , this.getPosY() , this.getPosZ() );
				}
			}
			else
				entity.setPosition(this.getPosX() , this.getPosY() , this.getPosZ() );
=======
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
>>>>>>> origin/feature/nuclearthermalrockets
		}
		else
			entity.setPosition(this.posX , this.posY , this.posZ );
	}

	@Override
	public List<ModuleBase> getModules(int ID, PlayerEntity player) {
		List<ModuleBase> modules;
		//If the rocket is flight don't load the interface
		modules = new LinkedList<>();

		if(ID == GuiHandler.guiId.MODULAR.ordinal()) {
			//Backgrounds
			if(world.isRemote) {
				modules.add(new ModuleImage(173, 0, new IconResource(128, 0, 48, 86, CommonResources.genericBackground)));
				modules.add(new ModuleImage(173, 86, new IconResource(98, 0, 78, 83, CommonResources.genericBackground)));
				modules.add(new ModuleImage(173, 168, new IconResource(98, 168, 78, 3, CommonResources.genericBackground)));
			}

<<<<<<< HEAD
			//Fuel
			modules.add(new ModuleProgress(192, 7, 0, new ProgressBarImage(2, 173, 12, 71, 17, 6, 3, 69, 1, 1, Direction.UP, TextureResources.rocketHud), this));

=======
>>>>>>> origin/feature/nuclearthermalrockets
			//TODO DEBUG tiles!
			//Render TEs in a pan-able list y-axis only
			List<TileEntity> tiles = storage.getGUITiles();
			List<ModuleBase> panModules = new ArrayList<>();
			for(int i = 0; i < tiles.size(); i++) {
				TileEntity tile  = tiles.get(i);
				BlockState state = storage.getBlockState(tile.getPos());
				try {
<<<<<<< HEAD
					modules.add(new ModuleSlotButton(8 + 18* (i % 9), 17 + 18*(i/9), this, new ItemStack(state.getBlock(), 1), world).setAdditionalData(i + tilebuttonOffset));
=======
					panModules.add(new ModuleSlotButton(18 * (i % 9),  18*(i/9), i + tilebuttonOffset, this, new ItemStack(state.getBlock(), 1, state.getBlock().getMetaFromState(state)), world));
>>>>>>> origin/feature/nuclearthermalrockets
				} catch (NullPointerException e) {

				}
			}
			modules.add(new ModuleContainerPanYOnly(8, 17, panModules, new LinkedList<>(), null, 171, 40, 0, 0));

			//Fuel
			modules.add(new ModuleProgress(192, 7, 0, new ProgressBarImage(2, 173, 12, 71, 17, 6, 3, 69, 1, 1, EnumFacing.UP, TextureResources.rocketHud), this));


			//Add buttons
			modules.add(new ModuleButton(180, 140, LibVulpes.proxy.getLocalizedString("msg.entity.rocket.disass"), this, zmaster587.libVulpes.inventory.TextureResources.buttonBuild, 64, 20).setAdditionalData(0));

			//modules.add(new ModuleButton(180, 95, 1, "", this, TextureResources.buttonLeft, 10, 16));
			//modules.add(new ModuleButton(202, 95, 2, "", this, TextureResources.buttonRight, 10, 16));

			modules.add(new ModuleButton(180, 114, LibVulpes.proxy.getLocalizedString("msg.entity.rocket.seldst"), this,  zmaster587.libVulpes.inventory.TextureResources.buttonBuild, 64,20).setAdditionalData(1));
			//modules.add(new ModuleText(180, 114, "Inventories", 0x404040));
<<<<<<< HEAD
		}
		else {
			ItemStack slot0 = storage.getGuidanceComputer().getStackInSlot(0);
			ResourceLocation uuid;
			//Station location select
			if( slot0 != null && slot0.getItem() instanceof ItemStationChip && !Constants.INVALID_PLANET.equals((uuid = ItemStationChip.getUUID(slot0)))) {
				ISpaceObject obj = SpaceObjectManager.getSpaceManager().getSpaceStation(uuid);
=======
		} else {
			ItemStack slot0 = storage.getGuidanceComputer() != null ? storage.getGuidanceComputer().getStackInSlot(0) : ItemStack.EMPTY;
			int uuid;
			//Station location select
			if(!slot0.isEmpty() && slot0.getItem() instanceof ItemStationChip && (uuid = ItemStationChip.getUUID(slot0)) != 0) {
				ISpaceObject spaceObject = SpaceObjectManager.getSpaceManager().getSpaceStation(uuid);
>>>>>>> origin/feature/nuclearthermalrockets

				modules.add(new ModuleStellarBackground(0, 0, zmaster587.libVulpes.inventory.TextureResources.starryBG));
				//modules.add(new ModuleImage(0, 0, icon));

				if(spaceObject == null)
					return modules;

<<<<<<< HEAD
				List<ModuleBase> list2 = new LinkedList<ModuleBase>();
				ModuleButton button = new ModuleButton(0, 0, LibVulpes.proxy.getLocalizedString("msg.entity.rocket.clear"), this, TextureResources.buttonGeneric, 72, 18).setAdditionalData(STATION_LOC_OFFSET);
=======
				List<ModuleBase> list2 = new LinkedList<>();
				ModuleButton button = new ModuleButton(0, 0, STATION_LOC_OFFSET, LibVulpes.proxy.getLocalizedString("msg.entity.rocket.clear"), this, TextureResources.buttonGeneric, 72, 18);
>>>>>>> origin/feature/nuclearthermalrockets
				list2.add(button);

				int i = 1;
				for( StationLandingLocation pos : ((SpaceStationObject)spaceObject).getLandingPads())
				{
					button = new ModuleButton(0, i*18, pos.toString(), this, TextureResources.buttonGeneric, 72, 18).setAdditionalData( i + STATION_LOC_OFFSET);
					list2.add(button);

					if(pos.getOccupied())
						button.setColor(0xFF0000);

					i++;
				}

				ModuleContainerPan pan = new ModuleContainerPan(25, 25, list2, new LinkedList<>(), null, 256, 256, 0, -48, 258, 256);
				modules.add(pan);

				StationLandingLocation location = storage.getGuidanceComputer().getLandingLocation(uuid);

				landingPadDisplayText.setText(location != null ? location.toString() : LibVulpes.proxy.getLocalizedString("msg.entity.rocket.none"));
				modules.add(landingPadDisplayText);
			}
			else {
				DimensionProperties properties = DimensionManager.getEffectiveDimId(world, new BlockPos(this.getPositionVec()));
				while(properties.getParentProperties() != null) properties = properties.getParentProperties();

				if(stats.isNuclear())
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
		FuelType fuelType = getRocketFuelType();
		
		if(id == 0 && fuelType != null) {
			switch (fuelType) {
				case LIQUID_BIPROPELLANT:
					return (getFuelAmount(FuelType.LIQUID_OXIDIZER) / (float) getFuelCapacity(FuelType.LIQUID_OXIDIZER) +
							getFuelAmount(fuelType)) / (float) getFuelCapacity(fuelType);
				case LIQUID_MONOPROPELLANT:
				case NUCLEAR_WORKING_FLUID:
					return getFuelAmount(fuelType) / (float) getFuelCapacity(fuelType);
			}
		}

		return 0;
	}

	public double getRelativeHeightFraction() {
		return (posY - getTopBlock(getPosition()).getY())/(getEntryHeight(dimension) - getTopBlock(getPosition()).getY());
	}

	public double getPreviousRelativeHeightFraction() {
		return (prevPosY - getTopBlock(getPosition()).getY())/(getEntryHeight(dimension) - getTopBlock(getPosition()).getY());
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
	@OnlyIn(value=Dist.CLIENT)
	public void onInventoryButtonPressed(ModuleButton button) {
		int buttonId = (int)button.getAdditionalData();

		switch(buttonId) {
		case 0:
			PacketHandler.sendToServer(new PacketEntity(this, (byte)EntityRocket.PacketType.DECONSTRUCT.ordinal()));
			break;
		case 1:
			PacketHandler.sendToServer(new PacketEntity(this, (byte)EntityRocket.PacketType.OPENPLANETSELECTION.ordinal()));
			break;
		default:
			PacketHandler.sendToServer(new PacketEntity(this, (byte)(buttonId + BUTTON_ID_OFFSET)));
			//Minecraft.getInstance().thePlayer.closeScreen();

			if(buttonId < STATION_LOC_OFFSET) {
<<<<<<< HEAD
				//TileEntity tile = storage.getGUItiles().get(buttonId - tilebuttonOffset);
				//storage.getBlockState(tile.getPos()).getBlock().onBlockActivated( storage.getBlockState(tile.getPos()), storage.world, tile.getPos(), (PlayerEntity)Minecraft.getInstance().player, Hand.MAIN_HAND, new BlockRayTraceResult(new Vector3d(0,0,0), Direction.DOWN, tile.getPos(), false));
=======
				TileEntity tile = storage.getGUITiles().get(buttonId - tilebuttonOffset);
				storage.getBlockState(tile.getPos()).getBlock().onBlockActivated(storage.world, tile.getPos(), storage.getBlockState(tile.getPos()), Minecraft.getMinecraft().player, EnumHand.MAIN_HAND, EnumFacing.DOWN, 0, 0, 0);
>>>>>>> origin/feature/nuclearthermalrockets
			}
		}
	}

	@Override
	public boolean canInteractWithContainer(PlayerEntity entity) {
		boolean ret = this.isAlive() && this.getDistanceSq(entity) < 64*64;
		if(!ret)
			RocketInventoryHelper.removePlayerFromInventoryBypass(entity);

		RocketInventoryHelper.updateTime(entity, world.getGameTime());

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
		return !ARConfiguration.getCurrentConfig().planetsMustBeDiscovered.get() || DimensionManager.getInstance().knownPlanets.contains(properties.getId());
	}

	@Override
	public boolean isStarKnown(StellarBody body) {
		return true;
	}

	@Override
	public IPacket<?> createSpawnPacket() {
		return NetworkHooks.getEntitySpawningPacket(this);
	}

	@Override
	public void readSpawnNBT(CompoundNBT nbt)
	{
		readNetworkableNBT(nbt);
	}
	
	@Override
	public Container createMenu(int winId, PlayerInventory inv, PlayerEntity player) {
		return new ContainerModular(LibvulpesGuiRegistry.CONTAINER_MODULAR_ENTITY, winId, player, getModules(getModularInvType().ordinal(), player), this,getModularInvType());
	}

	@Override
	public GuiHandler.guiId getModularInvType() {
		return guiId.MODULAR;
	}


	@Override
	public void writeSpawnData(PacketBuffer buffer) {
		CompoundNBT nbt = new CompoundNBT();
		writeAdditional(nbt);
		new PacketSpawnEntity(this, nbt).write(buffer);	
	}

	@Override
	public void readSpawnData(PacketBuffer additionalData) {
		PacketSpawnEntity packet = new PacketSpawnEntity();
		packet.read(additionalData);
		packet.execute(this);
	}
}
