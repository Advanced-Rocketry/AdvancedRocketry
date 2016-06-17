package zmaster587.advancedRocketry.client.render;

import org.lwjgl.opengl.GL11;

import zmaster587.advancedRocketry.tile.TileDrill;
import zmaster587.advancedRocketry.tile.multiblock.TileMultiblockMachine;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.AdvancedModelLoader;
import net.minecraftforge.client.model.IModelCustom;

public class RendererDrill  extends TileEntitySpecialRenderer {

	
	IModelCustom model = AdvancedModelLoader.loadModel(new ResourceLocation("advancedrocketry:models/drill.obj"));

	ResourceLocation texture = new ResourceLocation("advancedrocketry:textures/models/drill.png");
	
	
	@Override
	public void renderTileEntityAt(TileEntity tile, double x,
			double y, double z, float t) {
		
		
		TileDrill drillTile = (TileDrill)tile;

		if(drillTile.getDistanceExtended() == 0f)
			return;

		GL11.glPushMatrix();

		//Initial setup
		int bright = tile.getWorldObj().getLightBrightnessForSkyBlocks(tile.xCoord, tile.yCoord + 1, tile.zCoord,0);
		int brightX = bright % 65536;
		int brightY = bright / 65536;
		OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, brightX, brightY);
		
		
		GL11.glTranslated(x + 0.5, y + 1.5f, z + 0.5);
		bindTexture(texture);
		GL11.glScaled(1.5, 1.5, 1.5);
		model.renderOnly("Structure");
		
		GL11.glPushMatrix();
		double rotation = (tile.getWorldObj().getTotalWorldTime() % 30L)*10;
		double yOffset = 0.025*MathHelper.sin((tile.getWorldObj().getTotalWorldTime() & 0xffff)*1.1f);
		GL11.glTranslated(0, yOffset + drillTile.getDistanceExtended()*0.3f, 0);
		GL11.glRotated(rotation, 0, 1.0, 0);
		
		model.renderOnly("DrillHead");
		
		GL11.glPopMatrix();
		GL11.glPopMatrix();
	}

}
