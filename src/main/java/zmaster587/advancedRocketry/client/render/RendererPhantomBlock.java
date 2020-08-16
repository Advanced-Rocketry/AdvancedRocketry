package zmaster587.advancedRocketry.client.render;

import net.minecraft.block.state.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockRendererDispatcher;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.RayTraceResult;
import org.lwjgl.opengl.GL11;
import zmaster587.libVulpes.render.RenderHelper;
import zmaster587.libVulpes.tile.multiblock.TilePlaceholder;

public class RendererPhantomBlock extends TileEntitySpecialRenderer {

	private static BlockRendererDispatcher renderBlocks = Minecraft.getInstance().getBlockRendererDispatcher();

	@Override
	public void render(TileEntity tile, double x,
			double y, double z, float t, int damage, float a) {

		renderBlocks = Minecraft.getInstance().getBlockRendererDispatcher();
		TilePlaceholder tileGhost = (TilePlaceholder)tile;

		BlockState state = tileGhost.getReplacedState();

		//TODO: bring TESRS back
		/*if(tileGhost.getReplacedTileEntity() != null && !(tileGhost.getReplacedTileEntity() instanceof TileMultiBlock) && TileEntityRendererDispatcher.instance.hasSpecialRenderer(tileGhost.getReplacedTileEntity())) {
			GL11.glEnable(GL11.GL_BLEND);
			GL11.glBlendFunc(GL11.GL_ONE_MINUS_SRC_COLOR, GL11.GL_SRC_ALPHA);
			GL11.glColor4f(1f, 1f, 1f,0.7f);
			TileEntityRendererDispatcher.instance.renderTileEntityAt(tileGhost.getReplacedTileEntity(), x, y, z, t);
			GL11.glDisable(GL11.GL_BLEND);
		}*/

		matrix.push();

		matrix.translate(x - tile.getPos().getX(),y - tile.getPos().getY(),z - tile.getPos().getZ());

		net.minecraft.client.renderer.RenderHelper.disableStandardItemLighting();
		//Render Each block

		this.bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
		GlStateManager.enableBlend();
		GlStateManager.blendFunc(GL11.GL_ONE_MINUS_SRC_COLOR, GL11.GL_SRC_ALPHA);
		Tessellator tess = Tessellator.getInstance();
		BufferBuilder buffer =tess.getBuffer();

		buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.BLOCK);
		IBakedModel model = renderBlocks.getModelForState(state);
		renderBlocks.getBlockModelRenderer().renderModel( tile.getWorld(), model, state, tile.getPos(), buffer, false);
		tess.draw();

		net.minecraft.client.renderer.RenderHelper.enableStandardItemLighting();
		GlStateManager.disableBlend();
		matrix.pop();


		//TODO: bring tags back
		if(state != null) {
			//If the player is mousing over this block
			RayTraceResult movingObjPos = Minecraft.getInstance().objectMouseOver;
			try {
				if(Minecraft.getInstance().objectMouseOver != null && movingObjPos.getBlockPos().getX() == tile.getPos().getX() && movingObjPos.getBlockPos().getY() == tile.getPos().getY() && movingObjPos.getBlockPos().getZ() == tile.getPos().getZ()) {

					ItemStack stack = tile.getWorld().getBlockState(tile.getPos()).getBlock().getPickBlock(tile.getWorld().getBlockState(tile.getPos()), movingObjPos, Minecraft.getInstance().world, tile.getPos(), Minecraft.getInstance().player);
					if(stack == null)
						RenderHelper.renderTag(Minecraft.getInstance().player.getDistanceSq(movingObjPos.hitVec.x, movingObjPos.hitVec.y, movingObjPos.hitVec.z), "THIS IS AN ERROR, CONTACT THE DEV!!!", x,y,z, 10);
					else
						RenderHelper.renderTag(Minecraft.getInstance().player.getDistanceSq(movingObjPos.hitVec.x, movingObjPos.hitVec.y, movingObjPos.hitVec.z), stack.getDisplayName(), x+ 0.5f,y,z+ 0.5f, 10);
				}
			} catch (NullPointerException e) {
				//silence you fool
			}
		}
	}
}
