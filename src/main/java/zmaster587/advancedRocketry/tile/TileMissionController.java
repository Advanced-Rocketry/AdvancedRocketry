package zmaster587.advancedRocketry.tile;

import zmaster587.advancedRocketry.api.satellite.SatelliteBase;
import zmaster587.advancedRocketry.satellite.SatelliteOreMapping;
import zmaster587.advancedRocketry.satellite.SpySatellite;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public class TileMissionController extends TileEntity {
	
	//EntitySpaceshipBase entitySpaceShip;
	long returnTime;
	int missionTimeTotal;
	SatelliteBase satellite;
	
	public TileMissionController() {
		//entitySpaceShip = null;
		returnTime = 0;
		missionTimeTotal = 0;
		//satellite = new OreMappingSatallite();//SpySatellite();
	}
	
	public void interactSatellite(EntityPlayer player, World world, int x, int y, int z) {
		satellite.performAction(player, world,x,y,z);
	}
	
	public String getSatelliteName() {
		return satellite == null ? "None" : satellite.getName();
	}
	
	@Override
	public void writeToNBT(NBTTagCompound data) {
		data.setLong("returnTime", returnTime);
		data.setInteger("missionTimeTotal", missionTimeTotal);
		satellite.writeToNBT(data);
	}
	
	@Override
	public void readFromNBT(NBTTagCompound data) {
		returnTime = data.getLong("returnTime");
		missionTimeTotal = data.getInteger("missionTimeTotal");
		satellite.readFromNBT(data);
	}
}