package zmaster587.advancedRocketry.client.render.multiblocks;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;
import zmaster587.advancedRocketry.backwardCompat.ModelFormatException;
import zmaster587.advancedRocketry.backwardCompat.WavefrontObject;
import zmaster587.libVulpes.block.RotatableBlock;
import zmaster587.libVulpes.render.RenderHelper;
import zmaster587.libVulpes.tile.multiblock.TileMultiblockMachine;

import java.util.List;

public class RendererCuttingMachine extends TileEntitySpecialRenderer {

	private WavefrontObject model;

	public final static ResourceLocation texture = new ResourceLocation("advancedrocketry:textures/models/cuttingMachine.png");

	//private final RenderItem dummyItem = Minecraft.getMinecraft().getRenderItem();

	public RendererCuttingMachine() {
		try {
			model = new WavefrontObject(new ResourceLocation("advancedrocketry:models/cuttingMachine.obj"));
		} catch (ModelFormatException e) {
			
			e.printStackTrace();
		}
	}

	@Override
	public void render(TileEntity tile, double x, double y, double z,
			float partialTicks, int destroyStage, float a) {
		TileMultiblockMachine multiBlockTile = (TileMultiblockMachine)tile;

		if(!multiBlockTile.canRender())
			return;

		GL11.glPushMatrix();

		//Initial setup

		//Rotate and move the model into position
		GL11.glTranslated(x+.5f, y, z + 0.5f);
		EnumFacing front = RotatableBlock.getFront(tile.getWorld().getBlockState(tile.getPos())); //tile.getWorldObj().getBlockMetadata(tile.xCoord, tile.yCoord, tile.zCoord));
		GL11.glRotatef((front.getXOffset() == 1 ? 180 : 0) + front.getZOffset()*90f, 0, 1, 0);
		GL11.glTranslated(-.5f, 0, -1.5f);

		if(multiBlockTile.isRunning()) {

			float progress = multiBlockTile.getProgress(0)/(float)multiBlockTile.getTotalProgress(0);
			float tray;
			tray = 2.2f*progress;



			List<ItemStack> inputList = multiBlockTile.getInputs();
			if(inputList != null && !inputList.isEmpty() && progress < 0.65) {
				ItemStack inputStack = ItemStack.EMPTY;
				for (ItemStack stack: inputList) {
					if (!stack.isEmpty() && inputStack.isEmpty())
						inputStack = stack;
				}

				GL11.glPushMatrix();
				GL11.glRotatef(90, 1, 0, 0);
				GL11.glTranslated(1f, tray + .45, -1.05);
				RenderHelper.renderItem(multiBlockTile, inputStack, Minecraft.getMinecraft().getRenderItem());
				GL11.glPopMatrix();
			}


			List<ItemStack> outputList = multiBlockTile.getOutputs();
			if(outputList != null && !outputList.isEmpty() && progress >= 0.65) {
				ItemStack stack = outputList.get(0);

				GL11.glPushMatrix();
				GL11.glRotatef(90, 1, 0, 0);
				GL11.glTranslated(1f, tray + .45, -1.05);
				RenderHelper.renderItem(multiBlockTile, stack, Minecraft.getMinecraft().getRenderItem());
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
