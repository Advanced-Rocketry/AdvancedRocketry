package zmaster587.advancedRocketry.util;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.BiomeManager.BiomeEntry;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import zmaster587.advancedRocketry.AdvancedRocketry;
import zmaster587.advancedRocketry.api.Configuration;
import zmaster587.advancedRocketry.api.dimension.IDimensionProperties;
import zmaster587.advancedRocketry.api.dimension.solar.IGalaxy;
import zmaster587.advancedRocketry.api.dimension.solar.StellarBody;
import zmaster587.advancedRocketry.dimension.DimensionManager;
import zmaster587.advancedRocketry.dimension.DimensionProperties;

public class XMLPlanetLoader {

	Document doc;
	NodeList currentList;
	int currentNodeIndex;
	int starId;
	int offset;

	HashMap<StellarBody, Integer> maxPlanetNumber = new HashMap<StellarBody, Integer>();
	HashMap<StellarBody, Integer> maxGasPlanetNumber = new HashMap<StellarBody, Integer>();

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
		return maxPlanetNumber.get(body);
	}


	public int getMaxNumGasGiants(StellarBody body) {
		return maxGasPlanetNumber.get(body);
	}

	private List<DimensionProperties> readPlanetFromNode(Node planetNode, StellarBody star) {
		List<DimensionProperties> list = new ArrayList<DimensionProperties>();
		Node planetPropertyNode = planetNode.getFirstChild();


		DimensionProperties properties = new DimensionProperties(DimensionManager.getInstance().getNextFreeDim(offset));

		if(properties == null)
			return list;
		list.add(properties);
		offset++;//Increment for dealing with child planets


		//Set name for dimension if exists
		if(planetNode.hasAttributes()) {
			Node nameNode = planetNode.getAttributes().getNamedItem("name");
			if(nameNode != null && !nameNode.getNodeValue().isEmpty()) {
				properties.setName(nameNode.getNodeValue());
			}

			nameNode = planetNode.getAttributes().getNamedItem("DIMID");
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

			nameNode = planetNode.getAttributes().getNamedItem("dimMapping");
			if(nameNode != null) {
				properties.isNativeDimension = false;
			}

			nameNode = planetNode.getAttributes().getNamedItem("customIcon");
			if(nameNode != null) {
				properties.customIcon = nameNode.getTextContent();
			}
		}

		while(planetPropertyNode != null) {
			if(planetPropertyNode.getNodeName().equalsIgnoreCase("fogcolor")) {
				String[] colors = planetPropertyNode.getTextContent().split(",");
				try {
					if(colors.length >= 3) {
						float rgb[] = new float[3];


						for(int j = 0; j < 3; j++)
							rgb[j] = Float.parseFloat(colors[j]);
						properties.fogColor = rgb;

					}
					else if(colors.length == 1) {
						int cols = Integer.parseUnsignedInt(colors[0].substring(2), 16);
						float rgb[] = new float[3];

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
			else if(planetPropertyNode.getNodeName().equalsIgnoreCase("gas")) {
				Fluid f = FluidRegistry.getFluid(planetPropertyNode.getTextContent());
				
				if(f == null)
					AdvancedRocketry.logger.warn( "\"" + planetPropertyNode.getTextContent() + "\" is not a valid fluid"); //TODO: more detailed error msg
				else {
					properties.getHarvestableGasses().add(f);
				}
			}
			else if(planetPropertyNode.getNodeName().equalsIgnoreCase("oceanBlock")) {
				String blockName = planetPropertyNode.getTextContent();
				Block block = (Block) Block.blockRegistry.getObject(blockName);
				
				if(block == Blocks.air || block == null)
					AdvancedRocketry.logger.warn("Invalid ocean block: " + blockName); //TODO: more detailed error msg
				
				properties.setOceanBlock(block);
			}
			else if(planetPropertyNode.getNodeName().equalsIgnoreCase("fillerBlock")) {
				String blockName = planetPropertyNode.getTextContent();
				Block block = (Block) Block.blockRegistry.getObject(blockName);
				
				if(block == Blocks.air || block == null)
					AdvancedRocketry.logger.warn("Invalid ocean block: " + blockName); //TODO: more detailed error msg
				
				properties.setStoneBlock(block);
			}
			else if(planetPropertyNode.getNodeName().equalsIgnoreCase("skycolor")) {
				String[] colors = planetPropertyNode.getTextContent().split(",");
				try {

					if(colors.length >= 3) {
						float rgb[] = new float[3];

						for(int j = 0; j < 3; j++)
							rgb[j] = Float.parseFloat(colors[j]);
						properties.skyColor = rgb;

					}
					else if(colors.length == 1) {
						int cols = Integer.parseUnsignedInt(colors[0].substring(2), 16);
						float rgb[] = new float[3];

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
			else if(planetPropertyNode.getNodeName().equalsIgnoreCase("atmosphereDensity")) {

				try {
					properties.setAtmosphereDensityDirect(Math.min(Math.max(Integer.parseInt(planetPropertyNode.getTextContent()), DimensionProperties.MIN_ATM_PRESSURE), DimensionProperties.MAX_ATM_PRESSURE));
				} catch (NumberFormatException e) {
					AdvancedRocketry.logger.warn("Invalid atmosphereDensity specified"); //TODO: more detailed error msg
				}
			}
			else if(planetPropertyNode.getNodeName().equalsIgnoreCase("gravitationalmultiplier")) {

				try {
					properties.gravitationalMultiplier = Math.min(Math.max(Integer.parseInt(planetPropertyNode.getTextContent()), DimensionProperties.MIN_GRAVITY), DimensionProperties.MAX_GRAVITY)/100f;
				} catch (NumberFormatException e) {
					AdvancedRocketry.logger.warn("Invalid gravitationalMultiplier specified"); //TODO: more detailed error msg
				}
			}
			else if(planetPropertyNode.getNodeName().equalsIgnoreCase("orbitaldistance")) {

				try {
					properties.orbitalDist = Math.min(Math.max(Integer.parseInt(planetPropertyNode.getTextContent()), DimensionProperties.MIN_DISTANCE), DimensionProperties.MAX_DISTANCE);
				} catch (NumberFormatException e) {
					AdvancedRocketry.logger.warn("Invalid orbitalDist specified"); //TODO: more detailed error msg
				}
			}
			else if(planetPropertyNode.getNodeName().equalsIgnoreCase("orbitaltheta")) {

				try {
					properties.orbitTheta = (Integer.parseInt(planetPropertyNode.getTextContent()) % 360) * 2/Math.PI;
				} catch (NumberFormatException e) {
					AdvancedRocketry.logger.warn("Invalid orbitalTheta specified"); //TODO: more detailed error msg
				}
			}
			else if(planetPropertyNode.getNodeName().equalsIgnoreCase("rotationalperiod")) {
				try {
					int rotationalPeriod =  Integer.parseInt(planetPropertyNode.getTextContent());
					if(properties.rotationalPeriod > 0)
						properties.rotationalPeriod = rotationalPeriod;
					else
						AdvancedRocketry.logger.warn("rotational Period must be greater than 0"); //TODO: more detailed error msg
				} catch (NumberFormatException e) {
					AdvancedRocketry.logger.warn("Invalid rotational period specified"); //TODO: more detailed error msg
				}
			}
			else if(planetPropertyNode.getNodeName().equalsIgnoreCase("seaLevel")) {
				try {
					properties.setSeaLevel(Integer.parseInt(planetPropertyNode.getTextContent()));
				} catch (NumberFormatException e) {
					AdvancedRocketry.logger.warn("Invalid sealevel specified"); //TODO: more detailed error msg
				}
			}
			else if(planetPropertyNode.getNodeName().equalsIgnoreCase("biomeids")) {

				String biomeList[] = planetPropertyNode.getTextContent().split(",");
				for(int j = 0; j < biomeList.length; j++) {
					try {
						int biome =  Integer.parseInt(biomeList[j]);

						if(!properties.addBiome(biome))
							AdvancedRocketry.logger.warn(biomeList[j] + " is not a valid biome id"); //TODO: more detailed error msg
					} catch (NumberFormatException e) {
						AdvancedRocketry.logger.warn(biomeList[j] + " is not a valid biome id"); //TODO: more detailed error msg
					}
				}
			}
			else if(planetPropertyNode.getNodeName().equalsIgnoreCase("artifact")) {
				ItemStack stack = XMLAsteroidLoader.getStack(planetPropertyNode.getTextContent());

				if(stack != null)
					properties.getRequiredArtifacts().add(stack);
			}
			else if(planetPropertyNode.getNodeName().equalsIgnoreCase("planet")) {
				List<DimensionProperties> childList = readPlanetFromNode(planetPropertyNode, star);
				if(childList.size() > 0) {
					DimensionProperties child = childList.get(childList.size()-1); // Last entry in the list is the child planet
					properties.addChildPlanet(child);
					list.addAll(childList);
				}
			}
			else if(planetPropertyNode.getNodeName().equalsIgnoreCase("orbitalPhi")) {
				try {
					properties.orbitalPhi = (Integer.parseInt(planetPropertyNode.getTextContent()) % 360) * 180/Math.PI;
				} catch (NumberFormatException e) {
					AdvancedRocketry.logger.warn("Invalid orbitalTheta specified"); //TODO: more detailed error msg
				}
			}
			else if(planetPropertyNode.getNodeName().equalsIgnoreCase("oreGen")) {
				properties.oreProperties = XMLOreLoader.loadOre(planetPropertyNode);
			}
			else if(planetPropertyNode.getNodeName().equalsIgnoreCase("genType")) {
				try {
					properties.setGenType(Integer.parseInt(planetPropertyNode.getTextContent()));
				} catch (NumberFormatException e) {
					AdvancedRocketry.logger.warn("Invalid generator type specified"); //TODO: more detailed error msg
				}
			}
			else if(planetPropertyNode.getNodeName().equalsIgnoreCase("hasRings"))
				properties.hasRings = Boolean.parseBoolean(planetPropertyNode.getTextContent());
			else if(planetPropertyNode.getNodeName().equalsIgnoreCase("ringColor")) {
				String[] colors = planetPropertyNode.getTextContent().split(",");
				try {

					if(colors.length >= 3) {
						float rgb[] = new float[3];

						for(int j = 0; j < 3; j++)
							rgb[j] = Float.parseFloat(colors[j]);
						properties.ringColor = rgb;

					}
					else if(colors.length == 1) {
						int cols = Integer.parseUnsignedInt(colors[0].substring(2), 16);
						float rgb[] = new float[3];

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
			else if(planetPropertyNode.getNodeName().equalsIgnoreCase("GasGiant")) {
				String text = planetPropertyNode.getTextContent();
				if(text != null && !text.isEmpty() && text.equalsIgnoreCase("true"))
					properties.setGasGiant();
			}
			else if(planetPropertyNode.getNodeName().equalsIgnoreCase("isKnown")) {
				String text = planetPropertyNode.getTextContent();
				if(text != null && !text.isEmpty() && text.equalsIgnoreCase("true")) {
					Configuration.initiallyKnownPlanets.add(properties.getId());
				}
			}

			planetPropertyNode = planetPropertyNode.getNextSibling();
		}

		//Star may not be registered at this time, use ID version instead
		properties.setStar(star.getId());

		//Set temperature
		properties.averageTemperature = DimensionManager.getInstance().getTemperature(star, properties.getOrbitalDist(), properties.getAtmosphereDensity());

		//If no biomes are specified add some!
		if(properties.getBiomes().isEmpty())
			properties.addBiomes(properties.getViableBiomes());

		return list;
	}


	public StellarBody readStar(Node planetNode) {
		StellarBody star = readSubStar(planetNode);
		if(planetNode.hasAttributes()) {
			Node nameNode;

			nameNode = planetNode.getAttributes().getNamedItem("x");

			if(nameNode != null && !nameNode.getNodeValue().isEmpty()) {
				try {
					star.setPosX(Integer.parseInt(nameNode.getNodeValue()));
				} catch (NumberFormatException e) {
					AdvancedRocketry.logger.warn("Error Reading star " + star.getName());
				}
			}

			nameNode = planetNode.getAttributes().getNamedItem("y");

			if(nameNode != null && !nameNode.getNodeValue().isEmpty()) {
				try {
					star.setPosZ(Integer.parseInt(nameNode.getNodeValue()));
				} catch (NumberFormatException e) {
					AdvancedRocketry.logger.warn("Error Reading star " + star.getName());
				}
			}

			nameNode = planetNode.getAttributes().getNamedItem("numPlanets");

			try {
				maxPlanetNumber.put(star ,Integer.parseInt(nameNode.getNodeValue()));
			} catch (Exception e) {
				AdvancedRocketry.logger.warn("Invalid number of planets specified in xml config!");
			}

			nameNode = planetNode.getAttributes().getNamedItem("numGasGiants");
			try {
				maxGasPlanetNumber.put(star ,Integer.parseInt(nameNode.getNodeValue()));
			} catch (Exception e) {
				AdvancedRocketry.logger.warn("Invalid number of planets specified in xml config!");
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

			nameNode = planetNode.getAttributes().getNamedItem("temp");

			if(nameNode != null && !nameNode.getNodeValue().isEmpty()) {
				try {
					star.setTemperature(Integer.parseInt(nameNode.getNodeValue()));
				} catch (NumberFormatException e) {
					AdvancedRocketry.logger.warn("Error Reading star " + star.getName());
				}
			}
			
			nameNode = planetNode.getAttributes().getNamedItem("size");
			if(nameNode != null && !nameNode.getNodeValue().isEmpty()) {
				try {
					star.setSize(Float.parseFloat(nameNode.getNodeValue()));
				} catch (NumberFormatException e) {
					AdvancedRocketry.logger.warn("Error Reading star " + star.getName());
				}
			}
			
			nameNode = planetNode.getAttributes().getNamedItem("seperation");
			if(nameNode != null && !nameNode.getNodeValue().isEmpty()) {
				try {
					star.setStarSeperation(Float.parseFloat(nameNode.getNodeValue()));
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
				if(planetNode.getNodeName().equalsIgnoreCase("planet")) {
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
		//galaxy.
		String outputString = "<galaxy>\n";

		Collection<StellarBody> stars = galaxy.getStars();

		for(StellarBody star : stars) {
			outputString = outputString + "\t<star name=\"" + star.getName() + "\" temp=\"" + star.getTemperature() + "\" x=\"" + star.getPosX() 
					+ "\" y=\"" + star.getPosZ() + "\" size=\"" + star.getSize() + "\" numPlanets=\"0\" numGasGiants=\"0\">\n";

			for(StellarBody star2 : star.getSubStars()) {
				outputString = outputString + "\t\t<star temp=\"" + star2.getTemperature() + 
						"\" size=\"" + star2.getSize() + "\" seperation=\"" + star2.getStarSeperation() + "\" />\n";

			}
			
			for(IDimensionProperties properties : star.getPlanets()) {
				if(!properties.isMoon())
					outputString = outputString + writePlanet((DimensionProperties)properties, 2);
			}

			outputString = outputString + "\t</star>\n";
		}

		outputString = outputString + "</galaxy>";

		return outputString;
	}

	private static String writePlanet(DimensionProperties properties, int numTabs) {
		String outputString = "";
		String tabLen = "";

		for(int i = 0; i < numTabs; i++) {
			tabLen += "\t";
		}

		outputString = tabLen + "<planet name=\"" + properties.getName() + "\" DIMID=\"" + properties.getId() + "\"" +
				(properties.isNativeDimension ? "" : " dimMapping=\"\"") + 
				(properties.customIcon.isEmpty() ? "" : " customIcon=\"" + properties.customIcon + "\"") + ">\n";


		outputString = outputString + tabLen + "\t<isKnown>" + Configuration.initiallyKnownPlanets.contains(properties.getId()) + "</isKnown>\n";	
		if(properties.hasRings) {
			outputString = outputString + tabLen + "\t<hasRings>true</hasRings>\n";
			outputString = outputString + tabLen + "\t<ringColor>" + properties.ringColor[0] + "," + properties.ringColor[1] + "," + properties.ringColor[2] + "</ringColor>\n";
		}

		if(properties.isGasGiant())
		{
			outputString = outputString + tabLen + "\t<GasGiant>true</GasGiant>\n";
			if(!properties.getHarvestableGasses().isEmpty())
			{
				for(Fluid f : properties.getHarvestableGasses())
				{
					outputString = outputString + tabLen + "\t<gas>" + f.getName() + "</gas>\n";
				}
				
			}
		}

		outputString = outputString + tabLen + "\t<fogColor>" + properties.fogColor[0] + "," + properties.fogColor[1] + "," + properties.fogColor[2] + "</fogColor>\n";
		outputString = outputString + tabLen + "\t<skyColor>" + properties.skyColor[0] + "," + properties.skyColor[1] + "," + properties.skyColor[2] + "</skyColor>\n";
		outputString = outputString + tabLen + "\t<gravitationalMultiplier>" + (int)(properties.getGravitationalMultiplier()*100f) + "</gravitationalMultiplier>\n";
		outputString = outputString + tabLen + "\t<orbitalDistance>" + properties.getOrbitalDist() + "</orbitalDistance>\n";
		outputString = outputString + tabLen + "\t<orbitalPhi>" + (int)(properties.orbitalPhi* Math.PI/180) + "</orbitalPhi>\n";
		outputString = outputString + tabLen + "\t<rotationalPeriod>" + (int)properties.rotationalPeriod + "</rotationalPeriod>\n";
		outputString = outputString + tabLen + "\t<atmosphereDensity>" + (int)properties.getAtmosphereDensity() + "</atmosphereDensity>\n";
		outputString = outputString + tabLen + "\t<seaLevel>" + properties.getSeaLevel() + "</seaLevel>\n";
		outputString = outputString + tabLen + "\t<genType>" + properties.getGenType() + "</genType>\n";
		
		if(properties.oreProperties != null) {
			outputString = outputString + tabLen + "\t<oreGen>\n";
			outputString = outputString + XMLOreLoader.writeOreEntryXML(properties.oreProperties, numTabs+2);
			outputString = outputString + tabLen + "\t</oreGen>\n";
		}
		
		if(properties.isNativeDimension && !properties.isGasGiant()) {
			String biomeIds = "";
			for(BiomeEntry biome : properties.getBiomes()) {
				biomeIds = biomeIds + "," + biome.biome.biomeID;
			}
			if(!biomeIds.isEmpty())
				biomeIds = biomeIds.substring(1);
			else
				AdvancedRocketry.logger.warn("Dim " + properties.getId() + " has no biomes to save!");
			
			outputString = outputString + tabLen + "\t<biomeIds>" + biomeIds + "</biomeIds>\n";
		}

		for(ItemStack stack : properties.getRequiredArtifacts()) {
			outputString = outputString + tabLen + "\t<artifact>" + Item.itemRegistry.getNameForObject(stack.getItem()) + " " + stack.getItemDamage() + "</artifact>\n";
		}
		
		for(Integer properties2 : properties.getChildPlanets()) {
			outputString = outputString + writePlanet(DimensionManager.getInstance().getDimensionProperties(properties2), numTabs+1);
		}

		if(properties.getOceanBlock() != null) {
			outputString = outputString + tabLen + "\t<oceanBlock>" + Block.blockRegistry.getNameForObject(properties.getOceanBlock()) + "</oceanBlock>\n";
		}
		
		if(properties.getStoneBlock() != null) {
			outputString = outputString + tabLen + "\t<fillerBlock>" + Block.blockRegistry.getNameForObject(properties.getStoneBlock()) + "</fillerBlock>\n";
		}
		
		outputString = outputString + tabLen + "</planet>\n";
		return outputString;
	}

	public static class DimensionPropertyCoupling {

		public List<StellarBody> stars = new LinkedList<StellarBody>();
		public List<DimensionProperties> dims = new LinkedList<DimensionProperties>();


	}
}
