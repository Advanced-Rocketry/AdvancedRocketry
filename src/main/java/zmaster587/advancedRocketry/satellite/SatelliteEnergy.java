package zmaster587.advancedRocketry.satellite;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import zmaster587.advancedRocketry.AdvancedRocketry;
import zmaster587.advancedRocketry.api.ARConfiguration;
import zmaster587.advancedRocketry.api.Constants;
import zmaster587.advancedRocketry.api.SatelliteRegistry;
import zmaster587.advancedRocketry.api.satellite.SatelliteBase;
import zmaster587.advancedRocketry.api.satellite.SatelliteProperties;
import zmaster587.libVulpes.api.IUniversalEnergyTransmitter;
import zmaster587.libVulpes.util.UniversalBattery;

public class SatelliteEnergy extends SatelliteBase implements IUniversalEnergyTransmitter {

	UniversalBattery battery;
	long lastActionTime;
	byte teir;

	public SatelliteEnergy() {
	}

	@Override
	public boolean acceptsItemInConstruction(ItemStack item) {
		int flag = SatelliteRegistry.getSatelliteProperty(item).getPropertyFlag();
		
		return super.acceptsItemInConstruction(item) || SatelliteProperties.Property.BATTERY.isOfType(flag) || (SatelliteProperties.Property.POWER_GEN.isOfType(flag));
	}
	
	@Override
	public void setProperties(ItemStack satelliteProperties) {
		super.setProperties(satelliteProperties);
		battery = new UniversalBattery(Math.max(this.satelliteProperties.getPowerStorage(),1));
	}
	
	@Override
	public String getInfo(World world) {
		return "Collecting Energy";
	}

	protected int energyCreated(boolean simulate) {
		int amt =(int) ((AdvancedRocketry.proxy.getWorldTimeUniversal(0) - lastActionTime)*getPowerPerTick());
		if(!simulate)
			lastActionTime = AdvancedRocketry.proxy.getWorldTimeUniversal(0);
		return amt;
	}

	public int getPowerPerTick() {
		return satelliteProperties.getPowerGeneration();
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
		return (int) (100* ARConfiguration.getCurrentConfig().microwaveRecieverMulitplier);
	}

	@Override
	public void setDimensionId(World world) {
		super.setDimensionId(world);
		lastActionTime = world.getTotalWorldTime();
	}
	
	@Override
	public int transmitEnergy(EnumFacing dir, boolean simulate) {
				int energyCreated = energyCreated(simulate);
				battery.acceptEnergy(Math.max((energyCreated - getEnergyMTU(EnumFacing.DOWN)), 0), simulate);
				int energy = battery.extractEnergy(Math.max(getEnergyMTU(EnumFacing.DOWN) - energyCreated,0), simulate);
				return energy + energyCreated;
	}

	@Override
	public void writeToNBT(NBTTagCompound nbt) {
		super.writeToNBT(nbt);

		battery.writeToNBT(nbt);
		nbt.setLong("lastActionTime", lastActionTime);
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
		
		lastActionTime = nbt.getLong("lastActionTime");
		teir = nbt.getByte("teir");
	}
}
