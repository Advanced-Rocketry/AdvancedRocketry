package zmaster587.advancedRocketry.tile.multiblock;

import io.netty.buffer.ByteBuf;

import java.util.LinkedList;
import java.util.List;
import cpw.mods.fml.relauncher.Side;
import zmaster587.advancedRocketry.recipe.RecipesMachine;
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
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.IFluidHandler;

public abstract class TileMultiblockMachine extends TileMultiPowerConsumer {

	public enum NetworkPackets {
		TOGGLE,
		POWERERROR
	}

	private List<ItemStack> outputItemStacks;
	private List<FluidStack> outputFluidStacks;

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

			if(isComplete())
				setComplete(completeStructure());

			if(isComplete() && !worldObj.isRemote)
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

	@Override
	public void resetCache() {
		super.resetCache();
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
		outputFluidStacks = null;
		super.deconstructMultiBlock(world, destroyedX, destroyedY, destroyedZ, blockBroken);
	}

	protected void processComplete() {
		completionTime = 0;
		currentTime = 0;
		if(!worldObj.isRemote)
			dumpOutputToInventory();

		outputItemStacks = null;
		outputFluidStacks = null;

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
					outInventory.getStackInSlot(smartInventoryUpgrade ? outInventory.getSizeInventory() - i - 1 : i).stackSize += outputItemStacks.get(i).stackSize;
					outInventory.markDirty();
					totalItems++;
				}
			}
		}

		//Handle fluids
		for(int i = 0; i < outputFluidStacks.size() ; i++) {
			fluidOutPorts.get(i).fill(ForgeDirection.UNKNOWN, outputFluidStacks.get(i), true);
		}
	}

	//TODO: improve recipe checks
	//Attempt to get a valid recipe given the inputs, null if none found
	protected IRecipe getRecipe(List<IRecipe> set) {

		for(IRecipe recipe : set) {

			if(canProcessRecipe(recipe))
				return recipe;

		}
		return null;
	}

	/**
	 * Provided in case a machine needs to override outputs for some reason without replacing larger functions
	 * @param recipe recipe to get outputs for
	 * @return list of itemstacks the machine can output
	 */
	protected List<ItemStack> getItemOutputs(IRecipe recipe) {
		return recipe.getOutput();
	}

	/**
	 * Provided in case a machine needs to override outputs for some reason without replacing larger functions
	 * @param recipe recipe to get outputs for
	 * @return list of fluidstacks the machine can output
	 */
	protected List<FluidStack> getFluidOutputs(IRecipe recipe) {
		return recipe.getFluidOutputs();
	}


	public void consumeItems(IRecipe recipe) {
		LinkedList<LinkedList<ItemStack>> ingredients = recipe.getIngredients();

		for(int ingredientNum = 0;ingredientNum < ingredients.size(); ingredientNum++) {

			LinkedList<ItemStack> ingredient = ingredients.get(ingredientNum);

			ingredientCheck:

				for(IInventory hatch : itemInPorts) {
					for(int i = 0; i < hatch.getSizeInventory(); i++) {
						ItemStack stackInSlot = hatch.getStackInSlot(i);

						for (ItemStack stack : ingredient) {
							if(stackInSlot != null && stackInSlot.stackSize >= stack.stackSize && stackInSlot.isItemEqual(stack)) {
								hatch.decrStackSize(i, stack.stackSize);
								hatch.markDirty();
								break ingredientCheck;
							}
						}
					}
				}
		}


		//Consume fluids
		int[] fluidInputCounter = new int[recipe.getFluidIngredients().size()];

		for(int i = 0; i < recipe.getFluidIngredients().size(); i++) {
			fluidInputCounter[i] = recipe.getFluidIngredients().get(i).amount;
		}

		//Drain Fluid containers
		for(IFluidHandler fluidInput : fluidInPorts) {
			for(int i = 0; i < recipe.getFluidIngredients().size(); i++) {
				FluidStack fluidStack = recipe.getFluidIngredients().get(i).copy();
				fluidStack.amount = fluidInputCounter[i];

				FluidStack drainedFluid;
				drainedFluid = fluidInput.drain(ForgeDirection.UNKNOWN, recipe.getFluidIngredients().get(i), true);

				if(drainedFluid != null)
					fluidInputCounter[i] -= drainedFluid.amount;

			}
		}
	}

	//Can this recipe be processed
	public boolean canProcessRecipe(IRecipe recipe) {

		if( !isComplete() || invCheckFlag)
			return false;

		invCheckFlag = true;
		List<ItemStack> outputItems = getItemOutputs(recipe);

		boolean itemCheck = outputItems.size() == 0;


		LinkedList<LinkedList<ItemStack>> ingredients = recipe.getIngredients();
		short mask = 0x0;
		recipeCheck:

			for(int ingredientNum = 0;ingredientNum < ingredients.size(); ingredientNum++) {

				List<ItemStack> ingredient = ingredients.get(ingredientNum);
				ingredientCheck:

					for(IInventory hatch : itemInPorts) {

						for(int i = 0; i < hatch.getSizeInventory(); i++) {
							ItemStack stackInSlot = hatch.getStackInSlot(i);

							for(ItemStack stack : ingredient) {
								if(stackInSlot != null && stackInSlot.stackSize >= stack.stackSize && stackInSlot.isItemEqual(stack)) {
									mask |= (1 << ingredientNum);
									break ingredientCheck;
								}
							}
						}

						//If no matching item is found for the ingredient
						//break recipeCheck;
					}


			}
		if(mask != (1 << ( ( ingredients.size() ) )) - 1) {
			invCheckFlag = false;
			return false;
		}


		//Check output Items
		bottomItemCheck:
			for(IInventory outInventory : itemOutPorts) {
				for(int i = smartInventoryUpgrade ? outInventory.getSizeInventory() - outputItems.size() : 0; (i < (smartInventoryUpgrade ? outInventory.getSizeInventory() : outputItems.size())); i++) {
					ItemStack stack = outInventory.getStackInSlot(i);

					if(smartInventoryUpgrade) {
						ItemStack outputItem = outputItems.get(outInventory.getSizeInventory() - i - 1);


						//stack cannot be null when assigning flag
						if(stack == null || stack.isItemEqual(outputItem) && stack.stackSize + outputItem.stackSize <= outInventory.getInventoryStackLimit()) {
							invCheckFlag = false;
							itemCheck = true;
							break bottomItemCheck;
						}

						if(stack != null && ZUtils.getFirstFilledSlotIndex(outInventory) >= outputItems.size()) {
							//Range Check
							int outputSize = outputItems.size();
							int j;
							for(j = 0; j < outputSize; j++) {
								if(outInventory.getStackInSlot(j) != null) {
									invCheckFlag = false;
									itemCheck = false;
									break bottomItemCheck;
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
							itemCheck = true;
							break bottomItemCheck;
						}
					}
					else if(stack == null || stack.isItemEqual(outputItems.get(i)) && stack.stackSize + outputItems.get(i).stackSize <= outInventory.getInventoryStackLimit()) {
						itemCheck = true;
						break bottomItemCheck;
					}

				}
			}

		int[] fluidInputCounter = new int[recipe.getFluidIngredients().size()];

		//Populate Fluid Counters
		for(IFluidHandler fluidInput : fluidInPorts) {
			for(int i = 0; i < recipe.getFluidIngredients().size(); i++) {
				FluidStack fluidStack = fluidInput.drain(ForgeDirection.UNKNOWN, recipe.getFluidIngredients().get(i), false);

				if(fluidStack != null)
					fluidInputCounter[i] += fluidStack.amount;
			}
		}

		invCheckFlag = false;
		for(int i = 0; i < recipe.getFluidIngredients().size(); i++) {
			if(fluidInputCounter[i] < recipe.getFluidIngredients().get(i).amount)
				return false;
		}

		//Check outputs


		if(fluidOutPorts.size() < recipe.getFluidOutputs().size())
			return false;

		int[] fluidOutputCounter = new int[recipe.getFluidOutputs().size()];

		//Populate the list
		for(int i = 0; i < recipe.getFluidOutputs().size(); i++) {
			fluidOutputCounter[i] = recipe.getFluidOutputs().get(i).amount;
		}

		//Populate Fluid Counters
		for(int i = 0; i < recipe.getFluidOutputs().size(); i++) {
			fluidOutputCounter[i] -= fluidOutPorts.get(i).fill(ForgeDirection.UNKNOWN, recipe.getFluidOutputs().get(i), false);
		}

		for(int i = 0; i < fluidOutputCounter.length; i++)
			if(fluidOutputCounter[i] > 0 )
				return false;

		return itemCheck;
	}


	//Used To make sure the multiblock is valid
	/*@Override
	public void updateContainingBlockInfo() {
		super.updateContainingBlockInfo();

		completeStructure = completeStructure();
	}*/


	//Must be overridden or an NPE will occur
	public List<IRecipe> getMachineRecipeList() {
		List<IRecipe> list = RecipesMachine.getInstance().getRecipes(this.getClass());
		return list != null ? list : new LinkedList<IRecipe>();
	}

	//Called by inventory blocks that are part of the structure
	//This includes recipe management etc
	@Override
	public void onInventoryUpdated() {
		//If we are already processing something don't bother

		if(!invCheckFlag && outputItemStacks == null && outputFluidStacks == null) {
			IRecipe recipe;

			if(enabled && (recipe = getRecipe(getMachineRecipeList())) != null && canProcessRecipe(recipe)) {
				consumeItems(recipe);
				powerPerTick = (int)Math.ceil((getPowerMultiplierForRecipe(recipe)*recipe.getPower()));
				completionTime = (int)(getTimeMultiplierForRecipe(recipe)*recipe.getTime());
				outputItemStacks = recipe.getOutput();
				outputFluidStacks = recipe.getFluidOutputs();

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

		if(outputFluidStacks != null) {
			NBTTagList list = new NBTTagList();
			for(FluidStack stack : outputFluidStacks) {
				if(stack != null) {
					NBTTagCompound tag = new NBTTagCompound();
					stack.writeToNBT(tag);
					list.appendTag(tag);
				}
			}
			nbt.setTag("outputFluids", list);
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

		//Load output fluids being processed if applicable
		if(nbt.hasKey("outputFluids")) {
			outputFluidStacks = new LinkedList<FluidStack>();
			NBTTagList list = nbt.getTagList("outputFluids", 10);

			for(int i = 0; i < list.tagCount(); i++) {
				NBTTagCompound tag = list.getCompoundTagAt(i);
				outputFluidStacks.add(FluidStack.loadFluidStackFromNBT(tag));
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
