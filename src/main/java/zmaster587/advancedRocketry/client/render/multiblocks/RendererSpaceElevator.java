package zmaster587.advancedRocketry.client.render.multiblocks;

import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.ModelFormatException;
import net.minecraftforge.client.model.obj.WavefrontObject;
import net.minecraftforge.common.util.ForgeDirection;

import org.lwjgl.opengl.GL11;

import zmaster587.advancedRocketry.client.render.RenderLaser;
import zmaster587.advancedRocketry.tile.multiblock.TileSpaceElevator;
import zmaster587.libVulpes.block.RotatableBlock;

public class RendererSpaceElevator extends TileEntitySpecialRenderer {
	
	WavefrontObject model;

	public ResourceLocation baseTexture =  new ResourceLocation("advancedRocketry:textures/models/spaceElevator.jpg");
	RenderLaser laser;
	
	public RendererSpaceElevator() {
		
		try {
			model = new WavefrontObject(new ResourceLocation("advancedrocketry:models/spaceElevator.obj"));
		} catch (ModelFormatException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void renderTileEntityAt(TileEntity tile, double x,
			double y, double z, float f) {
		TileSpaceElevator multiBlockTile = (TileSpaceElevator)tile;

		if(!multiBlockTile.canRender())
			return;

		GL11.glPushMatrix();

		//Initial setup

		GL11.glTranslated(x + 0.5, y, z + .5);
		//Rotate and move the model into position
		ForgeDirection front = RotatableBlock.getFront(tile.getWorldObj().getBlockMetadata(tile.xCoord, tile.yCoord, tile.zCoord));
		GL11.glRotatef((front.offsetX == 1 ? 180 : 0) + front.offsetZ*90f, 0, 1, 0);
		//GL11.glTranslated(2f, 0, 0f);
		bindTexture(baseTexture);
		model.renderOnly("Base");
		
		GL11.glPopMatrix();
	}
}
