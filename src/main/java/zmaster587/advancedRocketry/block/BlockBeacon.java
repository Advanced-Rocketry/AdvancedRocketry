package zmaster587.advancedRocketry.block;

import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import zmaster587.advancedRocketry.AdvancedRocketry;
import zmaster587.advancedRocketry.dimension.DimensionManager;
import zmaster587.advancedRocketry.tile.multiblock.TileBeacon;
import zmaster587.libVulpes.block.multiblock.BlockMultiblockMachine;
import zmaster587.libVulpes.tile.multiblock.TileMultiBlock;
import zmaster587.libVulpes.util.HashedBlockPosition;

import java.util.Random;

public class BlockBeacon extends BlockMultiblockMachine {

    public BlockBeacon(Class<? extends TileMultiBlock> tileClass, int guiId) {
        super(tileClass, guiId);
    }

    @Override
    public void breakBlock(World world, BlockPos pos, IBlockState state) {
        TileEntity tile = world.getTileEntity(pos);
        if (tile instanceof TileBeacon && DimensionManager.getInstance().isDimensionCreated(world.provider.getDimension())) {
            DimensionManager.getInstance().getDimensionProperties(world.provider.getDimension()).removeBeaconLocation(world, new HashedBlockPosition(pos));
        }
        super.breakBlock(world, pos, state);
    }

    @SideOnly(Side.CLIENT)
    public void randomDisplayTick(IBlockState stateIn, World worldIn, BlockPos pos, Random rand) {
        if (worldIn.getTileEntity(pos) instanceof TileBeacon && ((TileBeacon) worldIn.getTileEntity(pos)).getMachineEnabled()) {
            EnumFacing enumfacing = stateIn.getValue(FACING);
            for (int i = 0; i < 10; i++)
                AdvancedRocketry.proxy.spawnParticle("reddust", worldIn, pos.getX() - enumfacing.getFrontOffsetX() + worldIn.rand.nextDouble(), pos.getY() + 5 - worldIn.rand.nextDouble(), pos.getZ() - enumfacing.getFrontOffsetZ() + worldIn.rand.nextDouble(), 0, 0, 0);
        }
    }
}
