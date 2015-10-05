package zmaster587.advancedRocketry.world.decoration;

import zmaster587.advancedRocketry.api.AdvRocketryBlocks;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

public class MapGenSpaceStation {
	
	public static void generateStation(World world, int blockX, int blockY, int blockZ) {
		
		//Center
		world.setBlock(blockX, blockY, blockZ, AdvRocketryBlocks.blockConcrete, 0, 6);
		world.setBlock(blockX + 1, blockY, blockZ + 1, AdvRocketryBlocks.blockConcrete, 0, 6);
		world.setBlock(blockX - 1, blockY, blockZ - 1, AdvRocketryBlocks.blockConcrete, 0, 6);
		world.setBlock(blockX + 1, blockY, blockZ - 1, AdvRocketryBlocks.blockConcrete, 0, 6);
		world.setBlock(blockX - 1, blockY, blockZ + 1, AdvRocketryBlocks.blockConcrete, 0, 6);
		
		world.setBlock(blockX + 1, blockY, blockZ, Blocks.piston, 5, 6);
		world.setBlock(blockX - 1, blockY, blockZ, Blocks.piston, 4, 6);
		world.setBlock(blockX, blockY, blockZ + 1, Blocks.piston, 3, 6);
		world.setBlock(blockX, blockY, blockZ - 1, Blocks.piston, 2, 6);
		
		generateArm(world, blockX, blockY, blockZ, ForgeDirection.NORTH);
		generateArm(world, blockX, blockY, blockZ, ForgeDirection.EAST);
		generateArm(world, blockX, blockY, blockZ, ForgeDirection.WEST);
		generateArm(world, blockX, blockY, blockZ, ForgeDirection.SOUTH);
		
	}
	
	private static void generateArm(World world, int blockX, int blockY, int blockZ, ForgeDirection direction) {
		for(int i = 0; i < 8; i++) {
			world.setBlock(blockX + direction.offsetX*2 + direction.offsetX*i, blockY, blockZ + direction.offsetZ*2 + direction.offsetZ*i, AdvRocketryBlocks.blockConcrete,0, 6);
		}
		
	}
}
