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
import zmaster587.advancedRocketry.api.AdvancedRocketryParticleTypes;
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

	public ItemJetpack(Properties props) {
		super(props);
	}


	private final ResourceLocation background = TextureResources.rocketHud;

	@Override
	public void onTick(World world, PlayerEntity player,
			ItemStack armorStack, IInventory inv, ItemStack componentStack) {

		if(player.isCreative()) {
			return;
		}

		int speedUpgrades = 0;
		boolean allowsHover = false;

		ItemStack helm = player.getItemStackFromSlot(EquipmentSlotType.HEAD);
		if(helm != null && helm.getItem() instanceof IModularArmor) {
			List<ItemStack> helmInv = ((IModularArmor)helm.getItem()).getComponents(helm);
			for(ItemStack stack : helmInv) {
				if(!stack.isEmpty()) {
					Item item = stack.getItem();

					if (item.getItem() == AdvancedRocketryItems.itemHoverUpgrade)
						if(stack.getDamage() == 0)
							allowsHover = true;
						else if(stack.getItem() == AdvancedRocketryItems.itemFlightSpeedUpgrade)
							speedUpgrades++;
				}
			}
		}

		MODES mode = getMode(componentStack);
		boolean isActive = isActive(componentStack, player);


		//Apply speed upgrades only if the player isn't using Elytra
		if(!player.isElytraFlying()) {
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
	public void onArmorDamaged(LivingEntity entity, ItemStack armorStack,
			ItemStack componentStack, DamageSource source, int damage) {
	}

	@Override
	public boolean isActive(ItemStack stack, PlayerEntity player) {
		return InputSyncHandler.isSpaceDown(player);
	}

	@Override
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
	public void onAccelerate(ItemStack stack, IInventory inv, PlayerEntity player) {
		boolean hasFuel = hasFuel(inv);

		MODES mode = getMode(stack);

		if(hasFuel) {

			player.addVelocity(0, ARConfiguration.getCurrentConfig().jetPackThrust.get() *0.1f, 0);
			if(player.world.isRemote) {
				float playerRot = (float) ((Math.PI/180f)*(player.rotationYaw - 55));
				double xPos = player.getPosX() + MathHelper.cos(playerRot)*.4f;
				double zPos = player.getPosZ() + MathHelper.sin(playerRot)*.4f;

				float ejectSpeed = mode == MODES.HOVER ? 0.1f : 0.3f;
				//AdvancedRocketry.proxy.spawnParticle(AdvancedRocketryParticleTypes.rocketFx, player.worldObj, xPos, player.posY - 0.75, zPos, (player.worldObj.rand.nextFloat() - 0.5f)/18f,-.1 ,(player.worldObj.rand.nextFloat() - 0.5f)/18f);

				AdvancedRocketry.proxy.spawnParticle(AdvancedRocketryParticleTypes.rocketFx, player.world, xPos, player.getPosY() + 0.75, zPos, 0, player.getMotion().y -ejectSpeed ,0);

				playerRot = (float) ((Math.PI/180f)*(player.rotationYaw - 125));
				xPos = player.getPosX() + MathHelper.cos(playerRot)*.4f;
				zPos = player.getPosZ() + MathHelper.sin(playerRot)*.4f;

				AdvancedRocketry.proxy.spawnParticle(AdvancedRocketryParticleTypes.rocketFx, player.world, xPos, player.getPosY() + 0.75, zPos, 0, player.getMotion().y -ejectSpeed ,0);
			}

			if(player.getMotion().y > -1) {
				player.fallDistance = 0;
			}
		}

	}

	@Override
	@OnlyIn(value=Dist.CLIENT)
	public ResourceIcon getComponentIcon(ItemStack armorStack) {

		return isEnabled(armorStack) ? getMode(armorStack) == MODES.HOVER ? new ResourceIcon(TextureResources.jetpackIconHover) : new ResourceIcon(TextureResources.jetpackIconEnabled) : new ResourceIcon(TextureResources.jetpackIconDisabled);
	}

	private MODES getMode(ItemStack stack) {
		if(stack.hasTag())
			return MODES.values()[stack.getTag().getInt("mode")];

		return MODES.values()[0];
	}

	private void setHeight(ItemStack stack, float height) {
		if(stack.hasTag())
			stack.getTag().putFloat("height", height);
		else {
			CompoundNBT nbt = new CompoundNBT();
			nbt.putFloat("height", height);
			stack.setTag(nbt);
		}
	}

	private float getHeight(ItemStack stack) {
		if(stack.hasTag())
			return stack.getTag().getFloat("height");
		return 0;
	}

	@Override
	public void changeMode(ItemStack stack, IInventory modules, PlayerEntity player) {
		CompoundNBT nbt;
		int mode = 0;

		ItemStack helm = player.getItemStackFromSlot(EquipmentSlotType.HEAD);
		if(helm != null && helm.getItem() instanceof IModularArmor) {
			List<ItemStack> helmInv = ((IModularArmor)helm.getItem()).getComponents(helm);
			for(ItemStack helmStack : helmInv) 
				if (stack != null && helmStack.getItem() == AdvancedRocketryItems.itemHoverUpgrade && helmStack.getDamage() == 0) {
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

			nbt.putInt("mode", mode);
			flagModeSwitched(stack);
		}
		else {
			nbt = new CompoundNBT();
			nbt.putInt("mode", mode);
			stack.setTag(nbt);
			flagModeSwitched(stack);
		}
		flagModeSwitched(stack);

		if(mode == MODES.HOVER.ordinal())
			setHeight(stack, (float)player.getPosY() + player.getHeight());
	}

	private void flagModeSwitched(ItemStack stack) {
		CompoundNBT nbt;
		if(stack.hasTag()) {
			nbt = stack.getTag();

			nbt.putBoolean("modeSwitch", true);
		}
		else {
			nbt = new CompoundNBT();
			nbt.putBoolean("modeSwitch", true);
			stack.setTag(nbt);
		}
	}


	private boolean hasModeSwitched(ItemStack stack) {
		CompoundNBT nbt;
		if(stack.hasTag() && stack.getTag().contains("modeSwitch")) {
			nbt = stack.getTag();

			boolean hasSwitched = nbt.getBoolean("modeSwitch");

			nbt.putBoolean("modeSwitch", false);
			return hasSwitched;
		}
		return false;
	}

	@Override
	public boolean isAllowedInSlot(ItemStack stack, EquipmentSlotType slot) {
		return slot == EquipmentSlotType.CHEST;
	}

	@Override
	@OnlyIn(value=Dist.CLIENT)
	public void renderScreen(MatrixStack mat, ItemStack componentStack, List<ItemStack> modules, RenderGameOverlayEvent event, Screen gui) {

		int amt = 0, maxAmt = 0;
		for (ItemStack currentStack : modules) {
			if (FluidUtils.containsFluid(currentStack, AdvancedRocketryFluids.hydrogenStill.get())) {
				FluidStack fluidStack = FluidUtils.getFluidForItem(currentStack);
				if (fluidStack != null)
					amt += fluidStack.getAmount();
				maxAmt += FluidUtils.getFluidItemCapacity(currentStack);
			}

			/*if(currentStack != null && currentStack.getItem() instanceof IFluidContainerItem ) {
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
			int screenX = Minecraft.getInstance().getMainWindow().getScaledWidth()/2 + RocketEventHandler.hydrogenBar.getRenderX();
			int screenY = Minecraft.getInstance().getMainWindow().getScaledHeight() + RocketEventHandler.hydrogenBar.getRenderY();

			//Draw BG
			gui.blit(mat,screenX, screenY, 23, 34, width, 17);
			gui.blit(mat, screenX , screenY, 23, 51, (int)(width*size), 17);


		}
	}
}
