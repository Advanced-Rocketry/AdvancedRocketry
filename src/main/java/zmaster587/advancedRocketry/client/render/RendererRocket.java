package zmaster587.advancedRocketry.client.render;

import org.lwjgl.opengl.GL11;

import zmaster587.advancedRocketry.api.IInfrastructure;
import zmaster587.advancedRocketry.entity.EntityRocket;
import zmaster587.advancedRocketry.util.StorageChunk;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockRendererDispatcher;
import net.minecraft.client.renderer.GLAllocation;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.VertexBuffer;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.Entity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.client.registry.IRenderFactory;

public class RendererRocket extends Render implements IRenderFactory<EntityRocket> {

	private static BlockRendererDispatcher renderBlocks = Minecraft.getMinecraft().getBlockRendererDispatcher();

	public RendererRocket(RenderManager manager) {
		super(manager);
	}


	//TODO: possibly optimize with GL lists
	@Override
	public void doRender(Entity entity, double x,
			double y, double z, float f1,
			float f2) {

		StorageChunk storage  = ((EntityRocket)entity).storage;


		VertexBuffer buffer = Tessellator.getInstance().getBuffer();

		if(storage == null || !storage.finalized)
			return;

		if(entity.getPassengers().contains(Minecraft.getMinecraft().thePlayer)) {

			y = +0.5 -((EntityRocket)entity).stats.getSeatY();
		}

		//Find the halfway point along the XZ plane
		float halfx = storage.getSizeX()/2f;
		float halfz = storage.getSizeZ()/2f;

		GL11.glPushMatrix();
		GL11.glTranslatef((float)x, (float)y, (float)z);

		GL11.glEnable(GL11.GL_BLEND);
		GL11.glBlendFunc(GL11.GL_ONE, GL11.GL_ONE);
		GL11.glColor4f(0.5f, 1f, .5f, .2f);

		GL11.glDisable(GL11.GL_TEXTURE_2D);
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

		GL11.glColor3f(1f, 1f, 1f);
		GL11.glDisable(GL11.GL_BLEND);
		GL11.glDisable(GL11.GL_LINE_STIPPLE);
		GL11.glEnable(GL11.GL_TEXTURE_2D);

		GL11.glPopMatrix();

		//Initial setup
		if(storage.world.displayListIndex == -1) {
			
			storage.world.displayListIndex = GLAllocation.generateDisplayLists(1);
			GL11.glPushMatrix();
			GL11.glNewList(storage.world.displayListIndex, GL11.GL_COMPILE);

			net.minecraft.client.renderer.RenderHelper.disableStandardItemLighting();

			//Render Each block
			Minecraft.getMinecraft().getTextureManager().bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
			for(int xx = 0; xx < storage.getSizeX(); xx++) {
				for(int zz = 0; zz < storage.getSizeZ(); zz++) {
					for(int yy = 0; yy < storage.getSizeY(); yy++) {
						IBlockState block  = storage.getBlockState(new BlockPos(xx, yy, zz));

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

			net.minecraft.client.renderer.RenderHelper.enableStandardItemLighting();
			
			GL11.glEndList();
			
			GL11.glPopMatrix();
		}

		GL11.glPushMatrix();
		GL11.glTranslatef((float)x - halfx, (float)y, (float)z - halfz);
		Minecraft.getMinecraft().getTextureManager().bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
		GL11.glCallList(storage.world.displayListIndex);
		
		

		//Render tile entities if applicable
		for(TileEntity tile : storage.getTileEntityList()) {
			TileEntitySpecialRenderer renderer = (TileEntitySpecialRenderer)TileEntityRendererDispatcher.instance.mapSpecialRenderers.get(tile.getClass());
			if(renderer != null ) {
				TileEntityRendererDispatcher.instance.renderTileEntityAt(tile, tile.getPos().getX(), tile.getPos().getY(), tile.getPos().getZ(), f1);
				
				//renderer.renderTileEntity(tile, tile.getPos().getX(), tile.getPos().getY(), tile.getPos().getZ(), f1, 0);
			}
		}
		//net.minecraft.client.renderer.RenderHelper.disableStandardItemLighting();
		GlStateManager.enableLighting();
		GlStateManager.color(1, 1, 1);
		GL11.glPopMatrix();
		
		
		//Clean up and make player not transparent
		OpenGlHelper.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, 0, 0);

	}

	@Override
	protected ResourceLocation getEntityTexture(Entity p_110775_1_) {
		return null;
	}

	@Override
	public Render<? super EntityRocket> createRenderFor(RenderManager manager) {
		return new RendererRocket(manager);
	}

}
