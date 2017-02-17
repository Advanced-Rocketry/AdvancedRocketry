package zmaster587.advancedRocketry.api;

import java.util.LinkedList;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraftforge.common.config.Property;

/**
 * Stores config variables
 *
 */
public class Configuration {
	public static final String configFolder = "advRocketry";
	
	public static int orbit = 1000;
	public static int MoonId = -1;
	public static int spaceDimId = -2;
	public static int fuelPointsPer10Mb = 10;
	public static int stationSize = 1024;
	
	public static double rocketThrustMultiplier;
	public static double fuelCapacityMultiplier;
	
	public static int maxBiomes = 512;
	public static boolean rocketRequireFuel = true;
	public static boolean enableOxygen = true;
	public static float buildSpeedMultiplier = 1f;
	
	public static boolean generateCopper;
	public static int copperPerChunk; 
	public static int copperClumpSize;
	
	public static boolean generateTin;
	public static int tinPerChunk;
	public static int tinClumpSize;
	
	public static boolean generateDilithium;
	public static int dilithiumClumpSize;
	public static int dilithiumPerChunk;
	public static int dilithiumPerChunkMoon;
	
	public static int aluminumPerChunk;
	public static int aluminumClumpSize;
	public static boolean generateAluminum;
	
	public static boolean generateRutile;
	public static int rutilePerChunk;
	public static int rutileClumpSize;
	public static boolean allowMakingItemsForOtherMods;
	public static boolean scrubberRequiresCartrige;
	public static float EUMult;
	public static float RFMult;
	public static boolean overrideGCAir;
	public static int fuelPointsPerDilithium;
	public static boolean electricPlantsSpawnLightning;

	public static boolean allowSawmillVanillaWood;

	public static double asteroidMiningMult;

	public static List<String> standardAsteroidOres = new LinkedList<String>();

	public static int atmosphereHandleBitMask;

	public static boolean automaticRetroRockets;

	public static boolean advancedVFX;

	public static boolean enableLaserDrill;

	public static int spaceSuitOxygenTime;

	public static float travelTimeMultiplier;

	public static int maxBiomesPerPlanet;

	public static boolean enableTerraforming;

	public static double gasCollectionMult;
	public static boolean allowTerraforming;

	public static int terraformingBlockSpeed;
	public static double terraformSpeed;
	public static boolean terraformRequiresFluid;
	public static float microwaveRecieverMulitplier;
	public static boolean blackListAllVanillaBiomes;
	public static double asteroidMiningTimeMult;
	public static boolean canPlayerRespawnInSpace;
	public static float spaceLaserPowerMult;
	public static List<Integer> laserBlackListDims = new LinkedList<Integer>();

	public static List<String> standardLaserDrillOres = new LinkedList<String>();

	public static boolean laserDrillPlanet;

	/** list of entities of which atmospheric effects should not be applied **/
	public static List<Class> bypassEntity = new LinkedList<Class>();
	
	public static List<Block> torchBlocks = new LinkedList<Block>();
	public static List<String> standardGeodeOres = new LinkedList<String>();

	public static boolean asteriodOresBlackList;
	public static boolean geodeOresBlackList;
	public static boolean laserDrillOresBlackList;

	public static boolean lockUI;

	public static int oxygenVentSize;

	public static int solarGeneratorMult;

	

}
