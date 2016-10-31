package zmaster587.advancedRocketry.client.render.multiblocks;

import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.AdvancedModelLoader;
import net.minecraftforge.client.model.IModelCustom;
import net.minecraftforge.common.util.ForgeDirection;

import org.lwjgl.opengl.GL11;

import zmaster587.advancedRocketry.inventory.TextureResources;
import zmaster587.advancedRocketry.tile.multiblock.TileRailgun;
import zmaster587.libVulpes.block.RotatableBlock;
import zmaster587.libVulpes.tile.multiblock.TileMultiBlock;

public class RendererRailgun extends TileEntitySpecialRenderer {
	
	IModelCustom model = AdvancedModelLoader.loadModel(new ResourceLocation("advancedrocketry:models/railgun.obj"));

	ResourceLocation texture =  new ResourceLocation("advancedRocketry:textures/models/railgun.png");
	
	public RendererRailgun() {
	}
	
	@Override
	public void renderTileEntityAt(TileEntity tile, double x,
			double y, double z, float f) {
		TileRailgun multiBlockTile = (TileRailgun)tile;

		if(!multiBlockTile.canRender())
			return;

		GL11.glPushMatrix();

		//Initial setup
		int bright = tile.getWorldObj().getLightBrightnessForSkyBlocks(tile.xCoord, tile.yCoord + 1, tile.zCoord,0);
		int brightX = bright % 65536;
		int brightY = bright / 65536;
		OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, brightX, brightY);

		GL11.glTranslated(x + 0.5, y, z + .5);
		//Rotate and move the model into position
		ForgeDirection front = RotatableBlock.getFront(tile.getBlockMetadata());
		GL11.glRotatef((front.offsetX == 1 ? 180 : 0) + front.offsetZ*90f, 0, 1, 0);
		GL11.glTranslated(1f, 0, 0f);
		bindTexture(texture);
		
		
		if(tile.getWorldObj().getTotalWorldTime() - multiBlockTile.recoil - 20 <= 0) {
			model.renderOnly("Base");
			GL11.glPushMatrix();
			GL11.glTranslated(0, (-20+(tile.getWorldObj().getTotalWorldTime() - multiBlockTile.recoil))/100f, 0);
			model.renderOnly("Barrel");
			GL11.glPopMatrix();
		}
		else
			model.renderAll();
		
		GL11.glPopMatrix();
	}
}
