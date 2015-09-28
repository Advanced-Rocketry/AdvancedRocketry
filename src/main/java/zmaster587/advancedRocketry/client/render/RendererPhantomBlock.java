package zmaster587.advancedRocketry.client.render;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;
import org.lwjgl.opengl.GL14;

import cpw.mods.fml.client.registry.ClientRegistry;
import zmaster587.advancedRocketry.tile.multiblock.TilePlaceholder;
import zmaster587.libVulpes.render.RenderHelper;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.MovingObjectPosition;
import net.minecraftforge.client.MinecraftForgeClient;

public class RendererPhantomBlock extends TileEntitySpecialRenderer {

	@Override
	public void renderTileEntityAt(TileEntity tile, double x,
			double y, double z, float t) {

		TilePlaceholder tileGhost = (TilePlaceholder)tile;
		Block block = tileGhost.getReplacedBlock();

		if(tileGhost.getReplacedTileEntity() != null && TileEntityRendererDispatcher.instance.hasSpecialRenderer(tileGhost.getReplacedTileEntity())) {
			GL11.glEnable(GL11.GL_BLEND);
			GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ZERO);
			TileEntityRendererDispatcher.instance.renderTileEntityAt(tileGhost.getReplacedTileEntity(), x, y, z, t);
			GL11.glDisable(GL11.GL_BLEND);
		}
		else if(block != null) {
			GL11.glPushMatrix();
			GL11.glTranslated(x,y,z);
			net.minecraft.client.renderer.RenderHelper.disableStandardItemLighting();
			//Render Each block
			Minecraft.getMinecraft().getTextureManager().bindTexture(TextureMap.locationBlocksTexture);
			RenderBlocks.getInstance().blockAccess = tileGhost.getWorldObj();
			GL11.glEnable(GL11.GL_BLEND);
			GL11.glDisable(GL11.GL_ALPHA_TEST);
			GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_DST_COLOR);
			Tessellator.instance.startDrawingQuads();
			
            if(block.getRenderType() == 0) {
                block.setBlockBoundsBasedOnState(tile.getWorldObj(), tile.xCoord, tile.yCoord, tile.zCoord);
                RenderBlocks.getInstance().setRenderBoundsFromBlock(block);
    			int l = block.colorMultiplier(tile.getWorldObj(), tile.xCoord, tile.yCoord, tile.zCoord);
    			
    	        float r = (float)(l >> 16 & 255) / 255.0F;
    	        float g = (float)(l >> 8 & 255) / 255.0F;
    	        float b = (float)(l & 255) / 255.0F;
    	        
    			RenderHelper.renderStandardBlockWithColorMultiplier(block, 0,0,0, r, g, b, 1f);
            }
            else
            	RenderBlocks.getInstance().renderBlockByRenderType(block, 0, 0, 0);
			Tessellator.instance.draw();
			GL11.glDisable(GL11.GL_BLEND);
			GL11.glPopMatrix();
		}
		
		if(block != null) {
			//If the player is mousing over this block
			MovingObjectPosition movingObjPos = Minecraft.getMinecraft().objectMouseOver;
			if(movingObjPos.blockX == tile.xCoord && movingObjPos.blockY == tile.yCoord && movingObjPos.blockZ == tile.zCoord) {
				ItemStack stack = tile.getWorldObj().getBlock(tile.xCoord, tile.yCoord, tile.zCoord).getPickBlock(movingObjPos, Minecraft.getMinecraft().theWorld, movingObjPos.blockX, movingObjPos.blockY, movingObjPos.blockZ, Minecraft.getMinecraft().thePlayer);
				if(stack == null)
					return;
				
				RenderHelper.renderTag(Minecraft.getMinecraft().thePlayer.getDistanceSq(movingObjPos.blockX, movingObjPos.blockY, movingObjPos.blockZ), stack.getDisplayName(), x,y,z, 10);
			}
		}

	}
}
