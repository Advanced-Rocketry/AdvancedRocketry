package zmaster587.advancedRocketry.inventory.modules;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.inventory.GuiContainer;
import zmaster587.libVulpes.util.IconResource;

public class ModuleImage extends ModuleBase {

	IconResource icon;
	
	public ModuleImage(int offsetX, int offsetY, IconResource icon) {
		super(offsetX, offsetY);
		this.icon = icon;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void renderBackground(GuiContainer gui, int x, int y, int mouseX, int mouseY,
			FontRenderer font) {
		super.renderBackground(gui, x, y, mouseX, mouseY, font);
		
		Minecraft.getMinecraft().getTextureManager().bindTexture(icon.getResourceLocation());
		gui.drawTexturedModalRect(x + offsetX, y + offsetY, icon.getxLoc(), icon.getyLoc(), icon.getxSize(), icon.getySize());
	}
	
}
