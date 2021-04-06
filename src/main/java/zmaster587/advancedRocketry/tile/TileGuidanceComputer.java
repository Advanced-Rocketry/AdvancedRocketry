package zmaster587.advancedRocketry.tile;

import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.util.Constants.NBT;
import zmaster587.advancedRocketry.api.ARConfiguration;
import zmaster587.advancedRocketry.api.AdvancedRocketryTileEntityType;
import zmaster587.advancedRocketry.api.Constants;
import zmaster587.advancedRocketry.api.satellite.SatelliteBase;
import zmaster587.advancedRocketry.api.stations.ISpaceObject;
import zmaster587.advancedRocketry.dimension.DimensionManager;
import zmaster587.advancedRocketry.item.ItemAsteroidChip;
import zmaster587.advancedRocketry.item.ItemPlanetIdentificationChip;
import zmaster587.advancedRocketry.item.ItemSatelliteIdentificationChip;
import zmaster587.advancedRocketry.item.ItemStationChip;
import zmaster587.advancedRocketry.item.ItemStationChip.LandingLocation;
import zmaster587.advancedRocketry.stations.SpaceObjectManager;
import zmaster587.advancedRocketry.stations.SpaceStationObject;
import zmaster587.advancedRocketry.util.PlanetaryTravelHelper;
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

	ResourceLocation destinationId;
	Vector3F<Float> landingPos;
	Map<ResourceLocation, HashedBlockPosition> landingLoc;

	public TileGuidanceComputer() {
		super( AdvancedRocketryTileEntityType.TILE_GUIDANCE_COMPUTER, 1);
		inventory.setCanInsertSlot(0, true);
		inventory.setCanExtractSlot(0, true);
		landingPos = new Vector3F<Float>(0f, 0f, 0f);
		destinationId = Constants.INVALID_PLANET;
		landingLoc = new HashMap<ResourceLocation, HashedBlockPosition>();
	}
	@Override
	public List<ModuleBase> getModules(int ID, PlayerEntity player) {
		List<ModuleBase> modules = super.getModules(ID, player);

		return modules;
	}

	@Override
	public int getInventoryStackLimit() {
		return 1;
	}

	public void setLandingLocation(ResourceLocation stationId, StationLandingLocation loc) {
		if(loc == null)
			landingLoc.remove(stationId);
		else
			landingLoc.put(stationId, loc.getPos());
	}

	public StationLandingLocation getLandingLocation(ResourceLocation stationId) {
		
		//Due to the fact that stations are not guaranteed to be loaded on startup, we get a real reference now
		ISpaceObject obj = SpaceObjectManager.getSpaceManager().getSpaceStation(stationId);
		if(obj == null) {
			landingLoc.remove(stationId);
			return null;
		}
		
		HashedBlockPosition myLoc = landingLoc.get(stationId);
		
		if(myLoc == null)
			return null;
		
		return ((SpaceStationObject)obj).getPadAtLocation(myLoc);
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
	public ResourceLocation getDestinationDimId(ResourceLocation currentDimension, BlockPos pos) {
		ItemStack stack = getStackInSlot(0);

		if(!stack.isEmpty()){
			Item itemType = stack.getItem();
			if (itemType instanceof ItemPlanetIdentificationChip) {
				ItemPlanetIdentificationChip item = (ItemPlanetIdentificationChip)itemType;

				return item.getDimensionId(stack);
			}
			else if(itemType instanceof ItemStationChip) {
				if(ARConfiguration.GetSpaceDimId().equals(currentDimension)) {
					ISpaceObject object = SpaceObjectManager.getSpaceManager().getSpaceStationFromBlockCoords(pos);
					if(object != null) {
						if(ItemStationChip.getUUID(stack).equals(object.getId()))
							return object.getOrbitingPlanetId();
					}
					else
						return Constants.INVALID_PLANET;
				}
				return ARConfiguration.GetSpaceDimId();
			}
			else if(itemType instanceof ItemAsteroidChip) {
				destinationId = currentDimension;

				//Caution Side-Effect: Updates landingPos.
				landingPos = new Vector3F<Float>((float)pos.getX(), (float)pos.getY(), (float)pos.getZ());
				return currentDimension;
			}
			else if(itemType instanceof ItemSatelliteIdentificationChip) {
				long l = getTargetSatellite();
				if(l != Constants.INVALID_SAT) {
					SatelliteBase sat = DimensionManager.getInstance().getSatellite(l);
					
					if(sat != null)
						return sat.getDimensionId().get();
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
	public Vector3F<Float> getLandingLocation(ResourceLocation landingDimension, boolean commit) {
		//Caution Side-Effect dependency: May require a call to getDestinationDimId to populate correct coordinates.
		ItemStack stack = getStackInSlot(0);
		//TODO: replace all nulls with current coordinates of the ship.
		//Make the if tree match the destination if tree:
		if(!stack.isEmpty()){
			Item itemType = stack.getItem();
			if (itemType instanceof ItemPlanetIdentificationChip) {
				//This could be the location of the rocket.
				return null;
			}
			else if(itemType instanceof ItemStationChip) {
				ItemStationChip chip = (ItemStationChip)stack.getItem();
				if(ARConfiguration.GetSpaceDimId().equals(landingDimension)) {
					//TODO: handle Exception
					ISpaceObject object = SpaceObjectManager.getSpaceManager().getSpaceStation(ItemStationChip.getUUID(stack));
					return getStationLocation(object, commit);
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
				//You can't actually go to the satellites.
				return null;
			} 
			else if (stack.getItem() == LibVulpesItems.itemLinker && ItemLinker.getDimId(stack) != Constants.INVALID_PLANET)
			{
				//Use the destination the Linker in the Guidance computer directs.
				BlockPos landingBlock = ItemLinker.getMasterCoords(stack);
				return new Vector3F<Float>(landingBlock.getX() + 0.5f, (float)ARConfiguration.getCurrentConfig().orbit.get(), landingBlock.getZ() + 0.5f);
			}

		}		
		else if (stack.isEmpty() && !Constants.INVALID_PLANET.equals(destinationId))
		{
			//Use the override coordinates from a Linker in a Docking Pad.
			return landingPos;
		}
		
		//We got nothing.
		return null;
	}
	
	private Vector3F<Float> getStationLocation(ISpaceObject object, boolean commit)
	{
		HashedBlockPosition vec = null;
		if(object instanceof SpaceStationObject) {
			if(landingLoc.get(object.getId()) != null) {
				vec = landingLoc.get(object.getId());

				if(commit)
					((SpaceStationObject)object).getPadAtLocation(landingLoc.get(object.getId())).setOccupied(true);
			}
			else
				vec = ((SpaceStationObject)object).getNextLandingPad(commit);
		}

		if(object == null)
			return null;

		if(vec == null)
			vec = object.getSpawnLocation();

		return new Vector3F<Float>(new Float(vec.x), new Float(vec.y), new Float(vec.z));
	}
	
	public void overrideLandingStation(ISpaceObject object)
	{
		setFallbackDestination(ARConfiguration.GetSpaceDimId(), getStationLocation(object, true));
	}
	
	public String getDestinationName(ResourceLocation landingDimension)
	{
		ItemStack stack = getStackInSlot(0);
		if(!stack.isEmpty() && stack.getItem() instanceof ItemStationChip) {
			ItemStationChip chip = (ItemStationChip)stack.getItem();
			if(!ARConfiguration.GetSpaceDimId().equals(landingDimension)) {
				LandingLocation loc = chip.getTakeoffCoords(stack, landingDimension);
				if(loc != null)
				{
					return loc.name;
				}
			}
		}
		return "";
	}

	public void setFallbackDestination(ResourceLocation dimID, Vector3F<Float> coords) {
		this.destinationId = dimID;
		this.landingPos = coords;
	}

	public int getLaunchSequence(ResourceLocation currentDimensionID, BlockPos currentPosition) {
		int totalBurn = (currentDimensionID.getPath().equals(ARConfiguration.getCurrentConfig().spaceDimId.get())) ? ARConfiguration.getCurrentConfig().stationClearanceHeight.get() : ARConfiguration.getCurrentConfig().orbit.get();
		ResourceLocation destinationDimensionID = getDestinationDimId(currentDimensionID, currentPosition);

		totalBurn += (currentDimensionID.getPath().equals(ARConfiguration.getCurrentConfig().spaceDimId.get())) ? getTransBodyInjection(currentDimensionID, destinationDimensionID, currentPosition) : getTransBodyInjection(currentDimensionID, destinationDimensionID);
		return totalBurn;
	}

	public int getTransBodyInjection(ResourceLocation currentDimensionID, ResourceLocation destinationDimensionID) {
		ISpaceObject destinationSpaceStation = SpaceObjectManager.getSpaceManager().getSpaceStation(ItemStationChip.getUUID(getStackInSlot(0)));
		destinationDimensionID = ((destinationDimensionID.getPath().equals(ARConfiguration.getCurrentConfig().spaceDimId.get())) && (destinationSpaceStation != null)) ? destinationSpaceStation.getOrbitingPlanetId() : destinationDimensionID;

		if (destinationDimensionID == Constants.INVALID_PLANET) {return 0;}
		return (PlanetaryTravelHelper.isTravelWithinOrbit(currentDimensionID, destinationDimensionID) && !isAsteroidMission()) ? 0 : PlanetaryTravelHelper.getTransbodyInjectionBurn(currentDimensionID, destinationDimensionID, isAsteroidMission());
	}

	public int getTransBodyInjection(ResourceLocation currentDimensionID, ResourceLocation destinationDimensionID, BlockPos currentPosition) {
		ISpaceObject currentSpaceStation = SpaceObjectManager.getSpaceManager().getSpaceStationFromBlockCoords(currentPosition);
		ISpaceObject destinationSpaceStation = SpaceObjectManager.getSpaceManager().getSpaceStation(ItemStationChip.getUUID(getStackInSlot(0)));
		destinationDimensionID = ((destinationDimensionID.getPath().equals(ARConfiguration.getCurrentConfig().spaceDimId.get())) && (destinationSpaceStation != null)) ? destinationSpaceStation.getOrbitingPlanetId() : destinationDimensionID;

		if (destinationDimensionID == Constants.INVALID_PLANET) {return 0;}
		return (PlanetaryTravelHelper.isTravelWithinOrbit(currentSpaceStation.getOrbitingPlanetId(), destinationDimensionID) && !isAsteroidMission()) ? 0 : PlanetaryTravelHelper.getTransbodyInjectionBurn(currentSpaceStation.getOrbitingPlanetId(), destinationDimensionID, isAsteroidMission());
	}

	public boolean isAsteroidMission () {
		return getStackInSlot(0).getItem() instanceof ItemAsteroidChip;
	}

	@Override
	public CompoundNBT write(CompoundNBT nbt) {
		super.write(nbt);
		if(destinationId != null)
			nbt.putString("destDimId", destinationId.toString());

		nbt.putFloat("landingx", landingPos.x);
		nbt.putFloat("landingy", landingPos.y);
		nbt.putFloat("landingz", landingPos.z);

		ListNBT stationList = new ListNBT();

		for(ResourceLocation locationID : landingLoc.keySet()) {
			CompoundNBT tag = new CompoundNBT();
			HashedBlockPosition loc = landingLoc.get(locationID);

			tag.putIntArray("pos", new int[] { loc.x, loc.y, loc.z });
			tag.putString("id", locationID.toString());
			stationList.add(tag);
		}
		nbt.put("stationMapping", stationList);
		return nbt;
	}

	@Override
	public void read(BlockState state, CompoundNBT nbt) {
		super.read(state, nbt);
		if(nbt.contains("destDimId"))
			destinationId = new ResourceLocation(nbt.getString("destDimId"));
		else
			destinationId = Constants.INVALID_PLANET;

		landingPos.x = nbt.getFloat("landingx");
		landingPos.y = nbt.getFloat("landingy");
		landingPos.z = nbt.getFloat("landingz");

		ListNBT stationList = nbt.getList("stationMapping", NBT.TAG_COMPOUND);

		for(int i = 0; i < stationList.size(); i++) {
			CompoundNBT tag = stationList.getCompound(i);
			int pos[];
			pos = tag.getIntArray("pos");
			ResourceLocation id = new ResourceLocation(tag.getString("id"));
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

	public void setReturnPosition(Vector3F<Float> pos, ResourceLocation dimId) {
		ItemStack stack = getStackInSlot(0);

		if(!stack.isEmpty() && stack.getItem() instanceof ItemStationChip) {
			ItemStationChip item = (ItemStationChip)stack.getItem();
			item.setTakeoffCoords(stack, pos, dimId, 0);
		}
	}

	@Override
	public String getModularInventoryName() {
		return "block.advancedrocketry.guidancecomputer";
	}
}
