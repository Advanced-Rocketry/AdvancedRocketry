package zmaster587.advancedRocketry.client.render.multiblocks;

import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;
import zmaster587.advancedRocketry.backwardCompat.ModelFormatException;
import zmaster587.advancedRocketry.backwardCompat.WavefrontObject;
import zmaster587.advancedRocketry.tile.multiblock.machine.TileChemicalReactor;
import zmaster587.libVulpes.block.RotatableBlock;

public class RendererChemicalReactor  extends TileEntitySpecialRenderer {

	WavefrontObject model;
	ResourceLocation texture;
	
	public RendererChemicalReactor(String modelPath, String texturePath) {
		texture = new ResourceLocation(texturePath);
		try {
			model = new WavefrontObject(new ResourceLocation(modelPath));
		} catch (ModelFormatException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void render(TileEntity tile, double x, double y, double z,
			float partialTicks, int destroyStage, float a) {
		
		TileChemicalReactor multiBlockTile = (TileChemicalReactor)tile;

		if(!multiBlockTile.canRender())
			return;

		//Rotate and move the model into position
		GL11.glPushMatrix();
		GL11.glTranslated(x+.5f, y, z + 0.5f);
		EnumFacing front = RotatableBlock.getFront(tile.getWorld().getBlockState(tile.getPos())); //tile.getWorldObj().getBlockMetadata(tile.xCoord, tile.yCoord, tile.zCoord));

		GL11.glRotatef((front.getZOffset() == 1 ? 180 : 0) - front.getXOffset()*90f, 0, 1, 0);
		GL11.glTranslated(1.5f, -1.0f, -.5f);

		bindTexture(texture);
		model.renderOnly("Hull");
		GL11.glPopMatrix();
	}
}
