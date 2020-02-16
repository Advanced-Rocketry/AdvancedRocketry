package zmaster587.advancedRocketry.atmosphere;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent.PlayerChangedDimensionEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent.PlayerLoggedOutEvent;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.event.entity.living.LivingEvent.LivingUpdateEvent;
import zmaster587.advancedRocketry.api.AreaBlob;
import zmaster587.advancedRocketry.api.Configuration;
import zmaster587.advancedRocketry.api.IAtmosphere;
import zmaster587.advancedRocketry.api.event.AtmosphereEvent;
import zmaster587.advancedRocketry.api.util.IBlobHandler;
import zmaster587.advancedRocketry.dimension.DimensionManager;
import zmaster587.advancedRocketry.network.PacketAtmSync;
import zmaster587.advancedRocketry.util.AtmosphereBlob;
import zmaster587.advancedRocketry.util.SealableBlockHandler;
import zmaster587.libVulpes.network.PacketHandler;
import zmaster587.libVulpes.util.BlockPosition;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

public class AtmosphereHandler {
	public static final DamageSource vacuumDamage = new DamageSource("Vacuum").setDamageBypassesArmor().setDamageIsAbsolute();
	public static final DamageSource oxygenToxicityDamage = new DamageSource("OxygenToxicity").setDamageBypassesArmor().setDamageIsAbsolute();

	public static long lastSuffocationTime = Integer.MIN_VALUE;
	private static final int MAX_BLOB_RADIUS = ((Configuration.atmosphereHandleBitMask & 1) == 1) ? 256 : Configuration.oxygenVentSize;
	private static HashMap<Integer, AtmosphereHandler> dimensionOxygen = new HashMap<Integer, AtmosphereHandler>();
	private static HashMap<EntityPlayer, IAtmosphere> prevAtmosphere = new HashMap<EntityPlayer, IAtmosphere>();

	private HashMap<IBlobHandler,AreaBlob> blobs;
	int dimId;

	//Stores current Atm on the CLIENT
	public static IAtmosphere currentAtm;
	public static int currentPressure;

	/**
	 * Registers the Atmosphere handler for the dimension given
	 * @param dimId the dimension id to register the dimension for
	 */
	public static void registerWorld(int dimId) {

		//If O2 is allowed and
		if(Configuration.enableOxygen && (Configuration.overrideGCAir || dimId != Configuration.MoonId || DimensionManager.getInstance().getDimensionProperties(dimId).isNativeDimension)) {
			dimensionOxygen.put(dimId, new AtmosphereHandler(dimId));
			MinecraftForge.EVENT_BUS.register(dimensionOxygen.get(dimId));
			FMLCommonHandler.instance().bus().register(dimensionOxygen.get(dimId));

		}
	}

	/**
	 * Unregisters the Atmosphere handler for the dimension given
	 * @param dimId the dimension id to register the dimension for
	 */
	public static void unregisterWorld(int dimId) {
		AtmosphereHandler handler = dimensionOxygen.remove(dimId);
		if(Configuration.enableOxygen && handler != null) {

			MinecraftForge.EVENT_BUS.unregister(handler);
			FMLCommonHandler.instance().bus().unregister(handler);
		}
	}

	private AtmosphereHandler(int dimId) {
		this.dimId = dimId;
		blobs = new HashMap<IBlobHandler,AreaBlob>();
	}

	@SubscribeEvent
	public void onTick(LivingUpdateEvent event) {

		if(!event.entity.worldObj.isRemote && event.entity.worldObj.provider.dimensionId == this.dimId) {
			IAtmosphere atmosType = getAtmosphereType(event.entity);

			if(event.entity instanceof EntityPlayer && atmosType != prevAtmosphere.get(event.entity)) {
				PacketHandler.sendToPlayer(new PacketAtmSync(atmosType.getUnlocalizedName(),  getAtmospherePressure(event.entity)), (EntityPlayer)event.entity);
				prevAtmosphere.put((EntityPlayer)event.entity, atmosType);
			}

			if(atmosType.canTick() &&
					!(event.entityLiving.isInWater()) ) {
				AtmosphereEvent event2 = new AtmosphereEvent.AtmosphereTickEvent(event.entity, atmosType);
				MinecraftForge.EVENT_BUS.post(event2);
				if(!event2.isCanceled() && !Configuration.bypassEntity.contains(event.entity.getClass())) 
					atmosType.onTick((EntityLivingBase)event.entityLiving);
			}
		}
	}

	@SubscribeEvent
	public void onPlayerChangeDim(PlayerChangedDimensionEvent event) {
		prevAtmosphere.remove(event.player);

	}

	@SubscribeEvent
	public void onPlayerLogoutEvent(PlayerLoggedOutEvent event) {
		prevAtmosphere.remove(event.player);
	}

	private void onBlockRemove(BlockPosition pos) {
		List<AreaBlob> blobs = getBlobWithinRadius(pos, MAX_BLOB_RADIUS);
		for(AreaBlob blob : blobs) {
			//Make sure that a block can actually be attached to the blob
			for(ForgeDirection dir : ForgeDirection.VALID_DIRECTIONS)
				if(blob.contains(pos.getPositionAtOffset(dir.offsetX, dir.offsetY, dir.offsetZ))) {
					blob.addBlock(pos, blobs);
					break;
				}
		}
	}

	/**
	 * @return true if the dimension has an atmospherehandler Object associated with it
	 */
	public static boolean hasAtmosphereHandler(int dimId) {
		return dimensionOxygen.containsKey(dimId);
	}

	//Called from World.setBlockMetaDataWithNotify
	public static void onBlockMetaChange(World world, int x , int y, int z) {
		if(Configuration.enableOxygen && !world.isRemote && world.getChunkFromBlockCoords(x, z).isChunkLoaded) {
			AtmosphereHandler handler = getOxygenHandler(world.provider.dimensionId);
			BlockPosition pos = new BlockPosition(x, y, z);
			int meta = world.getBlockMetadata(x, y, z);


			if(handler == null)
				return; //WTF

			List<AreaBlob> nearbyBlobs = handler.getBlobWithinRadius(pos, MAX_BLOB_RADIUS);
			for(AreaBlob blob : nearbyBlobs) {
				if(blob.getBlobMaxRadius() > pos.getDistance(blob.getRootPosition())) {
					if(blob.contains(pos) && !blob.isPositionAllowed(world, pos, nearbyBlobs))
						blob.removeBlock(x, y, z);
					else if(!blob.contains(pos) && blob.isPositionAllowed(world, pos, nearbyBlobs))
						handler.onBlockRemove(pos);
					else if(!blob.contains(pos) && !blob.isPositionAllowed(world, pos ,nearbyBlobs) && blob.getBlobSize() == 0) {
						blob.addBlock(blob.getRootPosition(), nearbyBlobs);
					}
				}
			}
		}
	}

	//Called from setBlock in World.class
	public static void onBlockChange(World world, int x, int y, int z) {

		if(Configuration.enableOxygen && !world.isRemote && world.getChunkFromBlockCoords(x, z).isChunkLoaded) {
			BlockPosition pos = new BlockPosition(x, y, z);

			AtmosphereHandler handler = getOxygenHandler(world.provider.dimensionId);

			if(handler == null)
				return; //WTF

			List<AreaBlob> nearbyBlobs = handler.getBlobWithinRadius(pos, MAX_BLOB_RADIUS);
			for(AreaBlob blob : nearbyBlobs) {

				if(world.isAirBlock(x, y, z))
					handler.onBlockRemove(pos);
				else {
					//Place block
					if( blob.contains(pos) && SealableBlockHandler.isFulBlock(world, pos)) {
						blob.removeBlock(x, y, z);
					}
					else if(!blob.contains(blob.getRootPosition())) {
						blob.addBlock(blob.getRootPosition(), nearbyBlobs);
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
	protected List<AreaBlob> getBlobWithinRadius(BlockPosition pos, int radius) {
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
	public static AtmosphereHandler getOxygenHandler(int dimNumber) {
		//Get your oxyclean!
		return dimensionOxygen.get(dimNumber);
	}

	/**
	 * Registers a Blob with the atmosphere handler.  
	 * Must be called before use
	 * @param handler IBlobHander to register with
	 * @param x
	 * @param y
	 * @param z
	 */
	public void registerBlob(IBlobHandler handler, int x, int y , int z) {
		AreaBlob blob = blobs.get(handler);
		if(blob == null) {
			blob = new AtmosphereBlob(handler);
			blobs.put(handler, blob);
			blob.setData(AtmosphereType.PRESSURIZEDAIR);
		}
	}

	/**
	 * Registers a Blob with the atmosphere handler.  
	 * Must be called before use
	 * @param handler IBlobHander to register with
	 * @param x
	 * @param y
	 * @param z
	 */
	public void registerBlob(IBlobHandler handler, int x, int y, int z, AreaBlob blob2) {
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
	public boolean addBlock(IBlobHandler handler, int x, int y, int z){
		return addBlock(handler, new BlockPosition(x, y, z));
	}

	/**
	 * Adds a block to the blob
	 * @param handler
	 */
	public boolean addBlock(IBlobHandler handler, BlockPosition pos){
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
	public IAtmosphere getAtmosphereType(int x, int y, int z) {
		if(Configuration.enableOxygen) {
			BlockPosition pos = new BlockPosition(x,y,z);
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
	 * Gets the atmosphere type at the location of this entity
	 * @param entity the entity to check against
	 * @return The atmosphere type this entity is inside of
	 */
	public IAtmosphere getAtmosphereType(Entity entity) {
		if(Configuration.enableOxygen) {
			BlockPosition pos = new BlockPosition((int)Math.floor(entity.posX), (int)Math.ceil(entity.posY), (int)Math.floor(entity.posZ ));
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
	 * @return the default atmosphere type used by this planet
	 */
	public IAtmosphere getDefaultAtmosphereType() {
		return DimensionManager.getInstance().getDimensionProperties(dimId).getAtmosphere();
	}

	/**
	 * Gets the pressure at the location of this entity
	 * @param entity the entity to check against
	 * @return The atmosphere pressure this entity is inside of, or -1 to use default
	 */
	public int getAtmospherePressure(Entity entity) {
		if(Configuration.enableOxygen) {
			BlockPosition pos = new BlockPosition((int)(entity.posX), (int)Math.ceil(entity.posY), (int)(entity.posZ));
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
	public boolean canEntityBreathe(EntityLiving entity) {
		if(Configuration.enableOxygen) {
			BlockPosition pos = new BlockPosition((int)Math.floor(entity.posX), (int)Math.ceil(entity.posY), (int)Math.floor(entity.posZ));
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
