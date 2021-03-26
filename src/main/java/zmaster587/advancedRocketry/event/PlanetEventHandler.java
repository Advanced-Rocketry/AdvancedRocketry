package zmaster587.advancedRocketry.event;

import net.minecraft.block.Block;
import net.minecraft.block.BlockTorch;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ActiveRenderInfo;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayer.SleepResult;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.Teleporter;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.client.event.EntityViewRenderEvent.FogColors;
import net.minecraftforge.client.event.EntityViewRenderEvent.RenderFogEvent;
import net.minecraftforge.event.entity.living.LivingEvent.LivingUpdateEvent;
import net.minecraftforge.event.entity.living.LivingFallEvent;
import net.minecraftforge.event.entity.living.LivingSpawnEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.RightClickBlock;
import net.minecraftforge.event.entity.player.PlayerSleepInBedEvent;
import net.minecraftforge.event.terraingen.OreGenEvent;
import net.minecraftforge.event.terraingen.PopulateChunkEvent;
import net.minecraftforge.event.world.BlockEvent.PlaceEvent;
import net.minecraftforge.event.world.ChunkEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.common.eventhandler.Event.Result;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.common.network.FMLNetworkEvent.ClientConnectedToServerEvent;
import net.minecraftforge.fml.common.network.FMLNetworkEvent.ClientDisconnectionFromServerEvent;
import net.minecraftforge.fml.common.network.FMLNetworkEvent.ServerConnectionFromClientEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import zmaster587.advancedRocketry.AdvancedRocketry;
import zmaster587.advancedRocketry.achievements.ARAdvancements;
import zmaster587.advancedRocketry.api.ARConfiguration;
import zmaster587.advancedRocketry.api.AdvancedRocketryBlocks;
import zmaster587.advancedRocketry.api.AdvancedRocketryItems;
import zmaster587.advancedRocketry.api.IPlanetaryProvider;
import zmaster587.advancedRocketry.api.stations.ISpaceObject;
import zmaster587.advancedRocketry.atmosphere.AtmosphereHandler;
import zmaster587.advancedRocketry.atmosphere.AtmosphereType;
import zmaster587.advancedRocketry.client.render.planet.RenderPlanetarySky;
import zmaster587.advancedRocketry.dimension.DimensionManager;
import zmaster587.advancedRocketry.dimension.DimensionProperties;
import zmaster587.advancedRocketry.network.PacketConfigSync;
import zmaster587.advancedRocketry.network.PacketDimInfo;
import zmaster587.advancedRocketry.network.PacketSpaceStationInfo;
import zmaster587.advancedRocketry.network.PacketStellarInfo;
import zmaster587.advancedRocketry.stations.SpaceObjectManager;
import zmaster587.advancedRocketry.stations.SpaceStationObject;
import zmaster587.advancedRocketry.util.BiomeHandler;
import zmaster587.advancedRocketry.util.GravityHandler;
import zmaster587.advancedRocketry.util.SpawnListEntryNBT;
import zmaster587.advancedRocketry.util.TransitionEntity;
import zmaster587.advancedRocketry.world.ChunkManagerPlanet;
import zmaster587.advancedRocketry.world.provider.WorldProviderPlanet;
import zmaster587.advancedRocketry.world.util.TeleporterNoPortal;
import zmaster587.libVulpes.api.IModularArmor;
import zmaster587.libVulpes.network.PacketHandler;
import zmaster587.libVulpes.util.HashedBlockPosition;

import java.util.Collection;
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
	public void onCrafting(net.minecraftforge.fml.common.gameevent.PlayerEvent.ItemCraftedEvent event) {
		if(event.crafting != null) {
			Item item = event.crafting.getItem();//TODO Advancments for crafting.
			//			if(item == LibVulpesItems.itemHoloProjector) 
			//				event.player.addStat(ARAchivements.holographic);
			//			else if(item == Item.getItemFromBlock(AdvancedRocketryBlocks.blockRollingMachine))
			//				event.player.addStat(ARAchivements.rollin);
			//			else if(item == Item.getItemFromBlock(AdvancedRocketryBlocks.blockCrystallizer))
			//				event.player.addStat(ARAchivements.crystalline);
			//			else if(item == Item.getItemFromBlock(AdvancedRocketryBlocks.blockLathe))
			//				event.player.addStat(ARAchivements.spinDoctor);
			//			else if(item ==Item.getItemFromBlock(AdvancedRocketryBlocks.blockElectrolyser))
			//				event.player.addStat(ARAchivements.electrifying);
			//			else if(item == Item.getItemFromBlock(AdvancedRocketryBlocks.blockArcFurnace))
			//				event.player.addStat(ARAchivements.feelTheHeat);
			//			else if(item == Item.getItemFromBlock(AdvancedRocketryBlocks.blockWarpCore))
			//				event.player.addStat(ARAchivements.warp);
			//			else if(item == Item.getItemFromBlock(AdvancedRocketryBlocks.blockPlatePress))
			//				event.player.addStat(ARAchivements.blockPresser);
		}
	}

	@SubscribeEvent
	public void CheckSpawn(LivingSpawnEvent.CheckSpawn event)
	{
		World world = event.getWorld();
		DimensionManager manager = DimensionManager.getInstance();

		if(manager.isInitialized())
		{
			DimensionProperties properties = manager.getDimensionProperties(world.provider.getDimension());
			if(properties != null) {
				if(!properties.getAtmosphere().isImmune(event.getEntityLiving().getClass()))
					event.setResult(Result.DENY);
			}
		}
	}

	@SubscribeEvent
	public void SpawnEntity(WorldEvent.PotentialSpawns event) {
		World world = event.getWorld();

		DimensionProperties properties = DimensionManager.getInstance().getDimensionProperties(world.provider.getDimension());
		if(properties != null) {
			List<SpawnListEntryNBT> entries = properties.getSpawnListEntries();
			if(!entries.isEmpty() && event.getType() != EnumCreatureType.MONSTER)
				event.getList().addAll(entries);
		}
	}

	@SubscribeEvent
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
	}

	@SubscribeEvent
	public void onPickup(net.minecraftforge.fml.common.gameevent.PlayerEvent.ItemPickupEvent event) {
		if(event.pickedUp != null && !event.pickedUp.getItem().isEmpty()) {

			//TODO pickup advancment
			//			zmaster587.libVulpes.api.material.Material mat = LibVulpes.materialRegistry.getMaterialFromItemStack( event.pickedUp.getEntityItem());
			//			if(mat != null && mat.getUnlocalizedName().contains("Dilithium"))
			//				event.player.addStat(ARAchivements.dilithiumCrystals);
		}
	}

	//Handle gravity
	@SubscribeEvent
	public void playerTick(LivingUpdateEvent event) {

		if(event.getEntity().world.isRemote && event.getEntity().posY > 260 && event.getEntity().posY < 270 && event.getEntity().motionY < -.1) {
			RocketEventHandler.destroyOrbitalTextures(event.getEntity().world);
		}
		if(event.getEntity().isInWater()) {
			if(AtmosphereType.LOWOXYGEN.isImmune(event.getEntityLiving()))
				event.getEntity().setAir(300);
		}

		if(!event.getEntity().world.isRemote && event.getEntity().world.getTotalWorldTime() % 20 ==0 && event.getEntity() instanceof EntityPlayer) {
			if(DimensionManager.getInstance().getDimensionProperties(event.getEntity().world.provider.getDimension()).getName().equals("Luna") && 
					event.getEntity().getPosition().distanceSq(2347,80, 67) < 512 ) {
				ARAdvancements.WENT_TO_THE_MOON.trigger((EntityPlayerMP)event.getEntity());
			}
		}
		if(event.getEntity() instanceof EntityPlayer && event.getEntity().world.provider.getDimension() == ARConfiguration.getCurrentConfig().spaceDimId && SpaceObjectManager.getSpaceManager().getSpaceStationFromBlockCoords(event.getEntity().getPosition()) == null) {
			double distance = 0;
			HashedBlockPosition teleportPosition = null;
			for (ISpaceObject object : SpaceObjectManager.getSpaceManager().getSpaceObjects()) {
				if (object instanceof SpaceStationObject) {
					SpaceStationObject station = ((SpaceStationObject) object);
					double distanceTo = event.getEntity().getPosition().getDistance(station.getSpawnLocation().x, station.getSpawnLocation().y, station.getSpawnLocation().z);
					if (distanceTo > distance) {
						distance = distanceTo;
                        teleportPosition = station.getSpawnLocation();
					}
				}
			}
			if (teleportPosition != null) {
				event.getEntity().sendMessage(new TextComponentString("You wake up on the space station with a lingering feeling that your far-reaching spacewalk was frowned upon by some elder fox deity, and that it would be foolish to attempt so again and to expect different results"));
				event.getEntity().sendMessage(new TextComponentString("Maybe you should think before overstepping clearly logical and absolute boundaries again then deciding it was a good idea and not your fault if things go wrong"));
				event.getEntity().setPositionAndUpdate(teleportPosition.x, teleportPosition.y, teleportPosition.z);
			} else {
				event.getEntity().sendMessage(new TextComponentString("You must be on a space station to be in this dimension, and none have been created!"));
				event.getEntity().getServer().getPlayerList().transferPlayerToDimension((EntityPlayerMP)event.getEntity(), 0, new TeleporterNoPortal( net.minecraftforge.common.DimensionManager.getWorld(0) ));
			}

		}

		//GravityHandler.applyGravity(event.getEntity());
	}

	@SubscribeEvent
	public void sleepEvent(PlayerSleepInBedEvent event) {

		if(event.getEntity().world.provider instanceof WorldProviderPlanet) {
			if (!ARConfiguration.getCurrentConfig().forcePlayerRespawnInSpace && AtmosphereHandler.hasAtmosphereHandler(event.getEntity().world.provider.getDimension()) && 
					!AtmosphereHandler.getOxygenHandler(event.getEntity().world.provider.getDimension()).getAtmosphereType(event.getPos()).isBreathable()) {
				event.setResult(SleepResult.OTHER_PROBLEM);
			}
		}
	}

	@SubscribeEvent
	public void blockPlacedEvent(PlaceEvent event)
	{
		if(!event.getWorld().isRemote  && AtmosphereHandler.getOxygenHandler(event.getWorld().provider.getDimension()) != null &&
				!AtmosphereHandler.getOxygenHandler(event.getWorld().provider.getDimension()).getAtmosphereType(event.getPos()).allowsCombustion()) {

			if(event.getPlacedBlock().getBlock() == Blocks.TORCH) {
				EnumFacing direction = event.getPlacedBlock().getValue(BlockTorch.FACING);
				event.getWorld().setBlockState(event.getPos(), AdvancedRocketryBlocks.blockUnlitTorch.getDefaultState().withProperty(BlockTorch.FACING, direction));
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
		EnumFacing direction = event.getFace();
		if(!event.getWorld().isRemote && direction != null  && event.getEntityPlayer() != null  && AtmosphereHandler.getOxygenHandler(event.getWorld().provider.getDimension()) != null &&
				!AtmosphereHandler.getOxygenHandler(event.getWorld().provider.getDimension()).getAtmosphereType(event.getPos().offset(direction)).allowsCombustion()) {

			if(event.getEntityPlayer().getHeldItem(event.getHand()) != null) {
				if(event.getEntityPlayer().getHeldItem(event.getHand()).getItem() == Items.FLINT_AND_STEEL || event.getEntityPlayer().getHeldItem(event.getHand()).getItem() == Items.FIRE_CHARGE|| event.getEntityPlayer().getHeldItem(event.getHand()).getItem() == Items.BLAZE_POWDER || event.getEntityPlayer().getHeldItem(event.getHand()).getItem() == Items.BLAZE_ROD )
					event.setCanceled(true);
			}
		}

		if(!event.getWorld().isRemote && event.getItemStack() != null && event.getItemStack().getItem() == Item.getItemFromBlock(AdvancedRocketryBlocks.blockGenericSeat) && event.getWorld().getBlockState(event.getPos()).getBlock() == Blocks.TNT) {
			ARAdvancements.BEER.trigger((EntityPlayerMP) event.getEntityPlayer());
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
	public void tick(TickEvent.ServerTickEvent event) {
		//Tick satellites
		if(event.phase == event.phase.END) {
			DimensionManager.getInstance().tickDimensions();
			time++;

			if(!transitionMap.isEmpty()) {
				Iterator<TransitionEntity> itr = transitionMap.iterator();

				while(itr.hasNext()) {
					TransitionEntity ent = itr.next();
					if(ent.entity.world.getTotalWorldTime() >= ent.time) {
						ent.entity.setLocationAndAngles(ent.location.getX(), ent.location.getY(), ent.location.getZ(), ent.entity.rotationYaw, ent.entity.rotationPitch);
						WorldServer newWorld = ent.entity.getServer().getWorld(ent.dimId);
						ent.entity.getServer().getPlayerList().transferPlayerToDimension((EntityPlayerMP)ent.entity, ent.dimId, new TeleporterNoPortal(newWorld));
						//should be loaded by now
						Entity rocket = newWorld.getEntityFromUuid(ent.entity2.getPersistentID());
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

	/*@SubscribeEvent
	public void connectToServer(ClientConnectedToServerEvent event) 
	{
		zmaster587.advancedRocketry.api.ARConfiguration.prevAsteroidTypes = zmaster587.advancedRocketry.api.ARConfiguration.asteroidTypes;
		zmaster587.advancedRocketry.api.ARConfiguration.asteroidTypes = new HashMap<String, AsteroidSmall>();
	}

	@SubscribeEvent
	public void disconnectFromServer(ClientDisconnectionFromServerEvent event)
	{
		zmaster587.advancedRocketry.api.ARConfiguration.asteroidTypes = zmaster587.advancedRocketry.api.ARConfiguration.prevAsteroidTypes;
	}*/


	// Used to save extra biome data
	/*@SubscribeEvent
	public void worldLoadEvent(WorldEvent.Load event) {
		if(event.getWorld().provider instanceof ProviderPlanet && DimensionManager.getInstance().getDimensionProperties(event.getWorld().provider.getDimension()).biomeProperties == null) {
			DimensionManager.getInstance().getDimensionProperties(event.getWorld().provider.getDimension()).biomeProperties = new ExtendedBiomeProperties(event.getWorld());
		}
	}

	// Used to load extra biome data
	@SubscribeEvent
	public void saveExtraData(ChunkDataEvent.Save event) {
		if(event.getWorld().provider instanceof ProviderPlanet) {
			NBTTagCompound nbt = event.getData();

			int xPos = event.getChunk().xPosition;//nbt.getInteger("xPos");
			int zPos = event.getChunk().zPosition;//nbt.getInteger("zPos");

			ChunkProperties properties = DimensionManager.getInstance().getDimensionProperties(event.getWorld().provider.getDimension()).biomeProperties.getChunkPropertiesFromChunkCoords(xPos, zPos);

			nbt.setIntArray("ExtendedBiomeArray", properties.getBlockBiomeArray());
		}
	}

	@SubscribeEvent
	public void loadExtraData(ChunkDataEvent.Load event) {
		if(event.getWorld().provider instanceof ProviderPlanet)  {
			NBTTagCompound nbt = event.getData();


			int xPos = event.getChunk().xPosition;//nbt.getInteger("xPos");
			int zPos = event.getChunk().zPosition;//nbt.getInteger("zPos");
			ChunkProperties properties = DimensionManager.getInstance().getDimensionProperties(event.getWorld().provider.getDimension()).biomeProperties.getChunkPropertiesFromChunkCoords(xPos, zPos);

			properties.setBlockBiomeArray(event.getData().getIntArray("ExtendedBiomeArray"));
		}
	}
	 */

	@SubscribeEvent
	public void worldLoadEvent(WorldEvent.Load event) {
		if(!event.getWorld().isRemote)
			AtmosphereHandler.registerWorld(event.getWorld().provider.getDimension());
		else if(ARConfiguration.getCurrentConfig().skyOverride && event.getWorld().provider.getDimension() == 0)
			event.getWorld().provider.setSkyRenderer(new RenderPlanetarySky());
	}

	@SubscribeEvent
	public void worldUnloadEvent(WorldEvent.Unload event) {
		if(!event.getWorld().isRemote)
			AtmosphereHandler.unregisterWorld(event.getWorld().provider.getDimension());
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
	public void fogColor(FogColors event) {


		IBlockState state = ActiveRenderInfo.getBlockStateAtEntityViewpoint(event.getEntity().world, event.getEntity(), (float)event.getRenderPartialTicks());

		Block block = state.getBlock();
		if(block.getMaterial(state) == Material.WATER)
			return;

		DimensionProperties properties = DimensionManager.getInstance().getDimensionProperties(event.getEntity().dimension);
		if(properties != null) {
			float fog = Math.min(properties.getAtmosphereDensityAtHeight(event.getEntity().posY), 200);

			if(event.getEntity().world.provider instanceof IPlanetaryProvider) {
				Vec3d color = event.getEntity().world.provider.getSkyColor(event.getEntity(), 0f);
				event.setRed((float) Math.min(color.x*1.4f,1f));
				event.setGreen((float) Math.min(color.y*1.4f, 1f));
				event.setBlue((float) Math.min(color.z*1.4f, 1f));
			}

			if(endTime > 0) {
				double amt = (endTime - Minecraft.getMinecraft().world.getTotalWorldTime()) / (double)duration;
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

			if(DimensionManager.getInstance().getDimensionProperties(event.world.provider.getDimension()).isTerraformed()) {
				Collection<Chunk> list = ((WorldServer)event.world).getChunkProvider().getLoadedChunks();
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
	@SideOnly(Side.CLIENT)
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
			ItemStack armor = Minecraft.getMinecraft().player.getItemStackFromSlot(EntityEquipmentSlot.HEAD);

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
		if(event.getEntity().world.provider instanceof IPlanetaryProvider) {
			IPlanetaryProvider planet = (IPlanetaryProvider)event.getEntity().world.provider;
			event.setDistance((float) (event.getDistance() * planet.getGravitationalMultiplier(event.getEntity().getPosition())));
		}
	}
}
