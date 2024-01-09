package zmaster587.advancedRocketry.client.render.multiblocks;

import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;
import zmaster587.advancedRocketry.backwardCompat.ModelFormatException;
import zmaster587.advancedRocketry.backwardCompat.WavefrontObject;
import zmaster587.advancedRocketry.tile.multiblock.energy.TileSolarArray;
import zmaster587.libVulpes.block.RotatableBlock;

public class RendererSolarArray extends TileEntitySpecialRenderer {

	WavefrontObject model;

	ResourceLocation texture = new ResourceLocation("advancedrocketry:textures/models/solararray.png");

	public RendererSolarArray(){
		try {
			model = new WavefrontObject(new ResourceLocation("advancedrocketry:models/solar_array.obj"));
		} catch (ModelFormatException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void render(TileEntity tile, double x,
			double y, double z, float f, int distance, float a) {
		TileSolarArray multiBlockTile = (TileSolarArray)tile;

		if(!multiBlockTile.canRender())
			return;

		GL11.glPushMatrix();

		//Rotate and move the model into position
		EnumFacing front = RotatableBlock.getFront(tile.getWorld().getBlockState(tile.getPos()));
		GL11.glTranslated(x + .5, y, z + .5);
		GL11.glRotatef((front.getXOffset() == 1 ? 0 : 180) + front.getZOffset()*90f, 0, 1, 0);
		
		GL11.glTranslated(-0.5f, 0f, 0.5f);

		bindTexture(texture);
		
		model.renderAll();
		
		GL11.glPopMatrix();
	}
}
