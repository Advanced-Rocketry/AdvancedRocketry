package zmaster587.advancedRocketry.inventory.modules;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import zmaster587.advancedRocketry.inventory.TextureResources;
import zmaster587.libVulpes.gui.CommonResources;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.item.ItemStack;

public class ModuleSlotButton extends ModuleButton {

	ItemStack stack;

	public ModuleSlotButton(int offsetX, int offsetY, int buttonId, IButtonInventory tile, ItemStack slotDisplay) {
		
		super(offsetX, offsetY, buttonId , "", tile, TextureResources.buttonNull, slotDisplay.getDisplayName() ,16,16);
		stack = slotDisplay;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void renderBackground(GuiContainer gui, int x, int y, int mouseX, int mouseY,
			FontRenderer font) {
		
		RenderBlocks renderBlocksRi = RenderBlocks.getInstance();
		TextureManager textureManager = Minecraft.getMinecraft().getTextureManager();
		
		
		textureManager.bindTexture(CommonResources.genericBackground);
		gui.drawTexturedModalRect(x + offsetX - 1, y + offsetY - 1, 176, 0, 18, 18);
		
		int p_77015_4_ = x + offsetX;
		int p_77015_5_ = y + offsetY;

		int zLevel = 500;
		

		int k = stack.getItemDamage();
		Object object = stack.getIconIndex();
		int l;
		float f;
		float f3;
		float f4;

		if (stack.getItemSpriteNumber() == 0 && RenderBlocks.renderItemIn3d(Block.getBlockFromItem(stack.getItem()).getRenderType()))
		{
			textureManager.bindTexture(TextureMap.locationBlocksTexture);
			Block block = Block.getBlockFromItem(stack.getItem());
			GL11.glEnable(GL11.GL_ALPHA_TEST);

			if (block.getRenderBlockPass() != 0)
			{
				GL11.glAlphaFunc(GL11.GL_GREATER, 0.1F);
				GL11.glEnable(GL11.GL_BLEND);
				OpenGlHelper.glBlendFunc(770, 771, 1, 0);
			}
			else
			{
				GL11.glAlphaFunc(GL11.GL_GREATER, 0.5F);
				GL11.glDisable(GL11.GL_BLEND);
			}

			GL11.glPushMatrix();
			GL11.glTranslatef((float)(p_77015_4_ - 2), (float)(p_77015_5_ + 3), -3.0F + zLevel);
			GL11.glScalef(10.0F, 10.0F, 10.0F);
			GL11.glTranslatef(1.0F, 0.5F, 1.0F);
			GL11.glScalef(1.0F, 1.0F, -1.0F);
			GL11.glRotatef(210.0F, 1.0F, 0.0F, 0.0F);
			GL11.glRotated(45.0F + ((System.currentTimeMillis() % 200000)/50F) * 2, 0.0F, 1.0F, 0.0F);
			l = stack.getItem().getColorFromItemStack(stack, 0);
			f3 = (float)(l >> 16 & 255) / 255.0F;
			f4 = (float)(l >> 8 & 255) / 255.0F;
			f = (float)(l & 255) / 255.0F;
			
            renderBlocksRi.renderBlockAsItem(block, k, 1.0F);
            renderBlocksRi.useInventoryTint = true;

			if (block.getRenderBlockPass() == 0)
			{
				GL11.glAlphaFunc(GL11.GL_GREATER, 0.1F);
			}

			GL11.glPopMatrix();
		}
		/*else if (stack.getItem().requiresMultipleRenderPasses())
	        {
	            GL11.glDisable(GL11.GL_LIGHTING);
	            GL11.glEnable(GL11.GL_ALPHA_TEST);
	            p_77015_2_.bindTexture(TextureMap.locationItemsTexture);
	            GL11.glDisable(GL11.GL_ALPHA_TEST);
	            GL11.glDisable(GL11.GL_TEXTURE_2D);
	            GL11.glEnable(GL11.GL_BLEND);
	            OpenGlHelper.glBlendFunc(0, 0, 0, 0);
	            GL11.glColorMask(false, false, false, true);
	            GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
	            Tessellator tessellator = Tessellator.instance;
	            tessellator.startDrawingQuads();
	            tessellator.setColorOpaque_I(-1);
	            tessellator.addVertex((double)(p_77015_4_ - 2), (double)(p_77015_5_ + 18), (double)zLevel);
	            tessellator.addVertex((double)(p_77015_4_ + 18), (double)(p_77015_5_ + 18), (double)zLevel);
	            tessellator.addVertex((double)(p_77015_4_ + 18), (double)(p_77015_5_ - 2), (double)zLevel);
	            tessellator.addVertex((double)(p_77015_4_ - 2), (double)(p_77015_5_ - 2), (double)zLevel);
	            tessellator.draw();
	            GL11.glColorMask(true, true, true, true);
	            GL11.glEnable(GL11.GL_TEXTURE_2D);
	            GL11.glEnable(GL11.GL_ALPHA_TEST);

	            Item item = stack.getItem();
	            for (l = 0; l < item.getRenderPasses(k); ++l)
	            {
	                OpenGlHelper.glBlendFunc(770, 771, 1, 0);
	                p_77015_2_.bindTexture(item.getSpriteNumber() == 0 ? TextureMap.locationBlocksTexture : TextureMap.locationItemsTexture);
	                IIcon iicon = item.getIcon(stack, l);
	                int i1 = stack.getItem().getColorFromItemStack(stack, l);
	                f = (float)(i1 >> 16 & 255) / 255.0F;
	                float f1 = (float)(i1 >> 8 & 255) / 255.0F;
	                float f2 = (float)(i1 & 255) / 255.0F;

	                if (this.renderWithColor)
	                {
	                    GL11.glColor4f(f, f1, f2, 1.0F);
	                }

	                GL11.glDisable(GL11.GL_LIGHTING); //Forge: Make sure that render states are reset, ad renderEffect can derp them up.
	                GL11.glEnable(GL11.GL_ALPHA_TEST);

	                this.renderIcon(p_77015_4_, p_77015_5_, iicon, 16, 16);

	                GL11.glDisable(GL11.GL_ALPHA_TEST);
	                GL11.glEnable(GL11.GL_LIGHTING);

	                if (renderEffect && stack.hasEffect(l))
	                {
	                    renderEffect(p_77015_2_, p_77015_4_, p_77015_5_);
	                }
	            }

	            GL11.glEnable(GL11.GL_LIGHTING);
	        }
	        else
	        {
	            GL11.glDisable(GL11.GL_LIGHTING);
	            GL11.glEnable(GL11.GL_BLEND);
	            OpenGlHelper.glBlendFunc(770, 771, 1, 0);
	            ResourceLocation resourcelocation = p_77015_2_.getResourceLocation(stack.getItemSpriteNumber());
	            p_77015_2_.bindTexture(resourcelocation);

	            if (object == null)
	            {
	                object = ((TextureMap)Minecraft.getMinecraft().getTextureManager().getTexture(resourcelocation)).getAtlasSprite("missingno");
	            }

	            l = stack.getItem().getColorFromItemStack(stack, 0);
	            f3 = (float)(l >> 16 & 255) / 255.0F;
	            f4 = (float)(l >> 8 & 255) / 255.0F;
	            f = (float)(l & 255) / 255.0F;

	            if (this.renderWithColor)
	            {
	                GL11.glColor4f(f3, f4, f, 1.0F);
	            }

	            GL11.glDisable(GL11.GL_LIGHTING); //Forge: Make sure that render states are reset, a renderEffect can derp them up.
	            GL11.glEnable(GL11.GL_ALPHA_TEST);
	            GL11.glEnable(GL11.GL_BLEND);

	            this.renderIcon(p_77015_4_, p_77015_5_, (IIcon)object, 16, 16);

	            GL11.glEnable(GL11.GL_LIGHTING);
	            GL11.glDisable(GL11.GL_ALPHA_TEST);
	            GL11.glDisable(GL11.GL_BLEND);

	            if (renderEffect && stack.hasEffect(0))
	            {
	                renderEffect(p_77015_2_, p_77015_4_, p_77015_5_);
	            }
	            GL11.glEnable(GL11.GL_LIGHTING);
	        }*/

		GL11.glEnable(GL11.GL_CULL_FACE);
	}

}
