package zmaster587.advancedRocketry.client.render.armor;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.model.BipedModel;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;
import zmaster587.advancedRocketry.backwardCompat.ModelFormatException;
import zmaster587.advancedRocketry.backwardCompat.WavefrontObject;

public class RenderJetPack extends BipedModel {
	static WavefrontObject model;
	static ResourceLocation texture = new ResourceLocation("advancedrocketry:textures/models/jetpack.png");;

	BipedModel biped;


	public RenderJetPack(BipedModel _default) {
		if(model == null)
			try {
				model = new WavefrontObject(new ResourceLocation("advancedrocketry:models/jetPack.obj"));
			} catch (ModelFormatException e) {
				e.printStackTrace();
			}
		biped = _default;
	}



	/**
	 * Sets the models various rotation angles then renders the model.
	 */
	public void render(LivingEntity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scale)
	{

		//super.render(p_78088_1_, p_78088_2_, p_78088_3_, p_78088_4_, p_78088_5_, p_78088_6_, p_78088_7_);


		biped.setRotationAngles(entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);
		biped.bipedRightArm.showModel = true;
		biped.bipedBody.showModel = true;
		biped.bipedLeftArm.showModel = true;
		matrix.push();
		if(entity.isSneaking()) {
			GL11.glTranslatef(0,.25f, 0);
		}
		
		biped.bipedBody.render(scale);
		biped.bipedLeftArm.render(scale);
		biped.bipedRightArm.render(scale);
		matrix.pop();
		
		matrix.push();
		//GL11.glTranslatef(x, y, z);
		if(entity.isSneaking()) {
			GL11.glRotatef(0.5F * (180F / (float)Math.PI), 1.0F, 0.0F, 0.0F);
			GL11.glTranslatef(0,.2f, 0);
		}
		Minecraft.getInstance().getTextureManager().bindTexture(texture);
		model.renderAll();
		matrix.pop();
	}
}
