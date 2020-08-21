/**
 * Yeah, i know, this is litterally a copy of the item renderer, other option was asm the class responsible for render distance
 */

package zmaster587.advancedRocketry.client.render.entity;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.model.ItemCameraTransforms.TransformType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.client.registry.IRenderFactory;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import zmaster587.advancedRocketry.entity.EntityItemAbducted;

import java.util.Random;

import com.mojang.blaze3d.matrix.MatrixStack;

@OnlyIn(value=Dist.CLIENT)
public class RendererItem extends EntityRenderer<EntityItemAbducted> implements IRenderFactory<EntityItemAbducted>
{
    private static final ResourceLocation RES_ITEM_GLINT = new ResourceLocation("textures/misc/enchanted_item_glint.png");
    /** The RNG used in RenderItem (for bobbing itemstacks on the ground) */
    private Random random = new Random();
    public boolean renderWithColor = true;
    /** Defines the zLevel of rendering of item on GUI. */
    public float zLevel;
    public static boolean renderInFrame;
    private static final String __OBFID = "CL_00001003";
    //RenderBlock itemRenderer;

    public RendererItem(EntityRendererManager renderManagerIn) {
    	super(renderManagerIn);
    }

    /**
     * Actually renders the given argument. This is a synthetic bridge method, always casting down its argument and then
     * handing it off to a worker function which does the actual work. In all probabilty, the class Render is generic
     * (EntityRenderer<T extends Entity) and this method has signature public void func_76986_a(T entity, double d, double d1,
     * double d2, float f, float f1). But JAD is pre 1.5 so doesn't do that.
     */
    @Override
    public void render(EntityItemAbducted entity, float entityYaw, float partialTicks, MatrixStack matrixStackIn,
    		IRenderTypeBuffer bufferIn, int packedLightIn)
    {
    	Minecraft.getInstance().getItemRenderer().renderItem(entity.getEntityItem(), TransformType.GROUND, packedLightIn, OverlayTexture.NO_OVERLAY, matrixStackIn, bufferIn);
    }

	@Override
	public EntityRenderer<? super EntityItemAbducted> createRenderFor(
			EntityRendererManager manager) {
		return new RendererItem(manager);
	}

	@Override
	public ResourceLocation getEntityTexture(EntityItemAbducted entity) {
		return null;
	}
}