package zmaster587.advancedRocketry.client.render.multiblocks;

import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;
import zmaster587.advancedRocketry.backwardCompat.ModelFormatException;
import zmaster587.advancedRocketry.backwardCompat.WavefrontObject;
import zmaster587.libVulpes.api.material.MaterialRegistry;
import zmaster587.libVulpes.block.RotatableBlock;
import zmaster587.libVulpes.tile.multiblock.TileMultiblockMachine;

public class RendererLathe extends TileEntitySpecialRenderer {
	WavefrontObject model;

	ResourceLocation texture = new ResourceLocation("advancedrocketry:textures/models/lathe.png");

	public RendererLathe() {
		try {
			model = new WavefrontObject(new ResourceLocation("advancedrocketry:models/lathe.obj"));
		} catch (ModelFormatException e) {
			e.printStackTrace();
		}

		GL11.glNewList(GL11.glGenLists(1), GL11.GL_COMPILE);
		model.renderOnly("Hull");
		GL11.glEndList();
	}

	@Override
	public void render(TileEntity tile, double x,
			double y, double z, float f, int damage, float a) {
		TileMultiblockMachine multiBlockTile = (TileMultiblockMachine)tile;

		if(!multiBlockTile.canRender())
			return;

		GL11.glPushMatrix();

		//Rotate and move the model into position
		GL11.glTranslated(x + .5f, y, z + 0.5f);
		EnumFacing front = RotatableBlock.getFront(tile.getWorld().getBlockState(tile.getPos())); //tile.getWorldObj().getBlockMetadata(tile.xCoord, tile.yCoord, tile.zCoord));
		GL11.glRotatef((front.getXOffset() == 1 ? 0 : 180) + front.getZOffset()*90f, 0, 1, 0);
		GL11.glTranslated(-.5f, -1f, -2.5f);


		ItemStack outputStack;
		if(multiBlockTile.isRunning()) {

			float progress = multiBlockTile.getProgress(0)/(float)multiBlockTile.getTotalProgress(0);

			bindTexture(texture);
			model.renderPart("Hull");

			GL11.glPushMatrix();

			if(progress < 0.95f)
				GL11.glTranslatef(0f, 0f, -(progress/.85f));
			else
				GL11.glTranslatef(0f, 0f, -((1 - progress)/.05f));

			model.renderOnly("Tool");
			GL11.glPopMatrix();

			GL11.glPushMatrix();
			GL11.glTranslatef(0.375f, 0.9375f, 0f);
			GL11.glRotatef(progress*1500, 0, 0, 1);
			model.renderOnly("Shaft");
			GL11.glPopMatrix();

			int color;
			//Check for rare bug when outputs is null, usually occurs if player opens machine within 1st tick
			if(multiBlockTile.getOutputs() != null && !(outputStack = multiBlockTile.getOutputs().get(0)).isEmpty())
				color = MaterialRegistry.getColorFromItemMaterial(outputStack);
			else
				color = 0;

			GL11.glPushMatrix();
			GL11.glColor3d((0xff & color >> 16)/256f, (0xff & color >> 8)/256f , (color & 0xff)/256f);
			GL11.glTranslatef(0.375f, 1.1875f, 0f);
			GL11.glRotatef(progress*1500, 0, 0, 1);
			model.renderOnly("Rod");
			GL11.glPopMatrix();
			
			GL11.glColor4f(1f, 1f, 1f, 1f);
		}
		else {
			bindTexture(texture);
			model.renderPart("Hull");

			model.renderPart("Tool");
			//model.renderAllExcept("rod", "Cylinder");

			GL11.glPushMatrix();
			GL11.glTranslatef(0.375f, 0.9375f, 0f);
			model.renderOnly("Shaft");
			GL11.glPopMatrix();
		}
		GL11.glPopMatrix();
	}
}
