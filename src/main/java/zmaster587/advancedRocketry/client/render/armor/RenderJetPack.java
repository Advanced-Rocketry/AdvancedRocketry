package zmaster587.advancedRocketry.client.render.armor;

import net.minecraft.client.renderer.entity.model.BipedModel;
import net.minecraft.client.renderer.model.Model;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.util.ResourceLocation;

import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;

import zmaster587.advancedRocketry.backwardCompat.ModelFormatException;
import zmaster587.advancedRocketry.backwardCompat.WavefrontObject;

public class RenderJetPack extends BipedModel {

	BipedModel biped;
	float scale;

	public RenderJetPack(BipedModel _default) {
		this(_default, 1);
	}

	public RenderJetPack(BipedModel _default, float scale) {
		super(scale);
		this.scale = scale;

	}

	protected Iterable<ModelRenderer> getBodyParts() {
		return ImmutableList.of(this.bipedBody, this.bipedRightArm, this.bipedLeftArm, this.bipedRightLeg, this.bipedLeftLeg, this.bipedHeadwear);
	}


	public static class JetpackModelRenderer extends ModelRenderer
	{
		static WavefrontObject model;
		static ResourceLocation texture = new ResourceLocation("advancedrocketry:textures/models/jetpack.png");
		public JetpackModelRenderer(Model model) {
			super(model);

			if(JetpackModelRenderer.model == null)
				try {
					JetpackModelRenderer.model = new WavefrontObject(new ResourceLocation("advancedrocketry:models/jetpack.obj"));
				} catch (ModelFormatException e) {
					e.printStackTrace();
				}
		}

		public void render(MatrixStack matrix, IVertexBuilder buffer, int packedLightIn, int packedOverlayIn, float red, float green, float blue, float alpha) {
			if (this.showModel) {

				matrix.push();
				//matrix.translate(x, y, z);
				//if(entity.isSneaking()) {
				//	GL11.glRotatef(0.5F * (180F / (float)Math.PI), 1.0F, 0.0F, 0.0F);
				//	matrix.translate(0,.2f, 0);
				//}
				model.renderAll(matrix, buffer, red, green, blue, alpha);
				matrix.pop();
			}
		}

	}
	/**
	 * Sets the models various rotation angles then renders the model.
	 */
}
