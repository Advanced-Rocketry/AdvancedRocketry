package zmaster587.advancedRocketry.world.provider;


import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.DimensionType;
import net.minecraft.world.WorldProvider;
import net.minecraft.world.biome.BiomeProvider;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.gen.IChunkGenerator;
import net.minecraft.world.storage.DerivedWorldInfo;
import net.minecraft.world.storage.WorldInfo;
import net.minecraftforge.client.IRenderHandler;
import net.minecraftforge.fml.relauncher.ReflectionHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import org.apache.commons.lang3.ArrayUtils;

import zmaster587.advancedRocketry.AdvancedRocketry;
import zmaster587.advancedRocketry.api.ARConfiguration;
import zmaster587.advancedRocketry.api.IAtmosphere;
import zmaster587.advancedRocketry.api.IPlanetaryProvider;
import zmaster587.advancedRocketry.api.dimension.solar.StellarBody;
import zmaster587.advancedRocketry.atmosphere.AtmosphereHandler;
import zmaster587.advancedRocketry.capability.DimensionCompat;
import zmaster587.advancedRocketry.client.render.planet.RenderPlanetarySky;
import zmaster587.advancedRocketry.dimension.DimensionManager;
import zmaster587.advancedRocketry.dimension.DimensionProperties;
import zmaster587.advancedRocketry.integration.CompatibilityMgr;
import zmaster587.advancedRocketry.util.AstronomicalBodyHelper;
import zmaster587.advancedRocketry.world.ChunkManagerPlanet;
import zmaster587.advancedRocketry.world.ChunkProviderCavePlanet;
import zmaster587.advancedRocketry.world.ChunkProviderPlanet;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Set;

public class WorldProviderPlanet extends WorldProvider implements IPlanetaryProvider {
	public BiomeProvider chunkMgrTerraformed;

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
	@Nonnull
	public IChunkGenerator createChunkGenerator() {
		int genType = DimensionManager.getInstance().getDimensionProperties(world.provider.getDimension()).getGenType();
		if(genType == 1)
		{
			return new ChunkProviderCavePlanet(this.world, false, this.world.getSeed(),world.getWorldInfo().getGeneratorOptions());
		}
		else
			return new ChunkProviderPlanet(this.world, this.world.getSeed(), ARConfiguration.getCurrentConfig().generateVanillaStructures, world.getWorldInfo().getGeneratorOptions());
	}

	@Override
	@SideOnly(Side.CLIENT)
	@Nullable
	public IRenderHandler getSkyRenderer() {
		if(!ARConfiguration.getCurrentConfig().planetSkyOverride)
			return null;
		
		int genType = DimensionManager.getInstance().getDimensionProperties(world.provider.getDimension()).getGenType();
		
		
		if(super.getSkyRenderer() == null)
			 setSkyRenderer(new RenderPlanetarySky());
		return super.getSkyRenderer();
	}

	@Override
	public long getSeed() {
		return super.getSeed() + getDimension();
	}

	/*@Override
	public BiomeGenBase getBiomeGenForCoords(int x, int z) {
		super.getBiomeGenForCoords(x, z)
		return worldChunkMgr.getBiomeGenAt(x, z);
		//return getDimensionProperties().biomeProperties.getChunkPropertiesFromBlockCoords(x, z).getBiomeGenForWorldCoords(x & 15, z & 15, this.worldChunkMgr);
	}*/

	
	
	@Override
	protected void init()
	{
		this.hasSkyLight = true;
		world.getWorldInfo().setTerrainType(AdvancedRocketry.planetWorldType);


		this.biomeProvider = new ChunkManagerPlanet(world, world.getWorldInfo().getGeneratorOptions(), DimensionManager.getInstance().getDimensionProperties(world.provider.getDimension()).getBiomes());
		this.chunkMgrTerraformed = new ChunkManagerPlanet(world, world.getWorldInfo().getGeneratorOptions(), DimensionManager.getInstance().getDimensionProperties(world.provider.getDimension()).getTerraformedBiomes());
		//AdvancedRocketry.planetWorldType.getChunkManager(worldObj);
	}
	@Override
	public boolean canDoRainSnowIce(Chunk chunk) {
		return getAtmosphereDensity(new BlockPos(0, 0, 0)) > 75 && super.canDoRainSnowIce(chunk);
	}
	
	@Override
	public void calculateInitialWeather() {
		super.calculateInitialWeather();
		doWeatherStuff();
	}
	
	@Override
	public void updateWeather() {
		super.updateWeather();
		doWeatherStuff();
	}
	
	private void doWeatherStuff()
	{
		if (getAtmosphereDensity(new BlockPos(0,0,0)) <= 75 && world.isRaining()) {
			if(!CompatibilityMgr.isSpongeInstalled)
			{
				try
				{
					WorldInfo worldInfo = ReflectionHelper.getPrivateValue(DerivedWorldInfo.class, (DerivedWorldInfo)this.world.getWorldInfo(), "delegate", "field_76115_a");
					worldInfo.setRaining(false);
				}
				catch (ClassCastException e)
				{
					//Fallback.  Sometimes mods screw with worldInfo
					this.world.getWorldInfo().setRaining(false);
				}
			}
			else
				//Hope that sponge cooperates
				this.world.getWorldInfo().setRaining(false);
		}
	}

	@Override
	public int getRespawnDimension(@Nonnull EntityPlayerMP player) {
		if(ARConfiguration.getCurrentConfig().canPlayerRespawnInSpace) {
			BlockPos coords = player.getBedLocation(getDimension());
			AtmosphereHandler atmhandler = AtmosphereHandler.getOxygenHandler(player.world.provider.getDimension());

			//this absolutely can be null, ignore your IDE's warning!
			if (ARConfiguration.getCurrentConfig().forcePlayerRespawnInSpace || coords != null && AtmosphereHandler.hasAtmosphereHandler(player.world.provider.getDimension()) && atmhandler != null && atmhandler.getAtmosphereType(coords).isBreathable()) {
				return getDimension();
			}
		}

		return DimensionCompat.getDefaultSpawnDimension();
	}
	
	@Override
	@Nonnull
	public WorldSleepResult canSleepAt(@Nonnull EntityPlayer player, @Nonnull BlockPos pos) {
		AtmosphereHandler atmhandler = AtmosphereHandler.getOxygenHandler(player.world.provider.getDimension());

		if (ARConfiguration.getCurrentConfig().forcePlayerRespawnInSpace || AtmosphereHandler.hasAtmosphereHandler(player.world.provider.getDimension()) && atmhandler != null && atmhandler.getAtmosphereType(pos).isBreathable()) {
			return WorldSleepResult.ALLOW;
		}
		else
		{
			return WorldSleepResult.DENY;
		}
	}
	
	@Override
	public boolean canRespawnHere() {
		return false;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public float[] calcSunriseSunsetColors(float p_76560_1_, float p_76560_2_) {

		float[] colors = getDimensionProperties(new BlockPos((int)Minecraft.getMinecraft().player.posX,0 , (int)Minecraft.getMinecraft().player.posZ)).sunriseSunsetColors;

		if(colors == null)
			return super.calcSunriseSunsetColors(p_76560_1_, p_76560_2_);

		float[] finalColors = new float[4];

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
			finalColors[3] = f6 * (getAtmosphereDensityFromHeight(Minecraft.getMinecraft().getRenderViewEntity().posY, new BlockPos((int)Minecraft.getMinecraft().player.posX, 0, (int)Minecraft.getMinecraft().player.posZ)));
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
		DimensionProperties properties = getDimensionProperties(Minecraft.getMinecraft().player.getPosition());
		StellarBody star = properties.getStar();
		
		//This is inaccurate at times, moreso for atmosphere, but I am NOT doing the required math for the realistic counterpart
		float atmosphere = getAtmosphereDensity(new BlockPos(0,0,0));
		//calculateCelestialAngle(p_76563_1_, p_76563_3_)
		float f1 = world.getCelestialAngle(partialTicks);
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

		//Vary brightness depending upon sun luminosity and planet distance
		//This takes into account how eyes work, that they're not linear in sensing light
		f2 *= (float)AstronomicalBodyHelper.getPlanetaryLightLevelMultiplier(AstronomicalBodyHelper.getStellarBrightness(star, properties.getSolarOrbitalDistance()));
		
		//Eclipse handling
		if(this.world.isRemote) {
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

	private float eclipseValue(@Nonnull DimensionProperties properties, float lightValue, double partialTicks) {
		
		double currentTheta = (((partialTicks*properties.orbitTheta + ((1-partialTicks)*properties.prevOrbitalTheta)) * 180/Math.PI)  % 360d);
		int solarDistance = properties.getSolarOrbitalDistance();
		float planetaryDistance = properties.getParentOrbitalDistance();

		float difference = solarDistance/(200-planetaryDistance + 0.00001f);
		
		
		float phiMultiplier = Math.max(Math.abs(MathHelper.cos((float)(properties.orbitalPhi * Math.PI/180)))-0.95f, 0)*20;

		int offset = (int)((200-planetaryDistance)/2f);

		//1 is fast attenuation
		//-1 is no atten
		//solar distance conrols fade, planetary distance controls duration
		if(phiMultiplier !=0 && currentTheta > 180 - offset && currentTheta < 180 + offset ) {
			lightValue *= phiMultiplier*(MathHelper.clamp((float) ((difference/20f) + (Math.abs(currentTheta - 180)*difference)/(10f)) ,0,1)) + (1-phiMultiplier);
			//f2 = 0;
		}
		return lightValue;
	}

	//No clouds
	@Override
	public float getCloudHeight() {
		return getAtmosphereDensity(new BlockPos(0,0,0)) > .75f ? 128 : -2000;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public boolean isSkyColored() {
		return false;
	}

	@Override
	@Nonnull
	public Vec3d getSkyColor(@Nonnull Entity cameraEntity, float partialTicks) {
		float[] vec = getDimensionProperties(new BlockPos((int)cameraEntity.posX, 0, (int)cameraEntity.posZ)).skyColor;
		if(vec == null)
			return super.getSkyColor(cameraEntity, partialTicks);
		else {
			Vec3d skyColorVec = super.getSkyColor(cameraEntity, partialTicks);
			return new Vec3d(vec[0] * skyColorVec.x, vec[1] * skyColorVec.y, vec[2] * skyColorVec.z) ;
		}
	}

	@Override
	@SideOnly(Side.CLIENT)
	@Nonnull
	public Vec3d getFogColor(float p_76562_1_, float p_76562_2_) {

		Vec3d superVec = super.getFogColor(p_76562_1_, p_76562_2_);
		//float multiplier = getAtmosphereDensityFromHeight(Minecraft.getMinecraft().renderViewEntity.posY);

		float[] vec = getDimensionProperties(new BlockPos((int)Minecraft.getMinecraft().player.posX, 0, (int)Minecraft.getMinecraft().player.posZ)).fogColor;
		return new Vec3d(vec[0] * superVec.x, vec[1] * superVec.y, vec[2] * superVec.z);
	}

	@Override
	public boolean doesXZShowFog(int x, int z) {
		return getDimensionProperties(new BlockPos(x,0,z)).getAtmosphereDensity() > 100;
	}

	@Override
	public double getHorizon() {
		int genType = DimensionManager.getInstance().getDimensionProperties(world.provider.getDimension()).getGenType();
		if (genType == 2)
			return 0;
		return 63;
	}

	@Override
	public float calculateCelestialAngle(long p_76563_1_, float p_76563_3_) {
		int rotationalPeriod;
		rotationalPeriod = getRotationalPeriod(new BlockPos(0,0,0));


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
	public double getGravitationalMultiplier(@Nullable BlockPos pos) {
		return getDimensionProperties(pos).gravitationalMultiplier;
	}

	@Override
	public int getOrbitingDimension(@Nullable BlockPos pos) {
		return getDimensionProperties(pos).getParentPlanet();
	}

	@Override
	public int[] getDimensionsInOrbit(@Nullable BlockPos pos) {
		Set<Integer> intSet = getDimensionProperties(pos).getChildPlanets();
		Integer[] intArray = new Integer[intSet.size()];
		return ArrayUtils.toPrimitive(getDimensionProperties(pos).getChildPlanets().toArray(intArray));
	}

	@Override
	public int getOrbitalDistance(@Nullable BlockPos pos) {
		return getDimensionProperties(pos).orbitalDist;
	}
	
	@Override
	public IAtmosphere getAtmosphere(@Nullable BlockPos pos) {
		return getDimensionProperties(pos).getAtmosphere();
	}

	@Override
	public float getAtmosphereDensity(@Nullable BlockPos pos) {
		return getDimensionProperties(pos).getAtmosphereDensity() / 100f;
	}

	@Override
	public float getAtmosphereDensityFromHeight(double y, @Nullable BlockPos pos) {
		return getDimensionProperties(pos).getAtmosphereDensityAtHeight(y);
	}

	@Override
	public int getAverageTemperature(@Nullable BlockPos pos) {
		return getDimensionProperties(pos).getAverageTemp();
	}

	@Override
	public int getRotationalPeriod(@Nullable BlockPos pos) {
		return getDimensionProperties(new BlockPos(0,0,0)).rotationalPeriod;
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
	public Vec3d getSunColor(@Nullable BlockPos pos) {
		float[] vec = getDimensionProperties(pos).getSunColor();
		return new Vec3d(vec[0],vec[1],vec[2]);
	}


	public int getSolarOrbitalDistance(@Nullable BlockPos pos) {
		return getDimensionProperties(pos).getSolarOrbitalDistance();
	}

	@Override
	public DimensionProperties getDimensionProperties(@Nullable BlockPos pos) {
		DimensionProperties properties = DimensionManager.getInstance().getDimensionProperties(this.getDimension());
		return properties == null ? new DimensionProperties(this.getDimension()) : properties;
	}

	@Override
	@Nonnull
	public DimensionType getDimensionType() {
		return DimensionManager.PlanetDimensionType;
	}
}
