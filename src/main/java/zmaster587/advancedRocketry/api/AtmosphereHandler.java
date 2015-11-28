package zmaster587.advancedRocketry.api;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.world.ChunkPosition;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.event.world.ExplosionEvent;
import zmaster587.advancedRocketry.api.util.AreaBlob;
import zmaster587.advancedRocketry.api.util.AtmosphereBlob;
import zmaster587.advancedRocketry.api.util.IBlobHandler;
import zmaster587.advancedRocketry.util.Configuration;
import zmaster587.advancedRocketry.world.DimensionManager;
import zmaster587.libVulpes.util.BlockPosition;

public class AtmosphereHandler {
	private static final int MAX_BLOB_RADIUS = 64;
	private static HashMap<Integer, AtmosphereHandler> dimensionOxygen = new HashMap<Integer, AtmosphereHandler>();

	private HashMap<IBlobHandler,AreaBlob> blobs;
	int dimId;

	/**
	 * Registers the Atmosphere handler for the dimension given
	 * @param dimId the dimension id to register the dimension for
	 */
	public static void registerWorld(int dimId) {
		dimensionOxygen.put(dimId, new AtmosphereHandler(dimId));
		if(Configuration.enableOxygen) {
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
	public void onBlockPlace(BlockEvent.PlaceEvent event) {
		BlockPosition pos = new BlockPosition(event.x, event.y, event.z);

		for(AreaBlob blob : blobs.values()) {
			if(blob.contains(pos)) {
				blob.removeBlock(event.x, event.y, event.z);
			}
			else if(!blob.contains(blob.getRootPosition())) {
				blob.addBlock(blob.getRootPosition());
			}
		}
	}

	@SubscribeEvent
	public void onExplosion(ExplosionEvent.Detonate event) {
		for(Object obj : event.explosion.affectedBlockPositions) {
			ChunkPosition pos = (ChunkPosition)obj;
			onBlockRemove(new BlockPosition(pos.chunkPosX, pos.chunkPosY, pos.chunkPosZ));
		}
	}

	@SubscribeEvent
	public void onTick(TickEvent.PlayerTickEvent event) {
		if(event.side.isServer() && event.player.dimension == this.dimId) {
			AtmosphereType atmosType = getAtmosphereType(event.player);

			if(atmosType.canTick())
				atmosType.onTick((EntityLivingBase)event.player);
		}
	}

	private void onBlockRemove(BlockPosition pos) {
		for(AreaBlob blob : getBlobWithinRadius(pos, MAX_BLOB_RADIUS)) {
			//Make sure that a block can actually be attached to the blob
			for(ForgeDirection dir : ForgeDirection.VALID_DIRECTIONS)
				if(blob.contains(pos.getPositionAtOffset(dir.offsetX, dir.offsetY, dir.offsetZ))) {
					blob.addBlock(pos);
					break;
				}
		}
	}

	@SubscribeEvent
	public void onBlockRemove(BlockEvent.BreakEvent event) {
		BlockPosition pos = new BlockPosition(event.x, event.y, event.z);
		onBlockRemove(pos);
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
			blob.setData(AtmosphereType.AIR);
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
		blobs.get(handler).clearBlob();
	}
	
	/**
	 * Adds a block to the blob
	 * @param handler
	 * @param x
	 * @param y
	 * @param z
	 */
	public void addBlock(IBlobHandler handler, int x, int y, int z){
		addBlock(handler, new BlockPosition(x, y, z));
	}
	
	/**
	 * Adds a block to the blob
	 * @param handler
	 * @param x
	 * @param y
	 * @param z
	 */
	public void addBlock(IBlobHandler handler, BlockPosition pos){
		AreaBlob blob = blobs.get(handler);
		blob.addBlock(pos);
	}

	/**
	 * @param x
	 * @param y
	 * @param z
	 * @return AtmosphereType at this location
	 */
	public AtmosphereType getAtmosphereType(int x, int y, int z) {
		if(Configuration.enableOxygen) {
			BlockPosition pos = new BlockPosition(x,y,z);
			for(AreaBlob blob : blobs.values()) {
				if(blob.contains(pos)) {
					return (AtmosphereType)blob.getData();
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
	public AtmosphereType getAtmosphereType(Entity entity) {
		if(Configuration.enableOxygen) {
			BlockPosition pos = new BlockPosition((int)(entity.posX - 1), (int)Math.ceil(entity.posY), (int)(entity.posZ - 1));
			for(AreaBlob blob : blobs.values()) {
				if(blob.contains(pos)) {
					return (AtmosphereType)blob.getData();
				}
			}

			return DimensionManager.getInstance().getDimensionProperties(dimId).getAtmosphere();
		}
		return AtmosphereType.AIR;
	}

	/**
	 * @param entity entity to check against
	 * @return true if the entity can breathe in the this atmosphere
	 */
	public boolean canEntityBreathe(EntityLiving entity) {
		if(Configuration.enableOxygen) {
			BlockPosition pos = new BlockPosition((int)Math.ceil(entity.posX), (int)Math.ceil(entity.posY), (int)Math.ceil(entity.posZ));
			for(AreaBlob blob : blobs.values()) {
				if(blob.contains(pos) && ((AtmosphereType)blob.getData()).isBreathable()) {
					return true;
				}
			}
			return DimensionManager.getInstance().getDimensionProperties(dimId).getAtmosphere().isBreathable();
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
	public void setAtmosphereType(IBlobHandler handler, AtmosphereType data) {
		blobs.get(handler).setData(data);
	}
	
	/**
	 * Gets the atmosphere type of this blob
	 * @param handler the handler for the blob
	 */
	public AtmosphereType getAtmosphereType(IBlobHandler handler) {
		return (AtmosphereType)blobs.get(handler).getData();
	}
}
