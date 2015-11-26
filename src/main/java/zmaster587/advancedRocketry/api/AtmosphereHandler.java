package zmaster587.advancedRocketry.api;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Stack;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.world.ChunkPosition;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.event.world.ExplosionEvent;
import zmaster587.advancedRocketry.api.util.AreaBlob;
import zmaster587.advancedRocketry.api.util.AtmosphereBlob;
import zmaster587.advancedRocketry.api.util.IBlobHandler;
import zmaster587.advancedRocketry.event.BlockBreakEvent;
import zmaster587.advancedRocketry.world.DimensionManager;
import zmaster587.libVulpes.util.BlockPosition;

public class AtmosphereHandler {
	private static final int MAX_BLOB_RADIUS = 64;
	private static HashMap<Integer, AtmosphereHandler> dimensionOxygen = new HashMap<Integer, AtmosphereHandler>();

	private HashMap<IBlobHandler,AreaBlob> blobs;
	int dimId;

	public static void registerWorld(int dimId) {
		dimensionOxygen.put(dimId, new AtmosphereHandler(dimId));
		MinecraftForge.EVENT_BUS.register(dimensionOxygen.get(dimId));
	}

	public static void unregisterWorld(int dimId) {
		AtmosphereHandler handler = dimensionOxygen.remove(dimId);
		if(handler != null)
			MinecraftForge.EVENT_BUS.unregister(handler);
	}

	public AtmosphereHandler(int dimId) {
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

	public AreaBlob getBlobFromHandler(IBlobHandler handler) {
		return blobs.get(handler);
	}

	public List<AreaBlob> getBlobWithinRadius(BlockPosition pos, int radius) {
		LinkedList<AreaBlob> list = new LinkedList<AreaBlob>();
		for(AreaBlob blob : blobs.values()) {
			if(blob.getRootPosition().getDistance(pos) - radius <= 0) {
				list.add(blob);
			}
		}
		return list;
	}

	private AreaBlob getBlobAtPosition(BlockPosition pos) {

		for(AreaBlob blob : blobs.values()) {
			if(blob.contains(pos)) {
				return blob;
			}
		}
		return null;
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
	 * Adds a block to the blob
	 * @param handler
	 * @param x
	 * @param y
	 * @param z
	 */
	public void addBlock(IBlobHandler handler, int x, int y, int z){
		AreaBlob blob = blobs.get(handler);
		if(blob == null) {
			blob = new AreaBlob(handler);
			blobs.put(handler, blob);
		}
		blob.addBlock(x, y, z);
	}

	/**
	 * @param x
	 * @param y
	 * @param z
	 * @return AtmosphereType at this location
	 */
	public AtmosphereType getAtmosphereType(int x, int y, int z) {
		BlockPosition pos = new BlockPosition(x,y,z);
		for(AreaBlob blob : blobs.values()) {
			if(blob.contains(pos)) {
				return (AtmosphereType)blob.getData();
			}
		}
		return DimensionManager.getInstance().getDimensionProperties(dimId).getAtmosphere();
	}

	public AtmosphereType getAtmosphereType(Entity entity) {
		BlockPosition pos = new BlockPosition((int)(entity.posX - 1), (int)Math.ceil(entity.posY), (int)(entity.posZ - 1));

		for(AreaBlob blob : blobs.values()) {
			if(blob.contains(pos)) {
				return (AtmosphereType)blob.getData();
			}
		}
		return DimensionManager.getInstance().getDimensionProperties(dimId).getAtmosphere();
	}

	/**
	 * @param entity entity to check against
	 * @return true if the entity can breathe in the this atmosphere
	 */
	public boolean canEntityBreathe(EntityLiving entity) {
		BlockPosition pos = new BlockPosition((int)Math.ceil(entity.posX), (int)Math.ceil(entity.posY), (int)Math.ceil(entity.posZ));
		for(AreaBlob blob : blobs.values()) {
			if(blob.contains(pos) && ((AtmosphereType)blob.getData()).isBreathable()) {
				return true;
			}
		}
		return DimensionManager.getInstance().getDimensionProperties(dimId).getAtmosphere().isBreathable();
	}
}
