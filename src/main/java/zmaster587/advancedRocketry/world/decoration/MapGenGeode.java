package zmaster587.advancedRocketry.world.decoration;

import zmaster587.advancedRocketry.api.AdvancedRocketryBlocks;
import zmaster587.libVulpes.block.BlockMeta;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;
import net.minecraft.world.gen.MapGenBase;

public class MapGenGeode extends MapGenBase {
	int chancePerChunk;

	private final static BlockMeta[] ores = {new BlockMeta(Blocks.iron_ore), new BlockMeta(Blocks.gold_ore), new BlockMeta(Blocks.redstone_ore), new BlockMeta(Blocks.lapis_ore)};

	public MapGenGeode(int chancePerChunk) {
		this.chancePerChunk = chancePerChunk;
	}

	@Override
	protected void func_151538_a(World world, int rangeX,
			int rangeZ, int chunkX, int chunkZ,
			Block[] chunkArray) {

		if(rand.nextInt(chancePerChunk) == Math.abs(rangeX) % chancePerChunk || rand.nextInt(chancePerChunk) == Math.abs(rangeZ) % chancePerChunk) {

			int radius = rand.nextInt(24) + 24; //24; 24 -> 48

			//TODO: make hemisphere from surface and line the side with ore of some kind

			int depth = radius*radius;

			int xCoord = -rangeX + chunkX;
			int zCoord =  -rangeZ + chunkZ;

			int avgY = (int) ((world.getBiomeGenForCoords(rangeX, rangeZ).rootHeight + 2) *32) - 3*radius/4;

			for(int x = 15; x >= 0; x--) {
				for(int z = 15; z >= 0; z--) {
					int index;

					for(int y = 255; y >= 0; y--) {
						index = (x * 16 + z) * 256 + y;
						if(chunkArray[index] != null)
							break;
					}

					int count = ( depth - ( ((xCoord*16)+x)*((xCoord*16)+x) + ((zCoord*16)+z)*((zCoord*16)+z) ) )/(radius*2);

					//Clears air for the ceiling
					for(int dist = -count; dist < Math.min(count,3); dist++) {
						index = (x * 16 + z) * 256 + avgY -dist;
						chunkArray[index] = null;
					}

					if(count >= 0) {

						if(count > 4) {
							int size = rand.nextInt(4) + 4;

							//Generates ore hanging from the ceiling
							if( x % 4 > 0 && z % 4 > 0) {
								for(int i = 1; i < size; i++)
									chunkArray[(x * 16 + z) * 256 + avgY + count - i] = ores[(x/4 + z/4) % ores.length].getBlock();
							}
							else {
								size -=2;
								for(int i = 1; i < size; i++) {
									chunkArray[(x * 16 + z) * 256 + avgY + count - i] = Blocks.stone;
								}
							}
							
							//Generates ore in the floor
							if( (x+2) % 4 > 0 && (z+2) % 4 > 0) {
								for(int i = 1; i < size; i++)
									chunkArray[(x * 16 + z) * 256 + avgY - count + i] = ores[((x+2)/4 + (z+2)/4) % ores.length].getBlock();
							}
							
						}

						chunkArray[(x * 16 + z) * 256 + avgY-count] = AdvancedRocketryBlocks.blocksGeode;
						chunkArray[(x * 16 + z) * 256 + avgY+count] = AdvancedRocketryBlocks.blocksGeode;//world.getBiomeGenForCoords(rangeX, rangeZ).topBlock;
					}
				}
			}
		}
	}
}
