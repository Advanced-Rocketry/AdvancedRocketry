package zmaster587.advancedRocketry.satellite;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import zmaster587.advancedRocketry.api.ARConfiguration;
import zmaster587.advancedRocketry.api.Constants;
import zmaster587.advancedRocketry.api.SatelliteRegistry;
import zmaster587.advancedRocketry.api.satellite.SatelliteBase;
import zmaster587.advancedRocketry.api.satellite.SatelliteProperties;
import zmaster587.libVulpes.api.IUniversalEnergyTransmitter;
import zmaster587.libVulpes.util.UniversalBattery;
import zmaster587.libVulpes.util.ZUtils;

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

	protected int energyCreated(World world, boolean simulate) {
		int amt =(int) ((world.getGameTime() - lastActionTime)*getPowerPerTick());
		if(!simulate)
			lastActionTime = world.getGameTime();
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
	public boolean performAction(PlayerEntity player, World world, BlockPos pos) {
		return false;
	}

	@Override
	public double failureChance() {
		return 0;
	}

	@Override
	public int getEnergyMTU(Direction side) {
		return (int) (100* ARConfiguration.getCurrentConfig().microwaveRecieverMulitplier.get());
	}

	@Override
	public void setDimensionId(World world) {
		super.setDimensionId(world);
		lastActionTime = world.getGameTime();
	}
	
	@Override
	public int transmitEnergy(Direction dir, boolean simulate) {
		if(getDimensionId().get() != Constants.INVALID_PLANET) {
			World world = ZUtils.getWorld(getDimensionId().get());
			if(world != null) {
				int energyCreated = energyCreated(world, simulate);
				battery.acceptEnergy(Math.max((energyCreated - getEnergyMTU(Direction.DOWN)), 0), simulate);
				int energy = battery.extractEnergy(Math.max(getEnergyMTU(Direction.DOWN) - energyCreated,0), simulate);
				return energy + energyCreated;
			}
		}
		return 0;
	}

	@Override
	public void writeToNBT(CompoundNBT nbt) {
		super.writeToNBT(nbt);

		battery.write(nbt);
		nbt.putLong("lastActionTime", lastActionTime);
		nbt.putByte("teir", teir);
	}

	@Override
	public void readFromNBT(CompoundNBT nbt) {
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
