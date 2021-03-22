package zmaster587.advancedRocketry.tile.multiblock.drill;

import io.netty.buffer.ByteBuf;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldProvider;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.common.ForgeChunkManager;
import net.minecraftforge.common.ForgeChunkManager.Ticket;
import net.minecraftforge.common.ForgeChunkManager.Type;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import zmaster587.advancedRocketry.AdvancedRocketry;
import zmaster587.advancedRocketry.api.AdvancedRocketryBlocks;
import zmaster587.advancedRocketry.api.AdvancedRocketryItems;
import zmaster587.advancedRocketry.api.ARConfiguration;
import zmaster587.advancedRocketry.dimension.DimensionProperties;
import zmaster587.advancedRocketry.inventory.TextureResources;
import zmaster587.advancedRocketry.stations.SpaceObjectManager;
import zmaster587.advancedRocketry.world.provider.WorldProviderSpace;
import zmaster587.libVulpes.LibVulpes;
import zmaster587.libVulpes.api.LibVulpesBlocks;
import zmaster587.libVulpes.compat.InventoryCompat;
import zmaster587.libVulpes.inventory.modules.*;
import zmaster587.libVulpes.network.PacketHandler;
import zmaster587.libVulpes.network.PacketMachine;
import zmaster587.libVulpes.tile.multiblock.TileMultiPowerConsumer;
import zmaster587.libVulpes.util.MultiInventory;
import zmaster587.libVulpes.util.ZUtils;


import java.util.LinkedList;
import java.util.List;


public class TileOrbitalLaserDrill extends TileMultiPowerConsumer implements ISidedInventory, IGuiCallback, IButtonInventory {

	ItemStack lens;
	private final AbstractDrill drill;
	protected boolean isRunning, finished, isJammed;
	private int radius, xCenter, yCenter, numSteps;
	private EnumFacing prevDir;
	public int laserX, laserZ, tickSinceLastOperation;
	private static final int POWER_PER_OPERATION =(int)( 10000 * ARConfiguration.getCurrentConfig().spaceLaserPowerMult);
	private ModuleTextBox locationX, locationZ;
	private ModuleText updateText;
	MultiInventory inv;
	
	Object[][][] structure = new Object[][][]{
			{
				{null, null, null, null, null, null, null, null, null, null, null},
				{null, null, null, null, null, null, null, null, null, null, null},
				{null, null, null, null, null, null, null, null, null, null, null},
				{null, LibVulpesBlocks.blockAdvStructureBlock, null, null, null, null, null, null, null, null, null},
				{LibVulpesBlocks.blockAdvStructureBlock, LibVulpesBlocks.blockAdvStructureBlock, LibVulpesBlocks.blockAdvStructureBlock, null, null, null, null, null, null, null, null},
				{null, LibVulpesBlocks.blockAdvStructureBlock, null, null, null, null, null, null, null, null, null},
				{null, null, null, null, null, null, null, null, null, null, null},
				{null, null, null, null, null, null, null, null, null, null, null},
				{null, null, null, null, null, null, null, null, null, null, null}
			},
			{
				{null, null, null, null, null, null,  LibVulpesBlocks.blockAdvStructureBlock, AdvancedRocketryBlocks.blockVacuumLaser, AdvancedRocketryBlocks.blockVacuumLaser, AdvancedRocketryBlocks.blockVacuumLaser, null},
				{null, null, null, null,  LibVulpesBlocks.blockAdvStructureBlock, AdvancedRocketryBlocks.blockLens, AdvancedRocketryBlocks.blockLens, AdvancedRocketryBlocks.blockVacuumLaser, AdvancedRocketryBlocks.blockVacuumLaser, AdvancedRocketryBlocks.blockVacuumLaser, 'P'},
				{null, null, null, null,  LibVulpesBlocks.blockAdvStructureBlock,  LibVulpesBlocks.blockAdvStructureBlock,  LibVulpesBlocks.blockAdvStructureBlock, AdvancedRocketryBlocks.blockVacuumLaser, AdvancedRocketryBlocks.blockVacuumLaser, AdvancedRocketryBlocks.blockVacuumLaser, 'P'},
				{LibVulpesBlocks.blockStructureBlock, LibVulpesBlocks.blockAdvStructureBlock, LibVulpesBlocks.blockStructureBlock, null, AdvancedRocketryBlocks.blockLens, null,  LibVulpesBlocks.blockAdvStructureBlock, AdvancedRocketryBlocks.blockVacuumLaser, AdvancedRocketryBlocks.blockVacuumLaser, AdvancedRocketryBlocks.blockVacuumLaser, null},
				{LibVulpesBlocks.blockAdvStructureBlock, LibVulpesBlocks.blockAdvStructureBlock, LibVulpesBlocks.blockAdvStructureBlock, AdvancedRocketryBlocks.blockLens,  LibVulpesBlocks.blockAdvStructureBlock, null, null, null, null, null, null},
				{LibVulpesBlocks.blockStructureBlock, LibVulpesBlocks.blockAdvStructureBlock, LibVulpesBlocks.blockStructureBlock, null, AdvancedRocketryBlocks.blockLens, null,  LibVulpesBlocks.blockAdvStructureBlock, AdvancedRocketryBlocks.blockVacuumLaser, AdvancedRocketryBlocks.blockVacuumLaser, AdvancedRocketryBlocks.blockVacuumLaser, null},
				{null, null, null, null,  LibVulpesBlocks.blockAdvStructureBlock,  LibVulpesBlocks.blockAdvStructureBlock,  LibVulpesBlocks.blockAdvStructureBlock, AdvancedRocketryBlocks.blockVacuumLaser, AdvancedRocketryBlocks.blockVacuumLaser, AdvancedRocketryBlocks.blockVacuumLaser, 'P'},
				{null, null, null, null,  LibVulpesBlocks.blockAdvStructureBlock, AdvancedRocketryBlocks.blockLens, AdvancedRocketryBlocks.blockLens, AdvancedRocketryBlocks.blockVacuumLaser, AdvancedRocketryBlocks.blockVacuumLaser, AdvancedRocketryBlocks.blockVacuumLaser, 'P'},
				{null, null, null, null, null, null,  LibVulpesBlocks.blockAdvStructureBlock, AdvancedRocketryBlocks.blockVacuumLaser, AdvancedRocketryBlocks.blockVacuumLaser, AdvancedRocketryBlocks.blockVacuumLaser, null}
			},
			{
			    {null, null, null, null, null, null,  LibVulpesBlocks.blockAdvStructureBlock, AdvancedRocketryBlocks.blockVacuumLaser, AdvancedRocketryBlocks.blockVacuumLaser, AdvancedRocketryBlocks.blockVacuumLaser, null},
		    	{null, null, null, null,  LibVulpesBlocks.blockAdvStructureBlock, AdvancedRocketryBlocks.blockLens, AdvancedRocketryBlocks.blockLens, AdvancedRocketryBlocks.blockVacuumLaser, AdvancedRocketryBlocks.blockVacuumLaser, AdvancedRocketryBlocks.blockVacuumLaser, 'P'},
		    	{'O', 'c', 'O', null,  LibVulpesBlocks.blockAdvStructureBlock,  LibVulpesBlocks.blockAdvStructureBlock,  LibVulpesBlocks.blockAdvStructureBlock, AdvancedRocketryBlocks.blockVacuumLaser, AdvancedRocketryBlocks.blockVacuumLaser, AdvancedRocketryBlocks.blockVacuumLaser, 'P'},
		    	{LibVulpesBlocks.blockStructureBlock, LibVulpesBlocks.blockStructureBlock, LibVulpesBlocks.blockStructureBlock, null, AdvancedRocketryBlocks.blockLens, null,  LibVulpesBlocks.blockAdvStructureBlock, AdvancedRocketryBlocks.blockVacuumLaser, AdvancedRocketryBlocks.blockVacuumLaser, AdvancedRocketryBlocks.blockVacuumLaser, null},
		    	{LibVulpesBlocks.blockStructureBlock, LibVulpesBlocks.blockStructureBlock, LibVulpesBlocks.blockStructureBlock, AdvancedRocketryBlocks.blockLens,  LibVulpesBlocks.blockAdvStructureBlock, null, null, null, null, null, null},
		    	{LibVulpesBlocks.blockStructureBlock, LibVulpesBlocks.blockStructureBlock, LibVulpesBlocks.blockStructureBlock, null, AdvancedRocketryBlocks.blockLens, null,  LibVulpesBlocks.blockAdvStructureBlock, AdvancedRocketryBlocks.blockVacuumLaser, AdvancedRocketryBlocks.blockVacuumLaser, AdvancedRocketryBlocks.blockVacuumLaser, null},
	    		{null, null, null, null,  LibVulpesBlocks.blockAdvStructureBlock,  LibVulpesBlocks.blockAdvStructureBlock,  LibVulpesBlocks.blockAdvStructureBlock, AdvancedRocketryBlocks.blockVacuumLaser, AdvancedRocketryBlocks.blockVacuumLaser, AdvancedRocketryBlocks.blockVacuumLaser, 'P'},
		    	{null, null, null, null,  LibVulpesBlocks.blockAdvStructureBlock, AdvancedRocketryBlocks.blockLens, AdvancedRocketryBlocks.blockLens, AdvancedRocketryBlocks.blockVacuumLaser, AdvancedRocketryBlocks.blockVacuumLaser, AdvancedRocketryBlocks.blockVacuumLaser, 'P'},
		    	{null, null, null, null, null, null,  LibVulpesBlocks.blockAdvStructureBlock, AdvancedRocketryBlocks.blockVacuumLaser, AdvancedRocketryBlocks.blockVacuumLaser, AdvancedRocketryBlocks.blockVacuumLaser, null}
	        },
	};
	
	public enum MODE{
		SINGLE,
		LINE_X,
		LINE_Z,
		SPIRAL
	}

	private MODE mode;

	Ticket ticket;

	public TileOrbitalLaserDrill() {
		super();
		lens = ItemStack.EMPTY;

		radius = 0;
		xCenter = 0;
		yCenter = 0;
		numSteps = 0;
		prevDir = null;

		tickSinceLastOperation = 0;
		laserX = 0;
		laserZ = 0;
		inv= new MultiInventory(this.itemOutPorts);
		
		if(ARConfiguration.getCurrentConfig().laserDrillPlanet)
			this.drill = new MiningDrill();
		else
			this.drill = new VoidDrill();
		
		isRunning = false;
		finished = false;
		isJammed = false;
		mode = MODE.SINGLE;
	}

	@Override
	public Object[][][] getStructure() {
		return structure;
	}
	
	//Required so we see the laser
	@SideOnly(Side.CLIENT)
	@Override
	public AxisAlignedBB getRenderBoundingBox() {
		return new AxisAlignedBB(this.pos.getX() -5, this.pos.getY() - 1000, this.pos.getZ() - 5, this.pos.getX() + 5, this.pos.getY() +50, this.pos.getZ() + 5);
	}

	@Override
	public boolean shouldHideBlock(World world, BlockPos pos, IBlockState tile) {
		return true;
	}
	
	@Override
	public String getMachineName() {
		return this.getModularInventoryName();
	}
	
	/*
	 * ID 0: client changed xcoord in interface
	 * ID 1: client changed zcoord in interface
	 * ID 2: sync whether the machine is running
	 * ID 3: sync Mode
	 * ID 4: jam reset
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
			this.attemptUnjam();

		markDirty();
	}

	private void resetSpiral() {
		radius = 0;
		prevDir = null;
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

	public void setMode(MODE m) {mode = m;}

	public void setFinished(boolean value) { finished = value; }

	public void setRunning(boolean value) {
		isRunning = value;
		markDirty();
		//worldObj.notifyBlockUpdate(pos, worldObj.getBlockState(pos),  worldObj.getBlockState(pos), 3);
	}
	
	@Override
	public void update() {
		
		//Freaky jenky crap to make sure the multiblock loads on chunkload etc
		if(timeAlive == 0 && !world.isRemote) {
			if(isComplete())
				canRender = completeStructure = completeStructure(world.getBlockState(pos));
			timeAlive = 0x1;
			checkCanRun();
		}
		
		if(!this.world.isRemote) {
			tickSinceLastOperation++;

			if(unableToRun()) {
				this.drill.deactivate();
				this.setFinished(true);
				this.setRunning(false);
			}
			else if(this.hasPowerForOperation() && this.isReadyForOperation() && !this.isJammed) {

				if(this.drill.needsRestart()) {
					this.setRunning(false);
					return;
				}

				ItemStack[] stacks = this.drill.performOperation();
				ZUtils.mergeInventory(stacks,this.inv);

				if(!ZUtils.isInvEmpty(stacks)) {
					//TODO: drop extra items
					this.drill.deactivate();
					this.isJammed = true;
				}
				
				this.batteries.setEnergyStored(this.batteries.getUniversalEnergyStored() - POWER_PER_OPERATION);
				this.tickSinceLastOperation = 0;
			}
		}

		if(this.drill.isFinished()) {
			setRunning(false);
			this.drill.deactivate();

			if(!this.isJammed) {
				if(this.mode == MODE.SINGLE)
					this.finished = true;

				if(this.world.getStrongPower(getPos()) != 0) {
					if(this.mode == MODE.LINE_X) {
						this.laserX += 3;
					}
					else if(this.mode == MODE.LINE_Z) {
						this.laserZ += 3;
					}
					else if(this.mode == MODE.SPIRAL) {
						this.numSteps++;
						if(this.radius < this.numSteps) {
							this.numSteps = 0;
							if(prevDir == EnumFacing.NORTH)
								prevDir = EnumFacing.EAST;
							else if(prevDir == EnumFacing.EAST){
								prevDir = EnumFacing.SOUTH;
								radius++;
							}
							else if(prevDir == EnumFacing.SOUTH)
								prevDir = EnumFacing.WEST;
							else {
								prevDir = EnumFacing.NORTH;
								radius++;
							}
						}

						this.laserX += 3*prevDir.getFrontOffsetX();
						this.laserZ += 3*prevDir.getFrontOffsetZ();
					}
				}
				//TODO: unneeded?
				checkCanRun();
			}
		}
	}

	public boolean isReadyForOperation() {
		if(batteries.getUniversalEnergyStored() == 0)
			return false;

		return tickSinceLastOperation > (3*this.batteries.getMaxEnergyStored()/(float)this.batteries.getUniversalEnergyStored());
	}

	public void onDestroy() {
		if(this.drill != null) {
			this.drill.deactivate();
		}
		ForgeChunkManager.releaseTicket(ticket);
	}

	@Override
	public void onChunkUnload() {
		if(this.drill != null) {
			this.drill.deactivate();
		}
		isRunning = false;
	}

	@Override
	public SPacketUpdateTileEntity getUpdatePacket() {
		NBTTagCompound nbt = new NBTTagCompound();
		this.writeToNBT(nbt);

		nbt.setBoolean("IsRunning", isRunning);

		return new SPacketUpdateTileEntity(getPos(), 0, nbt);
	}

	@Override 
	public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity pkt) {
		this.readFromNBT(pkt.getNbtCompound());
		isRunning = pkt.getNbtCompound().getBoolean("IsRunning");
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound nbt) {
		super.writeToNBT(nbt);
		
		if(lens != null) {
			NBTTagCompound tag = new NBTTagCompound();
			lens.writeToNBT(tag);
			nbt.setTag("GlassPane", tag);
		}

		nbt.setInteger("laserX", laserX);
		nbt.setInteger("laserZ", laserZ);
		nbt.setByte("mode", (byte)mode.ordinal());
		nbt.setBoolean("jammed",this.isJammed);

		if(mode == MODE.SPIRAL && prevDir != null) {
			nbt.setInteger("CenterX", xCenter);
			nbt.setInteger("CenterY", yCenter);
			nbt.setInteger("radius", radius);
			nbt.setInteger("numSteps", numSteps);
			nbt.setInteger("prevDir", prevDir.ordinal());
		}
		return nbt;
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt) {
		super.readFromNBT(nbt);

		if(nbt.hasKey("GlassPane")) {
			NBTTagCompound tag = nbt.getCompoundTag("GlassPane");
			lens = new ItemStack(tag);
		}

		laserX = nbt.getInteger("laserX");
		laserZ = nbt.getInteger("laserZ");
		mode = MODE.values()[nbt.getByte("mode")];
		this.isJammed = nbt.getBoolean("jammed");

		if(mode == MODE.SPIRAL && nbt.hasKey("prevDir")){
			xCenter = nbt.getInteger("CenterX");
			yCenter = nbt.getInteger("CenterY");
			radius = nbt.getInteger("radius");
			numSteps = nbt.getInteger("numSteps");
			prevDir = EnumFacing.values()[nbt.getInteger("prevDir")];
		}
	}

	/**
	 * Take items from internal inventory
	 */
	public void attemptUnjam() {
		if(!this.isJammed)
			return;

		if(this.hatchesAreEmpty()) {
			this.isJammed = false;
		}
		finished = false;

		checkCanRun();
	}

	private boolean hatchesAreEmpty() {
		for(int i = 1; i < this.inv.getSizeInventory(); ++i) {
			if (!this.inv.getStackInSlot(i).isEmpty()) {
				return false;
			}
		}
		return true;
	}

	private boolean canMachineSeeEarth() {
		return true;
	}

	private boolean unableToRun() {
		return lens.isEmpty() || !canMachineSeeEarth() || batteries.getUniversalEnergyStored() == 0 || !(this.world.provider instanceof WorldProviderSpace) || !zmaster587.advancedRocketry.dimension.DimensionManager.getInstance().canTravelTo(((WorldProviderSpace) this.world.provider).getDimensionProperties(getPos()).getParentPlanet()) ||
					   ARConfiguration.getCurrentConfig().laserBlackListDims.contains(((WorldProviderSpace) this.world.provider).getDimensionProperties(getPos()).getParentPlanet());
	}
	
	/**
	 * Checks to see if the situation for firing the laser exists... and changes the state accordingly
	 */
	public void checkCanRun() {
		//Laser requires lense, redstone power, not be jammed, and be in orbit and energy to function
		if(world.isBlockIndirectlyGettingPowered(getPos()) == 0 || unableToRun()) {
			drill.deactivate();

			setRunning(false);
		} else if(!this.finished && !this.isJammed && world.isBlockIndirectlyGettingPowered(getPos()) > 0) {

			//Laser will be on at this point
			int orbitDimId = SpaceObjectManager.getSpaceManager().getSpaceStationFromBlockCoords(this.pos).getOrbitingPlanetId();
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
				ticket = ForgeChunkManager.requestTicket(AdvancedRocketry.instance, this.world, Type.NORMAL);
				if(ticket != null)
					ForgeChunkManager.forceChunk(ticket, new ChunkPos(getPos().getX() / 16 - (getPos().getX() < 0 ? 1 : 0), getPos().getZ() / 16 - (getPos().getZ() < 0 ? 1 : 0)));
			}

			setRunning(drill.activate(orbitWorld, laserX, laserZ));
		}

		if(!this.world.isRemote)
			PacketHandler.sendToNearby(new PacketMachine(this, (byte)12), 128, pos, this.world.provider.getDimension());
	}

	public int getEnergyPercentScaled(int max) {
		return (int)(max * (batteries.getUniversalEnergyStored() / (float)batteries.getMaxEnergyStored()) );
	}

	public boolean hasEnergy() {
		return batteries.getUniversalEnergyStored() != 0;
	}

	//InventoryHandling start
	@Override
	public int getSizeInventory() {
		return inv.getSizeInventory();
	}

	@Override
	public ItemStack getStackInSlot(int i) {
		if(i == 0)
			return lens;
		else {
			i--;
			return inv.getStackInSlot(i);
		}
	}

	@Override
	public ItemStack decrStackSize(int i, int j) {
		ItemStack ret;

		if(i == 0) {
			ret = lens.copy();
			lens = ItemStack.EMPTY;
			return ret;
		}
		return ItemStack.EMPTY;
	}

	@Override
	public void setInventorySlotContents(int i, ItemStack itemstack) {

		//TODO: add gregcipies
		if(i == 0)
			lens = itemstack;
		else {

			if(InventoryCompat.canInjectItems(inv, itemstack))
				InventoryCompat.injectItem(inv, itemstack);

			this.checkCanRun();
		}
	}

	@Override
	public String getName() {
		return LibVulpes.proxy.getLocalizedString("tile.spaceLaser.name");
	}


	@Override
	public int getInventoryStackLimit() {
		return 64;
	}

	@Override
	public boolean isUsableByPlayer(EntityPlayer entityplayer) {
		return entityplayer.getDistanceSq(pos) <= 64;
	}

	@Override
	public boolean isEmpty() {
		return lens.isEmpty();
	}
	
	@Override
	public void openInventory(EntityPlayer entity) {
		// TODO Perhaps make sure laser isn't running
	}

	@Override
	public void closeInventory(EntityPlayer entity) {
		// TODO Auto-generated method stub

	}

	@Override
	public int[] getSlotsForFace(EnumFacing side) {
		return new int[] {};
	}

	
	@Override
	public boolean canInsertItem(int index, ItemStack itemStackIn, EnumFacing direction) {
		return false;
	}
	
	@Override
	public boolean canExtractItem(int index, ItemStack stack, EnumFacing direction) {
		return false;
	}

	@Override
	public boolean isItemValidForSlot(int i, ItemStack itemstack) {
		if(i == 0)
			return AdvancedRocketryItems.itemLens == itemstack.getItem();

		return inv.isItemValidForSlot(i, itemstack);
	}
	//InventoryHandling end

	//Redstone Flux start

	/**
	 * @return returns whether enough power is stored for the next operation
	 */
	public boolean hasPowerForOperation() {
		return POWER_PER_OPERATION <= batteries.getUniversalEnergyStored();
	}

	/**
	 * DO NOT USE UNLESS YOU HAVE NO OTHER OPTION!!!
	 * @param amt amount to set energy to
	 */
	public void setEnergy(int amt) {
		batteries.setEnergyStored(amt);
	}
	
	//Redstone Flux end

	public boolean isJammed() {
		return this.isJammed;
	}

	public void setJammed(boolean b) {
		this.isJammed = b;
	}

	@Override
	public boolean hasCustomName() {
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
		List<ModuleBase> modules = new LinkedList<>();

		if(world.isRemote) {
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
		modules.add(new ModuleButton(103, 62, 2, LibVulpes.proxy.getLocalizedString("msg.spacelaser.reset"), this,  zmaster587.libVulpes.inventory.TextureResources.buttonBuild, 34, 20));
		modules.add(new ModulePower(11, 25, batteries));
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

	@Override
	public ItemStack removeStackFromSlot(int index) {
		return ItemStack.EMPTY;
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