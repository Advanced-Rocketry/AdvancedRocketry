package zmaster587.advancedRocketry.client.render.multiblocks;

import java.util.List;

import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.AdvancedModelLoader;
import net.minecraftforge.client.model.IModelCustom;
import net.minecraftforge.common.util.ForgeDirection;

import org.lwjgl.opengl.GL11;

import zmaster587.libVulpes.block.RotatableBlock;
import zmaster587.libVulpes.tile.multiblock.TileMultiblockMachine;

public class RendererCuttingMachine extends TileEntitySpecialRenderer {

	IModelCustom model = AdvancedModelLoader.loadModel(new ResourceLocation("advancedrocketry:models/cuttingMachine.obj"));

	public static ResourceLocation texture = new ResourceLocation("advancedrocketry:textures/models/cuttingMachine.png");

	private final RenderItem dummyItem = new RenderItem();

	public RendererCuttingMachine() {
		dummyItem.setRenderManager(RenderManager.instance);
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
		GL11.glTranslated(x+.5f, y, z + 0.5f);
		ForgeDirection front = RotatableBlock.getFront(tile.getBlockMetadata());
		GL11.glRotatef((front.offsetX == 1 ? 180 : 0) + front.offsetZ*90f, 0, 1, 0);
		GL11.glTranslated(-.5f, 0, -1.5f);

		if(multiBlockTile.isRunning()) {

			float progress = multiBlockTile.getProgress(0)/(float)multiBlockTile.getTotalProgress(0);
			float tray;
			tray = 2.2f*progress;



			List<ItemStack> outputList = multiBlockTile.getOutputs();
			if(outputList != null && !outputList.isEmpty()) {
				ItemStack stack = outputList.get(0);
				EntityItem entity = new EntityItem(tile.getWorldObj());

				entity.setEntityItemStack(stack);
				entity.hoverStart = 0;

				GL11.glPushMatrix();
				GL11.glRotatef(90, 1, 0, 0);
				dummyItem.doRender(entity, 1, tray + .25, -1.05, 0.0F, 0.0F);
				GL11.glPopMatrix();
			}

			bindTexture(texture);
			model.renderPart("Hull");
			
			GL11.glPushMatrix();
			
			GL11.glTranslatef(1f, 1f, 1.5f);
			
			GL11.glRotatef(-6*multiBlockTile.getProgress(0) % 360, 1, 0, 0);
			GL11.glTranslatef(-1f, -1f, -1.5f);
			model.renderPart("Saw");
			GL11.glPopMatrix();

		}
		else {
			bindTexture(texture);
			model.renderAll();
		}
		GL11.glPopMatrix();
	}
}
