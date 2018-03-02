package zmaster587.advancedRocketry.world.biome;

import net.minecraft.init.Blocks;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.feature.WorldGenAbstractTree;
import zmaster587.advancedRocketry.world.gen.WorldGenAlienTree;
import zmaster587.advancedRocketry.world.gen.WorldGenNoTree;

import java.util.Random;

public class BiomeGenAlienForest extends Biome {

	public final static WorldGenAbstractTree alienTree = new WorldGenAlienTree(false);
	private final static WorldGenNoTree noTree = new WorldGenNoTree(false);

	public BiomeGenAlienForest(int biomeId, boolean register) {
		super(new BiomeProperties("Alien Forest").setWaterColor(0x8888FF));

        this.setRegistryName(new ResourceLocation("advancedrocketry:Alien Forest"));
		
		this.fillerBlock = Blocks.GRASS.getDefaultState();
		this.decorator.grassPerChunk = 50;
		this.decorator.flowersPerChunk = 0;
	}

	@Override
	public void decorate(World worldIn, Random rand, BlockPos pos) {
		//int xCoord = (chunkX << 4) + 8;
		//int zCoord = (chunkZ << 4) + 8;

		if(rand.nextInt(20) == 0) {


			alienTree.generate(worldIn, rand, worldIn.getHeight(pos));
		}

		super.decorate(worldIn, rand, pos);
	}

	@Override
	public WorldGenAbstractTree getRandomTreeFeature(Random rand) {
		return noTree;
	}

	@Override
	public int getFoliageColorAtPos(BlockPos pos) {
		int color = 0x55ffe1;
		return getModdedBiomeFoliageColor(color);
	}
	@Override
	public int getGrassColorAtPos(BlockPos pos) {
		int color = 0x7777ff;
		return getModdedBiomeFoliageColor(color);
	}
}
