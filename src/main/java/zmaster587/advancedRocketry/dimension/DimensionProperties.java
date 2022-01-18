package zmaster587.advancedRocketry.dimension;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.fluid.Fluid;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.*;
import net.minecraft.util.IItemProvider;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.Biome.Category;
import net.minecraftforge.common.util.Constants.NBT;
import net.minecraftforge.registries.ForgeRegistries;
import zmaster587.advancedRocketry.AdvancedRocketry;
import zmaster587.advancedRocketry.api.AdvancedRocketryBiomes;
import zmaster587.advancedRocketry.api.ARConfiguration;
import zmaster587.advancedRocketry.api.AdvancedRocketryAPI;
import zmaster587.advancedRocketry.api.Constants;
import zmaster587.advancedRocketry.api.IAtmosphere;
import zmaster587.advancedRocketry.api.SatelliteRegistry;
import zmaster587.advancedRocketry.api.atmosphere.AtmosphereRegister;
import zmaster587.advancedRocketry.api.dimension.IDimensionProperties;
import zmaster587.advancedRocketry.api.dimension.solar.StellarBody;
import zmaster587.advancedRocketry.api.satellite.SatelliteBase;
import zmaster587.advancedRocketry.api.stations.ISpaceObject;
import zmaster587.advancedRocketry.atmosphere.AtmosphereType;
import zmaster587.advancedRocketry.client.render.planet.ISkyRenderer;
import zmaster587.advancedRocketry.inventory.TextureResources;
import zmaster587.advancedRocketry.network.PacketDimInfo;
import zmaster587.advancedRocketry.network.PacketSatellite;
import zmaster587.advancedRocketry.stations.SpaceObjectManager;
import zmaster587.advancedRocketry.util.AstronomicalBodyHelper;
import zmaster587.advancedRocketry.util.OreGenProperties;
import zmaster587.advancedRocketry.util.SpacePosition;
import zmaster587.advancedRocketry.util.SpawnListEntryNBT;
import zmaster587.libVulpes.network.PacketHandler;
import zmaster587.libVulpes.util.HashedBlockPosition;
import zmaster587.libVulpes.util.VulpineMath;
import zmaster587.libVulpes.util.ZUtils;

import java.util.*;
import java.util.Map.Entry;

public class DimensionProperties implements Cloneable, IDimensionProperties {

	/**
	 * Temperatures are stored in Kelvin
	 * This facilitates precise temperature calculations and specifications
	 * 286 is Earthlike (13 C), Hot is 52 C, Cold is -23 C. Snowball is absolute zero
	 */
	public enum Temps {
		TOOHOT(450),
		HOT(325),
		NORMAL(275),
		COLD(250),
		FRIGID(175),
		SNOWBALL(0);

		private int temp;
		Temps(int i) {
			temp = i;
		}

		@Deprecated
		public int getTemp() {
			return temp;
		}


		public boolean hotterThan(Temps type)
		{
			return this.compareTo(type) < 0;
		}

		public boolean colderThan(Temps type)
		{
			return this.compareTo(type) > 0;
		}

		/**
		 * @param lowerBound lower Bound (inclusive)
		 * @param upperBound upper Bound (inclusive)
		 * @return true if this resides between the to bounds
		 */
		public boolean isInRange(Temps lowerBound, Temps upperBound) {
			return this.compareTo(lowerBound) <= 0 && this.compareTo(upperBound) >= 0;
		}

		/**
		 * @return a temperature that refers to the supplied value
		 */

		public static Temps getTempFromValue(int value) {
			for(Temps type : Temps.values()) {
				if(value > type.temp)
					return type;
			}
			return SNOWBALL;
		}
	}

	/**
	 * Contains standardized pressure ranges for planets
	 * where 100 is earthlike, largers values are higher pressure
	 */
	public enum AtmosphereTypes {
		SUPERHIGHPRESSURE(800),
		HIGHPRESSURE(200),
		NORMAL(75),
		LOW(25),
		NONE(0);

		private int value;

		AtmosphereTypes(int value) {
			this.value = value;
		}

		public int getAtmosphereValue() {
			return value;
		}

		public boolean denserThan(AtmosphereTypes type)
		{
			return this.compareTo(type) < 0;
		}

		public boolean lessDenseThan(AtmosphereTypes type)
		{
			return this.compareTo(type) > 0;
		}

		public static AtmosphereTypes getAtmosphereTypeFromValue(int value) {
			for(AtmosphereTypes type : AtmosphereTypes.values()) {
				if(value > type.value)
					return type;
			}
			return NONE;
		}
	}

	/**
	 * Contains default graphic {@link ResourceLocation} to display for different planet types
	 *
	 */
	public static final ResourceLocation atmosphere = new ResourceLocation("advancedrocketry:textures/planets/atmosphere2.png");
	public static final ResourceLocation atmosphereLEO = new ResourceLocation("advancedrocketry:textures/planets/atmosphereleo.png");
	public static final ResourceLocation atmGlow = new ResourceLocation("advancedrocketry:textures/planets/atmglow.png");
	public static final ResourceLocation planetRings = new ResourceLocation("advancedrocketry:textures/planets/rings.png");
	public static final ResourceLocation planetRingShadow = new ResourceLocation("advancedrocketry:textures/planets/ringshadow.png");

	public static final ResourceLocation shadow = new ResourceLocation("advancedrocketry:textures/planets/shadow.png");
	public static final ResourceLocation shadow3 = new ResourceLocation("advancedrocketry:textures/planets/shadow3.png");

	public enum PlanetIcons {
		EARTHLIKE(new ResourceLocation("advancedrocketry:textures/planets/earthlike.png")),
		LAVA(new ResourceLocation("advancedrocketry:textures/planets/lava.png")),
		MARSLIKE(new ResourceLocation("advancedrocketry:textures/planets/marslike.png")),
		MOON(new ResourceLocation("advancedrocketry:textures/planets/moon.png")),
		WATERWORLD(new ResourceLocation("advancedrocketry:textures/planets/waterworld.png")),
		ICEWORLD(new ResourceLocation("advancedrocketry:textures/planets/iceworld.png")),
		DESERT(new ResourceLocation("advancedrocketry:textures/planets/desertworld.png")),
		CARBON(new ResourceLocation("advancedrocketry:textures/planets/carbonworld.png")),
		VENUSIAN(new ResourceLocation("advancedrocketry:textures/planets/venusian.png")),
		GASGIANTBLUE(new ResourceLocation("advancedrocketry:textures/planets/gasgiantblue.png")),
		GASGIANTRED(new ResourceLocation("advancedrocketry:textures/planets/gasgiantred.png")),
		GASGIANTBROWN(new ResourceLocation("advancedrocketry:textures/planets/gasgiantbrown.png")),
		ASTEROID(new ResourceLocation("advancedrocketry:textures/planets/asteroid.png")),
		UNKNOWN(new ResourceLocation("advancedrocketry:textures/planets/unknown.png"))
		;



		private ResourceLocation resource;
		private ResourceLocation resourceLEO;

		PlanetIcons(ResourceLocation resource) {
			this.resource = resource;

			this.resourceLEO = new ResourceLocation(resource.toString().substring(0, resource.toString().length() - 4) + "leo.jpg");
		}

		PlanetIcons(ResourceLocation resource, ResourceLocation leo) {
			this.resource = resource;

			this.resourceLEO = atmosphereLEO;
		}

		public ResourceLocation getResource() {
			return resource;
		}

		public ResourceLocation getResourceLEO() {
			return resourceLEO;
		}
	}

	public static final int MAX_ATM_PRESSURE = 1600;
	public static final int MIN_ATM_PRESSURE = 0;

	public static final int MAX_DISTANCE = Integer.MAX_VALUE;
	public static final int MIN_DISTANCE = 1;

	public static final int MAX_GRAVITY = 400;
	public static final int MIN_GRAVITY = 0;



	//True if dimension is managed and created by AR (false otherwise)
	public boolean isNativeDimension;
	public boolean skyRenderOverride;
	//Gas giants DO NOT need a dimension registered to them
	public float[] skyColor;
	public float[] fogColor;
	public float[] ringColor;
	public float gravitationalMultiplier;
	public int orbitalDist;
	public boolean hasOxygen;
	public boolean colorOverride;
	private int originalAtmosphereDensity;
	//Used in solar panels
	public double peakInsolationMultiplier;
	public double peakInsolationMultiplierWithoutAtmosphere;
	private int atmosphereDensity;
	//Stored in Kelvin
	public int averageTemperature;
	public int rotationalPeriod;
	//Stored in radians
	public double orbitTheta;
	public double baseOrbitTheta;
	public double prevOrbitalTheta;
	public double orbitalPhi;
	public double rotationalPhi;
	public boolean isRetrograde;
	public OreGenProperties oreProperties = null;
	public List<ItemStack> laserDrillOres;
	public List<ItemStack> craterOres;
	public List<ItemStack> geodeOres;
	public String craterOresRaw;
	public String geodeOresRaw;
	// The parsing of laserOreDrills is destructive of the actual oredict entries, so we keep a copy of the raw data around for XML writing
	public String laserDrillOresRaw;
	public String customIcon;
	IAtmosphere atmosphereType;

	StellarBody star;
	ResourceLocation starId;
	private String name;
	public float[] sunriseSunsetColors;
	//public ExtendedBiomeProperties biomeProperties;
	private LinkedList<Biome> allowedBiomes;
	private LinkedList<Biome> terraformedBiomes;
	private boolean isRegistered = false;
	private boolean isTerraformed = false;
	public boolean hasRings;
	public boolean hasRivers;
	public List<ItemStack> requiredArtifacts;

	//Planet Heirachy
	private HashSet<ResourceLocation> childPlanets;
	private ResourceLocation parentPlanet;
	private ResourceLocation planetId;
	private boolean isStation;
	private boolean isGasGiant;
	private boolean canGenerateCraters;
	private boolean canGenerateGeodes;
	private boolean canGenerateVolcanoes;
	private boolean canGenerateStructures;
	private boolean canGenerateCaves;
	private boolean canDecorate; //Should the button draw shadows, etc.  Clientside
	private boolean overrideDecoration;
	private float craterFrequencyMultiplier;
	private float volcanoFrequencyMultiplier;
	private float geodeFrequencyMultiplier;
	ISkyRenderer sky;


	//Satellites
	private HashMap<Long,SatelliteBase> satellites;
	private HashMap<Long,SatelliteBase> tickingSatellites;
	private List<Fluid> harvestableAtmosphere;
	private List<SpawnListEntryNBT> spawnableEntities;
	private HashSet<HashedBlockPosition> beaconLocations;
	private BlockState oceanBlock;
	private BlockState fillerBlock;
	private int seaLevel;
	private int generatorType;

	public DimensionProperties(ResourceLocation id) {
		name = "Temp";
		resetProperties();

		planetId = id;
		parentPlanet = Constants.INVALID_PLANET;
		childPlanets = new HashSet<>();
		orbitalPhi = 0;
		isRetrograde = false;
		ringColor = new float[] {.4f, .4f, .7f};
		oceanBlock = null;
		fillerBlock = null;
		laserDrillOres = new ArrayList<>();
		geodeOres = new ArrayList<>();
		craterOres = new ArrayList<>();
		allowedBiomes = new LinkedList<>();
		terraformedBiomes = new LinkedList<>();
		satellites = new HashMap<>();
		requiredArtifacts = new LinkedList<>();
		tickingSatellites = new HashMap<>();
		isNativeDimension = true;
		skyRenderOverride = false;
		hasOxygen = true;
		colorOverride = false;
		peakInsolationMultiplier = -1;
		peakInsolationMultiplierWithoutAtmosphere = -1;
		isGasGiant = false;
		hasRings = false;
		canGenerateCraters = false;
		canGenerateGeodes = false;
		canGenerateStructures = false;
		canGenerateVolcanoes = false;
		canGenerateCaves = false;
		hasRivers = false;
		craterFrequencyMultiplier = 1f;
		volcanoFrequencyMultiplier = 1f;
		geodeFrequencyMultiplier = 1f;
		canDecorate = true;

		customIcon = "";
		harvestableAtmosphere = new LinkedList<>();
		spawnableEntities = new LinkedList<>();
		beaconLocations = new HashSet<>();
		seaLevel = 63;
		generatorType = 0;
	}

	public DimensionProperties(ResourceLocation id ,String name) {
		this(id);
		this.name = name;
	}

	public DimensionProperties(ResourceLocation id, boolean shouldRegister) {
		this(id);
		isStation = !shouldRegister;
	}

	public void copySatellites(DimensionProperties props) {
		this.satellites = props.satellites;
	}

	public void copyTerraformedBiomes(DimensionProperties props) {
		this.terraformedBiomes = props.terraformedBiomes;
		this.isTerraformed = props.isTerraformed;
	}

	@Override
	public Object clone() {
		try {
			return super.clone();
		} catch(CloneNotSupportedException e) {
			return null;
		}
	}

	/**
	 * @param world
	 * @return null to use default world gen properties, otherwise a list of ores to generate
	 */
	public OreGenProperties getOreGenProperties(World world) {
		if(oreProperties != null)
			return oreProperties;
		return OreGenProperties.getOresForPressure(AtmosphereTypes.getAtmosphereTypeFromValue(originalAtmosphereDensity), Temps.getTempFromValue(getAverageTemp()));
	}

	/**
	 * Resets all properties to default
	 */
	public void resetProperties() {
		fogColor = new float[] {1f,1f,1f};
		skyColor = new float[] {1f,1f,1f};
		sunriseSunsetColors = new float[] {.7f,.2f,.2f,1};
		ringColor = new float[] {.4f, .4f, .7f};
		gravitationalMultiplier = 1;
		rotationalPeriod = 24000;
		orbitalDist = 100;
		originalAtmosphereDensity = atmosphereDensity = 100;
		childPlanets = new HashSet<>();
		requiredArtifacts = new LinkedList<>();
		parentPlanet = Constants.INVALID_PLANET;
		starId = Constants.INVALID_STAR;
		averageTemperature = 100;
		hasRings = false;
		harvestableAtmosphere = new LinkedList<>();
		spawnableEntities = new LinkedList<>();
		beaconLocations = new HashSet<>();
		seaLevel = 63;
		oceanBlock = null;
		fillerBlock = null;
		generatorType = 0;
		sky = null;
		laserDrillOres = new ArrayList<>();
	}

	public ISkyRenderer getSkyRenderer()
	{
		return sky;
	}

	public void setSkyRenderer(ISkyRenderer sky)
	{
		this.sky = sky;
	}

	public List<Fluid> getHarvestableGasses() {
		return harvestableAtmosphere;
	}

	public List<ItemStack> getRequiredArtifacts() {
		return requiredArtifacts;
	}

	@Override
	public float getGravitationalMultiplier() {
		return gravitationalMultiplier;
	}

	public float getGravitationalMultiplier(BlockPos blockPos) {
		if(isStation())
		{
			ISpaceObject station = AdvancedRocketryAPI.spaceObjectManager.getSpaceStationFromBlockCoords(blockPos);
			if(station != null)
				return station.getProperties().getGravitationalMultiplier();
		}

		return getGravitationalMultiplier();
	}

	@Override
	public void setGravitationalMultiplier(float mult) {
		gravitationalMultiplier = mult;
	}

	public List<SpawnListEntryNBT> getSpawnListEntries() {
		return spawnableEntities;
	}

	/**
	 * @return the color of the sun as an array of floats represented as  {r,g,b}
	 */
	public float[] getSunColor() {

		if(getStar() == null)
			return new float[] {1,1,1};

		return getStar().getColor();
	}

	/**
	 * Sets the host star for the planet
	 * @param star the star to set as the host for this planet
	 */
	public void setStar(StellarBody star) {
		if(star == null)
		{
			starId = Constants.INVALID_STAR;
			return;
		}

		this.starId = star.getId();
		this.star = star;
		if(!this.isMoon() && !isStation() && !this.isStar())
			this.star.addPlanet(this);
	}

	public void setStar(ResourceLocation id) {
		this.starId = id;
		if(DimensionManager.getInstance().getStar(id) != null)
			setStar(DimensionManager.getInstance().getStar(id));
	}

	/**
	 * @return the host star for this planet
	 */
	public StellarBody getStar() {
		if(isStar())
			return getStarData();
		if(star == null)
			star = DimensionManager.getInstance().getStar(starId);
		return star;
	}

	public boolean hasSurface() {
		return !(isGasGiant() || isStar());
	}

	public boolean isGasGiant() {
		return isGasGiant;
	}

	public boolean isStar() {
		return Constants.STAR_NAMESPACE.equals(planetId.getNamespace());
	}

	public StellarBody getStarData() {
		return DimensionManager.getInstance().getStar(planetId);
	}

	public void setGasGiant(boolean gas) {
		this.isGasGiant = gas;
	}

	public boolean hasRings() {
		return this.hasRings;
	}

	public void setHasRings(boolean value) {
		this.hasRings = value;
	}

	//Adds a beacon location to the planet's surface
	public void addBeaconLocation(World world, HashedBlockPosition pos) {
		beaconLocations.add(pos);
		DimensionManager.getInstance().knownPlanets.add(getId());

		//LAAZZY
		if(!world.isRemote)
			PacketHandler.sendToAll(new PacketDimInfo(getId(), this));
	}

	public HashSet<HashedBlockPosition> getBeacons() {
		return beaconLocations;
	}

	//Removes a beacon location to the planet's surface
	public void removeBeaconLocation(World world, HashedBlockPosition pos) {
		beaconLocations.remove(pos);

		if(beaconLocations.isEmpty() && !ARConfiguration.getCurrentConfig().initiallyKnownPlanets.contains(getId()))
			DimensionManager.getInstance().knownPlanets.remove(getId());

		//LAAZZY
		if(!world.isRemote)
			PacketHandler.sendToAll(new PacketDimInfo(getId(), this));
	}

	/**
	 * @return the {@link ResourceLocation} representing this planet, generated from the planet's properties
	 */
	public ResourceLocation getPlanetIcon() {


		if(!customIcon.isEmpty())
		{
			try {
				String resource_location = "advancedrocketry:textures/planets/" + customIcon.toLowerCase() + ".png";
				if(TextureResources.planetResources.containsKey(resource_location))
					return TextureResources.planetResources.get(resource_location);

				ResourceLocation new_resource = new ResourceLocation(resource_location);
				TextureResources.planetResources.put(resource_location, new_resource);
				return new_resource;
			} catch(IllegalArgumentException e) {
				return PlanetIcons.UNKNOWN.resource;
			}

		}

		AtmosphereTypes atmType = AtmosphereTypes.getAtmosphereTypeFromValue(atmosphereDensity);
		Temps tempType = Temps.getTempFromValue(getAverageTemp());

		if(isStar() && getStarData().isBlackHole())
			return TextureResources.locationBlackHole_icon;

		if(isStar())
			return TextureResources.locationSunPng;

		if(isGasGiant())
			return PlanetIcons.GASGIANTBLUE.resource;

		if(isAsteroid())
			return PlanetIcons.ASTEROID.resource;

		if(tempType == Temps.TOOHOT)
			return PlanetIcons.MARSLIKE.resource;
		if(atmType != AtmosphereTypes.NONE && VulpineMath.isBetween(tempType.ordinal(), Temps.COLD.ordinal(), Temps.TOOHOT.ordinal()))
			return PlanetIcons.EARTHLIKE.resource;//TODO: humidity
		else if(tempType.compareTo(Temps.COLD) > 0)
			if(atmType.compareTo(AtmosphereTypes.LOW) > 0)
				return PlanetIcons.MOON.resource;
			else
				return PlanetIcons.ICEWORLD.resource;
		else if(atmType.compareTo(AtmosphereTypes.LOW) > 0) {

			if(tempType.compareTo(Temps.COLD) < 0)
				return PlanetIcons.MARSLIKE.resource;
			else
				return PlanetIcons.MOON.resource;
		}
		else
			return PlanetIcons.LAVA.resource;
	}

	/**
	 * @return the {@link ResourceLocation} representing this planet, generated from the planet's properties
	 */
	public ResourceLocation getPlanetIconLEO() {

		if(!customIcon.isEmpty())
		{
			try {
				String resource_location = "advancedrocketry:textures/planets/" + customIcon.toLowerCase() + "leo.jpg";
				if(TextureResources.planetResources.containsKey(resource_location))
					return TextureResources.planetResources.get(resource_location);

				ResourceLocation new_resource = new ResourceLocation(resource_location);
				TextureResources.planetResources.put(resource_location, new_resource);
				return new_resource;

			} catch(IllegalArgumentException e) {
				return PlanetIcons.UNKNOWN.resource;
			}
		}

		AtmosphereTypes atmType = AtmosphereTypes.getAtmosphereTypeFromValue(atmosphereDensity);
		Temps tempType = Temps.getTempFromValue(getAverageTemp());


		if(isGasGiant())
			return PlanetIcons.GASGIANTBLUE.resourceLEO;

		if(tempType == Temps.TOOHOT)
			return PlanetIcons.MARSLIKE.resourceLEO;
		if(atmType != AtmosphereTypes.NONE && VulpineMath.isBetween(tempType.ordinal(), Temps.COLD.ordinal(), Temps.TOOHOT.ordinal()))
			return PlanetIcons.EARTHLIKE.resourceLEO;//TODO: humidity
		else if(tempType.compareTo(Temps.COLD) > 0)
			if(atmType.compareTo(AtmosphereTypes.LOW) > 0)
				return PlanetIcons.MOON.resourceLEO;
			else
				return PlanetIcons.ICEWORLD.resourceLEO;
		else if(atmType.compareTo(AtmosphereTypes.LOW) > 0) {

			if(tempType.compareTo(Temps.COLD) < 0)
				return PlanetIcons.MARSLIKE.resourceLEO;
			else
				return PlanetIcons.MOON.resourceLEO;
		}
		else
			return PlanetIcons.LAVA.resourceLEO;
	}

	/**
	 * @return the name of the planet
	 */
	public String getName() {
		return name;
	}

	/**
	 * Sets the name of the planet
	 */
	public void setName(String name) {
		this.name = name;
	}

	//Planet hierarchy

	/**
	 * @return the DIMID of the planet
	 */
	public ResourceLocation getId() {
		return planetId;
	}

	/**
	 * @return the DimID of the parent planet
	 */
	public ResourceLocation getParentPlanet() {
		return parentPlanet;
	}

	/**
	 * @return the {@link DimensionProperties} of the parent planet
	 */
	public DimensionProperties getParentProperties() {
		if(!Constants.INVALID_PLANET.equals(parentPlanet))
			return DimensionManager.getInstance().getDimensionProperties(parentPlanet);
		return null;
	}

	/**
	 * Range 0 < value <= 200
	 * @return if the planet is a moon, then the distance from the host planet where the earth's moon is 100, higher is farther, if planet, distance from the star, 100 is earthlike, higher value is father
	 */
	public int getParentOrbitalDistance() {
		return orbitalDist;
	}

	/**
	 * @return if a planet, the same as getParentOrbitalDistance(), if a moon, the moon's distance from the host star
	 */
	public int getSolarOrbitalDistance() {
		if(!Constants.INVALID_PLANET.equals(parentPlanet))
			return getParentProperties().getSolarOrbitalDistance();
		return orbitalDist;
	}

	public double getSolarTheta() {
		if(!Constants.INVALID_PLANET.equals(parentPlanet))
			return getParentProperties().getSolarTheta();
		return orbitTheta;
	}

	/**
	 * Sets this planet as a moon of the supplied planet's id.
	 * @param parent parent planet's DimensionProperties, or null for none
	 */
	public void setParentPlanet(DimensionProperties parent) { this.setParentPlanet(parent, true); }

	/**
	 * Sets this planet as a moon of the supplied planet's ID
	 * @param parent DimensionProperties of the parent planet, or null for none
	 * @param update true to update the parent's planet to the change
	 */
	public void setParentPlanet(DimensionProperties parent, boolean update) {

		if(update) {
			if(!Constants.INVALID_PLANET.equals(parentPlanet))
				getParentProperties().childPlanets.remove(getId());

			if(parent == null) {
				parentPlanet = Constants.INVALID_PLANET;
			}
			else {
				parentPlanet = parent.getId();
				star = parent.getStar();
				if(!Constants.INVALID_PLANET.equals(parent.getId()))
					parent.childPlanets.add(getId());
			}
		}
		else {
			if(parent == null) {
				parentPlanet = Constants.INVALID_PLANET;
			}
			else {
				star = parent.getStar();
				starId = star == null ? Constants.INVALID_STAR : star.getId();
				parentPlanet = parent.getId();
			}
		}
	}

	/**
	 * @return true if the planet has moons
	 */
	public boolean hasChildren() {
		return !childPlanets.isEmpty();
	}

	/**
	 * @return true if this DIM orbits another
	 */
	public boolean isMoon() {
		return !Constants.INVALID_PLANET.equals(parentPlanet) && !SpaceObjectManager.WARPDIMID.equals(parentPlanet);
	}

	/**
	 * @return true if terraformed
	 */
	public boolean isTerraformed() {
		return isTerraformed;
	}

	public int getAtmosphereDensity() {
		return atmosphereDensity;
	}

	public void setAtmosphereDensity(int atmosphereDensity) {

		int prevAtm = this.atmosphereDensity;
		this.atmosphereDensity = atmosphereDensity;

		if (AtmosphereTypes.getAtmosphereTypeFromValue(prevAtm) != AtmosphereTypes.getAtmosphereTypeFromValue(this.atmosphereDensity)) {
			//setTerraformedBiomes(getViableBiomes());
			//isTerraformed = true;

			//((ChunkManagerPlanet)((WorldProviderPlanet)net.minecraftforge.common.DimensionManager.getProvider(getId())).chunkMgrTerraformed).resetCache();

		}

		PacketHandler.sendToAll(new PacketDimInfo(getId(), this));
	}

	public void setAtmosphereDensityDirect(int atmosphereDensity) {
		originalAtmosphereDensity = this.atmosphereDensity = atmosphereDensity;
	}

	/**
	 * 
	 * @return true if the dimension properties refer to that of a space station or orbiting object registered in {@link SpaceObjectManager}
	 */
	public boolean isStation() {
		return isStation;
	}

	//TODO: allow for more exotic atmospheres
	/**
	 * @return the default atmosphere of this dimension
	 */
	public IAtmosphere getAtmosphere() {
		if(hasAtmosphere() && hasOxygen) {
			if(averageTemperature >= 900)
				return AtmosphereType.SUPERHEATED;
			if(Temps.getTempFromValue(getAverageTemp()) == Temps.TOOHOT)
				return AtmosphereType.VERYHOT;
			if(AtmosphereTypes.getAtmosphereTypeFromValue(getAtmosphereDensity()) == AtmosphereTypes.SUPERHIGHPRESSURE)
				return AtmosphereType.SUPERHIGHPRESSURE;
			if(AtmosphereTypes.getAtmosphereTypeFromValue(getAtmosphereDensity()) == AtmosphereTypes.HIGHPRESSURE)
				return AtmosphereType.HIGHPRESSURE;
			if(AtmosphereTypes.getAtmosphereTypeFromValue(getAtmosphereDensity()) == AtmosphereTypes.LOW)
				return AtmosphereType.LOWOXYGEN;
			return AtmosphereType.AIR;
		} else if(hasAtmosphere() && !hasOxygen){
			if(averageTemperature >= 900)
				return AtmosphereType.SUPERHEATEDNOO2;
			if(Temps.getTempFromValue(averageTemperature) == Temps.TOOHOT)
				return AtmosphereType.VERYHOTNOO2;
			if(AtmosphereTypes.getAtmosphereTypeFromValue(getAtmosphereDensity()) == AtmosphereTypes.SUPERHIGHPRESSURE)
				return AtmosphereType.SUPERHIGHPRESSURENOO2;
			if(AtmosphereTypes.getAtmosphereTypeFromValue(getAtmosphereDensity()) == AtmosphereTypes.HIGHPRESSURE)
				return AtmosphereType.HIGHPRESSURENOO2;
			return AtmosphereType.NOO2;
		}
		return AtmosphereType.VACUUM;
	}

	/**
	 * @return {@link ResourceLocation} refering to the image to render as atmospheric haze as seen from orbit
	 */
	public static ResourceLocation getAtmosphereResource() {
		return atmosphere;
	}

	public static ResourceLocation getShadowResource() {
		return shadow;
	}


	public static ResourceLocation getAtmosphereLEOResource() {
		return atmosphereLEO;
	}

	/**
	 * @return true if the planet has an atmosphere
	 */
	public boolean hasAtmosphere() {
		return AtmosphereTypes.getAtmosphereTypeFromValue(atmosphereDensity).compareTo(AtmosphereTypes.NONE) < 0;
	}
	
	/**
	 * @return the multiplier compared to Earth(1040W) for peak insolation of the body
	 */
	public double getPeakInsolationMultiplier() {
		//Set peak insolation multiplier --  we do this here because I've had problems with it in the past in the XML loader, and people keep asking to change it
		//Assumes that a 16 atmosphere is 16x the partial pressure but not thicker, because I don't want to deal with that and this is fairly simple right now
		//Get what it would be relative to LEO, this gives ~0.76 for Earth at the surface
		double insolationRelativeToLEO = AstronomicalBodyHelper.getStellarBrightness(star, getSolarOrbitalDistance()) * Math.pow(Math.E, -(0.0026899d * getAtmosphereDensity()));
		//Multiply by Earth LEO/Earth Surface for ratio relative to Earth surface (1360/1040)
		peakInsolationMultiplier = insolationRelativeToLEO * 1.308d;
		return peakInsolationMultiplier;
	}

	/**
	 * @return the multiplier compared to Earth(1040W) for peak insolation of the body, ignoring the atmosphere
	 */
	public double getPeakInsolationMultiplierWithoutAtmosphere() {
		//Set peak insolation multiplier without atmosphere --  we do this here because I've had problems with it in the past in the XML loader, and people keep asking to change it
		peakInsolationMultiplierWithoutAtmosphere = AstronomicalBodyHelper.getStellarBrightness(star, getSolarOrbitalDistance()) * 1.308d;
		return peakInsolationMultiplierWithoutAtmosphere;
	}


	public boolean isAsteroid() {
		return generatorType == Constants.GENTYPE_ASTEROID;
	}

	/**
	 * @return true if the planet should be rendered with shadows, atmosphere glow, clouds, etc
	 */
	public boolean hasDecorators() {
		return !isAsteroid() && !isStar() || (canDecorate && overrideDecoration);
	}

	public void setDecoratoration(boolean value)
	{
		canDecorate = value;
		overrideDecoration = true;
	}

	public boolean isDecorationOverridden()
	{
		return overrideDecoration;
	}

	public void unsetDecoratoration()
	{
		overrideDecoration = false;
	}

	/**
	 * @return set of all moons orbiting this planet
	 */
	public Set<ResourceLocation> getChildPlanets() {
		return childPlanets;
	}

	/**
	 * @return how many moons deep this planet is, IE: if the moon of a moon of a planet then three is returned
	 */
	public int getPathLengthToStar() {
		if(isMoon())
			return 1 + getParentProperties().getPathLengthToStar();
		return 1;
	}

	/**
	 * Does not check for hierarchy loops!
	 * @param child DimensionProperties of the new child
	 * @return true if successfully added as a child planet
	 */
	public boolean addChildPlanet(DimensionProperties child) {
		//TODO: check for hierarchy loops!
		if(child == this)
			return false;

		childPlanets.add(child.getId());
		child.setParentPlanet(this);
		return true;
	}

	/**
	 * Removes the passed DIMID from the list of moons
	 * @param id DIMID of the child planet to remove
	 */
	public void removeChild(ResourceLocation id) {
		childPlanets.remove(id);
	}

	//Satellites --------------------------------------------------------
	/**
	 * Adds a satellite to this DIM
	 * @param satellite satellite to add
	 * @param world world to add the satellite to
	 */
	public void addSatellite(SatelliteBase satellite, World world) {
		//Prevent dupes
		if(satellites.containsKey(satellite.getId())) {
			satellites.remove(satellite.getId());
			tickingSatellites.remove(satellite.getId());
		}

		satellites.put(satellite.getId(), satellite);
		satellite.setDimensionId(world);


		if(satellite.canTick())
			tickingSatellites.put(satellite.getId(),satellite);

		if(!world.isRemote)
			PacketHandler.sendToAll(new PacketSatellite(satellite));
	}

	/**
	 * Adds a satellite to this DIM
	 * @param satellite satellite to add
	 * @param world world to add the satellite to
	 */
	public void addSatellite(SatelliteBase satellite, ResourceLocation world, boolean isRemote) {
		//Prevent dupes
		if(satellites.containsKey(satellite.getId())) {
			satellites.remove(satellite.getId());
			tickingSatellites.remove(satellite.getId());
		}

		satellites.put(satellite.getId(), satellite);
		satellite.setDimensionId(world);


		if(satellite.canTick())
			tickingSatellites.put(satellite.getId(),satellite);

		if(!isRemote)
			PacketHandler.sendToAll(new PacketSatellite(satellite));
	}

	/**
	 * Really only meant to be used on the client when receiving a packet
	 * @param satellite the satellite to add to orbit
	 */
	public void addSatellite(SatelliteBase satellite) {
		if(satellites.containsKey(satellite.getId())) {
			satellites.remove(satellite.getId());
			tickingSatellites.remove(satellite.getId());
		}
		satellites.put(satellite.getId(), satellite);

		if(satellite.canTick()) //TODO: check for dupes
			tickingSatellites.put(satellite.getId(), satellite);
	}

	/**
	 * Removes the satellite from orbit around this world
	 * @param satelliteId ID # for this satellite
	 * @return reference to the satellite object
	 */
	public SatelliteBase removeSatellite(long satelliteId) {
		SatelliteBase satellite = satellites.remove(satelliteId);
		if(satellite != null && satellite.canTick() && tickingSatellites.containsKey(satelliteId))
			tickingSatellites.get(satelliteId).remove();

		return satellite;
	}

	/**
	 * @param id ID # for this satellite
	 * @return a reference to the satelliteBase object given this ID
	 */
	public SatelliteBase getSatellite(long id) {
		return satellites.get(id);
	}

	/**
	 * Returns all of a dimension's satellites
	 * @return a Collection containing all of a dimension's satellites
	 */
	public Collection<SatelliteBase> getAllSatellites() {
		return this.satellites.values();
	}

	//TODO: multithreading
	/**
	 * Tick satellites as needed
	 */
	public void tick() {

		Iterator<SatelliteBase> iterator = tickingSatellites.values().iterator();

		while(iterator.hasNext()) {
			SatelliteBase satellite = iterator.next();
			satellite.tickEntity();

			if(satellite.isDead()) {
				iterator.remove();
				satellites.remove(satellite.getId());
			}
		}
		updateOrbit();
	}

	public void updateOrbit() {
		try
		{
		this.prevOrbitalTheta = this.orbitTheta;
		if (this.isMoon()) {
			this.orbitTheta = (AstronomicalBodyHelper.getMoonOrbitalTheta(orbitalDist, getParentProperties().gravitationalMultiplier) + baseOrbitTheta) * (isRetrograde ? -1 : 1);
		} else if (!this.isMoon()) {
			this.orbitTheta = (AstronomicalBodyHelper.getOrbitalTheta(orbitalDist, getStar().getSize()) + baseOrbitTheta) * (isRetrograde ? -1 : 1);
		}
		}
		catch(NullPointerException e)
		{
			
		}
	}

	/**
	 * @return true if this dimension is allowed to have rivers
	 */
	public boolean hasRivers() {
		return hasRivers || (AtmosphereTypes.getAtmosphereTypeFromValue(originalAtmosphereDensity).compareTo(AtmosphereTypes.LOW) <= 0 && Temps.getTempFromValue(getAverageTemp()).isInRange(Temps.COLD, Temps.HOT));
	}


	/**
	 * Each Planet is assigned a list of biomes that are allowed to spawn there
	 * @return List of biomes allowed to spawn on this planet
	 */
	public List<Biome> getBiomes() {
		return allowedBiomes;
	}

	public List<Biome> getTerraformedBiomes() {
		return terraformedBiomes;
	}

	/**
	 * Used to determine if a biome is allowed to spawn on ANY planet
	 * @param biome biome to check
	 * @return true if the biome is not allowed to spawn on any Dimension
	 */
	public boolean isBiomeblackListed(Biome biome) {
		return AdvancedRocketryBiomes.instance.getBlackListedBiomes().contains(AdvancedRocketryBiomes.getBiomeResource(biome));
	}

	/**
	 * @return a list of biomes allowed to spawn in this dimension
	 */
	public List<Biome> getViableBiomes() {
		Random random = new Random(System.nanoTime());
		List<Biome> viableBiomes = new ArrayList<>();

		if(atmosphereDensity > AtmosphereTypes.LOW.value && random.nextInt(3) == 0) {
			List<Biome> list = new LinkedList<>(AdvancedRocketryBiomes.instance.getSingleBiome());

			while(list.size() > 1) {
				Biome biome = list.get(random.nextInt(list.size()));
				Temps temp = Temps.getTempFromValue(averageTemperature);
				Temps biomeTemp = AdvancedRocketryBiomes.getBiomeTemp(biome);
				if((biomeTemp == Temps.COLD && temp.isInRange(Temps.FRIGID, Temps.NORMAL)) ||
						((biomeTemp == Temps.NORMAL || biome.getCategory() == Category.OCEAN) &&
								temp.isInRange(Temps.COLD, Temps.HOT)) ||
						(biomeTemp == Temps.HOT && temp.isInRange(Temps.NORMAL, Temps.HOT))) {
					viableBiomes.add(biome);
					return viableBiomes;
				}
				list.remove(biome);
			}
		}




		if(atmosphereDensity <= AtmosphereTypes.LOW.value)
		{
			viableBiomes.add(AdvancedRocketryBiomes.moonBiome);
			viableBiomes.add(AdvancedRocketryBiomes.moonBiomeDark);
		}

		else if(Temps.getTempFromValue(averageTemperature).hotterThan(Temps.TOOHOT)) {
			viableBiomes.add(AdvancedRocketryBiomes.hotDryBiome);
			viableBiomes.add(AdvancedRocketryBiomes.volcanic);
			viableBiomes.add(AdvancedRocketryBiomes.volcanicBarren);
		}
		else if(Temps.getTempFromValue(averageTemperature).hotterThan(Temps.HOT)) {
			Iterator<Biome> itr = AdvancedRocketryBiomes.getAllBiomes();
			while( itr.hasNext()) {
				Biome biome = itr.next();
				Temps biomeTemp = AdvancedRocketryBiomes.getBiomeTemp(biome);
				if((biomeTemp == Temps.HOT || biome.getCategory() == Category.OCEAN) && !isBiomeblackListed(biome)) {
					viableBiomes.add(biome);
				}
			}
		}
		else if(Temps.getTempFromValue(averageTemperature).hotterThan(Temps.NORMAL)) {
			Iterator<Biome> itr = AdvancedRocketryBiomes.getAllBiomes();
			while( itr.hasNext()) {
				Biome biome = itr.next();
				Temps biomeTemp = AdvancedRocketryBiomes.getBiomeTemp(biome);
				if(biomeTemp.isInRange(Temps.COLD, Temps.HOT) && !isBiomeblackListed(biome) || biome.getCategory() == Category.OCEAN) {
					viableBiomes.add(biome);
				}
			}
		}
		else if(Temps.getTempFromValue(averageTemperature).hotterThan(Temps.COLD)) {
			Iterator<Biome> itr = AdvancedRocketryBiomes.getAllBiomes();
			while( itr.hasNext()) {
				Biome biome = itr.next();
				Temps biomeTemp = AdvancedRocketryBiomes.getBiomeTemp(biome);
				if(biomeTemp.isInRange(Temps.FRIGID, Temps.NORMAL) && !isBiomeblackListed(biome) || biome.getCategory() == Category.OCEAN) {
					viableBiomes.add(biome);
				}
			}
		}
		else if(Temps.getTempFromValue(averageTemperature).hotterThan(Temps.FRIGID)) {
			Iterator<Biome> itr = AdvancedRocketryBiomes.getAllBiomes();
			while( itr.hasNext()) {
				Biome biome = itr.next();
				Temps biomeTemp = AdvancedRocketryBiomes.getBiomeTemp(biome);
				if(biomeTemp.isInRange(Temps.SNOWBALL, Temps.COLD) && !isBiomeblackListed(biome)) {
					viableBiomes.add(biome);
				}
			}
		}
		else {//(averageTemperature >= Temps.SNOWBALL.getTemp())
			Iterator<Biome> itr = AdvancedRocketryBiomes.getAllBiomes();
			while( itr.hasNext()) {
				Biome biome = itr.next();
				Temps biomeTemp = AdvancedRocketryBiomes.getBiomeTemp(biome);
				if(biomeTemp.isInRange(Temps.SNOWBALL, Temps.FRIGID) && !isBiomeblackListed(biome)) {
					viableBiomes.add(biome);
				}
			}
		}

		int maxBiomesPerPlanet = ARConfiguration.getCurrentConfig().maxBiomesPerPlanet.get();
		if(viableBiomes.size() > maxBiomesPerPlanet) {
			viableBiomes  = ZUtils.copyRandomElements(viableBiomes, maxBiomesPerPlanet);
		}

		if(atmosphereDensity > AtmosphereTypes.HIGHPRESSURE.value && Temps.getTempFromValue(averageTemperature).isInRange(Temps.NORMAL, Temps.HOT))
			viableBiomes.addAll(AdvancedRocketryBiomes.instance.getHighPressureBiomes());

		return viableBiomes;
	}
	
	/**
	 * Adds a biome to the list of biomes allowed to spawn on this planet
	 * @param biome biome to be added as viable
	 */
	public boolean addBiome(Biome biome) {
		ArrayList<Biome> biomes = new ArrayList<>();
		biomes.add(biome);
		allowedBiomes.addAll(getBiomesEntries(biomes));
		return true;
	}

	/**
	 * Adds a biome to the list of biomes allowed to spawn on this planet
	 * @param biomeId biome to be added as viable
	 * @return true if the biome was added successfully, false otherwise
	 */
	public boolean addBiome(ResourceLocation biomeId) {
		if(AdvancedRocketryBiomes.doesBiomeExist(biomeId)) {
			Biome biome =  AdvancedRocketryBiomes.getBiomeFromResourceLocation(biomeId);
			List<Biome> biomes = new ArrayList<>();
			biomes.add(biome);
			allowedBiomes.addAll(getBiomesEntries(biomes));
			return true;
		}
		return false;
	}

	/**
	 * Adds a list of biomes to the allowed list of biomes for this planet
	 * @param biomes 
	 */
	public void addBiomes(List<Biome> biomes) {
		//TODO check for duplicates
		allowedBiomes.addAll(getBiomesEntries(biomes));
	}

	/**
	 * Clears the list of allowed biomes and replaces it with the provided list
	 * @param biomes
	 */
	public void setBiomes(List<Biome> biomes) {
		allowedBiomes.clear();
		addBiomes(biomes);
	}

	public void setBiomeEntries(List<Biome> biomes) {
		//If list is itself DO NOT CLEAR IT
		if(biomes != allowedBiomes) {
			allowedBiomes.clear();
			allowedBiomes.addAll(biomes);
		}
	}

	public void setTerraformedBiomes(List<Biome> biomes) {
		terraformedBiomes.clear();
		terraformedBiomes.addAll(getBiomesEntries(biomes));
	}

	/**
	 * Adds all biomes of this type to the list of biomes allowed to generate
	 * @param type
	 */
	/*public void addBiomeType(BiomeDictionary.Type type) {

		ArrayList<Biome> entryList = new ArrayList<>(BiomeDictionary.getBiomes(type));

		//Neither are acceptable on planets
		entryList.remove(Biomes.NETHER_WASTES.getRegistryName());
		entryList.remove(Biomes.THE_END.getRegistryName());

		//Make sure we don't add double entries
		Iterator<Biome> iter = entryList.iterator();
		while(iter.hasNext()) {
			Biome nextBiome = iter.next();
			for(Biome entry : allowedBiomes) {
				if(BiomeDictionary.areSimilar(entry.biome, nextBiome))
					iter.remove();
			}

		}
		allowedBiomes.addAll(getBiomesEntries(entryList));

	}*/

	/**
	 * Removes all biomes of this type from the list of biomes allowed to generate
	 * @param type
	 */
	/*public void removeBiomeType(BiomeDictionary.Type type) {

		ArrayList<Biome> entryList = new ArrayList<>(BiomeDictionary.getBiomes(type));

		Iterator<Biome> itr = AdvancedRocketryBiomes.getAllBiomes();
		while(itr.hasNext()) {
			Biome biome = itr.next();
			allowedBiomes.removeIf(biome1 -> BiomeDictionary.areSimilar(biome1.biome, biome));
		}
	}*/

	/**
	 * Gets a list of BiomeEntries allowed to spawn in this dimension
	 * @param biomeIds
	 * @return the list of BiomeEntries
	 */
	private List<Biome> getBiomesEntries(List<Biome> biomeIds) {
		return biomeIds;

		/*ArrayList<Biome> biomeEntries = new ArrayList<>();

		for (Biome biomes : biomeIds) {
			/*if(biomes == Biome.desert) {
				biomeEntries.add(new Biome(BiomeGenBase.desert, 30));
				continue;
			}
			else if(biomes == BiomeGenBase.savanna) {
				biomeEntries.add(new Biome(BiomeGenBase.savanna, 20));
				continue;
			}
			else if(biomes == BiomeGenBase.plains) {
				biomeEntries.add(new Biome(BiomeGenBase.plains, 10));
				continue;
			}* /

			boolean notFound = true;

			label:

			for(BiomeManager.BiomeType types : BiomeManager.BiomeType.values()) {
				for(Biome entry : BiomeManager.getBiomes(types)) {
					if(biomes == null)
						AdvancedRocketry.logger.warn("Null biomes loaded for DIMID: " + this.getId());
					else if(entry.biome.equals(biomes)) {
						biomeEntries.add(entry);
						notFound = false;

						break label;
					}
				}
			}

			if(notFound && biomes != null) {
				biomeEntries.add(new Biome(biomes, 30));
			}
		}

		return biomeEntries;*/
	}

	public void initDefaultAttributes()
	{
		if(Temps.getTempFromValue(averageTemperature).hotterThan(DimensionProperties.Temps.HOT))
			setOceanBlock(Blocks.LAVA.getDefaultState());

		//Add planet Properties
		setGenerateCraters(AtmosphereTypes.getAtmosphereTypeFromValue(getAtmosphereDensity()).lessDenseThan(AtmosphereTypes.NORMAL));
		setGenerateVolcanos(Temps.getTempFromValue(averageTemperature).hotterThan(DimensionProperties.Temps.HOT));
		setGenerateStructures(isHabitable());
		setGenerateGeodes(getAtmosphereDensity() > 125);
	}


	private void readFromTechnicalNBT(CompoundNBT nbt)
	{
		isTerraformed = nbt.getBoolean("terraformed");
		ListNBT list;
		if(nbt.contains("beaconLocations")) {
			list = nbt.getList("beaconLocations", NBT.TAG_INT_ARRAY);

			for(int i = 0 ; i < list.size(); i++) {
				int[] location = list.getIntArray(i);
				beaconLocations.add(new HashedBlockPosition(location[0], location[1], location[2]));
			}
			DimensionManager.getInstance().knownPlanets.add(getId());
		}
		else
			beaconLocations.clear();


		//Load biomes
		if(nbt.contains("biomesTerra")) {

			terraformedBiomes.clear();

			ListNBT terraformedList = nbt.getList("biomesTerra", NBT.TAG_STRING);
			List<Biome> biomesList = new ArrayList<>();

			for(int i = 0; i < terraformedList.size(); i++) {
				biomesList.add(AdvancedRocketryBiomes.getBiomeFromResourceLocation(new ResourceLocation(terraformedList.getString(i))));
			}

			terraformedBiomes.addAll(getBiomesEntries(biomesList));
		}

		//Satellites
		if(nbt.contains("satellites")) {
			CompoundNBT allSatelliteNbt = nbt.getCompound("satellites");

			for(String keyObject : allSatelliteNbt.keySet()) {
				String key = keyObject;
				Long longKey = Long.parseLong(key);

				CompoundNBT satelliteNBT = allSatelliteNbt.getCompound(key);

				if(satellites.containsKey(longKey)){
					satellites.get(longKey).readFromNBT(satelliteNBT);
				} 
				else {
					//Check for NBT errors
					try {
						SatelliteBase satellite = SatelliteRegistry.createFromNBT(satelliteNBT);

						satellites.put(longKey, satellite);

						if(satellite.canTick()) {
							tickingSatellites.put(satellite.getId(), satellite);
						}

					} catch (NullPointerException e) {
						AdvancedRocketry.logger.warn("Satellite with bad NBT detected, Removing");
					}
				}
			}
		}
	}

	public void readFromNBT(CompoundNBT nbt) {
		ListNBT list;

		if(nbt.contains("skyColor")) {
			list = nbt.getList("skyColor", NBT.TAG_FLOAT);
			skyColor = new float[list.size()];
			for(int f = 0 ; f < list.size(); f++) {
				skyColor[f] = list.getFloat(f);
			}
		}

		if(nbt.contains("ringColor")) {
			list = nbt.getList("ringColor", NBT.TAG_FLOAT);
			ringColor = new float[list.size()];
			for(int f = 0 ; f < list.size(); f++) {
				ringColor[f] = list.getFloat(f);
			}
		}

		if(nbt.contains("sunriseSunsetColors")) {
			list = nbt.getList("sunriseSunsetColors", NBT.TAG_FLOAT);
			sunriseSunsetColors = new float[list.size()];
			for(int f = 0 ; f < list.size(); f++) {
				sunriseSunsetColors[f] = list.getFloat(f);
			}
		}

		if(nbt.contains("fogColor")) {
			list = nbt.getList("fogColor", NBT.TAG_FLOAT);
			fogColor = new float[list.size()];
			for(int f = 0 ; f < list.size(); f++) {
				fogColor[f] = list.getFloat(f);
			}
		}

		//Load biomes
		if(nbt.contains("biomes")) {

			allowedBiomes.clear();
			ListNBT biomeIds = nbt.getList("biomes", NBT.TAG_STRING);
			List<Biome> biomesList = new ArrayList<>();


			for(int i = 0; i < biomeIds.size(); i++)
			{
				biomesList.add(AdvancedRocketryBiomes.getBiomeFromResourceLocation(new ResourceLocation(biomeIds.getString(i))));
			}

			allowedBiomes.addAll(biomesList);
		}

		if(nbt.contains("laserDrillOres")) {
			laserDrillOres.clear();
			list = nbt.getList("laserDrillOres", NBT.TAG_COMPOUND);
			for(INBT entry : list) {
				assert entry instanceof CompoundNBT;
				laserDrillOres.add(ItemStack.read((CompoundNBT) entry));
			}
		}

		if(nbt.contains("laserDrillOresRaw")) {
			laserDrillOresRaw = nbt.getString("laserDrillOresRaw");
		}

		if(nbt.contains("geodeOres")) {
			geodeOres.clear();
			list = nbt.getList("geodeOres", NBT.TAG_COMPOUND);
			for(INBT entry : list) {
				assert entry instanceof CompoundNBT;
				geodeOres.add(ItemStack.read((CompoundNBT) entry));
			}
		}

		if(nbt.contains("geodeOresRaw")) {
			geodeOresRaw = nbt.getString("geodeOresRaw");
		}

		if(nbt.contains("craterOres")) {
			craterOres.clear();
			list = nbt.getList("craterOres", NBT.TAG_COMPOUND);
			for(INBT entry : list) {
				assert entry instanceof CompoundNBT;
				craterOres.add(ItemStack.read((CompoundNBT) entry));
			}
		}

		if(nbt.contains("craterOresRaw")) {
			craterOresRaw = nbt.getString("craterOresRaw");
		}

		if(nbt.contains("artifacts")) {
			requiredArtifacts.clear();
			list = nbt.getList("artifacts", NBT.TAG_COMPOUND);
			for(INBT entry : list) {
				assert entry instanceof CompoundNBT;
				requiredArtifacts.add(new ItemStack((IItemProvider) entry));
			}
		}

		gravitationalMultiplier = nbt.getFloat("gravitationalMultiplier");
		orbitalDist = nbt.getInt("orbitalDist");
		orbitTheta = nbt.getDouble("orbitTheta");
		baseOrbitTheta = nbt.getDouble("baseOrbitTheta");
		orbitalPhi = nbt.getDouble("orbitPhi");
		rotationalPhi = nbt.getDouble("rotationalPhi");
		isRetrograde = nbt.getBoolean("isRetrograde");
		hasOxygen = nbt.getBoolean("hasOxygen");
		colorOverride = nbt.getBoolean("colorOverride");
		atmosphereDensity = nbt.getInt("atmosphereDensity");

		if(nbt.contains("originalAtmosphereDensity"))
			originalAtmosphereDensity = nbt.getInt("originalAtmosphereDensity");
		else 
			originalAtmosphereDensity = atmosphereDensity;

		peakInsolationMultiplier = nbt.getDouble("peakInsolationMultiplier");
		peakInsolationMultiplierWithoutAtmosphere = nbt.getDouble("peakInsolationMultiplierWithoutAtmosphere");
		averageTemperature = nbt.getInt("avgTemperature");
		rotationalPeriod = nbt.getInt("rotationalPeriod");
		name = nbt.getString("name");
		customIcon = nbt.getString("icon");
		isNativeDimension = !nbt.contains("isNative") || nbt.getBoolean("isNative"); //Prevent world breakages when loading from old version
		isGasGiant = nbt.getBoolean("isGasGiant");
		hasRings = nbt.getBoolean("hasRings");
		seaLevel = nbt.getInt("sealevel");
		generatorType = nbt.getInt("genType");
		canGenerateCraters = nbt.getBoolean("canGenerateCraters");
		canGenerateGeodes = nbt.getBoolean("canGenerateGeodes");
		canGenerateStructures = nbt.getBoolean("canGenerateStructures");
		canGenerateVolcanoes = nbt.getBoolean("canGenerateVolcanos");
		canGenerateCaves = nbt.getBoolean("canGenerateCaves");
		hasRivers = nbt.getBoolean("hasRivers");
		geodeFrequencyMultiplier = nbt.getFloat("geodeFrequencyMultiplier");
		craterFrequencyMultiplier = nbt.getFloat("craterFrequencyMultiplier");
		volcanoFrequencyMultiplier = nbt.getFloat("volcanoFrequencyMultiplier");


		//Hierarchy
		if(nbt.contains("childrenPlanets")) {
			ListNBT childList = nbt.getList("childrenPlanets", NBT.TAG_STRING);
			for(int i = 0; i < childList.size(); i++)
				childPlanets.add( new ResourceLocation(childList.getString(i)));
		}

		//Note: parent planet must be set before setting the star otherwise it would cause duplicate planets in the StellarBody's array
		if(nbt.contains("parentPlanet"))
			parentPlanet = new ResourceLocation(nbt.getString("parentPlanet"));
		this.starId = new ResourceLocation(nbt.getString("starId"));
		this.setStar(DimensionManager.getInstance().getStar(new ResourceLocation(nbt.getString("starId"))));

		if(isGasGiant) {
			ListNBT fluidList = nbt.getList("fluids", NBT.TAG_STRING);
			getHarvestableGasses().clear();

			for(int i = 0; i < fluidList.size(); i++) {
				Fluid fluid = ForgeRegistries.FLUIDS.getValue(new ResourceLocation(fluidList.getString(i)));
				if(fluid != null)
					getHarvestableGasses().add(fluid);
			}

			//Do not allow empty atmospheres, at least not yet
			if(getHarvestableGasses().isEmpty())
				getHarvestableGasses().addAll(AtmosphereRegister.getInstance().getHarvestableGasses());
		}

		if(nbt.contains("oceanBlock")) {

			if(!ForgeRegistries.BLOCKS.containsKey(new ResourceLocation(nbt.getString("oceanBlock")))) {
				oceanBlock = null;
			}
			else {
				Block block = ForgeRegistries.BLOCKS.getValue(new ResourceLocation(nbt.getString("oceanBlock")));
				int meta = nbt.getInt("oceanBlockMeta");
				oceanBlock = block.getDefaultState(); //Block.getStateById(meta);
			}
		}
		else
			oceanBlock = null;

		if(nbt.contains("fillBlock")) {

			if(!ForgeRegistries.BLOCKS.containsKey(new ResourceLocation(nbt.getString("fillBlock")))) {
				fillerBlock = null;
			}
			else {
				Block block = ForgeRegistries.BLOCKS.getValue(new ResourceLocation(nbt.getString("fillBlock")));
				int meta = nbt.getInt("fillBlockMeta");
				fillerBlock = block.getDefaultState(); //Block.getStateById(meta);
			}
		}
		else
			fillerBlock = null;

		readFromTechnicalNBT(nbt);
	}

	private void writeTechnicalNBT(CompoundNBT nbt)
	{
		nbt.putBoolean("terraformed", isTerraformed);
		ListNBT list;
		if(!beaconLocations.isEmpty()) {
			list = new ListNBT();

			for(HashedBlockPosition pos : beaconLocations) {
				list.add(new IntArrayNBT(new int[] {pos.x, pos.y, pos.z}));
			}
			nbt.put("beaconLocations", list);
		}


		if(!terraformedBiomes.isEmpty()) {
			ListNBT terraformedBiomeList = new ListNBT();
			for (Biome terraformedBiome : terraformedBiomes) {

				terraformedBiomeList.add(StringNBT.valueOf(AdvancedRocketryBiomes.getBiomeResource(terraformedBiome).toString()));
			}
			nbt.put("biomesTerra", terraformedBiomeList);
		}

		//Satellites
		if(!satellites.isEmpty()) {
			CompoundNBT allSatalliteNbt = new CompoundNBT();
			for(Entry<Long, SatelliteBase> entry : satellites.entrySet()) {
				CompoundNBT satalliteNbt = new CompoundNBT();

				entry.getValue().writeToNBT(satalliteNbt);
				allSatalliteNbt.put(entry.getKey().toString(), satalliteNbt);
			}
			nbt.put("satellites", allSatalliteNbt);
		}
	}

	public void writeToNBT(CompoundNBT nbt) {
		ListNBT list;

		if(skyColor != null) {
			list = new ListNBT();
			for(float f : skyColor) {
				list.add(FloatNBT.valueOf(f));
			}
			nbt.put("skyColor", list);
		}

		if(sunriseSunsetColors != null) {
			list = new ListNBT();
			for(float f : sunriseSunsetColors) {
				list.add(FloatNBT.valueOf(f));
			}
			nbt.put("sunriseSunsetColors", list);
		}

		list = new ListNBT();
		for(float f : fogColor) {
			list.add(FloatNBT.valueOf(f));
		}
		nbt.put("fogColor", list);

		if(hasRings) {
			list = new ListNBT();
			for(float f : ringColor) {
				list.add(FloatNBT.valueOf(f));
			}
			nbt.put("ringColor", list);
		}

		if(!allowedBiomes.isEmpty()) {

			ListNBT biomeList = new ListNBT();
			for (Biome allowedBiome : allowedBiomes) {
				biomeList.add(StringNBT.valueOf(AdvancedRocketryBiomes.getBiomeResource(allowedBiome).toString()));
			}
			nbt.put("biomes", biomeList);
		}


		if(!laserDrillOres.isEmpty()) {
			list = new ListNBT();
			for(ItemStack ore : laserDrillOres) {
				CompoundNBT entry = new CompoundNBT();
				ore.write(entry);
				list.add(entry);
			}
			nbt.put("laserDrillOres",list);
		}

		if(laserDrillOresRaw != null) {
			nbt.put("laserDrillOresRaw", StringNBT.valueOf(laserDrillOresRaw));
		}

		if(!geodeOres.isEmpty()) {
			list = new ListNBT();
			for(ItemStack ore : geodeOres) {
				CompoundNBT entry = new CompoundNBT();
				ore.write(entry);
				list.add(entry);
			}
			nbt.put("geodeOres",list);
		}

		if(geodeOresRaw != null) {
			nbt.put("geodeOresRaw", StringNBT.valueOf(geodeOresRaw));
		}

		if(!craterOres.isEmpty()) {
			list = new ListNBT();
			for(ItemStack ore : craterOres) {
				CompoundNBT entry = new CompoundNBT();
				ore.write(entry);
				list.add(entry);
			}
			nbt.put("craterOres",list);
		}

		if(craterOresRaw != null) {
			nbt.put("craterOresRaw", StringNBT.valueOf(craterOresRaw));
		}

		if(!requiredArtifacts.isEmpty()) {
			list = new ListNBT();
			for(ItemStack ore : requiredArtifacts) {
				CompoundNBT entry = new CompoundNBT();
				ore.write(entry);
				list.add(entry);
			}
			nbt.put("artifacts", list);
		}
		
		nbt.putString("starId", starId.toString());
		nbt.putFloat("gravitationalMultiplier", gravitationalMultiplier);
		nbt.putInt("orbitalDist", orbitalDist);
		nbt.putDouble("orbitTheta", orbitTheta);
		nbt.putDouble("baseOrbitTheta", baseOrbitTheta);
		nbt.putDouble("orbitPhi", orbitalPhi);
		nbt.putDouble("rotationalPhi", rotationalPhi);
		nbt.putBoolean("isRetrograde", isRetrograde);
		nbt.putBoolean("hasOxygen", hasOxygen);
		nbt.putBoolean("colorOverride", colorOverride);
		nbt.putInt("atmosphereDensity", atmosphereDensity);
		nbt.putInt("originalAtmosphereDensity", originalAtmosphereDensity);
		nbt.putDouble("peakInsolationMultiplier", peakInsolationMultiplier);
		nbt.putDouble("peakInsolationMultiplierWithoutAtmosphere", peakInsolationMultiplierWithoutAtmosphere);
		nbt.putInt("avgTemperature", averageTemperature);
		nbt.putInt("rotationalPeriod", rotationalPeriod);
		nbt.putString("name", name);
		nbt.putString("icon", customIcon);
		nbt.putBoolean("isNative", isNativeDimension);
		nbt.putBoolean("isGasGiant", isGasGiant);
		nbt.putBoolean("hasRings", hasRings);
		nbt.putInt("sealevel", seaLevel);
		nbt.putInt("genType", generatorType);
		nbt.putBoolean("canGenerateCraters", canGenerateCraters);
		nbt.putBoolean("canGenerateGeodes", canGenerateGeodes);
		nbt.putBoolean("canGenerateStructures", canGenerateStructures);
		nbt.putBoolean("canGenerateVolcanos", canGenerateVolcanoes);
		nbt.putBoolean("canGenerateCaves", canGenerateCaves);
		nbt.putBoolean("hasRivers", hasRivers);
		nbt.putFloat("geodeFrequencyMultiplier", geodeFrequencyMultiplier);
		nbt.putFloat("craterFrequencyMultiplier", craterFrequencyMultiplier);
		nbt.putFloat("volcanoFrequencyMultiplier", volcanoFrequencyMultiplier);

		//Hierarchy
		if(!childPlanets.isEmpty()) {
			ListNBT childList = new ListNBT();
			for(ResourceLocation childPlanet : childPlanets)
				childList.add(StringNBT.valueOf(childPlanet.toString()));

			nbt.put("childrenPlanets", childList);
		}

		if(parentPlanet != null)
			nbt.putString("parentPlanet", parentPlanet.toString());

		if(isGasGiant) {
			ListNBT fluidList = new ListNBT();

			for(Fluid f : getHarvestableGasses()) {
				fluidList.add(StringNBT.valueOf(f.getRegistryName().toString()));
			}

			nbt.put("fluids", fluidList);
		}

		if(oceanBlock != null) {
			nbt.putString("oceanBlock", oceanBlock.getBlock().getRegistryName().toString());
			nbt.putInt("oceanBlockMeta", Block.getStateId(oceanBlock));
		}

		if(fillerBlock != null) {
			nbt.putString("fillBlock", fillerBlock.getBlock().getRegistryName().toString());
			nbt.putInt("fillBlockMeta", Block.getStateId(fillerBlock));
		}


		writeTechnicalNBT(nbt);
	}
	public String generateDimJSON()
	{
		long seed = 0;
		List<String> structures = new LinkedList<>();
		List<String> biomeConditionalStructures = new LinkedList<>();
		List<String> biomeStrings = new LinkedList<>();
		Random random = new Random("Yes, I know that temp, altidude and humidity are not the same as the noise generator, but im coming in anywayyy".hashCode());

		
		if(!isAsteroid()) {
			if(canGenerateCraters()) {
				structures.add("\"advancedrocketry:crater\"");

				biomeConditionalStructures.add(
						"	          \"advancedrocketry:crater\": {\n" + 
								"	            \"spacing\": " + 40 * getCraterMultiplier() + ",\n" +
								"	            \"separation\": " + 20 * getCraterMultiplier() + ",\n" +
								"	            \"salt\": 0\n" + 
						"	          }");

			}
			if(canGenerateVolcanoes) {
				structures.add("\"advancedrocketry:volcano\"");
				biomeConditionalStructures.add(
						"	          \"advancedrocketry:volcano\": {\n" + 
								"	            \"spacing\": " + 40 * getVolcanoMultiplier() + ",\n" +
								"	            \"separation\": " + 20 * getVolcanoMultiplier() + ",\n" +
								"	            \"salt\": 0\n" + 
						"	          }");
			}
			if(canGenerateGeodes()) {
				structures.add("\"advancedrocketry:geode\"");
				biomeConditionalStructures.add(
						"	          \"advancedrocketry:geode\": {\n" + 
								"	            \"spacing\": " + 40 * getGeodeMultiplier() + ",\n" +
								"	            \"separation\": " + 20 * getGeodeMultiplier() + ",\n" +
								"	            \"salt\": 0\n" + 
						"	          }");
			}
			
			for(Biome biome : getBiomes()) {
				biomeStrings.add(
						"	    {\n" + 
								"              \"biome\": \"" + biome.getRegistryName().toString() + "\",\n" + 
								"              \"parameters\": {\n" + 
								"                \"altitude\": " + random.nextFloat() + ",\n" +
								"		\"humidity\": " + biome.getDownfall() + ",\n" +
								"                \"temperature\": " + biome.getTemperature() + ",				\n" +
								"                \"weirdness\": " + biome.getScale() + ",\n" +
								"                \"offset\": 0\n" + 
								"              }\n" + 
						"            }");
			}


			return 
					"{\n" + 
					"      \"generator\": {\n" + 
					"        \"type\": \"advancedrocketry:planetary_noise\",\n" + 
					"        \"seed\": " + seed + ",\n" +
					(structures.isEmpty() ?  "        \"starts\": [],\n" : "        \"starts\": [" + String.join(", ", structures) +  "],\n") + 
					"        \"dimension_props\": \"" + getId().toString() +  "\",\n" + 
					"        \"biome_source\": {\n" + 
					"		\n" + 
					"		\n" + 
					"		\"altitude_noise\": {\"firstOctave\": -7,\"amplitudes\": [1.0,1.0]},\n" + 
					"		\"humidity_noise\": {\"firstOctave\": -7,\"amplitudes\": [1.0,1.0]},\n" + 
					"		\"temperature_noise\": {\"firstOctave\": -7,\"amplitudes\": [1.0,1.0]},\n" + 
					"		\"weirdness_noise\": {\"firstOctave\": -7,\"amplitudes\": [1.0,1.0]},\n" + 
					"		\n" + 
					"		\n" + 
					"          \"type\": \"advancedrocketry:planetary\",\n" + 
					"          \"seed\": " + seed + ",\n" +
					"          \"biomes\": [\n" + 
					String.join(",\n", biomeStrings) + "\n" +
					"          ]\n" + 
					"        },\n" + 
					"    \"settings\": {\n" + 
					"      \"name\": \"" + getId().toString() + "\",\n" + 
					"      \"bedrock_roof_position\": -10,\n" + 
					"      \"bedrock_floor_position\": 0,\n" + 
					"      \"sea_level\": "  + getSeaLevel() + ",\n" +
					"      \"disable_mob_generation\": " +  (isHabitable() ? "true" : "false") + ",\n" + 
					"      \"default_block\": {\n" + 
					"        \"Name\": \"" + getStoneBlock().getBlock().getRegistryName().toString() + "\"\n" + 
					"      },\n" + 
					"      \"default_fluid\": {\n" + 
					"        \"Name\": \"" + getOceanBlock().getBlock().getRegistryName().toString() + "\",\n" + 
					"        \"Properties\": {\n" + 
					"          \"level\": \"0\"\n" + 
					"        }\n" + 
					"      },\n" + 
					"      \"noise\": {\n" + 
					"        \"density_factor\": 1,\n" + 
					"        \"density_offset\": -0.46875,\n" + 
					"        \"simplex_surface_noise\": true,\n" + 
					"        \"random_density_offset\": true,\n" + 
					"        \"island_noise_override\": false,\n" + 
					"        \"amplified\": false,\n" + 
					"        \"size_horizontal\": 1,\n" + 
					"        \"size_vertical\": 2,\n" + 
					"        \"height\": 256,\n" + 
					"        \"sampling\": {\n" + 
					"          \"xz_scale\": 1,\n" + 
					"          \"y_scale\": 1,\n" + 
					"          \"xz_factor\": 80,\n" + 
					"          \"y_factor\": 160\n" + 
					"        },\n" + 
					"        \"bottom_slide\": {\n" + 
					"          \"target\": -30,\n" + 
					"          \"size\": 0,\n" + 
					"          \"offset\": 0\n" + 
					"        },\n" + 
					"        \"top_slide\": {\n" + 
					"          \"target\": -10,\n" + 
					"          \"size\": 3,\n" + 
					"          \"offset\": 0\n" + 
					"        }\n" + 
					"      },\n" + 
					"      \"structures\": {\n" + 
					"        \"structures\": {\n" + 
					String.join(",\n", biomeConditionalStructures) +
					"        }\n" + 
					"      }\n" + 
					"    }\n" + 
					"      },\n" + 
					"      \"type\": \"advancedrocketry:planet\"\n" + 
					"    }";
		}


		Biome biome = AdvancedRocketryBiomes.moonBiomeDark;
		biomeStrings.add(
				"	    {\n" + 
						"              \"biome\": \"" + biome.getRegistryName().toString() + "\",\n" + 
						"              \"parameters\": {\n" + 
						"                \"altitude\": " + random.nextFloat() + ",\n" +
						"		\"humidity\": " + biome.getDownfall() + ",\n" +
						"                \"temperature\": " + biome.getTemperature() + ",				\n" +
						"                \"weirdness\": " + biome.getScale() + ",\n" +
						"                \"offset\": 0\n" + 
						"              }\n" + 
				"            }");
		
		return 
				"{\n" + 
				"      \"generator\": {\n" + 
				"        \"type\": \"advancedrocketry:planetary_noise\",\n" + 
				"        \"seed\": " + seed + ",\n" +
				(structures.isEmpty() ?  "        \"starts\": [],\n" : "        \"starts\": [" + String.join(", ", structures) +  "],\n") + 
				"        \"dimension_props\": \"" + getId().toString() +  "\",\n" + 
				"        \"biome_source\": {\n" + 
				"		\n" + 
				"		\n" + 
				"		\"altitude_noise\": {\"firstOctave\": -7,\"amplitudes\": [1.0,1.0]},\n" + 
				"		\"humidity_noise\": {\"firstOctave\": -7,\"amplitudes\": [1.0,1.0]},\n" + 
				"		\"temperature_noise\": {\"firstOctave\": -7,\"amplitudes\": [1.0,1.0]},\n" + 
				"		\"weirdness_noise\": {\"firstOctave\": -7,\"amplitudes\": [1.0,1.0]},\n" + 
				"		\n" + 
				"		\n" + 
				"          \"type\": \"advancedrocketry:planetary\",\n" + 
				"          \"seed\": " + seed + ",\n" +
				"          \"biomes\": [\n" + 
				String.join(",\n", biomeStrings) + "\n" +
				"          ]\n" + 
				"        },\n" + 
				"    \"settings\": {\n" + 
				"      \"name\": \"" + getId().toString() + "\",\n" + 
				"      \"bedrock_roof_position\": -10,\n" + 
				"      \"bedrock_floor_position\": -10,\n" + 
				"      \"sea_level\": "  + getSeaLevel() + ",\n" +
				"      \"disable_mob_generation\": " +  (isHabitable() ? "true" : "false") + ",\n" + 
				"      \"default_block\": {\n" + 
				"        \"Name\": \"" + getStoneBlock().getBlock().getRegistryName().toString() + "\"\n" + 
				"      },\n" + 
				"      \"default_fluid\": {\n" + 
				"        \"Name\": \"" + getOceanBlock().getBlock().getRegistryName().toString() + "\",\n" + 
				"        \"Properties\": {\n" + 
				"          \"level\": \"0\"\n" + 
				"        }\n" + 
				"      },\n" + 
				"      \"noise\": {\n" + 
				"        \"density_factor\": 0,\n" + 
				"        \"density_offset\": -0.46875,\n" + 
				"        \"simplex_surface_noise\": true,\n" + 
				"        \"random_density_offset\": false,\n" + 
				"        \"island_noise_override\": true,\n" + 
				"        \"amplified\": false,\n" + 
				"        \"size_horizontal\": 2,\n" + 
				"        \"size_vertical\": 1,\n" + 
				"        \"height\": 256,\n" + 
				"        \"sampling\": {\n" + 
				"          \"xz_scale\": 2,\n" + 
				"          \"y_scale\": 1,\n" + 
				"          \"xz_factor\": 80,\n" + 
				"          \"y_factor\": 160\n" + 
				"        },\n" + 
				"        \"bottom_slide\": {\n" + 
				"          \"target\": -30,\n" + 
				"          \"size\": 7,\n" + 
				"          \"offset\": 1\n" + 
				"        },\n" + 
				"        \"top_slide\": {\n" + 
				"          \"target\": -3000,\n" + 
				"          \"size\": 64,\n" + 
				"          \"offset\": -46\n" + 
				"        }\n" + 
				"      },\n" + 
				"      \"structures\": {\n" + 
				"        \"structures\": {\n" + 
				String.join(",\n", biomeConditionalStructures) +
				"        }\n" + 
				"      }\n" + 
				"    }\n" + 
				"      },\n" + 
				"      \"type\": \"advancedrocketry:planet\"\n" + 
				"    }";
	}

	/**
	 * @return temperature of the planet in Kelvin
	 */
	@Override
	public int getAverageTemp() {
		
		// Star is sometimes null early on in the loading process
		if(this.getStar() == null)
			return averageTemperature;
		averageTemperature = AstronomicalBodyHelper.getAverageTemperature(this.getStar(), this.getSolarOrbitalDistance(), this.getAtmosphereDensity());
		return averageTemperature;
	}

	public BlockState getOceanBlock() {
		return oceanBlock == null ? Blocks.WATER.getDefaultState() : oceanBlock;
	}

	public void setOceanBlock(BlockState block) {
		oceanBlock = block;
	}

	public BlockState getStoneBlock() {
		return fillerBlock == null ? Blocks.STONE.getDefaultState() : fillerBlock;
	}

	public void setStoneBlock(BlockState block) {
		fillerBlock = block;
	}

	public static DimensionProperties createFromNBT(ResourceLocation id, CompoundNBT nbt) {
		DimensionProperties properties = new DimensionProperties(id);
		properties.readFromNBT(nbt);
		properties.planetId = id;

		return properties;
	}

	/**
	 * Function for calculating atmosphere thinning with respect to height, normalized
	 * @param y
	 * @return the density of the atmosphere at the given height
	 */
	public float getAtmosphereDensityAtHeight(double y) {
		return atmosphereDensity*MathHelper.clamp((float) ( 1 + (256 - y)/200f), 0f,1f)/100f;
	}

	/**
	 * Gets the fog color at a given altitude, used to assist the illusion of thinning atmosphere
	 * @param y y-height
	 * @param fogColor current fog color at this location
	 * @return
	 */
	public float[] getFogColorAtHeight(double y, Vector3d fogColor) {
		float atmDensity = getAtmosphereDensityAtHeight(y);
		return new float[] { (float) (atmDensity * fogColor.x), (float) (atmDensity * fogColor.y), (float) (atmDensity * fogColor.z) };
	}

	public boolean isHabitable() {
		return this.getAtmosphere().isBreathable()
				&& Temps.getTempFromValue(this.averageTemperature).isInRange(Temps.COLD, Temps.HOT);
	}

	/**
	 * Sets the planet's id
	 * @param id
	 */
	public void setId(ResourceLocation id) {
		this.planetId = id;
	}

	@Override
	public void setParentOrbitalDistance(int distance) {
		this.orbitalDist = distance;

	}

	public double[] getPlanetPosition() {
		double orbitalDistance = this.orbitalDist;
		double theta = this.orbitTheta;
		double phi = this.orbitalPhi;

		return new double[] {orbitalDistance*Math.cos(theta), orbitalDistance*Math.sin(phi), orbitalDistance*Math.sin(theta)};
	}

	public ResourceLocation getStarId() {
		return starId;
	}

	@Override
	public String toString() {
		return String.format("Dimension ID: %d.  Dimension Name: %s.  Parent Star %d ", getId(), getName(), getStarId());
	}

	@Override
	public double getOrbitTheta() {
		return orbitTheta;
	}

	@Override
	public int getOrbitalDist() {
		return orbitalDist;
	}

	public int getSeaLevel() {
		return seaLevel;
	}

	public void setSeaLevel(int seaLevel) {
		this.seaLevel = MathHelper.clamp(seaLevel, 0, 255);
	}

	public void setGenType(int genType)
	{
		this.generatorType = genType;
	}

	public int getGenType() {
		return generatorType;
	}

	public void setGenerateCraters(boolean canGenerateCraters) {
		this.canGenerateCraters = canGenerateCraters;
	}

	public boolean canGenerateCraters() {
		return this.canGenerateCraters;
	}

	public float getCraterMultiplier() {
		return craterFrequencyMultiplier;
	}

	public void setCraterMultiplier(float craterFrequencyMultiplier) {
		this.craterFrequencyMultiplier = craterFrequencyMultiplier;
	}

	public void setGenerateGeodes(boolean canGenerateGeodes) {
		this.canGenerateGeodes = canGenerateGeodes;
	}

	public boolean canGenerateGeodes() {
		return this.canGenerateGeodes;
	}

	public float getGeodeMultiplier() {
		return volcanoFrequencyMultiplier;
	}

	public void setGeodeMultiplier(float geodeFrequencyMultiplier) {
		this.geodeFrequencyMultiplier = geodeFrequencyMultiplier;
	}

	public void setGenerateVolcanos(boolean canGenerateVolcanos) {
		this.canGenerateVolcanoes = canGenerateVolcanos;
	}

	public boolean canGenerateVolcanos() {
		return this.canGenerateVolcanoes;
	}

	public float getVolcanoMultiplier() {
		return volcanoFrequencyMultiplier;
	}

	public void setVolcanoMultiplier(float volcanoFrequencyMultiplier) {
		this.volcanoFrequencyMultiplier = volcanoFrequencyMultiplier;
	}
	public void setGenerateStructures(boolean canGenerateStructures) {
		this.canGenerateStructures = canGenerateStructures;
	}

	public boolean canGenerateStructures() {
		return canGenerateStructures;
	}

	public void setGenerateCaves(boolean canGenerateCaves) {
		this.canGenerateCaves = canGenerateCaves;
	}

	public boolean canGenerateCaves() {
		return this.canGenerateCaves;
	}

	public float getRenderSizePlanetView() {
		return (isMoon() ? 8f : 10f)*Math.max(this.getGravitationalMultiplier()*this.getGravitationalMultiplier(), .5f)*100;
	}

	public float getRenderSizeSolarView() {
		return (isMoon() ? 0.2f : 1f)*Math.max(this.getGravitationalMultiplier()*this.getGravitationalMultiplier(), .5f)*100;
	}

	// Relative to parent
	@Override
	public SpacePosition getSpacePosition() {
		float distanceMultiplier = isMoon() ? 75f : 100f;

		SpacePosition spacePosition = new SpacePosition();
		spacePosition.star = getStar();
		spacePosition.world = this;
		spacePosition.isInInterplanetarySpace = this.isMoon();
		spacePosition.pitch = 0;
		spacePosition.roll = 0;
		spacePosition.yaw = 0;

		spacePosition = spacePosition.getFromSpherical(distanceMultiplier*orbitalDist + (isMoon() ? 100 : 0), orbitTheta);

		return spacePosition;
	}

	@Override
	public float[] getRingColor() {
		return ringColor;
	}

	@Override
	public float[] getSkyColor() {
		return skyColor;
	}
}
