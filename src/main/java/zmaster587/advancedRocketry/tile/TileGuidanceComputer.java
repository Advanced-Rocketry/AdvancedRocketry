package zmaster587.advancedRocketry.tile;

import java.util.List;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import zmaster587.advancedRocketry.api.Configuration;
import zmaster587.advancedRocketry.api.stations.ISpaceObject;
import zmaster587.advancedRocketry.dimension.DimensionManager;
import zmaster587.advancedRocketry.item.ItemAsteroidChip;
import zmaster587.advancedRocketry.item.ItemPlanetIdentificationChip;
import zmaster587.advancedRocketry.item.ItemStationChip;
import zmaster587.advancedRocketry.stations.SpaceObject;
import zmaster587.advancedRocketry.stations.SpaceObjectManager;
import zmaster587.libVulpes.inventory.modules.IModularInventory;
import zmaster587.libVulpes.inventory.modules.ModuleBase;
import zmaster587.libVulpes.tile.multiblock.hatch.TileInventoryHatch;
import zmaster587.libVulpes.util.HashedBlockPosition;
import zmaster587.libVulpes.util.Vector3F;

public class TileGuidanceComputer extends TileInventoryHatch implements IModularInventory {

	int destinationId;
	Vector3F<Float> landingPos;
	
	public TileGuidanceComputer() {
		super(1);
		landingPos = new Vector3F<Float>(0f, 0f, 0f);
		destinationId = -1;
	}
	@Override
	public List<ModuleBase> getModules(int ID, EntityPlayer player) {
		return super.getModules(ID, player);
	}

	@Override
	public int getInventoryStackLimit() {
		return 1;
	}
	
	/**
	 * Gets the dimension to travel to if applicable
	 * @return The dimension to travel to or -1 if not valid
	 */
	public int getDestinationDimId(int currentDimension, BlockPos pos) {
		ItemStack stack = getStackInSlot(0);

		if(stack != null){
			Item itemType = stack.getItem();
			if (itemType instanceof ItemPlanetIdentificationChip) {
				ItemPlanetIdentificationChip item = (ItemPlanetIdentificationChip)itemType;

				return item.getDimensionId(stack);
			}
			else if(itemType instanceof ItemStationChip) {
				if(Configuration.spaceDimId == currentDimension) {
					ISpaceObject object = SpaceObjectManager.getSpaceManager().getSpaceStationFromBlockCoords(pos);
					if(object != null)
						return object.getOrbitingPlanetId();
					return -1;
				}
				return Configuration.spaceDimId;
			}
			else if(itemType instanceof ItemAsteroidChip) {
				return currentDimension;
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
				ISpaceObject object = SpaceObjectManager.getSpaceManager().getSpaceStation(ItemStationChip.getUUID(stack));
				HashedBlockPosition vec = null;
				if(object instanceof SpaceObject)
					vec = ((SpaceObject)object).getNextLandingPad(commit);

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
	public NBTTagCompound writeToNBT(NBTTagCompound nbt) {
		super.writeToNBT(nbt);
		nbt.setInteger("destDimId", destinationId);
		
		nbt.setFloat("landingx", landingPos.x);
		nbt.setFloat("landingy", landingPos.y);
		nbt.setFloat("landingz", landingPos.z);
		
		return nbt;
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt) {
		super.readFromNBT(nbt);
		destinationId = nbt.getInteger("destDimId");
		
		landingPos.x = nbt.getFloat("landingx");
		landingPos.y = nbt.getFloat("landingy");
		landingPos.z = nbt.getFloat("landingz");
	}

	@Override
	public void setInventorySlotContents(int slot, ItemStack stack) {
		super.setInventorySlotContents(slot, stack);
		
		//If the item in the slot is modified then reset dimid
		if(stack != null)
			destinationId = -1;
	}
	
	public void setReturnPosition(Vector3F<Float> pos, int dimId) {
		ItemStack stack = getStackInSlot(0);

		if(stack != null && stack.getItem() instanceof ItemStationChip) {
			ItemStationChip item = (ItemStationChip)stack.getItem();
			item.setTakeoffCoords(stack, pos, dimId);
		}
	}

	@Override
	public String getModularInventoryName() {
		return "tile.guidanceComputer.name";
	}
}
