package zmaster587.advancedRocketry.block.cable;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import zmaster587.advancedRocketry.tile.cables.TileDataPipe;
import zmaster587.advancedRocketry.tile.cables.TilePipe;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNullableByDefault;
import java.util.Random;

public class BlockPipe extends Block {

    private static AxisAlignedBB bb = new AxisAlignedBB(0.15, 0.15, 0.15, 0.85, 0.85, 0.85);

    protected BlockPipe(Material material) {
        super(material);

    }

    @Override
    @Nonnull
    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source,
                                        BlockPos pos) {
        return bb;
    }

    @Override
    public boolean isNormalCube(IBlockState state, IBlockAccess world,
                                BlockPos pos) {
        return false;
    }

    @Override
    public boolean isOpaqueCube(IBlockState state) {
        return false;
    }

    @Override
    public boolean isFullCube(IBlockState state) {
        return false;
    }

    @Override
    public boolean hasTileEntity(IBlockState state) {
        return true;
    }

    @Override
    @ParametersAreNullableByDefault
    public boolean shouldSideBeRendered(IBlockState blockState,
                                        IBlockAccess blockAccess, BlockPos pos, EnumFacing side) {
        return true;
    }

    @Override
    public void updateTick(World worldIn, BlockPos pos, IBlockState state,
                           Random rand) {
        super.updateTick(worldIn, pos, state, rand);
        TilePipe pipe = ((TilePipe) worldIn.getTileEntity(pos));

        if (pipe != null && !pipe.isInitialized()) {
            pipe.onPlaced();
            pipe.markDirty();
        }
    }

    @Override
    public void onBlockPlacedBy(World worldIn, BlockPos pos, IBlockState state,
                                EntityLivingBase placer, @Nonnull ItemStack stack) {
        ((TilePipe) worldIn.getTileEntity(pos)).onPlaced();
    }


    @Override
    @ParametersAreNullableByDefault
    public TileEntity createTileEntity(World world, IBlockState state) {
        return new TileDataPipe();
    }

    @Override
    public void onNeighborChange(IBlockAccess world, BlockPos pos, BlockPos neighbor) {
        ((TilePipe) world.getTileEntity(pos)).onNeighborTileChange(neighbor);
    }


}