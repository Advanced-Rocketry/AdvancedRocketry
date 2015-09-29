package zmaster587.advancedRocketry.util;

import java.util.ArrayList;
import java.util.List;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.ChatComponentText;
import net.minecraftforge.client.event.ClientChatReceivedEvent;

public class Debugger implements ICommand {
	
	public static boolean renderList = false;
	@SubscribeEvent
	public void chatRecievedEvent(ClientChatReceivedEvent event) {
	}

	@Override
	public int compareTo(Object arg0) {
		return new String("changeRender").compareTo((String) arg0);
	}

	@Override
	public String getCommandName() {
		return "changeRender";
	}

	@Override
	public String getCommandUsage(ICommandSender sender) {
		return "mute help";
	}

	@Override
	public List getCommandAliases() {
		return null;
	}

	@Override
	public void processCommand(ICommandSender sender, String[] command) {
		renderList = !renderList;
		sender.addChatMessage(new ChatComponentText("GL Lists now " + (renderList ? "codePoo" : "codeCrap")));
	}

	@Override
	public boolean canCommandSenderUseCommand(ICommandSender p_71519_1_) {
		return true;
	}

	@Override
	public List addTabCompletionOptions(ICommandSender p_71516_1_,
			String[] p_71516_2_) {
		List <String> list = new ArrayList<String>();
		list.add("changeRender");
		return list;
	}

	@Override
	public boolean isUsernameIndex(String[] string, int num) {
		return true;
	}
}
