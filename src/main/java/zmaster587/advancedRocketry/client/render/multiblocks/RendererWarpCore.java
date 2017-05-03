package zmaster587.advancedRocketry.client.render.multiblocks;

import org.lwjgl.opengl.GL11;

import zmaster587.advancedRocketry.api.Configuration;
import zmaster587.advancedRocketry.api.stations.ISpaceObject;
import zmaster587.advancedRocketry.dimension.DimensionProperties;
import zmaster587.advancedRocketry.stations.SpaceObject;
import zmaster587.advancedRocketry.stations.SpaceObjectManager;
import zmaster587.advancedRocketry.world.provider.WorldProviderSpace;
import zmaster587.libVulpes.block.RotatableBlock;
import zmaster587.libVulpes.render.RenderHelper;
import zmaster587.libVulpes.tile.multiblock.TileMultiBlock;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.AdvancedModelLoader;
import net.minecraftforge.client.model.IModelCustom;
import net.minecraftforge.common.util.ForgeDirection;

public class RendererWarpCore extends TileEntitySpecialRenderer {

	IModelCustom model = AdvancedModelLoader.loadModel(new ResourceLocation("advancedrocketry:models/warpcore.obj"));

	ResourceLocation texture = new ResourceLocation("advancedrocketry:textures/models/warpcore.png");

	private final RenderItem dummyItem = new RenderItem();

	public RendererWarpCore() {
		dummyItem.setRenderManager(RenderManager.instance);
	}

	@Override
	public void renderTileEntityAt(TileEntity tile, double x,
			double y, double z, float f) {
		TileMultiBlock multiBlockTile = (TileMultiBlock)tile;

		if(!multiBlockTile.canRender())
			return;

		GL11.glPushMatrix();

		//Initial setup
		int bright = tile.getWorldObj().getLightBrightnessForSkyBlocks(tile.xCoord, tile.yCoord + 1, tile.zCoord,0);
		int brightX = bright % 65536;
		int brightY = bright / 65536;
		OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, brightX, brightY);

		//Rotate and move the model into position
		GL11.glTranslated(x + 0.5, y, z + 0.5);
		ForgeDirection front = RotatableBlock.getFront(tile.getBlockMetadata());
		GL11.glRotatef((front.offsetX == 1 ? 180 : 0) + front.offsetZ*90f, 0, 1, 0);
		GL11.glTranslated(1f, 0, 0f);

		bindTexture(texture);
		model.renderOnly("Base");

		GL11.glDisable(GL11.GL_TEXTURE_2D);
		
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glBlendFunc(GL11.GL_ONE, GL11.GL_ONE_MINUS_SRC_ALPHA);
		
		GL11.glDisable(GL11.GL_LIGHTING);
		GL11.glColor4f(1f, 0.4f, 0.4f, 0.8f);
		GL11.glPushMatrix();
		Tessellator.instance.startDrawingQuads();
		RenderHelper.renderCubeWithUV(Tessellator.instance, -0.1f, 1, -0.1f, 0.1f, 2, 0.1f, 0, 1, 0, 1);
		Tessellator.instance.draw();
		GL11.glPopMatrix();
		GL11.glEnable(GL11.GL_LIGHTING);

		
		if(tile.getWorldObj().provider instanceof WorldProviderSpace) {
			
			ISpaceObject obj = SpaceObjectManager.getSpaceManager().getSpaceStationFromBlockCoords(tile.xCoord, tile.zCoord);
			if(obj instanceof SpaceObject && ((SpaceObject)obj).getFuelAmount() > 50) {

				double speedMult = ((DimensionProperties)obj.getProperties()).getParentPlanet() == SpaceObjectManager.WARPDIMID ? 1.5d : 0.1d;
				
				double speedRotate = speedMult*0.25d;
				
				
				GL11.glColor4f(0.4f, 0.4f, 1f, 0.6f);
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
				
				GL11.glColor4f(0.4f, 1f, 0.4f, 0.8f);
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
		GL11.glColor4f(1f, 1f, 1f, 1f);
		GL11.glDisable(GL11.GL_BLEND);
		GL11.glEnable(GL11.GL_TEXTURE_2D);
		GL11.glPopMatrix();
	}
}
