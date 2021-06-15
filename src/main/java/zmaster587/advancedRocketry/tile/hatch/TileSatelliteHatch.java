package zmaster587.advancedRocketry.tile.hatch;

import net.minecraft.item.ItemStack;
import zmaster587.advancedRocketry.api.SatelliteRegistry;
import zmaster587.advancedRocketry.api.satellite.SatelliteBase;
import zmaster587.advancedRocketry.api.satellite.SatelliteProperties;
import zmaster587.advancedRocketry.item.ItemSatellite;
import zmaster587.libVulpes.tile.multiblock.hatch.TileInventoryHatch;

public class TileSatelliteHatch extends TileInventoryHatch {

	public TileSatelliteHatch() {
		super();
	}

	public TileSatelliteHatch(int i) {
		super(1);
		inventory.setCanInsertSlot(0, true);
		inventory.setCanExtractSlot(0, true);
	}

	@Override
	public String getModularInventoryName() {
		return "container.satellite";
	}

	public SatelliteBase getSatellite() {

		ItemStack itemStack = inventory.getStackInSlot(0);
		if(!itemStack.isEmpty() && itemStack.getItem() instanceof ItemSatellite) {
			SatelliteProperties properties = SatelliteRegistry.getSatelliteProperty(itemStack);
			
			if(properties == null)
				return null;

			SatelliteBase satellite = SatelliteRegistry.getSatellite(properties.getSatelliteType());

			if(satellite == null)
				return null;
			
			satellite.setProperties(itemStack);
			return satellite;
		}
		else
			return null;
	}
}
