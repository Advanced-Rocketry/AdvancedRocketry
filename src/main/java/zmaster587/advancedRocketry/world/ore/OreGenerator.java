package zmaster587.advancedRocketry.world.ore;

import java.util.Random;

import zmaster587.advancedRocketry.api.Configuration;
import zmaster587.advancedRocketry.api.material.MaterialRegistry;
import zmaster587.advancedRocketry.api.material.MaterialRegistry.Materials;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.gen.feature.WorldGenMinable;
import cpw.mods.fml.common.IWorldGenerator;

public class OreGenerator implements IWorldGenerator {

	private static Block dilithiumTargetOre;

	private void generate(World world, Materials material, int numPerChunk,int clumpSize, int chunkX, int chunkZ, Random random) {
		for(int i = 0; i < numPerChunk; i++) {
			int coordX = 16*chunkX + random.nextInt(16);
			int coordY = random.nextInt(64);
			int coordZ = 16*chunkZ + random.nextInt(16);

			new WorldGenMinable(Block.getBlockFromItem(material.getProduct(MaterialRegistry.AllowedProducts.ORE).getItem()), 
					material.getMeta(), clumpSize, Blocks.stone).generate(world, random, coordX, coordY, coordZ);
		}
	}

	public static void setDilithiumTargetBlock(Block block) {
		dilithiumTargetOre = block;
	}

	@Override
	public void generate(Random random, int chunkX, int chunkZ, World world,
			IChunkProvider chunkGenerator, IChunkProvider chunkProvider) {

		if(Configuration.generateCopper) {
			generate(world, Materials.COPPER, Configuration.copperPerChunk, Configuration.copperClumpSize, chunkX, chunkZ, random);
		}

		if(Configuration.generateTin) {
			generate(world, Materials.TIN, Configuration.tinPerChunk, Configuration.tinClumpSize, chunkX, chunkZ, random);
		}
		if(Configuration.generateRutile) {
			generate(world, Materials.RUTILE, Configuration.rutilePerChunk, Configuration.rutileClumpSize, chunkX, chunkZ, random);
		}

		if(Configuration.generateDilithium) {
		int dilithiumChance = world.provider.dimensionId == Configuration.MoonId ? Configuration.dilithiumPerChunkMoon : Configuration.dilithiumPerChunk;
		for(int i = 0; i < dilithiumChance; i++) {
			int coordX = 16*chunkX + random.nextInt(16);
			int coordY = random.nextInt(64);
			int coordZ = 16*chunkZ + random.nextInt(16);

			new WorldGenMinable(MaterialRegistry.Materials.DILITHIUM.getBlock(), MaterialRegistry.Materials.DILITHIUM.getMeta(), Configuration.dilithiumClumpSize, dilithiumTargetOre).generate(world, random, coordX, coordY, coordZ);
			}
		}
	}
}
