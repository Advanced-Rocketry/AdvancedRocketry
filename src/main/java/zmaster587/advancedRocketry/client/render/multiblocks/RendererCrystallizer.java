package zmaster587.advancedRocketry.client.render.multiblocks;

import java.util.List;

import org.lwjgl.opengl.GL11;

import zmaster587.advancedRocketry.tile.multiblock.TileMultiBlockMachine;
import zmaster587.libVulpes.block.RotatableBlock;
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

public class RendererCrystallizer extends TileEntitySpecialRenderer {

	IModelCustom model = AdvancedModelLoader.loadModel(new ResourceLocation("advancedrocketry:models/crystallizer.obj"));

	ResourceLocation texture = new ResourceLocation("advancedrocketry:textures/models/crystallizer.png");

	private final RenderItem dummyItem = new RenderItem();

	public RendererCrystallizer() {
		dummyItem.setRenderManager(RenderManager.instance);
	}

	@Override
	public void renderTileEntityAt(TileEntity tile, double x,
			double y, double z, float f) {
		TileMultiBlockMachine multiBlockTile = (TileMultiBlockMachine)tile;

		if(!multiBlockTile.isComplete())
			return;

		GL11.glPushMatrix();

		//Initial setup
		int bright = tile.getWorldObj().getLightBrightnessForSkyBlocks(tile.xCoord, tile.yCoord + 1, tile.zCoord,0);
		int brightX = bright % 65536;
		int brightY = bright / 65536;
		OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, brightX, brightY);

		//Rotate and move the model into position
		GL11.glTranslated(x+.5f, y, z + 0.5f);
		ForgeDirection front = RotatableBlock.getFront(tile.blockMetadata);
		GL11.glRotatef((front.offsetX == 1 ? 180 : 0) + front.offsetZ*90f, 0, 1, 0);
		GL11.glTranslated(-.5f, 0, -1.5f);

		if(multiBlockTile.isRunning()) {

			float progress = multiBlockTile.getProgress()/(float)multiBlockTile.getTotalProgress();

			//if(progress > 0.5) {
				List<ItemStack> outputList = multiBlockTile.getOutputs();
				if(outputList != null && !outputList.isEmpty()) {
					ItemStack stack = outputList.get(0);
					EntityItem entity = new EntityItem(tile.getWorldObj());

					entity.setEntityItemStack(stack);
					entity.hoverStart = 0;

					int rotation = (int)(tile.getWorldObj().getTotalWorldTime() % 360);
					GL11.glPushMatrix();
					GL11.glTranslatef(0, 1, 0);
					
					GL11.glPushMatrix();
					GL11.glTranslated(1, 0.2, 0.7);
					GL11.glRotatef(rotation, 0, 1, 0);
					GL11.glScalef(progress, progress, progress);
					dummyItem.doRender(entity, 0,0,0,  0.0F, 0.0F);
					GL11.glPopMatrix();
					
					GL11.glPushMatrix();
					GL11.glTranslated(1, 0.2, 1.5);
					GL11.glRotatef(rotation, 0, 1, 0);
					GL11.glScalef(progress, progress, progress);
					dummyItem.doRender(entity, 0,0,0,  0.0F, 0.0F);
					GL11.glPopMatrix();

					GL11.glPushMatrix();
					GL11.glTranslated(1, 0.2, 2.3);
					GL11.glRotatef(rotation, 0, 1, 0);
					GL11.glScalef(progress, progress, progress);
					dummyItem.doRender(entity, 0,0,0,  0.0F, 0.0F);
					GL11.glPopMatrix();
					
					GL11.glPopMatrix();
					
				}
			//}

			bindTexture(texture);
			model.renderPart("Hull");

			GL11.glPushMatrix();
			GL11.glEnable(GL11.GL_BLEND);
			GL11.glBlendFunc( GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA );

			GL11.glColor4f(0.1f, 0.1f, 0.1f, 0.9f);
			GL11.glDisable(GL11.GL_TEXTURE_2D);
			GL11.glTranslatef(0, 1.1f, 0);
			
			//Fill before emptying
			if(progress < 0.05)
				GL11.glScaled(1, 20*progress, 1);
			else
				GL11.glScaled(1, (1.1-(progress*1.111)), 1);
			
			GL11.glTranslatef(0, -1.1f, 0);
			model.renderPart("Liquid");


			GL11.glEnable(GL11.GL_TEXTURE_2D);
			GL11.glDisable(GL11.GL_BLEND);
			GL11.glPopMatrix();

		}
		else {
			bindTexture(texture);
			model.renderPart("Hull");
		}
		GL11.glPopMatrix();
	}

}
