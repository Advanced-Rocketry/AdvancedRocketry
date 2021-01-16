package zmaster587.advancedRocketry.tile;

import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import zmaster587.advancedRocketry.api.Configuration;
import zmaster587.advancedRocketry.dimension.DimensionManager;
import zmaster587.advancedRocketry.dimension.DimensionProperties;
import zmaster587.libVulpes.LibVulpes;
import zmaster587.libVulpes.inventory.TextureResources;
import zmaster587.libVulpes.inventory.modules.ModuleBase;
import zmaster587.libVulpes.inventory.modules.ModuleProgress;
import zmaster587.libVulpes.inventory.modules.ModuleSlotArray;
import zmaster587.libVulpes.inventory.modules.ModuleText;
import zmaster587.libVulpes.tile.TileInventoriedForgePowerMachine;

public class TileSolarPanel extends TileInventoriedForgePowerMachine {

	ModuleText text;

	public TileSolarPanel() {
		super(10000, 1);
		text = new ModuleText(60, 40, LibVulpes.proxy.getLocalizedString("msg.solar.collectingEnergy"), 0x2f2f2f);
	}

	@Override
	public boolean canGeneratePower() {
		float angle = worldObj.getCelestialAngle(0);
		return worldObj.canBlockSeeTheSky(xCoord,yCoord + 1,zCoord) && (angle > 0.75 || angle < 0.25);
	}

	@Override
	public void updateEntity() {
		if(canGeneratePower()) {
			if(worldObj.isRemote)
				text.setText(LibVulpes.proxy.getLocalizedString("msg.solar.collectingEnergy") + "\n" + getPowerPerOperation() + " " + LibVulpes.proxy.getLocalizedString("msg.powerunit.rfpertick"));
			if(hasEnoughEnergyBuffer(getPowerPerOperation())) {
				if(!worldObj.isRemote) this.energy.acceptEnergy(getPowerPerOperation(), false);
				onGeneratePower();
			}
			else
				notEnoughBufferForFunction();
		}
		else if(worldObj.isRemote)
			text.setText(LibVulpes.proxy.getLocalizedString("msg.solar.cannotcollectEnergy"));

		if(!worldObj.isRemote)
			transmitPower();
	}

	@Override
	public List<ModuleBase> getModules(int ID, EntityPlayer player) {
		List<ModuleBase> modules = super.getModules(ID, player);

		modules.add(text);

		return modules;
	}


	@Override
	public int getPowerPerOperation() {
		DimensionProperties properties = DimensionManager.getInstance().getDimensionProperties(worldObj.provider.dimensionId);
		//Slight adjustment to make Earth 0.9995 into a 1.0
		//Then multiplied by two for 520W = 1 RF/t becoming 2 RF/t @ 100% efficiency
		//Makes solar panels not return 0 everywhere
		return (int)(2.0d * 1.0005d * Configuration.solarGeneratorMult * properties.getPeakInsolationMultiplier());
	}

	@Override
	public void onGeneratePower() {

	}

	@Override
	public String getModularInventoryName() {
		return "tile.solarGenerator.name";
	}

	@Override
	public boolean canInteractWithContainer(EntityPlayer entity) {
		return true;
	}
}
