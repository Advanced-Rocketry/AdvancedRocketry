package zmaster587.advancedRocketry.client.render.multiblocks;

import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.vector.Quaternion;

import org.lwjgl.opengl.GL11;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;

import zmaster587.advancedRocketry.backwardCompat.ModelFormatException;
import zmaster587.advancedRocketry.backwardCompat.WavefrontObject;
import zmaster587.advancedRocketry.tile.multiblock.energy.TileSolarArray;
import zmaster587.libVulpes.block.RotatableBlock;
import zmaster587.libVulpes.render.RenderHelper;

public class RendererSolarArray extends TileEntityRenderer<TileSolarArray> {

	WavefrontObject model;

	ResourceLocation texture = new ResourceLocation("advancedrocketry:textures/models/solararray.png");

	public RendererSolarArray(TileEntityRendererDispatcher tile){
		super(tile);
		try {
			model = new WavefrontObject(new ResourceLocation("advancedrocketry:models/solar_array.obj"));
		} catch (ModelFormatException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void render(TileSolarArray tile, float partialTicks, MatrixStack matrix,
			IRenderTypeBuffer buffer, int combinedLight, int combinedOverlay) {

		if(!tile.canRender())
			return;

		matrix.push();

		//Rotate and move the model into position
		Direction front = RotatableBlock.getFront(tile.getWorld().getBlockState(tile.getPos()));
		matrix.translate(0.5, 0, 0.5);
		matrix.rotate(new Quaternion(0,(front.getXOffset() == 1 ? 0 : 180) + front.getZOffset()*90f,0, true));
		
		matrix.translate(-0.5f, 0f, 0.5f);
		
		IVertexBuilder builder = buffer.getBuffer(RenderHelper.getSolidEntityModelRenderType(texture));
		
		model.tessellateAll(matrix, combinedLight, combinedOverlay, builder);
		
		matrix.pop();
	}
}
