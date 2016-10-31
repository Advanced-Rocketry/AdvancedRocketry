package zmaster587.advancedRocketry.tile.multiblock;

import io.netty.buffer.ByteBuf;

import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.World;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.common.ForgeChunkManager;
import net.minecraftforge.common.ForgeChunkManager.Ticket;
import net.minecraftforge.common.ForgeChunkManager.Type;
import net.minecraftforge.fml.relauncher.Side;
import zmaster587.advancedRocketry.AdvancedRocketry;
import zmaster587.advancedRocketry.entity.EntityItemAbducted;
import zmaster587.advancedRocketry.util.AudioRegistry;
import zmaster587.libVulpes.LibVulpes;
import zmaster587.libVulpes.api.LibVulpesBlocks;
import zmaster587.libVulpes.block.BlockMeta;
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
		{	{null, null, null, null, null}, 
			{null, null, "coilCopper" , null, null},
			{null, "coilCopper", null , "coilCopper", null},
			{null, null, "coilCopper" , null, null},
			{null, null, null, null, null}},

			{	{null, null, null, null, null}, 
				{null, null, "coilCopper" , null, null},
				{null, "coilCopper", null , "coilCopper", null},
				{null, null, "coilCopper" , null, null},
				{null, null, null, null, null}},

				{	{null, null, null, null, null}, 
					{null, null, "coilCopper" , null, null},
					{null, "coilCopper", null , "coilCopper", null},
					{null, null, "coilCopper" , null, null},
					{null, null, null, null, null}},

					{	{null, null, null, null, null}, 
						{null, null, "coilCopper" , null, null},
						{null, "coilCopper", null , "coilCopper", null},
						{null, null, "coilCopper" , null, null},
						{null, null, null, null, null}},

						{	{null, null, null, null, null}, 
							{null, null, "coilCopper" , null, null},
							{null, "coilCopper", null , "coilCopper", null},
							{null, null, "coilCopper" , null, null},
							{null, null, null, null, null}},


							{	{null, null, null, null, null}, 
								{null, null, "coilCopper" , null, null},
								{null, "coilCopper", null , "coilCopper", null},
								{null, null, "coilCopper" , null, null},
								{null, null, null, null, null}},

								{	{null, null, null, null, null}, 
									{null, null, "coilCopper" , null, null},
									{null, "coilCopper", null , "coilCopper", null},
									{null, null, "coilCopper" , null, null},
									{null, null, null, null, null}},

									{	{'*', '*', '*', '*', '*'}, 
										{'*', "blockTitanium", "blockTitanium" , "blockTitanium", '*'},
										{'*', "blockTitanium", "blockTitanium" , "blockTitanium", '*'},
										{'*', "blockTitanium", "blockTitanium" , "blockTitanium", '*'},
										{'*', '*', '*', '*', '*'}},

										{{'*', '*', 'c', '*', '*'}, 
											{'*', "blockTitanium", "blockTitanium" , "blockTitanium", '*'},
											{'*', "blockTitanium", "blockTitanium" , "blockTitanium", '*'},
											{'*', "blockTitanium", "blockTitanium" , "blockTitanium", '*'},
											{'*', '*', '*', '*', '*'}}

	};

	public TileRailgun() {
		inv = new EmbeddedInventory(1);
		powerPerTick = 100000;
		redstoneControl = new ModuleRedstoneOutputButton(174, 4, -1, "", this);
		state = RedstoneState.OFF;
		redstoneControl.setRedstoneState(state);
	}

	@Override
	public List<BlockMeta> getAllowableWildCardBlocks() {
		List<BlockMeta> blocks = super.getAllowableWildCardBlocks();

		blocks.addAll(getAllowableBlocks('P'));
		blocks.addAll(getAllowableBlocks('I'));
		blocks.addAll(getAllowableBlocks('O'));
		blocks.add(new BlockMeta(LibVulpesBlocks.blockAdvStructureBlock));

		return blocks;
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
	public void onLoad() {
		if(ticket == null) {
			ticket = ForgeChunkManager.requestTicket(AdvancedRocketry.instance, this.worldObj, Type.NORMAL);
			if(ticket != null)
				ForgeChunkManager.forceChunk(ticket, new ChunkPos(getPos().getX() / 16 - (getPos().getX() < 0 ? 1 : 0), getPos().getZ() / 16 - (getPos().getZ() < 0 ? 1 : 0)));
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
		if(itemInPorts.isEmpty()) {
			attemptCompleteStructure(worldObj.getBlockState(pos));
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

		boolean powered = worldObj.isBlockIndirectlyGettingPowered(pos) > 0;

		return (state == RedstoneState.ON && powered) || (!powered && state == RedstoneState.INVERTED);
	}

	private boolean attemptCargoTransfer() {
		if(worldObj.isRemote)
			return false;

		ItemStack tfrStack = null;
		IInventory inv2 = null;
		int index = 0;
		BlockPos invPos;

		out:
			for(IInventory inv : this.itemInPorts) {
				for(int i = inv.getSizeInventory() - 1; i >= 0 ; i--) {
					if((tfrStack = inv.getStackInSlot(i)) != null && inv.getStackInSlot(i).stackSize >= minStackTransferSize) {
						inv2 = inv;
						index = i;

						invPos = ((TileEntity)inv).getPos();

						break out;
					}
					else tfrStack = null;
				}
			}

		if(tfrStack != null) {
			ItemStack stack = inv.getStackInSlot(0);

			if(stack != null && stack.getItem() instanceof ItemLinker) {
				int x,y,z, dimId;
				BlockPos pos;

				pos = ItemLinker.getMasterCoords(stack);
				dimId = ItemLinker.getDimId(stack);

				if(dimId != -1) {
					World world = DimensionManager.getWorld(dimId);
					TileEntity tile;

					if(world != null && (tile = world.getTileEntity(pos)) instanceof TileRailgun && ((TileRailgun)tile).canRecieveCargo(tfrStack) &&
							zmaster587.advancedRocketry.dimension.DimensionManager.getInstance().areDimensionsInSamePlanetMoonSystem(this.worldObj.provider.getDimension(),
									zmaster587.advancedRocketry.dimension.DimensionManager.getEffectiveDimId(world, pos).getId())) {


						((TileRailgun)tile).onRecieveCargo(tfrStack);
						inv2.setInventorySlotContents(index, null);
						inv2.markDirty();
						worldObj.notifyBlockUpdate(pos, worldObj.getBlockState(pos),  worldObj.getBlockState(pos), 2);

						EnumFacing dir = RotatableBlock.getFront(worldObj.getBlockState(pos));

						EntityItemAbducted ent = new EntityItemAbducted(this.worldObj, this.pos.getX() - 2*dir.getFrontOffsetX() + 0.5f, this.pos.getY() + 5, this.pos.getZ() - 2*dir.getFrontOffsetZ() + 0.5f, tfrStack);
						this.worldObj.spawnEntityInWorld(ent);
						PacketHandler.sendToNearby(new PacketMachine(this, (byte) 3), this.worldObj.provider.getDimension(), this.pos.getX() - dir.getFrontOffsetX(), this.pos.getY() + 5, this.pos.getZ() - dir.getFrontOffsetZ(),  64d);
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
		return new AxisAlignedBB(this.pos.getX() -5, this.pos.getY(), this.pos.getZ() - 5, this.pos.getX() + 5, this.pos.getY() +10, this.pos.getZ() + 5);
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
	public void setInventorySlotContents(int i, ItemStack j) {
		inv.setInventorySlotContents(i, j);

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
	public void openInventory(EntityPlayer player) {

	}

	@Override
	public void closeInventory(EntityPlayer player) {

	}

	@Override
	public boolean isItemValidForSlot(int i, ItemStack stack) {
		return stack == null || stack.getItem() instanceof ItemLinker;
	}

	@Override
	public boolean onLinkStart(ItemStack item, TileEntity entity,
			EntityPlayer player, World world) {
		ItemLinker.setMasterCoords(item, this.getPos());
		ItemLinker.setDimId(item, world.provider.getDimension());
		if(!world.isRemote)
			player.addChatMessage(new TextComponentString("Coordinates programmed into Linker"));
		return true;
	}

	@Override
	public boolean onLinkComplete(ItemStack item, TileEntity entity,
			EntityPlayer player, World world) {
		return false;
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound nbt) {
		super.writeToNBT(nbt);
		inv.writeToNBT(nbt);
		nbt.setInteger("minTfrSize", minStackTransferSize);
		nbt.setByte("redstoneState", (byte) state.ordinal());
		return nbt;
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
				EnumFacing dir = RotatableBlock.getFront(worldObj.getBlockState(pos));
				LibVulpes.proxy.playSound(worldObj, pos, AudioRegistry.railgunFire, SoundCategory.BLOCKS, Minecraft.getMinecraft().gameSettings.getSoundLevel(SoundCategory.BLOCKS), 0.975f + worldObj.rand.nextFloat()*0.05f);
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
		nbt.setInteger("minTfrSize", minStackTransferSize);
	}

	@Override
	protected void readNetworkData(NBTTagCompound nbt) {
		super.readNetworkData(nbt);
		state = RedstoneState.values()[nbt.getByte("redstoneState")];
		redstoneControl.setRedstoneState(state);
		minStackTransferSize = nbt.getInteger("minTfrSize");
	}

	@Override
	public String getName() {
		return getMachineName();
	}

	@Override
	public boolean hasCustomName() {
		return false;
	}

	@Override
	public ItemStack removeStackFromSlot(int index) {
		return inv.removeStackFromSlot(index);
	}

	@Override
	public int getField(int id) {
		return inv.getField(id);
	}

	@Override
	public void setField(int id, int value) {
		inv.setField(id, value);
	}

	@Override
	public int getFieldCount() {
		return inv.getFieldCount();
	}

	@Override
	public void clear() {
		inv.clear();
	}
}
