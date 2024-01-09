package zmaster587.advancedRocketry.mission;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import zmaster587.advancedRocketry.api.ARConfiguration;
import zmaster587.advancedRocketry.api.IInfrastructure;
import zmaster587.advancedRocketry.api.fuel.FuelRegistry;
import zmaster587.advancedRocketry.entity.EntityRocket;
import zmaster587.advancedRocketry.entity.EntityStationDeployedRocket;
import zmaster587.libVulpes.LibVulpes;
import zmaster587.libVulpes.util.HashedBlockPosition;

import java.util.LinkedList;

public class MissionGasCollection extends MissionResourceCollection {


	private Fluid gasFluid;
	public MissionGasCollection() {
		super();
	}

	public MissionGasCollection(long l, EntityRocket entityRocket, LinkedList<IInfrastructure> connectedInfrastructure, Fluid gasFluid) {
		super((long) (l*ARConfiguration.getCurrentConfig().gasCollectionMult), entityRocket, connectedInfrastructure);
		this.gasFluid = gasFluid;
	}

	@Override
	public String getName() {
		return LibVulpes.proxy.getLocalizedString("mission.gascollection.name");
	}

	@Override
	public void onMissionComplete() {

		if((int)rocketStats.getStatTag("intakePower") > 0 && gasFluid != null) {
			Fluid type = gasFluid;//FluidRegistry.getFluid("hydrogen");
			//Fill gas tanks
			for(TileEntity tile : this.rocketStorage.getFluidTiles()) {
				tile.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, null).fill(new FluidStack(type, 64000), true);
			}
		}

		World world = DimensionManager.getWorld(launchDimension);
		if (world == null)
		{
			DimensionManager.initDimension(launchDimension);
			world = DimensionManager.getWorld(launchDimension);
		}
		
		EntityStationDeployedRocket rocket = new EntityStationDeployedRocket(world, rocketStorage, rocketStats, x, y, z);

		FuelRegistry.FuelType fuelType = rocket.getRocketFuelType();
		if(fuelType != null) {
			rocket.setFuelAmount(fuelType, 0);
			if (fuelType == FuelRegistry.FuelType.LIQUID_BIPROPELLANT)
				rocket.setFuelAmount(FuelRegistry.FuelType.LIQUID_OXIDIZER, 0);
		}
		rocket.readMissionPersistentNBT(missionPersistantNBT);

		EnumFacing dir = rocket.forwardDirection;
		rocket.forceSpawn = true;

		rocket.setPosition(dir.getXOffset()*64d + rocket.launchLocation.x + (rocketStorage.getSizeX() % 2 == 0 ? 0 : 0.5d), y, dir.getZOffset()*64d + rocket.launchLocation.z + (rocketStorage.getSizeZ() % 2 == 0 ? 0 : 0.5d));
		world.spawnEntity(rocket);
		rocket.setInOrbit(true);
		rocket.setInFlight(true);
		//rocket.motionY = -1.0;

		for(HashedBlockPosition i : infrastructureCoords) {
			TileEntity tile = world.getTileEntity(new BlockPos(i.x, i.y, i.z));
			if(tile instanceof IInfrastructure) {
				((IInfrastructure)tile).unlinkMission();
				rocket.linkInfrastructure(((IInfrastructure)tile));
			}
		}
	}
	
	@Override
	public void writeToNBT(NBTTagCompound nbt) {
		super.writeToNBT(nbt);
		nbt.setString("gas", gasFluid.getName());
	}
	
	@Override
	public void readFromNBT(NBTTagCompound nbt) {
		super.readFromNBT(nbt);
		gasFluid = FluidRegistry.getFluid(nbt.getString("gas"));
	}
}
