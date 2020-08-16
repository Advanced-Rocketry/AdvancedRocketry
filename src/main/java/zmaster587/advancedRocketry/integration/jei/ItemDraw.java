package zmaster587.advancedRocketry.integration.jei;

import com.mojang.blaze3d.matrix.MatrixStack;

import mezz.jei.api.gui.drawable.IDrawable;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;

public class ItemDraw implements IDrawable {


	ItemStack stack;

	public ItemDraw(ItemStack stack)
	{
		this.stack = stack;
	}

	@Override
	public int getWidth() {
		return 24;
	}

	@Override
	public int getHeight() {
		return 24;
	}

	@Override
	public void draw(MatrixStack matrixStack, int xOffset, int yOffset) {
		Minecraft.getInstance().getItemRenderer().renderItemIntoGUI(stack, 0, 0);

	}

}
