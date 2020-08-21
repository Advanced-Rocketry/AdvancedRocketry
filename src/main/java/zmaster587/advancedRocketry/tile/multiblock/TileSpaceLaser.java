package zmaster587.advancedRocketry.tile.multiblock;

import io.netty.buffer.ByteBuf;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import zmaster587.advancedRocketry.AdvancedRocketry;
import zmaster587.advancedRocketry.api.AdvancedRocketryItems;
import zmaster587.advancedRocketry.api.AdvancedRocketryTileEntityType;
import zmaster587.advancedRocketry.dimension.DimensionManager;
import zmaster587.advancedRocketry.api.ARConfiguration;
import zmaster587.advancedRocketry.inventory.TextureResources;
import zmaster587.advancedRocketry.satellite.SatelliteLaser;
import zmaster587.advancedRocketry.satellite.SatelliteLaserNoDrill;
import zmaster587.advancedRocketry.stations.SpaceObjectManager;
import zmaster587.advancedRocketry.world.provider.WorldProviderSpace;
import zmaster587.libVulpes.LibVulpes;
import zmaster587.libVulpes.api.LibVulpesBlocks;
import zmaster587.libVulpes.block.RotatableBlock;
import zmaster587.libVulpes.compat.InventoryCompat;
import zmaster587.libVulpes.inventory.modules.*;
import zmaster587.libVulpes.network.PacketHandler;
import zmaster587.libVulpes.network.PacketMachine;
import zmaster587.libVulpes.tile.multiblock.TileMultiPowerConsumer;
import zmaster587.libVulpes.util.MultiInventory;
import zmaster587.libVulpes.util.ZUtils;

import java.util.LinkedList;
import java.util.List;

public class TileSpaceLaser extends TileMultiPowerConsumer implements ISidedInventory, IGuiCallback, IButtonInventory {

	private static final int INVSIZE = 9;
	ItemStack glassPanel;
	SatelliteLaserNoDrill laserSat;
	protected boolean isRunning, finished;
	protected IInventory adjInv;
	private int radius, xCenter, yCenter, numSteps;
	private Direction prevDir;
	public int laserX, laserZ, tickSinceLastOperation;
	private static final Direction[] VALID_INVENTORY_DIRECTIONS = { Direction.NORTH, Direction.EAST, Direction.SOUTH, Direction.WEST};
	private static final int POWER_PER_OPERATION =(int)( 10000* ARConfiguration.getCurrentConfig().spaceLaserPowerMult);
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
				{null, 'P', Blocks.GLASS, 'P', null},
				{'P', LibVulpesBlocks.blockAdvStructureBlock, Blocks.GLASS, LibVulpesBlocks.blockAdvStructureBlock, 'P'},
				{null, 'P', LibVulpesBlocks.blockAdvStructureBlock, 'P', null},
				{null, null, 'P', null, null}
			},
			{
				{null, null, LibVulpesBlocks.blockAdvStructureBlock, null, null},
				{null, LibVulpesBlocks.blockAdvStructureBlock, LibVulpesBlocks.blockAdvStructureBlock, LibVulpesBlocks.blockAdvStructureBlock, null},
				{LibVulpesBlocks.blockAdvStructureBlock, LibVulpesBlocks.blockAdvStructureBlock, Blocks.GLASS, LibVulpesBlocks.blockAdvStructureBlock, LibVulpesBlocks.blockAdvStructureBlock},
				{null, LibVulpesBlocks.blockAdvStructureBlock, LibVulpesBlocks.blockAdvStructureBlock, LibVulpesBlocks.blockAdvStructureBlock, null},
				{null, null, LibVulpesBlocks.blockAdvStructureBlock, null, null}
			},
			{
				{null, null, 'O', null, null},
				{null, LibVulpesBlocks.blockAdvStructureBlock, Blocks.GLASS, LibVulpesBlocks.blockAdvStructureBlock, null},
				{'O', Blocks.GLASS, Blocks.GLASS, Blocks.GLASS, 'O'},
				{null, LibVulpesBlocks.blockAdvStructureBlock, Blocks.GLASS, LibVulpesBlocks.blockAdvStructureBlock, null},
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
	
	public TileSpaceLaser() { 
		super(AdvancedRocketryTileEntityType.TILE_SPACE_LASER);
		glassPanel = ItemStack.EMPTY;
		//invBuffer = new ItemStack[INVSIZE];
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
			laserSat = new SatelliteLaser(inv);
		else
			laserSat = new SatelliteLaserNoDrill(inv);
		
		isRunning = false;
		finished = false;
		mode = MODE.SINGLE;
	}

	@Override
	public Object[][][] getStructure() {
		return structure;
	}
	
	//Required so we see the laser
	@OnlyIn(value=Dist.CLIENT)
	@Override
	public AxisAlignedBB getRenderBoundingBox() {
		return new AxisAlignedBB(this.pos.getX() -5, this.pos.getY() - 1000, this.pos.getZ() - 5, this.pos.getX() + 5, this.pos.getY() +50, this.pos.getZ() + 5);
	}
	
	
	@Override
	public String getMachineName() {
		return getModularInventoryName();
	}
	
	/*
	 * ID 0: client changed xcoord in interface
	 * ID 1: client changed ycoord in interface
	 * ID 2: sync whether the machine is running
	 * ID 3: sync Mode
	 * ID 4: jam reset
	 */
	@Override
	public void writeDataToNetwork(PacketBuffer out, byte id) {
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
	public void readDataFromNetwork(PacketBuffer in, byte id,
			CompoundNBT nbt) {
		super.readDataFromNetwork(in, id, nbt);
		if(id == 10)
			nbt.putInt("laserX", in.readInt());
		else if(id == 11)
			nbt.putInt("laserZ", in.readInt());
		else if(id == 12)
			nbt.putBoolean("isRunning", in.readBoolean());
		else if(id == 13)
			nbt.putInt("mode", in.readInt());

	}

	@Override
	public void useNetworkData(PlayerEntity player, Dist side, byte id,
			CompoundNBT nbt) {
		
		super.useNetworkData(player, side, id, nbt);
		if(id == 10) {
			this.laserX = nbt.getInt("laserX");
			finished = false;

			if(mode == MODE.SPIRAL)
				resetSpiral();
		}
		else if(id == 11) {
			this.laserZ = nbt.getInt("laserZ");
			finished = false;
			if(mode == MODE.SPIRAL)
				resetSpiral();
		}
		else if(id == 12)
			this.isRunning = nbt.getBoolean("isRunning");
		else if(id == 13 && !isRunning())
			this.mode = MODE.values()[nbt.getInt("mode")];
		else if(id == 14)
			this.attempUnjam();

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

	public void setMode(MODE m) {mode = m;};

	public void setFinished(boolean value) { finished = value; }

	public void setRunning(boolean value) {
		isRunning = value;
		markDirty();
		//worldObj.notifyBlockUpdate(pos, worldObj.getBlockState(pos),  worldObj.getBlockState(pos), 3);
	}
	
	@Override
	public void tick() {
		
		//Freaky jenky crap to make sure the multiblock loads on chunkload etc
		if(timeAlive == 0 && !world.isRemote) {
			if(isComplete())
				canRender = completeStructure = completeStructure(world.getBlockState(pos));
			timeAlive = 0x1;
			checkCanRun();
		}
		
		//TODO: drain energy
		if(!this.world.isRemote) {
			tickSinceLastOperation++;

			if(!isAllowedToRun()) {
				laserSat.deactivateLaser();
				this.setFinished(true);
				this.setRunning(false);
			}
			else
			if(hasPowerForOperation() && isReadyForOperation() && laserSat.isAlive() && !laserSat.getJammed()) {
				laserSat.performOperation();
				
				batteries.setEnergyStored(batteries.getUniversalEnergyStored() - POWER_PER_OPERATION);
				tickSinceLastOperation = 0;
			}
		}

		if(laserSat.isFinished()) {
			setRunning(false);
			laserSat.deactivateLaser();

			if(!laserSat.getJammed()) {
				if(mode == MODE.SINGLE) 
					finished = true;

				if(this.world.getStrongPower(getPos()) != 0) {
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
							if(prevDir == Direction.NORTH)
								prevDir = Direction.EAST;
							else if(prevDir == Direction.EAST){
								prevDir = Direction.SOUTH;
								radius++;
							}
							else if(prevDir == Direction.SOUTH)
								prevDir = Direction.WEST;
							else {
								prevDir = Direction.NORTH;
								radius++;
							}
						}

						this.laserX += 3*prevDir.getXOffset();
						this.laserZ += 3*prevDir.getZOffset();
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
		if(laserSat != null) {
			laserSat.deactivateLaser();
		}
		if(!world.isRemote)
		{
			ServerWorld svrWorld = (ServerWorld) world;
			svrWorld.forceChunk(new ChunkPos(getPos()).x, new ChunkPos(getPos()).z, false);
		}
	}

	@Override
	public void onChunkUnloaded() {
		if(laserSat != null) {
			laserSat.deactivateLaser();
		}
		isRunning = false;
	}

	@Override
	public SUpdateTileEntityPacket getUpdatePacket() {
		CompoundNBT nbt = new CompoundNBT();
		this.write(nbt);

		nbt.putBoolean("IsRunning", isRunning);

		return new SUpdateTileEntityPacket(getPos(), 0, nbt);
	}

	@Override 
	public void onDataPacket(NetworkManager net, SUpdateTileEntityPacket pkt) {
		this.func_230337_a_(getBlockState(), pkt.getNbtCompound());
		isRunning = pkt.getNbtCompound().getBoolean("IsRunning");
	}

	@Override
	public CompoundNBT write(CompoundNBT nbt) {
		super.write(nbt);

		CompoundNBT laser = new CompoundNBT();
		laserSat.writeToNBT(laser);

		nbt.put("laser", laser);
		
		if(glassPanel != null) {
			CompoundNBT tag = new CompoundNBT();
			glassPanel.write(tag);
			nbt.put("GlassPane", tag);
		}

		nbt.putInt("laserX", laserX);
		nbt.putInt("laserZ", laserZ);
		nbt.putByte("mode", (byte)mode.ordinal());

		if(mode == MODE.SPIRAL && prevDir != null) {
			nbt.putInt("CenterX", xCenter);
			nbt.putInt("CenterY", yCenter);
			nbt.putInt("radius", radius);
			nbt.putInt("numSteps", numSteps);
			nbt.putInt("prevDir", prevDir.ordinal());
		}
		return nbt;
	}

	@Override
	public void func_230337_a_(BlockState state, CompoundNBT nbt) {
		super.func_230337_a_(state, nbt);

		laserSat.readFromNBT(nbt.getCompound("laser"));
		if(nbt.contains("GlassPane")) {
			CompoundNBT tag = nbt.getCompound("GlassPane");
			glassPanel = ItemStack.read(tag);
		}

		laserX = nbt.getInt("laserX");
		laserZ = nbt.getInt("laserZ");
		mode = MODE.values()[nbt.getByte("mode")];

		if(mode == MODE.SPIRAL && nbt.contains("prevDir")){
			xCenter = nbt.getInt("CenterX");
			yCenter = nbt.getInt("CenterY");
			radius = nbt.getInt("radius");
			numSteps = nbt.getInt("numSteps");
			prevDir = Direction.values()[nbt.getInt("prevDir")];
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
		Direction front = RotatableBlock.getFront(world.getBlockState(getPos()));

		for(Direction f : VALID_INVENTORY_DIRECTIONS) {
			if(f == front)
				continue;

			TileEntity e = this.world.getTileEntity(getPos().offset(f));


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

		Direction front = RotatableBlock.getFront(world.getBlockState(pos));

		for(Direction f : VALID_INVENTORY_DIRECTIONS) {
			if(f == front)
				continue;

			TileEntity e = this.world.getTileEntity(getPos());


			if(InventoryCompat.canInjectItems(e, item))
				return e;
		}
		return null;
	}

	private boolean canMachineSeeEarth() {
		//for(int i = getPos().getY() - 1; i > 0; i--) {
		//	if(worldObj.isBlockNormalCube(new BlockPos(getPos().getX(), i, getPos().getZ()), true))
		//		return false;
		//}
		return true;
	}

	private boolean isAllowedToRun() {
		return !(glassPanel.isEmpty()|| batteries.getUniversalEnergyStored() == 0 || !zmaster587.advancedRocketry.dimension.DimensionManager.getInstance().canTravelTo(DimensionManager.getInstance().getDimensionProperties(world, getPos()).getParentPlanet()) ||
				ARConfiguration.getCurrentConfig().laserBlackListDims.contains(DimensionManager.getInstance().getDimensionProperties(world, getPos()).getParentPlanet()));
	}
	
	/**
	 * Checks to see if the situation for firing the laser exists... and changes the state accordingly
	 */
	public void checkCanRun() {
		//Laser requires lense, redstone power, not be jammed, and be in orbit and energy to function
		if(world.getRedstonePowerFromNeighbors(getPos()) == 0 || !isAllowedToRun()) {
			if(laserSat.isAlive()) {
				laserSat.deactivateLaser();
			}

			setRunning(false);
		} else if(!laserSat.isAlive() && !finished && !laserSat.getJammed() && world.getRedstonePowerFromNeighbors(getPos()) > 0 && canMachineSeeEarth()) {

			//Laser will be on at this point
			ResourceLocation orbitDimId = DimensionManager.getInstance().getDimensionProperties(world, getPos()).getParentPlanet();
			if(orbitDimId == SpaceObjectManager.WARPDIMID)
				return;
			ServerWorld orbitWorld = ZUtils.getWorld(orbitDimId);

			if(orbitWorld == null) {
				ZUtils.initDimension(orbitDimId);
				orbitWorld = ZUtils.getWorld(orbitDimId);
				if(orbitWorld == null)
					return;
			}


			if(!world.isRemote)
			{
				ServerWorld svrWorld = (ServerWorld) world;
				svrWorld.forceChunk(new ChunkPos(getPos()).x, new ChunkPos(getPos()).z, false);
			}

			setRunning(laserSat.activateLaser(orbitWorld, laserX, laserZ));
		}

		if(!this.world.isRemote)
			PacketHandler.sendToNearby(new PacketMachine(this, (byte)12), ZUtils.getDimensionIdentifier(world), pos, 128);
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
			glassPanel = ItemStack.EMPTY;
			return ret;
		}
		return ItemStack.EMPTY;
	}

	@Override
	public void setInventorySlotContents(int i, ItemStack itemstack) {

		//TODO: add gregcipies
		if(i == 0)
			glassPanel = itemstack;
		else {

			if(InventoryCompat.canInjectItems(inv, itemstack))
				InventoryCompat.injectItem(inv, itemstack);

			this.checkCanRun();
		}
	}


	@Override
	public int getInventoryStackLimit() {
		return 64;
	}

	@Override
	public boolean isUsableByPlayer(PlayerEntity entityplayer) {
		return pos.distanceSq(new BlockPos(entityplayer.getPositionVec())) <= 64;
	}

	@Override
	public boolean isEmpty() {
		return glassPanel.isEmpty();
	}
	
	@Override
	public void openInventory(PlayerEntity entity) {
		// TODO Perhaps make sure laser isn't running
	}

	@Override
	public void closeInventory(PlayerEntity entity) {
		// TODO Auto-generated method stub

	}

	@Override
	public int[] getSlotsForFace(Direction side) {
		return new int[] {};
	}

	
	@Override
	public boolean canInsertItem(int index, ItemStack itemStackIn,
			Direction direction) {
		return false;
	}
	
	@Override
	public boolean canExtractItem(int index, ItemStack stack,
			Direction direction) {
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
	 * @param simulate true to simulate.. false to drain the power
	 * @return returns whether enough power is stored for the next opertation
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
		return laserSat.getJammed();
	}

	public void setJammed(boolean b) {
		laserSat.setJammed(b);

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
	public List<ModuleBase> getModules(int id, PlayerEntity player) {
		List<ModuleBase> modules = new LinkedList<ModuleBase>();

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
		
		modules.add(new ModuleButton(103, 20, "", this,  zmaster587.libVulpes.inventory.TextureResources.buttonLeft, 5, 8).setAdditionalData(0));
		modules.add(new ModuleButton(157, 20, "", this,  zmaster587.libVulpes.inventory.TextureResources.buttonRight, 5, 8).setAdditionalData(1));
		modules.add(new ModuleButton(103, 62, LibVulpes.proxy.getLocalizedString("msg.spacelaser.reset"), this,  zmaster587.libVulpes.inventory.TextureResources.buttonBuild, 34, 20).setAdditionalData(2));
		modules.add(new ModulePower(11, 25, batteries));
		modules.add(new ModuleSlotArray(56, 54, this, 0, 1));
		
		


		return modules;
	}

	@Override
	public String getModularInventoryName() {
		return "tile.spaceLaser.name";
	}

	@Override
	public boolean canInteractWithContainer(PlayerEntity entity) {
		return true;
	}

	@Override
	public void onInventoryButtonPressed(ModuleButton button) {

		int buttonId = button.getAdditionalData() == null ? -1 : (int)button.getAdditionalData();
		
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
	public void clear() {
		
	}
}