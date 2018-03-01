package zmaster587.advancedRocketry.world.decoration;

import net.minecraft.block.BlockPistonBase;
import net.minecraft.init.Blocks;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import zmaster587.advancedRocketry.api.AdvancedRocketryBlocks;

public class MapGenSpaceStation {

	public static void generateStation(World world, int blockX, int blockY, int blockZ) {

		BlockPos pos = new BlockPos(blockX, blockY, blockZ); 

		//Center
		world.setBlockState(pos, AdvancedRocketryBlocks.blockConcrete.getDefaultState(), 6);
		world.setBlockState(pos.add(1,0,1), AdvancedRocketryBlocks.blockConcrete.getDefaultState(), 6);
		world.setBlockState(pos.add(-1,0,-1), AdvancedRocketryBlocks.blockConcrete.getDefaultState(), 6);
		world.setBlockState(pos.add(1,0,-1), AdvancedRocketryBlocks.blockConcrete.getDefaultState(), 6);
		world.setBlockState(pos.add(-1,0,1), AdvancedRocketryBlocks.blockConcrete.getDefaultState(), 6);


		for(EnumFacing dir : EnumFacing.HORIZONTALS) {
			world.setBlockState(pos.offset(dir), Blocks.PISTON.getDefaultState().withProperty(BlockPistonBase.FACING, dir), 6);
			generateArm(world, pos, dir);
		}

	}

	private static void generateArm(World world, BlockPos pos, EnumFacing direction) {
		for(int i = 0; i < 8; i++) {
			world.setBlockState(pos.offset(direction, i+2), AdvancedRocketryBlocks.blockConcrete.getDefaultState(),6);
		}

	}
}
