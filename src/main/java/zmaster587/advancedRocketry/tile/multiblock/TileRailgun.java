package zmaster587.advancedRocketry.tile.multiblock;

import io.netty.buffer.ByteBuf;

import java.util.List;

import cpw.mods.fml.relauncher.Side;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.SoundCategory;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.MathHelper;
import net.minecraft.world.ChunkCoordIntPair;
import net.minecraft.world.World;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.common.ForgeChunkManager;
import net.minecraftforge.common.ForgeChunkManager.Ticket;
import net.minecraftforge.common.ForgeChunkManager.Type;
import net.minecraftforge.common.util.ForgeDirection;
import zmaster587.advancedRocketry.AdvancedRocketry;
import zmaster587.advancedRocketry.entity.EntityItemAbducted;
import zmaster587.libVulpes.block.RotatableBlock;
import zmaster587.libVulpes.interfaces.ILinkableTile;
import zmaster587.libVulpes.inventory.modules.IGuiCallback;
import zmaster587.libVulpes.inventory.modules.ModuleBase;
import zmaster587.libVulpes.inventory.modules.ModuleNumericTextbox;
import zmaster587.libVulpes.inventory.modules.ModuleRedstoneOutputButton;
import zmaster587.libVulpes.inventory.modules.ModuleSlotArray;
import zmaster587.libVulpes.inventory.modules.ModuleText;
import zmaster587.libVulpes.items.ItemLinker;
import zmaster587.libVulpes.network.PacketHandler;
import zmaster587.libVulpes.network.PacketMachine;
import zmaster587.libVulpes.tile.multiblock.TileMultiPowerConsumer;
import zmaster587.libVulpes.util.EmbeddedInventory;
import zmaster587.libVulpes.util.ZUtils;
import zmaster587.libVulpes.util.ZUtils.RedstoneState;

public class TileRailgun extends TileMultiPowerConsumer implements IInventory, ILinkableTile, IGuiCallback {
	private EmbeddedInventory inv;
	Ticket ticket;
	public long recoil;
	int minStackTransferSize = 1;
	ModuleNumericTextbox textBox;
	RedstoneState state;
	ModuleRedstoneOutputButton redstoneControl;

	public static final Object[][][] structure = { 

		{{null, null, null},
			{null, "coilCopper", null},
			{null, null, null}},

			{{null, null, null},
				{null, "coilCopper", null},
				{null, null, null}},
				{{null, null, null},
					{null, "coilCopper", null},
					{null, null, null}},

					{{null, null, null},
						{null, "coilCopper", null},
						{null, null, null}},

						{{"blockTitanium", 'c', "blockTitanium"}, 
							{'O', "blockTitanium", 'I'},
							{"blockTitanium", 'P', "blockTitanium"}},

	};

	public TileRailgun() {
		inv = new EmbeddedInventory(1);
		powerPerTick = 100000;
		redstoneControl = new ModuleRedstoneOutputButton(174, 4, -1, "", this);
	}

	@Override
	public Object[][][] getStructure() {
		return structure;
	}

	@Override
	public List<ModuleBase> getModules(int ID, EntityPlayer player) {
		List<ModuleBase> modules = super.getModules(ID, player);

		modules.add(new ModuleSlotArray(40, 40, this, 0, 1));
		if(worldObj.isRemote) {
			//if(textBox == null) {
			textBox = new ModuleNumericTextbox(this, 80, 40, 32, 12, 2);
			//}
			textBox.setText(String.valueOf(minStackTransferSize));
			modules.add(new ModuleText(60, 25, "Min Transfer Size", 0x2b2b2b));
			modules.add(textBox);
		}
		
		modules.add(redstoneControl);

		return modules;
	}

	@Override
	protected void onCreated() {
		if(ticket == null) {
			ticket = ForgeChunkManager.requestTicket(AdvancedRocketry.instance, this.worldObj, Type.NORMAL);
			if(ticket != null)
				ForgeChunkManager.forceChunk(ticket, new ChunkCoordIntPair(this.xCoord / 16 - (this.xCoord < 0 ? 1 : 0), this.zCoord / 16 - (this.zCoord < 0 ? 1 : 0)));
		}
	}

	@Override
	public void invalidate() {
		super.invalidate();
		ForgeChunkManager.releaseTicket(ticket);
	}

	@Override
	public void onInventoryUpdated() {
		//Needs completion
		if(itemInPorts.isEmpty() /*&& !worldObj.isRemote*/) {
			attemptCompleteStructure();
		}
	}

	@Override
	public void onInventoryButtonPressed(int buttonId) {
		super.onInventoryButtonPressed(buttonId);
		
		if(buttonId == -1) {
			state = redstoneControl.getState();
			PacketHandler.sendToServer(new PacketMachine(this, (byte)5));
		}
	}
	
	@Override
	protected void onRunningPoweredTick() {
		//Do nothing, or add charge effect
	}

	@Override
	public void useEnergy(int amt) {
		if(!worldObj.isRemote && enabled && isRedstoneStateSatisfied() && attemptCargoTransfer())
			super.useEnergy(amt);
	}

	@Override
	public boolean isRunning() {
		return isComplete();
	}
	
	private boolean isRedstoneStateSatisfied() {
		if(state == RedstoneState.OFF)
			return true;
		
		boolean powered = worldObj.isBlockIndirectlyGettingPowered(xCoord, yCoord, zCoord);
		
		return (state == RedstoneState.ON && powered) || (!powered && state == RedstoneState.INVERTED);
	}

	private boolean attemptCargoTransfer() {
		if(worldObj.isRemote)
			return false;
		
		ItemStack tfrStack = null;
		IInventory inv2 = null;
		int index = 0;
		int xPos = 0,yPos = 0,zPos = 0;

		out:
			for(IInventory inv : this.itemInPorts) {
				for(int i = inv.getSizeInventory() - 1; i >= 0 ; i--) {
					if((tfrStack = inv.getStackInSlot(i)) != null && inv.getStackInSlot(i).stackSize >= minStackTransferSize) {
						inv2 = inv;
						index = i;

						xPos = ((TileEntity)inv).xCoord;
						yPos = ((TileEntity)inv).yCoord;
						zPos = ((TileEntity)inv).zCoord;

						break out;
					}
					else tfrStack = null;
				}
			}

		if(tfrStack != null) {
			ItemStack stack = inv.getStackInSlot(0);

			if(stack != null && stack.getItem() instanceof ItemLinker) {
				int x,y,z, dimId;

				x = ItemLinker.getMasterX(stack);
				y = ItemLinker.getMasterY(stack);
				z = ItemLinker.getMasterZ(stack);
				dimId = ItemLinker.getDimId(stack);

				if(dimId != -1) {
					World world = DimensionManager.getWorld(dimId);
					TileEntity tile;

					if(world != null && (tile = world.getTileEntity(x, y, z)) instanceof TileRailgun && ((TileRailgun)tile).canRecieveCargo(tfrStack) &&
							zmaster587.advancedRocketry.dimension.DimensionManager.getInstance().areDimensionsInSamePlanetMoonSystem(this.worldObj.provider.dimensionId,
									zmaster587.advancedRocketry.dimension.DimensionManager.getEffectiveDimId(world, tile.xCoord, tile.zCoord).getId())) {


						((TileRailgun)tile).onRecieveCargo(tfrStack);
						inv2.setInventorySlotContents(index, null);
						inv2.markDirty();
						world.markBlockForUpdate(xPos, yPos, zPos);
						ForgeDirection dir = RotatableBlock.getFront(this.getBlockMetadata());

						EntityItemAbducted ent = new EntityItemAbducted(this.worldObj, this.xCoord - dir.offsetX + 0.5f, this.yCoord + 5, this.zCoord - dir.offsetZ + 0.5f, tfrStack);
						this.worldObj.spawnEntityInWorld(ent);
						PacketHandler.sendToNearby(new PacketMachine(this, (byte) 3), this.worldObj.provider.dimensionId, this.xCoord - dir.offsetX, this.yCoord + 5, this.zCoord - dir.offsetZ, 64);
						return true;
					}
				}
			}
		}
		return false;
	}

	public boolean canRecieveCargo(ItemStack stack) {
		for(IInventory inv : this.itemOutPorts) {
			if(ZUtils.numEmptySlots(inv) > 0)
				return true;
		}

		return false;
	}

	public void onRecieveCargo(ItemStack stack) {
		for(IInventory inv : this.itemOutPorts) {
			if(ZUtils.doesInvHaveRoom(stack, inv)) {
				ZUtils.mergeInventory(stack, inv);
				break;
			}
		}
	}

	@Override
	public String getMachineName() {
		return "tile.railgun.name";
	}

	@Override
	public AxisAlignedBB getRenderBoundingBox() {
		return AxisAlignedBB.getBoundingBox(xCoord -2,yCoord, zCoord -2, xCoord + 2, yCoord + 5, zCoord + 2);
	}

	@Override
	public int getSizeInventory() {
		return 1;
	}

	@Override
	public ItemStack getStackInSlot(int i) {
		return inv.getStackInSlot(i);
	}

	@Override
	public ItemStack decrStackSize(int i, int j) {
		return inv.decrStackSize(i, j);
	}

	@Override
	public ItemStack getStackInSlotOnClosing(int i) {
		return inv.getStackInSlotOnClosing(i);
	}

	@Override
	public void setInventorySlotContents(int i, ItemStack j) {
		inv.setInventorySlotContents(i, j);

	}

	@Override
	public String getInventoryName() {
		return getMachineName();
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
	public boolean isItemValidForSlot(int i, ItemStack stack) {
		return stack == null || stack.getItem() instanceof ItemLinker;
	}

	@Override
	public boolean onLinkStart(ItemStack item, TileEntity entity,
			EntityPlayer player, World world) {
		ItemLinker.setMasterCoords(item, this.xCoord, this.yCoord, this.zCoord);
		ItemLinker.setDimId(item, world.provider.dimensionId);
		if(!world.isRemote)
			player.addChatMessage(new ChatComponentText("Coordinates programmed into Linker"));
		return true;
	}

	@Override
	public boolean onLinkComplete(ItemStack item, TileEntity entity,
			EntityPlayer player, World world) {
		return false;
	}

	@Override
	public void writeToNBT(NBTTagCompound nbt) {
		super.writeToNBT(nbt);
		inv.writeToNBT(nbt);
		nbt.setInteger("minTfrSize", minStackTransferSize);
		nbt.setByte("redstoneState", (byte) state.ordinal());
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt) {
		super.readFromNBT(nbt);
		inv.readFromNBT(nbt);
		minStackTransferSize = nbt.getInteger("minTfrSize");

		state = RedstoneState.values()[nbt.getByte("redstoneState")];
		redstoneControl.setRedstoneState(state);
	}

	@Override
	public void writeDataToNetwork(ByteBuf out, byte id) {
		if(id == 4)
			out.writeInt(minStackTransferSize);
		else if(id == 5)
			out.writeByte(state.ordinal());
		else
			super.writeDataToNetwork(out, id);
	}

	@Override
	public void readDataFromNetwork(ByteBuf in, byte packetId,
			NBTTagCompound nbt) {
		if(packetId == 4)
			nbt.setInteger("minTransferSize", in.readInt());
		else if(packetId == 5) 
			nbt.setByte("state", in.readByte());
		else
			super.readDataFromNetwork(in, packetId, nbt);
	}

	@Override
	public void useNetworkData(EntityPlayer player, Side side, byte id,
			NBTTagCompound nbt) {
		if(side.isClient()) {
			if(id == 3) {
				ForgeDirection dir = RotatableBlock.getFront(this.getBlockMetadata());
				worldObj.playSound(xCoord + dir.offsetX, yCoord + 5, zCoord + dir.offsetZ, "advancedrocketry:railgunBang", Minecraft.getMinecraft().gameSettings.getSoundLevel(SoundCategory.BLOCKS),  0.975f + worldObj.rand.nextFloat()*0.05f, false);
				recoil = worldObj.getTotalWorldTime();
			}
		}
		else if(id == 4) {
			minStackTransferSize = nbt.getInteger("minTransferSize");

		}			
		else if(id == 5) {
			state = RedstoneState.values()[nbt.getByte("state")];
		}
		else
			super.useNetworkData(player, side, id, nbt);
	}

	@Override
	public void onModuleUpdated(ModuleBase module) {
		if(module == textBox) {
			if(textBox.getText().isEmpty())
				minStackTransferSize = 1;
			else
				minStackTransferSize = MathHelper.clamp_int(Integer.parseInt(textBox.getText()),1, 64);
			PacketHandler.sendToServer(new PacketMachine(this, (byte)4));
		}
	}
	
	@Override
	protected void writeNetworkData(NBTTagCompound nbt) {
		super.writeNetworkData(nbt);
		nbt.setByte("state", (byte)state.ordinal());
	}
	
	@Override
	protected void readNetworkData(NBTTagCompound nbt) {
		super.readNetworkData(nbt);
		redstoneControl.setRedstoneState(state);
	}
}
