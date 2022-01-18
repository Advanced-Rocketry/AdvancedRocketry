package zmaster587.advancedRocketry.tile.satellite;

import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.api.distmarker.Dist;
import zmaster587.advancedRocketry.api.AdvancedRocketryItems;
import zmaster587.advancedRocketry.api.AdvancedRocketryTileEntityType;
import zmaster587.advancedRocketry.api.SatelliteRegistry;
import zmaster587.advancedRocketry.api.satellite.SatelliteBase;
import zmaster587.advancedRocketry.api.satellite.SatelliteProperties;
import zmaster587.advancedRocketry.dimension.DimensionManager;
import zmaster587.advancedRocketry.inventory.TextureResources;
import zmaster587.advancedRocketry.item.*;
import zmaster587.advancedRocketry.item.tools.ItemOreScanner;
import zmaster587.libVulpes.LibVulpes;
import zmaster587.libVulpes.client.util.ProgressBarImage;
import zmaster587.libVulpes.inventory.modules.*;
import zmaster587.libVulpes.network.PacketHandler;
import zmaster587.libVulpes.network.PacketMachine;
import zmaster587.libVulpes.tile.multiblock.TileMultiPowerConsumer;
import zmaster587.libVulpes.util.EmbeddedInventory;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.LinkedList;
import java.util.List;

public class TileSatelliteAssembler extends TileMultiPowerConsumer implements IModularInventory, IInventory, IButtonInventory {

	public static final Object[][][] structure = new Object[][][] {
		{{'c'}},
		{{'P'}}
	};

	EmbeddedInventory inventory;
	//Slot 0: Main satellite device
	//Slot 1 -> 6: Other functional pieces
	//Slot 7: output
	//Slot 8: chip printing slot
	//Slot 9: chip duping slot
	//Slot 10: temp holding space (Inaccessible)

	private static final byte primaryFunctionSlot = 0;
	private static final byte modularFunctionSlotStart = 1;
	private static final byte modularFunctionSlotEnd = 6;
	private static final byte outputSlot = 7;
	private static final byte chipSlot = 8;
	private static final byte chipCopySlot = 9;
	private static final byte holdingSlot = 10;
	private static final byte chassisSlot = 11;

	public TileSatelliteAssembler() {
		super(AdvancedRocketryTileEntityType.TILE_SATELLITE_ASSEMBLER);
		inventory = new EmbeddedInventory(5);
		powerPerTick = 10;
	}

	@Override
	public Object[][][] getStructure() {
		return structure;
	}

	@Override
	protected void processComplete() {
		super.processComplete();
		setInventorySlotContents(outputSlot, removeStackFromSlot(holdingSlot));
	}

	public boolean canAssembleSatellite() {

		if(getStackInSlot(chassisSlot).isEmpty())
			return false;

		boolean hasPowerGeneration = false;
		//First make sure everything is a satellite part, and check to see if satellite has any power generation
		for(int i = primaryFunctionSlot; i <= modularFunctionSlotEnd; i++) {
			ItemStack stack = getStackInSlot(i);
			if (!stack.isEmpty() && SatelliteRegistry.getSatelliteProperty(stack) != null && SatelliteRegistry.getSatelliteProperty(stack).getPropertyFlag() == SatelliteProperties.Property.POWER_GEN.getFlag() && SatelliteRegistry.getSatelliteProperty(stack).getPowerGeneration() > 0)
				hasPowerGeneration = true;
		}

		//Make sure critical parts exist and output is empty
		if(getStackInSlot(primaryFunctionSlot).isEmpty() || SatelliteRegistry.getSatelliteProperty(getStackInSlot(primaryFunctionSlot)) == null || !hasPowerGeneration)
			return false;
		if(!getStackInSlot(holdingSlot).isEmpty() || !getStackInSlot(outputSlot).isEmpty() )
			return false;

		String satType = SatelliteRegistry.getSatelliteProperty(getStackInSlot(primaryFunctionSlot)).getSatelliteType();
		SatelliteBase sat = SatelliteRegistry.getSatellite(satType);

		return sat.isAcceptableControllerItemStack(getStackInSlot(chipSlot));
	}

	/**
	 * Assumes everything is in the proper place in the inventory to construct the satellite
	 * If unsure check canAssembleSatellite() first
	 */
	public void assembleSatellite() {
		//Basic properties of the satellite
		int powerStorage = 0, powerGeneration = 0, maxData = 0;

		//Get the primary function from slot 0
		String satType = SatelliteRegistry.getSatelliteProperty(getStackInSlot(primaryFunctionSlot)).getSatelliteType();
		SatelliteBase sat = SatelliteRegistry.getSatellite(satType);
		
		if(!world.isRemote) {
			//Grab properties from the items in slots 1-6
			for (int currentSlotIndex = modularFunctionSlotStart; currentSlotIndex <= modularFunctionSlotEnd; currentSlotIndex++) {
				ItemStack stack = getStackInSlot(currentSlotIndex);
				if (SatelliteRegistry.getSatelliteProperty(stack) != null) {
					if (SatelliteRegistry.getSatelliteProperty(stack).getPropertyFlag() == SatelliteProperties.Property.BATTERY.getFlag()) powerStorage += SatelliteRegistry.getSatelliteProperty(getStackInSlot(currentSlotIndex)).getPowerStorage();
					if (SatelliteRegistry.getSatelliteProperty(stack).getPropertyFlag() == SatelliteProperties.Property.POWER_GEN.getFlag()) powerGeneration += SatelliteRegistry.getSatelliteProperty(getStackInSlot(currentSlotIndex)).getPowerGeneration();
					if (SatelliteRegistry.getSatelliteProperty(stack).getPropertyFlag() == SatelliteProperties.Property.DATA.getFlag()) maxData += SatelliteRegistry.getSatelliteProperty(getStackInSlot(currentSlotIndex)).getMaxDataStorage();
				}
			}

			//Set final satellite properties
			//720 here is the base power buffer, so the satellite has SOMETHING to run on
			SatelliteProperties properties = new SatelliteProperties(powerGeneration, powerStorage + 720, satType, maxData);
			properties.setId(DimensionManager.getInstance().getNextSatelliteId());

			//Create the output item
			ItemSatellite satItem = (ItemSatellite)AdvancedRocketryItems.itemSatellite;
			ItemStack output = getStackInSlot(chassisSlot);
			satItem.setSatellite(output, properties);

			setInventorySlotContents(chassisSlot, ItemStack.EMPTY);

			//Set the ID chip
			setInventorySlotContents(chipSlot, sat.getControllerItemStack(getStackInSlot(chipSlot), properties));
			//Move item to temporary holding slot
			setInventorySlotContents(holdingSlot, output);
		}

		completionTime = 100;
	}

	private boolean canCopy() {
		ItemStack stack0 = getStackInSlot(chipSlot);
		ItemStack stack1 = getStackInSlot(chipCopySlot);
		getStackInSlot(outputSlot);

		boolean chipsExist = !stack0.isEmpty() && !stack1.isEmpty();
		if(!chipsExist)
			return false;
		boolean isSatellite = ((stack0.getItem() instanceof ItemSatellite || stack0.getItem() instanceof ItemSatelliteChip) && stack1.getItem().equals(stack0.getItem()));
		boolean isStation = stack0.getItem() instanceof ItemStationChip && !ItemStationChip.getUUID(stack0).equals(DimensionManager.overworldProperties.getId()) && stack1.getItem() instanceof ItemStationChip;
		boolean isPlanet = (stack0.getItem() instanceof ItemPlanetChip && stack1.getItem() instanceof ItemPlanetChip);
		boolean isOreScanner = (stack0.getItem() instanceof ItemOreScanner && stack1.getItem() instanceof ItemOreScanner);
		return !isRunning() && getStackInSlot(outputSlot).isEmpty() && (isStation || stack0.hasTag()) && 
				(isSatellite  || isStation || isPlanet || isOreScanner);
	}

	private void copyChip() {

		ItemStack slot0 = getStackInSlot(chipSlot);
		ItemStack slot1 = getStackInSlot(chipCopySlot);

		if(slot0.getItem() instanceof ItemSatelliteChip || slot0.getItem() instanceof ItemOreScanner || slot0.getItem() instanceof ItemPlanetChip || slot0.getItem() instanceof ItemStationChip) {
			setInventorySlotContents(holdingSlot, getStackInSlot(chipSlot).copy());
		} else {
			ItemSatelliteChip itemIdChip = (ItemSatelliteChip)slot1.getItem();

			itemIdChip.setSatellite(slot1, SatelliteRegistry.getSatelliteProperty(slot0));
			setInventorySlotContents(holdingSlot, slot1);
		}
		decrStackSize(chipCopySlot, 1);
		completionTime = 100;

		this.markDirty();
	}

	private void doActionById(int buttonId)
	{
		if(buttonId == 0) {
			if(canAssembleSatellite())
				assembleSatellite();
		}
		else if(buttonId == 1)
			if(canCopy())
				copyChip();
	}
	
	@Override
	public void useNetworkData(PlayerEntity player, Dist side, byte id,
			CompoundNBT nbt) {
		super.useNetworkData(player, side, id, nbt);


		doActionById(id - 100);
	}

	@Override
	public List<ModuleBase> getModules(int ID, PlayerEntity player) {
		List<ModuleBase> modules = new LinkedList<>();

		modules.add(new ModulePower(18, 20, getBatteries()));
		modules.add(new ModuleTexturedLimitedSlotArray(152, 10, this, 0, 1, TextureResources.functionComponent)); //Functional Piece
		modules.add(new ModuleTexturedLimitedSlotArray(116, 30, this, 1, 4, TextureResources.powercomponent));  //Generators
		modules.add(new ModuleTexturedLimitedSlotArray(116, 50, this, 4, 7, TextureResources.ioSlot));
		modules.add(new ModuleTexturedLimitedSlotArray(38, 16, this, chassisSlot, chassisSlot + 1, TextureResources.slotSatellite)); 	// Id chip
		modules.add(new ModuleOutputSlotArray(58, 36, this, 7, 8));   // Output
		modules.add(new ModuleTexturedSlotArray(58, 16, this, chipSlot, chipSlot + 1, TextureResources.idChip)); 	// Id chip
		modules.add(new ModuleTexturedSlotArray(82, 16, this, chipCopySlot, chipCopySlot+1, TextureResources.idChip)); 	// Id chip
		modules.add(new ModuleProgress(75, 36, 0, new ProgressBarImage(217,0, 17, 17, 234, 0, Direction.DOWN, TextureResources.progressBars), this));
		modules.add(new ModuleButton(40, 56, "Build", this,  zmaster587.libVulpes.inventory.TextureResources.buttonBuild).setAdditionalData(0));
		modules.add(new ModuleButton(173, 3, "", this, TextureResources.buttonCopy, LibVulpes.proxy.getLocalizedString("msg.satbuilder.writesecondchip"), 24, 24).setAdditionalData(1));

		return modules;
	}

	@Override
	public void onInventoryButtonPressed(ModuleButton buttonId) {
		if(world.isRemote)
			PacketHandler.sendToServer(new PacketMachine(this, (byte)((int)buttonId.getAdditionalData() + 100)) );

		doActionById((int)buttonId.getAdditionalData());

	}

	@Override
	public String getMachineName() {
		return "block.advancedrocketry.satelliteassembler";
	}

	@Override
	public int getSizeInventory() {
		return inventory.getSizeInventory();
	}

	@Override
	@Nonnull
	public ItemStack getStackInSlot(int slot) {
		if(slot < outputSlot) {
			ItemStack chassis = getStackInSlot(chassisSlot);

			if(!chassis.isEmpty() && chassis.getItem() instanceof ItemSatellite) {
				EmbeddedInventory inv = ((ItemSatellite)chassis.getItem()).readInvFromNBT(chassis);
				return inv.getStackInSlot(slot);
			}
			return ItemStack.EMPTY;
		}
		return inventory.getStackInSlot(slot - 7);
	}

	@Override
	@Nonnull
	public ItemStack decrStackSize(int slot, int amt) {
		if(slot < outputSlot) {
			ItemStack chassis = getStackInSlot(chassisSlot);

			if(!chassis.isEmpty() && chassis.getItem() instanceof ItemSatellite) {
				EmbeddedInventory inv = ((ItemSatellite)chassis.getItem()).readInvFromNBT(chassis);
				ItemStack stack = inv.decrStackSize(slot, amt);
				((ItemSatellite)chassis.getItem()).writeInvToNBT(chassis, inv);
				return stack;
			}

			return ItemStack.EMPTY;
		}

		return inventory.decrStackSize(slot - 7, amt);
	}


	@Override
	public void setInventorySlotContents(int slot, @Nonnull ItemStack stack) {
		if(slot < outputSlot) {
			ItemStack chassis = getStackInSlot(chassisSlot);

			if(!chassis.isEmpty() && chassis.getItem() instanceof ItemSatellite) {
				EmbeddedInventory inv = ((ItemSatellite)chassis.getItem()).readInvFromNBT(chassis);
				inv.setInventorySlotContents(slot, stack);
				((ItemSatellite)chassis.getItem()).writeInvToNBT(chassis, inv);
			}
			return;
		}
		inventory.setInventorySlotContents(slot-7, stack);
	}


	@Override
	public int getInventoryStackLimit() {
		return 1;
	}

	@Override
	public boolean isUsableByPlayer(PlayerEntity player) {
		return pos.distanceSq(new BlockPos(player.getPositionVec())) < 4192;
	}

	@Override
	@ParametersAreNonnullByDefault
	public void openInventory(PlayerEntity player) {
		inventory.openInventory(player);
	}

	@Override
	@ParametersAreNonnullByDefault
	public void closeInventory(PlayerEntity player) {
		inventory.closeInventory(player);
	}

	@Override
	public boolean isItemValidForSlot(int slot, @Nonnull ItemStack stack) {
		if(slot < outputSlot) {
			ItemStack chassis = getStackInSlot(chassisSlot);

			if(!chassis.isEmpty() && chassis.getItem() instanceof ItemSatellite) {
				EmbeddedInventory inv = ((ItemSatellite)chassis.getItem()).readInvFromNBT(chassis);
				return inv.isItemValidForSlot(slot, stack);
			}
			return false;
		}
		return inventory.isItemValidForSlot(slot - 7, stack);
	}

	@Override
	public CompoundNBT write(CompoundNBT nbt) {
		super.write(nbt);

		inventory.write(nbt);
		return nbt;
	}

	@Override
	public void read(BlockState state, CompoundNBT nbt) {
		super.read(state, nbt);

		inventory.readFromNBT(nbt);
	}

	@Nonnull
	@Override
	public ItemStack removeStackFromSlot(int index) {
		if(index < outputSlot) {
			ItemStack chassis = getStackInSlot(chassisSlot);

			if(!chassis.isEmpty() && chassis.getItem() instanceof ItemSatellite) {
				EmbeddedInventory inv = ((ItemSatellite)chassis.getItem()).readInvFromNBT(chassis);
				ItemStack stack = inv.removeStackFromSlot(index);
				((ItemSatellite)chassis.getItem()).writeInvToNBT(chassis,inv);
				return stack;
			}
			return ItemStack.EMPTY;
		}

		return inventory.removeStackFromSlot(index - 7);
	}

	@Override
	public void clear() {
		inventory.clear();
	}
	
	@Override
	public boolean isEmpty() {
		return inventory.isEmpty();
	}
}