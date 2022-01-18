package zmaster587.advancedRocketry.tile.multiblock.orbitallaserdrill;

import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import zmaster587.advancedRocketry.api.AdvancedRocketryItems;
import zmaster587.advancedRocketry.api.AdvancedRocketryTileEntityType;
import zmaster587.advancedRocketry.dimension.DimensionManager;
import zmaster587.advancedRocketry.api.ARConfiguration;
import zmaster587.advancedRocketry.api.AdvancedRocketryBlocks;
import zmaster587.advancedRocketry.inventory.TextureResources;
import zmaster587.advancedRocketry.stations.SpaceObjectManager;
import zmaster587.libVulpes.LibVulpes;
import zmaster587.libVulpes.api.LibVulpesBlocks;
import zmaster587.libVulpes.inventory.modules.*;
import zmaster587.libVulpes.network.PacketHandler;
import zmaster587.libVulpes.network.PacketMachine;
import zmaster587.libVulpes.tile.multiblock.TileMultiblockMachine;
import zmaster587.libVulpes.util.MultiInventory;
import zmaster587.libVulpes.util.ZUtils;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.LinkedList;
import java.util.List;


public class TileOrbitalLaserDrill extends TileMultiblockMachine implements ISidedInventory, IGuiCallback, IButtonInventory {

	private ItemStack lens;
	private final AbstractDrill drill;
	protected boolean isRunning, finished, isJammed;
	private int radius, xCenter, yCenter, numSteps;
	private Direction prevDir;
	public int laserX, laserZ, tickSinceLastOperation;
	private static final int POWER_PER_OPERATION =(int)( 20000* ARConfiguration.getCurrentConfig().spaceLaserPowerMult.get());
	private ModuleTextBox locationX, locationZ;
	private ModuleText updateText;
	private MultiInventory inv;
	
	Object[][][] structure = new Object[][][]{
			{
				{null, null, null, null, null, null, null, null, null, null, null},
				{null, null, null, null, null, null, null, null, null, null, null},
				{null, null, null, null, null, null, null, null, null, null, null},
				{null, LibVulpesBlocks.blockAdvancedMachineStructure, null, null, null, null, null, null, null, null, null},
				{LibVulpesBlocks.blockAdvancedMachineStructure, LibVulpesBlocks.blockAdvancedMachineStructure, LibVulpesBlocks.blockAdvancedMachineStructure, null, null, null, null, null, null, null, null},
				{null, LibVulpesBlocks.blockAdvancedMachineStructure, null, null, null, null, null, null, null, null, null},
				{null, null, null, null, null, null, null, null, null, null, null},
				{null, null, null, null, null, null, null, null, null, null, null},
				{null, null, null, null, null, null, null, null, null, null, null}
			},
			{
				{null, null, null, null, null, null,  LibVulpesBlocks.blockAdvancedMachineStructure, AdvancedRocketryBlocks.blockLaser, AdvancedRocketryBlocks.blockLaser, AdvancedRocketryBlocks.blockLaser, null},
				{null, null, null, null,  LibVulpesBlocks.blockAdvancedMachineStructure, AdvancedRocketryBlocks.blockLens, AdvancedRocketryBlocks.blockLens, AdvancedRocketryBlocks.blockLaser, AdvancedRocketryBlocks.blockLaser, AdvancedRocketryBlocks.blockLaser, 'P'},
				{null, null, null, null,  LibVulpesBlocks.blockAdvancedMachineStructure,  LibVulpesBlocks.blockAdvancedMachineStructure,  LibVulpesBlocks.blockAdvancedMachineStructure, AdvancedRocketryBlocks.blockLaser, AdvancedRocketryBlocks.blockLaser, AdvancedRocketryBlocks.blockLaser, 'P'},
				{LibVulpesBlocks.blockMachineStructure, LibVulpesBlocks.blockAdvancedMachineStructure, LibVulpesBlocks.blockMachineStructure, null, AdvancedRocketryBlocks.blockLens, null,  LibVulpesBlocks.blockAdvancedMachineStructure, AdvancedRocketryBlocks.blockLaser, AdvancedRocketryBlocks.blockLaser, AdvancedRocketryBlocks.blockLaser, null},
				{LibVulpesBlocks.blockAdvancedMachineStructure, LibVulpesBlocks.blockAdvancedMachineStructure, LibVulpesBlocks.blockAdvancedMachineStructure, AdvancedRocketryBlocks.blockLens,  LibVulpesBlocks.blockAdvancedMachineStructure, null, null, null, null, null, null},
				{LibVulpesBlocks.blockMachineStructure, LibVulpesBlocks.blockAdvancedMachineStructure, LibVulpesBlocks.blockMachineStructure, null, AdvancedRocketryBlocks.blockLens, null,  LibVulpesBlocks.blockAdvancedMachineStructure, AdvancedRocketryBlocks.blockLaser, AdvancedRocketryBlocks.blockLaser, AdvancedRocketryBlocks.blockLaser, null},
				{null, null, null, null,  LibVulpesBlocks.blockAdvancedMachineStructure,  LibVulpesBlocks.blockAdvancedMachineStructure,  LibVulpesBlocks.blockAdvancedMachineStructure, AdvancedRocketryBlocks.blockLaser, AdvancedRocketryBlocks.blockLaser, AdvancedRocketryBlocks.blockLaser, 'P'},
				{null, null, null, null,  LibVulpesBlocks.blockAdvancedMachineStructure, AdvancedRocketryBlocks.blockLens, AdvancedRocketryBlocks.blockLens, AdvancedRocketryBlocks.blockLaser, AdvancedRocketryBlocks.blockLaser, AdvancedRocketryBlocks.blockLaser, 'P'},
				{null, null, null, null, null, null,  LibVulpesBlocks.blockAdvancedMachineStructure, AdvancedRocketryBlocks.blockLaser, AdvancedRocketryBlocks.blockLaser, AdvancedRocketryBlocks.blockLaser, null}
			},
			{
			    {null, null, null, null, null, null,  LibVulpesBlocks.blockAdvancedMachineStructure, AdvancedRocketryBlocks.blockLaser, AdvancedRocketryBlocks.blockLaser, AdvancedRocketryBlocks.blockLaser, null},
		    	{null, null, null, null,  LibVulpesBlocks.blockAdvancedMachineStructure, AdvancedRocketryBlocks.blockLens, AdvancedRocketryBlocks.blockLens, AdvancedRocketryBlocks.blockLaser, AdvancedRocketryBlocks.blockLaser, AdvancedRocketryBlocks.blockLaser, 'P'},
		    	{'O', 'c', 'O', null,  LibVulpesBlocks.blockAdvancedMachineStructure,  LibVulpesBlocks.blockAdvancedMachineStructure,  LibVulpesBlocks.blockAdvancedMachineStructure, AdvancedRocketryBlocks.blockLaser, AdvancedRocketryBlocks.blockLaser, AdvancedRocketryBlocks.blockLaser, 'P'},
		    	{LibVulpesBlocks.blockMachineStructure, LibVulpesBlocks.blockMachineStructure, LibVulpesBlocks.blockMachineStructure, null, AdvancedRocketryBlocks.blockLens, null,  LibVulpesBlocks.blockAdvancedMachineStructure, AdvancedRocketryBlocks.blockLaser, AdvancedRocketryBlocks.blockLaser, AdvancedRocketryBlocks.blockLaser, null},
		    	{LibVulpesBlocks.blockMachineStructure, LibVulpesBlocks.blockMachineStructure, LibVulpesBlocks.blockMachineStructure, AdvancedRocketryBlocks.blockLens,  LibVulpesBlocks.blockAdvancedMachineStructure, null, null, null, null, null, null},
		    	{LibVulpesBlocks.blockMachineStructure, LibVulpesBlocks.blockMachineStructure, LibVulpesBlocks.blockMachineStructure, null, AdvancedRocketryBlocks.blockLens, null,  LibVulpesBlocks.blockAdvancedMachineStructure, AdvancedRocketryBlocks.blockLaser, AdvancedRocketryBlocks.blockLaser, AdvancedRocketryBlocks.blockLaser, null},
	    		{null, null, null, null,  LibVulpesBlocks.blockAdvancedMachineStructure,  LibVulpesBlocks.blockAdvancedMachineStructure,  LibVulpesBlocks.blockAdvancedMachineStructure, AdvancedRocketryBlocks.blockLaser, AdvancedRocketryBlocks.blockLaser, AdvancedRocketryBlocks.blockLaser, 'P'},
		    	{null, null, null, null,  LibVulpesBlocks.blockAdvancedMachineStructure, AdvancedRocketryBlocks.blockLens, AdvancedRocketryBlocks.blockLens, AdvancedRocketryBlocks.blockLaser, AdvancedRocketryBlocks.blockLaser, AdvancedRocketryBlocks.blockLaser, 'P'},
		    	{null, null, null, null, null, null,  LibVulpesBlocks.blockAdvancedMachineStructure, AdvancedRocketryBlocks.blockLaser, AdvancedRocketryBlocks.blockLaser, AdvancedRocketryBlocks.blockLaser, null}
	        },
	};
	
	public enum MODE{
		SINGLE,
		LINE_X,
		LINE_Z,
		SPIRAL
	}

	private MODE mode;
	
	public TileOrbitalLaserDrill() { 
		super(AdvancedRocketryTileEntityType.TILE_ORBITAL_LASER_DRILL);
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
		
		if(ARConfiguration.getCurrentConfig().laserDrillPlanet.get())
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
	@OnlyIn(value=Dist.CLIENT)
	@Override
	@Nonnull
	public AxisAlignedBB getRenderBoundingBox() {
		return new AxisAlignedBB(this.pos.getX() -5, this.pos.getY() - 1000, this.pos.getZ() - 5, this.pos.getX() + 5, this.pos.getY() +50, this.pos.getZ() + 5);
	}

	@Override
	public boolean shouldHideBlock(World world, BlockPos pos, BlockState tile) {
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
	public void tick() {
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
		if(!world.isRemote)
		{
			ServerWorld svrWorld = (ServerWorld) world;
			svrWorld.forceChunk(new ChunkPos(getPos()).x, new ChunkPos(getPos()).z, false);
		}
	}

	@Override
	public void onChunkUnloaded() {
		if(this.drill != null) {
			this.drill.deactivate();
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
		this.read(getBlockState(), pkt.getNbtCompound());
		isRunning = pkt.getNbtCompound().getBoolean("IsRunning");
	}

	@Override
	public CompoundNBT write(CompoundNBT nbt) {
		super.write(nbt);
		
		if(lens != null) {
			CompoundNBT tag = new CompoundNBT();
			lens.write(tag);
			nbt.put("GlassPane", tag);
		}

		nbt.putInt("laserX", laserX);
		nbt.putInt("laserZ", laserZ);
		nbt.putByte("mode", (byte)mode.ordinal());
		nbt.putBoolean("jammed",this.isJammed);

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
	public void read(BlockState state, CompoundNBT nbt) {
		super.read(state, nbt);

		if(nbt.contains("GlassPane")) {
			CompoundNBT tag = nbt.getCompound("GlassPane");
			lens = ItemStack.read(tag);
		}

		laserX = nbt.getInt("laserX");
		laserZ = nbt.getInt("laserZ");
		mode = MODE.values()[nbt.getByte("mode")];
		this.isJammed = nbt.getBoolean("jammed");

		if(mode == MODE.SPIRAL && nbt.contains("prevDir")){
			xCenter = nbt.getInt("CenterX");
			yCenter = nbt.getInt("CenterY");
			radius = nbt.getInt("radius");
			numSteps = nbt.getInt("numSteps");
			prevDir = Direction.values()[nbt.getInt("prevDir")];
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
		return lens.isEmpty() || !canMachineSeeEarth() 
				|| batteries.getUniversalEnergyStored() == 0 
				|| ZUtils.getDimensionIdentifier(world).equals(DimensionManager.spaceId)
				|| !DimensionManager.getInstance().canTravelTo(DimensionManager.getInstance().getDimensionProperties(world, getPos()).getParentPlanet()) 
				|| ARConfiguration.getCurrentConfig().laserBlackListDims.get().contains(DimensionManager.getInstance().getDimensionProperties(world, getPos()).getParentPlanet().toString());
		}
	
	/**
	 * Checks to see if the situation for firing the laser exists... and changes the state accordingly
	 */
	public void checkCanRun() {
		//Laser requires lens, redstone power, not be jammed, and be in orbit and energy to function

		if(world.getRedstonePowerFromNeighbors(getPos()) == 0 || unableToRun()) {
			drill.deactivate();

			setRunning(false);
		} else if(!this.finished && !this.isJammed && world.isBlockPowered(getPos())) {

			//Laser will be on at this point
			ResourceLocation orbitDimId = SpaceObjectManager.getSpaceManager().getSpaceStationFromBlockCoords(this.pos).getOrbitingPlanetId();
			if(SpaceObjectManager.WARPDIMID.equals(orbitDimId))
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

			setRunning(drill.activate(orbitWorld, laserX, laserZ));
		}

		if(!this.world.isRemote)
			PacketHandler.sendToNearby(new PacketMachine(this, (byte)12), ZUtils.getDimensionIdentifier(world), pos, 128);
	}

	//InventoryHandling start
	@Override
	public int getSizeInventory() {
		return inv.getSizeInventory();
	}

	@Override
	@Nonnull
	public ItemStack getStackInSlot(int i) {
		if(i == 0)
			return lens;
		else {
			i--;
			return inv.getStackInSlot(i);
		}
	}

	@Override
	@Nonnull
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
	public void setInventorySlotContents(int i, @Nonnull ItemStack itemstack) {
		if(i == 0)
			lens = itemstack;
		else {
			this.checkCanRun();
		}
	}

	@Override
	public boolean isUsableByPlayer(PlayerEntity entityplayer) {
		return pos.distanceSq(new BlockPos(entityplayer.getPositionVec())) <= 64;
	}

	@Override
	public boolean isEmpty() {
		return lens.isEmpty();
	}
	
	@Override
	@ParametersAreNonnullByDefault
	public void openInventory(PlayerEntity entity) {
		// TODO Perhaps make sure laser isn't running
	}

	@Override
	@ParametersAreNonnullByDefault
	public void closeInventory(PlayerEntity entity) {
		// TODO Auto-generated method stub

	}

	@Nonnull
	@Override
	@ParametersAreNonnullByDefault
	public int[] getSlotsForFace(Direction side) {
		return new int[] {};
	}

	
	@Override
	@ParametersAreNonnullByDefault
	public boolean canInsertItem(int index, ItemStack itemStackIn, @Nullable  Direction direction) {
		return false;
	}
	
	@Override
	@ParametersAreNonnullByDefault
	public boolean canExtractItem(int index, ItemStack stack, Direction direction) {
		return false;
	}

	@Override
	public boolean isItemValidForSlot(int i, @Nonnull ItemStack itemstack) {
		if(i == 0)
			return AdvancedRocketryItems.itemLensBlock == itemstack.getItem();

		return inv.isItemValidForSlot(i, itemstack);
	}

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

	public boolean isJammed() {
		return this.isJammed;
	}

	public void setJammed(boolean b) {
		this.isJammed = b;
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
		
		modules.add(new ModuleButton(103, 20, "", this,  zmaster587.libVulpes.inventory.TextureResources.buttonLeft, 5, 8).setAdditionalData(0));
		modules.add(new ModuleButton(157, 20, "", this,  zmaster587.libVulpes.inventory.TextureResources.buttonRight, 5, 8).setAdditionalData(1));
		modules.add(new ModuleButton(103, 62, LibVulpes.proxy.getLocalizedString("msg.orbitallaserdrill.reset"), this,  zmaster587.libVulpes.inventory.TextureResources.buttonBuild, 64, 20).setAdditionalData(2));
		modules.add(new ModulePower(11, 25, batteries));
		modules.add(new ModuleSlotArray(56, 54, this, 0, 1));

		return modules;
	}

	@Override
	public String getModularInventoryName() {
		return "block.advancedrocketry.orbitallaserdrill";
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
	@Nonnull
	public ItemStack removeStackFromSlot(int index) {
		return ItemStack.EMPTY;
	}

	@Override
	public void clear() { }
}