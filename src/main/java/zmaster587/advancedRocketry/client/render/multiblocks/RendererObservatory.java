package zmaster587.advancedRocketry.client.render.multiblocks;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
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
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
		
		GL11.glPushMatrix();

		//Rotate and move the model into position
		EnumFacing front = RotatableBlock.getFront(tile.getWorld().getBlockState(tile.getPos())); //tile.getWorldObj().getBlockMetadata(tile.xCoord, tile.yCoord, tile.zCoord));//tile.getWorldObj().getBlockMetadata(tile.xCoord, tile.yCoord, tile.zCoord));
		GL11.glTranslated(x + .5, y, z + .5);
		GL11.glRotatef((front.getFrontOffsetX() == 1 ? 180 : 0) + front.getFrontOffsetZ()*90f, 0, 1, 0);

		GL11.glTranslated(2, -1, 0);

		bindTexture(texture);

		float offset = multiBlockTile.getOpenProgress();

		if(offset != 0f) {
			model.renderOnly("Base");

			model.renderPart("Scope");
			model.renderPart("Axis");

			GL11.glPushMatrix();
			GL11.glTranslatef(0, 0, -offset);
			model.renderOnly("CasingXMinus");
			GL11.glPopMatrix();

			GL11.glPushMatrix();
			GL11.glTranslatef(0,0,offset);
			model.renderOnly("CasingXPlus");
			GL11.glPopMatrix();

		}
		else {
			model.renderOnly("Base");
			model.renderOnly("CasingXMinus");
			model.renderOnly("CasingXPlus");
		}
		GL11.glPopMatrix();
	}
}
