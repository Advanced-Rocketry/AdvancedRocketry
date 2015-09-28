package zmaster587.advancedRocketry.Inventory.modules;

import java.util.ArrayList;
import java.util.List;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.Tessellator;

public class ModuleText extends ModuleBase {

	List<String> text;
	int color;

	public ModuleText(int offsetX, int offsetY, String text, int color) {
		super(offsetX, offsetY);

		this.text = new ArrayList<String>();

		setText(text);
		this.color = color;
	}

	public void setText(String text) {

		this.text.clear();
		for(String str : text.split("\\n")) {
			this.text.add(str);
		}
	}

	public String getText() {

		String str = "";

		for(String str2 : this.text) {
			str += "\n" + str2;
		}

		return str.substring(1);
	}

	@Override
	public void renderBackground(GuiContainer gui, int x, int y, int mouseX, int mouseY, FontRenderer font) {

		GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);
		for(int i = 0; i < text.size(); i++) {
			font.drawString(text.get(i), x + offsetX, y + offsetY + i*font.FONT_HEIGHT, color);
		}
		GL11.glPopAttrib();
	}
}
