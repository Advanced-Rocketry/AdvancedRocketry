package zmaster587.advancedRocketry.client.render.multiblocks;

<<<<<<< HEAD
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.fluid.Fluid;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.vector.Quaternion;
import net.minecraftforge.fluids.FluidStack;
=======
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
>>>>>>> origin/feature/nuclearthermalrockets

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.IVertexBuilder;

import zmaster587.advancedRocketry.api.AdvancedRocketryFluids;
import zmaster587.advancedRocketry.backwardCompat.ModelFormatException;
import zmaster587.advancedRocketry.backwardCompat.WavefrontObject;
import zmaster587.advancedRocketry.tile.multiblock.machine.TileCentrifuge;
import zmaster587.libVulpes.block.RotatableBlock;
<<<<<<< HEAD
import zmaster587.libVulpes.render.RenderHelper;
=======
>>>>>>> origin/feature/nuclearthermalrockets

public class RenderCentrifuge extends TileEntityRenderer<TileCentrifuge> {

	WavefrontObject model;

	ResourceLocation texture = new ResourceLocation("advancedrocketry","textures/models/centrifuge.png");

	public RenderCentrifuge(TileEntityRendererDispatcher tile){
		super(tile);
		try {
			model = new WavefrontObject(new ResourceLocation("advancedrocketry","models/centrifuge.obj"));
		} catch (ModelFormatException e) {
			e.printStackTrace();
		}
	}

	
	@Override
	public void render(TileCentrifuge tile, float partialTicks, MatrixStack matrix,
			IRenderTypeBuffer buffer, int combinedLightIn, int combinedOverlayIn) {

		if(!tile.canRender())
			return;

		matrix.push();

		//Initial setup

		//Rotate and move the model into position
		Direction front = RotatableBlock.getFront(tile.getWorld().getBlockState(tile.getPos()));
		matrix.translate( 0.5, 0, 0.5);
		matrix.rotate(new Quaternion(0, (front.getZOffset() == 1 ? 180 : 0) - front.getXOffset()*90f, 0 ,true));
		matrix.translate(-0.5f, -1f, 1.5f);
		
		IVertexBuilder builder = buffer.getBuffer(RenderHelper.getTranslucentEntityModelRenderType(texture)); 

		model.renderOnly(matrix, combinedLightIn, combinedOverlayIn, builder, "Hull");


		if(tile.isRunning())
		{
			matrix.push();
			matrix.rotate(new Quaternion(0, System.currentTimeMillis() * -100f, 0, true));
			model.renderOnly(matrix, combinedLightIn, combinedOverlayIn, builder, "Cylinder");
			matrix.pop();

		} else {
			model.renderOnly(matrix, combinedLightIn, combinedOverlayIn, builder, "Cylinder");
		}
		matrix.pop();
	}
}
