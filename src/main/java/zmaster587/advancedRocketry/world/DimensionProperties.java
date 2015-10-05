package zmaster587.advancedRocketry.world;

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

import zmaster587.advancedRocketry.api.AdvancedRocketryBiomes;
import zmaster587.advancedRocketry.api.SatelliteRegistry;
import zmaster587.advancedRocketry.api.satellite.SatelliteBase;
import zmaster587.advancedRocketry.network.PacketHandler;
import zmaster587.advancedRocketry.network.PacketSatellite;
import zmaster587.advancedRocketry.world.solar.StellarBody;
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
import net.minecraftforge.common.BiomeDictionary;
import net.minecraftforge.common.BiomeManager;
import net.minecraftforge.common.BiomeManager.BiomeEntry;
import net.minecraftforge.common.util.Constants.NBT;

public class DimensionProperties implements Cloneable {

	static enum Temps {
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

		public static Temps getTempFromValue(int value) {
			for(Temps type : Temps.values()) {
				if(value > type.temp)
					return type;
			}
			return SNOWBALL;
		}
	}

	static enum AtmosphereTypes {
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

		/*public int compareTo(AtmosphereTypes type) {
			if(type == this)
				return 0;
			else if(type.value < this.value)
				return -1;

			return 1;
		}*/
	}

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

	public float[] skyColor;
	public float[] fogColor;
	public float gravitationalMultiplier;
	public int orbitalDist;
	public int atmosphereDensity;
	public int averageTemperature;
	public int rotationalPeriod;
	public double orbitTheta;
	StellarBody star;
	public String name;
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

	public void resetProperties() {
		fogColor = new float[] {1,1,1};
		skyColor = new float[] {1f,1f,1f};
		sunriseSunsetColors = new float[] {.7f,.2f,.2f,1};
		gravitationalMultiplier = 1;
		rotationalPeriod = 24000;
		orbitalDist = 100;
		atmosphereDensity = 100;
	}

	public float[] getSunColor() {
		return star.getColor();
	}

	public void setStar(StellarBody star) {
		this.star = star;
		if(!this.isMoon() && !isStation())
			this.star.addPlanet(this);
	}

	public StellarBody getStar() {
		return star;
	}

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

	public String getName() {
		return name;
	}

	//Planet hierarchy

	public int getId() {
		return planetId;
	}

	public int getParentPlanet() {
		return parentPlanet;
	}

	public DimensionProperties getParentProperties() {
		if(parentPlanet != -1)
			return DimensionManager.getInstance().getDimensionProperties(parentPlanet);
		return null;
	}

	public int getParentOrbitalDistance() {
		return orbitalDist;
	}

	public int getSolarOrbitalDistance() {
		if(parentPlanet != -1)
			return getParentProperties().getSolarOrbitalDistance();
		return orbitalDist;
	}

	public void setParentPlanet(int parentId) {
		this.setParentPlanet(parentId, true);
	}

	public void setParentPlanet(int parentId, boolean update) {

		if(update) {
			if(parentPlanet != -1)
				getParentProperties().childPlanets.remove(new Integer(getId()));

			parentPlanet = parentId;
			if(parentId != -1)
				getParentProperties().childPlanets.add(getId());
		}
		else 
			parentPlanet = parentId;
	}

	public boolean hasChildren() {
		return !childPlanets.isEmpty();
	}

	public boolean isMoon() {
		return parentPlanet != -1;
	}

	public boolean isStation() {
		return isStation;
	}

	public static ResourceLocation getAtmosphereResource() {
		return PlanetIcons.atmosphere;
	}

	public boolean hasAtmosphere() {
		return AtmosphereTypes.getAtmosphereTypeFromValue(atmosphereDensity).compareTo(AtmosphereTypes.LOW) == -1;
	}

	public Set<Integer> getChildPlanets() {
		return childPlanets;
	}

	public int getPathLengthToStar() {
		if(isMoon())
			return 1 + getParentProperties().getPathLengthToStar();
		return 1;
	}

	public boolean addChildPlanet(int id) {
		//TODO: possibly check to the stellar level
		if(id == parentPlanet)
			return false;

		childPlanets.add(id);
		DimensionManager.getInstance().getDimensionProperties(id).setParentPlanet(planetId);
		return true;
	}

	public void removeChild(int id) {
		childPlanets.remove(id);
	}

	//Satallites
	public void addSatallite(SatelliteBase satallite, World world) {
		satallites.put(satallite.getId(), satallite);
		satallite.setDimensionId(world);


		if(satallite.canTick())
			tickingSatallites.add(satallite);

		if(!world.isRemote)
			PacketHandler.sendToAll(new PacketSatellite(satallite));
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

	public SatelliteBase removeSatellite(long satalliteId) {
		SatelliteBase satallite = satallites.remove(satalliteId);

		if(satallite != null && satallite.canTick())
			tickingSatallites.remove(satallite);

		return satallite;
	}

	public SatelliteBase getSatallite(long id) {
		return satallites.get(id);
	}

	//TODO: multithreading
	public void tick() {
		Iterator<SatelliteBase> iterator = tickingSatallites.iterator();

		while(iterator.hasNext()) {
			SatelliteBase satallite = iterator.next();
			satallite.tickEntity();
		}
	}

	public boolean hasRivers() {
		return AtmosphereTypes.getAtmosphereTypeFromValue(atmosphereDensity).compareTo(AtmosphereTypes.LOW) != -1 && VulpineMath.isBetween(Temps.getTempFromValue(averageTemperature).ordinal(), Temps.COLD.ordinal(), Temps.HOT.ordinal());
	}


	public List<BiomeEntry> getBiomes(int id) {
		return (List<BiomeEntry>)allowedBiomes.clone();
	}

	public boolean isBiomeblackListed(BiomeGenBase biome) {
		return biome.biomeID == BiomeGenBase.sky.biomeID || biome.biomeID == BiomeGenBase.hell.biomeID;
	}

	public List<BiomeGenBase> getViableBiomes() {

		ArrayList<BiomeGenBase> viableBiomes = new ArrayList<BiomeGenBase>();

		if(atmosphereDensity < AtmosphereTypes.LOW.value)
			viableBiomes.add(AdvancedRocketryBiomes.moonBiome);

		else if(averageTemperature > Temps.TOOHOT.getTemp()) {
			viableBiomes.add(AdvancedRocketryBiomes.hotDryBiome);
		}
		else if(averageTemperature > Temps.HOT.getTemp()) {
			viableBiomes.addAll(Arrays.asList(BiomeDictionary.getBiomesForType(BiomeDictionary.Type.HOT)));
		}
		else if(averageTemperature > Temps.NORMAL.getTemp()) {
			for(BiomeGenBase biome : BiomeGenBase.getBiomeGenArray()) {
				if(biome != null && !BiomeDictionary.isBiomeOfType(biome,BiomeDictionary.Type.COLD) && !isBiomeblackListed(biome)) {
					viableBiomes.add(biome);
				}
			}
		}
		else if(averageTemperature > Temps.COLD.getTemp()) {
			for(BiomeGenBase biome : BiomeGenBase.getBiomeGenArray()) {
				if(biome != null && !BiomeDictionary.isBiomeOfType(biome,BiomeDictionary.Type.HOT) && !isBiomeblackListed(biome)) {
					viableBiomes.add(biome);
				}
			}
		}
		else if(averageTemperature > Temps.FRIGID.getTemp()) {

			viableBiomes.addAll(Arrays.asList(BiomeDictionary.getBiomesForType(BiomeDictionary.Type.COLD)));
		}
		else {//(averageTemperature >= Temps.SNOWBALL.getTemp())
			viableBiomes.addAll(Arrays.asList(BiomeDictionary.getBiomesForType(BiomeDictionary.Type.COLD)));
			//TODO:
		}

		return viableBiomes;
	}

	public void addBiome(BiomeGenBase biome) {
		ArrayList<BiomeGenBase> biomes = new ArrayList<BiomeGenBase>();
		biomes.add(biome);
		allowedBiomes.addAll(getBiomesEntries(biomes));
	}

	public void addBiomes(List<BiomeGenBase> biomes) {
		//TODO check for duplicates
		allowedBiomes.addAll(getBiomesEntries(biomes));
	}

	public void setBiomes(List<BiomeGenBase> biomes) {
		allowedBiomes.clear();
		addBiomes(biomes);
	}

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
							System.out.println("WTF null biome");
						else if(entry.biome.biomeID == biomes.biomeID) {
							biomeEntries.add(entry);
							notFound = false;

							break label;
						}
					}
				}

			if(notFound) {
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

	public float getAtmosphereDensityAtHeight(double y) {
		return atmosphereDensity*MathHelper.clamp_float((float) ( 1 + (256 - y)/200f), 0f,1f)/100f;
	}

	public float[] getFogColorAtHeight(double y, Vec3 fogColor) {
		float atmDensity = getAtmosphereDensityAtHeight(y);
		return new float[] { (float) (atmDensity * fogColor.xCoord), (float) (atmDensity * fogColor.yCoord), (float) (atmDensity * fogColor.zCoord) };
	}

	public void setId(int id) {
		this.planetId = id;
	}
}