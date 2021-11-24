package zmaster587.advancedRocketry.tile.atmosphere;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.fluid.Fluid;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Direction;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import zmaster587.advancedRocketry.api.AdvancedRocketryFluids;
import zmaster587.advancedRocketry.api.AdvancedRocketryTileEntityType;
import zmaster587.advancedRocketry.api.armor.IFillableArmor;
import zmaster587.advancedRocketry.util.ItemAirUtils;
import zmaster587.libVulpes.api.IModularArmor;
import zmaster587.libVulpes.api.LibvulpesGuiRegistry;
import zmaster587.libVulpes.gui.CommonResources;
import zmaster587.libVulpes.inventory.ContainerModular;
import zmaster587.libVulpes.inventory.GuiHandler;
import zmaster587.libVulpes.inventory.GuiHandler.guiId;
import zmaster587.libVulpes.inventory.modules.*;
import zmaster587.libVulpes.tile.TileInventoriedFEConsumerTank;
import zmaster587.libVulpes.util.FluidUtils;
import zmaster587.libVulpes.util.IconResource;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.ArrayList;
import java.util.List;

public class TileGasChargePad extends TileInventoriedFEConsumerTank implements IModularInventory {
	public TileGasChargePad() {
		super(AdvancedRocketryTileEntityType.TILE_GAS_CHARGE_PAD, 0, 2, 16000);
	}

	@Nonnull
	@Override
	@ParametersAreNonnullByDefault
	public int[] getSlotsForFace(Direction side) {
		return new int[] {};
	}

	@Override
	public boolean isItemValidForSlot(int slot, @Nonnull ItemStack stack) {
		return false;
	}

	@Override
	public int fill(FluidStack resource, FluidAction doFill) {

		if(canFill(resource.getFluid()))
			return super.fill(resource, doFill);
		return 0;
	}

	@Override
	public boolean canFill(Fluid fluid) {
		return FluidUtils.areFluidsSameType(fluid, AdvancedRocketryFluids.oxygenStill.get()) || FluidUtils.areFluidsSameType(fluid, AdvancedRocketryFluids.hydrogenStill.get());
	}	

	@Override
	public int getPowerPerOperation() {
		return 0;
	}

	@Override
	public boolean canPerformFunction() {
		if(!world.isRemote) {
			for( PlayerEntity player : this.world.getEntitiesWithinAABB(PlayerEntity.class, new AxisAlignedBB(pos, pos.add(1,2,1)))) {
				ItemStack stack = player.getItemStackFromSlot(EquipmentSlotType.CHEST);

				if(!stack.isEmpty()) {
					IFillableArmor fillable = null;

					if(stack.getItem() instanceof IFillableArmor)
						fillable = (IFillableArmor)stack.getItem();
					else if(ItemAirUtils.INSTANCE.isStackValidAirContainer(stack))
						fillable = new ItemAirUtils.ItemAirWrapper(stack);
					
					//Check for O2 fill
					if(fillable != null ) {
						int amtFluid = fillable.getMaxAir(stack) - fillable.getAirRemaining(stack);
						FluidStack fluidStack = this.drain(amtFluid, FluidAction.SIMULATE);

						if(amtFluid > 0 &&
								fluidStack != null && FluidUtils.areFluidsSameType(fluidStack.getFluid(), AdvancedRocketryFluids.oxygenStill.get()) && fluidStack.getAmount() > 0)  {
							FluidStack fstack = this.drain(amtFluid, FluidAction.EXECUTE);
							this.markDirty();
							world.markChunkDirty(getPos(), this);
							fillable.increment(stack, fstack.getAmount());
							return true;
						}
					}
				}

				//Check for H2 fill (possibly merge with O2 fill
				//Fix conflict with O2 fill
				if(!this.tank.getFluid().isEmpty() && !FluidUtils.areFluidsSameType(this.tank.getFluid().getFluid(), AdvancedRocketryFluids.oxygenStill.get()) && !stack.isEmpty() && stack.getItem() instanceof IModularArmor) {
					IInventory inv = ((IModularArmor)stack.getItem()).loadModuleInventory(stack);

					FluidStack fluidStack = this.drain(100, FluidAction.SIMULATE);
					if(!fluidStack.isEmpty()) {
						for(int i = 0; i < inv.getSizeInventory(); i++) {

							if(!((IModularArmor)stack.getItem()).canBeExternallyModified(stack, i))
								continue;

							ItemStack module = inv.getStackInSlot(i);
							if(FluidUtils.containsFluid(module)) {
								int amtFilled = module.getCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY, Direction.UP).orElseThrow(null).fill(fluidStack, FluidAction.EXECUTE);
								if(amtFilled == 100) {
									this.drain(100, FluidAction.EXECUTE);
									
									this.markDirty();
									world.markChunkDirty(getPos(), this);
									
									((IModularArmor)stack.getItem()).saveModuleInventory(stack, inv);

									return true;
								}
							}
						}
					}
				}

				return false;
			}
		}
		return false;
	}

	@Override
	public void performFunction() {

	}

	@Override
	public List<ModuleBase> getModules(int ID, PlayerEntity player) {
		ArrayList<ModuleBase> modules = new ArrayList<>();

		modules.add(new ModuleSlotArray(50, 21, this, 0, 1));
		modules.add(new ModuleSlotArray(50, 57, this, 1, 2));
		if(world.isRemote)
			modules.add(new ModuleImage(49, 38, new IconResource(194, 0, 18, 18, CommonResources.genericBackground)));

		//modules.add(new ModulePower(18, 20, this));
		modules.add(new ModuleLiquidIndicator(32, 20, this));

		//modules.add(toggleSwitch = new ModuleToggleSwitch(160, 5, 0, "", this, TextureResources.buttonToggleImage, 11, 26, getMachineEnabled()));
		return modules;
	}

	@Override
	public String getModularInventoryName() {
		return "block.advancedrocketry.gaschargepad";
	}

	@Override
	public boolean canInteractWithContainer(PlayerEntity entity) {
		return true;
	}

	@Override
	public void setInventorySlotContents(int slot, @Nonnull ItemStack stack) {
		super.setInventorySlotContents(slot, stack);
		while(useBucket(0, getStackInSlot(0)));
	}
	
	private boolean useBucket(int slot, @Nonnull ItemStack stack) {
		return FluidUtils.attemptDrainContainerIInv(inventory, tank, stack, 0, 1);
	}

	@Override
	public boolean isEmpty() {
		return inventory.isEmpty();
	}

	@Nonnull
	@Override
	public ITextComponent getDisplayName() {
		return new TranslationTextComponent(getModularInventoryName());
	}

	@Override
	@ParametersAreNonnullByDefault
	public Container createMenu(int id, PlayerInventory inv, PlayerEntity player) {
		return new ContainerModular(LibvulpesGuiRegistry.CONTAINER_MODULAR_TILE, id, player, getModules(getModularInvType().ordinal(), player), this, getModularInvType());
	}

	@Override
	public GuiHandler.guiId getModularInvType() {
		return guiId.MODULAR;
	}
}
