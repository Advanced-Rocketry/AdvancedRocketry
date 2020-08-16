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
	public void render(TileEntity tile, double x,
			double y, double z, float f, int damage, float a) {
		TileMultiBlock multiBlockTile = (TileMultiBlock)tile;

		if(!multiBlockTile.canRender())
			return;

		matrix.push();

		//Initial setup

		//Rotate and move the model into position
		matrix.translate(x + 0.5, y, z + 0.5);
		Direction front = RotatableBlock.getFront(tile.getWorld().getBlockState(tile.getPos())); //tile.getWorldObj().getBlockMetadata(tile.xCoord, tile.yCoord, tile.zCoord));
		GL11.glRotatef((front.getXOffset() == 1 ? 180 : 0) + front.getZOffset()*90f, 0, 1, 0);
		matrix.translate(1f, 0, 0f);
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
		GlStateManager.disableLighting();
		bindTexture(tubeTexture);
		model.renderOnly("Tubes");
		GlStateManager.disableTexture();
		
		GlStateManager.color4f(0, 0.9f, col);
		OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, 196, 196);
		model.renderOnly("BlueRing");
		GlStateManager.color4f(col, col, col);
		GlStateManager.enableLighting();
		GlStateManager.enableTexture();
		
		matrix.pop();
	}
}
