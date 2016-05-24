package zmaster587.advancedRocketry.client.render;

import org.lwjgl.opengl.GL11;

import zmaster587.advancedRocketry.entity.EntityRocket;
import zmaster587.advancedRocketry.util.StorageChunk;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.MinecraftForgeClient;

public class RendererRocket extends Render {

	
	//TODO: possibly optimize with GL lists
	@Override
	public void doRender(Entity entity, double x,
			double y, double z, float f1,
			float f2) {

		StorageChunk storage  = ((EntityRocket)entity).storage;

		if(storage == null)
			return;

		if(Minecraft.getMinecraft().thePlayer == entity.riddenByEntity) {
			
			y = -1.25 -((EntityRocket)entity).stats.getSeatY();
		}
		
		//Find the halfway point along the XZ plane
		float halfx = storage.getSizeX()/2f;
		float halfz = storage.getSizeZ()/2f;

		GL11.glPushMatrix();
		GL11.glTranslatef((float)x - halfx, (float)y, (float)z - halfz);
		net.minecraft.client.renderer.RenderHelper.disableStandardItemLighting();

		//Render Each block
		RenderBlocks.getInstance().blockAccess = storage;
		Minecraft.getMinecraft().getTextureManager().bindTexture(TextureMap.locationBlocksTexture);
		for(int xx = 0; xx < storage.getSizeX(); xx++) {
			for(int zz = 0; zz < storage.getSizeZ(); zz++) {
				for(int yy = 0; yy < storage.getSizeY(); yy++) {
					Block block  = storage.getBlock(xx, yy, zz);
					if(block.canRenderInPass(MinecraftForgeClient.getRenderPass())) {
						Tessellator.instance.startDrawingQuads();
						RenderBlocks.getInstance().renderBlockByRenderType(block, xx, yy, zz);

						Tessellator.instance.draw();
					}
				}
			}

		}

		net.minecraft.client.renderer.RenderHelper.enableStandardItemLighting();
		
		//Render tile entities if applicable
		for(TileEntity tile : storage.getTileEntityList()) {
			TileEntitySpecialRenderer renderer = (TileEntitySpecialRenderer)TileEntityRendererDispatcher.instance.mapSpecialRenderers.get(tile.getClass());
			if(renderer != null ) {
				renderer.renderTileEntityAt(tile, tile.xCoord, tile.yCoord,  tile.zCoord, f1);
			}
		}
		GL11.glPopMatrix();
	}

	@Override
	protected ResourceLocation getEntityTexture(Entity p_110775_1_) {
		return null;
	}

}
