package zmaster587.advancedRocketry.client.render;

import org.lwjgl.opengl.GL11;

import zmaster587.libVulpes.block.RotatableBlock;
import zmaster587.libVulpes.render.RenderHelper;
import zmaster587.libVulpes.tile.multiblock.TileMultiBlock;
import zmaster587.libVulpes.tile.multiblock.TilePlaceholder;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.MovingObjectPosition;
import net.minecraftforge.common.util.ForgeDirection;

public class RendererPhantomBlock extends TileEntitySpecialRenderer {

	private static RenderBlocks renderBlocks = RenderBlocks.getInstance();

	@Override
	public void renderTileEntityAt(TileEntity tile, double x,
			double y, double z, float t) {

		TilePlaceholder tileGhost = (TilePlaceholder)tile;
		Block block = tileGhost.getReplacedBlock();

		if(tileGhost.getReplacedTileEntity() != null && !(tileGhost.getReplacedTileEntity() instanceof TileMultiBlock) && TileEntityRendererDispatcher.instance.hasSpecialRenderer(tileGhost.getReplacedTileEntity())) {
			GL11.glEnable(GL11.GL_BLEND);
			GL11.glBlendFunc(GL11.GL_ONE_MINUS_SRC_COLOR, GL11.GL_SRC_ALPHA);
			GL11.glColor4f(1f, 1f, 1f,0.7f);
			TileEntityRendererDispatcher.instance.renderTileEntityAt(tileGhost.getReplacedTileEntity(), x, y, z, t);
			GL11.glDisable(GL11.GL_BLEND);
		}
		else if(block != null) {
			GL11.glPushMatrix();

			GL11.glTranslated(x,y,z);
			if(block instanceof RotatableBlock) {
				ForgeDirection direction = ForgeDirection.getOrientation(tileGhost.getReplacedBlockMeta());
				GL11.glTranslated(.5f,.5f,.5f);
				if(direction.offsetX != 0 ) {
					GL11.glRotatef( -90, 0,direction.offsetX,0);
				}
				else if(direction.offsetZ  == 1) {
					GL11.glRotatef( 180, direction.offsetZ,0,0);
					GL11.glRotatef( 180, 0,0,1);
				}
				//GL11.glScalef(-1, -1, -1);
				GL11.glTranslated(-.5f,-.5f,-.5f);
			}

			net.minecraft.client.renderer.RenderHelper.disableStandardItemLighting();
			//Render Each block
			Minecraft.getMinecraft().getTextureManager().bindTexture(TextureMap.locationBlocksTexture);
			renderBlocks.blockAccess = tileGhost.getWorldObj();
			renderBlocks.renderAllFaces = true;

			GL11.glEnable(GL11.GL_BLEND);
			GL11.glBlendFunc(GL11.GL_ONE_MINUS_SRC_COLOR, GL11.GL_SRC_ALPHA);
			
			Tessellator.instance.startDrawingQuads();

			if(block.getRenderType() == 0) {
				block.setBlockBoundsBasedOnState(tile.getWorldObj(), tile.xCoord, tile.yCoord, tile.zCoord);
				renderBlocks.setRenderBoundsFromBlock(block);
				int l = block.colorMultiplier(tile.getWorldObj(), tile.xCoord, tile.yCoord, tile.zCoord);

				float r = (float)(l >> 16 & 255) / 255.0F;
				float g = (float)(l >> 8 & 255) / 255.0F;
				float b = (float)(l & 255) / 255.0F;
				RenderHelper.renderStandardBlockWithColorMultiplier(block, 0,0,0, r, g, b, .3f);
			}
			else
				renderBlocks.renderBlockByRenderType(block, 0, 0, 0);
			Tessellator.instance.draw();
			net.minecraft.client.renderer.RenderHelper.enableStandardItemLighting();
			GL11.glDisable(GL11.GL_BLEND);
			GL11.glPopMatrix();
		}

		if(block != null) {
			//If the player is mousing over this block
			MovingObjectPosition movingObjPos = Minecraft.getMinecraft().objectMouseOver;
			if(Minecraft.getMinecraft().objectMouseOver != null && movingObjPos.blockX == tile.xCoord && movingObjPos.blockY == tile.yCoord && movingObjPos.blockZ == tile.zCoord) {
				ItemStack stack = tile.getWorldObj().getBlock(tile.xCoord, tile.yCoord, tile.zCoord).getPickBlock(movingObjPos, Minecraft.getMinecraft().theWorld, movingObjPos.blockX, movingObjPos.blockY, movingObjPos.blockZ, Minecraft.getMinecraft().thePlayer);
				if(stack == null)
					RenderHelper.renderTag(Minecraft.getMinecraft().thePlayer.getDistanceSq(movingObjPos.blockX, movingObjPos.blockY, movingObjPos.blockZ), "THIS IS AN ERROR, CONTACT THE DEV!!!", x,y,z, 10);
				else
					RenderHelper.renderTag(Minecraft.getMinecraft().thePlayer.getDistanceSq(movingObjPos.blockX, movingObjPos.blockY, movingObjPos.blockZ), stack.getDisplayName(), x,y,z, 10);
			}
		}
	}
}
