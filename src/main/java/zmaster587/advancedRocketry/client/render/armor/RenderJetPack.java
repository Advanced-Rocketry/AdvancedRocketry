package zmaster587.advancedRocketry.client.render.armor;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.AdvancedModelLoader;
import net.minecraftforge.client.model.IModelCustom;

public class RenderJetPack extends ModelBiped {
	IModelCustom model;
	ResourceLocation texture;

	public RenderJetPack() {
		texture = new ResourceLocation("advancedrocketry:textures/models/jetpack.png");
		model = AdvancedModelLoader.loadModel(new ResourceLocation("advancedrocketry:models/jetPack.obj"));
	}

	/**
	 * Sets the models various rotation angles then renders the model.
	 */
	public void render(Entity p_78088_1_, float x, float y, float z, float p_78088_5_, float p_78088_6_, float p_78088_7_)
	{

		//super.render(p_78088_1_, p_78088_2_, p_78088_3_, p_78088_4_, p_78088_5_, p_78088_6_, p_78088_7_);
		

		GL11.glPushMatrix();
		GL11.glTranslatef(x, y, z);
		if(p_78088_1_.isSneaking())
			GL11.glRotatef(0.5F * (180F / (float)Math.PI), 1.0F, 0.0F, 0.0F);
		Minecraft.getMinecraft().renderEngine.bindTexture(texture);
		model.renderAll();
		GL11.glPopMatrix();

		/*super.
        this.setRotationAngles(p_78088_2_, p_78088_3_, p_78088_4_, p_78088_5_, p_78088_6_, p_78088_7_, p_78088_1_);

        if (this.isChild)
        {
            float f6 = 2.0F;
            GL11.glPushMatrix();
            GL11.glScalef(1.5F / f6, 1.5F / f6, 1.5F / f6);
            GL11.glTranslatef(0.0F, 16.0F * p_78088_7_, 0.0F);
            this.bipedHead.render(p_78088_7_);
            GL11.glPopMatrix();
            GL11.glPushMatrix();
            GL11.glScalef(1.0F / f6, 1.0F / f6, 1.0F / f6);
            GL11.glTranslatef(0.0F, 24.0F * p_78088_7_, 0.0F);
            this.bipedBody.render(p_78088_7_);
            this.bipedRightArm.render(p_78088_7_);
            this.bipedLeftArm.render(p_78088_7_);
            this.bipedRightLeg.render(p_78088_7_);
            this.bipedLeftLeg.render(p_78088_7_);
            this.bipedHeadwear.render(p_78088_7_);
            GL11.glPopMatrix();
        }
        else
        {
            this.bipedHead.render(p_78088_7_);
            this.bipedBody.render(p_78088_7_);
            this.bipedRightArm.render(p_78088_7_);
            this.bipedLeftArm.render(p_78088_7_);
            this.bipedRightLeg.render(p_78088_7_);
            this.bipedLeftLeg.render(p_78088_7_);
            this.bipedHeadwear.render(p_78088_7_);
        }*/
	}
}
