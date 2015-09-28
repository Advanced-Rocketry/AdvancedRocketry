package zmaster587.advancedRocketry.client.render.multiblocks;

import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.AdvancedModelLoader;
import net.minecraftforge.client.model.IModelCustom;
import net.minecraftforge.common.util.ForgeDirection;

import org.lwjgl.opengl.GL11;

import zmaster587.advancedRocketry.api.MaterialRegistry;
import zmaster587.advancedRocketry.tile.multiblock.TileMultiblockMachine;
import zmaster587.libVulpes.block.RotatableBlock;

public class RendererRollingMachine extends TileEntitySpecialRenderer {
	IModelCustom model = AdvancedModelLoader.loadModel(new ResourceLocation("advancedrocketry:models/rollingMachine.obj"));

	ResourceLocation texture = new ResourceLocation("advancedrocketry:textures/models/rollingMachine.png");


	@Override
	public void renderTileEntityAt(TileEntity tile, double x,
			double y, double z, float f) {
		TileMultiblockMachine multiBlockTile = (TileMultiblockMachine)tile;

		if(!multiBlockTile.canRender())
			return;

		GL11.glPushMatrix();

		//Initial setup
		int bright = tile.getWorldObj().getLightBrightnessForSkyBlocks(tile.xCoord, tile.yCoord + 1, tile.zCoord,0);
		int brightX = bright % 65536;
		int brightY = bright / 65536;
		OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, brightX, brightY);

		//Rotate and move the model into position
		GL11.glTranslated(x + .5f, y, z + 0.5f);
		ForgeDirection front = RotatableBlock.getFront(tile.getBlockMetadata());
		GL11.glRotatef((front.offsetX == 1 ? 180 : 0) + front.offsetZ*90f, 0, 1, 0);
		GL11.glTranslated(-.5f, -1f, -0.5f);

		if(multiBlockTile.isRunning()) {
			float progress = multiBlockTile.getProgress(0)/(float)multiBlockTile.getTotalProgress(0);

			bindTexture(texture);
			model.renderOnly("Hull");

			GL11.glPushMatrix();
			GL11.glTranslatef(2.12f, 1.0f, 2.56f);
			GL11.glRotatef(-progress*720, 1, 0, 0);
			model.renderOnly("Roller1");
			GL11.glPopMatrix();

			GL11.glPushMatrix();
			GL11.glTranslatef(2.12f, 0.375f,2.18f);
			GL11.glRotatef(progress*720, 1, 0, 0);
			model.renderOnly("Roller2");
			GL11.glPopMatrix();

			GL11.glPushMatrix();
			GL11.glTranslatef(2.12f, 0.375f, 2.93f);
			GL11.glRotatef(15 + progress*720, 1, 0, 0);
			model.renderOnly("Roller2");
			GL11.glPopMatrix();
			
			
			
			int color = MaterialRegistry.getMaterialFromItemStack(multiBlockTile.getOutputs().get(0)).getColor();
			GL11.glColor3d((0xff & color >> 16)/256f, (0xff & color >> 8)/256f , (color & 0xff)/256f);
			
			//Render the ingot
			if(progress < 0.6f) {
				GL11.glPushMatrix();
				GL11.glDisable(GL11.GL_TEXTURE_2D);
				GL11.glTranslatef(2.125f, 0.875f, 1.3125f + progress*2f);
				model.renderOnly("Ingot");	
				GL11.glEnable(GL11.GL_TEXTURE_2D);
				GL11.glPopMatrix();
			}
			//Render thr plate
			if(progress > 0.5f) {

				GL11.glPushMatrix();
				GL11.glDisable(GL11.GL_TEXTURE_2D);
				GL11.glTranslatef(2.125f, 0.875f, 1.7125f + progress*2f);
				model.renderOnly("Plate");	
				GL11.glEnable(GL11.GL_TEXTURE_2D);
				GL11.glPopMatrix();
			}
			GL11.glColor3f(1f,1f,1f);
		}
		else {
			bindTexture(texture);
			model.renderOnly("Hull");

			GL11.glPushMatrix();
			GL11.glTranslatef(2.12f, 1.0f, 2.56f);
			model.renderOnly("Roller1");
			GL11.glPopMatrix();

			GL11.glPushMatrix();
			GL11.glTranslatef(2.12f, 0.375f,2.18f);
			model.renderOnly("Roller2");
			GL11.glPopMatrix();

			GL11.glPushMatrix();

			GL11.glTranslatef(2.12f, 0.375f, 2.93f);
			GL11.glRotatef(15, 1, 0, 0);

			model.renderOnly("Roller2");
			GL11.glPopMatrix();

		}
		GL11.glPopMatrix();
	}
}
