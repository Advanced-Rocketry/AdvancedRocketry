package zmaster587.advancedRocketry.client.render.multiblocks;

import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;
import zmaster587.advancedRocketry.backwardCompat.ModelFormatException;
import zmaster587.advancedRocketry.backwardCompat.WavefrontObject;
import zmaster587.advancedRocketry.tile.multiblock.TileRailgun;
import zmaster587.libVulpes.block.RotatableBlock;

public class RendererRailgun extends TileEntitySpecialRenderer {
	
	WavefrontObject model;

	ResourceLocation texture =  new ResourceLocation("advancedRocketry:textures/models/railgun.png");
	
	public RendererRailgun() {
		try {
			model = new WavefrontObject(new ResourceLocation("advancedrocketry:models/railgun.obj"));
		} catch (ModelFormatException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void render(TileEntity tile, double x,
			double y, double z, float f, int damage, float a) {
		TileRailgun multiBlockTile = (TileRailgun)tile;

		if(!multiBlockTile.canRender())
			return;

		matrix.push();

		//Initial setup

		matrix.translate(x + 0.5, y, z + .5);
		//Rotate and move the model into position
		Direction front = RotatableBlock.getFront(tile.getWorld().getBlockState(tile.getPos()));
		GL11.glRotatef((front.getXOffset() == 1 ? 180 : 0) + front.getZOffset()*90f, 0, 1, 0);
		matrix.translate(2f, 0, 0f);
		bindTexture(texture);
		
		
		if(tile.getWorld().getGameTime() - multiBlockTile.recoil - 20 <= 0) {
			model.renderOnly("Base");
			matrix.push();
			matrix.translate(0, (-20+(tile.getWorld().getGameTime() - multiBlockTile.recoil))/50f, 0);
			model.renderOnly("Barrel");
			matrix.pop();
		}
		else
			model.renderAll();
		
		matrix.pop();
	}
}
