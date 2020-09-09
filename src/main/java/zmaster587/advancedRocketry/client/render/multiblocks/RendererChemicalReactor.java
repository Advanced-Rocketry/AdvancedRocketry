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
import zmaster587.advancedRocketry.tile.multiblock.machine.TileChemicalReactor;
import zmaster587.libVulpes.block.RotatableBlock;
import zmaster587.libVulpes.render.RenderHelper;

public class RendererChemicalReactor  extends TileEntityRenderer {

	WavefrontObject model;
	ResourceLocation texture;
	
	public RendererChemicalReactor(TileEntityRendererDispatcher tile) {
		super(tile);
		texture = new ResourceLocation("advancedrocketry","textures/models/chemicalreactor.png");
		try {
			model = new WavefrontObject(new ResourceLocation("advancedrocketry","models/chemicalreactor.obj"));
		} catch (ModelFormatException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void render(TileEntity tile, float partialTicks, MatrixStack matrix,
			IRenderTypeBuffer buffer, int combinedLightIn, int combinedOverlayIn) {
		
		TileChemicalReactor multiBlockTile = (TileChemicalReactor)tile;

		if(!multiBlockTile.canRender())
			return;

		matrix.push();

		
		//Rotate and move the model into position
		matrix.push();
		matrix.translate(0.5f, 0, 0.5f);
		Direction front = RotatableBlock.getFront(tile.getWorld().getBlockState(tile.getPos())); //tile.getWorldObj().getBlockMetadata(tile.xCoord, tile.yCoord, tile.zCoord));
		
		matrix.rotate(new Quaternion(0,(front.getZOffset() == 1 ? 180 : 0) - front.getXOffset()*90f,0, true));
		IVertexBuilder entityTransparentBuilder = buffer.getBuffer(RenderHelper.getSolidEntityModelRenderType(texture));
		model.renderOnly(matrix, combinedLightIn, combinedOverlayIn, entityTransparentBuilder, "mesh");
		matrix.pop();
		
		
		
		matrix.translate(.5f, 0, 0.5f);
		
		matrix.rotate(new Quaternion(0,(front.getZOffset() == 1 ? 180 : 0) - front.getXOffset()*90f,0, true));
		
		matrix.translate(0f, -0.5f, 1f );
		if(multiBlockTile.isRunning())
			matrix.rotate(new Quaternion((8*tile.getWorld().getGameTime()) % 360,0,0, true));
		model.renderOnly(matrix, combinedLightIn, combinedOverlayIn, entityTransparentBuilder, "Cylinder");
		
		matrix.pop();
	}
}
