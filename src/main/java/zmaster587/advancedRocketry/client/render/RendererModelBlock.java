package zmaster587.advancedRocketry.client.render;

import org.lwjgl.opengl.GL11;

import zmaster587.advancedRocketry.tile.TileModelRender;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.AdvancedModelLoader;
import net.minecraftforge.client.model.IModelCustom;

public class RendererModelBlock  extends TileEntitySpecialRenderer {

	protected static IModelCustom rocketModel = AdvancedModelLoader.loadModel(new ResourceLocation("advancedrocketry:models/combustion.obj"));
	protected static ResourceLocation rocketTexture = new ResourceLocation("advancedrocketry:textures/models/combustion.png");
	
	protected static IModelCustom middleTankModel = AdvancedModelLoader.loadModel(new ResourceLocation("advancedrocketry:models/middleTank.obj"));
	protected static ResourceLocation  middleTankTexture = new ResourceLocation("advancedrocketry:textures/models/tank.png");

	protected static IModelCustom endTankModel = AdvancedModelLoader.loadModel(new ResourceLocation("advancedrocketry:models/endTank.obj"));
	protected static ResourceLocation endTankTexture = new ResourceLocation("advancedrocketry:textures/models/tank.png");
	
	protected static IModelCustom topTankModel =  AdvancedModelLoader.loadModel(new ResourceLocation("advancedrocketry:models/topTank.obj"));
	protected static ResourceLocation topTankTexture =new ResourceLocation("advancedrocketry:textures/models/tank.png");
	
	

	public RendererModelBlock() {
	}

	@Override
	public void renderTileEntityAt(TileEntity tile, double x,
			double y, double z, float f) {
		
		TileModelRender rendertile = (TileModelRender)tile;
		GL11.glPushMatrix();
		GL11.glTranslated(x + 0.5, y+ 0.5, z+ 0.5);
		
		int modelNum = rendertile.getModel();
		
		if(modelNum == TileModelRender.models.ROCKET.ordinal()) {
			bindTexture(rocketTexture);
			rocketModel.renderAll();
		}
		else if(modelNum == TileModelRender.models.TANKMIDDLE.ordinal()) {
			bindTexture(middleTankTexture);
			middleTankModel.renderAll();
		}
		else if(modelNum == TileModelRender.models.TANKEND.ordinal()) {
			bindTexture(endTankTexture);
			endTankModel.renderAll();
		}
		else if(modelNum == TileModelRender.models.TANKTOP.ordinal()) {
			bindTexture(topTankTexture);
			topTankModel.renderAll();
		}
		//bindTexture(rendertile.getTexture());
		//rendertile.getModel().renderAll();
		
		
		GL11.glPopMatrix();
	}

}
