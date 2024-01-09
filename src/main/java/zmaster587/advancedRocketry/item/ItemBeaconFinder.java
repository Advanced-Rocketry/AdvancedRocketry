package zmaster587.advancedRocketry.item;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.opengl.GL11;
import zmaster587.advancedRocketry.dimension.DimensionManager;
import zmaster587.libVulpes.api.IArmorComponent;
import zmaster587.libVulpes.client.ResourceIcon;
import zmaster587.libVulpes.inventory.TextureResources;
import zmaster587.libVulpes.render.RenderHelper;
import zmaster587.libVulpes.util.HashedBlockPosition;

import org.jetbrains.annotations.NotNull;
import java.util.List;

public class ItemBeaconFinder extends Item implements IArmorComponent {

	@Override
	public void onTick(World world, EntityPlayer player, @NotNull ItemStack armorStack, IInventory modules, @NotNull ItemStack componentStack) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean onComponentAdded(World world, @NotNull ItemStack armorStack) {
		return true;
	}

	@Override
	public void onComponentRemoved(World world, @NotNull ItemStack armorStack) {
	}

	@Override
	public void onArmorDamaged(EntityLivingBase entity, @NotNull ItemStack armorStack, @NotNull ItemStack componentStack, DamageSource source, int damage) {
	}

	@Override
	public boolean isAllowedInSlot(@NotNull ItemStack componentStack, EntityEquipmentSlot armorType) {
		return armorType == EntityEquipmentSlot.HEAD;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void renderScreen(@NotNull ItemStack componentStack, List<ItemStack> modules, RenderGameOverlayEvent event, Gui gui) {
		
		int dimid = Minecraft.getMinecraft().world.provider.getDimension();
		
		if(DimensionManager.getInstance().isDimensionCreated(dimid)) {
			for(HashedBlockPosition pos : DimensionManager.getInstance().getDimensionProperties(dimid).getBeacons()) {
				
				GL11.glPushMatrix();
				
				double deltaX = Minecraft.getMinecraft().player.posX - pos.x;
				double deltaZ = Minecraft.getMinecraft().player.posZ - pos.z;
				
				double angle = MathHelper.wrapDegrees(MathHelper.atan2(deltaZ, deltaX)*180/Math.PI + 90 - Minecraft.getMinecraft().player.rotationYawHead);
				
				//GL11.glTranslatef(pos.x, pos.y, pos.z);
				GL11.glTranslated((event.getResolution().getScaledWidth_double()*angle/180f) + event.getResolution().getScaledWidth()/2f,0,5);
				//GL11.glDepthMask(false);
				//GL11.glDisable(GL11.GL_TEXTURE_2D);
				Minecraft.getMinecraft().renderEngine.bindTexture(TextureResources.buttonDown[0]);
				
				GlStateManager.color(0.5f, 0.5f, 1, 1);
				
		        Tessellator tessellator = Tessellator.getInstance();
		        BufferBuilder vertexbuffer = tessellator.getBuffer();
		        
		        vertexbuffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
		        RenderHelper.renderNorthFaceWithUV(vertexbuffer, -1000, -10, 0, 10, 20, 0, 1, 0, 1);
				tessellator.draw();
				
				//GL11.glDepthMask(true);
				//GL11.glEnable(GL11.GL_TEXTURE_2D);
				GlStateManager.color(1, 1, 1, 1);
				GL11.glPopMatrix();
				
			}
		}
	}

	@Override
	public ResourceIcon getComponentIcon(@NotNull ItemStack armorStack) {
		return null;
	}

}
