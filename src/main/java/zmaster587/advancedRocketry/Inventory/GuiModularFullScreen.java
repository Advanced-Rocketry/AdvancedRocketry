package zmaster587.advancedRocketry.Inventory;

import java.util.List;

import zmaster587.advancedRocketry.Inventory.modules.IModularInventory;
import zmaster587.advancedRocketry.Inventory.modules.ModuleBase;
import zmaster587.libVulpes.gui.CommonResources;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;

public class GuiModularFullScreen extends GuiModular {

	public GuiModularFullScreen(EntityPlayer playerInv,
			List<ModuleBase> modules, IModularInventory modularInv,
			boolean includePlayerInv, boolean includeHotBar, String name) {
		super(playerInv, modules, modularInv, includePlayerInv,includeHotBar, name);
		
		this.xSize = Minecraft.getMinecraft().displayWidth;
		this.ySize = Minecraft.getMinecraft().displayHeight;
		this.width = Minecraft.getMinecraft().displayWidth;
		this.height = Minecraft.getMinecraft().displayHeight;
	}
	
	@Override
	public void initGui() {
		this.xSize = Minecraft.getMinecraft().displayWidth;
		this.ySize = Minecraft.getMinecraft().displayHeight;
		this.width = Minecraft.getMinecraft().displayWidth;
		this.height = Minecraft.getMinecraft().displayHeight;
		
		super.initGui();
	}
}
