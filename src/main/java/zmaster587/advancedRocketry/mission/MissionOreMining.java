package zmaster587.advancedRocketry.mission;

import java.util.LinkedList;
import java.util.List;

import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.DimensionManager;
import zmaster587.advancedRocketry.api.Configuration;
import zmaster587.advancedRocketry.api.DataStorage.DataType;
import zmaster587.advancedRocketry.api.IInfrastructure;
import zmaster587.advancedRocketry.entity.EntityRocket;
import zmaster587.advancedRocketry.item.ItemAsteroidChip;
import zmaster587.advancedRocketry.util.AsteroidSmall.StackEntry;
import zmaster587.libVulpes.util.HashedBlockPosition;

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

			if(stack != null) {

				distanceData = ((ItemAsteroidChip)stack.getItem()).getData(stack,DataType.DISTANCE);
				compositionData = ((ItemAsteroidChip)stack.getItem()).getData(stack,DataType.COMPOSITION);
				massData = ((ItemAsteroidChip)stack.getItem()).getData(stack,DataType.MASS);
				maxData = ((ItemAsteroidChip)stack.getItem()).getMaxData(stack);

				//fill the inventory of the rocket
				if(distanceData/(double)maxData > Math.random()) {
					ItemStack[] stacks;
					List<StackEntry> stacks2 = Configuration.asteroidTypes.get(((ItemAsteroidChip)stack.getItem()).getType(stack)).getHarvest(((ItemAsteroidChip)stack.getItem()).getUUID(stack));
					List<ItemStack> totalStacksList = new LinkedList<ItemStack>();
					for(StackEntry entry : stacks2) {
						
						if(compositionData/(float)maxData >= Math.random())
							entry.stack.stackSize *= 1.25f;
						
						if(massData/(float)maxData >= Math.random())
							entry.stack.stackSize *= 1.25f;
						
						//if(entry.stack.getMaxStackSize() < entry.stack.stackSize) {
							for(int i = 0; i < entry.stack.stackSize/entry.stack.getMaxStackSize(); i++) {
								ItemStack stack2 = new ItemStack(entry.stack.getItem(), entry.stack.getMaxStackSize(), entry.stack.getMetadata());
								totalStacksList.add(stack2);
							}
						//}
						entry.stack.stackSize %= entry.stack.getMaxStackSize();
						totalStacksList.add(entry.stack);
					}

					stacks = new ItemStack[totalStacksList.size()];
					totalStacksList.toArray(stacks);
					
					for(int i = 0,  g = 0; i < rocketStorage.getInventoryTiles().size(); i++) {
						IInventory tile = (IInventory) rocketStorage.getInventoryTiles().get(i);


						for(int offset = 0; offset < tile.getSizeInventory() && g < stacks.length; offset++, g++) {
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

		for(HashedBlockPosition i : infrastructureCoords) {
			TileEntity tile = world.getTileEntity(new BlockPos(i.x, i.y, i.z));
			if(tile instanceof IInfrastructure) {
				((IInfrastructure)tile).unlinkMission();
				rocket.linkInfrastructure(((IInfrastructure)tile));
			}
		}
	}
}
