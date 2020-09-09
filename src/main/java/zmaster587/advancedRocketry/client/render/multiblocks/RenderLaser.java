package zmaster587.advancedRocketry.client.render.multiblocks;

import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.vector.Quaternion;

import org.lwjgl.opengl.GL11;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;

import zmaster587.advancedRocketry.backwardCompat.ModelFormatException;
import zmaster587.advancedRocketry.backwardCompat.WavefrontObject;
import zmaster587.advancedRocketry.tile.multiblock.TileSpaceLaser;
import zmaster587.libVulpes.block.RotatableBlock;
import zmaster587.libVulpes.render.RenderHelper;
import zmaster587.libVulpes.tile.multiblock.TileMultiBlock;

public class RenderLaser extends TileEntityRenderer<TileSpaceLaser> {
	
	WavefrontObject model;

	ResourceLocation texture =  new ResourceLocation("advancedrocketry","textures/models/laser.png");
	
	
	public RenderLaser(TileEntityRendererDispatcher tile) {
		super(tile);
		try {
			model = new WavefrontObject(new ResourceLocation("advancedrocketry","models/laser.obj"));
		} catch (ModelFormatException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void render(TileSpaceLaser tile, float partialTicks, MatrixStack matrix,
			IRenderTypeBuffer buffer, int combinedLightIn, int combinedOverlayIn) {
		TileMultiBlock multiBlockTile = (TileMultiBlock)tile;

		if(!multiBlockTile.canRender())
			return;

		matrix.push();

		//Initial setup

		//Rotate and move the model into position
		matrix.translate(0.5, 0, 0.5);
		Direction front = RotatableBlock.getFront(tile.getWorld().getBlockState(tile.getPos())); //tile.getWorldObj().getBlockMetadata(tile.xCoord, tile.yCoord, tile.zCoord));
		matrix.rotate(new Quaternion(0, (front.getXOffset() == 1 ? 180 : 0) + front.getZOffset()*90f, 0, true));
		matrix.translate(2f, 0, 0f);
		
		IVertexBuilder entitySolidBuilder = buffer.getBuffer(RenderHelper.getSolidEntityModelRenderType(texture));
		model.tessellateAll(matrix, combinedLightIn, combinedOverlayIn, entitySolidBuilder);
		IVertexBuilder lazerType = buffer.getBuffer(RenderHelper.getLaserBeamType());
		
		
		//Laser
		if(((TileSpaceLaser)multiBlockTile).isRunning())
		{
			matrix.translate(-0.5f, 0, -0.5f);
			float x = 0, y = 0, z = 0;
			for(float radius = 0.1F; radius < .5; radius += .1F) {
				for(double i = 0; i < 2*Math.PI; i += Math.PI) {
					
					lazerType.pos(- x , -y - 100,  - z).endVertex();
					lazerType.pos(- x, -y - 100, - z).endVertex();
					lazerType.pos(- (radius* Math.cos(i)) + 0.5F, 0,- (radius* Math.sin(i)) + 0.5F).endVertex();
					lazerType.pos(+ (radius* Math.sin(i)) + 0.5F, 0, (radius* Math.cos(i)) + 0.5F).endVertex();
				}
	
				for(double i = 0; i < 2*Math.PI; i += Math.PI) {
					lazerType.pos(- x, -y - 100,- z).endVertex();
					lazerType.pos(- x, -y - 100, - z).endVertex();
					lazerType.pos(+ (radius* Math.sin(i)) + 0.5F, 0, -(radius* Math.cos(i)) + 0.5F).endVertex();
					lazerType.pos(- (radius* Math.cos(i)) + 0.5F, 0,(radius* Math.sin(i)) + 0.5F).endVertex();
				}
			}
		}
		matrix.pop();
	}
}
