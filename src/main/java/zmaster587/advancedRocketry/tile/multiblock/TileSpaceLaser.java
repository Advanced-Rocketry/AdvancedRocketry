package zmaster587.advancedRocketry.tile.multiblock;

import io.netty.buffer.ByteBuf;

import java.util.LinkedList;
import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ISidedInventory;
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
import net.minecraftforge.common.ForgeChunkManager.Ticket;
import net.minecraftforge.common.ForgeChunkManager.Type;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.oredict.OreDictionary;
import zmaster587.advancedRocketry.AdvancedRocketry;
import zmaster587.advancedRocketry.api.AdvancedRocketryItems;
import zmaster587.advancedRocketry.api.Configuration;
import zmaster587.advancedRocketry.integration.CompatibilityMgr;
import zmaster587.advancedRocketry.inventory.TextureResources;
import zmaster587.advancedRocketry.satellite.SatelliteLaser;
import zmaster587.advancedRocketry.satellite.SatelliteLaserNoDrill;
import zmaster587.advancedRocketry.stations.SpaceObjectManager;
import zmaster587.advancedRocketry.world.provider.WorldProviderSpace;
import zmaster587.libVulpes.api.LibVulpesBlocks;
import zmaster587.libVulpes.block.RotatableBlock;
import zmaster587.libVulpes.compat.InventoryCompat;
import zmaster587.libVulpes.inventory.modules.IButtonInventory;
import zmaster587.libVulpes.inventory.modules.IGuiCallback;
import zmaster587.libVulpes.inventory.modules.IModularInventory;
import zmaster587.libVulpes.inventory.modules.ModuleBase;
import zmaster587.libVulpes.inventory.modules.ModuleButton;
import zmaster587.libVulpes.inventory.modules.ModuleImage;
import zmaster587.libVulpes.inventory.modules.ModuleNumericTextbox;
import zmaster587.libVulpes.inventory.modules.ModulePower;
import zmaster587.libVulpes.inventory.modules.ModuleSlotArray;
import zmaster587.libVulpes.inventory.modules.ModuleText;
import zmaster587.libVulpes.inventory.modules.ModuleTextBox;
import zmaster587.libVulpes.network.PacketHandler;
import zmaster587.libVulpes.network.PacketMachine;
import zmaster587.libVulpes.tile.multiblock.TileMultiPowerConsumer;
import zmaster587.libVulpes.util.INetworkMachine;
import zmaster587.libVulpes.util.MultiInventory;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class TileSpaceLaser extends TileMultiPowerConsumer implements ISidedInventory, INetworkMachine, IModularInventory, IGuiCallback, IButtonInventory {

	private static final int INVSIZE = 9;
	ItemStack glassPanel;
	//ItemStack invBuffer[];
	SatelliteLaserNoDrill laserSat;
	protected boolean isRunning, finished;
	protected IInventory adjInv;
	private int radius, xCenter, yCenter, numSteps;
	private ForgeDirection prevDir;
	public int laserX, laserZ, tickSinceLastOperation;
	private static final ForgeDirection[] VALID_INVENTORY_DIRECTIONS = { ForgeDirection.NORTH, ForgeDirection.EAST, ForgeDirection.SOUTH, ForgeDirection.WEST};
	private static final int POWER_PER_OPERATION = (int) (10000  * Configuration.spaceLaserPowerMult); 
	private ModuleTextBox locationX, locationZ;
	private ModuleText updateText;
	MultiInventory inv;

	Object[][][] structure = new Object[][][]{
			{
				{null, null, null, null, null},
				{null, null, LibVulpesBlocks.blockAdvStructureBlock, null, null},
				{null, LibVulpesBlocks.blockAdvStructureBlock, LibVulpesBlocks.blockAdvStructureBlock, LibVulpesBlocks.blockAdvStructureBlock, null},
				{null, null, LibVulpesBlocks.blockAdvStructureBlock, null, null},
				{null, null, null, null, null}
			},
			{
				{null, null,'c', null, null},
				{null, 'P', Blocks.glass, 'P', null},
				{'P', LibVulpesBlocks.blockAdvStructureBlock, Blocks.glass, LibVulpesBlocks.blockAdvStructureBlock, 'P'},
				{null, 'P', LibVulpesBlocks.blockAdvStructureBlock, 'P', null},
				{null, null, 'P', null, null}
			},
			{
				{null, null, LibVulpesBlocks.blockAdvStructureBlock, null, null},
				{null, LibVulpesBlocks.blockAdvStructureBlock, LibVulpesBlocks.blockAdvStructureBlock, LibVulpesBlocks.blockAdvStructureBlock, null},
				{LibVulpesBlocks.blockAdvStructureBlock, LibVulpesBlocks.blockAdvStructureBlock, Blocks.glass, LibVulpesBlocks.blockAdvStructureBlock, LibVulpesBlocks.blockAdvStructureBlock},
				{null, LibVulpesBlocks.blockAdvStructureBlock, LibVulpesBlocks.blockAdvStructureBlock, LibVulpesBlocks.blockAdvStructureBlock, null},
				{null, null, LibVulpesBlocks.blockAdvStructureBlock, null, null}
			},
			{
				{null, null, 'O', null, null},
				{null, LibVulpesBlocks.blockAdvStructureBlock, Blocks.glass, LibVulpesBlocks.blockAdvStructureBlock, null},
				{'O', Blocks.glass, Blocks.glass, Blocks.glass, 'O'},
				{null, LibVulpesBlocks.blockAdvStructureBlock, Blocks.glass, LibVulpesBlocks.blockAdvStructureBlock, null},
				{null, null, 'O', null, null}
			},
	};
	
	public enum MODE{
		SINGLE,
		LINE_X,
		LINE_Z,
		SPIRAL
	};

	private MODE mode;

	Ticket ticket;

	public TileSpaceLaser() {
		super();
		glassPanel = null;
		//invBuffer = new ItemStack[INVSIZE];
		radius = 0;
		xCenter = 0;
		yCenter = 0;
		numSteps = 0;
		prevDir = ForgeDirection.UNKNOWN;

		
		inv = new MultiInventory(itemOutPorts);
		tickSinceLastOperation = 0;
		laserX = 0;
		laserZ = 0;
		if(Configuration.laserDrillPlanet)
			laserSat = new SatelliteLaser(inv);
		else
			laserSat = new SatelliteLaserNoDrill(inv);
		
		isRunning = false;
		finished = false;
		mode = MODE.SINGLE;
	}

	//Required so we see the laser
	@SideOnly(Side.CLIENT)
	@Override
	public AxisAlignedBB getRenderBoundingBox() {
		return AxisAlignedBB.getBoundingBox(this.xCoord -5, this.yCoord - 100, this.zCoord - 5, this.xCoord + 5, this.yCoord +5, this.zCoord + 5);
	}

	@Override
	public Object[][][] getStructure() {
		return structure;
	}
	
	@Override
	public String getMachineName() {
		return getInventoryName();
	}
	
	/*
	 * ID 10: client changed xcoord in interface
	 * ID 11: client changed ycoord in interface
	 * ID 12: sync whether the machine is running
	 * ID 13: sync Mode
	 * ID 14: jam reset
	 */
	@Override
	public void writeDataToNetwork(ByteBuf out, byte id) {
		super.writeDataToNetwork(out, id);
		if(id == 10)
			out.writeInt(this.laserX);
		else if(id == 11)
			out.writeInt(this.laserZ);
		else if(id == 12)
			out.writeBoolean(isRunning);
		else if(id == 13)
			out.writeInt(mode.ordinal());
	}

	@Override
	public void readDataFromNetwork(ByteBuf in, byte id,
			NBTTagCompound nbt) {
		super.readDataFromNetwork(in, id, nbt);
		if(id == 10)
			nbt.setInteger("laserX", in.readInt());
		else if(id == 11)
			nbt.setInteger("laserZ", in.readInt());
		else if(id == 12)
			nbt.setBoolean("isRunning", in.readBoolean());
		else if(id == 13)
			nbt.setInteger("mode", in.readInt());

	}

	@Override
	public void useNetworkData(EntityPlayer player, Side side, byte id,
			NBTTagCompound nbt) {
		super.useNetworkData(player, side, id, nbt);
		if(id == 10) {
			this.laserX = nbt.getInteger("laserX");
			finished = false;

			if(mode == MODE.SPIRAL)
				resetSpiral();
		}
		else if(id == 11) {
			this.laserZ = nbt.getInteger("laserZ");
			finished = false;
			if(mode == MODE.SPIRAL)
				resetSpiral();
		}
		else if(id == 12)
			this.isRunning = nbt.getBoolean("isRunning");
		else if(id == 13 && !isRunning())
			this.mode = MODE.values()[nbt.getInteger("mode")];
		else if(id == 14)
			this.attempUnjam();

		markDirty();
	}

	private void resetSpiral() {
		radius = 0;
		prevDir = ForgeDirection.UNKNOWN;
		xCenter = 0;
		yCenter = 0;
		numSteps = 0;
	}

	@Override
	public boolean isRunning() {return isRunning && isComplete();}

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

	private void setRunning(boolean value) {
		this.isRunning = value;
		markDirty();
		this.worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
	}

	@Override
	public void updateEntity() {
		//TODO: drain energy
		
		//Freaky jenky crap to make sure the multiblock loads on chunkload etc
		if(timeAlive == 0 && !worldObj.isRemote) {
			if(isComplete())
				canRender = completeStructure = completeStructure();
			timeAlive = 0x1;
		}
		
		if(!this.worldObj.isRemote) {
			tickSinceLastOperation++;

			if(!isAllowedToRun()) {
				laserSat.deactivateLaser();
				this.setFinished(true);
				this.setRunning(false);
			}
			else
				if(hasPowerForOperation() && isReadyForOperation() && laserSat.isAlive() && !laserSat.getJammed()) {
					laserSat.performOperation();

					batteries.extractEnergy(POWER_PER_OPERATION, false);
					tickSinceLastOperation = 0;
				}
		}

		if(laserSat.isFinished()) {
			setRunning(false);
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
		if(batteries.getEnergyStored() == 0)
			return false;

		return tickSinceLastOperation > (3*this.batteries.getMaxEnergyStored()/(float)this.batteries.getEnergyStored());
	}

	public void onDestroy() {
		if(laserSat != null) {
			laserSat.deactivateLaser();
		}
		ForgeChunkManager.releaseTicket(ticket);
	}

	@Override
	public void onChunkUnload() {
		super.onChunkUnload();
		if(laserSat != null) {
			laserSat.deactivateLaser();
		}
		isRunning = false;
	}

	@Override
	public Packet getDescriptionPacket() {
		NBTTagCompound nbt = ((S35PacketUpdateTileEntity)super.getDescriptionPacket()).func_148857_g();
		this.writeToNBT(nbt);

		nbt.setBoolean("IsRunning", isRunning);

		return new S35PacketUpdateTileEntity(this.xCoord, this.yCoord, this.zCoord, 0, nbt);
	}

	@Override 
	public void onDataPacket(NetworkManager net, S35PacketUpdateTileEntity pkt) {
		super.onDataPacket(net, pkt);
		this.readFromNBT(pkt.func_148857_g());
		isRunning = pkt.func_148857_g().getBoolean("IsRunning");
	}

	@Override
	public void writeToNBT(NBTTagCompound nbt) {
		super.writeToNBT(nbt);

		NBTTagCompound laser = new NBTTagCompound();
		laserSat.writeToNBT(laser);

		nbt.setTag("laser", laser);

		NBTTagList list = new NBTTagList();


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

		laserSat.readFromNBT(nbt.getCompoundTag("laser"));
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
	private Object getAvalibleInv() {
		ForgeDirection front = RotatableBlock.getFront(this.getBlockMetadata());

		for(ForgeDirection f : VALID_INVENTORY_DIRECTIONS) {
			if(f == front)
				continue;

			TileEntity e = this.worldObj.getTileEntity(this.xCoord + f.offsetX, this.yCoord + f.offsetY, this.zCoord + f.offsetZ);


			if(InventoryCompat.canInjectItems(e))
				return (IInventory)e;
		}
		return null;
	}

	/**
	 * Gets the first inv with a slot able to hold 'itemStack'
	 * @param item item to fit into inventory
	 * @return inv with capablity of holding 'itemStack'
	 */
	private Object getAvalibleInv(ItemStack item) {
		if(item == null)
			return getAvalibleInv();

		ForgeDirection front = RotatableBlock.getFront(this.getBlockMetadata());

		for(ForgeDirection f : VALID_INVENTORY_DIRECTIONS) {
			if(f == front)
				continue;

			TileEntity e = this.worldObj.getTileEntity(this.xCoord + f.offsetX, this.yCoord + f.offsetY, this.zCoord + f.offsetZ);


			if(InventoryCompat.canInjectItems(e, item))
				return e;
		}
		return null;
	}

	private boolean canMachineSeeEarth() {
		//for(int i = yCoord - 1; i > 0; i--) {
		//	if(worldObj.isBlockNormalCubeDefault(xCoord, i, zCoord,true))
		//		return false;
		//}
		return true;
	}

	private boolean isAllowedToRun() {
		return !(glassPanel == null || batteries.getEnergyStored() == 0 || !(this.worldObj.provider instanceof WorldProviderSpace) || !zmaster587.advancedRocketry.dimension.DimensionManager.getInstance().canTravelTo(((WorldProviderSpace)this.worldObj.provider).getDimensionProperties(xCoord, zCoord).getParentPlanet()) ||
				Configuration.laserBlackListDims.contains(((WorldProviderSpace)this.worldObj.provider).getDimensionProperties(xCoord, zCoord).getParentPlanet()));
	}
	
	/**
	 * Checks to see if the situation for firing the laser exists... and changes the state accordingly
	 */
	public void checkCanRun() {
		//Laser requires lense, redstone power, not be jammed, and be in orbit and energy to function
		if(!isAllowedToRun() || !worldObj.isBlockIndirectlyGettingPowered(xCoord, yCoord, zCoord)) {
			if(laserSat.isAlive()) {
				laserSat.deactivateLaser();
			}

			setRunning(false);
		} else if(!laserSat.isAlive() && !finished && !laserSat.getJammed() && worldObj.isBlockIndirectlyGettingPowered(xCoord, yCoord, zCoord) && canMachineSeeEarth()) {

			//Laser will be on at this point
			int orbitDimId = ((WorldProviderSpace)this.worldObj.provider).getDimensionProperties(xCoord, zCoord).getParentPlanet();
			if(orbitDimId == SpaceObjectManager.WARPDIMID)
				return;
			WorldServer orbitWorld = DimensionManager.getWorld(orbitDimId);

			if(orbitWorld == null) {
				DimensionManager.initDimension(orbitDimId);
				orbitWorld = DimensionManager.getWorld(orbitDimId);
				if(orbitWorld == null)
					return;
			}


			if(ticket == null) {
				ticket = ForgeChunkManager.requestTicket(AdvancedRocketry.instance, this.worldObj, Type.NORMAL);
				if(ticket != null)
					ForgeChunkManager.forceChunk(ticket, new ChunkCoordIntPair(this.xCoord / 16 - (this.xCoord < 0 ? 1 : 0), this.zCoord / 16 - (this.zCoord < 0 ? 1 : 0)));
			}

			setRunning(laserSat.activateLaser(orbitWorld, laserX, laserZ));
		}

		if(!this.worldObj.isRemote)
			PacketHandler.sendToNearby(new PacketMachine(this, (byte)12), this.xCoord, this.yCoord, this.zCoord, 128, this.worldObj.provider.dimensionId);
	}

	public int getEnergyPercentScaled(int max) {
		return (int)(max * (batteries.getEnergyStored() / (float)batteries.getMaxEnergyStored()) );
	}

	public boolean hasEnergy() {
		return batteries.getEnergyStored() != 0;
	}

	//InventoryHandling start
	@Override
	public int getSizeInventory() {
		return inv.getSizeInventory();
	}

	@Override
	public ItemStack getStackInSlot(int i) {
		if(i == 0)
			return glassPanel;
		else {
			i--;
			return inv.getStackInSlot(i);
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

				//TileEntity e = this.worldObj.getTileEntity(this.xCoord + f.offsetX, this.yCoord + f.offsetY, this.zCoord + f.offsetZ);

				if(InventoryCompat.canInjectItems(inv, itemstack))
					InventoryCompat.injectItem(inv, itemstack);
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
		return new int[] {};
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
			return CompatibilityMgr.gregtechLoaded ? OreDictionary.getOreName(OreDictionary.getOreID(itemstack)).equals("lenseRuby") : AdvancedRocketryItems.itemLens == itemstack.getItem() ? true : false;

			return inv.isItemValidForSlot(i, itemstack);
	}
	//InventoryHandling end

	//Redstone Flux start

	/**
	 * @param simulate true to simulate.. false to drain the power
	 * @return returns whether enough power is stored for the next opertation
	 */
	public boolean hasPowerForOperation() {
		return POWER_PER_OPERATION <= batteries.getEnergyStored();
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
	public void onModuleUpdated(ModuleBase module) {

		if(module == locationX) {
			if(!((ModuleTextBox)module).getText().isEmpty() && !((ModuleTextBox)module).getText().contentEquals("-"))
				laserX = Integer.parseInt(((ModuleTextBox)module).getText());
			PacketHandler.sendToServer(new PacketMachine(this,(byte) 10));
		}
		else if(module == locationZ) {
			if(!((ModuleTextBox)module).getText().isEmpty() && !((ModuleTextBox)module).getText().contentEquals("-"))
				laserZ = Integer.parseInt(((ModuleTextBox)module).getText());
			PacketHandler.sendToServer(new PacketMachine(this,(byte) 11));
		}


	}

	@Override
	public List<ModuleBase> getModules(int id, EntityPlayer player) {
		List<ModuleBase> modules = new LinkedList<ModuleBase>();

		if(worldObj.isRemote) {
			modules.add(locationX = new ModuleNumericTextbox(this, 113, 31, 50, 10, 16));
			modules.add(locationZ = new ModuleNumericTextbox(this, 113, 41, 50, 10, 16));

			locationX.setText(String.valueOf(this.laserX));
			locationZ.setText(String.valueOf(this.laserZ));

			modules.add(updateText = new ModuleText(130, 20, this.getMode().toString(), 0x0b0b0b, true));
			modules.add(new ModuleText(103, 33, "X:",  0x0b0b0b));
			modules.add(new ModuleText(103, 43, "Z:",  0x0b0b0b));

			modules.add(new ModuleImage(8, 16, TextureResources.laserGuiBG));
		}

		modules.add(new ModuleButton(103, 20, 0, "", this,  zmaster587.libVulpes.inventory.TextureResources.buttonLeft, 5, 8));
		modules.add(new ModuleButton(157, 20, 1, "", this,  zmaster587.libVulpes.inventory.TextureResources.buttonRight, 5, 8));
		modules.add(new ModuleButton(103, 62, 2, "Reset", this,  zmaster587.libVulpes.inventory.TextureResources.buttonBuild, 34, 20));
		modules.add(new ModulePower(11, 25, getBatteries()));
		modules.add(new ModuleSlotArray(56, 54, this, 0, 1));




		return modules;
	}

	@Override
	public String getModularInventoryName() {
		return "tile.spaceLaser.name";
	}

	@Override
	public boolean canInteractWithContainer(EntityPlayer entity) {
		return true;
	}

	@Override
	public void onInventoryButtonPressed(int buttonId) {

		if(buttonId == 0){
			this.decrementMode();
			updateText.setText(this.getMode().toString());
		}
		else if(buttonId == 1) {
			this.incrementMode();
			updateText.setText(this.getMode().toString());
		}
		else if(buttonId == 2) {
			PacketHandler.sendToServer(new PacketMachine(this, (byte)14));
			return;
		}
		else 
			return;

		if(!this.isRunning())
			PacketHandler.sendToServer(new PacketMachine(this, (byte)13));
	}
}