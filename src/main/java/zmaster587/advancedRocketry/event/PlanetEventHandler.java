package zmaster587.advancedRocketry.event;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.TorchBlock;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ActiveRenderInfo;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityClassification;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerEntity.SleepResult;
import net.minecraft.entity.player.ServerPlayerEntity;

import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.Biome.SpawnListEntry;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.EntityViewRenderEvent.FogColors;
import net.minecraftforge.client.event.EntityViewRenderEvent.RenderFogEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.TickEvent.ServerTickEvent;
import net.minecraftforge.event.entity.living.LivingEvent.LivingUpdateEvent;
import net.minecraftforge.event.entity.living.LivingFallEvent;
import net.minecraftforge.event.entity.living.LivingSpawnEvent;
import net.minecraftforge.event.entity.living.LivingSpawnEvent.CheckSpawn;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.RightClickBlock;
import net.minecraftforge.event.entity.player.PlayerSleepInBedEvent;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.event.world.ChunkEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.eventbus.api.Event.Result;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import org.lwjgl.opengl.GL11;

import com.mojang.blaze3d.platform.GlStateManager;

import zmaster587.advancedRocketry.AdvancedRocketry;
import zmaster587.advancedRocketry.achievements.ARAchivements;
import zmaster587.advancedRocketry.api.AdvancedRocketryBlocks;
import zmaster587.advancedRocketry.api.AdvancedRocketryItems;
import zmaster587.advancedRocketry.api.ARConfiguration;
import zmaster587.advancedRocketry.api.IPlanetaryProvider;
import zmaster587.advancedRocketry.api.stations.ISpaceObject;
import zmaster587.advancedRocketry.atmosphere.AtmosphereHandler;
import zmaster587.advancedRocketry.atmosphere.AtmosphereType;
import zmaster587.advancedRocketry.client.ClientRenderHelper;
import zmaster587.advancedRocketry.client.render.planet.RenderPlanetarySky;
import zmaster587.advancedRocketry.dimension.DimensionManager;
import zmaster587.advancedRocketry.dimension.DimensionProperties;
import zmaster587.advancedRocketry.network.PacketAsteroidInfo;
import zmaster587.advancedRocketry.network.PacketConfigSync;
import zmaster587.advancedRocketry.network.PacketDimInfo;
import zmaster587.advancedRocketry.network.PacketSpaceStationInfo;
import zmaster587.advancedRocketry.network.PacketStellarInfo;
import zmaster587.advancedRocketry.stations.SpaceObjectManager;
import zmaster587.advancedRocketry.util.AsteroidSmall;
import zmaster587.advancedRocketry.util.BiomeHandler;
import zmaster587.advancedRocketry.util.GravityHandler;
import zmaster587.advancedRocketry.util.SpawnListEntryNBT;
import zmaster587.advancedRocketry.util.TransitionEntity;
import zmaster587.advancedRocketry.world.ChunkManagerPlanet;
import zmaster587.advancedRocketry.world.provider.WorldProviderPlanet;
import zmaster587.advancedRocketry.world.util.TeleporterNoPortal;
import zmaster587.libVulpes.api.IModularArmor;
import zmaster587.libVulpes.event.BucketHandler;
import zmaster587.libVulpes.network.PacketHandler;
import zmaster587.libVulpes.util.ZUtils;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Map.Entry;

public class PlanetEventHandler {

	public static long time = 0;
	private static long endTime, duration;
	private static List<TransitionEntity> transitionMap = new LinkedList<TransitionEntity>();

	public static void addDelayedTransition(TransitionEntity entity) {
		transitionMap.add(entity);
	}

	@SubscribeEvent
	public void CheckSpawn(LivingSpawnEvent.CheckSpawn event)
	{
		World world = (World) event.getWorld();
		DimensionManager manager = DimensionManager.getInstance();

		if(manager.isInitialized())
		{
			DimensionProperties properties = manager.getDimensionProperties(world);
			if(properties != null) {
				if(!properties.getAtmosphere().isImmune(event.getEntityLiving().getClass()))
					event.setResult(Result.DENY);
			}
		}
	}

	@SubscribeEvent
	public void SpawnEntity(WorldEvent.PotentialSpawns event) {
		World world = (World) event.getWorld();

		DimensionProperties properties = DimensionManager.getInstance().getDimensionProperties(world);
		if(properties != null) {
			List<SpawnListEntryNBT> entries = properties.getSpawnListEntries();
			if(!entries.isEmpty() && event.getType() != EntityClassification.MONSTER)
				event.getList().addAll(entries);
		}
	}

	/*@SubscribeEvent
	public void onWorldGen(OreGenEvent.GenerateMinable event) {

		if(event.getWorld().provider instanceof WorldProviderPlanet && 
				DimensionManager.getInstance().getDimensionProperties(event.getWorld().provider.getDimension()).getOreGenProperties(event.getWorld()) != null) {

			switch(event.getType()) {
			case COAL:
			case DIAMOND:
			case EMERALD:
			case GOLD:
			case IRON:
			case LAPIS:
			case QUARTZ:
			case REDSTONE:
			case CUSTOM:
				event.setResult(Result.DENY);
				break;
			default:
				event.setResult(Result.DEFAULT);
			}
		}
	}*/

	//Handle gravity
	@SubscribeEvent
	public void playerTick(LivingUpdateEvent event) {

		if(event.getEntity().world.isRemote && event.getEntity().getPosY() > 260 && event.getEntity().getPosY() < 270 && event.getEntity().getMotion().y < -.1) {
			RocketEventHandler.destroyOrbitalTextures(event.getEntity().world);
		}
		if(event.getEntity().isInWater()) {
			if(AtmosphereType.LOWOXYGEN.isImmune(event.getEntityLiving()))
				event.getEntity().setAir(300);
		}

		if(!event.getEntity().world.isRemote && event.getEntity().world.getGameTime() % 20 ==0 && event.getEntity() instanceof PlayerEntity) {
			if(DimensionManager.getInstance().getDimensionProperties(event.getEntity().world).getName().equals("Luna") && 
					event.getEntity().getPositionVec().squareDistanceTo(2347,80, 67) < 512 ) {
				ARAchivements.WENT_TO_THE_MOON.trigger((ServerPlayerEntity)event.getEntity());
			}	
		}

		GravityHandler.applyGravity(event.getEntity());
	}

	@SubscribeEvent
	public void sleepEvent(PlayerSleepInBedEvent event) {
		DimensionProperties props = DimensionManager.getInstance().getDimensionProperties(event.getEntity().getEntityWorld());
		if(props != DimensionManager.defaultSpaceDimensionProperties) {
			if (!ARConfiguration.getCurrentConfig().forcePlayerRespawnInSpace && AtmosphereHandler.hasAtmosphereHandler(event.getEntity().world) && 
					!AtmosphereHandler.getOxygenHandler(event.getEntity().world).getAtmosphereType(event.getPos()).isBreathable()) {
				event.setResult(SleepResult.OTHER_PROBLEM);
			}
		}
	}

	@SubscribeEvent
	public void blockPlacedEvent(BlockEvent.EntityPlaceEvent event)
	{
		World world =event.getEntity().getEntityWorld(); 
		if(!world.isRemote  && AtmosphereHandler.getOxygenHandler(world) != null &&
				!AtmosphereHandler.getOxygenHandler(world).getAtmosphereType(event.getPos()).allowsCombustion()) {

			if(event.getPlacedBlock().getBlock() == Blocks.TORCH) {
				event.getWorld().setBlockState(event.getPos(), AdvancedRocketryBlocks.blockUnlitTorch.getDefaultState(),20);
			}
			else if(zmaster587.advancedRocketry.api.ARConfiguration.getCurrentConfig().torchBlocks.contains(event.getPlacedBlock().getBlock()))
			{
				event.setResult(Result.DENY);
				event.setCanceled(true);
			}
		}
	}
	
	@SubscribeEvent
	public void blockRightClicked(RightClickBlock event) {
		Direction direction = event.getFace();
		if(!event.getWorld().isRemote && direction != null  && event.getPlayer() != null  && AtmosphereHandler.getOxygenHandler(event.getWorld()) != null &&
				!AtmosphereHandler.getOxygenHandler(event.getWorld()).getAtmosphereType(event.getPos().offset(direction)).allowsCombustion()) {

			if(event.getPlayer().getHeldItem(event.getHand()) != null) {
				if(event.getPlayer().getHeldItem(event.getHand()).getItem() == Items.FLINT_AND_STEEL || event.getPlayer().getHeldItem(event.getHand()).getItem() == Items.FIRE_CHARGE|| event.getPlayer().getHeldItem(event.getHand()).getItem() == Items.BLAZE_POWDER || event.getPlayer().getHeldItem(event.getHand()).getItem() == Items.BLAZE_ROD )
					event.setCanceled(true);
			}
		}

		if(!event.getWorld().isRemote && event.getItemStack() != null && event.getItemStack().getItem() == Item.getItemFromBlock(AdvancedRocketryBlocks.blockGenericSeat) && event.getWorld().getBlockState(event.getPos()).getBlock() == Blocks.TNT) {
			ARAchivements.BEER.trigger((ServerPlayerEntity) event.getPlayer());
		}
	}

	@SubscribeEvent
	public void disconnected(ClientDisconnectionFromServerEvent event) {
		// Reload configs from disk
		ARConfiguration.useClientDiskConfig();
		//zmaster587.advancedRocketry.dimension.DimensionManager.getInstance().unregisterAllDimensions();
	}


	//TODO: more robust way of inv checking
	/*@SubscribeEvent
	public void containerOpen(PlayerContainerEvent event) {
		//event.getEntity()Player.openContainer
		if(RocketInventoryHelper.canPlayerBypassInvChecks(event.getEntityPlayer()) && event instanceof PlayerContainerEvent.Close)
			RocketInventoryHelper.removePlayerFromInventoryBypass(event.getEntityPlayer());
		if(event instanceof PlayerContainerEvent.Open) {

		}
	}*/

	//Tick dimensions, needed for satellites, and guis
	@SubscribeEvent
	public void tick(ServerTickEvent event) {
		//Tick satellites
		if(event.phase == event.phase.END) {
			DimensionManager.getInstance().tickDimensions();
			time++;

			if(!transitionMap.isEmpty()) {
				Iterator<TransitionEntity> itr = transitionMap.iterator();

				while(itr.hasNext()) {
					TransitionEntity ent = itr.next();
					if(ent.entity.world.getGameTime() >= ent.time) {
						ent.entity.setLocationAndAngles(ent.location.getX(), ent.location.getY(), ent.location.getZ(), ent.entity.rotationYaw, ent.entity.rotationPitch);
						ServerWorld newWorld = ent.dimId;
						ent.entity.changeDimension(newWorld, new TeleporterNoPortal(newWorld));
						//should be loaded by now
						Entity rocket = newWorld.getEntityByUuid(ent.entity2.getUniqueID());
						if(rocket != null)
							ent.entity.startRiding(rocket);
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
	public void playerLoggedInEvent(ServerConnectionFromClientEvent event) {

		//Send config first
		if(!event.isLocal())
			PacketHandler.sendToDispatcher(new PacketConfigSync(), event.getManager());

		//Make sure stars are sent next
		for(int i : DimensionManager.getInstance().getStarIds()) {
			PacketHandler.sendToDispatcher(new PacketStellarInfo(i, DimensionManager.getInstance().getStar(i)), event.getManager());
		}

		for(int i : DimensionManager.getInstance().getRegisteredDimensions()) {
			PacketHandler.sendToDispatcher(new PacketDimInfo(i, DimensionManager.getInstance().getDimensionProperties(i)), event.getManager());
		}

		for(ISpaceObject obj : SpaceObjectManager.getSpaceManager().getSpaceObjects()) {
			PacketHandler.sendToDispatcher(new PacketSpaceStationInfo(obj.getId(), obj), event.getManager());
		}

		PacketHandler.sendToDispatcher(new PacketDimInfo(0, DimensionManager.getInstance().getDimensionProperties(0)), event.getManager());
	}

	public void connectToServer(ClientConnectedToServerEvent event)
	{
		zmaster587.advancedRocketry.dimension.DimensionManager.getInstance().unregisterAllDimensions();
	}

	@SubscribeEvent
	public void worldLoadEvent(WorldEvent.Load event) {
		if(!event.getWorld().isRemote())
			AtmosphereHandler.registerWorld((World)event.getWorld());
		else if(ARConfiguration.getCurrentConfig().skyOverride && event.getWorld().provider.getDimension() == 0)
			event.getWorld().provider.setSkyRenderer(new RenderPlanetarySky());
	}

	@SubscribeEvent
	public void worldUnloadEvent(WorldEvent.Unload event) {
		if(!event.getWorld().isRemote())
			AtmosphereHandler.unregisterWorld(event.getWorld());
	}

	/**
	 * Starts a burst, used for move to warp effect
	 * @param endTime
	 * @param duration
	 */
	@OnlyIn(value=Dist.CLIENT)
	public static void runBurst(long endTime, long duration) {
		PlanetEventHandler.endTime = endTime;
		PlanetEventHandler.duration = duration;
	}

	//Handle fog density and color
	@SubscribeEvent
	@OnlyIn(value=Dist.CLIENT)
	public void fogColor(FogColors event) {
		BlockState state = ActiveRenderInfo.getBlockStateAtEntityViewpoint(event.getEntity().world, event.getEntity(), (float)event.getRenderPartialTicks());

		Block block = state.getBlock();
		if(block.getMaterial(state) == Material.WATER)
			return;

		DimensionProperties properties = DimensionManager.getInstance().getDimensionProperties(event.getEntity().dimension);
		if(properties != null) {
			float fog = Math.min(properties.getAtmosphereDensityAtHeight(event.getEntity().posY), 200);

			if(event.getEntity().world.provider instanceof IPlanetaryProvider) {
				Vector3d color = event.getEntity().world.provider.getSkyColor(event.getEntity(), 0f);
				event.setRed((float) Math.min(color.x*1.4f,1f));
				event.setGreen((float) Math.min(color.y*1.4f, 1f));
				event.setBlue((float) Math.min(color.z*1.4f, 1f));
			}

			if(endTime > 0) {
				double amt = (endTime - Minecraft.getInstance().world.getGameTime()) / (double)duration;
				if(amt < 0) {
					endTime = 0;
				}
				else {
					event.setRed((float) amt);
					event.setGreen((float) amt);
					event.setBlue((float) amt);
				}

			} else {
				event.setRed(event.getRed()* fog);
				event.setGreen(event.getGreen()* fog);
				event.setBlue(event.getBlue()* fog);
			}
		}
	}

	@SubscribeEvent
	public void serverTickEvent(TickEvent.WorldTickEvent event) {
		if(zmaster587.advancedRocketry.api.ARConfiguration.getCurrentConfig().allowTerraforming && event.world.provider.getClass() == WorldProviderPlanet.class) {

			if(DimensionManager.getInstance().getDimensionProperties(event.world).isTerraformed()) {
				Collection<Chunk> list = ((ServerWorld)event.world).getChunkProvider().getLoadedChunks();
				if(list.size() > 0) {
					try {
						int listSize = list.size();

						for(Chunk chunk : list) {

							if(ARConfiguration.getCurrentConfig().terraformingBlockSpeed > listSize || event.world.rand.nextFloat() < ARConfiguration.getCurrentConfig().terraformingBlockSpeed/(float)listSize)
							{
								int coord = event.world.rand.nextInt(256);
								int x = (coord & 0xF) + chunk.x*16;
								int z = (coord >> 4) + chunk.z*16;

								BiomeHandler.changeBiome(event.world, Biome.getIdForBiome(((ChunkManagerPlanet)((WorldProviderPlanet)event.world.provider).chunkMgrTerraformed).getBiomeGenAt(x,z)), x, z);
							}
						}
					} catch (NullPointerException e) {
						//Ghost
					}
				}
			}
		}
	}

	@SubscribeEvent
	public void chunkLoadEvent(PopulateChunkEvent.Post event) {
		if(zmaster587.advancedRocketry.api.ARConfiguration.getCurrentConfig().allowTerraforming && event.getWorld().provider.getClass() == WorldProviderPlanet.class) {

			if(DimensionManager.getInstance().getDimensionProperties(event.getWorld().provider.getDimension()).isTerraformed()) {
				Chunk chunk = event.getWorld().getChunkFromChunkCoords(event.getChunkX(), event.getChunkZ());
				modifyChunk(event.getWorld(), (WorldProviderPlanet) event.getWorld().provider, chunk);
			}
		}
	}

	@SubscribeEvent
	public void chunkLoadEvent(ChunkEvent.Load event) {
	}

	public static void modifyChunk(World world ,WorldProviderPlanet provider, Chunk chunk) {
		for(int x = 0; x < 16; x++) {
			for(int z = 0; z < 16; z++) {
				BiomeHandler.changeBiome(world, Biome.getIdForBiome(((ChunkManagerPlanet)((WorldProviderPlanet)world.provider).chunkMgrTerraformed).getBiomeGenAt(x + chunk.x*16,z + chunk.z*16)), chunk, x + chunk.x* 16, z + chunk.z*16);
			}
		}
	}


	static final ItemStack component = new ItemStack(AdvancedRocketryItems.itemUpgrade, 1, 4);
	@SubscribeEvent
	@OnlyIn(value=Dist.CLIENT)
	public void fogColor(RenderFogEvent event) {

		if(false || event.getFogMode() == -1) {
			return;
		}
		DimensionProperties properties = DimensionManager.getInstance().getDimensionProperties(event.getEntity().dimension);
		if(properties != null && event.getState().getBlock() != Blocks.WATER && event.getState().getBlock() != Blocks.LAVA) {//& properties.atmosphereDensity > 125) {
			float fog = Math.min(properties.getAtmosphereDensityAtHeight(event.getEntity().posY), 200);
			//GL11.glFogi(GL11.GL_FOG_MODE, GL11.GL_EXP);
			GlStateManager.setFog(GlStateManager.FogMode.LINEAR);



			float f1 = event.getFarPlaneDistance();
			float near;
			float far;

			int atmosphere = Math.min(properties.getAtmosphereDensity(), 200);
			ItemStack armor = Minecraft.getInstance().player.getItemStackFromSlot(EquipmentSlotType.HEAD);

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
				atmosphere = Math.min(AtmosphereHandler.currentPressure, 200);
			}

			if(atmosphere > 100) {
				near = 0.75f*f1*(2.00f - atmosphere*atmosphere/10000f);
				far = f1;
			}
			else {
				near = 0.75f*f1*(2.00f -atmosphere/100f);
				far = f1*(2.002f - atmosphere/100f);
			}

			GlStateManager.setFogStart(near);
			GlStateManager.setFogEnd(far);
			GlStateManager.setFogDensity(0);


			//event.setCanceled(false);
		}

	}



	//Saves NBT data
	@SubscribeEvent
	public void worldSaveEvent(WorldEvent.Save event) {
		//TODO: save only the one dimension
		if(event.getWorld().provider.getDimension() == 0)
			try {
				DimensionManager.getInstance().saveDimensions(DimensionManager.workingPath);
			} catch (Exception e) {
				AdvancedRocketry.logger.fatal("An error has occured saving planet data, this can happen if another mod causes the game to crash during game load.  If the game has fully loaded, then this is a serious error, Advanced Rocketry data has not been saved.");
				e.printStackTrace();
			}
	}


	//Make sure the player doesnt die on low gravity worlds
	@SubscribeEvent
	public void fallEvent(LivingFallEvent event) {
		if(DimensionManager.getInstance().isDimensionCreated(ZUtils.getDimensionIdentifier(event.getEntity().world))) {
			IPlanetaryProvider planet = (IPlanetaryProvider) DimensionManager.getInstance().getDimensionProperties(ZUtils.getDimensionIdentifier(event.getEntity().world));
			event.setDistance((float) (event.getDistance() * planet.getGravitationalMultiplier(new BlockPos(event.getEntity().getPositionVec()))));
		}
	}
}
