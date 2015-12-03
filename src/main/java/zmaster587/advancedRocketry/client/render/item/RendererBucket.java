package zmaster587.advancedRocketry.client.render.item;

import javax.swing.Icon;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraftforge.client.IItemRenderer;
import net.minecraftforge.fluids.FluidContainerRegistry;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;

public class RendererBucket implements IItemRenderer {

	@Override
	public boolean handleRenderType(ItemStack item, ItemRenderType type) {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public boolean shouldUseRenderHelper(ItemRenderType type, ItemStack item,
			ItemRendererHelper helper) {
		return false;
	}

	@Override
	public void renderItem(ItemRenderType type, ItemStack item, Object... data) {

		// ====================== Render item texture ======================
		FluidStack fluidStack = FluidContainerRegistry.getFluidForFilledItem(item);
		int color = fluidStack.getFluid().getColor();

		IIcon fluidIcon = item.getIconIndex();
		IIcon bucketIcon = Items.bucket.getIconIndex(new ItemStack(Items.bucket));
		if(type == ItemRenderType.INVENTORY) {
			GL11.glEnable(GL11.GL_ALPHA_TEST);
			RenderItem.getInstance().renderIcon(0, 0, bucketIcon, 16, 16);

			GL11.glColor3ub((byte)((color >>> 16) & 0xFF), (byte)((color >>> 8) & 0xFF), (byte)(color & 0xFF));

			RenderItem.getInstance().renderIcon(0, 0, fluidIcon, 16, 16);
			GL11.glColor3f(1f, 1f, 1f);
		}
		else {
			
			ItemRenderer.renderItemIn2D(Tessellator.instance, ((IIcon)bucketIcon).getMinU(), ((IIcon)bucketIcon).getMinV(), ((IIcon)bucketIcon).getMaxU(), ((IIcon)bucketIcon).getMaxV(), ((IIcon)bucketIcon).getIconWidth(), ((IIcon)bucketIcon).getIconHeight(), 0.1f);
			GL11.glColor3ub((byte)((color >>> 16) & 0xFF), (byte)((color >>> 8) & 0xFF), (byte)(color & 0xFF));
			ItemRenderer.renderItemIn2D(Tessellator.instance, ((IIcon)fluidIcon).getMinU(), ((IIcon)fluidIcon).getMinV(), ((IIcon)fluidIcon).getMaxU(), ((IIcon)fluidIcon).getMaxV(), ((IIcon)fluidIcon).getIconWidth(), ((IIcon)fluidIcon).getIconHeight(), 0.1f);
			GL11.glColor3f(1f, 1f, 1f);
		}
	}
}
