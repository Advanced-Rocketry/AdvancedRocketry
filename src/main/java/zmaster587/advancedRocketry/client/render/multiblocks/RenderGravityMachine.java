package zmaster587.advancedRocketry.client.render.multiblocks;

import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.ModelFormatException;
import net.minecraftforge.client.model.obj.WavefrontObject;
import net.minecraftforge.common.util.ForgeDirection;

import org.lwjgl.opengl.GL11;

import zmaster587.advancedRocketry.tile.multiblock.TileGravityController;
import zmaster587.libVulpes.block.RotatableBlock;

public class RenderGravityMachine extends TileEntitySpecialRenderer {
	
	WavefrontObject model;

	ResourceLocation texture =  new ResourceLocation("advancedRocketry:textures/models/gravityMachine.png");
	
	public RenderGravityMachine() {
		try {
			model = new WavefrontObject(new ResourceLocation("advancedrocketry:models/gravityMachine.obj"));
		} catch (ModelFormatException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void renderTileEntityAt(TileEntity tile, double x,
			double y, double z, float f) {
		TileGravityController multiBlockTile = (TileGravityController)tile;

		if(!multiBlockTile.canRender())
			return;

		GL11.glPushMatrix();

		//Initial setup
		int bright = tile.getWorldObj().getLightBrightnessForSkyBlocks(tile.xCoord, tile.yCoord + 1, tile.zCoord,0);
		int brightX = bright % 65536;
		int brightY = bright / 65536;
		OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, brightX, brightY);
		
		GL11.glTranslated(x + 0.5, y - .5f, z + .5);
		//Rotate and move the model into position
		ForgeDirection front = RotatableBlock.getFront(tile.getBlockMetadata());
		GL11.glRotatef((front.offsetX == 1 ? 180 : 0) + front.offsetZ*90f, 0, 1, 0);
		//GL11.glTranslated(2f, 0, 0f);
		bindTexture(texture);
		
		model.renderOnly("Base");
		GL11.glDisable(GL11.GL_LIGHTING);
		int maxSize = 5;
		
		//Render blur
		GL11.glDisable(GL11.GL_TEXTURE_2D);
		GL11.glColor4f(0f, 1f, 1f, Math.max(((float)multiBlockTile.getGravityMultiplier() - 0.1f)*0.2f,0f));
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glDepthMask(false);
		
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		GL11.glAlphaFunc(GL11.GL_GREATER, 0f);
		
		GL11.glPushMatrix();
		GL11.glScaled(1.1, 1, 1.1);
		for(int i = 0; i < 4; i++) {
			GL11.glScaled(.93, 1, .93);
			model.renderOnly("Blur");
		}
		GL11.glPopMatrix();
		
		GL11.glAlphaFunc(GL11.GL_GREATER, 0.1f);
		GL11.glBlendFunc(GL11.GL_ONE, GL11.GL_ONE_MINUS_SRC_ALPHA);
		
		GL11.glDepthMask(true);
		GL11.glDisable(GL11.GL_BLEND);
		GL11.glColor4f(1f, 1f, 1f,1f);
		GL11.glEnable(GL11.GL_TEXTURE_2D);
		// END render blur
		
		
		GL11.glRotated(multiBlockTile.getArmRotation(), 0, 1, 0);
		for(int i = 0; i < maxSize; i++) {
			GL11.glRotated(360/maxSize, 0, 1, 0);
			model.renderOnly("Arm");
		}
		GL11.glEnable(GL11.GL_LIGHTING);
		GL11.glPopMatrix();
	}
}
