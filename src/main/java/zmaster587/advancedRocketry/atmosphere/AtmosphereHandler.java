package zmaster587.advancedRocketry.atmosphere;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingEvent.LivingUpdateEvent;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerChangedDimensionEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerLoggedOutEvent;
import zmaster587.advancedRocketry.api.AreaBlob;
import zmaster587.advancedRocketry.api.Configuration;
import zmaster587.advancedRocketry.api.IAtmosphere;
import zmaster587.advancedRocketry.api.util.IBlobHandler;
import zmaster587.advancedRocketry.dimension.DimensionManager;
import zmaster587.advancedRocketry.network.PacketAtmSync;
import zmaster587.advancedRocketry.util.AtmosphereBlob;
import zmaster587.advancedRocketry.util.SealableBlockHandler;
import zmaster587.libVulpes.network.PacketHandler;
import zmaster587.libVulpes.util.HashedBlockPosition;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

public class AtmosphereHandler {
	public static final DamageSource vacuumDamage = new DamageSource("Vacuum").setDamageBypassesArmor().setDamageIsAbsolute();

	public static long lastSuffocationTime = Integer.MIN_VALUE;
	private static final int MAX_BLOB_RADIUS = 64;
	private static HashMap<Integer, AtmosphereHandler> dimensionOxygen = new HashMap<Integer, AtmosphereHandler>();
	private static HashMap<EntityPlayer, IAtmosphere> prevAtmosphere = new HashMap<EntityPlayer, IAtmosphere>();
	
	private HashMap<IBlobHandler,AreaBlob> blobs;
	int dimId;

	//Stores current Atm on the CLIENT
	public static IAtmosphere currentAtm;
	
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
		Entity entity = event.getEntity();
		if(!entity.worldObj.isRemote && entity.worldObj.provider.getDimension() == this.dimId) {
			IAtmosphere atmosType = getAtmosphereType(entity);

			if(entity instanceof EntityPlayer && atmosType != prevAtmosphere.get(entity)) {
				PacketHandler.sendToPlayer(new PacketAtmSync(atmosType.getUnlocalizedName()), (EntityPlayer)entity);
				prevAtmosphere.put((EntityPlayer)entity, atmosType);
			}
			
			if(atmosType.canTick())
				atmosType.onTick((EntityLivingBase)event.getEntityLiving());
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

	private void onBlockRemove(HashedBlockPosition pos) {
		for(AreaBlob blob : getBlobWithinRadius(pos, MAX_BLOB_RADIUS)) {
			//Make sure that a block can actually be attached to the blob
			for(EnumFacing dir : EnumFacing.VALUES)
				if(blob.contains(pos.getPositionAtOffset(dir))) {
					blob.addBlock(pos);
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
	/*public static void onBlockMetaChange(World world, int x , int y, int z) {
		if(Configuration.enableOxygen && !world.isRemote && world.getChunkFromBlockCoords(new BlockPos(x, y, z)).isLoaded()) {
			AtmosphereHandler handler = getOxygenHandler(world.provider.getDimension());
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

		if(Configuration.enableOxygen && !world.isRemote && world.getChunkFromBlockCoords(new BlockPos(bpos)).isLoaded()) {
			HashedBlockPosition pos = new HashedBlockPosition(bpos);

			AtmosphereHandler handler = getOxygenHandler(world.provider.getDimension());

			if(handler == null)
				return; //WTF
			
			for(AreaBlob blob : handler.getBlobWithinRadius(pos, MAX_BLOB_RADIUS)) {

				if(world.isAirBlock(bpos))
					handler.onBlockRemove(pos);
				else {
					//Place block
					if( blob.contains(pos) && SealableBlockHandler.isFulBlock(world, pos.getBlockPos())) {
						blob.removeBlock(pos);
					}
					else if(!blob.contains(blob.getRootPosition())) {
						blob.addBlock(blob.getRootPosition());
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
	public void registerBlob(IBlobHandler handler, BlockPos pos) {
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
	 */
	public void addBlock(IBlobHandler handler, HashedBlockPosition pos){
		AreaBlob blob = blobs.get(handler);
		blob.addBlock(pos);
	}

	/**
	 * @param x
	 * @param y
	 * @param z
	 * @return AtmosphereType at this location
	 */
	public IAtmosphere getAtmosphereType(BlockPos pos2) {
		if(Configuration.enableOxygen) {
			HashedBlockPosition pos = new HashedBlockPosition(pos2);
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
			HashedBlockPosition pos = new HashedBlockPosition((int)(entity.posX - 1), (int)Math.ceil(entity.posY), (int)(entity.posZ - 1));
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
	 * @param entity entity to check against
	 * @return true if the entity can breathe in the this atmosphere
	 */
	public boolean canEntityBreathe(EntityLiving entity) {
		if(Configuration.enableOxygen) {
			HashedBlockPosition pos = new HashedBlockPosition((int)Math.ceil(entity.posX), (int)Math.ceil(entity.posY), (int)Math.ceil(entity.posZ));
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
