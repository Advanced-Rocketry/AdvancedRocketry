package zmaster587.advancedRocketry.Inventory;

import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;

public class GuiSpySatellite extends GuiScreen {
	
	TileEntity tileEntity;
	public GuiSpySatellite(TileEntity tile, EntityPlayer player) {
		tileEntity = tile;
	}
}
