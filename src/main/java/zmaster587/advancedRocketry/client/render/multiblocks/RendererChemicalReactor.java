package zmaster587.advancedRocketry.client.render.multiblocks;

import net.minecraft.client.renderer.OpenGlHelper;
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
	public void renderTileEntityAt(TileEntity tile, double x, double y, double z,
			float partialTicks, int destroyStage) {
		
		TileChemicalReactor multiBlockTile = (TileChemicalReactor)tile;

		if(!multiBlockTile.canRender())
			return;

		GL11.glPushMatrix();

		
		//Rotate and move the model into position
		GL11.glPushMatrix();
		GL11.glTranslated(x+.5f, y, z + 0.5f);
		EnumFacing front = RotatableBlock.getFront(tile.getWorld().getBlockState(tile.getPos())); //tile.getWorldObj().getBlockMetadata(tile.xCoord, tile.yCoord, tile.zCoord));
		
		GL11.glRotatef((front.getFrontOffsetZ() == 1 ? 180 : 0) - front.getFrontOffsetX()*90f, 0, 1, 0);
		bindTexture(texture);
		model.renderOnly("mesh");
		GL11.glPopMatrix();
		
		
		
		GL11.glTranslated(x+.5f, y, z + 0.5f);
		GL11.glRotatef((front.getFrontOffsetZ() == 1 ? 180 : 0) - front.getFrontOffsetX()*90f, 0, 1, 0);
		
		GL11.glTranslated(0f, -0.5f, 1f );
		if(multiBlockTile.isRunning())
			GL11.glRotated((8*tile.getWorld().getTotalWorldTime()) % 360, 1, 0, 0);
		model.renderOnly("Cylinder");
		
		GL11.glPopMatrix();
	}
}
