package zmaster587.advancedRocketry.client.render.multiblocks;

import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;
import zmaster587.advancedRocketry.backwardCompat.ModelFormatException;
import zmaster587.advancedRocketry.backwardCompat.WavefrontObject;
import zmaster587.advancedRocketry.tile.multiblock.drill.TileOrbitalLaserDrill;
import zmaster587.libVulpes.block.RotatableBlock;
import zmaster587.libVulpes.tile.multiblock.TileMultiBlock;

public class RenderOrbitalLaserDrill extends TileEntitySpecialRenderer {
	
	WavefrontObject model;

	ResourceLocation texture =  new ResourceLocation("advancedRocketry:textures/models/orbitallaserdrill.png");
	
	
	public RenderOrbitalLaserDrill() {
		try {
			model = new WavefrontObject(new ResourceLocation("advancedrocketry:models/orbitallaserdrill.obj"));
		} catch (ModelFormatException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void render(TileEntity tile, double x,
			double y, double z, float f, int damage, float a) {
		TileMultiBlock multiBlockTile = (TileMultiBlock)tile;

		if(!multiBlockTile.canRender())
			return;

		GL11.glPushMatrix();

		//Initial setup

		//Rotate and move the model into position
		GL11.glTranslated(x + 0.5, y, z + 0.5);
		EnumFacing front = RotatableBlock.getFront(tile.getWorld().getBlockState(tile.getPos())); //tile.getWorldObj().getBlockMetadata(tile.xCoord, tile.yCoord, tile.zCoord));
		GL11.glRotatef((front.getFrontOffsetX() == 1 ? 180 : 0) + front.getFrontOffsetZ()*90f, 0, 1, 0);
		GL11.glTranslated(2.5f, 0, 4.5f);
		bindTexture(texture);
		model.renderAll();
		
		
		
		//Laser
		if(((TileOrbitalLaserDrill)multiBlockTile).isRunning())
		{
			GL11.glTranslated(-1.0f, 0, -5f);
			BufferBuilder buffer = Tessellator.getInstance().getBuffer();
			GlStateManager.disableLighting();
			GlStateManager.disableFog();
			GlStateManager.enableBlend();
			GlStateManager.depthMask(false);
			GlStateManager.disableTexture2D();
			GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE);
			GlStateManager.color(0.9F, 0.2F, 0.3F, 1F);
			//GL11.glB
			//GL11.gl
			buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION);
	
			for(float radius = 0.1F; radius < .5; radius += .1F) {
				for(double i = 0; i < 2*Math.PI; i += Math.PI) {
					
					buffer.pos(- x , -y - 100,  - z).endVertex();
					buffer.pos(- x, -y - 100, - z).endVertex();
					buffer.pos(- (radius* Math.cos(i)) + 0.5F, 0,- (radius* Math.sin(i)) + 0.5F).endVertex();
					buffer.pos(+ (radius* Math.sin(i)) + 0.5F, 0, (radius* Math.cos(i)) + 0.5F).endVertex();
				}
	
				for(double i = 0; i < 2*Math.PI; i += Math.PI) {
					buffer.pos(- x, -y - 100,- z).endVertex();
					buffer.pos(- x, -y - 100, - z).endVertex();
					buffer.pos(+ (radius* Math.sin(i)) + 0.5F, 0, -(radius* Math.cos(i)) + 0.5F).endVertex();
					buffer.pos(- (radius* Math.cos(i)) + 0.5F, 0,(radius* Math.sin(i)) + 0.5F).endVertex();
				}
			}
	
			Tessellator.getInstance().draw();
			
			GlStateManager.color(1f,1f,1f, 1f);
			GlStateManager.disableBlend();
			GlStateManager.enableLighting();
			GlStateManager.enableTexture2D();
			GlStateManager.enableFog();
			GlStateManager.depthMask(true);
		}
		GL11.glPopMatrix();
	}
}
