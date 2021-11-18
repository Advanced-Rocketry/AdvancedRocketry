package zmaster587.advancedRocketry.block.cable;

import net.minecraft.block.Block;
import net.minecraft.util.math.AxisAlignedBB;

public class BlockPipe extends Block {

	private static AxisAlignedBB bb = new AxisAlignedBB(0.15, 0.15, 0.15, 0.85, 0.85, 0.85);
	
	protected BlockPipe(Properties material) {
		super(material);

	}

	/*@Override
	public AxisAlignedBB getBoundingBox(BlockState state, IBlockAccess source,
			BlockPos pos) {
		return bb;
	}
	
	@Override
	public boolean isNormalCube(BlockState state, IBlockAccess world,
			BlockPos pos) {
		return false;
	}

	@Override
	public boolean isOpaqueCube(BlockState state) {
		return false;
	}

	@Override
	public boolean isFullCube(BlockState state) {
		return false;
	}

	@Override
	public boolean hasTileEntity(BlockState state) {
		return true;
	}

	@Override
@ParametersAreNullableByDefault
	public boolean shouldSideBeRendered(BlockState blockState,
			IBlockAccess blockAccess, BlockPos pos, Direction side) {
	public boolean shouldSideBeRendered(IBlockState blockState,
			IBlockAccess blockAccess, BlockPos pos, EnumFacing side) {
		return true;
	}

	@Override
	public void updateTick(World worldIn, BlockPos pos, BlockState state,
			Random rand) {
		super.updateTick(worldIn, pos, state, rand);
		TilePipe pipe = ((TilePipe)worldIn.getTileEntity(pos));

		if (pipe != null && !pipe.isInitialized()) {
			pipe.onPlaced();
			pipe.markDirty();
		}
	}
	
	@Override
	public void onBlockPlacedBy(World worldIn, BlockPos pos, BlockState state,
			LivingEntity placer, ItemStack stack) {
		((TilePipe)worldIn.getTileEntity(pos)).onPlaced();
	}


	@Override
    @ParametersAreNullableByDefault
	public TileEntity createTileEntity(World world, BlockState state) {
		return new TileDataPipe();
	}

	@Override
	public void neighborChanged(BlockState state, World worldIn, BlockPos pos,
			Block blockIn, BlockPos fromPos) {
		((TilePipe)worldIn.getTileEntity(pos)).onPlaced();
	}

	@Override
	public void onNeighborChange(IBlockAccess world, BlockPos pos,
			BlockPos neighbor) {
		((TilePipe)world.getTileEntity(pos)).onNeighborTileChange(neighbor);
	}*/


}