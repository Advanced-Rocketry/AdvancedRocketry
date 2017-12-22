package zmaster587.advancedRocketry.satellite;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import zmaster587.advancedRocketry.api.Configuration;
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
		
		return super.acceptsItemInConstruction(item) || SatelliteProperties.Property.BATTERY.isOfType(flag) || (SatelliteProperties.Property.POWER_GEN.isOfType(flag) && SatelliteRegistry.getSatelliteProperty(item).getPowerGeneration() >= 10);
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

	protected int energyCreated(World world) {
		int amt =(int) ((world.getTotalWorldTime() - lastActionTime)*getPowerPerTick());
		lastActionTime = world.getTotalWorldTime();
		return amt;
	}

	public int getPowerPerTick() {
		return satelliteProperties.getPowerGeneration()/10;
	}

	@Override
	public String getName() {
		return "Microwave Energy Satellite";
	}

	@Override
	public boolean performAction(EntityPlayer player, World world, int x,
			int y, int z) {
		return false;
	}

	@Override
	public double failureChance() {
		return 0;
	}

	@Override
	public int getEnergyMTU(ForgeDirection side) {
		return (int) (100* Configuration.microwaveRecieverMulitplier);
	}

	@Override
	public void setDimensionId(World world) {
		super.setDimensionId(world);
		lastActionTime = world.getTotalWorldTime();
	}
	
	@Override
	public int transmitEnergy(ForgeDirection dir, boolean simulate) {
		if(getDimensionId() != -1) {
			World world = net.minecraftforge.common.DimensionManager.getWorld(getDimensionId());
			if(world != null) {
				battery.acceptEnergy(energyCreated(world), simulate);
				int energy = battery.extractEnergy(getEnergyMTU(ForgeDirection.UNKNOWN), simulate);
				return energy;
			}
		}
		return 0;
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
