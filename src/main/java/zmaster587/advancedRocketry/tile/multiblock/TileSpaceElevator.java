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
import zmaster587.advancedRocketry.api.ARConfiguration;
import zmaster587.advancedRocketry.api.AdvancedRocketryItems;
import zmaster587.advancedRocketry.api.AdvancedRocketryTileEntityType;
import zmaster587.advancedRocketry.dimension.DimensionManager;
import zmaster587.advancedRocketry.entity.EntityElevatorCapsule;
import zmaster587.advancedRocketry.stations.SpaceObjectManager;
import zmaster587.advancedRocketry.stations.SpaceStationObject;
import zmaster587.advancedRocketry.util.DimensionBlockPosition;
import zmaster587.advancedRocketry.util.PlanetaryTravelHelper;
import zmaster587.libVulpes.LibVulpes;
import zmaster587.libVulpes.api.LibVulpesBlocks;
import zmaster587.libVulpes.api.LibvulpesGuiRegistry;
import zmaster587.libVulpes.block.BlockMeta;
import zmaster587.libVulpes.block.RotatableBlock;
import zmaster587.libVulpes.interfaces.ILinkableTile;
import zmaster587.libVulpes.inventory.ContainerModular;
import zmaster587.libVulpes.inventory.GuiHandler;
import zmaster587.libVulpes.inventory.TextureResources;
import zmaster587.libVulpes.inventory.modules.IModularInventory;
import zmaster587.libVulpes.inventory.modules.ModuleBase;
import zmaster587.libVulpes.inventory.modules.ModuleButton;
import zmaster587.libVulpes.inventory.modules.ModuleText;
import zmaster587.libVulpes.items.ItemLinker;
import zmaster587.libVulpes.network.PacketHandler;
import zmaster587.libVulpes.network.PacketMachine;
import zmaster587.libVulpes.tile.multiblock.TileMultiPowerConsumer;
import zmaster587.libVulpes.util.HashedBlockPosition;
import zmaster587.libVulpes.util.ZUtils;

import java.util.List;

public class TileSpaceElevator extends TileMultiPowerConsumer implements ILinkableTile, IModularInventory, ITickableTileEntity {


boolean openFullScreen = false;
	
	Object[][][] structure =
		{
			{
				{null,null,null,'P','c','P',null,null,null},
				{new ResourceLocation("forge", "blocks/steel"),null,null,new ResourceLocation("minecraft", "slabs"),new ResourceLocation("minecraft", "slabs"),new ResourceLocation("minecraft", "slabs"),null,null,new ResourceLocation("forge", "blocks/steel")},
				{null,LibVulpesBlocks.blockAdvStructureBlock,new ResourceLocation("minecraft", "slabs"),new ResourceLocation("minecraft", "slabs"),new ResourceLocation("minecraft", "slabs"),new ResourceLocation("minecraft", "slabs"),new ResourceLocation("minecraft", "slabs"),LibVulpesBlocks.blockAdvStructureBlock,null},
				{null,new ResourceLocation("minecraft", "slabs"),LibVulpesBlocks.blockAdvStructureBlock,new ResourceLocation("minecraft", "slabs"),new ResourceLocation("minecraft", "slabs"),new ResourceLocation("minecraft", "slabs"),LibVulpesBlocks.blockAdvStructureBlock,new ResourceLocation("minecraft", "slabs"),null},
				{new ResourceLocation("minecraft", "slabs"),new ResourceLocation("minecraft", "slabs"),new ResourceLocation("minecraft", "slabs"),LibVulpesBlocks.blockAdvStructureBlock,LibVulpesBlocks.blockAdvStructureBlock,LibVulpesBlocks.blockAdvStructureBlock,new ResourceLocation("minecraft", "slabs"),new ResourceLocation("minecraft", "slabs"),new ResourceLocation("minecraft", "slabs")},
				{new ResourceLocation("minecraft", "slabs"),new ResourceLocation("minecraft", "slabs"),new ResourceLocation("minecraft", "slabs"),LibVulpesBlocks.blockAdvStructureBlock,LibVulpesBlocks.motors,LibVulpesBlocks.blockAdvStructureBlock,new ResourceLocation("minecraft", "slabs"),new ResourceLocation("minecraft", "slabs"),new ResourceLocation("minecraft", "slabs")},
				{new ResourceLocation("minecraft", "slabs"),new ResourceLocation("minecraft", "slabs"),new ResourceLocation("minecraft", "slabs"),LibVulpesBlocks.blockAdvStructureBlock,LibVulpesBlocks.blockAdvStructureBlock,LibVulpesBlocks.blockAdvStructureBlock,new ResourceLocation("minecraft", "slabs"),new ResourceLocation("minecraft", "slabs"),new ResourceLocation("minecraft", "slabs")},
				{null,new ResourceLocation("minecraft", "slabs"),LibVulpesBlocks.blockAdvStructureBlock,new ResourceLocation("minecraft", "slabs"),new ResourceLocation("minecraft", "slabs"),new ResourceLocation("minecraft", "slabs"),LibVulpesBlocks.blockAdvStructureBlock,new ResourceLocation("minecraft", "slabs"),null},
				{null,LibVulpesBlocks.blockAdvStructureBlock,new ResourceLocation("minecraft", "slabs"),new ResourceLocation("minecraft", "slabs"),new ResourceLocation("minecraft", "slabs"),new ResourceLocation("minecraft", "slabs"),new ResourceLocation("minecraft", "slabs"),LibVulpesBlocks.blockAdvStructureBlock,null},
				{new ResourceLocation("forge", "blocks/steel"),null,null,new ResourceLocation("minecraft", "slabs"),new ResourceLocation("minecraft", "slabs"),new ResourceLocation("minecraft", "slabs"),null,null,new ResourceLocation("forge", "blocks/steel")}
			}
		};

	EntityElevatorCapsule capsule;
	boolean firstTick;
	DimensionBlockPosition dimBlockPos;

	private ModuleText landingPadDisplayText;
	private static final byte SUMMON_PACKET = 2;
	private static final int BUTTON_ID_OFFSET = 5;

	public TileSpaceElevator() {
		super(AdvancedRocketryTileEntityType.TILE_SPACE_ELEVATOR);
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


		World otherPlanet;
		if((otherPlanet = ZUtils.getWorld(dimBlockPos.dimid)) == null) {
			ZUtils.initDimension(dimBlockPos.dimid);
			otherPlanet = ZUtils.getWorld(dimBlockPos.dimid);
		}

		if(otherPlanet != null) {
			TileEntity tile = otherPlanet.getTileEntity(dimBlockPos.pos.getBlockPos());
			if(tile instanceof TileSpaceElevator) {
				((TileSpaceElevator) tile).updateTetherLinkPosition(dimBlockPos, null);
				updateTetherLinkPosition(new DimensionBlockPosition(ZUtils.getDimensionIdentifier(world), new HashedBlockPosition(getPos())), null);
			}
		}
	}

	@Override
	public Object[][][] getStructure() {
		return structure;
	}

	@Override
	public String getMachineName() {
		return getModularInventoryName();
	}

	@Override
	public AxisAlignedBB getRenderBoundingBox() {
		
		return new AxisAlignedBB(pos.add(-5,-300,-5),pos.add(5,3000,5));
	}

	@Override
	public boolean shouldHideBlock(World world, BlockPos pos, BlockState tile) {
		return true;
	}

	@Override
	public List<ModuleBase> getModules(int ID, PlayerEntity player) {
		List<ModuleBase> modules = super.getModules(ID, player);



		if(ID == GuiHandler.guiId.MODULAR.ordinal()) {
			modules.add(new ModuleButton(50, 47, LibVulpes.proxy.getLocalizedString("msg.spaceElevator.button.summon"), this, TextureResources.buttonBuild, 80, 18));
			if (isTetherConnected()) {
				modules.add(new ModuleText(30, 23, LibVulpes.proxy.getLocalizedString("msg.spaceElevator.warning.anchored0"), 0x2d2d2d));
				modules.add(new ModuleText(30, 35, LibVulpes.proxy.getLocalizedString("msg.spaceElevator.warning.anchored1"), 0x2d2d2d));
			} else {
				modules.add(new ModuleText(30, 23, LibVulpes.proxy.getLocalizedString("msg.spaceElevator.warning.unanchored"), 0x2d2d2d));
			}
		}

		return modules;
	}

	public static boolean isDestinationValid(ResourceLocation destinationDimensionID, DimensionBlockPosition pos, HashedBlockPosition myPos, ResourceLocation myDimensionID) {
		if (pos == null || pos.pos == null)
			return false;
		
		
		if (DimensionManager.getInstance().isSpaceDimension(myDimensionID) && SpaceObjectManager.getSpaceManager().getSpaceStationFromBlockCoords(myPos.getBlockPos()) != null) {
			return PlanetaryTravelHelper.isTravelWithinGeostationaryOrbit((SpaceStationObject)SpaceObjectManager.getSpaceManager().getSpaceStationFromBlockCoords(myPos.getBlockPos()), pos.dimid);
		} else if (DimensionManager.getInstance().isSpaceDimension(pos.dimid) && SpaceObjectManager.getSpaceManager().getSpaceStationFromBlockCoords(pos.pos.getBlockPos()) != null) {
			return PlanetaryTravelHelper.isTravelWithinGeostationaryOrbit((SpaceStationObject)SpaceObjectManager.getSpaceManager().getSpaceStationFromBlockCoords(pos.pos.getBlockPos()), myDimensionID);
		}
		return false;
	}

	public static boolean wouldTetherBreakOnConnect(ResourceLocation destinationDimensionID, DimensionBlockPosition pos, HashedBlockPosition myPos, ResourceLocation myDimensionID) {
		SpaceStationObject spaceStation = (DimensionManager.getInstance().isSpaceDimension(myDimensionID)) ? (SpaceStationObject) SpaceObjectManager.getSpaceManager().getSpaceStationFromBlockCoords(myPos.getBlockPos()) : (SpaceStationObject)SpaceObjectManager.getSpaceManager().getSpaceStationFromBlockCoords(pos.pos.getBlockPos());
        return spaceStation != null && spaceStation.wouldStationBreakTether();
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

		int yOffset = (isAnchorOnSpaceStation()) ? - 5 : 1;
		capsule.setPosition(getLandingLocationX(), getPos().getY() + yOffset, getLandingLocationZ());

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
		else if(id == BUTTON_ID_OFFSET) {
			dimBlockPos = null;
			capsule.setDst(null);

			markDirty();
			world.notifyBlockUpdate(pos, world.getBlockState(pos),  world.getBlockState(pos), 3);
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
		return getPos().getX() + facing.getXOffset()*-5 - facing.getZOffset()*2 + 0.5;
	}

	public double getLandingLocationZ() {
		Direction facing = RotatableBlock.getFront(world.getBlockState(getPos()));
		return getPos().getZ() + facing.getXOffset()*2 + facing.getZOffset()*-5 + 0.5;
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
		int yOffset = (isAnchorOnSpaceStation()) ? - 4 : 1;
		capsule.setPosition(capsulePosX, getPos().getY() + yOffset, capsulePosZ);

		capsule.setDst(dimBlockPos);
		capsule.setSourceTile(new DimensionBlockPosition(ZUtils.getDimensionIdentifier(world), new HashedBlockPosition(this.getPos())));

		world.addEntity(capsule);
	}

	@Override
	public boolean onLinkStart(ItemStack item, TileEntity entity,
			PlayerEntity player, World world) {
		ItemLinker.setMasterCoords(item, this.getPos());
		ItemLinker.setDimId(item, ZUtils.getDimensionIdentifier(world));
		if(dimBlockPos != null) {
			player.sendMessage(new TranslationTextComponent("msg.spaceelevator.linkcannotchangeerror"), Util.DUMMY_UUID);
			return false;
		}
		if(!world.isRemote)
			player.sendMessage(new TranslationTextComponent("msg.linker.program"), Util.DUMMY_UUID);
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
				player.sendMessage(new TranslationTextComponent("msg.spaceelevator.samedimensionerror"), Util.DUMMY_UUID);
				return false;
			}


			World world;
			if((world = ZUtils.getWorld(dimPos.dimid)) == null) {
				ZUtils.initDimension(dimPos.dimid);
				world = ZUtils.getWorld(dimPos.dimid);
			}

			if(!isDestinationValid(dimPos.dimid, dimPos, new HashedBlockPosition(getPos()), ZUtils.getDimensionIdentifier(myWorld))) {
				player.sendMessage(new TranslationTextComponent("msg.spaceElevator.linkNotGeostationaryError"), Util.DUMMY_UUID);
				return false;
			}

			if(wouldTetherBreakOnConnect(dimPos.dimid, dimPos, new HashedBlockPosition(getPos()), ZUtils.getDimensionIdentifier(myWorld))) {
				player.sendMessage(new TranslationTextComponent("msg.spaceElevator.tetherWouldBreakError"), Util.DUMMY_UUID);
				return false;
			}

			if(dimBlockPos != null) {
				player.sendMessage(new TranslationTextComponent("msg.spaceElevator.linkCannotChangeError"), Util.DUMMY_UUID);
				return false;
			}

			if(world != null) {
				TileEntity tile = world.getTileEntity(dimPos.pos.getBlockPos());
				if(tile instanceof TileSpaceElevator) {
					updateTetherLinkPosition(new DimensionBlockPosition(ZUtils.getDimensionIdentifier(this.world), new HashedBlockPosition(getPos())), dimPos);
					((TileSpaceElevator) tile).updateTetherLinkPosition(dimPos, new DimensionBlockPosition(ZUtils.getDimensionIdentifier(this.world), new HashedBlockPosition(getPos())));
					player.sendMessage(new TranslationTextComponent("msg.spaceElevator.newDstAdded"), Util.DUMMY_UUID);

					if (capsule != null) {
						capsule.setDst(dimBlockPos);
					}
					markDirty();
					world.notifyBlockUpdate(pos, world.getBlockState(pos),  world.getBlockState(pos), 3);

					return true;
				}
			}

		}

		return false;
	}

	public boolean isAnchorOnSpaceStation() {
		return DimensionManager.getInstance().isSpaceDimension(world);
	}

	public void updateTetherLinkPosition(DimensionBlockPosition myPosition, DimensionBlockPosition dimensionBlockPosition) {
		if (DimensionManager.getInstance().isSpaceDimension(myPosition.dimid) && SpaceObjectManager.getSpaceManager().getSpaceStationFromBlockCoords(myPosition.pos.getBlockPos()) != null) {
			if (dimensionBlockPosition != null) {
				SpaceObjectManager.getSpaceManager().getSpaceStationFromBlockCoords(myPosition.pos.getBlockPos()).setDeltaRotation(0, Direction.EAST);
				SpaceObjectManager.getSpaceManager().getSpaceStationFromBlockCoords(myPosition.pos.getBlockPos()).setDeltaRotation( 0, Direction.UP);
				SpaceObjectManager.getSpaceManager().getSpaceStationFromBlockCoords(myPosition.pos.getBlockPos()).setDeltaRotation( 0, Direction.NORTH);
			}
			SpaceObjectManager.getSpaceManager().getSpaceStationFromBlockCoords(myPosition.pos.getBlockPos()).setIsAnchored( (dimensionBlockPosition == null) ? false : true);
		}
		dimBlockPos = dimensionBlockPosition;
	}

	public boolean isTetherConnected() {
		return dimBlockPos != null;
	}

	@Override
	public String getModularInventoryName() { return "block.advancedrocketry.spaceelevatorcontroller"; }

	@Override
	public boolean isUsableByPlayer(PlayerEntity player) {
		return true;
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
	public void read(BlockState state, CompoundNBT nbt) {
		super.read(state, nbt);
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
