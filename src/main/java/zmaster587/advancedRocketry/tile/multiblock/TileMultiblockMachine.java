package zmaster587.advancedRocketry.tile.multiblock;

import io.netty.buffer.ByteBuf;

import java.util.LinkedList;
import java.util.List;

import cpw.mods.fml.relauncher.Side;
import zmaster587.advancedRocketry.tile.TileInputHatch;
import zmaster587.advancedRocketry.tile.TileOutputHatch;
import zmaster587.libVulpes.interfaces.IRecipe;
import zmaster587.libVulpes.util.ZUtils;
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

public abstract class TileMultiblockMachine extends TileMultiPowerConsumer {

	public enum NetworkPackets {
		TOGGLE,
		POWERERROR
	}

	protected LinkedList<IInventory> itemInPorts = new LinkedList<IInventory>();
	protected LinkedList<IInventory> itemOutPorts = new LinkedList<IInventory>();


	private List<ItemStack> outputItemStacks;

	boolean smartInventoryUpgrade = true;
	//When using smart inventories sometimes setInventory content calls need to be made
	//This flag prevents infinite recursion by having a value of true if any invCheck has started
	boolean invCheckFlag = false;

	public TileMultiblockMachine() {
		super();
		outputItemStacks = null;
	}

	public List<ItemStack> getOutputs() {
		return outputItemStacks;
	}

	@Override
	public Packet getDescriptionPacket() {
		NBTTagCompound nbt = new NBTTagCompound();

		writeToNBT(nbt);
		nbt.setBoolean("built", canRender);
		nbt.setBoolean("hadPowerLastTick", hadPowerLastTick);
		return new S35PacketUpdateTileEntity(xCoord, yCoord, zCoord, 0, nbt);
	}

	@Override
	public void onDataPacket(NetworkManager net, S35PacketUpdateTileEntity pkt) {
		NBTTagCompound nbt = pkt.func_148857_g();

		canRender = nbt.getBoolean("built");
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
		if(timeAlive == 0  && !worldObj.isRemote) {

			if(completeStructure)
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

	@Override
	public void setMachineEnabled(boolean enabled) {
		super.setMachineEnabled(enabled);
		onInventoryUpdated();
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
		outputItemStacks = null;
		super.deconstructMultiBlock(world, destroyedX, destroyedY, destroyedZ, blockBroken);
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

	//When the output of the recipe is dumped to the inventory
	protected void dumpOutputToInventory() {

		int totalItems = 0;
		for(IInventory outInventory : itemOutPorts) {
			for(int i = totalItems; i < outputItemStacks.size(); i++) {
				ItemStack stack = outInventory.getStackInSlot(smartInventoryUpgrade ? outInventory.getSizeInventory() - i - 1 : i);

				if(stack == null) {
					outInventory.setInventorySlotContents(smartInventoryUpgrade ? outInventory.getSizeInventory() - i - 1 : i, outputItemStacks.get(i));
					outInventory.markDirty();
					totalItems++;
				}
				else if(stack.isItemEqual(outputItemStacks.get(i)) && stack.stackSize + outputItemStacks.get(i).stackSize <= outInventory.getInventoryStackLimit()) {
					outInventory.markDirty();
					outInventory.getStackInSlot(smartInventoryUpgrade ? outInventory.getSizeInventory() - i - 1 : i).stackSize += outputItemStacks.get(i).stackSize;
					totalItems++;
				}
			}
		}
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
							//break recipeCheck;
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




	//Must be overridden or an NPE will occur
	public abstract List<IRecipe> getMachineRecipeList();

	//Called by inventory blocks that are part of the structure
	//This includes recipe management etc
	public void onInventoryUpdated() {
		//If we are already processing something don't bother
		if(outputItemStacks == null) {
			IRecipe recipe;

			if(enabled && (recipe = getRecipe(getMachineRecipeList())) != null && canProcessRecipe(recipe)) {
				consumeItems(recipe);
				powerPerTick = (int)Math.ceil((getPowerMultiplierForRecipe(recipe)*recipe.getPower()));
				completionTime = (int)(getTimeMultiplierForRecipe(recipe)*recipe.getTime());
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

	protected float getTimeMultiplierForRecipe(IRecipe recipe) {
		return 1f;
	}

	protected float getPowerMultiplierForRecipe(IRecipe recipe) {
		return 1f;
	}

	@Override
	protected void integrateTile(TileEntity tile) {
		super.integrateTile(tile);

		if(tile instanceof TileInputHatch)
			itemInPorts.add((IInventory) tile);
		else if(tile instanceof TileOutputHatch) 
			itemOutPorts.add((IInventory) tile);
	}

	@Override
	public void writeToNBT(NBTTagCompound nbt) {
		super.writeToNBT(nbt);

		//Save output items if applicable
		if(outputItemStacks != null) {
			NBTTagList list = new NBTTagList();
			for(ItemStack stack : outputItemStacks) {
				if(stack != null) {
					NBTTagCompound tag = new NBTTagCompound();
					stack.writeToNBT(tag);
					list.appendTag(tag);
				}
			}
			nbt.setTag("outputItems", list);
		}
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt) {
		super.readFromNBT(nbt);

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

	public boolean attemptCompleteStructure() {
		boolean completeStructure = super.attemptCompleteStructure();
		if(completeStructure)
			onInventoryUpdated();

		return completeStructure;
	}

	@Override
	public void writeDataToNetwork(ByteBuf out, byte id) {
		super.writeDataToNetwork(out, id);
	}

	@Override
	public void readDataFromNetwork(ByteBuf in, byte packetId,
			NBTTagCompound nbt) {
		super.readDataFromNetwork(in, packetId, nbt);
	}

	@Override
	public void useNetworkData(EntityPlayer player, Side side, byte id,
			NBTTagCompound nbt) {
		super.useNetworkData(player, side, id, nbt);
	}
}
