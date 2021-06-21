package zmaster587.advancedRocketry.item.components;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.DamageSource;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler.FluidAction;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import org.lwjgl.opengl.GL11;

import com.mojang.blaze3d.matrix.MatrixStack;
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
<<<<<<< HEAD

	private static enum MODES {
=======
	
	private enum MODES {
>>>>>>> origin/feature/nuclearthermalrockets
		NORMAL,
		HOVER
	}

	public ItemJetpack(Properties props) {
		super(props);
	}


	private ResourceLocation background = TextureResources.rocketHud;

	@Override
<<<<<<< HEAD
	public void onTick(World world, PlayerEntity player,
			ItemStack armorStack, IInventory inv, ItemStack componentStack) {
=======
	public void onTick(World world, EntityPlayer player,
					   @Nonnull ItemStack armorStack, IInventory inv, @Nonnull ItemStack componentStack) {
>>>>>>> origin/feature/nuclearthermalrockets

		if(player.isCreative()) {
			return;
		}

		int speedUpgrades = 0;
		boolean allowsHover = false;

<<<<<<< HEAD
		ItemStack helm = player.getItemStackFromSlot(EquipmentSlotType.HEAD);
		if(helm != null && helm.getItem() instanceof IModularArmor) {
=======
		ItemStack helm = player.getItemStackFromSlot(EntityEquipmentSlot.HEAD);
		if(!helm.isEmpty() && helm.getItem() instanceof IModularArmor) {
>>>>>>> origin/feature/nuclearthermalrockets
			List<ItemStack> helmInv = ((IModularArmor)helm.getItem()).getComponents(helm);
			for(ItemStack stack : helmInv) {
				if(!stack.isEmpty()) {
					Item item = stack.getItem();

					if (item.getItem() == AdvancedRocketryItems.itemUpgradeHover)
						if(stack.getDamage() == 0)
							allowsHover = true;
						else if(stack.getItem() == AdvancedRocketryItems.itemUpgradeSpeed)
							speedUpgrades++;
				}
			}
		}

		MODES mode = getMode(componentStack);
		boolean isActive = isActive(componentStack, player);


		//Apply speed upgrades only if the player isn't using Elytra
		if(!player.isElytraFlying())
		{
			player.setMotion(player.getMotion().x + speedUpgrades*0.02f, player.getMotion().y, player.getMotion().z + speedUpgrades*0.02f);
		}

		// If the move
		if(hasModeSwitched(componentStack))
			player.abilities.isFlying = false;

		if(isEnabled(componentStack)) {
			if(mode == MODES.HOVER) {
				if(!allowsHover)
					changeMode(componentStack, inv, player);

				if(!hasFuel(inv))
				{
					player.abilities.isFlying = false;
				}
				else if (InputSyncHandler.isSpaceDown(player))
				{
					onAccelerate(componentStack, inv, player);
					setHeight(componentStack, (int)player.getPosY() + player.getHeight());
				}
				else if ((isActive || player.isSneaking()) && player.isAirBorne) {
					setHeight(componentStack, (int)player.getPosY() + player.getHeight());

					if(player.getMotion().y < -0.6)
						onAccelerate(componentStack, inv, player);
				}
				else if(player.getPosY() < getHeight(componentStack)) {
					onAccelerate(componentStack, inv, player);

					if( player.getMotion().y < 0.1 && player.getMotion().y > -0.1)
						player.setMotion(new Vector3d( player.getMotion().x, player.getMotion().y * 0.01, player.getMotion().z ));
				}

			}
			else if(isActive) {
				onAccelerate(componentStack, inv, player);
			}
		}
		else if(mode == MODES.HOVER)
			if(!isActive)
				player.abilities.isFlying = false;
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
	public boolean isActive(ItemStack stack, PlayerEntity player) {
=======
	public void onArmorDamaged(EntityLivingBase entity, @Nonnull ItemStack armorStack,
							   @Nonnull ItemStack componentStack, DamageSource source, int damage) {
	}

	@Override
	public boolean isActive(@Nonnull ItemStack stack, EntityPlayer player) {
>>>>>>> origin/feature/nuclearthermalrockets
		return InputSyncHandler.isSpaceDown(player);
	}

	@Override
<<<<<<< HEAD
	public boolean isEnabled(ItemStack stack) {
		return stack.hasTag() && stack.getTag().getBoolean("enabled");
	}

	@Override
	public void setEnabledState(ItemStack stack, boolean state) {
		CompoundNBT nbt;
		if(stack.hasTag()) {
			nbt = stack.getTag();
			nbt.putBoolean("enabled", state);
			flagModeSwitched(stack);
		}
		else if(state) {
			nbt = new CompoundNBT();
			nbt.putBoolean("enabled", state);
			stack.setTag(nbt);
=======
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
>>>>>>> origin/feature/nuclearthermalrockets
			flagModeSwitched(stack);
		}
	}

	boolean hasFuel(IInventory inv)
	{
		boolean hasFuel = false;

		for(int i = 0; i < inv.getSizeInventory(); i++) {
			ItemStack fuelTank = inv.getStackInSlot(i);

			if(FluidUtils.containsFluid(fuelTank, AdvancedRocketryFluids.hydrogenStill.get())) {
				hasFuel = FluidUtils.getFluidHandler(fuelTank).drain(1, FluidAction.EXECUTE) != null;
				if(hasFuel)
					break;
			}

		}
		return hasFuel;
	}

	@Override
<<<<<<< HEAD
	public void onAccelerate(ItemStack stack, IInventory inv, PlayerEntity player) {
=======
	public void onAccelerate(@Nonnull ItemStack stack, IInventory inv, EntityPlayer player) {
>>>>>>> origin/feature/nuclearthermalrockets
		boolean hasFuel = hasFuel(inv);

		MODES mode = getMode(stack);

		if(hasFuel) {

			player.addVelocity(0, (double)ARConfiguration.getCurrentConfig().jetPackThrust.get()*0.1f, 0);
			if(player.world.isRemote) {
<<<<<<< HEAD
				double xPos = player.getPosX();
				double zPos = player.getPosZ();
=======
				double xPos;
				double zPos;
>>>>>>> origin/feature/nuclearthermalrockets
				float playerRot = (float) ((Math.PI/180f)*(player.rotationYaw - 55));
				xPos = player.getPosX() + MathHelper.cos(playerRot)*.4f;
				zPos = player.getPosZ() + MathHelper.sin(playerRot)*.4f;

				float ejectSpeed = mode == MODES.HOVER ? 0.1f : 0.3f;
				//AdvancedRocketry.proxy.spawnParticle("smallRocketFlame", player.worldObj, xPos, player.posY - 0.75, zPos, (player.worldObj.rand.nextFloat() - 0.5f)/18f,-.1 ,(player.worldObj.rand.nextFloat() - 0.5f)/18f);

				AdvancedRocketry.proxy.spawnParticle("smallRocketFlame", player.world, xPos, player.getPosY() + 0.75, zPos, 0, player.getMotion().y -ejectSpeed ,0);

				playerRot = (float) ((Math.PI/180f)*(player.rotationYaw - 125));
				xPos = player.getPosX() + MathHelper.cos(playerRot)*.4f;
				zPos = player.getPosZ() + MathHelper.sin(playerRot)*.4f;

				AdvancedRocketry.proxy.spawnParticle("smallRocketFlame", player.world, xPos, player.getPosY() + 0.75, zPos, 0, player.getMotion().y -ejectSpeed ,0);
			}

			if(player.getMotion().y > -1) {
				player.fallDistance = 0;
			}
		}

	}

	@Override
<<<<<<< HEAD
	@OnlyIn(value=Dist.CLIENT)
	public ResourceIcon getComponentIcon(ItemStack armorStack) {

		return isEnabled(armorStack) ? getMode(armorStack) == MODES.HOVER ? new ResourceIcon(TextureResources.jetpackIconHover) : new ResourceIcon(TextureResources.jetpackIconEnabled) : new ResourceIcon(TextureResources.jetpackIconDisabled);
	}

	private MODES getMode(ItemStack stack) {
		if(stack.hasTag())
			return MODES.values()[stack.getTag().getInt("mode")];
=======
	@SideOnly(Side.CLIENT)
	public ResourceIcon getComponentIcon(@Nonnull ItemStack armorStack) {
		
		return isEnabled(armorStack) ? getMode(armorStack) == MODES.HOVER ? new ResourceIcon(TextureResources.jetpackIconHover) : new ResourceIcon(TextureResources.jetpackIconEnabled) : new ResourceIcon(TextureResources.jetpackIconDisabled);
	}

	private MODES getMode(@Nonnull ItemStack stack) {
		if(stack.hasTagCompound())
			return MODES.values()[stack.getTagCompound().getInteger("mode")];
>>>>>>> origin/feature/nuclearthermalrockets

		return MODES.values()[0];
	}

<<<<<<< HEAD
	private void setHeight(ItemStack stack, float height) {
		if(stack.hasTag())
			stack.getTag().putFloat("height", height);
=======
	private void setHeight(@Nonnull ItemStack stack, float height) {
		if(stack.hasTagCompound())
			stack.getTagCompound().setFloat("height", height);
>>>>>>> origin/feature/nuclearthermalrockets
		else {
			CompoundNBT nbt = new CompoundNBT();
			nbt.putFloat("height", height);
			stack.setTag(nbt);
		}
	}

<<<<<<< HEAD
	private float getHeight(ItemStack stack) {
		if(stack.hasTag())
			return stack.getTag().getFloat("height");
=======
	private float getHeight(@Nonnull ItemStack stack) {
		if(stack.hasTagCompound())
			return stack.getTagCompound().getFloat("height");
>>>>>>> origin/feature/nuclearthermalrockets
		return 0;
	}

	@Override
<<<<<<< HEAD
	public void changeMode(ItemStack stack, IInventory modules, PlayerEntity player) {
		CompoundNBT nbt;
		int mode = 0;

		ItemStack helm = player.getItemStackFromSlot(EquipmentSlotType.HEAD);
		if(helm != null && helm.getItem() instanceof IModularArmor) {
			List<ItemStack> helmInv = ((IModularArmor)helm.getItem()).getComponents(helm);
			for(ItemStack helmStack : helmInv) 
				if (stack != null && helmStack.getItem() == AdvancedRocketryItems.itemUpgradeHover && helmStack.getDamage() == 0) {
=======
	public void changeMode(@Nonnull ItemStack stack, IInventory modules, EntityPlayer player) {
		NBTTagCompound nbt;
		int mode = 0;

		ItemStack helm = player.getItemStackFromSlot(EntityEquipmentSlot.HEAD);
		if(!helm.isEmpty() && helm.getItem() instanceof IModularArmor) {
			List<ItemStack> helmInv = ((IModularArmor)helm.getItem()).getComponents(helm);
			for(ItemStack helmStack : helmInv) 
				if (!stack.isEmpty() && helmStack.getItem() == AdvancedRocketryItems.itemUpgrade && helmStack.getItemDamage() == 0) {
>>>>>>> origin/feature/nuclearthermalrockets
					mode = 1;
					break;
				}
		}


		if(stack.hasTag()) {
			nbt = stack.getTag();
			if(mode == 1) {
				mode = nbt.getInt("mode");
				mode++;
				if(mode >= MODES.values().length)
					mode =0;
			}

<<<<<<< HEAD
			nbt.putInt("mode", mode);
			flagModeSwitched(stack);
		}
		else {
			nbt = new CompoundNBT();
			nbt.putInt("mode", mode);
			stack.setTag(nbt);
			flagModeSwitched(stack);
=======
			nbt.setInteger("mode", mode);
		} else {
			nbt = new NBTTagCompound();
			nbt.setInteger("mode", mode);
			stack.setTagCompound(nbt);
>>>>>>> origin/feature/nuclearthermalrockets
		}
		flagModeSwitched(stack);

		if(mode == MODES.HOVER.ordinal())
			setHeight(stack, (float)player.getPosY() + player.getHeight());
	}
<<<<<<< HEAD

	private void flagModeSwitched(ItemStack stack) {
		CompoundNBT nbt;
		if(stack.hasTag()) {
			nbt = stack.getTag();
=======
	
	private void flagModeSwitched(@Nonnull ItemStack stack) {
		NBTTagCompound nbt;
		if(stack.hasTagCompound()) {
			nbt = stack.getTagCompound();
>>>>>>> origin/feature/nuclearthermalrockets

			nbt.putBoolean("modeSwitch", true);
		}
		else {
			nbt = new CompoundNBT();
			nbt.putBoolean("modeSwitch", true);
			stack.setTag(nbt);
		}
	}
<<<<<<< HEAD


	private boolean hasModeSwitched(ItemStack stack) {
		CompoundNBT nbt;
		if(stack.hasTag() && stack.getTag().contains("modeSwitch")) {
			nbt = stack.getTag();
=======
	
	
	private boolean hasModeSwitched(@Nonnull ItemStack stack) {
		NBTTagCompound nbt;
		if(stack.hasTagCompound() && stack.getTagCompound().hasKey("modeSwitch")) {
			nbt = stack.getTagCompound();
>>>>>>> origin/feature/nuclearthermalrockets

			boolean hasSwitched = nbt.getBoolean("modeSwitch");

			nbt.putBoolean("modeSwitch", false);
			return hasSwitched;
		}
		return false;
	}

	@Override
<<<<<<< HEAD
	public boolean isAllowedInSlot(ItemStack stack, EquipmentSlotType slot) {
		return slot == EquipmentSlotType.CHEST;
=======
	public boolean isAllowedInSlot(@Nonnull ItemStack stack, EntityEquipmentSlot slot) {
		return slot == EntityEquipmentSlot.CHEST;
>>>>>>> origin/feature/nuclearthermalrockets
	}

	@Override
<<<<<<< HEAD
	@OnlyIn(value=Dist.CLIENT)
	public void renderScreen(MatrixStack mat, ItemStack componentStack, List<ItemStack> modules, RenderGameOverlayEvent event, Screen gui) {
		List<ItemStack> inv = modules;

		int amt = 0, maxAmt = 0;
		for(int i = 0; i < inv.size(); i++) {
			ItemStack currentStack = inv.get(i);

			if(FluidUtils.containsFluid(currentStack, AdvancedRocketryFluids.hydrogenStill.get())) {
				FluidStack fluidStack = FluidUtils.getFluidForItem(currentStack);
				if(fluidStack != null)
					amt+= fluidStack.getAmount();
				maxAmt += FluidUtils.getFluidItemCapacity(currentStack);
			}

			/*if(currentStack != null && currentStack.getItem() instanceof IFluidContainerItem ) {
=======
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
>>>>>>> origin/feature/nuclearthermalrockets
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

			Minecraft.getInstance().getTextureManager().bindTexture(background);
			GL11.glColor3f(1f, 1f, 1f);
			int width = 83;
			int screenX = RocketEventHandler.hydrogenBar.getRenderX();
			int screenY = RocketEventHandler.hydrogenBar.getRenderY();

			//Draw BG
			gui.blit(mat,screenX, screenY, 23, 34, width, 17);
			gui.blit(mat, screenX , screenY, 23, 51, (int)(width*size), 17);


		}
	}
}
