package zmaster587.advancedRocketry.tile.multiblock;

import io.netty.buffer.ByteBuf;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

import cpw.mods.fml.relauncher.Side;
import zmaster587.advancedRocketry.AdvancedRocketry;
import zmaster587.advancedRocketry.api.AdvRocketryBlocks;
import zmaster587.advancedRocketry.block.multiblock.BlockMultiblockStructure;
import zmaster587.advancedRocketry.tile.TileInputHatch;
import zmaster587.advancedRocketry.tile.TileOutputHatch;
import zmaster587.advancedRocketry.tile.TileRFBattery;
import zmaster587.advancedRocketry.util.MultiBattery;
import zmaster587.libVulpes.api.IUniversalEnergy;
import zmaster587.libVulpes.block.RotatableBlock;
import zmaster587.libVulpes.interfaces.IRecipe;
import zmaster587.libVulpes.tile.IMultiblock;
import zmaster587.libVulpes.util.INetworkMachine;
import zmaster587.libVulpes.util.Vector3F;
import zmaster587.libVulpes.util.ZUtils;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

public class TileMultiBlockMachine extends TileEntity implements INetworkMachine {

	public enum NetworkPackets {
		TOGGLE,
		POWERERROR
	}

	protected MultiBattery batteries = new MultiBattery();
	protected LinkedList<IInventory> itemInPorts = new LinkedList<IInventory>();
	protected LinkedList<IInventory> itemOutPorts = new LinkedList<IInventory>();

	private int completionTime, currentTime;
	private int powerPerTick;
	private List<ItemStack> outputItemStacks;
	protected boolean completeStructure, enabled;
	protected byte timeAlive = 0;
	boolean smartInventoryUpgrade = true;
	//When using smart inventories sometimes setInventory content calls need to be made
	//This flag prevents infinite recursion by having a value of true if any invCheck has started
	boolean invCheckFlag = false;

	//On server determines change in power state, on client determines last power state on server
	boolean hadPowerLastTick = true;

	public TileMultiBlockMachine() {
		outputItemStacks = null;
		completeStructure = false;
		enabled = false;
		completionTime = -1;
		currentTime = -1;
		hadPowerLastTick = true;
	}

	//Needed for GUI stuff
	public MultiBattery getBatteries() {
		return batteries;
	}

	public int getProgress() {
		return currentTime;
	}

	public int getTotalProgress() {
		return completionTime;
	}

	public String getMachineName() {
		return "";
	}

	public void setProgress(int progress) {
		currentTime = progress;
	}

	public void setTotalOperationTime(int progress) {
		completionTime = progress;
	}

	public boolean isUsableByPlayer(EntityPlayer player) {
		return player.getDistance(xCoord, yCoord, zCoord) < 64;
	}

	public List<ItemStack> getOutputs() {
		return outputItemStacks;
	}

	public boolean isComplete() {
		return completeStructure;
	}

	@Override
	public Packet getDescriptionPacket() {
		NBTTagCompound nbt = new NBTTagCompound();

		writeToNBT(nbt);
		nbt.setBoolean("built", completeStructure);
		nbt.setBoolean("hadPowerLastTick", hadPowerLastTick);
		return new S35PacketUpdateTileEntity(xCoord, yCoord, zCoord, 0, nbt);
	}

	@Override
	public void onDataPacket(NetworkManager net, S35PacketUpdateTileEntity pkt) {
		NBTTagCompound nbt = pkt.func_148857_g();

		completeStructure = nbt.getBoolean("built");
		hadPowerLastTick = nbt.getBoolean("hadPowerLastTick");
		readFromNBT(nbt);
	}

	@Override
	public boolean canUpdate() {
		return true;
	}

	@Override
	public void updateEntity() {
		super.updateEntity();

		//Freaky jenky crap to make sure the multiblock loads on chunkload etc
		if(timeAlive == 0 ) {

			completeStructure = completeStructure();
			if(completeStructure && !worldObj.isRemote)
				onInventoryUpdated();
			timeAlive = 0x1;
		}

		//In case the machine jams for some reason
		if(!isRunning() && worldObj.getTotalWorldTime() % 1000L == 0)
			onInventoryUpdated();

		if(isRunning()) {
			if( hasEnergy(powerPerTick) || (worldObj.isRemote && hadPowerLastTick)) {

				//Increment for both client and server
				currentTime++;

				//If server then check to see if we need to update the client, use power and process output if applicable
				if(!worldObj.isRemote) {

					if(!hadPowerLastTick) {
						hadPowerLastTick = true;
						markDirty();
						worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
					}

					useEnergy(powerPerTick);
				}
				if(currentTime == completionTime)
					processComplete();
			}
			else if(!worldObj.isRemote && hadPowerLastTick) { //If server and out of power check to see if client needs update
				hadPowerLastTick = false;
				markDirty();
				worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
			}
		}
	}

	public void setMachineEnabled(boolean enabled) {
		this.enabled = enabled;
		onInventoryUpdated();
	}

	public boolean getMachineEnabled() {
		return enabled;
	}

	public void resetCache() {
		itemInPorts.clear();
		itemOutPorts.clear();
		batteries.clear();
	}

	/**
	 * @param world world
	 * @param destroyedX x coord of destroyed block
	 * @param destroyedY y coord of destroyed block
	 * @param destroyedZ z coord of destroyed block
	 * @param blockBroken set true if the block is being broken, otherwise some other means is being used to disassemble the machine
	 */
	public void deconstructMultiBlock(World world, int destroyedX, int destroyedY, int destroyedZ, boolean blockBroken) {
		resetCache();
		outputItemStacks = null;
		completionTime = 0;
		currentTime = 0;
		completeStructure = false;
		worldObj.setBlockMetadataWithNotify(xCoord, yCoord, zCoord, this.blockMetadata & 7, 2); //Turn off machine

		this.markDirty();
		worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);

		//UNDO all the placeholder blocks
		ForgeDirection front = getFrontDirection();

		Object[][][] structure = getStructure();
		Vector3F<Integer> offset = getControllerOffset(structure);


		//Mostly to make sure IMultiblocks lose their choke-hold on this machines and to revert placeholder blocks
		for(int y = 0; y < structure.length; y++) {
			for(int z = 0; z < structure[0].length; z++) {
				for(int x = 0; x< structure[0][0].length; x++) {

					int globalX = xCoord + (x - offset.x)*front.offsetZ - (z-offset.z)*front.offsetX;
					int globalY = yCoord - y + offset.y;
					int globalZ = zCoord - (x - offset.x)*front.offsetX  - (z-offset.z)*front.offsetZ;



					//This block is being broken anyway so don't bother
					if(blockBroken && globalX == destroyedX &&
							globalY == destroyedY &&
							globalZ == destroyedZ)
						continue;
					TileEntity tile = worldObj.getTileEntity(globalX, globalY, globalZ);
					Block block = worldObj.getBlock(globalX, globalY, globalZ);


					if(block instanceof BlockMultiblockStructure) {
						((BlockMultiblockStructure)block).destroyStructure(worldObj, globalX, globalY, z, worldObj.getBlockMetadata(globalX, globalY, globalZ));
					}

					if(tile instanceof TilePlaceholder) {
						TilePlaceholder placeholder = (TilePlaceholder)tile;

						//Must set incomplete BEFORE changing the block to prevent stack overflow!
						placeholder.setIncomplete();

						worldObj.setBlock(tile.xCoord, tile.yCoord, tile.zCoord, placeholder.getReplacedBlock(), placeholder.getReplacedBlockMeta(), 3);

						//Dont try to set a tile if none existed
						if(placeholder.getReplacedTileEntity() != null) {
							NBTTagCompound nbt = new NBTTagCompound();
							placeholder.getReplacedTileEntity().writeToNBT(nbt);

							worldObj.getTileEntity(tile.xCoord, tile.yCoord, tile.zCoord).readFromNBT(nbt);
						}
					}
					//Make all pointers incomplete
					else if(tile instanceof IMultiblock) {
						((IMultiblock)tile).setIncomplete();
					}
				}
			}
		}
	}

	protected void processComplete() {
		completionTime = 0;
		currentTime = 0;
		dumpOutputToInventory();

		outputItemStacks = null;
		onInventoryUpdated();

		this.markDirty();
		worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
	}


	public ForgeDirection getFrontDirection() {
		//Make sure meta is not -1
		this.blockMetadata = worldObj.getBlockMetadata(xCoord, yCoord, zCoord);
		return RotatableBlock.getFront(this.blockMetadata);
	}

	//When the output of the recipe is dumped to the inventory
	protected void dumpOutputToInventory() {

		for(IInventory outInventory : itemOutPorts) {

			for(int i = 0; i < outputItemStacks.size(); i++) {
				ItemStack stack = outInventory.getStackInSlot(smartInventoryUpgrade ? outInventory.getSizeInventory() - i - 1 : i);

				if(stack == null) {
					outInventory.setInventorySlotContents(smartInventoryUpgrade ? outInventory.getSizeInventory() - i - 1 : i, outputItemStacks.get(i));
					outInventory.markDirty();
				}
				else if(stack.isItemEqual(outputItemStacks.get(i)) && stack.stackSize + outputItemStacks.get(i).stackSize <= outInventory.getInventoryStackLimit()) {
					outInventory.markDirty();
					outInventory.getStackInSlot(smartInventoryUpgrade ? outInventory.getSizeInventory() - i - 1 : i).stackSize += outputItemStacks.get(i).stackSize;
				}
			}
		}
	}

	//True if the machine is running
	public boolean isRunning() {
		return completionTime > 0;
	}

	//Attempt to get a valid recipe given the inputs, null if none found
	protected IRecipe getRecipe(List<IRecipe> recipes) {

		for(IRecipe recipe : recipes) {
			List<ItemStack> ingredients = recipe.getIngredients();
			recipeCheck:

				for(int ingredientNum = 0;ingredientNum < ingredients.size(); ingredientNum++) {

					ItemStack ingredient = ingredients.get(ingredientNum);

					short mask = 0x0;

					ingredientCheck:

						for(IInventory hatch : itemInPorts) {

							for(int i = 0; i < hatch.getSizeInventory(); i++) {
								ItemStack stackInSlot = hatch.getStackInSlot(i);


								if(stackInSlot != null && stackInSlot.stackSize >= ingredient.stackSize && stackInSlot.isItemEqual(ingredient)) {
									mask |= (1 << ingredientNum);
									break ingredientCheck;
								}
							}

							//If no matching item is found for the ingredient
							break recipeCheck;
						}

					if(mask == (1 << ( ( ingredients.size() ) ) - 1) && canProcessRecipe(recipe) )
						return recipe;
				}
		}
		return null;
	}

	//Get outputs of the machine
	protected List<ItemStack> getOutputs(IRecipe recipe) {
		return recipe.getOutput();
	}

	public void useEnergy(int amt) {
		batteries.extractEnergy(ForgeDirection.UNKNOWN, amt, false);
	}

	public boolean hasEnergy(int amt) {
		return batteries.getEnergyStored(ForgeDirection.UNKNOWN) >= amt;
	}


	public void consumeItems(IRecipe recipe) {
		List<ItemStack> ingredients = recipe.getIngredients();

		for(int ingredientNum = 0;ingredientNum < ingredients.size(); ingredientNum++) {

			ItemStack ingredient = ingredients.get(ingredientNum);

			ingredientCheck:

				for(IInventory hatch : itemInPorts) {
					for(int i = 0; i < hatch.getSizeInventory(); i++) {
						ItemStack stackInSlot = hatch.getStackInSlot(i);

						if(stackInSlot != null && stackInSlot.stackSize >= ingredient.stackSize && stackInSlot.isItemEqual(ingredient)) {
							hatch.decrStackSize(i, ingredient.stackSize);
							hatch.markDirty();
							break ingredientCheck;
						}
					}
				}
		}
	}

	//Can this recipe be processed
	public boolean canProcessRecipe(IRecipe recipe) {
		if(invCheckFlag)
			return false;

		invCheckFlag = true;
		List<ItemStack> outputItems = getOutputs(recipe);

		for(IInventory outInventory : itemOutPorts) {
			for(int i = smartInventoryUpgrade ? outInventory.getSizeInventory() - outputItems.size() : 0; (i < (smartInventoryUpgrade ? outInventory.getSizeInventory() : outputItems.size())); i++) {
				ItemStack stack = outInventory.getStackInSlot(i);

				if(smartInventoryUpgrade) {
					ItemStack outputItem = outputItems.get(outInventory.getSizeInventory() - i - 1);


					//stack cannot be null when assigning flag
					if(stack == null || stack.isItemEqual(outputItem) && stack.stackSize + outputItem.stackSize <= outInventory.getInventoryStackLimit()) {
						invCheckFlag = false;
						return true && completeStructure;
					}

					if(stack != null && ZUtils.getFirstFilledSlotIndex(outInventory) >= outputItems.size()) {
						//Range Check
						int outputSize = outputItems.size();
						int j;
						for(j = 0; j < outputSize; j++) {
							if(outInventory.getStackInSlot(j) != null) {
								invCheckFlag = false;
								return false;
							}
						}

						int numExtraMoves = outInventory.getSizeInventory() - ZUtils.getFirstFilledSlotIndex(outInventory) - 1;

						//J will be last slot in index by now
						for(j = j + numExtraMoves - 1; j >= 0; j--) {
							int slot = outInventory.getSizeInventory() - 1;
							outInventory.setInventorySlotContents(slot - j - outputItems.size(), outInventory.getStackInSlot(slot - j));
							outInventory.setInventorySlotContents(slot - j, null);
						}

						invCheckFlag = false;
						return true && completeStructure;
					}
				}
				else if(stack == null || stack.isItemEqual(outputItems.get(i)) && stack.stackSize + outputItems.get(i).stackSize <= outInventory.getInventoryStackLimit())
					return true && completeStructure;

			}
		}
		invCheckFlag = false;
		return false;
	}


	//Used To make sure the multiblock is valid
	/*@Override
	public void updateContainingBlockInfo() {
		super.updateContainingBlockInfo();

		completeStructure = completeStructure();
	}*/

	public Object[][][] getStructure() {
		return null;
	}

	public boolean attemptCompleteStructure() {
		if(!completeStructure)
			completeStructure = completeStructure();

		if(completeStructure)
			onInventoryUpdated();
		return completeStructure;
	}

	/**
	 * Returns a hashset of blocks which are allowable in spaces set as *
	 */
	protected HashSet<Block> getAllowableWildCardBlocks() {
		return new HashSet<Block>();
	}

	/**
	 * Use '*' to allow any kind of Hatch, or energy device or anything returned by getAllowableWildcards
	 * Use 'L' for liquid hatches TODO
	 * Use 'H' for item hatch TODO
	 * Use 'P' for power TODO
	 * Use a class extending tile entity to require that tile be at that location
	 * Use a Block to force the user to place that block there
	 * @return true if the structure is valid
	 */
	protected boolean completeStructure() {

		//Make sure the environment is clean
		resetCache();

		Object[][][] structure = getStructure();

		Vector3F<Integer> offset = getControllerOffset(structure);

		ForgeDirection front = getFrontDirection();

		//Store tile entities for later processing so we don't risk the check failing halfway through leaving half the multiblock assigned
		LinkedList<TileEntity> tiles = new LinkedList<TileEntity>();

		for(int y = 0; y < structure.length; y++) {
			for(int z = 0; z < structure[0].length; z++) {
				for(int x = 0; x< structure[0][0].length; x++) {

					int globalX = xCoord + (x - offset.x)*front.offsetZ - (z-offset.z)*front.offsetX;
					int globalY = yCoord - y + offset.y;
					int globalZ = zCoord - (x - offset.x)*front.offsetX  - (z-offset.z)*front.offsetZ;

					TileEntity tile = worldObj.getTileEntity(globalX, globalY, globalZ);
					Block block = worldObj.getBlock(globalX, globalY, globalZ);

					if(tile != null)
						tiles.add(tile);

					//If the other block already thinks it's complete just assume valid
					if(tile instanceof TilePlaceholder) {
						if(((IMultiblock)tile).getMasterBlock() != this)
							return false;
						else 
							continue;
					}

					if(structure[y][z][x] instanceof Character && (Character)structure[y][z][x] == '*') {

						if(!(tile instanceof TileInventoryHatch) && !(tile instanceof TileRFBattery) && !getAllowableWildCardBlocks().contains(block)) {	
							return false;
						}

					}
					else if(structure[y][z][x] instanceof Block && block != structure[y][z][x]) {

						return false;
					}
					else if(structure[y][z][x] instanceof Class<?> && tile.getClass() != structure[y][z][x]) {

						return false;
					}

				}
			}
		}

		for(int y = 0; y < structure.length; y++) {
			for(int z = 0; z < structure[0].length; z++) {
				for(int x = 0; x< structure[0][0].length; x++) {

					int globalX = xCoord + (x - offset.x)*front.offsetZ - (z-offset.z)*front.offsetX;
					int globalY = yCoord - y + offset.y;
					int globalZ = zCoord - (x - offset.x)*front.offsetX  - (z-offset.z)*front.offsetZ;

					TileEntity tile = worldObj.getTileEntity(globalX, globalY, globalZ);
					Block block = worldObj.getBlock(globalX, globalY, globalZ);

					if(block instanceof BlockMultiblockStructure) {
						((BlockMultiblockStructure)block).completeStructure(worldObj, globalX, globalY, z, worldObj.getBlockMetadata(globalX, globalY, globalZ));
					}

					if(!(tile instanceof IMultiblock) && !(tile instanceof TileMultiBlockMachine)) {
						byte meta = (byte)worldObj.getBlockMetadata(globalX, globalY, globalZ);

						worldObj.setBlock(globalX, globalY, globalZ, AdvRocketryBlocks.blockPlaceHolder);
						TilePlaceholder newTile = (TilePlaceholder)worldObj.getTileEntity(globalX, globalY, globalZ);

						newTile.setReplacedBlock(block);
						newTile.setReplacedBlockMeta(meta);
						newTile.setReplacedTileEntity(tile);
						newTile.setMasterBlock(xCoord, yCoord, zCoord);
					}
				}
			}
		}

		//Now that we know the multiblock is valid we can assign
		for(TileEntity tile : tiles) {
			if(tile instanceof IMultiblock)
				((IMultiblock) tile).setComplete(xCoord, yCoord, zCoord);

			if(tile instanceof TileInputHatch)
				itemInPorts.add((IInventory) tile);
			else if(tile instanceof TileOutputHatch) 
				itemOutPorts.add((IInventory) tile);
			else if(tile instanceof TileRFBattery) {
				batteries.addBattery((IUniversalEnergy) tile);
			}
		}
		return true;
	}

	private Vector3F<Integer> getControllerOffset(Object[][][] structure) {
		for(int y = 0; y < structure.length; y++) {
			for(int z = 0; z < structure[0].length; z++) {
				for(int x = 0; x< structure[0][0].length; x++) {
					if(structure[y][z][x] instanceof Character && (Character)structure[y][z][x] == 'c')
						return new Vector3F<Integer>(x, y, z);
				}
			}
		}
		return null;
	}

	//Must be overridden or an NPE will occur
	public List<IRecipe> getMachineRecipeList() {
		return null;
	}

	//Called by inventory blocks that are part of the structure
	//This includes recipe management etc
	public void onInventoryUpdated() {
		//If we are already processing something don't bother
		if(outputItemStacks == null) {
			IRecipe recipe;

			if(enabled && (recipe = getRecipe(getMachineRecipeList())) != null && canProcessRecipe(recipe)) {
				consumeItems(recipe);
				powerPerTick = recipe.getPower();
				completionTime = recipe.getTime();
				outputItemStacks = recipe.getOutput();

				markDirty();
				worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);

				setMachineRunning(true); //turn on machine

			}
			else {
				setMachineRunning(false);
			}
		}
	}

	public void setMachineRunning(boolean running) {
		if(running && this.blockMetadata < 8) {
			this.blockMetadata |= 8;
			worldObj.setBlockMetadataWithNotify(xCoord, yCoord, zCoord, this.blockMetadata, 2);
		}
		else if(!running && this.blockMetadata >= 8) {
			this.blockMetadata &= 7;
			worldObj.setBlockMetadataWithNotify(xCoord, yCoord, zCoord, this.blockMetadata, 2); //Turn off machine
		}
	}

	@Override
	public void writeToNBT(NBTTagCompound nbt) {
		super.writeToNBT(nbt);

		nbt.setInteger("completionTime", this.completionTime);
		nbt.setInteger("currentTime", this.currentTime);
		nbt.setInteger("powerPerTick", this.powerPerTick);
		nbt.setBoolean("enabled", enabled);

		//Save output items if applicable
		if(outputItemStacks != null) {
			NBTTagList list = new NBTTagList();
			for(ItemStack stack : outputItemStacks) {
				NBTTagCompound tag = new NBTTagCompound();
				stack.writeToNBT(tag);
				list.appendTag(tag);
			}
			nbt.setTag("outputItems", list);
		}
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt) {
		super.readFromNBT(nbt);

		completionTime = nbt.getInteger("completionTime");
		currentTime = nbt.getInteger("currentTime");
		powerPerTick = nbt.getInteger("powerPerTick");
		enabled = nbt.getBoolean("enabled");

		//Load output items being processed if applicable
		if(nbt.hasKey("outputItems")) {
			outputItemStacks = new LinkedList<ItemStack>();
			NBTTagList list = nbt.getTagList("outputItems", 10);

			for(int i = 0; i < list.tagCount(); i++) {
				NBTTagCompound tag = list.getCompoundTagAt(i);

				outputItemStacks.add(ItemStack.loadItemStackFromNBT(tag));
			}
		}
	}

	@Override
	public void writeDataToNetwork(ByteBuf out, byte id) {
		if(id == NetworkPackets.TOGGLE.ordinal()) {
			out.writeBoolean(enabled);
		}
		if(id == NetworkPackets.POWERERROR.ordinal()) {
			out.writeBoolean(hadPowerLastTick);
		}
	}

	@Override
	public void readDataFromNetwork(ByteBuf in, byte packetId,
			NBTTagCompound nbt) {
		if(packetId == NetworkPackets.TOGGLE.ordinal()) {
			nbt.setBoolean("enabled", in.readBoolean());
		}
		else if(packetId == NetworkPackets.POWERERROR.ordinal()) {
			nbt.setBoolean("hadPowerLastTick", in.readBoolean());
		}
	}

	@Override
	public void useNetworkData(EntityPlayer player, Side side, byte id,
			NBTTagCompound nbt) {
		if(id == NetworkPackets.TOGGLE.ordinal()) {
			setMachineEnabled(nbt.getBoolean("enabled"));
		}
		else if(id == NetworkPackets.TOGGLE.ordinal()) {
			hadPowerLastTick = nbt.getBoolean("hadPowerLastTick");
		}
	}
}
