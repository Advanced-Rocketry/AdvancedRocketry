package zmaster587.advancedRocketry.client.render.armor;

import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;
import zmaster587.advancedRocketry.backwardCompat.ModelFormatException;
import zmaster587.advancedRocketry.backwardCompat.WavefrontObject;

public class RenderJetPack extends ModelBiped {
	static WavefrontObject model;
	static ResourceLocation texture = new ResourceLocation("advancedrocketry:textures/models/jetpack.png");

	ModelBiped biped;


	public RenderJetPack(ModelBiped _default) {
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
	public void render(Entity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scale)
	{

		//super.render(p_78088_1_, p_78088_2_, p_78088_3_, p_78088_4_, p_78088_5_, p_78088_6_, p_78088_7_);


		biped.setRotationAngles(limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale, entity);
		biped.bipedRightArm.showModel = true;
		biped.bipedBody.showModel = true;
		biped.bipedLeftArm.showModel = true;
		GL11.glPushMatrix();
		if(entity.isSneaking()) {
			GL11.glTranslatef(0,.25f, 0);
		}
		
		biped.bipedBody.render(scale);
		biped.bipedLeftArm.render(scale);
		biped.bipedRightArm.render(scale);
		GL11.glPopMatrix();
		
		GL11.glPushMatrix();
		//GL11.glTranslatef(x, y, z);
		if(entity.isSneaking()) {
			GL11.glRotatef(0.5F * (180F / (float)Math.PI), 1.0F, 0.0F, 0.0F);
			GL11.glTranslatef(0,.2f, 0);
		}
		Minecraft.getMinecraft().renderEngine.bindTexture(texture);
		model.renderAll();
		GL11.glPopMatrix();
	}
}
