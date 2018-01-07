package zmaster587.advancedRocketry.world.ore;

import java.util.Random;

import zmaster587.advancedRocketry.api.Configuration;
import zmaster587.advancedRocketry.dimension.DimensionManager;
import zmaster587.advancedRocketry.dimension.DimensionProperties;
import zmaster587.advancedRocketry.world.provider.WorldProviderPlanet;
import zmaster587.libVulpes.api.material.AllowedProducts;
import zmaster587.libVulpes.api.material.Material;
import zmaster587.libVulpes.api.material.MaterialRegistry;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.gen.feature.WorldGenMinable;
import net.minecraft.world.gen.feature.WorldGenerator;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.terraingen.OreGenEvent;
import net.minecraftforge.event.terraingen.OreGenEvent.GenerateMinable.EventType;
import cpw.mods.fml.common.IWorldGenerator;
import cpw.mods.fml.common.eventhandler.Event.Result;

public class OreGenerator extends WorldGenerator implements IWorldGenerator {


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

		OreGenEvent event = new OreGenEvent.GenerateMinable(world, random, this, chunkX, chunkZ, EventType.CUSTOM);
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
					dilithiumChance = DimensionProperties.AtmosphereTypes.getAtmosphereTypeFromValue(DimensionManager.getInstance().getDimensionProperties(world.provider.dimensionId).getAtmosphereDensity()) == DimensionProperties.AtmosphereTypes.NONE ? Configuration.dilithiumPerChunkMoon : Configuration.dilithiumPerChunk;;
				}
				
				for(int i = 0; i < dilithiumChance; i++) {
					int coordX = 16*chunkX + random.nextInt(16);
					int coordY = random.nextInt(64);
					int coordZ = 16*chunkZ + random.nextInt(16);

					new WorldGenMinable(MaterialRegistry.getMaterialFromName("Dilithium").getBlock(), MaterialRegistry.getMaterialFromName("Dilithium").getMeta(), Configuration.dilithiumClumpSize, Blocks.stone).generate(world, random, coordX, coordY, coordZ);
				}
			}
		}
	}

	@Override
	public boolean generate(World p_76484_1_, Random p_76484_2_,
			int p_76484_3_, int p_76484_4_, int p_76484_5_) {
		return false;
	}
}
