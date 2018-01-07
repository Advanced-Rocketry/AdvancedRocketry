package zmaster587.advancedRocketry.world.ore;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.gen.IChunkGenerator;
import net.minecraft.world.gen.feature.WorldGenMinable;
import net.minecraft.world.gen.feature.WorldGenerator;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.terraingen.OreGenEvent;
import net.minecraftforge.event.terraingen.OreGenEvent.GenerateMinable.EventType;
import net.minecraftforge.fml.common.IWorldGenerator;
import net.minecraftforge.fml.common.eventhandler.Event.Result;
import zmaster587.advancedRocketry.api.Configuration;
import zmaster587.advancedRocketry.dimension.DimensionManager;
import zmaster587.advancedRocketry.dimension.DimensionProperties;
import zmaster587.advancedRocketry.world.provider.WorldProviderPlanet;
import zmaster587.libVulpes.api.material.AllowedProducts;
import zmaster587.libVulpes.api.material.Material;
import zmaster587.libVulpes.api.material.MaterialRegistry;

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
		
		OreGenEvent event = new OreGenEvent.GenerateMinable(world, random, this, new BlockPos(chunkX,0,chunkZ), EventType.CUSTOM);
		MinecraftForge.ORE_GEN_BUS.post(event);
		if(event.getResult() != Result.DENY) {
			if(Configuration.generateCopper) {
				generate(world, MaterialRegistry.getMaterialFromName("Copper"), Configuration.copperPerChunk, Configuration.copperClumpSize, chunkX, chunkZ, random);
			}

			if(Configuration.generateTin) {
				generate(world, MaterialRegistry.getMaterialFromName("Tin"), Configuration.tinPerChunk, Configuration.tinClumpSize, chunkX, chunkZ, random);
			}
			if(Configuration.generateRutile) {
				generate(world, MaterialRegistry.getMaterialFromName("Rutile"), Configuration.rutilePerChunk, Configuration.rutileClumpSize, chunkX, chunkZ, random);
			}
			if(Configuration.generateAluminum) {
				generate(world, MaterialRegistry.getMaterialFromName("Aluminum"), Configuration.aluminumPerChunk, Configuration.aluminumClumpSize, chunkX, chunkZ, random);
			}
			if(Configuration.generateIridium) {
				generate(world, MaterialRegistry.getMaterialFromName("Iridium"), Configuration.IridiumPerChunk, Configuration.IridiumClumpSize, chunkX, chunkZ, random);
			}

			if(Configuration.generateDilithium) {
				int dilithiumChance = Configuration.dilithiumPerChunk;
				if(world.provider instanceof WorldProviderPlanet) {
					dilithiumChance = DimensionProperties.AtmosphereTypes.getAtmosphereTypeFromValue(DimensionManager.getInstance().getDimensionProperties(world.provider.getDimension()).getAtmosphereDensity()) == DimensionProperties.AtmosphereTypes.NONE ? Configuration.dilithiumPerChunkMoon : Configuration.dilithiumPerChunk;;
				}
				
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
