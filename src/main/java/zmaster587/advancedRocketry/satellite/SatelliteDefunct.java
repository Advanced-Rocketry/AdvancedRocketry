package zmaster587.advancedRocketry.satellite;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import zmaster587.advancedRocketry.api.satellite.SatelliteBase;
import zmaster587.advancedRocketry.api.satellite.SatelliteProperties;

public class SatelliteDefunct extends SatelliteBase {
	
	public SatelliteDefunct() {
		satelliteProperties = new SatelliteProperties();
	}
	
	@Override
	public String getInfo(World world) {
		return "No Info";
	}

	@Override
	public String getName() {
		return "Offline Satellite";
	}

	@Override
	public boolean performAction(EntityPlayer player, World world, BlockPos pos) {
		return false;
	}

	@Override
	public double failureChance() {
		return 0;
	}
}
