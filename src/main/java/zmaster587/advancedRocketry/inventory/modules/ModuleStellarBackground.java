package zmaster587.advancedRocketry.inventory.modules;

import com.mojang.blaze3d.matrix.MatrixStack;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.inventory.container.Container;
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
	public void renderBackground(ContainerScreen<? extends Container> gui, MatrixStack matrix, int x, int y, int mouseX,
			int mouseY, FontRenderer font) {
		if(this.icon != null) {
<<<<<<< HEAD
			Minecraft.getInstance().getTextureManager().bindTexture(this.icon);
			gui.blit(matrix, 0, 0, 0, 0, gui.getXSize(),  gui.getYSize());
			Minecraft.getInstance().getTextureManager().bindTexture(TextureResources.genericStation);
			gui.blit(matrix, (int)(256), (int)(64), 0, 0, 256, 256);
=======
			gui.mc.getTextureManager().bindTexture(this.icon);
			gui.drawTexturedModalRect(0, 0, 0, 0, gui.width,  gui.height);
			gui.mc.getTextureManager().bindTexture(TextureResources.genericStation);
			gui.drawTexturedModalRect(256, 64, 0, 0, 256, 256);
>>>>>>> origin/feature/nuclearthermalrockets
		}
	}
}
