package zmaster587.advancedRocketry.tile;

import zmaster587.advancedRocketry.api.IAtmosphere;
import zmaster587.advancedRocketry.atmosphere.AtmosphereHandler;
import zmaster587.advancedRocketry.atmosphere.AtmosphereType;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.ForgeDirection;

public class TileAtmosphereDetector extends TileEntity {

	IAtmosphere atmosphereToDetect;

	public TileAtmosphereDetector() {
		//TODO: allow detection of any type
		atmosphereToDetect = AtmosphereType.AIR;
	}

	@Override
	public boolean canUpdate() {
		return true;
	}

	@Override
	public void updateEntity() {
		if(!worldObj.isRemote && worldObj.getWorldTime() % 10 == 0) {
			int meta = worldObj.getBlockMetadata(xCoord, yCoord, zCoord);
			boolean detectedAtm = false;
			for(int i = 1; i < ForgeDirection.values().length; i++) {
				ForgeDirection direction = ForgeDirection.getOrientation(i);
				detectedAtm = (atmosphereToDetect == AtmosphereHandler.getOxygenHandler(worldObj.provider.dimensionId).getAtmosphereType(xCoord + direction.offsetX, yCoord + direction.offsetY, zCoord + direction.offsetZ));
				if(detectedAtm) break;
			}

			if((meta == 1) != detectedAtm) {
				worldObj.setBlockMetadataWithNotify(xCoord, yCoord, zCoord, detectedAtm ? 1 : 0, 3);
			}
		}
	}
}
