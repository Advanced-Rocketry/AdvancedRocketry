package zmaster587.advancedRocketry.world.decoration;

import net.minecraft.block.BlockSlab;
import net.minecraft.block.BlockSlab.EnumBlockHalf;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.event.terraingen.PopulateChunkEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import zmaster587.advancedRocketry.api.AdvancedRocketryBlocks;
import zmaster587.advancedRocketry.dimension.DimensionManager;

public class MapGenLander {

	@SubscribeEvent
	public void populateChunkPostEvent(PopulateChunkEvent.Post event) {
		World worldIn = event.getWorld();
		BlockPos position = new BlockPos(16*event.getChunkX() + 11, 0, 16*event.getChunkZ() + 3);

		if(DimensionManager.getInstance().getDimensionProperties(worldIn.provider.getDimension()).getName().equals("Luna") && position.getZ() == 67 && position.getX() == 2347) {

			position = worldIn.getHeight(position).down();
			
			worldIn.setBlockState(position.add(0, 0, 3), Blocks.STONE_SLAB.getDefaultState().withProperty(BlockSlab.HALF, EnumBlockHalf.TOP));
			worldIn.setBlockState(position.add(0, 0, -3), Blocks.STONE_SLAB.getDefaultState().withProperty(BlockSlab.HALF, EnumBlockHalf.TOP));
			worldIn.setBlockState(position.add(3, 0, 0), Blocks.STONE_SLAB.getDefaultState().withProperty(BlockSlab.HALF, EnumBlockHalf.TOP));
			worldIn.setBlockState(position.add(-3, 0, 0), Blocks.STONE_SLAB.getDefaultState().withProperty(BlockSlab.HALF, EnumBlockHalf.TOP));
			
			position = position.up();

			worldIn.setBlockState(position, AdvancedRocketryBlocks.blockEngine.getDefaultState());
			worldIn.setBlockState(position.add(0, 0, 3), Blocks.IRON_BARS.getDefaultState());
			worldIn.setBlockState(position.add(0, 0, -3), Blocks.IRON_BARS.getDefaultState());
			worldIn.setBlockState(position.add(3, 0, 0), Blocks.IRON_BARS.getDefaultState());
			worldIn.setBlockState(position.add(-3, 0, 0), Blocks.IRON_BARS.getDefaultState());

			position = position.up();
			worldIn.setBlockState(position.add(0, 0, 3), Blocks.IRON_BARS.getDefaultState());
			worldIn.setBlockState(position.add(0, 0, -3), Blocks.IRON_BARS.getDefaultState());
			worldIn.setBlockState(position.add(3, 0, 0), Blocks.IRON_BARS.getDefaultState());
			worldIn.setBlockState(position.add(-3, 0, 0), Blocks.IRON_BARS.getDefaultState());

			for(int x = -1; x <= 1; x++ ) {
				worldIn.setBlockState(position.add(-2, 0, x), Blocks.GOLD_BLOCK.getDefaultState());
				worldIn.setBlockState(position.add(2, 0, x), Blocks.GOLD_BLOCK.getDefaultState());
				worldIn.setBlockState(position.add(x, 0, -2), Blocks.GOLD_BLOCK.getDefaultState());
				worldIn.setBlockState(position.add(x, 0, 2), Blocks.GOLD_BLOCK.getDefaultState());
				for(int z = -1; z <= 1; z++) {
					worldIn.setBlockState(position.add(x, 0, z), Blocks.IRON_BLOCK.getDefaultState());
				}
			}

			position = position.up();
			worldIn.setBlockState(position.add(0, 0, 3), Blocks.IRON_BARS.getDefaultState());
			worldIn.setBlockState(position.add(0, 0, -3), Blocks.IRON_BARS.getDefaultState());
			worldIn.setBlockState(position.add(3, 0, 0), Blocks.IRON_BARS.getDefaultState());
			worldIn.setBlockState(position.add(-3, 0, 0), Blocks.IRON_BARS.getDefaultState());

			for(int x = -1; x <= 1; x++ ) {
				worldIn.setBlockState(position.add(-2, 0, x), Blocks.GOLD_BLOCK.getDefaultState());
				worldIn.setBlockState(position.add(2, 0, x), Blocks.GOLD_BLOCK.getDefaultState());
				worldIn.setBlockState(position.add(x, 0, -2), Blocks.GOLD_BLOCK.getDefaultState());
				worldIn.setBlockState(position.add(x, 0, 2), Blocks.GOLD_BLOCK.getDefaultState());
			}

			worldIn.setBlockState(position.add(0, 0, 1), Blocks.IRON_BLOCK.getDefaultState());
			worldIn.setBlockState(position.add(1, 0, 0), Blocks.IRON_BLOCK.getDefaultState());
			worldIn.setBlockState(position.add(0, 0, -1), Blocks.IRON_BLOCK.getDefaultState());
			worldIn.setBlockState(position.add(-1, 0, 0), Blocks.IRON_BLOCK.getDefaultState());

			position = worldIn.getHeight(position.add(10,0,15));

			for(int x = 0; x <= 4; x++ ) 
				worldIn.setBlockState(position.add(0,x,0), Blocks.IRON_BARS.getDefaultState());

			worldIn.setBlockState(position.add(1,4,0), Blocks.IRON_BARS.getDefaultState());
			worldIn.setBlockState(position.add(2,4,0), Blocks.IRON_BARS.getDefaultState());
		}
	}

}
