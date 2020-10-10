package zmaster587.advancedRocketry.event;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.PortalInfo;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityClassification;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerEntity.SleepResult;
import net.minecraft.entity.player.ServerPlayerEntity;

import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.NetworkManager;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.DimensionType;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.ClientPlayerNetworkEvent.LoggedOutEvent;
import net.minecraftforge.client.event.EntityViewRenderEvent.FogColors;
import net.minecraftforge.client.event.EntityViewRenderEvent.RenderFogEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.TickEvent.ServerTickEvent;
import net.minecraftforge.event.entity.living.LivingEvent.LivingUpdateEvent;
import net.minecraftforge.event.entity.living.LivingFallEvent;
import net.minecraftforge.event.entity.living.LivingSpawnEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.RightClickBlock;
import net.minecraftforge.event.entity.player.PlayerSleepInBedEvent;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.eventbus.api.Event.Result;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.network.NetworkEvent.ClientCustomPayloadLoginEvent;
import net.minecraftforge.fml.network.NetworkEvent.ServerCustomPayloadLoginEvent;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;

import zmaster587.advancedRocketry.AdvancedRocketry;
import zmaster587.advancedRocketry.achievements.ARAchivements;
import zmaster587.advancedRocketry.api.AdvancedRocketryBlocks;
import zmaster587.advancedRocketry.api.AdvancedRocketryItems;
import zmaster587.advancedRocketry.api.ARConfiguration;
import zmaster587.advancedRocketry.api.stations.ISpaceObject;
import zmaster587.advancedRocketry.atmosphere.AtmosphereHandler;
import zmaster587.advancedRocketry.atmosphere.AtmosphereType;
import zmaster587.advancedRocketry.dimension.DimensionManager;
import zmaster587.advancedRocketry.dimension.DimensionProperties;
import zmaster587.advancedRocketry.network.PacketConfigSync;
import zmaster587.advancedRocketry.network.PacketDimInfo;
import zmaster587.advancedRocketry.network.PacketSpaceStationInfo;
import zmaster587.advancedRocketry.network.PacketStellarInfo;
import zmaster587.advancedRocketry.stations.SpaceObjectManager;
import zmaster587.advancedRocketry.util.GravityHandler;
import zmaster587.advancedRocketry.util.SpawnListEntryNBT;
import zmaster587.advancedRocketry.util.TransitionEntity;
import zmaster587.advancedRocketry.world.util.TeleporterNoPortal;
import zmaster587.advancedRocketry.world.util.WorldDummy;
import zmaster587.libVulpes.api.IModularArmor;
import zmaster587.libVulpes.network.PacketHandler;
import zmaster587.libVulpes.util.ZUtils;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

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
		IWorld world = event.getWorld();
		DimensionManager manager = DimensionManager.getInstance();

		if(manager.isInitialized() && world instanceof World)
		{
			DimensionProperties properties = manager.getDimensionProperties( ZUtils.getDimensionIdentifier((World) world) );
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
				ARAchivements.triggerAchievement(ARAchivements.WENT_TO_THE_MOON, (ServerPlayerEntity)event.getEntity());
			}	
		}

		GravityHandler.applyGravity(event.getEntity());
	}

	@SubscribeEvent
	public void sleepEvent(PlayerSleepInBedEvent event) {
		DimensionProperties props = DimensionManager.getInstance().getDimensionProperties(event.getEntity().getEntityWorld());
		if(props != DimensionManager.defaultSpaceDimensionProperties) {
			if (!ARConfiguration.getCurrentConfig().forcePlayerRespawnInSpace.get() && AtmosphereHandler.hasAtmosphereHandler(event.getEntity().world) && 
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
			ARAchivements.triggerAchievement(ARAchivements.BEER, (ServerPlayerEntity)event.getEntity());
		}
	}

	@SubscribeEvent
	@OnlyIn(value=Dist.CLIENT)
	public void disconnected(LoggedOutEvent event) {
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
						ServerWorld newWorld = ent.dimId;
						if(ent.entity instanceof ServerPlayerEntity)
						{
							((ServerPlayerEntity)ent.entity).teleport(newWorld, ent.location.getX(), ent.location.getY(), ent.location.getZ(), ent.entity.rotationYaw, ent.entity.rotationPitch);
						}
						else
						{
							PortalInfo info = new PortalInfo(new Vector3d(ent.location.getX(), ent.location.getY(), ent.location.getZ()), ent.entity.getMotion(), ent.entity.rotationYaw, ent.entity.rotationPitch);
							ent.entity.changeDimension(newWorld, new TeleporterNoPortal(newWorld, info));
						}
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
	@OnlyIn(value=Dist.CLIENT)
	public void tickClient(TickEvent.ClientTickEvent event) {
		if(event.phase == event.phase.END)
			DimensionManager.getInstance().tickDimensionsClient();
	}

	//Make sure the player receives data about the dimensions
	@SubscribeEvent
	public void playerLoggedInEvent(ServerCustomPayloadLoginEvent event) {
		NetworkManager mgr = event.getSource().get().getNetworkManager();

		//Send config first
		DistExecutor.runWhenOn(Dist.DEDICATED_SERVER, () -> () -> { PacketHandler.sendToDispatcher(new PacketConfigSync(), mgr); } );

		//Make sure stars are sent next
		for(ResourceLocation i : DimensionManager.getInstance().getStarIds()) {
			PacketHandler.sendToDispatcher(new PacketStellarInfo(i, DimensionManager.getInstance().getStar(i)), mgr);
		}

		for(ResourceLocation i : DimensionManager.getInstance().getRegisteredDimensions()) {
			PacketHandler.sendToDispatcher(new PacketDimInfo(i, DimensionManager.getInstance().getDimensionProperties(i)), mgr);
		}

		for(ISpaceObject obj : SpaceObjectManager.getSpaceManager().getSpaceObjects()) {
			PacketHandler.sendToDispatcher(new PacketSpaceStationInfo(obj.getId(), obj), mgr);
		}

		PacketHandler.sendToDispatcher(new PacketDimInfo(DimensionManager.getInstance().overworldProperties.getId(), DimensionManager.overworldProperties), mgr);
	}

	@OnlyIn(value=Dist.CLIENT)
	public void connectToServer(ClientCustomPayloadLoginEvent event)
	{
		zmaster587.advancedRocketry.dimension.DimensionManager.getInstance().unregisterAllDimensions();
	}

	@SubscribeEvent
	public void worldLoadEvent(WorldEvent.Load event) {
		if(!event.getWorld().isRemote())
			AtmosphereHandler.registerWorld((World)event.getWorld());
	}

	@SubscribeEvent
	public void worldUnloadEvent(WorldEvent.Unload event) {
		if(!event.getWorld().isRemote())
			AtmosphereHandler.unregisterWorld((World) event.getWorld());
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
		
		Entity entity = event.getInfo().getRenderViewEntity();
		World world = entity.world;
		BlockState state = event.getInfo().getBlockAtCamera();

		if(state.getMaterial() == Material.WATER)
			return;

		DimensionProperties properties = DimensionManager.getInstance().getDimensionProperties(world);
		if(properties != null) {
			float fog = Math.min(properties.getAtmosphereDensityAtHeight(entity.getPosY()), 200);

			float[] color =  properties.getSkyColor();
			event.setRed((float) Math.min(event.getRed()*color[0]*1.0f,1f));
			event.setGreen((float) Math.min(event.getGreen()*color[1]*1.0f, 1f));
			event.setBlue((float) Math.min(event.getBlue()*color[2]*1.0f, 1f));

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

	//TODO: terraforming
	/*@SubscribeEvent
	public void serverTickEvent(TickEvent.WorldTickEvent event) {
		if(zmaster587.advancedRocketry.api.ARConfiguration.getCurrentConfig().allowTerraforming.get() && DimensionManager.getInstance().isDimensionCreated(event.world)) {

			if(DimensionManager.getInstance().getDimensionProperties(event.world).isTerraformed()) {
				Collection<Chunk> list = ((ServerWorld)event.world).getChunkProvider().getLoadedChunks();
				if(list.size() > 0) {
					try {
						int listSize = list.size();

						for(Chunk chunk : list) {

							if(ARConfiguration.getCurrentConfig().terraformingBlockSpeed.get() > listSize || event.world.rand.nextFloat() < ARConfiguration.getCurrentConfig().terraformingBlockSpeed/(float)listSize)
							{
								int coord = event.world.rand.nextInt(256);
								int x = (coord & 0xF) + chunk.getPos().x*16;
								int z = (coord >> 4) + chunk.getPos().z*16;

								BiomeHandler.changeBiome(event.world, Biome.getIdForBiome(((ChunkManagerPlanet)((WorldProviderPlanet)event.world.provider).chunkMgrTerraformed).getBiomeGenAt(x,z)), x, z);
							}
						}
					} catch (NullPointerException e) {
						//Ghost
					}
				}
			}
		}
	}*/


	static final ItemStack component = new ItemStack(AdvancedRocketryItems.itemUpgradeFogGoggles, 1);
	@SubscribeEvent
	@OnlyIn(value=Dist.CLIENT)
	public void fogColor(RenderFogEvent event) {

		if(event.getInfo().getRenderViewEntity().getEntityWorld() == null || event.getInfo().getRenderViewEntity().getEntityWorld() instanceof WorldDummy)
			return;
		

		DimensionProperties properties = DimensionManager.getInstance().getDimensionProperties(event.getInfo().getRenderViewEntity().getEntityWorld());
		if(properties != null && event.getInfo().getBlockAtCamera().getBlock() != Blocks.WATER && event.getInfo().getBlockAtCamera().getBlock() != Blocks.LAVA) {//& properties.atmosphereDensity > 125) {
			float fog = Math.min(properties.getAtmosphereDensityAtHeight(event.getInfo().getRenderViewEntity().getPosY()), 200);
			//GL11.glFogi(GL11.GL_FOG_MODE, GL11.GL_EXP);
			GlStateManager.fogMode(GlStateManager.FogMode.LINEAR.param);



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
				near = 0.1f*f1*(2.00f -atmosphere/100f);
				far = f1*(2.002f - atmosphere/100f);
			}

			//RenderSystem.fogStart(near);
			RenderSystem.fogEnd(far);
			RenderSystem.fogDensity(0);
			//event.setCanceled(false);
		}

	}



	//Saves NBT data
	@SubscribeEvent
	public void worldSaveEvent(WorldEvent.Save event) {
		//TODO: save only the one dimension
		if(event.getWorld().func_230315_m_().func_242725_p() == DimensionType.field_242710_a)
			try {
				DimensionManager.getInstance().saveDimensions(DimensionManager.workingPath);
			} catch (Exception e) {
				AdvancedRocketry.logger.fatal("An error has occured saving planet data, this can happen if another mod causes the game to crash during game load.  If the game has fully loaded, then this is a serious error, Advanced Rocketry data has not been saved.");
				e.printStackTrace();
			}
	}


	//Make sure the player doesn't die on low gravity worlds
	@SubscribeEvent
	public void fallEvent(LivingFallEvent event) {
		if(DimensionManager.getInstance().isDimensionCreated(ZUtils.getDimensionIdentifier(event.getEntity().world))) {
			DimensionProperties planet =  DimensionManager.getInstance().getDimensionProperties(ZUtils.getDimensionIdentifier(event.getEntity().world));
			event.setDistance((float) (event.getDistance() * planet.getGravitationalMultiplier(new BlockPos(event.getEntity().getPositionVec()))));
		}
	}
}
