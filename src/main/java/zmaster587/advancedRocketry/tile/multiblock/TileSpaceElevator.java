package zmaster587.advancedRocketry.tile.multiblock;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import zmaster587.advancedRocketry.api.ARConfiguration;
import zmaster587.advancedRocketry.entity.EntityElevatorCapsule;
import zmaster587.advancedRocketry.stations.SpaceObjectManager;
import zmaster587.advancedRocketry.stations.SpaceStationObject;
import zmaster587.advancedRocketry.util.DimensionBlockPosition;
import zmaster587.advancedRocketry.util.PlanetaryTravelHelper;
import zmaster587.libVulpes.LibVulpes;
import zmaster587.libVulpes.api.LibVulpesBlocks;
import zmaster587.libVulpes.block.RotatableBlock;
import zmaster587.libVulpes.interfaces.ILinkableTile;
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

import javax.annotation.Nonnull;
import java.util.List;

public class TileSpaceElevator extends TileMultiPowerConsumer implements IModularInventory, ILinkableTile, ITickable {

	Object[][][] structure =
		{
			{
				{null,null,null,'P','c','P',null,null,null},
				{"blockSteel",null,null,"slab","slab","slab",null,null,"blockSteel"},
				{null,LibVulpesBlocks.blockAdvStructureBlock,"slab","slab","slab","slab","slab",LibVulpesBlocks.blockAdvStructureBlock,null},
				{null,"slab",LibVulpesBlocks.blockAdvStructureBlock,"slab","slab","slab",LibVulpesBlocks.blockAdvStructureBlock,"slab",null},
				{"slab","slab","slab",LibVulpesBlocks.blockAdvStructureBlock,LibVulpesBlocks.blockAdvStructureBlock,LibVulpesBlocks.blockAdvStructureBlock,"slab","slab","slab"},
				{"slab","slab","slab",LibVulpesBlocks.blockAdvStructureBlock,LibVulpesBlocks.motors,LibVulpesBlocks.blockAdvStructureBlock,"slab","slab","slab"},
				{"slab","slab","slab",LibVulpesBlocks.blockAdvStructureBlock,LibVulpesBlocks.blockAdvStructureBlock,LibVulpesBlocks.blockAdvStructureBlock,"slab","slab","slab"},
				{null,"slab",LibVulpesBlocks.blockAdvStructureBlock,"slab","slab","slab",LibVulpesBlocks.blockAdvStructureBlock,"slab",null},
				{null,LibVulpesBlocks.blockAdvStructureBlock,"slab","slab","slab","slab","slab",LibVulpesBlocks.blockAdvStructureBlock,null},
				{"blockSteel",null,null,"slab","slab","slab",null,null,"blockSteel"}
			}
		};

	EntityElevatorCapsule capsule;
	boolean firstTick;
	private boolean isTetherConnected;
	DimensionBlockPosition dimBlockPos;

	private ModuleText landingPadDisplayText;
	private static final byte SUMMON_PACKET = 2;
	private static final int BUTTON_ID_OFFSET = 5;

	public TileSpaceElevator() {
		super();
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
		
		Entity entity = getCapsuleOnLine();
		
		if(entity != null)
			entity.setDead();


		World otherPlanet;
		if((otherPlanet = DimensionManager.getWorld(dimBlockPos.dimid)) == null) {
			DimensionManager.initDimension(dimBlockPos.dimid);
			otherPlanet = DimensionManager.getWorld(dimBlockPos.dimid);
		}

		if(otherPlanet != null) {
			TileEntity tile = otherPlanet.getTileEntity(dimBlockPos.pos.getBlockPos());
			if(tile instanceof TileSpaceElevator) {
				((TileSpaceElevator) tile).updateTetherLinkPosition(dimBlockPos, null);
				updateTetherLinkPosition(new DimensionBlockPosition(world.provider.getDimension(), new HashedBlockPosition(getPos())), null);
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
	public boolean shouldHideBlock(World world, BlockPos pos, IBlockState tile) {
		return true;
	}

	@Override
	public List<ModuleBase> getModules(int ID, EntityPlayer player) {
		List<ModuleBase> modules = super.getModules(ID, player);



		if(ID == GuiHandler.guiId.MODULAR.ordinal()) {
			modules.add(new ModuleButton(50, 47, 1, LibVulpes.proxy.getLocalizedString("msg.spaceElevator.button.summon"), this, TextureResources.buttonBuild, 80, 18));
			if (isTetherConnected()) {
				modules.add(new ModuleText(30, 23, LibVulpes.proxy.getLocalizedString("msg.spaceElevator.warning.anchored0"), 0x2d2d2d));
				modules.add(new ModuleText(30, 35, LibVulpes.proxy.getLocalizedString("msg.spaceElevator.warning.anchored1"), 0x2d2d2d));
			} else {
				modules.add(new ModuleText(30, 32, LibVulpes.proxy.getLocalizedString("msg.spaceElevator.warning.unanchored"), 0x2d2d2d));
			}

		}

		return modules;
	}

	public static boolean isDestinationValid(int destinationDimensionID, DimensionBlockPosition pos, HashedBlockPosition myPos, int myDimensionID) {
		if (pos == null || pos.pos == null)
			return false;
		if (myDimensionID == ARConfiguration.getCurrentConfig().spaceDimId && SpaceObjectManager.getSpaceManager().getSpaceStationFromBlockCoords(myPos.getBlockPos()) != null) {
			return PlanetaryTravelHelper.isTravelWithinGeostationaryOrbit((SpaceStationObject)SpaceObjectManager.getSpaceManager().getSpaceStationFromBlockCoords(myPos.getBlockPos()), pos.dimid);
		} else if (pos.dimid == ARConfiguration.getCurrentConfig().spaceDimId && SpaceObjectManager.getSpaceManager().getSpaceStationFromBlockCoords(pos.pos.getBlockPos()) != null) {
			return PlanetaryTravelHelper.isTravelWithinGeostationaryOrbit((SpaceStationObject)SpaceObjectManager.getSpaceManager().getSpaceStationFromBlockCoords(pos.pos.getBlockPos()), myDimensionID);
		}
		return false;
	}

	public static boolean wouldTetherBreakOnConnect(int destinationDimensionID, DimensionBlockPosition pos, HashedBlockPosition myPos, int myDimensionID) {
		SpaceStationObject spaceStation = (myDimensionID == ARConfiguration.getCurrentConfig().spaceDimId) ? (SpaceStationObject) SpaceObjectManager.getSpaceManager().getSpaceStationFromBlockCoords(myPos.getBlockPos()) : (SpaceStationObject)SpaceObjectManager.getSpaceManager().getSpaceStationFromBlockCoords(pos.pos.getBlockPos());
        return spaceStation != null && spaceStation.wouldStationBreakTether();
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

		int yOffset = (isAnchorOnSpaceStation()) ? - 5 : 1;
		capsule.setPosition(getLandingLocationX(), getPos().getY() + yOffset, getLandingLocationZ());

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
	public void useNetworkData(EntityPlayer player, Side side, byte id,
			NBTTagCompound nbt) {

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
		return getPos().getX() + facing.getFrontOffsetX()*-5 - facing.getFrontOffsetZ()*2 + 0.5;
	}

	public double getLandingLocationZ() {
		EnumFacing facing = RotatableBlock.getFront(world.getBlockState(getPos()));
		return getPos().getZ() + facing.getFrontOffsetX()*2 + facing.getFrontOffsetZ()*-5 + 0.5;
	}


	public void summonCapsule() {
		//Don't spawn a new capsule if one exists
		if(getCapsuleOnLine() != null || !isTetherConnected())
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
		int yOffset = (isAnchorOnSpaceStation()) ? - 4 : 1;
		capsule.setPosition(capsulePosX, getPos().getY() + yOffset, capsulePosZ);

		capsule.setDst(dimBlockPos);
		capsule.setSourceTile(new DimensionBlockPosition(world.provider.getDimension(), new HashedBlockPosition(this.getPos())));

		world.spawnEntity(capsule);
	}

	@Override
	public boolean onLinkStart(@Nonnull ItemStack item, TileEntity entity,
							   EntityPlayer player, World world) {
		ItemLinker.setMasterCoords(item, this.getPos());
		ItemLinker.setDimId(item, world.provider.getDimension());
		if(dimBlockPos != null) {
			player.sendMessage(new TextComponentTranslation("msg.spaceElevator.linkCannotChangeError"));
			return false;
		}
		if(!world.isRemote)
			player.sendMessage(new TextComponentTranslation("msg.linker.program"));
		return true;
	}

	@Override
	public boolean onLinkComplete(@Nonnull ItemStack item, TileEntity entity,
			EntityPlayer player, World myWorld) {

		if(!myWorld.isRemote) {

			int dimid = ItemLinker.getDimId(item);
			BlockPos pos = ItemLinker.getMasterCoords(item);

			DimensionBlockPosition dimPos = new DimensionBlockPosition(dimid, new HashedBlockPosition(pos));

			if(dimPos.dimid == world.provider.getDimension())
			{
				player.sendMessage(new TextComponentTranslation("msg.spaceElevator.sameDimensionError"));
				return false;
			}


			World world;
			if((world = DimensionManager.getWorld(dimPos.dimid)) == null) {
				DimensionManager.initDimension(dimPos.dimid);
				world = DimensionManager.getWorld(dimPos.dimid);
			}

			if(!isDestinationValid(dimPos.dimid, dimPos, new HashedBlockPosition(getPos()), myWorld.provider.getDimension())) {
				player.sendMessage(new TextComponentTranslation("msg.spaceElevator.linkNotGeostationaryError"));
				return false;
			}

			if(wouldTetherBreakOnConnect(dimPos.dimid, dimPos, new HashedBlockPosition(getPos()), myWorld.provider.getDimension())) {
				player.sendMessage(new TextComponentTranslation("msg.spaceElevator.tetherWouldBreakError"));
				return false;
			}

			if(dimBlockPos != null) {
				player.sendMessage(new TextComponentTranslation("msg.spaceElevator.linkCannotChangeError"));
				return false;
			}

			if(world != null) {
				TileEntity tile = world.getTileEntity(dimPos.pos.getBlockPos());
				if(tile instanceof TileSpaceElevator) {
					updateTetherLinkPosition(new DimensionBlockPosition(this.world.provider.getDimension(), new HashedBlockPosition(getPos())), dimPos);
					((TileSpaceElevator) tile).updateTetherLinkPosition(dimPos, new DimensionBlockPosition(this.world.provider.getDimension(), new HashedBlockPosition(getPos())));
					player.sendMessage(new TextComponentTranslation("msg.spaceElevator.newDstAdded"));

					if (capsule != null) {
						capsule.setDst(dimBlockPos);
					}
					this.markDirty();
					this.world.notifyBlockUpdate(pos, world.getBlockState(pos),  world.getBlockState(pos), 3);
					this.isTetherConnected = true;
					setMachineRunning(true);
					setMachineEnabled(true);

					return true;
				}
			}

		}

		return false;
	}

	public boolean isAnchorOnSpaceStation() {
		return world.provider.getDimension() == ARConfiguration.getCurrentConfig().spaceDimId;
	}

	public void updateTetherLinkPosition(DimensionBlockPosition myPosition, DimensionBlockPosition dimensionBlockPosition) {
		if (myPosition.dimid == ARConfiguration.getCurrentConfig().spaceDimId && SpaceObjectManager.getSpaceManager().getSpaceStationFromBlockCoords(myPosition.pos.getBlockPos()) != null) {
			if (dimensionBlockPosition != null) {
				SpaceObjectManager.getSpaceManager().getSpaceStationFromBlockCoords(myPosition.pos.getBlockPos()).setDeltaRotation(0, EnumFacing.EAST);
				SpaceObjectManager.getSpaceManager().getSpaceStationFromBlockCoords(myPosition.pos.getBlockPos()).setDeltaRotation( 0, EnumFacing.UP);
				SpaceObjectManager.getSpaceManager().getSpaceStationFromBlockCoords(myPosition.pos.getBlockPos()).setDeltaRotation( 0, EnumFacing.NORTH);
			}
			SpaceObjectManager.getSpaceManager().getSpaceStationFromBlockCoords(myPosition.pos.getBlockPos()).setIsAnchored(dimensionBlockPosition != null);
		}
		dimBlockPos = dimensionBlockPosition;
	}

	public boolean isTetherConnected() {
		return isTetherConnected;
	}

	@Override
	public String getModularInventoryName() { return "tile.spaceElevatorController.name"; }

	@Override
	public boolean isUsableByPlayer(EntityPlayer player) {
		return true;
	}

	@Override
	public void writeNetworkData(NBTTagCompound nbt) {
		if(dimBlockPos != null) {
			nbt.setInteger("dstDimId", dimBlockPos.dimid);
			nbt.setIntArray("dstPos", new int[] { dimBlockPos.pos.x, dimBlockPos.pos.y, dimBlockPos.pos.z });
			nbt.setBoolean("tether", isTetherConnected);
		} else

		super.writeNetworkData(nbt);
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt) {
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
		isTetherConnected = nbt.getBoolean("tether");

		landingPadDisplayText.setText(dimBlockPos != null ? dimBlockPos.toString() : LibVulpes.proxy.getLocalizedString("msg.label.noneSelected"));
	}
}
