package zmaster587.advancedRocketry.client.render.entity;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderItem;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderEntityItem;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.client.registry.IRenderFactory;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import zmaster587.advancedRocketry.entity.EntityItemAbducted;

import javax.annotation.Nullable;
import java.util.Random;

/**
 * Yeah, i know, this is literally a copy of the item renderer, other option was asm the class responsible for render distance
 */
@SideOnly(Side.CLIENT)
public class RendererItem extends Render<EntityItemAbducted> implements IRenderFactory<EntityItemAbducted>
{
    private static final ResourceLocation RES_ITEM_GLINT = new ResourceLocation("textures/misc/enchanted_item_glint.png");
    /** The RNG used in RenderItem (for bobbing itemstacks on the ground) */
    private Random random = new Random();
    public boolean renderWithColor = true;
    /** Defines the zLevel of rendering of item on GUI. */
    public float zLevel;
    public static boolean renderInFrame;
    private static final String __OBFID = "CL_00001003";
    RenderEntityItem itemRenderer;

    public RendererItem(RenderManager renderManagerIn, RenderItem p_i46167_2_) {
    	super(renderManagerIn);
    	
    	itemRenderer = new RenderEntityItem(renderManagerIn, p_i46167_2_);
    }

    /**
     * Actually renders the given argument. This is a synthetic bridge method, always casting down its argument and then
     * handing it off to a worker function which does the actual work. In all probabilty, the class Render is generic
     * (Render<T extends Entity) and this method has signature public void func_76986_a(T entity, double d, double d1,
     * double d2, float f, float f1). But JAD is pre 1.5 so doesn't do that.
     */
    @Override
    public void doRender(EntityItemAbducted p_76986_1_, double p_76986_2_, double p_76986_4_, double p_76986_6_, float p_76986_8_, float p_76986_9_)
    {
    	itemRenderer.doRender(p_76986_1_.getItemEntity(), p_76986_2_, p_76986_4_, p_76986_6_, p_76986_8_, p_76986_9_);
    }

	@Override
	protected ResourceLocation getEntityTexture(@Nullable EntityItemAbducted entity) {
		return TextureMap.LOCATION_BLOCKS_TEXTURE;
	}

	@Override
	public Render<? super EntityItemAbducted> createRenderFor(
			RenderManager manager) {
		return new RendererItem(manager, Minecraft.getMinecraft().getRenderItem());
	}
}