package zmaster587.advancedRocketry.tile.station;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import zmaster587.advancedRocketry.api.Configuration;
import zmaster587.advancedRocketry.api.EntityRocketBase;
import zmaster587.advancedRocketry.api.IInfrastructure;
import zmaster587.advancedRocketry.api.RocketEvent.RocketLandedEvent;
import zmaster587.advancedRocketry.api.RocketEvent.RocketLaunchEvent;
import zmaster587.advancedRocketry.api.RocketEvent.RocketPreLaunchEvent;
import zmaster587.advancedRocketry.api.stations.ISpaceObject;
import zmaster587.advancedRocketry.api.stations.SpaceObjectManager;
import zmaster587.advancedRocketry.entity.EntityRocket;
import zmaster587.advancedRocketry.stations.SpaceObject;
import zmaster587.libVulpes.api.LibVulpesItems;
import zmaster587.libVulpes.interfaces.ILinkableTile;
import zmaster587.libVulpes.items.ItemLinker;
import zmaster587.libVulpes.tile.IMultiblock;
import zmaster587.libVulpes.tile.multiblock.hatch.TileInventoryHatch;
import zmaster587.libVulpes.util.BlockPosition;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.ChatComponentText;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;

public class TileLandingPad extends TileInventoryHatch implements ILinkableTile {

	private List<BlockPosition> blockPos;

	public TileLandingPad() {
		super(1);
		MinecraftForge.EVENT_BUS.register(this);
		blockPos = new LinkedList<BlockPosition>();
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
		return "tile.landingPad.name";
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
				player.addChatMessage(new ChatComponentText("Linked Sucessfully"));

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
		List<EntityRocketBase> rockets = worldObj.getEntitiesWithinAABB(EntityRocketBase.class, bbCache);

		if(rockets.contains(rocket)) {
			for(IInfrastructure infrastructure : getConnectedInfrastructure()) {
				rocket.linkInfrastructure(infrastructure);
			}
		}
	}

	@SubscribeEvent
	public void onRocketLaunch(RocketPreLaunchEvent event) {

		ItemStack stack = getStackInSlot(0);
		if(stack != null && stack.getItem() == LibVulpesItems.itemLinker && ItemLinker.getDimId(stack) != -1) {

			EntityRocketBase rocket = (EntityRocketBase)event.entity;
			AxisAlignedBB bbCache = AxisAlignedBB.getBoundingBox(this.xCoord - 1, this.yCoord, this.zCoord - 1, this.xCoord + 1, this.yCoord + 2, this.zCoord + 1);
			List<EntityRocketBase> rockets = worldObj.getEntitiesWithinAABB(EntityRocketBase.class, bbCache);

			if(rockets.contains(rocket)) {
				if(event.entity instanceof EntityRocket) {
					((EntityRocket)rocket).setOverriddenCoords(ItemLinker.getDimId(stack), 
							ItemLinker.getMasterX(stack) + 0.5f, Configuration.orbit, ItemLinker.getMasterZ(stack) + 0.5f);
				}
			}
		}
	}
	
	public void registerTileWithStation(World world, int x, int y, int z) {
		if(!world.isRemote && world.provider.dimensionId == Configuration.spaceDimId) {
			ISpaceObject spaceObj = SpaceObjectManager.getSpaceManager().getSpaceStationFromBlockCoords(x, z);
		
			if(spaceObj instanceof SpaceObject) {
				((SpaceObject)spaceObj).addLandingPad(x, z);
				
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
	
	@Override
	public void setInventorySlotContents(int slot, ItemStack stack) {
		super.setInventorySlotContents(slot, stack);
		
		if(stack != null) {
			unregisterTileWithStation(worldObj, xCoord, yCoord, zCoord);
		}
		else {
			registerTileWithStation(worldObj, xCoord, yCoord, zCoord);
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
	public void readFromNBT(NBTTagCompound nbt) {
		super.readFromNBT(nbt);
		blockPos.clear();
		if(nbt.hasKey("infrastructureLocations")) {
			int array[] = nbt.getIntArray("infrastructureLocations");

			for(int counter = 0; counter < array.length; counter += 3) {
				blockPos.add(new BlockPosition(array[counter], array[counter+1], array[counter+2]));
			}
		}
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

	}

}
