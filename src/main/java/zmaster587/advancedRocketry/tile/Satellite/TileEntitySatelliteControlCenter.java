package zmaster587.advancedRocketry.tile.Satellite;

import java.util.LinkedList;
import java.util.List;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.fml.relauncher.Side;
import zmaster587.advancedRocketry.api.DataStorage;
import zmaster587.advancedRocketry.api.DataStorage.DataType;
import zmaster587.advancedRocketry.api.satellite.IDataHandler;
import zmaster587.advancedRocketry.api.satellite.SatelliteBase;
import zmaster587.advancedRocketry.dimension.DimensionManager;
import zmaster587.advancedRocketry.inventory.TextureResources;
import zmaster587.advancedRocketry.inventory.modules.ModuleData;
import zmaster587.advancedRocketry.inventory.modules.ModuleSatellite;
import zmaster587.advancedRocketry.item.ItemData;
import zmaster587.advancedRocketry.item.ItemSatelliteIdentificationChip;
import zmaster587.advancedRocketry.satellite.SatelliteData;
import zmaster587.advancedRocketry.util.IDataInventory;
import zmaster587.libVulpes.inventory.modules.IButtonInventory;
import zmaster587.libVulpes.inventory.modules.IModularInventory;
import zmaster587.libVulpes.inventory.modules.ModuleBase;
import zmaster587.libVulpes.inventory.modules.ModuleButton;
import zmaster587.libVulpes.inventory.modules.ModulePower;
import zmaster587.libVulpes.inventory.modules.ModuleSlotArray;
import zmaster587.libVulpes.inventory.modules.ModuleText;
import zmaster587.libVulpes.inventory.modules.ModuleToggleSwitch;
import zmaster587.libVulpes.network.PacketHandler;
import zmaster587.libVulpes.network.PacketMachine;
import zmaster587.libVulpes.tile.TileInventoriedRFConsumer;
import zmaster587.libVulpes.util.INetworkMachine;

public class TileEntitySatelliteControlCenter extends TileInventoriedRFConsumer implements INetworkMachine, IModularInventory, IButtonInventory, IDataInventory {


	//ModuleText satelliteText;
	ModuleSatellite moduleSatellite;
	ModuleText moduleText;
	DataStorage data;

	public TileEntitySatelliteControlCenter() {
		super(10000, 2);

		data = new DataStorage();
		data.setMaxData(1000);
	}

	@Override
	public int[] getSlotsForFace(EnumFacing side) {
		return new int[0];
	}

	@Override
	public String getModularInventoryName() {
		return "container.satelliteMonitor";
	}

	@Override
	public boolean isItemValidForSlot(int slot, ItemStack stack) {
		return true;
	}

	@Override
	public boolean canPerformFunction() {
		return world.getTotalWorldTime() % 16 == 0 && getSatelliteFromSlot(0) != null;
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
	public void writeDataToNetwork(ByteBuf out, byte packetId) {
	}

	@Override
	public void readDataFromNetwork(ByteBuf in, byte packetId,
			NBTTagCompound nbt) {

	}
	@Override
	public void useNetworkData(EntityPlayer player, Side side, byte id,
			NBTTagCompound nbt) {

		if(id == 0) {
			storeData(0);
		}
		else if( id == 100 ) {

			SatelliteBase satellite = moduleSatellite.getSatellite();
			if(satellite != null && DimensionManager.getInstance().areDimensionsInSamePlanetMoonSystem(satellite.getDimensionId(), DimensionManager.getEffectiveDimId(world, pos).getId())) {
				satellite.performAction(player, world, pos);
			}
		}
		else if( id == 101) {
			onInventoryButtonPressed(id - 100);
		}
	}

	@Override
	public void setInventorySlotContents(int slot, ItemStack stack) {
		super.setInventorySlotContents(slot, stack);
		moduleSatellite.setSatellite(getSatelliteFromSlot(0));
		updateInventoryInfo();
	}

	public void updateInventoryInfo() {
		if(moduleText != null) {

			SatelliteBase satellite = moduleSatellite.getSatellite();
			if(satellite != null) {
				if(getEnergyStored() < getPowerPerOperation()) 
					moduleText.setText("Not Enough power!");

				else if(!DimensionManager.getInstance().areDimensionsInSamePlanetMoonSystem(satellite.getDimensionId(), DimensionManager.getEffectiveDimId(world, pos).getId())) {
					moduleText.setText(satellite.getName() + "\n\nToo Far" );
				}

				else
					moduleText.setText(satellite.getName() + "\n\nInfo:\n" + satellite.getInfo(world));
			}
			else
				moduleText.setText("No Link...");
		}
	}


	public SatelliteBase getSatelliteFromSlot(int slot) {

		ItemStack stack = getStackInSlot(slot);
		if(stack != null && stack.getItem() instanceof ItemSatelliteIdentificationChip) {
			ItemSatelliteIdentificationChip idchip = (ItemSatelliteIdentificationChip)stack.getItem();

			return idchip.getSatellite(stack);
		}

		return null;
	}

	@Override
	public List<ModuleBase> getModules(int ID, EntityPlayer player) {

		List<ModuleBase> modules = new LinkedList<ModuleBase>();
		modules.add(new ModulePower(18, 20, this.energy));
		modules.add(new ModuleButton(116, 70, 0, "Connect!", this,  zmaster587.libVulpes.inventory.TextureResources.buttonBuild));
		modules.add(new ModuleButton(173, 3, 1, "", this, TextureResources.buttonKill, "Destroy Satellite", 24, 24));
		modules.add(new ModuleData(28, 20, 1, this, data));

		moduleSatellite = new ModuleSatellite(152, 10, this, 0);
		modules.add(moduleSatellite);

		//Try to assign a satellite ASAP
		moduleSatellite.setSatellite(getSatelliteFromSlot(0));

		moduleText = new ModuleText(60, 20, "No Link...", 0x404040);
		modules.add(moduleText);

		updateInventoryInfo();
		return modules;
	}

	@Override
	public void onInventoryButtonPressed(int buttonId) {

		if(buttonId == 0) {
			PacketHandler.sendToServer(new PacketMachine(this, (byte)(100 + buttonId)) );

		}
		else if(buttonId == 1) {
			ItemStack stack = getStackInSlot(0);

			if(stack != null && stack.getItem() instanceof ItemSatelliteIdentificationChip) {
				ItemSatelliteIdentificationChip idchip = (ItemSatelliteIdentificationChip)stack.getItem();

				SatelliteBase satellite = idchip.getSatellite(stack);

				//Somebody might want to erase the chip of an already existing satellite
				if(satellite != null)
					DimensionManager.getInstance().getDimensionProperties(satellite.getDimensionId()).removeSatellite(satellite.getId());

				idchip.erase(stack);
				setInventorySlotContents(0, stack);
				PacketHandler.sendToServer(new PacketMachine(this, (byte)(100 + buttonId)) );
			}
		}

	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound nbt) {
		super.writeToNBT(nbt);

		NBTTagCompound data = new NBTTagCompound();

		this.data.writeToNBT(data);
		nbt.setTag("data", data);
		return nbt;
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt) {
		super.readFromNBT(nbt);

		NBTTagCompound data = nbt.getCompoundTag("data");
		this.data.readFromNBT(data);
	}

	@Override
	public void loadData(int id) {
	}

	@Override
	public void storeData(int id) {
		if(!world.isRemote) {
			ItemStack inv = getStackInSlot(1);
			if(inv != null && inv.getItem() instanceof ItemData && inv.getCount() == 1) {
				ItemData dataItem = (ItemData)inv.getItem();
				data.removeData(dataItem.addData(inv, data.getData(), data.getDataType()), true);
			}
		}
		else {
			PacketHandler.sendToServer(new PacketMachine(this, (byte)0));
		}
	}

	@Override
	public int extractData(int maxAmount, DataType type, EnumFacing dir, boolean commit) {
		//TODO
		if(type == data.getDataType() ||  data.getDataType() == DataType.UNDEFINED) {
			SatelliteBase satellite = getSatelliteFromSlot(0);
			if(satellite != null && satellite instanceof SatelliteData && DimensionManager.getInstance().areDimensionsInSamePlanetMoonSystem(satellite.getDimensionId(), DimensionManager.getEffectiveDimId(world, pos).getId())) {
				satellite.performAction(null, world, pos);
			}
			
			return data.removeData(maxAmount, commit);
		}
		return 0;
	}

	@Override
	public int addData(int maxAmount, DataType type, EnumFacing dir, boolean commit) {

		return data.addData(maxAmount, type, commit);
	}

	@Override
	public boolean canInteractWithContainer(EntityPlayer entity) {
		return true;
	}
}
