package zmaster587.advancedRocketry.command;

import net.minecraft.block.Block;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.common.MinecraftForge;
import zmaster587.advancedRocketry.AdvancedRocketry;
import zmaster587.advancedRocketry.api.AdvancedRocketryAPI;
import zmaster587.advancedRocketry.api.AdvancedRocketryItems;
import zmaster587.advancedRocketry.api.ARConfiguration;
import zmaster587.advancedRocketry.api.DataStorage.DataType;
import zmaster587.advancedRocketry.api.dimension.IDimensionProperties;
import zmaster587.advancedRocketry.api.dimension.solar.StellarBody;
import zmaster587.advancedRocketry.api.stations.ISpaceObject;
import zmaster587.advancedRocketry.dimension.DimensionManager;
import zmaster587.advancedRocketry.dimension.DimensionProperties;
import zmaster587.advancedRocketry.integration.CompatibilityMgr;
import zmaster587.advancedRocketry.item.ItemData;
import zmaster587.advancedRocketry.item.ItemMultiData;
import zmaster587.advancedRocketry.item.ItemStationChip;
import zmaster587.advancedRocketry.network.PacketDimInfo;
import zmaster587.advancedRocketry.network.PacketStellarInfo;
import zmaster587.advancedRocketry.stations.SpaceObjectManager;
import zmaster587.advancedRocketry.unit.IngameTestOrchestrator;
import zmaster587.advancedRocketry.world.util.TeleporterNoPortal;
import zmaster587.advancedRocketry.world.util.TeleporterNoPortalSeekBlock;
import zmaster587.libVulpes.network.PacketHandler;
import zmaster587.libVulpes.util.HashedBlockPosition;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class WorldCommand implements ICommand {



	private List<String> aliases;
	public WorldCommand() {
		aliases = new ArrayList<String>();
		aliases.add("advancedrocketry");
		aliases.add("advrocketry");
		aliases.add("ar");
	}

	@Override
	public String getName() {
		return "advancedrocketry";
	}

	@Override
	public String getUsage(ICommandSender sender) {
		return "advancedrocketry help";
	}

	@Override
	public List<String> getAliases() {
		return aliases;
	}

	private void commandAddTorch(ICommandSender sender, String cmdstring[]) {
		
		if(cmdstring.length >= 2 && cmdstring[1].equalsIgnoreCase("help"))
		{
			sender.sendMessage(new TextComponentString( aliases.get(0) + " " + cmdstring[0] +  " - Adds the currently held block to the list of objects that drop when there's no atmosphere"));
			return;
		}
		
		Entity player = sender.getCommandSenderEntity();
		if(!(player instanceof EntityPlayer)) {
			sender.sendMessage(new TextComponentString("Not a player entity"));
			return;
		}
		
		Block block = Block.getBlockFromItem(((EntityPlayer)player).getHeldItemMainhand().getItem());
		if (block != Blocks.AIR)
		{
			if(ARConfiguration.getCurrentConfig().torchBlocks.contains(block) )
				sender.sendMessage(new TextComponentString(block.getLocalizedName() + " is already in the torch list"));
			else
			{
				
				ARConfiguration.getCurrentConfig().addTorchblock(block);
				
				sender.sendMessage(new TextComponentString(block.getLocalizedName() + " added to the torch list"));
			}
		}
		else
			sender.sendMessage(new TextComponentString("Held block cannot be added to torch list"));
	}
	
	private void commandAddSolidBlockOverride(ICommandSender sender, String cmdstring[]) {
		if(cmdstring.length >= 2 && cmdstring[1].equalsIgnoreCase("help"))
		{
			sender.sendMessage(new TextComponentString( aliases.get(0) + " " + cmdstring[0] +  " - Adds the currently held block to the list of blocks that can hold a seal"));
			return;
		}
		
		Entity player = sender.getCommandSenderEntity();
		if(!(player instanceof EntityPlayer)) {
			sender.sendMessage(new TextComponentString("Not a player entity"));
			return;
		}
		
		Block block = Block.getBlockFromItem(((EntityPlayer)player).getHeldItemMainhand().getItem());
		if (block != Blocks.AIR)
		{
			if(ARConfiguration.getCurrentConfig(). torchBlocks.contains(block) )
				sender.sendMessage(new TextComponentString(block.getLocalizedName() + " is already in the sealed blocks list"));
			else
			{
				
				ARConfiguration.getCurrentConfig().addSealedBlock(block);
				
				sender.sendMessage(new TextComponentString(block.getLocalizedName() + " added to the sealed block list"));
			}
		}
		else
			sender.sendMessage(new TextComponentString("Held block cannot be added to sealed block list"));
	}
	
	private void commandGiveStation(ICommandSender sender, String cmdstring[])
	{
		if(cmdstring.length < 2 || (cmdstring.length >= 2 && cmdstring[1].equalsIgnoreCase("help")))
		{
			sender.sendMessage(new TextComponentString(aliases.get(0) + " " + cmdstring[0] +  " - Gives the player playerName (if supplied) a spacestation with ID stationID"));
			sender.sendMessage(new TextComponentString("Usage: /advrocketry " + cmdstring[0] + " <stationId> [PlayerName]"));
			return;
		}
		
		EntityPlayer player = null;
		if(cmdstring.length >= 3) {
			player = getPlayerByName(cmdstring[2]);
			if(player == null) {
				sender.sendMessage(new TextComponentString("Player " + cmdstring[2] + " not found"));
				return;
			}
		}
		else if(sender.getCommandSenderEntity() != null)
			player = ((EntityPlayer)sender);
	
		if(cmdstring.length >= 2 && player != null) {
			int stationId = Integer.parseInt(cmdstring[1]);
			ItemStack stack = new ItemStack(AdvancedRocketryItems.itemSpaceStationChip);
			ItemStationChip.setUUID(stack, stationId);
			player.inventory.addItemStackToInventory(stack);
		}
		else
			sender.sendMessage(new TextComponentString("Usage: /advrocketry " + cmdstring[0] + " <stationId> [PlayerName]"));
	}
	
	private void commandFillData(ICommandSender sender, String cmdstring[])
	{
		if(cmdstring.length < 2)
			return;
		
		ItemStack stack;
		if(sender.getCommandSenderEntity() != null ) {
			stack = ((EntityPlayer)sender.getCommandSenderEntity()).getHeldItem(EnumHand.MAIN_HAND);

			if(cmdstring.length < 2 || (cmdstring.length >= 2 && cmdstring[1].equalsIgnoreCase("help"))) {
				sender.sendMessage(new TextComponentString(aliases.get(0) + " " + cmdstring[0] + " [datatype] [amountFill]\n"));
				sender.sendMessage(new TextComponentString("Fills the amount of the data type specifies into the chip being held."));
				sender.sendMessage(new TextComponentString("If the datatype is not specified then command fills all datatypes, if no amountFill is specified completely fills the chip"));
				return;
			}

			if(stack != null && stack.getItem() instanceof ItemData) {
				ItemData item = (ItemData) stack.getItem();
				int dataAmount = item.getMaxData(stack.getItemDamage());
				DataType dataType = null;

				if(cmdstring.length >= 2) {
					try {
						dataType = DataType.valueOf(cmdstring[1].toUpperCase(Locale.ENGLISH));
					} catch (IllegalArgumentException e) {
						sender.sendMessage(new TextComponentString("Did you mean: /advrocketry" + cmdstring[0] + " [datatype] [amountFill]"));
						sender.sendMessage(new TextComponentString("Not a valid datatype"));
						String value = "";
						for(DataType data : DataType.values())
							if(!data.name().equals("UNDEFINED"))
								value += data.name().toLowerCase() + ", ";

						sender.sendMessage(new TextComponentString("Try " + value));

						return;
					}
				}
				if(cmdstring.length >= 3)
					try {
						dataAmount = Integer.parseInt(cmdstring[2]);
					} catch(NumberFormatException e) {
						sender.sendMessage(new TextComponentString("Did you mean: /advrocketry" + cmdstring[0] + " [datatype] [amountFill]"));
						sender.sendMessage(new TextComponentString("Not a valid number"));
						return;
					}

				if(dataType != null)
					item.setData(stack, dataAmount, dataType);
				else
				{
					for(DataType type : DataType.values())
						item.setData(stack, dataAmount, type);
				}
				sender.sendMessage(new TextComponentString("Data filled!"));
			}
			else if(stack != null && stack.getItem() instanceof ItemMultiData) {
				ItemMultiData item = (ItemMultiData) stack.getItem();
				int dataAmount = item.getMaxData(stack);
				DataType dataType = null;

				if(cmdstring.length >= 2) {
					try {
						dataType = DataType.valueOf(cmdstring[1].toUpperCase(Locale.ENGLISH));
					} catch (IllegalArgumentException e) {
						sender.sendMessage(new TextComponentString("Did you mean: /advrocketry" + cmdstring[0] + " [datatype] [amountFill]"));
						sender.sendMessage(new TextComponentString("Not a valid datatype"));
						String value = "";
						for(DataType data : DataType.values())
							if(!data.name().equals("UNDEFINED"))
								value += data.name().toLowerCase() + ", ";

						sender.sendMessage(new TextComponentString("Try " + value));
						return;
					}
				}
				if(cmdstring.length >= 3)
					try {
						dataAmount = Integer.parseInt(cmdstring[2]);
					} catch(NumberFormatException e) {
						sender.sendMessage(new TextComponentString("Did you mean: /advrocketry" + cmdstring[0] + " [datatype] [amountFill]"));
						sender.sendMessage(new TextComponentString("Not a valid number"));
						return;
					}

				if(dataType != null)
					item.setData(stack, dataAmount, dataType);
				else
				{
					for(DataType type : DataType.values())
						item.setData(stack, dataAmount, type);
				}

				sender.sendMessage(new TextComponentString("Data filled!"));
			}
			else
				sender.sendMessage(new TextComponentString("Not Holding data item"));
		}
		else
			sender.sendMessage(new TextComponentString("Ghosts don't have items!"));
	}
	
	
	private void commandReloadRecipes(ICommandSender sender, String cmdstring[])
	{
		if(cmdstring.length >= 2 && cmdstring[1].equalsIgnoreCase("help")) {
			sender.sendMessage(new TextComponentString(aliases.get(0) + " " + cmdstring[0] + " - Reloads recipes from the XML files in the config folder"));
			return;
		}
		
		try {
			AdvancedRocketry.machineRecipes.clearAllMachineRecipes();
			AdvancedRocketry.machineRecipes.registerAllMachineRecipes();
			AdvancedRocketry.machineRecipes.createAutoGennedRecipes(AdvancedRocketry.modProducts);
			AdvancedRocketry.machineRecipes.registerXMLRecipes();

			sender.sendMessage(new TextComponentString("Recipes Reloaded"));

			CompatibilityMgr.reloadRecipes();
		} catch (Exception e) {
			sender.sendMessage(new TextComponentString("Serious error has occured!  Possible recipe corruption"));
			sender.sendMessage(new TextComponentString("Please check logs!"));
			sender.sendMessage(new TextComponentString("You may be able to recify this error by repairing the XML and/or"));
			sender.sendMessage(new TextComponentString("restarting the game"));
		}
	}
	
	private void commandSetGravity(ICommandSender sender, String cmdstring[])
	{
		if(cmdstring.length >= 2) {
			if( cmdstring[1].equalsIgnoreCase("help")) {
				sender.sendMessage(new TextComponentString(cmdstring[0] + " <amount> - sets your gravity to amount where 1 is earthlike"));
				return;
			}
			if(sender instanceof Entity) {
				Entity player = null;
				if(cmdstring.length > 2)
					player = sender.getServer().getPlayerList().getPlayerByUsername(cmdstring[2]);
				else
					player = (Entity) sender;
				if(player != null) {
					try {
						double d = Double.parseDouble(cmdstring[1]);
						if(d == 0)
							AdvancedRocketryAPI.gravityManager.clearGravityEffect(player);
						else
							AdvancedRocketryAPI.gravityManager.setGravityMultiplier((Entity) sender, d);
					} catch(NumberFormatException e) {
						sender.sendMessage(new TextComponentString(cmdstring[1] + " is not a valid number"));
					}
				} else {
					sender.sendMessage(new TextComponentString("Not a valid player"));
				}
			} else {
				sender.sendMessage(new TextComponentString("Not a valid player"));
			}
		}
		else {
			sender.sendMessage(new TextComponentString(aliases.get(0) + " " + cmdstring[0] + " gravity_multiplier [playerName]"));
			sender.sendMessage(new TextComponentString(""));
			sender.sendMessage(new TextComponentString("use 0 as the gravity_multiplier to allow regular planet gravity to take over"));
		}
	}
	
	private void commandGoto(ICommandSender sender, String cmdstring[])
	{
		EntityPlayer player;
		if(sender instanceof Entity && (player = sender.getEntityWorld().getPlayerEntityByName(sender.getName())) != null) {
			if(cmdstring.length < 2 || (cmdstring.length >= 2 && cmdstring[1].equalsIgnoreCase("help"))) {
				sender.sendMessage(new TextComponentString(cmdstring[0] + " <dimId> - teleports the player to the supplied dimension"));
				sender.sendMessage(new TextComponentString(cmdstring[0] + " station <station ID> - teleports the player to the supplied station"));
				return;
			}
			try {
				int dim;

				if(cmdstring.length == 2) {
					dim = Integer.parseInt(cmdstring[1]);
					if(net.minecraftforge.common.DimensionManager.isDimensionRegistered(dim))
					{
						if(net.minecraftforge.common.DimensionManager.getWorld(dim) == null) {
							net.minecraftforge.common.DimensionManager.initDimension(dim);
						}
						player.getServer().getPlayerList().transferPlayerToDimension((EntityPlayerMP) player,  dim , new TeleporterNoPortalSeekBlock((WorldServer) net.minecraftforge.common.DimensionManager.getWorld(dim)));
					}
					else
						sender.sendMessage(new TextComponentString("Dimension does not exist"));
				}
				else if(cmdstring[1].equalsIgnoreCase("station")) {
					dim = ARConfiguration.getCurrentConfig().spaceDimId;
					int stationId = Integer.parseInt(cmdstring[2]);
					ISpaceObject object = SpaceObjectManager.getSpaceManager().getSpaceStation(stationId);

					if(object != null) {
						if(player.world.provider.getDimension() != ARConfiguration.getCurrentConfig().spaceDimId)
							player.getServer().getPlayerList().transferPlayerToDimension((EntityPlayerMP) player,  dim , new TeleporterNoPortal((WorldServer)player.world));
						HashedBlockPosition vec = object.getSpawnLocation();
						player.setPositionAndUpdate(vec.x, vec.y, vec.z);
					}
					else {
						sender.sendMessage(new TextComponentString("Station " + stationId + " does not exist!"));
					}
				}


			} catch(NumberFormatException e) {
				sender.sendMessage(new TextComponentString(cmdstring[0] + " <dimId>"));
				sender.sendMessage(new TextComponentString(cmdstring[0] + "station <station ID>"));
			}
		}					
		else 
			sender.sendMessage(new TextComponentString("Must be a player to use this command"));
	}
	
	private void commandFetch(ICommandSender sender, String cmdstring[])
	{
		if(cmdstring.length < 2)
			return;
		
		EntityPlayer me = (EntityPlayer) sender.getCommandSenderEntity();
		EntityPlayer player = getPlayerByName(cmdstring[1]);
		System.out.println(cmdstring[1] + "   " + sender.getCommandSenderEntity());

		if(player == null) {
			sender.sendMessage(new TextComponentString("Invalid player name: " + cmdstring[1]));
		}
		else {
			player.getServer().getPlayerList().transferPlayerToDimension((EntityPlayerMP) player,  me.world.provider.getDimension() , new TeleporterNoPortal(me.getServer().getWorld(me.world.provider.getDimension())));
			player.setPosition(me.posX, me.posY, me.posZ);
		}
	}
	
	private void commandPlanetList(ICommandSender sender, String cmdstring[])
	{
		sender.sendMessage(new TextComponentString("Dimensions:"));
		for(int i : DimensionManager.getInstance().getRegisteredDimensions()) {
			sender.sendMessage(new TextComponentString("DIM" + i + ":  " + DimensionManager.getInstance().getDimensionProperties(i).getName())); 
		}
	}
	
	private void commandPlanetHelp(ICommandSender sender, String cmdstring[])
	{
		sender.sendMessage(new TextComponentString("Planet:"));
		sender.sendMessage(new TextComponentString("planet delete [dimid]"));
		sender.sendMessage(new TextComponentString("planet generate [starId] (moon/gas) [name] [atmosphere randomness] [distance Randomness] [gravity randomness] (atmosphere base) (distance base) (gravity base)"));
		sender.sendMessage(new TextComponentString("planet list"));
		sender.sendMessage(new TextComponentString("planet reset [dimid]"));
		sender.sendMessage(new TextComponentString("planet set [property]"));
		sender.sendMessage(new TextComponentString("planet get [property]"));
	}
	
	private void commandPlanetReset(ICommandSender sender, String cmdstring[])
	{
		int dimId;
		if(cmdstring.length == 3) {
			try {
				dimId = Integer.parseInt(cmdstring[2]);
				DimensionManager.getInstance().getDimensionProperties(dimId).resetProperties();
				PacketHandler.sendToAll(new PacketDimInfo(dimId, DimensionManager.getInstance().getDimensionProperties(dimId)));
			} catch (NumberFormatException e) {
				sender.sendMessage(new TextComponentString("Invalid dimId"));
			}
		}
		else if(cmdstring.length == 2) {
			if(sender.getCommandSenderEntity() != null) {
				if(DimensionManager.getInstance().isDimensionCreated((dimId = sender.getEntityWorld().provider.getDimension()))) {
					DimensionManager.getInstance().getDimensionProperties(dimId).resetProperties();
					PacketHandler.sendToAll(new PacketDimInfo(dimId, DimensionManager.getInstance().getDimensionProperties(dimId)));
				}
			}
			else {
				sender.sendMessage(new TextComponentString("Please specify dimension ID"));
			}
		}
	}
	
	private void commandPlanetDelete(ICommandSender sender, String cmdstring[])
	{
		if(cmdstring.length == 3) {
			int deletedDimId;
			try {
				deletedDimId = Integer.parseInt(cmdstring[2]);

				if(DimensionManager.getInstance().isDimensionCreated(deletedDimId)) {

					if(net.minecraftforge.common.DimensionManager.getWorld(deletedDimId) == null || net.minecraftforge.common.DimensionManager.getWorld(deletedDimId).playerEntities.isEmpty()) {
						DimensionManager.getInstance().deleteDimension(deletedDimId);
						PacketHandler.sendToAll(new PacketDimInfo(deletedDimId, null));
						sender.sendMessage(new TextComponentString("Dim " + deletedDimId + " deleted!"));
					}
					else {
						//If the world still has players abort and list players
						sender.sendMessage(new TextComponentString("World still has players:"));

						for(EntityPlayer player : (List<EntityPlayer>)net.minecraftforge.common.DimensionManager.getWorld(deletedDimId).playerEntities) {
							sender.sendMessage(player.getDisplayName());
						}

					}


				} else {
					sender.sendMessage(new TextComponentString("Dimension does not exist"));
				}

			} catch(NumberFormatException exception) {
				sender.sendMessage(new TextComponentString("Invalid Argument"));
			}
		}
		else {
			sender.sendMessage(new TextComponentString(cmdstring[0] + " " + cmdstring[1] + " " + cmdstring[2] + " <dimid>"));
		}
	}
	
	private void commandPlanetGenerate(ICommandSender sender, String cmdstring[])
	{
		int gasOffset = 0;
		boolean gassy = false;
		boolean moon = false;
		int starId = 0;

		if(cmdstring.length > 2 ) {
			try {
				starId = Integer.parseInt(cmdstring[2]);
				gasOffset++;

			} catch(NumberFormatException e) {

			}
		}

		if(cmdstring.length > 2 + gasOffset) {
			if(cmdstring[2 + gasOffset].equalsIgnoreCase("moon")) {
				gasOffset++;
				moon = true;

				if(!DimensionManager.getInstance().isDimensionCreated(starId)) {
					sender.sendMessage(new TextComponentString("Invalid planet ID"));
					sender.sendMessage(new TextComponentString(cmdstring[0] + " " + cmdstring[1] + "[planetId] [moon] [gas] <name> <atmosphereRandomness> <distanceRandomness> <gravityRandomness>"));

					return;
				}
			}
			else if(DimensionManager.getInstance().getStar(starId) == null) {
				sender.sendMessage(new TextComponentString("Invalid star ID"));
				sender.sendMessage(new TextComponentString(cmdstring[0] + " " + cmdstring[1] + "[starId] [gas] <name> <atmosphereRandomness> <distanceRandomness> <gravityRandomness>"));

				return;
			}
		}

		if(cmdstring.length > 2 + gasOffset && cmdstring[2 + gasOffset].equalsIgnoreCase("gas")) {
			gasOffset++;
			gassy = true;
		}

		try {
			//Advancedrocketry planet generate <name> <atmosphereRandomness> <distanceRandomness> <gravityRandomness>
			if(cmdstring.length == 6 + gasOffset) {

				int planetId = starId;
				if(moon)
					starId = DimensionManager.getInstance().getDimensionProperties(planetId).getStarId();

				DimensionProperties properties;
				if(!gassy)
					properties = DimensionManager.getInstance().generateRandom(starId, cmdstring[2 + gasOffset], Integer.parseInt(cmdstring[3 + gasOffset]), Integer.parseInt(cmdstring[4 + gasOffset]), Integer.parseInt(cmdstring[5 + gasOffset]));
				else
					properties = DimensionManager.getInstance().generateRandomGasGiant(starId, cmdstring[2 + gasOffset], Integer.parseInt(cmdstring[3 + gasOffset]), Integer.parseInt(cmdstring[4 + gasOffset]), Integer.parseInt(cmdstring[5 + gasOffset]),1,1,1);

				if(properties == null)
					sender.sendMessage(new TextComponentString("Dimension: " + cmdstring[2 + gasOffset] + " failed to generate!"));
				else
					sender.sendMessage(new TextComponentString("Dimension: " + cmdstring[2 + gasOffset] + " Generated!"));

				if(moon) {
					properties.setParentPlanet(DimensionManager.getInstance().getDimensionProperties(planetId));
					DimensionManager.getInstance().getStar(starId).removePlanet(properties);
				}

				sender.sendMessage(new TextComponentString("Dimension Generated!"));
			}
			else if(cmdstring.length == 9  + gasOffset) {

				int planetId = starId;
				if(moon)
					starId = DimensionManager.getInstance().getDimensionProperties(planetId).getStarId();

				DimensionProperties properties;

				if(!gassy)
					properties = DimensionManager.getInstance().generateRandom(starId,cmdstring[2 + gasOffset] ,Integer.parseInt(cmdstring[3 + gasOffset]), Integer.parseInt(cmdstring[4 + gasOffset]), Integer.parseInt(cmdstring[5 + gasOffset]),Integer.parseInt(cmdstring[6 + gasOffset]), Integer.parseInt(cmdstring[7 + gasOffset]), Integer.parseInt(cmdstring[8 + gasOffset]));
				else
					properties = DimensionManager.getInstance().generateRandomGasGiant(starId, cmdstring[2 + gasOffset] ,Integer.parseInt(cmdstring[3 + gasOffset]), Integer.parseInt(cmdstring[4 + gasOffset]), Integer.parseInt(cmdstring[5 + gasOffset]),Integer.parseInt(cmdstring[6 + gasOffset]), Integer.parseInt(cmdstring[7 + gasOffset]), Integer.parseInt(cmdstring[8 + gasOffset]));

				if(properties == null)
					sender.sendMessage(new TextComponentString("Dimension: " + cmdstring[2 + gasOffset] + " failed to generate!"));
				else
					sender.sendMessage(new TextComponentString("Dimension: " + cmdstring[2 + gasOffset] + " Generated!"));

				if(moon) {
					properties.setParentPlanet(DimensionManager.getInstance().getDimensionProperties(planetId));
					DimensionManager.getInstance().getStar(starId).removePlanet(properties);
				}
			}
			else {
				sender.sendMessage(new TextComponentString(cmdstring[0] + " " + cmdstring[1] + " [starId] [moon] [gas] <name> <atmosphereRandomness> <distanceRandomness> <gravityRandomness>"));
				sender.sendMessage(new TextComponentString(""));
				sender.sendMessage(new TextComponentString(cmdstring[0] + " " + cmdstring[1] + " [starId] [moon] [gas] <name> <atmosphere base value> <distance base value> <gravity base value> <atmosphereRandomness> <distanceRandomness> <gravityRandomness>"));
			}
		} catch(NumberFormatException e) {
			sender.sendMessage(new TextComponentString(cmdstring[0] + " " + cmdstring[1] + " [starId] [moon] [gas] <name> <atmosphereRandomness> <distanceRandomness> <gravityRandomness>"));
			sender.sendMessage(new TextComponentString(""));
			sender.sendMessage(new TextComponentString(cmdstring[0] + " " + cmdstring[1] + " [starId] [moon] [gas] <name> <atmosphere base value> <distance base value> <gravity base value> <atmosphereRandomness> <distanceRandomness> <gravityRandomness>"));
		}
	}
	
	private void commandPlanetSet(ICommandSender sender, String cmdstring[])
	{
		if(cmdstring.length < 3)
			return;
		
		int dimId;
		if( !DimensionManager.getInstance().isDimensionCreated((dimId = sender.getEntityWorld().provider.getDimension())))
			return;

		int commandOffset = 0;
		
		if(cmdstring.length > 3) {
			try {
				dimId = Integer.parseInt(cmdstring[2]);
				commandOffset = 1;
			} 
			catch (NumberFormatException e) {
				//XXX: Ugh... ordering sucks, what properties does the dimension class have if you don't know what dim it is yet?
				//sender.sendMessage(new TextComponentString("Invalid Property or Dimension"));
			}
		}

		if(!DimensionManager.getInstance().isDimensionCreated(dimId)) {
			sender.sendMessage(new TextComponentString("Invalid Dimensions"));
			return;
		}

		DimensionProperties properties = DimensionManager.getInstance().getDimensionProperties(dimId);
		try {
			if(cmdstring[2 + commandOffset].equalsIgnoreCase("atmosphereDensity")) {
				properties.setAtmosphereDensityDirect(Integer.parseUnsignedInt(cmdstring[3 + commandOffset]));
				sender.sendMessage(new TextComponentString("Setting " + cmdstring[2 + commandOffset] + " for dimension " + dimId + " to " + cmdstring[3 + commandOffset]));
				PacketHandler.sendToAll(new PacketDimInfo(dimId, properties));
			}
			else {

				Field field = properties.getClass().getDeclaredField(cmdstring[2 + commandOffset]);

				if(field.getType().isArray()) {

					if(Float.TYPE == field.getType().getComponentType()) {
						float var[] = (float[])field.get(properties);

						if(cmdstring.length - 3 - commandOffset == var.length) {

							//Make sure we catch if some invalid arg is entered
							String outString = "";
							for(int i = 0; i < var.length; i++) {
								var[i] = Float.parseFloat(cmdstring[3+i + commandOffset]);
								outString = outString + cmdstring[3+i + commandOffset] + " ";
							}

							field.set(properties, var);
							sender.sendMessage(new TextComponentString("Setting " + cmdstring[2 + commandOffset] + " for dimension " + dimId + " to " + outString));
						}
					}

					if(Integer.TYPE == field.getType().getComponentType()) {
						int var[] = (int[])field.get(properties);

						if(cmdstring.length - 3 - commandOffset == var.length) {

							//Make sure we catch if some invalid arg is entered
							String outString = "";
							for(int i = 0; i < var.length; i++) {
								var[i] = Integer.parseInt(cmdstring[3+i + commandOffset]);
								outString = outString + cmdstring[3+i + commandOffset] + " ";
							}

							field.set(properties, var);
							sender.sendMessage(new TextComponentString("Setting " + cmdstring[2 + commandOffset] + " for dimension " + dimId + " to " + outString));
						}
					}
				}
				else {
					if(Integer.TYPE == field.getType() )
						field.set(properties, Integer.parseInt(cmdstring[3 + commandOffset]));
					else if(Float.TYPE == field.getType())
						field.set(properties, Float.parseFloat(cmdstring[3 + commandOffset]));
					else if(Double.TYPE == field.getType()) 
						field.set(properties, Double.parseDouble(cmdstring[3 + commandOffset]));
					else if(Boolean.TYPE == field.getType())
						field.set(properties, Boolean.parseBoolean(cmdstring[3 + commandOffset]));
					else
						field.set(properties, cmdstring[3 + commandOffset]);
					sender.sendMessage(new TextComponentString("Setting " + cmdstring[2 + commandOffset] + " for dimension " + dimId + " to " + cmdstring[3 + commandOffset]));
				}
				
				PacketHandler.sendToAll(new PacketDimInfo(dimId, properties));
				return;
			}
		} catch (NumberFormatException e) {
			sender.sendMessage(new TextComponentString("Invalid Argument for parameter " + cmdstring[2 + commandOffset]));
		} catch (Exception e) {

			e.printStackTrace();
		}
	}
	
	private void commandPlanetGet(ICommandSender sender, String cmdstring[])
	{
		if (cmdstring.length < 3)
			return;
		
		int dimId;
		if( !DimensionManager.getInstance().isDimensionCreated((dimId = sender.getEntityWorld().provider.getDimension())))
			return;
		int commandOffset = 0;
		if(cmdstring.length > 3) {
			try {
				dimId = Integer.parseInt(cmdstring[2]);
				commandOffset = 1;
			} 
			catch (NumberFormatException e) {
				sender.sendMessage(new TextComponentString("Invalid Dimensions"));
			}
		}

		if(!DimensionManager.getInstance().isDimensionCreated(dimId)) {
			sender.sendMessage(new TextComponentString("Invalid Dimensions"));
			return;
		}

		DimensionProperties properties = DimensionManager.getInstance().getDimensionProperties(dimId);
		if(cmdstring[2 + commandOffset].equalsIgnoreCase("atmosphereDensity")) {
			sender.sendMessage(new TextComponentString(Integer.toString(properties.getAtmosphereDensity())));
		} 
		else {
			try {
				Field field = properties.getClass().getDeclaredField(cmdstring[2 + commandOffset]);

				sender.sendMessage(new TextComponentString(field.get(properties).toString()));

			} catch (Exception e) {

				e.printStackTrace();
				sender.sendMessage(new TextComponentString("An error has occured, please check logs"));
			}
		}
	}
	
	private void commandStarGet(ICommandSender sender, String cmdstring[])
	{
		try {
			int id = Integer.parseInt(cmdstring[3]);
			StellarBody star =  DimensionManager.getInstance().getStar(id);
			if(star == null)
				sender.sendMessage(new TextComponentString("Error: " + cmdstring[3] + " is not a valid star ID"));
			else {
				if(cmdstring[2].equalsIgnoreCase("temp")) {
					sender.sendMessage(new TextComponentString("Temp: " + star.getTemperature()));
				}
				else if(cmdstring[2].equalsIgnoreCase("planets")) {
					sender.sendMessage(new TextComponentString("Planets orbiting the star:"));
					for(IDimensionProperties planets : star.getPlanets()) {
						sender.sendMessage(new TextComponentString("ID: " + planets.getId() + " : " + planets.getName()));
					}
				}
				else if(cmdstring[2].equalsIgnoreCase("pos")) {
					sender.sendMessage(new TextComponentString("Pos: " + star.getPosX() + "," + star.getPosZ()));
				}
			}// end star existance validation
		} catch (NumberFormatException e) {
			sender.sendMessage(new TextComponentString("Error: " + cmdstring[3] + " is not a valid star ID"));
		}
	}
	
	private void commandStarSet(ICommandSender sender, String cmdstring[])
	{
		try {
			int id = Integer.parseInt(cmdstring[3]);
			StellarBody star =  DimensionManager.getInstance().getStar(id);
			if(star == null)
				sender.sendMessage(new TextComponentString("Error: " + cmdstring[3] + " is not a valid star ID"));
			else {
				if(cmdstring[2].equalsIgnoreCase("temp")) {
					try {
						star.setTemperature(Integer.parseInt(cmdstring[4]));
						sender.sendMessage(new TextComponentString("Temp set to " + star.getTemperature()));
					} catch(NumberFormatException e) {
						sender.sendMessage(new TextComponentString("star set temp <starId> <temp>"));
					}
				} else if(cmdstring.length > 5 && cmdstring[2].equalsIgnoreCase("pos")) {
					try {
						int x= Integer.parseInt(cmdstring[4]);
						int z = Integer.parseInt(cmdstring[5]);
						star.setPosX(x);
						star.setPosZ(z);
						sender.sendMessage(new TextComponentString("Position set to " + x + "," + z));
					} catch(NumberFormatException e) {
						sender.sendMessage(new TextComponentString("star set pos <starId> <x> <y>"));
					}
				}
			}// end star existance validation
		} catch (NumberFormatException e) {
			sender.sendMessage(new TextComponentString("Error: " + cmdstring[3] + " is not a valid star ID"));
		}
	}
	
	private void commandBiomeDump(ICommandSender sender, String cmdstring[])
	{
		
		if(cmdstring.length >= 2 && cmdstring[1].compareToIgnoreCase("help") == 0)
		{
			sender.sendMessage(new TextComponentString("Developer command: Dumps biome info to BiomeDump.txt!"));
			return;
		}
		
		try {
			File file = new File("./BiomeDump.txt");
			if(!file.exists())
				file.createNewFile();

			BufferedWriter writer = new BufferedWriter(new FileWriter(file));
			
			writer.append("ID\tResource name\n");
			for(ResourceLocation resource : Biome.REGISTRY.getKeys()) {
				writer.append(Biome.getIdForBiome(Biome.REGISTRY.getObject(resource)) + "\t" + resource.toString() + "\n");
			}
			
			writer.close();
			sender.sendMessage(new TextComponentString("The File \"BiomeDump.txt\" has been written to the current directory"));
		} 
		catch(Exception e) {
			sender.sendMessage(new TextComponentString("An error has occured writing to the file"));
		}
	}
	
	private void commandStarGenerate(ICommandSender sender, String cmdstring[])
	{
		try {
			String name = cmdstring[2];
			int temp = Integer.parseInt(cmdstring[3]);
			int x = Integer.parseInt(cmdstring[4]);
			int z = Integer.parseInt(cmdstring[5]);
			StellarBody star = new StellarBody();
			star.setTemperature(temp);
			star.setPosX(x);
			star.setPosZ(z);
			star.setName(name);
			star.setId(DimensionManager.getInstance().getNextFreeStarId());
			if(star.getId() != -1) {
				DimensionManager.getInstance().addStar(star);
				PacketHandler.sendToAll(new PacketStellarInfo(star.getId(), star));
				sender.sendMessage(new TextComponentString("star Added!"));
			}
			else
				sender.sendMessage(new TextComponentString("Why can't I hold all these stars! (either you have an insane number of stars or something really broke!)"));

		} catch(NumberFormatException e) {
			sender.sendMessage(new TextComponentString("star generate <name> <temp> <x> <y>"));
		}
	}
	
	private void commandBeginTest(ICommandSender sender, String cmdstring[])
	{
		if(cmdstring.length >= 2 && cmdstring[1].compareToIgnoreCase("help") == 0)
		{
			sender.sendMessage(new TextComponentString("Developer command: Runs system tests, debug only!"));
			return;
		}
		if(sender.getCommandSenderEntity() != null)
		{
			if(!IngameTestOrchestrator.registered)
				MinecraftForge.EVENT_BUS.register(IngameTestOrchestrator.instance);
			EntityPlayer player = ((EntityPlayer)sender);
			IngameTestOrchestrator.runTests(player.getEntityWorld(), player);
		}
	}
	
	private void commandPlanet(ICommandSender sender, String cmdstring[])
	{
		if(cmdstring.length < 2)
		{
			commandPlanetHelp(sender, cmdstring);
			return;
		}
		
		switch(cmdstring[1])
		{
		case "reset":
			commandPlanetReset(sender, cmdstring);
			break;
		case "list":
			commandPlanetList(sender, cmdstring);
			break;
		case "delete":
			commandPlanetDelete(sender,cmdstring);
			break;
		case "generate":
			commandPlanetGenerate(sender, cmdstring);
			break;
		case "set":
			commandPlanetSet(sender, cmdstring);
			break;
		case "get":
			commandPlanetGet(sender, cmdstring);
			break;
		case "help":
		default:
			commandPlanetHelp(sender, cmdstring);
		}
	}
	
	private void commandStar(ICommandSender sender, String cmdstring[])
	{
		if(cmdstring.length > 1) {
			if(cmdstring[1].equalsIgnoreCase("list")) {
				for(StellarBody star : DimensionManager.getInstance().getStars())
					sender.sendMessage(new TextComponentString(String.format("Star ID: %d   Name: %s  Num Planets: %d", star.getId(), star.getName(), star.getNumPlanets())));
			}
			else if(cmdstring[1].equalsIgnoreCase("help")) {
				printStarHelp(sender);
			}
		}
		if(cmdstring.length > 3) {
			if(cmdstring[1].equalsIgnoreCase("get")) {
				commandStarGet(sender, cmdstring);
			} //get
		} if(cmdstring.length > 4) {
			if(cmdstring[1].equalsIgnoreCase("set")) {
				commandStarSet(sender, cmdstring);
			}
		}
		if(cmdstring.length > 5) {
			if(cmdstring[1].equalsIgnoreCase("generate")) {
				commandStarGenerate(sender, cmdstring);
			}
		}
	}
	
	@Override
	public void execute(MinecraftServer server, ICommandSender sender,
			String[] string) throws CommandException {

		//advrocketry planet set <var value>
		int opLevel = 2;
		if(string.length == 0 || (string.length >= 1 && string[0].equalsIgnoreCase("help"))) {
			sender.sendMessage(new TextComponentString("Subcommands:"));
			sender.sendMessage(new TextComponentString("planet"));
			sender.sendMessage(new TextComponentString("filldata"));
			sender.sendMessage(new TextComponentString("goto"));
			sender.sendMessage(new TextComponentString("star"));
			sender.sendMessage(new TextComponentString("fetch"));
			sender.sendMessage(new TextComponentString("giveStation"));
			sender.sendMessage(new TextComponentString("reloadRecipes"));
			sender.sendMessage(new TextComponentString("setGravity"));
			sender.sendMessage(new TextComponentString("addTorch"));
			sender.sendMessage(new TextComponentString("[Enter /advrocketry <subcommand> help for more info]"));
		}
		
		switch(string[0])
		{
		case "dumpBiomes":
			commandBiomeDump(sender, string);
			break;
		case "beginTest":
			commandBeginTest(sender, string);
			break;
		case "addTorch":
			commandAddTorch(sender, string);
			break;
		case "addSolidBlockOverride":
			commandAddSolidBlockOverride(sender, string);
			break;
		case "givestation":
			commandGiveStation(sender, string);
			break;
		case "filldata":
			commandFillData(sender, string);
			break;
		case "reloadRecipes":
			commandReloadRecipes(sender, string);
			break;
		case "setGravity":
			commandSetGravity(sender, string);
			break;
		case "goto":
			commandGoto(sender, string);
			break;
		case "fetch":
			commandFetch(sender, string);
			break;
		case "planet":
			commandPlanet(sender, string);
			break;
		case "star":
			commandStar(sender, string);
			break;
		}
	}

	private void printStarHelp(ICommandSender sender) {
		sender.sendMessage(new TextComponentString("star list"));
		sender.sendMessage(new TextComponentString("star get temp <star id>"));
		sender.sendMessage(new TextComponentString("star get planets <star id>"));
		sender.sendMessage(new TextComponentString("star get pos <star id>"));
		sender.sendMessage(new TextComponentString("star set temp <star id> <temperature>"));
		sender.sendMessage(new TextComponentString("star set pos <star id> <x> <y>"));
		sender.sendMessage(new TextComponentString("star generate <name> <temp> <x> <y>"));
	}

	@Override
	public boolean checkPermission(MinecraftServer server, ICommandSender sender) {
		return sender.canUseCommand(2, getName());

	}

	@Override
	public List<String> getTabCompletions(MinecraftServer server,
			ICommandSender sender, String[] string, BlockPos targetPos) {
		ArrayList<String> list = new ArrayList<String>();

		if(string.length == 1) {
			list.add("beginTest");
			list.add("planet");
			list.add("goto");
			list.add("fetch");
			list.add("star");
			list.add("filldata");
			list.add("setGravity");
			list.add("reloadRecipes");
			list.add("givestation");
			list.add("dumpBiomes");
			list.add("addTorch");
			list.add("addSolidBlockOverride");
		} else if(string.length == 2) {
			ArrayList<String> list2 = new ArrayList<String>();
			list2.add("get");
			list2.add("set");
			list2.add("list");
			list2.add("generate");
			if(string[0].equalsIgnoreCase("planet")) {
				list2.add("reset");
				list2.add("new");
				list2.add("delete");


				for(String str : list2) {
					if(str.startsWith(string[1]))
						list.add(str);
				}
			}
		} else if(( string[1].equalsIgnoreCase("get") || string[1].equalsIgnoreCase("set")) && string[0].equalsIgnoreCase("planet") && string.length == 3) {
			for(Field field : DimensionProperties.class.getFields()) {
				if(field.getName().startsWith(string[2]))
					list.add(field.getName());

			}
			list.add("atmosphereDensity");
		}

		return list;
	}

	@Override
	public boolean isUsernameIndex(String[] string, int number) {
		return number == 1 && string[0].equalsIgnoreCase("fetch");
	}
	@Override
	public int compareTo(ICommand arg0) {
		return this.getName().compareTo(arg0.getName());
	}

	private EntityPlayer getPlayerByName(String name) {
		EntityPlayer player = null;
		for(World world : net.minecraftforge.common.DimensionManager.getWorlds()) {
			player = world.getPlayerEntityByName(name);
			if ( player != null) break;
		}

		return player;
	}
}
