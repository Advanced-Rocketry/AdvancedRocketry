package zmaster587.advancedRocketry.command;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import zmaster587.advancedRocketry.AdvancedRocketry;
import zmaster587.advancedRocketry.api.AdvancedRocketryAPI;
import zmaster587.advancedRocketry.api.AdvancedRocketryItems;
import zmaster587.advancedRocketry.api.Configuration;
import zmaster587.advancedRocketry.api.DataStorage.DataType;
import zmaster587.advancedRocketry.api.dimension.IDimensionProperties;
import zmaster587.advancedRocketry.api.dimension.solar.StellarBody;
import zmaster587.advancedRocketry.api.stations.ISpaceObject;
import zmaster587.advancedRocketry.dimension.DimensionManager;
import zmaster587.advancedRocketry.dimension.DimensionProperties;
import zmaster587.advancedRocketry.item.ItemMultiData;
import zmaster587.advancedRocketry.item.ItemStationChip;
import zmaster587.advancedRocketry.network.PacketDimInfo;
import zmaster587.advancedRocketry.network.PacketStellarInfo;
import zmaster587.advancedRocketry.stations.SpaceObjectManager;
import zmaster587.advancedRocketry.world.util.TeleporterNoPortal;
import zmaster587.libVulpes.network.PacketHandler;
import zmaster587.libVulpes.util.BlockPosition;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ChatComponentText;
import net.minecraft.world.World;

public class WorldCommand implements ICommand {



	private List aliases;
	public WorldCommand() {
		aliases = new ArrayList<String>();
		aliases.add("advancedRocketry");
		aliases.add("advRocketry");
	}

	@Override
	public int compareTo(Object arg) {
		if(arg instanceof ICommand)
			return this.getCommandName().compareTo(((ICommand) arg).getCommandName());
		return 0;
	}

	@Override
	public String getCommandName() {
		return "advancedRocketry";
	}

	@Override
	public String getCommandUsage(ICommandSender sender) {
		return "advancedRocketry help";
	}

	@Override
	public List getCommandAliases() {
		return aliases;
	}

	@Override
	public void processCommand(ICommandSender sender, String[] string) {

		//advRocketry planet set <var value>
		int opLevel = 2;
		if(!(sender instanceof EntityPlayer))
		{
			if(sender != null)
				sender.addChatMessage(new ChatComponentText("Commands can only be executed by a player"));
			return;
		}
		
		if(string.length >= 1 && string[0].equalsIgnoreCase("givestation")) {
			if(string.length >= 2) {
				int stationId = Integer.parseInt(string[1]);
				ItemStack stack = new ItemStack(AdvancedRocketryItems.itemSpaceStationChip);
				((ItemStationChip)AdvancedRocketryItems.itemSpaceStationChip).setUUID(stack, stationId);
				((EntityPlayer)sender).inventory.addItemStackToInventory(stack);
			}
			else
				sender.addChatMessage(new ChatComponentText("Usage: /advRocketry " + string[0] + " <stationId>"));
		}

		if(string.length >= 1 &&  string[0].equalsIgnoreCase("filldata")) {
			ItemStack stack;
			if(sender instanceof EntityPlayer ) {
				stack = ((EntityPlayer)sender).getHeldItem();
				
				if(string.length >= 2 && string[1].equalsIgnoreCase("help")) {
					sender.addChatMessage(new ChatComponentText("Usage: /advRocketry" + string[0] + " [datatype] [amountFill]\n"));
					sender.addChatMessage(new ChatComponentText("Fills the amount of the data type specifies into the chip being held."));
					sender.addChatMessage(new ChatComponentText("If the datatype is not specified then command fills all datatypes, if no amountFill is specified completely fills the chip"));
					return;
				}
				
				if(stack != null && stack.getItem() instanceof ItemMultiData) {
					ItemMultiData item = (ItemMultiData) stack.getItem();
					int dataAmount = item.getMaxData(stack);
					DataType dataType = null;

					if(string.length >= 2) {
						try {
							dataType = DataType.valueOf(string[1].toUpperCase(Locale.ENGLISH));
						} catch (IllegalArgumentException e) {
							sender.addChatMessage(new ChatComponentText("Did you mean: /advRocketry" + string[0] + " [datatype] [amountFill]"));
							sender.addChatMessage(new ChatComponentText("Not a valid datatype"));
							String value = "";
							for(DataType data : DataType.values())
								if(!data.name().equals("UNDEFINED"))
								value += data.name().toLowerCase() + ", ";
							
							sender.addChatMessage(new ChatComponentText("Try " + value));
							return;
						}
					}
					if(string.length >= 3)
						try {
							dataAmount = Integer.parseInt(string[2]);
						} catch(NumberFormatException e) {
							sender.addChatMessage(new ChatComponentText("Did you mean: /advRocketry" + string[0] + " [datatype] [amountFill]"));
							sender.addChatMessage(new ChatComponentText("Not a valid number"));
							return;
						}

					if(dataType != null)
						item.setData(stack, dataAmount, dataType);
					else
					{
						for(DataType type : DataType.values())
							item.setData(stack, dataAmount, type);
					}
					
					sender.addChatMessage(new ChatComponentText("Data filled!"));
				}
				else
					sender.addChatMessage(new ChatComponentText("Not Holding data item"));
			}
			else
				sender.addChatMessage(new ChatComponentText("Ghosts don't have items!"));
			return;
		}
		
		if(string.length >= 1 &&  string[0].equalsIgnoreCase("reloadRecipes")) {
			try {
				AdvancedRocketry.machineRecipes.clearAllMachineRecipes();
				AdvancedRocketry.machineRecipes.registerAllMachineRecipes();
				AdvancedRocketry.machineRecipes.createAutoGennedRecipes(AdvancedRocketry.modProducts);
				AdvancedRocketry.machineRecipes.registerXMLRecipes();
				
				sender.addChatMessage(new ChatComponentText("Recipes Reloaded"));
			} catch (Exception e) {
				sender.addChatMessage(new ChatComponentText("Serious error has occured!  Possible recipe corruption"));
				sender.addChatMessage(new ChatComponentText("Please check logs!"));
				sender.addChatMessage(new ChatComponentText("You may be able to recify this error by repairing the XML and/or"));
				sender.addChatMessage(new ChatComponentText("restarting the game"));
			}

			
			return;
		}

		if(string.length >= 1 && string[0].equalsIgnoreCase("setGravity")) {
			if(string.length >= 2) {
				if(sender instanceof Entity) {
					Entity player;
					if(string.length > 2)
						player = sender.getEntityWorld().getPlayerEntityByName(string[2]);
					else
						player = (Entity) sender;
					if(player != null) {
						try {
							double d = Double.parseDouble(string[1]);
							if(d == 0)
								AdvancedRocketryAPI.gravityManager.clearGravityEffect(player);
							else
								AdvancedRocketryAPI.gravityManager.setGravityMultiplier((Entity) sender, d);
						} catch(NumberFormatException e) {
							sender.addChatMessage(new ChatComponentText(string[1] + " is not a valid number"));
						}
					} else {
						sender.addChatMessage(new ChatComponentText("Not a valid player"));
					}
				}
			}
			else {
				sender.addChatMessage(new ChatComponentText("Help: "));
				sender.addChatMessage(new ChatComponentText("/advRocketry " + string[0] + " gravity_multiplier [playerName]"));
				sender.addChatMessage(new ChatComponentText(""));
				sender.addChatMessage(new ChatComponentText("use 0 as the gravity_multiplier to allow regular planet gravity to take over"));
			}
			return;
		}

		if(string.length > 1) {

			if(string[0].equalsIgnoreCase("goto") && (string.length == 2 || string.length == 3)) {
				EntityPlayer player = sender.getEntityWorld().getPlayerEntityByName(sender.getCommandSenderName());
				if(player != null) {
					try {
						int dim;

						if(string.length == 2) {
							dim = Integer.parseInt(string[1]);
							if(net.minecraftforge.common.DimensionManager.isDimensionRegistered(dim))
								MinecraftServer.getServer().getConfigurationManager().transferPlayerToDimension((EntityPlayerMP) player,  dim , new TeleporterNoPortal(MinecraftServer.getServer().worldServerForDimension(dim)));
							else
								sender.addChatMessage(new ChatComponentText("Dimension does not exist"));
						}
						else if(string[1].equalsIgnoreCase("station")) {
							dim = Configuration.spaceDimId;
							int stationId = Integer.parseInt(string[2]);
							ISpaceObject object = SpaceObjectManager.getSpaceManager().getSpaceStation(stationId);

							if(object != null) {
								if(player.worldObj.provider.dimensionId != Configuration.spaceDimId)
									MinecraftServer.getServer().getConfigurationManager().transferPlayerToDimension((EntityPlayerMP) player,  dim , new TeleporterNoPortal(MinecraftServer.getServer().worldServerForDimension(dim)));
								BlockPosition vec = object.getSpawnLocation();
								player.setPositionAndUpdate(vec.x, vec.y, vec.z);
							}
							else {
								sender.addChatMessage(new ChatComponentText("Station " + stationId + " does not exist!"));
							}
						}


					} catch(NumberFormatException e) {
						sender.addChatMessage(new ChatComponentText(string[0] + " <dimId>"));
						sender.addChatMessage(new ChatComponentText(string[0] + "station <station ID>"));
					}
				}					
				else 
					sender.addChatMessage(new ChatComponentText("Must be a player to use this command"));
			}
			else if(string[0].equalsIgnoreCase("fetch") && string.length == 2) {
				EntityPlayer me = sender.getEntityWorld().getPlayerEntityByName(sender.getCommandSenderName());
				EntityPlayer player = null;

				for(World world : MinecraftServer.getServer().worldServers) {
					player = world.getPlayerEntityByName(string[1]);
					if(player != null)
						break;
				}



				System.out.println(string[1] + "   " + sender.getCommandSenderName());

				if(player == null) {
					sender.addChatMessage(new ChatComponentText("Invalid player name: " + string[1]));
				}
				else {
					MinecraftServer.getServer().getConfigurationManager().transferPlayerToDimension((EntityPlayerMP) player,  me.worldObj.provider.dimensionId , new TeleporterNoPortal(MinecraftServer.getServer().worldServerForDimension(me.worldObj.provider.dimensionId)));
					player.setPosition(me.posX, me.posY, me.posZ);
				}
			}
			else if(string[0].equalsIgnoreCase("planet")) {

				int dimId;
				if(string[1].equalsIgnoreCase("reset")) {
					if(string.length == 3) {
						try {
							dimId = Integer.parseInt(string[2]);
							DimensionManager.getInstance().getDimensionProperties(dimId).resetProperties();
							PacketHandler.sendToAll(new PacketDimInfo(dimId, DimensionManager.getInstance().getDimensionProperties(dimId)));
						} catch (NumberFormatException e) {
							sender.addChatMessage(new ChatComponentText("Invalid dimId"));
						}
					}
					else if(string.length == 2) {
						if(DimensionManager.getInstance().isDimensionCreated((dimId = sender.getEntityWorld().provider.dimensionId))) {
							DimensionManager.getInstance().getDimensionProperties(dimId).resetProperties();
							PacketHandler.sendToAll(new PacketDimInfo(dimId, DimensionManager.getInstance().getDimensionProperties(dimId)));
						}
					}
				}
				else if(string[1].equalsIgnoreCase("list")) { //Lists dimensions

					sender.addChatMessage(new ChatComponentText("Dimensions:"));
					for(int i : DimensionManager.getInstance().getRegisteredDimensions()) {
						sender.addChatMessage(new ChatComponentText("DIM" + i + ":  " + DimensionManager.getInstance().getDimensionProperties(i).getName())); 
					}
				}
				else if(string[1].equalsIgnoreCase("delete")) {
					// advRocketry planet delete <name>
					if(string.length == 3) {
						int deletedDimId;
						try {
							deletedDimId = Integer.parseInt(string[2]);

							if(DimensionManager.getInstance().isDimensionCreated(deletedDimId)) {

								if(net.minecraftforge.common.DimensionManager.getWorld(deletedDimId) == null || net.minecraftforge.common.DimensionManager.getWorld(deletedDimId).playerEntities.isEmpty()) {
									DimensionManager.getInstance().deleteDimension(deletedDimId);
									PacketHandler.sendToAll(new PacketDimInfo(deletedDimId, null));
									sender.addChatMessage(new ChatComponentText("Deleted!"));
								}
								else {
									//If the world still has players abort and list players
									sender.addChatMessage(new ChatComponentText("World still has players:"));

									for(EntityPlayer player : (List<EntityPlayer>)net.minecraftforge.common.DimensionManager.getWorld(deletedDimId).playerEntities) {
										sender.addChatMessage(new ChatComponentText(player.getDisplayName()));
									}

								}


							} else {
								sender.addChatMessage(new ChatComponentText("Dimension does not exist"));
							}

						} catch(NumberFormatException exception) {
							sender.addChatMessage(new ChatComponentText("Invalid Argument"));
						}
					}
					else {
						sender.addChatMessage(new ChatComponentText(string[0] + " " + string[1] + " " + string[2] + " <dimid>"));
					}
				}
				/*
				 * Attempt to generate a planet
				 */
				else if(string[1].equalsIgnoreCase("generate")) {

					int gasOffset = 0;
					boolean gassy = false;
					boolean moon = false;
					int starId = 0;

					if(string.length > 2 ) {
						try {
							starId = Integer.parseInt(string[2]);
							gasOffset++;

						} catch(NumberFormatException e) {

						}
					}

					if(string.length > 2 + gasOffset) {
						if(string[2 + gasOffset].equalsIgnoreCase("moon")) {
							gasOffset++;
							moon = true;

							if(!DimensionManager.getInstance().isDimensionCreated(starId)) {
								sender.addChatMessage(new ChatComponentText("Invalid planet ID"));
								sender.addChatMessage(new ChatComponentText(string[0] + " " + string[1] + "[planetId] [moon] [gas] <name> <atmosphereRandomness> <distanceRandomness> <gravityRandomness>"));

								return;
							}
						}
						else if(DimensionManager.getInstance().getStar(starId) == null) {
							sender.addChatMessage(new ChatComponentText("Invalid star ID"));
							sender.addChatMessage(new ChatComponentText(string[0] + " " + string[1] + "[starId] [gas] <name> <atmosphereRandomness> <distanceRandomness> <gravityRandomness>"));

							return;
						}
					}

					if(string.length > 2 + gasOffset && string[2 + gasOffset].equalsIgnoreCase("gas")) {
						gasOffset++;
						gassy = true;
					}

					try {
						//Advancedrocketry planet generate <name> <atmosphereRandomness> <distanceRandomness> <gravityRandomness>
						if(string.length == 6 + gasOffset) {

							int planetId = starId;
							if(moon)
								starId = DimensionManager.getInstance().getDimensionProperties(planetId).getStarId();

							DimensionProperties properties;
							if(!gassy)
								properties = DimensionManager.getInstance().generateRandom(starId, string[2 + gasOffset], Integer.parseInt(string[3 + gasOffset]), Integer.parseInt(string[4 + gasOffset]), Integer.parseInt(string[5 + gasOffset]));
							else
								properties = DimensionManager.getInstance().generateRandomGasGiant(starId, string[2 + gasOffset], Integer.parseInt(string[3 + gasOffset]), Integer.parseInt(string[4 + gasOffset]), Integer.parseInt(string[5 + gasOffset]),1,1,1);

							if(properties == null)
								sender.addChatMessage(new ChatComponentText("Dimension: " + string[2 + gasOffset] + " failed to generate!"));
							else
								sender.addChatMessage(new ChatComponentText("Dimension: " + string[2 + gasOffset] + " Generated!"));

							if(moon) {
								properties.setParentPlanet(DimensionManager.getInstance().getDimensionProperties(planetId));
								DimensionManager.getInstance().getStar(starId).removePlanet(properties);
							}

							sender.addChatMessage(new ChatComponentText("Dimension Generated!"));
						}
						else if(string.length == 9  + gasOffset) {

							int planetId = starId;
							if(moon)
								starId = DimensionManager.getInstance().getDimensionProperties(planetId).getStarId();

							DimensionProperties properties;

							if(!gassy)
								properties = DimensionManager.getInstance().generateRandom(starId,string[2 + gasOffset] ,Integer.parseInt(string[3 + gasOffset]), Integer.parseInt(string[4 + gasOffset]), Integer.parseInt(string[5 + gasOffset]),Integer.parseInt(string[6 + gasOffset]), Integer.parseInt(string[7 + gasOffset]), Integer.parseInt(string[8 + gasOffset]));
							else
								properties = DimensionManager.getInstance().generateRandomGasGiant(starId, string[2 + gasOffset] ,Integer.parseInt(string[3 + gasOffset]), Integer.parseInt(string[4 + gasOffset]), Integer.parseInt(string[5 + gasOffset]),Integer.parseInt(string[6 + gasOffset]), Integer.parseInt(string[7 + gasOffset]), Integer.parseInt(string[8 + gasOffset]));

							if(properties == null)
								sender.addChatMessage(new ChatComponentText("Dimension: " + string[2 + gasOffset] + " failed to generate!"));
							else
								sender.addChatMessage(new ChatComponentText("Dimension: " + string[2 + gasOffset] + " Generated!"));

							if(moon) {
								properties.setParentPlanet(DimensionManager.getInstance().getDimensionProperties(planetId));
								DimensionManager.getInstance().getStar(starId).removePlanet(properties);
							}
						}
						else {
							sender.addChatMessage(new ChatComponentText(string[0] + " " + string[1] + " [starId] [moon] [gas] <name> <atmosphereRandomness> <distanceRandomness> <gravityRandomness>"));
							sender.addChatMessage(new ChatComponentText(""));
							sender.addChatMessage(new ChatComponentText(string[0] + " " + string[1] + " [starId] [moon] [gas] <name> <atmosphere base value> <distance base value> <gravity base value> <atmosphereRandomness> <distanceRandomness> <gravityRandomness>"));
						}
					} catch(NumberFormatException e) {
						sender.addChatMessage(new ChatComponentText(string[0] + " " + string[1] + " [starId] [moon] [gas] <name> <atmosphereRandomness> <distanceRandomness> <gravityRandomness>"));
						sender.addChatMessage(new ChatComponentText(""));
						sender.addChatMessage(new ChatComponentText(string[0] + " " + string[1] + " [starId] [moon] [gas] <name> <atmosphere base value> <distance base value> <gravity base value> <atmosphereRandomness> <distanceRandomness> <gravityRandomness>"));
					}
				}
				//Make sure player is in Dimension we have control over
				else if( DimensionManager.getInstance().isDimensionCreated((dimId = sender.getEntityWorld().provider.dimensionId)) ) {

					if(string[1].equalsIgnoreCase("set") && string.length > 2) {

						DimensionProperties properties = DimensionManager.getInstance().getDimensionProperties(dimId);


						try {
							if(string[2].equalsIgnoreCase("atmosphereDensity")) {
								properties.setAtmosphereDensityDirect(Integer.parseUnsignedInt(string[3]));
								PacketHandler.sendToAll(new PacketDimInfo(dimId, properties));
							}
							else {

								Field field = properties.getClass().getDeclaredField(string[2]);

								if(field.getType().isArray()) {

									if(Float.TYPE == field.getType().getComponentType()) {
										float var[] = (float[])field.get(properties);

										if(string.length - 3 == var.length) {

											//Make sure we catch if some invalid arg is entered
											for(int i = 0; i < var.length; i++) {
												var[i] = Float.parseFloat(string[3+i]);
											}

											field.set(properties, var);

										}
									}

									if(Integer.TYPE == field.getType().getComponentType()) {
										int var[] = (int[])field.get(properties);

										if(string.length - 3 == var.length) {

											//Make sure we catch if some invalid arg is entered

											for(int i = 0; i < var.length; i++) {
												var[i] = Integer.parseInt(string[3+i]);
											}

											field.set(properties, var);

										}
									}
								}
								else {
									if(Integer.TYPE == field.getType() )
										field.set(properties, Integer.parseInt(string[3]));
									else if(Float.TYPE == field.getType())
										field.set(properties, Float.parseFloat(string[3]));
									else if(Double.TYPE == field.getType()) 
										field.set(properties, Double.parseDouble(string[3]));
									else if(Boolean.TYPE == field.getType())
										field.set(properties, Boolean.parseBoolean(string[3]));
									else
										field.set(properties, string[3]);
								}

								PacketHandler.sendToAll(new PacketDimInfo(dimId, properties));
								return;
							}
						} catch (NumberFormatException e) {

							sender.addChatMessage(new ChatComponentText("Invalid Argument for parameter " + string[2]));
						} catch (Exception e) {

							e.printStackTrace();
						}
					}
					else if(string[1].equalsIgnoreCase("get") && string.length == 3) {
						DimensionProperties properties = DimensionManager.getInstance().getDimensionProperties(dimId);
						if(string[2].equalsIgnoreCase("atmosphereDensity")) {
							sender.addChatMessage(new ChatComponentText(Integer.toString(properties.getAtmosphereDensity())));
						} 
						else {
							try {
								Field field = properties.getClass().getDeclaredField(string[2]);

								sender.addChatMessage(new ChatComponentText(field.get(properties).toString()));

							} catch (Exception e) {

								e.printStackTrace();
							}
						}
					}
				}
			} //string[0] = planet
			else if(string[0].equals("star")) {
				if(string.length > 1) {
					if(string[1].equalsIgnoreCase("list")) {
						for(StellarBody star : DimensionManager.getInstance().getStars())
							sender.addChatMessage(new ChatComponentText(String.format("Star ID: %d   Name: %s  Num Planets: %d", star.getId(), star.getName(), star.getNumPlanets())));
					}
					else if(string[1].equalsIgnoreCase("help")) {
						printStarHelp(sender);
					}
				}
				if(string.length > 3) {
					if(string[1].equalsIgnoreCase("get")) {
						try {
							int id = Integer.parseInt(string[3]);
							StellarBody star =  DimensionManager.getInstance().getStar(id);
							if(star == null)
								sender.addChatMessage(new ChatComponentText("Error: " + string[3] + " is not a valid star ID"));
							else {
								if(string[2].equalsIgnoreCase("temp")) {
									sender.addChatMessage(new ChatComponentText("Temp: " + star.getTemperature()));
								}
								else if(string[2].equalsIgnoreCase("planets")) {
									sender.addChatMessage(new ChatComponentText("Planets orbiting the star:"));
									for(IDimensionProperties planets : star.getPlanets()) {
										sender.addChatMessage(new ChatComponentText("ID: " + planets.getId() + " : " + planets.getName()));
									}
								}
								else if(string[2].equalsIgnoreCase("pos")) {
									sender.addChatMessage(new ChatComponentText("Pos: " + star.getPosX() + "," + star.getPosZ()));
								}
							}// end star existance validation
						} catch (NumberFormatException e) {
							sender.addChatMessage(new ChatComponentText("Error: " + string[3] + " is not a valid star ID"));
						}
					} //get
				} if(string.length > 4) {
					if(string[1].equalsIgnoreCase("set")) {
						try {
							int id = Integer.parseInt(string[3]);
							StellarBody star =  DimensionManager.getInstance().getStar(id);
							if(star == null)
								sender.addChatMessage(new ChatComponentText("Error: " + string[3] + " is not a valid star ID"));
							else {
								if(string[2].equalsIgnoreCase("temp")) {
									try {
										star.setTemperature(Integer.parseInt(string[4]));
										sender.addChatMessage(new ChatComponentText("Temp set to " + star.getTemperature()));
									} catch(NumberFormatException e) {
										sender.addChatMessage(new ChatComponentText("star set temp <starId> <temp>"));
									}
								} else if(string.length > 5 && string[2].equalsIgnoreCase("pos")) {
									try {
										int x= Integer.parseInt(string[4]);
										int z = Integer.parseInt(string[5]);
										star.setPosX(x);
										star.setPosZ(z);
										sender.addChatMessage(new ChatComponentText("Position set to " + x + "," + z));
									} catch(NumberFormatException e) {
										sender.addChatMessage(new ChatComponentText("star set pos <starId> <x> <y>"));
									}
								}
							}// end star existance validation
						} catch (NumberFormatException e) {
							sender.addChatMessage(new ChatComponentText("Error: " + string[3] + " is not a valid star ID"));
						}
					}
				}
				if(string.length > 5) {
					if(string[1].equalsIgnoreCase("generate")) {
						try {
							String name = string[2];
							int temp = Integer.parseInt(string[3]);
							int x = Integer.parseInt(string[4]);
							int z = Integer.parseInt(string[5]);
							StellarBody star = new StellarBody();
							star.setTemperature(temp);
							star.setPosX(x);
							star.setPosZ(z);
							star.setName(name);
							star.setId(DimensionManager.getInstance().getNextFreeStarId());
							if(star.getId() != -1) {
								DimensionManager.getInstance().addStar(star);
								PacketHandler.sendToAll(new PacketStellarInfo(star.getId(), star));
								sender.addChatMessage(new ChatComponentText("star Added!"));
							}
							else
								sender.addChatMessage(new ChatComponentText("Why can't I hold all these stars! (either you have an insane number of stars or something really broke!)"));

						} catch(NumberFormatException e) {
							sender.addChatMessage(new ChatComponentText("star generate <name> <temp> <x> <y>"));
						}
					}
				}
			} //string[0] = star
		} // len > 2

	}

	private void printStarHelp(ICommandSender sender) {
		sender.addChatMessage(new ChatComponentText("star list"));
		sender.addChatMessage(new ChatComponentText("star get temp <star id>"));
		sender.addChatMessage(new ChatComponentText("star get planets <star id>"));
		sender.addChatMessage(new ChatComponentText("star get pos <star id>"));
		sender.addChatMessage(new ChatComponentText("star set temp <star id> <temperature>"));
		sender.addChatMessage(new ChatComponentText("star set pos <star id> <x> <y>"));
		sender.addChatMessage(new ChatComponentText("star generate <name> <temp> <x> <y>"));
	}

	@Override
	public boolean canCommandSenderUseCommand(ICommandSender sender) {
		return !sender.getCommandSenderName().equalsIgnoreCase("RCon") && sender.canCommandSenderUseCommand(2, getCommandName());
	}

	@Override
	public List addTabCompletionOptions(ICommandSender sender,
			String[] string) {
		ArrayList<String> list = new ArrayList<String>();

		if(string.length == 1) {
			list.add("planet");
			list.add("goto");
			list.add("fetch");
			list.add("star");
			list.add("reloadRecipes");
			list.add("givestation");
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
}
