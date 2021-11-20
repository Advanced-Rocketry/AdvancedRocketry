package zmaster587.advancedRocketry.tile.station;

import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Util;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.api.distmarker.Dist;
import zmaster587.advancedRocketry.api.ARConfiguration;
import zmaster587.advancedRocketry.api.AdvancedRocketryTileEntityType;
import zmaster587.advancedRocketry.api.Constants;
import zmaster587.advancedRocketry.api.EntityRocketBase;
import zmaster587.advancedRocketry.api.IInfrastructure;
import zmaster587.advancedRocketry.api.RocketEvent.RocketDismantleEvent;
import zmaster587.advancedRocketry.api.RocketEvent.RocketLandedEvent;
import zmaster587.advancedRocketry.api.RocketEvent.RocketPreLaunchEvent;
import zmaster587.advancedRocketry.api.stations.ISpaceObject;
import zmaster587.advancedRocketry.dimension.DimensionManager;
import zmaster587.advancedRocketry.entity.EntityRocket;
import zmaster587.advancedRocketry.stations.SpaceObjectManager;
import zmaster587.advancedRocketry.stations.SpaceStationObject;
import zmaster587.advancedRocketry.util.StationLandingLocation;
import zmaster587.libVulpes.LibVulpes;
import zmaster587.libVulpes.api.LibVulpesItems;
import zmaster587.libVulpes.interfaces.ILinkableTile;
import zmaster587.libVulpes.inventory.modules.IGuiCallback;
import zmaster587.libVulpes.inventory.modules.ModuleBase;
import zmaster587.libVulpes.inventory.modules.ModuleText;
import zmaster587.libVulpes.inventory.modules.ModuleTextBox;
import zmaster587.libVulpes.items.ItemLinker;
import zmaster587.libVulpes.network.PacketHandler;
import zmaster587.libVulpes.network.PacketMachine;
import zmaster587.libVulpes.tile.IMultiblock;
import zmaster587.libVulpes.tile.multiblock.hatch.TileInventoryHatch;
import zmaster587.libVulpes.util.HashedBlockPosition;
import zmaster587.libVulpes.util.INetworkMachine;
import zmaster587.libVulpes.util.ZUtils;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public class TileLandingPad extends TileInventoryHatch implements ILinkableTile, IGuiCallback, INetworkMachine {

	private List<HashedBlockPosition> blockPos;
	ModuleTextBox moduleNameTextbox;
	String name;

	public TileLandingPad() {
		super(AdvancedRocketryTileEntityType.TILE_LANDING_PAD , 1);
		inventory.setCanInsertSlot(0, true);
		inventory.setCanExtractSlot(0, true);
		MinecraftForge.EVENT_BUS.register(this);
		blockPos = new LinkedList<>();
		moduleNameTextbox = new ModuleTextBox(this, 40, 30, 60, 12, 9);
		name = "";
	}
	@Override
	public void remove() {
		super.remove();
		MinecraftForge.EVENT_BUS.unregister(this);
		for(HashedBlockPosition pos : blockPos) {
			TileEntity tile = world.getTileEntity(pos.getBlockPos());
			if(tile instanceof IMultiblock)
				((IMultiblock)tile).setIncomplete();
		}
	}

	@Override
	public void onModuleUpdated(ModuleBase module) {
		if(module == moduleNameTextbox) {
			name =  moduleNameTextbox.getText();
			PacketHandler.sendToServer(new PacketMachine(this, (byte)0));
		}
	}

	@Override
	public List<ModuleBase> getModules(int ID, PlayerEntity player) {
		List<ModuleBase> modules = super.getModules(ID, player);

		modules.add(new ModuleText(40, 20, LibVulpes.proxy.getLocalizedString("msg.label.name") + ":", 0x2f2f2f));
		modules.add(moduleNameTextbox);
		return modules;
	}

	@Override
	public void onChunkUnloaded() {
		super.onChunkUnloaded();
		MinecraftForge.EVENT_BUS.unregister(this);
	}

	@Override
	@ParametersAreNonnullByDefault
	public boolean onLinkStart(ItemStack item, TileEntity entity,
			PlayerEntity player, World world) {
		ItemLinker.setMasterCoords(item, getPos());
		ItemLinker.setDimId(item, ZUtils.getDimensionIdentifier(world));
		return true;
	}

	@Override
	public String getModularInventoryName() {
		return "block.advancedrocketry.landingpad";
	}

	@Override
	@ParametersAreNonnullByDefault
	public boolean onLinkComplete(ItemStack item, TileEntity entity,
			PlayerEntity player, World world) {

		TileEntity tile = world.getTileEntity(ItemLinker.getMasterCoords(item));

		if(tile instanceof IInfrastructure) {
			HashedBlockPosition pos = new HashedBlockPosition(tile.getPos());
			if(!blockPos.contains(pos)) {
				blockPos.add(pos);
			}

			AxisAlignedBB bbCache =  new AxisAlignedBB(this.getPos().add(-1,0,-1), this.getPos().add(1,2,1));


			List<EntityRocketBase> rockets = world.getEntitiesWithinAABB(EntityRocketBase.class, bbCache);
			for(EntityRocketBase rocket : rockets) {
				rocket.linkInfrastructure((IInfrastructure) tile);
			}


			if(!world.isRemote) {
				player.sendMessage(new TranslationTextComponent("msg.linker.success"), Util.DUMMY_UUID);

				if(tile instanceof IMultiblock)
					((IMultiblock)tile).setMasterBlock(getPos());
			}

			ItemLinker.resetPosition(item);
			return true;
		}
		return false;
	}

	@SubscribeEvent
	public void onRocketLand(RocketLandedEvent event) {
		EntityRocketBase rocket = (EntityRocketBase)event.getEntity();

		AxisAlignedBB bbCache =  new AxisAlignedBB(this.getPos().add(-1,0,-1), this.getPos().add(1,2,1));

		if(bbCache.intersects(rocket.getBoundingBox())) {
			if(!event.world.isRemote)
				for(IInfrastructure infrastructure : getConnectedInfrastructure()) {
					rocket.linkInfrastructure(infrastructure);
				}
			ItemStack stack = getStackInSlot(0);
			if(stack.getItem() == LibVulpesItems.itemLinker && ItemLinker.getDimId(stack) != Constants.INVALID_PLANET &&
					event.getEntity() instanceof EntityRocket) {
				((EntityRocket)rocket).setOverriddenCoords(ItemLinker.getDimId(stack), 
						ItemLinker.getMasterX(stack) + 0.5f, ARConfiguration.getCurrentConfig().orbit.get(), ItemLinker.getMasterZ(stack) + 0.5f);
			}
		}
	}

	@SubscribeEvent
	public void onRocketLaunch(RocketPreLaunchEvent event) {

		ItemStack stack = getStackInSlot(0);
		if(stack.getItem() == LibVulpesItems.itemLinker && ItemLinker.getDimId(stack) != Constants.INVALID_PLANET) {

			EntityRocketBase rocket = (EntityRocketBase)event.getEntity();
			AxisAlignedBB bbCache =  new AxisAlignedBB(this.getPos().add(-1,0,-1), this.getPos().add(1,2,1));

			if(bbCache.intersects(rocket.getBoundingBox())) {
				if(event.getEntity() instanceof EntityRocket) {
					((EntityRocket)rocket).setOverriddenCoords(ItemLinker.getDimId(stack), 
							ItemLinker.getMasterX(stack) + 0.5f, ARConfiguration.getCurrentConfig().orbit.get(), ItemLinker.getMasterZ(stack) + 0.5f);
				}
			}
		}
	}

	@SubscribeEvent
	public void onRocketDismantle(RocketDismantleEvent event) {
		if(!world.isRemote && DimensionManager.spaceId.equals(ZUtils.getDimensionIdentifier(world))) {

			EntityRocketBase rocket = (EntityRocketBase)event.getEntity();
			AxisAlignedBB bbCache =  new AxisAlignedBB(this.getPos().add(-1,0,-1), this.getPos().add(1,2,1));

			if(bbCache.intersects(rocket.getBoundingBox())) {

				ISpaceObject spaceObj = SpaceObjectManager.getSpaceManager().getSpaceStationFromBlockCoords(pos);

				if(spaceObj instanceof SpaceStationObject) {
					((SpaceStationObject)spaceObj).setPadStatus(pos, false);
				}
			}
		}
	}

	public void registerTileWithStation(World world, BlockPos pos) {
		if(!world.isRemote && DimensionManager.spaceId.equals(ZUtils.getDimensionIdentifier(world))) {
			ISpaceObject spaceObj = SpaceObjectManager.getSpaceManager().getSpaceStationFromBlockCoords(pos);

			if(spaceObj instanceof SpaceStationObject) {
				((SpaceStationObject)spaceObj).addLandingPad(pos, name);

				AxisAlignedBB bbCache =  new AxisAlignedBB(this.getPos().add(-1,0,-1), this.getPos().add(1,2,1));
				List<EntityRocketBase> rockets = world.getEntitiesWithinAABB(EntityRocketBase.class, bbCache);

				if(!rockets.isEmpty())
					((SpaceStationObject)spaceObj).setPadStatus(pos, true);
			}
		}
	}

	public void setAllowAutoLand(World world, BlockPos pos, boolean allow) {
		if(!world.isRemote && DimensionManager.spaceId.equals(ZUtils.getDimensionIdentifier(world))) {
			ISpaceObject spaceObj = SpaceObjectManager.getSpaceManager().getSpaceStationFromBlockCoords(pos);

			if(spaceObj instanceof SpaceStationObject) {
				((SpaceStationObject)spaceObj).setLandingPadAutoLandStatus(pos, allow);
			}
		}
	}

	public void unregisterTileWithStation(World world, BlockPos pos) {
		if(!world.isRemote && DimensionManager.spaceId.equals(ZUtils.getDimensionIdentifier(world))) {
			ISpaceObject spaceObj = SpaceObjectManager.getSpaceManager().getSpaceStationFromBlockCoords(pos);
			if(spaceObj instanceof SpaceStationObject)
				((SpaceStationObject)spaceObj).removeLandingPad(pos);
		}
	}

	@Override
	public void setInventorySlotContents(int slot, @Nonnull ItemStack stack) {
		super.setInventorySlotContents(slot, stack);

		if(!stack.isEmpty()) {
			setAllowAutoLand(world, pos,false);

			AxisAlignedBB bbCache =  new AxisAlignedBB(this.getPos().add(-1,0,-1), this.getPos().add(1,2,1));
			List<EntityRocketBase> rockets = world.getEntitiesWithinAABB(EntityRocketBase.class, bbCache);

			for(EntityRocketBase rocket : rockets) {
				if(rocket instanceof EntityRocket) {
					if(stack.getItem() == LibVulpesItems.itemLinker && ItemLinker.getDimId(stack) != Constants.INVALID_PLANET) {
						((EntityRocket)rocket).setOverriddenCoords(ItemLinker.getDimId(stack), 
								ItemLinker.getMasterX(stack) + 0.5f, ARConfiguration.getCurrentConfig().orbit.get(), ItemLinker.getMasterZ(stack) + 0.5f);
					}
					else
						((EntityRocket)rocket).setOverriddenCoords(Constants.INVALID_PLANET, 0,0,0);
				}
			}
		}
		else {
			setAllowAutoLand(world, pos, true);

			AxisAlignedBB bbCache =  new AxisAlignedBB(this.getPos().add(-1,0,-1), this.getPos().add(1,2,1));
			List<EntityRocketBase> rockets = world.getEntitiesWithinAABB(EntityRocketBase.class, bbCache);

			for(EntityRocketBase rocket : rockets) {
				if(rocket instanceof EntityRocket) {
					((EntityRocket)rocket).setOverriddenCoords(Constants.INVALID_PLANET, 0,0,0);
				}
			}
		}

	}

	@Override
	public boolean isItemValidForSlot(int slot, @Nonnull ItemStack stack) {
		return stack.getItem() == LibVulpesItems.itemLinker;
	}

	public List<IInfrastructure> getConnectedInfrastructure() {
		List<IInfrastructure> infrastructure = new LinkedList<>();

		Iterator<HashedBlockPosition> iter = blockPos.iterator();

		while(iter.hasNext()) {
			HashedBlockPosition position = iter.next();
			TileEntity tile = world.getTileEntity(position.getBlockPos());
			if(tile instanceof IInfrastructure) {
				infrastructure.add((IInfrastructure)tile);
			}
			else
				iter.remove();
		}

		return infrastructure;
	}

	@Override
	public void writeDataToNetwork(PacketBuffer out, byte id) {
		if(id == 0) {
			PacketBuffer buff = new PacketBuffer(out);
			buff.writeInt(name.length());
			buff.writeString(name);
		}
	}

	@Override
	public void readDataFromNetwork(PacketBuffer in, byte packetId,
			CompoundNBT nbt) {
		int len = in.readInt();
		PacketBuffer buff = new PacketBuffer(in);
		nbt.putString("id", buff.readString(len));
	}

	@Override
	public SUpdateTileEntityPacket getUpdatePacket() {
		return new SUpdateTileEntityPacket(pos, 0, write(new CompoundNBT()));
	}

	@Override
	public void onDataPacket(NetworkManager net, SUpdateTileEntityPacket pkt) {
		super.onDataPacket(net, pkt);
		read(getBlockState(), pkt.getNbtCompound());
		moduleNameTextbox.setText(name);
	}

	@Override
	public CompoundNBT getUpdateTag() {
		return write(new CompoundNBT());
	}
	
	@Override
	public void handleUpdateTag(BlockState state, CompoundNBT tag) {
		super.handleUpdateTag(state, tag);
		moduleNameTextbox.setText(name);
	}


	@Override
	public void useNetworkData(PlayerEntity player, Dist side, byte id,
			CompoundNBT nbt) {
		if(id == 0) {
			name = nbt.getString("id");
			if(!world.isRemote && DimensionManager.spaceId.equals(ZUtils.getDimensionIdentifier(world))) {
				ISpaceObject spaceObj = SpaceObjectManager.getSpaceManager().getSpaceStationFromBlockCoords(pos);

				if(spaceObj instanceof SpaceStationObject) {
					StationLandingLocation loc = ((SpaceStationObject)spaceObj).getPadAtLocation(new HashedBlockPosition(pos));
					if(loc != null)
						((SpaceStationObject)spaceObj).setPadName(this.world, new HashedBlockPosition(pos), name);
					else
						((SpaceStationObject)spaceObj).addLandingPad(pos, name);
				}
			}
		}
		markDirty();
		world.notifyBlockUpdate(pos, world.getBlockState(pos),  world.getBlockState(pos), 3);
	}

	@Override
	public void read(BlockState state, CompoundNBT nbt) {
		super.read(state, nbt);
		blockPos.clear();
		if(nbt.contains("infrastructureLocations")) {
			int[] array = nbt.getIntArray("infrastructureLocations");

			for(int counter = 0; counter < array.length; counter += 3) {
				blockPos.add(new HashedBlockPosition(array[counter], array[counter+1], array[counter+2]));
			}
		}
		name = nbt.getString("name");
	}

	@Override
	public CompoundNBT write(CompoundNBT nbt) {
		super.write(nbt);

		if(!blockPos.isEmpty()) {
			int[] array = new int[blockPos.size()*3];
			int counter = 0;
			for(HashedBlockPosition pos : blockPos) {
				array[counter] = pos.x;
				array[counter+1] = pos.y;
				array[counter+2] = pos.z;
				counter += 3;
			}

			nbt.putIntArray("infrastructureLocations", array);
		}

		if(name != null && !name.isEmpty())
			nbt.putString("name", name);

		return nbt;
	}

}
