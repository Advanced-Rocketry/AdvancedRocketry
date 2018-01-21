package zmaster587.advancedRocketry.world.provider;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import zmaster587.advancedRocketry.AdvancedRocketry;
import zmaster587.advancedRocketry.api.AdvancedRocketryBiomes;
import zmaster587.advancedRocketry.api.Configuration;
import zmaster587.advancedRocketry.api.stations.ISpaceObject;
import zmaster587.advancedRocketry.client.render.planet.RenderPlanetarySky;
import zmaster587.advancedRocketry.client.render.planet.RenderSpaceSky;
import zmaster587.advancedRocketry.dimension.DimensionManager;
import zmaster587.advancedRocketry.dimension.DimensionProperties;
import zmaster587.advancedRocketry.stations.SpaceObject;
import zmaster587.advancedRocketry.stations.SpaceObjectManager;
import zmaster587.advancedRocketry.world.ChunkProviderSpace;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.biome.WorldChunkManager;
import net.minecraft.world.biome.WorldChunkManagerHell;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraftforge.client.IRenderHandler;

public class WorldProviderSpace extends WorldProviderPlanet {
	private IRenderHandler skyRender;
	
	@Override
	public double getHorizon() {
		return 0;
	}
	
	//TODO: figure out celestial angle from coords
	
	@Override
	public boolean isPlanet() {
		return false;
	}
	
	
	public int getAverageGroundLevel() {
		return 0;
	}
	
	@Override
	public IChunkProvider createChunkGenerator() {
		return new ChunkProviderSpace(this.worldObj, this.worldObj.getSeed());
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public IRenderHandler getSkyRenderer() {
		if(Configuration.stationSkyOverride)
			return skyRender == null ? skyRender = new RenderSpaceSky() : skyRender;
		
		return super.getSkyRenderer();
	}
	
	@Override
	public float getAtmosphereDensity(int x, int z) {
		return 0;
	}
	
	@Override
	public float calculateCelestialAngle(long worldTime, float p_76563_3_) {
		return AdvancedRocketry.proxy.calculateCelestialAngleSpaceStation();
	}
	
	@Override
	protected void registerWorldChunkManager() {
		worldObj.getWorldInfo().setTerrainType(AdvancedRocketry.spaceWorldType);
		this.worldChunkMgr = new WorldChunkManagerHell(AdvancedRocketryBiomes.spaceBiome, 0.0F);
		this.hasNoSky = false;
	}
	
	@Override
	public DimensionProperties getDimensionProperties(int x , int z) {
		ISpaceObject object = SpaceObjectManager.getSpaceManager().getSpaceStationFromBlockCoords(x, z);
		if(object != null)
			return (DimensionProperties)object.getProperties();
		return DimensionManager.defaultSpaceDimensionProperties;
	}
}
