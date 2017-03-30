package zmaster587.advancedRocketry.mission;

import java.util.LinkedList;
import java.util.List;

import net.minecraft.init.Blocks;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.oredict.OreDictionary;
import zmaster587.advancedRocketry.api.Configuration;
import zmaster587.advancedRocketry.api.DataStorage.DataType;
import zmaster587.advancedRocketry.api.IInfrastructure;
import zmaster587.advancedRocketry.entity.EntityRocket;
import zmaster587.advancedRocketry.item.ItemAsteroidChip;
import zmaster587.libVulpes.util.BlockPosition;

public class MissionOreMining extends MissionResourceCollection {


	public MissionOreMining() {
		super();
	}

	public MissionOreMining(long l, EntityRocket entityRocket,
			LinkedList<IInfrastructure> connectedInfrastructure) {
		super(l, entityRocket, connectedInfrastructure);
	}

	@Override
	public void onMissionComplete() {

		if(rocketStats.getDrillingPower() != 0f) {
			int distanceData, compositionData, massData, maxData;

			ItemStack stack = rocketStorage.getGuidanceComputer().getStackInSlot(0);

			if(stack != null && stack.getItem() instanceof ItemAsteroidChip) {

				distanceData = ((ItemAsteroidChip)stack.getItem()).getData(stack,DataType.DISTANCE);
				compositionData = ((ItemAsteroidChip)stack.getItem()).getData(stack,DataType.COMPOSITION);
				massData = ((ItemAsteroidChip)stack.getItem()).getData(stack,DataType.MASS);
				maxData = ((ItemAsteroidChip)stack.getItem()).getMaxData(stack);

				//fill the inventory of the rocket
				if(distanceData/(double)maxData > Math.random()) {
					int totalStacks = (int) ((1+(massData/100f))*Configuration.asteroidMiningMult*(Math.random()/0.5f + 0.5f)*5);
					ItemStack[] stacks = new ItemStack[totalStacks];
					for (int i = 0; i < totalStacks; i++) {
						if((compositionData/(double)maxData)*0.9d > Math.random()) {
							String oreDictName = Configuration.standardAsteroidOres.get((int)(Math.random()*Configuration.standardAsteroidOres.size()));
							List<ItemStack> ores = OreDictionary.getOres(oreDictName);
							if(ores != null && !ores.isEmpty()) {
								stacks[i] = ores.get(0).copy();
								stacks[i].stackSize = stacks[i].getMaxStackSize();
								continue;
							}
						}

						stacks[i] = new ItemStack(Blocks.stone,64);
					}

					for(int i = 0,  g = 0; i < rocketStorage.getInventoryTiles().size(); i++) {
						IInventory tile = (IInventory) rocketStorage.getInventoryTiles().get(i);


						for(int offset = 0; offset < tile.getSizeInventory() && g < totalStacks; offset++, g++) {
							if(tile.getStackInSlot(offset) == null)
								tile.setInventorySlotContents(offset, stacks[g]);
						}
					}
				}
			}
		}

		rocketStorage.getGuidanceComputer().setInventorySlotContents(0, null);
		EntityRocket rocket = new EntityRocket(DimensionManager.getWorld(launchDimension), rocketStorage, rocketStats, x, 999, z);

		World world = DimensionManager.getWorld(launchDimension);
		world.spawnEntityInWorld(rocket);
		rocket.setInOrbit(true);
		rocket.setInFlight(true);
		rocket.motionY = -1.0;

		for(BlockPosition i : infrastructureCoords) {
			TileEntity tile = world.getTileEntity(i.x, i.y, i.z);
			if(tile instanceof IInfrastructure) {
				((IInfrastructure)tile).unlinkMission();
				rocket.linkInfrastructure(((IInfrastructure)tile));
			}
		}
	}
}
