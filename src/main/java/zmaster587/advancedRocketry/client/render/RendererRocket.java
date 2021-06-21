package zmaster587.advancedRocketry.client.render;

import net.minecraft.block.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.*;
<<<<<<< HEAD
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.texture.OverlayTexture;
=======
import net.minecraft.client.renderer.chunk.IRenderChunkFactory;
import net.minecraft.client.renderer.chunk.ListChunkFactory;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.texture.TextureMap;
>>>>>>> origin/feature/nuclearthermalrockets
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
<<<<<<< HEAD
import net.minecraft.util.math.vector.Quaternion;
=======
import net.minecraft.world.IBlockAccess;
>>>>>>> origin/feature/nuclearthermalrockets
import net.minecraftforge.fml.client.registry.IRenderFactory;

import java.lang.reflect.Method;

import org.lwjgl.opengl.GL11;

import com.mojang.blaze3d.matrix.MatrixStack;

import zmaster587.advancedRocketry.entity.EntityRocket;
import zmaster587.advancedRocketry.util.StorageChunk;

<<<<<<< HEAD
public class RendererRocket extends EntityRenderer<EntityRocket> implements IRenderFactory<EntityRocket> {
=======
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class RendererRocket extends Render implements IRenderFactory<EntityRocket> {
>>>>>>> origin/feature/nuclearthermalrockets

	private static BlockRendererDispatcher renderBlocks = Minecraft.getInstance().getBlockRendererDispatcher();

	Class tileEntityBlockChiseled;
	Method getState;

<<<<<<< HEAD
	public RendererRocket(EntityRendererManager manager) {
=======
	public RendererRocket(@Nonnull RenderManager manager) {
>>>>>>> origin/feature/nuclearthermalrockets
		super(manager);

		/*try {
			tileEntityBlockChiseled = Class.forName("mod.chiselsandbits.chiseledblock.TileEntityBlockChiseled");
			getState = tileEntityBlockChiseled.getMethod("getRenderState", IBlockAccess.class);
			AdvancedRocketry.logger.info("Chisel and bits support HAS BEEN loaded");
		}
		catch(ClassNotFoundException | NoSuchMethodException e) {
			AdvancedRocketry.logger.info("Chisel and bits support NOT loaded");
		}*/
	}

	//TODO: possibly optimize with GL lists
	@Override
<<<<<<< HEAD
	public void render(EntityRocket entity, float entityYaw, float partialTicks, MatrixStack matrix,
			IRenderTypeBuffer bufferIn, int packedLightIn) {
=======
	public void doRender(@Nonnull Entity entity, double x,
			double y, double z, float f1,
			float f2) {
>>>>>>> origin/feature/nuclearthermalrockets

		StorageChunk storage  = entity.storage;

		if(storage == null)
			return;
		
		//Find the halfway point along the XZ plane
		float halfx = storage.getSizeX()/2f;
		float halfy = storage.getSizeY()/2f;
		float halfz = storage.getSizeZ()/2f;

		/*if(entity.getPassengers().contains(Minecraft.getInstance().player)) {
			float angle = (float)(((EntityRocket)entity).getRCSRotateProgress()*0.9f*Math.PI/180f);
			y = ((EntityRocket)entity).stats.getSeatY();
			y= (0.5-((EntityRocket)entity).stats.getSeatY())*MathHelper.cos(angle) + (0)*MathHelper.sin(angle);
			//y = +0.5 -((EntityRocket)entity).stats.getSeatY();
		}*/

		/*matrix.push();

		GlStateManager.enableBlend();
		GlStateManager.blendFunc(GL11.GL_ONE, GL11.GL_ONE);
		GlStateManager.color4f(0.5f, 1f, .5f, .2f);

		GlStateManager.disableTexture();
		GL11.glEnable(GL11.GL_LINE_STIPPLE);
		GL11.glLineWidth(1f);
		GL11.glLineStipple(5, (short)0x2222);

		if(!((EntityRocket)entity).isInFlight()) {
			for(IInfrastructure inf : ((EntityRocket)entity).getConnectedInfrastructure()) {
				if(inf.canRenderConnection()) {
					TileEntity tile = (TileEntity)inf;

					buffer.begin(GL11.GL_LINE_LOOP, DefaultVertexFormats.POSITION);

					buffer.pos(0, storage.getSizeY()/2f, 0).endVertex();
					buffer.pos((tile.getPos().getX() - entity.getPosX() + 0.5f)/2f, storage.getSizeY()/2f, (tile.getPos().getZ() - entity.getPosZ() + 0.5f)/2f).endVertex();
					buffer.pos(tile.getPos().getX() - entity.getPosX() + 0.5f, tile.getPos().getY() - entity.posY  + 0.5f, tile.getPos().getZ() - entity.getPosZ() + 0.5f).endVertex();
					buffer.pos((tile.getPos().getX() - entity.getPosX() + 0.5f)/2f, storage.getSizeY()/2f, (tile.getPos().getZ() - entity.getPosZ() + 0.5f)/2f).endVertex();

					//RenderHelper.renderCrossXZ(Tessellator.instance, .2f, 0, storage.getSizeY()/2f, 0, tile.xCoord - entity.getPosX() + 0.5f, tile.yCoord - entity.posY  + 0.5f, tile.zCoord - entity.getPosZ() + 0.5f);
					//RenderHelper.renderBlockWithEndPointers(Tessellator.instance, .2f, 0, storage.getSizeY()/2f, 0, tile.xCoord - entity.getPosX(), tile.yCoord - entity.posY, tile.zCoord - entity.getPosZ());
					Tessellator.getInstance().draw();
					//RenderHelper.renderCubeWithUV(matrix, tess, 0, 0, 0, 2, 55, 2, 0, 1, 0, 1);
				}
			}
		}

		GlStateManager.color4f(1f, 1f, 1f);
		GlStateManager.disableBlend();
		GL11.glDisable(GL11.GL_LINE_STIPPLE);
		GlStateManager.enableTexture();

		matrix.pop();*/

		//Initial setup

		matrix.push();
		matrix.translate((float)0, (float) halfy, (float)0);
		matrix.rotate(new Quaternion(((EntityRocket)entity).getRCSRotateProgress()*0.9f,0,0, true));
		matrix.rotate(new Quaternion(0,0, ((EntityRocket)entity).rotationYaw, true));
		matrix.translate((float)- halfx, (float)0 - halfy, (float)- halfz);
		//GL11.glNewList(storage.world.displayListIndex, GL11.GL_COMPILE);
		//net.minecraft.client.renderer.RenderHelper.disableStandardItemLighting();

		//Render Each block
		for(int xx = 0; xx < storage.getSizeX(); xx++) {
			for(int zz = 0; zz < storage.getSizeZ(); zz++) {
				for(int yy = 0; yy < storage.getSizeY(); yy++) {
					BlockState block  = storage.getBlockState(new BlockPos(xx, yy, zz));
					matrix.push();
					matrix.translate(xx, yy, zz);
					try {
						Minecraft.getInstance().getBlockRendererDispatcher().renderBlock(block, matrix, bufferIn, packedLightIn, OverlayTexture.NO_OVERLAY);
					} 
					catch (NullPointerException e) {
						//System.out.println(block. + " cannot be rendered on rocket at " + entity.getPosition());
					}
					matrix.pop();
				}
			}
		}

<<<<<<< HEAD
		//Render tile entities if applicable
		for(TileEntity tile : storage.getTileEntityList()) {
			
				matrix.push();
				matrix.translate(tile.getPos().getX(), tile.getPos().getY(), tile.getPos().getZ());
=======
		GL11.glPushMatrix();
		GL11.glTranslatef((float)x, (float)y + halfy, (float)z);
		GL11.glRotatef(((EntityRocket)entity).getRCSRotateProgress()*0.9f, 1f, 0f, 0f);
		GL11.glRotatef(entity.rotationYaw, 0f, 0f, 1f);
		GL11.glTranslatef(- halfx, (float)0 - halfy, - halfz);
		Minecraft.getMinecraft().getTextureManager().bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
		GL11.glCallList(storage.world.displayListIndex);



		//Render tile entities if applicable
		for(TileEntity tile : storage.getTileEntityList()) {
			TileEntitySpecialRenderer renderer = TileEntityRendererDispatcher.instance.renderers.get(tile.getClass());
			if(renderer != null ) {
>>>>>>> origin/feature/nuclearthermalrockets

				if(tileEntityBlockChiseled == null || !tileEntityBlockChiseled.isInstance(tile))
				{
					TileEntityRendererDispatcher.instance.renderTileEntity(tile, partialTicks, matrix, bufferIn);
					//renderer.render(tile, partialTicks, matrix, bufferIn, packedLightIn / 0xffff, packedLightIn & 0xffff);
				}
				//renderer.renderTileEntity(tile, tile.getPos().getX(), tile.getPos().getY(), tile.getPos().getZ(), f1, 0);
				matrix.pop();
		}

		//Chisel compat
		/*if(getState != null)
		{
			TileEntityRendererDispatcher.instance.preDrawBatch();
			for(TileEntity tile : storage.getTileEntityList()) {
<<<<<<< HEAD
				TileEntityRenderer renderer = (TileEntityRenderer)TileEntityRendererDispatcher.instance.renderers.get(tile.getClass());
=======
				TileEntitySpecialRenderer renderer = TileEntityRendererDispatcher.instance.renderers.get(tile.getClass());
>>>>>>> origin/feature/nuclearthermalrockets
				if(renderer != null ) {

					if(tileEntityBlockChiseled.isInstance(tile) && getState != null)
					{
						matrix.push();
						try {
							getState.invoke(tile, storage.world);
							//Chisel transforms by -TileEntityRendererDispatcher.staticPlayer, we already transformed, so we must negate it
							matrix.translate(TileEntityRendererDispatcher.staticPlayerX, TileEntityRendererDispatcher.staticPlayerY, TileEntityRendererDispatcher.staticPlayerZ);
							TileEntityRendererDispatcher.instance.render(tile, tile.getPos().getX(), tile.getPos().getY(), tile.getPos().getZ(), f1);
<<<<<<< HEAD
							matrix.pop();
						} catch (IllegalAccessException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} catch (IllegalArgumentException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} catch (InvocationTargetException e) {
=======
							GL11.glPopMatrix();
						} catch (IllegalAccessException | InvocationTargetException | IllegalArgumentException e) {
>>>>>>> origin/feature/nuclearthermalrockets
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}

					//renderer.renderTileEntity(tile, tile.getPos().getX(), tile.getPos().getY(), tile.getPos().getZ(), f1, 0);
				}
			}
			TileEntityRendererDispatcher.instance.drawBatch(0);
		}*/
		
		//Clean up
		matrix.pop();
	}

	@Override
<<<<<<< HEAD
	public EntityRenderer<? super EntityRocket> createRenderFor(EntityRendererManager manager) {
		return new RendererRocket(manager);
=======
	protected ResourceLocation getEntityTexture(@Nullable Entity p_110775_1_) {
		return null;
>>>>>>> origin/feature/nuclearthermalrockets
	}

	@Override
	public ResourceLocation getEntityTexture(EntityRocket entity) {
		return null;
	}

}
