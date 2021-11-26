package zmaster587.advancedRocketry.client.render.entity;

import net.minecraft.block.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Quaternion;
import net.minecraftforge.fml.client.registry.IRenderFactory;

import com.mojang.blaze3d.matrix.MatrixStack;

import zmaster587.advancedRocketry.entity.EntityRocket;
import zmaster587.advancedRocketry.util.StorageChunk;

import javax.annotation.ParametersAreNonnullByDefault;

public class RendererRocket extends EntityRenderer<EntityRocket> implements IRenderFactory<EntityRocket> {

	private static BlockRendererDispatcher renderBlocks = Minecraft.getInstance().getBlockRendererDispatcher();

	Class tileEntityBlockChiseled;

	@ParametersAreNonnullByDefault
	public RendererRocket(EntityRendererManager manager) {
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
	@ParametersAreNonnullByDefault
	public void render(EntityRocket entity, float entityYaw, float partialTicks, MatrixStack matrix, IRenderTypeBuffer bufferIn, int packedLightIn) {
		StorageChunk storage  = entity.storage;

		if(storage == null)
			return;
		
		//Find the halfway point along the XZ plane
		float halfx = storage.getSizeX()/2f;
		float halfy = storage.getSizeY()/2f;
		float halfz = storage.getSizeZ()/2f;

        //NONAPPLICABLE FIX FROM 1.12 THESE THREE LINES
		/*if(Minecraft.getMinecraft().player != null && entity.getPassengers().contains(Minecraft.getMinecraft().player)) {
			y = -((EntityRocket)entity).stats.getSeatY();
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
		matrix.translate((float)0, halfy, (float)0);
		matrix.rotate(new Quaternion(entity.getRCSRotateProgress()*0.9f,0,0, true));
		matrix.rotate(new Quaternion(0,0, entity.rotationYaw, true));
		matrix.translate(- halfx, (float)0 - halfy, - halfz);
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

		//Render tile entities if applicable
		for(TileEntity tile : storage.getTileEntityList()) {
			
				matrix.push();
				matrix.translate(tile.getPos().getX(), tile.getPos().getY(), tile.getPos().getZ());

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
				TileEntityRenderer renderer = (TileEntityRenderer)TileEntityRendererDispatcher.instance.renderers.get(tile.getClass());
				if(renderer != null ) {

					if(tileEntityBlockChiseled.isInstance(tile) && getState != null)
					{
						matrix.push();
						try {
							getState.invoke(tile, storage.world);
							//Chisel transforms by -TileEntityRendererDispatcher.staticPlayer, we already transformed, so we must negate it
							matrix.translate(TileEntityRendererDispatcher.staticPlayerX, TileEntityRendererDispatcher.staticPlayerY, TileEntityRendererDispatcher.staticPlayerZ);
							TileEntityRendererDispatcher.instance.render(tile, tile.getPos().getX(), tile.getPos().getY(), tile.getPos().getZ(), f1);
							matrix.pop();
						} catch (IllegalAccessException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} catch (IllegalArgumentException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} catch (InvocationTargetException e) {
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
	public EntityRenderer<? super EntityRocket> createRenderFor(EntityRendererManager manager) {
		return new RendererRocket(manager);
	}

	@Override
	@ParametersAreNonnullByDefault
	public ResourceLocation getEntityTexture(EntityRocket entity) {
		return null;
	}

}
