package zmaster587.advancedRocketry.world.biome;

import java.util.Random;

import zmaster587.advancedRocketry.world.decoration.MapGenInvertedPillar;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.gen.MapGenBase;
import net.minecraft.world.gen.feature.WorldGenAbstractTree;
import net.minecraft.world.gen.feature.WorldGenShrub;

public class BiomeGenOceanSpires extends BiomeGenBase {

	MapGenBase oceanSpire;
	
	public BiomeGenOceanSpires(int id, boolean register) {
		super(id, register);
		
		this.biomeName = "OceanSpires";
		this.rootHeight=-0.5f;
		this.heightVariation=0.0f;
		this.theBiomeDecorator.clayPerChunk = 0;
		this.theBiomeDecorator.flowersPerChunk = 0;
		this.theBiomeDecorator.mushroomsPerChunk = 0;
		this.theBiomeDecorator.treesPerChunk = 0;
		this.theBiomeDecorator.grassPerChunk = 7;
		this.theBiomeDecorator.waterlilyPerChunk = 0;
		this.theBiomeDecorator.sandPerChunk = 0;
		this.theBiomeDecorator.sandPerChunk2 = 0;
		this.spawnableCreatureList.clear();
		this.topBlock = Blocks.gravel;
		this.fillerBlock = Blocks.gravel;
		
		oceanSpire = new MapGenInvertedPillar(4, Blocks.mossy_cobblestone, Blocks.cobblestone, Blocks.dirt);
	}

	@Override
	public void genTerrainBlocks(World world, Random rand,
			Block[] block, byte[] abyte, int x, 
			int z, double noise) {
		super.genTerrainBlocks(world, rand, block, abyte, x, z, noise);
		
		if(x % 16 == 0 && z % 16 == 0 )
			oceanSpire.func_151539_a(null, world, x/16, z/16, block);
	}

    public WorldGenAbstractTree func_150567_a(Random rand) {
        return new WorldGenShrub(3, 0);
    }

	public BiomeGenBase.TempCategory getTempCategory() {
		return BiomeGenBase.TempCategory.OCEAN;
	}
}
