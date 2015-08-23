package zmaster587.advancedRocketry.world;


import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import zmaster587.advancedRocketry.AdvancedRocketry;
import zmaster587.advancedRocketry.api.IPlanetaryProvider;
import zmaster587.advancedRocketry.client.render.planet.RenderPlanetarySky;
import zmaster587.advancedRocketry.world.DimensionProperties;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;
import net.minecraft.world.WorldProvider;
import net.minecraft.world.WorldType;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraftforge.client.IRenderHandler;

public class ProviderPlanet extends WorldProvider implements IPlanetaryProvider {
	private IRenderHandler skyRender;

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
	@SideOnly(Side.CLIENT)
	public IRenderHandler getSkyRenderer() {
		return skyRender == null ? skyRender = new RenderPlanetarySky() : skyRender;
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
		this.worldChunkMgr = new ChunkManagerPlanet(worldObj);
		//AdvancedRocketry.planetWorldType.getChunkManager(worldObj);
	}

	@Override
	public IChunkProvider createChunkGenerator() {
		return new ChunkProviderPlanet(worldObj, worldObj.getSeed(), false);
	}
	
	@Override
	public void updateWeather() {

		if(getAtmosphereDensity() > 75)
			super.updateWeather();
	}

	@Override
	public boolean canRespawnHere() {
		return false;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public float[] calcSunriseSunsetColors(float p_76560_1_, float p_76560_2_) {
		float[] colors = getDimensionProperties().sunriseSunsetColors;

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
			finalColors[3] = f6 * (getAtmosphereDensityFromHeight(Minecraft.getMinecraft().renderViewEntity.posY));
			return finalColors;
		}
		else
		{
			return null;
		}

		//return super.calcSunriseSunsetColors(p_76560_1_, p_76560_2_); //getDimensionProperties().sunriseSunsetColors;
	}

	@Override
	public float getSunBrightness(float par1) {
		float atmosphere = getAtmosphereDensity();
		return super.getSunBrightness(par1)*(0.5f + (2 - atmosphere)*.5f) - (1-atmosphere)*0.4f;
	}

	//No clouds
	@Override
	public float getCloudHeight() {
		return getAtmosphereDensity() > .75f ? 128 : 0;
	}


	//Always see stars
	@Override
	@SideOnly(Side.CLIENT)
	public float getStarBrightness(float par1) {
		return super.getStarBrightness(par1);

	}
	@Override
	@SideOnly(Side.CLIENT)
	public boolean isSkyColored() {
		return false;
	}

	@Override
	public Vec3 getSkyColor(Entity cameraEntity, float partialTicks) {
		float[] vec = getDimensionProperties().skyColor;
		if(vec == null)
			return super.getSkyColor(cameraEntity, partialTicks);
		else {
			Vec3 skyColorVec =  Vec3.createVectorHelper(1, 1, 1);//super.getSkyColor(cameraEntity, partialTicks);
			return Vec3.createVectorHelper(vec[0] * skyColorVec.xCoord, vec[1] * skyColorVec.yCoord, vec[2] * skyColorVec.zCoord) ;
		}
	}

	@Override
	@SideOnly(Side.CLIENT)
	public Vec3 getFogColor(float p_76562_1_, float p_76562_2_) {

		Vec3 superVec = super.getFogColor(p_76562_1_, p_76562_2_);
		//float multiplier = getAtmosphereDensityFromHeight(Minecraft.getMinecraft().renderViewEntity.posY);

		float[] vec = getDimensionProperties().fogColor;
		return Vec3.createVectorHelper(vec[0] * superVec.xCoord, vec[1] * superVec.yCoord, vec[2] * superVec.zCoord);
	}

	@Override
	public boolean doesXZShowFog(int p_76568_1_, int p_76568_2_) {
		return getDimensionProperties().atmosphereDensity > 100;
	}

	@Override
	public String getDimensionName() {
		return getDimensionProperties().name;
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
		int j = (int)(p_76563_1_ % getRotationalPeriod());
		float f1 = ((float)j + p_76563_3_) / (float)getRotationalPeriod() - 0.25F;

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
	public double getGravitationalMultiplier() {
		return getDimensionProperties().gravitationalMultiplier;
	}

	@Override
	public int getOrbitingDimension() {
		return 0;
	}

	@Override
	public int[] getDimensionsInOrbit() {
		return null;
	}

	@Override
	public int getOrbitalDistance() {
		return getDimensionProperties().orbitalDist;
	}

	@Override
	public float getAtmosphereDensity() {
		return (float)(getDimensionProperties().atmosphereDensity/100f);
	}
	
	@Override
	public float getAtmosphereDensityFromHeight(double y) {
		return getDimensionProperties().getAtmosphereDensityAtHeight(y);
	}

	@Override
	public int getAverageTemperature() {
		return getDimensionProperties().averageTemperature;
	}

	@Override
	public int getRotationalPeriod() {
		return getDimensionProperties().rotationalPeriod;
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
	public Vec3 getSunColor() {
		float[] vec = getDimensionProperties().sunColor;
		return Vec3.createVectorHelper(vec[0],vec[1],vec[2]);
	}
	
	@Override
	public DimensionProperties getDimensionProperties() {
		DimensionProperties properties = DimensionManager.getInstance().getDimensionProperties(this.dimensionId);
		return properties == null ? new DimensionProperties(this.dimensionId) : properties;
	}
}
