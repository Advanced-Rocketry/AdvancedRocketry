package zmaster587.advancedRocketry.tile.multiblock;

import io.netty.buffer.ByteBuf;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

import cpw.mods.fml.relauncher.Side;
import zmaster587.advancedRocketry.api.DataStorage;
import zmaster587.advancedRocketry.api.DataStorage.DataType;
import zmaster587.advancedRocketry.client.render.util.ProgressBarImage;
import zmaster587.advancedRocketry.inventory.TextureResources;
import zmaster587.advancedRocketry.inventory.modules.IModularInventory;
import zmaster587.advancedRocketry.inventory.modules.ModuleBase;
import zmaster587.advancedRocketry.inventory.modules.ModuleData;
import zmaster587.advancedRocketry.inventory.modules.ModuleProgress;
import zmaster587.advancedRocketry.item.ItemData;
import zmaster587.advancedRocketry.network.PacketHandler;
import zmaster587.advancedRocketry.network.PacketMachine;
import zmaster587.advancedRocketry.tile.data.TileDataBus;
import zmaster587.advancedRocketry.util.IDataInventory;
import zmaster587.libVulpes.block.BlockMeta;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraftforge.common.util.ForgeDirection;

public class TileObservatory extends TileMultiPowerConsumer implements IModularInventory, IDataInventory {


	private static final Object[][][] structure = new Object[][][]{

		{	{Blocks.air, Blocks.air, Blocks.air, Blocks.air, Blocks.air}, 
			{Blocks.air, Blocks.iron_block, Blocks.coal_block, Blocks.iron_block, Blocks.air},
			{Blocks.air, Blocks.iron_block, Blocks.iron_block, Blocks.iron_block, Blocks.air},
			{Blocks.air, Blocks.iron_block, Blocks.iron_block, Blocks.iron_block, Blocks.air},
			{Blocks.air, Blocks.air, Blocks.air, Blocks.air, Blocks.air}},

			{	{Blocks.air,Blocks.air,Blocks.air,Blocks.air,Blocks.air}, 
				{Blocks.air, Blocks.iron_block, Blocks.iron_block, Blocks.iron_block, Blocks.air},
				{Blocks.air, Blocks.iron_block, Blocks.coal_block, Blocks.iron_block, Blocks.air},
				{Blocks.air, Blocks.iron_block, Blocks.iron_block, Blocks.iron_block, Blocks.air},
				{Blocks.air,Blocks.air,Blocks.air,Blocks.air,Blocks.air}},

				{	{Blocks.air, Blocks.iron_block, Blocks.iron_block, Blocks.iron_block, Blocks.air}, 
					{Blocks.iron_block, Blocks.air, Blocks.air, Blocks.air, Blocks.iron_block},
					{Blocks.iron_block, Blocks.air, Blocks.air, Blocks.air, Blocks.iron_block},
					{Blocks.iron_block, Blocks.air, Blocks.coal_block, Blocks.air, Blocks.iron_block},
					{Blocks.air, Blocks.iron_block, Blocks.iron_block, Blocks.iron_block, Blocks.air}},

					{	{ Blocks.air,'*', 'c', '*',Blocks.air}, 
						{'*',Blocks.iron_block, Blocks.iron_block, Blocks.iron_block,'*'},
						{'*',Blocks.iron_block, Blocks.iron_block, Blocks.iron_block,'*'},
						{'*',Blocks.iron_block, Blocks.iron_block, Blocks.iron_block,'*'},
						{Blocks.air,'*', '*', '*', Blocks.air}},

						{	{Blocks.air,'*', '*', '*', Blocks.air}, 
							{'*',Blocks.iron_block, Blocks.iron_block, Blocks.iron_block,'*'},
							{'*',Blocks.iron_block, Blocks.iron_block, Blocks.iron_block,'*'},
							{'*',Blocks.iron_block, Blocks.iron_block, Blocks.iron_block,'*'},
							{Blocks.air,'*', '*', '*',Blocks.air}}};

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
	public void updateEntity() {

		//Freaky jenky crap to make sure the multiblock loads on chunkload etc
		if(timeAlive == 0 ) {
			attemptCompleteStructure();
			timeAlive = 0x1;
		}

		if((worldObj.isRemote && isOpen) || (!worldObj.isRemote && isRunning() && getMachineEnabled() && !worldObj.isRaining() && worldObj.canBlockSeeTheSky(xCoord, yCoord+1, zCoord) && worldObj.getBlockLightValue(xCoord, yCoord + 1, zCoord)  <= 6) ) {
			
			if(!isOpen) {
				isOpen= true;
				
				markDirty();
				worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
			}
			
			if(openProgress >= openTime)
				super.updateEntity();
			else
				openProgress++;
		}
		else if(openProgress > 0) {
			
			if(isOpen) {
				isOpen = false;
				
				markDirty();
				worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
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
		int amount = 1;

		for( TileDataBus datum : dataCables ) {
			amount -= datum.addData(amount, DataStorage.DataType.DISTANCE);
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
		return AxisAlignedBB.getBoundingBox(xCoord -2,yCoord -2, zCoord -2, xCoord + 2, yCoord + 2, zCoord + 2);
	}

	@Override
	public List<BlockMeta> getAllowableWildCardBlocks() {
		List<BlockMeta> list = super.getAllowableWildCardBlocks();

		list.add(new BlockMeta(Blocks.iron_block,BlockMeta.WILDCARD));
		list.addAll(getPowerInputBlocks());
		list.addAll(getDataBlocks());
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
	public void writeToNBT(NBTTagCompound nbt) {
		super.writeToNBT(nbt);
		if(dataChip != null) {
			NBTTagCompound dataItem = new NBTTagCompound();
			dataChip.writeToNBT(dataItem);
			nbt.setTag("dataItem", dataItem);
		}
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
	public boolean completeStructure() {
		boolean result = super.completeStructure();
		if(result) {
			worldObj.setBlockMetadataWithNotify(xCoord, yCoord, zCoord, this.getBlockMetadata() | 8, 2);
		}
		else
			worldObj.setBlockMetadataWithNotify(xCoord, yCoord, zCoord, this.getBlockMetadata() & 7, 2);

		completionTime = observationtime;
		return result;
	}

	@Override
	public String getMachineName() {
		return "container.observatory";
	}

	@Override
	public List<ModuleBase> getModules() {
		List<ModuleBase> modules = super.getModules();

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
	public ItemStack getStackInSlotOnClosing(int slot) {
		return dataChip;
	}

	@Override
	public void setInventorySlotContents(int slot, ItemStack stack) {
		dataChip = stack;

	}

	@Override
	public boolean hasCustomInventoryName() {
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
	public void openInventory() {

	}

	@Override
	public void closeInventory() {

	}

	@Override
	public boolean isItemValidForSlot(int p_94041_1_, ItemStack p_94041_2_) {
		return true;
	}

	@Override
	public int extractData(int maxAmount, DataType type) {
		return 0;
	}

	@Override
	public int addData(int maxAmount, DataType type) {
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
				data.addData(tile.extractData(data.getMaxData() - data.getData(), data.getDataType()), dataType);
			}

			dataItem.setData(dataChip, data.getData(), data.getDataType());
		}

		if(worldObj.isRemote) {
			PacketHandler.sendToServer(new PacketMachine(this, (byte)-1));
		}
	}

	@Override
	public String getInventoryName() {
		return null;
	}
}
