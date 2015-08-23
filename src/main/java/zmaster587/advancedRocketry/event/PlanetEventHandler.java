package zmaster587.advancedRocketry.event;

import net.minecraftforge.event.entity.living.LivingFallEvent;
import net.minecraftforge.event.entity.player.PlayerOpenContainerEvent;
import net.minecraftforge.event.world.WorldEvent;
import zmaster587.advancedRocketry.api.IPlanetaryProvider;
import zmaster587.advancedRocketry.network.PacketDimInfo;
import zmaster587.advancedRocketry.network.PacketHandler;
import zmaster587.advancedRocketry.world.DimensionManager;
import zmaster587.advancedRocketry.world.DimensionProperties;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.eventhandler.Event.Result;
import cpw.mods.fml.common.gameevent.PlayerEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import cpw.mods.fml.common.network.FMLNetworkEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class PlanetEventHandler {

	public static long time = 0;
	
	//Handle gravity
	@SubscribeEvent
	public void playerTick(TickEvent.PlayerTickEvent event) {
		if(event.player.worldObj.provider instanceof IPlanetaryProvider && !event.player.isInWater()) {
			IPlanetaryProvider planet = (IPlanetaryProvider)event.player.worldObj.provider;
			if(!event.player.capabilities.isFlying) {
				event.player.motionY += 0.04f - planet.getGravitationalMultiplier()*0.04f;
			}
		}
	}

	@SubscribeEvent
	public void containerOpen(PlayerOpenContainerEvent event) {
		event.setResult(Result.ALLOW);
	}
	
	//Tick dimensions, needed for satellites, and guis
	@SubscribeEvent
	public void tick(TickEvent.ServerTickEvent event) {
		//Tick satellites
		DimensionManager.getInstance().tickDimensions();
		time++;
	}

	//Make sure the player receives data about the dimensions
	@SubscribeEvent
	public void playerChangedDimensionEvent(PlayerEvent.PlayerChangedDimensionEvent event) {
		if(!event.player.worldObj.isRemote) {
			if(DimensionManager.getInstance().isDimensionCreated(event.toDim))
				PacketHandler.sendToPlayer(new PacketDimInfo(event.toDim, DimensionManager.getInstance().getDimensionProperties(event.toDim)), event.player);
		}

	}

	//Make sure the player receives data about the dimensions
	@SubscribeEvent
	public void playerLoggedInEvent(FMLNetworkEvent.ServerConnectionFromClientEvent event) {
		for(int i : DimensionManager.getInstance().getregisteredDimensions()) {
			PacketHandler.sendToDispatcher(new PacketDimInfo(i, DimensionManager.getInstance().getDimensionProperties(i)), event.manager);
		}
		PacketHandler.sendToDispatcher(new PacketDimInfo(0, DimensionManager.getInstance().getDimensionProperties(0)), event.manager);
		
	}


	// Used to save extra biome data
	/*@SubscribeEvent
	public void worldLoadEvent(WorldEvent.Load event) {
		if(event.world.provider instanceof ProviderPlanet && DimensionManager.getInstance().getDimensionProperties(event.world.provider.dimensionId).biomeProperties == null) {
			DimensionManager.getInstance().getDimensionProperties(event.world.provider.dimensionId).biomeProperties = new ExtendedBiomeProperties(event.world);
		}
	}

	// Used to load extra biome data
	@SubscribeEvent
	public void saveExtraData(ChunkDataEvent.Save event) {
		if(event.world.provider instanceof ProviderPlanet) {
			NBTTagCompound nbt = event.getData();

			int xPos = event.getChunk().xPosition;//nbt.getInteger("xPos");
			int zPos = event.getChunk().zPosition;//nbt.getInteger("zPos");

			ChunkProperties properties = DimensionManager.getInstance().getDimensionProperties(event.world.provider.dimensionId).biomeProperties.getChunkPropertiesFromChunkCoords(xPos, zPos);

			nbt.setIntArray("ExtendedBiomeArray", properties.getBlockBiomeArray());
		}
	}

	@SubscribeEvent
	public void loadExtraData(ChunkDataEvent.Load event) {
		if(event.world.provider instanceof ProviderPlanet)  {
			NBTTagCompound nbt = event.getData();


			int xPos = event.getChunk().xPosition;//nbt.getInteger("xPos");
			int zPos = event.getChunk().zPosition;//nbt.getInteger("zPos");
			ChunkProperties properties = DimensionManager.getInstance().getDimensionProperties(event.world.provider.dimensionId).biomeProperties.getChunkPropertiesFromChunkCoords(xPos, zPos);

			properties.setBlockBiomeArray(event.getData().getIntArray("ExtendedBiomeArray"));
		}
	}
*/
	
	//Handle fog density and color
	@SubscribeEvent
	@SideOnly(Side.CLIENT)
	public void fogColor(net.minecraftforge.client.event.EntityViewRenderEvent.FogColors event) {
		DimensionProperties properties = DimensionManager.getInstance().getDimensionProperties(event.entity.dimension);
		if(properties != null) {
			float fog = properties.getAtmosphereDensityAtHeight(event.entity.posY);

			event.red *= fog;
			event.green *= fog;
			event.blue *= fog;

		}
	}

	//Saves NBT data
	@SubscribeEvent
	public void worldSaveEvent(WorldEvent.Save event) {
		//TODO: save only the one dimension
		if(event.world.provider.dimensionId == 0)
			//DimensionManager.getInstance().getDimensionProperties(event.world.provider.dimensionId).
			DimensionManager.getInstance().saveDimensions(DimensionManager.filePath);
	}


	//Make sure the player doesnt die on low gravity worlds
	@SubscribeEvent
	public void fallEvent(LivingFallEvent event) {
		if(event.entity.worldObj.provider instanceof IPlanetaryProvider) {
			IPlanetaryProvider planet = (IPlanetaryProvider)event.entity.worldObj.provider;
			event.distance *= planet.getGravitationalMultiplier();
		}
	}
}
