package zmaster587.advancedRocketry.client.render.multiblocks;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;

import org.lwjgl.opengl.GL11;
import zmaster587.advancedRocketry.backwardCompat.ModelFormatException;
import zmaster587.advancedRocketry.backwardCompat.WavefrontObject;
import zmaster587.advancedRocketry.tile.multiblock.energy.TileBlackHoleGenerator;
import zmaster587.advancedRocketry.tile.multiblock.machine.TileCentrifuge;
import zmaster587.libVulpes.block.RotatableBlock;
import zmaster587.libVulpes.render.RenderHelper;
import zmaster587.libVulpes.tile.multiblock.TileMultiPowerConsumer;
import zmaster587.libVulpes.tile.multiblock.TileMultiPowerProducer;

public class RenderCentrifuge extends TileEntitySpecialRenderer {

	WavefrontObject model;

	ResourceLocation texture = new ResourceLocation("advancedrocketry:textures/models/centrifuge.png");

	public RenderCentrifuge(){
		try {
			model = new WavefrontObject(new ResourceLocation("advancedrocketry:models/centrifuge.obj"));
		} catch (ModelFormatException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void render(TileEntity tile, double x, double y, double z,
			float partialTicks, int destroyStage, float a) {
		TileCentrifuge multiBlockTile = (TileCentrifuge)tile;

		if(!multiBlockTile.canRender())
			return;

		matrix.push();

		//Initial setup

		//Rotate and move the model into position
		Direction front = RotatableBlock.getFront(tile.getWorld().getBlockState(tile.getPos()));
		matrix.translate(x + 0.5, y, z + 0.5);
		GL11.glRotatef((front.getZOffset() == 1 ? 180 : 0) - front.getXOffset()*90f, 0, 1, 0);
		matrix.translate(0, 0, 0 + 1);

		bindTexture(texture);

		model.renderOnly("Frame");


		if(multiBlockTile.isRunning())
		{
			float lavaheight = multiBlockTile.getNormallizedProgress(0);
			GL11.glRotated(multiBlockTile.getWorld().getGameTime() * -10f, 0, 1, 0);
			model.renderOnly("Spinning");


			ResourceLocation fluidIcon = new ResourceLocation("advancedrocketry:textures/blocks/fluid/oxygen_flow.png");
			Fluid fluid = FluidRegistry.getFluid("enrichedlava");
			if(fluid != null)
			{
				matrix.push();

				double minU = 0, maxU = 1, minV = 0, maxV = 1;
				TextureMap map = Minecraft.getInstance().getTextureMapBlocks();
				TextureAtlasSprite sprite = map.getTextureExtry(fluid.getStill().toString());
				if(sprite != null) {
					minU = sprite.getMinU();
					maxU = sprite.getMaxU();
					minV = sprite.getMinV();
					maxV = sprite.getMaxV();
					GlStateManager.bindTexture(map.getGlTextureId());
				}
				else {
					int color = fluid.getColor();
					GlStateManager.color4f(((color >>> 16) & 0xFF)/255f, ((color >>> 8) & 0xFF)/255f, ((color& 0xFF)/255f),1f);

					bindTexture(fluidIcon);
				}
				Tessellator tess = Tessellator.getInstance();

				float amt = 1.0f;

				GlStateManager.disableLighting();
				GlStateManager.enableBlend();

				GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

				AxisAlignedBB bb = new AxisAlignedBB(-1.2, -0.5, -0.5, 1.2, 0.3 - 0.6*lavaheight, 0.5);

				for(int i = 0; i < 4; i++)
				{
					tess.getBuffer().begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
					RenderHelper.renderCubeWithUV(tess.getBuffer(), bb.minX + 0.01, bb.minY + 0.01, bb.minZ + 0.01, bb.maxX - 0.01, bb.maxY*amt - 0.01, bb.maxZ - 0.01, minU, maxU, minV, maxV);
					tess.draw();
					GL11.glRotatef(45f, 0, 1, 0);
				}

				GlStateManager.enableLighting();
				GlStateManager.disableBlend();
				matrix.pop();
				GlStateManager.color4f(1f, 1f, 1f);
			}
		}
		else
		{
			model.renderOnly("Spinning");
		}
		matrix.pop();
	}
}
