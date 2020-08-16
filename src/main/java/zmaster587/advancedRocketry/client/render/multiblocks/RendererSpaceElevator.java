package zmaster587.advancedRocketry.client.render.multiblocks;

import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.Entity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;
import zmaster587.advancedRocketry.backwardCompat.ModelFormatException;
import zmaster587.advancedRocketry.backwardCompat.WavefrontObject;
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
	public void render(TileEntity tile, double x,
			double y, double z, float f, int damage, float a) {
		TileSpaceElevator multiBlockTile = (TileSpaceElevator)tile;

		if(!multiBlockTile.canRender())
			return;

		double renderX = x + multiBlockTile.getLandingLocationX() - multiBlockTile.getPos().getX();
		double renderZ = z + multiBlockTile.getLandingLocationZ() - multiBlockTile.getPos().getZ();
		
		laser.doRender((Entity)null, renderX - .5, y+2.5, renderZ - .5, 0, f);
		
		matrix.push();

		//Initial setup

		matrix.translate(x + 0.5, y, z + .5);
		//Rotate and move the model into position
		Direction front = RotatableBlock.getFront(tile.getWorld().getBlockState(tile.getPos()));
		GL11.glRotatef((front.getXOffset() == 1 ? 180 : 0) + front.getZOffset()*90f, 0, 1, 0);
		//matrix.translate(2f, 0, 0f);
		bindTexture(baseTexture);
		model.renderOnly("Base");
		matrix.pop();
		
		//Render Beads
		matrix.push();
		matrix.translate(x + multiBlockTile.getLandingLocationX() - multiBlockTile.getPos().getX(), y, z + multiBlockTile.getLandingLocationZ() - multiBlockTile.getPos().getZ());
		BufferBuilder buffer = Tessellator.getInstance().getBuffer();
		GlStateManager.disableLighting();
		GlStateManager.disableFog();
		GlStateManager.enableBlend();
		GlStateManager.depthMask(false);

		GlStateManager.disableTexture();
		GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE);

		buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_NORMAL);
		GlStateManager.color4f(1, 1 , 1 , 0.11f);

		double position = (System.currentTimeMillis() % 16000)/200f;

		for(int i = 0 ; i < 10; i++) {
			for(float radius = 0.25F; radius < 1.25; radius += .25F) {

				RenderHelper.renderCube(buffer, -radius, -radius + position + i*80 + 4, -radius, radius, radius + position + i*80 + 4, radius);

			}
		}
		for(int i = 1 ; i < 11; i++) {
			for(float radius = 0.25F; radius < 1.25; radius += .25F) {

				RenderHelper.renderCube(buffer, -radius, -radius - position + i*80 + 4, -radius, radius, radius - position + i*80 + 4, radius);

			}
		}

		Tessellator.getInstance().draw();

		GlStateManager.disableBlend();
		GlStateManager.enableLighting();
		GlStateManager.enableTexture();
		GlStateManager.enableFog();
		GlStateManager.depthMask(true);
		GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		matrix.pop();
		
		
	}
}
