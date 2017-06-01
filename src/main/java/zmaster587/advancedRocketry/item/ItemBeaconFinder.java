package zmaster587.advancedRocketry.item;

import java.util.List;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import zmaster587.advancedRocketry.dimension.DimensionManager;
import zmaster587.libVulpes.api.IArmorComponent;
import zmaster587.libVulpes.client.ResourceIcon;
import zmaster587.libVulpes.inventory.TextureResources;
import zmaster587.libVulpes.render.RenderHelper;
import zmaster587.libVulpes.util.BlockPosition;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.client.event.RenderGameOverlayEvent;

public class ItemBeaconFinder extends Item implements IArmorComponent {

	ResourceIcon icon;
	
	@Override
	public void onTick(World world, EntityPlayer player, ItemStack armorStack,
			IInventory modules, ItemStack componentStack) {
		// TODO Auto-generated method stub
		
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
	@SideOnly(Side.CLIENT)
	public void renderScreen(ItemStack componentStack, List<ItemStack> modules,
			RenderGameOverlayEvent event, Gui gui) {
		
		int dimid = Minecraft.getMinecraft().theWorld.provider.dimensionId;
		
		if(DimensionManager.getInstance().isDimensionCreated(dimid)) {
			for(BlockPosition pos : DimensionManager.getInstance().getDimensionProperties(dimid).getBeacons()) {
				
				GL11.glPushMatrix();
				
				double deltaX = Minecraft.getMinecraft().thePlayer.posX - pos.x;
				double deltaZ = Minecraft.getMinecraft().thePlayer.posZ - pos.z;
				
				double angle = MathHelper.wrapAngleTo180_double(Math.atan2(deltaZ, deltaX)*180/Math.PI + 90 - Minecraft.getMinecraft().thePlayer.rotationYawHead);
				
				//GL11.glTranslatef(pos.x, pos.y, pos.z);
				GL11.glTranslated((event.resolution.getScaledWidth_double()*angle/180f) + event.resolution.getScaledWidth()/2,0,5);
				//GL11.glDepthMask(false);
				//GL11.glDisable(GL11.GL_TEXTURE_2D);
				Minecraft.getMinecraft().renderEngine.bindTexture(TextureResources.buttonDown[0]);
				
				GL11.glColor4f(0.5f, 0.5f, 1, 1);
				
		        Tessellator tessellator = Tessellator.instance;
		        
		        tessellator.startDrawingQuads();
		        RenderHelper.renderNorthFaceWithUV(tessellator, -1000, -10, 0, 10, 20, 0, 1, 0, 1);
				tessellator.draw();
				
				//GL11.glDepthMask(true);
				//GL11.glEnable(GL11.GL_TEXTURE_2D);
				GL11.glColor4f(1, 1, 1, 1);
				GL11.glPopMatrix();
				
			}
		}
	}

	@Override
	public ResourceIcon getComponentIcon(ItemStack armorStack) {
		if(icon == null)
			this.icon = new ResourceIcon(TextureMap.locationItemsTexture, this.getIcon(armorStack, 0));
		
		return this.icon;
	}

}
