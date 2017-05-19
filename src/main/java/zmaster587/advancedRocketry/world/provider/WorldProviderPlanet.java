package zmaster587.advancedRocketry.world.provider;


import java.util.Set;

import org.apache.commons.lang3.ArrayUtils;

import zmaster587.advancedRocketry.AdvancedRocketry;
import zmaster587.advancedRocketry.api.AdvancedRocketryBlocks;
import zmaster587.advancedRocketry.api.Configuration;
import zmaster587.advancedRocketry.api.IPlanetaryProvider;
import zmaster587.advancedRocketry.atmosphere.AtmosphereHandler;
import zmaster587.advancedRocketry.client.render.planet.RenderPlanetarySky;
import zmaster587.advancedRocketry.dimension.DimensionManager;
import zmaster587.advancedRocketry.dimension.DimensionProperties;
import zmaster587.advancedRocketry.world.ChunkManagerPlanet;
import zmaster587.advancedRocketry.world.ChunkProviderPlanet;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.DimensionType;
import net.minecraft.world.WorldProvider;
import net.minecraft.world.biome.BiomeProvider;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.IChunkGenerator;
import net.minecraftforge.client.IRenderHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class WorldProviderPlanet extends WorldProvider implements IPlanetaryProvider {
	private IRenderHandler skyRender;
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
	public IChunkGenerator createChunkGenerator() {
		return new ChunkProviderPlanet(this.world, this.world.getSeed(), false, world.getWorldInfo().getGeneratorOptions());
	}

	@Override
	@SideOnly(Side.CLIENT)
	public IRenderHandler getSkyRenderer() {
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
		world.getWorldInfo().setTerrainType(AdvancedRocketry.planetWorldType);


		this.biomeProvider = new ChunkManagerPlanet(world, world.getWorldInfo().getGeneratorOptions(), DimensionManager.getInstance().getDimensionProperties(world.provider.getDimension()).getBiomes());
		this.chunkMgrTerraformed = new ChunkManagerPlanet(world, world.getWorldInfo().getGeneratorOptions(), DimensionManager.getInstance().getDimensionProperties(world.provider.getDimension()).getTerraformedBiomes());
		//AdvancedRocketry.planetWorldType.getChunkManager(worldObj);
	}
	@Override
	public boolean canDoRainSnowIce(Chunk chunk) {
		// TODO Auto-generated method stub
		return getAtmosphereDensity(new BlockPos(0,0,0)) > 75 ? super.canDoRainSnowIce(chunk) : false;
	}
	@Override
	public void updateWeather() {

		if(getAtmosphereDensity(new BlockPos(0,0,0)) > 75)
			super.updateWeather();
	}

	@Override
	public int getRespawnDimension(EntityPlayerMP player) {
		if(AtmosphereHandler.hasAtmosphereHandler(getDimension()) && Configuration.canPlayerRespawnInSpace) {
			BlockPos coords = player.getBedLocation(getDimension());
			
			if(coords != null && world.getBlockState(coords).getBlock() == AdvancedRocketryBlocks.blockAstroBed && AtmosphereHandler.getOxygenHandler(player.world.provider.getDimension()).getAtmosphereType(coords).isBreathable())
				return getDimension();
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

		float[] colors = getDimensionProperties(new BlockPos((int)Minecraft.getMinecraft().player.posX,0 , (int)Minecraft.getMinecraft().player.posZ)).sunriseSunsetColors;

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
		float atmosphere = getAtmosphereDensity(new BlockPos(0,0,0));
		Math.abs(1-atmosphere);
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

		//Eclipse handling
		if(this.world.isRemote) {
			DimensionProperties properties = getDimensionProperties(Minecraft.getMinecraft().player.getPosition());
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
			lightValue *= phiMuliplier*(MathHelper.clamp((float) ((difference/20f) + (Math.abs(currentTheta - 180)*difference)/(10f)) ,0,1)) + (1-phiMuliplier);
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
	public Vec3d getSkyColor(Entity cameraEntity, float partialTicks) {
		float[] vec = getDimensionProperties(new BlockPos((int)cameraEntity.posX, 0, (int)cameraEntity.posZ)).skyColor;
		if(vec == null)
			return super.getSkyColor(cameraEntity, partialTicks);
		else {
			Vec3d skyColorVec = super.getSkyColor(cameraEntity, partialTicks);
			return new Vec3d(vec[0] * skyColorVec.xCoord, vec[1] * skyColorVec.yCoord, vec[2] * skyColorVec.zCoord) ;
		}
	}

	@Override
	@SideOnly(Side.CLIENT)
	public Vec3d getFogColor(float p_76562_1_, float p_76562_2_) {

		Vec3d superVec = super.getFogColor(p_76562_1_, p_76562_2_);
		//float multiplier = getAtmosphereDensityFromHeight(Minecraft.getMinecraft().renderViewEntity.posY);

		float[] vec = getDimensionProperties(new BlockPos((int)Minecraft.getMinecraft().player.posX, 0, (int)Minecraft.getMinecraft().player.posZ)).fogColor;
		return new Vec3d(vec[0] * superVec.xCoord, vec[1] * superVec.yCoord, vec[2] * superVec.zCoord);
	}

	@Override
	public boolean doesXZShowFog(int x, int z) {
		return getDimensionProperties(new BlockPos(x,0,z)).getAtmosphereDensity() > 100;
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
	public double getGravitationalMultiplier(BlockPos pos) {
		return getDimensionProperties(pos).gravitationalMultiplier;
	}

	@Override
	public int getOrbitingDimension(BlockPos pos) {
		return getDimensionProperties(pos).getParentPlanet();
	}

	@Override
	public int[] getDimensionsInOrbit(BlockPos pos) {
		Set<Integer> intSet = getDimensionProperties(pos).getChildPlanets();
		Integer[] intArray = new Integer[intSet.size()];
		return ArrayUtils.toPrimitive(getDimensionProperties(pos).getChildPlanets().toArray(intArray));
	}

	@Override
	public int getOrbitalDistance(BlockPos pos) {
		return getDimensionProperties(pos).orbitalDist;
	}

	@Override
	public float getAtmosphereDensity(BlockPos pos) {
		return (float)(getDimensionProperties(pos).getAtmosphereDensity()/100f);
	}

	@Override
	public float getAtmosphereDensityFromHeight(double y, BlockPos pos) {
		return getDimensionProperties(pos).getAtmosphereDensityAtHeight(y);
	}

	@Override
	public int getAverageTemperature(BlockPos pos) {
		return getDimensionProperties(pos).averageTemperature;
	}

	@Override
	public int getRotationalPeriod(BlockPos pos) {
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
	public Vec3d getSunColor(BlockPos pos) {
		float[] vec = getDimensionProperties(pos).getSunColor();
		return new Vec3d(vec[0],vec[1],vec[2]);
	}


	public int getSolarOrbitalDistance(BlockPos pos) {
		return getDimensionProperties(pos).getSolarOrbitalDistance();
	}

	@Override
	public DimensionProperties getDimensionProperties(BlockPos pos) {
		DimensionProperties properties = DimensionManager.getInstance().getDimensionProperties(this.getDimension());
		return properties == null ? new DimensionProperties(this.getDimension()) : properties;
	}

	@Override
	public DimensionType getDimensionType() {
		return DimensionManager.PlanetDimensionType;
	}
}
