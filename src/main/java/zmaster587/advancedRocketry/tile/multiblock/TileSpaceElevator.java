package zmaster587.advancedRocketry.tile.multiblock;

import io.netty.buffer.ByteBuf;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Util;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.network.NetworkHooks;
import zmaster587.advancedRocketry.AdvancedRocketry;
import zmaster587.advancedRocketry.api.AdvancedRocketryItems;
import zmaster587.advancedRocketry.api.AdvancedRocketryTileEntityType;
import zmaster587.advancedRocketry.entity.EntityElevatorCapsule;
import zmaster587.advancedRocketry.inventory.modules.ModuleStellarBackground;
import zmaster587.advancedRocketry.item.ItemSpaceElevatorChip;
import zmaster587.advancedRocketry.util.DimensionBlockPosition;
import zmaster587.libVulpes.LibVulpes;
import zmaster587.libVulpes.api.LibVulpesBlocks;
import zmaster587.libVulpes.api.LibvulpesGuiRegistry;
import zmaster587.libVulpes.block.BlockMeta;
import zmaster587.libVulpes.block.RotatableBlock;
import zmaster587.libVulpes.interfaces.ILinkableTile;
import zmaster587.libVulpes.inventory.ContainerModular;
import zmaster587.libVulpes.inventory.GuiHandler;
import zmaster587.libVulpes.inventory.TextureResources;
import zmaster587.libVulpes.inventory.modules.*;
import zmaster587.libVulpes.items.ItemLinker;
import zmaster587.libVulpes.network.PacketHandler;
import zmaster587.libVulpes.network.PacketMachine;
import zmaster587.libVulpes.tile.multiblock.TileMultiPowerConsumer;
import zmaster587.libVulpes.util.EmbeddedInventory;
import zmaster587.libVulpes.util.HashedBlockPosition;
import zmaster587.libVulpes.util.ZUtils;

import java.util.LinkedList;
import java.util.List;

public class TileSpaceElevator extends TileMultiPowerConsumer implements ILinkableTile, IInventory, ITickableTileEntity {

boolean openFullScreen = false;
	
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
				{ '*', '*', new ResourceLocation("forge","blockcoil"), new ResourceLocation("forge","blockcoil"), new ResourceLocation("forge","blockcoil"), '*', '*' },
				{ LibVulpesBlocks.motors, new ResourceLocation("forge","blockcoil"), Blocks.AIR, Blocks.AIR, Blocks.AIR, new ResourceLocation("forge","blockcoil"), LibVulpesBlocks.motors },
				{ 'P', new ResourceLocation("forge","blockcoil"), Blocks.AIR, Blocks.AIR, Blocks.AIR, new ResourceLocation("forge","blockcoil"), 'P' },
				{ LibVulpesBlocks.motors, new ResourceLocation("forge","blockcoil"), Blocks.AIR, Blocks.AIR, Blocks.AIR, new ResourceLocation("forge","blockcoil"), LibVulpesBlocks.motors },
				{ '*', '*', new ResourceLocation("forge","blockcoil"), new ResourceLocation("forge","blockcoil"), new ResourceLocation("forge","blockcoil"), '*', '*' },
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
		super(AdvancedRocketryTileEntityType.TILE_SPACE_ELEVATOR);
		inv = new EmbeddedInventory(1);
		capsule = null;
		firstTick = true;

		landingPadDisplayText = new ModuleText(256, 16, "", 0x00FF00, 2f);
		landingPadDisplayText.setColor(0x00ff00);
		dimBlockPos = null;
	}
	
	@Override
	public void deconstructMultiBlock(World world, BlockPos destroyedPos,
			boolean blockBroken, BlockState state) {
		super.deconstructMultiBlock(world, destroyedPos, blockBroken, state);
		
		Entity e = getCapsuleOnLine();
		
		if(e != null)
			e.remove();
	}

	@Override
	public Object[][][] getStructure() {
		return structure;
	}

	@Override
	public String getMachineName() {
		return "block.advancedrocketry.spaceelevatorcontroller";
	}

	@Override
	public AxisAlignedBB getRenderBoundingBox() {
		
		return new AxisAlignedBB(pos.add(-5,-3,-5),pos.add(5,3000,5));
	}

	@Override
	public List<ModuleBase> getModules(int ID, PlayerEntity player) {
		List<ModuleBase> modules = super.getModules(ID, player);



		if(ID == GuiHandler.guiId.MODULAR.ordinal()) {
			modules.add(new ModuleButton(50, 47, LibVulpes.proxy.getLocalizedString("msg.spaceelevator.button.summon"), this, TextureResources.buttonBuild, 80, 18).setAdditionalData(1));
			modules.add(new ModuleButton(50, 67, LibVulpes.proxy.getLocalizedString("msg.label.selectdst"), this, TextureResources.buttonBuild, 80, 18).setAdditionalData(2));
			modules.add(new ModuleTexturedSlotArray(50, 20, this, 0, 1, zmaster587.advancedRocketry.inventory.TextureResources.idChip));
			modules.add(new ModuleText(70, 23, LibVulpes.proxy.getLocalizedString("msg.spaceelevator.label.chip"), 0x2d2d2d));
		}
		else {
			modules.clear();
			modules.add(new ModuleStellarBackground(0, 0, zmaster587.libVulpes.inventory.TextureResources.starryBG));

			List<ModuleBase> list2 = new LinkedList<ModuleBase>();
			ModuleButton button = new ModuleButton(0, 0, LibVulpes.proxy.getLocalizedString("msg.label.clear"), this, zmaster587.advancedRocketry.inventory.TextureResources.buttonGeneric, 256, 18).setAdditionalData(BUTTON_ID_OFFSET);
			list2.add(button);

			ItemStack stack = getChip();

			if(stack != null) {
				List<DimensionBlockPosition> list;
				list = ((ItemSpaceElevatorChip)AdvancedRocketryItems.itemSpaceElevatorChip).getBlockPositions(stack);

				int i = 1;
				for( DimensionBlockPosition pos : list) 
				{
					button = new ModuleButton(0, i*18, pos.toString(), this, zmaster587.advancedRocketry.inventory.TextureResources.buttonGeneric, 256, 18).setAdditionalData(i + BUTTON_ID_OFFSET);
					list2.add(button);

					if(!isDstValid(world, pos, new HashedBlockPosition(getPos())))
						button.setColor(0xFFFF2222);

					i++;
				}
			}

			ModuleContainerPan pan = new ModuleContainerPan(25, 25, list2, new LinkedList<ModuleBase>(), null, 512, 256, 0, -48, 258, 256);
			modules.add(pan);

			landingPadDisplayText.setText(dimBlockPos != null ? dimBlockPos.toString() : LibVulpes.proxy.getLocalizedString("msg.label.noneselected"));
			modules.add(landingPadDisplayText);
		}

		return modules;
	}

	public static boolean isDstValid(World worldObj, DimensionBlockPosition pos, HashedBlockPosition myPos) {
		if(pos == null || pos.pos == null)
			return false;
		
		return !ZUtils.getDimensionIdentifier(worldObj).equals(pos.dimid) && zmaster587.advancedRocketry.dimension.DimensionManager.getInstance().areDimensionsInSamePlanetMoonSystem(zmaster587.advancedRocketry.dimension.DimensionManager.getEffectiveDimId(pos.dimid, pos.pos.getBlockPos()).getId(), zmaster587.advancedRocketry.dimension.DimensionManager.getEffectiveDimId(worldObj, myPos.getBlockPos()).getId()) ;
	}

	public boolean attemptLaunch() {
		if(!isComplete() || !enabled || !hasEnergy(50000))
			return false;
		useEnergy(50000);
		return true;
	}
	
	@Override
	@OnlyIn(value=Dist.CLIENT)
	public void onInventoryButtonPressed(ModuleButton button) {
		
		int buttonId = button.getAdditionalData() == null ? -1 : (int)button.getAdditionalData();
		
		if(buttonId == 1) {
			PacketHandler.sendToServer(new PacketMachine(this, SUMMON_PACKET));
		}
		else if(buttonId == 2) {
			PacketHandler.sendToServer(new PacketMachine(this, SELECT_DST));
		}
		if( buttonId >= BUTTON_ID_OFFSET) {
			PacketHandler.sendToServer(new PacketMachine(this, (byte)buttonId));
		}


		super.onInventoryButtonPressed(button);
	}

	public void notifyLanded(EntityElevatorCapsule e) {
		if(capsule != null && capsule != e && capsule.isAlive())
			e.remove();
		else {
			capsule = e;
			capsule.setSourceTile(new DimensionBlockPosition(ZUtils.getDimensionIdentifier(world), new HashedBlockPosition(pos)));
			capsule.setDst(dimBlockPos);
		}
		capsule.setPosition(getLandingLocationX(), getPos().getY() - 1, getLandingLocationZ());

		Direction facing = RotatableBlock.getFront(world.getBlockState(getPos()));
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
	public void writeDataToNetwork(PacketBuffer out, byte id) {
		super.writeDataToNetwork(out, id);
	}

	@Override
	public void readDataFromNetwork(PacketBuffer in, byte packetId,
			CompoundNBT nbt) {
		super.readDataFromNetwork(in, packetId, nbt);
	}

	@Override
	public void useNetworkData(PlayerEntity player, Dist side, byte id,
			CompoundNBT nbt) {

		if(id == SUMMON_PACKET) {
			summonCapsule();
		}
		else if (id == SELECT_DST) {
			openFullScreen = true;
			player.closeScreen();
			NetworkHooks.openGui((ServerPlayerEntity) player, this, buf -> {buf.writeInt(GuiHandler.guiId.MODULARFULLSCREEN.ordinal()); buf.writeBlockPos(pos); });
			openFullScreen = false;
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
						if((world = ZUtils.getWorld(dimBlockPos.dimid)) == null) {
							ZUtils.initDimension(dimBlockPos.dimid);
							world = ZUtils.getWorld(dimBlockPos.dimid);
						}

						if(world != null) {
							TileEntity tile = world.getTileEntity(dimBlockPos.pos.getBlockPos());

							if(tile instanceof TileSpaceElevator) {

								summonCapsule();

								capsule.setDst(dimBlockPos);
								capsule.setSourceTile(new DimensionBlockPosition(ZUtils.getDimensionIdentifier(this.getWorld()), new HashedBlockPosition(getPos())));
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

		if(capsule != null && !capsule.isAlive())
			capsule = null;

		double capsulePosX = getLandingLocationX();
		double capsulePosZ = getLandingLocationZ();
		for (EntityElevatorCapsule e :world.getEntitiesWithinAABB(EntityElevatorCapsule.class, new AxisAlignedBB(capsulePosX - 3, getPos().getY() - 1, capsulePosZ - 3, capsulePosX + 3, EntityElevatorCapsule.MAX_HEIGHT, capsulePosZ + 3))) {
			if(!e.isInMotion() && e.isAlive())
				capsule = e;
		}

		return capsule;
	}

	public double getLandingLocationX() {
		Direction facing = RotatableBlock.getFront(world.getBlockState(getPos()));
		return getPos().getX() + facing.getXOffset()*-3 - facing.getZOffset() + 0.5;
	}

	public double getLandingLocationZ() {
		Direction facing = RotatableBlock.getFront(world.getBlockState(getPos()));
		return getPos().getZ() + facing.getXOffset()*1 + facing.getZOffset()*-3 + 0.5;
	}


	public void summonCapsule() {
		//Don't spawn a new capsule if one exists
		if(getCapsuleOnLine() != null)
			return;

		capsule = new EntityElevatorCapsule(world);
		Direction facing = RotatableBlock.getFront(world.getBlockState(getPos()));
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
		capsule.setPositionAndRotation(capsulePosX, getPos().getY() - 1, capsulePosZ, capsule.rotationYaw, capsule.rotationPitch);

		capsule.setSourceTile(new DimensionBlockPosition(ZUtils.getDimensionIdentifier(world), new HashedBlockPosition(this.getPos())));

		world.addEntity(capsule);
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
			PlayerEntity player, World world) {
		ItemLinker.setMasterCoords(item, this.getPos());
		ItemLinker.setDimId(item, ZUtils.getDimensionIdentifier(world));
		if(!world.isRemote)
			player.sendMessage(new TranslationTextComponent("msg.linker.program"), Util.field_240973_b_);
		return true;
	}

	@Override
	public boolean onLinkComplete(ItemStack item, TileEntity entity,
			PlayerEntity player, World myWorld) {

		if(!myWorld.isRemote) {

			ResourceLocation dimid = ItemLinker.getDimId(item);
			BlockPos pos = ItemLinker.getMasterCoords(item);

			DimensionBlockPosition dimPos = new DimensionBlockPosition(dimid, new HashedBlockPosition(pos));

			if(dimPos.dimid == ZUtils.getDimensionIdentifier(myWorld))
			{
				player.sendMessage(new TranslationTextComponent("msg.spaceelevator.samedimensionerror"), Util.field_240973_b_);
				return false;
			}

			World world;
			if((world = ZUtils.getWorld(dimPos.dimid)) == null) {
				ZUtils.initDimension(dimPos.dimid);
				world = ZUtils.getWorld(dimPos.dimid);
			}

			if(world != null) {
				TileEntity tile = world.getTileEntity(dimPos.pos.getBlockPos());
				if(tile instanceof TileSpaceElevator) {

					boolean flag = getChip() != null && ((TileSpaceElevator) tile).getChip() != null;
					if(flag) {
						addEntryToList(dimPos);
						addEntryToList(new DimensionBlockPosition(ZUtils.getDimensionIdentifier(myWorld), new HashedBlockPosition(getPos())));
						((TileSpaceElevator) tile).addEntryToList(new DimensionBlockPosition(ZUtils.getDimensionIdentifier(myWorld), new HashedBlockPosition(getPos())));
						((TileSpaceElevator) tile).addEntryToList(dimPos);
						
						player.sendMessage(new TranslationTextComponent("msg.spaceelevator.newdstadded"), Util.field_240973_b_);
						return true;
					}
					else
					{
						player.sendMessage(new TranslationTextComponent("msg.spaceelevator.nocchiperror"), Util.field_240973_b_);
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
	public boolean isUsableByPlayer(PlayerEntity player) {
		return true;
	}
	
	@Override
	public boolean isEmpty() {
		return inv.isEmpty();
	}

	@Override
	public void openInventory(PlayerEntity player) {
		inv.openInventory(player);
	}

	@Override
	public void closeInventory(PlayerEntity player) {
		inv.closeInventory(player);

	}

	@Override
	public boolean isItemValidForSlot(int index, ItemStack stack) {
		return inv.isItemValidForSlot(index, stack);
	}
	
	@Override
	public void clear() {
		inv.clear();
	}

	@Override
	public CompoundNBT write(CompoundNBT nbt) {
		inv.write(nbt);
		return super.write(nbt);
	}

	@Override
	public void writeNetworkData(CompoundNBT nbt) {


		if(dimBlockPos != null)
		{
			nbt.putString("dstDimId", dimBlockPos.dimid.toString());
			nbt.putIntArray("dstPos", new int[] { dimBlockPos.pos.x, dimBlockPos.pos.y, dimBlockPos.pos.z });

		}

		super.writeNetworkData(nbt);
	}

	@Override
	public void func_230337_a_(BlockState state, CompoundNBT nbt) {
		inv.readFromNBT(nbt);
		super.func_230337_a_(state, nbt);
	}

	@Override
	public void readNetworkData(CompoundNBT nbt) {
		super.readNetworkData(nbt);


		if(nbt.contains("dstDimId")) {
			ResourceLocation id = new ResourceLocation(nbt.getString("dstDimId"));
			int[] pos = nbt.getIntArray("dstPos");
			dimBlockPos = new DimensionBlockPosition(id, new HashedBlockPosition(pos[0], pos[1], pos[2]));
		}
		else
			dimBlockPos = null;

		landingPadDisplayText.setText(dimBlockPos != null ? dimBlockPos.toString() : LibVulpes.proxy.getLocalizedString("msg.label.noneselected"));
	}
	
	@Override
	public Container createMenu(int id, PlayerInventory inv, PlayerEntity player) {
		GuiHandler.guiId guiType = openFullScreen ? GuiHandler.guiId.MODULARFULLSCREEN : getModularInvType();
		openFullScreen = false;
		
		return new ContainerModular(LibvulpesGuiRegistry.CONTAINER_MODULAR_TILE, id, player, getModules(guiType.ordinal(), player), this, guiType);
	}
	
	@Override
	public GuiHandler.guiId getModularInvType() {
		return GuiHandler.guiId.MODULAR;
	}
}
