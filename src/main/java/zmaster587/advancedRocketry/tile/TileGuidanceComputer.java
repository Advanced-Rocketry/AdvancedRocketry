package zmaster587.advancedRocketry.tile;

import java.util.List;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import zmaster587.advancedRocketry.api.Configuration;
import zmaster587.advancedRocketry.api.stations.ISpaceObject;
import zmaster587.advancedRocketry.api.stations.SpaceObjectManager;
import zmaster587.advancedRocketry.dimension.DimensionManager;
import zmaster587.advancedRocketry.inventory.modules.IModularInventory;
import zmaster587.advancedRocketry.inventory.modules.ModuleBase;
import zmaster587.advancedRocketry.item.ItemPlanetIdentificationChip;
import zmaster587.advancedRocketry.item.ItemStationChip;
import zmaster587.advancedRocketry.stations.SpaceObject;
import zmaster587.advancedRocketry.tile.multiblock.TileInventoryHatch;
import zmaster587.libVulpes.util.BlockPosition;
import zmaster587.libVulpes.util.Vector3F;

public class TileGuidanceComputer extends TileInventoryHatch implements IModularInventory {

	public TileGuidanceComputer() {
		super(1);
	}
	@Override
	public List<ModuleBase> getModules() {
		return super.getModules();
	}

	@Override
	public int getInventoryStackLimit() {
		return 1;
	}

	/**
	 * Gets the dimension to travel to if applicable
	 * @return The dimension to travel to or -1 if not valid
	 */
	public int getDestinationDimId(int currentDimension) {
		ItemStack stack = getStackInSlot(0);

		if(stack != null){

			Item itemType = stack.getItem();
			if (itemType instanceof ItemPlanetIdentificationChip) {
				ItemPlanetIdentificationChip item = (ItemPlanetIdentificationChip)itemType;

				return item.getDimensionId(stack);
			}
			else if(itemType instanceof ItemStationChip) {
				if(Configuration.spaceDimId == currentDimension)
					return 0;
				return Configuration.spaceDimId;
			}

		}
		return -1;
	}

	/**
	 * returns the location the rocket should land
	 * @return
	 */
	public Vector3F<Float> getLandingLocation(int landingDimension) {
		ItemStack stack = getStackInSlot(0);
		if(stack != null && stack.getItem() instanceof ItemStationChip) {
			ItemStationChip chip = (ItemStationChip)stack.getItem();
			if(landingDimension == Configuration.spaceDimId) {
				//TODO: handle Exception
				ISpaceObject object = SpaceObjectManager.getSpaceManager().getSpaceStation(chip.getDamage(stack));
				BlockPosition vec = null;
				if(object instanceof SpaceObject)
					vec = ((SpaceObject)object).getNextLandingPad();

				if(vec == null)
					vec = object.getSpawnLocation();

				return new Vector3F<Float>(new Float(vec.x), new Float(vec.y), new Float(vec.z));
			}
			else {
				return chip.getTakeoffCoords(stack);
			}
		}
		return null;
	}



	public void setReturnPosition(Vector3F<Float> pos) {
		ItemStack stack = getStackInSlot(0);

		if(stack != null && stack.getItem() instanceof ItemStationChip) {
			ItemStationChip item = (ItemStationChip)stack.getItem();
			item.setTakeoffCoords(stack, pos);
		}
	}

	@Override
	public String getModularInventoryName() {
		return "tile.guidanceComputer.name";
	}
}
