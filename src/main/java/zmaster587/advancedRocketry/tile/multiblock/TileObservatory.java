package zmaster587.advancedRocketry.tile.multiblock;

import io.netty.buffer.ByteBuf;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import zmaster587.advancedRocketry.api.AdvancedRocketryBlocks;
import zmaster587.advancedRocketry.api.ARConfiguration;
import zmaster587.advancedRocketry.api.DataStorage;
import zmaster587.advancedRocketry.api.DataStorage.DataType;
import zmaster587.advancedRocketry.inventory.TextureResources;
import zmaster587.advancedRocketry.inventory.modules.ModuleData;
import zmaster587.advancedRocketry.item.ItemAsteroidChip;
import zmaster587.advancedRocketry.item.ItemData;
import zmaster587.advancedRocketry.tile.hatch.TileDataBus;
import zmaster587.advancedRocketry.util.AsteroidSmall;
import zmaster587.advancedRocketry.util.AsteroidSmall.StackEntry;
import zmaster587.advancedRocketry.util.IDataInventory;
import zmaster587.libVulpes.LibVulpes;
import zmaster587.libVulpes.api.LibVulpesBlocks;
import zmaster587.libVulpes.block.BlockMeta;
import zmaster587.libVulpes.block.multiblock.BlockMultiblockMachine;
import zmaster587.libVulpes.client.util.ProgressBarImage;
import zmaster587.libVulpes.inventory.GuiHandler;
import zmaster587.libVulpes.inventory.modules.*;
import zmaster587.libVulpes.network.PacketHandler;
import zmaster587.libVulpes.network.PacketMachine;
import zmaster587.libVulpes.tile.multiblock.TileMultiBlock;
import zmaster587.libVulpes.tile.multiblock.TileMultiPowerConsumer;
import zmaster587.libVulpes.util.EmbeddedInventory;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

public class TileObservatory extends TileMultiPowerConsumer implements IModularInventory, IDataInventory, IGuiCallback {

	private static final Block lens[] = { AdvancedRocketryBlocks.blockLens, Blocks.GLASS };
	
	private static final Object[][][] structure = new Object[][][]{

		{	{null, null, null, null, null}, 
			{null, Blocks.STONE, lens, Blocks.STONE, null},
			{null, Blocks.STONE, Blocks.STONE, Blocks.STONE, null},
			{null, Blocks.STONE, Blocks.STONE, Blocks.STONE, null},
			{null, null, null, null, null}},

			{	{null,null,null,null,null}, 
				{null, Blocks.STONE, Blocks.STONE, Blocks.STONE, null},
				{null, Blocks.STONE, lens, Blocks.STONE, null},
				{null, Blocks.STONE, Blocks.STONE, Blocks.STONE, null},
				{null,null,null,null,null}},

				{	{null, Blocks.STONE, Blocks.STONE, Blocks.STONE, null}, 
					{Blocks.STONE, Blocks.AIR, Blocks.AIR, Blocks.AIR, Blocks.STONE},
					{Blocks.STONE, Blocks.AIR, Blocks.AIR, Blocks.AIR, Blocks.STONE},
					{Blocks.STONE, Blocks.AIR, lens, Blocks.AIR, Blocks.STONE},
					{null, Blocks.STONE, Blocks.STONE, Blocks.STONE, null}},

					{	{ null,'*', 'c', '*',null}, 
						{'*',Blocks.STONE, Blocks.STONE, Blocks.STONE,'*'},
						{'*',Blocks.STONE, Blocks.STONE, Blocks.STONE,'*'},
						{'*',Blocks.STONE, Blocks.STONE, Blocks.STONE,'*'},
						{null,'*', '*', '*', null}},

						{	{null,'*', '*', '*', null}, 
							{'*',Blocks.STONE, Blocks.STONE, Blocks.STONE,'*'},
							{'*',Blocks.STONE, LibVulpesBlocks.motors, Blocks.STONE,'*'},
							{'*',Blocks.STONE, Blocks.STONE, Blocks.STONE,'*'},
							{null,'*', '*', '*',null}}};

	final static int openTime = 100;
	final static int observationtime = 1000;
	private static final byte TAB_SWITCH = 10;
	private static final byte BUTTON_PRESS = 11;
	private static final short LIST_OFFSET = 100;
	private static final byte PROCESS_CHIP = 12;
	private static final byte SEED_CHANGE = 13;
	private int viewDistance;
	private int lastButton;
	private long lastSeed;
	private String lastType;
	int openProgress;
	private LinkedList<TileDataBus> dataCables;
	private HashMap<Integer, String> buttonType  = new HashMap<Integer, String>();
	private boolean isOpen;
	private ModuleTab tabModule;
	private int dataConsumedPerRefresh = 100;
	EmbeddedInventory inv = new EmbeddedInventory(3);

	public TileObservatory() {
		openProgress = 0;
		viewDistance = 0;
		lastButton = -1;
		lastSeed = -1;
		completionTime = observationtime;
		dataCables = new LinkedList<TileDataBus>();
		tabModule = new ModuleTab(4,0,0,this, 2, new String[]{LibVulpes.proxy.getLocalizedString("msg.tooltip.data"), LibVulpes.proxy.getLocalizedString("msg.tooltip.asteroidselection")}, new ResourceLocation[][] { TextureResources.tabData, TextureResources.tabAsteroid} );
	}

	public float getOpenProgress() {
		return openProgress/(float)openTime;
	}

	@Override
	protected void integrateTile(TileEntity tile) {
		super.integrateTile(tile);

		if(tile instanceof TileDataBus) {
			dataCables.add((TileDataBus)tile);
			((TileDataBus)tile).lockData(((TileDataBus)tile).getDataObject().getDataType());
		}
	}
	
	@Override
	public void deconstructMultiBlock(World world, BlockPos destroyedPos,
			boolean blockBroken, IBlockState state) {
		super.deconstructMultiBlock(world, destroyedPos, blockBroken, state);
		viewDistance = 0;
	}

	@Override
	protected void replaceStandardBlock(BlockPos newPos, IBlockState state,
			TileEntity tile) {
		
		Block block = state.getBlock();
		
		if(block == AdvancedRocketryBlocks.blockLens) {
			viewDistance += 5;
		}
		else if( block == LibVulpesBlocks.blockMotor ) {
			viewDistance += 25; 
		}
		else if( block == LibVulpesBlocks.blockAdvancedMotor ) {
			viewDistance += 50; 
		}
		else if( block == LibVulpesBlocks.blockEnhancedMotor ) {
			viewDistance += 100; 
		}
		else if( block == LibVulpesBlocks.blockEliteMotor ) {
			viewDistance += 175; 
		}
		
		super.replaceStandardBlock(newPos, state, tile);
	}
	
	@Override
	public void update() {

		//Freaky jenky crap to make sure the multiblock loads on chunkload etc
		if(timeAlive == 0 ) {
			attemptCompleteStructure(world.getBlockState(pos));
			timeAlive = 0x1;
		}

		if((world.isRemote && isOpen) || (!world.isRemote && isRunning() && getMachineEnabled() && !world.isRaining() && world.canBlockSeeSky(pos.add(0,1,0)) && !world.isDaytime()) ) {

			if(!isOpen) {
				isOpen= true;

				markDirty();
				world.notifyBlockUpdate(pos, world.getBlockState(pos),  world.getBlockState(pos), 3);
			}

			if(openProgress < openTime)
				openProgress++;
		}
		else if(openProgress > 0) {

			if(isOpen) {
				isOpen = false;

				markDirty();
				world.notifyBlockUpdate(pos, world.getBlockState(pos),  world.getBlockState(pos), 3);
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

		nbt.setInteger("viewableDist", viewDistance);
		nbt.setLong("lastSeed", lastSeed);
		nbt.setInteger("lastButton", lastButton);
		if(lastType != null && !lastType.isEmpty())
			nbt.setString("lastType", lastType);
	}

	@Override
	protected void readNetworkData(NBTTagCompound nbt) {
		super.readNetworkData(nbt);
		openProgress = nbt.getInteger("openProgress");

		isOpen = nbt.getBoolean("isOpen");

		viewDistance = nbt.getInteger("viewableDist");
		lastSeed = nbt.getLong("lastSeed");
		lastButton = nbt.getInteger("lastButton");
		lastType = nbt.getString("lastType");
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound nbt) {
		super.writeToNBT(nbt);
		inv.writeToNBT(nbt);
		return nbt;
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt) {
		super.readFromNBT(nbt);

		inv.readFromNBT(nbt);
	}

	public LinkedList<TileDataBus> getDataBus() {
		return dataCables;
	}

	private int getDataAmt(DataType type) {
		int data = 0;
		for(TileDataBus tile : getDataBus()) {
			if(tile.getDataObject().getDataType() == type)
				data += tile.getDataObject().getData();
		}
		return data;
	}

	@Override
	public boolean completeStructure(IBlockState state) {
		boolean result = super.completeStructure(state);
		if(result) {
			((BlockMultiblockMachine)world.getBlockState(pos).getBlock()).setBlockState(world, world.getBlockState(pos), pos, true);
		}
		else
			((BlockMultiblockMachine)world.getBlockState(pos).getBlock()).setBlockState(world, world.getBlockState(pos), pos, false);


		completionTime = observationtime;
		return result;
	}

	@Override
	public String getMachineName() {
		return "container.observatory";
	}

	@Override
	public List<ModuleBase> getModules(int ID, EntityPlayer player) {
		List<ModuleBase> modules = new LinkedList<ModuleBase>();

		modules.add(tabModule);

		if(tabModule.getTab() == 1) {

			//ADD io slots
			modules.add(new ModuleTexturedSlotArray(5, 120, this, 1, 2, TextureResources.idChip));
			modules.add(new ModuleOutputSlotArray(45, 120 , this, 2, 3));
			modules.add(new ModuleProgress(25, 120, 0, new ProgressBarImage(217,0, 17, 17, 234, 0, EnumFacing.DOWN, TextureResources.progressBars), this));
			modules.add(new ModuleButton(25, 120, 1, "", this,  zmaster587.libVulpes.inventory.TextureResources.buttonNull,  LibVulpes.proxy.getLocalizedString("msg.observetory.text.processdiscovery"), 17, 17));

			
			ModuleButton scanButton = new ModuleButton(100, 120, 2, LibVulpes.proxy.getLocalizedString("msg.observetory.scan.button"), this,  zmaster587.libVulpes.inventory.TextureResources.buttonBuild, LibVulpes.proxy.getLocalizedString("msg.observetory.scan.tooltip"), 64, 18);
			
			scanButton.setColor(extractData(dataConsumedPerRefresh, DataType.DISTANCE, EnumFacing.DOWN, false) == dataConsumedPerRefresh ? 0x00ff00 : 0xff0000);
			
			modules.add(scanButton);


			List<ModuleBase> list2 = new LinkedList<ModuleBase>();
			List<ModuleBase> buttonList = new LinkedList<ModuleBase>();
			buttonType.clear();


			int g = 0;
			AsteroidSmall asteroidSmol;
			if(lastButton != -1 && lastType != null && !lastType.isEmpty() && (asteroidSmol = ARConfiguration.getCurrentConfig().asteroidTypes.get(lastType)) != null) {
				List<StackEntry> harvestList = asteroidSmol.getHarvest(lastSeed + lastButton, Math.max(1 - ((Math.min(getDataAmt(DataType.COMPOSITION),2000)  + Math.min(getDataAmt(DataType.MASS), 2000) )/4000f), 0));
				for(StackEntry entry : harvestList) {
					//buttonList.add(new ModuleButton((g % 3)*24, 24*(g/3), -2, "",this, TextureResources.tabData, 24, 24));
					buttonList.add(new ModuleSlotButton((g % 3)*24 + 1, 24*(g/3) + 1, -2, this, entry.stack, String.valueOf(entry.midpoint) + " +/-  " + String.valueOf(entry.variablility), getWorld()));
					buttonList.add(new ModuleText((g % 3)*24 + 1, 24*(g/3) + 1, String.valueOf(entry.midpoint) + "\n+/- " + String.valueOf(entry.variablility) , 0xFFFFFF, 0.5f ));
					g++;
				}
				
				float time = asteroidSmol.timeMultiplier;
				
				buttonList.add(new ModuleText(0, 24*(1+(g/3)), String.format("%s\n%.2fx", LibVulpes.proxy.getLocalizedString("msg.observetory.text.missionTime") ,time), 0x2f2f2f));
			}
			
			
			

			//Calculate Types
			int totalAmountAllowed = 10;
			float totalWeight = 0;
			List<AsteroidSmall> viableTypes = new LinkedList<AsteroidSmall>();
			for(String str :  ARConfiguration.getCurrentConfig().asteroidTypes.keySet()) {
				AsteroidSmall asteroid = ARConfiguration.getCurrentConfig().asteroidTypes.get(str);
				if(asteroid.distance <= getMaxDistance()) {
					totalWeight += asteroid.getProbability();
					viableTypes.add(asteroid);
				}
			}

			//Yeah, eww
			List<AsteroidSmall> finalList = new LinkedList<AsteroidSmall>();
			Random rand = new Random(lastSeed);
			for(AsteroidSmall asteroid : viableTypes) {
				for(int i = 0; i < totalAmountAllowed; i++) {
					if(asteroid.getProbability()/totalWeight >= rand.nextFloat())
						finalList.add(asteroid);
				}
			}


			for(int i = 0; i < finalList.size(); i++) {
				AsteroidSmall asteroid = finalList.get(i);

				ModuleButton button = new ModuleButton(0, i*18, LIST_OFFSET + i, asteroid.getName(), this, TextureResources.buttonAsteroid, 72, 18);

				if(lastButton - LIST_OFFSET == i) {
					button.setColor(0xFFFF00);
				}

				list2.add(button);
				buttonType.put(i, asteroid.getName());
			}


			modules.add(new ModuleText(10, 18, LibVulpes.proxy.getLocalizedString("msg.observetory.text.asteroids"), 0x2d2d2d));
			modules.add(new ModuleText(105, 18, LibVulpes.proxy.getLocalizedString("msg.observetory.text.composition"), 0x2d2d2d));

			//Add borders for asteroid
			int baseX = 5;
			int baseY = 32;
			int sizeX = 72;
			int sizeY = 46;
			if(world.isRemote) {
				//Border
				modules.add(new ModuleScaledImage(baseX - 3,baseY - 3,3, baseY + sizeY  +6, TextureResources.verticalBar));
				modules.add(new ModuleScaledImage(baseX + sizeX, baseY- 3, -3, baseY + sizeY + 6, TextureResources.verticalBar));
				modules.add(new ModuleScaledImage(baseX,baseY - 3,sizeX,3, TextureResources.horizontalBar));
				modules.add(new ModuleScaledImage(baseX, 2*baseY + sizeY ,sizeX,-3, TextureResources.horizontalBar));
			}

			//Relying on a bug, is this safe?
			if(lastSeed != -1) {
				ModuleContainerPan pan = new ModuleContainerPan(baseX, baseY, list2, new LinkedList<ModuleBase>(), null, sizeX -2, sizeY, 0, -48, 0, 72);
				modules.add(pan);
			}

			//Ore display
			baseX = 100;
			baseY = 32;
			sizeX = 72;
			sizeY = 46;
			if(world.isRemote) {
				//Border
				modules.add(new ModuleScaledImage(baseX - 3,baseY - 3,3, baseY + sizeY  +6, TextureResources.verticalBar));
				modules.add(new ModuleScaledImage(baseX + sizeX, baseY- 3, -3, baseY + sizeY + 6, TextureResources.verticalBar));
				modules.add(new ModuleScaledImage(baseX,baseY - 3,sizeX,3, TextureResources.horizontalBar));
				modules.add(new ModuleScaledImage(baseX, 2*baseY + sizeY ,sizeX,-3, TextureResources.horizontalBar));
			}

			ModuleContainerPan pan2 = new ModuleContainerPan(baseX, baseY, buttonList, new LinkedList<ModuleBase>(), null, 40, 48, 0, 0, 0, 72);
			modules.add(pan2);
		} else if(tabModule.getTab() == 0) {
			modules.add(new ModulePower(18, 20, getBatteries()));
			modules.add(toggleSwitch = new ModuleToggleSwitch(160, 5, 0, "", this,  zmaster587.libVulpes.inventory.TextureResources.buttonToggleImage, 11, 26, getMachineEnabled()));
			
			List<DataStorage> distanceStorage = new LinkedList<DataStorage>();
			List<DataStorage> compositionStorage = new LinkedList<DataStorage>();
			List<DataStorage> massStorage = new LinkedList<DataStorage>();
			for(int i = 0; i < dataCables.size(); i++) {

				DataStorage storage = dataCables.get(i).getDataObject();
				DataType type = dataCables.get(i).getDataObject().getDataType();
				if(type == DataType.COMPOSITION)
					compositionStorage.add(storage);
				else if(type == DataType.DISTANCE)
					distanceStorage.add(storage);
				else if(type == DataType.MASS)
					massStorage.add(storage);
			}

			if(distanceStorage.size() > 0 ) {
				modules.add(new ModuleData(40, 20, 0, this, (DataStorage[]) distanceStorage.toArray(new DataStorage[distanceStorage.size()])));
			}

			if(compositionStorage.size() > 0 ) {
				modules.add(new ModuleData(80, 20, 0, this, (DataStorage[]) compositionStorage.toArray(new DataStorage[compositionStorage.size()])));
			}

			if(massStorage.size() > 0 ) {
				modules.add(new ModuleData(120, 20, 0, this, (DataStorage[]) massStorage.toArray(new DataStorage[massStorage.size()])));
			}
			
			modules.add(new ModuleText(10, 90, LibVulpes.proxy.getLocalizedString("msg.observetory.text.observabledistance") + " " + getMaxDistance(), 0x2d2d2d, false));
		}

		/*DataStorage data[] = new DataStorage[dataCables.size()];

		if(data.length > 0)
			modules.add(new ModuleData(40, 20, 0, this, data));*/
		//modules.add(new ModuleProgress(120, 30, 0, TextureResources.progressScience, this));

		return modules;
	}

	public int getMaxDistance() {
		return viewDistance + 10;
	}

	@Override
	public void onInventoryButtonPressed(int buttonId) {
		super.onInventoryButtonPressed(buttonId);

		if(buttonId == 1) {
			//Begin discovery processing
			PacketHandler.sendToServer(new PacketMachine(this, (byte)PROCESS_CHIP));
		}

		if(buttonId >= LIST_OFFSET) {
			lastButton = buttonId;
			lastType = buttonType.get(lastButton - LIST_OFFSET);
			PacketHandler.sendToServer(new PacketMachine(this, (byte)BUTTON_PRESS));
		}
		if(buttonId == 2) {
			
			//for(TileDataBus bus : getDataBus()) {
				if(extractData(dataConsumedPerRefresh, DataType.DISTANCE, EnumFacing.UP, false) == dataConsumedPerRefresh) {
					lastSeed = world.getTotalWorldTime()/100;
					lastButton = -1;
					lastType = "";
					PacketHandler.sendToServer(new PacketMachine(this, (byte)SEED_CHANGE));
				}
			//}
		}
	}


	@Override
	public void useNetworkData(EntityPlayer player, Side side, byte id,
			NBTTagCompound nbt) {
		super.useNetworkData(player, side, id, nbt);

		if(id == -1)
			storeData(-1);
		else if(id == TAB_SWITCH && !world.isRemote) {
			tabModule.setTab(nbt.getShort("tab"));
			player.openGui(LibVulpes.instance, GuiHandler.guiId.MODULARNOINV.ordinal(), getWorld(), pos.getX(), pos.getY(), pos.getZ());
		}
		else if(id == BUTTON_PRESS && !world.isRemote) {
			lastButton = nbt.getShort("button");
			lastType = buttonType.get(lastButton - LIST_OFFSET);
			markDirty();
			world.notifyBlockUpdate(pos, world.getBlockState(pos),  world.getBlockState(pos), 2);
			player.openGui(LibVulpes.instance, GuiHandler.guiId.MODULARNOINV.ordinal(), getWorld(), pos.getX(), pos.getY(), pos.getZ());

		}
		else if(id == SEED_CHANGE) {
			if(extractData(dataConsumedPerRefresh, DataType.DISTANCE, EnumFacing.UP, false) >= dataConsumedPerRefresh) {
				lastSeed = world.getTotalWorldTime()/100;
				lastButton = -1;
				lastType = "";
				extractData(dataConsumedPerRefresh, DataType.DISTANCE, EnumFacing.UP, true);
				world.notifyBlockUpdate(pos, world.getBlockState(pos),  world.getBlockState(pos), 2);
				markDirty();
				player.openGui(LibVulpes.instance, GuiHandler.guiId.MODULARNOINV.ordinal(), getWorld(), pos.getX(), pos.getY(), pos.getZ());
			}


		}
		else if(id == PROCESS_CHIP && !world.isRemote) {

			if(inv.getStackInSlot(2).isEmpty() && isOpen && hasEnergy(500) && lastButton != -1) {
				ItemStack stack = inv.decrStackSize(1, 1);
				if(stack != null && stack.getItem() instanceof ItemAsteroidChip) {
					((ItemAsteroidChip)(stack.getItem())).setUUID(stack, lastSeed);
					((ItemAsteroidChip)(stack.getItem())).setType(stack, lastType);
					((ItemAsteroidChip)(stack.getItem())).setMaxData(stack, 1000);
					inv.setInventorySlotContents(2, stack);

					extractData(1000, DataType.COMPOSITION, EnumFacing.UP, true);
					extractData(1000, DataType.MASS, EnumFacing.UP, true);
					useEnergy(500);
				}
			}
		}
	}

	@Override
	public void writeDataToNetwork(ByteBuf out, byte id) {
		super.writeDataToNetwork(out, id);

		if(id == TAB_SWITCH)
			out.writeShort(tabModule.getTab());
		else if(id == BUTTON_PRESS)
			out.writeShort(lastButton);
	}

	@Override
	public void readDataFromNetwork(ByteBuf in, byte packetId,
			NBTTagCompound nbt) {
		super.readDataFromNetwork(in, packetId, nbt);

		if(packetId == TAB_SWITCH)
			nbt.setShort("tab", in.readShort());
		else if(packetId == BUTTON_PRESS)
			nbt.setShort("button", in.readShort());

	}

	@Override
	public int getSizeInventory() {
		return inv.getSizeInventory();
	}

	@Override
	public ItemStack getStackInSlot(int slot) {
		return inv.getStackInSlot(slot);
	}

	@Override
	public ItemStack decrStackSize(int slot, int amount) {
		return inv.decrStackSize(slot, amount);
	}

	@Override
	public void setInventorySlotContents(int slot, ItemStack stack) {
		inv.setInventorySlotContents(slot, stack);
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
	public boolean isUsableByPlayer(EntityPlayer player) {
		return true;
	}
	
	@Override
	public boolean isEmpty() {
		return inv.isEmpty();
	}

	@Override
	public void openInventory(EntityPlayer player) {

	}

	@Override
	public void closeInventory(EntityPlayer player) {
	}

	@Override
	public boolean isItemValidForSlot(int p_94041_1_, ItemStack p_94041_2_) {
		return inv.isItemValidForSlot(p_94041_1_, p_94041_2_);
	}

	@Override
	public int extractData(int maxAmount, DataType type, EnumFacing dir, boolean commit) {
		int amt = 0;
		for(TileDataBus tile : getDataBus()) {
			int dataAmt = tile.extractData(maxAmount, type, dir, commit);
			amt += dataAmt;
			maxAmount -= dataAmt;
		}
		return amt;
	}

	@Override
	public int addData(int maxAmount, DataType type, EnumFacing dir, boolean commit) {
		return 0;
	}

	@Override
	public void loadData(int id) {

	}

	@Override
	public void storeData(int id) {
		ItemStack dataChip = inv.getStackInSlot(0);

		if(dataChip != null && dataChip.getItem() instanceof ItemData && dataChip.getCount() == 1) {

			ItemData dataItem = (ItemData)dataChip.getItem();
			DataStorage data = dataItem.getDataStorage(dataChip);

			for(TileDataBus tile : dataCables) {
				DataStorage.DataType dataType = tile.getDataObject().getDataType();
				data.addData(tile.extractData(data.getMaxData() - data.getData(), data.getDataType(), EnumFacing.UP, true), dataType ,true);
			}

			dataItem.setData(dataChip, data.getData(), data.getDataType());
		}

		if(world.isRemote) {
			PacketHandler.sendToServer(new PacketMachine(this, (byte)-1));
		}
	}

	@Override
	public String getName() {
		return null;
	}

	@Override
	public ItemStack removeStackFromSlot(int index) {
		return inv.removeStackFromSlot(index);
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

	@Override
	public void onModuleUpdated(ModuleBase module) {
		//ReopenUI on server
		PacketHandler.sendToServer(new PacketMachine(this, TAB_SWITCH));
	}
}
