package zmaster587.advancedRocketry.client.render.multiblocks;

import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.vector.Quaternion;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;

import zmaster587.advancedRocketry.backwardCompat.ModelFormatException;
import zmaster587.advancedRocketry.backwardCompat.WavefrontObject;
import zmaster587.advancedRocketry.tile.multiblock.TileRailgun;
import zmaster587.libVulpes.block.RotatableBlock;
import zmaster587.libVulpes.render.RenderHelper;

import javax.annotation.ParametersAreNonnullByDefault;

public class RendererRailgun extends TileEntityRenderer<TileRailgun> {
	
	WavefrontObject model;
	ResourceLocation texture =  new ResourceLocation("advancedrocketry","textures/models/railgun.png");
	
	public RendererRailgun(TileEntityRendererDispatcher tile) {
		super(tile);
		try {
			model = new WavefrontObject(new ResourceLocation("advancedrocketry","models/railgun.obj"));
		} catch (ModelFormatException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	@ParametersAreNonnullByDefault
	public void render(TileRailgun tile, float partialTicks, MatrixStack matrix, IRenderTypeBuffer buffer, int combinedLightIn, int combinedOverlayIn) {

		if(!tile.canRender())
			return;
		
		if (tile.getWorld() != null) {
			combinedLightIn = WorldRenderer.getCombinedLight(tile.getWorld(), tile.getPos().add(0, 2, 0));
		} else {
			combinedLightIn = 15728880;
		}

		matrix.push();

		//Initial setup

		matrix.translate( 0.5, 0, 0.5);
		//Rotate and move the model into position
		Direction front = RotatableBlock.getFront(tile.getWorld().getBlockState(tile.getPos()));
		matrix.rotate(new Quaternion(0, (front.getXOffset() == 1 ? 180 : 0) + front.getZOffset()*90f, 0, true));
		matrix.translate(3f, 0, 0f);
		IVertexBuilder entitySolidBuilder = buffer.getBuffer(RenderHelper.getTranslucentEntityModelRenderType(texture));
		
		
		if(tile.getWorld().getGameTime() - tile.recoil - 20 <= 0) {
			model.renderOnly(matrix, combinedLightIn, combinedOverlayIn, entitySolidBuilder, "Base");
			matrix.push();
			matrix.translate(0, (-20+(tile.getWorld().getGameTime() - tile.recoil))/50f, 0);
			model.renderOnly(matrix, combinedLightIn, combinedOverlayIn, entitySolidBuilder, "Barrel");
			matrix.pop();
		}
		else
			model.tessellateAll(matrix, combinedLightIn, combinedOverlayIn, entitySolidBuilder);
		
		matrix.pop();
	}
}
