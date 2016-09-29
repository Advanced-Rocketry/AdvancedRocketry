package zmaster587.advancedRocketry.world.ore;

import java.util.Random;

import zmaster587.advancedRocketry.api.Configuration;
import zmaster587.libVulpes.api.material.AllowedProducts;
import zmaster587.libVulpes.api.material.Material;
import zmaster587.libVulpes.api.material.MaterialRegistry;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.gen.feature.WorldGenMinable;
import cpw.mods.fml.common.IWorldGenerator;

public class OreGenerator implements IWorldGenerator {


	private void generate(World world, Material material, int numPerChunk,int clumpSize, int chunkX, int chunkZ, Random random) {
		for(int i = 0; i < numPerChunk; i++) {
			int coordX = 16*chunkX + random.nextInt(16);
			int coordY = random.nextInt(64);
			int coordZ = 16*chunkZ + random.nextInt(16);

			new WorldGenMinable(Block.getBlockFromItem(material.getProduct(AllowedProducts.getProductByName("ORE")).getItem()), 
					material.getMeta(), clumpSize, Blocks.stone).generate(world, random, coordX, coordY, coordZ);
		}
	}

	@Override
	public void generate(Random random, int chunkX, int chunkZ, World world,
			IChunkProvider chunkGenerator, IChunkProvider chunkProvider) {

		if(Configuration.generateCopper) {
			generate(world, MaterialRegistry.getMaterialFromName("Copper"), Configuration.copperPerChunk, Configuration.copperClumpSize, chunkX, chunkZ, random);
		}

		if(Configuration.generateTin) {
			generate(world, MaterialRegistry.getMaterialFromName("Tin"), Configuration.tinPerChunk, Configuration.tinClumpSize, chunkX, chunkZ, random);
		}
		if(Configuration.generateRutile) {
			generate(world, MaterialRegistry.getMaterialFromName("Rutile"), Configuration.rutilePerChunk, Configuration.rutileClumpSize, chunkX, chunkZ, random);
		}

		if(Configuration.generateDilithium) {
			int dilithiumChance = world.provider.dimensionId == Configuration.MoonId ? Configuration.dilithiumPerChunkMoon : Configuration.dilithiumPerChunk;
			for(int i = 0; i < dilithiumChance; i++) {
				int coordX = 16*chunkX + random.nextInt(16);
				int coordY = random.nextInt(64);
				int coordZ = 16*chunkZ + random.nextInt(16);

				new WorldGenMinable(MaterialRegistry.getMaterialFromName("Dilithium").getBlock(), MaterialRegistry.getMaterialFromName("Dilithium").getMeta(), Configuration.dilithiumClumpSize, Blocks.stone).generate(world, random, coordX, coordY, coordZ);
			}
		}
	}
}
