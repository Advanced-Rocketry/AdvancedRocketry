package zmaster587.advancedRocketry.client.render.multiblocks;

import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;
import zmaster587.advancedRocketry.backwardCompat.ModelFormatException;
import zmaster587.advancedRocketry.backwardCompat.WavefrontObject;
import zmaster587.advancedRocketry.client.render.RenderLaser;
import zmaster587.libVulpes.block.RotatableBlock;
import zmaster587.libVulpes.tile.multiblock.TileMultiPowerConsumer;

public class RenderBeacon extends TileEntitySpecialRenderer {

	WavefrontObject model;

	public ResourceLocation baseTexture =  new ResourceLocation("advancedRocketry:textures/models/beacon.jpg");
	RenderLaser laser;

	public RenderBeacon() {

		try {
			model = new WavefrontObject(new ResourceLocation("advancedrocketry:models/beacon.obj"));
		} catch (ModelFormatException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void render(TileEntity tile, double x,
			double y, double z, float f, int damage, float a) {
		TileMultiPowerConsumer multiBlockTile = (TileMultiPowerConsumer)tile;

		if(!multiBlockTile.canRender())
			return;

		matrix.push();

		//Initial setup

		matrix.translate(x + 0.5, y, z + .5);
		//Rotate and move the model into position
		Direction front = RotatableBlock.getFront(tile.getWorld().getBlockState(tile.getPos()));
		GL11.glRotatef((front.getXOffset() == 1 ? 180 : 0) + front.getZOffset()*90f, 0, 1, 0);
		//matrix.translate(2f, 0, 0f);
		bindTexture(baseTexture);
		model.renderOnly("Base");

		GL11.glTranslatef(1, 0, 0);
		matrix.push();
		if(multiBlockTile.getMachineEnabled())
			GL11.glRotated((System.currentTimeMillis() & 0xFFFF)/20d, 0, 1, 0);
		model.renderOnly("OuterSpin");
		matrix.pop();

		matrix.push();
		if(multiBlockTile.getMachineEnabled())
			GL11.glRotated(-(System.currentTimeMillis() & 0xFFFF)/6d, 0, 1, 0);
		model.renderOnly("InnerSpin");
		matrix.pop();


		matrix.pop();
	}
}
