package zmaster587.advancedRocketry.client.render;

import net.minecraft.block.state.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.chunk.ChunkCompileTaskGenerator;
import net.minecraft.client.renderer.chunk.IRenderChunkFactory;
import net.minecraft.client.renderer.chunk.ListChunkFactory;
import net.minecraft.client.renderer.chunk.RenderChunk;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.Entity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.fml.client.registry.IRenderFactory;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.lwjgl.opengl.GL11;

import zmaster587.advancedRocketry.AdvancedRocketry;
import zmaster587.advancedRocketry.api.IInfrastructure;
import zmaster587.advancedRocketry.entity.EntityRocket;
import zmaster587.advancedRocketry.util.StorageChunk;

public class RendererRocket extends Render implements IRenderFactory<EntityRocket> {

	private static BlockRendererDispatcher renderBlocks = Minecraft.getInstance().getBlockRendererDispatcher();
	private IRenderChunkFactory factory = new ListChunkFactory();

	Class tileEntityBlockChiseled;
	Method getState;

	public RendererRocket(EntityRendererManager manager) {
		super(manager);

		try {
			tileEntityBlockChiseled = Class.forName("mod.chiselsandbits.chiseledblock.TileEntityBlockChiseled");
			getState = tileEntityBlockChiseled.getMethod("getRenderState", IBlockAccess.class);
			AdvancedRocketry.logger.info("Chisel and bits support HAS BEEN loaded");
		}
		catch(ClassNotFoundException e) {
			AdvancedRocketry.logger.info("Chisel and bits support NOT loaded");
		}
		catch(NoSuchMethodException e)
		{
			AdvancedRocketry.logger.info("Chisel and bits support NOT loaded");
		}
	}


	//TODO: possibly optimize with GL lists
	@Override
	public void doRender(Entity entity, double x,
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

		/*if(entity.getPassengers().contains(Minecraft.getInstance().player)) {
			float angle = (float)(((EntityRocket)entity).getRCSRotateProgress()*0.9f*Math.PI/180f);
			y = ((EntityRocket)entity).stats.getSeatY();
			y= (0.5-((EntityRocket)entity).stats.getSeatY())*MathHelper.cos(angle) + (0)*MathHelper.sin(angle);
			//y = +0.5 -((EntityRocket)entity).stats.getSeatY();
		}*/

		matrix.push();
		GL11.glTranslatef((float)x, (float)y, (float)z);

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
					//RenderHelper.renderCubeWithUV(tess, 0, 0, 0, 2, 55, 2, 0, 1, 0, 1);
				}
			}
		}

		GlStateManager.color4f(1f, 1f, 1f);
		GlStateManager.disableBlend();
		GL11.glDisable(GL11.GL_LINE_STIPPLE);
		GlStateManager.enableTexture();

		matrix.pop();

		//Initial setup
		if(storage.world.displayListIndex == -1) {

			storage.world.displayListIndex = GLAllocation.generateDisplayLists(1);
			matrix.push();
			GL11.glNewList(storage.world.displayListIndex, GL11.GL_COMPILE);
			net.minecraft.client.renderer.RenderHelper.disableStandardItemLighting();

			//Render Each block
			net.minecraftforge.client.ForgeHooksClient.setRenderLayer(BlockRenderLayer.SOLID);
			Minecraft.getInstance().getTextureManager().bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
			for(int xx = 0; xx < storage.getSizeX(); xx++) {
				for(int zz = 0; zz < storage.getSizeZ(); zz++) {
					for(int yy = 0; yy < storage.getSizeY(); yy++) {
						BlockState block  = storage.getBlockState(new BlockPos(xx, yy, zz));

						//I'm not dealing with untextured blocks from chisel and bits today
						//Just assume everything from C&B is a bit
						if(block.getBlock().getRegistryName().getResourceDomain().equals("chiselsandbits"))
							continue;

						buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.BLOCK);
						try {
							Minecraft.getInstance().getBlockRendererDispatcher().renderBlock(block, new BlockPos(xx, yy, zz), storage.world, buffer);
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

			matrix.pop();
		}

		matrix.push();
		GL11.glTranslatef((float)x, (float)y + halfy, (float)z);
		GL11.glRotatef(((EntityRocket)entity).getRCSRotateProgress()*0.9f, 1f, 0f, 0f);
		GL11.glRotatef(((EntityRocket)entity).rotationYaw, 0f, 0f, 1f);
		GL11.glTranslatef((float)- halfx, (float)0 - halfy, (float)- halfz);
		Minecraft.getInstance().getTextureManager().bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
		GL11.glCallList(storage.world.displayListIndex);



		//Render tile entities if applicable
		for(TileEntity tile : storage.getTileEntityList()) {
			TileEntitySpecialRenderer renderer = (TileEntitySpecialRenderer)TileEntityRendererDispatcher.instance.renderers.get(tile.getClass());
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
				TileEntitySpecialRenderer renderer = (TileEntitySpecialRenderer)TileEntityRendererDispatcher.instance.renderers.get(tile.getClass());
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
		}
		
		//Clean up
		GlStateManager.disableBlend();
		GlStateManager.enableTexture();
		GlStateManager.enableLighting();
		GlStateManager.resetColor();
		matrix.pop();


		//Clean up and make player not transparent
		OpenGlHelper.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, 0, 0);

	}

	@Override
	protected ResourceLocation getEntityTexture(Entity p_110775_1_) {
		return null;
	}

	@Override
	public EntityRenderer<? super EntityRocket> createRenderFor(EntityRendererManager manager) {
		return new RendererRocket(manager);
	}

}
