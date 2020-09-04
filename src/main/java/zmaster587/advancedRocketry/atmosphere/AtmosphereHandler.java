package zmaster587.advancedRocketry.atmosphere;

import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingEvent.LivingUpdateEvent;
import net.minecraftforge.event.entity.player.PlayerEvent.PlayerChangedDimensionEvent;
import net.minecraftforge.event.entity.player.PlayerEvent.PlayerLoggedOutEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import zmaster587.advancedRocketry.api.AreaBlob;
import zmaster587.advancedRocketry.api.ARConfiguration;
import zmaster587.advancedRocketry.api.IAtmosphere;
import zmaster587.advancedRocketry.api.event.AtmosphereEvent;
import zmaster587.advancedRocketry.api.util.IBlobHandler;
import zmaster587.advancedRocketry.dimension.DimensionManager;
import zmaster587.advancedRocketry.dimension.DimensionProperties;
import zmaster587.advancedRocketry.network.PacketAtmSync;
import zmaster587.advancedRocketry.util.AtmosphereBlob;
import zmaster587.libVulpes.network.PacketHandler;
import zmaster587.libVulpes.util.HashedBlockPosition;
import zmaster587.libVulpes.util.ZUtils;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

public class AtmosphereHandler {
	public static final DamageSource vacuumDamage = new DamageSource("Vacuum").setDamageBypassesArmor().setDamageIsAbsolute();
	public static final DamageSource lowOxygenDamage = new DamageSource("LowOxygen").setDamageBypassesArmor().setDamageIsAbsolute();
	public static final DamageSource heatDamage = new DamageSource("Heat").setDamageBypassesArmor().setDamageIsAbsolute();
	public static final DamageSource oxygenToxicityDamage = new DamageSource("OxygenToxicity").setDamageBypassesArmor().setDamageIsAbsolute();

	public static long lastSuffocationTime = Integer.MIN_VALUE;
	private static final int MAX_BLOB_RADIUS = ((ARConfiguration.getCurrentConfig().atmosphereHandleBitMask.get() & 1) == 1) ? 256 : ARConfiguration.getCurrentConfig().oxygenVentSize.get();
	private static HashMap<ResourceLocation, AtmosphereHandler> dimensionOxygen = new HashMap<ResourceLocation, AtmosphereHandler>();
	private static HashMap<PlayerEntity, IAtmosphere> prevAtmosphere = new HashMap<PlayerEntity, IAtmosphere>();

	private HashMap<IBlobHandler,AreaBlob> blobs;
	ResourceLocation dimId;

	//Stores current Atm on the CLIENT
	public static IAtmosphere currentAtm;
	public static int currentPressure;

	/**
	 * Registers the Atmosphere handler for the dimension given
	 * @param dimId the dimension id to register the dimension for
	 */
	public static void registerWorld(World world) {
		ResourceLocation dimId = ZUtils.getDimensionIdentifier(world);
		//If O2 is allowed and
		DimensionProperties dimProp = DimensionManager.getInstance().getDimensionProperties(dimId);
		if(ARConfiguration.getCurrentConfig().enableOxygen.get() && dimProp.hasSurface() && (ARConfiguration.getCurrentConfig().overrideGCAir.get() || dimId != ARConfiguration.getCurrentConfig().MoonId || dimProp.isNativeDimension)) {
			dimensionOxygen.put(dimId, new AtmosphereHandler(dimId));
			MinecraftForge.EVENT_BUS.register(dimensionOxygen.get(dimId));
		}
	}

	/**
	 * Unregisters the Atmosphere handler for the dimension given
	 * @param dimId the dimension id to register the dimension for
	 */
	public static void unregisterWorld(World dimId) {
		AtmosphereHandler handler = dimensionOxygen.remove(dimId);
		if(ARConfiguration.getCurrentConfig().enableOxygen.get() && handler != null) {

			MinecraftForge.EVENT_BUS.unregister(handler);
			FMLJavaModLoadingContext.get().getModEventBus().unregister(handler);
		}
	}

	private AtmosphereHandler(ResourceLocation dimId) {
		this.dimId = dimId;
		blobs = new HashMap<IBlobHandler,AreaBlob>();
	}

	@SubscribeEvent
	public void onTick(LivingUpdateEvent event) {
		Entity entity = event.getEntity();
		if(!entity.world.isRemote && ZUtils.getDimensionIdentifier(entity.world) == this.dimId) {
			IAtmosphere atmosType = getAtmosphereType(entity);

			if(entity instanceof PlayerEntity && atmosType != prevAtmosphere.get(entity)) {
				PacketHandler.sendToPlayer(new PacketAtmSync(atmosType.getUnlocalizedName(), getAtmospherePressure(entity)), (PlayerEntity)entity);
				prevAtmosphere.put((PlayerEntity)entity, atmosType);
			}

			if(atmosType.canTick() &&
					!(event.getEntityLiving().isInLava() || event.getEntityLiving().isInWater()) ) {
				AtmosphereEvent event2 = new AtmosphereEvent.AtmosphereTickEvent(entity, atmosType);
				MinecraftForge.EVENT_BUS.post(event2);
				if(!event2.isCanceled() && !atmosType.isImmune(event.getEntity().getClass()))
					atmosType.onTick((LivingEntity)event.getEntityLiving());
			}
		}
	}

	@SubscribeEvent
	public void onPlayerChangeDim(PlayerChangedDimensionEvent event) {
		prevAtmosphere.remove(event.getPlayer());

	}

	@SubscribeEvent
	public void onPlayerLogoutEvent(PlayerLoggedOutEvent event) {
		prevAtmosphere.remove(event.getPlayer());
	}

	private void onBlockRemove(HashedBlockPosition pos) {
		List<AreaBlob> blobs = getBlobWithinRadius(pos, MAX_BLOB_RADIUS);
		for(AreaBlob blob : blobs) {
			//Make sure that a block can actually be attached to the blob
			for(Direction dir : Direction.values())
				if(blob.contains(pos.getPositionAtOffset(dir))) {
					blob.addBlock(pos, blobs);
					break;
				}
		}
	}

	/**
	 * @return true if the dimension has an atmospherehandler Object associated with it
	 */
	public static boolean hasAtmosphereHandler(ResourceLocation dimId) {
		return dimensionOxygen.containsKey(dimId);
	}
	
	public static boolean hasAtmosphereHandler(World dimId) {
		return dimensionOxygen.containsKey(ZUtils.getDimensionIdentifier(dimId));
	}

	//Called from World.setBlockMetaDataWithNotify
	/*public static void onBlockMetaChange(World world, int x , int y, int z) {
		if(Configuration.enableOxygen && !world.isRemote && world.getChunkFromBlockCoords(new BlockPos(x, y, z)).isLoaded()) {
			AtmosphereHandler handler = getOxygenHandler(world);
			HashedBlockPosition pos = new HashedBlockPosition(x, y, z);


			if(handler == null)
				return; //WTF

			for(AreaBlob blob : handler.getBlobWithinRadius(pos, MAX_BLOB_RADIUS)) {

				if(blob.contains(pos) && !blob.isPositionAllowed(world, pos))
					blob.removeBlock(x, y, z);
				else if(!blob.contains(pos) && blob.isPositionAllowed(world, pos))
					handler.onBlockRemove(pos);
				else if(!blob.contains(pos) && !blob.isPositionAllowed(world, pos) && blob.getBlobSize() == 0) {
					blob.addBlock(blob.getRootPosition());
				}
			}
		}
	}*/

	//Called from setBlock in World.class
	public static void onBlockChange(World world, BlockPos bpos) {

		if(ARConfiguration.getCurrentConfig().enableOxygen.get() && !world.isRemote && world.isBlockLoaded(new BlockPos(bpos))) {
			HashedBlockPosition pos = new HashedBlockPosition(bpos);

			AtmosphereHandler handler = getOxygenHandler(ZUtils.getDimensionIdentifier(world));

			//Bonus chests cause world gen to begin before loading the world
			//Because atmosphere handlers are created at world load time
			//there is a possibility handler can be null here
			if(handler == null)
				return; //WTF

			List<AreaBlob> nearbyBlobs = handler.getBlobWithinRadius(pos, MAX_BLOB_RADIUS);
			for(AreaBlob blob : nearbyBlobs) {

				if(blob.getBlobMaxRadius() > pos.getDistance(blob.getRootPosition())) {
					if(world.isAirBlock(bpos))
						handler.onBlockRemove(pos);
					else {
						//Place block
						if( blob.contains(pos) && !blob.isPositionAllowed(world, pos, nearbyBlobs)) {
							blob.removeBlock(pos);
						}
						else if(!blob.contains(blob.getRootPosition())) {
							blob.addBlock(blob.getRootPosition(), nearbyBlobs);
						}
						else if(!blob.contains(pos) && blob.isPositionAllowed(world, pos, nearbyBlobs))//isFulBlock(world, pos.getBlockPos()))
							blob.addBlock(pos, nearbyBlobs);
					}
				}
			}
		}
	}

	/**
	 * Gets a list of AreaBlobs within a radius
	 * @param pos position
	 * @param radius distance from the position to find blobs within
	 * @return List of AreaBlobs within the radius from the position
	 */
	protected List<AreaBlob> getBlobWithinRadius(HashedBlockPosition pos, int radius) {
		LinkedList<AreaBlob> list = new LinkedList<AreaBlob>();
		for(AreaBlob blob : blobs.values()) {
			if(blob.getRootPosition().getDistance(pos) - radius <= 0) {
				list.add(blob);
			}
		}
		return list;
	}

	/**
	 * @param dimNumber dimension number for which to get the oxygenhandler
	 * @return the oxygen handler for the planet or null if none exists
	 */
	public static AtmosphereHandler getOxygenHandler(ResourceLocation dimNumber) {
		//Get your oxyclean!
		return dimensionOxygen.get(dimNumber);
	}
	
	/**
	 * @param dimNumber dimension number for which to get the oxygenhandler
	 * @return the oxygen handler for the planet or null if none exists
	 */
	public static AtmosphereHandler getOxygenHandler(World world) {
		//Get your oxyclean!
		return getOxygenHandler(ZUtils.getDimensionIdentifier(world));
	}

	/**
	 * Registers a Blob with the atmosphere handler.  
	 * Must be called before use
	 * @param handler IBlobHander to register with
	 * @param x
	 * @param y
	 * @param z
	 */
	public void registerBlob(IBlobHandler handler, BlockPos pos) {
		AreaBlob blob = blobs.get(handler);
		if(blob == null) {
			blob = new AtmosphereBlob(handler);
			blobs.put(handler, blob);
			blob.setData(AtmosphereType.PRESSURIZEDAIR);
		}
	}

	/**
	 * Registers a Blob with provided blob type
	 * Must be called before use
	 * @param handler IBlobHander to register with
	 * @param x
	 * @param y
	 * @param z
	 */
	public void registerBlob(IBlobHandler handler, BlockPos pos, AreaBlob blob2) {
		AreaBlob blob = blobs.get(handler);
		if(blob == null) {
			blob = blob2;
			blobs.put(handler, blob);
			blob.setData(AtmosphereType.PRESSURIZEDAIR);
		}
	}

	/**
	 * Unregisters a blob from the atmosphere handler
	 * @param handler IBlobHandlerObject the blob is associated with
	 */
	public void unregisterBlob(IBlobHandler handler) {
		blobs.remove(handler);
	}

	/**
	 * Removes all blocks from the blob associated with this handler
	 * @param handler the handler associated with this blob
	 */
	public void clearBlob(IBlobHandler handler) {

		if(blobs.containsKey(handler)) {
			blobs.get(handler).clearBlob();
		}
	}

	/**
	 * Adds a block to the blob
	 * @param handler
	 * @param x
	 * @param y
	 * @param z
	 */
	public void addBlock(IBlobHandler handler, int x, int y, int z){
		addBlock(handler, new HashedBlockPosition(x, y, z));
	}

	/**
	 * Adds a block to the blob
	 * @param handler
	 * @return true if blob addition is successful
	 */
	public boolean addBlock(IBlobHandler handler, HashedBlockPosition pos){
		AreaBlob blob = blobs.get(handler);
		blob.addBlock(pos, getBlobWithinRadius(pos, MAX_BLOB_RADIUS));
		return !blob.getLocations().isEmpty();
	}

	/**
	 * @param x
	 * @param y
	 * @param z
	 * @return AtmosphereType at this location
	 */
	public IAtmosphere getAtmosphereType(BlockPos pos2) {
		if(ARConfiguration.getCurrentConfig().enableOxygen.get()) {
			HashedBlockPosition pos = new HashedBlockPosition(pos2);
			for(AreaBlob blob : blobs.values()) {
				if(blob.contains(pos)) {
					return (IAtmosphere)blob.getData();
				}
			}


			return getDefaultAtmosphereType();
		}

		return AtmosphereType.AIR;
	}

	/**
	 * @return the default atmosphere type used by this planet
	 */
	public IAtmosphere getDefaultAtmosphereType() {
		return DimensionManager.getInstance().getDimensionProperties(dimId).getAtmosphere();
	}

	/**
	 * Gets the atmosphere type at the location of this entity
	 * @param entity the entity to check against
	 * @return The atmosphere type this entity is inside of
	 */
	public IAtmosphere getAtmosphereType(Entity entity) {
		if(ARConfiguration.getCurrentConfig().enableOxygen.get()) {
			HashedBlockPosition pos = new HashedBlockPosition((int)Math.floor(entity.getPosX()), (int)Math.ceil(entity.getPosY()), (int)Math.floor(entity.getPosZ()));
			for(AreaBlob blob : blobs.values()) {
				if(blob.contains(pos)) {
					return (IAtmosphere)blob.getData();
				}
			}

			return DimensionManager.getInstance().getDimensionProperties(dimId).getAtmosphere();
		}
		return AtmosphereType.AIR;
	}

	/**
	 * Gets the pressure at the location of this entity
	 * @param entity the entity to check against
	 * @return The atmosphere pressure this entity is inside of, or -1 to use default
	 */
	public int getAtmospherePressure(Entity entity) {
		if(ARConfiguration.getCurrentConfig().enableOxygen.get()) {
			HashedBlockPosition pos = new HashedBlockPosition((int)Math.floor(entity.getPosX()), (int)Math.ceil(entity.getPosY()), (int)Math.floor(entity.getPosZ()));
			for(AreaBlob blob : blobs.values()) {
				if(blob.contains(pos) && blob instanceof AtmosphereBlob) {
					return ((AtmosphereBlob)blob).getPressure();
				}
			}
		}
		return -1;
	}

	/**
	 * @param entity entity to check against
	 * @return true if the entity can breathe in the this atmosphere
	 */
	public boolean canEntityBreathe(LivingEntity entity) {
		if(ARConfiguration.getCurrentConfig().enableOxygen.get()) {
			HashedBlockPosition pos = new HashedBlockPosition((int)Math.floor(entity.getPosX()), (int)Math.ceil(entity.getPosY()), (int)Math.floor(entity.getPosZ()));
			for(AreaBlob blob : blobs.values()) {
				if(blob.contains(pos) && ((IAtmosphere)blob.getData()).isImmune(entity)) {
					return true;
				}
			}
			return DimensionManager.getInstance().getDimensionProperties(dimId).getAtmosphere().isImmune(entity);
		}

		return true;
	}

	/**
	 * @param handler the handler registered to this blob
	 * @return The current size of the blob
	 */
	public int getBlobSize(IBlobHandler handler) {
		return blobs.get(handler).getBlobSize();
	}

	/**
	 * Changes the atmosphere type of this blob
	 * @param handler the handler for the blob
	 * @param data the AtmosphereType to set this blob to.
	 */
	public void setAtmosphereType(IBlobHandler handler, IAtmosphere data) {
		blobs.get(handler).setData(data);
	}

	/**
	 * Gets the atmosphere type of this blob
	 * @param handler the handler for the blob
	 */
	public IAtmosphere getAtmosphereType(IBlobHandler handler) {
		return (IAtmosphere)blobs.get(handler).getData();
	}
}
