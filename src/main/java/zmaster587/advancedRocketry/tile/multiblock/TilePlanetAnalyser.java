package zmaster587.advancedRocketry.tile.multiblock;

import io.netty.buffer.ByteBuf;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

import cpw.mods.fml.relauncher.Side;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import zmaster587.advancedRocketry.Inventory.TextureResources;
import zmaster587.advancedRocketry.Inventory.modules.IModularInventory;
import zmaster587.advancedRocketry.Inventory.modules.ModuleBase;
import zmaster587.advancedRocketry.Inventory.modules.ModuleButton;
import zmaster587.advancedRocketry.Inventory.modules.ModuleData;
import zmaster587.advancedRocketry.Inventory.modules.ModuleOutputSlotArray;
import zmaster587.advancedRocketry.Inventory.modules.ModulePower;
import zmaster587.advancedRocketry.Inventory.modules.ModuleProgress;
import zmaster587.advancedRocketry.Inventory.modules.ModuleRadioButton;
import zmaster587.advancedRocketry.Inventory.modules.ModuleSlider;
import zmaster587.advancedRocketry.Inventory.modules.ModuleSlotArray;
import zmaster587.advancedRocketry.Inventory.modules.ModuleText;
import zmaster587.advancedRocketry.Inventory.modules.ModuleTexturedSlotArray;
import zmaster587.advancedRocketry.Inventory.modules.ModuleToggleSwitch;
import zmaster587.advancedRocketry.api.AdvancedRocketryItems;
import zmaster587.advancedRocketry.client.render.util.IndicatorBarImage;
import zmaster587.advancedRocketry.client.render.util.ProgressBarImage;
import zmaster587.advancedRocketry.item.ItemSatelliteIdentificationChip;
import zmaster587.advancedRocketry.item.ItemPlanetIdentificationChip;
import zmaster587.advancedRocketry.network.PacketHandler;
import zmaster587.advancedRocketry.network.PacketMachine;
import zmaster587.advancedRocketry.tile.TileInputHatch;
import zmaster587.advancedRocketry.tile.TileOutputHatch;
import zmaster587.advancedRocketry.tile.data.TileDataBus;
import zmaster587.advancedRocketry.util.DataStorage;
import zmaster587.advancedRocketry.util.DataStorage.DataType;
import zmaster587.advancedRocketry.world.DimensionManager;
import zmaster587.libVulpes.gui.CommonResources;

public class TilePlanetAnalyser extends TileEntityMultiPowerConsumer implements IModularInventory {

	private static final Object[][][] structure = new Object[][][]{
		{{Blocks.stone_slab, 'c', Blocks.stone_slab},
			{Blocks.stone_slab, Blocks.stone_slab, Blocks.stone_slab}},

			{{'P','I', 'O'},
				{'D','D','D'}}
	};


	private TileDataBus dataCables[];
	private ModuleRadioButton densityButton, distanceButton, massButton;

	/** 0 = Vacuum, 1 = Normal, 2 = Dense **/
	private int distanceSetting;

	private int densitySetting, massSetting;
	private int densityData, distanceData, massData;
	TileInventoryHatch inputHatch, outputHatch;

	public TilePlanetAnalyser() {
		dataCables = new TileDataBus[3];
		powerPerTick = 100;
	}

	@Override
	protected HashSet<Block> getAllowableWildCardBlocks() {
		HashSet<Block> set = super.getAllowableWildCardBlocks();
		set.add(Blocks.iron_block);
		return set;
	}

	@Override
	protected void integrateTile(TileEntity tile) {
		super.integrateTile(tile);

		if(tile instanceof TileDataBus) {
			for(int i = 0; i < dataCables.length; i++) {
				if(dataCables[i] == null) {
					dataCables[i] = (TileDataBus)tile;

					switch(i) {
					case 0:
						dataCables[i].lockData(DataStorage.DataType.DISTANCE);
						break;
					case 1:
						dataCables[i].lockData(DataStorage.DataType.MASS);
						break;
					case 2:
						dataCables[i].lockData(DataStorage.DataType.ATMOSPHEREDENSITY);
					}
					break;
				}
			}
		}
		else if(tile instanceof TileInputHatch) {
			inputHatch = (TileInventoryHatch) tile;
		}
		else if(tile instanceof TileOutputHatch) {
			outputHatch = (TileOutputHatch) tile;
		}
	}

	@Override
	public void deconstructMultiBlock(World world, int destroyedX,
			int destroyedY, int destroyedZ, boolean blockBroken) {

		//Make sure to unlock the data cables
		for(int i = 0; i < dataCables.length; i++) {
			if(dataCables[i] != null)
				dataCables[i].lockData(null);
		}

		super.deconstructMultiBlock(world, destroyedX, destroyedY, destroyedZ,
				blockBroken);
	}

	@Override
	public void resetCache() {
		super.resetCache();
		Arrays.fill(dataCables, null);
		inputHatch = null;
		outputHatch = null;
	}

	@Override
	public Object[][][] getStructure() {
		return structure;
	}

	@Override
	public String getMachineName() {
		return "tile.planetanalyser.name";
	}

	@Override
	public AxisAlignedBB getRenderBoundingBox() { //TODO
		return AxisAlignedBB.getBoundingBox(xCoord -2,yCoord -2, zCoord -2, xCoord + 2, yCoord + 2, zCoord + 2);
	}

	private boolean canProcess() {

		for(int i = 0; i < dataCables.length; i++) {
			if(dataCables[i].getData() < 100)
				return false;
		}

		ItemStack inputStack = inputHatch.getStackInSlot(0);

		return inputStack != null && 
				inputStack.getItem() instanceof ItemSatelliteIdentificationChip && 
				((ItemSatelliteIdentificationChip)inputStack.getItem()).getSatellite(inputStack) == null &&
				outputHatch.getStackInSlot(0) == null;
	}

	private void process() {

		//Disable user input while the machine is processing to prevent the outcome being modified
		distanceButton.setEnabled(false);
		densityButton.setEnabled(false);
		massButton.setEnabled(false);

		distanceData = dataCables[0].extractData(1000, DataType.DISTANCE);
		massData = dataCables[1].extractData(1000, DataType.MASS);
		densityData = dataCables[2].extractData(1000, DataType.ATMOSPHEREDENSITY);

		inputHatch.decrStackSize(0, 1);

		completionTime = 500;
	}

	@Override
	protected void processComplete() {
		super.processComplete();

		//The machine is done re-enable user input
		distanceButton.setEnabled(true);
		densityButton.setEnabled(true);
		massButton.setEnabled(true);

		if(!worldObj.isRemote) {
			//TODO;
			ItemStack outputItem = new ItemStack(AdvancedRocketryItems.itemPlanetIdChip);
			ItemPlanetIdentificationChip item = (ItemPlanetIdentificationChip)outputItem.getItem();

			int baseAtmosphere, baseDistance, baseGravity, atmosphereFactor, distanceFactor, gravityFactor;

			if(this.densityData < 200) 
				atmosphereFactor = baseAtmosphere = 100;
			else if(this.densityData < 300) {
				//75, 100 ,125 +/- 75

				baseAtmosphere = 75 + (densitySetting*25);
				atmosphereFactor = 75;
			}
			else if(this.densityData < 400) {
				//50, 100, 150 +/- 50

				baseAtmosphere = 50 + (densitySetting*50);
				atmosphereFactor = 25;
			}
			else if(this.densityData < 450) {
				//25, 100, 175 +/- 25

				baseAtmosphere = 25 + (densitySetting*75);
				atmosphereFactor = 25;
			}
			else {
				//50, 100, 150 +/- 1
				baseAtmosphere = 50 + (densitySetting*50);
				atmosphereFactor = 1;
			}



			if(this.distanceData < 200) 
				distanceFactor = baseDistance = 100;
			else if(this.distanceData < 300) {
				//75, 100 ,125 +/- 75

				baseDistance = 75 + (distanceSetting*25);
				distanceFactor = 75;
			}
			else if(this.distanceData < 400) {
				//50, 100, 150 +/- 50

				baseDistance = 50 + (distanceSetting*50);
				distanceFactor = 25;
			}
			else if(this.distanceData < 450) {
				//25, 100, 175 +/- 25

				baseDistance = 25 + (distanceSetting*75);
				distanceFactor = 25;
			}
			else {
				//50, 100, 150 +/- 1
				baseDistance = 50 + (distanceSetting*50);
				distanceFactor = 1;
			}

			if(this.massData < 200) 
				gravityFactor = baseGravity = 100;
			else if(this.massData < 300) {
				//75, 100 ,125 +/- 75

				baseGravity = 75 + (massSetting*25);
				gravityFactor = 75;
			}
			else if(this.massData < 400) {
				//50, 100, 150 +/- 50

				baseGravity = 50 + (massSetting*50);
				gravityFactor = 25;
			}
			else if(this.massData < 450) {
				//25, 100, 175 +/- 25

				baseGravity = 25 + (massSetting*75);
				gravityFactor = 25;
			}
			else {
				//50, 100, 150 +/- 1
				baseGravity = 50 + (massSetting*50);
				gravityFactor = 1;
			}

			//TODO: fix naming system
			int dimensionId = DimensionManager.getInstance().generateRandom("", baseAtmosphere, baseDistance, baseGravity, atmosphereFactor, distanceFactor, gravityFactor);

			item.setDimensionId(outputItem, dimensionId);
			outputHatch.setInventorySlotContents(0, outputItem);
		}

		distanceData = 0;
		densityData = 0;
		massData = 0;
	}

	@Override
	public boolean completeStructure() {
		boolean result = super.completeStructure();
		if(result) {
			worldObj.setBlockMetadataWithNotify(xCoord, yCoord, zCoord, this.blockMetadata | 8, 2);
		}
		else
			worldObj.setBlockMetadataWithNotify(xCoord, yCoord, zCoord, this.blockMetadata & 7, 2);
		return result;
	}

	@Override
	public void onInventoryButtonPressed(int buttonId) {
		if(buttonId == 0)
			super.onInventoryButtonPressed(buttonId);
		else if(buttonId == 1) { //Process button is pressed
			PacketHandler.sendToServer(new PacketMachine(this, (byte)2));
		}
		else if(buttonId == 2) {
			//densitySetting = densityButton.getOptionSelected();
			distanceSetting = distanceButton.getOptionSelected();
			PacketHandler.sendToServer(new PacketMachine(this,(byte)101));
		}
	}

	@Override
	public void writeDataToNetwork(ByteBuf out, byte id) {
		super.writeDataToNetwork(out, id);
		if(id > 100) {
			out.writeInt(densitySetting);
			out.writeInt(distanceButton.getOptionSelected());
			out.writeInt(massButton.getOptionSelected());
		}

	}

	@Override
	public void readDataFromNetwork(ByteBuf in, byte packetId,
			NBTTagCompound nbt) {
		super.readDataFromNetwork(in, packetId, nbt);

		if(packetId > 100) {
			nbt.setInteger("state", in.readInt());
			nbt.setInteger("state2", in.readInt());
			nbt.setInteger("state3", in.readInt());
		}
	}

	@Override
	public void useNetworkData(EntityPlayer player, Side side, byte id,
			NBTTagCompound nbt) {
		super.useNetworkData(player, side, id, nbt);

		if(id > 100) {
			densitySetting = nbt.getInteger("state");
			//densityButton.setOptionSeleted(densitySetting);

			distanceSetting = nbt.getInteger("state2");
			distanceButton.setOptionSeleted(distanceSetting);

			massSetting = nbt.getInteger("state3");
			massButton.setOptionSeleted(massSetting);
		}
		else if(id == 2) {
			if(canProcess())
				process();
		}
	}

	@Override
	public List<ModuleBase> getModules() {

		LinkedList<ModuleBase> modules = new LinkedList<ModuleBase>();
		modules.add(new ModulePower(18, 20, getBatteries()));

		//TODO: write NBT
		for(int i = 0; i < 3; i++) {
			if(dataCables[i] != null)
				modules.add(new ModuleData(32 + (i*30), 20, 0, dataCables[i],  dataCables[i].getDataObject()));
		}

		int xStart = 150;
		int yStart = 14;

		modules.add(new ModuleTexturedSlotArray(xStart, yStart, inputHatch, 0, 1, TextureResources.idChip));
		modules.add(new ModuleOutputSlotArray(xStart, yStart + 40, outputHatch, 0, 1));


		modules.add(new ModuleProgress(xStart, yStart + 20, 0, new ProgressBarImage(217,0, 17, 17, 234, 0, ForgeDirection.DOWN, TextureResources.progressBars), this));


		modules.add(new ModuleButton(xStart, yStart + 20, 1, "", this, TextureResources.buttonNull, "Process discovery", 17, 17));


		int buttonXSize = 44;
		int buttonYSize = 16;
		xStart = 15;

		List<ModuleToggleSwitch> toggleSwitches = new ArrayList<ModuleToggleSwitch>();
		ResourceLocation toggleImages[] = {TextureResources.buttonBuild[0], TextureResources.buttonBuild[1]};

		/*toggleSwitches.add(new ModuleToggleSwitch(xStart, 86, 2, "None", this, toggleImages, buttonXSize, buttonYSize, densitySetting == 0));
		toggleSwitches.add(new ModuleToggleSwitch(xStart, 106, 2, "Normal", this, toggleImages, buttonXSize, buttonYSize, densitySetting == 1));
		toggleSwitches.add(new ModuleToggleSwitch(xStart, 126, 2, "Dense", this, toggleImages, buttonXSize, buttonYSize, densitySetting == 2));
		modules.add(densityButton = new ModuleRadioButton(this, toggleSwitches));*/

		modules.add(new ModuleSlider(xStart, 86, 1, new IndicatorBarImage(2, 7, 12, 81, 17, 0, 6, 6, 1, 0, ForgeDirection.UP, TextureResources.rocketHud), this));

		toggleSwitches = new ArrayList<ModuleToggleSwitch>();

		toggleSwitches.add(new ModuleToggleSwitch(xStart + 50, 86, 2, "Close", this, toggleImages, buttonXSize, buttonYSize, distanceSetting == 0));
		toggleSwitches.add(new ModuleToggleSwitch(xStart + 50, 106, 2, "Normal", this, toggleImages, buttonXSize, buttonYSize, distanceSetting == 1));
		toggleSwitches.add(new ModuleToggleSwitch(xStart + 50, 126, 2, "Far", this, toggleImages, buttonXSize, buttonYSize, distanceSetting == 2));
		modules.add(distanceButton = new ModuleRadioButton(this, toggleSwitches));

		toggleSwitches = new ArrayList<ModuleToggleSwitch>();

		toggleSwitches.add(new ModuleToggleSwitch(xStart + 100, 86, 2, "Small", this, toggleImages, buttonXSize, buttonYSize, massSetting == 0));
		toggleSwitches.add(new ModuleToggleSwitch(xStart + 100, 106, 2, "Normal", this, toggleImages, buttonXSize, buttonYSize, massSetting == 1));
		toggleSwitches.add(new ModuleToggleSwitch(xStart + 100, 126, 2, "Large", this, toggleImages, buttonXSize, buttonYSize, massSetting == 2));
		modules.add(massButton = new ModuleRadioButton(this, toggleSwitches));

		if(isRunning()) {
			densityButton.setEnabled(false);
			distanceButton.setEnabled(false);
			massButton.setEnabled(false);
		}

		modules.add(new ModuleText(xStart, 76, "Atmos",0x404040));
		modules.add(new ModuleText(xStart + 50, 76, "Distance",0x404040));
		modules.add(new ModuleText(xStart + 110, 76, "Mass",0x404040));



		return modules;
	}

	@Override
	public int getProgress(int id) {
		if(id == 1) {
			return densitySetting;
		}
		else
			return super.getProgress(id);
	}

	@Override
	public float getNormallizedProgress(int id) {
		if(id == 1)
			return getProgress(id)/ (float)getTotalProgress(id);
		else
			return completionTime > 0 ? currentTime/(float)completionTime : 0f;
	}

	@Override
	public void setProgress(int id, int progress) {
		if(id == 1) {
			densitySetting = progress;
			PacketHandler.sendToServer(new PacketMachine(this, (byte)101));
		}
		else
			super.setProgress(id, progress);
	}

	@Override
	public void setTotalProgress(int id, int progress) {
		if(id == 0)
			super.setTotalProgress(id, progress);
	}

	@Override
	public int getTotalProgress(int id) {
		if(id == 1)
			return 200;
		else
			return super.getTotalProgress(id);
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt) {
		super.readFromNBT(nbt);

		densitySetting = nbt.getInteger("density");
		distanceSetting = nbt.getInteger("distance");
		massSetting = nbt.getInteger("mass");

		//If this key exists then they should all exist
		if(nbt.hasKey("densityData")) {
			densityData = nbt.getInteger("densityData");
			distanceData = nbt.getInteger("distanceData");
			massData = nbt.getInteger("massData");
		}
	}

	@Override
	public void writeToNBT(NBTTagCompound nbt) {
		super.writeToNBT(nbt);

		nbt.setInteger("density", densitySetting);
		nbt.setInteger("distance", distanceSetting);
		nbt.setInteger("mass", massSetting);

		if(densityData > 0) {
			nbt.setInteger("densityData", densityData);
			nbt.setInteger("distanceData", distanceData);
			nbt.setInteger("massData", massData);
		}
	}

}
