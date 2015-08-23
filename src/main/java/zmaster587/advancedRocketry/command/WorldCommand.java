package zmaster587.advancedRocketry.command;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import zmaster587.advancedRocketry.world.DimensionManager;
import zmaster587.advancedRocketry.world.DimensionProperties;
import zmaster587.advancedRocketry.world.TeleporterNoPortal;
import zmaster587.advancedRocketry.world.biome.BiomeGenAlienForest;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ChatComponentText;

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

		if(string.length > 1) {

			if(string[0].equalsIgnoreCase("goto") && string.length == 2) {
				EntityPlayer player = sender.getEntityWorld().getPlayerEntityByName(sender.getCommandSenderName());

				try {
					int dim = Integer.parseInt(string[1]);

					if(player != null) {
						if(net.minecraftforge.common.DimensionManager.isDimensionRegistered(dim))
							MinecraftServer.getServer().getConfigurationManager().transferPlayerToDimension((EntityPlayerMP) player,  dim , new TeleporterNoPortal(MinecraftServer.getServer().worldServerForDimension(dim)));
						else
							sender.addChatMessage(new ChatComponentText("Dimension does not exist"));
					}
					else 
						sender.addChatMessage(new ChatComponentText("Must be a player to use this command"));
					
				} catch(NumberFormatException e) {
					sender.addChatMessage(new ChatComponentText(string[0] + " <dimId>"));
				}

			}
			else if(string[0].equalsIgnoreCase("planet")) {

				int dimId;
				if(string[1].equalsIgnoreCase("reset")) {
					if(string.length == 3) {
						try {
							dimId = Integer.parseInt(string[2]);
							DimensionManager.getInstance().getDimensionProperties(dimId).resetProperties();

						} catch (NumberFormatException e) {
							sender.addChatMessage(new ChatComponentText("Invalid dimId"));
						}
					}
					else if(string.length == 2) {
						if(DimensionManager.getInstance().isDimensionCreated((dimId = sender.getEntityWorld().provider.dimensionId))) {
							DimensionManager.getInstance().setDimProperties(dimId, new DimensionProperties(dimId));
						}
					}
				}
				else if(string[1].equalsIgnoreCase("tree")) {
					
					for(int x = -30; x < 30; x++)
						for(int y = 64; y < 120; y++) {
							for(int z = -30; z < 30; z++) {
								sender.getEntityWorld().setBlockToAir(x, y, z);
							}
						}
					
					BiomeGenAlienForest.alienTree.generate(sender.getEntityWorld(), new Random(), 0, 63, 0);
				}
				else if(string[1].equalsIgnoreCase("list")) { //Lists dimensions

					sender.addChatMessage(new ChatComponentText("Dimensions:"));
					for(int i : DimensionManager.getInstance().getregisteredDimensions()) {
						sender.addChatMessage(new ChatComponentText("DIM" + i + ":  " + DimensionManager.getInstance().getDimensionProperties(i).name)); 
					}
				} else if(string[1].equalsIgnoreCase("new")) {
					// advRocketry planet new <name>
					if(string.length == 3) {
						DimensionManager.getInstance().registerDim(new DimensionProperties(DimensionManager.getInstance().getNextFreeDim(), string[2]));
						sender.addChatMessage(new ChatComponentText("Dimension Created!"));
					}
					else {
						sender.addChatMessage(new ChatComponentText(string[0] + " " + string[1] + " " + string[2] + " <name>"));
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
									DimensionManager.getInstance().unregisterDimension(deletedDimId);
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
						sender.addChatMessage(new ChatComponentText(string[0] + " " + string[1] + " " + string[2] + " <name>"));
					}
				}
				else if(string[1].equalsIgnoreCase("generate")) {

					try {
						//Advancedrocketry planet generate <name> <atmosphereRandomness> <distanceRandomness> <gravityRandomness>
						if(string.length == 6) {
							DimensionManager.getInstance().generateRandom(string[2], Integer.parseInt(string[3]), Integer.parseInt(string[4]), Integer.parseInt(string[5]));
							sender.addChatMessage(new ChatComponentText("Dimension Generated!"));
						}
						else if(string.length == 9) {
							DimensionManager.getInstance().generateRandom(string[2] ,Integer.parseInt(string[3]), Integer.parseInt(string[4]), Integer.parseInt(string[5]),Integer.parseInt(string[6]), Integer.parseInt(string[7]), Integer.parseInt(string[8]));
						}
						else {
							sender.addChatMessage(new ChatComponentText(string[0] + " " + string[1] + " <name> <atmosphereRandomness> <distanceRandomness> <gravityRandomness>"));
							sender.addChatMessage(new ChatComponentText(""));
							sender.addChatMessage(new ChatComponentText(string[0] + " " + string[1] + " <name> <atmosphere base value> <distance base value> <gravity base value> <atmosphereRandomness> <distanceRandomness> <gravityRandomness>"));
						}
					} catch(NumberFormatException e) {
						sender.addChatMessage(new ChatComponentText(string[0] + " " + string[1] + " <name> <atmosphereRandomness> <distanceRandomness> <gravityRandomness>"));
						sender.addChatMessage(new ChatComponentText(""));
						sender.addChatMessage(new ChatComponentText(string[0] + " " + string[1] + " <name> <atmosphere base value> <distance base value> <gravity base value> <atmosphereRandomness> <distanceRandomness> <gravityRandomness>"));
					}
				}
				//Make sure player is in Dimension we have control over
				else if( DimensionManager.getInstance().isDimensionCreated((dimId = sender.getEntityWorld().provider.dimensionId)) ) {

					if(string[1].equalsIgnoreCase("set") && string.length > 2) {

						DimensionProperties properties = DimensionManager.getInstance().getDimensionProperties(dimId);

						try {
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
							return;

						} catch (NumberFormatException e) {

							sender.addChatMessage(new ChatComponentText("Invalid Argument for parameter " + string[2]));
						} catch (Exception e) {

							e.printStackTrace();
						}
					}
					else if(string[1].equalsIgnoreCase("get") && string.length == 3) {
						DimensionProperties properties = DimensionManager.getInstance().getDimensionProperties(dimId);
						
						try {
							Field field = properties.getClass().getDeclaredField(string[2]);
							
							sender.addChatMessage(new ChatComponentText(field.get(properties).toString()));
							
						} catch (Exception e) {

							e.printStackTrace();
						}
					}
				}
			} //string[0] = planet
		} // len > 2

	}

	@Override
	public boolean canCommandSenderUseCommand(ICommandSender sender) {
		return true;
	}

	@Override
	public List addTabCompletionOptions(ICommandSender sender,
			String[] string) {
		ArrayList<String> list = new ArrayList<String>();

		if(string.length == 1) {
			list.add("planet");
			list.add("goto");
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
		}

		return list;
	}

	@Override
	public boolean isUsernameIndex(String[] string, int number) {
		return false;
	}
}
