package zmaster587.advancedRocketry.client.render.multiblocks;

import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.vector.Quaternion;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;

import zmaster587.advancedRocketry.backwardCompat.ModelFormatException;
import zmaster587.advancedRocketry.backwardCompat.WavefrontObject;
import zmaster587.advancedRocketry.inventory.TextureResources;
import zmaster587.advancedRocketry.tile.multiblock.TileAtmosphereTerraformer;
import zmaster587.libVulpes.block.RotatableBlock;
import zmaster587.libVulpes.render.RenderHelper;

import javax.annotation.ParametersAreNonnullByDefault;

public class RenderTerraformer extends TileEntityRenderer<TileAtmosphereTerraformer> {
	
	WavefrontObject model;
	ResourceLocation tubeTexture =  new ResourceLocation("advancedrocketry","textures/models/tubes.png");
	
	
	public RenderTerraformer(TileEntityRendererDispatcher tile) {
		super(tile);
		try {
			model = new WavefrontObject(new ResourceLocation("advancedrocketry","models/terraformeratm.obj"));
		} catch (ModelFormatException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	@ParametersAreNonnullByDefault
	public void render(TileAtmosphereTerraformer tile, float partialTicks, MatrixStack matrix, IRenderTypeBuffer buffer, int combinedLightIn, int combinedOverlayIn) {

		if(!tile.canRender())
			return;

		matrix.push();

		//Initial setup

		//Rotate and move the model into position
		matrix.translate( 0.5, 0,  0.5);
		Direction front = RotatableBlock.getFront(tile.getWorld().getBlockState(tile.getPos())); //tile.getWorldObj().getBlockMetadata(tile.xCoord, tile.yCoord, tile.zCoord));
		
		matrix.rotate(new Quaternion(0, (front.getXOffset() == 1 ? 180 : 0) + front.getZOffset()*90f, 0, true));
		matrix.translate(1f, 0, 0f);
		
		IVertexBuilder fanBuilder = buffer.getBuffer(RenderHelper.getSolidEntityModelRenderType(TextureResources.fan));
		
		model.renderOnly(matrix, combinedLightIn, combinedOverlayIn, fanBuilder, "Fan");
		
		IVertexBuilder plate = buffer.getBuffer(RenderHelper.getSolidEntityModelRenderType(TextureResources.metalPlate));
		
		model.renderOnly(matrix, combinedLightIn, combinedOverlayIn, plate, "Body");
		
		
		float col = .4f;
		//GL11.glColor3f(col, col, col);
		model.renderOnly(matrix, combinedLightIn, combinedOverlayIn, plate, "DarkBody");
		col = 1f;
		//GL11.glColor3f(col, col, col);
		
		IVertexBuilder diamondPlate = buffer.getBuffer(RenderHelper.getSolidEntityModelRenderType(TextureResources.diamondMetal));

		model.renderOnly(matrix, combinedLightIn, combinedOverlayIn, diamondPlate, "Floor");
		
		
		//Baked a light map, make tubes smooth
		IVertexBuilder tubes = buffer.getBuffer(RenderHelper.getSolidEntityModelRenderType(tubeTexture));
		model.renderOnly(matrix, combinedLightIn, combinedOverlayIn, tubes, "Tubes");
		
		/*GlStateManager.color4f(0, 0.9f, col);
		OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, 196, 196);
		model.renderOnly(matrix, combinedLightIn, combinedOverlayIn, "BlueRing");
		GlStateManager.color4f(col, col, col);
		GlStateManager.enableLighting();
		GlStateManager.enableTexture();*/
		
		matrix.pop();
	}
}
