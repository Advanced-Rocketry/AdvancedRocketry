package zmaster587.advancedRocketry.inventory.modules;

import net.minecraft.client.gui.widget.button.AbstractButton;
import zmaster587.advancedRocketry.dimension.DimensionProperties;
import zmaster587.advancedRocketry.inventory.GuiPlanetButton;
import zmaster587.libVulpes.inventory.modules.IButtonInventory;
import zmaster587.libVulpes.inventory.modules.ModuleButton;

import java.util.LinkedList;
import java.util.List;

import net.minecraft.client.gui.widget.button.Button;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class ModuleButtonPlanet extends ModuleButton {

	DimensionProperties properties;
	
	public ModuleButtonPlanet(int offsetX, int offsetY,
			String text, IButtonInventory tile,
			DimensionProperties properties, String tooltipText, int sizeX,
			int sizeY) {
		super(offsetX, offsetY, text, tile, null, tooltipText, sizeX,
				sizeY);
		this.properties = properties;
	}
	
	@OnlyIn(value=Dist.CLIENT)
	public List<AbstractButton> addButtons(int x, int y) {

		List<AbstractButton> list = new LinkedList<>();

		button = new GuiPlanetButton(x + offsetX, y + offsetY, sizeX, sizeY, properties);

		button.visible = visible;

		if(!sound.isEmpty()) {
			button.setSound(sound);
			sound = "";
		}

		button.setBackgroundColor(bgColor);
		list.add(button);

		return list;
	}

}
