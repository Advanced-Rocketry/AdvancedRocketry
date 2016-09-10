package zmaster587.advancedRocketry.tile.infrastructure;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ChatComponentText;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidContainerRegistry;
import zmaster587.advancedRocketry.api.fuel.FuelRegistry;
import zmaster587.advancedRocketry.api.fuel.FuelRegistry.FuelType;
import zmaster587.advancedRocketry.api.AdvancedRocketryBlocks;
import zmaster587.advancedRocketry.api.Configuration;
import zmaster587.advancedRocketry.api.EntityRocketBase;
import zmaster587.advancedRocketry.api.IInfrastructure;
import zmaster587.advancedRocketry.api.IMission;
import zmaster587.advancedRocketry.block.BlockTileRedstoneEmitter;
import zmaster587.advancedRocketry.tile.TileRocketBuilder;
import zmaster587.libVulpes.gui.CommonResources;
import zmaster587.libVulpes.interfaces.ILinkableTile;
import zmaster587.libVulpes.inventory.modules.IModularInventory;
import zmaster587.libVulpes.inventory.modules.ModuleBase;
import zmaster587.libVulpes.inventory.modules.ModuleImage;
import zmaster587.libVulpes.inventory.modules.ModuleLiquidIndicator;
import zmaster587.libVulpes.inventory.modules.ModulePower;
import zmaster587.libVulpes.inventory.modules.ModuleSlotArray;
import zmaster587.libVulpes.items.ItemLinker;
import zmaster587.libVulpes.tile.IMultiblock;
import zmaster587.libVulpes.tile.TileInventoriedRFConsumerTank;
import zmaster587.libVulpes.util.BlockPosition;
import zmaster587.libVulpes.util.IconResource;

public class TileEntityFuelingStation extends TileInventoriedRFConsumerTank implements IModularInventory, IMultiblock, IInfrastructure, ILinkableTile {
	EntityRocketBase linkedRocket;
	BlockPosition masterBlock;
	
	public TileEntityFuelingStation() {
		super(1000,3, 5000);
		masterBlock = new BlockPosition(0, -1, 0);
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

				tank.drain(linkedRocket.addFuelAmount((int)(multiplier*Configuration.fuelPointsPer10Mb)), true);
				
				//If the rocket is full then emit redstone
				((BlockTileRedstoneEmitter)AdvancedRocketryBlocks.blockFuelingStation).setRedstoneState(worldObj, xCoord, yCoord, zCoord, linkedRocket.getFuelAmount() == linkedRocket.getFuelCapacity());
			}
		}
		useBucket(0, inventory.getStackInSlot(0));
	}

	@Override
	public int getPowerPerOperation() {
		return 30;
	}

	@Override
	public void updateEntity() {
		super.updateEntity();
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

			if(emptyContainer != null && inventory.getStackInSlot(1) == null || (emptyContainer.isItemEqual(inventory.getStackInSlot(1)) && inventory.getStackInSlot(1).stackSize < inventory.getStackInSlot(1).getMaxStackSize())) {
				tank.fill(FluidContainerRegistry.getFluidForFilledItem(stack), true);

				if(inventory.getStackInSlot(1) == null)
					super.setInventorySlotContents(1, emptyContainer);
				else
					inventory.getStackInSlot(1).stackSize++;
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
		((BlockTileRedstoneEmitter)AdvancedRocketryBlocks.blockFuelingStation).setRedstoneState(worldObj, xCoord, yCoord, zCoord, false);
		
	}

	@Override
	public boolean disconnectOnLiftOff() {
		return true;
	}

	@Override
	public boolean linkRocket(EntityRocketBase rocket) {
		this.linkedRocket = rocket;
		((BlockTileRedstoneEmitter)AdvancedRocketryBlocks.blockFuelingStation).setRedstoneState(worldObj, xCoord, yCoord, zCoord, linkedRocket.getFuelAmount() == linkedRocket.getFuelCapacity());
		return true;
	}

	@Override
	public boolean onLinkStart(ItemStack item, TileEntity entity,
			EntityPlayer player, World world) {

		ItemLinker.setMasterCoords(item, this.xCoord, this.yCoord, this.zCoord);

		if(this.linkedRocket != null) {
			this.linkedRocket.unlinkInfrastructure(this);
			this.unlinkRocket();
		}
		
		if(player.worldObj.isRemote)
			Minecraft.getMinecraft().ingameGUI.getChatGUI().printChatMessage((new ChatComponentText("You program the linker with the fueling station at: " + this.xCoord + " " + this.yCoord + " " + this.zCoord)));
		return true;
	}

	@Override
	public void invalidate() {
		super.invalidate();
		if(getMasterBlock() instanceof TileRocketBuilder)
			((TileRocketBuilder)getMasterBlock()).removeConnectedInfrastructure(this);
		
		//Mostly for client rendering stuff
		if(linkedRocket != null)
			linkedRocket.unlinkInfrastructure(this);
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
	public List<ModuleBase> getModules(int ID, EntityPlayer player) {
		List<ModuleBase> list = new ArrayList<ModuleBase>();
		
		list.add(new ModulePower(156, 12, this));
		list.add(new ModuleSlotArray(45, 18, this, 0, 1));
		list.add(new ModuleSlotArray(45, 54, this, 1, 2));
		if(worldObj.isRemote)
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

	@Override
	public boolean linkMission(IMission misson) {
		return false;
	}
	
	@Override
	public void unlinkMission() {
	}

	@Override
	public void writeToNBT(NBTTagCompound nbt) {
		super.writeToNBT(nbt);
		
		if(hasMaster()) {
			nbt.setIntArray("masterPos", new int[] {masterBlock.x, masterBlock.y, masterBlock.z});
		}
	}
	
	@Override
	public void readFromNBT(NBTTagCompound nbt) {
		super.readFromNBT(nbt);
		if(nbt.hasKey("masterPos")) {
			int[] pos = nbt.getIntArray("masterPos");
			setMasterBlock(pos[0], pos[1], pos[2]);
		}
	}
	
	@Override
	public boolean hasMaster() {
		return masterBlock.y > -1;
	}

	@Override
	public TileEntity getMasterBlock() {
		return worldObj.getTileEntity(masterBlock.x, masterBlock.y, masterBlock.z);
	}

	@Override
	public void setComplete(int x, int y, int z) {
		
	}

	@Override
	public void setIncomplete() {
		masterBlock.y = -1;
	}

	@Override
	public void setMasterBlock(int x, int y, int z) {
		masterBlock = new BlockPosition(x, y, z);
	}
	
	public boolean canRenderConnection() {
		return true;
	}
}
