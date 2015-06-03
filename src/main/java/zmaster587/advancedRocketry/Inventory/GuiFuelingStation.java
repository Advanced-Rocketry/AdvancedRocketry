package zmaster587.advancedRocketry.Inventory;

import org.lwjgl.opengl.GL11;

import zmaster587.advancedRocketry.tile.TileEntityFuelingStation;
import zmaster587.libVulpes.gui.CommonResources;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.IIcon;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.FluidTankInfo;

public class GuiFuelingStation extends GuiProgressBarContainer {

	TileEntityFuelingStation tile;

	public GuiFuelingStation(InventoryPlayer inv ,TileEntityFuelingStation tile) {
		super(new ContainerFuelingStation(inv, tile));
		this.tile = tile;
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float p_146976_1_,
			int p_146976_2_, int p_146976_3_) {

		this.mc.renderEngine.bindTexture(CommonResources.genericBackground);

		int x = (width - xSize) / 2, y = (height - ySize) / 2;
		this.drawTexturedModalRect(x, y, 0, 0, 176, 171);

		//Tank BG
		this.drawTexturedModalRect(x + 26, y + 17, 176, 58, 14, 54);

		//Top slot
		this.drawTexturedModalRect(x + 44, y + 17, 176, 0, 18, 18);

		//Arrow
		this.drawTexturedModalRect(x + 44, y + 35, 194, 0, 18, 18);

		//Bottom slot
		this.drawTexturedModalRect(x + 44, y + 53, 176, 0, 18, 18);

		//power bar
		this.drawTexturedModalRect(x + 154, y + 21, 176, 18, 8, 40);

		//Battery Icon
		this.drawTexturedModalRect(x + 156, y + 62, 15, 171, 4, 9);

		//Power amount
		this.drawProgressBarVertical(x + 155, y + 22, 0, 177, 6, 38, tile.getPower()/(float)tile.getMaxEnergyStored(ForgeDirection.UNKNOWN));

		//Draw Fluid
		FluidTankInfo info = tile.getTankInfo(ForgeDirection.UNKNOWN)[0];
		if(info.fluid != null) {
			IIcon fluidIcon = info.fluid.getFluid().getIcon();
			
			mc.renderEngine.bindTexture(TextureMap.locationBlocksTexture);
			
			int color = info.fluid.getFluid().getColor(info.fluid);
			
			GL11.glColor3b((byte)((color >> 16) & 127), (byte)((color >> 8) & 127), (byte)(color & 127));
			//GL11.glColor3b((byte)127, (byte)127, (byte)127);
			
			this.drawProgressBarIconVertical(x + 27, y + 18, fluidIcon, 12, 52, info.fluid.amount/(float)info.capacity);
		}
	} 

}
