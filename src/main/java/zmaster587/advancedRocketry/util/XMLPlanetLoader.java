package zmaster587.advancedRocketry.util;

import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTException;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.common.BiomeManager.BiomeEntry;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fml.common.registry.EntityRegistry;
import net.minecraftforge.oredict.OreDictionary;
import org.w3c.dom.*;
import org.xml.sax.SAXException;
import zmaster587.advancedRocketry.AdvancedRocketry;
import zmaster587.advancedRocketry.api.ARConfiguration;
import zmaster587.advancedRocketry.api.dimension.IDimensionProperties;
import zmaster587.advancedRocketry.api.dimension.solar.IGalaxy;
import zmaster587.advancedRocketry.api.dimension.solar.StellarBody;
import zmaster587.advancedRocketry.dimension.DimensionManager;
import zmaster587.advancedRocketry.dimension.DimensionProperties;

import javax.annotation.Nonnull;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.util.*;
import java.util.stream.Collectors;

public class XMLPlanetLoader {

	
	private static final String ATTR_TEMP = "temp";
	private static final String GENERATEGEODES = "generateGeodes";
	private static final String GENERATESTRUCTURES = "generateStructures";
	private static final String GENERATEVOLCANOS = "generateVolcanos";
	private static final String GENERATECRATERS = "generateCraters";
	private static final String ELEMENT_GALAXY = "galaxy";
	private static final String ELEMENT_STAR = "star";
	private static final String ELEMENT_PLANET = "planet";
	private static final String ATTR_BLACKHOLE = "blackHole";
	private static final String ATTR_NAME = "name";
	private static final String ATTR_X = "x";
	private static final String ATTR_Y = "y";
	private static final String ATTR_SIZE = "size";
	private static final String ATTR_NUMPLANETS = "numPlanets";
	private static final String ATTR_NUMGASPLANETS = "numGasGiants";
	private static final String ATTR_SEPERATION = "separation";
	private static final String ATTR_DIMID = "DIMID";
	private static final String ATTR_NATIVEDIM = "dimMapping";
	private static final String ATTR_ICON = "customIcon";
	private static final String ELEMENT_ISKNOWN = "isKnown";
	private static final String ELEMENT_HASRINGS = "hasRings";
	private static final String ELEMENT_RINGCOLOR = "ringColor";
	private static final String ELEMENT_GASGIANT = "GasGiant";
	private static final String ELEMENT_GAS = "gas";
	private static final String ELEMENT_FOGCOLOR = "fogColor";
	private static final String ELEMENT_SKYCOLOR = "skyColor";
	private static final String ELEMENT_GRAVITY = "gravitationalMultiplier";
	private static final String ELEMENT_DISTANCE = "orbitalDistance";
	private static final String ELEMENT_BASEORBITTHETA = "orbitalTheta";
	private static final String ELEMENT_PHI = "orbitalPhi";
	private static final String ELEMENT_RETROGRADE = "retrograde";
	private static final String PEAK_INSOLATION = "peakInsolation";
	private static final String PEAK_INSOLATION_WITHOUT_ATMOSPHERE = "peakInsolationNoAtmosphere";
	private static final String AVG_TEMPERATURE = "avgTemperature";
	private static final String ELEMENT_PERIOD = "rotationalPeriod";
	private static final String ELEMENT_HASOXYGEN = "hasOxygen";
	private static final String ELEMENT_ATMDENSITY = "atmosphereDensity";
	private static final String ELEMENT_SEALEVEL = "seaLevel";
	private static final String ELEMENT_GENTYPE = "genType";
	private static final String ELEMENT_OREGEN = "oreGen";
	private static final String ELEMENT_LASER_DRILL_ORES = "laserDrillOres";
	private static final String ELEMENT_GEODE_ORES = "geodeOres";
	private static final String ELEMENT_CRATER_ORES = "craterOres";
	private static final String ELEMENT_BIOMEIDS = "biomeIds";
	private static final String ELEMENT_ARTIFACT = "artifact";
	private static final String ELEMENT_OCEANBLOCK = "oceanBlock";
	private static final String ELEMENT_FILLERBLOCK = "fillerBlock";
	private static final String ELEMENT_SPAWNABLE = "spawnable";
	private static final String ELEMENT_CRATER_MULTIPLIER = "craterFrequencyMultiplier";
	private static final String ELEMENT_VOLCANO_MULTIPLIER = "volcanoFrequencyMultiplier";
	private static final String ELEMENT_GEODE_MULTIPLIER = "geodefrequencyMultiplier";
	private static final String ELEMENT_CAN_DECORATE = "hasShading";
	private static final String ATTR_WEIGHT = "weight";
	private static final String ATTR_GROUPMIN = "groupMin";
	private static final String ATTR_GROUPMAX = "groupMax";
	private static final String ATTR_NBT = "nbt";
	
	Document doc;
	NodeList currentList;
	int currentNodeIndex;
	int starId;
	int offset;

	HashMap<StellarBody, Integer> maxPlanetNumber = new HashMap<>();
	HashMap<StellarBody, Integer> maxGasPlanetNumber = new HashMap<>();

	public boolean loadFile(File xmlFile) throws IOException {
		DocumentBuilder docBuilder;
		doc = null;
		try {
			docBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
		} catch (ParserConfigurationException e) {
			return false;
		}

		try {
			doc = docBuilder.parse(xmlFile);
		} catch (SAXException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}
	public XMLPlanetLoader() {
		doc = null;
		currentNodeIndex = -1;
		starId=0;
	}

	public boolean isValid() {
		return doc != null;
	}

	public int getMaxNumPlanets(StellarBody body) {
		if(!maxPlanetNumber.containsKey(body)) {
			AdvancedRocketry.logger.warn("Star ID " + body.getId() + " has no entry for numPlanets");
			return 0;
		}
		return maxPlanetNumber.get(body);
	}


	public int getMaxNumGasGiants(StellarBody body) {
		if(!maxGasPlanetNumber.containsKey(body)) {
			AdvancedRocketry.logger.warn("Star ID " + body.getId() + " has no entry for numGasGiants");
			return 0;
		}
		return maxGasPlanetNumber.get(body);
	}

	private List<DimensionProperties> readPlanetFromNode(Node planetNode, StellarBody star) {
		List<DimensionProperties> list = new ArrayList<>();
		Node planetPropertyNode = planetNode.getFirstChild();


		DimensionProperties properties = new DimensionProperties(DimensionManager.getInstance().getNextFreeDim(offset));
		list.add(properties);
		offset++;//Increment for dealing with child planets


		//Set name for dimension if exists
		if(planetNode.hasAttributes()) {
			Node nameNode = planetNode.getAttributes().getNamedItem("name");
			if(nameNode != null && !nameNode.getNodeValue().isEmpty()) {
				properties.setName(nameNode.getNodeValue());
			}

			nameNode = planetNode.getAttributes().getNamedItem(ATTR_DIMID);
			if(nameNode != null && !nameNode.getNodeValue().isEmpty()) {
				try {
					if(nameNode.getTextContent().isEmpty()) throw new NumberFormatException();
					properties.setId(Integer.parseInt(nameNode.getTextContent()));
					//We're not using the offset so decrement to prepare for next planet
					offset--;
				} catch (NumberFormatException e) {
					AdvancedRocketry.logger.warn("Invalid DIMID specified for planet " + properties.getName()); //TODO: more detailed error msg
					list.remove(properties);
					offset--;
					return list;
				}
			}

			nameNode = planetNode.getAttributes().getNamedItem(ATTR_NATIVEDIM);
			if(nameNode != null) {
				properties.isNativeDimension = false;
			}

			nameNode = planetNode.getAttributes().getNamedItem(ATTR_ICON);
			if(nameNode != null) {
				properties.customIcon = nameNode.getTextContent();
			}
		}

		while(planetPropertyNode != null) {
			if(planetPropertyNode.getNodeName().equalsIgnoreCase(ELEMENT_FOGCOLOR)) {
				String[] colors = planetPropertyNode.getTextContent().split(",");
				try {
					if(colors.length >= 3) {
						float[] rgb = new float[3];


						for(int j = 0; j < 3; j++)
							rgb[j] = Float.parseFloat(colors[j]);
						properties.fogColor = rgb;

					}
					else if(colors.length == 1) {
						int cols = Integer.parseUnsignedInt(colors[0].substring(2), 16);
						float[] rgb = new float[3];

						rgb[0] = ((cols >>> 16) & 0xff) / 255f;
						rgb[1] = ((cols >>> 8) & 0xff) / 255f;
						rgb[2] = (cols & 0xff) / 255f;

						properties.fogColor = rgb;
					}
					else
						AdvancedRocketry.logger.warn("Invalid number of floats specified for fog color (Required 3, comma sperated)"); //TODO: more detailed error msg
				} catch (NumberFormatException e) {
					AdvancedRocketry.logger.warn("Invalid fog color specified"); //TODO: more detailed error msg
				}
			}
			else if(planetPropertyNode.getNodeName().equalsIgnoreCase(ELEMENT_GAS)) {
				Fluid fluid = FluidRegistry.getFluid(planetPropertyNode.getTextContent());

				if(fluid == null)
					AdvancedRocketry.logger.warn( "\"" + planetPropertyNode.getTextContent() + "\" is not a valid fluid"); //TODO: more detailed error msg
				else {
					properties.getHarvestableGasses().add(fluid);
				}
			}
			else if(planetPropertyNode.getNodeName().equalsIgnoreCase(ELEMENT_OCEANBLOCK)) {
				String blockName = planetPropertyNode.getTextContent();
				Block block = Block.REGISTRY.getObject(new ResourceLocation(blockName));

				if(block == Blocks.AIR)
					AdvancedRocketry.logger.warn("Invalid ocean block: " + blockName); //TODO: more detailed error msg

				properties.setOceanBlock(block.getDefaultState());
			}
			else if(planetPropertyNode.getNodeName().equalsIgnoreCase(ELEMENT_FILLERBLOCK)) {
				String blockName = planetPropertyNode.getTextContent();
				String[] splitBlockName = blockName.split(":");

				if(splitBlockName.length < 2) {
					AdvancedRocketry.logger.warn("Invalid resource location for fillerBlock: " + blockName);
				}
				else {
					Block block = Block.REGISTRY.getObject(new ResourceLocation(splitBlockName[0],splitBlockName[1]));
					int metaValue = 0;
					
					if(splitBlockName.length > 2) {
						try {
							metaValue = Integer.parseInt(splitBlockName[2]);
						}
						catch(NumberFormatException e) {
							AdvancedRocketry.logger.warn("Invalid meta value location for fillerBlock: " + blockName + " using " + splitBlockName[2] );
						}
					}
					
					if(block == Blocks.AIR)
						AdvancedRocketry.logger.warn("Invalid filler block: " + blockName); //TODO: more detailed error msg

					properties.setStoneBlock(block.getStateFromMeta(metaValue));
				}
			}
			else if(planetPropertyNode.getNodeName().equalsIgnoreCase(ELEMENT_SKYCOLOR)) {
				String[] colors = planetPropertyNode.getTextContent().split(",");
				try {

					if(colors.length >= 3) {
						float[] rgb = new float[3];

						for(int j = 0; j < 3; j++)
							rgb[j] = Float.parseFloat(colors[j]);
						properties.skyColor = rgb;

					}
					else if(colors.length == 1) {
						int cols = Integer.parseUnsignedInt(colors[0].substring(2), 16);
						float[] rgb = new float[3];

						rgb[0] = ((cols >>> 16) & 0xff) / 255f;
						rgb[1] = ((cols >>> 8) & 0xff) / 255f;
						rgb[2] = (cols & 0xff) / 255f;

						properties.skyColor = rgb;
					}
					else
						AdvancedRocketry.logger.warn("Invalid number of floats specified for sky color (Required 3, comma sperated)"); //TODO: more detailed error msg

				} catch (NumberFormatException e) {
					AdvancedRocketry.logger.warn("Invalid sky color specified"); //TODO: more detailed error msg
				}
			}
			else if(planetPropertyNode.getNodeName().equalsIgnoreCase(ELEMENT_HASOXYGEN)) {

				String text = planetPropertyNode.getTextContent();
				if(text != null && !text.isEmpty() && text.equalsIgnoreCase("false"))
					properties.hasOxygen = false;
			}
			else if(planetPropertyNode.getNodeName().equalsIgnoreCase(ELEMENT_ATMDENSITY)) {

				try {
					properties.setAtmosphereDensityDirect(Math.min(Math.max(Integer.parseInt(planetPropertyNode.getTextContent()), DimensionProperties.MIN_ATM_PRESSURE), DimensionProperties.MAX_ATM_PRESSURE));
				} catch (NumberFormatException e) {
					AdvancedRocketry.logger.warn("Invalid atmosphereDensity specified"); //TODO: more detailed error msg
				}
			}
			else if(planetPropertyNode.getNodeName().equalsIgnoreCase(ELEMENT_GRAVITY)) {

				try {
					properties.gravitationalMultiplier = Math.min(Math.max(Integer.parseInt(planetPropertyNode.getTextContent()), DimensionProperties.MIN_GRAVITY), DimensionProperties.MAX_GRAVITY)/100f;
				} catch (NumberFormatException e) {
					AdvancedRocketry.logger.warn("Invalid gravitationalMultiplier specified"); //TODO: more detailed error msg
				}
			}
			else if(planetPropertyNode.getNodeName().equalsIgnoreCase(ELEMENT_DISTANCE)) {

				try {
					properties.orbitalDist = Math.min(Math.max(Integer.parseInt(planetPropertyNode.getTextContent()), DimensionProperties.MIN_DISTANCE), DimensionProperties.MAX_DISTANCE);
				} catch (NumberFormatException e) {
					AdvancedRocketry.logger.warn("Invalid orbitalDist specified"); //TODO: more detailed error msg
				}
			}
			else if(planetPropertyNode.getNodeName().equalsIgnoreCase(ELEMENT_BASEORBITTHETA)) {

				try {
					properties.baseOrbitTheta = ((Integer.parseInt(planetPropertyNode.getTextContent()) + 180) % 360) * Math.PI/180f;
				} catch (NumberFormatException e) {
					AdvancedRocketry.logger.warn("Invalid orbitalTheta specified"); //TODO: more detailed error msg
				}
			}
			else if(planetPropertyNode.getNodeName().equalsIgnoreCase(ELEMENT_RETROGRADE)) {
				String text = planetPropertyNode.getTextContent();
				if(text != null && !text.isEmpty() && text.equalsIgnoreCase("true"))
					properties.isRetrograde = true;
			}
			else if(planetPropertyNode.getNodeName().equalsIgnoreCase(ELEMENT_PERIOD)) {
				try {
					int rotationalPeriod =  Integer.parseInt(planetPropertyNode.getTextContent());
					if(rotationalPeriod > 0)
						properties.rotationalPeriod = rotationalPeriod;
					else
						AdvancedRocketry.logger.warn("rotational Period must be greater than 0 for dimension " + properties.getId()); //TODO: more detailed error msg
				} catch (NumberFormatException e) {
					AdvancedRocketry.logger.warn("Invalid rotational period specified"); //TODO: more detailed error msg
				}
			}
			else if(planetPropertyNode.getNodeName().equalsIgnoreCase(ELEMENT_SEALEVEL)) {
				try {
					properties.setSeaLevel(Integer.parseInt(planetPropertyNode.getTextContent()));
				} catch (NumberFormatException e) {
					AdvancedRocketry.logger.warn("Invalid sealeve specified"); //TODO: more detailed error msg
				}
			}
			else if(planetPropertyNode.getNodeName().equalsIgnoreCase(ELEMENT_BIOMEIDS)) {

				String[] biomeList = planetPropertyNode.getTextContent().split(",");
				for (String s : biomeList) {

					ResourceLocation location = new ResourceLocation(s);
					if (Biome.REGISTRY.containsKey(location)) {
						Biome biome = Biome.REGISTRY.getObject(location);
						if (biome == null || !properties.addBiome(Biome.getIdForBiome(biome)))
							AdvancedRocketry.logger.warn("Error adding " + s); //TODO: more detailed error msg
					} else {
						try {
							int biome = Integer.parseInt(s);

							if (!properties.addBiome(biome))
								AdvancedRocketry.logger.warn(s + " is not a valid biome id"); //TODO: more detailed error msg
						} catch (NumberFormatException e) {
							AdvancedRocketry.logger.warn(s + " is not a valid biome id or name"); //TODO: more detailed error msg
						}
					}
				}
			}
			else if(planetPropertyNode.getNodeName().equalsIgnoreCase(ELEMENT_SPAWNABLE)) {
				int weight = 100;
				int groupMin = 1, groupMax = 1;
				String nbtString = "";
				Node weightNode = planetPropertyNode.getAttributes().getNamedItem(ATTR_WEIGHT);
				Node groupMinNode = planetPropertyNode.getAttributes().getNamedItem(ATTR_GROUPMIN);
				Node groupMaxNode = planetPropertyNode.getAttributes().getNamedItem(ATTR_GROUPMIN);
				Node nbtNode = planetPropertyNode.getAttributes().getNamedItem(ATTR_NBT);

				//Get spawn properties
				if(weightNode != null) {
					try {
						weight = Integer.parseInt(weightNode.getTextContent());
						weight = Math.max(1, weight);
					} catch(NumberFormatException ignored) {
					}
				}
				if(groupMinNode != null) {
					try {
						groupMin = Integer.parseInt(groupMinNode.getTextContent());
						groupMin = Math.max(1, groupMin);
					} catch(NumberFormatException ignored) {
					}
				}
				if(groupMaxNode != null) {
					try {
						groupMax = Integer.parseInt(groupMaxNode.getTextContent());
						groupMax = Math.max(1, groupMax);
					} catch(NumberFormatException ignored) {
					}
				}

				if(nbtNode != null) {
					nbtString = nbtNode.getTextContent();
				}
				
				if (groupMax < groupMin) {
					groupMax = groupMin;
				}

				Class clazz = EntityList.getClass(new ResourceLocation(planetPropertyNode.getTextContent()));

				//If not using string name maybe it's a class name?
				if(clazz == null) {
					try {
						clazz = Class.forName(planetPropertyNode.getTextContent());
						if(clazz != null && !Entity.class.isAssignableFrom(clazz))
							clazz = null;

					} catch (Exception e) {
						//Fail silently
					}
				}

				if(clazz != null) {
					SpawnListEntryNBT entry = new SpawnListEntryNBT(clazz, weight, groupMin, groupMax);
					if(!nbtString.isEmpty())
						try {
							entry.setNbt(nbtString);
						} catch (DOMException e) {
							AdvancedRocketry.logger.fatal("===== Configuration Error!  Please check your save's planetDefs.xml config file =====\n"
									+ e.getLocalizedMessage()
									+ "\nThe following is not valid JSON:\n" + nbtString);
						} catch (NBTException e) {
							AdvancedRocketry.logger.fatal("===== Configuration Error!  Please check your save's planetDefs.xml config file =====\n"
									+ e.getLocalizedMessage()
									+ "\nThe following is not valid NBT data:\n" + nbtString);
						}
						
					properties.getSpawnListEntries().add(entry);
				}
				else
					AdvancedRocketry.logger.warn("Cannot find " + planetPropertyNode.getTextContent() + " while registering entity for planet spawn");



			}
			else if(planetPropertyNode.getNodeName().equalsIgnoreCase(ELEMENT_ARTIFACT)) {
				ItemStack stack = XMLPlanetLoader.getStack(planetPropertyNode.getTextContent());

				if(stack != null)
					properties.getRequiredArtifacts().add(stack);
			}
			else if(planetPropertyNode.getNodeName().equalsIgnoreCase(ELEMENT_PLANET)) {
				List<DimensionProperties> childList = readPlanetFromNode(planetPropertyNode, star);
				if(childList.size() > 0) {
					DimensionProperties child = childList.get(0); // First entry in the list is the child planet
					properties.addChildPlanet(child);
					list.addAll(childList);
				}
			}
			else if(planetPropertyNode.getNodeName().equalsIgnoreCase(ELEMENT_PHI)) {
				try {
					properties.orbitalPhi = (Integer.parseInt(planetPropertyNode.getTextContent()) % 360);
				} catch (NumberFormatException e) {
					AdvancedRocketry.logger.warn("Invalid orbitalPhi specified"); //TODO: more detailed error msg
				}
			}
			else if(planetPropertyNode.getNodeName().equalsIgnoreCase(ELEMENT_OREGEN)) {
				properties.oreProperties = XMLOreLoader.loadOre(planetPropertyNode);
			}
			else if(planetPropertyNode.getNodeName().equalsIgnoreCase(ELEMENT_LASER_DRILL_ORES) && !properties.isGasGiant()) {

				properties.laserDrillOresRaw = planetPropertyNode.getTextContent();

				String[] entries = properties.laserDrillOresRaw.split(",");
				for (String entry : entries) {

					String[] parts = entry.split(";");

					if (OreDictionary.doesOreNameExist(parts[0].trim())) {
						ItemStack item = OreDictionary.getOres(parts[0]).get(0);
						if(parts.length > 1) {
							try {
								item.setCount(Integer.parseInt(parts[1]));
							} catch (NumberFormatException ignored) {}
						}
						properties.laserDrillOres.add(item);
					}
					else if (Item.getByNameOrId(parts[0].trim()) != null) {
						int quantity = 1;
						int damage = 0;
						if(parts.length > 1) {
							try {
								quantity = Integer.parseInt(parts[1]);
							} catch (NumberFormatException ignored) {}
							if (parts.length > 2) {
								try {
									damage = Integer.parseInt(parts[2]);
								} catch (NumberFormatException ignored) {}
							}
						}
						properties.laserDrillOres.add(new ItemStack(Objects.requireNonNull(Item.getByNameOrId(parts[0].trim())),quantity,damage));
					}
					else {
						AdvancedRocketry.logger.warn(parts[0] + " is not a valid OreDictionary name or item ID");
					}
				}
			}
			else if(planetPropertyNode.getNodeName().equalsIgnoreCase(ELEMENT_GEODE_ORES)) {
				String[] entries = planetPropertyNode.getTextContent().split(",");
				properties.geodeOres.addAll(Arrays.stream(entries)
						.filter(e->OreDictionary.doesOreNameExist(e.trim()))
						.collect(Collectors.toSet())
				);
			}
			else if(planetPropertyNode.getNodeName().equalsIgnoreCase(ELEMENT_CRATER_ORES)) {
				String[] entries = planetPropertyNode.getTextContent().split(",");
				properties.craterOres.addAll(Arrays.stream(entries)
						.filter(e->OreDictionary.doesOreNameExist(e.trim()))
						.collect(Collectors.toSet())
				);
			}
			else if(planetPropertyNode.getNodeName().equalsIgnoreCase(ELEMENT_GENTYPE)) {
				try {
					properties.setGenType(Integer.parseInt(planetPropertyNode.getTextContent()));
				} catch (NumberFormatException e) {
					AdvancedRocketry.logger.warn("Invalid generator type specified"); //TODO: more detailed error msg
				}
			}
			else if(planetPropertyNode.getNodeName().equalsIgnoreCase(ELEMENT_HASRINGS))
				properties.hasRings = Boolean.parseBoolean(planetPropertyNode.getTextContent());
			else if(planetPropertyNode.getNodeName().equalsIgnoreCase(ELEMENT_CAN_DECORATE))
				properties.setDecoratoration(Boolean.parseBoolean(planetPropertyNode.getTextContent()));
			else if(planetPropertyNode.getNodeName().equalsIgnoreCase(ELEMENT_RINGCOLOR)) {
				String[] colors = planetPropertyNode.getTextContent().split(",");
				try {

					if(colors.length >= 3) {
						float[] rgb = new float[3];

						for(int j = 0; j < 3; j++)
							rgb[j] = Float.parseFloat(colors[j]);
						properties.ringColor = rgb;

					}
					else if(colors.length == 1) {
						int cols = Integer.parseUnsignedInt(colors[0].substring(2), 16);
						float[] rgb = new float[3];

						rgb[0] = ((cols >>> 16) & 0xff) / 255f;
						rgb[1] = ((cols >>> 8) & 0xff) / 255f;
						rgb[2] = (cols & 0xff) / 255f;

						properties.ringColor = rgb;
					}
					else
						AdvancedRocketry.logger.warn("Invalid number of floats specified for ring color (Required 3, comma sperated)"); //TODO: more detailed error msg

				} catch (NumberFormatException e) {
					AdvancedRocketry.logger.warn("Invalid sky color specified"); //TODO: more detailed error msg
				}
			}
			else if(planetPropertyNode.getNodeName().equalsIgnoreCase(ELEMENT_GASGIANT)) {
				String text = planetPropertyNode.getTextContent();
				if(text != null && !text.isEmpty() && text.equalsIgnoreCase("true"))
					properties.setGasGiant(true);
			}
			else if(planetPropertyNode.getNodeName().equalsIgnoreCase(ELEMENT_ISKNOWN)) {
				String text = planetPropertyNode.getTextContent();
				if(text != null && !text.isEmpty() && text.equalsIgnoreCase("true")) {
					ARConfiguration.getCurrentConfig().initiallyKnownPlanets.add(properties.getId());
				}
			}
			else if(planetPropertyNode.getNodeName().equalsIgnoreCase(GENERATECRATERS)) {
				String text = planetPropertyNode.getTextContent();
				if(text != null && !text.isEmpty()) {
					properties.setGenerateCraters(text.equalsIgnoreCase("true"));
				}
			}
			else if(planetPropertyNode.getNodeName().equalsIgnoreCase(ELEMENT_CRATER_MULTIPLIER)) {
				try {
					properties.setCraterMultiplier(Float.parseFloat(planetPropertyNode.getTextContent()));
				} catch (NumberFormatException e) {
					AdvancedRocketry.logger.warn("Invalid crater multiplier specified, must be a number"); 
				}
			}
			else if(planetPropertyNode.getNodeName().equalsIgnoreCase(ELEMENT_VOLCANO_MULTIPLIER)) {
				try {
					properties.setVolcanoMultiplier(Float.parseFloat(planetPropertyNode.getTextContent()));
				} catch (NumberFormatException e) {
					AdvancedRocketry.logger.warn("Invalid volcano multiplier specified, must be a number"); 
				}
			}
			else if(planetPropertyNode.getNodeName().equalsIgnoreCase(ELEMENT_GEODE_MULTIPLIER)) {
				try {
					properties.setGeodeMultiplier(Float.parseFloat(planetPropertyNode.getTextContent()));
				} catch (NumberFormatException e) {
					AdvancedRocketry.logger.warn("Invalid geode multiplier specified, must be a number"); 
				}
			}
			else if(planetPropertyNode.getNodeName().equalsIgnoreCase(GENERATEGEODES)) {
				String text = planetPropertyNode.getTextContent();
				if(text != null && !text.isEmpty()) {
					properties.setGenerateGeodes(text.equalsIgnoreCase("true"));
				}
			}
			else if(planetPropertyNode.getNodeName().equalsIgnoreCase(GENERATEVOLCANOS)) {
				String text = planetPropertyNode.getTextContent();
				if(text != null && !text.isEmpty()) {
					properties.setGenerateVolcanos(text.equalsIgnoreCase("true"));
				}
			}
			else if(planetPropertyNode.getNodeName().equalsIgnoreCase(GENERATESTRUCTURES)) {
				String text = planetPropertyNode.getTextContent();
				if(text != null && !text.isEmpty()) {
					properties.setGenerateStructures(text.equalsIgnoreCase("true"));
				}
			}
			

			planetPropertyNode = planetPropertyNode.getNextSibling();
		}

		//Star may not be registered at this time, use ID version instead
		properties.setStar(star.getId());

		//Set peak insolation multiplier
		//Assumes that a 16 atmosphere is 16x the partial pressure but not thicker, because I don't want to deal with that and this is fairly simple right now
		//Get what it would be relative to LEO, this gives ~0.76 for Earth at the surface
		double insolationRelativeToLEO = AstronomicalBodyHelper.getStellarBrightness(star, properties.getSolarOrbitalDistance()) * Math.pow(Math.E, -(0.0026899d * properties.getAtmosphereDensity()));
		//Multiply by Earth LEO/Earth Surface for ratio relative to Earth surface (1360/1040)
		properties.peakInsolationMultiplier = insolationRelativeToLEO * 1.308d;
		//Without atmosphere
		properties.peakInsolationMultiplierWithoutAtmosphere = AstronomicalBodyHelper.getStellarBrightness(star, properties.getSolarOrbitalDistance()) * 1.308d;
		
		//Set temperature
		properties.averageTemperature = AstronomicalBodyHelper.getAverageTemperature(star, properties.getSolarOrbitalDistance(), properties.getAtmosphereDensity());

		//If no biomes are specified add some!
		if(properties.getBiomes().isEmpty())
			properties.addBiomes(properties.getViableBiomes());

		return list;
	}


	public StellarBody readStar(Node planetNode) {
		StellarBody star = readSubStar(planetNode);
		if(planetNode.hasAttributes()) {
			Node nameNode;

			nameNode = planetNode.getAttributes().getNamedItem(ATTR_X);

			if(nameNode != null && !nameNode.getNodeValue().isEmpty()) {
				try {
					star.setPosX(Integer.parseInt(nameNode.getNodeValue()));
				} catch (NumberFormatException e) {
					AdvancedRocketry.logger.warn("Error Reading star " + star.getName());
				}
			}

			nameNode = planetNode.getAttributes().getNamedItem(ATTR_Y);

			if(nameNode != null && !nameNode.getNodeValue().isEmpty()) {
				try {
					star.setPosZ(Integer.parseInt(nameNode.getNodeValue()));
				} catch (NumberFormatException e) {
					AdvancedRocketry.logger.warn("Error Reading star " + star.getName());
				}
			}

			nameNode = planetNode.getAttributes().getNamedItem(ATTR_NUMPLANETS);

			try {
				maxPlanetNumber.put(star ,Integer.parseInt(nameNode.getNodeValue()));
			} catch (Exception e) {
				AdvancedRocketry.logger.warn("Invalid number of planets specified in xml config!");
			}

			nameNode = planetNode.getAttributes().getNamedItem(ATTR_NUMGASPLANETS);
			try {
				maxGasPlanetNumber.put(star ,Integer.parseInt(nameNode.getNodeValue()));
			} catch (Exception e) {
				AdvancedRocketry.logger.warn("Invalid number of planets specified in xml config!");
			}
			
			nameNode = planetNode.getAttributes().getNamedItem(ATTR_BLACKHOLE);
			if(nameNode != null && nameNode.getNodeValue().equalsIgnoreCase("true")) {
				star.setBlackHole(true);
			}
		}

		star.setId(starId++);
		return star;
	}

	public StellarBody readSubStar(Node planetNode) {
		StellarBody star = new StellarBody();
		if(planetNode.hasAttributes()) {
			Node nameNode = planetNode.getAttributes().getNamedItem("name");
			if(nameNode != null && !nameNode.getNodeValue().isEmpty()) {
				star.setName(nameNode.getNodeValue());
			}

			nameNode = planetNode.getAttributes().getNamedItem(ATTR_TEMP);

			if(nameNode != null && !nameNode.getNodeValue().isEmpty()) {
				try {
					star.setTemperature(Integer.parseInt(nameNode.getNodeValue()));
				} catch (NumberFormatException e) {
					AdvancedRocketry.logger.warn("Error Reading star " + star.getName());
				}
			}

			nameNode = planetNode.getAttributes().getNamedItem(ATTR_SIZE);
			if(nameNode != null && !nameNode.getNodeValue().isEmpty()) {
				try {
					star.setSize(Float.parseFloat(nameNode.getNodeValue()));
				} catch (NumberFormatException e) {
					AdvancedRocketry.logger.warn("Error Reading star " + star.getName());
				}
			}

			nameNode = planetNode.getAttributes().getNamedItem(ATTR_BLACKHOLE);
			if(nameNode != null && nameNode.getNodeValue().equalsIgnoreCase("true")) {
				star.setBlackHole(true);
			}
			
			nameNode = planetNode.getAttributes().getNamedItem(ATTR_SEPERATION);
			if(nameNode != null && !nameNode.getNodeValue().isEmpty()) {
				try {
					star.setStarSeparation(Float.parseFloat(nameNode.getNodeValue()));
				} catch (NumberFormatException e) {
					AdvancedRocketry.logger.warn("Error Reading star " + star.getName());
				}
			}
		}

		return star;
	}

	public DimensionPropertyCoupling readAllPlanets() {
		DimensionPropertyCoupling coupling = new DimensionPropertyCoupling();

		Node masterNode = doc.getElementsByTagName("galaxy").item(0).getFirstChild();

		//readPlanetFromNode changes value
		//Yes it's hacky but that's another reason why it's private

		offset = DimensionManager.dimOffset;
		while(masterNode != null) {
			if(!masterNode.getNodeName().equals("star")) {
				masterNode = masterNode.getNextSibling();
				continue;
			}

			StellarBody star = readStar(masterNode);
			coupling.stars.add(star);

			NodeList planetNodeList = masterNode.getChildNodes();

			Node planetNode = planetNodeList.item(0);

			while(planetNode != null) {
				if(planetNode.getNodeName().equalsIgnoreCase(ELEMENT_PLANET)) {
					coupling.dims.addAll(readPlanetFromNode(planetNode, star));
				}
				if(planetNode.getNodeName().equalsIgnoreCase("star")) {
					StellarBody star2 = readSubStar(planetNode);
					star.addSubStar(star2);
				}
				planetNode = planetNode.getNextSibling();
			}

			masterNode = masterNode.getNextSibling();
		}
		return coupling;
	}

	public static String writeXML(IGalaxy galaxy) {
		
		Document doc;
		DocumentBuilder docBuilder;
		try {
			docBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
		} catch (ParserConfigurationException e) {
			return "";
		}
		doc = docBuilder.newDocument();
		Element galaxyElement = doc.createElement(ELEMENT_GALAXY);
		doc.appendChild(galaxyElement);
		
		//galaxy.

		Collection<StellarBody> stars = galaxy.getStars();

		for(StellarBody star : stars) {
			Element nodeStar = doc.createElement(ELEMENT_STAR);
			nodeStar.setAttribute(ATTR_BLACKHOLE, Boolean.toString(star.isBlackHole()));
			nodeStar.setAttribute(ATTR_NAME, star.getName());
			nodeStar.setAttribute(ATTR_TEMP, Integer.toString(star.getTemperature()));
			nodeStar.setAttribute(ATTR_X, Integer.toString(star.getPosX()));
			nodeStar.setAttribute(ATTR_Y, Integer.toString(star.getPosZ()));
			nodeStar.setAttribute(ATTR_SIZE, Float.toString(star.getSize()));
			nodeStar.setAttribute(ATTR_NUMPLANETS, "0");
			nodeStar.setAttribute(ATTR_NUMGASPLANETS, "0");
			

			for(StellarBody star2 : star.getSubStars()) {
				Element nodeSubStar = doc.createElement(ELEMENT_STAR);
				
				nodeSubStar.setAttribute(ATTR_BLACKHOLE, Boolean.toString(star2.isBlackHole()));
				nodeSubStar.setAttribute(ATTR_TEMP, Integer.toString(star2.getTemperature()));
				nodeSubStar.setAttribute(ATTR_SIZE, Float.toString(star2.getSize()));
				nodeSubStar.setAttribute(ATTR_SEPERATION, Float.toString(star2.getStarSeparation()));
				nodeStar.appendChild(nodeSubStar);
			}

			for(IDimensionProperties properties : star.getPlanets()) {
				if(!properties.isMoon())
					nodeStar.appendChild(writePlanet(doc, (DimensionProperties)properties));
			}
			
			galaxyElement.appendChild(nodeStar);
		}
		
        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer;
		try {
			transformer = transformerFactory.newTransformer();
		} catch (TransformerConfigurationException e) {
			//TODO: error handling
			return "";
		}
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");
        DOMSource source = new DOMSource(doc);
        
        OutputStream stream = new ByteArrayOutputStream();
        
        StreamResult result = new StreamResult(stream);
        try {
			transformer.transform(source, result);
		} catch (TransformerException e) {
			//TODO: error handling
			e.printStackTrace();
			return "";
		}
		
		return stream.toString();
	}
	
	private static Node createTextNode(Document doc, String nodeName, double nodeText)
	{
		return createTextNode(doc, nodeName, Double.toString(nodeText));
	}
	
	private static Node createTextNode(Document doc, String nodeName, boolean nodeText)
	{
		return createTextNode(doc, nodeName, Boolean.toString(nodeText));
	}
	
	private static Node createTextNode(Document doc, String nodeName, int nodeText)
	{
		return createTextNode(doc, nodeName, Integer.toString(nodeText));
	}
	
	private static Node createTextNode(Document doc, String nodeName, String nodeText)
	{
		Element element = doc.createElement(nodeName);
		element.appendChild(doc.createTextNode(nodeText));
		
		return element;
	}
	
	private static Node writePlanet(Document doc, DimensionProperties properties)
	{
		Element nodePlanet = doc.createElement(ELEMENT_PLANET);
		nodePlanet.setAttribute(ATTR_NAME, properties.getName());
		nodePlanet.setAttribute(ATTR_DIMID, Integer.toString(properties.getId()));
		if(!properties.isNativeDimension)
			nodePlanet.setAttribute(ATTR_NATIVEDIM, "");
		if (!properties.customIcon.isEmpty())
			nodePlanet.setAttribute(ATTR_ICON, properties.customIcon);
		
		nodePlanet.appendChild(createTextNode(doc, ELEMENT_ISKNOWN, Boolean.toString(ARConfiguration.getCurrentConfig().initiallyKnownPlanets.contains(properties.getId()))));
		
		if(properties.hasRings) {
			nodePlanet.appendChild(createTextNode(doc, ELEMENT_HASRINGS, "true"));
			nodePlanet.appendChild(createTextNode(doc, ELEMENT_RINGCOLOR, properties.ringColor[0] + "," + properties.ringColor[1] + "," + properties.ringColor[2]));
		}

		if(!properties.hasOxygen)
		{
			nodePlanet.appendChild(createTextNode(doc, ELEMENT_HASOXYGEN, "false"));
		}

		if(properties.isGasGiant())
		{
			nodePlanet.appendChild(createTextNode(doc, ELEMENT_GASGIANT, "true"));
			
			if(!properties.getHarvestableGasses().isEmpty())
			{
				for(Fluid f : properties.getHarvestableGasses())
				{
					nodePlanet.appendChild(createTextNode(doc, ELEMENT_GAS,  f.getName()));
				}

			}
		}
		
		nodePlanet.appendChild(createTextNode(doc, ELEMENT_FOGCOLOR, properties.fogColor[0] + "," + properties.fogColor[1] + "," + properties.fogColor[2]));
		nodePlanet.appendChild(createTextNode(doc, ELEMENT_SKYCOLOR, properties.skyColor[0] + "," + properties.skyColor[1] + "," + properties.skyColor[2]));
		nodePlanet.appendChild(createTextNode(doc, ELEMENT_GRAVITY, (int)(properties.getGravitationalMultiplier()*100f)));
		nodePlanet.appendChild(createTextNode(doc, ELEMENT_DISTANCE, properties.getOrbitalDist()));
		nodePlanet.appendChild(createTextNode(doc, ELEMENT_BASEORBITTHETA, (int)((properties.baseOrbitTheta * 180f/Math.PI) - 180)));
		nodePlanet.appendChild(createTextNode(doc, ELEMENT_PHI, (int)(properties.orbitalPhi)));
		nodePlanet.appendChild(createTextNode(doc, ELEMENT_RETROGRADE, properties.isRetrograde));
		nodePlanet.appendChild(createTextNode(doc, PEAK_INSOLATION, (properties.peakInsolationMultiplier)));
		nodePlanet.appendChild(createTextNode(doc, PEAK_INSOLATION_WITHOUT_ATMOSPHERE, (properties.peakInsolationMultiplierWithoutAtmosphere)));
		nodePlanet.appendChild(createTextNode(doc, AVG_TEMPERATURE, properties.averageTemperature));
		nodePlanet.appendChild(createTextNode(doc, ELEMENT_PERIOD, properties.rotationalPeriod));
		nodePlanet.appendChild(createTextNode(doc, ELEMENT_ATMDENSITY, properties.getAtmosphereDensity()));
		nodePlanet.appendChild(createTextNode(doc, GENERATECRATERS, properties.canGenerateCraters()));
		nodePlanet.appendChild(createTextNode(doc, GENERATEVOLCANOS, properties.canGenerateVolcanos()));
		nodePlanet.appendChild(createTextNode(doc, GENERATESTRUCTURES, properties.canGenerateStructures()));
		nodePlanet.appendChild(createTextNode(doc, GENERATEGEODES, properties.canGenerateGeodes()));
		
		if(properties.canGenerateCraters() && properties.getCraterMultiplier() == 1)
			nodePlanet.appendChild(createTextNode(doc, ELEMENT_CRATER_MULTIPLIER, properties.getCraterMultiplier()));
		
		if(properties.canGenerateVolcanos() && properties.getVolcanoMultiplier() == 1)
			nodePlanet.appendChild(createTextNode(doc, ELEMENT_VOLCANO_MULTIPLIER, properties.getVolcanoMultiplier()));
		
		if(properties.canGenerateGeodes() && properties.getGeodeMultiplier() == 1)
			nodePlanet.appendChild(createTextNode(doc, ELEMENT_GEODE_MULTIPLIER, properties.getGeodeMultiplier()));
		

		if(properties.getSeaLevel() != 63)
			nodePlanet.appendChild(createTextNode(doc, ELEMENT_SEALEVEL, properties.getSeaLevel()));

		if(properties.getGenType() != 0)
			nodePlanet.appendChild(createTextNode(doc, ELEMENT_GENTYPE, properties.getGenType()));

		if(properties.oreProperties != null) {
			nodePlanet.appendChild(XMLOreLoader.writeOreEntryXML(doc, properties.oreProperties));
		}
		if(properties.laserDrillOresRaw != null) {
			nodePlanet.appendChild(createTextNode(doc, ELEMENT_LASER_DRILL_ORES, properties.laserDrillOresRaw));
		}
		if(!properties.geodeOres.isEmpty()) {
			StringJoiner joiner = new StringJoiner(",");
			for(String ore: properties.geodeOres) {
				joiner.add(ore);
			}
			nodePlanet.appendChild(createTextNode(doc, ELEMENT_GEODE_ORES, joiner.toString()));
		}
		if(!properties.craterOres.isEmpty()) {
			StringJoiner joiner = new StringJoiner(",");
			for(String ore: properties.craterOres) {
				joiner.add(ore);
			}
			nodePlanet.appendChild(createTextNode(doc, ELEMENT_CRATER_ORES, joiner.toString()));
		}

		if(properties.isDecorationOverridden())
			nodePlanet.appendChild(createTextNode(doc, ELEMENT_CAN_DECORATE, properties.hasDecorators()));
		
		if(properties.isNativeDimension && !properties.isGasGiant()) {
			StringBuilder biomeIds = new StringBuilder();
			for(BiomeEntry biome : properties.getBiomes()) {
				try {
					biomeIds.append(",").append(Biome.REGISTRY.getNameForObject(biome.biome).toString());//Biome.getIdForBiome(biome.biome);
				} catch (NullPointerException e) {
					AdvancedRocketry.logger.warn("Error saving biomes for world, biomes list saved may be incomplete.  World: " + properties.getId());
				}
			}
			if(biomeIds.length() > 0)
				biomeIds = new StringBuilder(biomeIds.substring(1));
			else
				AdvancedRocketry.logger.warn("Dim " + properties.getId() + " has no biomes to save!");
			nodePlanet.appendChild(createTextNode(doc, ELEMENT_BIOMEIDS, biomeIds.toString()));
		}

		for(ItemStack stack : properties.getRequiredArtifacts()) {
			nodePlanet.appendChild(createTextNode(doc, ELEMENT_ARTIFACT, stack.getItem().getRegistryName() + " " + stack.getItemDamage() + " " + stack.getCount()));
		}

		for(Integer properties2 : properties.getChildPlanets()) {
			nodePlanet.appendChild(writePlanet(doc, DimensionManager.getInstance().getDimensionProperties(properties2)));
		}

		if(properties.getOceanBlock() != null) {
			nodePlanet.appendChild(createTextNode(doc, ELEMENT_OCEANBLOCK, Block.REGISTRY.getNameForObject(properties.getOceanBlock().getBlock()).toString()));
		}

		if(properties.getStoneBlock() != null) {
			int meta = properties.getStoneBlock().getBlock().getMetaFromState(properties.getStoneBlock());
			if(meta != 0)
				nodePlanet.appendChild(createTextNode(doc, ELEMENT_FILLERBLOCK, Block.REGISTRY.getNameForObject(properties.getStoneBlock().getBlock()) + ":" + meta));
			else
				nodePlanet.appendChild(createTextNode(doc, ELEMENT_FILLERBLOCK, Block.REGISTRY.getNameForObject(properties.getStoneBlock().getBlock()).toString()));
		}

		for(SpawnListEntryNBT e : properties.getSpawnListEntries()) {
			String nbtString = e.getNBTString();
			if (!nbtString.isEmpty())
				nbtString = " nbt=\"" + nbtString.replaceAll("\"", "&quot;") + "\"";
			Element spawnable = doc.createElement(ELEMENT_SPAWNABLE);
			spawnable.setAttribute(ATTR_WEIGHT, Integer.toString(e.itemWeight));
			spawnable.setAttribute(ATTR_GROUPMIN, Integer.toString(e.minGroupCount));
			spawnable.setAttribute(ATTR_GROUPMAX, Integer.toString(e.maxGroupCount));
			spawnable.setAttribute(ATTR_NBT, nbtString.replaceAll("\"", "&quot;"));
			
			spawnable.appendChild(doc.createTextNode(EntityRegistry.getEntry(e.entityClass).getRegistryName().toString()));
			
			nodePlanet.appendChild(spawnable);
		}
		
		return nodePlanet;
	}
	
	public static class DimensionPropertyCoupling {

		public List<StellarBody> stars = new LinkedList<>();
		public List<DimensionProperties> dims = new LinkedList<>();

	}

	@Nonnull
	public static ItemStack getStack(String text) {
		String[] splitStr = text.split(" ");
		int meta = 0;
		int size = 1;
		//format: "name meta size"
		if(splitStr.length > 1) {
			try {
				meta = Integer.parseInt(splitStr[1]);
			} catch( NumberFormatException ignored) {}

			if(splitStr.length > 2)
			{
				try {
					size = Integer.parseInt(splitStr[2]);
				} catch( NumberFormatException ignored) {}
			}
		}

		ItemStack stack = ItemStack.EMPTY;
		Block block = Block.getBlockFromName(splitStr[0]);
		if(block == null) {
			Item item = Item.getByNameOrId(splitStr[0]);
			if(item != null)
				stack = new ItemStack(item, size, meta);
		}
		else
			stack = new ItemStack(block, size, meta);

		return stack;
	}
}
