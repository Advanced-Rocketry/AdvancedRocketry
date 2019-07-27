package zmaster587.advancedRocketry.tile;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.util.Constants.NBT;
import zmaster587.advancedRocketry.api.Configuration;
import zmaster587.advancedRocketry.api.Constants;
import zmaster587.advancedRocketry.api.satellite.SatelliteBase;
import zmaster587.advancedRocketry.api.stations.ISpaceObject;
import zmaster587.advancedRocketry.dimension.DimensionManager;
import zmaster587.advancedRocketry.item.ItemAsteroidChip;
import zmaster587.advancedRocketry.item.ItemPlanetIdentificationChip;
import zmaster587.advancedRocketry.item.ItemSatelliteIdentificationChip;
import zmaster587.advancedRocketry.item.ItemStationChip;
import zmaster587.advancedRocketry.item.ItemStationChip.LandingLocation;
import zmaster587.advancedRocketry.stations.SpaceObject;
import zmaster587.advancedRocketry.stations.SpaceObjectManager;
import zmaster587.advancedRocketry.util.StationLandingLocation;
import zmaster587.libVulpes.api.LibVulpesItems;
import zmaster587.libVulpes.inventory.modules.IModularInventory;
import zmaster587.libVulpes.inventory.modules.ModuleBase;
import zmaster587.libVulpes.items.ItemLinker;
import zmaster587.libVulpes.tile.multiblock.hatch.TileInventoryHatch;
import zmaster587.libVulpes.util.HashedBlockPosition;
import zmaster587.libVulpes.util.Vector3F;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TileGuidanceComputer extends TileInventoryHatch implements IModularInventory {

	int destinationId;
	Vector3F<Float> landingPos;
	Map<Integer, HashedBlockPosition> landingLoc;

	public TileGuidanceComputer() {
		super(1);
		landingPos = new Vector3F<Float>(0f, 0f, 0f);
		destinationId = Constants.INVALID_PLANET;
		landingLoc = new HashMap<Integer, HashedBlockPosition>();
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
		
		//Due to the fact that stations are not guaranteed to be loaded on startup, we get a real reference now
		ISpaceObject obj = SpaceObjectManager.getSpaceManager().getSpaceStation(stationId);
		if(obj == null) {
			landingLoc.remove(stationId);
			return null;
		}
		
		HashedBlockPosition myLoc = landingLoc.get(stationId);
		
		if(myLoc == null)
			return null;
		
		return ((SpaceObject)obj).getPadAtLocation(myLoc);
	}

	public long getTargetSatellite() {
		ItemStack stack = getStackInSlot(0);
		if(!stack.isEmpty() && stack.getItem() instanceof ItemSatelliteIdentificationChip) {
			return ((ItemSatelliteIdentificationChip)stack.getItem()).getSatelliteId(stack);
		}
		return -1;
	}
	
	/**
	 * Gets the dimension to travel to if applicable
	 * @return The dimension to travel to or Constants.INVALID_PLANET if not valid
	 */
	public int getDestinationDimId(int currentDimension, BlockPos pos) {
		ItemStack stack = getStackInSlot(0);

		if(!stack.isEmpty()){
			Item itemType = stack.getItem();
			if (itemType instanceof ItemPlanetIdentificationChip) {
				ItemPlanetIdentificationChip item = (ItemPlanetIdentificationChip)itemType;

				return item.getDimensionId(stack);
			}
			else if(itemType instanceof ItemStationChip) {
				if(Configuration.spaceDimId == currentDimension) {
					ISpaceObject object = SpaceObjectManager.getSpaceManager().getSpaceStationFromBlockCoords(pos);
					if(object != null) {
						if(ItemStationChip.getUUID(stack) == object.getId())
							return object.getOrbitingPlanetId();
					}
					else
						return Constants.INVALID_PLANET;
				}
				return Configuration.spaceDimId;
			}
			else if(itemType instanceof ItemAsteroidChip) {
				destinationId = currentDimension;

				//Caution Side-Effect: Updates landingPos.
				landingPos = new Vector3F<Float>((float)pos.getX(), (float)pos.getY(), (float)pos.getZ());
				return currentDimension;
			}
			else if(itemType instanceof ItemSatelliteIdentificationChip) {
				long l = getTargetSatellite();
				if(l != Constants.INVALID_PLANET) {
					SatelliteBase sat = DimensionManager.getInstance().getSatellite(l);
					
					if(sat != null)
						return sat.getDimensionId();
				}
			} 
			else if (stack.getItem() == LibVulpesItems.itemLinker && ItemLinker.getDimId(stack) != Constants.INVALID_PLANET)
			{
				//Use the destination the Linker in the Guidance computer directs.
				return ItemLinker.getDimId(stack);
			}

		}

		//Almost always the Override setting (Linker in Docking Pad)
		return destinationId;
	}

	/**
	 * returns the location the rocket should land
	 * @return
	 */
	public Vector3F<Float> getLandingLocation(int landingDimension, boolean commit) {
		//Caution Side-Effect dependency: May require a call to getDestinationDimId to populate correct coordinates.
		ItemStack stack = getStackInSlot(0);
		//ToDo: replace all nulls with current coordinates of the ship.
		//Make the if tree match the destination if tree:
		if(!stack.isEmpty()){
			Item itemType = stack.getItem();
			if (itemType instanceof ItemPlanetIdentificationChip) {
				//This could be the location of the rocket.
				return null;
			}
			else if(itemType instanceof ItemStationChip) {
				ItemStationChip chip = (ItemStationChip)stack.getItem();
				if(landingDimension == Configuration.spaceDimId) {
					//TODO: handle Exception
					ISpaceObject object = SpaceObjectManager.getSpaceManager().getSpaceStation(ItemStationChip.getUUID(stack));
					HashedBlockPosition vec = null;
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
					LandingLocation loc = chip.getTakeoffCoords(stack, landingDimension);
					if(loc != null)
					{
						return loc.location;
					}
					return null;
				}
			}
			else if(itemType instanceof ItemAsteroidChip) {
				//Caution Side-Effect dependency: landingPos from getDim.				
				return landingPos;
			}
			else if(itemType instanceof ItemSatelliteIdentificationChip) {
				//You can't actually go to the Satellites
				return null;
			} 
			else if (stack.getItem() == LibVulpesItems.itemLinker && ItemLinker.getDimId(stack) != Constants.INVALID_PLANET)
			{
				//Use the destination the Linker in the Guidance computer directs.
				BlockPos landingBlock = ItemLinker.getMasterCoords(stack);
				return new Vector3F<Float>(landingBlock.getX() + 0.5f, (float)Configuration.orbit, landingBlock.getZ() + 0.5f);
			}

		}		
		else if (stack.isEmpty() && destinationId != Constants.INVALID_PLANET)
		{
			//Use the override coordinates from a Linker in a Docking Pad.
			return landingPos;
		}
		
		//We got nothing.
		return null;
	}
	
	public String getDestinationName(int landingDimension)
	{
		ItemStack stack = getStackInSlot(0);
		if(!stack.isEmpty() && stack.getItem() instanceof ItemStationChip) {
			ItemStationChip chip = (ItemStationChip)stack.getItem();
			if(landingDimension != Configuration.spaceDimId) {
				LandingLocation loc = chip.getTakeoffCoords(stack, landingDimension);
				if(loc != null)
				{
					return loc.name;
				}
			}
		}
		return "";
	}

	public void setFallbackDestination(int dimID, Vector3F<Float> coords) {
		this.destinationId = dimID;
		this.landingPos = coords;
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound nbt) {
		super.writeToNBT(nbt);
		nbt.setInteger("destDimId", destinationId);

		nbt.setFloat("landingx", landingPos.x);
		nbt.setFloat("landingy", landingPos.y);
		nbt.setFloat("landingz", landingPos.z);

		NBTTagList stationList = new NBTTagList();

		for(int locationID : landingLoc.keySet()) {
			NBTTagCompound tag = new NBTTagCompound();
			HashedBlockPosition loc = landingLoc.get(locationID);

			tag.setIntArray("pos", new int[] { loc.x, loc.y, loc.z });
			tag.setInteger("id", locationID);
			stationList.appendTag(tag);
		}
		nbt.setTag("stationMapping", stationList);
		return nbt;
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
			landingLoc.put(id, new HashedBlockPosition(pos[0], pos[1], pos[2]));
		}
	}

	@Override
	public void setInventorySlotContents(int slot, ItemStack stack) {
		super.setInventorySlotContents(slot, stack);

		//If the item in the slot is modified then reset dimid
		if(!stack.isEmpty())
			destinationId = Constants.INVALID_PLANET;
	}

	public void setReturnPosition(Vector3F<Float> pos, int dimId) {
		ItemStack stack = getStackInSlot(0);

		if(!stack.isEmpty() && stack.getItem() instanceof ItemStationChip) {
			ItemStationChip item = (ItemStationChip)stack.getItem();
			item.setTakeoffCoords(stack, pos, dimId, 0);
		}
	}

	@Override
	public String getModularInventoryName() {
		return "tile.guidanceComputer.name";
	}
}
