package zmaster587.advancedRocketry.client.render.multiblocks;

import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
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

		GL11.glPushMatrix();

		//Initial setup

		GL11.glTranslated(x + 0.5, y, z + .5);
		//Rotate and move the model into position
		EnumFacing front = RotatableBlock.getFront(tile.getWorld().getBlockState(tile.getPos()));
		GL11.glRotatef((front.getFrontOffsetX() == 1 ? 180 : 0) + front.getFrontOffsetZ()*90f, 0, 1, 0);
		GL11.glTranslated(2f, 0, 0f);
		bindTexture(texture);
		
		
		if(tile.getWorld().getTotalWorldTime() - multiBlockTile.recoil - 20 <= 0) {
			model.renderOnly("Base");
			GL11.glPushMatrix();
			GL11.glTranslated(0, (-20+(tile.getWorld().getTotalWorldTime() - multiBlockTile.recoil))/50f, 0);
			model.renderOnly("Barrel");
			GL11.glPopMatrix();
		}
		else
			model.renderAll();
		
		GL11.glPopMatrix();
	}
}
