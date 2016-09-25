package zmaster587.advancedRocketry.tile.multiblock;

import java.util.LinkedList;
import java.util.List;

import zmaster587.advancedRocketry.api.DataStorage;
import zmaster587.advancedRocketry.api.DataStorage.DataType;
import zmaster587.advancedRocketry.inventory.TextureResources;
import zmaster587.advancedRocketry.inventory.modules.ModuleData;
import zmaster587.advancedRocketry.item.ItemData;
import zmaster587.advancedRocketry.tile.hatch.TileDataBus;
import zmaster587.advancedRocketry.util.IDataInventory;
import zmaster587.libVulpes.block.BlockMeta;
import zmaster587.libVulpes.block.multiblock.BlockMultiblockMachine;
import zmaster587.libVulpes.inventory.modules.IModularInventory;
import zmaster587.libVulpes.inventory.modules.ModuleBase;
import zmaster587.libVulpes.inventory.modules.ModuleProgress;
import zmaster587.libVulpes.network.PacketHandler;
import zmaster587.libVulpes.network.PacketMachine;
import zmaster587.libVulpes.tile.multiblock.TileMultiBlock;
import zmaster587.libVulpes.tile.multiblock.TileMultiPowerConsumer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraftforge.fml.relauncher.Side;

public class TileObservatory extends TileMultiPowerConsumer implements IModularInventory, IDataInventory {


	private static final Object[][][] structure = new Object[][][]{

		{	{Blocks.AIR, Blocks.AIR, Blocks.AIR, Blocks.AIR, Blocks.AIR}, 
			{Blocks.AIR, Blocks.STONE, Blocks.GLASS, Blocks.STONE, Blocks.AIR},
			{Blocks.AIR, Blocks.STONE, Blocks.STONE, Blocks.STONE, Blocks.AIR},
			{Blocks.AIR, Blocks.STONE, Blocks.STONE, Blocks.STONE, Blocks.AIR},
			{Blocks.AIR, Blocks.AIR, Blocks.AIR, Blocks.AIR, Blocks.AIR}},

			{	{Blocks.AIR,Blocks.AIR,Blocks.AIR,Blocks.AIR,Blocks.AIR}, 
				{Blocks.AIR, Blocks.STONE, Blocks.STONE, Blocks.STONE, Blocks.AIR},
				{Blocks.AIR, Blocks.STONE, Blocks.GLASS, Blocks.STONE, Blocks.AIR},
				{Blocks.AIR, Blocks.STONE, Blocks.STONE, Blocks.STONE, Blocks.AIR},
				{Blocks.AIR,Blocks.AIR,Blocks.AIR,Blocks.AIR,Blocks.AIR}},

				{	{null, Blocks.STONE, Blocks.STONE, Blocks.STONE, null}, 
					{Blocks.STONE, Blocks.AIR, Blocks.AIR, Blocks.AIR, Blocks.STONE},
					{Blocks.STONE, Blocks.AIR, Blocks.AIR, Blocks.AIR, Blocks.STONE},
					{Blocks.STONE, Blocks.AIR, Blocks.GLASS, Blocks.AIR, Blocks.STONE},
					{null, Blocks.STONE, Blocks.STONE, Blocks.STONE, null}},

					{	{ null,'*', 'c', '*',null}, 
						{'*',Blocks.STONE, Blocks.STONE, Blocks.STONE,'*'},
						{'*',Blocks.STONE, Blocks.STONE, Blocks.STONE,'*'},
						{'*',Blocks.STONE, Blocks.STONE, Blocks.STONE,'*'},
						{null,'*', '*', '*', null}},

						{	{null,'*', '*', '*', null}, 
							{'*',Blocks.STONE, Blocks.STONE, Blocks.STONE,'*'},
							{'*',Blocks.STONE, Blocks.STONE, Blocks.STONE,'*'},
							{'*',Blocks.STONE, Blocks.STONE, Blocks.STONE,'*'},
							{null,'*', '*', '*',null}}};

	final static int openTime = 100;
	final static int observationtime = 1000;
	int openProgress;
	private LinkedList<TileDataBus> dataCables;
	private boolean isOpen;
	ItemStack dataChip;

	public TileObservatory() {
		openProgress = 0;
		completionTime = observationtime;
		dataCables = new LinkedList<TileDataBus>();
	}

	public float getOpenProgress() {
		return openProgress/(float)openTime;
	}

	@Override
	protected void integrateTile(TileEntity tile) {
		super.integrateTile(tile);

		if(tile instanceof TileDataBus) {
			dataCables.add((TileDataBus)tile);
		}
	}

	@Override
	public void update() {

		//Freaky jenky crap to make sure the multiblock loads on chunkload etc
		if(timeAlive == 0 ) {
			attemptCompleteStructure(worldObj.getBlockState(pos));
			timeAlive = 0x1;
		}

		if((worldObj.isRemote && isOpen) || (!worldObj.isRemote && isRunning() && getMachineEnabled() && !worldObj.isRaining() && worldObj.canBlockSeeSky(pos.add(0,1,0)) && !worldObj.isDaytime()) ) {
			
			if(!isOpen) {
				isOpen= true;
				
				markDirty();
				worldObj.notifyBlockUpdate(pos, worldObj.getBlockState(pos),  worldObj.getBlockState(pos), 3);
			}
			
			if(openProgress >= openTime)
				super.update();
			else
				openProgress++;
		}
		else if(openProgress > 0) {
			
			if(isOpen) {
				isOpen = false;
				
				markDirty();
				worldObj.notifyBlockUpdate(pos, worldObj.getBlockState(pos),  worldObj.getBlockState(pos), 3);
			}
			
			openProgress--;
		}
	}

	//Always running if enabled
	@Override
	public boolean isRunning() {
		return true;
	}

	@Override
	protected void processComplete() {
		super.processComplete();
		completionTime = observationtime;
		int amount = 25;

		for( TileDataBus datum : dataCables ) {
			amount -= datum.addData(amount, DataStorage.DataType.DISTANCE, EnumFacing.UP, true);
			if(amount == 0)
				break;
		}
	}

	@Override
	public void resetCache() {
		super.resetCache();
		dataCables.clear();
	}

	@Override
	public Object[][][] getStructure() {
		return structure;
	}

	@Override
	public AxisAlignedBB getRenderBoundingBox() {
		
		return new AxisAlignedBB(pos.add(-5,-3,-5), pos.add(5,3,5));
	}

	@Override
	public List<BlockMeta> getAllowableWildCardBlocks() {
		List<BlockMeta> list = super.getAllowableWildCardBlocks();

		list.add(new BlockMeta(Blocks.IRON_BLOCK,BlockMeta.WILDCARD));
		list.addAll(TileMultiBlock.getMapping('P'));
		list.addAll(TileMultiBlock.getMapping('D'));
		return list;
	}
	
	@Override
	protected void writeNetworkData(NBTTagCompound nbt) {
		super.writeNetworkData(nbt);
		nbt.setInteger("openProgress", openProgress);
		nbt.setBoolean("isOpen", isOpen);
	}
	
	@Override
	protected void readNetworkData(NBTTagCompound nbt) {
		super.readNetworkData(nbt);
		openProgress = nbt.getInteger("openProgress");

		isOpen = nbt.getBoolean("isOpen");
	}
	
	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound nbt) {
		super.writeToNBT(nbt);
		if(dataChip != null) {
			NBTTagCompound dataItem = new NBTTagCompound();
			dataChip.writeToNBT(dataItem);
			nbt.setTag("dataItem", dataItem);
		}
		return nbt;
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt) {
		super.readFromNBT(nbt);
		
		if(nbt.hasKey("dataItem")) {
			dataChip = ItemStack.loadItemStackFromNBT(nbt.getCompoundTag("dataItem"));
		}
	}

	public LinkedList<TileDataBus> getDataBus() {
		return dataCables;
	}

	@Override
	public boolean completeStructure(IBlockState state) {
		boolean result = super.completeStructure(state);
		if(result) {
			((BlockMultiblockMachine)worldObj.getBlockState(pos).getBlock()).setBlockState(worldObj, worldObj.getBlockState(pos), pos, true);
		}
		else
			((BlockMultiblockMachine)worldObj.getBlockState(pos).getBlock()).setBlockState(worldObj, worldObj.getBlockState(pos), pos, false);
		

		completionTime = observationtime;
		return result;
	}

	@Override
	public String getMachineName() {
		return "container.observatory";
	}

	@Override
	public List<ModuleBase> getModules(int ID, EntityPlayer player) {
		List<ModuleBase> modules = super.getModules(ID, player);

		DataStorage data[] = new DataStorage[dataCables.size()];

		for(int i = 0; i < data.length; i++) {
			data[i] = dataCables.get(i).getDataObject();
		}

		if(data.length > 0)
			modules.add(new ModuleData(40, 20, 0, this, data));
		modules.add(new ModuleProgress(120, 30, 0, TextureResources.progressScience, this));

		return modules;
	}


	@Override
	public void useNetworkData(EntityPlayer player, Side side, byte id,
			NBTTagCompound nbt) {
		super.useNetworkData(player, side, id, nbt);

		if(id == -1)
			storeData();
	}

	@Override
	public int getSizeInventory() {
		return 1;
	}

	@Override
	public ItemStack getStackInSlot(int slot) {
		return dataChip;
	}

	@Override
	public ItemStack decrStackSize(int slot, int amount) {
		return dataChip.splitStack(amount);
	}

	@Override
	public void setInventorySlotContents(int slot, ItemStack stack) {
		dataChip = stack;

	}

	@Override
	public boolean hasCustomName() {
		return false;
	}

	@Override
	public int getInventoryStackLimit() {
		return 1;
	}

	@Override
	public boolean isUseableByPlayer(EntityPlayer player) {
		return true;
	}

	@Override
	public void openInventory(EntityPlayer player) {

	}

	@Override
	public void closeInventory(EntityPlayer player) {

	}

	@Override
	public boolean isItemValidForSlot(int p_94041_1_, ItemStack p_94041_2_) {
		return true;
	}

	@Override
	public int extractData(int maxAmount, DataType type, EnumFacing dir, boolean commit) {
		return 0;
	}

	@Override
	public int addData(int maxAmount, DataType type, EnumFacing dir, boolean commit) {
		return 0;
	}

	@Override
	public void loadData() {

	}

	@Override
	public void storeData() {
		if(dataChip != null && dataChip.getItem() instanceof ItemData && dataChip.stackSize == 1) {

			ItemData dataItem = (ItemData)dataChip.getItem();
			DataStorage data = dataItem.getDataStorage(dataChip);

			for(TileDataBus tile : dataCables) {
				DataStorage.DataType dataType = tile.getDataObject().getDataType();
				data.addData(tile.extractData(data.getMaxData() - data.getData(), data.getDataType(), EnumFacing.UP, true), dataType ,true);
			}

			dataItem.setData(dataChip, data.getData(), data.getDataType());
		}

		if(worldObj.isRemote) {
			PacketHandler.sendToServer(new PacketMachine(this, (byte)-1));
		}
	}

	@Override
	public String getName() {
		return null;
	}

	@Override
	public ItemStack removeStackFromSlot(int index) {
		ItemStack dataChip = this.dataChip;
		this.dataChip = null;
		return dataChip;
	}

	@Override
	public int getField(int id) {
		return 0;
	}

	@Override
	public void setField(int id, int value) {
		
	}

	@Override
	public int getFieldCount() {
		return 0;
	}

	@Override
	public void clear() {
		
	}
}
