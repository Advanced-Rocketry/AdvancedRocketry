package zmaster587.advancedRocketry.client.render;

import org.lwjgl.opengl.GL11;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.world.IBlockAccess;
import cpw.mods.fml.client.registry.ISimpleBlockRenderingHandler;

public class RenderColoredBlock implements ISimpleBlockRenderingHandler {

	@Override
	public void renderInventoryBlock(Block block, int metadata, int modelId,
			RenderBlocks renderer) {
		
		int color = block.getBlockColor();
		GL11.glColor3b((byte)(color & 0xFF), (byte)((color << 8) & 0xFF), (byte)((color << 16) & 0xFF));
		renderer.renderBlockAsItem(block, metadata, 1f);
	}

	@Override
	public boolean renderWorldBlock(IBlockAccess world, int x, int y, int z,
			Block block, int modelId, RenderBlocks renderer) {
		
		int color = block.getBlockColor();
		GL11.glColor3b((byte)(color & 0xFF), (byte)((color << 8) & 0xFF), (byte)((color << 16) & 0xFF));
		renderer.renderStandardBlock(block, x, y, z);
		//GL11.glColor3b(red, green, blue);
		
		return false;
	}

	@Override
	public boolean shouldRender3DInInventory(int modelId) {
		return true;
	}

	@Override
	public int getRenderId() {
		return 0;
	}

}
