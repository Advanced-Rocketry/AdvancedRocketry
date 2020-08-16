package zmaster587.advancedRocketry.client.render.multiblocks;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;
import zmaster587.advancedRocketry.backwardCompat.ModelFormatException;
import zmaster587.advancedRocketry.backwardCompat.WavefrontObject;
import zmaster587.advancedRocketry.tile.multiblock.TileObservatory;
import zmaster587.libVulpes.block.RotatableBlock;

public class RendererObservatory  extends TileEntitySpecialRenderer {

	WavefrontObject model;

	ResourceLocation texture = new ResourceLocation("advancedrocketry:textures/models/T1Observatory.png");

	public RendererObservatory() {
		try {
			model = new WavefrontObject(new ResourceLocation("advancedrocketry:models/observatory.obj"));
		} catch (ModelFormatException e) {
			e.printStackTrace();
		}
	}


	@Override
	public void render(TileEntity tile, double x,
			double y, double z, float f, int damage, float a) {
		TileObservatory multiBlockTile = (TileObservatory)tile;

		if(!multiBlockTile.canRender())
			return;

		//Initial setup
        int i = this.getWorld().getCombinedLight(tile.getPos().add(0, 1, 0), 0);
        int j = i % 65536;
        int k = i / 65536;
        OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, (float)j, (float)k);
        GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
		
		matrix.push();

		//Rotate and move the model into position
		Direction front = RotatableBlock.getFront(tile.getWorld().getBlockState(tile.getPos())); //tile.getWorldObj().getBlockMetadata(tile.xCoord, tile.yCoord, tile.zCoord));//tile.getWorldObj().getBlockMetadata(tile.xCoord, tile.yCoord, tile.zCoord));
		matrix.translate(x + .5, y, z + .5);
		GL11.glRotatef((front.getXOffset() == 1 ? 180 : 0) + front.getZOffset()*90f, 0, 1, 0);

		matrix.translate(2, -1, 0);

		bindTexture(texture);

		float offset = multiBlockTile.getOpenProgress();

		if(offset != 0f) {
			model.renderOnly("Base");

			model.renderPart("Scope");
			model.renderPart("Axis");

			matrix.push();
			GL11.glTranslatef(0, 0, -offset);
			model.renderOnly("CasingXMinus");
			matrix.pop();

			matrix.push();
			GL11.glTranslatef(0,0,offset);
			model.renderOnly("CasingXPlus");
			matrix.pop();

		}
		else {
			model.renderOnly("Base");
			model.renderOnly("CasingXMinus");
			model.renderOnly("CasingXPlus");
		}
		matrix.pop();
	}
}
