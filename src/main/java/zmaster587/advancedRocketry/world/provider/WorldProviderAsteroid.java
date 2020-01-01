package zmaster587.advancedRocketry.world.provider;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.biome.BiomeProviderSingle;
import net.minecraft.world.gen.IChunkGenerator;
import net.minecraftforge.client.IRenderHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import zmaster587.advancedRocketry.AdvancedRocketry;
import zmaster587.advancedRocketry.api.AdvancedRocketryBiomes;
import zmaster587.advancedRocketry.api.ARConfiguration;
import zmaster587.advancedRocketry.api.stations.ISpaceObject;
import zmaster587.advancedRocketry.client.render.planet.RenderAsteroidSky;
import zmaster587.advancedRocketry.client.render.planet.RenderSpaceSky;
import zmaster587.advancedRocketry.dimension.DimensionManager;
import zmaster587.advancedRocketry.dimension.DimensionProperties;
import zmaster587.advancedRocketry.stations.SpaceObjectManager;
import zmaster587.advancedRocketry.world.ChunkProviderAsteroids;
import zmaster587.advancedRocketry.world.ChunkProviderSpace;

public class WorldProviderAsteroid extends WorldProviderPlanet {
	private IRenderHandler skyRender;
	
	@Override
	public double getHorizon() {
		return 0;
	}
	
	
	public int getAverageGroundLevel() {
		return 0;
	}
	
	@Override
	public IChunkGenerator createChunkGenerator() {
		return new ChunkProviderAsteroids(this.world, false, this.world.getSeed(), world.getWorldInfo().getGeneratorOptions());
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public IRenderHandler getSkyRenderer() {
		if(ARConfiguration.getCurrentConfig().stationSkyOverride)
			return skyRender == null ? skyRender = new RenderAsteroidSky() : skyRender;
		
		return super.getSkyRenderer();
	}
	
	@Override
	public float getAtmosphereDensity(BlockPos pos) {
		return 0;
	}
	
	@Override
	public float calculateCelestialAngle(long worldTime, float p_76563_3_) {
		return 0.2f;
	}
	
	@Override
	protected void init() {
		// TODO Auto-generated method stub
		//super.createBiomeProvider();
		this.hasSkyLight=true;
		world.getWorldInfo().setTerrainType(AdvancedRocketry.planetWorldType);
		
		this.biomeProvider = new BiomeProviderSingle(AdvancedRocketryBiomes.spaceBiome);//new ChunkManagerPlanet(worldObj, worldObj.getWorldInfo().getGeneratorOptions(), DimensionManager.getInstance().getDimensionProperties(worldObj.provider.getDimension()).getBiomes());
		
	}
}
