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
import zmaster587.advancedRocketry.tile.multiblock.machine.TilePrecisionLaserEtcher;
import zmaster587.libVulpes.block.RotatableBlock;
import zmaster587.libVulpes.render.RenderHelper;

import javax.annotation.ParametersAreNonnullByDefault;

public class RendererPrecisionLaserEtcher extends TileEntityRenderer<TilePrecisionLaserEtcher> {

	WavefrontObject model;
	ResourceLocation texture = new ResourceLocation("advancedrocketry:textures/models/precisionlaseretcher.png");

	public RendererPrecisionLaserEtcher(TileEntityRendererDispatcher tile) {
		super(tile);
		try {
			model = new WavefrontObject(new ResourceLocation("advancedrocketry:models/precisionlaseretcher.obj"));
		} catch (ModelFormatException e) {
			e.printStackTrace();
		}

	}

	@Override
	@ParametersAreNonnullByDefault
	public void render(TilePrecisionLaserEtcher tile, float f, MatrixStack matrix, IRenderTypeBuffer buffer, int combinedLightIn, int combinedOverlayIn) {

		if(!tile.canRender())
			return;

		matrix.push();

		//Rotate and move the model into position
		matrix.translate(0.5, 0, 0.5);
		Direction front = RotatableBlock.getFront(tile.getWorld().getBlockState(tile.getPos()));
		matrix.rotate(new Quaternion(0,(front.getXOffset() == 1 ? 0 : 180) + front.getZOffset()*90f,0, true));
		matrix.translate(0.5f, 0f, 1.5f);
		
		IVertexBuilder entitySolidBuilder = buffer.getBuffer(RenderHelper.getSolidEntityModelRenderType(texture));

		if(tile.isRunning()) {

			float progress = tile.getProgress(0)/(float)tile.getTotalProgress(0) + 90f/(float)tile.getTotalProgress(0);

			model.renderOnly(matrix, combinedLightIn, combinedOverlayIn, entitySolidBuilder, "Hull"); 

            //Full assembly translation and render
			matrix.push();
			float progress2 = ((16 * progress) - (int)(16 * progress));

			if (progress < 0.875){
				if (progress2 > 0.875) {
					matrix.translate(0f, 0f, (progress2 - 0.875f)/2f);
				}
				matrix.translate(0f, 0f, (progress - (progress2/16f)));
			} else
				matrix.translate(0f, 0f, ((1 - progress) / .15f));

			model.renderOnly(matrix, combinedLightIn, combinedOverlayIn, entitySolidBuilder, "Mount");

			//Render laser and laser translation
			matrix.push();
			if (progress < 0.875) {
				if (progress2 < 0.875f)
					matrix.translate(-progress2, 0f, 0f);
				else
					matrix.translate(-((1 - progress2) / .15f), 0f, 0f);
			}
			model.renderOnly(matrix, combinedLightIn, combinedOverlayIn, entitySolidBuilder, "Laser");
			matrix.pop();
			matrix.pop();


		}
		else {
			model.tessellateAll(matrix, combinedLightIn, combinedOverlayIn, entitySolidBuilder);
		}
		matrix.pop();
	}
}
