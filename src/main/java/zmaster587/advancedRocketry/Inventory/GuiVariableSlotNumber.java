package zmaster587.advancedRocketry.Inventory;

import zmaster587.advancedRocketry.entity.EntityRocket;
import zmaster587.libVulpes.gui.CommonResources;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ISidedInventory;

public class GuiVariableSlotNumber extends GuiContainer {

	int startSlot, endSlot;
	IInventory inv;
	IInventory playerInv;
	
	public GuiVariableSlotNumber(EntityPlayer player, IInventory tile, int min, int max) {
		super(new ContainerVariableSlotNumber(player, tile, min, max));
		
		this.playerInv = player.inventory;
		this.inv = tile;
		startSlot = min;
		endSlot = max;
	}

    protected void drawGuiContainerForegroundLayer(int p_146979_1_, int p_146979_2_)
    {
        this.fontRendererObj.drawString(this.inv.hasCustomInventoryName() ? this.inv.getInventoryName() : I18n.format(this.inv.getInventoryName(), new Object[0]), 8, 6, 4210752);
        this.fontRendererObj.drawString(this.playerInv.hasCustomInventoryName() ? this.playerInv.getInventoryName() : I18n.format(this.playerInv.getInventoryName(), new Object[0]), 8, this.ySize - 96 + 2, 4210752);
    }
	
	@Override
	protected void drawGuiContainerBackgroundLayer(float p_146976_1_,
			int p_146976_2_, int p_146976_3_) {
		
		this.mc.renderEngine.bindTexture(CommonResources.genericBackground);
		
		int x = (width - xSize) / 2, y = (height - ySize) / 2;
		this.drawTexturedModalRect(x, y, 0, 0, 176, 171);
		
		for(int i = 0; i + startSlot < endSlot; i++) {
			this.drawTexturedModalRect(x + 7 + (18* (i % 9)), y + 16 + (18*(i/9)), 176, 0, 18, 18);
		}
		
	}

}
