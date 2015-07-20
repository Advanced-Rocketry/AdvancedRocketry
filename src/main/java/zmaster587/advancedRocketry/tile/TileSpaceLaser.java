package zmaster587.advancedRocketry.tile;

import io.netty.buffer.ByteBuf;
import cofh.api.energy.EnergyStorage;
import cofh.api.energy.IEnergyHandler;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import zmaster587.advancedRocketry.AdvancedRocketry;
import zmaster587.advancedRocketry.api.AdvRocketryBlocks;
import zmaster587.advancedRocketry.integration.CompatibilityMgr;
import zmaster587.advancedRocketry.network.PacketHandler;
import zmaster587.advancedRocketry.network.PacketMachine;
import zmaster587.advancedRocketry.satallite.SatelliteLaser;
import zmaster587.libVulpes.block.RotatableBlock;
import zmaster587.libVulpes.util.INetworkMachine;
import zmaster587.libVulpes.util.ZUtils;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.ChunkCoordIntPair;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.common.ForgeChunkManager;
import net.minecraftforge.common.ForgeChunkManager.Type;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.common.ForgeChunkManager.Ticket;
import net.minecraftforge.oredict.OreDictionary;

public class TileSpaceLaser extends TileEntity implements ISidedInventory, IEnergyHandler, INetworkMachine {

	private static final int INVSIZE = 9;
	protected EnergyStorage storage = new EnergyStorage(1000000);
	ItemStack glassPanel;
	//ItemStack invBuffer[];
	SatelliteLaser laserSat;
	protected boolean isRunning, finished;
	protected IInventory adjInv;
	private int radius, xCenter, yCenter, numSteps;
	private ForgeDirection prevDir;
	public int laserX, laserZ, tickSinceLastOperation;
	private static final ForgeDirection[] VALID_INVENTORY_DIRECTIONS = { ForgeDirection.NORTH, ForgeDirection.EAST, ForgeDirection.SOUTH, ForgeDirection.WEST};
	private static final int POWER_PER_OPERATION = 10000;

	public enum MODE{
		SINGLE,
		LINE_X,
		LINE_Z,
		SPIRAL
	};

	private MODE mode;

	Ticket ticket;

	public TileSpaceLaser() {
		glassPanel = null;
		//invBuffer = new ItemStack[INVSIZE];
		radius = 0;
		xCenter = 0;
		yCenter = 0;
		numSteps = 0;
		prevDir = ForgeDirection.UNKNOWN;

		tickSinceLastOperation = 0;
		laserX = 0;
		laserZ = 0;
		laserSat = new SatelliteLaser(this);
		isRunning = false;
		finished = false;
		mode = MODE.SINGLE;
	}

	//Required so we see the laser
	@SideOnly(Side.CLIENT)
	@Override
	public AxisAlignedBB getRenderBoundingBox() {
		return AxisAlignedBB.getBoundingBox(this.xCoord + 1, this.yCoord, this.zCoord + 1, this.xCoord, this.yCoord - 100, this.zCoord);
	}

	/*
	 * ID 0: client changed xcoord in interface
	 * ID 1: client changed ycoord in interface
	 * ID 2: sync whether the machine is running
	 * ID 3: sync Mode
	 * ID 4: jam reset
	 */
	@Override
	public void writeDataToNetwork(ByteBuf out, byte id) {
		if(id == 0)
			out.writeInt(this.laserX);
		else if(id == 1)
			out.writeInt(this.laserZ);
		else if(id == 2)
			out.writeBoolean(isRunning);
		else if(id == 3)
			out.writeInt(mode.ordinal());
	}

	@Override
	public void readDataFromNetwork(ByteBuf in, byte id,
			NBTTagCompound nbt) {
		if(id == 0)
			nbt.setInteger("laserX", in.readInt());
		else if(id == 1)
			nbt.setInteger("laserZ", in.readInt());
		else if(id == 2)
			nbt.setBoolean("isRunning", in.readBoolean());
		else if(id == 3)
			nbt.setInteger("mode", in.readInt());

	}

	@Override
	public void useNetworkData(EntityPlayer player, Side side, byte id,
			NBTTagCompound nbt) {
		if(id == 0) {
			this.laserX = nbt.getInteger("laserX");
			finished = false;

			if(mode == MODE.SPIRAL)
				resetSpiral();
		}
		else if(id == 1) {
			this.laserZ = nbt.getInteger("laserZ");
			finished = false;
			if(mode == MODE.SPIRAL)
				resetSpiral();
		}
		else if(id == 2)
			this.isRunning = nbt.getBoolean("isRunning");
		else if(id == 3 && !isRunning())
			this.mode = MODE.values()[nbt.getInteger("mode")];
		else if(id == 4)
			this.attempUnjam();

	}

	private void resetSpiral() {
		radius = 0;
		prevDir = ForgeDirection.UNKNOWN;
		xCenter = 0;
		yCenter = 0;
		numSteps = 0;
	}

	public boolean isRunning() {return isRunning;}

	public boolean isFinished() {return finished;}

	public MODE getMode() {return mode;}

	public void incrementMode() {
		if(mode == MODE.SPIRAL)
			resetSpiral();

		int num = mode.ordinal();
		num++;

		if(num >= MODE.values().length)
			num = 0;

		mode = MODE.values()[num];
	}

	public void decrementMode() {
		if(mode == MODE.SPIRAL)
			resetSpiral();

		int num = mode.ordinal();
		num--;

		if(num < 0)
			num = MODE.values().length - 1;

		mode = MODE.values()[num];
	}

	public void setMode(MODE m) {mode = m;};

	@Override
	public boolean canUpdate() {return true;}

	public void setFinished(boolean value) { finished = value; }

	@Override
	public void updateEntity() {
		//TODO: drain energy
		if(!this.worldObj.isRemote) {
			tickSinceLastOperation++;
		}

		if(hasPowerForOperation() && isReadyForOperation() && laserSat.isAlive() && !laserSat.getJammed()) {
			laserSat.performOperation();

			storage.setEnergyStored(storage.getEnergyStored() - POWER_PER_OPERATION);
			tickSinceLastOperation = 0;
		}

		if(laserSat.isFinished()) {
			this.isRunning = false;
			laserSat.deactivateLaser();

			if(!laserSat.getJammed()) {
				if(mode == MODE.SINGLE) 
					finished = true;

				if(this.worldObj.getBlockPowerInput(this.xCoord, this.yCoord, this.zCoord) != 0) {
					if(mode == MODE.LINE_X) {
						this.laserX += 3;
					}
					else if(mode == MODE.LINE_Z) {
						this.laserZ += 3;
					}
					else if(mode == MODE.SPIRAL) {
						numSteps++;
						if(radius < numSteps) {
							numSteps = 0;
							if(prevDir == ForgeDirection.NORTH)
								prevDir = ForgeDirection.EAST;
							else if(prevDir == ForgeDirection.EAST){
								prevDir = ForgeDirection.SOUTH;
								radius++;
							}
							else if(prevDir == ForgeDirection.SOUTH)
								prevDir = ForgeDirection.WEST;
							else {
								prevDir = ForgeDirection.NORTH;
								radius++;
							}
						}

						this.laserX += 3*prevDir.offsetX;
						this.laserZ += 3*prevDir.offsetZ;
					}
				}
				//TODO: unneeded?
				checkCanRun();
			}
		}
	}

	public boolean isReadyForOperation() {
		if(storage.getEnergyStored() == 0)
			return false;

		return tickSinceLastOperation > (3*this.storage.getMaxEnergyStored()/(float)this.storage.getEnergyStored());
	}

	public void onDestroy() {
		if(laserSat != null) {
			laserSat.deactivateLaser();
		}
		ForgeChunkManager.releaseTicket(ticket);
	}

	@Override
	public void onChunkUnload() {
		if(laserSat != null) {
			laserSat.deactivateLaser();
		}
		isRunning = false;
	}

	@Override
	public Packet getDescriptionPacket() {
		NBTTagCompound nbt = new NBTTagCompound();
		this.writeToNBT(nbt);

		nbt.setBoolean("IsRunning", isRunning);
		
		return new S35PacketUpdateTileEntity(this.xCoord, this.yCoord, this.zCoord, 0, nbt);
	}

	@Override 
	public void onDataPacket(NetworkManager net, S35PacketUpdateTileEntity pkt) {
		this.readFromNBT(pkt.func_148857_g());
		isRunning = pkt.func_148857_g().getBoolean("IsRunning");
	}

	@Override
	public void writeToNBT(NBTTagCompound nbt) {
		super.writeToNBT(nbt);
		storage.writeToNBT(nbt);

		NBTTagCompound laser = new NBTTagCompound();
		laserSat.writeToNBT(laser);

		nbt.setTag("laser", laser);

		NBTTagList list = new NBTTagList();
		NBTTagList itemList = new NBTTagList();
		/*for(int i = 0; i < invBuffer.length; i++)
		{
			ItemStack stack = invBuffer[i];
			if(stack != null) {
				NBTTagCompound tag = new NBTTagCompound();
				tag.setByte("Slot", (byte)(i));
				stack.writeToNBT(tag);
				itemList.appendTag(tag);
			}
		}
		nbt.setTag("InventoryBuffer", itemList);*/


		if(glassPanel != null) {
			NBTTagCompound tag = new NBTTagCompound();
			glassPanel.writeToNBT(tag);
			nbt.setTag("GlassPane", tag);
		}

		nbt.setInteger("laserX", laserX);
		nbt.setInteger("laserZ", laserZ);
		nbt.setByte("mode", (byte)mode.ordinal());

		if(mode == MODE.SPIRAL) {
			nbt.setInteger("CenterX", xCenter);
			nbt.setInteger("CenterY", yCenter);
			nbt.setInteger("radius", radius);
			nbt.setInteger("numSteps", numSteps);
			nbt.setInteger("prevDir", prevDir.ordinal());
		}
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt) {
		super.readFromNBT(nbt);
		storage.readFromNBT(nbt);

		laserSat.readFromNBT(nbt.getCompoundTag("laser"));

		/*NBTTagList tagList = nbt.getTagList("Inventory");
		for (int i = 0; i < tagList.tagCount(); i++) {
			NBTTagCompound tag = (NBTTagCompound) tagList.tagAt(i);
			byte slot = tag.getByte("Slot");
			if (slot >= 0 && slot < invBuffer.length) {
				invBuffer[slot] = ItemStack.loadItemStackFromNBT(tag);
			}
		}*/
		if(nbt.hasKey("GlassPane")) {
			NBTTagCompound tag = nbt.getCompoundTag("GlassPane");
			glassPanel = ItemStack.loadItemStackFromNBT(tag);
		}

		laserX = nbt.getInteger("laserX");
		laserZ = nbt.getInteger("laserZ");
		mode = MODE.values()[nbt.getByte("mode")];

		if(mode == MODE.SPIRAL){
			xCenter = nbt.getInteger("CenterX");
			yCenter = nbt.getInteger("CenterY");
			radius = nbt.getInteger("radius");
			numSteps = nbt.getInteger("numSteps");
			prevDir = ForgeDirection.values()[nbt.getInteger("prevDir")];
		}
	}

	/**
	 * Needs to be called when the laser is finished
	 */
	public void onLaserFinish() {

	}

	/**
	 * Take items from internal inventory
	 * @return
	 */
	public void attempUnjam() {
		if(!laserSat.getJammed())
			return;

		/*IInventory depositInv = getAvalibleInv(ZUtils.getFirstFilledSlot(invBuffer));

		//Assign subInv


		while(depositInv != null) {
			ZUtils.mergeInventory(invBuffer, depositInv);

			if(ZUtils.isInvEmpty(invBuffer))
				break;
			depositInv = getAvalibleInv(ZUtils.getFirstFilledSlot(invBuffer));
		}*/

		//TODO: finish
		laserSat.setJammed(false);
		//!ZUtils.isInvEmpty(invBuffer);
		finished = false;

		checkCanRun();
	}

	//TODO: buildcraft support

	/**
	 * Gets the first inventory with an empty slot
	 * @return first available inventory or null
	 */
	private IInventory getAvalibleInv() {
		ForgeDirection front = RotatableBlock.getFront(this.getBlockMetadata());

		for(ForgeDirection f : VALID_INVENTORY_DIRECTIONS) {
			if(f == front)
				continue;

			TileEntity e = this.worldObj.getTileEntity(this.xCoord + f.offsetX, this.yCoord + f.offsetY, this.zCoord + f.offsetZ);


			if(e != null && e instanceof IInventory && ZUtils.numEmptySlots((IInventory)e) > 0)
				return (IInventory)e;
		}
		return null;
	}

	/**
	 * Gets the first inv with a slot able to hold 'itemStack'
	 * @param item item to fit into inventory
	 * @return inv with capablity of holding 'itemStack'
	 */
	private IInventory getAvalibleInv(ItemStack item) {
		if(item == null)
			return getAvalibleInv();

		ForgeDirection front = RotatableBlock.getFront(this.getBlockMetadata());

		for(ForgeDirection f : VALID_INVENTORY_DIRECTIONS) {
			if(f == front)
				continue;

			TileEntity e = this.worldObj.getTileEntity(this.xCoord + f.offsetX, this.yCoord + f.offsetY, this.zCoord + f.offsetZ);


			if(e != null && e instanceof IInventory && (ZUtils.numEmptySlots((IInventory)e) > 0 || ZUtils.doesInvHaveRoom(item, (IInventory)e)))
				return (IInventory)e;
		}
		return null;
	}

	private boolean canMachineSeeEarth() {
		for(int i = yCoord - 1; i > 0; i--) {
			if(worldObj.isBlockNormalCubeDefault(xCoord, i, zCoord,true))
				return false;
		}
		return true;
	}
	
	/**
	 * Checks to see if the situation for firing the laser exists... and changes the state accordingly
	 */
	public void checkCanRun() {
		//Laser requires lense, redstone power, not be jammed, and be in orbit and energy to function
		if(!worldObj.isBlockIndirectlyGettingPowered(xCoord, yCoord, zCoord) || glassPanel == null || storage.getEnergyStored() == 0 /*|| !(this.worldObj.provider instanceof IOrbitDimension)*/) {
			if(laserSat.isAlive()) {
				laserSat.deactivateLaser();
			}

			isRunning = false;
		} else if(!laserSat.isAlive() && !finished && !laserSat.getJammed() && worldObj.isBlockIndirectlyGettingPowered(xCoord, yCoord, zCoord) && canMachineSeeEarth()) {

			//Laser will be on at this point
			int orbitDimId =0;//= WorldUtil.getProviderForName(((IOrbitDimension)this.worldObj.provider).getPlanetToOrbit()).dimensionId;
			WorldServer orbitWorld = DimensionManager.getWorld(orbitDimId);


			if(ticket == null) {
				ticket = ForgeChunkManager.requestTicket(AdvancedRocketry.instance, this.worldObj, Type.NORMAL);
				if(ticket != null)
					ForgeChunkManager.forceChunk(ticket, new ChunkCoordIntPair(this.xCoord / 16 - (this.xCoord < 0 ? 1 : 0), this.zCoord / 16 - (this.zCoord < 0 ? 1 : 0)));
			}

			isRunning = laserSat.activateLaser(orbitWorld, laserX, laserZ);
		}

		if(!this.worldObj.isRemote)
			PacketHandler.sentToNearby(new PacketMachine(this, (byte)2), this.xCoord, this.yCoord, this.zCoord, 128, this.worldObj.provider.dimensionId);
	}

	public int getEnergyPercentScaled(int max) {
		return (int)(max * (storage.getEnergyStored() / (float)storage.getMaxEnergyStored()) );
	}

	public boolean hasEnergy() {
		return storage.getEnergyStored() != 0;
	}

	//InventoryHandling start
	@Override
	public int getSizeInventory() {
		int sizeInv = 0;
		ForgeDirection front = RotatableBlock.getFront(this.getBlockMetadata());

		for(ForgeDirection f : VALID_INVENTORY_DIRECTIONS) {
			if(f == front)
				continue;

			TileEntity e = this.worldObj.getTileEntity(this.xCoord + f.offsetX, this.yCoord + f.offsetY, this.zCoord + f.offsetZ);

			//TODO: may cause inf loop
			if(e != null && e instanceof IInventory)
				sizeInv += ((IInventory)e).getSizeInventory();
		}

		return sizeInv;
	}

	@Override
	public ItemStack getStackInSlot(int i) {
		if(i == 0)
			return glassPanel;
		else {
			ForgeDirection front = RotatableBlock.getFront(this.getBlockMetadata());

			for(ForgeDirection f : VALID_INVENTORY_DIRECTIONS) {
				if(f == front)
					continue;

				TileEntity e = this.worldObj.getTileEntity(this.xCoord + f.offsetX, this.yCoord + f.offsetY, this.zCoord + f.offsetZ);

				//TODO: may cause inf loop
				if(e != null && e instanceof IInventory)
					if(i < ((IInventory)e).getSizeInventory())
						return ((IInventory)e).getStackInSlot(i);
					else
						i -= ((IInventory)e).getSizeInventory();
			}
			return null;
		}
	}

	@Override
	public ItemStack decrStackSize(int i, int j) {
		ItemStack ret;

		if(i == 0) {
			ret = glassPanel.copy();
			glassPanel = null;
			return ret;
		}
		return null;
	}

	@Override
	public ItemStack getStackInSlotOnClosing(int i) {
		if(i == 0)
			return glassPanel;
		return null;
	}

	@Override
	public void setInventorySlotContents(int i, ItemStack itemstack) {

		//TODO: add gregcipies
		if(i == 0)
			glassPanel = itemstack;
		else {

			ForgeDirection front = RotatableBlock.getFront(this.getBlockMetadata());

			for(ForgeDirection f : VALID_INVENTORY_DIRECTIONS) {
				if(f == front)
					continue;

				TileEntity e = this.worldObj.getTileEntity(this.xCoord + f.offsetX, this.yCoord + f.offsetY, this.zCoord + f.offsetZ);

				//TODO: may cause inf loop
				if(e != null && e instanceof IInventory)
					if(i < ((IInventory)e).getSizeInventory()) {
						((IInventory)e).setInventorySlotContents(i, itemstack);
						break;
					}	
					else
						i -= ((IInventory)e).getSizeInventory();
			}

			this.checkCanRun();
		}
	}

	@Override
	public String getInventoryName() {
		return "Orbital Laser";
	}


	@Override
	public int getInventoryStackLimit() {
		return 64;
	}

	@Override
	public boolean isUseableByPlayer(EntityPlayer entityplayer) {
		return entityplayer.getDistanceSq(this.xCoord, this.yCoord, this.zCoord) <= 64;
	}

	@Override
	public void openInventory() {
		// TODO Perhaps make sure laser isn't running
	}

	@Override
	public void closeInventory() {
		// TODO Auto-generated method stub

	}

	@Override
	public int[] getAccessibleSlotsFromSide(int var1) {
		return null;
	}

	@Override
	public boolean canInsertItem(int i, ItemStack itemstack, int j) {
		return false;
	}

	@Override
	public boolean canExtractItem(int i, ItemStack itemstack, int j) {
		return false;
	}

	@Override
	public boolean isItemValidForSlot(int i, ItemStack itemstack) {
		if(i == 0)
			return CompatibilityMgr.gregtechLoaded ? OreDictionary.getOreName(OreDictionary.getOreID(itemstack)).equals("lenseRuby") : Item.getItemFromBlock(Blocks.glass_pane) == itemstack.getItem() ? true : false;

			ForgeDirection front = RotatableBlock.getFront(this.getBlockMetadata());

			for(ForgeDirection f : VALID_INVENTORY_DIRECTIONS) {
				if(f == front)
					continue;

				TileEntity e = this.worldObj.getTileEntity(this.xCoord + f.offsetX, this.yCoord + f.offsetY, this.zCoord + f.offsetZ);

				//TODO: may cause inf loop
				if(e != null && e instanceof IInventory)
					if(i < ((IInventory)e).getSizeInventory())
						return ((IInventory)e).isItemValidForSlot(i,itemstack);
					else
						i -= ((IInventory)e).getSizeInventory();
			}
			return false;
	}
	//InventoryHandling end

	//Redstone Flux start

	/**
	 * @param simulate true to simulate.. false to drain the power
	 * @return returns whether enough power is stored for the next opertation
	 */
	public boolean hasPowerForOperation() {
		return POWER_PER_OPERATION <= storage.getEnergyStored();
	}

	/**
	 * DO NOT USE UNLESS YOU HAVE NO OTHER OPTION!!!
	 * @param amt amount to set energy to
	 */
	public void setEnergy(int amt) {
		storage.setEnergyStored(amt);
	}

	@Override
	public int receiveEnergy(ForgeDirection from, int maxReceive,
			boolean simulate) {
		if(from == ForgeDirection.DOWN || from == RotatableBlock.getFront(this.worldObj.getBlockMetadata(this.xCoord, this.yCoord, this.zCoord)))
			return 0;

		return storage.receiveEnergy(maxReceive, simulate);
	}

	@Override
	public int extractEnergy(ForgeDirection from, int maxExtract,
			boolean simulate) {
		return 0;
	}

	@Override
	public int getEnergyStored(ForgeDirection from) {
		return storage.getEnergyStored();
	}

	@Override
	public int getMaxEnergyStored(ForgeDirection from) {
		return storage.getMaxEnergyStored();
	}
	//Redstone Flux end

	public boolean isJammed() {
		return laserSat.getJammed();
	}

	public void setJammed(boolean b) {
		laserSat.setJammed(b);

	}

	@Override
	public boolean hasCustomInventoryName() {
		return false;
	}

	@Override
	public boolean canConnectEnergy(ForgeDirection from) {
		return from != ForgeDirection.DOWN && from != RotatableBlock.getFront(this.worldObj.getBlockMetadata(this.xCoord, this.yCoord, this.zCoord));
	}
}