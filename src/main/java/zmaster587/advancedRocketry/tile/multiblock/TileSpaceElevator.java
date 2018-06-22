package zmaster587.advancedRocketry.tile.multiblock;

import io.netty.buffer.ByteBuf;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.World;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import zmaster587.advancedRocketry.AdvancedRocketry;
import zmaster587.advancedRocketry.api.AdvancedRocketryItems;
import zmaster587.advancedRocketry.entity.EntityElevatorCapsule;
import zmaster587.advancedRocketry.inventory.modules.ModuleStellarBackground;
import zmaster587.advancedRocketry.item.ItemSpaceElevatorChip;
import zmaster587.advancedRocketry.util.DimensionBlockPosition;
import zmaster587.libVulpes.LibVulpes;
import zmaster587.libVulpes.api.LibVulpesBlocks;
import zmaster587.libVulpes.block.BlockMeta;
import zmaster587.libVulpes.block.RotatableBlock;
import zmaster587.libVulpes.interfaces.ILinkableTile;
import zmaster587.libVulpes.inventory.GuiHandler;
import zmaster587.libVulpes.inventory.TextureResources;
import zmaster587.libVulpes.inventory.modules.*;
import zmaster587.libVulpes.items.ItemLinker;
import zmaster587.libVulpes.network.PacketHandler;
import zmaster587.libVulpes.network.PacketMachine;
import zmaster587.libVulpes.tile.multiblock.TileMultiPowerConsumer;
import zmaster587.libVulpes.util.EmbeddedInventory;
import zmaster587.libVulpes.util.HashedBlockPosition;

import java.util.LinkedList;
import java.util.List;

public class TileSpaceElevator extends TileMultiPowerConsumer implements ILinkableTile, IInventory, ITickable {

	Object[][][] structure =
		{
			{
				{null,null,null,null,null,null,null},
				{null,Blocks.REDSTONE_LAMP,null,Blocks.AIR,null,Blocks.REDSTONE_LAMP,null},
				{null,null,Blocks.AIR,Blocks.AIR,Blocks.AIR,null,null},
				{null,Blocks.AIR,Blocks.AIR,Blocks.AIR,Blocks.AIR,Blocks.AIR,null},
				{null,null,Blocks.AIR,Blocks.AIR,Blocks.AIR,null,null},
				{null,Blocks.REDSTONE_LAMP,null,Blocks.AIR,null,Blocks.REDSTONE_LAMP,null},
				{null,null,null,null,null,null,null},
			},
			{
				{null,null,'*','*','*',null,null},
				{null,'*','*','*','*','*',null},
				{'*','*',Blocks.AIR,Blocks.AIR,Blocks.AIR,'*','*'},
				{'*','*',Blocks.AIR,Blocks.AIR,Blocks.AIR,'*','*'},
				{'*','*',Blocks.AIR,Blocks.AIR,Blocks.AIR,'*','*'},
				{null,'*','*','*','*','*',null},
				{null,null,'*','*','*',null,null},
			},
			{
				{null,'*','*',null,'c','*',null},
				{'*','*','*',null,'*','*','*'},
				{'*','*',Blocks.AIR,Blocks.AIR,Blocks.AIR,'*','*'},
				{'*','*',Blocks.AIR,Blocks.AIR,Blocks.AIR,'*','*'},
				{'*','*',Blocks.AIR,Blocks.AIR,Blocks.AIR,'*','*'},
				{'*','*','*','*','*','*','*'},
				{null,'*','*','*','*','*',null},
			},
			{
				{'*','*','*',null,'*','*','*'},
				{'*','*','*',null,'*','*','*'},
				{'*','*',Blocks.AIR,Blocks.AIR,Blocks.AIR,'*','*'},
				{'*','*',Blocks.AIR,Blocks.AIR,Blocks.AIR,'*','*'},
				{'*','*',Blocks.AIR,Blocks.AIR,Blocks.AIR,'*','*'},
				{'*','*','*','*','*','*','*'},
				{'*','*','*','*','*','*','*'},
			},
			{
				{ '*', '*', '*', '*', '*', '*', '*' },
				{ '*', '*', "blockCoil", "blockCoil", "blockCoil", '*', '*' },
				{ LibVulpesBlocks.motors, "blockCoil", Blocks.AIR, Blocks.AIR, Blocks.AIR, "blockCoil", LibVulpesBlocks.motors },
				{ 'P', "blockCoil", Blocks.AIR, Blocks.AIR, Blocks.AIR, "blockCoil", 'P' },
				{ LibVulpesBlocks.motors, "blockCoil", Blocks.AIR, Blocks.AIR, Blocks.AIR, "blockCoil", LibVulpesBlocks.motors },
				{ '*', '*', "blockCoil", "blockCoil", "blockCoil", '*', '*' },
				{ '*', '*', LibVulpesBlocks.motors, 'P', LibVulpesBlocks.motors, '*', '*' },
			}
		};

	EmbeddedInventory inv;
	EntityElevatorCapsule capsule;
	boolean firstTick;
	DimensionBlockPosition dimBlockPos;

	private ModuleText landingPadDisplayText;
	private static final byte SUMMON_PACKET = 2;
	private static final byte SELECT_DST = 3;
	private static final int BUTTON_ID_OFFSET = 5;

	public TileSpaceElevator() {
		super();
		inv = new EmbeddedInventory(1);
		capsule = null;
		firstTick = true;

		landingPadDisplayText = new ModuleText(256, 16, "", 0x00FF00, 2f);
		landingPadDisplayText.setColor(0x00ff00);
		dimBlockPos = null;
	}
	
	@Override
	public void deconstructMultiBlock(World world, BlockPos destroyedPos,
			boolean blockBroken, IBlockState state) {
		super.deconstructMultiBlock(world, destroyedPos, blockBroken, state);
		
		Entity e = getCapsuleOnLine();
		
		if(e != null)
			e.setDead();
	}

	@Override
	public Object[][][] getStructure() {
		return structure;
	}

	@Override
	public String getMachineName() {
		return "tile.spaceElevatorController.name";
	}

	@Override
	public AxisAlignedBB getRenderBoundingBox() {
		
		return new AxisAlignedBB(pos.add(-5,-3,-5),pos.add(5,3000,5));
	}

	@Override
	public List<ModuleBase> getModules(int ID, EntityPlayer player) {
		List<ModuleBase> modules = super.getModules(ID, player);



		if(ID == GuiHandler.guiId.MODULAR.ordinal()) {
			modules.add(new ModuleButton(50, 47, 1, LibVulpes.proxy.getLocalizedString("msg.spaceElevator.button.summon"), this, TextureResources.buttonBuild, 80, 18));
			modules.add(new ModuleButton(50, 67, 2, LibVulpes.proxy.getLocalizedString("msg.label.selectDst"), this, TextureResources.buttonBuild, 80, 18));
			modules.add(new ModuleTexturedSlotArray(50, 20, this, 0, 1, zmaster587.advancedRocketry.inventory.TextureResources.idChip));
			modules.add(new ModuleText(70, 23, LibVulpes.proxy.getLocalizedString("msg.spaceElevator.label.chip"), 0x2d2d2d));
		}
		else {
			modules.clear();
			modules.add(new ModuleStellarBackground(0, 0, zmaster587.libVulpes.inventory.TextureResources.starryBG));

			List<ModuleBase> list2 = new LinkedList<ModuleBase>();
			ModuleButton button = new ModuleButton(0, 0, BUTTON_ID_OFFSET, LibVulpes.proxy.getLocalizedString("msg.label.clear"), this, zmaster587.advancedRocketry.inventory.TextureResources.buttonGeneric, 256, 18);
			list2.add(button);

			ItemStack stack = getChip();

			if(stack != null) {
				List<DimensionBlockPosition> list;
				list = ((ItemSpaceElevatorChip)AdvancedRocketryItems.itemSpaceElevatorChip).getBlockPositions(stack);

				int i = 1;
				for( DimensionBlockPosition pos : list) 
				{
					button = new ModuleButton(0, i*18, i + BUTTON_ID_OFFSET, pos.toString(), this, zmaster587.advancedRocketry.inventory.TextureResources.buttonGeneric, 256, 18);
					list2.add(button);

					if(!isDstValid(world, pos, new HashedBlockPosition(getPos())))
						button.setColor(0xFFFF2222);

					i++;
				}
			}

			ModuleContainerPan pan = new ModuleContainerPan(25, 25, list2, new LinkedList<ModuleBase>(), null, 512, 256, 0, -48, 258, 256);
			modules.add(pan);

			landingPadDisplayText.setText(dimBlockPos != null ? dimBlockPos.toString() : LibVulpes.proxy.getLocalizedString("msg.label.noneSelected"));
			modules.add(landingPadDisplayText);
		}

		return modules;
	}

	public static boolean isDstValid(World worldObj, DimensionBlockPosition pos, HashedBlockPosition myPos) {
		if(pos == null || pos.pos == null)
			return false;
		
		return worldObj.provider.getDimension() != pos.dimid && zmaster587.advancedRocketry.dimension.DimensionManager.getInstance().areDimensionsInSamePlanetMoonSystem(zmaster587.advancedRocketry.dimension.DimensionManager.getEffectiveDimId(pos.dimid, pos.pos.getBlockPos()).getId(), zmaster587.advancedRocketry.dimension.DimensionManager.getEffectiveDimId(worldObj, myPos.getBlockPos()).getId()) ;
	}

	public boolean attemptLaunch() {
		if(!isComplete() || !enabled || !hasEnergy(50000))
			return false;
		useEnergy(50000);
		return true;
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public void onInventoryButtonPressed(int buttonId) {
		if(buttonId == 1) {
			PacketHandler.sendToServer(new PacketMachine(this, SUMMON_PACKET));
		}
		else if(buttonId == 2) {
			PacketHandler.sendToServer(new PacketMachine(this, SELECT_DST));
		}
		if( buttonId >= BUTTON_ID_OFFSET) {
			PacketHandler.sendToServer(new PacketMachine(this, (byte)buttonId));
		}


		super.onInventoryButtonPressed(buttonId);
	}

	public void notifyLanded(EntityElevatorCapsule e) {
		if(capsule != null && capsule != e && !capsule.isDead)
			e.setDead();
		else {
			capsule = e;
			capsule.setSourceTile(new DimensionBlockPosition(world.provider.getDimension(), new HashedBlockPosition(pos)));
			capsule.setDst(dimBlockPos);
		}
		capsule.setPosition(getLandingLocationX(), getPos().getY() - 1, getLandingLocationZ());

		EnumFacing facing = RotatableBlock.getFront(world.getBlockState(getPos()));
		switch(facing) {
		case EAST:
			capsule.rotationYaw = 180;
			break;
		case SOUTH:
			capsule.rotationYaw = 90;
			break;
		case NORTH:
			capsule.rotationYaw = 270;
			break;
		default:
			capsule.rotationYaw = 0;
		}
	}

	@Override
	public void update() {
		super.update();
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

		if(id == SUMMON_PACKET) {
			summonCapsule();
		}
		else if (id == SELECT_DST) {
			player.closeScreen();
			player.openGui(LibVulpes.instance, GuiHandler.guiId.MODULARFULLSCREEN.ordinal(), player.world, this.pos.getX(), this.pos.getY(), this.pos.getZ());
		}
		else if(id == BUTTON_ID_OFFSET) {
			dimBlockPos = null;
			capsule.setDst(null);

			markDirty();
			world.notifyBlockUpdate(pos, world.getBlockState(pos),  world.getBlockState(pos), 3);
		}
		else if(id > BUTTON_ID_OFFSET) {
			ItemStack stack = inv.getStackInSlot(0);

			if(stack != null && stack.getItem() instanceof ItemSpaceElevatorChip) {
				List<DimensionBlockPosition> list;
				list = ((ItemSpaceElevatorChip)AdvancedRocketryItems.itemSpaceElevatorChip).getBlockPositions(stack);

				try {
					DimensionBlockPosition dstpos = list.get(id - BUTTON_ID_OFFSET - 1);
					if(isDstValid(world,dstpos, new HashedBlockPosition(getPos()))) {

						dimBlockPos = dstpos;
						World world;
						if((world = DimensionManager.getWorld(dimBlockPos.dimid)) == null) {
							DimensionManager.initDimension(dimBlockPos.dimid);
							world = DimensionManager.getWorld(dimBlockPos.dimid);
						}

						if(world != null) {
							TileEntity tile = world.getTileEntity(dimBlockPos.pos.getBlockPos());

							if(tile instanceof TileSpaceElevator) {

								summonCapsule();

								capsule.setDst(dimBlockPos);
								capsule.setSourceTile(new DimensionBlockPosition(this.getWorld().provider.getDimension(), new HashedBlockPosition(getPos())));
								markDirty();
								this.world.notifyBlockUpdate(pos, world.getBlockState(pos),  world.getBlockState(pos), 3);
								return;
							}
						}
					}
				} catch (IndexOutOfBoundsException e) {
					AdvancedRocketry.logger.warn("Space Elevator at location " + this.pos + " recieved invalid button press!");
					//Sigh...
				}
				dimBlockPos = null;
			}
		}

		super.useNetworkData(player, side, id, nbt);
	}

	public EntityElevatorCapsule getCapsuleOnLine() {

		if(capsule != null && capsule.isDead)
			capsule = null;

		double capsulePosX = getLandingLocationX();
		double capsulePosZ = getLandingLocationZ();
		for (EntityElevatorCapsule e :world.getEntitiesWithinAABB(EntityElevatorCapsule.class, new AxisAlignedBB(capsulePosX - 3, getPos().getY() - 1, capsulePosZ - 3, capsulePosX + 3, EntityElevatorCapsule.MAX_HEIGHT, capsulePosZ + 3))) {
			if(!e.isInMotion() && !e.isDead)
				capsule = e;
		}

		return capsule;
	}

	public double getLandingLocationX() {
		EnumFacing facing = RotatableBlock.getFront(world.getBlockState(getPos()));
		return getPos().getX() + facing.getFrontOffsetX()*-3 - facing.getFrontOffsetZ() + 0.5;
	}

	public double getLandingLocationZ() {
		EnumFacing facing = RotatableBlock.getFront(world.getBlockState(getPos()));
		return getPos().getZ() + facing.getFrontOffsetX()*1 + facing.getFrontOffsetZ()*-3 + 0.5;
	}


	public void summonCapsule() {
		//Don't spawn a new capsule if one exists
		if(getCapsuleOnLine() != null)
			return;

		capsule = new EntityElevatorCapsule(world);
		EnumFacing facing = RotatableBlock.getFront(world.getBlockState(getPos()));
		switch(facing) {
		case EAST:
			capsule.rotationYaw = 180;
			break;
		case SOUTH:
			capsule.rotationYaw = 90;
			break;
		case NORTH:
			capsule.rotationYaw = 270;
			break;
		default:
			capsule.rotationYaw = 0;
		}

		double capsulePosX = getLandingLocationX();
		double capsulePosZ = getLandingLocationZ();
		capsule.setPosition(capsulePosX, getPos().getY() - 1, capsulePosZ);

		capsule.setSourceTile(new DimensionBlockPosition(world.provider.getDimension(), new HashedBlockPosition(this.getPos())));

		world.spawnEntity(capsule);
	}

	@Override
	public List<BlockMeta> getAllowableWildCardBlocks() {
		List<BlockMeta> list = super.getAllowableWildCardBlocks();
		list.add(new BlockMeta(Blocks.STONE));
		list.add(new BlockMeta(Blocks.SANDSTONE));
		list.add(new BlockMeta(Blocks.IRON_BLOCK));
		list.add(new BlockMeta(LibVulpesBlocks.blockStructureBlock));
		list.add(new BlockMeta(LibVulpesBlocks.blockAdvStructureBlock));

		return list;
	}

	@Override
	public boolean onLinkStart(ItemStack item, TileEntity entity,
			EntityPlayer player, World world) {
		ItemLinker.setMasterCoords(item, this.getPos());
		ItemLinker.setDimId(item, world.provider.getDimension());
		if(!world.isRemote)
			player.sendMessage(new TextComponentString(LibVulpes.proxy.getLocalizedString("msg.linker.program")));
		return true;
	}

	@Override
	public boolean onLinkComplete(ItemStack item, TileEntity entity,
			EntityPlayer player, World myWorld) {

		if(!myWorld.isRemote) {

			int dimid = ItemLinker.getDimId(item);
			BlockPos pos = ItemLinker.getMasterCoords(item);

			DimensionBlockPosition dimPos = new DimensionBlockPosition(dimid, new HashedBlockPosition(pos));

			if(dimPos.dimid == world.provider.getDimension())
			{
				player.sendMessage(new TextComponentString(LibVulpes.proxy.getLocalizedString("msg.spaceElevator.sameDimensionError")));
				return false;
			}

			World world;
			if((world = DimensionManager.getWorld(dimPos.dimid)) == null) {
				DimensionManager.initDimension(dimPos.dimid);
				world = DimensionManager.getWorld(dimPos.dimid);
			}

			if(world != null) {
				TileEntity tile = world.getTileEntity(dimPos.pos.getBlockPos());
				if(tile instanceof TileSpaceElevator) {

					boolean flag = getChip() != null && ((TileSpaceElevator) tile).getChip() != null;
					if(flag) {
						addEntryToList(dimPos);
						addEntryToList(new DimensionBlockPosition(this.world.provider.getDimension(), new HashedBlockPosition(getPos())));
						((TileSpaceElevator) tile).addEntryToList(new DimensionBlockPosition(this.world.provider.getDimension(), new HashedBlockPosition(getPos())));
						((TileSpaceElevator) tile).addEntryToList(dimPos);
						
						player.sendMessage(new TextComponentString(LibVulpes.proxy.getLocalizedString("msg.spaceElevator.newDstAdded")));
						return true;
					}
					else
					{
						player.sendMessage(new TextComponentString(LibVulpes.proxy.getLocalizedString("msg.spaceElevator.noChipError")));
						return false;
					}
				}
			}
		}

		return false;
	}

	private ItemStack getChip() {
		if(inv.getStackInSlot(0) != null && inv.getStackInSlot(0).getItem() instanceof ItemSpaceElevatorChip)
			return inv.getStackInSlot(0);
		return null;
	}

	private boolean addEntryToList(DimensionBlockPosition pos) {
		ItemStack chip = getChip();
		if(chip != null) {
			List<DimensionBlockPosition> list = ((ItemSpaceElevatorChip)AdvancedRocketryItems.itemSpaceElevatorChip).getBlockPositions(chip);

			if(!list.contains(pos))
				list.add(pos);

			((ItemSpaceElevatorChip)AdvancedRocketryItems.itemSpaceElevatorChip).setBlockPositions(chip, list);
			return true;
		}
		return false;
	}

	@Override
	public String getName() {
		return getModularInventoryName();
	}

	@Override
	public boolean hasCustomName() {
		return false;
	}

	@Override
	public int getSizeInventory() {
		return inv.getSizeInventory();
	}

	@Override
	public ItemStack getStackInSlot(int index) {
		return inv.getStackInSlot(index);
	}

	@Override
	public ItemStack decrStackSize(int index, int count) {
		return inv.decrStackSize(index, count);
	}

	@Override
	public ItemStack removeStackFromSlot(int index) {
		return inv.removeStackFromSlot(index);
	}

	@Override
	public void setInventorySlotContents(int index, ItemStack stack) {
		inv.setInventorySlotContents(index, stack);
	}

	@Override
	public int getInventoryStackLimit() {
		return inv.getInventoryStackLimit();
	}

	@Override
	public boolean isUsableByPlayer(EntityPlayer player) {
		return true;
	}
	
	@Override
	public boolean isEmpty() {
		return inv.isEmpty();
	}

	@Override
	public void openInventory(EntityPlayer player) {
		inv.openInventory(player);
	}

	@Override
	public void closeInventory(EntityPlayer player) {
		inv.closeInventory(player);

	}

	@Override
	public boolean isItemValidForSlot(int index, ItemStack stack) {
		return inv.isItemValidForSlot(index, stack);
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

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound nbt) {
		inv.writeToNBT(nbt);
		return super.writeToNBT(nbt);
	}

	@Override
	public void writeNetworkData(NBTTagCompound nbt) {


		if(dimBlockPos != null)
		{
			nbt.setInteger("dstDimId", dimBlockPos.dimid);
			nbt.setIntArray("dstPos", new int[] { dimBlockPos.pos.x, dimBlockPos.pos.y, dimBlockPos.pos.z });

		}

		super.writeNetworkData(nbt);
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt) {
		inv.readFromNBT(nbt);
		super.readFromNBT(nbt);
	}

	@Override
	public void readNetworkData(NBTTagCompound nbt) {
		super.readNetworkData(nbt);


		if(nbt.hasKey("dstDimId")) {
			int id = nbt.getInteger("dstDimId");
			int[] pos = nbt.getIntArray("dstPos");
			dimBlockPos = new DimensionBlockPosition(id, new HashedBlockPosition(pos[0], pos[1], pos[2]));
		}
		else
			dimBlockPos = null;

		landingPadDisplayText.setText(dimBlockPos != null ? dimBlockPos.toString() : LibVulpes.proxy.getLocalizedString("msg.label.noneSelected"));
	}
}
