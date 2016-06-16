package zmaster587.advancedRocketry.tile.multiblock;

import io.netty.buffer.ByteBuf;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import cpw.mods.fml.relauncher.Side;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import zmaster587.advancedRocketry.api.AdvancedRocketryBlocks;
import zmaster587.advancedRocketry.api.AdvancedRocketryItems;
import zmaster587.advancedRocketry.api.DataStorage;
import zmaster587.advancedRocketry.api.DataStorage.DataType;
import zmaster587.advancedRocketry.client.render.util.ProgressBarImage;
import zmaster587.advancedRocketry.inventory.TextureResources;
import zmaster587.advancedRocketry.inventory.modules.IModularInventory;
import zmaster587.advancedRocketry.inventory.modules.ModuleBase;
import zmaster587.advancedRocketry.inventory.modules.ModuleButton;
import zmaster587.advancedRocketry.inventory.modules.ModuleData;
import zmaster587.advancedRocketry.inventory.modules.ModuleOutputSlotArray;
import zmaster587.advancedRocketry.inventory.modules.ModulePower;
import zmaster587.advancedRocketry.inventory.modules.ModuleProgress;
import zmaster587.advancedRocketry.inventory.modules.ModuleSlotArray;
import zmaster587.advancedRocketry.inventory.modules.ModuleText;
import zmaster587.advancedRocketry.inventory.modules.ModuleTexturedSlotArray;
import zmaster587.advancedRocketry.inventory.modules.ModuleToggleSwitch;
import zmaster587.advancedRocketry.item.ItemAsteroidChip;
import zmaster587.advancedRocketry.item.ItemPlanetIdentificationChip;
import zmaster587.advancedRocketry.network.PacketHandler;
import zmaster587.advancedRocketry.network.PacketMachine;
import zmaster587.advancedRocketry.tile.TileInputHatch;
import zmaster587.advancedRocketry.tile.data.TileDataBus;
import zmaster587.advancedRocketry.util.EmbeddedInventory;
import zmaster587.advancedRocketry.util.ITilePlanetSystemSelectable;
import zmaster587.libVulpes.block.BlockMeta;

public class TilePlanetAnalyser extends TileMultiPowerConsumer implements IModularInventory, IInventory, ITilePlanetSystemSelectable {

	private static final Object[][][] structure = new Object[][][]{
		{{AdvancedRocketryBlocks.blockPlanetSelector, 'c', Blocks.stone_slab},
			{Blocks.stone_slab, Blocks.stone_slab, Blocks.stone_slab}},

			{{'P','I', 'I'},
				{'D','D','D'}}
	};


	private TileDataBus dataCables[];
	private ModuleText planetText;
	private int selectedPlanetId = -1;
	private boolean researchingDistance, researchingAtmosphere, researchingMass;
	private int atmosphereProgress, distanceProgress, massProgress;
	private static final int maxResearchTime = 20;
	private EmbeddedInventory inventory;
	TileInventoryHatch inputHatch, inputHatch2;

	public TilePlanetAnalyser() {
		dataCables = new TileDataBus[3];
		powerPerTick = 100;
		massProgress = distanceProgress = atmosphereProgress = -1;
		inventory = new EmbeddedInventory(3);
	}

	@Override
	public List<BlockMeta> getAllowableWildCardBlocks() {
		List<BlockMeta> list = super.getAllowableWildCardBlocks();
		list.add(new BlockMeta(Blocks.iron_block,BlockMeta.WILDCARD));
		return list;
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
						dataCables[i].lockData(DataStorage.DataType.COMPOSITION);
						break;
					case 1:
						dataCables[i].lockData(DataStorage.DataType.DISTANCE);
						break;
					case 2:
						dataCables[i].lockData(DataStorage.DataType.MASS);
					}
					break;
				}
			}
		}
		else if(tile instanceof TileInputHatch) {
			if(inputHatch == null)
				inputHatch = (TileInventoryHatch) tile;
			else
				inputHatch2 = (TileInventoryHatch) tile; 
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
		inputHatch2 = null;
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

		ItemStack inputStack = this.getStackInSlot(0);

		return inputStack != null && 
				inputStack.getItem().equals(AdvancedRocketryItems.itemAsteroidChip) && 
				!inputStack.hasTagCompound() &&
				this.getStackInSlot(1) == null;
	}

	private void process() {

		//Disable user input while the machine is processing to prevent the outcome being modified

		this.decrStackSize(0, 1);

		completionTime = 500;
	}

	@Override
	protected void processComplete() {
		super.processComplete();

		//The machine is done re-enable user input

		if(!worldObj.isRemote) {
			//TODO;
			ItemStack outputItem = new ItemStack(AdvancedRocketryItems.itemAsteroidChip);
			ItemAsteroidChip item = (ItemAsteroidChip)outputItem.getItem();
			
			//Get UUID
			item.setUUID(outputItem, (new Random(worldObj.getTotalWorldTime())).nextLong() % 10000);
			item.setMaxData(outputItem, 2000);
			//TODO: fix naming system
			//int dimensionId = DimensionManager.getInstance().generateRandom("", baseAtmosphere, baseDistance, baseGravity, atmosphereFactor, distanceFactor, gravityFactor);

			this.setInventorySlotContents(1, outputItem);
		}
	}

	@Override
	public boolean completeStructure() {
		boolean result = super.completeStructure();
		if(result) {
			worldObj.setBlockMetadataWithNotify(xCoord, yCoord, zCoord, this.getBlockMetadata() | 8, 2);
		}
		else
			worldObj.setBlockMetadataWithNotify(xCoord, yCoord, zCoord, this.getBlockMetadata() & 7, 2);
		return result;
	}

	@Override
	public ItemStack getChipWithId(int id) {
		if(inputHatch != null) {
			for(int i = 0; i < inputHatch.getSizeInventory(); i++) {
				ItemStack stack = inputHatch.getStackInSlot(i);

				if(stack != null && stack.getItem() instanceof ItemPlanetIdentificationChip) {
					ItemPlanetIdentificationChip item = (ItemPlanetIdentificationChip)stack.getItem();
					if(item.hasValidDimension(stack) && item.getDimensionId(stack) == id)
						return stack;
				}
			}
		}
		
		if(inputHatch2 != null) {
			for(int i = 0; i < inputHatch2.getSizeInventory(); i++) {
				ItemStack stack = inputHatch2.getStackInSlot(i);

				if(stack != null && stack.getItem() instanceof ItemPlanetIdentificationChip) {
					ItemPlanetIdentificationChip item = (ItemPlanetIdentificationChip)stack.getItem();
					if(item.hasValidDimension(stack) && item.getDimensionId(stack) == id)
						return stack;
				}
			}
		}
		
		return null;
	}

	private void incrementDataOnChip(int planetId, int amount, DataStorage.DataType dataType) {
		ItemStack stack = getStackInSlot(2);
		if(stack != null && stack.getItem().equals(AdvancedRocketryItems.itemAsteroidChip)) {
			ItemAsteroidChip item = (ItemAsteroidChip)stack.getItem();
			item.addData(stack, amount, dataType);
		}
	}

	private void attemptAllResearchStart() {

		if(researchingAtmosphere && atmosphereProgress < 0 && dataCables[0].extractData(1, DataStorage.DataType.COMPOSITION) > 0)
			atmosphereProgress = 0;

		if(researchingDistance && distanceProgress < 0 && dataCables[1].extractData(1, DataStorage.DataType.DISTANCE) > 0)
			distanceProgress = 0;

		if(researchingMass && massProgress < 0 && dataCables[2].extractData(1, DataStorage.DataType.MASS) > 0)
			massProgress = 0;

		this.markDirty();
		worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
	}

	@Override
	protected void onRunningPoweredTick() {
		if(completionTime > 0)
			super.onRunningPoweredTick();

		if(atmosphereProgress > -1) {
			if(atmosphereProgress == maxResearchTime) {
				atmosphereProgress = -1;

				if(!worldObj.isRemote) {
					incrementDataOnChip(selectedPlanetId, 1, DataType.COMPOSITION);
					attemptAllResearchStart();
				}
			}
			else
				atmosphereProgress++;
		}

		if(massProgress > -1) {
			if(massProgress == maxResearchTime) {

				massProgress = -1;

				if(!worldObj.isRemote) {
					incrementDataOnChip(selectedPlanetId, 1, DataType.MASS);
					attemptAllResearchStart();
				}
			}
			else
				massProgress++;
		}

		if(distanceProgress > -1) {
			if(distanceProgress == maxResearchTime) {
				distanceProgress = -1;
				if(!worldObj.isRemote) {
					incrementDataOnChip(selectedPlanetId, 1, DataType.DISTANCE);
					attemptAllResearchStart();
				}
			}
			else
				distanceProgress++;
		}
	}

	@Override
	public boolean isRunning() {
		return super.isRunning() || (getStackInSlot(2) != null && getStackInSlot(2).getItem().equals(AdvancedRocketryItems.itemAsteroidChip) && (atmosphereProgress > -1 || distanceProgress > -1 || massProgress > -1));
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
			//distanceSetting = distanceButton.getOptionSelected();
			//PacketHandler.sendToServer(new PacketMachine(this,(byte)101));
		}
		else if(buttonId == 4) {
			researchingAtmosphere = !researchingAtmosphere;
			PacketHandler.sendToServer(new PacketMachine(this, (byte)4));
		}
		else if(buttonId == 5) {
			researchingDistance = !researchingDistance;
			PacketHandler.sendToServer(new PacketMachine(this, (byte)4));
		}
		else if(buttonId == 6) {
			researchingMass = ! researchingMass;
			PacketHandler.sendToServer(new PacketMachine(this, (byte)4));
		}
	}

	@Override
	public void writeDataToNetwork(ByteBuf out, byte id) {
		super.writeDataToNetwork(out, id);
		if(id == 3) 
			out.writeInt(selectedPlanetId);
		if(id == 4) {
			out.writeInt((researchingAtmosphere ? 1 : 0) | (researchingDistance ? 2 : 0) | (researchingMass ? 4 : 0));
		}

	}

	@Override
	public void readDataFromNetwork(ByteBuf in, byte packetId,
			NBTTagCompound nbt) {
		super.readDataFromNetwork(in, packetId, nbt);

		if(packetId == 3 || packetId == 4 || packetId > 100) {
			nbt.setInteger("state", in.readInt());
		}

	}

	@Override
	public void useNetworkData(EntityPlayer player, Side side, byte id,
			NBTTagCompound nbt) {
		super.useNetworkData(player, side, id, nbt);

		if(id == 3) {
			setSelectedPlanetId(nbt.getInteger("state"));
			this.markDirty();
			worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
		}
		else if(id == 2) {
			if(canProcess())
				process();
		}
		else if (id == 4) {
			int states = nbt.getInteger("state");

			researchingAtmosphere = (states & 1) != 0;
			researchingDistance = (states & 2) != 0;
			researchingMass =	  (states & 4) != 0;

			attemptAllResearchStart();

			this.markDirty();
			worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
		}
	}

	@Override
	public List<ModuleBase> getModules(int ID) {

		LinkedList<ModuleBase> modules = new LinkedList<ModuleBase>();
		modules.add(new ModulePower(18, 20, getBatteries()));

		//TODO: write NBT
		for(int i = 0; i < 3; i++) {
			if(dataCables[i] != null)
				modules.add(new ModuleData(32 + (i*30), 20, 0, dataCables[i],  dataCables[i].getDataObject()));
		}

		int xStart = 150;
		int yStart = 14;

		modules.add(new ModuleTexturedSlotArray(xStart, yStart, this, 0, 1, TextureResources.idChip));
		modules.add(new ModuleOutputSlotArray(xStart, yStart + 40, this, 1, 2));


		modules.add(new ModuleProgress(xStart, yStart + 20, 0, new ProgressBarImage(217,0, 17, 17, 234, 0, ForgeDirection.DOWN, TextureResources.progressBars), this));

		modules.add(new ModuleButton(xStart, yStart + 20, 1, "", this, TextureResources.buttonNull, "Process discovery", 17, 17));

		modules.add(new ModuleText(15, 76, "Research",0x404040));

		modules.add(new ModuleToggleSwitch(15, 86, 4, "", this, TextureResources.buttonToggleImage, "Composition Research", 11, 26, researchingAtmosphere));
		modules.add(new ModuleToggleSwitch(65, 86, 5, "", this, TextureResources.buttonToggleImage,"Distance Research", 11, 26, researchingDistance));
		modules.add(new ModuleToggleSwitch(125, 86, 6, "", this, TextureResources.buttonToggleImage, "Mass Research", 11, 26, researchingMass));

		//Research indicators
		modules.add(new ModuleProgress(26, 86, 1, TextureResources.progressScience, this));
		modules.add(new ModuleProgress(76, 86, 2, TextureResources.progressScience, this));
		modules.add(new ModuleProgress(136, 86, 3, TextureResources.progressScience, this));
		
		modules.add(new ModuleSlotArray(26, 120, this, 2, 3));

		/*modules.add(new ModuleText(15, 76, "Atmos",0x404040));
		modules.add(new ModuleText(65, 76, "Distance",0x404040));
		modules.add(new ModuleText(125, 76, "Mass",0x404040));*/

		/*List<ModuleBase> subModule = new LinkedList<ModuleBase>();
		int solarRadius = 100;
		int center = 1000/2;

		subModule.add(new ModuleButton(center-solarRadius/2, center-solarRadius/2, 99, "", this, new ResourceLocation[] { TextureResources.locationSunPng}, solarRadius, solarRadius));
		DimensionProperties properties = DimensionManager.getSol().getPlanets().get(0);

		subModule.add(new ModuleButton(center + properties.getMapDisplayPositionX() , center + properties.getMapDisplayPositionY(), 99, "", this, new ResourceLocation[] { properties.getPlanetIcon() }, properties.getName(), properties.getMapDisplayeSize(), properties.getMapDisplayeSize()));

		properties = DimensionManager.getInstance().getDimensionProperties(2);

		subModule.add(new ModuleButton(center + properties.getMapDisplayPositionX() , center + properties.getMapDisplayPositionY(), 99, "", this, new ResourceLocation[] { properties.getPlanetIcon() }, properties.getName(), properties.getMapDisplayeSize(), properties.getMapDisplayeSize()));*/

		return modules;
	}


	@Override
	public void setSelectedPlanetId(int id) {
		selectedPlanetId = id;
		if(worldObj.isRemote) {
			PacketHandler.sendToServer(new PacketMachine(this, (byte)3));
			setPlanetNumberText(selectedPlanetId);
		}
	}

	private void setPlanetNumberText(int i) {
		if(worldObj.isRemote && planetText != null)
			planetText.setText("Planet " + i);
	}

	public int getSelectedPlanetId() {
		return selectedPlanetId;
	}

	@Override
	public List<Integer> getVisiblePlanets() {
		ArrayList<Integer> visibleList = new ArrayList<Integer>();
		visibleList.add(0);

		if(inputHatch != null) {
			for(int i = 0; i < inputHatch.getSizeInventory(); i++) {
				ItemStack stack = inputHatch.getStackInSlot(i);

				if(stack != null && stack.getItem() instanceof ItemPlanetIdentificationChip) {
					ItemPlanetIdentificationChip item = (ItemPlanetIdentificationChip)stack.getItem();
					if(item.hasValidDimension(stack) && item.getData(stack, DataType.DISTANCE) > 50 && item.getData(stack, DataType.MASS) > 50)
						visibleList.add(item.getDimensionId(stack));
				}
			}
		}
		
		if(inputHatch2 != null) {
			for(int i = 0; i < inputHatch2.getSizeInventory(); i++) {
				ItemStack stack = inputHatch2.getStackInSlot(i);

				if(stack != null && stack.getItem() instanceof ItemPlanetIdentificationChip) {
					ItemPlanetIdentificationChip item = (ItemPlanetIdentificationChip)stack.getItem();
					if(item.hasValidDimension(stack) && item.getData(stack, DataType.DISTANCE) > 50 && item.getData(stack, DataType.MASS) > 50)
						visibleList.add(item.getDimensionId(stack));
				}
			}
		}

		return visibleList;
	}

	@Override
	public int getProgress(int id) {
		if(id == 0)
			return super.getProgress(id);
		else if(id == 1)
			return atmosphereProgress;
		else if(id == 2)
			return distanceProgress;
		else if(id == 3)
			return massProgress;
		return 0;
	}

	@Override
	public int getTotalProgress(int id) {
		if(id == 0) 
			return super.getTotalProgress(id);
		return maxResearchTime;
	}

	@Override
	public float getNormallizedProgress(int id) {
		if(id != 0)
			return getProgress(id)/ (float)getTotalProgress(id);
		else
			return completionTime > 0 ? currentTime/(float)completionTime : 0f;
	}

	@Override
	public void setProgress(int id, int progress) {
		if(id == 0)
			super.setProgress(id, progress);
		else if(id == 1)
			atmosphereProgress = progress;
		else if(id == 2)
			distanceProgress = progress;
		else
			massProgress = progress;
	}

	@Override
	public void setTotalProgress(int id, int progress) {
		if(id == 0)
			super.setTotalProgress(id, progress);
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt) {
		super.readFromNBT(nbt);
		inventory.readFromNBT(nbt);

		selectedPlanetId = nbt.getInteger("planet");

		if(worldObj != null)
			setPlanetNumberText(selectedPlanetId);

		researchingAtmosphere = nbt.getBoolean("researchingAtmosphere");
		researchingDistance = nbt.getBoolean("researchingDistance");
		researchingMass = nbt.getBoolean("researchingMass");
		atmosphereProgress = nbt.getInteger("atmosphereProgress");
		distanceProgress = nbt.getInteger("distanceProgress");
		massProgress = nbt.getInteger("massProgress");
	}

	@Override
	public void writeToNBT(NBTTagCompound nbt) {
		super.writeToNBT(nbt);
		inventory.writeToNBT(nbt);

		nbt.setInteger("planet", selectedPlanetId);

		nbt.setBoolean("researchingAtmosphere", researchingAtmosphere);
		nbt.setBoolean("researchingDistance", researchingDistance);
		nbt.setBoolean("researchingMass", researchingMass);
		nbt.setInteger("atmosphereProgress", atmosphereProgress);
		nbt.setInteger("distanceProgress", distanceProgress);
		nbt.setInteger("massProgress", massProgress);
	}

	@Override
	public int getSizeInventory() {
		return inventory.getSizeInventory();
	}

	@Override
	public ItemStack getStackInSlot(int slot) {
		return inventory.getStackInSlot(slot);
	}

	@Override
	public ItemStack decrStackSize(int slot, int amount) {
		return inventory.decrStackSize(slot, amount);
	}

	@Override
	public ItemStack getStackInSlotOnClosing(int slot) {
		return inventory.getStackInSlotOnClosing(slot);
	}

	@Override
	public void setInventorySlotContents(int slot, ItemStack stack) {
		inventory.setInventorySlotContents(slot, stack);
	}

	@Override
	public String getInventoryName() {
		return getMachineName();
	}

	@Override
	public boolean hasCustomInventoryName() {
		return false;
	}

	@Override
	public int getInventoryStackLimit() {
		return inventory.getInventoryStackLimit();
	}

	@Override
	public boolean isUseableByPlayer(EntityPlayer player) {
		return player.getDistance(xCoord, yCoord, zCoord) < 64;
	}

	@Override
	public void openInventory() {

	}

	@Override
	public void closeInventory() {

	}

	@Override
	public boolean isItemValidForSlot(int slot, ItemStack stack) {
		return false;//inventory.isItemValidForSlot(slot, stack);
	}
}
