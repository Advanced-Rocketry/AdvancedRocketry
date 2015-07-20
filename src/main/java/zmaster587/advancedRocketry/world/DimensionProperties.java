package zmaster587.advancedRocketry.world;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import scala.actors.threadpool.Arrays;
import zmaster587.advancedRocketry.AdvancedRocketry;
import zmaster587.advancedRocketry.api.AdvancedRocketryBiomes;
import zmaster587.advancedRocketry.world.storage.ExtendedBiomeProperties;

import com.google.common.collect.ImmutableList;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagFloat;
import net.minecraft.nbt.NBTTagInt;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraftforge.common.BiomeDictionary;
import net.minecraftforge.common.BiomeManager;
import net.minecraftforge.common.BiomeManager.BiomeEntry;
import net.minecraftforge.common.util.Constants.NBT;

public class DimensionProperties {

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
		
		public int getTemp() {
			return temp;
		}
	}
	
	public static DimensionProperties overworldProperties;
	
	static {
		overworldProperties = new DimensionProperties();
		overworldProperties.atmosphereDensity = 100;
		overworldProperties.averageTemperature = 100;
		overworldProperties.gravitationalMultiplier = 100;
		overworldProperties.orbitalDist = 100;
		overworldProperties.skyColor = new float[] {1f, 1f, 1f};
	}
	
	public float[] skyColor;
	public float[] fogColor;
	public float gravitationalMultiplier;
	public int orbitalDist;
	public int atmosphereDensity;
	public int averageTemperature;
	public int rotationalPeriod;
	public float[] sunColor;
	public String name;
	public float[] sunriseSunsetColors;
	public ExtendedBiomeProperties biomeProperties;
	private ArrayList<BiomeEntry> allowedBiomes;
	
	public DimensionProperties() {
		name = "Temp";
		
		resetProperties();
		
		allowedBiomes = new ArrayList<BiomeManager.BiomeEntry>();
	}

	public DimensionProperties(String name) {
		this();
		this.name = name;
	}


	public void resetProperties() {
		fogColor = new float[] {1,1,1};
		sunColor = new float[] {.7f,.5f,.1f};
		skyColor = new float[] {1f,1f,1f};
		sunriseSunsetColors = new float[] {.7f,.2f,.2f,1};
		gravitationalMultiplier = 1;
		rotationalPeriod = 24000;
		orbitalDist = 100;
		atmosphereDensity = 100;
	}
	
	public List<BiomeEntry> getBiomes(int id) {
		return allowedBiomes;
	}

	public boolean isBiomeblackListed(BiomeGenBase biome) {
		return biome.biomeID == BiomeGenBase.sky.biomeID || biome.biomeID == BiomeGenBase.hell.biomeID;
	}
	
	public List<BiomeGenBase> getViableBiomes() {
		
		ArrayList<BiomeGenBase> viableBiomes = new ArrayList<BiomeGenBase>();
		
		if(averageTemperature > Temps.TOOHOT.getTemp()) {
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
		else if(averageTemperature > Temps.SNOWBALL.getTemp()) {
			//TODO:
		}
		else {
			//TODO
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


			for(BiomeManager.BiomeType types : BiomeManager.BiomeType.values()) {
				for(BiomeEntry entry : BiomeManager.getBiomes(types)) {
					if(biomes == null)
						System.out.println("WTF null biome");
					else if(entry.biome.biomeID == biomes.biomeID) {
						biomeEntries.add(entry);
						notFound = false;
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

		if(nbt.hasKey("sunColor")) {
			list = nbt.getTagList("sunColor", NBT.TAG_FLOAT);
			sunColor = new float[list.tagCount()];
			for(int f = 0 ; f < list.tagCount(); f++) {
				sunColor[f] = list.func_150308_e(f);
			}
		}

		//Load biomes
		if(nbt.hasKey("biomes")) {
			list = nbt.getTagList("biomes", NBT.TAG_FLOAT);
			
			ArrayList<BiomeGenBase> biomeGenList = new ArrayList<BiomeGenBase>();
			for(int f = 0 ; f < list.tagCount(); f++) {
				int id = (int)list.func_150308_e(f);
				BiomeGenBase biome = AdvancedRocketryBiomes.instance.getBiomeById(id);
				
				biomeGenList.add(biome);
			}
			allowedBiomes.addAll(getBiomesEntries(biomeGenList));
		}

		gravitationalMultiplier = nbt.getFloat("gravitationalMultiplier");
		orbitalDist = nbt.getInteger("orbitalDist");
		atmosphereDensity = nbt.getInteger("atmosphereDensity");
		averageTemperature =	nbt.getInteger("avgTemperature");
		rotationalPeriod = nbt.getInteger("rotationalPeriod");
		name = nbt.getString("name");
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

		list = new NBTTagList();
		for(float f : sunColor) {
			list.appendTag(new NBTTagFloat(f));
		}
		nbt.setTag("sunColor", list);
		
		list = new NBTTagList();
		for(BiomeEntry biomeEntry : allowedBiomes) {
			list.appendTag(new NBTTagFloat(biomeEntry.biome.biomeID));
		}
		nbt.setTag("biomes", list);

		nbt.setFloat("gravitationalMultiplier", gravitationalMultiplier);
		nbt.setInteger("orbitalDist", orbitalDist);
		nbt.setInteger("atmosphereDensity", atmosphereDensity);
		nbt.setInteger("avgTemperature", averageTemperature);
		nbt.setInteger("rotationalPeriod", rotationalPeriod);
		nbt.setString("name", name);
	}

	public static DimensionProperties createFromNBT(NBTTagCompound nbt) {
		DimensionProperties properties = new DimensionProperties();
		properties.readFromNBT(nbt);

		return properties;
	}
	
	public float getAtmosphereDensityAtHeight(double y) {
		return atmosphereDensity*MathHelper.clamp_float((float) ( 1 + (256 - y)/200f), 0f,1f)/100f;
	}
	
	public float[] getFogColorAtHeight(double y, Vec3 fogColor) {
		float atmDensity = getAtmosphereDensityAtHeight(y);
		return new float[] { (float) (atmDensity * fogColor.xCoord), (float) (atmDensity * fogColor.yCoord), (float) (atmDensity * fogColor.zCoord) };
	}
}