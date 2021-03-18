package zmaster587.advancedRocketry.satellite;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import zmaster587.advancedRocketry.api.ARConfiguration;
import zmaster587.advancedRocketry.api.satellite.SatelliteBase;
import zmaster587.libVulpes.api.IUniversalEnergyTransmitter;
import zmaster587.libVulpes.util.UniversalBattery;

public class SatelliteMicrowaveEnergy extends SatelliteBase implements IUniversalEnergyTransmitter {

	byte teir;

	public SatelliteMicrowaveEnergy() {
	}
	
	@Override
	public void setProperties(ItemStack satelliteProperties) {
		super.setProperties(satelliteProperties);
	}
	
	@Override
	public String getInfo(World world) {
		return "Collecting Energy";
	}


	@Override
	public String getName() {
		return "Microwave Energy Satellite";
	}

	@Override
	public boolean performAction(EntityPlayer player, World world, BlockPos pos) {
		return false;
	}

	@Override
	public double failureChance() {
		return 0;
	}

	@Override
	public int getEnergyMTU(EnumFacing side) {
		return (int) (ARConfiguration.getCurrentConfig().microwaveRecieverMulitplier) * battery.extractEnergy(battery.getMaxEnergyStored(), true);
	}

	@Override
	public void setDimensionId(World world) {
		super.setDimensionId(world);
	}
	
	@Override
	public int transmitEnergy(EnumFacing dir, boolean simulate) {
		return getEnergyMTU(EnumFacing.DOWN);
	}

	@Override
	public void writeToNBT(NBTTagCompound nbt) {
		super.writeToNBT(nbt);

		battery.writeToNBT(nbt);
		nbt.setByte("teir", teir);
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt) {
		super.readFromNBT(nbt);

		if(battery == null)
			battery = new UniversalBattery();
		battery.readFromNBT(nbt);
		
		//Fix breakages with earlier version of the mod
		if(battery.getMaxEnergyStored() != this.satelliteProperties.getPowerStorage() || battery.getMaxEnergyStored() == 0)
			battery.setMaxEnergyStored(Math.max(this.satelliteProperties.getPowerStorage(),1));

		teir = nbt.getByte("teir");
	}
}
