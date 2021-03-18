package zmaster587.advancedRocketry.mission;

import net.minecraft.fluid.Fluid;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler.FluidAction;
import net.minecraftforge.registries.ForgeRegistries;
import zmaster587.advancedRocketry.api.ARConfiguration;
import zmaster587.advancedRocketry.api.IInfrastructure;
import zmaster587.advancedRocketry.entity.EntityRocket;
import zmaster587.advancedRocketry.entity.EntityStationDeployedRocket;
import zmaster587.libVulpes.LibVulpes;
import zmaster587.libVulpes.util.HashedBlockPosition;
import zmaster587.libVulpes.util.ZUtils;

import java.util.LinkedList;

public class MissionGasCollection extends MissionResourceCollection {


	Fluid gasFluid;
	public MissionGasCollection() {
		super();
	}

	public MissionGasCollection(long l, EntityRocket entityRocket, LinkedList<IInfrastructure> connectedInfrastructure, Fluid gasFluid) {
		super((long) (l*ARConfiguration.getCurrentConfig().gasCollectionMult.get()), entityRocket, connectedInfrastructure);
		this.gasFluid = gasFluid;
	}

	@Override
	public String getName() {
		return LibVulpes.proxy.getLocalizedString("mission.gascollection.name");
	}

	@Override
	public void onMissionComplete() {

		if((int)rocketStats.getStatTag("intakePower") > 0 && gasFluid != null) {
			int amountOfGas = Integer.MAX_VALUE;
			Fluid type = gasFluid;//FluidRegistry.getFluid("hydrogen");
			//Fill gas tanks
			for(TileEntity tile : this.rocketStorage.getFluidTiles()) {
				amountOfGas -= ((IFluidHandler)tile).fill(new FluidStack(type, amountOfGas), FluidAction.EXECUTE);

				if(amountOfGas == 0)
					break;
			}
		}

		World world = ZUtils.getWorld(launchDimension);
		if (world == null)
		{
			world = ZUtils.getWorld(launchDimension);
		}
		
		EntityStationDeployedRocket rocket = new EntityStationDeployedRocket(world, rocketStorage, rocketStats, x, y, z);
		rocket.setFuelAmountMonoproellant(0);
		rocket.setFuelAmountBipropellant(0);
		rocket.setFuelAmountOxidizer(0);
		rocket.readMissionPersistantNBT(missionPersistantNBT);

		Direction dir = rocket.forwardDirection;
		rocket.forceSpawn = true;

		rocket.setPosition(dir.getXOffset()*64d + rocket.launchLocation.x + (rocketStorage.getSizeX() % 2 == 0 ? 0 : 0.5d), y, dir.getZOffset()*64d + rocket.launchLocation.z + (rocketStorage.getSizeZ() % 2 == 0 ? 0 : 0.5d));
		world.addEntity(rocket);
		rocket.setInOrbit(true);
		rocket.setInFlight(true);
		//rocket.getMotion().y = -1.0;

		for(HashedBlockPosition i : infrastructureCoords) {
			TileEntity tile = world.getTileEntity(new BlockPos(i.x, i.y, i.z));
			if(tile instanceof IInfrastructure) {
				((IInfrastructure)tile).unlinkMission();
				rocket.linkInfrastructure(((IInfrastructure)tile));
			}
		}
	}
	
	@Override
	public void writeToNBT(CompoundNBT nbt) {
		super.writeToNBT(nbt);
		nbt.putString("gas", gasFluid.getRegistryName().toString());
	}
	
	@Override
	public void readFromNBT(CompoundNBT nbt) {
		super.readFromNBT(nbt);
		gasFluid = ForgeRegistries.FLUIDS.getValue(new ResourceLocation(nbt.getString("gas")));
	}
}
