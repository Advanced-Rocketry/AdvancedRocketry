package zmaster587.advancedRocketry.command;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Locale;

import javax.annotation.Nullable;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.FloatArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.arguments.DimensionArgument;
import net.minecraft.command.arguments.EntityArgument;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.fml.server.ServerLifecycleHooks;
import zmaster587.advancedRocketry.api.AdvancedRocketryItems;
import zmaster587.advancedRocketry.api.Constants;
import zmaster587.advancedRocketry.api.DataStorage.DataType;
import zmaster587.advancedRocketry.api.dimension.solar.StellarBody;
import zmaster587.advancedRocketry.api.stations.ISpaceObject;
import zmaster587.advancedRocketry.dimension.DimensionManager;
import zmaster587.advancedRocketry.dimension.DimensionProperties;
import zmaster587.advancedRocketry.item.ItemDataChip;
import zmaster587.advancedRocketry.item.ItemMultiData;
import zmaster587.advancedRocketry.item.ItemStationChip;
import zmaster587.advancedRocketry.network.PacketDimInfo;
import zmaster587.advancedRocketry.stations.SpaceObjectManager;
import zmaster587.libVulpes.network.PacketHandler;
import zmaster587.libVulpes.util.HashedBlockPosition;
import zmaster587.libVulpes.util.ZUtils;

public class PlanetCommand {

	public static void register(CommandDispatcher<CommandSource> dispatcher) {
		

		dispatcher.register(Commands.literal("advancedrocketry").then(Commands.literal("planet")
				.executes((value) -> commandPlanetHelp(value.getSource()))
				.then(Commands.literal("reset").executes((value) -> commandPlanetReset(value.getSource(), null))
				.then(Commands.argument("dim", DimensionArgument.getDimension())).executes((value) -> commandPlanetReset(value.getSource(), DimensionArgument.getDimensionArgument(value, "dim"))) )
				
				.then(Commands.literal("list").executes((value) -> commandPlanetList(value.getSource())))
				
				.then(Commands.literal("delete").then(Commands.argument("dim", DimensionArgument.getDimension()).executes((value) -> commandPlanetDelete(value.getSource(), DimensionArgument.getDimensionArgument(value, "dim")))) )
				
				
				.then(Commands.literal("generate").then( Commands.literal("moon").then(Commands.argument("dim", DimensionArgument.getDimension())
						// generate moon dim atm dist gravity
						.then(Commands.argument("atm", IntegerArgumentType.integer(0)).then(Commands.argument("dist", IntegerArgumentType.integer(0)).then(Commands.argument("gravity", FloatArgumentType.floatArg(0)).executes((value) -> commandMoonGenerate(value.getSource(), false, null, DimensionArgument.getDimensionArgument(value, "dim"), IntegerArgumentType.getInteger(value, "atm"), IntegerArgumentType.getInteger(value, "dist"), IntegerArgumentType.getInteger(value, "grav"))) ) ) )
						
						// generate moon dim name atm dist gravity		
						.then(Commands.argument("name", StringArgumentType.word()).then(Commands.argument("atm", IntegerArgumentType.integer(0)).then(Commands.argument("dist", IntegerArgumentType.integer(0)).then(Commands.argument("gravity", FloatArgumentType.floatArg(0)).executes((value) -> commandMoonGenerate(value.getSource(), false, StringArgumentType.getString(value, "name"), DimensionArgument.getDimensionArgument(value, "dim"), IntegerArgumentType.getInteger(value, "atm"), IntegerArgumentType.getInteger(value, "dist"), IntegerArgumentType.getInteger(value, "grav"))) ) )) )
						
						.then(Commands.literal("gas")
								// generate moon dim gas atm dist gravity
								.then(Commands.argument("atm", IntegerArgumentType.integer(0)).then(Commands.argument("dist", IntegerArgumentType.integer(0)).then(Commands.argument("gravity", FloatArgumentType.floatArg(0)).executes((value) -> commandMoonGenerate(value.getSource(), true, null, DimensionArgument.getDimensionArgument(value, "dim"), IntegerArgumentType.getInteger(value, "atm"), IntegerArgumentType.getInteger(value, "dist"), IntegerArgumentType.getInteger(value, "grav"))) ) ) )
						
								// generate moon dim gas name atm dist gravity		
								.then(Commands.argument("name", StringArgumentType.word()).then(Commands.argument("atm", IntegerArgumentType.integer(0)).then(Commands.argument("dist", IntegerArgumentType.integer(0)).then(Commands.argument("gravity", FloatArgumentType.floatArg(0)).executes((value) -> commandMoonGenerate(value.getSource(), true, StringArgumentType.getString(value, "name"), DimensionArgument.getDimensionArgument(value, "dim"), IntegerArgumentType.getInteger(value, "atm"), IntegerArgumentType.getInteger(value, "dist"), IntegerArgumentType.getInteger(value, "grav"))) )) )) )
						))
						
				.then(Commands.argument("starName", StarArgument.getStar())
						// generate starId atm dist gravity
						.then(Commands.argument("atm", IntegerArgumentType.integer(0)).then(Commands.argument("dist", IntegerArgumentType.integer(0)).then(Commands.argument("gravity", FloatArgumentType.floatArg(0)).executes((value) -> commandPlanetGenerate(value.getSource(), false, null, StarArgument.getStarArgument(value, "starName"), IntegerArgumentType.getInteger(value, "atm"), IntegerArgumentType.getInteger(value, "dist"), IntegerArgumentType.getInteger(value, "grav"))) ) ) )
						
						// generate starId name atm dist gravity		
						.then(Commands.argument("name", StringArgumentType.word()).then(Commands.argument("atm", IntegerArgumentType.integer(0)).then(Commands.argument("dist", IntegerArgumentType.integer(0)).then(Commands.argument("gravity", FloatArgumentType.floatArg(0)).executes((value) -> commandPlanetGenerate(value.getSource(), false, StringArgumentType.getString(value, "name"), StarArgument.getStarArgument(value, "starName"), IntegerArgumentType.getInteger(value, "atm"), IntegerArgumentType.getInteger(value, "dist"), IntegerArgumentType.getInteger(value, "grav"))) ) )) )
						
						// generate starId atm dist gravity
						.then(Commands.literal("gas")
								// generate starId gas atm dist gravity
								.then(Commands.argument("atm", IntegerArgumentType.integer(0)).then(Commands.argument("dist", IntegerArgumentType.integer(0)).then(Commands.argument("gravity", FloatArgumentType.floatArg(0)).executes((value) -> commandPlanetGenerate(value.getSource(), true, null, StarArgument.getStarArgument(value, "starName"), IntegerArgumentType.getInteger(value, "atm"), IntegerArgumentType.getInteger(value, "dist"), IntegerArgumentType.getInteger(value, "grav"))) ) ) )
								
								// generate starId gas name atm dist gravity	
								.then(Commands.argument("name", StringArgumentType.word()).then(Commands.argument("atm", IntegerArgumentType.integer(0)).then(Commands.argument("dist", IntegerArgumentType.integer(0)).then(Commands.argument("gravity", FloatArgumentType.floatArg(0)).executes((value) -> commandPlanetGenerate(value.getSource(), true, StringArgumentType.getString(value, "name"), StarArgument.getStarArgument(value, "starName"), IntegerArgumentType.getInteger(value, "atm"), IntegerArgumentType.getInteger(value, "dist"), IntegerArgumentType.getInteger(value, "grav"))) ) )) )
								)
						))
				// set varName value
				.then(Commands.literal("set").then(Commands.argument("varName", ReflectionArgument.getReflected(DimensionProperties.class)).then(Commands.argument("value", StringArgumentType.word() ).executes((value) -> commandPlanetSet(value.getSource(), null, ReflectionArgument.getReflectionArgument(value, "varName", DimensionProperties.class), StringArgumentType.getString(value, "value"))) ) )
				
						// set dim varName value
						.then(Commands.argument("dim", DimensionArgument.getDimension()).then(Commands.argument("varName", ReflectionArgument.getReflected(DimensionProperties.class)).then(Commands.argument("value", StringArgumentType.word() ).executes((value) -> commandPlanetSet(value.getSource(), DimensionArgument.getDimensionArgument(value, "dim"), ReflectionArgument.getReflectionArgument(value, "varName", DimensionProperties.class), StringArgumentType.getString(value, "value"))) ) ) )
						)
				// get dimvarName
				.then(Commands.literal("get").then(Commands.argument("varName", ReflectionArgument.getReflected(DimensionProperties.class)).executes((value) -> commandPlanetGet(value.getSource(), null, ReflectionArgument.getReflectionArgument(value, "varName", DimensionProperties.class))))
						// get dim varName
						.then(Commands.argument("dim", DimensionArgument.getDimension()).then(Commands.argument("varName", ReflectionArgument.getReflected(DimensionProperties.class)).then(Commands.argument("value", StringArgumentType.word() ).executes((value) -> commandPlanetGet(value.getSource(), DimensionArgument.getDimensionArgument(value, "dim"), ReflectionArgument.getReflectionArgument(value, "varName", DimensionProperties.class))) ) ))
						)
				.then(Commands.literal("delete").then(Commands.argument("dim", DimensionArgument.getDimension()).executes((value) -> commandPlanetDelete(value.getSource(), DimensionArgument.getDimensionArgument(value, "dim"))) ))
				.then(Commands.literal("reset").then(Commands.argument("dim", DimensionArgument.getDimension()).executes((value) -> commandPlanetReset(value.getSource(), DimensionArgument.getDimensionArgument(value, "dim"))) ))
				.then(Commands.literal("help").executes((value) -> commandPlanetHelp(value.getSource())))
				)
				.then(Commands.literal("goto").then(Commands.argument("dim", DimensionArgument.getDimension()).executes((value -> commandGoto(value.getSource(), DimensionArgument.getDimensionArgument(value, "dim")))))
				.then(Commands.literal("station").then(Commands.argument("stationid", IntegerArgumentType.integer(1)).executes((value) -> commandGotoStation(value.getSource(), IntegerArgumentType.getInteger(value, "stationid")))))
				)
				// giveStation ID
				.then(Commands.literal("giveStation").then(Commands.argument("stationId", StringArgumentType.string()).executes((value) -> commandGiveStation(value.getSource(), null, StringArgumentType.getString(value, "stationId")))
				//giveStation ID player
					.then(Commands.argument("player", EntityArgument.player()).executes((value) -> commandGiveStation(value.getSource(), EntityArgument.getPlayer(value, "player"), StringArgumentType.getString(value, "stationId"))))))
				
				// filldata Type
				.then(Commands.literal("fillData").then( Commands.argument("dataType", StringArgumentType.word()).executes( (value) -> commandFillData(value.getSource(), StringArgumentType.getString(value, "dataType"), -1))
				// filldata type amount
						.then(Commands.argument("amount", IntegerArgumentType.integer(0)).executes( (value) -> commandFillData(value.getSource(), StringArgumentType.getString(value, "dataType"), IntegerArgumentType.getInteger(value, "amount"))) )  ))
				// star stuff, good star lord there's a lot here, and more to come
				.then(Commands.literal("star").then(Commands.literal("list").executes((value) -> commandListStars(value.getSource())) ))
				);
	}
	
	private static int commandListStars(CommandSource sender)
	{
		for(StellarBody star : DimensionManager.getInstance().getStars())
			sender.sendFeedback(new StringTextComponent(String.format("Star ID: %d   Name: %s  Num Planets: %d", star.getId(), star.getName(), star.getNumPlanets())), false);
	
		return 0;
	}
	
	private static int commandGoto(CommandSource sender, ServerWorld world)
	{
		ServerPlayerEntity player;
		if(sender.getEntity() != null && (player = (ServerPlayerEntity) sender.getEntity()) != null) {
			player.teleport(world, player.getPosX(), player.getPosY(), player.getPosZ(), 0, 0);
		}					
		else 
			sender.sendFeedback(new StringTextComponent("Must be a player to use this command"), true);
		
		return 0;
	}
	
	private static int commandGotoStation(CommandSource sender, int stationIdStr)
	{
		PlayerEntity player;
		ResourceLocation stationId = new ResourceLocation(SpaceObjectManager.STATION_NAMESPACE, String.valueOf(stationIdStr));
		ServerWorld world = ZUtils.getWorld(DimensionManager.spaceId);
		if(sender.getEntity() != null && (player = (PlayerEntity) sender.getEntity()) != null) {
			ISpaceObject object = SpaceObjectManager.getSpaceManager().getSpaceStation(stationId);

			if(object != null) {
				HashedBlockPosition vec = object.getSpawnLocation();
				if(!DimensionManager.spaceId.equals(ZUtils.getDimensionIdentifier(player.world)))
					((ServerPlayerEntity) player).teleport(world, vec.x, vec.y, vec.z, 0, 0);
				
				player.setPositionAndUpdate(vec.x, vec.y, vec.z);
			}
			else {
				sender.sendFeedback(new StringTextComponent("Station " + stationId + " does not exist!"), true);
			}
		}					
		else 
			sender.sendFeedback(new StringTextComponent("Must be a player to use this command"), true);
		
		return 0;
	}
	
	private static int commandFillData(CommandSource sender, String dataTypeStr, int amountFill) throws CommandSyntaxException
	{
		
		ItemStack stack;
		if(sender.getEntity() != null ) {
			stack = sender.asPlayer().getHeldItem(Hand.MAIN_HAND);

			if(!stack.isEmpty() && stack.getItem() instanceof ItemDataChip) {
				ItemDataChip item = (ItemDataChip) stack.getItem();
				int dataAmount = item.getMaxData(stack);
				DataType dataType = null;

				if(dataTypeStr != null) {
					try {
						dataType = DataType.valueOf(dataTypeStr.toUpperCase(Locale.ENGLISH));
					} catch (IllegalArgumentException e) {
						sender.sendFeedback(new StringTextComponent("Not a valid datatype"), false);
						StringBuilder value = new StringBuilder();
						for(DataType data : DataType.values())
							if(!data.name().equals("UNDEFINED"))
								value.append(data.name().toLowerCase()).append(", ");

						sender.sendFeedback(new StringTextComponent("Try " + value), false);

						return -1;
					}
				}
				if(amountFill >= -1)
					dataAmount = amountFill;

				if(dataType != null)
					item.setData(stack, dataAmount, dataType);
				else
				{
					for(DataType type : DataType.values())
						item.setData(stack, dataAmount, type);
				}
				sender.sendFeedback(new StringTextComponent("Data filled!"), false);
			}
			else if(stack.isEmpty() && stack.getItem() instanceof ItemMultiData) {
				ItemMultiData item = (ItemMultiData) stack.getItem();
				int dataAmount = item.getMaxData(stack);
				DataType dataType = null;

				if(dataTypeStr != null) {
					try {
						dataType = DataType.valueOf(dataTypeStr.toUpperCase(Locale.ENGLISH));
					} catch (IllegalArgumentException e) {
						sender.sendFeedback(new StringTextComponent("Not a valid datatype"), false);
						String value = "";
						for(DataType data : DataType.values())
							if(!data.name().equals("UNDEFINED"))
								value += data.name().toLowerCase() + ", ";

						sender.sendFeedback(new StringTextComponent("Try " + value),false);
						return -1;
					}
				}
				if(amountFill >= 0)
					dataAmount = amountFill;

				if(dataType != null)
					item.setData(stack, dataAmount, dataType);
				else
				{
					for(DataType type : DataType.values())
						item.setData(stack, dataAmount, type);
				}

				sender.sendFeedback(new StringTextComponent("Data filled!"), false);
			}
			else
				sender.sendFeedback(new StringTextComponent("Not Holding data item"),false);
		}
		else
			sender.sendFeedback(new StringTextComponent("Ghosts don't have items!"),false);
		
		return 0;
	}
	
	private static int commandGiveStation(CommandSource sender, @Nullable PlayerEntity player, String stationIdStr) {
		ResourceLocation stationId = new ResourceLocation(Constants.modId, stationIdStr);
		if(player == null && sender.getEntity() != null)
			try {
				player = sender.asPlayer();
			} catch (CommandSyntaxException e) {
				e.printStackTrace();
				return -1;
			}
	
		if(player != null) {
			ItemStack stack = new ItemStack(AdvancedRocketryItems.itemSpaceStationChip);
			ItemStationChip.setUUID(stack, stationId);
			player.inventory.addItemStackToInventory(stack);
		}
		
		return 0;
	}

	private static int commandPlanetDelete(CommandSource sender, ServerWorld world) {

		ResourceLocation deletedDimId = ZUtils.getDimensionIdentifier(world);

		if(DimensionManager.getInstance().isDimensionCreated(deletedDimId)) {

			if(world == null || world.getPlayers().isEmpty()) {
				DimensionManager.getInstance().deleteDimension(deletedDimId);
				PacketHandler.sendToAll(new PacketDimInfo(deletedDimId, null));
				sender.sendFeedback(new StringTextComponent("Dim " + deletedDimId + " deleted!"), true);
			}
			else {
				//If the world still has players abort and list players
				sender.sendFeedback(new StringTextComponent("World still has players:"), false);

				List<ServerPlayerEntity> players = ServerLifecycleHooks.getCurrentServer().getPlayerList().getPlayers();
				for(PlayerEntity player : world.getPlayers()) {
					sender.sendFeedback(player.getDisplayName(), true);
				}

			}


		} else {
			sender.sendFeedback(new StringTextComponent("Dimension does not exist"), true);
		}
		
		return 0;
	}

	private static int commandMoonGenerate(CommandSource sender, boolean gas, @Nullable String name, World world, int atm, int dist, int gravity)
	{
		ResourceLocation planetId = ZUtils.getDimensionIdentifier(world);
		ResourceLocation starId = DimensionManager.getInstance().getDimensionProperties(planetId).getStarId();

		DimensionProperties properties;
		if(!gas)
			properties = DimensionManager.getInstance().generateRandom(starId, "unnamed", atm, dist, gravity);
		else
			properties = DimensionManager.getInstance().generateRandomGasGiant(starId, "unnamed", atm, dist, gravity,1,1,1);

		if(properties == null)
			sender.sendFeedback(new StringTextComponent("Dimension: " + name + " failed to generate!"), true);
		else
			sender.sendFeedback(new StringTextComponent("Dimension: " + name + " Generated!"), true);

		properties.setParentPlanet(DimensionManager.getInstance().getDimensionProperties(planetId));
		DimensionManager.getInstance().getStar(starId).removePlanet(properties);
		return 0;
	}
	
	private static int commandPlanetGenerate(CommandSource sender, boolean gas, @Nullable String name, StellarBody starName, int atm, int dist, int gravity)
	{
		ResourceLocation starId = starName.getId();

		DimensionProperties properties;
		if(!gas)
			properties = DimensionManager.getInstance().generateRandom(starId, "unnamed", atm, dist, gravity);
		else
			properties = DimensionManager.getInstance().generateRandomGasGiant(starId, "unnamed", atm, dist, gravity,1,1,1);

		if(properties == null)
			sender.sendFeedback(new StringTextComponent("Dimension: " + name + " failed to generate!"), true);
		else
			sender.sendFeedback(new StringTextComponent("Dimension: " + name + " Generated!"), true);
		return 0;
	}

	private static int commandPlanetSet(CommandSource sender, @Nullable World world, String fieldName, String value)
	{
		ResourceLocation dimId;
		if( !DimensionManager.getInstance().isDimensionCreated((dimId = ZUtils.getDimensionIdentifier(sender.getWorld()))))
			return -1;
		
		String[] cmdString = value.split(" ");

		int commandOffset = 0;

		if(world != null) {
			dimId = ZUtils.getDimensionIdentifier(world);
		}

		if(!DimensionManager.getInstance().isDimensionCreated(dimId)) {
			sender.sendFeedback(new StringTextComponent("Invalid Dimensions"), true);
			return -1;
		}

		DimensionProperties properties = DimensionManager.getInstance().getDimensionProperties(dimId);
		try {
			if(fieldName.equalsIgnoreCase("atmosphereDensity")) {
				properties.setAtmosphereDensityDirect(Integer.parseUnsignedInt(value));
				sender.sendFeedback(new StringTextComponent("Setting " + fieldName + " for dimension " + dimId + " to " + value), true);
				PacketHandler.sendToAll(new PacketDimInfo(dimId, properties));
			}
			else {

				Field field = properties.getClass().getDeclaredField(fieldName);

				if(field.getType().isArray()) {

					if(Float.TYPE == field.getType().getComponentType()) {
						float[] var = (float[])field.get(properties);

						if(cmdString.length == var.length) {

							//Make sure we catch if some invalid arg is entered
							String outString = "";
							for(int i = 0; i < var.length; i++) {
								var[i] = Float.parseFloat(cmdString[i]);
								outString = outString + cmdString[i] + " ";
							}

							field.set(properties, var);
							sender.sendFeedback(new StringTextComponent("Setting " + fieldName + " for dimension " + dimId + " to " + outString), true);
						}
					}

					if(Integer.TYPE == field.getType().getComponentType()) {
						int[] var = (int[])field.get(properties);

						if(cmdString.length == var.length) {

							//Make sure we catch if some invalid arg is entered
							String outString = "";
							for(int i = 0; i < var.length; i++) {
								var[i] = Integer.parseInt(cmdString[i]);
								outString = outString + cmdString[i] + " ";
							}

							field.set(properties, var);
							sender.sendFeedback(new StringTextComponent("Setting " + fieldName + " for dimension " + dimId + " to " + outString), true);
						}
					}
				}
				else {
					if(Integer.TYPE == field.getType() )
						field.set(properties, Integer.parseInt(value));
					else if(Float.TYPE == field.getType())
						field.set(properties, Float.parseFloat(value));
					else if(Double.TYPE == field.getType()) 
						field.set(properties, Double.parseDouble(value));
					else if(Boolean.TYPE == field.getType())
						field.set(properties, Boolean.parseBoolean(value));
					else
						field.set(properties, value);
					sender.sendFeedback(new StringTextComponent("Setting " + fieldName + " for dimension " + dimId + " to " + value), true);
				}

				PacketHandler.sendToAll(new PacketDimInfo(dimId, properties));
				return 0;
			}
		} catch (NumberFormatException e) {
			sender.sendFeedback(new StringTextComponent("Invalid Argument for parameter " + fieldName), true);
			return -1;
		} catch (Exception e) {

			e.printStackTrace();
		}
		return 0;
	}

	private static int commandPlanetGet(CommandSource sender, @Nullable World world, String fieldName)
	{

		ResourceLocation dimId;
		if( !DimensionManager.getInstance().isDimensionCreated((dimId = ZUtils.getDimensionIdentifier(sender.getWorld()))))
			return -1;
		
		int commandOffset = 0;
		if(world != null) {
			try {
				dimId = ZUtils.getDimensionIdentifier(world);
				commandOffset = 1;
			} 
			catch (NumberFormatException e) {
				sender.sendFeedback(new StringTextComponent("Invalid Dimensions"), true);
			}
		}

		if(!DimensionManager.getInstance().isDimensionCreated(dimId)) {
			sender.sendFeedback(new StringTextComponent("Invalid Dimensions"), true);
			return -1;
		}

		DimensionProperties properties = DimensionManager.getInstance().getDimensionProperties(dimId);
		if(fieldName.equalsIgnoreCase("atmosphereDensity")) {
			sender.sendFeedback(new StringTextComponent(Integer.toString(properties.getAtmosphereDensity())), true);
		} 
		else {
			try {
				Field field = properties.getClass().getDeclaredField(fieldName);

				sender.sendFeedback(new StringTextComponent(field.get(properties).toString()), true);

			} catch (Exception e) {

				e.printStackTrace();
				sender.sendFeedback(new StringTextComponent("An error has occured, please check logs"), true);
			}
		}
		
		return 0;
	}

	private static int commandPlanetList(CommandSource sender)
	{
		sender.sendFeedback(new StringTextComponent("Dimensions:"), false);
		for(ResourceLocation i : DimensionManager.getInstance().getRegisteredDimensions()) {
			sender.sendFeedback(new StringTextComponent("DIM" + i + ":  " + DimensionManager.getInstance().getDimensionProperties(i).getName()), false); 
		}
		
		return 0;
	}

	private static int commandPlanetHelp(CommandSource sender)
	{
		sender.sendFeedback(new StringTextComponent("Planet:"), false);
		sender.sendFeedback(new StringTextComponent("planet delete [dimid]"), false);
		sender.sendFeedback(new StringTextComponent("planet generate [starId] (moon/gas) [name] [atmosphere randomness] [distance Randomness] [gravity randomness] (atmosphere base) (distance base) (gravity base)"), false);
		sender.sendFeedback(new StringTextComponent("planet list"), false);
		sender.sendFeedback(new StringTextComponent("planet reset [dimid]"), false);
		sender.sendFeedback(new StringTextComponent("planet set [property]"), false);
		sender.sendFeedback(new StringTextComponent("planet get [property]"), false);
		
		return 0;
	}

	private static int commandPlanetReset(CommandSource sender, World dimension)
	{
		ResourceLocation dimId;
		if(dimension != null) {
			try {
				dimId = ZUtils.getDimensionIdentifier(dimension);
				DimensionManager.getInstance().getDimensionProperties(dimId).resetProperties();
				PacketHandler.sendToAll(new PacketDimInfo(dimId, DimensionManager.getInstance().getDimensionProperties(dimId)));
			} catch (NumberFormatException e) {
				sender.sendFeedback(new StringTextComponent("Invalid dimId"), true);
			}
		}
		else {
			if(sender.getEntity() != null) {
				if(DimensionManager.getInstance().isDimensionCreated((dimId = ZUtils.getDimensionIdentifier(sender.getWorld())))) {
					DimensionManager.getInstance().getDimensionProperties(dimId).resetProperties();
					PacketHandler.sendToAll(new PacketDimInfo(dimId, DimensionManager.getInstance().getDimensionProperties(dimId)));
				}
			}
			else {
				sender.sendFeedback(new StringTextComponent("Please specify dimension ID") ,true);
			}
		}
		return 0;
	}

}
