package zmaster587.advancedRocketry.tile.multiblock;

import io.netty.buffer.ByteBuf;

import java.util.LinkedList;
import java.util.List;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.common.util.ForgeDirection;
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
import zmaster587.libVulpes.inventory.modules.ModuleBase;
import zmaster587.libVulpes.inventory.modules.ModuleButton;
import zmaster587.libVulpes.inventory.modules.ModuleContainerPan;
import zmaster587.libVulpes.inventory.modules.ModuleSlotArray;
import zmaster587.libVulpes.inventory.modules.ModuleText;
import zmaster587.libVulpes.inventory.modules.ModuleTexturedSlotArray;
import zmaster587.libVulpes.items.ItemLinker;
import zmaster587.libVulpes.network.PacketHandler;
import zmaster587.libVulpes.network.PacketMachine;
import zmaster587.libVulpes.tile.multiblock.TileMultiPowerConsumer;
import zmaster587.libVulpes.util.BlockPosition;
import zmaster587.libVulpes.util.EmbeddedInventory;

public class TileSpaceElevator extends TileMultiPowerConsumer implements ILinkableTile, IInventory {

	Object[][][] structure =
		{
			{
				{null,null,null,null,null,null,null},
				{null,Blocks.redstone_lamp,null,Blocks.air,null,Blocks.redstone_lamp,null},
				{null,null,Blocks.air,Blocks.air,Blocks.air,null,null},
				{null,Blocks.air,Blocks.air,Blocks.air,Blocks.air,Blocks.air,null},
				{null,null,Blocks.air,Blocks.air,Blocks.air,null,null},
				{null,Blocks.redstone_lamp,null,Blocks.air,null,Blocks.redstone_lamp,null},
				{null,null,null,null,null,null,null},
			},
			{
				{null,null,'*','*','*',null,null},
				{null,'*','*','*','*','*',null},
				{'*','*',Blocks.air,Blocks.air,Blocks.air,'*','*'},
				{'*','*',Blocks.air,Blocks.air,Blocks.air,'*','*'},
				{'*','*',Blocks.air,Blocks.air,Blocks.air,'*','*'},
				{null,'*','*','*','*','*',null},
				{null,null,'*','*','*',null,null},
			},
			{
				{null,'*','*',null,'c','*',null},
				{'*','*','*',null,'*','*','*'},
				{'*','*',Blocks.air,Blocks.air,Blocks.air,'*','*'},
				{'*','*',Blocks.air,Blocks.air,Blocks.air,'*','*'},
				{'*','*',Blocks.air,Blocks.air,Blocks.air,'*','*'},
				{'*','*','*','*','*','*','*'},
				{null,'*','*','*','*','*',null},
			},
			{
				{'*','*','*',null,'*','*','*'},
				{'*','*','*',null,'*','*','*'},
				{'*','*',Blocks.air,Blocks.air,Blocks.air,'*','*'},
				{'*','*',Blocks.air,Blocks.air,Blocks.air,'*','*'},
				{'*','*',Blocks.air,Blocks.air,Blocks.air,'*','*'},
				{'*','*','*','*','*','*','*'},
				{'*','*','*','*','*','*','*'},
			},
			{
				{ '*', '*', '*', '*', '*', '*', '*' },
				{ '*', '*', "blockCoil", "blockCoil", "blockCoil", '*', '*' },
				{ LibVulpesBlocks.motors, "blockCoil", Blocks.air, Blocks.air, Blocks.air, "blockCoil", LibVulpesBlocks.motors },
				{ 'P', "blockCoil", Blocks.air, Blocks.air, Blocks.air, "blockCoil", 'P' },
				{ LibVulpesBlocks.motors, "blockCoil", Blocks.air, Blocks.air, Blocks.air, "blockCoil", LibVulpesBlocks.motors },
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
	public Object[][][] getStructure() {
		return structure;
	}

	@Override
	public String getMachineName() {
		return "tile.spaceElevatorController.name";
	}

	@Override
	public void deconstructMultiBlock(World world, int destroyedX,
			int destroyedY, int destroyedZ, boolean blockBroken) {
		super.deconstructMultiBlock(world, destroyedX, destroyedY, destroyedZ,
				blockBroken);
		
		Entity e = getCapsuleOnLine();
		
		if(e != null)
			e.setDead();
	}
	
	@Override
	public AxisAlignedBB getRenderBoundingBox() {
		
		return AxisAlignedBB.getBoundingBox(xCoord - 5, yCoord - 3,zCoord - 5, xCoord + 5, yCoord + 3000, zCoord + 5);
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

					if(!isDstValid(worldObj, pos, new BlockPosition(xCoord, yCoord, zCoord)))
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

	public static boolean isDstValid(World worldObj, DimensionBlockPosition pos, BlockPosition myPos) {
		if(pos == null || pos.pos == null)
			return false;

		World world;
		if((world = DimensionManager.getWorld(pos.dimid)) == null) {
			DimensionManager.initDimension(pos.dimid);
			world = DimensionManager.getWorld(pos.dimid);
		}
		
		if(world == null)
			return false;
		return worldObj.provider.dimensionId != pos.dimid && zmaster587.advancedRocketry.dimension.DimensionManager.getEffectiveDimId(world, pos.pos.x, pos.pos.z) == zmaster587.advancedRocketry.dimension.DimensionManager.getEffectiveDimId(worldObj, myPos.x, myPos.z);
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
			capsule.setSourceTile(new DimensionBlockPosition(worldObj.provider.dimensionId, new BlockPosition(xCoord, yCoord, zCoord)));
			capsule.setDst(dimBlockPos);
		}
		capsule.setPosition(getLandingLocationX(), yCoord - 1, getLandingLocationZ());

		ForgeDirection facing = RotatableBlock.getFront(worldObj.getBlockMetadata(xCoord, yCoord, zCoord));
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
			player.openGui(LibVulpes.instance, GuiHandler.guiId.MODULARFULLSCREEN.ordinal(), player.worldObj, xCoord, yCoord, zCoord);
		}
		else if(id == BUTTON_ID_OFFSET) {
			dimBlockPos = null;
			capsule.setDst(null);

			markDirty();
			worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
		}
		else if(id > BUTTON_ID_OFFSET) {
			ItemStack stack = inv.getStackInSlot(0);

			if(stack != null && stack.getItem() instanceof ItemSpaceElevatorChip) {
				List<DimensionBlockPosition> list;
				list = ((ItemSpaceElevatorChip)AdvancedRocketryItems.itemSpaceElevatorChip).getBlockPositions(stack);

				try {
					DimensionBlockPosition dstpos = list.get(id - BUTTON_ID_OFFSET - 1);
					if(isDstValid(worldObj,dstpos, new BlockPosition(xCoord, yCoord, zCoord))) {

						dimBlockPos = dstpos;
						World world;
						if((world = DimensionManager.getWorld(dimBlockPos.dimid)) == null) {
							DimensionManager.initDimension(dimBlockPos.dimid);
							world = DimensionManager.getWorld(dimBlockPos.dimid);
						}

						if(world != null) {
							TileEntity tile = world.getTileEntity(dimBlockPos.pos.x, dimBlockPos.pos.y, dimBlockPos.pos.z);

							if(tile instanceof TileSpaceElevator) {

								summonCapsule();

								capsule.setDst(dimBlockPos);
								capsule.setSourceTile(new DimensionBlockPosition(this.worldObj.provider.dimensionId, new BlockPosition(xCoord, yCoord, zCoord)));
								markDirty();
								worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
								return;
							}
						}
					}
				} catch (IndexOutOfBoundsException e) {
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
		for (Object e : worldObj.getEntitiesWithinAABB(EntityElevatorCapsule.class, AxisAlignedBB.getBoundingBox(capsulePosX - 1, yCoord - 1, capsulePosZ - 1, capsulePosX + 1, EntityElevatorCapsule.MAX_HEIGHT, capsulePosZ + 1))) {
			EntityElevatorCapsule capsule =(EntityElevatorCapsule)e;
			if(!capsule.isInMotion() && !capsule.isDead)
				this.capsule = capsule;
		}

		return capsule;
	}

	public double getLandingLocationX() {
		ForgeDirection facing = RotatableBlock.getFront(worldObj.getBlockMetadata(xCoord, yCoord, zCoord));
		return xCoord + facing.offsetX*-3 - facing.offsetZ + 0.5;
	}

	public double getLandingLocationZ() {
		ForgeDirection facing = RotatableBlock.getFront(worldObj.getBlockMetadata(xCoord, yCoord, zCoord));
		return zCoord + facing.offsetX*1 + facing.offsetZ*-3 + 0.5;
	}


	public void summonCapsule() {
		//Don't spawn a new capsule if one exists
		if(getCapsuleOnLine() != null)
			return;

		capsule = new EntityElevatorCapsule(worldObj);
		ForgeDirection facing = RotatableBlock.getFront(worldObj.getBlockMetadata(xCoord, yCoord, zCoord));
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
		capsule.setPosition(capsulePosX, yCoord - 1, capsulePosZ);

		capsule.setSourceTile(new DimensionBlockPosition(worldObj.provider.dimensionId, new BlockPosition(xCoord, yCoord, zCoord)));

		worldObj.spawnEntityInWorld(capsule);
	}

	@Override
	public List<BlockMeta> getAllowableWildCardBlocks() {
		List<BlockMeta> list = super.getAllowableWildCardBlocks();
		list.add(new BlockMeta(Blocks.stone));
		list.add(new BlockMeta(Blocks.sandstone));
		list.add(new BlockMeta(Blocks.iron_block));
		list.add(new BlockMeta(LibVulpesBlocks.blockStructureBlock));
		list.add(new BlockMeta(LibVulpesBlocks.blockAdvStructureBlock));

		return list;
	}

	@Override
	public boolean onLinkStart(ItemStack item, TileEntity entity,
			EntityPlayer player, World world) {
		ItemLinker.setMasterCoords(item, xCoord, yCoord, zCoord);
		ItemLinker.setDimId(item, world.provider.dimensionId);
		if(!world.isRemote)
			player.addChatMessage(new ChatComponentText("Coordinates programmed into Linker"));
		return true;
	}

	@Override
	public boolean onLinkComplete(ItemStack item, TileEntity entity,
			EntityPlayer player, World myWorld) {

		if(!myWorld.isRemote) {

			int dimid = ItemLinker.getDimId(item);
			BlockPosition pos = ItemLinker.getMasterCoords(item);

			DimensionBlockPosition dimPos = new DimensionBlockPosition(dimid, pos);

			if(dimPos.dimid == worldObj.provider.dimensionId)
			{
				player.addChatMessage(new ChatComponentText(LibVulpes.proxy.getLocalizedString("msg.spaceElevator.sameDimensionError")));
				return false;
			}

			World world;
			if((world = DimensionManager.getWorld(dimPos.dimid)) == null) {
				DimensionManager.initDimension(dimPos.dimid);
				world = DimensionManager.getWorld(dimPos.dimid);
			}

			if(world != null) {
				TileEntity tile = world.getTileEntity(dimPos.pos.x, dimPos.pos.y, dimPos.pos.z);
				if(tile instanceof TileSpaceElevator) {

					boolean flag = getChip() != null && ((TileSpaceElevator) tile).getChip() != null;
					if(flag) {
						addEntryToList(dimPos);
						addEntryToList(new DimensionBlockPosition(worldObj.provider.dimensionId, new BlockPosition(xCoord, yCoord, zCoord)));
						((TileSpaceElevator) tile).addEntryToList(new DimensionBlockPosition(worldObj.provider.dimensionId, new BlockPosition(xCoord, yCoord, zCoord)));
						((TileSpaceElevator) tile).addEntryToList(dimPos);
						
						player.addChatMessage(new ChatComponentText(LibVulpes.proxy.getLocalizedString("msg.spaceElevator.newDstAdded")));
						return true;
					}
					else
					{
						player.addChatMessage(new ChatComponentText(LibVulpes.proxy.getLocalizedString("msg.spaceElevator.noChipError")));
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
	public void setInventorySlotContents(int index, ItemStack stack) {
		inv.setInventorySlotContents(index, stack);
	}

	@Override
	public int getInventoryStackLimit() {
		return inv.getInventoryStackLimit();
	}

	@Override
	public boolean isUseableByPlayer(EntityPlayer player) {
		return true;
	}


	@Override
	public boolean isItemValidForSlot(int index, ItemStack stack) {
		return inv.isItemValidForSlot(index, stack);
	}

	@Override
	public void writeToNBT(NBTTagCompound nbt) {
		inv.writeToNBT(nbt);
		super.writeToNBT(nbt);
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
			dimBlockPos = new DimensionBlockPosition(id, new BlockPosition(pos[0], pos[1], pos[2]));
		}
		else
			dimBlockPos = null;

		landingPadDisplayText.setText(dimBlockPos != null ? dimBlockPos.toString() : LibVulpes.proxy.getLocalizedString("msg.label.noneSelected"));
	}

	@Override
	public ItemStack getStackInSlotOnClosing(int p_70304_1_) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getInventoryName() {
		return getModularInventoryName();
	}

	@Override
	public boolean hasCustomInventoryName() {
		return false;
	}

	@Override
	public void openInventory() {
		inv.openInventory();
	}

	@Override
	public void closeInventory() {
		inv.closeInventory();
	}
}
