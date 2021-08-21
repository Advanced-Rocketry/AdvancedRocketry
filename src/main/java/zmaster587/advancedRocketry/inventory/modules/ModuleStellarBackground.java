package zmaster587.advancedRocketry.inventory.modules;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.util.ResourceLocation;
import zmaster587.advancedRocketry.inventory.TextureResources;
import zmaster587.libVulpes.inventory.modules.ModuleBase;

public class ModuleStellarBackground extends ModuleBase {

	ResourceLocation icon;
	
	public ModuleStellarBackground(int offsetX, int offsetY, ResourceLocation icon) {
		super(offsetX, offsetY);
		this.icon = icon;
	}
	
	@Override
	public void renderBackground(GuiContainer gui, int x, int y, int mouseX,
			int mouseY, FontRenderer font) {
		if(this.icon != null) {
			gui.mc.getTextureManager().bindTexture(this.icon);
			gui.drawTexturedModalRect(0, 0, 0, 0, gui.width,  gui.height);
			gui.mc.getTextureManager().bindTexture(TextureResources.genericStation);
			gui.drawTexturedModalRect(256, 64, 0, 0, 256, 256);
		}
	}
}
