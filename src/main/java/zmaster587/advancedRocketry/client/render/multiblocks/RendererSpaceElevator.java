package zmaster587.advancedRocketry.client.render.multiblocks;

import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.entity.Entity;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.vector.Quaternion;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;

import zmaster587.advancedRocketry.backwardCompat.ModelFormatException;
import zmaster587.advancedRocketry.backwardCompat.WavefrontObject;
import zmaster587.advancedRocketry.client.render.RenderLaser;
import zmaster587.advancedRocketry.tile.multiblock.TileSpaceElevator;
import zmaster587.libVulpes.block.RotatableBlock;
import zmaster587.libVulpes.render.RenderHelper;

public class RendererSpaceElevator extends TileEntityRenderer<TileSpaceElevator> {
	
	WavefrontObject model;

	public ResourceLocation baseTexture =  new ResourceLocation("advancedrocketry","textures/models/spaceelevator.jpg");
	RenderLaser laser;
	
	public RendererSpaceElevator(TileEntityRendererDispatcher tile) {
		super(tile);
		laser = new RenderLaser(1, new float[] { 0,0 , 0, 0}, new float[] { 1, 1 , 0, 0.11f} );
		try {
			model = new WavefrontObject(new ResourceLocation("advancedrocketry","models/spaceelevator.obj"));
		} catch (ModelFormatException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void render(TileSpaceElevator tile, float partialTicks, MatrixStack matrix,
			IRenderTypeBuffer buffer, int combinedLightIn, int combinedOverlayIn) {

		if(!tile.canRender())
			return;
		
		if (tile.getWorld() != null) {
			combinedLightIn = WorldRenderer.getCombinedLight(tile.getWorld(), tile.getPos().add(0, 1, 0));
		} else {
			combinedLightIn = 15728880;
		}

		double renderX = tile.getLandingLocationX() - tile.getPos().getX();
		double renderZ = tile.getLandingLocationZ() - tile.getPos().getZ();
		
		matrix.push();
		matrix.translate(renderX - .5, 2.5, renderZ - .5);
		laser.doRender(buffer, matrix);
		matrix.pop();
		
		matrix.push();

		//Initial setup

		matrix.translate( 0.5, 0, 0.5);
		//Rotate and move the model into position
		Direction front = RotatableBlock.getFront(tile.getWorld().getBlockState(tile.getPos()));
		matrix.rotate(new Quaternion(0, (front.getXOffset() == 1 ? 180 : 0) + front.getZOffset()*90f, 0, true));
		
		IVertexBuilder entitySolidBuilder = buffer.getBuffer(RenderHelper.getSolidEntityModelRenderType(baseTexture));
		model.renderOnly(matrix, combinedLightIn, combinedOverlayIn, entitySolidBuilder, "Base");
		matrix.pop();
		
		//Render Beads
		
		IVertexBuilder translucentBuilder = buffer.getBuffer(RenderHelper.getTranslucentManualRenderType());
		
		matrix.push();
		matrix.translate(tile.getLandingLocationX() - tile.getPos().getX(), 0, tile.getLandingLocationZ() - tile.getPos().getZ());

		double position = (System.currentTimeMillis() % 16000)/200f;

		for(int i = 0 ; i < 10; i++) {
			for(float radius = 0.25F; radius < 1.25; radius += .25F) {

				RenderHelper.renderCube(matrix, translucentBuilder, -radius, -radius + position + i*80 + 4, -radius, radius, radius + position + i*80 + 4, radius, 1, 1 , 1 , 0.11f);

			}
		}
		for(int i = 1 ; i < 11; i++) {
			for(float radius = 0.25F; radius < 1.25; radius += .25F) {

				RenderHelper.renderCube(matrix, translucentBuilder, -radius, -radius - position + i*80 + 4, -radius, radius, radius - position + i*80 + 4, radius, 1, 1 , 1 , 0.11f);

			}
		}
		
		matrix.pop();
	}
}
