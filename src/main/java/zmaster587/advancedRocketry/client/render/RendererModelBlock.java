package zmaster587.advancedRocketry.client.render;

import org.lwjgl.opengl.GL11;

import zmaster587.advancedRocketry.api.AdvancedRocketryBlocks;
import zmaster587.advancedRocketry.client.render.multiblocks.RendererCuttingMachine;
import zmaster587.advancedRocketry.inventory.TextureResources;
import zmaster587.advancedRocketry.tile.TileModelRender;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.IItemRenderer;
import net.minecraftforge.client.model.AdvancedModelLoader;
import net.minecraftforge.client.model.IModelCustom;

public class RendererModelBlock  extends TileEntitySpecialRenderer implements IItemRenderer {

	protected static IModelCustom rocketModel = AdvancedModelLoader.loadModel(new ResourceLocation("advancedrocketry:models/combustion.obj"));
	protected static ResourceLocation rocketTexture = new ResourceLocation("advancedrocketry:textures/models/combustion.png");
	
	protected static IModelCustom middleTankModel = AdvancedModelLoader.loadModel(new ResourceLocation("advancedrocketry:models/middleTank.obj"));
	protected static ResourceLocation  middleTankTexture = new ResourceLocation("advancedrocketry:textures/models/tank.png");

	protected static IModelCustom endTankModel = AdvancedModelLoader.loadModel(new ResourceLocation("advancedrocketry:models/endTank.obj"));
	protected static ResourceLocation endTankTexture = new ResourceLocation("advancedrocketry:textures/models/tank.png");
	
	protected static IModelCustom topTankModel =  AdvancedModelLoader.loadModel(new ResourceLocation("advancedrocketry:models/topTank.obj"));
	protected static ResourceLocation topTankTexture =new ResourceLocation("advancedrocketry:textures/models/tank.png");
	
	protected static IModelCustom motorModel =  AdvancedModelLoader.loadModel(new ResourceLocation("advancedrocketry:models/motor.obj"));
	protected static ResourceLocation motorTexture = new ResourceLocation("advancedrocketry:textures/models/motor.png");

	protected static IModelCustom sawBladeModel =  AdvancedModelLoader.loadModel(new ResourceLocation("advancedrocketry:models/sawBlade.obj"));
	protected static ResourceLocation sawBladeTexture = RendererCuttingMachine.texture;

	public RendererModelBlock() {
	}

	@Override
	public void renderTileEntityAt(TileEntity tile, double x,
			double y, double z, float f) {
		
		TileModelRender rendertile = (TileModelRender)tile;
		GL11.glPushMatrix();
		GL11.glTranslated(x + 0.5, y+ 0.5, z+ 0.5);
		GL11.glRotatef((rendertile.getRotation().offsetX*90) + (rendertile.getRotation().offsetZ == -1 ? 180 : 0), 0, 1, 0);
		
		
		int modelNum = rendertile.getModel();
		TextureManager textureMgr = Minecraft.getMinecraft().getTextureManager();
		
		if(modelNum == TileModelRender.models.ROCKET.ordinal()) {
			textureMgr.bindTexture(rocketTexture);
			if(rendertile.getRotation().offsetY == 0)
				GL11.glRotatef(90,1,0,0);
			rocketModel.renderAll();
		}
		else if(modelNum == TileModelRender.models.TANKMIDDLE.ordinal()) {
			textureMgr.bindTexture(middleTankTexture);
			middleTankModel.renderAll();
		}
		else if(modelNum == TileModelRender.models.TANKEND.ordinal()) {
			textureMgr.bindTexture(endTankTexture);
			endTankModel.renderAll();
		}
		else if(modelNum == TileModelRender.models.TANKTOP.ordinal()) {
			textureMgr.bindTexture(topTankTexture);
			topTankModel.renderAll();
		}
		else if(modelNum == TileModelRender.models.SAWBLADE.ordinal()) {
			textureMgr.bindTexture(sawBladeTexture);
			sawBladeModel.renderAll();
		}
		else if(modelNum == TileModelRender.models.MOTOR.ordinal()) {
			textureMgr.bindTexture(motorTexture);
			motorModel.renderAll();
		}
		//bindTexture(rendertile.getTexture());
		//rendertile.getModel().renderAll();
		
		
		GL11.glPopMatrix();
	}

	@Override
	public boolean handleRenderType(ItemStack item, ItemRenderType type) {
		Block block = Block.getBlockFromItem(item.getItem());
		return true;
	}

	@Override
	public boolean shouldUseRenderHelper(ItemRenderType type, ItemStack item,
			ItemRendererHelper helper) {
		return false;
	}

	@Override
	public void renderItem(ItemRenderType type, ItemStack item, Object... data) {
		Block block = Block.getBlockFromItem(item.getItem());
		
		if(type.INVENTORY == type) {
			GL11.glPushMatrix();
			GL11.glScalef(-10, -10, -10);
			GL11.glRotatef(45, 0, 1, 0);
			GL11.glRotatef(-25, 1, 0, 0);
			GL11.glRotatef(-15, 0, 0, 1);
			GL11.glEnable(GL11.GL_LIGHTING);
			renderTileEntityAt(block.createTileEntity(null, 0), -2.2f, -2.1f, 0f, 0f);
			GL11.glDisable(GL11.GL_LIGHTING);
			GL11.glPopMatrix();
			
		}
		else {
			GL11.glPushMatrix();
			GL11.glRotatef(25, 0, 0, 1);
			GL11.glRotatef(20, 0, 1, 0);
			GL11.glScalef(.75f, .75f, .75f);
			renderTileEntityAt(block.createTileEntity(null, 0), .5f, -.7, 0f, 0f);
			GL11.glPopMatrix();
		}
	}

}
