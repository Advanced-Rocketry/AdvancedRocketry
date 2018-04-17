package zmaster587.advancedRocketry.tile.station;

import io.netty.buffer.ByteBuf;

import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.relauncher.Side;
import zmaster587.advancedRocketry.AdvancedRocketry;
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
import zmaster587.libVulpes.util.BlockPosition;
import zmaster587.libVulpes.util.INetworkMachine;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.ChatComponentText;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;

public class TileLandingPad extends TileInventoryHatch implements ILinkableTile, IGuiCallback, INetworkMachine {

	private List<BlockPosition> blockPos;
	ModuleTextBox moduleNameTextbox;
	String name;

	public TileLandingPad() {
		super(1);
		MinecraftForge.EVENT_BUS.register(this);
		blockPos = new LinkedList<BlockPosition>();
		moduleNameTextbox = new ModuleTextBox(this, 40, 30, 60, 12, 9);
		name = "";
	}
	@Override
	public void invalidate() {
		super.invalidate();
		MinecraftForge.EVENT_BUS.unregister(this);
		for(BlockPosition pos : blockPos) {
			TileEntity tile = worldObj.getTileEntity(pos.x, pos.y, pos.z);
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
		ItemLinker.setMasterCoords(item, this.xCoord, this.yCoord, this.zCoord);
		ItemLinker.setDimId(item, world.provider.dimensionId);
		return true;
	}

	@Override
	public String getModularInventoryName() {
		return "tile.dockingPad.name";
	}

	@Override
	public boolean onLinkComplete(ItemStack item, TileEntity entity,
			EntityPlayer player, World world) {
		TileEntity tile = world.getTileEntity(((ItemLinker)item.getItem()).getMasterX(item), ((ItemLinker)item.getItem()).getMasterY(item), ((ItemLinker)item.getItem()).getMasterZ(item));

		if(tile instanceof IInfrastructure) {
			BlockPosition pos = new BlockPosition(tile.xCoord, tile.yCoord, tile.zCoord);
			if(!blockPos.contains(pos)) {
				blockPos.add(pos);
			}

			AxisAlignedBB bbCache = AxisAlignedBB.getBoundingBox(this.xCoord - 1, this.yCoord, this.zCoord - 1, this.xCoord + 1, this.yCoord + 2, this.zCoord + 1);


			List<EntityRocketBase> rockets = worldObj.getEntitiesWithinAABB(EntityRocketBase.class, bbCache);
			for(EntityRocketBase rocket : rockets) {
				rocket.linkInfrastructure((IInfrastructure) tile);
			}


			if(!worldObj.isRemote) {
				player.addChatMessage(new ChatComponentText(LibVulpes.proxy.getLocalizedString("msg.linker.success")));

				if(tile instanceof IMultiblock)
					((IMultiblock)tile).setMasterBlock(xCoord, yCoord, zCoord);
			}

			ItemLinker.resetPosition(item);
			return true;
		}
		return false;
	}

	@SubscribeEvent
	public void onRocketLand(RocketLandedEvent event) {
		EntityRocketBase rocket = (EntityRocketBase)event.entity;
		AxisAlignedBB bbCache = AxisAlignedBB.getBoundingBox(this.xCoord - 1, this.yCoord, this.zCoord - 1, this.xCoord + 1, this.yCoord + 2, this.zCoord + 1);
		if(this.hasWorldObj())
		{
			List<EntityRocketBase> rockets = worldObj.getEntitiesWithinAABB(EntityRocketBase.class, bbCache);

			if(bbCache.intersectsWith(rocket.boundingBox)) {
				if(!worldObj.isRemote)
					for(IInfrastructure infrastructure : getConnectedInfrastructure()) {
						rocket.linkInfrastructure(infrastructure);
					}
				ItemStack stack = getStackInSlot(0);
				if(stack != null && stack.getItem() == LibVulpesItems.itemLinker && ItemLinker.getDimId(stack) != -1 &&
						event.entity instanceof EntityRocket) {
					((EntityRocket)rocket).setOverriddenCoords(ItemLinker.getDimId(stack), 
							ItemLinker.getMasterX(stack) + 0.5f, Configuration.orbit, ItemLinker.getMasterZ(stack) + 0.5f);
				}
			}
		}
		else
		{
			AdvancedRocketry.logger.fatal("WORLD IS NULL UPON ROCKET LAND, THIS IS VERY BAD AND SHOULD NEVER HAPPEN.  Is there some forge modifying coremod installed?  Location: " + xCoord + " " + yCoord + " " + zCoord + "   Invalidated: " + this.tileEntityInvalid );
		}
	}

	@SubscribeEvent
	public void onRocketLaunch(RocketPreLaunchEvent event) {

		ItemStack stack = getStackInSlot(0);
		if(stack != null && stack.getItem() == LibVulpesItems.itemLinker && ItemLinker.getDimId(stack) != -1) {

			EntityRocketBase rocket = (EntityRocketBase)event.entity;
			AxisAlignedBB bbCache = AxisAlignedBB.getBoundingBox(this.xCoord - 1, this.yCoord, this.zCoord - 1, this.xCoord + 1, this.yCoord + 2, this.zCoord + 1);
			List<EntityRocketBase> rockets = worldObj.getEntitiesWithinAABB(EntityRocketBase.class, bbCache);

			if(bbCache.intersectsWith(rocket.boundingBox)) {
				if(event.entity instanceof EntityRocket) {
					((EntityRocket)rocket).setOverriddenCoords(ItemLinker.getDimId(stack), 
							ItemLinker.getMasterX(stack) + 0.5f, Configuration.orbit, ItemLinker.getMasterZ(stack) + 0.5f);
				}
			}
		}
	}


	@SubscribeEvent
	public void onRocketDismantle(RocketDismantleEvent event) {
		if(!worldObj.isRemote && worldObj.provider.dimensionId == Configuration.spaceDimId) {

			EntityRocketBase rocket = (EntityRocketBase)event.entity;
			AxisAlignedBB bbCache = AxisAlignedBB.getBoundingBox(this.xCoord - 1, this.yCoord, this.zCoord - 1, this.xCoord + 1, this.yCoord + 2, this.zCoord + 1);

			if(bbCache.intersectsWith(rocket.boundingBox)) {

				ISpaceObject spaceObj = SpaceObjectManager.getSpaceManager().getSpaceStationFromBlockCoords(xCoord, zCoord);

				if(spaceObj instanceof SpaceObject) {
					((SpaceObject)spaceObj).setPadStatus(xCoord, zCoord, false);
				}
			}
		}
	}

	public void registerTileWithStation(World world, int x, int y, int z) {
		if(!world.isRemote && world.provider.dimensionId == Configuration.spaceDimId) {
			ISpaceObject spaceObj = SpaceObjectManager.getSpaceManager().getSpaceStationFromBlockCoords(x, z);

			if(spaceObj instanceof SpaceObject) {
				((SpaceObject)spaceObj).addLandingPad(x, z, name);

				AxisAlignedBB bbCache = AxisAlignedBB.getBoundingBox(this.xCoord - 1, this.yCoord, this.zCoord - 1, this.xCoord + 1, this.yCoord + 2, this.zCoord + 1);
				List<EntityRocketBase> rockets = worldObj.getEntitiesWithinAABB(EntityRocketBase.class, bbCache);

				if(rockets != null && !rockets.isEmpty())
					((SpaceObject)spaceObj).setPadStatus(x, z, true);
			}
		}
	}

	public void unregisterTileWithStation(World world, int x, int y, int z) {
		if(!world.isRemote && world.provider.dimensionId == Configuration.spaceDimId) {
			ISpaceObject spaceObj = SpaceObjectManager.getSpaceManager().getSpaceStationFromBlockCoords(x, z);
			if(spaceObj instanceof SpaceObject)
				((SpaceObject)spaceObj).removeLandingPad(x, z);
		}
	}

	public void setAllowAutoLand(World world, int x, int z, boolean allow) {
		if(!world.isRemote && world.provider.dimensionId == Configuration.spaceDimId) {
			ISpaceObject spaceObj = SpaceObjectManager.getSpaceManager().getSpaceStationFromBlockCoords(x,z);

			if(spaceObj instanceof SpaceObject) {
				((SpaceObject)spaceObj).setLandingPadAutoLandStatus(x,z, allow);
			}
		}
	}
	
	@Override
	public void setInventorySlotContents(int slot, ItemStack stack) {
		super.setInventorySlotContents(slot, stack);

		if(stack != null) {
			setAllowAutoLand(worldObj, xCoord, zCoord, false);

			AxisAlignedBB bbCache = AxisAlignedBB.getBoundingBox(this.xCoord - 1, this.yCoord, this.zCoord - 1, this.xCoord + 1, this.yCoord + 2, this.zCoord + 1);
			List<EntityRocketBase> rockets = worldObj.getEntitiesWithinAABB(EntityRocketBase.class, bbCache);

			for(EntityRocketBase rocket : rockets) {
				if(rocket instanceof EntityRocket) {
					if(stack != null && stack.getItem() == LibVulpesItems.itemLinker && ItemLinker.getDimId(stack) != -1) {
						((EntityRocket)rocket).setOverriddenCoords(ItemLinker.getDimId(stack), 
								ItemLinker.getMasterX(stack) + 0.5f, Configuration.orbit, ItemLinker.getMasterZ(stack) + 0.5f);
					}
					else
						((EntityRocket)rocket).setOverriddenCoords(-1, 0,0,0);
				}
			}
		}
		else {
			setAllowAutoLand(worldObj, xCoord, zCoord, true);
			
			AxisAlignedBB bbCache = AxisAlignedBB.getBoundingBox(this.xCoord - 1, this.yCoord, this.zCoord - 1, this.xCoord + 1, this.yCoord + 2, this.zCoord + 1);
			List<EntityRocketBase> rockets = worldObj.getEntitiesWithinAABB(EntityRocketBase.class, bbCache);

			for(EntityRocketBase rocket : rockets) {
				if(rocket instanceof EntityRocket) {
					((EntityRocket)rocket).setOverriddenCoords(-1, 0,0,0);
				}
			}
		}

	}

	@Override
	public boolean isItemValidForSlot(int slot, ItemStack stack) {
		return stack != null && stack.getItem() == LibVulpesItems.itemLinker;
	}

	public List<IInfrastructure> getConnectedInfrastructure() {
		List<IInfrastructure> infrastructure = new LinkedList<IInfrastructure>();

		Iterator<BlockPosition> iter = blockPos.iterator();

		while(iter.hasNext()) {
			BlockPosition position = iter.next();
			TileEntity tile = worldObj.getTileEntity(position.x, position.y, position.z);
			if((tile = worldObj.getTileEntity(position.x, position.y, position.z)) instanceof IInfrastructure) {
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
			try {
				buff.writeStringToBuffer(name);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public void readDataFromNetwork(ByteBuf in, byte packetId,
			NBTTagCompound nbt) {
		int len = in.readInt();
		PacketBuffer buff = new PacketBuffer(in);
		try {
			nbt.setString("id", buff.readStringFromBuffer(len));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public S35PacketUpdateTileEntity getDescriptionPacket() {
		NBTTagCompound nbt = new NBTTagCompound();
		writeToNBT(nbt);
		return new S35PacketUpdateTileEntity(xCoord, yCoord, zCoord, getBlockMetadata(), nbt);
	}

	@Override
	public void onDataPacket(NetworkManager net, S35PacketUpdateTileEntity pkt) {
		super.onDataPacket(net, pkt);
		readFromNBT(pkt.func_148857_g());
		moduleNameTextbox.setText(name);
	}
	
	@Override
	public void useNetworkData(EntityPlayer player, Side side, byte id,
			NBTTagCompound nbt) {
		if(id == 0) {
			name = nbt.getString("id");
			if(!worldObj.isRemote && worldObj.provider.dimensionId == Configuration.spaceDimId) {
				ISpaceObject spaceObj = SpaceObjectManager.getSpaceManager().getSpaceStationFromBlockCoords(xCoord, zCoord);

				if(spaceObj instanceof SpaceObject) {
					StationLandingLocation loc = ((SpaceObject)spaceObj).getPadAtLocation(xCoord, zCoord);
					if(loc != null)
						((SpaceObject)spaceObj).setPadName(this.worldObj, xCoord,0 , zCoord, name);
					else
						((SpaceObject)spaceObj).addLandingPad(xCoord, zCoord, name);
				}
			}
		}
		markDirty();
		worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt) {
		super.readFromNBT(nbt);
		blockPos.clear();
		if(nbt.hasKey("infrastructureLocations")) {
			int array[] = nbt.getIntArray("infrastructureLocations");

			for(int counter = 0; counter < array.length; counter += 3) {
				blockPos.add(new BlockPosition(array[counter], array[counter+1], array[counter+2]));
			}
		}
		name = nbt.getString("name");
	}

	@Override
	public void writeToNBT(NBTTagCompound nbt) {
		super.writeToNBT(nbt);

		if(!blockPos.isEmpty()) {
			int[] array = new int[blockPos.size()*3];
			int counter = 0;
			for(BlockPosition pos : blockPos) {
				array[counter] = pos.x;
				array[counter+1] = pos.y;
				array[counter+2] = pos.z;
				counter += 3;
			}

			nbt.setIntArray("infrastructureLocations", array);
		}
		
		if(name != null && !name.isEmpty())
			nbt.setString("name", name);
	}

}
