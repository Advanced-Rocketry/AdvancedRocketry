package zmaster587.advancedRocketry.client.render.multiblocks;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderItem;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;
import zmaster587.advancedRocketry.api.stations.ISpaceObject;
import zmaster587.advancedRocketry.backwardCompat.ModelFormatException;
import zmaster587.advancedRocketry.backwardCompat.WavefrontObject;
import zmaster587.advancedRocketry.stations.SpaceObject;
import zmaster587.advancedRocketry.stations.SpaceObjectManager;
import zmaster587.advancedRocketry.world.provider.WorldProviderSpace;
import zmaster587.libVulpes.block.RotatableBlock;
import zmaster587.libVulpes.render.RenderHelper;
import zmaster587.libVulpes.tile.multiblock.TileMultiBlock;

public class RendererWarpCore extends TileEntitySpecialRenderer {

	public static WavefrontObject model;

	ResourceLocation texture = new ResourceLocation("advancedrocketry:textures/models/warpcore.png");

	private final RenderItem dummyItem = Minecraft.getMinecraft().getRenderItem();

	public RendererWarpCore() {
		try {
			model = new WavefrontObject(new ResourceLocation("advancedrocketry:models/warpcore.obj"));
		} catch (ModelFormatException e) {
			e.printStackTrace();
		}
	
	}

	@Override
	public void render(TileEntity tile, double x,
			double y, double z, float f, int damage, float a) {
		TileMultiBlock multiBlockTile = (TileMultiBlock)tile;

		if(!multiBlockTile.canRender())
			return;

		GL11.glPushMatrix();

		//Rotate and move the model into position
		GL11.glTranslated(x + 0.5, y, z + 0.5);
		EnumFacing front = RotatableBlock.getFront(tile.getWorld().getBlockState(tile.getPos())); //tile.getWorldObj().getBlockMetadata(tile.xCoord, tile.yCoord, tile.zCoord));
		GL11.glRotatef((front.getFrontOffsetX() == 1 ? 180 : 0) + front.getFrontOffsetZ()*90f, 0, 1, 0);
		GL11.glTranslated(1f, 0, 0f);

		bindTexture(texture);
		model.renderOnly("Base");

		GL11.glDisable(GL11.GL_TEXTURE_2D);
		
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glBlendFunc(GL11.GL_ONE, GL11.GL_ONE_MINUS_SRC_ALPHA);
		
		GL11.glDisable(GL11.GL_LIGHTING);
		GlStateManager.color(1f, 0.4f, 0.4f, 0.8f);
		GL11.glPushMatrix();
		
		BufferBuilder buffer = Tessellator.getInstance().getBuffer();
		
		buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
		RenderHelper.renderCubeWithUV(buffer, -0.1f, 1, -0.1f, 0.1f, 2, 0.1f, 0, 1, 0, 1);
		Tessellator.getInstance().draw();
		GL11.glPopMatrix();
		GL11.glEnable(GL11.GL_LIGHTING);
		GlStateManager.color(1f, 1f,1f, 1f);
		
		if(tile.getWorld().provider instanceof WorldProviderSpace) {
			
			ISpaceObject obj = SpaceObjectManager.getSpaceManager().getSpaceStationFromBlockCoords(tile.getPos());
			if(obj instanceof SpaceObject && ((SpaceObject)obj).getFuelAmount() > 50) {

				double speedMult = 1.5;//((DimensionProperties)obj.getProperties()).getParentPlanet() == SpaceObjectManager.WARPDIMID ? 1.5d : 0.1d;
				
				double speedRotate = speedMult*0.25d;
				
				
				GlStateManager.color(0.4f, 0.4f, 1f, 0.6f);
				GL11.glPushMatrix();
				GL11.glRotated(speedRotate*System.currentTimeMillis() % 360, 0f, 1f, 0f);
				model.renderOnly("Rotate1");
				GL11.glPopMatrix();

				GL11.glPushMatrix();
				GL11.glRotated(180 + speedRotate*System.currentTimeMillis() % 360, 0f, 1f, 0f);
				model.renderOnly("Rotate1");
				GL11.glPopMatrix();

				GL11.glPushMatrix();
				GL11.glRotated(-speedRotate*System.currentTimeMillis() % 360, 0f, 1f, 0f);
				model.renderOnly("Rotate2");
				GL11.glPopMatrix();

				GL11.glPushMatrix();
				GL11.glRotated(180 -speedRotate*System.currentTimeMillis() % 360, 0f, 1f, 0f);
				model.renderOnly("Rotate2");
				GL11.glPopMatrix();

				speedRotate = 0.03d*speedMult;
				
				GlStateManager.color(0.4f, 1f, 0.4f, 0.8f);
				int amt = 3;
				float offset = 360/(float)amt;
				for(int j = 0; j < 5; j++) {
					for(int i = 0; i < amt; i++) {
						GL11.glPushMatrix();
						GL11.glRotated(((j+1)*speedRotate*System.currentTimeMillis() % 360) + (i + j/5f)*offset, 0f, 1f, 0f);
						GL11.glTranslatef(0, 0.1f*j-.2f + (5-j)*0.02f*(float)Math.sin(0.001d*System.currentTimeMillis()), 0.2f);
						//GL11.glTranslatef(0f, 0.1f*(0.5f - MathHelper.sin((float)(0.001*System.currentTimeMillis() % 100))), 0f);
						model.renderOnly("Ball");
						GL11.glPopMatrix();
					}
				}
			}
		}
		
		GlStateManager.color(1f, 1f, 1f, 1f);
		GL11.glDisable(GL11.GL_BLEND);
		GL11.glEnable(GL11.GL_TEXTURE_2D);
		GL11.glPopMatrix();
	}
}
