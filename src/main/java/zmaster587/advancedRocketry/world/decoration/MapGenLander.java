package zmaster587.advancedRocketry.world.decoration;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import zmaster587.advancedRocketry.api.AdvancedRocketryBlocks;
import zmaster587.advancedRocketry.dimension.DimensionManager;
import zmaster587.libVulpes.util.BlockPosition;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;
import net.minecraftforge.event.terraingen.PopulateChunkEvent;

public class MapGenLander {

	@SubscribeEvent
	public void populateChunkPostEvent(PopulateChunkEvent.Post event) {
		World worldIn = event.world;
		BlockPosition position = new BlockPosition(16*event.chunkX + 3,0, 16*event.chunkZ + 11);

		if(DimensionManager.getInstance().getDimensionProperties(worldIn.provider.dimensionId).getName().equals("Luna") && position.x == 67 && position.z == 2347) {

			position.y = (short) (worldIn.getTopSolidOrLiquidBlock(position.x, position.z) - 1);
			
			worldIn.setBlock(position.x, position.y, position.z + 3, Blocks.stone_slab,8, 2);
			worldIn.setBlock(position.x, position.y, position.z - 3, Blocks.stone_slab,8, 2);
			worldIn.setBlock(position.x + 3, position.y, position.z, Blocks.stone_slab,8, 2);
			worldIn.setBlock(position.x - 3, position.y, position.z, Blocks.stone_slab,8, 2);
			
			position.y++;

			worldIn.setBlock(position.x, position.y, position.z, AdvancedRocketryBlocks.blockEngine);
			worldIn.setBlock(position.x, position.y, position.z + 3, Blocks.iron_bars);
			worldIn.setBlock(position.x, position.y, position.z - 3, Blocks.iron_bars);
			worldIn.setBlock(position.x + 3, position.y, position.z, Blocks.iron_bars);
			worldIn.setBlock(position.x - 3, position.y, position.z, Blocks.iron_bars);

			position.y++;
			
			worldIn.setBlock(position.x, position.y, position.z + 3, Blocks.iron_bars);
			worldIn.setBlock(position.x, position.y, position.z - 3, Blocks.iron_bars);
			worldIn.setBlock(position.x + 3, position.y, position.z, Blocks.iron_bars);
			worldIn.setBlock(position.x - 3, position.y, position.z, Blocks.iron_bars);

			for(int x = -1; x <= 1; x++ ) {
				worldIn.setBlock(position.x - 2, position.y, position.z + x, Blocks.gold_block);
				worldIn.setBlock(position.x + 2, position.y, position.z + x, Blocks.gold_block);
				worldIn.setBlock(position.x + x, position.y, position.z + 2, Blocks.gold_block);
				worldIn.setBlock(position.x + x, position.y, position.z - 2, Blocks.gold_block);
				for(int z = -1; z <= 1; z++) {
					worldIn.setBlock(position.x + x, position.y, position.z + z, Blocks.iron_block);
				}
			}

			position.y++;
			worldIn.setBlock(position.x, position.y, position.z + 3, Blocks.iron_bars);
			worldIn.setBlock(position.x, position.y, position.z - 3, Blocks.iron_bars);
			worldIn.setBlock(position.x + 3, position.y, position.z, Blocks.iron_bars);
			worldIn.setBlock(position.x - 3, position.y, position.z, Blocks.iron_bars);

			for(int x = -1; x <= 1; x++ ) {
				worldIn.setBlock(position.x - 2, position.y, position.z + x, Blocks.gold_block);
				worldIn.setBlock(position.x + 2, position.y, position.z + x, Blocks.gold_block);
				worldIn.setBlock(position.x + x, position.y, position.z + 2, Blocks.gold_block);
				worldIn.setBlock(position.x + x, position.y, position.z - 2, Blocks.gold_block);
			}

			worldIn.setBlock(position.x, position.y, position.z + 1, Blocks.iron_block);
			worldIn.setBlock(position.x, position.y, position.z - 1, Blocks.iron_block);
			worldIn.setBlock(position.x + 1, position.y, position.z, Blocks.iron_block);
			worldIn.setBlock(position.x - 1, position.y, position.z, Blocks.iron_block);

			position.x += 10;
			position.z += 15;
			position.y = (short) worldIn.getTopSolidOrLiquidBlock(position.x, position.z);

			for(int x = 0; x <= 4; x++ ) 
				worldIn.setBlock(position.x, position.y + x, position.z, Blocks.iron_bars);
			
			worldIn.setBlock(position.x + 1, position.y + 4, position.z, Blocks.iron_bars);
			worldIn.setBlock(position.x + 2, position.y + 4, position.z, Blocks.iron_bars);
		}
	}

}
