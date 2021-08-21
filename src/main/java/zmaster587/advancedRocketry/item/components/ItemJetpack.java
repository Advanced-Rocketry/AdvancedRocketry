package zmaster587.advancedRocketry.item.components;

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
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.opengl.GL11;
import zmaster587.advancedRocketry.AdvancedRocketry;
import zmaster587.advancedRocketry.api.ARConfiguration;
import zmaster587.advancedRocketry.api.AdvancedRocketryFluids;
import zmaster587.advancedRocketry.api.AdvancedRocketryItems;
import zmaster587.advancedRocketry.event.RocketEventHandler;
import zmaster587.advancedRocketry.inventory.TextureResources;
import zmaster587.libVulpes.api.IArmorComponent;
import zmaster587.libVulpes.api.IJetPack;
import zmaster587.libVulpes.api.IModularArmor;
import zmaster587.libVulpes.client.ResourceIcon;
import zmaster587.libVulpes.util.FluidUtils;
import zmaster587.libVulpes.util.InputSyncHandler;

import javax.annotation.Nonnull;
import java.util.List;

public class ItemJetpack extends Item implements IArmorComponent, IJetPack {
	
	private enum MODES {
		NORMAL,
		HOVER
	}

	public ItemJetpack() {
	}
	

	private ResourceLocation background = TextureResources.rocketHud;

	@Override
	public void onTick(World world, EntityPlayer player,
					   @Nonnull ItemStack armorStack, IInventory inv, @Nonnull ItemStack componentStack) {

		if(player.capabilities.isCreativeMode) {
			return;
		}

		int speedUpgrades = 0;
		boolean allowsHover = false;

		ItemStack helm = player.getItemStackFromSlot(EntityEquipmentSlot.HEAD);
		if(!helm.isEmpty() && helm.getItem() instanceof IModularArmor) {
			List<ItemStack> helmInv = ((IModularArmor)helm.getItem()).getComponents(helm);
			for(ItemStack stack : helmInv) {
				if(!stack.isEmpty()) {
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

		
		//Apply speed upgrades only if the player isn't using Elytra
		if(!player.isElytraFlying())
		{
			player.motionX *= 1 + speedUpgrades*0.02f;
			player.motionZ *= 1 + speedUpgrades*0.02f;
		}
		
		// If the move
		if(hasModeSwitched(componentStack))
			player.capabilities.isFlying = false;

		if(isEnabled(componentStack)) {
			if(mode == MODES.HOVER) {
				if(!allowsHover)
					changeMode(componentStack, inv, player);
				
				if(!hasFuel(inv))
				{
					player.capabilities.isFlying = false;
				}
				else if (InputSyncHandler.isSpaceDown(player))
				{
					onAccelerate(componentStack, inv, player);
					setHeight(componentStack, (int)player.posY + player.height);
				}
				else if ((isActive || player.isSneaking()) && !player.onGround) {
					setHeight(componentStack, (int)player.posY + player.height);
					
					if(player.motionY < -0.6)
						onAccelerate(componentStack, inv, player);
				}
				else if(player.posY < getHeight(componentStack)) {
					onAccelerate(componentStack, inv, player);
					
					if( player.motionY < 0.1 && player.motionY > -0.1)
						player.motionY *= 0.01;
				}
				
			}
			else if(isActive) {
				onAccelerate(componentStack, inv, player);
			}
		}
		else if(mode == MODES.HOVER)
			if(!isActive)
				player.capabilities.isFlying = false;
	}


	@Override
	public boolean onComponentAdded(World world, @Nonnull ItemStack armorStack) {
		return true;
	}

	@Override
	public void onComponentRemoved(World world, @Nonnull ItemStack armorStack) {

	}

	@Override
	public void onArmorDamaged(EntityLivingBase entity, @Nonnull ItemStack armorStack,
							   @Nonnull ItemStack componentStack, DamageSource source, int damage) {
	}

	@Override
	public boolean isActive(@Nonnull ItemStack stack, EntityPlayer player) {
		return InputSyncHandler.isSpaceDown(player);
	}

	@Override
	public boolean isEnabled(@Nonnull ItemStack stack) {
		return stack.hasTagCompound() && stack.getTagCompound().getBoolean("enabled");
	}

	@Override
	public void setEnabledState(@Nonnull ItemStack stack, boolean state) {
		NBTTagCompound nbt;
		if(stack.hasTagCompound()) {
			nbt = stack.getTagCompound();
			nbt.setBoolean("enabled", state);
			flagModeSwitched(stack);
		}
		else if(state) {
			nbt = new NBTTagCompound();
			//noinspection ConstantConditions
			nbt.setBoolean("enabled", state);
			stack.setTagCompound(nbt);
			flagModeSwitched(stack);
		}
	}

	boolean hasFuel(IInventory inv)
	{
		boolean hasFuel = false;

		for(int i = 0; i < inv.getSizeInventory(); i++) {
			ItemStack fuelTank = inv.getStackInSlot(i);

			if(FluidUtils.containsFluid(fuelTank, AdvancedRocketryFluids.fluidHydrogen)) {
				hasFuel = FluidUtils.getFluidHandler(fuelTank).drain(1,true) != null;
				if(hasFuel)
					break;
			}

		}
		return hasFuel;
	}
	
	@Override
	public void onAccelerate(@Nonnull ItemStack stack, IInventory inv, EntityPlayer player) {
		boolean hasFuel = hasFuel(inv);

		MODES mode = getMode(stack);

		if(hasFuel) {

			player.addVelocity(0, (double)ARConfiguration.getCurrentConfig().jetPackThrust*0.1f, 0);
			if(player.world.isRemote) {
				double xPos;
				double zPos;
				float playerRot = (float) ((Math.PI/180f)*(player.rotationYaw - 55));
				xPos = player.posX + MathHelper.cos(playerRot)*.4f;
				zPos = player.posZ + MathHelper.sin(playerRot)*.4f;
				
				float ejectSpeed = mode == MODES.HOVER ? 0.1f : 0.3f;
				//AdvancedRocketry.proxy.spawnParticle("smallRocketFlame", player.worldObj, xPos, player.posY - 0.75, zPos, (player.worldObj.rand.nextFloat() - 0.5f)/18f,-.1 ,(player.worldObj.rand.nextFloat() - 0.5f)/18f);

				AdvancedRocketry.proxy.spawnParticle("smallRocketFlame", player.world, xPos, player.posY + 0.75, zPos, 0, player.motionY -ejectSpeed ,0);

				playerRot = (float) ((Math.PI/180f)*(player.rotationYaw - 125));
				xPos = player.posX + MathHelper.cos(playerRot)*.4f;
				zPos = player.posZ + MathHelper.sin(playerRot)*.4f;
				
				AdvancedRocketry.proxy.spawnParticle("smallRocketFlame", player.world, xPos, player.posY + 0.75, zPos, 0, player.motionY -ejectSpeed ,0);
			}

			if(player.motionY > -1) {
				player.fallDistance = 0;
			}
		}

	}

	@Override
	@SideOnly(Side.CLIENT)
	public ResourceIcon getComponentIcon(@Nonnull ItemStack armorStack) {
		
		return isEnabled(armorStack) ? getMode(armorStack) == MODES.HOVER ? new ResourceIcon(TextureResources.jetpackIconHover) : new ResourceIcon(TextureResources.jetpackIconEnabled) : new ResourceIcon(TextureResources.jetpackIconDisabled);
	}

	private MODES getMode(@Nonnull ItemStack stack) {
		if(stack.hasTagCompound())
			return MODES.values()[stack.getTagCompound().getInteger("mode")];

		return MODES.values()[0];
	}

	private void setHeight(@Nonnull ItemStack stack, float height) {
		if(stack.hasTagCompound())
			stack.getTagCompound().setFloat("height", height);
		else {
			NBTTagCompound nbt = new NBTTagCompound();
			nbt.setFloat("height", height);
			stack.setTagCompound(nbt);
		}
	}

	private float getHeight(@Nonnull ItemStack stack) {
		if(stack.hasTagCompound())
			return stack.getTagCompound().getFloat("height");
		return 0;
	}

	@Override
	public void changeMode(@Nonnull ItemStack stack, IInventory modules, EntityPlayer player) {
		NBTTagCompound nbt;
		int mode = 0;

		ItemStack helm = player.getItemStackFromSlot(EntityEquipmentSlot.HEAD);
		if(!helm.isEmpty() && helm.getItem() instanceof IModularArmor) {
			List<ItemStack> helmInv = ((IModularArmor)helm.getItem()).getComponents(helm);
			for(ItemStack helmStack : helmInv) 
				if (!stack.isEmpty() && helmStack.getItem() == AdvancedRocketryItems.itemUpgrade && helmStack.getItemDamage() == 0) {
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
		} else {
			nbt = new NBTTagCompound();
			nbt.setInteger("mode", mode);
			stack.setTagCompound(nbt);
		}
		flagModeSwitched(stack);

		if(mode == MODES.HOVER.ordinal())
			setHeight(stack, (float)player.posY + player.height);
	}
	
	private void flagModeSwitched(@Nonnull ItemStack stack) {
		NBTTagCompound nbt;
		if(stack.hasTagCompound()) {
			nbt = stack.getTagCompound();

			nbt.setBoolean("modeSwitch", true);
		}
		else {
			nbt = new NBTTagCompound();
			nbt.setBoolean("modeSwitch", true);
			stack.setTagCompound(nbt);
		}
	}
	
	
	private boolean hasModeSwitched(@Nonnull ItemStack stack) {
		NBTTagCompound nbt;
		if(stack.hasTagCompound() && stack.getTagCompound().hasKey("modeSwitch")) {
			nbt = stack.getTagCompound();

			boolean hasSwitched = nbt.getBoolean("modeSwitch");
			
			nbt.setBoolean("modeSwitch", false);
			return hasSwitched;
		}
		return false;
	}

	@Override
	public boolean isAllowedInSlot(@Nonnull ItemStack stack, EntityEquipmentSlot slot) {
		return slot == EntityEquipmentSlot.CHEST;
	}


	@Override
	@SideOnly(Side.CLIENT)
	public void renderScreen(@Nonnull ItemStack componentStack, List<ItemStack> modules, RenderGameOverlayEvent event, Gui gui) {
		int amt = 0, maxAmt = 0;
		for (ItemStack currentStack : modules) {
			if (FluidUtils.containsFluid(currentStack, AdvancedRocketryFluids.fluidHydrogen)) {
				FluidStack fluidStack = FluidUtils.getFluidForItem(currentStack);
				if (fluidStack != null)
					amt += fluidStack.amount;
				maxAmt += FluidUtils.getFluidItemCapacity(currentStack);
			}
			
			/*if(!currentStack.isEmpty() && currentStack.getItem() instanceof IFluidContainerItem ) {
				FluidStack fluid = ((IFluidContainerItem)currentStack.getItem()).getFluid(currentStack);
				if(fluid == null)
					maxAmt += ((IFluidContainerItem)currentStack.getItem()).getCapacity(currentStack);
				else if(fluid.getFluid() == AdvancedRocketryFluids.fluidHydrogen) {
					maxAmt += ((IFluidContainerItem)currentStack.getItem()).getCapacity(currentStack);
					amt += fluid.amount;
				}
			}*/
		}

		if(maxAmt > 0) {
			float size = amt/(float)maxAmt;

			Minecraft.getMinecraft().renderEngine.bindTexture(background);
			GL11.glColor3f(1f, 1f, 1f);
			int width = 83;
			int screenX = RocketEventHandler.hydrogenBar.getRenderX();
			int screenY = RocketEventHandler.hydrogenBar.getRenderY();

			//Draw BG
			gui.drawTexturedModalRect(screenX, screenY, 23, 34, width, 17);
			gui.drawTexturedModalRect(screenX , screenY, 23, 51, (int)(width*size), 17);
		}
	}
}
