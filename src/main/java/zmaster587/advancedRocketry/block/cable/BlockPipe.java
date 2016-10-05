package zmaster587.advancedRocketry.block.cable;

import java.util.Random;

import zmaster587.advancedRocketry.tile.cables.TileDataPipe;
import zmaster587.advancedRocketry.tile.cables.TilePipe;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockPipe extends Block {

	protected BlockPipe(Material material) {
		super(material);

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
	public boolean shouldSideBeRendered(IBlockState blockState,
			IBlockAccess blockAccess, BlockPos pos, EnumFacing side) {
		return true;
	}

	@Override
	public void updateTick(World worldIn, BlockPos pos, IBlockState state,
			Random rand) {
		super.updateTick(worldIn, pos, state, rand);
		TilePipe pipe = ((TilePipe)worldIn.getTileEntity(pos));

		if (!pipe.isInitialized()) {
			pipe.onPlaced();
			pipe.markDirty();
		}
	}

	@Override
	public void onBlockAdded(World worldIn, BlockPos pos, IBlockState state) {
		super.onBlockAdded(worldIn, pos, state);
		((TilePipe)worldIn.getTileEntity(pos)).onPlaced();

	}
	
	
	@Override
	public TileEntity createTileEntity(World world, IBlockState state) {
		return new TileDataPipe();
	}

	@Override
	public void neighborChanged(IBlockState state, World worldIn, BlockPos pos,
			Block blockIn) {
		if(!worldIn.isRemote)
		((TilePipe)worldIn.getTileEntity(pos)).onPlaced();
	}

	@Override
	public void onNeighborChange(IBlockAccess world, BlockPos pos,
			BlockPos neighbor) {
		((TilePipe)world.getTileEntity(pos)).onNeighborTileChange(neighbor);
	}


}