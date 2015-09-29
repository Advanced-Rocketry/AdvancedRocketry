package zmaster587.advancedRocketry.client.render.multiblocks;

import org.lwjgl.opengl.GL11;

import zmaster587.advancedRocketry.tile.multiblock.TileMultiPowerConsumer;
import zmaster587.advancedRocketry.tile.multiblock.TileObservatory;
import zmaster587.advancedRocketry.util.Debugger;
import zmaster587.libVulpes.block.RotatableBlock;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.AdvancedModelLoader;
import net.minecraftforge.client.model.IModelCustom;
import net.minecraftforge.common.util.ForgeDirection;

public class RendererObservatory  extends TileEntitySpecialRenderer {

	IModelCustom model = AdvancedModelLoader.loadModel(new ResourceLocation("advancedrocketry:models/observatory.obj"));

	ResourceLocation texture = new ResourceLocation("advancedrocketry:textures/models/T1Observatory.png");

	private static int bodyList;

	public RendererObservatory() {
		GL11.glNewList(bodyList = GL11.glGenLists(1), GL11.GL_COMPILE);
		model.renderOnly("body");
		GL11.glEndList();
	}


	@Override
	public void renderTileEntityAt(TileEntity tile, double x,
			double y, double z, float f) {
		TileObservatory multiBlockTile = (TileObservatory)tile;

		if(!multiBlockTile.canRender())
			return;

		GL11.glPushMatrix();

		//Initial setup
		int bright = tile.getWorldObj().getLightBrightnessForSkyBlocks(tile.xCoord, tile.yCoord + 1, tile.zCoord,0);
		int brightX = bright % 65536;
		int brightY = bright / 65536;
		OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, brightX, brightY);

		//Rotate and move the model into position
		ForgeDirection front = RotatableBlock.getFront(tile.getBlockMetadata());//tile.getWorldObj().getBlockMetadata(tile.xCoord, tile.yCoord, tile.zCoord));
		GL11.glTranslated(x + .5, y, z + .5);
		GL11.glRotatef((front.offsetX == 1 ? 180 : 0) + front.offsetZ*90f, 0, 1, 0);

		GL11.glTranslated(2, -1, 0);

		bindTexture(texture);

		float offset = multiBlockTile.getOpenProgress();

		if(offset != 0f) {
			if(Debugger.renderList)
				GL11.glCallList(bodyList);
			else 
				model.renderOnly("Base");

			model.renderPart("Scope");
			model.renderPart("Axis");

			GL11.glPushMatrix();
			GL11.glTranslatef(0, 0, -offset);
			model.renderOnly("CasingXMinus");
			GL11.glPopMatrix();

			GL11.glPushMatrix();
			GL11.glTranslatef(0,0,offset);
			model.renderOnly("CasingXPlus");
			GL11.glPopMatrix();

		}
		else {
			if(Debugger.renderList)
				GL11.glCallList(bodyList);
			else 
				model.renderOnly("Base");
			model.renderOnly("CasingXMinus");
			model.renderOnly("CasingXPlus");
		}
		GL11.glPopMatrix();
	}
}
