package zmaster587.advancedRocketry.client.render;

import net.minecraft.block.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockRendererDispatcher;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.RayTraceResult.Type;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;

import zmaster587.libVulpes.render.RenderHelper;
import zmaster587.libVulpes.tile.TileSchematic;
import zmaster587.libVulpes.tile.multiblock.TilePlaceholder;

public class RendererPhantomBlock extends TileEntityRenderer<TileSchematic> {

	public RendererPhantomBlock(TileEntityRendererDispatcher rendererDispatcherIn) {
		super(rendererDispatcherIn);
	}

	private static BlockRendererDispatcher renderBlocks = Minecraft.getInstance().getBlockRendererDispatcher();

	@Override
	public void render(TileSchematic tile, float partialTicks, MatrixStack matrix,
			IRenderTypeBuffer buffer, int combinedLightIn, int combinedOverlayIn) {

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
		//Render Each block


		IVertexBuilder blockTranslucent = buffer.getBuffer(RenderHelper.getTranslucentBlock());

		IBakedModel model = renderBlocks.getModelForState(state);
		renderBlocks.getBlockModelRenderer().renderModel(tile.getWorld(), model, state, tile.getPos(), matrix, blockTranslucent, false, tile.getWorld().getRandom(), 0, combinedOverlayIn,  net.minecraftforge.client.model.data.EmptyModelData.INSTANCE);

		matrix.pop();


		//TODO: bring tags back
		if(state != null) {
			//If the player is mousing over this block
			RayTraceResult movingObjPos = Minecraft.getInstance().objectMouseOver;
			try {
				if(movingObjPos.getType() == Type.BLOCK)
				{
					BlockRayTraceResult result = (BlockRayTraceResult)movingObjPos;

					matrix.push();
					matrix.translate(0.5, 0.5, 0.5);
					if(Minecraft.getInstance().objectMouseOver != null && result.getPos().getX() == tile.getPos().getX() && result.getPos().getY() == tile.getPos().getY() && result.getPos().getZ() == tile.getPos().getZ()) {

						ItemStack stack = tile.getWorld().getBlockState(tile.getPos()).getBlock().getPickBlock(tile.getWorld().getBlockState(tile.getPos()), movingObjPos, Minecraft.getInstance().world, tile.getPos(), Minecraft.getInstance().player);
						if(stack == null)
							RenderHelper.renderTag(matrix, buffer, Minecraft.getInstance().player.getDistanceSq(result.getHitVec().x, result.getHitVec().y, result.getHitVec().z), "THIS IS AN ERROR, CONTACT THE DEV!!!", 0,1);
						else
							RenderHelper.renderTag(matrix, buffer, Minecraft.getInstance().player.getDistanceSq(result.getHitVec().x, result.getHitVec().y, result.getHitVec().z), stack.getDisplayName().getString(), 0xE00000, 1);
					}
					matrix.pop();
				}
			} catch (NullPointerException e) {
				//silence you fool
			}
		}
	}
}
