package zmaster587.advancedRocketry.tile;

import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
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
		text = new ModuleText(60, 40, LibVulpes.proxy.getLocalizedString("msg.solar=collectingEnergy"), 0x2f2f2f);
	}

	@Override
	public boolean canGeneratePower() {
		return world.canBlockSeeSky(this.pos.up()) && world.isDaytime();
	}

	@Override
	public void update() {
		if(canGeneratePower()) {
			if(world.isRemote)
				text.setText(LibVulpes.proxy.getLocalizedString("msg.solar=collectingEnergy") + "\n" + getPowerPerOperation() + " " + LibVulpes.proxy.getLocalizedString("msg.powerunit.rfpertick"));
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
		return zmaster587.advancedRocketry.api.Configuration.solarGeneratorMult;
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
