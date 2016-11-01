package zmaster587.advancedRocketry.world.ore;

import java.util.Random;

import zmaster587.advancedRocketry.api.Configuration;
import zmaster587.libVulpes.api.material.AllowedProducts;
import zmaster587.libVulpes.api.material.Material;
import zmaster587.libVulpes.api.material.MaterialRegistry;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.IChunkGenerator;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.gen.feature.WorldGenMinable;
import net.minecraft.world.gen.feature.WorldGenerator;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.terraingen.OreGenEvent;
import net.minecraftforge.event.terraingen.OreGenEvent.GenerateMinable.EventType;
import net.minecraftforge.fml.common.IWorldGenerator;

public class OreGenerator extends WorldGenerator implements IWorldGenerator {

	private static IBlockState dilithiumTargetOre;

	private void generate(World world, Material material, int numPerChunk,int clumpSize, int chunkX, int chunkZ, Random random) {
		for(int i = 0; i < numPerChunk; i++) {
			int coordX = 16*chunkX + random.nextInt(16);
			int coordY = random.nextInt(64);
			int coordZ = 16*chunkZ + random.nextInt(16);

			Block block = Block.getBlockFromItem(material.getProduct(AllowedProducts.getProductByName("ORE")).getItem());

			new WorldGenMinable(block.getStateFromMeta(material.getMeta()),
					clumpSize).generate(world, random, new BlockPos(coordX, coordY, coordZ));
		}
	}

	@Override
	public void generate(Random random, int chunkX, int chunkZ, World world,
			IChunkGenerator chunkGenerator, IChunkProvider chunkProvider) {

		if(MinecraftForge.ORE_GEN_BUS.post(new OreGenEvent.GenerateMinable(world, random, this, new BlockPos(chunkX,0,chunkZ), EventType.CUSTOM))) {
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
				int dilithiumChance = world.provider.getDimension() == Configuration.MoonId ? Configuration.dilithiumPerChunkMoon : Configuration.dilithiumPerChunk;
				for(int i = 0; i < dilithiumChance; i++) {
					int coordX = 16*chunkX + random.nextInt(16);
					int coordY = random.nextInt(64);
					int coordZ = 16*chunkZ + random.nextInt(16);

					new WorldGenMinable(MaterialRegistry.getMaterialFromName("Dilithium").getBlock().getDefaultState(), Configuration.dilithiumClumpSize).generate(world, random, new BlockPos(coordX, coordY, coordZ));
				}
			}
		}
	}

	@Override
	public boolean generate(World worldIn, Random rand, BlockPos position) {
		return false;
	}
}
