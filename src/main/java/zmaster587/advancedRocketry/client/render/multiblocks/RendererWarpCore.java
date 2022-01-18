package zmaster587.advancedRocketry.client.render.multiblocks;

import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.vector.Quaternion;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;

import zmaster587.advancedRocketry.api.ARConfiguration;
import zmaster587.advancedRocketry.api.stations.ISpaceObject;
import zmaster587.advancedRocketry.backwardCompat.ModelFormatException;
import zmaster587.advancedRocketry.backwardCompat.WavefrontObject;
import zmaster587.advancedRocketry.dimension.DimensionManager;
import zmaster587.advancedRocketry.stations.SpaceStationObject;
import zmaster587.advancedRocketry.tile.multiblock.TileWarpCore;
import zmaster587.advancedRocketry.stations.SpaceObjectManager;
import zmaster587.libVulpes.block.RotatableBlock;
import zmaster587.libVulpes.render.RenderHelper;
import zmaster587.libVulpes.util.ZUtils;

import javax.annotation.ParametersAreNonnullByDefault;

public class RendererWarpCore extends TileEntityRenderer<TileWarpCore> {

	public static WavefrontObject model;
	ResourceLocation texture = new ResourceLocation("advancedrocketry","textures/models/warpcore.png");

	//private final RenderItem dummyItem = Minecraft.getInstance().getRenderItem();

	public RendererWarpCore(TileEntityRendererDispatcher tile) {
		super(tile);
		try {
			model = new WavefrontObject(new ResourceLocation("advancedrocketry","models/warpcore.obj"));
		} catch (ModelFormatException e) {
			e.printStackTrace();
		}
	}

	@Override
	@ParametersAreNonnullByDefault
	public void render(TileWarpCore tile, float partialTicks, MatrixStack matrix, IRenderTypeBuffer buffer, int combinedLightIn, int combinedOverlayIn)  {

		if(!tile.canRender())
			return;

		matrix.push();

		//Rotate and move the model into position
		matrix.translate( 0.5, 0, 0.5);
		Direction front = RotatableBlock.getFront(tile.getWorld().getBlockState(tile.getPos())); //tile.getWorldObj().getBlockMetadata(tile.xCoord, tile.yCoord, tile.zCoord));
		matrix.rotate(new Quaternion(0, (front.getXOffset() == 1 ? 180 : 0) + front.getZOffset()*90f, 0, true));
		matrix.translate(1f, 0, 0f);

		IVertexBuilder entitySolidBuilder = buffer.getBuffer(RenderHelper.getSolidEntityModelRenderType(texture));
		
		
		
		model.renderOnly(matrix, combinedLightIn, combinedOverlayIn, entitySolidBuilder, "Base");
		matrix.push();
		
		IVertexBuilder entitySolidBuilderManual = buffer.getBuffer(RenderHelper.getSolidManualRenderType());
		RenderHelper.renderCube(matrix, entitySolidBuilderManual, -0.1f, 1, -0.1f, 0.1f, 2, 0f, 0.8f, 0.4f, 0.4f, 0.8f);
		matrix.pop();
		
		
		if(DimensionManager.spaceId.equals(ZUtils.getDimensionIdentifier(tile.getWorld()))) {

			ISpaceObject obj = SpaceObjectManager.getSpaceManager().getSpaceStationFromBlockCoords(tile.getPos());
			if(obj instanceof SpaceStationObject && ((SpaceStationObject)obj).getFuelAmount() > 50) {
				IVertexBuilder entityTranslucentBuilder = buffer.getBuffer(RenderHelper.getTranslucentEntityModelRenderType(texture));
				double speedMult = 1.5;//((DimensionProperties)obj.getProperties()).getParentPlanet() == SpaceObjectManager.WARPDIMID ? 1.5d : 0.1d;
				
				double speedRotate = speedMult*0.25d;
				
				matrix.push();
				matrix.rotate(new Quaternion(0, (float) (speedRotate*System.currentTimeMillis() % 360), 0, true));
				model.renderOnly(matrix, combinedLightIn, combinedOverlayIn, entityTranslucentBuilder, "Rotate1");
				matrix.pop();

				matrix.push();
				matrix.rotate(new Quaternion(0, (float) (180 + speedRotate*System.currentTimeMillis() % 360), 0, true));
				model.renderOnly(matrix, combinedLightIn, combinedOverlayIn, entityTranslucentBuilder, "Rotate1");
				matrix.pop();

				matrix.push();
				matrix.rotate(new Quaternion(0, (float) -(speedRotate*System.currentTimeMillis() % 360), 0, true));
				model.renderOnly(matrix, combinedLightIn, combinedOverlayIn, entityTranslucentBuilder,"Rotate2");
				matrix.pop();

				matrix.push();
				matrix.rotate(new Quaternion(0, (float) (180-speedRotate*System.currentTimeMillis() % 360), 0, true));
				model.renderOnly(matrix, combinedLightIn, combinedOverlayIn, entityTranslucentBuilder, "Rotate2");
				matrix.pop();

				speedRotate = 0.03d*speedMult;
				int amt = 3;
				float offset = 360/(float)amt;
				for(int j = 0; j < 5; j++) {
					for(int i = 0; i < amt; i++) {
						matrix.push();
						matrix.rotate(new Quaternion(0, (float) (((j+1)*speedRotate*System.currentTimeMillis() % 360) + (i + j/5f)*offset), 0, true));
						matrix.translate(0, 0.1f*j-.2f + (5-j)*0.02f*(float)Math.sin(0.001d*System.currentTimeMillis()), 0.2f);
						//matrix.translate(0f, 0.1f*(0.5f - MathHelper.sin((float)(0.001*System.currentTimeMillis() % 100))), 0f);
						model.renderOnly(matrix, combinedLightIn, combinedOverlayIn, entityTranslucentBuilder, "Ball");
						matrix.pop();
					}
				}
			}
		}
		
		matrix.pop();
	}
}
