package zmaster587.advancedRocketry.item;

import java.util.List;

import org.lwjgl.opengl.GL11;

import zmaster587.advancedRocketry.atmosphere.AtmosphereHandler;
import zmaster587.advancedRocketry.atmosphere.AtmosphereType;
import zmaster587.advancedRocketry.inventory.TextureResources;
import zmaster587.libVulpes.LibVulpes;
import zmaster587.libVulpes.api.IArmorComponent;
import zmaster587.libVulpes.client.ResourceIcon;
import zmaster587.libVulpes.render.RenderHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.DamageSource;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.client.event.RenderGameOverlayEvent;

public class ItemAtmosphereAnalzer extends Item implements IArmorComponent {

	private static ResourceIcon icon;
	private static ResourceLocation eyeCandySpinner = new ResourceLocation("advancedrocketry:textures/gui/eyeCandy/spinnyThing.png");

	@Override
	public void onTick(World world, EntityPlayer player, ItemStack armorStack,
			IInventory modules, ItemStack componentStack) {

	}

	private String[] getAtmosphereReadout(ItemStack stack, AtmosphereType atm) {
		if(atm == null)
			atm = AtmosphereType.AIR;

		String str[] = new String[2];

		str[0] = "Atmosphere Type: " + LibVulpes.proxy.getLocalizedString(atm.getUnlocalizedName());
		str[1] = "Breathable: " + (atm.isBreathable() ? "Yes" : "No");

		return str;
	}

	@Override
	public ItemStack onItemRightClick(ItemStack stack, World world,
			EntityPlayer player) {
		if(!world.isRemote) {
			String str[] = getAtmosphereReadout(stack, (AtmosphereType) AtmosphereHandler.getOxygenHandler(world.provider.dimensionId).getAtmosphereType(player));
			for(String str1 : str)
					player.addChatMessage(new ChatComponentText(str1));
		}
		return super.onItemRightClick(stack, world, player);
	}

	@Override
	public boolean onComponentAdded(World world, ItemStack armorStack) {
		return true;
	}

	@Override
	public void onComponentRemoved(World world, ItemStack armorStack) {
	}

	@Override
	public void onArmorDamaged(EntityLivingBase entity, ItemStack armorStack,
			ItemStack componentStack, DamageSource source, int damage) {

	}

	@Override
	public boolean isAllowedInSlot(ItemStack componentStack, int targetSlot) {
		return targetSlot == 0;
	}

	@Override
	public void renderScreen(ItemStack componentStack, List<ItemStack> modules,
			RenderGameOverlayEvent event, Gui gui) {
		
		FontRenderer fontRenderer = Minecraft.getMinecraft().fontRenderer;
		
		int screenX = 8;
		int screenY = event.resolution.getScaledHeight() - fontRenderer.FONT_HEIGHT*3;

		String str[] = getAtmosphereReadout(componentStack, (AtmosphereType) AtmosphereHandler.currentAtm);
		//Draw BG
		gui.drawString(fontRenderer, str[0], screenX, screenY, 0xaaffff);
		gui.drawString(fontRenderer, str[1], screenX, screenY + fontRenderer.FONT_HEIGHT*4/3, 0xaaffff);
	
		//Render Eyecandy
		GL11.glColor3f(1f, 1f, 1f);
		GL11.glPushMatrix();
		Minecraft.getMinecraft().renderEngine.bindTexture(eyeCandySpinner);
		GL11.glTranslatef(20, screenY + 8, 0);
		GL11.glRotatef(( System.currentTimeMillis() / 100 ) % 360, 0, 0, 1);
		
		Tessellator.instance.startDrawingQuads();
		RenderHelper.renderNorthFaceWithUV(Tessellator.instance, -1, -16,  -16, 16,  16, 0, 1, 0, 1);
		Tessellator.instance.draw();
		GL11.glPopMatrix();
		
		
		Minecraft.getMinecraft().renderEngine.bindTexture(TextureResources.frameHUDBG);
		Tessellator.instance.startDrawingQuads();
		RenderHelper.renderNorthFaceWithUV(Tessellator.instance, -1, 0,  screenY - 12, 16,  screenY + 26, 0, 0.25f, 0, 1);
		RenderHelper.renderNorthFaceWithUV(Tessellator.instance, -1, 16,  screenY - 12, 128,  screenY + 26, 0.5f, 0.5f, 0, 1);
		RenderHelper.renderNorthFaceWithUV(Tessellator.instance, -1, 128,  screenY - 12, 144,  screenY + 26, 0.75f, 1f, 0, 1);
		Tessellator.instance.draw();
	}

	@Override
	public ResourceIcon getComponentIcon(ItemStack armorStack) {
		if(icon == null)
			this.icon = new ResourceIcon(TextureMap.locationItemsTexture, this.getIcon(armorStack, 0));

		return this.icon;
	}

}
