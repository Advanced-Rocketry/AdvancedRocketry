package zmaster587.advancedRocketry.tile;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import zmaster587.advancedRocketry.api.ARConfiguration;
import zmaster587.advancedRocketry.api.AdvancedRocketryTileEntityType;
import zmaster587.advancedRocketry.dimension.DimensionManager;
import zmaster587.advancedRocketry.dimension.DimensionProperties;
import zmaster587.advancedRocketry.stations.SpaceObjectManager;
import zmaster587.libVulpes.LibVulpes;
import zmaster587.libVulpes.api.LibvulpesGuiRegistry;
import zmaster587.libVulpes.inventory.ContainerModular;
import zmaster587.libVulpes.inventory.GuiHandler;
import zmaster587.libVulpes.inventory.GuiHandler.guiId;
import zmaster587.libVulpes.inventory.modules.ModuleBase;
import zmaster587.libVulpes.inventory.modules.ModuleText;
import zmaster587.libVulpes.tile.TileInventoriedForgePowerMachine;
import zmaster587.libVulpes.util.ZUtils;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;

public class TileSolarGenerator extends TileInventoriedForgePowerMachine {

	ModuleText text;

	public TileSolarGenerator() {
		super(AdvancedRocketryTileEntityType.TILE_SOLAR_PANEL, 10000, 1);
		text = new ModuleText(60, 40, LibVulpes.proxy.getLocalizedString("msg.solar.collectingenergy"), 0x2f2f2f);
	}

	@Override
	public boolean canGeneratePower() {
		return world.canBlockSeeSky(this.pos.up()) && world.isDaytime();
	}

	@Override
	public void tick() {
		if(canGeneratePower()) {
			if(world.isRemote)
				text.setText(LibVulpes.proxy.getLocalizedString("msg.solar.collectingenergy") + "\n" + getPowerPerOperation() + " " + LibVulpes.proxy.getLocalizedString("msg.powerunit.rfpertick"));
			if(hasEnoughEnergyBuffer(getPowerPerOperation())) {
				if(!world.isRemote) this.energy.acceptEnergy(getPowerPerOperation(), false);
				onGeneratePower();
			}
			else
				notEnoughBufferForFunction();
		} else if(world.isRemote)
			text.setText(LibVulpes.proxy.getLocalizedString("msg.solar.cannotcollectenergy"));

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
		DimensionProperties properties =DimensionManager.getInstance().getDimensionProperties(world);
		double insolationMultiplier = (ZUtils.getDimensionIdentifier(world) != null && ZUtils.getDimensionIdentifier(world).equals(DimensionManager.spaceId)) ? SpaceObjectManager.getSpaceManager().getSpaceStationFromBlockCoords(this.pos).getInsolationMultiplier() : properties.getPeakInsolationMultiplier();
		//Slight adjustment to make Earth 0.9995 into a 1.0
		return (int)Math.min((1.0005d * 2d * ARConfiguration.getCurrentConfig().solarGeneratorMult.get() * insolationMultiplier), 10000);
	}

	@Override
	public void onGeneratePower() {

	}

	@Override
	public String getModularInventoryName() {
		return "block.advancedrocketry.solargenerator";
	}

	@Override
	public boolean canInteractWithContainer(PlayerEntity entity) {
		return true;
	}

	@Override
	public GuiHandler.guiId getModularInvType() {
		return guiId.MODULAR;
	}

	@Nonnull
	@Override
	public ITextComponent getDisplayName() {
		return new TranslationTextComponent(getModularInventoryName());
	}

	@Override
	@ParametersAreNonnullByDefault
	public Container createMenu(int id, PlayerInventory inv, PlayerEntity player) {
		return new ContainerModular(LibvulpesGuiRegistry.CONTAINER_MODULAR_TILE, id, player, getModules(getModularInvType().ordinal(), player), this, getModularInvType());
	}
}
