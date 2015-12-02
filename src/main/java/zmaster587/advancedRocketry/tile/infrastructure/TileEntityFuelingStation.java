package zmaster587.advancedRocketry.tile.infrastructure;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ChatComponentText;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidContainerRegistry;
import net.minecraftforge.fluids.FluidStack;
import zmaster587.advancedRocketry.Inventory.modules.IModularInventory;
import zmaster587.advancedRocketry.Inventory.modules.IProgressBar;
import zmaster587.advancedRocketry.Inventory.modules.ModuleBase;
import zmaster587.advancedRocketry.Inventory.modules.ModuleImage;
import zmaster587.advancedRocketry.Inventory.modules.ModuleLiquidIndicator;
import zmaster587.advancedRocketry.Inventory.modules.ModulePower;
import zmaster587.advancedRocketry.Inventory.modules.ModuleProgress;
import zmaster587.advancedRocketry.Inventory.modules.ModuleSlotArray;
import zmaster587.advancedRocketry.api.fuel.FuelRegistry;
import zmaster587.advancedRocketry.api.fuel.FuelRegistry.FuelType;
import zmaster587.advancedRocketry.api.Configuration;
import zmaster587.advancedRocketry.api.IEntityRocket;
import zmaster587.advancedRocketry.api.IInfrastructure;
import zmaster587.advancedRocketry.entity.EntityRocket;
import zmaster587.libVulpes.gui.CommonResources;
import zmaster587.libVulpes.interfaces.ILinkableTile;
import zmaster587.libVulpes.item.ItemLinker;
import zmaster587.libVulpes.tile.TileInventoriedRFConsumerTank;
import zmaster587.libVulpes.util.IconResource;

public class TileEntityFuelingStation extends TileInventoriedRFConsumerTank implements IModularInventory, IInfrastructure, ILinkableTile {
	IEntityRocket linkedRocket;

	public TileEntityFuelingStation() {
		super(1000,3, 5000);
	}

	@Override
	public int getMaxLinkDistance() {
		return 10;
	}
	
	@Override
	public void performFunction() {
		if(!worldObj.isRemote) {
			if(tank.getFluid() != null) {
				float multiplier = FuelRegistry.instance.getMultiplier(FuelType.LIQUID, tank.getFluid().getFluid());
				linkedRocket.addFuelAmount((int)(multiplier*Configuration.fuelPointsPer10Mb));
				tank.drain(10, true);
			}
		}
		useBucket(0, inv[0]);
	}

	@Override
	public int getPowerPerOperation() {
		return 30;
	}

	@Override
	public boolean canPerformFunction() {
		// TODO Solid fuel?
		return linkedRocket != null && ( /*(inv != null) ||*/ (tank.getFluid() != null && tank.getFluidAmount() > 9 && linkedRocket.getRocketStats().getFuelAmount(FuelType.LIQUID) < linkedRocket.getRocketStats().getFuelCapacity(FuelType.LIQUID)) );
	}

	@Override
	public boolean canFill(ForgeDirection from, Fluid fluid) {
		return FuelRegistry.instance.isFuel(FuelType.LIQUID,fluid);
	}


	@Override
	public String getModularInventoryName() {
		return "Fueling Station";
	}

	@Override
	public boolean hasCustomInventoryName() {
		return true;
	}

	@Override
	public void setInventorySlotContents(int slot, ItemStack stack) {
		
		if(!useBucket(slot, stack))
			super.setInventorySlotContents(slot, stack);
	}

	//Yes i was lazy
	//TODO: make better
	private boolean useBucket( int slot, ItemStack stack) {
		if(slot == 0 && FluidContainerRegistry.isFilledContainer(stack) && FuelRegistry.instance.isFuel(FuelType.LIQUID,FluidContainerRegistry.getFluidForFilledItem(stack).getFluid()) && tank.getFluidAmount() + FluidContainerRegistry.getContainerCapacity(stack) <= tank.getCapacity()) {
			ItemStack emptyContainer = FluidContainerRegistry.drainFluidContainer(stack);

			if(emptyContainer != null && inv[1] == null || (emptyContainer.isItemEqual(inv[1]) && inv[1].stackSize < inv[1].getMaxStackSize())) {
				tank.fill(FluidContainerRegistry.getFluidForFilledItem(stack), true);

				if(inv[1] == null)
					super.setInventorySlotContents(1, emptyContainer);
				else
					inv[1].stackSize++;
				decrStackSize(0, 1);
			}
			else
				return false;
		}
		else
			return false;
		
		return true;
	}

	@Override
	public boolean isItemValidForSlot(int slot, ItemStack stack) {
		
		if(FluidContainerRegistry.isFilledContainer(stack))
			return FuelRegistry.instance.isFuel(FuelType.LIQUID, FluidContainerRegistry.getFluidForFilledItem(stack).getFluid());
		return FuelRegistry.instance.isFuel(FuelType.LIQUID,stack);
	}

	@Override
	public void unlinkRocket() {
		this.linkedRocket = null;

	}

	@Override
	public boolean disconnectOnLiftOff() {
		return true;
	}

	@Override
	public boolean linkRocket(IEntityRocket rocket) {
		this.linkedRocket = rocket;
		return true;
	}

	@Override
	public boolean onLinkStart(ItemStack item, TileEntity entity,
			EntityPlayer player, World world) {

		ItemLinker.setMasterCoords(item, this.xCoord, this.yCoord, this.zCoord);

		if(player.worldObj.isRemote)
			Minecraft.getMinecraft().ingameGUI.getChatGUI().printChatMessage((new ChatComponentText("You program the linker with the fueling station at: " + this.xCoord + " " + this.yCoord + " " + this.zCoord)));
		return true;
	}

	@Override
	public boolean onLinkComplete(ItemStack item, TileEntity entity,
			EntityPlayer player, World world) {
		if(player.worldObj.isRemote)
			Minecraft.getMinecraft().ingameGUI.getChatGUI().printChatMessage((new ChatComponentText("This must be the first machine to link!")));
		return false;
	}

	@Override
	public int[] getAccessibleSlotsFromSide(int side) {
		if(ForgeDirection.getOrientation(side) == ForgeDirection.DOWN)
			return  new int[]{1};
		return  new int[]{0}; 
	}

	@Override
	public List<ModuleBase> getModules() {
		List<ModuleBase> list = new ArrayList<ModuleBase>();
		
		list.add(new ModulePower(156, 12, this));
		list.add(new ModuleSlotArray(45, 18, this, 0, 1));
		list.add(new ModuleSlotArray(45, 54, this, 1, 2));
		list.add(new ModuleImage(44, 35, new IconResource(194, 0, 18, 18, CommonResources.genericBackground)));
		list.add(new ModuleLiquidIndicator(27, 18, this));
		
		return list;
	}

	@Override
	public String getInventoryName() {
		return null;
	}

	@Override
	public boolean canInteractWithContainer(EntityPlayer entity) {
		return true;
	}

}
