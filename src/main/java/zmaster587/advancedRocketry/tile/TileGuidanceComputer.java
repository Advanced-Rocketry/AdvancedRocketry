package zmaster587.advancedRocketry.tile;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraftforge.common.util.Constants.NBT;
import zmaster587.advancedRocketry.api.Configuration;
import zmaster587.advancedRocketry.api.satellite.SatelliteBase;
import zmaster587.advancedRocketry.api.stations.ISpaceObject;
import zmaster587.advancedRocketry.dimension.DimensionManager;
import zmaster587.advancedRocketry.inventory.TextureResources;
import zmaster587.advancedRocketry.item.ItemAsteroidChip;
import zmaster587.advancedRocketry.item.ItemPlanetIdentificationChip;
import zmaster587.advancedRocketry.item.ItemSatelliteIdentificationChip;
import zmaster587.advancedRocketry.item.ItemStationChip;
import zmaster587.advancedRocketry.stations.SpaceObject;
import zmaster587.advancedRocketry.stations.SpaceObjectManager;
import zmaster587.advancedRocketry.util.StationLandingLocation;
import zmaster587.libVulpes.inventory.modules.IButtonInventory;
import zmaster587.libVulpes.inventory.modules.IModularInventory;
import zmaster587.libVulpes.inventory.modules.ModuleBase;
import zmaster587.libVulpes.inventory.modules.ModuleButton;
import zmaster587.libVulpes.inventory.modules.ModuleContainerPan;
import zmaster587.libVulpes.tile.multiblock.hatch.TileInventoryHatch;
import zmaster587.libVulpes.util.BlockPosition;
import zmaster587.libVulpes.util.Vector3F;

public class TileGuidanceComputer extends TileInventoryHatch implements IModularInventory {

	int destinationId;
	Vector3F<Float> landingPos;
	Map<Integer, BlockPosition> landingLoc;

	public TileGuidanceComputer() {
		super(1);
		landingPos = new Vector3F<Float>(0f, 0f, 0f);
		destinationId = -1;
		landingLoc = new HashMap<Integer, BlockPosition>();
	}
	@Override
	public List<ModuleBase> getModules(int ID, EntityPlayer player) {
		List<ModuleBase> modules = super.getModules(ID, player);

		return modules;
	}

	@Override
	public int getInventoryStackLimit() {
		return 1;
	}

	public void setLandingLocation(int stationId, StationLandingLocation loc) {
		if(loc == null)
			landingLoc.remove(stationId);
		else
			landingLoc.put(stationId, loc.getPos());
	}

	public StationLandingLocation getLandingLocation(int stationId) {
		
		//Due to the fact that stations are not garunteed to be loaded on startup, we get a real reference now
		ISpaceObject obj = SpaceObjectManager.getSpaceManager().getSpaceStation(stationId);
		if(obj == null) {
			landingLoc.remove(stationId);
			return null;
		}
		
		BlockPosition myLoc = landingLoc.get(stationId);
		
		if(myLoc == null)
			return null;
		
		return ((SpaceObject)obj).getPadAtLocation(myLoc);
	}

	public long getTargetSatellite() {
		ItemStack stack = getStackInSlot(0);
		if(stack != null && stack.getItem() instanceof ItemSatelliteIdentificationChip) {
			return ((ItemSatelliteIdentificationChip)stack.getItem()).getSatelliteId(stack);
		}
		return -1;
	}
	
	/**
	 * Gets the dimension to travel to if applicable
	 * @return The dimension to travel to or -1 if not valid
	 */
	public int getDestinationDimId(int currentDimension, int x, int z) {
		ItemStack stack = getStackInSlot(0);

		if(stack != null){
			Item itemType = stack.getItem();
			if (itemType instanceof ItemPlanetIdentificationChip) {
				ItemPlanetIdentificationChip item = (ItemPlanetIdentificationChip)itemType;

				return item.getDimensionId(stack);
			}
			else if(itemType instanceof ItemStationChip) {
				if(Configuration.spaceDimId == currentDimension) {
					ISpaceObject object = SpaceObjectManager.getSpaceManager().getSpaceStationFromBlockCoords(x, z);
					if(object != null) {
						if(ItemStationChip.getUUID(stack) == object.getId())
							return object.getOrbitingPlanetId();
					}
					else
						return -1;
				}
				return Configuration.spaceDimId;
			}
			else if(itemType instanceof ItemAsteroidChip) {
				return currentDimension;
			}
			else if(itemType instanceof ItemSatelliteIdentificationChip) {
				long l = getTargetSatellite();
				if(l != -1) {
					SatelliteBase sat = DimensionManager.getInstance().getSatellite(l);
					return sat.getDimensionId();
				}
			}

		}

		return destinationId;
	}

	/**
	 * returns the location the rocket should land
	 * @return
	 */
	public Vector3F<Float> getLandingLocation(int landingDimension, boolean commit) {
		ItemStack stack = getStackInSlot(0);
		if(stack != null && stack.getItem() instanceof ItemStationChip) {
			ItemStationChip chip = (ItemStationChip)stack.getItem();
			if(landingDimension == Configuration.spaceDimId) {
				//TODO: handle Exception
				Long uuid = chip.getUUID(stack);
				if(uuid == null)
					return null;
				ISpaceObject object = SpaceObjectManager.getSpaceManager().getSpaceStation((int)(long)uuid);
				BlockPosition vec = null;
				if(object instanceof SpaceObject) {
					if(landingLoc.get(object.getId()) != null) {
						vec = landingLoc.get(object.getId());

						if(commit)
							((SpaceObject)object).getPadAtLocation(landingLoc.get(object.getId())).setOccupied(true);
					}
					else
						vec = ((SpaceObject)object).getNextLandingPad(commit);
				}

				if(object == null)
					return null;

				if(vec == null)
					vec = object.getSpawnLocation();

				return new Vector3F<Float>(new Float(vec.x), new Float(vec.y), new Float(vec.z));
			}
			else {
				return chip.getTakeoffCoords(stack, landingDimension);
			}
		}

		if(destinationId != -1)
			return landingPos;
		return null;
	}

	public void setFallbackDestination(int dimID, Vector3F<Float> coords) {
		this.destinationId = dimID;
		this.landingPos = coords;
	}

	@Override
	public void writeToNBT(NBTTagCompound nbt) {
		super.writeToNBT(nbt);
		nbt.setInteger("destDimId", destinationId);

		nbt.setFloat("landingx", landingPos.x);
		nbt.setFloat("landingy", landingPos.y);
		nbt.setFloat("landingz", landingPos.z);

		NBTTagList stationList = new NBTTagList();

		for(int locationID : landingLoc.keySet()) {
			NBTTagCompound tag = new NBTTagCompound();
			BlockPosition loc = landingLoc.get(locationID);

			tag.setIntArray("pos", new int[] { loc.x, loc.y, loc.z });
			tag.setInteger("id", locationID);
			stationList.appendTag(tag);
		}
		nbt.setTag("stationMapping", stationList);
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt) {
		super.readFromNBT(nbt);
		destinationId = nbt.getInteger("destDimId");

		landingPos.x = nbt.getFloat("landingx");
		landingPos.y = nbt.getFloat("landingy");
		landingPos.z = nbt.getFloat("landingz");

		NBTTagList stationList = nbt.getTagList("stationMapping", NBT.TAG_COMPOUND);

		for(int i = 0; i < stationList.tagCount(); i++) {
			NBTTagCompound tag = stationList.getCompoundTagAt(i);
			int pos[];
			pos = tag.getIntArray("pos");
			int id = tag.getInteger("id");
			landingLoc.put(id, new BlockPosition(pos[0], pos[1], pos[2]));
		}
	}

	@Override
	public void setInventorySlotContents(int slot, ItemStack stack) {
		super.setInventorySlotContents(slot, stack);

		//If the item in the slot is modified then reset dimid
		if(stack != null)
			destinationId = -1;
	}
	public void setReturnPosition(Vector3F<Float> pos, int dimid) {
		ItemStack stack = getStackInSlot(0);

		if(stack != null && stack.getItem() instanceof ItemStationChip) {
			ItemStationChip item = (ItemStationChip)stack.getItem();
			item.setTakeoffCoords(stack, pos, dimid);
		}
	}

	@Override
	public String getModularInventoryName() {
		return "tile.guidanceComputer.name";
	}
}
