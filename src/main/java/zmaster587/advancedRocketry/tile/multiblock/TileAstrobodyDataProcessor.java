package zmaster587.advancedRocketry.tile.multiblock;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import zmaster587.advancedRocketry.api.AdvancedRocketryItems;
import zmaster587.advancedRocketry.api.AdvancedRocketryTileEntityType;
import zmaster587.advancedRocketry.api.DataStorage;
import zmaster587.advancedRocketry.api.DataStorage.DataType;
import zmaster587.advancedRocketry.inventory.TextureResources;
import zmaster587.advancedRocketry.inventory.modules.ModuleData;
import zmaster587.advancedRocketry.item.ItemAsteroidChip;
import zmaster587.libVulpes.LibVulpes;
import zmaster587.libVulpes.block.BlockMeta;
import zmaster587.libVulpes.block.multiblock.BlockMultiblockMachine;
import zmaster587.libVulpes.inventory.modules.*;
import zmaster587.libVulpes.network.PacketHandler;
import zmaster587.libVulpes.network.PacketMachine;
import zmaster587.libVulpes.tile.multiblock.TileMultiPowerConsumer;
import zmaster587.libVulpes.tile.multiblock.hatch.TileInputHatch;
import zmaster587.libVulpes.tile.multiblock.hatch.TileInventoryHatch;
import zmaster587.libVulpes.tile.multiblock.hatch.TileOutputHatch;
import zmaster587.libVulpes.util.EmbeddedInventory;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class TileAstrobodyDataProcessor extends TileMultiPowerConsumer implements IModularInventory, IInventory {

	private static final Object[][][] structure = new Object[][][]{
		{{"slabs", 'c', "slabs"},
			{"slabs", "slabs", "slabs"}},

			{{'P','I', 'O'},
				{'D','D','D'}}
	};


	private TileDataBus[] dataCables;
	private boolean researchingDistance, researchingAtmosphere, researchingMass;
	private int atmosphereProgress, distanceProgress, massProgress;
	private static final int maxResearchTime = 20;
	private EmbeddedInventory inventory;
	private TileInventoryHatch inputHatch, outputHatch;

	public TileAstrobodyDataProcessor() {
		super(AdvancedRocketryTileEntityType.TILE_ASTROBODY_DATA_PROCESSOR);
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
	public boolean shouldHideBlock(World world, BlockPos pos2, BlockState tile) { return true; }

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
	public void deconstructMultiBlock(World world, BlockPos destroyedPos, boolean blockBroken, BlockState state) {

		//Make sure to unlock the data cables
		for (TileDataBus dataCable : dataCables) {
			if (dataCable != null)
				dataCable.lockData(null);
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
		return "block.advancedrocketry.astrobodydataprocessor";
	}

	@Override
	@Nonnull
	public AxisAlignedBB getRenderBoundingBox() {

		return new AxisAlignedBB(pos.add(-2,-2,-2),pos.add(2,2,2));
	}

	@Override
	public void onInventoryUpdated() {

		super.onInventoryUpdated();
		if(inputHatch == null)
			return;

		if(getStackInSlot(0) == ItemStack.EMPTY) {
			for(int j = 0; j < inputHatch.getSizeInventory(); j++) {
				ItemStack stack2 = inputHatch.getStackInSlot(j);
				if(!stack2.isEmpty() && stack2.getItem() instanceof ItemAsteroidChip && ((ItemAsteroidChip)stack2.getItem()).getUUID(stack2) != null) {
					setInventorySlotContents(0, inputHatch.decrStackSize(j, 1));
					break;
				}
			}
		}
		attemptAllResearchStart();
	}

	@Override
	protected void processComplete() {
		if(!world.isRemote) {
			//Move chip to output
			for(int i = 0; i < outputHatch.getSizeInventory(); i++) {
				if(outputHatch.getStackInSlot(i) == ItemStack.EMPTY) {
					outputHatch.setInventorySlotContents(i, this.decrStackSize(0, 1));
					return;
				}
			}
		}
	}

	@Override
	public boolean completeStructure(BlockState state) {
		boolean result = super.completeStructure(state);
		((BlockMultiblockMachine)world.getBlockState(pos).getBlock()).setBlockState(world, world.getBlockState(pos), pos, result);
		return result;
	}

	private void incrementDataOnChip(int planetId, int amount, DataStorage.DataType dataType) {
		ItemStack stack = getStackInSlot(0);
		if(!stack.isEmpty() && stack.getItem().equals(AdvancedRocketryItems.itemAsteroidChip)) {
			ItemAsteroidChip item = (ItemAsteroidChip)stack.getItem();
			item.addData(stack, amount, dataType);
			int maxData = item.getMaxData(stack);

			if(item.getData(stack, DataType.COMPOSITION) == maxData && item.getData(stack, DataType.DISTANCE) == maxData && item.getData(stack, DataType.MASS) == maxData) {
				processComplete();
			}
		}
	}

	private void attemptAllResearchStart() {
		ItemStack stack = getStackInSlot(0);
		if(stack.isEmpty() || !(stack.getItem() instanceof ItemAsteroidChip))
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
				return dataCables[0].extractData(1, DataStorage.DataType.COMPOSITION, Direction.UP, !simulate);
		case DISTANCE:
			if(dataCables[1] != null)
				return dataCables[1].extractData(1, DataStorage.DataType.DISTANCE, Direction.UP, !simulate);
		case MASS:
			if(dataCables[2] != null)
				return dataCables[2].extractData(1, DataStorage.DataType.MASS, Direction.UP, !simulate);

		default:
			return 0;
		}
	}

	@Override
	protected void onRunningPoweredTick() {
		if(completionTime > 0)
			super.onRunningPoweredTick();

		ItemStack stack = getStackInSlot(0);

		if(!stack.isEmpty() && stack.getItem().equals(AdvancedRocketryItems.itemAsteroidChip)) {
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
		return (!getStackInSlot(0).isEmpty() && getStackInSlot(0).getItem().equals(AdvancedRocketryItems.itemAsteroidChip) && (researchingAtmosphere || researchingDistance || researchingMass));
	}

	@Override
	public void onInventoryButtonPressed(ModuleButton button) {
		int buttonId = button.getAdditionalData() == null ? -1 : (int)button.getAdditionalData();
		
		if(buttonId == 0)
			super.onInventoryButtonPressed(button);
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
	public void writeDataToNetwork(PacketBuffer out, byte id) {
		super.writeDataToNetwork(out, id);
		if(id == 4) {
			out.writeInt((researchingAtmosphere ? 1 : 0) | (researchingDistance ? 2 : 0) | (researchingMass ? 4 : 0));
		}

	}

	@Override
	public void readDataFromNetwork(PacketBuffer in, byte packetId,
			CompoundNBT nbt) {
		super.readDataFromNetwork(in, packetId, nbt);

		if(packetId == 3 || packetId == 4 || packetId > 100) {
			nbt.putInt("state", in.readInt());
		}

	}

	@Override
	public void useNetworkData(PlayerEntity player, Dist side, byte id,
			CompoundNBT nbt) {
		super.useNetworkData(player, side, id, nbt);

		if (id == 4) {
			int states = nbt.getInt("state");

			researchingAtmosphere = (states & 1) != 0;
			researchingDistance = (states & 2) != 0;
			researchingMass =	  (states & 4) != 0;

			attemptAllResearchStart();

			this.markDirty();
		}
	}

	@Override
	public List<ModuleBase> getModules(int ID, PlayerEntity player) {

		LinkedList<ModuleBase> modules = new LinkedList<>();
		modules.add( new ModulePower(18, 20, getBatteries()));

		//TODO: write NBT
		for(int i = 0; i < 3; i++) {
			if(dataCables[i] != null)
				modules.add(new ModuleData(32 + (i*50), 20, 0, dataCables[i],  dataCables[i].getDataObject()));
		}

		int xStart = 150;
		int yStart = 14;

		modules.add(new ModuleText(15, 76, "Research",0x404040));

		modules.add(new ModuleToggleSwitch(15, 86, "", this,  zmaster587.libVulpes.inventory.TextureResources.buttonToggleImage, LibVulpes.proxy.getLocalizedString("msg.abdp.compositionresearch"), 11, 26, researchingAtmosphere).setAdditionalData(4));
		modules.add(new ModuleToggleSwitch(65, 86, "", this,  zmaster587.libVulpes.inventory.TextureResources.buttonToggleImage, LibVulpes.proxy.getLocalizedString("msg.abdp.distanceresearch"), 11, 26, researchingDistance).setAdditionalData(5));
		modules.add(new ModuleToggleSwitch(125, 86, "", this,  zmaster587.libVulpes.inventory.TextureResources.buttonToggleImage, LibVulpes.proxy.getLocalizedString("msg.abdp.massresearch"), 11, 26, researchingMass).setAdditionalData(6));

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
		DimensionProperties properties = DimensionManager.getInstance().getStar(new ResourceLocation(Constants.STAR_NAMESPACE, "0")).getPlanets().get(0);

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
	public void read(BlockState state, CompoundNBT nbt) {
		super.read(state, nbt);
		inventory.readFromNBT(nbt);
		atmosphereProgress = nbt.getInt("atmosphereProgress");
		distanceProgress = nbt.getInt("distanceProgress");
		massProgress = nbt.getInt("massProgress");
	}
	
	@Override
	protected void readNetworkData(CompoundNBT nbt) {
		super.readNetworkData(nbt);
		researchingAtmosphere = nbt.getBoolean("researchingAtmosphere");
		researchingDistance = nbt.getBoolean("researchingDistance");
		researchingMass = nbt.getBoolean("researchingMass");
	}

	@Override
	public CompoundNBT write(CompoundNBT nbt) {
		super.write(nbt);
		inventory.write(nbt);

		nbt.putBoolean("researchingAtmosphere", researchingAtmosphere);
		nbt.putBoolean("researchingDistance", researchingDistance);
		nbt.putBoolean("researchingMass", researchingMass);
		nbt.putInt("atmosphereProgress", atmosphereProgress);
		nbt.putInt("distanceProgress", distanceProgress);
		nbt.putInt("massProgress", massProgress);

		return nbt;
	}

	@Override
	protected void writeNetworkData(CompoundNBT nbt) {
		super.writeNetworkData(nbt);
		nbt.putBoolean("researchingAtmosphere", researchingAtmosphere);
		nbt.putBoolean("researchingDistance", researchingDistance);
		nbt.putBoolean("researchingMass", researchingMass);
	}
	
	@Override
	public int getSizeInventory() {
		return inventory.getSizeInventory();
	}

	@Override
	@Nonnull
	public ItemStack getStackInSlot(int slot) {
		return inventory.getStackInSlot(slot);
	}

	@Override
	@Nonnull
	public ItemStack decrStackSize(int slot, int amount) {
		return inventory.decrStackSize(slot, amount);
	}

	@Override
	public void setInventorySlotContents(int slot, @Nonnull ItemStack stack) {
		inventory.setInventorySlotContents(slot, stack);
		onInventoryUpdated();
	}

	@Override
	public int getInventoryStackLimit() {
		return 1;
	}

	@Override
	public boolean isUsableByPlayer(PlayerEntity player) {
		return pos.distanceSq(new BlockPos(player.getPositionVec())) < 4096;
	}
	@Override
	public boolean isEmpty() {
		return inventory.isEmpty();
	}

	@Override
	@ParametersAreNonnullByDefault
	public void openInventory(PlayerEntity player) {

	}

	@Override
	@ParametersAreNonnullByDefault
	public void closeInventory(PlayerEntity player) {

	}

	@Override
	public boolean isItemValidForSlot(int slot, @Nonnull ItemStack stack) {
		return false;//inventory.isItemValidForSlot(slot, stack);
	}

	@Override
	@Nonnull
	public ItemStack removeStackFromSlot(int index) {
		return inventory.removeStackFromSlot(index);
	}

	@Override
	public void clear() {
		inventory.clear();
	}
}
