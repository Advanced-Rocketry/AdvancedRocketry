package zmaster587.advancedRocketry.tile;

import net.minecraft.block.state.IBlockState;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.BlockPos;
import zmaster587.advancedRocketry.AdvancedRocketry;
import zmaster587.advancedRocketry.api.AdvancedRocketryBlocks;
import zmaster587.libVulpes.block.BlockFullyRotatable;

public class TileForceFieldProjector extends TileEntity implements ITickable {

    private final short MAX_RANGE = 32;
    private short extensionRange;

    public TileForceFieldProjector() {
        extensionRange = 0;
    }


    public void destroyField(EnumFacing facing) {
        while (extensionRange > 0) {
            BlockPos nextPos = pos.offset(facing, extensionRange);

            if (world.getBlockState(nextPos).getBlock() == AdvancedRocketryBlocks.blockForceField)
                world.setBlockToAir(nextPos);
            extensionRange--;
        }
    }

    @Override
    public void update() {
        if (world.getTotalWorldTime() % 5 == 0) {
            if (world.isBlockPowered(getPos())) {
                if (extensionRange <= MAX_RANGE) {
                    if (extensionRange == 0)
                        extensionRange = 1;

                    IBlockState state = world.getBlockState(getPos());

                    if (state.getBlock() == AdvancedRocketryBlocks.blockForceFieldProjector) {
                        EnumFacing facing = BlockFullyRotatable.getFront(state);
                        BlockPos nextPos = pos.offset(facing, extensionRange);

                        if (world.getBlockState(nextPos).getBlock().isReplaceable(world, nextPos)) {
                            world.setBlockState(nextPos, AdvancedRocketryBlocks.blockForceField.getDefaultState());
                        }

                        if (world.getBlockState(nextPos).getBlock() == AdvancedRocketryBlocks.blockForceField) {
                            extensionRange++;
                        }
                    }
                }
            } else if (extensionRange > 0) {
                IBlockState state = world.getBlockState(getPos());

                if (state.getBlock() == AdvancedRocketryBlocks.blockForceFieldProjector) {
                    EnumFacing facing = BlockFullyRotatable.getFront(state);
                    BlockPos nextPos;

                    //check to make sure the force field that was deleted last time stayed deleted
                    if (extensionRange < MAX_RANGE) {
                        nextPos = pos.offset(facing, extensionRange + 1);

                        if (world.getBlockState(nextPos).getBlock() == AdvancedRocketryBlocks.blockForceField) {
                            world.setBlockToAir(nextPos);
                            AdvancedRocketry.logger.warn("Force field projector at " + pos + " found a force field block at " + nextPos + " that didn't stay deleted on retraction! Attempting deletion again.");
                            return;
                        }
                    } else if (extensionRange > MAX_RANGE)
                        extensionRange = MAX_RANGE;

                    nextPos = pos.offset(facing, extensionRange);

                    if (world.getBlockState(nextPos).getBlock() == AdvancedRocketryBlocks.blockForceField) {
                        world.setBlockToAir(nextPos);
                    }

                    extensionRange--;
                }
            }
        }
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound nbt) {
        nbt.setShort("ext", extensionRange);
        return super.writeToNBT(nbt);
    }

    @Override
    public void readFromNBT(NBTTagCompound nbt) {
        extensionRange = nbt.getShort("ext");
        super.readFromNBT(nbt);
    }

}
