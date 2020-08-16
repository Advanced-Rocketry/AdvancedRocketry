package zmaster587.advancedRocketry.command;

import net.minecraft.block.Block;
import net.minecraft.command.CommandException;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
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

import org.lwjgl.system.CallbackI.S;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

public class WorldCommand implements Command<S> {



	private List aliases;
	public WorldCommand() {
		aliases = new ArrayList<String>();
		aliases.add("advancedRocketry");
		aliases.add("advRocketry");
	}

	@Override
	public String getName() {
		return "advancedRocketry";
	}

	@Override
	public String getUsage(ICommandSender sender) {
		return "advancedRocketry help";
	}

	@Override
	public List getAliases() {
		return aliases;
	}

	private void commandAddTorch(ICommandSender sender, String cmdstring[]) {
		
		if(cmdstring.length >= 2 && cmdstring[1].equalsIgnoreCase("help"))
		{
			sender.sendMessage(new StringTextComponent( aliases.get(0) + " " + cmdstring[0] +  " - Adds the currently held block to the list of objects that drop when there's no atmosphere"));
			return;
		}
		
		Entity player = sender.getCommandSenderEntity();
		if(!(player instanceof PlayerEntity)) {
			sender.sendMessage(new StringTextComponent("Not a player entity"));
			return;
		}
		
		Block block = Block.getBlockFromItem(((PlayerEntity)player).getHeldItemMainhand().getItem());
		if (block != Blocks.AIR)
		{
			if(ARConfiguration.getCurrentConfig().torchBlocks.contains(block) )
				sender.sendMessage(new StringTextComponent(block.getLocalizedName() + " is already in the torch list"));
			else
			{
				
				ARConfiguration.getCurrentConfig().addTorchblock(block);
				
				sender.sendMessage(new StringTextComponent(block.getLocalizedName() + " added to the torch list"));
			}
		}
		else
			sender.sendMessage(new StringTextComponent("Held block cannot be added to torch list"));
	}
	
	private void commandAddSolidBlockOverride(ICommandSender sender, String cmdstring[]) {
		if(cmdstring.length >= 2 && cmdstring[1].equalsIgnoreCase("help"))
		{
			sender.sendMessage(new StringTextComponent( aliases.get(0) + " " + cmdstring[0] +  " - Adds the currently held block to the list of blocks that can hold a seal"));
			return;
		}
		
		Entity player = sender.getCommandSenderEntity();
		if(!(player instanceof PlayerEntity)) {
			sender.sendMessage(new StringTextComponent("Not a player entity"));
			return;
		}
		
		Block block = Block.getBlockFromItem(((PlayerEntity)player).getHeldItemMainhand().getItem());
		if (block != Blocks.AIR)
		{
			if(ARConfiguration.getCurrentConfig(). torchBlocks.contains(block) )
				sender.sendMessage(new StringTextComponent(block.getLocalizedName() + " is already in the sealed blocks list"));
			else
			{
				
				ARConfiguration.getCurrentConfig().addSealedBlock(block);
				
				sender.sendMessage(new StringTextComponent(block.getLocalizedName() + " added to the sealed block list"));
			}
		}
		else
			sender.sendMessage(new StringTextComponent("Held block cannot be added to sealed block list"));
	}
	
	private void commandGiveStation(ICommandSender sender, String cmdstring[])
	{
		if(cmdstring.length < 2 || (cmdstring.length >= 2 && cmdstring[1].equalsIgnoreCase("help")))
		{
			sender.sendMessage(new StringTextComponent(aliases.get(0) + " " + cmdstring[0] +  " - Gives the player playerName (if supplied) a spacestation with ID stationID"));
			sender.sendMessage(new StringTextComponent("Usage: /advRocketry " + cmdstring[0] + " <stationId> [PlayerName]"));
			return;
		}
		
		PlayerEntity player = null;
		if(cmdstring.length >= 3) {
			player = getPlayerByName(cmdstring[2]);
			if(player == null) {
				sender.sendMessage(new StringTextComponent("Player " + cmdstring[2] + " not found"));
				return;
			}
		}
		else if(sender.getCommandSenderEntity() != null)
			player = ((PlayerEntity)sender);
	
		if(cmdstring.length >= 2 && player != null) {
			int stationId = Integer.parseInt(cmdstring[1]);
			ItemStack stack = new ItemStack(AdvancedRocketryItems.itemSpaceStationChip);
			ItemStationChip.setUUID(stack, stationId);
			player.inventory.addItemStackToInventory(stack);
		}
		else
			sender.sendMessage(new StringTextComponent("Usage: /advRocketry " + cmdstring[0] + " <stationId> [PlayerName]"));
	}
	
	private void commandFillData(ICommandSender sender, String cmdstring[])
	{
		if(cmdstring.length < 2)
			return;
		
		ItemStack stack;
		if(sender.getCommandSenderEntity() != null ) {
			stack = ((PlayerEntity)sender.getCommandSenderEntity()).getHeldItem(Hand.MAIN_HAND);

			if(cmdstring.length < 2 || (cmdstring.length >= 2 && cmdstring[1].equalsIgnoreCase("help"))) {
				sender.sendMessage(new StringTextComponent(aliases.get(0) + " " + cmdstring[0] + " [datatype] [amountFill]\n"));
				sender.sendMessage(new StringTextComponent("Fills the amount of the data type specifies into the chip being held."));
				sender.sendMessage(new StringTextComponent("If the datatype is not specified then command fills all datatypes, if no amountFill is specified completely fills the chip"));
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
						sender.sendMessage(new StringTextComponent("Did you mean: /advRocketry" + cmdstring[0] + " [datatype] [amountFill]"));
						sender.sendMessage(new StringTextComponent("Not a valid datatype"));
						String value = "";
						for(DataType data : DataType.values())
							if(!data.name().equals("UNDEFINED"))
								value += data.name().toLowerCase() + ", ";

						sender.sendMessage(new StringTextComponent("Try " + value));

						return;
					}
				}
				if(cmdstring.length >= 3)
					try {
						dataAmount = Integer.parseInt(cmdstring[2]);
					} catch(NumberFormatException e) {
						sender.sendMessage(new StringTextComponent("Did you mean: /advRocketry" + cmdstring[0] + " [datatype] [amountFill]"));
						sender.sendMessage(new StringTextComponent("Not a valid number"));
						return;
					}

				if(dataType != null)
					item.setData(stack, dataAmount, dataType);
				else
				{
					for(DataType type : DataType.values())
						item.setData(stack, dataAmount, type);
				}
				sender.sendMessage(new StringTextComponent("Data filled!"));
			}
			else if(stack != null && stack.getItem() instanceof ItemMultiData) {
				ItemMultiData item = (ItemMultiData) stack.getItem();
				int dataAmount = item.getMaxData(stack);
				DataType dataType = null;

				if(cmdstring.length >= 2) {
					try {
						dataType = DataType.valueOf(cmdstring[1].toUpperCase(Locale.ENGLISH));
					} catch (IllegalArgumentException e) {
						sender.sendMessage(new StringTextComponent("Did you mean: /advRocketry" + cmdstring[0] + " [datatype] [amountFill]"));
						sender.sendMessage(new StringTextComponent("Not a valid datatype"));
						String value = "";
						for(DataType data : DataType.values())
							if(!data.name().equals("UNDEFINED"))
								value += data.name().toLowerCase() + ", ";

						sender.sendMessage(new StringTextComponent("Try " + value));
						return;
					}
				}
				if(cmdstring.length >= 3)
					try {
						dataAmount = Integer.parseInt(cmdstring[2]);
					} catch(NumberFormatException e) {
						sender.sendMessage(new StringTextComponent("Did you mean: /advRocketry" + cmdstring[0] + " [datatype] [amountFill]"));
						sender.sendMessage(new StringTextComponent("Not a valid number"));
						return;
					}

				if(dataType != null)
					item.setData(stack, dataAmount, dataType);
				else
				{
					for(DataType type : DataType.values())
						item.setData(stack, dataAmount, type);
				}

				sender.sendMessage(new StringTextComponent("Data filled!"));
			}
			else
				sender.sendMessage(new StringTextComponent("Not Holding data item"));
		}
		else
			sender.sendMessage(new StringTextComponent("Ghosts don't have items!"));
	}
	
	
	private void commandReloadRecipes(ICommandSender sender, String cmdstring[])
	{
		if(cmdstring.length >= 2 && cmdstring[1].equalsIgnoreCase("help")) {
			sender.sendMessage(new StringTextComponent(aliases.get(0) + " " + cmdstring[0] + " - Reloads recipes from the XML files in the config folder"));
			return;
		}
		
		try {
			AdvancedRocketry.machineRecipes.clearAllMachineRecipes();
			AdvancedRocketry.machineRecipes.registerAllMachineRecipes();
			AdvancedRocketry.machineRecipes.createAutoGennedRecipes(AdvancedRocketry.modProducts);
			AdvancedRocketry.machineRecipes.registerXMLRecipes();

			sender.sendMessage(new StringTextComponent("Recipes Reloaded"));

			CompatibilityMgr.reloadRecipes();
		} catch (Exception e) {
			sender.sendMessage(new StringTextComponent("Serious error has occured!  Possible recipe corruption"));
			sender.sendMessage(new StringTextComponent("Please check logs!"));
			sender.sendMessage(new StringTextComponent("You may be able to recify this error by repairing the XML and/or"));
			sender.sendMessage(new StringTextComponent("restarting the game"));
		}
	}
	
	private void commandSetGravity(ICommandSender sender, String cmdstring[])
	{
		if(cmdstring.length >= 2) {
			if( cmdstring[1].equalsIgnoreCase("help")) {
				sender.sendMessage(new StringTextComponent(cmdstring[0] + " <amount> - sets your gravity to amount where 1 is earthlike"));
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
						sender.sendMessage(new StringTextComponent(cmdstring[1] + " is not a valid number"));
					}
				} else {
					sender.sendMessage(new StringTextComponent("Not a valid player"));
				}
			} else {
				sender.sendMessage(new StringTextComponent("Not a valid player"));
			}
		}
		else {
			sender.sendMessage(new StringTextComponent(aliases.get(0) + " " + cmdstring[0] + " gravity_multiplier [playerName]"));
			sender.sendMessage(new StringTextComponent(""));
			sender.sendMessage(new StringTextComponent("use 0 as the gravity_multiplier to allow regular planet gravity to take over"));
		}
	}
	
	private void commandGoto(ICommandSender sender, String cmdstring[])
	{
		PlayerEntity player;
		if(sender instanceof Entity && (player = sender.getEntityWorld().getPlayerEntityByName(sender.getName())) != null) {
			if(cmdstring.length < 2 || (cmdstring.length >= 2 && cmdstring[1].equalsIgnoreCase("help"))) {
				sender.sendMessage(new StringTextComponent(cmdstring[0] + " <dimId> - teleports the player to the supplied dimension"));
				sender.sendMessage(new StringTextComponent(cmdstring[0] + "station <station ID> - teleports the player to the supplied station"));
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
						player.getServer().getPlayerList().transferPlayerToDimension((ServerPlayerEntity) player,  dim , new TeleporterNoPortalSeekBlock((ServerWorld) net.minecraftforge.common.DimensionManager.getWorld(dim)));
					}
					else
						sender.sendMessage(new StringTextComponent("Dimension does not exist"));
				}
				else if(cmdstring[1].equalsIgnoreCase("station")) {
					dim = ARConfiguration.getCurrentConfig().spaceDimId;
					int stationId = Integer.parseInt(cmdstring[2]);
					ISpaceObject object = SpaceObjectManager.getSpaceManager().getSpaceStation(stationId);

					if(object != null) {
						if(player.world.provider.getDimension() != ARConfiguration.getCurrentConfig().spaceDimId)
							player.getServer().getPlayerList().transferPlayerToDimension((ServerPlayerEntity) player,  dim , new TeleporterNoPortal((ServerWorld)player.world));
						HashedBlockPosition vec = object.getSpawnLocation();
						player.setPositionAndUpdate(vec.x, vec.y, vec.z);
					}
					else {
						sender.sendMessage(new StringTextComponent("Station " + stationId + " does not exist!"));
					}
				}


			} catch(NumberFormatException e) {
				sender.sendMessage(new StringTextComponent(cmdstring[0] + " <dimId>"));
				sender.sendMessage(new StringTextComponent(cmdstring[0] + "station <station ID>"));
			}
		}					
		else 
			sender.sendMessage(new StringTextComponent("Must be a player to use this command"));
	}
	
	private void commandFetch(ICommandSender sender, String cmdstring[])
	{
		if(cmdstring.length < 2)
			return;
		
		PlayerEntity me = (PlayerEntity) sender.getCommandSenderEntity();
		PlayerEntity player = getPlayerByName(cmdstring[1]);
		System.out.println(cmdstring[1] + "   " + sender.getCommandSenderEntity());

		if(player == null) {
			sender.sendMessage(new StringTextComponent("Invalid player name: " + cmdstring[1]));
		}
		else {
			player.getServer().getPlayerList().transferPlayerToDimension((ServerPlayerEntity) player,  me.world.provider.getDimension() , new TeleporterNoPortal(me.getServer().getWorld(me.world.provider.getDimension())));
			player.setPosition(me.posX, me.posY, me.posZ);
		}
	}
	
	
	private void commandStarGet(ICommandSender sender, String cmdstring[])
	{
		try {
			int id = Integer.parseInt(cmdstring[3]);
			StellarBody star =  DimensionManager.getInstance().getStar(id);
			if(star == null)
				sender.sendMessage(new StringTextComponent("Error: " + cmdstring[3] + " is not a valid star ID"));
			else {
				if(cmdstring[2].equalsIgnoreCase("temp")) {
					sender.sendMessage(new StringTextComponent("Temp: " + star.getTemperature()));
				}
				else if(cmdstring[2].equalsIgnoreCase("planets")) {
					sender.sendMessage(new StringTextComponent("Planets orbiting the star:"));
					for(IDimensionProperties planets : star.getPlanets()) {
						sender.sendMessage(new StringTextComponent("ID: " + planets.getId() + " : " + planets.getName()));
					}
				}
				else if(cmdstring[2].equalsIgnoreCase("pos")) {
					sender.sendMessage(new StringTextComponent("Pos: " + star.getPosX() + "," + star.getPosZ()));
				}
			}// end star existance validation
		} catch (NumberFormatException e) {
			sender.sendMessage(new StringTextComponent("Error: " + cmdstring[3] + " is not a valid star ID"));
		}
	}
	
	private void commandStarSet(ICommandSender sender, String cmdstring[])
	{
		try {
			int id = Integer.parseInt(cmdstring[3]);
			StellarBody star =  DimensionManager.getInstance().getStar(id);
			if(star == null)
				sender.sendMessage(new StringTextComponent("Error: " + cmdstring[3] + " is not a valid star ID"));
			else {
				if(cmdstring[2].equalsIgnoreCase("temp")) {
					try {
						star.setTemperature(Integer.parseInt(cmdstring[4]));
						sender.sendMessage(new StringTextComponent("Temp set to " + star.getTemperature()));
					} catch(NumberFormatException e) {
						sender.sendMessage(new StringTextComponent("star set temp <starId> <temp>"));
					}
				} else if(cmdstring.length > 5 && cmdstring[2].equalsIgnoreCase("pos")) {
					try {
						int x= Integer.parseInt(cmdstring[4]);
						int z = Integer.parseInt(cmdstring[5]);
						star.setPosX(x);
						star.setPosZ(z);
						sender.sendMessage(new StringTextComponent("Position set to " + x + "," + z));
					} catch(NumberFormatException e) {
						sender.sendMessage(new StringTextComponent("star set pos <starId> <x> <y>"));
					}
				}
			}// end star existance validation
		} catch (NumberFormatException e) {
			sender.sendMessage(new StringTextComponent("Error: " + cmdstring[3] + " is not a valid star ID"));
		}
	}
	
	private void commandBiomeDump(ICommandSender sender, String cmdstring[])
	{
		
		if(cmdstring.length >= 2 && cmdstring[1].compareToIgnoreCase("help") == 0)
		{
			sender.sendMessage(new StringTextComponent("Developer command: Dumps biome info to BiomeDump.txt!"));
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
			sender.sendMessage(new StringTextComponent("The File \"BiomeDump.txt\" has been written to the current directory"));
		} 
		catch(Exception e) {
			sender.sendMessage(new StringTextComponent("An error has occured writing to the file"));
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
				sender.sendMessage(new StringTextComponent("star Added!"));
			}
			else
				sender.sendMessage(new StringTextComponent("Why can't I hold all these stars! (either you have an insane number of stars or something really broke!)"));

		} catch(NumberFormatException e) {
			sender.sendMessage(new StringTextComponent("star generate <name> <temp> <x> <y>"));
		}
	}
	
	private void commandBeginTest(ICommandSender sender, String cmdstring[])
	{
		if(cmdstring.length >= 2 && cmdstring[1].compareToIgnoreCase("help") == 0)
		{
			sender.sendMessage(new StringTextComponent("Developer command: Runs system tests, debug only!"));
			return;
		}
		if(sender.getCommandSenderEntity() != null)
		{
			if(!IngameTestOrchestrator.registered)
				MinecraftForge.EVENT_BUS.register(IngameTestOrchestrator.instance);
			PlayerEntity player = ((PlayerEntity)sender);
			IngameTestOrchestrator.runTests(player.getEntityWorld(), player);
		}
	}
	

	
	private void commandStar(ICommandSender sender, String cmdstring[])
	{
		if(cmdstring.length > 1) {
			if(cmdstring[1].equalsIgnoreCase("list")) {
				for(StellarBody star : DimensionManager.getInstance().getStars())
					sender.sendMessage(new StringTextComponent(String.format("Star ID: %d   Name: %s  Num Planets: %d", star.getId(), star.getName(), star.getNumPlanets())));
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

		//advRocketry planet set <var value>
		int opLevel = 2;
		if(string.length == 0 || (string.length >= 1 && string[0].equalsIgnoreCase("help"))) {
			sender.sendMessage(new StringTextComponent("Subcommands:"));
			sender.sendMessage(new StringTextComponent("planet"));
			sender.sendMessage(new StringTextComponent("filldata"));
			sender.sendMessage(new StringTextComponent("goto"));
			sender.sendMessage(new StringTextComponent("star"));
			sender.sendMessage(new StringTextComponent("fetch"));
			sender.sendMessage(new StringTextComponent("giveStation"));
			sender.sendMessage(new StringTextComponent("reloadRecipes"));
			sender.sendMessage(new StringTextComponent("setGravity"));
			sender.sendMessage(new StringTextComponent("addTorch"));
			sender.sendMessage(new StringTextComponent("[Enter /advRocketry <subcommand> help for more info]"));
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
		sender.sendMessage(new StringTextComponent("star list"));
		sender.sendMessage(new StringTextComponent("star get temp <star id>"));
		sender.sendMessage(new StringTextComponent("star get planets <star id>"));
		sender.sendMessage(new StringTextComponent("star get pos <star id>"));
		sender.sendMessage(new StringTextComponent("star set temp <star id> <temperature>"));
		sender.sendMessage(new StringTextComponent("star set pos <star id> <x> <y>"));
		sender.sendMessage(new StringTextComponent("star generate <name> <temp> <x> <y>"));
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

	private PlayerEntity getPlayerByName(String name) {
		PlayerEntity player = null;
		for(World world : net.minecraftforge.common.DimensionManager.getWorlds()) {
			player = world.getPlayerEntityByName(name);
			if ( player != null) break;
		}

		return player;
	}
}
