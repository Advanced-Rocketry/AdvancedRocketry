package zmaster587.advancedRocketry.block;

import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import zmaster587.libVulpes.block.RotatableBlock;

public class BlockRotatableModel extends RotatableBlock {
	
	public BlockRotatableModel(Material par2Material) {
		super(par2Material);
		
	}
	
	 @Override
	public boolean isOpaqueCube(IBlockState state) {
		return false;
	}
	
	
}
