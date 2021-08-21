package zmaster587.advancedRocketry.tile;

import net.minecraft.entity.player.EntityPlayer;
import zmaster587.advancedRocketry.api.ARConfiguration;
import zmaster587.advancedRocketry.api.AdvancedRocketryBlocks;
import zmaster587.advancedRocketry.dimension.DimensionManager;
import zmaster587.advancedRocketry.dimension.DimensionProperties;
import zmaster587.advancedRocketry.stations.SpaceObjectManager;
import zmaster587.libVulpes.LibVulpes;
import zmaster587.libVulpes.inventory.modules.ModuleBase;
import zmaster587.libVulpes.inventory.modules.ModuleText;
import zmaster587.libVulpes.tile.TileInventoriedForgePowerMachine;

import java.util.List;

public class TileSolarPanel extends TileInventoriedForgePowerMachine {

	ModuleText text;

	public TileSolarPanel() {
		super(10000, 1);
		text = new ModuleText(60, 40, LibVulpes.proxy.getLocalizedString("msg.solar.collectingEnergy"), 0x2f2f2f);
	}

	@Override
	public boolean canGeneratePower() {
		return world.canBlockSeeSky(this.pos.up()) && world.isDaytime();
	}

	@Override
	public void update() {
		if(canGeneratePower()) {
			if(world.isRemote)
				text.setText(LibVulpes.proxy.getLocalizedString("msg.solar.collectingEnergy") + "\n" + getPowerPerOperation() + " " + LibVulpes.proxy.getLocalizedString("msg.powerunit.rfpertick"));
			if(hasEnoughEnergyBuffer(getPowerPerOperation())) {
				if(!world.isRemote) this.energy.acceptEnergy(getPowerPerOperation(), false);
				onGeneratePower();
			}
			else
				notEnoughBufferForFunction();
		}
		else if(world.isRemote)
			text.setText(LibVulpes.proxy.getLocalizedString("msg.solar.cannotcollectEnergy"));

		if(!world.isRemote)
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
		DimensionProperties properties =DimensionManager.getInstance().getDimensionProperties(world.provider.getDimension());
		double insolationMultiplier = (world.provider.getDimension() == ARConfiguration.getCurrentConfig().spaceDimId) ? SpaceObjectManager.getSpaceManager().getSpaceStationFromBlockCoords(this.pos).getInsolationMultiplier() : properties.getPeakInsolationMultiplier();
		//Slight adjustment to make Earth 0.9995 into a 1.0
		//Then multiplied by two for 520W = 1 RF/t becoming 2 RF/t @ 100% efficiency
		//Makes solar panels not return 0 everywhere
		return (int)Math.min((1.0005d * 2d * ARConfiguration.getCurrentConfig().solarGeneratorMult * insolationMultiplier), 10000);
	}

	@Override
	public void onGeneratePower() {

	}

	@Override
	public String getModularInventoryName() {
		return AdvancedRocketryBlocks.blockSolarGenerator.getLocalizedName();
	}

	@Override
	public boolean canInteractWithContainer(EntityPlayer entity) {
		return true;
	}
}
