package zmaster587.advancedRocketry.client.render.multiblocks;

import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.AdvancedModelLoader;
import net.minecraftforge.client.model.IModelCustom;
import net.minecraftforge.common.util.ForgeDirection;

import org.lwjgl.opengl.GL11;

import zmaster587.libVulpes.block.RotatableBlock;
import zmaster587.libVulpes.tile.multiblock.TileMultiPowerConsumer;

public class RenderBiomeScanner extends TileEntitySpecialRenderer {

	IModelCustom model = AdvancedModelLoader.loadModel(new ResourceLocation("advancedrocketry:models/biomeScanner.obj"));

	ResourceLocation texture = new ResourceLocation("advancedrocketry:textures/models/biomeScanner.jpg");

	public RenderBiomeScanner(){}
	
	@Override
	public void renderTileEntityAt(TileEntity tile, double x,
			double y, double z, float f) {
		TileMultiPowerConsumer multiBlockTile = (TileMultiPowerConsumer)tile;

		if(!multiBlockTile.canRender())
			return;

		GL11.glPushMatrix();

		//Initial setup
		int bright = tile.getWorldObj().getLightBrightnessForSkyBlocks(tile.xCoord, tile.yCoord - 1, tile.zCoord,0);
		int brightX = bright % 65536;
		int brightY = bright / 65536;
		OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, brightX, brightY);

		//Rotate and move the model into position
		ForgeDirection front = RotatableBlock.getFront(tile.getBlockMetadata());//tile.getWorldObj().getBlockMetadata(tile.xCoord, tile.yCoord, tile.zCoord));
		GL11.glTranslated(x + .5, y, z + .5);

		bindTexture(texture);
		
		model.renderAll();
		
		GL11.glPopMatrix();
	}

}
