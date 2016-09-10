package zmaster587.advancedRocketry.command;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import zmaster587.advancedRocketry.api.Configuration;
import zmaster587.advancedRocketry.api.IAtmosphere;
import zmaster587.advancedRocketry.api.stations.ISpaceObject;
import zmaster587.advancedRocketry.atmosphere.AtmosphereHandler;
import zmaster587.advancedRocketry.atmosphere.AtmosphereType;
import zmaster587.advancedRocketry.dimension.DimensionManager;
import zmaster587.advancedRocketry.dimension.DimensionProperties;
import zmaster587.advancedRocketry.network.PacketDimInfo;
import zmaster587.advancedRocketry.stations.SpaceObject;
import zmaster587.advancedRocketry.stations.SpaceObjectManager;
import zmaster587.advancedRocketry.world.biome.BiomeGenAlienForest;
import zmaster587.advancedRocketry.world.biome.BiomeGenDeepSwamp;
import zmaster587.advancedRocketry.world.util.TeleporterNoPortal;
import zmaster587.libVulpes.network.PacketHandler;
import zmaster587.libVulpes.util.BlockPosition;
import zmaster587.libVulpes.util.Vector3F;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
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
					for(int i : DimensionManager.getInstance().getregisteredDimensions()) {
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

					if(string.length > 2 && string[2].equalsIgnoreCase("gas")) {
						gasOffset = 1;
					}

					try {
						//Advancedrocketry planet generate <name> <atmosphereRandomness> <distanceRandomness> <gravityRandomness>
						if(string.length == 6 + gasOffset) {
							if(gasOffset == 0)
								DimensionManager.getInstance().generateRandom(string[2 + gasOffset], Integer.parseInt(string[3 + gasOffset]), Integer.parseInt(string[4 + gasOffset]), Integer.parseInt(string[5 + gasOffset]));
							else
								DimensionManager.getInstance().generateRandomGasGiant(string[2 + gasOffset], Integer.parseInt(string[3 + gasOffset]), Integer.parseInt(string[4 + gasOffset]), Integer.parseInt(string[5 + gasOffset]),0,0,0);

							sender.addChatMessage(new ChatComponentText("Dimension Generated!"));
						}
						else if(string.length == 9  + gasOffset) {
							if(gasOffset == 0)
								DimensionManager.getInstance().generateRandom(string[2 + gasOffset] ,Integer.parseInt(string[3 + gasOffset]), Integer.parseInt(string[4 + gasOffset]), Integer.parseInt(string[5 + gasOffset]),Integer.parseInt(string[6 + gasOffset]), Integer.parseInt(string[7 + gasOffset]), Integer.parseInt(string[8 + gasOffset]));
							else
								DimensionManager.getInstance().generateRandomGasGiant(string[2 + gasOffset] ,Integer.parseInt(string[3 + gasOffset]), Integer.parseInt(string[4 + gasOffset]), Integer.parseInt(string[5 + gasOffset]),Integer.parseInt(string[6 + gasOffset]), Integer.parseInt(string[7 + gasOffset]), Integer.parseInt(string[8 + gasOffset]));

							sender.addChatMessage(new ChatComponentText("Dimension: " + string[2 + gasOffset] + " Generated!"));
						}
						else {
							sender.addChatMessage(new ChatComponentText(string[0] + " " + string[1] + "[gas] <name> <atmosphereRandomness> <distanceRandomness> <gravityRandomness>"));
							sender.addChatMessage(new ChatComponentText(""));
							sender.addChatMessage(new ChatComponentText(string[0] + " " + string[1] + "[gas] <name> <atmosphere base value> <distance base value> <gravity base value> <atmosphereRandomness> <distanceRandomness> <gravityRandomness>"));
						}
					} catch(NumberFormatException e) {
						sender.addChatMessage(new ChatComponentText(string[0] + " " + string[1] + "[gas] <name> <atmosphereRandomness> <distanceRandomness> <gravityRandomness>"));
						sender.addChatMessage(new ChatComponentText(""));
						sender.addChatMessage(new ChatComponentText(string[0] + " " + string[1] + "[gas] <name> <atmosphere base value> <distance base value> <gravity base value> <atmosphereRandomness> <distanceRandomness> <gravityRandomness>"));
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
		} // len > 2

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
		} else if(string.length == 2) {
			ArrayList<String> list2 = new ArrayList<String>();
			list2.add("get");
			list2.add("set");
			list2.add("reset");
			list2.add("new");
			list2.add("delete");
			list2.add("list");
			list2.add("generate");


			for(String str : list2) {
				if(str.startsWith(string[1]))
					list.add(str);
			}

		} else if(( string[1].equalsIgnoreCase("get") || string[1].equalsIgnoreCase("set")) && string.length == 3) {
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
