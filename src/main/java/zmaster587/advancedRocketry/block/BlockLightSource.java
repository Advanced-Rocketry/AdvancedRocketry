package zmaster587.advancedRocketry.block;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;

/**
 * InvisLight source
 *
 */
public class BlockLightSource extends Block {

	public BlockLightSource() {
		super(Material.GLASS);
		setTranslationKey("lightSource");
		setLightLevel(1F);
	}

	@Override
	public boolean canCollideCheck(IBlockState state, boolean hitIfLiquid) {
		 return false;
	}

	@Override
	public AxisAlignedBB getCollisionBoundingBox(IBlockState blockState,
			IBlockAccess worldIn, BlockPos pos) {
		return null;
	}
	
	@Override
	public int getLightValue(IBlockState state, IBlockAccess world, BlockPos pos) {
		return 15;
	}
    
	@Override
	public boolean isOpaqueCube(IBlockState state) {
		return false;
	}

	@Override
	public EnumBlockRenderType getRenderType(IBlockState state) {
		return EnumBlockRenderType.INVISIBLE;
	}
}