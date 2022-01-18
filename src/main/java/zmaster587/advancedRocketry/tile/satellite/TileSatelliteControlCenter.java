package zmaster587.advancedRocketry.tile.satellite;

import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.Direction;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import zmaster587.advancedRocketry.api.AdvancedRocketryTileEntityType;
import zmaster587.advancedRocketry.api.DataStorage;
import zmaster587.advancedRocketry.api.DataStorage.DataType;
import zmaster587.advancedRocketry.api.satellite.SatelliteBase;
import zmaster587.advancedRocketry.dimension.DimensionManager;
import zmaster587.advancedRocketry.inventory.TextureResources;
import zmaster587.advancedRocketry.inventory.modules.ModuleData;
import zmaster587.advancedRocketry.inventory.modules.ModuleSatellite;
import zmaster587.advancedRocketry.item.ItemDataChip;
import zmaster587.advancedRocketry.item.ItemSatelliteChip;
import zmaster587.advancedRocketry.satellite.SatelliteData;
import zmaster587.advancedRocketry.util.IDataInventory;
import zmaster587.advancedRocketry.util.PlanetaryTravelHelper;
import zmaster587.libVulpes.LibVulpes;
import zmaster587.libVulpes.api.LibvulpesGuiRegistry;
import zmaster587.libVulpes.inventory.ContainerModular;
import zmaster587.libVulpes.inventory.GuiHandler;
import zmaster587.libVulpes.inventory.GuiHandler.guiId;
import zmaster587.libVulpes.inventory.modules.*;
import zmaster587.libVulpes.network.PacketHandler;
import zmaster587.libVulpes.network.PacketMachine;
import zmaster587.libVulpes.tile.TileInventoriedFEConsumer;
import zmaster587.libVulpes.util.INetworkMachine;
import zmaster587.libVulpes.util.ZUtils;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.LinkedList;
import java.util.List;

public class TileSatelliteControlCenter extends TileInventoriedFEConsumer implements INetworkMachine, IModularInventory, IButtonInventory, IDataInventory {


	//private ModuleText satelliteText;
	private SatelliteBase satellite;
	private ModuleText moduleText;
	private DataStorage data;

	public TileSatelliteControlCenter() {
	super(AdvancedRocketryTileEntityType.TILE_SATELLITE_CONTROL_CENTER, 10000, 2);
		data = new DataStorage();
		data.setMaxData(1000);
	}

	@Nonnull
	@Override
	@ParametersAreNonnullByDefault
	public int[] getSlotsForFace(Direction side) {
		return new int[0];
	}

	@Override
	public String getModularInventoryName() {
		return "block.advancedrocketry.satellitecontrolcenter";
	}

	@Override
	public boolean isItemValidForSlot(int slot, @Nonnull ItemStack stack) {
		return true;
	}

	@Override
	public boolean canPerformFunction() {
		return world.getGameTime() % 16 == 0 && getSatelliteFromSlot(0) != null;
	}

	@Override
	public int getPowerPerOperation() {
		return 1;
	}

	@Override
	public void performFunction() {
		if(world.isRemote)
			updateInventoryInfo();
	}

	@Override
	public void writeDataToNetwork(PacketBuffer out, byte packetId) {
	}

	@Override
	public void readDataFromNetwork(PacketBuffer in, byte packetId,
			CompoundNBT nbt) {

	}
	@Override
	public void useNetworkData(PlayerEntity player, Dist side, byte id,
			CompoundNBT nbt) {

		if(id == 0) {
			storeData(0);
		}
		else if( id == 100 ) {

			if(satellite != null && PlanetaryTravelHelper.isTravelAnywhereInPlanetarySystem(satellite.getDimensionId().get(), DimensionManager.getEffectiveDimId(ZUtils.getDimensionIdentifier(world), pos).getId())) {
				satellite.performAction(player, world, pos);
			}
		}
		else if( id == 101) {
			actionById(id - 100);
		}
	}

	@Override
	public void setInventorySlotContents(int slot, @Nonnull ItemStack stack) {
		super.setInventorySlotContents(slot, stack);
		satellite = getSatelliteFromSlot(0);
		updateInventoryInfo();
	}

	public void updateInventoryInfo() {
		if(moduleText != null) {

			if(satellite != null) {
				if(getUniversalEnergyStored() < getPowerPerOperation()) 
					moduleText.setText(LibVulpes.proxy.getLocalizedString("msg.notenoughpower"));

				else if(!PlanetaryTravelHelper.isTravelAnywhereInPlanetarySystem(satellite.getDimensionId().get(), DimensionManager.getEffectiveDimId(ZUtils.getDimensionIdentifier(world), pos).getId())) {
					moduleText.setText(satellite.getName() + "\n\n" + LibVulpes.proxy.getLocalizedString("msg.satctrlcenter.toofar") );
				}

				else
					moduleText.setText(satellite.getName() + "\n\n" + LibVulpes.proxy.getLocalizedString("msg.satctrlcenter.info") + "\n" + satellite.getInfo(world));
			}
			else
				moduleText.setText(LibVulpes.proxy.getLocalizedString("msg.satctrlcenter.nolink"));
		}
	}


	public SatelliteBase getSatelliteFromSlot(int slot) {

		ItemStack stack = getStackInSlot(slot);
		if(!stack.isEmpty() && stack.getItem() instanceof ItemSatelliteChip) {
			ItemSatelliteChip idchip = (ItemSatelliteChip)stack.getItem();

			return idchip.getSatellite(stack);
		}

		return null;
	}

	@Override
	public List<ModuleBase> getModules(int ID, PlayerEntity player) {

		List<ModuleBase> modules = new LinkedList<>();
		modules.add(new ModulePower(18, 20, this.energy));
		modules.add(new ModuleButton(116, 70, LibVulpes.proxy.getLocalizedString("msg.satctrlcenter.connect"), this,  zmaster587.libVulpes.inventory.TextureResources.buttonBuild).setAdditionalData(0));
		modules.add(new ModuleButton(173, 3, "", this, TextureResources.buttonKill, LibVulpes.proxy.getLocalizedString("msg.satctrlcenter.destroysat"), 24, 24).setAdditionalData(1));
		modules.add(new ModuleData(28, 20, 1, this, data));

		ModuleSatellite moduleSatellite = new ModuleSatellite(152, 10, this, 0);
		modules.add(moduleSatellite);

		//Try to assign a satellite ASAP
		moduleSatellite.setSatellite(getSatelliteFromSlot(0));

		moduleText = new ModuleText(60, 20, LibVulpes.proxy.getLocalizedString("msg.satctrlcenter.nolink"), 0x404040);
		modules.add(moduleText);

		updateInventoryInfo();
		return modules;
	}

	private void actionById(int buttonId)
	{

		if(buttonId == 0) {
			PacketHandler.sendToServer(new PacketMachine(this, (byte)(100 + buttonId)) );

		}
		else if(buttonId == 1) {
			ItemStack stack = getStackInSlot(0);

			if(!stack.isEmpty() && stack.getItem() instanceof ItemSatelliteChip) {
				ItemSatelliteChip idchip = (ItemSatelliteChip)stack.getItem();

				SatelliteBase satellite = idchip.getSatellite(stack);

				//Somebody might want to erase the chip of an already existing satellite
				if(satellite != null)
					DimensionManager.getInstance().getDimensionProperties(satellite.getDimensionId().get()).removeSatellite(satellite.getId());

				idchip.erase(stack);
				setInventorySlotContents(0, stack);
				PacketHandler.sendToServer(new PacketMachine(this, (byte)(100 + buttonId)) );
			}
		}
	}
	
	@Override
	public void onInventoryButtonPressed(ModuleButton buttonId) {


	}

	@Override
	public CompoundNBT write(CompoundNBT nbt) {
		super.write(nbt);

		CompoundNBT data = new CompoundNBT();

		this.data.writeToNBT(data);
		nbt.put("data", data);
		return nbt;
	}

	@Override
	public void read(BlockState state, CompoundNBT nbt) {
		super.read(state, nbt);

		CompoundNBT data = nbt.getCompound("data");
		this.data.readFromNBT(data);
	}

	@Override
	public void loadData(int id) {
	}

	@Override
	public void storeData(int id) {
		if(!world.isRemote) {
			ItemStack stack = getStackInSlot(1);
			if(!stack.isEmpty() && stack.getItem() instanceof ItemDataChip && stack.getCount() == 1) {
				ItemDataChip dataItem = (ItemDataChip)stack.getItem();
				data.removeData(dataItem.addData(stack, data.getData(), data.getDataType()), true);
			}
		}
		else {
			PacketHandler.sendToServer(new PacketMachine(this, (byte)0));
		}
	}

	@Override
	public int extractData(int maxAmount, DataType type, Direction dir, boolean commit) {
		//TODO
		
		SatelliteBase satellite = getSatelliteFromSlot(0);
		if(satellite instanceof SatelliteData && PlanetaryTravelHelper.isTravelAnywhereInPlanetarySystem(satellite.getDimensionId().get(), DimensionManager.getEffectiveDimId(ZUtils.getDimensionIdentifier(world), pos).getId())) {
				satellite.performAction(null, world, pos);
		}
		
		if(type == data.getDataType() ||  data.getDataType() == DataType.UNDEFINED) {
			return data.removeData(maxAmount, commit);
		}
		
		return 0;
	}

	@Override
	public int addData(int maxAmount, DataType type, Direction dir, boolean commit) {

		return data.addData(maxAmount, type, commit);
	}

	@Override
	public boolean canInteractWithContainer(PlayerEntity entity) {
		return true;
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
