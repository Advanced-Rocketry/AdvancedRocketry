package zmaster587.advancedRocketry.client.render.multiblocks;

import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.ModelFormatException;
import net.minecraftforge.client.model.obj.WavefrontObject;
import net.minecraftforge.common.util.ForgeDirection;

import org.lwjgl.opengl.GL11;

import zmaster587.advancedRocketry.client.render.RenderLaser;
import zmaster587.advancedRocketry.tile.multiblock.TileSpaceElevator;
import zmaster587.libVulpes.block.RotatableBlock;
import zmaster587.libVulpes.render.RenderHelper;

public class RendererSpaceElevator extends TileEntitySpecialRenderer {
	
	WavefrontObject model;

	public ResourceLocation baseTexture =  new ResourceLocation("advancedRocketry:textures/models/spaceElevator.jpg");
	RenderLaser laser;
	
	public RendererSpaceElevator() {
		laser = new RenderLaser(1, new float[] { 0,0 , 0, 0}, new float[] { 1, 1 , 0, 0.11f} );
		try {
			model = new WavefrontObject(new ResourceLocation("advancedrocketry:models/spaceElevator.obj"));
		} catch (ModelFormatException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void renderTileEntityAt(TileEntity tile, double x,
			double y, double z, float f) {
		TileSpaceElevator multiBlockTile = (TileSpaceElevator)tile;

		if(!multiBlockTile.canRender())
			return;
		
		double renderX = x + multiBlockTile.getLandingLocationX() - multiBlockTile.xCoord;
		double renderZ = z + multiBlockTile.getLandingLocationZ() - multiBlockTile.zCoord;
		
		laser.doRender((Entity)null, renderX - .5, y+2.5, renderZ - .5, 0, f);

		GL11.glPushMatrix();

		//Initial setup

		GL11.glTranslated(x + 0.5, y, z + .5);
		//Rotate and move the model into position
		ForgeDirection front = RotatableBlock.getFront(tile.getWorldObj().getBlockMetadata(tile.xCoord, tile.yCoord, tile.zCoord));
		GL11.glRotatef((front.offsetX == 1 ? 180 : 0) + front.offsetZ*90f, 0, 1, 0);
		//GL11.glTranslated(2f, 0, 0f);
		bindTexture(baseTexture);
		model.renderOnly("Base");
		
		GL11.glPopMatrix();
		
		//Render Beads
		GL11.glPushMatrix();
		GL11.glTranslated(renderX, y, renderZ);
		Tessellator buffer = Tessellator.instance;
		GL11.glDisable(GL11.GL_LIGHTING);
		GL11.glDisable(GL11.GL_FOG);
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glDepthMask(false);

		GL11.glDisable(GL11.GL_TEXTURE_2D);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE);

		buffer.startDrawingQuads();
		GL11.glColor4f(1, 1 , 1 , 0.11f);

		double position = (System.currentTimeMillis() % 16000)/200f;

		for(int i = 0 ; i < 10; i++) {
			for(float radius = 0.25F; radius < 1.25; radius += .25F) {

				RenderHelper.renderCubeWithUV(buffer, -radius, -radius + position + i*80 + 4f, -radius, radius, radius + position + i*80 + 4f, radius,0,0,0,0);

			}
		}
		for(int i = 1 ; i < 11; i++) {
			for(float radius = 0.25F; radius < 1.25; radius += .25F) {

				RenderHelper.renderCubeWithUV(buffer, -radius, -radius - position + i*80 + 4, -radius, radius, radius - position + i*80 + 4, radius, 0,0,0,0);

			}
		}

		buffer.draw();

		GL11.glDisable(GL11.GL_BLEND);
		GL11.glEnable(GL11.GL_LIGHTING);
		GL11.glEnable(GL11.GL_TEXTURE_2D);
		GL11.glEnable(GL11.GL_FOG);
		GL11.glDepthMask(true);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		GL11.glPopMatrix();
	}
}
