package zmaster587.advancedRocketry.client.render.multiblocks;

import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.opengl.GL11;
import zmaster587.advancedRocketry.backwardCompat.ModelFormatException;
import zmaster587.advancedRocketry.backwardCompat.WavefrontObject;
import zmaster587.advancedRocketry.tile.multiblock.machine.TileCentrifuge;
import zmaster587.libVulpes.block.RotatableBlock;

public class RenderCentrifuge extends TileEntitySpecialRenderer {

	WavefrontObject model;

	ResourceLocation texture = new ResourceLocation("advancedrocketry:textures/models/centrifuge.png");

	public RenderCentrifuge(){
		try {
			model = new WavefrontObject(new ResourceLocation("advancedrocketry:models/centrifuge.obj"));
		} catch (ModelFormatException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void render(TileEntity tile, double x, double y, double z,
			float partialTicks, int destroyStage, float a) {
		TileCentrifuge multiBlockTile = (TileCentrifuge)tile;

		if(!multiBlockTile.canRender())
			return;

		GL11.glPushMatrix();

		//Initial setup

		//Rotate and move the model into position
		EnumFacing front = RotatableBlock.getFront(tile.getWorld().getBlockState(tile.getPos()));
		GL11.glTranslated(x + 0.5, y, z + 0.5);
		GL11.glRotatef((front.getZOffset() == 1 ? 180 : 0) - front.getXOffset()*90f, 0, 1, 0);
		GL11.glTranslated(-0.5f, -1f, 1.5f);

		bindTexture(texture);

		model.renderOnly("Hull");



		if(multiBlockTile.isRunning())
		{
			GL11.glPushMatrix();
			GL11.glRotated(multiBlockTile.getWorld().getTotalWorldTime() * -100f, 0, 1, 0);
			model.renderOnly("Cylinder");
			GL11.glPopMatrix();

		} else {
			GL11.glPushMatrix();
			model.renderOnly("Cylinder");
			GL11.glPopMatrix();
		}
		GL11.glPopMatrix();


	}
}
