package zmaster587.advancedRocketry.client.render.multiblocks;

import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.opengl.GL11;

import zmaster587.advancedRocketry.backwardCompat.ModelFormatException;
import zmaster587.advancedRocketry.backwardCompat.WavefrontObject;
import zmaster587.libVulpes.block.RotatableBlock;
import zmaster587.libVulpes.tile.multiblock.TileMultiBlock;

public class RenderLaser extends TileEntitySpecialRenderer {
	
	WavefrontObject model;

	ResourceLocation texture =  new ResourceLocation("advancedRocketry:textures/models/laser.png");
	
	
	public RenderLaser() {
		try {
			model = new WavefrontObject(new ResourceLocation("advancedrocketry:models/laser.obj"));
		} catch (ModelFormatException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void render(TileEntity tile, double x,
			double y, double z, float f, int damage, float a) {
		TileMultiBlock multiBlockTile = (TileMultiBlock)tile;

		if(!multiBlockTile.canRender())
			return;

		GL11.glPushMatrix();

		//Initial setup

		//Rotate and move the model into position
		GL11.glTranslated(x + 0.5, y, z + 0.5);
		EnumFacing front = RotatableBlock.getFront(tile.getWorld().getBlockState(tile.getPos())); //tile.getWorldObj().getBlockMetadata(tile.xCoord, tile.yCoord, tile.zCoord));
		GL11.glRotatef((front.getFrontOffsetX() == 1 ? 180 : 0) + front.getFrontOffsetZ()*90f, 0, 1, 0);
		GL11.glTranslated(2f, 0, 0f);
		bindTexture(texture);
		model.renderAll();
		
		GL11.glPopMatrix();
	}
}
