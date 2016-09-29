package zmaster587.advancedRocketry.client.render;

import org.lwjgl.opengl.GL11;

import zmaster587.libVulpes.block.RotatableBlock;
import zmaster587.libVulpes.render.RenderHelper;
import zmaster587.libVulpes.tile.multiblock.TileMultiBlock;
import zmaster587.libVulpes.tile.multiblock.TilePlaceholder;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockRendererDispatcher;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.VertexBuffer;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;

public class RendererPhantomBlock extends TileEntitySpecialRenderer {

	private static BlockRendererDispatcher renderBlocks = Minecraft.getMinecraft().getBlockRendererDispatcher();

	@Override
	public void renderTileEntityAt(TileEntity tile, double x,
			double y, double z, float t, int damage) {

		renderBlocks = Minecraft.getMinecraft().getBlockRendererDispatcher();
		TilePlaceholder tileGhost = (TilePlaceholder)tile;

		IBlockState state = tileGhost.getReplacedState();

		//TODO: bring TESRS back
		/*if(tileGhost.getReplacedTileEntity() != null && !(tileGhost.getReplacedTileEntity() instanceof TileMultiBlock) && TileEntityRendererDispatcher.instance.hasSpecialRenderer(tileGhost.getReplacedTileEntity())) {
			GL11.glEnable(GL11.GL_BLEND);
			GL11.glBlendFunc(GL11.GL_ONE_MINUS_SRC_COLOR, GL11.GL_SRC_ALPHA);
			GL11.glColor4f(1f, 1f, 1f,0.7f);
			TileEntityRendererDispatcher.instance.renderTileEntityAt(tileGhost.getReplacedTileEntity(), x, y, z, t);
			GL11.glDisable(GL11.GL_BLEND);
		}*/
		
		GL11.glPushMatrix();

		GL11.glTranslated(x - tile.getPos().getX(),y - tile.getPos().getY(),z - tile.getPos().getZ());

		net.minecraft.client.renderer.RenderHelper.disableStandardItemLighting();
		//Render Each block

		this.bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glBlendFunc(GL11.GL_ONE_MINUS_SRC_COLOR, GL11.GL_SRC_ALPHA);
		Tessellator tess = Tessellator.getInstance();
		VertexBuffer buffer =tess.getBuffer();

		buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.BLOCK);
		IBakedModel model = renderBlocks.getModelForState(state);
		renderBlocks.getBlockModelRenderer().renderModel( tile.getWorld(), model, state, tile.getPos(), buffer, false);
		tess.draw();

		net.minecraft.client.renderer.RenderHelper.enableStandardItemLighting();
		GL11.glDisable(GL11.GL_BLEND);
		GL11.glPopMatrix();


		//TODO: bring tags back
		/*if(block != null) {
			//If the player is mousing over this block
			MovingObjectPosition movingObjPos = Minecraft.getMinecraft().objectMouseOver;
			if(Minecraft.getMinecraft().objectMouseOver != null && movingObjPos.blockX == tile.xCoord && movingObjPos.blockY == tile.yCoord && movingObjPos.blockZ == tile.zCoord) {
				ItemStack stack = tile.getWorldObj().getBlock(tile.xCoord, tile.yCoord, tile.zCoord).getPickBlock(movingObjPos, Minecraft.getMinecraft().theWorld, movingObjPos.blockX, movingObjPos.blockY, movingObjPos.blockZ, Minecraft.getMinecraft().thePlayer);
				if(stack == null)
					RenderHelper.renderTag(Minecraft.getMinecraft().thePlayer.getDistanceSq(movingObjPos.blockX, movingObjPos.blockY, movingObjPos.blockZ), "THIS IS AN ERROR, CONTACT THE DEV!!!", x,y,z, 10);
				else
					RenderHelper.renderTag(Minecraft.getMinecraft().thePlayer.getDistanceSq(movingObjPos.blockX, movingObjPos.blockY, movingObjPos.blockZ), stack.getDisplayName(), x,y,z, 10);
			}
		}*/
	}
}
