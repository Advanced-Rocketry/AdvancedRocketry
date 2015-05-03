package zmaster587.advancedRocketry.client.render;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.client.registry.ClientRegistry;
import zmaster587.advancedRocketry.entity.EntityRocket;
import zmaster587.advancedRocketry.util.StorageChunk;
import zmaster587.libVulpes.render.RenderHelper;
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

public class RendererRocket extends Render {

	@Override
	public void doRender(Entity entity, double x,
			double y, double z, float f1,
			float f2) {

		StorageChunk storage  = ((EntityRocket)entity).storage;
		//RenderBlocks.getInstance().renderBlockAllFaces(p_147769_1_, p_147769_2_, p_147769_3_, p_147769_4_);

		if(storage == null)
			return;

		float halfx = storage.getSizeX()/2f;
		float halfy = storage.getSizeY()/2f;
		float halfz = storage.getSizeZ()/2f;

		GL11.glPushMatrix();
		GL11.glTranslatef((float)x - halfx, (float)y, (float)z - halfz);
		net.minecraft.client.renderer.RenderHelper.disableStandardItemLighting();


		//RenderBlocks blk = new RenderBlocks(storage);
		//net.minecraftforge.client.ForgeHooksClient.setWorldRendererRB(blk);

		RenderBlocks.getInstance().blockAccess = storage;
		Minecraft.getMinecraft().getTextureManager().bindTexture(TextureMap.locationBlocksTexture);
		for(int xx = 0; xx < storage.getSizeX(); xx++) {
			for(int zz = 0; zz < storage.getSizeZ(); zz++) {
				for(int yy = 0; yy < storage.getSizeY(); yy++) {
					if(storage.getBlock(xx, yy, zz).canRenderInPass(0)) {
						Tessellator.instance.startDrawingQuads();
						RenderBlocks.getInstance().renderBlockByRenderType(storage.getBlock(xx, yy, zz), xx, yy, zz);

						//RenderBlocks.getInstance().renderBlockAllFaces();
						Tessellator.instance.draw();
					}
				}
			}

		}

		net.minecraft.client.renderer.RenderHelper.enableStandardItemLighting();
		for(TileEntity tile : storage.getTileEntityList()) {
			TileEntitySpecialRenderer renderer = (TileEntitySpecialRenderer)TileEntityRendererDispatcher.instance.mapSpecialRenderers.get(tile.getClass());
			if(renderer != null )
				renderer.renderTileEntityAt(tile, tile.xCoord, tile.yCoord,  tile.zCoord, f1);
		}

		GL11.glPopMatrix();
	}

	@Override
	protected ResourceLocation getEntityTexture(Entity p_110775_1_) {
		return null;
	}

}
