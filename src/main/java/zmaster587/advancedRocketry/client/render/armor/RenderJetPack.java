package zmaster587.advancedRocketry.client.render.armor;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.model.BipedModel;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;

import zmaster587.advancedRocketry.backwardCompat.ModelFormatException;
import zmaster587.advancedRocketry.backwardCompat.WavefrontObject;

public class RenderJetPack extends BipedModel {
	static WavefrontObject model;
	static ResourceLocation texture = new ResourceLocation("advancedrocketry:textures/models/jetpack.png");;

	BipedModel biped;
	float scale;

	public RenderJetPack(BipedModel _default) {
		this(_default, 1);
	}
	
	public RenderJetPack(BipedModel _default, float scale) {
		super(scale);
		this.scale = scale;
		if(model == null)
			try {
				model = new WavefrontObject(new ResourceLocation("advancedrocketry:models/jetPack.obj"));
			} catch (ModelFormatException e) {
				e.printStackTrace();
			}
		biped = _default;
	}
	
	
	@Override
	public void setRotationAngles(LivingEntity entityIn, float limbSwing, float limbSwingAmount, float ageInTicks,
			float netHeadYaw, float headPitch) {
		// TODO Auto-generated method stub
		super.setRotationAngles(entityIn, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);
	}
	
	/**
	 * Sets the models various rotation angles then renders the model.
	 */
	public void render(MatrixStack matrix, IVertexBuilder bufferIn, int packedLightIn, int packedOverlayIn,
			float red, float green, float blue, float alpha)
	{

		//super.render(p_78088_1_, p_78088_2_, p_78088_3_, p_78088_4_, p_78088_5_, p_78088_6_, p_78088_7_);

		/*Entity entity = this.getEntityModel()
		biped.setRotationAngles(entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);
		biped.bipedRightArm.showModel = true;
		biped.bipedBody.showModel = true;
		biped.bipedLeftArm.showModel = true;
		matrix.push();
		if(entity.isSneaking()) {
			matrix.translate(0,.25f, 0);
		}
		
		biped.bipedBody.render(scale);
		biped.bipedLeftArm.render(scale);
		biped.bipedRightArm.render(scale);
		matrix.pop();
		
		matrix.push();
		//matrix.translate(x, y, z);
		if(entity.isSneaking()) {
			GL11.glRotatef(0.5F * (180F / (float)Math.PI), 1.0F, 0.0F, 0.0F);
			matrix.translate(0,.2f, 0);
		}
		Minecraft.getInstance().getTextureManager().bindTexture(texture);
		model.renderAll();
		matrix.pop();*/
	}
}
