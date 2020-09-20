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
	public boolean func_230383_a_(ISeedReader world, StructureManager structureMgr, ChunkGenerator chunkGen, Random rand, MutableBoundingBox bb, ChunkPos chunkPos, BlockPos blockPos)
	{

		int size = 64;
		int xCoord = (chunkPos.x << 4);
		int zCoord = (chunkPos.z << 4);
		double distFromCenterX =  xCoord - xCenter - 64;
		double distFromCenterZ =  zCoord - zCenter - 64;
		

		int baseHeight = 40;
		int lavaNodeHeight = 25;
		int crackle;

		for(int x = 15; x >= 0; x--) {
			for(int z = 15; z >= 0; z--) {

				//Do some roughness
				crackle = rand.nextInt(2);
				int realXcoord = (xCoord)+x;
				int realZcoord = (zCoord)+z;

				double x2 = (distFromCenterX+x)*(distFromCenterX+x);
				double z2 = (distFromCenterZ+z)*(distFromCenterZ+z);
				double radius = Math.sqrt(x2 + z2);
				double func = 1/(Math.pow(1.028, radius-(size-sizeDeviation)*3)) + 
						+ baseHeight
						- 8/(Math.pow(1.09, radius-((size-sizeDeviation)/2.6)))
						-Math.pow(1.7, radius - size*.9);

				for(int y = 254; y >= 1; y--) {
					boolean underSurface = func >= y+crackle;
					boolean innerLayer = func >= y+crackle && radius < 5 && y > lavaNodeHeight;

					if(innerLayer)
						setBlockState(world, blockEnrichedLava.get().getDefaultState(), realXcoord, y, realZcoord, bb);
					else if(underSurface)
						setBlockState(world, blockCasing.getDefaultState(), realXcoord, y, realZcoord, bb);


					double sphereradius = x2+z2+(y - lavaNodeHeight)*(y - lavaNodeHeight);
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
}