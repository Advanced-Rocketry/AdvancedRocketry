package zmaster587.advancedRocketry.world.decoration;

import net.minecraft.block.Blocks;
import net.minecraft.block.SlabBlock;
import net.minecraft.state.properties.SlabType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.gen.Heightmap.Type;
import net.minecraftforge.event.world.ChunkEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import zmaster587.advancedRocketry.api.AdvancedRocketryBlocks;
import zmaster587.advancedRocketry.dimension.DimensionManager;
import zmaster587.libVulpes.util.ZUtils;

public class MapGenLander {

	@SubscribeEvent
	public void populateChunkPostEvent(ChunkEvent.Load event) {
		
		World worldIn = (World)event.getWorld();
		BlockPos position = new BlockPos(16*event.getChunk().getPos().x + 11, 0, 16*event.getChunk().getPos().z + 3);

		if(DimensionManager.getInstance().getDimensionProperties(ZUtils.getDimensionIdentifier(worldIn)).getName().equals("Luna") && position.getZ() == 67 && position.getX() == 2347) {

			position = worldIn.getHeight(Type.WORLD_SURFACE, position).down();
			
			worldIn.setBlockState(position.add(0, 0, 3), Blocks.STONE_SLAB.getDefaultState().with(SlabBlock.TYPE, SlabType.TOP));
			worldIn.setBlockState(position.add(0, 0, -3), Blocks.STONE_SLAB.getDefaultState().with(SlabBlock.TYPE, SlabType.TOP));
			worldIn.setBlockState(position.add(3, 0, 0), Blocks.STONE_SLAB.getDefaultState().with(SlabBlock.TYPE, SlabType.TOP));
			worldIn.setBlockState(position.add(-3, 0, 0), Blocks.STONE_SLAB.getDefaultState().with(SlabBlock.TYPE, SlabType.TOP));
			
			position = position.up();

			worldIn.setBlockState(position, AdvancedRocketryBlocks.blockMonopropellantEngine.getDefaultState());
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

			position = worldIn.getHeight(Type.WORLD_SURFACE, position.add(10,0,15));

			for(int y = 0; y <= 4; y++ )
				worldIn.setBlockState(position.add(0,y,0), Blocks.IRON_BARS.getDefaultState());

			worldIn.setBlockState(position.add(1,4,0), Blocks.IRON_BARS.getDefaultState());
			worldIn.setBlockState(position.add(2,4,0), Blocks.IRON_BARS.getDefaultState());
		}
	}

}
