package zmaster587.advancedRocketry.tile.station;

import io.netty.buffer.ByteBuf;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import zmaster587.advancedRocketry.api.Configuration;
import zmaster587.advancedRocketry.api.EntityRocketBase;
import zmaster587.advancedRocketry.api.IInfrastructure;
import zmaster587.advancedRocketry.api.RocketEvent.RocketDismantleEvent;
import zmaster587.advancedRocketry.api.RocketEvent.RocketLandedEvent;
import zmaster587.advancedRocketry.api.RocketEvent.RocketLaunchEvent;
import zmaster587.advancedRocketry.api.RocketEvent.RocketPreLaunchEvent;
import zmaster587.advancedRocketry.api.stations.ISpaceObject;
import zmaster587.advancedRocketry.entity.EntityRocket;
import zmaster587.advancedRocketry.stations.SpaceObject;
import zmaster587.advancedRocketry.stations.SpaceObjectManager;
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
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;

public class TileLandingPad extends TileInventoryHatch implements ILinkableTile, IGuiCallback, INetworkMachine {

	private List<HashedBlockPosition> blockPos;
	ModuleTextBox moduleNameTextbox;
	String name;

	public TileLandingPad() {
		super(1);
		MinecraftForge.EVENT_BUS.register(this);
		blockPos = new LinkedList<HashedBlockPosition>();
		moduleNameTextbox = new ModuleTextBox(this, 40, 30, 60, 12, 9);
		name = "";
	}
	@Override
	public void invalidate() {
		super.invalidate();
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
	public List<ModuleBase> getModules(int ID, EntityPlayer player) {
		List<ModuleBase> modules = super.getModules(ID, player);

		modules.add(new ModuleText(40, 20, LibVulpes.proxy.getLocalizedString("msg.label.name") + ":", 0x2f2f2f));
		modules.add(moduleNameTextbox);
		return modules;
	}

	@Override
	public void onChunkUnload() {
		super.onChunkUnload();
		MinecraftForge.EVENT_BUS.unregister(this);
	}

	@Override
	public boolean onLinkStart(ItemStack item, TileEntity entity,
			EntityPlayer player, World world) {
		ItemLinker.setMasterCoords(item, getPos());
		ItemLinker.setDimId(item, world.provider.getDimension());
		return true;
	}

	@Override
	public String getModularInventoryName() {
		return "tile.landingPad.name";
	}

	@Override
	public boolean onLinkComplete(ItemStack item, TileEntity entity,
			EntityPlayer player, World world) {

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
				player.sendMessage(new TextComponentString(LibVulpes.proxy.getLocalizedString("msg.linker.success")));

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

		if(bbCache.intersects(rocket.getEntityBoundingBox())) {
			if(!world.isRemote)
				for(IInfrastructure infrastructure : getConnectedInfrastructure()) {
					rocket.linkInfrastructure(infrastructure);
				}
			ItemStack stack = getStackInSlot(0);
			if(stack.getItem() == LibVulpesItems.itemLinker && ItemLinker.getDimId(stack) != -1 &&
					event.getEntity() instanceof EntityRocket) {
				((EntityRocket)rocket).setOverriddenCoords(ItemLinker.getDimId(stack), 
						ItemLinker.getMasterX(stack) + 0.5f, Configuration.orbit, ItemLinker.getMasterZ(stack) + 0.5f);
			}
		}
	}

	@SubscribeEvent
	public void onRocketLaunch(RocketPreLaunchEvent event) {

		ItemStack stack = getStackInSlot(0);
		if(stack.getItem() == LibVulpesItems.itemLinker && ItemLinker.getDimId(stack) != -1) {

			EntityRocketBase rocket = (EntityRocketBase)event.getEntity();
			AxisAlignedBB bbCache =  new AxisAlignedBB(this.getPos().add(-1,0,-1), this.getPos().add(1,2,1));

			if(bbCache.intersects(rocket.getEntityBoundingBox())) {
				if(event.getEntity() instanceof EntityRocket) {
					((EntityRocket)rocket).setOverriddenCoords(ItemLinker.getDimId(stack), 
							ItemLinker.getMasterX(stack) + 0.5f, Configuration.orbit, ItemLinker.getMasterZ(stack) + 0.5f);
				}
			}
		}
	}

	@SubscribeEvent
	public void onRocketDismantle(RocketDismantleEvent event) {
		if(!world.isRemote && world.provider.getDimension() == Configuration.spaceDimId) {

			EntityRocketBase rocket = (EntityRocketBase)event.getEntity();
			AxisAlignedBB bbCache =  new AxisAlignedBB(this.getPos().add(-1,0,-1), this.getPos().add(1,2,1));

			if(bbCache.intersects(rocket.getEntityBoundingBox())) {

				ISpaceObject spaceObj = SpaceObjectManager.getSpaceManager().getSpaceStationFromBlockCoords(pos);

				if(spaceObj instanceof SpaceObject) {
					((SpaceObject)spaceObj).setPadStatus(pos, false);
				}
			}
		}
	}

	public void registerTileWithStation(World world, BlockPos pos) {
		if(!world.isRemote && world.provider.getDimension() == Configuration.spaceDimId) {
			ISpaceObject spaceObj = SpaceObjectManager.getSpaceManager().getSpaceStationFromBlockCoords(pos);

			if(spaceObj instanceof SpaceObject) {
				((SpaceObject)spaceObj).addLandingPad(pos, name);

				AxisAlignedBB bbCache =  new AxisAlignedBB(this.getPos().add(-1,0,-1), this.getPos().add(1,2,1));
				List<EntityRocketBase> rockets = world.getEntitiesWithinAABB(EntityRocketBase.class, bbCache);

				if(rockets != null && !rockets.isEmpty())
					((SpaceObject)spaceObj).setPadStatus(pos, true);
			}
		}
	}

	public void setAllowAutoLand(World world, BlockPos pos, boolean allow) {
		if(!world.isRemote && world.provider.getDimension() == Configuration.spaceDimId) {
			ISpaceObject spaceObj = SpaceObjectManager.getSpaceManager().getSpaceStationFromBlockCoords(pos);

			if(spaceObj instanceof SpaceObject) {
				((SpaceObject)spaceObj).setLandingPadAutoLandStatus(pos, allow);
			}
		}
	}

	public void unregisterTileWithStation(World world, BlockPos pos) {
		if(!world.isRemote && world.provider.getDimension() == Configuration.spaceDimId) {
			ISpaceObject spaceObj = SpaceObjectManager.getSpaceManager().getSpaceStationFromBlockCoords(pos);
			if(spaceObj instanceof SpaceObject)
				((SpaceObject)spaceObj).removeLandingPad(pos);
		}
	}

	@Override
	public void setInventorySlotContents(int slot, ItemStack stack) {
		super.setInventorySlotContents(slot, stack);

		if(!stack.isEmpty()) {
			setAllowAutoLand(world, pos,false);

			AxisAlignedBB bbCache =  new AxisAlignedBB(this.getPos().add(-1,0,-1), this.getPos().add(1,2,1));
			List<EntityRocketBase> rockets = world.getEntitiesWithinAABB(EntityRocketBase.class, bbCache);

			for(EntityRocketBase rocket : rockets) {
				if(rocket instanceof EntityRocket) {
					if(stack.getItem() == LibVulpesItems.itemLinker && ItemLinker.getDimId(stack) != -1) {
						((EntityRocket)rocket).setOverriddenCoords(ItemLinker.getDimId(stack), 
								ItemLinker.getMasterX(stack) + 0.5f, Configuration.orbit, ItemLinker.getMasterZ(stack) + 0.5f);
					}
					else
						((EntityRocket)rocket).setOverriddenCoords(-1, 0,0,0);
				}
			}
		}
		else {
			setAllowAutoLand(world, pos, true);

			AxisAlignedBB bbCache =  new AxisAlignedBB(this.getPos().add(-1,0,-1), this.getPos().add(1,2,1));
			List<EntityRocketBase> rockets = world.getEntitiesWithinAABB(EntityRocketBase.class, bbCache);

			for(EntityRocketBase rocket : rockets) {
				if(rocket instanceof EntityRocket) {
					((EntityRocket)rocket).setOverriddenCoords(-1, 0,0,0);
				}
			}
		}

	}

	@Override
	public boolean isItemValidForSlot(int slot, ItemStack stack) {
		return stack.getItem() == LibVulpesItems.itemLinker;
	}

	public List<IInfrastructure> getConnectedInfrastructure() {
		List<IInfrastructure> infrastructure = new LinkedList<IInfrastructure>();

		Iterator<HashedBlockPosition> iter = blockPos.iterator();

		while(iter.hasNext()) {
			HashedBlockPosition position = iter.next();
			TileEntity tile = world.getTileEntity(position.getBlockPos());
			if((tile = world.getTileEntity(position.getBlockPos())) instanceof IInfrastructure) {
				infrastructure.add((IInfrastructure)tile);
			}
			else
				iter.remove();
		}

		return infrastructure;
	}

	@Override
	public void writeDataToNetwork(ByteBuf out, byte id) {
		if(id == 0) {
			PacketBuffer buff = new PacketBuffer(out);
			buff.writeInt(name.length());
			buff.writeString(name);
		}
	}

	@Override
	public void readDataFromNetwork(ByteBuf in, byte packetId,
			NBTTagCompound nbt) {
		int len = in.readInt();
		PacketBuffer buff = new PacketBuffer(in);
		nbt.setString("id", buff.readString(len));
	}

	@Override
	public SPacketUpdateTileEntity getUpdatePacket() {
		return new SPacketUpdateTileEntity(pos, getBlockMetadata(), writeToNBT(new NBTTagCompound()));
	}

	@Override
	public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity pkt) {
		super.onDataPacket(net, pkt);
		readFromNBT(pkt.getNbtCompound());
		moduleNameTextbox.setText(name);
	}

	@Override
	public NBTTagCompound getUpdateTag() {
		return writeToNBT(new NBTTagCompound());
	}

	@Override
	public void handleUpdateTag(NBTTagCompound tag) {
		super.handleUpdateTag(tag);
		moduleNameTextbox.setText(name);
	}


	@Override
	public void useNetworkData(EntityPlayer player, Side side, byte id,
			NBTTagCompound nbt) {
		if(id == 0) {
			name = nbt.getString("id");
			if(!world.isRemote && world.provider.getDimension() == Configuration.spaceDimId) {
				ISpaceObject spaceObj = SpaceObjectManager.getSpaceManager().getSpaceStationFromBlockCoords(pos);

				if(spaceObj instanceof SpaceObject) {
					StationLandingLocation loc = ((SpaceObject)spaceObj).getPadAtLocation(new HashedBlockPosition(pos));
					if(loc != null)
						((SpaceObject)spaceObj).setPadName(this.world, new HashedBlockPosition(pos), name);
					else
						((SpaceObject)spaceObj).addLandingPad(pos, name);
				}
			}
		}
		markDirty();
		world.notifyBlockUpdate(pos, world.getBlockState(pos),  world.getBlockState(pos), 3);
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt) {
		super.readFromNBT(nbt);
		blockPos.clear();
		if(nbt.hasKey("infrastructureLocations")) {
			int array[] = nbt.getIntArray("infrastructureLocations");

			for(int counter = 0; counter < array.length; counter += 3) {
				blockPos.add(new HashedBlockPosition(array[counter], array[counter+1], array[counter+2]));
			}
		}
		name = nbt.getString("name");
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound nbt) {
		super.writeToNBT(nbt);

		if(!blockPos.isEmpty()) {
			int[] array = new int[blockPos.size()*3];
			int counter = 0;
			for(HashedBlockPosition pos : blockPos) {
				array[counter] = pos.x;
				array[counter+1] = pos.y;
				array[counter+2] = pos.z;
				counter += 3;
			}

			nbt.setIntArray("infrastructureLocations", array);
		}

		if(name != null && !name.isEmpty())
			nbt.setString("name", name);

		return nbt;
	}

}
