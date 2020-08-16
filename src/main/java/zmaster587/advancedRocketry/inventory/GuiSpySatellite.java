package zmaster587.advancedRocketry.inventory;

import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.tileentity.TileEntity;

public class GuiSpySatellite extends GuiScreen {
	
	TileEntity tileEntity;
	public GuiSpySatellite(TileEntity tile, PlayerEntity player) {
		tileEntity = tile;
	}
}
