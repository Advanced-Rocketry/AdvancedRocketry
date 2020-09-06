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
import zmaster587.advancedRocketry.client.render.RenderLaser;
import zmaster587.advancedRocketry.tile.multiblock.TileBeacon;
import zmaster587.libVulpes.block.RotatableBlock;
import zmaster587.libVulpes.render.RenderHelper;

public class RenderBeacon extends TileEntityRenderer<TileBeacon> {

	WavefrontObject model;

	public ResourceLocation baseTexture =  new ResourceLocation("advancedrocketry","textures/models/beacon.jpg");
	RenderLaser laser;

	public RenderBeacon(TileEntityRendererDispatcher disp) {
		super(disp);

		try {
			model = new WavefrontObject(new ResourceLocation("advancedrocketry","models/beacon.obj"));
		} catch (ModelFormatException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void render(TileBeacon multiBlockTile, float partialTicks, MatrixStack matrix,
			IRenderTypeBuffer buffer, int combinedLightIn, int combinedOverlayIn) {

		if(!multiBlockTile.canRender())
			return;

		matrix.push();

		//Initial setup

		matrix.translate(0.5, 0, 0.5);
		//Rotate and move the model into position
		IVertexBuilder entityTransparentBuilder = buffer.getBuffer(RenderHelper.getSolidEntityModelRenderType(baseTexture));
		
		Direction front = RotatableBlock.getFront(multiBlockTile.getWorld().getBlockState(multiBlockTile.getPos()));
		matrix.rotate(new Quaternion( 0, (front.getXOffset() == 1 ? 180 : 0) + front.getZOffset()*90f, 0, true ));
		//matrix.translate(2f, 0, 0f);
		model.renderOnly(entityTransparentBuilder, "Base");

		matrix.translate(1, 0, 0);
		matrix.push();
		if(multiBlockTile.getMachineEnabled())
			matrix.rotate(new Quaternion(0, (System.currentTimeMillis() & 0xFFFF)/20f, 0, true));
		model.renderOnly(entityTransparentBuilder, "OuterSpin");
		matrix.pop();

		matrix.push();
		if(multiBlockTile.getMachineEnabled())
			matrix.rotate(new Quaternion(0, -(System.currentTimeMillis() & 0xFFFF)/6f, 0, true));
		model.renderOnly(entityTransparentBuilder, "InnerSpin");
		matrix.pop();


		matrix.pop();
	}
}
