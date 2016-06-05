package zmaster587.advancedRocketry.dimension;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.commons.lang3.ArrayUtils;

import scala.reflect.internal.Trees.If;
import scala.util.Random;
import zmaster587.advancedRocketry.AdvancedRocketry;
import zmaster587.advancedRocketry.api.AdvancedRocketryBiomes;
import zmaster587.advancedRocketry.api.IAtmosphere;
import zmaster587.advancedRocketry.api.SatelliteRegistry;
import zmaster587.advancedRocketry.api.dimension.IDimensionProperties;
import zmaster587.advancedRocketry.api.dimension.solar.StellarBody;
import zmaster587.advancedRocketry.api.satellite.SatelliteBase;
import zmaster587.advancedRocketry.atmosphere.AtmosphereType;
import zmaster587.advancedRocketry.network.PacketHandler;
import zmaster587.advancedRocketry.network.PacketSatellite;
import zmaster587.libVulpes.util.VulpineMath;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagFloat;
import net.minecraft.nbt.NBTTagIntArray;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.biome.BiomeGenBase.TempCategory;
import net.minecraftforge.common.BiomeDictionary;
import net.minecraftforge.common.BiomeManager;
import net.minecraftforge.common.BiomeManager.BiomeEntry;
import net.minecraftforge.common.util.Constants.NBT;

public class DimensionProperties implements Cloneable, IDimensionProperties {

	/**
	 * Contains standardized temperature ranges for planets
	 * where 100 is earthlike, larger values are hotter
	 */
	public static enum Temps {
		TOOHOT(150),
		HOT(125),
		NORMAL(75),
		COLD(50),
		FRIGID(25),
		SNOWBALL(0);

		private int temp;
		Temps(int i) {
			temp = i;
		}

		@Deprecated
		public int getTemp() {
			return temp;
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
	public static enum AtmosphereTypes {
		HIGHPRESSURE(150),
		NORMAL(75),
		LOW(25),
		NONE(0);

		private int value;

		private AtmosphereTypes(int value) {
			this.value = value;
		}

		public int getAtmosphereValue() {
			return value;
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
	public static enum PlanetIcons {
		EARTHLIKE(new ResourceLocation("advancedrocketry:textures/planets/Earthlike.png")),
		LAVA(new ResourceLocation("advancedrocketry:textures/planets/Lava.png")),
		MARSLIKE(new ResourceLocation("advancedrocketry:textures/planets/marslike.png")),
		MOON(new ResourceLocation("advancedrocketry:textures/planets/moon.png")),
		WATERWORLD(new ResourceLocation("advancedrocketry:textures/planets/WaterWorld.png")),
		ICEWORLD(new ResourceLocation("advancedrocketry:textures/planets/IceWorld.png")),
		UNKNOWN(new ResourceLocation("advancedrocketry:textures/planets/Unknown.png")),
		;

		public static final ResourceLocation atmosphere = new ResourceLocation("advancedrocketry:textures/planets/Atmosphere.png");
		private ResourceLocation resource;

		private PlanetIcons(ResourceLocation resource) {
			this.resource = resource;
		}

		public ResourceLocation getResource() {
			return resource;
		}
	}

	public static final int MAX_ATM_PRESSURE = 200;
	public static final int MIN_ATM_PRESSURE = 0;
	
	public static final int MAX_DISTANCE = 200;
	public static final int MIN_DISTANCE = 0;
	
	public static final int MAX_GRAVITY = 200;
	public static final int MIN_GRAVITY = 0;
	
	//True if dimension is managed and created by AR (false otherwise)
	public boolean isNativeDimension;
	public float[] skyColor;
	public float[] fogColor;
	public float gravitationalMultiplier;
	public int orbitalDist;
	public int atmosphereDensity;
	public int averageTemperature;
	public int rotationalPeriod;
	public double orbitTheta;
	StellarBody star;
	private String name;
	public float[] sunriseSunsetColors;
	//public ExtendedBiomeProperties biomeProperties;
	private LinkedList<BiomeEntry> allowedBiomes;
	private boolean isRegistered = false;

	//Planet Heirachy
	private HashSet<Integer> childPlanets;
	private int parentPlanet;
	private int planetId;
	private boolean isStation;

	//Satallites
	private HashMap<Long,SatelliteBase> satallites;
	private List<SatelliteBase> tickingSatallites;


	public DimensionProperties(int id) {
		name = "Temp";
		resetProperties();

		planetId = id;
		parentPlanet = -1;
		childPlanets = new HashSet<Integer>();

		allowedBiomes = new LinkedList<BiomeManager.BiomeEntry>();
		satallites = new HashMap<>();
		tickingSatallites = new LinkedList<SatelliteBase>();
		isNativeDimension = true;
	}

	public DimensionProperties(int id ,String name) {
		this(id);
		this.name = name;
	}

	public DimensionProperties(int id, boolean shouldRegister) {
		this(id);
		isStation = !shouldRegister;
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
	 * Resets all properties to default
	 */
	public void resetProperties() {
		fogColor = new float[] {1,1,1};
		skyColor = new float[] {1f,1f,1f};
		sunriseSunsetColors = new float[] {.7f,.2f,.2f,1};
		gravitationalMultiplier = 1;
		rotationalPeriod = 24000;
		orbitalDist = 100;
		atmosphereDensity = 100;
	}

	@Override
	public float getGravitationalMultiplier() {
		return gravitationalMultiplier;
	}
	
	@Override
	public void setGravitationalMultiplier(float mult) {
		gravitationalMultiplier = mult;
	}
	
	/**
	 * @return the color of the sun as an array of floats represented as  {r,g,b}
	 */
	public float[] getSunColor() {
		return star.getColor();
	}

	/**
	 * Sets the host star for the planet
	 * @param star the star to set as the host for this planet
	 */
	public void setStar(StellarBody star) {
		this.star = star;
		if(!this.isMoon() && !isStation())
			this.star.addPlanet(this);
	}

	/**
	 * @return the host star for this planet
	 */
	public StellarBody getStar() {
		return star;
	}

	/**
	 * @return the {@link ResourceLocation} representing this planet, generated from the planet's properties
	 */
	public ResourceLocation getPlanetIcon() {
		AtmosphereTypes atmType = AtmosphereTypes.getAtmosphereTypeFromValue(atmosphereDensity);
		Temps tempType = Temps.getTempFromValue(averageTemperature);


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
	public int getId() {
		return planetId;
	}

	/**
	 * @return the DimID of the parent planet
	 */
	public int getParentPlanet() {
		return parentPlanet;
	}

	/**
	 * @return the {@link DimensionProperties} of the parent planet
	 */
	public DimensionProperties getParentProperties() {
		if(parentPlanet != -1)
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
		if(parentPlanet != -1)
			return getParentProperties().getSolarOrbitalDistance();
		return orbitalDist;
	}

	/**
	 * Sets this planet as a moon of the supplied planet's id.
	 * @param parentId parent planet's DIMID, or -1 for none
	 */
	public void setParentPlanet(DimensionProperties parent) {
		this.setParentPlanet(parent, true);
	}

	/**
	 * Sets this planet as a moon of the supplied planet's ID
	 * @param parentId DIMID of the parent planet
	 * @param update true to update the parent's planet to the change
	 */
	public void setParentPlanet(DimensionProperties parent, boolean update) {

		if(update) {
			if(parentPlanet != -1)
				parent.childPlanets.remove(new Integer(getId()));

			parentPlanet = parent.getId();
			if(parent.getId() != -1)
				parent.childPlanets.add(getId());
		}
		else 
			parentPlanet = parent.getId();
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
		return parentPlanet != -1;
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
		if(hasAtmosphere())
			return AtmosphereType.AIR;
		return AtmosphereType.VACUUM;
	}

	/**
	 * @return {@link ResourceLocation} refering to the image to render as atmospheric haze as seen from orbit
	 */
	public static ResourceLocation getAtmosphereResource() {
		return PlanetIcons.atmosphere;
	}

	/**
	 * @return true if the planet has an atmosphere
	 */
	public boolean hasAtmosphere() {
		return AtmosphereTypes.getAtmosphereTypeFromValue(atmosphereDensity).compareTo(AtmosphereTypes.LOW) < 0;
	}

	/**
	 * @return set of all moons orbiting this planet
	 */
	public Set<Integer> getChildPlanets() {
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
	 * @param id DIMID of the new child
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
	 * @param id
	 */
	public void removeChild(int id) {
		childPlanets.remove(id);
	}

	//Satallites --------------------------------------------------------
	/**
	 * Adds a satellite to this DIM
	 * @param satellite satellite to add
	 * @param world world to add the satellite to
	 */
	public void addSatallite(SatelliteBase satellite, World world) {
		satallites.put(satellite.getId(), satellite);
		satellite.setDimensionId(world);


		if(satellite.canTick())
			tickingSatallites.add(satellite);

		if(!world.isRemote)
			PacketHandler.sendToAll(new PacketSatellite(satellite));
	}

	/**
	 * Really only meant to be used on the client when recieving a packet
	 * @param satallite
	 */
	public void addSatallite(SatelliteBase satallite) {
		satallites.put(satallite.getId(), satallite);

		if(satallite.canTick() && !tickingSatallites.contains(satallite)) //TODO: check for dupes
			tickingSatallites.add(satallite);
	}

	/**
	 * Removes the satellite from orbit around this world
	 * @param satalliteId ID # for this satellite
	 * @return reference to the satellite object
	 */
	public SatelliteBase removeSatellite(long satalliteId) {
		SatelliteBase satallite = satallites.remove(satalliteId);

		if(satallite != null && satallite.canTick())
			tickingSatallites.remove(satallite);

		return satallite;
	}

	/**
	 * @param id ID # for this satellite
	 * @return a reference to the satelliteBase object given this ID
	 */
	public SatelliteBase getSatallite(long id) {
		return satallites.get(id);
	}

	//TODO: multithreading
	/**
	 * Tick satellites as needed
	 */
	public void tick() {
		Iterator<SatelliteBase> iterator = tickingSatallites.iterator();

		while(iterator.hasNext()) {
			SatelliteBase satallite = iterator.next();
			satallite.tickEntity();
		}
	}

	/**
	 * @return true if this dimension is allowed to have rivers
	 */
	public boolean hasRivers() {
		return AtmosphereTypes.getAtmosphereTypeFromValue(atmosphereDensity).compareTo(AtmosphereTypes.LOW) <= 0 && Temps.getTempFromValue(averageTemperature).isInRange(Temps.COLD, Temps.HOT);
	}


	/**
	 * Each Planet is assigned a list of biomes that are allowed to spawn there
	 * @return List of biomes allowed to spawn on this planet
	 */
	public List<BiomeEntry> getBiomes() {
		return (List<BiomeEntry>)allowedBiomes.clone();
	}

	/**
	 * Used to determine if a biome is allowed to spawn on ANY planet
	 * @param biome biome to check
	 * @return true if the biome is not allowed to spawn on any Dimension
	 */
	public boolean isBiomeblackListed(BiomeGenBase biome) {
		return AdvancedRocketryBiomes.instance.getBlackListedBiomes().contains(biome.biomeID);
	}

	/**
	 * @return a list of biomes allowed to spawn in this dimension
	 */
	public List<BiomeGenBase> getViableBiomes() {
		Random random = new Random(System.currentTimeMillis());
		ArrayList<BiomeGenBase> viableBiomes = new ArrayList<BiomeGenBase>();

		if(random.nextInt(5) == 0) {
			List<BiomeGenBase> list = new LinkedList<BiomeGenBase>(AdvancedRocketryBiomes.instance.getSingleBiome());

			while(list.size() > 1) {
				BiomeGenBase biome = list.get(random.nextInt(list.size()));
				Temps temp = Temps.getTempFromValue(averageTemperature);
				if((biome.getTempCategory() == TempCategory.COLD && temp.isInRange(Temps.FRIGID, Temps.NORMAL)) ||
						((biome.getTempCategory() == TempCategory.MEDIUM || biome.getTempCategory() == TempCategory.OCEAN) &&
								temp.isInRange(Temps.COLD, Temps.HOT)) ||
								(biome.getTempCategory() == TempCategory.WARM && temp.isInRange(Temps.NORMAL, Temps.HOT))) {
					viableBiomes.add(biome);
					return viableBiomes;
				}
				list.remove(biome);
			}
		}

		
		if(atmosphereDensity < AtmosphereTypes.LOW.value)
			viableBiomes.add(AdvancedRocketryBiomes.moonBiome);

		else if(averageTemperature > Temps.TOOHOT.getTemp()) {
			viableBiomes.add(AdvancedRocketryBiomes.hotDryBiome);
		}
		else if(averageTemperature > Temps.HOT.getTemp()) {
			for(BiomeGenBase biome : BiomeGenBase.getBiomeGenArray()) {
				if(biome != null && (BiomeDictionary.isBiomeOfType(biome,BiomeDictionary.Type.HOT) || BiomeDictionary.isBiomeOfType(biome,BiomeDictionary.Type.OCEAN))  && !isBiomeblackListed(biome)) {
					viableBiomes.add(biome);
				}
			}
		}
		else if(averageTemperature > Temps.NORMAL.getTemp()) {
			for(BiomeGenBase biome : BiomeGenBase.getBiomeGenArray()) {
				if(biome != null && !BiomeDictionary.isBiomeOfType(biome,BiomeDictionary.Type.COLD) && !isBiomeblackListed(biome)) {
					viableBiomes.add(biome);
				}
			}
			viableBiomes.addAll(Arrays.asList(BiomeDictionary.getBiomesForType(BiomeDictionary.Type.OCEAN)));
		}
		else if(averageTemperature > Temps.COLD.getTemp()) {
			for(BiomeGenBase biome : BiomeGenBase.getBiomeGenArray()) {
				if(biome != null && !BiomeDictionary.isBiomeOfType(biome,BiomeDictionary.Type.HOT) && !isBiomeblackListed(biome)) {
					viableBiomes.add(biome);
				}
			}
			viableBiomes.addAll(Arrays.asList(BiomeDictionary.getBiomesForType(BiomeDictionary.Type.OCEAN)));
		}
		else if(averageTemperature > Temps.FRIGID.getTemp()) {

			viableBiomes.addAll(Arrays.asList(BiomeDictionary.getBiomesForType(BiomeDictionary.Type.COLD)));
		}
		else {//(averageTemperature >= Temps.SNOWBALL.getTemp())
			viableBiomes.addAll(Arrays.asList(BiomeDictionary.getBiomesForType(BiomeDictionary.Type.COLD)));
			//TODO:
		}

		if(atmosphereDensity > AtmosphereTypes.HIGHPRESSURE.value && Temps.getTempFromValue(averageTemperature).isInRange(Temps.NORMAL, Temps.HOT))
			viableBiomes.addAll(AdvancedRocketryBiomes.instance.getHighPressureBiomes());

		return viableBiomes;
	}

	/**
	 * Adds a biome to the list of biomes allowed to spawn on this planet
	 * @param biome biome to be added as viable
	 */
	public void addBiome(BiomeGenBase biome) {
		ArrayList<BiomeGenBase> biomes = new ArrayList<BiomeGenBase>();
		biomes.add(biome);
		allowedBiomes.addAll(getBiomesEntries(biomes));
	}

	/**
	 * Adds a biome to the list of biomes allowed to spawn on this planet
	 * @param biome biome to be added as viable
	 * @return true if the biome was added sucessfully, false otherwise
	 */
	public boolean addBiome(int biomeId) {
		
		BiomeGenBase biome =  BiomeGenBase.getBiome(biomeId);
		if(biomeId == 0 || biome != BiomeGenBase.ocean) {
			List<BiomeGenBase> biomes = new ArrayList<BiomeGenBase>();
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
	public void addBiomes(List<BiomeGenBase> biomes) {
		//TODO check for duplicates
		allowedBiomes.addAll(getBiomesEntries(biomes));
	}

	/**
	 * Clears the list of allowed biomes and replaces it with the provided list
	 * @param biomes
	 */
	public void setBiomes(List<BiomeGenBase> biomes) {
		allowedBiomes.clear();
		addBiomes(biomes);
	}

	/**
	 * Adds all biomes of this type to the list of biomes allowed to generate
	 * @param type
	 */
	public void addBiomeType(BiomeDictionary.Type type) {

		ArrayList<BiomeGenBase> entryList = new ArrayList<BiomeGenBase>();

		entryList.addAll(Arrays.asList(BiomeDictionary.getBiomesForType(type)));

		//Neither are acceptable on planets
		entryList.remove(BiomeGenBase.hell);
		entryList.remove(BiomeGenBase.sky);

		//Make sure we dont add double entries
		Iterator<BiomeGenBase> iter = entryList.iterator();
		while(iter.hasNext()) {
			BiomeGenBase nextbiome = iter.next();
			for(BiomeEntry entry : allowedBiomes) {
				if(BiomeDictionary.areBiomesEquivalent(entry.biome, nextbiome))
					iter.remove();
			}

		}
		allowedBiomes.addAll(getBiomesEntries(entryList));

	}

	/**
	 * Removes all biomes of this type from the list of biomes allowed to generate
	 * @param type
	 */
	public void removeBiomeType(BiomeDictionary.Type type) {

		ArrayList<BiomeGenBase> entryList = new ArrayList<BiomeGenBase>();

		entryList.addAll(Arrays.asList(BiomeDictionary.getBiomesForType(type)));

		for(BiomeGenBase biome : entryList) {
			Iterator<BiomeEntry> iterator = allowedBiomes.iterator();
			while(iterator.hasNext()) {
				if(BiomeDictionary.areBiomesEquivalent(iterator.next().biome, biome))
					iterator.remove();
			}
		}

	}

	/**
	 * Gets a list of BiomeEntries allowed to spawn in this dimension
	 * @param biomeIds
	 * @return
	 */
	private ArrayList<BiomeEntry> getBiomesEntries(List<BiomeGenBase> biomeIds) {

		ArrayList<BiomeEntry> biomeEntries = new ArrayList<BiomeManager.BiomeEntry>();

		for(BiomeGenBase biomes : biomeIds) {

			if(biomes == BiomeGenBase.desert) {
				biomeEntries.add(new BiomeEntry(BiomeGenBase.desert, 30));
				continue;
			}
			else if(biomes == BiomeGenBase.savanna) {
				biomeEntries.add(new BiomeEntry(BiomeGenBase.savanna, 20));
				continue;
			}
			else if(biomes == BiomeGenBase.plains) {
				biomeEntries.add(new BiomeEntry(BiomeGenBase.plains, 10));
				continue;
			}

			boolean notFound = true;

			label:

				for(BiomeManager.BiomeType types : BiomeManager.BiomeType.values()) {
					for(BiomeEntry entry : BiomeManager.getBiomes(types)) {
						if(biomes == null)
							AdvancedRocketry.logger.warning("Null biomes loaded for DIMID: " + this.getId());
						else if(entry.biome.biomeID == biomes.biomeID) {
							biomeEntries.add(entry);
							notFound = false;

							break label;
						}
					}
				}

			if(notFound && biomes != null) {
				biomeEntries.add(new BiomeEntry(biomes, 30));
			}
		}

		return biomeEntries;
	}

	public void readFromNBT(NBTTagCompound nbt) {
		NBTTagList list;

		if(nbt.hasKey("skyColor")) {
			list = nbt.getTagList("skyColor", NBT.TAG_FLOAT);
			skyColor = new float[list.tagCount()];
			for(int f = 0 ; f < list.tagCount(); f++) {
				skyColor[f] = list.func_150308_e(f);
			}
		}

		if(nbt.hasKey("sunriseSunsetColors")) {
			list = nbt.getTagList("sunriseSunsetColors", NBT.TAG_FLOAT);
			sunriseSunsetColors = new float[list.tagCount()];
			for(int f = 0 ; f < list.tagCount(); f++) {
				sunriseSunsetColors[f] = list.func_150308_e(f);
			}
		}

		if(nbt.hasKey("fogColor")) {
			list = nbt.getTagList("fogColor", NBT.TAG_FLOAT);
			fogColor = new float[list.tagCount()];
			for(int f = 0 ; f < list.tagCount(); f++) {
				fogColor[f] = list.func_150308_e(f);
			}
		}

		//Load biomes
		if(nbt.hasKey("biomes")) {

			allowedBiomes.clear();
			int biomeIds[] = nbt.getIntArray("biomes");
			List<BiomeGenBase> biomesList = new ArrayList<BiomeGenBase>();


			for(int i = 0; i < biomeIds.length; i++) {
				biomesList.add(AdvancedRocketryBiomes.instance.getBiomeById(biomeIds[i]));
			}

			allowedBiomes.addAll(getBiomesEntries(biomesList));
		}



		gravitationalMultiplier = nbt.getFloat("gravitationalMultiplier");
		orbitalDist = nbt.getInteger("orbitalDist");
		orbitTheta = nbt.getDouble("orbitTheta");
		atmosphereDensity = nbt.getInteger("atmosphereDensity");
		averageTemperature = nbt.getInteger("avgTemperature");
		rotationalPeriod = nbt.getInteger("rotationalPeriod");
		name = nbt.getString("name");
		isNativeDimension = nbt.hasKey("isNative") ? nbt.getBoolean("isNative") : true; //Prevent world breakages when loading from old version

		//Hierarchy
		if(nbt.hasKey("childrenPlanets")) {
			for(int i : nbt.getIntArray("childrenPlanets"))
				childPlanets.add(i);
		}

		//Note: parent planet must be set before setting the star otherwise it would cause duplicate planets in the StellarBody's array
		parentPlanet = nbt.getInteger("parentPlanet");
		this.setStar( DimensionManager.getInstance().getStar(nbt.getInteger("starId")));

		//Satallites

		if(nbt.hasKey("satallites")) {
			NBTTagCompound allSatalliteNbt = nbt.getCompoundTag("satallites");

			for(Object keyObject : allSatalliteNbt.func_150296_c()) {
				String key = (String)keyObject;
				Long longKey = Long.parseLong(key);

				NBTTagCompound satalliteNbt = allSatalliteNbt.getCompoundTag(key);

				if(satallites.containsKey(longKey)){
					satallites.get(longKey).readFromNBT(satalliteNbt);
				} 
				else {
					SatelliteBase satallite = SatelliteRegistry.createFromNBT(satalliteNbt);

					satallites.put(longKey, satallite);

					if(satallite.canTick()) {
						tickingSatallites.add(satallite);
					}
				}
			}
		}
	}

	public void writeToNBT(NBTTagCompound nbt) {
		NBTTagList list;

		if(skyColor != null) {
			list = new NBTTagList();
			for(float f : skyColor) {
				list.appendTag(new NBTTagFloat(f));
			}
			nbt.setTag("skyColor", list);
		}

		if(sunriseSunsetColors != null) {
			list = new NBTTagList();
			for(float f : sunriseSunsetColors) {
				list.appendTag(new NBTTagFloat(f));
			}
			nbt.setTag("sunriseSunsetColors", list);
		}

		list = new NBTTagList();
		for(float f : fogColor) {
			list.appendTag(new NBTTagFloat(f));
		}
		nbt.setTag("fogColor", list);

		if(!allowedBiomes.isEmpty()) {
			int biomeId[] = new int[allowedBiomes.size()];
			for(int i = 0; i < allowedBiomes.size(); i++) {
				biomeId[i] = allowedBiomes.get(i).biome.biomeID;
			}
			nbt.setIntArray("biomes", biomeId);
		}

		nbt.setInteger("starId", star.getId());
		nbt.setFloat("gravitationalMultiplier", gravitationalMultiplier);
		nbt.setInteger("orbitalDist", orbitalDist);
		nbt.setDouble("orbitTheta", orbitTheta);
		nbt.setInteger("atmosphereDensity", atmosphereDensity);
		nbt.setInteger("avgTemperature", averageTemperature);
		nbt.setInteger("rotationalPeriod", rotationalPeriod);
		nbt.setString("name", name);
		nbt.setBoolean("isNative", isNativeDimension);

		//Hierarchy
		if(!childPlanets.isEmpty()) {
			Integer intList[] = new Integer[childPlanets.size()];

			NBTTagIntArray childArray = new NBTTagIntArray(ArrayUtils.toPrimitive(childPlanets.toArray(intList)));
			nbt.setTag("childrenPlanets", childArray);
		}

		nbt.setInteger("parentPlanet", parentPlanet);

		//Satallites

		if(!satallites.isEmpty()) {
			NBTTagCompound allSatalliteNbt = new NBTTagCompound();
			for(Entry<Long, SatelliteBase> entry : satallites.entrySet()) {
				NBTTagCompound satalliteNbt = new NBTTagCompound();

				entry.getValue().writeToNBT(satalliteNbt);
				allSatalliteNbt.setTag(entry.getKey().toString(), satalliteNbt);
			}
			nbt.setTag("satallites", allSatalliteNbt);
		}

	}

	public static DimensionProperties createFromNBT(int id, NBTTagCompound nbt) {
		DimensionProperties properties = new DimensionProperties(id);
		properties.readFromNBT(nbt);
		properties.planetId = id;

		return properties;
	}

	/**
	 * Function for calculating atmosphere thinning with respect to hieght 
	 * @param y
	 * @return the density of the atmosphere at the given height
	 */
	public float getAtmosphereDensityAtHeight(double y) {
		return atmosphereDensity*MathHelper.clamp_float((float) ( 1 + (256 - y)/200f), 0f,1f)/100f;
	}

	/**
	 * Gets the fog color at a given altitude, used to assist the illusion of thinning atmosphere
	 * @param y
	 * @param fogColor current fog color at this location
	 * @return
	 */
	public float[] getFogColorAtHeight(double y, Vec3 fogColor) {
		float atmDensity = getAtmosphereDensityAtHeight(y);
		return new float[] { (float) (atmDensity * fogColor.xCoord), (float) (atmDensity * fogColor.yCoord), (float) (atmDensity * fogColor.zCoord) };
	}

	/**
	 * Sets the planet's id
	 * @param id
	 */
	public void setId(int id) {
		this.planetId = id;
	}
}