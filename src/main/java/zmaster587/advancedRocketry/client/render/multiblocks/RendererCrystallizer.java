package zmaster587.advancedRocketry.client.render.multiblocks;

import java.util.List;

import org.lwjgl.opengl.GL11;

import zmaster587.advancedRocketry.backwardCompat.ModelFormatException;
import zmaster587.advancedRocketry.backwardCompat.WavefrontObject;
import zmaster587.libVulpes.block.RotatableBlock;
import zmaster587.libVulpes.tile.multiblock.TileMultiblockMachine;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.RenderItem;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.item.EntityItemFrame;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemSkull;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.storage.MapData;

public class RendererCrystallizer extends TileEntitySpecialRenderer {

	WavefrontObject model;

	ResourceLocation texture = new ResourceLocation("advancedrocketry:textures/models/crystallizer.png");


	public RendererCrystallizer() {

		try {
			model =  new WavefrontObject(new ResourceLocation("advancedrocketry:models/crystallizer.obj"));
		} catch (ModelFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void render(TileEntity tile, double x,
			double y, double z, float f,  int destroyStage, float a) {
		TileMultiblockMachine multiBlockTile = (TileMultiblockMachine)tile;

		if(!multiBlockTile.canRender())
			return;

		GL11.glPushMatrix();

		//Rotate and move the model into position
		GL11.glTranslated(x+.5f, y, z + 0.5f);
		EnumFacing front = RotatableBlock.getFront(tile.getWorld().getBlockState(tile.getPos())); //tile.getWorldObj().getBlockMetadata(tile.xCoord, tile.yCoord, tile.zCoord));
		GL11.glRotatef((front.getFrontOffsetX() == 1 ? 180 : 0) + front.getFrontOffsetZ()*90f, 0, 1, 0);
		GL11.glTranslated(-.5f, 0, -1.5f);

		if(multiBlockTile.isRunning()) {

			float progress = multiBlockTile.getProgress(0)/(float)multiBlockTile.getTotalProgress(0);

			bindTexture(texture);
			model.renderPart("Hull");

			List<ItemStack> outputList = multiBlockTile.getOutputs();
			if(outputList != null && !outputList.isEmpty()) {
				ItemStack stack = outputList.get(0);
				EntityItem entity = new EntityItem(tile.getWorld());

				entity.setItem(stack);
				entity.hoverStart = 0;

				int rotation = (int)(tile.getWorld().getTotalWorldTime() % 360);
				GL11.glPushMatrix();
				GL11.glTranslatef(0, 1, 0);

				GL11.glPushMatrix();
				GL11.glTranslated(1, 0.2, 0.7);
				GL11.glRotatef(rotation, 0, 1, 0);
				GL11.glScalef(progress, progress, progress);
				zmaster587.libVulpes.render.RenderHelper.renderItem(multiBlockTile, stack, Minecraft.getMinecraft().getRenderItem());
				GL11.glPopMatrix();

				GL11.glPushMatrix();
				GL11.glTranslated(1, 0.2, 1.5);
				GL11.glRotatef(rotation, 0, 1, 0);
				GL11.glScalef(progress, progress, progress);
				zmaster587.libVulpes.render.RenderHelper.renderItem(multiBlockTile, stack, Minecraft.getMinecraft().getRenderItem());
				GL11.glPopMatrix();

				GL11.glPushMatrix();
				GL11.glTranslated(1, 0.2, 2.3);
				GL11.glRotatef(rotation, 0, 1, 0);
				GL11.glScalef(progress, progress, progress);
				zmaster587.libVulpes.render.RenderHelper.renderItem(multiBlockTile, stack, Minecraft.getMinecraft().getRenderItem());
				GL11.glPopMatrix();

				GL11.glPopMatrix();



				GL11.glPushMatrix();
				GL11.glEnable(GL11.GL_BLEND);
				GL11.glBlendFunc( GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA );

				int color = Minecraft.getMinecraft().getItemColors().getColorFromItemstack(stack, 0);

				float divisor = 1/255f;

				GL11.glColor4f((color & 0xFF)*divisor*.5f, ((color & 0xFF00) >>> 8)*divisor*.5f,  ((color & 0xFF0000) >>> 16)*divisor*.5f, 0xE4*divisor);
				GL11.glDisable(GL11.GL_TEXTURE_2D);
				GL11.glTranslatef(0, 1.1f, 0);

				//Fill before emptying
				if(progress < 0.05)
					GL11.glScaled(1, 20*progress, 1);
				else
					GL11.glScaled(1, (1.1-(progress*1.111)), 1);

				GL11.glTranslatef(0, -1.1f, 0);
				model.renderPart("Liquid");
			}
			
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
