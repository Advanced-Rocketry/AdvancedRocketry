package zmaster587.advancedRocketry.client.render.multiblocks;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.AdvancedModelLoader;
import net.minecraftforge.client.model.IModelCustom;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTankInfo;

import org.lwjgl.opengl.GL11;

import zmaster587.advancedRocketry.tile.multiblock.TileMultiblockMachine;
import zmaster587.advancedRocketry.tile.multiblock.machine.TileChemicalReactor;
import zmaster587.libVulpes.block.RotatableBlock;
import zmaster587.libVulpes.render.RenderHelper;

public class RendererChemicalReactor  extends TileEntitySpecialRenderer {

	IModelCustom model;
	ResourceLocation texture;
	
	public RendererChemicalReactor(String modelPath, String texturePath) {
		texture = new ResourceLocation(texturePath);
		model = AdvancedModelLoader.loadModel(new ResourceLocation(modelPath));
	}
	
	@Override
	public void renderTileEntityAt(TileEntity tile, double x,
			double y, double z, float f) {
		TileChemicalReactor multiBlockTile = (TileChemicalReactor)tile;

		if(!multiBlockTile.canRender())
			return;

		GL11.glPushMatrix();

		//Initial setup
		int bright = tile.getWorldObj().getLightBrightnessForSkyBlocks(tile.xCoord, tile.yCoord + 1, tile.zCoord,0);
		int brightX = bright % 65536;
		int brightY = bright / 65536;
		OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, brightX, brightY);
		
		//Rotate and move the model into position
		GL11.glPushMatrix();
		GL11.glTranslated(x+.5f, y, z + 0.5f);
		ForgeDirection front = RotatableBlock.getFront(tile.getBlockMetadata());
		GL11.glRotatef((front.offsetZ == 1 ? 180 : 0) - front.offsetX*90f, 0, 1, 0);
		bindTexture(texture);
		model.renderOnly("mesh");
		GL11.glPopMatrix();
		
		
		
		GL11.glTranslated(x+.5f, y, z + 0.5f);
		GL11.glRotatef((front.offsetZ == 1 ? 180 : 0) - front.offsetX*90f, 0, 1, 0);
		
		GL11.glTranslated(0f, -0.5f, 1f );
		if(multiBlockTile.isRunning())
			GL11.glRotated((8*tile.getWorldObj().getTotalWorldTime()) % 360, 1, 0, 0);
		model.renderOnly("Cylinder");
		
		GL11.glPopMatrix();
	}
}
