package zmaster587.advancedRocketry.world.gen;

import java.util.Random;

import cpw.mods.fml.common.IWorldGenerator;
import zmaster587.advancedRocketry.dimension.DimensionManager;
import zmaster587.advancedRocketry.dimension.DimensionProperties;
import zmaster587.advancedRocketry.integration.CompatibilityMgr;
import zmaster587.advancedRocketry.util.OreGenProperties;
import zmaster587.advancedRocketry.world.WorldGenMineableSpecial;
import zmaster587.libVulpes.block.BlockMeta;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.gen.feature.WorldGenMinable;

public class CustomizableOreGen implements IWorldGenerator {

	BlockMeta oreToGen;
	int numPerChunk, clumpSize, heightLevel, difference;


	public CustomizableOreGen(BlockMeta oreToGen, int numPerChunk, int clumpSize, int minHeight, int maxHeight) {
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
			IChunkProvider chunkGenerator, IChunkProvider chunkProvider) {

		Block block = Blocks.stone;
		if(DimensionManager.getInstance().isDimensionCreated(world.provider.dimensionId))
		{
			Block state = ((DimensionProperties)DimensionManager.getInstance().getDimensionProperties(world.provider.dimensionId)).getStoneBlock();
			if(state != null)
				block = state;
		}
		
		for(int i = 0; i < numPerChunk; i++) {
			int coordX = 16*chunkX + random.nextInt(16);
			int coordY = heightLevel + random.nextInt(difference);
			int coordZ = 16*chunkZ + random.nextInt(16);
			
			//Generate GT ores if needed
			if(CompatibilityMgr.gregtechLoaded && oreToGen.getMeta() > 15)
				new WorldGenMineableSpecial(oreToGen.getBlock(), oreToGen.getMeta(), clumpSize, block).generate(world, random, coordX, coordY, coordZ);
			else
				new WorldGenMinable(oreToGen.getBlock(), oreToGen.getMeta(), clumpSize, block).generate(world, random, coordX, coordY, coordZ);
		}

	}
}
