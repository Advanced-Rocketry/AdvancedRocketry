package zmaster587.advancedRocketry.client.render.multiblocks;

import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.AdvancedModelLoader;
import net.minecraftforge.client.model.IModelCustom;
import net.minecraftforge.common.util.ForgeDirection;

import org.lwjgl.opengl.GL11;

import zmaster587.advancedRocketry.api.material.MaterialRegistry;
import zmaster587.advancedRocketry.tile.multiblock.TileMultiblockMachine;
import zmaster587.advancedRocketry.util.Debugger;
import zmaster587.libVulpes.block.RotatableBlock;

public class RendererLathe extends TileEntitySpecialRenderer {
	IModelCustom model = AdvancedModelLoader.loadModel(new ResourceLocation("advancedrocketry:models/lathe.obj"));

	ResourceLocation texture = new ResourceLocation("advancedrocketry:textures/models/lathe.png");

	private static int bodyList;

	public RendererLathe() {
		GL11.glNewList(bodyList = GL11.glGenLists(1), GL11.GL_COMPILE);
		model.renderOnly("body");
		GL11.glEndList();
	}

	@Override
	public void renderTileEntityAt(TileEntity tile, double x,
			double y, double z, float f) {
		TileMultiblockMachine multiBlockTile = (TileMultiblockMachine)tile;

		if(!multiBlockTile.canRender())
			return;

		GL11.glPushMatrix();

		//Initial setup
		int bright = tile.getWorldObj().getLightBrightnessForSkyBlocks(tile.xCoord, tile.yCoord + 1, tile.zCoord,0);
		int brightX = bright % 65536;
		int brightY = bright / 65536;
		OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, brightX, brightY);

		//Rotate and move the model into position
		GL11.glTranslated(x + .5f, y, z + 0.5f);
		ForgeDirection front = RotatableBlock.getFront(tile.getBlockMetadata());
		GL11.glRotatef((front.offsetX == 1 ? 180 : 0) + front.offsetZ*90f, 0, 1, 0);
		GL11.glTranslated(-.5f, -1f, -2.5f);

		if(multiBlockTile.isRunning()) {

			float progress = multiBlockTile.getProgress(0)/(float)multiBlockTile.getTotalProgress(0);

			bindTexture(texture);
			if(Debugger.renderList)
				GL11.glCallList(bodyList);
			else 
				model.renderPart("body");

			GL11.glPushMatrix();
			if(progress < 0.95f)
				GL11.glTranslatef(0f, 0f, progress/.95f);
			else
				GL11.glTranslatef(0f, 0f, (1 - progress)/.05f);

			model.renderOnly("Tray");
			GL11.glPopMatrix();

			GL11.glPushMatrix();
			GL11.glTranslatef(.5f, 1.5625f, 0f);
			GL11.glRotatef(progress*1500, 0, 0, 1);
			model.renderOnly("Cylinder");

			int color = MaterialRegistry.getMaterialFromItemStack(multiBlockTile.getOutputs().get(0)).getColor();
			GL11.glColor3d((0xff & color >> 16)/256f, (0xff & color >> 8)/256f , (color & 0xff)/256f);

			model.renderOnly("rod");
			GL11.glPopMatrix();

		}
		else {
			bindTexture(texture);
			if(Debugger.renderList)
				GL11.glCallList(bodyList);
			else 
				model.renderPart("body");

			model.renderPart("Tray");
			//model.renderAllExcept("rod", "Cylinder");
		}
		GL11.glPopMatrix();
	}
}
