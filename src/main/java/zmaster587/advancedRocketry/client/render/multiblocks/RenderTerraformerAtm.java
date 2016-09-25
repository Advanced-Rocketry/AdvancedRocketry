package zmaster587.advancedRocketry.client.render.multiblocks;

import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.opengl.GL11;

import zmaster587.advancedRocketry.backwardCompat.ModelFormatException;
import zmaster587.advancedRocketry.backwardCompat.WavefrontObject;
import zmaster587.advancedRocketry.inventory.TextureResources;
import zmaster587.libVulpes.block.RotatableBlock;
import zmaster587.libVulpes.tile.multiblock.TileMultiBlock;

public class RenderTerraformerAtm extends TileEntitySpecialRenderer {
	
	WavefrontObject model;

	ResourceLocation tubeTexture =  new ResourceLocation("advancedRocketry:textures/models/tubes.png");
	
	
	public RenderTerraformerAtm() {
		try {
			model = new WavefrontObject(new ResourceLocation("advancedrocketry:models/terraformerAtm.obj"));
		} catch (ModelFormatException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void renderTileEntityAt(TileEntity tile, double x,
			double y, double z, float f, int damage) {
		TileMultiBlock multiBlockTile = (TileMultiBlock)tile;

		if(!multiBlockTile.canRender())
			return;

		GL11.glPushMatrix();

		//Initial setup

		//Rotate and move the model into position
		GL11.glTranslated(x + 0.5, y, z + 0.5);
		EnumFacing front = RotatableBlock.getFront(tile.getWorld().getBlockState(tile.getPos())); //tile.getWorldObj().getBlockMetadata(tile.xCoord, tile.yCoord, tile.zCoord));
		GL11.glRotatef((front.getFrontOffsetX() == 1 ? 180 : 0) + front.getFrontOffsetZ()*90f, 0, 1, 0);
		GL11.glTranslated(1f, 0, 0f);
		bindTexture(TextureResources.fan);
		model.renderOnly("Fan");
		
		bindTexture(TextureResources.metalPlate);
		model.renderOnly("Body");
		float col = .4f;
		GL11.glColor3f(col, col, col);
		model.renderOnly("DarkBody");
		col = 1f;
		GL11.glColor3f(col, col, col);

		bindTexture(TextureResources.diamondMetal);
		model.renderOnly("Floor");
		
		
		//Baked a light map, make tubes smooth
		GL11.glDisable(GL11.GL_LIGHTING);
		bindTexture(tubeTexture);
		model.renderOnly("Tubes");
		GL11.glDisable(GL11.GL_TEXTURE_2D);
		
		GL11.glColor3f(0, 0.9f, col);
		OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, 196, 196);
		model.renderOnly("BlueRing");
		GL11.glColor3f(col, col, col);
		GL11.glEnable(GL11.GL_LIGHTING);
		GL11.glEnable(GL11.GL_TEXTURE_2D);
		
		GL11.glPopMatrix();
	}
}
