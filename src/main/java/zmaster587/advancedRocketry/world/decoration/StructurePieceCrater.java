package zmaster587.advancedRocketry.world.decoration;

import java.util.BitSet;
import java.util.Random;
import java.util.function.Function;

import com.mojang.serialization.Codec;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MutableBoundingBox;
import net.minecraft.world.ISeedReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.chunk.IChunk;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.Heightmap;
import net.minecraft.world.gen.Heightmap.Type;
import net.minecraft.world.gen.carver.WorldCarver;
import net.minecraft.world.gen.feature.ProbabilityConfig;
import net.minecraft.world.gen.feature.structure.IStructurePieceType;
import net.minecraft.world.gen.feature.structure.ScatteredStructurePiece;
import net.minecraft.world.gen.feature.structure.StructureManager;
import net.minecraft.world.gen.feature.template.TemplateManager;
import zmaster587.advancedRocketry.api.AdvancedRocketryBiomes;

public class StructurePieceCrater extends ScatteredStructurePiece {

	int chancePerChunk;
	int radius;
	int xCenter, zCenter;

	public StructurePieceCrater(Random random, int x, int z) {
		super(AdvancedRocketryBiomes.STRUCTURE_PIECE_CRATER, random, x, 64, z, 128 + 32, 15, 128 + 32);
		this.setCoordBaseMode(null);
		radius = random.nextInt(56) + 8; //64; 8 -> 64
		xCenter = x;
		zCenter = z;
	}

    public StructurePieceCrater(TemplateManager mgr, CompoundNBT piece) {
        super(AdvancedRocketryBiomes.STRUCTURE_PIECE_CRATER, piece);
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

	public boolean func_230383_a_(ISeedReader world, StructureManager structureMgr, ChunkGenerator chunkGen, Random rand, MutableBoundingBox bb, ChunkPos chunkPos, BlockPos blockPos)
	{

		int depth = radius*radius;

		int xCoord = chunkPos.x << 4;
		int zCoord = chunkPos.z << 4;
		int ridgeSize = 12;
		double distFromCenterX =  xCoord - xCenter - radius - 2*ridgeSize;
		double distFromCenterZ =  zCoord - zCenter - radius - 2*ridgeSize;


		for(int relx = 15; relx >= -0; relx--) {
			for(int relz = 15; relz >= -0; relz--) {
				//for(int y = 254; y >= 0; y--) { //&& chunkPrimerIn.getBlockState(x, y, z).isOpaqueCube()) {
				{	
					int x = relx + xCoord;
					int z = relz + zCoord;
					int y = world.getHeight(Type.WORLD_SURFACE_WG, x, z)-1;


					

					int count = (int) (( depth - ( (distFromCenterX+relx)*(distFromCenterX+relx) + (distFromCenterZ+relz)*(distFromCenterZ+relz) ) )/(radius*2));

					for(int dist = 0; dist < count; dist++) {
						if(y-dist > 2)
							this.setBlockState(world, Blocks.AIR.getDefaultState(), x, y-dist, z, bb);
					}

					

					if(count <= 0 && count > -2*ridgeSize) {


						for(int dist = 0; dist < ((ridgeSize*ridgeSize) - (count+ridgeSize)*(count+ridgeSize))/(ridgeSize*2); dist++) {
							if(y + dist < 255)
								this.setBlockState(world, world.getBiome(new BlockPos(x, y + dist, z)).func_242440_e().func_242502_e().getTop(), x, y + dist, z,bb);
						}
					}

					if(count > 1 && (y-count > 2))
						this.setBlockState(world, world.getBiome(new BlockPos(x, y - count, z)).func_242440_e().func_242502_e().getTop(), x, y - count, z,bb);
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