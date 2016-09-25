package zmaster587.advancedRocketry.item.components;

import java.lang.reflect.Field;
import java.util.List;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.DamageSource;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.IFluidContainerItem;
import net.minecraftforge.fml.relauncher.ReflectionHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import zmaster587.advancedRocketry.AdvancedRocketry;
import zmaster587.advancedRocketry.api.AdvancedRocketryFluids;
import zmaster587.advancedRocketry.api.AdvancedRocketryItems;
import zmaster587.advancedRocketry.inventory.TextureResources;
import zmaster587.libVulpes.api.IArmorComponent;
import zmaster587.libVulpes.api.IJetPack;
import zmaster587.libVulpes.api.IModularArmor;
import zmaster587.libVulpes.client.ResourceIcon;
import zmaster587.libVulpes.util.InputSyncHandler;

public class ItemJetpack extends Item implements IArmorComponent, IJetPack {

	Field flySpeed;
	
	private static enum MODES {
		NORMAL,
		HOVER;
	}

	public ItemJetpack() {
		flySpeed = ReflectionHelper.findField(net.minecraft.entity.player.PlayerCapabilities.class, "flySpeed", "field_75096_f");
		flySpeed.setAccessible(true);
	}
	
	private static final ResourceIcon jetpackHover = new ResourceIcon(TextureResources.jetpackIconHover);
	private static final ResourceIcon jetpackEnabled = new ResourceIcon(TextureResources.jetpackIconEnabled);
	private static final ResourceIcon jetpackDisabled = new ResourceIcon(TextureResources.jetpackIconDisabled);
	private ResourceLocation background = TextureResources.rocketHud;

	@Override
	public void onTick(World world, EntityPlayer player,
			ItemStack armorStack, IInventory inv, ItemStack componentStack) {

		if(player.capabilities.isCreativeMode) {
			try {
				flySpeed.setFloat(player.capabilities, 0.05f);
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			}
			return;
		}

		int speedUpgrades = 1;
		boolean allowsHover = false;

		ItemStack helm = player.getItemStackFromSlot(EntityEquipmentSlot.HEAD);
		if(helm != null && helm.getItem() instanceof IModularArmor) {
			List<ItemStack> helmInv = ((IModularArmor)helm.getItem()).getComponents(helm);
			for(ItemStack stack : helmInv) {
				if(stack != null) {
					Item item = stack.getItem();

					if (item == AdvancedRocketryItems.itemUpgrade)
						if(stack.getItemDamage() == 0)
							allowsHover = true;
						else if(stack.getItemDamage() == 1)
							speedUpgrades++;
				}
			}
		}

		MODES mode = getMode(componentStack);
		boolean isActive = isActive(componentStack, player);

		try {
			flySpeed.setFloat(player.capabilities, speedUpgrades*0.02f);
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
		//ObfuscationReflectionHelper.setPrivateValue(net.minecraft.entity.player.PlayerCapabilities.class, player.capabilities, speedUpgrades*0.02f, "flySpeed");
		player.capabilities.isFlying = false;

		if(isEnabled(componentStack)) {
			if(mode == MODES.HOVER) {
				if(!allowsHover)
					changeMode(componentStack, inv, player);

				if((isActive || player.isSneaking()) && !player.onGround)
					setHeight(componentStack, (int)player.posY + player.height);

				onAccelerate(componentStack, inv, player);
			}
			else if(isActive) {
				onAccelerate(componentStack, inv, player);
			}
		}
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
	public boolean isActive(ItemStack stack, EntityPlayer player) {
		return InputSyncHandler.isSpaceDown(player);
	}

	@Override
	public boolean isEnabled(ItemStack stack) {
		return stack.hasTagCompound() && stack.getTagCompound().getBoolean("enabled");
	}

	@Override
	public void setEnabledState(ItemStack stack, boolean state) {
		NBTTagCompound nbt;
		if(stack.hasTagCompound()) {
			nbt = stack.getTagCompound();
			nbt.setBoolean("enabled", state);
		}
		else if(state) {
			nbt = new NBTTagCompound();
			nbt.setBoolean("enabled", state);
			stack.setTagCompound(nbt);
		}
	}

	@Override
	public void onAccelerate(ItemStack stack, IInventory inv, EntityPlayer player) {
		boolean hasFuel = false;

		MODES mode = getMode(stack);

		for(int i = 0; i < inv.getSizeInventory(); i++) {
			ItemStack fuelTank = inv.getStackInSlot(i);

			if(fuelTank != null && fuelTank.getItem() instanceof IFluidContainerItem) {
				FluidStack fluid = ((IFluidContainerItem)fuelTank.getItem()).drain(fuelTank, 1, false);
				if(fluid != null && fluid.getFluid() == AdvancedRocketryFluids.fluidHydrogen) {
					((IFluidContainerItem)fuelTank.getItem()).drain(fuelTank, 1, true);
					hasFuel = true;
					break;
				}
			}

		}

		if(hasFuel) {

			if(mode == MODES.HOVER)
				player.capabilities.isFlying = true;
			else 
				player.addVelocity(0, 0.1, 0);

			if(player.worldObj.isRemote) {
				double xPos = player.posX;
				double zPos = player.posZ;
				float playerRot = (float) ((Math.PI/180f)*(player.rotationYaw - 55));
				xPos = player.posX + MathHelper.cos(playerRot)*.4f;
				zPos = player.posZ + MathHelper.sin(playerRot)*.4f;
				
				float ejectSpeed = mode == MODES.HOVER ? 0.1f : 0.3f;
				//AdvancedRocketry.proxy.spawnParticle("smallRocketFlame", player.worldObj, xPos, player.posY - 0.75, zPos, (player.worldObj.rand.nextFloat() - 0.5f)/18f,-.1 ,(player.worldObj.rand.nextFloat() - 0.5f)/18f);

				AdvancedRocketry.proxy.spawnParticle("smallRocketFlame", player.worldObj, xPos, player.posY - 0.75, zPos, 0, player.motionY -ejectSpeed ,0);

				playerRot = (float) ((Math.PI/180f)*(player.rotationYaw - 125));
				xPos = player.posX + MathHelper.cos(playerRot)*.4f;
				zPos = player.posZ + MathHelper.sin(playerRot)*.4f;
				
				AdvancedRocketry.proxy.spawnParticle("smallRocketFlame", player.worldObj, xPos, player.posY - 0.75, zPos, 0, player.motionY -ejectSpeed ,0);
			}

			if(player.motionY > -1) {
				player.fallDistance = 0;
			}
		}

	}

	@Override
	@SideOnly(Side.CLIENT)
	public ResourceIcon getComponentIcon(ItemStack armorStack) {
		return isEnabled(armorStack) ? getMode(armorStack) == MODES.HOVER ? jetpackHover : jetpackEnabled : jetpackDisabled;
	}

	private MODES getMode(ItemStack stack) {
		if(stack.hasTagCompound())
			return MODES.values()[stack.getTagCompound().getInteger("mode")];

		return MODES.values()[0];
	}

	private void setHeight(ItemStack stack, float height) {
		if(stack.hasTagCompound())
			stack.getTagCompound().setFloat("height", height);
		else {
			NBTTagCompound nbt = new NBTTagCompound();
			nbt.setFloat("height", height);
			stack.setTagCompound(nbt);
		}
	}

	private float getHeight(ItemStack stack) {
		if(stack.hasTagCompound())
			return stack.getTagCompound().getFloat("height");
		return 0;
	}

	@Override
	public void changeMode(ItemStack stack, IInventory modules, EntityPlayer player) {
		NBTTagCompound nbt;
		int mode = 0;

		ItemStack helm = player.getItemStackFromSlot(EntityEquipmentSlot.HEAD);
		if(helm != null && helm.getItem() instanceof IModularArmor) {
			List<ItemStack> helmInv = ((IModularArmor)helm.getItem()).getComponents(helm);
			for(ItemStack helmStack : helmInv) 
				if (stack != null && helmStack.getItem() == AdvancedRocketryItems.itemUpgrade && helmStack.getItemDamage() == 0) {
					mode = 1;
					break;
				}
		}


		if(stack.hasTagCompound()) {
			nbt = stack.getTagCompound();
			if(mode == 1) {
				mode = nbt.getInteger("mode");
				mode++;
				if(mode >= MODES.values().length)
					mode =0;
			}

			nbt.setInteger("mode", mode);
		}
		else {
			nbt = new NBTTagCompound();
			nbt.setInteger("mode", mode);
			stack.setTagCompound(nbt);
		}

		if(mode == MODES.HOVER.ordinal())
			setHeight(stack, (float)player.posY + player.height);
	}

	@Override
	public boolean isAllowedInSlot(ItemStack stack, EntityEquipmentSlot slot) {
		return slot == EntityEquipmentSlot.CHEST;
	}


	@Override
	@SideOnly(Side.CLIENT)
	public void renderScreen(ItemStack componentStack, List<ItemStack> modules, RenderGameOverlayEvent event, Gui gui) {
		List<ItemStack> inv = modules;

		int amt = 0, maxAmt = 0;
		for(int i = 0; i < inv.size(); i++) {
			ItemStack currentStack = inv.get(i);

			if(currentStack != null && currentStack.getItem() instanceof IFluidContainerItem ) {
				FluidStack fluid = ((IFluidContainerItem)currentStack.getItem()).getFluid(currentStack);
				if(fluid == null)
					maxAmt += ((IFluidContainerItem)currentStack.getItem()).getCapacity(currentStack);
				else if(fluid.getFluid() == AdvancedRocketryFluids.fluidHydrogen) {
					maxAmt += ((IFluidContainerItem)currentStack.getItem()).getCapacity(currentStack);
					amt += fluid.amount;
				}
			}
		}

		if(maxAmt > 0) {
			float size = amt/(float)maxAmt;

			Minecraft.getMinecraft().renderEngine.bindTexture(background);
			GL11.glColor3f(1f, 1f, 1f);
			int width = 83;
			int screenX = event.getResolution().getScaledWidth()/2 + 8;
			int screenY = event.getResolution().getScaledHeight() - 74;

			//Draw BG
			gui.drawTexturedModalRect(screenX, screenY, 23, 34, width, 17);
			gui.drawTexturedModalRect(screenX , screenY, 23, 51, (int)(width*size), 17);
		}
	}
}
