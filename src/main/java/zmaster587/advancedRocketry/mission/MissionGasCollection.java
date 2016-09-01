package zmaster587.advancedRocketry.mission;

import java.util.LinkedList;
import java.util.List;

import net.minecraft.init.Blocks;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.IFluidHandler;
import net.minecraftforge.oredict.OreDictionary;
import zmaster587.advancedRocketry.api.Configuration;
import zmaster587.advancedRocketry.api.IInfrastructure;
import zmaster587.advancedRocketry.api.DataStorage.DataType;
import zmaster587.advancedRocketry.entity.EntityRocket;
import zmaster587.advancedRocketry.entity.EntityStationDeployedRocket;
import zmaster587.advancedRocketry.item.ItemAsteroidChip;
import zmaster587.libVulpes.LibVulpes;
import zmaster587.libVulpes.util.BlockPosition;

public class MissionGasCollection extends MissionResourceCollection {

	public MissionGasCollection() {
		super();
	}

	public MissionGasCollection(long l, EntityRocket entityRocket, LinkedList<IInfrastructure> connectedInfrastructure) {
		super(l, entityRocket, connectedInfrastructure);
	}
	
	@Override
	public String getName() {
		return LibVulpes.proxy.getLocalizedString("mission.gascollection.name");
	}
	
	@Override
	public void onMissionComplete() {
		
		int amountOfGas = 128000;
		Fluid type = FluidRegistry.getFluid("hydrogen");
		//Fill gas tanks
		for(TileEntity tile : this.rocketStorage.getFluidTiles()) {
			amountOfGas -= ((IFluidHandler)tile).fill(ForgeDirection.UNKNOWN, new FluidStack(type, amountOfGas), true);
		
			if(amountOfGas == 0)
				break;
		}
		
		EntityStationDeployedRocket rocket = new EntityStationDeployedRocket(DimensionManager.getWorld(launchDimension), rocketStorage, rocketStats, x, y, z);

		World world = DimensionManager.getWorld(launchDimension);
		rocket.readMissionPersistantNBT(missionPersistantNBT);
		
		ForgeDirection dir = rocket.forwardDirection;
		
		rocket.setPosition(dir.offsetX*64d + rocket.launchLocation.x + (rocketStorage.getSizeX() % 2 == 0 ? 0 : 0.5d), y, dir.offsetZ*64d + rocket.launchLocation.z + (rocketStorage.getSizeZ() % 2 == 0 ? 0 : 0.5d));
		world.spawnEntityInWorld(rocket);
		rocket.setInOrbit(true);
		rocket.setInFlight(true);
		//rocket.motionY = -1.0;

		for(BlockPosition i : infrastructureCoords) {
			TileEntity tile = world.getTileEntity(i.x, i.y, i.z);
			if(tile instanceof IInfrastructure) {
				((IInfrastructure)tile).unlinkMission();
				rocket.linkInfrastructure(((IInfrastructure)tile));
			}
		}
	}
}
