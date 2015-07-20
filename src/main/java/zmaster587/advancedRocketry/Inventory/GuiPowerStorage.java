package zmaster587.advancedRocketry.Inventory;

import zmaster587.libVulpes.api.IUniversalEnergy;
import zmaster587.libVulpes.gui.CommonResources;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraftforge.common.util.ForgeDirection;

public class GuiPowerStorage extends GuiProgressBarContainer {

	IUniversalEnergy tile;
	IInventory playerInv;

	public GuiPowerStorage(InventoryPlayer playerInv, IUniversalEnergy tile) {
		super(new ContainerPowerStorageBox(playerInv, tile));
		this.tile = tile;
		this.playerInv = playerInv;
	}
	
	public GuiPowerStorage(ContainerPowered container, InventoryPlayer playerInv, IUniversalEnergy tile) {
		super(container);
		this.tile = tile;
		this.playerInv = playerInv;
	}

	@Override
	protected void drawGuiContainerForegroundLayer(int a,
			int b) {
		super.drawGuiContainerForegroundLayer(a, b);

		if(tile instanceof IInventory)
		this.fontRendererObj.drawString(((IInventory)tile).hasCustomInventoryName() ? ((IInventory)tile).getInventoryName() : I18n.format(((IInventory)tile).getInventoryName(), new Object[0]), 8, 6, 4210752);

		this.fontRendererObj.drawString(this.playerInv.hasCustomInventoryName() ? this.playerInv.getInventoryName() : I18n.format(this.playerInv.getInventoryName(), new Object[0]), 8, this.ySize - 96 + 2, 4210752);
		
		//Draw energy
		this.fontRendererObj.drawString("Stored:", 24, this.ySize - 147, 4210752);
		this.fontRendererObj.drawString(tile.getEnergyStored(ForgeDirection.UNKNOWN) + " RF", 24, this.ySize - 137, 4210752);
		
		this.fontRendererObj.drawString("Capacity:", 24, this.ySize - 114, 4210752);
		this.fontRendererObj.drawString(tile.getMaxEnergyStored(ForgeDirection.UNKNOWN) + " RF", 24, this.ySize - 104, 4210752);
	}


	@Override
	protected void drawGuiContainerBackgroundLayer(float p_146976_1_,
			int p_146976_2_, int p_146976_3_) {

		this.mc.renderEngine.bindTexture(CommonResources.genericBackground);

		int x = (width - xSize) / 2, y = (height - ySize) / 2;
		this.drawTexturedModalRect(x, y, 0, 0, 176, 171);

		//Power bar background
		this.drawTexturedModalRect(x + 10, y+ 29, 176, 18, 8, 40);
		
		//Battery Icon
		this.drawTexturedModalRect(x + 12, y + 18, 15, 171, 4, 9);
		
		//Power Bar
		this.drawProgressBarVertical(x + 11, y + 30, 0, 171, 6, 38, tile.getEnergyStored(ForgeDirection.UNKNOWN)/(float)tile.getMaxEnergyStored(ForgeDirection.UNKNOWN));
	}
}
