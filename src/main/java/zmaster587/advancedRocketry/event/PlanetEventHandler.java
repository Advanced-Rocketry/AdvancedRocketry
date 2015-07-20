package zmaster587.advancedRocketry.event;

import org.lwjgl.opengl.GL11;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.client.event.EntityViewRenderEvent.RenderFogEvent;
import net.minecraftforge.event.entity.living.LivingFallEvent;
import net.minecraftforge.event.world.ChunkDataEvent;
import net.minecraftforge.event.world.WorldEvent;
import zmaster587.advancedRocketry.api.IPlanetaryProvider;
import zmaster587.advancedRocketry.network.PacketDimInfo;
import zmaster587.advancedRocketry.network.PacketHandler;
import zmaster587.advancedRocketry.world.DimensionManager;
import zmaster587.advancedRocketry.world.DimensionProperties;
import zmaster587.advancedRocketry.world.ProviderPlanet;
import zmaster587.advancedRocketry.world.storage.ExtendedBiomeProperties;
import zmaster587.advancedRocketry.world.storage.ExtendedBiomeProperties.ChunkProperties;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import cpw.mods.fml.common.network.FMLNetworkEvent;

public class PlanetEventHandler {

	@SubscribeEvent
	public void playerTick(TickEvent.PlayerTickEvent event) {
		if(event.player.worldObj.provider instanceof IPlanetaryProvider) {
			IPlanetaryProvider planet = (IPlanetaryProvider)event.player.worldObj.provider;
			if(!event.player.capabilities.isFlying) {
				event.player.motionY += 0.04f - planet.getGraviationalMultiplyer()*0.04f;
			}
		}
	}

	@SubscribeEvent
	public void playerChangedDimensionEvent(PlayerEvent.PlayerChangedDimensionEvent event) {
		if(!event.player.worldObj.isRemote) {

			if(DimensionManager.getInstance().isDimensionCreated(event.toDim))
				PacketHandler.sendToPlayer(new PacketDimInfo(event.toDim, DimensionManager.getInstance().getDimensionProperties(event.toDim)), event.player);
		}

	}

	@SubscribeEvent
	public void playerLoggedInEvent(FMLNetworkEvent.ServerConnectionFromClientEvent event) {
		for(int i : DimensionManager.getInstance().getregisteredDimensions()) {
			PacketHandler.sendToDispatcher(new PacketDimInfo(i, DimensionManager.getInstance().getDimensionProperties(i)), event.manager);
		}
	}


	// Used to save extra biome data
	@SubscribeEvent
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

			int xPos = nbt.getInteger("xPos");
			int zPos = nbt.getInteger("zPos");

			ChunkProperties properties = DimensionManager.getInstance().getDimensionProperties(event.world.provider.dimensionId).biomeProperties.getChunkPropertiesFromChunkCoords(xPos, zPos);

			nbt.setIntArray("ExtendedBiomeArray", properties.getBlockBiomeArray());
		}
	}

	@SubscribeEvent
	public void loadExtraData(ChunkDataEvent.Load event) {
		if(event.world.provider instanceof ProviderPlanet)  {
			NBTTagCompound nbt = event.getData();

			int xPos = nbt.getInteger("xPos");
			int zPos = nbt.getInteger("zPos");
			ChunkProperties properties = DimensionManager.getInstance().getDimensionProperties(event.world.provider.dimensionId).biomeProperties.getChunkPropertiesFromChunkCoords(xPos, zPos);

			properties.setBlockBiomeArray(event.getData().getIntArray("ExtendedBiomeArray"));
		}
	}


	@SubscribeEvent
	public void fogDensityEvent(RenderFogEvent event) {
		DimensionProperties properties = DimensionManager.getInstance().getDimensionProperties(event.entity.dimension);

		if(properties != null) {
			float fog = properties.getAtmosphereDensityAtHeight(event.entity.posY);

			if(fog ==0)
				fog = 0.00000000001f;
			
				GL11.glFogi(GL11.GL_FOG_MODE, GL11.GL_LINEAR);
				GL11.glFogf(GL11.GL_FOG_START, event.farPlaneDistance * .075f/ fog);
				GL11.glFogf(GL11.GL_FOG_END, event.farPlaneDistance * 1f/ fog);
		}
	}

	@SubscribeEvent
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
		DimensionManager.getInstance().saveDimensions(DimensionManager.filePath);
	}


	@SubscribeEvent
	public void fallEvent(LivingFallEvent event) {
		if(event.entity.worldObj.provider instanceof IPlanetaryProvider) {
			IPlanetaryProvider planet = (IPlanetaryProvider)event.entity.worldObj.provider;
			event.distance *= planet.getGraviationalMultiplyer();
		}
	}
}
