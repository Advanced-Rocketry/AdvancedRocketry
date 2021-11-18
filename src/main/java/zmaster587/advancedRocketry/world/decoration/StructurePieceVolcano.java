package zmaster587.advancedRocketry.world.decoration;

import java.util.Random;
import java.util.function.Supplier;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MutableBoundingBox;
import net.minecraft.world.ISeedReader;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.feature.structure.ScatteredStructurePiece;
import net.minecraft.world.gen.feature.structure.StructureManager;
import net.minecraft.world.gen.feature.template.TemplateManager;
import zmaster587.advancedRocketry.api.AdvancedRocketryBiomes;
import zmaster587.advancedRocketry.api.AdvancedRocketryBlocks;

public class StructurePieceVolcano extends ScatteredStructurePiece {

	private static final Supplier<? extends Block> blockEnrichedLava = AdvancedRocketryBlocks.blockEnrichedLavaFluid;
	private static final Block blockCasing = AdvancedRocketryBlocks.blockBasalt;
	int xCenter, zCenter;
	int sizeDeviation;

	public StructurePieceVolcano(Random random, int x, int z) {
		super(AdvancedRocketryBiomes.STRUCTURE_PIECE_VOLCANO, random, x, 64, z, 128, 64, 128);
		this.setCoordBaseMode(null);
		xCenter = x;
		zCenter = z;
		sizeDeviation = random.nextInt(8); // 0 -> 8
	}
	
    public StructurePieceVolcano(TemplateManager mgr, CompoundNBT piece) {
        super(AdvancedRocketryBiomes.STRUCTURE_PIECE_VOLCANO, piece);
        xCenter = piece.getInt("xCenter");
        zCenter =  piece.getInt("zCenter");
        sizeDeviation =  piece.getInt("sizeDev");
     }
	
    /**
     * (abstract) Helper method to read subclass data from NBT
     */
    protected void readAdditional(CompoundNBT tagCompound) {
       super.readAdditional(tagCompound);
       tagCompound.putInt("xCenter", xCenter);
       tagCompound.putInt("zCenter", zCenter);
       tagCompound.putInt("sizeDev", sizeDeviation);
    }


	@Override
	public boolean func_230383_a_(ISeedReader world, StructureManager structureMgr, ChunkGenerator chunkGen, Random rand, MutableBoundingBox bb, ChunkPos chunkPos, BlockPos blockPos) {

			//Standard coefficient stuff
			int size = 64;
			int sizeDeviation = rand.nextInt(8); // 0 -> 8
			int baseHeight = 25;
			int lavaNodeHeight = 25;
			//Random coefficients for the sin functions
			int[] sinCoefficients = {rand.nextInt(10) + 1, rand.nextInt(10) + 1, rand.nextInt(10) + 1, rand.nextInt(10) + 1, rand.nextInt(10) + 1};
			int numBumps = rand.nextInt(5) + 1;

		int xCoord = (chunkPos.x << 4);
		int zCoord = (chunkPos.z << 4);

			for(int x = 15; x >= 0; x--) {
				for(int z = 15; z >= 0; z--) {

					int realXcoord = (xCoord)+x;
					int realZcoord = (zCoord)+z;

					//Radius determination based on random functions
					int x2 = realXcoord*realXcoord;
					int z2 = realZcoord*realZcoord;
					double radius = getRadius(Math.sqrt(x2 + z2), realXcoord, realZcoord,  numBumps, sinCoefficients);
					//Do some roughness
					int crackle = rand.nextInt(2);

					double func = 1/(Math.pow(1.028, radius-(size-sizeDeviation)*3)) + baseHeight - 8/(Math.pow(1.09, radius-((size-sizeDeviation)/2.6))) -Math.pow(1.7, radius - size*.9);

					for(int y = 254; y >= 1; y--) {
						boolean underSurface = func >= y+crackle;
						boolean innerLayer = func >= y+crackle && radius < 5 && y > lavaNodeHeight;

						if(innerLayer)
							setBlockState(world, blockEnrichedLava.get().getDefaultState(), realXcoord, y, realZcoord, bb);
						else if(underSurface)
							setBlockState(world, blockEnrichedLava.get().getDefaultState(), realXcoord, y, realZcoord, bb);


						double sphereradius = (radius * radius) + (y - lavaNodeHeight)*(y - lavaNodeHeight);
						if(sphereradius < 23*23)
							setBlockState(world, blockEnrichedLava.get().getDefaultState(), realXcoord, y, realZcoord, bb);
						else if(sphereradius < 25*25)
							setBlockState(world, blockCasing.getDefaultState(), realXcoord, y, realZcoord, bb);

						//For the top of the bulb, im lazy
						if(innerLayer)
							setBlockState(world, blockEnrichedLava.get().getDefaultState(), realXcoord, y, realZcoord, bb);
					}
				}
			}
			return true;
	}
	
	protected void setBlockState(ISeedReader worldIn, BlockState blockstateIn, int x, int y, int z, MutableBoundingBox boundingboxIn) {
		//super.setBlockState(worldIn, blockstateIn, x, y, z, boundingboxIn);
		worldIn.setBlockState(new BlockPos(x,y,z), blockstateIn, 2);
	}

	//Very fun function for fancy radius
	//Int[] MUST be the same size as max bumps or larger!
	private double getRadius(double base, int x, int z, int bumps, int[] random) {
		//We need to start this out with polar coordinates
		double radians = Math.atan2(x, z);

		//Then we want to add some sin-function bumps to it, as determined by the bumps
		//They increase theta each time because then we can get different-placed perturbations
		//An example graph for this is here: https://www.desmos.com/calculator/5ojoqscuxv
		int extras = 0;
		for (int i = 2; i < Math.min(5, bumps) + 2; i++){
			extras += random[i-2] * base * Math.sin(i * radians) * 0.0125;
		}

		return base + extras;
	}
}