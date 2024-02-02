package zmaster587.advancedRocketry.mission;

import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import zmaster587.advancedRocketry.AdvancedRocketry;
import zmaster587.advancedRocketry.api.ARConfiguration;
import zmaster587.advancedRocketry.api.AdvancedRocketryItems;
import zmaster587.advancedRocketry.api.DataStorage.DataType;
import zmaster587.advancedRocketry.api.IInfrastructure;
import zmaster587.advancedRocketry.entity.EntityRocket;
import zmaster587.advancedRocketry.item.ItemAsteroidChip;
import zmaster587.advancedRocketry.tile.TileGuidanceComputer;
import zmaster587.advancedRocketry.util.Asteroid;
import zmaster587.advancedRocketry.util.Asteroid.StackEntry;
import zmaster587.libVulpes.util.HashedBlockPosition;

import java.util.LinkedList;
import java.util.List;

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

        if (rocketStats.getDrillingPower() != 0f) {
            int distanceData, compositionData, massData, maxData;

            TileGuidanceComputer computer = rocketStorage.getGuidanceComputer();
            if (computer == null) {
                AdvancedRocketry.logger.warn("Cannot find guidance computer in rocket landing at " + x + ", " + z + " in dim " + launchDimension + ".  Unable to respawn the rocket.");
                return;
            }
            ItemStack stack = computer.getStackInSlot(0);

            if (!stack.isEmpty() && stack.getItem() instanceof ItemAsteroidChip) {

                distanceData = ((ItemAsteroidChip) stack.getItem()).getData(stack, DataType.DISTANCE);
                compositionData = ((ItemAsteroidChip) stack.getItem()).getData(stack, DataType.COMPOSITION);
                massData = ((ItemAsteroidChip) stack.getItem()).getData(stack, DataType.MASS);
                maxData = ((ItemAsteroidChip) stack.getItem()).getMaxData(stack);

                //fill the inventory of the rocket
                if (distanceData / (double) maxData > Math.random()) {
                    ItemStack[] stacks;

                    Asteroid asteroid = ARConfiguration.getCurrentConfig().asteroidTypes.get(((ItemAsteroidChip) stack.getItem()).getType(stack));

                    if (asteroid != null) {

                        List<StackEntry> stacks2 = asteroid.getHarvest(((ItemAsteroidChip) stack.getItem()).getUUID(stack));
                        List<ItemStack> totalStacksList = new LinkedList<>();
                        for (StackEntry entry : stacks2) {

                            if (compositionData / (float) maxData >= Math.random())
                                entry.stack.setCount((int) (entry.stack.getCount() * 1.25f));

                            if (massData / (float) maxData >= Math.random())
                                entry.stack.setCount((int) (entry.stack.getCount() * 1.25f));

                            //if(entry.stack.getMaxStackSize() < entry.stack.stackSize) {
                            for (int i = 0; i < entry.stack.getCount() / entry.stack.getMaxStackSize(); i++) {
                                ItemStack stack2 = new ItemStack(entry.stack.getItem(), entry.stack.getMaxStackSize(), entry.stack.getMetadata());
                                totalStacksList.add(stack2);
                            }
                            //}
                            entry.stack.setCount(entry.stack.getCount() % entry.stack.getMaxStackSize());
                            totalStacksList.add(entry.stack);
                        }

                        stacks = new ItemStack[totalStacksList.size()];
                        totalStacksList.toArray(stacks);

                        for (int i = 0, g = 0; i < rocketStorage.getInventoryTiles().size(); i++) {
                            if (rocketStorage.getInventoryTiles().get(i).hasCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, EnumFacing.UP)) {
                                IItemHandler capabilityItemHandle = rocketStorage.getInventoryTiles().get(i).getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, EnumFacing.UP);

                                for (int offset = 0; offset < capabilityItemHandle.getSlots() && g < stacks.length; offset++, g++) {
                                    if (capabilityItemHandle.getStackInSlot(offset).isEmpty())
                                        capabilityItemHandle.insertItem(offset, stacks[g], false);
                                }
                            } else {
                                IInventory tile = (IInventory) rocketStorage.getInventoryTiles().get(i);


                                for (int offset = 0; offset < tile.getSizeInventory() && g < stacks.length; offset++, g++) {
                                    if (tile.getStackInSlot(offset).isEmpty())
                                        tile.setInventorySlotContents(offset, stacks[g]);
                                }
                            }
                        }
                    }
                }
            }
        }

        rocketStorage.getGuidanceComputer().setInventorySlotContents(0, ItemStack.EMPTY);
        //Return asteroid ID chip
        rocketStorage.getGuidanceComputer().setInventorySlotContents(0, new ItemStack(AdvancedRocketryItems.itemAsteroidChip));
        EntityRocket rocket = new EntityRocket(DimensionManager.getWorld(launchDimension), rocketStorage, rocketStats, x, 999, z);

        World world = DimensionManager.getWorld(launchDimension);
        world.spawnEntity(rocket);
        rocket.setInOrbit(true);
        rocket.setInFlight(true);
        rocket.motionY = -1.0;

        for (HashedBlockPosition i : infrastructureCoords) {
            TileEntity tile = world.getTileEntity(new BlockPos(i.x, i.y, i.z));
            if (tile instanceof IInfrastructure) {
                ((IInfrastructure) tile).unlinkMission();
                rocket.linkInfrastructure(((IInfrastructure) tile));
            }
        }
    }
}
