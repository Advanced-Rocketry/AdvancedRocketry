package zmaster587.advancedRocketry.block;

import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import zmaster587.libVulpes.block.BlockTile;

public class BlockTileRedstoneEmitter extends BlockTile {

    public BlockTileRedstoneEmitter(Class<? extends TileEntity> tileClass,
                                    int guiId) {
        super(tileClass, guiId);
    }

    @Override
    public int getWeakPower(IBlockState blockState, IBlockAccess blockAccess,
                            BlockPos pos, EnumFacing side) {
        return blockState.getValue(STATE) ? 15 : 0;
    }

    @Override
    public boolean canProvidePower(IBlockState state) {
        return true;
    }

    public void setRedstoneState(World world, IBlockState state, BlockPos pos, boolean newState) {
        if (world.getBlockState(pos).getBlock() != this)
            return;

        world.setBlockState(pos, state.withProperty(STATE, newState));
        world.notifyBlockUpdate(pos, state, state, 3);
    }
}
