package zmaster587.advancedRocketry.tile;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import zmaster587.advancedRocketry.api.AdvancedRocketryTileEntityType;
import zmaster587.libVulpes.LibVulpes;
import zmaster587.libVulpes.api.LibvulpesGuiRegistry;
import zmaster587.libVulpes.inventory.ContainerModular;
import zmaster587.libVulpes.inventory.GuiHandler.guiId;
import zmaster587.libVulpes.inventory.modules.ModuleBase;
import zmaster587.libVulpes.inventory.modules.ModuleText;
import zmaster587.libVulpes.tile.TileInventoriedForgePowerMachine;

import java.util.List;

public class TileSolarPanel extends TileInventoriedForgePowerMachine {

	ModuleText text;

	public TileSolarPanel() {
		super(AdvancedRocketryTileEntityType.TILE_SOLAR_PANEL, 10000, 1);
		text = new ModuleText(60, 40, LibVulpes.proxy.getLocalizedString("msg.solar.collectingEnergy"), 0x2f2f2f);
	}

	@Override
	public boolean canGeneratePower() {
		return world.canBlockSeeSky(this.pos.up()) && world.isDaytime();
	}

	@Override
	public void tick() {
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
	public List<ModuleBase> getModules(int ID, PlayerEntity player) {
		List<ModuleBase> modules = super.getModules(ID, player);

		modules.add(text);

		return modules;
	}


	@Override
	public int getPowerPerOperation() {
		return zmaster587.advancedRocketry.api.ARConfiguration.getCurrentConfig().solarGeneratorMult;
	}

	@Override
	public void onGeneratePower() {

	}

	@Override
	public String getModularInventoryName() {
		return "tile.solarGenerator.name";
	}

	@Override
	public boolean canInteractWithContainer(PlayerEntity entity) {
		return true;
	}

	@Override
	public int getModularInvType() {
		return guiId.MODULAR.ordinal();
	}

	@Override
	public ITextComponent getDisplayName() {
		return new TranslationTextComponent(getModularInventoryName());
	}

	@Override
	public Container createMenu(int id, PlayerInventory inv, PlayerEntity player) {
		return new ContainerModular(LibvulpesGuiRegistry.CONTAINER_MODULAR_TILE, id, player, getModules(getModularInvType(), player), this);
	}
}
