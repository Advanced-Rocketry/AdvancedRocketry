package zmaster587.advancedRocketry.client.render;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.chunk.IRenderChunkFactory;
import net.minecraft.client.renderer.chunk.ListChunkFactory;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.Entity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.fml.client.registry.IRenderFactory;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.lwjgl.opengl.GL11;

import zmaster587.advancedRocketry.AdvancedRocketry;
import zmaster587.advancedRocketry.api.IInfrastructure;
import zmaster587.advancedRocketry.entity.EntityRocket;
import zmaster587.advancedRocketry.util.StorageChunk;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class RendererRocket extends Render implements IRenderFactory<EntityRocket> {

	private static BlockRendererDispatcher renderBlocks = Minecraft.getMinecraft().getBlockRendererDispatcher();
	private IRenderChunkFactory factory = new ListChunkFactory();

	Class tileEntityBlockChiseled;
	Method getState;

	public RendererRocket(@Nonnull RenderManager manager) {
		super(manager);

		try {
			tileEntityBlockChiseled = Class.forName("mod.chiselsandbits.chiseledblock.TileEntityBlockChiseled");
			getState = tileEntityBlockChiseled.getMethod("getRenderState", IBlockAccess.class);
			AdvancedRocketry.logger.info("Chisel and bits support HAS BEEN loaded");
		}
		catch(ClassNotFoundException | NoSuchMethodException e) {
			AdvancedRocketry.logger.info("Chisel and bits support NOT loaded");
		}
	}


	//TODO: possibly optimize with GL lists
	@Override
	public void doRender(@Nonnull Entity entity, double x,
			double y, double z, float f1,
			float f2) {

		StorageChunk storage  = ((EntityRocket)entity).storage;


		BufferBuilder buffer = Tessellator.getInstance().getBuffer();

		if(storage == null || !storage.finalized)
			return;
		
		//Find the halfway point along the XZ plane
		float halfx = storage.getSizeX()/2f;
		float halfy = storage.getSizeY()/2f;
		float halfz = storage.getSizeZ()/2f;

		/*if(entity.getPassengers().contains(Minecraft.getMinecraft().player)) {
			float angle = (float)(((EntityRocket)entity).getRCSRotateProgress()*0.9f*Math.PI/180f);
			y = ((EntityRocket)entity).stats.getSeatY();
			y= (0.5-((EntityRocket)entity).stats.getSeatY())*MathHelper.cos(angle) + (0)*MathHelper.sin(angle);
			//y = +0.5 -((EntityRocket)entity).stats.getSeatY();
		}*/

		GL11.glPushMatrix();
		GL11.glTranslatef((float)x, (float)y, (float)z);

		GlStateManager.enableBlend();
		GlStateManager.blendFunc(GL11.GL_ONE, GL11.GL_ONE);
		GlStateManager.color(0.5f, 1f, .5f, .2f);

		GlStateManager.disableTexture2D();
		GL11.glEnable(GL11.GL_LINE_STIPPLE);
		GL11.glLineWidth(1f);
		GL11.glLineStipple(5, (short)0x2222);

		if(!((EntityRocket)entity).isInFlight()) {
			for(IInfrastructure inf : ((EntityRocket)entity).getConnectedInfrastructure()) {
				if(inf.canRenderConnection()) {
					TileEntity tile = (TileEntity)inf;

					buffer.begin(GL11.GL_LINE_LOOP, DefaultVertexFormats.POSITION);

					buffer.pos(0, storage.getSizeY()/2f, 0).endVertex();
					buffer.pos((tile.getPos().getX() - entity.posX + 0.5f)/2f, storage.getSizeY()/2f, (tile.getPos().getZ() - entity.posZ + 0.5f)/2f).endVertex();
					buffer.pos(tile.getPos().getX() - entity.posX + 0.5f, tile.getPos().getY() - entity.posY  + 0.5f, tile.getPos().getZ() - entity.posZ + 0.5f).endVertex();
					buffer.pos((tile.getPos().getX() - entity.posX + 0.5f)/2f, storage.getSizeY()/2f, (tile.getPos().getZ() - entity.posZ + 0.5f)/2f).endVertex();

					//RenderHelper.renderCrossXZ(Tessellator.instance, .2f, 0, storage.getSizeY()/2f, 0, tile.xCoord - entity.posX + 0.5f, tile.yCoord - entity.posY  + 0.5f, tile.zCoord - entity.posZ + 0.5f);
					//RenderHelper.renderBlockWithEndPointers(Tessellator.instance, .2f, 0, storage.getSizeY()/2f, 0, tile.xCoord - entity.posX, tile.yCoord - entity.posY, tile.zCoord - entity.posZ);
					Tessellator.getInstance().draw();
					//RenderHelper.renderCubeWithUV(tess, 0, 0, 0, 2, 55, 2, 0, 1, 0, 1);
				}
			}
		}

		GlStateManager.color(1f, 1f, 1f);
		GlStateManager.disableBlend();
		GL11.glDisable(GL11.GL_LINE_STIPPLE);
		GlStateManager.enableTexture2D();

		GL11.glPopMatrix();

		//Initial setup
		if(storage.world.displayListIndex == -1) {

			storage.world.displayListIndex = GLAllocation.generateDisplayLists(1);
			GL11.glPushMatrix();
			GL11.glNewList(storage.world.displayListIndex, GL11.GL_COMPILE);
			net.minecraft.client.renderer.RenderHelper.disableStandardItemLighting();

			//Render Each block
			net.minecraftforge.client.ForgeHooksClient.setRenderLayer(BlockRenderLayer.SOLID);
			Minecraft.getMinecraft().getTextureManager().bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
			for(int xx = 0; xx < storage.getSizeX(); xx++) {
				for(int zz = 0; zz < storage.getSizeZ(); zz++) {
					for(int yy = 0; yy < storage.getSizeY(); yy++) {
						IBlockState block  = storage.getBlockState(new BlockPos(xx, yy, zz));

						//I'm not dealing with untextured blocks from chisel and bits today
						//Just assume everything from C&B is a bit
						if(block.getBlock().getRegistryName().getResourceDomain().equals("chiselsandbits"))
							continue;

						buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.BLOCK);
						try {
							Minecraft.getMinecraft().getBlockRendererDispatcher().renderBlock(block, new BlockPos(xx, yy, zz), storage.world, buffer);
						} 
						catch (NullPointerException e) {
							System.out.println(block.getBlock().getUnlocalizedName() + " cannot be rendered on rocket at " + entity.getPosition());
						}
						Tessellator.getInstance().draw();
					}
				}
			}
			net.minecraftforge.client.ForgeHooksClient.setRenderLayer(null);

			net.minecraft.client.renderer.RenderHelper.enableStandardItemLighting();

			GL11.glEndList();

			GL11.glPopMatrix();
		}

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

				if(tileEntityBlockChiseled == null || !tileEntityBlockChiseled.isInstance(tile))
				{
					TileEntityRendererDispatcher.instance.render(tile, tile.getPos().getX(), tile.getPos().getY(), tile.getPos().getZ(), f1);
				}
				//renderer.renderTileEntity(tile, tile.getPos().getX(), tile.getPos().getY(), tile.getPos().getZ(), f1, 0);
			}
		}

		//Chisel compat
		if(getState != null)
		{
			TileEntityRendererDispatcher.instance.preDrawBatch();
			for(TileEntity tile : storage.getTileEntityList()) {
				TileEntitySpecialRenderer renderer = TileEntityRendererDispatcher.instance.renderers.get(tile.getClass());
				if(renderer != null ) {

					if(tileEntityBlockChiseled.isInstance(tile) && getState != null)
					{
						GL11.glPushMatrix();
						try {
							getState.invoke(tile, storage.world);
							//Chisel transforms by -TileEntityRendererDispatcher.staticPlayer, we already transformed, so we must negate it
							GL11.glTranslated(TileEntityRendererDispatcher.staticPlayerX, TileEntityRendererDispatcher.staticPlayerY, TileEntityRendererDispatcher.staticPlayerZ);
							TileEntityRendererDispatcher.instance.render(tile, tile.getPos().getX(), tile.getPos().getY(), tile.getPos().getZ(), f1);
							GL11.glPopMatrix();
						} catch (IllegalAccessException | InvocationTargetException | IllegalArgumentException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}

					//renderer.renderTileEntity(tile, tile.getPos().getX(), tile.getPos().getY(), tile.getPos().getZ(), f1, 0);
				}
			}
			TileEntityRendererDispatcher.instance.drawBatch(0);
		}
		
		//Clean up
		GlStateManager.disableBlend();
		GlStateManager.enableTexture2D();
		GlStateManager.enableLighting();
		GlStateManager.resetColor();
		GL11.glPopMatrix();


		//Clean up and make player not transparent
		OpenGlHelper.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, 0, 0);

	}

	@Override
	protected ResourceLocation getEntityTexture(@Nullable Entity p_110775_1_) {
		return null;
	}

	@Override
	public Render<? super EntityRocket> createRenderFor(RenderManager manager) {
		return new RendererRocket(manager);
	}

}
