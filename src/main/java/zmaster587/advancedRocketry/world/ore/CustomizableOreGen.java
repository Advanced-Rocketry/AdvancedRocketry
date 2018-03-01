package zmaster587.advancedRocketry.world.ore;

import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.gen.IChunkGenerator;
import net.minecraft.world.gen.feature.WorldGenMinable;
import net.minecraftforge.fml.common.IWorldGenerator;
import zmaster587.advancedRocketry.util.OreGenProperties;

import java.util.Random;

public class CustomizableOreGen implements IWorldGenerator {

	IBlockState oreToGen;
	int numPerChunk, clumpSize, heightLevel, difference;


	public CustomizableOreGen(IBlockState oreToGen, int numPerChunk, int clumpSize, int minHeight, int maxHeight) {
		this.oreToGen = oreToGen;
		this.numPerChunk = numPerChunk;
		this.clumpSize = clumpSize;
		difference = maxHeight - minHeight;
		heightLevel = minHeight;
	}

	public CustomizableOreGen(OreGenProperties.OreEntry genProps ) {
		this.oreToGen = genProps.getBlockState();
		this.clumpSize = genProps.getClumpSize();
		this.heightLevel = genProps.getMinHeight();
		this.difference = genProps.getMaxHeight() - this.heightLevel;
		this.numPerChunk = genProps.getChancePerChunk();
	}
	
	@Override
	public void generate(Random random, int chunkX, int chunkZ, World world,
			IChunkGenerator chunkGenerator, IChunkProvider chunkProvider) {

		for(int i = 0; i < numPerChunk; i++) {
			int coordX = 16*chunkX + random.nextInt(16);
			int coordY = heightLevel + random.nextInt(difference);
			int coordZ = 16*chunkZ + random.nextInt(16);

			new WorldGenMinable(oreToGen, clumpSize).generate(world, random, new BlockPos(coordX, coordY, coordZ));
		}

	}
}
