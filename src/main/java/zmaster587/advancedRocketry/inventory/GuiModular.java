package zmaster587.advancedRocketry.inventory;

import java.util.List;

import zmaster587.advancedRocketry.inventory.modules.IModularInventory;
import zmaster587.advancedRocketry.inventory.modules.ModuleBase;
import zmaster587.libVulpes.gui.CommonResources;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraftforge.common.util.ForgeDirection;

public class GuiModular extends GuiContainer {
	
	List<ModuleBase> modules;
	String unlocalizedName;
	boolean hasSlots;
	
	public GuiModular(EntityPlayer playerInv, List<ModuleBase> modules, IModularInventory modularInv, boolean includePlayerInv, boolean includeHotBar, String name) {
		super(new ContainerModular(playerInv, modules,modularInv, includePlayerInv, includeHotBar));
		this.modules = modules;
		unlocalizedName = name;
		hasSlots = includePlayerInv;
	}
	
	@Override
	public void initGui() {
		super.initGui();
		
		int x = (width - xSize) / 2;
		int y = (height - ySize) / 2;
		
		for(ModuleBase module : modules) {
			List<GuiButton> buttonList = module.addButtons(x, y);
			if(!buttonList.isEmpty()) {
				this.buttonList.addAll(buttonList);
			}
		}
	}
	
	@Override
	public void actionPerformed(GuiButton button) {
		super.actionPerformed(button);
		
		for(ModuleBase module : modules) {
			module.actionPerform(button);
		}
		
	}

	@Override
	protected void drawGuiContainerForegroundLayer(int a,
			int b) {
		super.drawGuiContainerForegroundLayer(a, b);
		
		this.fontRendererObj.drawString(I18n.format(unlocalizedName, new Object[0]), 8, 6, 4210752);

		for(ModuleBase module : modules)
			module.renderForeground((width - xSize)/2, (height - ySize) / 2,a - (width - xSize)/2 ,b - (height - ySize) / 2, zLevel, this, this.fontRendererObj);
		
	}
	
	@Override
	protected void mouseClicked(int x, int y, int button) {

		super.mouseClicked(x, y, button);
		
		for(ModuleBase module : modules)
			module.onMouseClicked(this, x - (width - xSize) / 2, y - (height - ySize) / 2, button);
	}
	
	@Override
	protected void mouseClickMove(int x, int y,
			int button, long timeSinceLastClick) {
		super.mouseClickMove(x, y, button, timeSinceLastClick);
		
		for(ModuleBase module : modules)
			module.onMouseClickedAndDragged(x - (width - xSize) / 2, y - (height - ySize) / 2, button,timeSinceLastClick);
	}
	
	@Override
	protected void drawGuiContainerBackgroundLayer(float f1,
			int i2, int i3) {
		this.mc.renderEngine.bindTexture(CommonResources.genericBackground);
		
		int x = (width - xSize) / 2, y = (height - ySize) / 2;
		this.drawTexturedModalRect(x, y, 0, 0, 176, 171);
		
		if(!hasSlots) {
			this.drawTexturedModalRect(x + 7, y + 88, 7, 12, 162, 54);
		}
		
		for(ModuleBase module : modules) {
			module.renderBackground(this, x, y, i2, i3, fontRendererObj);
		}
	}
}
