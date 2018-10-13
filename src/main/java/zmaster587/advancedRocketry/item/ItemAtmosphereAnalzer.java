package zmaster587.advancedRocketry.item;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentBase;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.opengl.GL11;
import zmaster587.advancedRocketry.atmosphere.AtmosphereHandler;
import zmaster587.advancedRocketry.atmosphere.AtmosphereType;
import zmaster587.advancedRocketry.dimension.DimensionManager;
import zmaster587.advancedRocketry.event.RocketEventHandler;
import zmaster587.advancedRocketry.inventory.TextureResources;
import zmaster587.libVulpes.LibVulpes;
import zmaster587.libVulpes.api.IArmorComponent;
import zmaster587.libVulpes.client.ResourceIcon;
import zmaster587.libVulpes.render.RenderHelper;

import java.util.LinkedList;
import java.util.List;

public class ItemAtmosphereAnalzer extends Item implements IArmorComponent {

	private static ResourceIcon icon;
	private static ResourceLocation eyeCandySpinner = new ResourceLocation("advancedrocketry:textures/gui/eyeCandy/spinnyThing.png");
	
	private static String breathable = LibVulpes.proxy.getLocalizedString("msg.atmanal.canbreathe");
	private static String atmtype = LibVulpes.proxy.getLocalizedString("msg.atmanal.atmType");
	private static String yes = LibVulpes.proxy.getLocalizedString("msg.yes");
	private static String no = LibVulpes.proxy.getLocalizedString("msg.no");

	@Override
	public void onTick(World world, EntityPlayer player, ItemStack armorStack,
			IInventory modules, ItemStack componentStack) {

	}

	private List<ITextComponent> getAtmosphereReadout(ItemStack stack, AtmosphereType atm, World world) {
		if(atm == null)
			atm = AtmosphereType.AIR;
		

		List<ITextComponent> str = new LinkedList<ITextComponent>();
		
		str.add(new TextComponentTranslation("%s %s %s",
				new TextComponentTranslation("msg.atmanal.atmtype"),
				new TextComponentTranslation(atm.getUnlocalizedName()),
				new TextComponentString((AtmosphereHandler.currentPressure == -1 ? (DimensionManager.getInstance().isDimensionCreated(world.provider.getDimension()) ? DimensionManager.getInstance().getDimensionProperties(world.provider.getDimension()).getAtmosphereDensity()/100f : 1) : AtmosphereHandler.currentPressure/100f) + " atm")
				));
		str.add(new TextComponentTranslation("%s %s", 
				new TextComponentTranslation("msg.atmanal.canbreathe"),
				atm.isBreathable() ? new TextComponentTranslation("msg.yes") : new TextComponentTranslation("msg.no")));
		
		return str;
	}

	@Override
	public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn, EnumHand hand) {
		ItemStack stack = playerIn.getHeldItem(hand);
		if(!worldIn.isRemote) {
			List<ITextComponent> str = getAtmosphereReadout(stack, (AtmosphereType) AtmosphereHandler.getOxygenHandler(worldIn.provider.getDimension()).getAtmosphereType(playerIn),worldIn);
			for(ITextComponent str1 : str)
				playerIn.sendMessage(str1);
		}
		return super.onItemRightClick(worldIn, playerIn, hand);
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
	public boolean isAllowedInSlot(ItemStack componentStack, EntityEquipmentSlot targetSlot) {
		return targetSlot == EntityEquipmentSlot.HEAD;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void renderScreen(ItemStack componentStack, List<ItemStack> modules,
			RenderGameOverlayEvent event, Gui gui) {
		
		FontRenderer fontRenderer = Minecraft.getMinecraft().fontRenderer;
		
		int screenX = RocketEventHandler.atmBar.getRenderX();//8;
		int screenY = RocketEventHandler.atmBar.getRenderY();//event.getResolution().getScaledHeight() - fontRenderer.FONT_HEIGHT*3;

		List<ITextComponent> str = getAtmosphereReadout(componentStack, (AtmosphereType) AtmosphereHandler.currentAtm, Minecraft.getMinecraft().world);
		//Draw BG
		gui.drawString(fontRenderer, str.get(0).getFormattedText(), screenX, screenY, 0xaaffff);
		gui.drawString(fontRenderer, str.get(1).getFormattedText(), screenX, screenY + fontRenderer.FONT_HEIGHT*4/3, 0xaaffff);
	
		//Render Eyecandy
		GL11.glColor3f(1f, 1f, 1f);
		GL11.glPushMatrix();
		Minecraft.getMinecraft().renderEngine.bindTexture(eyeCandySpinner);
		GL11.glTranslatef(screenX + 12, screenY + 8, 0);
		GL11.glRotatef(( System.currentTimeMillis() / 100 ) % 360, 0, 0, 1);
		
		BufferBuilder buffer = Tessellator.getInstance().getBuffer();
		
		buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
		RenderHelper.renderNorthFaceWithUV(buffer, -1, -16,  -16, 16,  16, 0, 1, 0, 1);
		Tessellator.getInstance().draw();
		GL11.glPopMatrix();
		
		
		Minecraft.getMinecraft().renderEngine.bindTexture(TextureResources.frameHUDBG);
		buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
		RenderHelper.renderNorthFaceWithUV(buffer, -1, screenX - 8,  screenY - 12, screenX + 8,  screenY + 26, 0, 0.25f, 0, 1);
		RenderHelper.renderNorthFaceWithUV(buffer, -1, screenX + 8,  screenY - 12, screenX + 212,  screenY + 26, 0.5f, 0.5f, 0, 1);
		RenderHelper.renderNorthFaceWithUV(buffer, -1, screenX + 212,  screenY - 12, screenX + 228,  screenY + 26, 0.75f, 1f, 0, 1);
		Tessellator.getInstance().draw();
	}

	@Override
	public ResourceIcon getComponentIcon(ItemStack armorStack) {
		return null;
	}

}
