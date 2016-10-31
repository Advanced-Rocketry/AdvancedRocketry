package zmaster587.advancedRocketry.event;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.lwjgl.opengl.GL11;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ActiveRenderInfo;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayer.EnumStatus;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import net.minecraft.world.WorldProvider;
import net.minecraft.world.WorldServer;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.event.entity.living.LivingEvent.LivingUpdateEvent;
import net.minecraftforge.event.entity.living.LivingFallEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.entity.player.PlayerSleepInBedEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.Action;
import net.minecraftforge.event.entity.player.PlayerOpenContainerEvent;
import net.minecraftforge.event.terraingen.PopulateChunkEvent;
import net.minecraftforge.event.world.ChunkEvent;
import net.minecraftforge.event.world.WorldEvent;
import zmaster587.advancedRocketry.achievements.ARAchivements;
import zmaster587.advancedRocketry.api.AdvancedRocketryBiomes;
import zmaster587.advancedRocketry.api.AdvancedRocketryBlocks;
import zmaster587.advancedRocketry.api.AdvancedRocketryItems;
import zmaster587.advancedRocketry.api.Configuration;
import zmaster587.advancedRocketry.api.IPlanetaryProvider;
import zmaster587.advancedRocketry.api.stations.ISpaceObject;
import zmaster587.advancedRocketry.atmosphere.AtmosphereHandler;
import zmaster587.advancedRocketry.dimension.DimensionManager;
import zmaster587.advancedRocketry.dimension.DimensionProperties;
import zmaster587.advancedRocketry.network.PacketDimInfo;
import zmaster587.advancedRocketry.network.PacketSpaceStationInfo;
import zmaster587.advancedRocketry.network.PacketStellarInfo;
import zmaster587.advancedRocketry.stations.SpaceObjectManager;
import zmaster587.advancedRocketry.util.BiomeHandler;
import zmaster587.advancedRocketry.util.TransitionEntity;
import zmaster587.advancedRocketry.world.provider.WorldProviderPlanet;
import zmaster587.advancedRocketry.world.util.TeleporterNoPortal;
import zmaster587.libVulpes.LibVulpes;
import zmaster587.libVulpes.api.IModularArmor;
import zmaster587.libVulpes.api.LibVulpesItems;
import zmaster587.libVulpes.network.PacketHandler;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.eventhandler.Event.Result;
import cpw.mods.fml.common.gameevent.PlayerEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent.PlayerRespawnEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import cpw.mods.fml.common.network.FMLNetworkEvent;
import cpw.mods.fml.common.network.FMLNetworkEvent.ClientDisconnectionFromServerEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class PlanetEventHandler {

	public static long time = 0;
	private static long endTime, duration;
	private static Map<Long,TransitionEntity> transitionMap = new HashMap<Long,TransitionEntity>();

	public static void addDelayedTransition(long tick, TransitionEntity entity) {
		transitionMap.put(tick, entity);
	}

	@SubscribeEvent
	public void sleepEvent(PlayerSleepInBedEvent event) {

		if(event.entity.worldObj.provider instanceof WorldProviderPlanet && 
				AtmosphereHandler.hasAtmosphereHandler(event.entity.worldObj.provider.dimensionId) && !AtmosphereHandler.getOxygenHandler(event.entity.worldObj.provider.dimensionId).getAtmosphereType(event.x, event.y, event.z).isBreathable()) {
			event.result = EnumStatus.OTHER_PROBLEM;
		}
	}
	
	@SubscribeEvent
	public void onCrafting(PlayerEvent.ItemCraftedEvent event) {
		if(event.crafting != null) {
			Item item = event.crafting.getItem();
			if(item == LibVulpesItems.itemHoloProjector) 
			event.player.triggerAchievement(ARAchivements.holographic);
			else if(item == Item.getItemFromBlock(AdvancedRocketryBlocks.blockRollingMachine))
				event.player.triggerAchievement(ARAchivements.rollin);
			else if(item == Item.getItemFromBlock(AdvancedRocketryBlocks.blockCrystallizer))
				event.player.triggerAchievement(ARAchivements.crystalline);
			else if(item == Item.getItemFromBlock(AdvancedRocketryBlocks.blockLathe))
				event.player.triggerAchievement(ARAchivements.spinDoctor);
			else if(item ==Item.getItemFromBlock(AdvancedRocketryBlocks.blockElectrolyser))
				event.player.triggerAchievement(ARAchivements.electrifying);
			else if(item == Item.getItemFromBlock(AdvancedRocketryBlocks.blockArcFurnace))
				event.player.triggerAchievement(ARAchivements.feelTheHeat);
			else if(item == Item.getItemFromBlock(AdvancedRocketryBlocks.blockWarpCore))
				event.player.triggerAchievement(ARAchivements.feelTheHeat);
		}
	}
	
	@SubscribeEvent
	public void onPickup(PlayerEvent.ItemPickupEvent event) {
		if(event.pickedUp != null) {
			Item item = event.pickedUp.getEntityItem().getItem();
			
			
			zmaster587.libVulpes.api.material.Material mat = LibVulpes.materialRegistry.getMaterialFromItemStack( event.pickedUp.getEntityItem());
			if(mat != null && mat.getUnlocalizedName().contains("Dilithium"))
				event.player.triggerAchievement(ARAchivements.dilithiumCrystals);
		}
	}

	//Handle gravity
	@SubscribeEvent
	public void playerTick(LivingUpdateEvent event) {

		if(event.entity.worldObj.isRemote && event.entity.posY > 260 && event.entity.posY < 270 && event.entity.motionY < -.1) {
			RocketEventHandler.destroyOrbitalTextures(event.entity.worldObj);
		}
		if(event.entity.worldObj.provider instanceof IPlanetaryProvider && !event.entity.isInWater()) {
			IPlanetaryProvider planet = (IPlanetaryProvider)event.entity.worldObj.provider;
			if(!(event.entity instanceof EntityPlayer) || !((EntityPlayer)event.entity).capabilities.isFlying) {
				//event.entity.motionY += 0.075f - planet.getGravitationalMultiplier((int)event.entity.posX, (int)event.entity.posZ)*0.075f;
			}
		}
		else if(event.entity.worldObj.provider.dimensionId == 0) {
			if(!(event.entity instanceof EntityPlayer) || !((EntityPlayer)event.entity).capabilities.isFlying) {
				//event.entity.motionY += 0.075f - DimensionManager.overworldProperties.gravitationalMultiplier*0.075f;
			}
		}
		
		if(!event.entity.worldObj.isRemote && event.entity.worldObj.getTotalWorldTime() % 20 ==0 && event.entity instanceof EntityPlayer) {
			if(DimensionManager.getInstance().getDimensionProperties(event.entity.worldObj.provider.dimensionId).getName().equals("Luna") && 
					event.entity.getDistanceSq(67, 80, 2347) < 512 ) {
				((EntityPlayer)event.entity).triggerAchievement(ARAchivements.weReallyWentToTheMoon);
			}	
		}
	}

	@SubscribeEvent
	public void blockPlaceEvent(PlayerInteractEvent event) {
		ForgeDirection direction = ForgeDirection.getOrientation(event.face);
		if(!event.world.isRemote && Action.RIGHT_CLICK_BLOCK == event.action && event.entityPlayer != null  && AtmosphereHandler.getOxygenHandler(event.world.provider.dimensionId) != null &&
				!AtmosphereHandler.getOxygenHandler(event.world.provider.dimensionId).getAtmosphereType(event.x + direction.offsetX, event.y + direction.offsetY, event.z + direction.offsetZ).allowsCombustion()) {

			if(event.entityPlayer.getCurrentEquippedItem() != null) {
				if(event.entityPlayer.getCurrentEquippedItem().getItem() == Item.getItemFromBlock(Blocks.torch) && 
						event.world.getBlock(event.x, event.y, event.z).isSideSolid(event.world, event.x, event.y, event.z, direction)) {
					event.setCanceled(true);
					event.world.setBlock(event.x + direction.offsetX, event.y + direction.offsetY, event.z + direction.offsetZ, AdvancedRocketryBlocks.blockUnlitTorch);
				}
				else if(zmaster587.advancedRocketry.api.Configuration.torchBlocks.contains(Block.getBlockFromItem(event.entityPlayer.getCurrentEquippedItem().getItem())) )
					event.setCanceled(true);
				else if(event.entityPlayer.getCurrentEquippedItem().getItem() == Items.flint_and_steel || event.entityPlayer.getCurrentEquippedItem().getItem() == Items.fire_charge || event.entityPlayer.getCurrentEquippedItem().getItem() == Items.blaze_powder || event.entityPlayer.getCurrentEquippedItem().getItem() == Items.blaze_rod || event.entityPlayer.getCurrentEquippedItem().getItem() == Items.lava_bucket)
					event.setCanceled(true);
			}
		}
		
		if(!event.world.isRemote && event.entityPlayer != null && event.entityPlayer.getCurrentEquippedItem() != null && event.entityPlayer.getCurrentEquippedItem().getItem() == Items.bed && event.world.provider instanceof WorldProviderPlanet) {
			AdvancedRocketryItems.itemAstroBed.onItemUse( event.entityPlayer.getCurrentEquippedItem(),  event.entityPlayer,  event.entityPlayer.worldObj, event.x, event.y, event.z, event.face, 0, 0, 0);
			event.setCanceled(true);
		}
		
		if(!event.world.isRemote && event.entityPlayer != null && event.entityPlayer.getCurrentEquippedItem() != null && event.entityPlayer.getCurrentEquippedItem().getItem() == Item.getItemFromBlock(AdvancedRocketryBlocks.blockGenericSeat) && event.world.getBlock(event.x, event.y, event.z) == Blocks.tnt) {
			event.entityPlayer.triggerAchievement(ARAchivements.beerOnTheSun);
		}
	}

	@EventHandler
	public void disconnected(ClientDisconnectionFromServerEvent event) {
		zmaster587.advancedRocketry.dimension.DimensionManager.getInstance().unregisterAllDimensions();
	}

	/*@SubscribeEvent
	public void entityRegister(EntityConstructing event) {
		if(event.entity instanceof EntityPlayer) {
			event.entity.registerExtendedProperties(PlayerDataHandler.IDENTIFIER, new PlayerDataHandler());
		}
	}*/

	//TODO move
	//Has weak refs so if the player gets killed/logsout etc the entry doesnt stay trapped in RAM
	private static HashSet<WeakReference<EntityPlayer>> inventoryCheckPlayerBypassMap = new HashSet<WeakReference<EntityPlayer>>();

	public static void addPlayerToInventoryBypass(EntityPlayer player) {
		inventoryCheckPlayerBypassMap.add(new WeakReference<>(player));
	}

	public static void removePlayerFromInventoryBypass(EntityPlayer player) {
		Iterator<WeakReference<EntityPlayer>> iter = inventoryCheckPlayerBypassMap.iterator();

		while(iter.hasNext()) {
			WeakReference<EntityPlayer> player2 = iter.next();
			if(player2.get() == player || player2.get() == null)
				iter.remove();
		}
	}

	public static boolean canPlayerBypassInvChecks(EntityPlayer player) {
		Iterator<WeakReference<EntityPlayer>> iter = inventoryCheckPlayerBypassMap.iterator();
		while(iter.hasNext()) {
			WeakReference<EntityPlayer> player2 = iter.next();
			if(player2.get() == player)
				return true;
		}
		return false;
	}

	@SubscribeEvent
	public void containerOpen(PlayerOpenContainerEvent event) {
		//event.entityPlayer.openContainer
		if(canPlayerBypassInvChecks(event.entityPlayer))
			if(event.entityPlayer.openContainer.windowId == 0)
				removePlayerFromInventoryBypass(event.entityPlayer);
			else
				event.setResult(Result.ALLOW);
	}

	//Tick dimensions, needed for satellites, and guis
	@SubscribeEvent
	public void tick(TickEvent.ServerTickEvent event) {
		//Tick satellites
		if(event.phase == event.phase.END) {
			DimensionManager.getInstance().tickDimensions();
			time++;

			if(!transitionMap.isEmpty()) {
				Iterator<Entry<Long, TransitionEntity>> itr = transitionMap.entrySet().iterator();

				while(itr.hasNext()) {
					Entry<Long, TransitionEntity> entry = itr.next();
					TransitionEntity ent = entry.getValue();
					if(ent.entity.worldObj.getTotalWorldTime() >= entry.getKey()) {
						ent.entity.setLocationAndAngles(ent.location.x, ent.location.y, ent.location.z, ent.entity.rotationYaw, ent.entity.rotationPitch);
						
						MinecraftServer.getServer().getConfigurationManager().transferPlayerToDimension((EntityPlayerMP)ent.entity, ent.dimId, new TeleporterNoPortal(MinecraftServer.getServer().worldServerForDimension(ent.dimId)));

						ent.entity.mountEntity(ent.entity2);
						itr.remove();
					}
				}
			}
		}
	}

	@SubscribeEvent
	public void tickClient(TickEvent.ClientTickEvent event) {
		if(event.phase == event.phase.END)
			DimensionManager.getInstance().tickDimensionsClient();
	}

	//Make sure the player receives data about the dimensions
	@SubscribeEvent
	public void playerLoggedInEvent(FMLNetworkEvent.ServerConnectionFromClientEvent event) {

		//Make sure stars are sent first
		for(int i : DimensionManager.getInstance().getStarIds()) {
			PacketHandler.sendToDispatcher(new PacketStellarInfo(i, DimensionManager.getInstance().getStar(i)), event.manager);
		}

		for(int i : DimensionManager.getInstance().getregisteredDimensions()) {
			PacketHandler.sendToDispatcher(new PacketDimInfo(i, DimensionManager.getInstance().getDimensionProperties(i)), event.manager);
		}

		for(ISpaceObject obj : SpaceObjectManager.getSpaceManager().getSpaceObjects()) {
			PacketHandler.sendToDispatcher(new PacketSpaceStationInfo(obj.getId(), obj), event.manager);
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

	@SubscribeEvent
	public void worldLoadEvent(WorldEvent.Load event) {
		if(!event.world.isRemote)
			AtmosphereHandler.registerWorld(event.world.provider.dimensionId);
	}

	@SubscribeEvent
	public void worldUnloadEvent(WorldEvent.Unload event) {
		if(!event.world.isRemote)
			AtmosphereHandler.unregisterWorld(event.world.provider.dimensionId);
	}

	/**
	 * Starts a burst, used for move to warp effect
	 * @param endTime
	 * @param duration
	 */
	@SideOnly(Side.CLIENT)
	public static void runBurst(long endTime, long duration) {
		PlanetEventHandler.endTime = endTime;
		PlanetEventHandler.duration = duration;
	}

	//Handle fog density and color
	@SubscribeEvent
	@SideOnly(Side.CLIENT)
	public void fogColor(net.minecraftforge.client.event.EntityViewRenderEvent.FogColors event) {



		Block block = ActiveRenderInfo.getBlockAtEntityViewpoint(event.entity.worldObj, event.entity, (float)event.renderPartialTicks);
		if(block.getMaterial() == Material.water)
			return;

		DimensionProperties properties = DimensionManager.getInstance().getDimensionProperties(event.entity.dimension);
		if(properties != null) {
			float fog = properties.getAtmosphereDensityAtHeight(event.entity.posY);

			if(event.entity.worldObj.provider instanceof IPlanetaryProvider) {
				Vec3 color = event.entity.worldObj.provider.getSkyColor(event.entity, 0f);
				event.red = (float) Math.min(color.xCoord*1.4f,1f);
				event.green = (float) Math.min(color.yCoord*1.4f, 1f);
				event.blue = (float) Math.min(color.zCoord*1.4f, 1f);
			}

			if(endTime > 0) {
				double amt = (endTime - Minecraft.getMinecraft().theWorld.getTotalWorldTime()) / (double)duration;
				if(amt < 0) {
					endTime = 0;
				}
				else
					event.green = event.blue = event.red = (float)amt;

			} else {
				event.red *= fog;
				event.green *= fog;
				event.blue *= fog;
			}
		}
	}

	@SubscribeEvent
	public void serverTickEvent(TickEvent.WorldTickEvent event) {
		if(zmaster587.advancedRocketry.api.Configuration.allowTerraforming && event.world.provider.getClass() == WorldProviderPlanet.class) {

			if(DimensionManager.getInstance().getDimensionProperties(event.world.provider.dimensionId).isTerraformed()) {
				List<Chunk> list = ((WorldServer)event.world).theChunkProviderServer.loadedChunks;
				if(list.size() > 0) {
					for(int i = 0; i < Configuration.terraformingBlockSpeed; i++) {
						Chunk chunk = list.get(event.world.rand.nextInt(list.size()));
						int coord = event.world.rand.nextInt(256);
						int x = (coord & 0xF) + chunk.xPosition*16;
						int z = (coord >> 4) + chunk.zPosition*16;

						BiomeHandler.changeBiome(event.world, ((WorldProviderPlanet)event.world.provider).chunkMgrTerraformed.getBiomeGenAt(x,z).biomeID, x, z);
					}
				}
			}
		}
	}

	@SubscribeEvent
	public void chunkLoadEvent(PopulateChunkEvent.Post event) {
		if(zmaster587.advancedRocketry.api.Configuration.allowTerraforming && event.world.provider.getClass() == WorldProviderPlanet.class) {

			if(DimensionManager.getInstance().getDimensionProperties(event.world.provider.dimensionId).isTerraformed()) {
				Chunk chunk = event.world.getChunkFromChunkCoords(event.chunkX, event.chunkZ);
				modifyChunk(event.world, (WorldProviderPlanet) event.world.provider, chunk);
			}
		}
	}

	@SubscribeEvent
	public void chunkLoadEvent(ChunkEvent.Load event) {
	}

	public static void modifyChunk(World world ,WorldProviderPlanet provider, Chunk chunk) {
		for(int x = 0; x < 16; x++) {
			for(int z = 0; z < 16; z++) {

				BiomeHandler.changeBiome(world, provider.chunkMgrTerraformed.getBiomeGenAt(x + chunk.xPosition*16,z + chunk.zPosition*16).biomeID, chunk, x + chunk.xPosition* 16, z + chunk.zPosition*16);
			}
		}
	}


	static final ItemStack component = new ItemStack(AdvancedRocketryItems.itemUpgrade, 1, 4);
	@SubscribeEvent
	@SideOnly(Side.CLIENT)
	public void fogColor(net.minecraftforge.client.event.EntityViewRenderEvent.RenderFogEvent event) {

		if(false || event.fogMode == -1) {
			return;
		}

		DimensionProperties properties = DimensionManager.getInstance().getDimensionProperties(event.entity.dimension);
		if(properties != null && event.block != Blocks.water && event.block != Blocks.lava) {//& properties.atmosphereDensity > 125) {
			float fog = properties.getAtmosphereDensityAtHeight(event.entity.posY);
			//GL11.glFogi(GL11.GL_FOG_MODE, GL11.GL_EXP);
			GL11.glFogi(GL11.GL_FOG_MODE, GL11.GL_LINEAR);



			float f1 = event.farPlaneDistance;
			float near;
			float far;

			int atmosphere = properties.getAtmosphereDensity();
			ItemStack armor = Minecraft.getMinecraft().thePlayer.getCurrentArmor(3);

			if(armor != null && armor.getItem() instanceof IModularArmor) {
				for(ItemStack i : ((IModularArmor)armor.getItem()).getComponents(armor)) {
					if(i.isItemEqual(component)) {
						atmosphere = Math.min(atmosphere, 100);
						break;
					}
				}
			}
			
			//Check environment
			if(AtmosphereHandler.currentPressure != -1) {
				atmosphere = AtmosphereHandler.currentPressure;
			}

			if(atmosphere > 100) {
				near = 0.75f*f1*(2.00f - atmosphere*atmosphere/10000f);
				far = f1;
			}
			else {
				near = 0.75f*f1*(2.00f -atmosphere/100f);
				far = f1*(2.002f - atmosphere/100f);
			}

			GL11.glFogf(GL11.GL_FOG_START, near);
			GL11.glFogf(GL11.GL_FOG_END, far);
			GL11.glFogf(GL11.GL_FOG_DENSITY, 0);


			//event.setCanceled(false);
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
			event.distance *= planet.getGravitationalMultiplier((int)event.entity.posX, (int)event.entity.posZ);
		}
	}
}
