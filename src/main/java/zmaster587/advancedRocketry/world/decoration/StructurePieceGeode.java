package zmaster587.advancedRocketry.world.decoration;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MutableBoundingBox;
import net.minecraft.world.ISeedReader;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.feature.structure.ScatteredStructurePiece;
import net.minecraft.world.gen.feature.structure.StructureManager;
import net.minecraft.world.gen.feature.template.TemplateManager;
import zmaster587.advancedRocketry.api.AdvancedRocketryBlocks;
import zmaster587.advancedRocketry.api.ARConfiguration;
import zmaster587.advancedRocketry.api.AdvancedRocketryBiomes;
import zmaster587.advancedRocketry.dimension.DimensionManager;
import zmaster587.advancedRocketry.dimension.DimensionProperties;
import zmaster587.libVulpes.util.ZUtils;

import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class StructurePieceGeode extends ScatteredStructurePiece {
	int radius;
	int xCenter, zCenter;

	private static List<BlockState> ores;

	public static void init() {
		if(ores == null) {
			ores = new LinkedList<>();
			IntStream.range(0, ARConfiguration.getCurrentConfig().standardGeodeOres.size()).forEach(i -> ores.add(ARConfiguration.getCurrentConfig().standardGeodeOres.get(i).getDefaultState()));
		}
	}
	
	public StructurePieceGeode(Random random, int x, int z) {
		super(AdvancedRocketryBiomes.STRUCTURE_PIECE_GEODE, random, x, 64, z, 128, 15, 128);
		this.setCoordBaseMode(null);
		radius = random.nextInt(ARConfiguration.getCurrentConfig().geodeVariation.get()) + ARConfiguration.getCurrentConfig().geodeBaseSize.get() - (ARConfiguration.getCurrentConfig().geodeVariation.get()/2); //24; 24 -> 48
		xCenter = x;
		zCenter = z;
	}

    public StructurePieceGeode(TemplateManager mgr, CompoundNBT piece) {
        super(AdvancedRocketryBiomes.STRUCTURE_PIECE_GEODE, piece);
        radius = piece.getInt("Radius");
        xCenter = piece.getInt("xCenter");
        zCenter =  piece.getInt("zCenter");
     }
	

    /**
     * (abstract) Helper method to read subclass data from NBT
     */
    protected void readAdditional(CompoundNBT tagCompound) {
       super.readAdditional(tagCompound);
       tagCompound.putInt("Radius", radius);
       tagCompound.putInt("xCenter", xCenter);
       tagCompound.putInt("zCenter", zCenter);
    }

	public boolean func_230383_a_(ISeedReader world, StructureManager structureMgr, ChunkGenerator chunkGen, Random rand, MutableBoundingBox bb, ChunkPos chunkPos, BlockPos blockPos) {
		int depth = radius*radius;

		int chunkX = chunkPos.x;
		int chunkZ = chunkPos.z;
		
		int xCoord = (chunkX << 4) - xCenter - radius;
		int zCoord =  (chunkZ << 4) - zCenter - radius;

		DimensionProperties props = DimensionManager.getInstance().getDimensionProperties(ZUtils.getDimensionIdentifier(world.getWorld()));
		ores.addAll(
				props.geodeOres.stream()
						.map(s-> Block.getBlockFromItem(s.getItem()).getDefaultState())
						.collect(Collectors.toList())
		);

		int avgY = 64;

		for(int relx = 15; relx >= 0; relx--) {
			for(int relz = 15; relz >= 0; relz--) {

				int count = ( depth - ( ((xCoord)+relx)*((xCoord)+relx) + ((zCoord)+relz)*((zCoord)+relz) ) )/(radius*2);

				int x = (chunkX << 4) + relx;
				int z = (chunkZ << 4) + relz;
				
				//Check for IOB exceptions early, in case it generates near bedrock or something
				if(avgY-count < 1 || avgY+count > 255)
					continue;

				//Clears air for the ceiling
				for(int dist = -count; dist < Math.min(count,3); dist++) {
					setBlockState(world, Blocks.AIR.getDefaultState(), x, avgY - dist, z, bb);
				}

				if(count >= 0) {

					if(count > 4) {
						int size = rand.nextInt(4) + 4;

						//Generates ore hanging from the ceiling
						if( relx % 4 > 0 && relz % 4 > 0) {
							for(int i = 1; i < size; i++)
								setBlockState(world, ores.get((relx/4 + relz/4) % ores.size()), x, avgY + count - i, z, bb);
						}
						else {
							size -=2;
							for(int i = 1; i < size; i++) {
								setBlockState(world, Blocks.STONE.getDefaultState(), x, avgY + count - i, z, bb);
							}
						}

						//Generates ore in the floor
						if( (relx+2) % 4 > 0 && (relz+2) % 4 > 0) {
							for(int i = 1; i < size; i++)
								setBlockState(world, ores.get((relx/4 + relz/4) % ores.size()), x, avgY - count + i, z, bb);
						}

					}

					setBlockState(world, AdvancedRocketryBlocks.blockGeode.getDefaultState(), x, avgY - count, z, bb);
					setBlockState(world, AdvancedRocketryBlocks.blockGeode.getDefaultState(), x, avgY + count, z, bb);
				}
			}
		}
		return true;
	}

   protected void setBlockState(ISeedReader worldIn, BlockState blockstateIn, int x, int y, int z, MutableBoundingBox boundingboxIn) {
	   //super.setBlockState(worldIn, blockstateIn, x, y, z, boundingboxIn);
	   worldIn.setBlockState(new BlockPos(x,y,z), blockstateIn, 2);
   }
}
