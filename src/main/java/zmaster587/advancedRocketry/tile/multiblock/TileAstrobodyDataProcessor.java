package zmaster587.advancedRocketry.tile.multiblock;

import io.netty.buffer.ByteBuf;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import zmaster587.advancedRocketry.api.AdvancedRocketryBlocks;
import zmaster587.advancedRocketry.api.AdvancedRocketryItems;
import zmaster587.advancedRocketry.api.DataStorage;
import zmaster587.advancedRocketry.api.DataStorage.DataType;
import zmaster587.advancedRocketry.inventory.TextureResources;
import zmaster587.advancedRocketry.inventory.modules.ModuleData;
import zmaster587.advancedRocketry.item.ItemAsteroidChip;
import zmaster587.advancedRocketry.item.ItemPlanetIdentificationChip;
import zmaster587.advancedRocketry.tile.hatch.TileDataBus;
import zmaster587.advancedRocketry.util.ITilePlanetSystemSelectable;
import zmaster587.advancedRocketry.world.util.MultiData;
import zmaster587.libVulpes.block.BlockMeta;
import zmaster587.libVulpes.block.multiblock.BlockMultiblockMachine;
import zmaster587.libVulpes.client.util.ProgressBarImage;
import zmaster587.libVulpes.inventory.modules.IModularInventory;
import zmaster587.libVulpes.inventory.modules.ModuleBase;
import zmaster587.libVulpes.inventory.modules.ModuleButton;
import zmaster587.libVulpes.inventory.modules.ModuleOutputSlotArray;
import zmaster587.libVulpes.inventory.modules.ModulePower;
import zmaster587.libVulpes.inventory.modules.ModuleProgress;
import zmaster587.libVulpes.inventory.modules.ModuleSlotArray;
import zmaster587.libVulpes.inventory.modules.ModuleText;
import zmaster587.libVulpes.inventory.modules.ModuleTexturedSlotArray;
import zmaster587.libVulpes.inventory.modules.ModuleToggleSwitch;
import zmaster587.libVulpes.network.PacketHandler;
import zmaster587.libVulpes.network.PacketMachine;
import zmaster587.libVulpes.tile.multiblock.TileMultiPowerConsumer;
import zmaster587.libVulpes.tile.multiblock.hatch.TileInputHatch;
import zmaster587.libVulpes.tile.multiblock.hatch.TileInventoryHatch;
import zmaster587.libVulpes.tile.multiblock.hatch.TileOutputHatch;
import zmaster587.libVulpes.util.EmbeddedInventory;

public class TileAstrobodyDataProcessor extends TileMultiPowerConsumer implements IModularInventory, IInventory {

	private static final Object[][][] structure = new Object[][][]{
		{{Blocks.STONE_SLAB, 'c', Blocks.STONE_SLAB},
			{Blocks.STONE_SLAB, Blocks.STONE_SLAB, Blocks.STONE_SLAB}},

			{{'P','I', 'O'},
				{'D','D','D'}}
	};


	private TileDataBus dataCables[];
	private boolean researchingDistance, researchingAtmosphere, researchingMass;
	private int atmosphereProgress, distanceProgress, massProgress;
	private static final int maxResearchTime = 20;
	private EmbeddedInventory inventory;
	TileInventoryHatch inputHatch, outputHatch;

	public TileAstrobodyDataProcessor() {
		dataCables = new TileDataBus[3];
		powerPerTick = 100;
		massProgress = distanceProgress = atmosphereProgress = -1;
		inventory = new EmbeddedInventory(1);
	}

	@Override
	public List<BlockMeta> getAllowableWildCardBlocks() {
		List<BlockMeta> list = super.getAllowableWildCardBlocks();
		list.add(new BlockMeta(Blocks.IRON_BLOCK,BlockMeta.WILDCARD));
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
			inputHatch = (TileInventoryHatch) tile;

		}
		else if(tile instanceof TileOutputHatch) {
			outputHatch = (TileInventoryHatch) tile; 
		}
	}

	@Override
	public void deconstructMultiBlock(World world, BlockPos destroyedPos, boolean blockBroken, IBlockState state) {

		//Make sure to unlock the data cables
		for(int i = 0; i < dataCables.length; i++) {
			if(dataCables[i] != null)
				dataCables[i].lockData(null);
		}

		super.deconstructMultiBlock(world, destroyedPos,
				blockBroken, state);
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
	public AxisAlignedBB getRenderBoundingBox() {

		return new AxisAlignedBB(pos.add(-2,-2,-2),pos.add(2,2,2));
	}

	@Override
	public void onInventoryUpdated() {

		super.onInventoryUpdated();
		if(inputHatch == null)
			return;

		if(getStackInSlot(0) == null) {
			for(int j = 0; j < inputHatch.getSizeInventory(); j++) {
				ItemStack stack2 = inputHatch.getStackInSlot(j);
				if(stack2 != null && stack2.getItem() instanceof ItemAsteroidChip && ((ItemAsteroidChip)stack2.getItem()).getUUID(stack2) != null) {
					setInventorySlotContents(0, inputHatch.decrStackSize(j, 1));
					break;
				}
			}
		}
		attemptAllResearchStart();
	}

	@Override
	protected void processComplete() {
		super.processComplete();

		//The machine is done re-enable user input

		if(!world.isRemote) {
			ItemStack outputItem = new ItemStack(AdvancedRocketryItems.itemAsteroidChip);
			ItemAsteroidChip item = (ItemAsteroidChip)outputItem.getItem();

			//Get UUID
			item.setUUID(outputItem, (new Random(world.getTotalWorldTime())).nextLong() % 10000);
			item.setMaxData(outputItem, 2000);
			//TODO: fix naming system
			//int dimensionId = DimensionManager.getInstance().generateRandom("", baseAtmosphere, baseDistance, baseGravity, atmosphereFactor, distanceFactor, gravityFactor);

			for(int i = 0; i < outputHatch.getSizeInventory(); i++) {
				if(outputHatch.getStackInSlot(i) == null) {
					outputHatch.setInventorySlotContents(i, outputItem);
					return;
				}
			}

			this.setInventorySlotContents(1, outputItem);
		}
	}

	@Override
	public boolean completeStructure(IBlockState state) {
		boolean result = super.completeStructure(state);
		if(result) {
			((BlockMultiblockMachine)world.getBlockState(pos).getBlock()).setBlockState(world, world.getBlockState(pos), pos, true);
		}
		else
			((BlockMultiblockMachine)world.getBlockState(pos).getBlock()).setBlockState(world, world.getBlockState(pos), pos, false);
		return result;
	}

	private void incrementDataOnChip(int planetId, int amount, DataStorage.DataType dataType) {
		ItemStack stack = getStackInSlot(0);
		if(stack != null && stack.getItem().equals(AdvancedRocketryItems.itemAsteroidChip)) {
			ItemAsteroidChip item = (ItemAsteroidChip)stack.getItem();
			item.addData(stack, amount, dataType);
			int maxData = item.getMaxData(stack);

			if(item.getData(stack, DataType.COMPOSITION) == maxData && item.getData(stack, DataType.DISTANCE) == maxData && item.getData(stack, DataType.MASS) == maxData) {
				for(int i = 0; i < outputHatch.getSizeInventory(); i++) {
					if(outputHatch.getStackInSlot(i) == null) {
						outputHatch.setInventorySlotContents(i, stack);

						setInventorySlotContents(0, null);
						return;
					}
				}
			}
		}
	}

	private void attemptAllResearchStart() {
		ItemStack stack = getStackInSlot(0);
		if(stack == null || !(stack.getItem() instanceof ItemAsteroidChip))
			return;

		ItemAsteroidChip item = (ItemAsteroidChip)stack.getItem();

		if(researchingAtmosphere && atmosphereProgress < 0 && extractData(1, DataStorage.DataType.COMPOSITION, false) > 0 && !item.isFull(stack, DataStorage.DataType.COMPOSITION))
			atmosphereProgress = 0;

		if(researchingDistance && distanceProgress < 0 && extractData(1, DataStorage.DataType.DISTANCE, false) > 0 && !item.isFull(stack, DataStorage.DataType.DISTANCE))
			distanceProgress = 0;

		if(researchingMass && massProgress < 0 && extractData(1, DataStorage.DataType.MASS, false) > 0 && !item.isFull(stack, DataStorage.DataType.MASS))
			massProgress = 0;

		this.markDirty();
	}

	private int extractData(int amt, DataStorage.DataType type, boolean simulate) {
		switch(type) {
		case COMPOSITION:
			if(dataCables[0] != null)
				return dataCables[0].extractData(1, DataStorage.DataType.COMPOSITION, EnumFacing.UP, !simulate);
		case DISTANCE:
			if(dataCables[1] != null)
				return dataCables[1].extractData(1, DataStorage.DataType.DISTANCE, EnumFacing.UP, !simulate);
		case MASS:
			if(dataCables[2] != null)
				return dataCables[2].extractData(1, DataStorage.DataType.MASS, EnumFacing.UP, !simulate);

		default:
			return 0;
		}
	}

	@Override
	protected void onRunningPoweredTick() {
		if(completionTime > 0)
			super.onRunningPoweredTick();

		ItemStack stack = getStackInSlot(0);

		if(stack != null && stack.getItem().equals(AdvancedRocketryItems.itemAsteroidChip)) {
			ItemAsteroidChip item = (ItemAsteroidChip) stack.getItem();

			if(researchingAtmosphere && extractData(1, DataStorage.DataType.COMPOSITION, true) > 0 && !item.isFull(stack, DataStorage.DataType.COMPOSITION)) {
				if(atmosphereProgress == maxResearchTime) {
					atmosphereProgress = -1;

					if(!world.isRemote) {
						incrementDataOnChip(0, 1, DataType.COMPOSITION);
						extractData(1, DataStorage.DataType.COMPOSITION, false);
						//attemptAllResearchStart();
					}
				}
				else
					atmosphereProgress++;
			}

			if(researchingMass && extractData(1, DataStorage.DataType.MASS, true) > 0 && !item.isFull(stack, DataStorage.DataType.MASS)) {
				if(massProgress == maxResearchTime) {

					massProgress = -1;

					if(!world.isRemote) {
						incrementDataOnChip(0, 1, DataType.MASS);
						extractData(1, DataStorage.DataType.MASS, false);
						//attemptAllResearchStart();
					}
				}
				else
					massProgress++;
			}

			if(researchingDistance && extractData(1, DataStorage.DataType.DISTANCE, true) > 0  && !item.isFull(stack, DataStorage.DataType.DISTANCE)) {
				if(distanceProgress == maxResearchTime) {
					distanceProgress = -1;
					if(!world.isRemote) {
						incrementDataOnChip(0, 1, DataType.DISTANCE);
						extractData(1, DataStorage.DataType.DISTANCE, false);
						//attemptAllResearchStart();
					}
				}
				else
					distanceProgress++;
			}
		}
	}

	@Override
	public boolean isRunning() {
		return (getStackInSlot(0) != null && getStackInSlot(0).getItem().equals(AdvancedRocketryItems.itemAsteroidChip) && (researchingAtmosphere || researchingDistance || researchingMass));
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

		if (id == 4) {
			int states = nbt.getInteger("state");

			researchingAtmosphere = (states & 1) != 0;
			researchingDistance = (states & 2) != 0;
			researchingMass =	  (states & 4) != 0;

			attemptAllResearchStart();

			this.markDirty();
		}
	}

	@Override
	public List<ModuleBase> getModules(int ID, EntityPlayer player) {

		LinkedList<ModuleBase> modules = new LinkedList<ModuleBase>();
		modules.add(new ModulePower(18, 20, getBatteries()));

		//TODO: write NBT
		for(int i = 0; i < 3; i++) {
			if(dataCables[i] != null)
				modules.add(new ModuleData(32 + (i*50), 20, 0, dataCables[i],  dataCables[i].getDataObject()));
		}

		int xStart = 150;
		int yStart = 14;

		modules.add(new ModuleText(15, 76, "Research",0x404040));

		modules.add(new ModuleToggleSwitch(15, 86, 4, "", this,  zmaster587.libVulpes.inventory.TextureResources.buttonToggleImage, "Composition Research", 11, 26, researchingAtmosphere));
		modules.add(new ModuleToggleSwitch(65, 86, 5, "", this,  zmaster587.libVulpes.inventory.TextureResources.buttonToggleImage,"Distance Research", 11, 26, researchingDistance));
		modules.add(new ModuleToggleSwitch(125, 86, 6, "", this,  zmaster587.libVulpes.inventory.TextureResources.buttonToggleImage, "Mass Research", 11, 26, researchingMass));

		//Research indicators
		modules.add(new ModuleProgress(26, 86, 1, TextureResources.progressScience, this));
		modules.add(new ModuleProgress(76, 86, 2, TextureResources.progressScience, this));
		modules.add(new ModuleProgress(136, 86, 3, TextureResources.progressScience, this));

		modules.add(new ModuleSlotArray(76, 120, this, 0, 1));

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
		return 0f;
	}

	@Override
	public void setProgress(int id, int progress) {
		if(id == 1)
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
		atmosphereProgress = nbt.getInteger("atmosphereProgress");
		distanceProgress = nbt.getInteger("distanceProgress");
		massProgress = nbt.getInteger("massProgress");
	}
	
	@Override
	protected void readNetworkData(NBTTagCompound nbt) {
		super.readNetworkData(nbt);
		researchingAtmosphere = nbt.getBoolean("researchingAtmosphere");
		researchingDistance = nbt.getBoolean("researchingDistance");
		researchingMass = nbt.getBoolean("researchingMass");
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound nbt) {
		super.writeToNBT(nbt);
		inventory.writeToNBT(nbt);

		nbt.setBoolean("researchingAtmosphere", researchingAtmosphere);
		nbt.setBoolean("researchingDistance", researchingDistance);
		nbt.setBoolean("researchingMass", researchingMass);
		nbt.setInteger("atmosphereProgress", atmosphereProgress);
		nbt.setInteger("distanceProgress", distanceProgress);
		nbt.setInteger("massProgress", massProgress);

		return nbt;
	}

	@Override
	protected void writeNetworkData(NBTTagCompound nbt) {
		super.writeNetworkData(nbt);
		nbt.setBoolean("researchingAtmosphere", researchingAtmosphere);
		nbt.setBoolean("researchingDistance", researchingDistance);
		nbt.setBoolean("researchingMass", researchingMass);
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
	public void setInventorySlotContents(int slot, ItemStack stack) {
		inventory.setInventorySlotContents(slot, stack);
		onInventoryUpdated();
	}

	@Override
	public String getName() {
		return getMachineName();
	}

	@Override
	public boolean hasCustomName() {
		return false;
	}

	@Override
	public int getInventoryStackLimit() {
		return inventory.getInventoryStackLimit();
	}

	@Override
	public boolean isUsableByPlayer(EntityPlayer player) {
		return player.getDistanceSq(pos) < 4096;
	}
	@Override
	public boolean isEmpty() {
		return inventory.isEmpty();
	}

	@Override
	public void openInventory(EntityPlayer player) {

	}

	@Override
	public void closeInventory(EntityPlayer player) {

	}

	@Override
	public boolean isItemValidForSlot(int slot, ItemStack stack) {
		return false;//inventory.isItemValidForSlot(slot, stack);
	}

	@Override
	public ItemStack removeStackFromSlot(int index) {
		return inventory.removeStackFromSlot(index);
	}

	@Override
	public int getField(int id) {
		return inventory.getField(id);
	}

	@Override
	public void setField(int id, int value) {
		inventory.setField(id, value);
	}

	@Override
	public int getFieldCount() {
		return inventory.getFieldCount();
	}

	@Override
	public void clear() {
		inventory.clear();
	}
}
