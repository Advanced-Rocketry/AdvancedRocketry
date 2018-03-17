package zmaster587.advancedRocketry.world.provider;


import java.util.Set;

import org.apache.commons.lang3.ArrayUtils;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import zmaster587.advancedRocketry.AdvancedRocketry;
import zmaster587.advancedRocketry.api.Configuration;
import zmaster587.advancedRocketry.api.IPlanetaryProvider;
import zmaster587.advancedRocketry.atmosphere.AtmosphereHandler;
import zmaster587.advancedRocketry.client.render.planet.RenderPlanetarySky;
import zmaster587.advancedRocketry.dimension.DimensionManager;
import zmaster587.advancedRocketry.dimension.DimensionProperties;
import zmaster587.advancedRocketry.world.ChunkManagerPlanet;
import zmaster587.advancedRocketry.world.ChunkProviderCavePlanet;
import zmaster587.advancedRocketry.world.ChunkProviderPlanet;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;
import net.minecraft.world.WorldProvider;
import net.minecraft.world.biome.WorldChunkManager;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraftforge.client.IRenderHandler;

public class WorldProviderPlanet extends WorldProvider implements IPlanetaryProvider {
	private IRenderHandler skyRender;
	public WorldChunkManager chunkMgrTerraformed;

	/*@Override
	protected void registerWorldChunkManager() {
		//this.worldChunkMgr = new WorldChunkManagerHell(BiomeGenBase.extremeHills, 0.0f);
		this.worldChunkMgr = new ChunkManagerPlanet(getSeed(), planetWorldType);
		this.hasNoSky = false;

		biomeMaps.put(new Integer(1), new Integer(1));

		//TODO: test this in SMP
		//this.setSkyRenderer(new RenderSkyMoon());
	}*/

	@Override

	public IChunkProvider createChunkGenerator() {
		if(DimensionManager.getInstance().getDimensionProperties(worldObj.provider.dimensionId).getGenType() == 1)
		{
			return new ChunkProviderCavePlanet(this.worldObj,this.worldObj.getSeed());
		}
		else
			return new ChunkProviderPlanet(this.worldObj, this.worldObj.getSeed(), false);
	}
	
	
	@Override
	@SideOnly(Side.CLIENT)
	public IRenderHandler getSkyRenderer() {
		if(!Configuration.planetSkyOverride)
			return null;
		if(super.getSkyRenderer() == null && Configuration.planetSkyOverride)
			super.setSkyRenderer( new RenderPlanetarySky());
		
		return super.getSkyRenderer();
	}

	@Override
	public long getSeed() {
		return super.getSeed() + dimensionId;
	}

	/*@Override
	public BiomeGenBase getBiomeGenForCoords(int x, int z) {
		super.getBiomeGenForCoords(x, z)
		return worldChunkMgr.getBiomeGenAt(x, z);
		//return getDimensionProperties().biomeProperties.getChunkPropertiesFromBlockCoords(x, z).getBiomeGenForWorldCoords(x & 15, z & 15, this.worldChunkMgr);
	}*/

	@Override
	protected void registerWorldChunkManager()
	{
		worldObj.getWorldInfo().setTerrainType(AdvancedRocketry.planetWorldType);
		
		this.worldChunkMgr = new ChunkManagerPlanet(worldObj, DimensionManager.getInstance().getDimensionProperties(dimensionId).getBiomes());
		this.chunkMgrTerraformed = new ChunkManagerPlanet(worldObj, DimensionManager.getInstance().getDimensionProperties(dimensionId).getTerraformedBiomes());
		//AdvancedRocketry.planetWorldType.getChunkManager(worldObj);
	}
	@Override
	public boolean canDoRainSnowIce(Chunk chunk) {
		// TODO Auto-generated method stub
		return getAtmosphereDensity(0,0) > 75 ? super.canDoRainSnowIce(chunk) : false;
	}
	@Override
	public void updateWeather() {

		if(getAtmosphereDensity(0,0) > 75)
			super.updateWeather();
	}

	@Override
	public int getRespawnDimension(EntityPlayerMP player) {

		if(AtmosphereHandler.hasAtmosphereHandler(dimensionId) && Configuration.canPlayerRespawnInSpace) {
			ChunkCoordinates coords = player.getBedLocation(dimensionId);
			
			if(Configuration.forcePlayerRespawnInSpace || coords != null && AtmosphereHandler.getOxygenHandler(player.worldObj.provider.dimensionId).getAtmosphereType(coords.posX, coords.posY, coords.posZ).isBreathable())
				return dimensionId;
		}
		return 0;
	}
	
	@Override
	public boolean canRespawnHere() {
		return false;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public float[] calcSunriseSunsetColors(float p_76560_1_, float p_76560_2_) {

		float[] colors = getDimensionProperties((int)Minecraft.getMinecraft().thePlayer.posX, (int)Minecraft.getMinecraft().thePlayer.posZ).sunriseSunsetColors;

		if(colors == null)
			return super.calcSunriseSunsetColors(p_76560_1_, p_76560_2_);

		float finalColors[] = new float[4];

		float f2 = 0.4F;
		float f3 = MathHelper.cos(p_76560_1_ * (float)Math.PI * 2.0F) - 0.0F;
		float f4 = -0.0F;

		if (f3 >= f4 - f2 && f3 <= f4 + f2)
		{
			float f5 = (f3 - f4) / f2 * 0.5F + 0.5F;
			float f6 = 1.0F - (1.0F - MathHelper.sin(f5 * (float)Math.PI)) * 0.99F;
			f6 *= f6;
			finalColors[0] = f5 * 0.3F + colors[0];
			finalColors[1] = f5 * f5 * 0.7F + colors[1];
			finalColors[2] = f5 * f5 * 0.1F + colors[2];
			finalColors[3] = f6 * (getAtmosphereDensityFromHeight(Minecraft.getMinecraft().renderViewEntity.posY, (int)Minecraft.getMinecraft().thePlayer.posX, (int)Minecraft.getMinecraft().thePlayer.posZ));
			return finalColors;
		}
		else
		{
			return null;
		}

		//return super.calcSunriseSunsetColors(p_76560_1_, p_76560_2_); //getDimensionProperties().sunriseSunsetColors;
	}

	@Override
	public float getSunBrightness(float partialTicks) {
		float atmosphere = getAtmosphereDensity(0,0);
		Math.abs(1-atmosphere);
		//calculateCelestialAngle(p_76563_1_, p_76563_3_)
        float f1 = worldObj.getCelestialAngle(partialTicks);
        float f2 = 1.0F - (MathHelper.cos(f1 * (float)Math.PI * 2.0F) * 2.0F + 0.2F) - atmosphere/4f;
        
        if (f2 < 0.0F)
        {
            f2 = 0.0F ;
        }

        if (f2 > 1.0F)
        {
            f2 = 1.0F;
        }

        f2 = 1.0F - f2;
        
		//Eclipse handling
		if(this.worldObj.isRemote) {
			DimensionProperties properties = getDimensionProperties((int)Minecraft.getMinecraft().thePlayer.posX, (int)Minecraft.getMinecraft().thePlayer.posZ);
			if(properties.isMoon()) {
				f2 = eclipseValue(properties, f2, partialTicks);
			}
			else {
				for(int i : properties.getChildPlanets()) {
					DimensionProperties childProps = DimensionManager.getInstance().getDimensionProperties(i);
					f2 = eclipseValue(childProps, f2, partialTicks);
				}
			}
		}
        
		return f2*super.getSunBrightness(partialTicks);
	}

	private float eclipseValue(DimensionProperties properties, float lightValue, double partialTicks) {
		
		double currentTheta = (((partialTicks*properties.orbitTheta + ((1-partialTicks)*properties.prevOrbitalTheta)) * 180/Math.PI)  % 360d);
		int solarDistance = properties.getSolarOrbitalDistance();
		float planetaryDistance = properties.getParentOrbitalDistance();

		float difference = solarDistance/(200-planetaryDistance + 0.00001f);
		
		
		float phiMuliplier = (float) (Math.max(Math.abs(MathHelper.cos((float)(properties.orbitalPhi * Math.PI/180)))-0.95f, 0)*20);

		int offset = (int)((200-planetaryDistance)/2f);

		//1 is fast attenuation
		//-1 is no atten
		//solar distance conrols fade, planetary distance controls duration
		if(phiMuliplier !=0 && currentTheta > 180 - offset && currentTheta < 180 + offset ) {
			lightValue *= phiMuliplier*(MathHelper.clamp_float((float) ((difference/20f) + (Math.abs(currentTheta - 180)*difference)/(10f)) ,0,1)) + (1-phiMuliplier);
			//f2 = 0;
		}
		return lightValue;
	}
	
	//No clouds
	@Override
	public float getCloudHeight() {
		return getAtmosphereDensity(0,0) > .75f ? 128 : -2000;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public boolean isSkyColored() {
		return false;
	}

	@Override
	public Vec3 getSkyColor(Entity cameraEntity, float partialTicks) {
		float[] vec = getDimensionProperties((int)cameraEntity.posX, (int)cameraEntity.posZ).skyColor;
		if(vec == null)
			return super.getSkyColor(cameraEntity, partialTicks);
		else {
			Vec3 skyColorVec = super.getSkyColor(cameraEntity, partialTicks);
			return Vec3.createVectorHelper(vec[0] * skyColorVec.xCoord, vec[1] * skyColorVec.yCoord, vec[2] * skyColorVec.zCoord) ;
		}
	}

	@Override
	@SideOnly(Side.CLIENT)
	public Vec3 getFogColor(float p_76562_1_, float p_76562_2_) {

		Vec3 superVec = super.getFogColor(p_76562_1_, p_76562_2_);
		//float multiplier = getAtmosphereDensityFromHeight(Minecraft.getMinecraft().renderViewEntity.posY);

		float[] vec = getDimensionProperties((int)Minecraft.getMinecraft().thePlayer.posX,(int)Minecraft.getMinecraft().thePlayer.posZ).fogColor;
		return Vec3.createVectorHelper(vec[0] * superVec.xCoord, vec[1] * superVec.yCoord, vec[2] * superVec.zCoord);
	}

	@Override
	public boolean doesXZShowFog(int x, int z) {
		return getDimensionProperties(x,z).getAtmosphereDensity() > 100;
	}

	@Override
	public String getDimensionName() {
		return getDimensionProperties(0,0).getName();
	}

	//Make the world deep
	@Override
	public int getAverageGroundLevel() {
		return 96;
	}

	@Override
	public double getHorizon() {
		return 63;
	}

	@Override
	public float calculateCelestialAngle(long p_76563_1_, float p_76563_3_) {
		int rotationalPeriod;
		rotationalPeriod = getRotationalPeriod(0,0);


		int j = (int)(p_76563_1_ % rotationalPeriod);

		float f1 = ((float)j + p_76563_3_) / (float)rotationalPeriod - 0.25F;

		if (f1 < 0.0F)
		{
			++f1;
		}

		if (f1 > 1.0F)
		{
			--f1;
		}

		float f2 = f1;
		f1 = 1.0F - (float)((Math.cos((double)f1 * Math.PI) + 1.0D) / 2.0D);
		f1 = f2 + (f1 - f2) / 3.0F;
		return f1;
	}

	@Override
	public double getGravitationalMultiplier(int x, int z) {
		return getDimensionProperties(x,z).gravitationalMultiplier;
	}

	@Override
	public int getOrbitingDimension(int x ,int z) {
		return getDimensionProperties(x,z).getParentPlanet();
	}

	@Override
	public int[] getDimensionsInOrbit(int x, int z) {
		Set<Integer> intSet = getDimensionProperties(x,z).getChildPlanets();
		Integer[] intArray = new Integer[intSet.size()];
		return ArrayUtils.toPrimitive(getDimensionProperties(x,z).getChildPlanets().toArray(intArray));
	}

	@Override
	public int getOrbitalDistance(int x, int z) {
		return getDimensionProperties(x,z).orbitalDist;
	}

	@Override
	public float getAtmosphereDensity(int x, int z) {
		return (float)(getDimensionProperties(x,z).getAtmosphereDensity()/100f);
	}

	@Override
	public float getAtmosphereDensityFromHeight(double y, int x, int z) {
		return getDimensionProperties(x,z).getAtmosphereDensityAtHeight(y);
	}

	@Override
	public int getAverageTemperature(int x, int z) {
		return getDimensionProperties(x,z).averageTemperature;
	}

	@Override
	public int getRotationalPeriod(int x, int z) {
		return Math.max(getDimensionProperties(0,0).rotationalPeriod,1);
	}

	@Override
	public int getWetness() {
		return 0;
	}

	@Override
	public String getSaveFolder() {
		return "advRocketry/" + super.getSaveFolder();
	}

	@Override
	public boolean isPlanet() {
		return true;
	}

	@Override
	public Vec3 getSunColor(int x, int z) {
		float[] vec = getDimensionProperties(x,z).getSunColor();
		return Vec3.createVectorHelper(vec[0],vec[1],vec[2]);
	}

	public int getSolarOrbitalDistance(int x, int z) {
		return getDimensionProperties(x,z).getSolarOrbitalDistance();
	}

	@Override
	public DimensionProperties getDimensionProperties(int x, int z) {
		DimensionProperties properties = DimensionManager.getInstance().getDimensionProperties(this.dimensionId);
		return properties == null ? new DimensionProperties(this.dimensionId) : properties;
	}
}
