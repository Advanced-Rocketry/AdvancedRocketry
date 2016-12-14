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
	}

	@Override
	public String getModularInventoryName() {
		return "container.satellite";
	}

	public SatelliteBase getSatellite() {

		ItemStack itemStack = inventory.getStackInSlot(0);
		if(itemStack != null && itemStack.getItem() instanceof ItemSatellite) {
			SatelliteProperties properties = ((ItemSatellite)itemStack.getItem()).getSatellite(itemStack);

			SatelliteBase satellite = SatelliteRegistry.getSatallite(properties.getSatelliteType());

			if(satellite == null)
				return null;
			
			satellite.setProperties(properties);
			return satellite;
		}
		else
			return null;
	}
}
