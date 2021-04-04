package zmaster587.advancedRocketry.satellite;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import zmaster587.advancedRocketry.api.ARConfiguration;
import zmaster587.advancedRocketry.api.satellite.SatelliteBase;
import zmaster587.libVulpes.LibVulpes;
import zmaster587.libVulpes.api.IUniversalEnergyTransmitter;

public class SatelliteMicrowaveEnergy extends SatelliteBase implements IUniversalEnergyTransmitter {

	byte teir;

	public SatelliteMicrowaveEnergy() {
		super();
	}

	@Override
	public void setProperties(ItemStack satelliteProperties) {
		super.setProperties(satelliteProperties);
	}
	
	@Override
	public String getInfo(World world) {
		return LibVulpes.proxy.getLocalizedString("msg.itemsatellite.microwavestatus");
	}


	@Override
	public String getName() {
		return "Microwave Energy Satellite";
	}

	@Override
	public boolean performAction(PlayerEntity player, World world, BlockPos pos) {
		return false;
	}

	@Override
	public double failureChance() {
		return 0;
	}

	@Override
	public int getEnergyMTU(Direction side) {
		return (int) ((ARConfiguration.getCurrentConfig().microwaveRecieverMulitplier.get()) * battery.extractEnergy(battery.getMaxEnergyStored(), true));
	}

	@Override
	public void setDimensionId(World world) {
		super.setDimensionId(world);
	}
	
	@Override
	public int transmitEnergy(Direction dir, boolean simulate) {
		return getEnergyMTU(Direction.DOWN);
	}

	@Override
	public void writeToNBT(CompoundNBT nbt) {
		super.writeToNBT(nbt);
		nbt.putByte("teir", teir);
	}

	@Override
	public void readFromNBT(CompoundNBT nbt) {
		super.readFromNBT(nbt);
		teir = nbt.getByte("teir");
	}
}
