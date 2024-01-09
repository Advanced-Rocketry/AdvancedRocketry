package zmaster587.advancedRocketry.client.render.multiblocks;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;
import zmaster587.advancedRocketry.backwardCompat.ModelFormatException;
import zmaster587.advancedRocketry.backwardCompat.WavefrontObject;
import zmaster587.advancedRocketry.tile.multiblock.TileAreaGravityController;
import zmaster587.libVulpes.block.RotatableBlock;

public class RenderAreaGravityController extends TileEntitySpecialRenderer {
	
	WavefrontObject model;

	ResourceLocation texture =  new ResourceLocation("advancedRocketry:textures/models/areagravitycontroller.png");
	
	public RenderAreaGravityController() {
		try {
			model = new WavefrontObject(new ResourceLocation("advancedrocketry:models/areagravitycontroller.obj"));
		} catch (ModelFormatException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void render(TileEntity tile, double x,
			double y, double z, float f, int damage, float a) {
		TileAreaGravityController multiBlockTile = (TileAreaGravityController)tile;

		if(!multiBlockTile.canRender())
			return;

		GL11.glPushMatrix();

		//Initial setup

		GL11.glTranslated(x + 0.5, y - .5f, z + .5);
		//Rotate and move the model into position
		EnumFacing front = RotatableBlock.getFront(tile.getWorld().getBlockState(tile.getPos()));
		GL11.glRotatef((front.getXOffset() == 1 ? 180 : 0) + front.getZOffset()*90f, 0, 1, 0);
		//GL11.glTranslated(2f, 0, 0f);
		bindTexture(texture);
		
		model.renderOnly("Hull");
		GL11.glDisable(GL11.GL_LIGHTING);
		int maxSize = 5;
		
		//Render blur
		GL11.glDisable(GL11.GL_TEXTURE_2D);
		GlStateManager.color(0f, 1f, 1f, Math.max(((float)multiBlockTile.getGravityMultiplier() - 0.1f)*0.2f,0f));
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
		GlStateManager.color(1f, 1f, 1f,1f);
		GL11.glEnable(GL11.GL_TEXTURE_2D);
		// END render blur
		
		
		GL11.glRotated(multiBlockTile.getArmRotation(), 0, 1, 0);
		for(int i = 0; i < maxSize; i++) {
			GL11.glRotated(360d / maxSize, 0, 1, 0);
			model.renderOnly("Arm");
		}
		GL11.glEnable(GL11.GL_LIGHTING);
		GL11.glPopMatrix();
	}
}
