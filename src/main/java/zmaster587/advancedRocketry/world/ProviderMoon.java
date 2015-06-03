package zmaster587.advancedRocketry.world;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import zmaster587.advancedRocketry.client.render.RenderSkyMoon;
import zmaster587.advancedRocketry.util.Configuration;
import net.minecraft.util.Vec3;
import net.minecraft.world.WorldProvider;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.biome.WorldChunkManagerHell;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraftforge.client.IRenderHandler;

public class ProviderMoon extends WorldProvider {

	static final BiomeGenBase moonBiome = new BiomeGenMoon(90, true);
	private IRenderHandler skyRender;

	@Override
	protected void registerWorldChunkManager() {
		this.worldChunkMgr = new WorldChunkManagerHell(moonBiome, 0.0f);

		this.dimensionId = Configuration.MoonId;
		this.hasNoSky = false;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public IRenderHandler getSkyRenderer() {
		if(skyRender == null)
			skyRender = new RenderSkyMoon();

		return skyRender;
	}
	
	@Override
	public boolean canRespawnHere() {
		return false;
	}

	@Override
	public float[] calcSunriseSunsetColors(float p_76560_1_, float p_76560_2_) {
		return null;
	}
	
	@Override
	public float getSunBrightness(float par1) {
		return super.getSunBrightness(par1)*0.5f - 0.2f;
	}
	
	//No clouds
	@Override
	public float getCloudHeight() {
		return 0;
	}
	
	
	//Always see stars
	@Override
	@SideOnly(Side.CLIENT)
	public float getStarBrightness(float par1) {
		return .6f;
	}
	@Override
	@SideOnly(Side.CLIENT)
	public boolean isSkyColored() {
		return false;
	}

	@Override
	public Vec3 getFogColor(float p_76562_1_, float p_76562_2_) {
		// TODO Auto-generated method stub
		return Vec3.createVectorHelper(0d,0d,0d);
	}

	@Override
	public IChunkProvider createChunkGenerator() {
		return new ChunkProviderMoon(worldObj, worldObj.getSeed(), false);
	}

	@Override
	public String getDimensionName() {
		return "Moon";
	}

	//Make the world deep
	@Override
	public int getAverageGroundLevel() {
		return 96;
	}
	
	@Override
	public float calculateCelestialAngle(long p_76563_1_, float p_76563_3_) {
		 int j = (int)(p_76563_1_ % 240000L);
	        float f1 = ((float)j + p_76563_3_) / 240000.0F - 0.25F;

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
	public BiomeGenBase getBiomeGenForCoords(int x, int z) {
		return moonBiome;
	}
}
