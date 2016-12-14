package zmaster587.advancedRocketry.client.render.item;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraftforge.client.IItemRenderer;

public class RendererLaserGun implements IItemRenderer {

	@Override
	public boolean handleRenderType(ItemStack item, ItemRenderType type) {
		// TODO Auto-generated method stub
		return type == ItemRenderType.EQUIPPED_FIRST_PERSON || type == ItemRenderType.EQUIPPED;
	}

	@Override
	public boolean shouldUseRenderHelper(ItemRenderType type, ItemStack item,
			ItemRendererHelper helper) {
		return false;
	}

	@Override
	public void renderItem(ItemRenderType type, ItemStack item, Object... data) {

		IIcon icon = item.getIconIndex();

		GL11.glPushMatrix();
		if(type == ItemRenderType.EQUIPPED_FIRST_PERSON) {
			GL11.glTranslatef(1.3f, .3f, 0);
			GL11.glRotated(210, 0, 0, 1);
			GL11.glRotated(180, 1, 0, 0);
			
			ItemRenderer.renderItemIn2D(Tessellator.instance, ((IIcon)icon).getMinU(), ((IIcon)icon).getMinV(), ((IIcon)icon).getMaxU(), ((IIcon)icon).getMaxV(), ((IIcon)icon).getIconWidth(), ((IIcon)icon).getIconHeight(), 0.1f);
		}
		else if(type == ItemRenderType.EQUIPPED) {
			GL11.glTranslatef(0.7f, 1.3f, 0);
			GL11.glRotated(-70, 0, 0, 1);
			GL11.glRotated(180, 1, 0, 0);
			ItemRenderer.renderItemIn2D(Tessellator.instance, ((IIcon)icon).getMinU(), ((IIcon)icon).getMinV(), ((IIcon)icon).getMaxU(), ((IIcon)icon).getMaxV(), ((IIcon)icon).getIconWidth(), ((IIcon)icon).getIconHeight(), 0.1f);
		
		}
			GL11.glPopMatrix();
	}

}
