package zmaster587.advancedRocketry.item;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.lwjgl.opengl.GL11;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.platform.GlStateManager;

import zmaster587.advancedRocketry.dimension.DimensionManager;
import zmaster587.libVulpes.api.IArmorComponent;
import zmaster587.libVulpes.client.ResourceIcon;
import zmaster587.libVulpes.inventory.TextureResources;
import zmaster587.libVulpes.render.RenderHelper;
import zmaster587.libVulpes.util.HashedBlockPosition;
import zmaster587.libVulpes.util.ZUtils;

import javax.annotation.Nonnull;
import java.util.List;

public class ItemBeaconFinder extends Item implements IArmorComponent {

	public ItemBeaconFinder(Properties properties) {
		super(properties);
	}

	@Override
<<<<<<< HEAD
	public void onTick(World world, PlayerEntity player, ItemStack armorStack,
			IInventory modules, ItemStack componentStack) {
=======
	public void onTick(World world, EntityPlayer player, @Nonnull ItemStack armorStack, IInventory modules, @Nonnull ItemStack componentStack) {
>>>>>>> origin/feature/nuclearthermalrockets
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean onComponentAdded(World world, @Nonnull ItemStack armorStack) {
		return true;
	}

	@Override
	public void onComponentRemoved(World world, @Nonnull ItemStack armorStack) {
	}

	@Override
<<<<<<< HEAD
	public void onArmorDamaged(LivingEntity entity, ItemStack armorStack,
			ItemStack componentStack, DamageSource source, int damage) {
	}

	@Override
	public boolean isAllowedInSlot(ItemStack componentStack,
			EquipmentSlotType armorType) {
		return armorType == EquipmentSlotType.HEAD;
	}

	@Override
	@OnlyIn(value=Dist.CLIENT)
	public void renderScreen(MatrixStack matrix, ItemStack componentStack, List<ItemStack> modules, RenderGameOverlayEvent event,
			Screen gui) {
=======
	public void onArmorDamaged(EntityLivingBase entity, @Nonnull ItemStack armorStack, @Nonnull ItemStack componentStack, DamageSource source, int damage) {
	}

	@Override
	public boolean isAllowedInSlot(@Nonnull ItemStack componentStack, EntityEquipmentSlot armorType) {
		return armorType == EntityEquipmentSlot.HEAD;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void renderScreen(@Nonnull ItemStack componentStack, List<ItemStack> modules, RenderGameOverlayEvent event, Gui gui) {
>>>>>>> origin/feature/nuclearthermalrockets
		
		ResourceLocation dimid = ZUtils.getDimensionIdentifier(Minecraft.getInstance().world);
		
		if(DimensionManager.getInstance().isDimensionCreated(dimid)) {
			for(HashedBlockPosition pos : DimensionManager.getInstance().getDimensionProperties(dimid).getBeacons()) {
				
				GL11.glPushMatrix();
				
				double deltaX = Minecraft.getInstance().player.getPosX() - pos.x;
				double deltaZ = Minecraft.getInstance().player.getPosZ() - pos.z;
				
				double angle = MathHelper.wrapDegrees(MathHelper.atan2(deltaZ, deltaX)*180/Math.PI + 90 - Minecraft.getInstance().player.rotationYawHead);
				
				//GL11.glTranslatef(pos.x, pos.y, pos.z);
<<<<<<< HEAD
				GL11.glTranslated((Minecraft.getInstance().getMainWindow().getScaledWidth()*angle/180f) + Minecraft.getInstance().getMainWindow().getScaledWidth()/2,0,5);
=======
				GL11.glTranslated((event.getResolution().getScaledWidth_double()*angle/180f) + event.getResolution().getScaledWidth()/2f,0,5);
>>>>>>> origin/feature/nuclearthermalrockets
				//GL11.glDepthMask(false);
				//GL11.glDisable(GL11.GL_TEXTURE_2D);
				Minecraft.getInstance().getTextureManager().bindTexture(TextureResources.buttonDown[0]);
				
				GlStateManager.color4f(0.5f, 0.5f, 1, 1);
				
		        Tessellator tessellator = Tessellator.getInstance();
		        BufferBuilder vertexbuffer = tessellator.getBuffer();
		        
		        vertexbuffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
		        RenderHelper.renderNorthFaceWithUV(matrix, vertexbuffer, -1000, -10, 0, 10, 20, 0, 1, 0, 1);
				tessellator.draw();
				
				//GL11.glDepthMask(true);
				//GL11.glEnable(GL11.GL_TEXTURE_2D);
				GlStateManager.color4f(1, 1, 1, 1);
				GL11.glPopMatrix();
				
			}
		}
	}

	@Override
	public ResourceIcon getComponentIcon(@Nonnull ItemStack armorStack) {
		return null;
	}

}
