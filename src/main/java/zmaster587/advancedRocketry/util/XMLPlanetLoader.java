package zmaster587.advancedRocketry.util;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import zmaster587.advancedRocketry.AdvancedRocketry;
import zmaster587.advancedRocketry.dimension.DimensionManager;
import zmaster587.advancedRocketry.dimension.DimensionProperties;

public class XMLPlanetLoader {

	Document doc;
	NodeList currentList;
	int currentNodeIndex;

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
	}

	public boolean isValid() {
		return doc != null;
	}

	public int getMaxNumPlanets() {
		Node node = doc.getElementsByTagName("planets").item(0);
		int returnValue = -1;

		if(node.hasAttributes()) {
			NamedNodeMap map = node.getAttributes();
			Node attr = map.getNamedItem("numPlanets");

			try {
				returnValue= Integer.parseInt(attr.getNodeValue());
			} catch (NumberFormatException e) {
				AdvancedRocketry.logger.warning("Invalid number of planets specified in xml config!");
			}
		}
		return returnValue;
	}
	

	public int getMaxNumGasGiants() {
		Node node = doc.getElementsByTagName("planets").item(0);
		int returnValue = -1;

		if(node.hasAttributes()) {
			NamedNodeMap map = node.getAttributes();
			Node attr = map.getNamedItem("numGasGiants");

			try {
				returnValue= Integer.parseInt(attr.getNodeValue());
			} catch (NumberFormatException e) {
				AdvancedRocketry.logger.warning("Invalid number of planets specified in xml config!");
			}
		}
		return returnValue;
	}

	private List<DimensionProperties> readPlanetFromNode(Node planetNode) {
		List<DimensionProperties> list = new ArrayList<DimensionProperties>();
		Node planetPropertyNode = planetNode.getFirstChild();
		

		DimensionProperties properties = new DimensionProperties(DimensionManager.getInstance().getNextFreeDim());
		properties.setStar(DimensionManager.getSol());
		list.add(properties);
		DimensionManager.dimOffset++;//Increment for dealing with child planets
		

		//Set name for dimension if exists
		if(planetNode.hasAttributes()) {
			Node nameNode = planetNode.getAttributes().getNamedItem("name");
			if(nameNode != null && !nameNode.getNodeValue().isEmpty()) {
				properties.setName(nameNode.getNodeValue());
			}
		}

		while(planetPropertyNode != null) {
			if(planetPropertyNode.getNodeName().equalsIgnoreCase("fogcolor")) {
				String[] colors = planetPropertyNode.getTextContent().split(",");
				if(colors.length >= 3) {
					float rgb[] = new float[3];

					try {
						for(int j = 0; j < 3; j++)
							rgb[j] = Float.parseFloat(colors[j]);
						properties.fogColor = rgb;
					} catch (NumberFormatException e) {
						AdvancedRocketry.logger.warning("Invalid fog color specified"); //TODO: more detailed error msg
					}
				}
				else
					AdvancedRocketry.logger.warning("Invalid number of floats specified for fog color (Required 3, comma sperated)"); //TODO: more detailed error msg
			}
			else if(planetPropertyNode.getNodeName().equalsIgnoreCase("skycolor")) {
				String[] colors = planetPropertyNode.getTextContent().split(",");
				if(colors.length >= 3) {
					float rgb[] = new float[3];

					try {
						for(int j = 0; j < 3; j++)
							rgb[j] = Float.parseFloat(colors[j]);
						properties.skyColor = rgb;
					} catch (NumberFormatException e) {
						AdvancedRocketry.logger.warning("Invalid sky color specified"); //TODO: more detailed error msg
					}
				}
				else
					AdvancedRocketry.logger.warning("Invalid number of floats specified for sky color (Required 3, comma sperated)"); //TODO: more detailed error msg
			}
			else if(planetPropertyNode.getNodeName().equalsIgnoreCase("atmosphereDensity")) {

				try {
					properties.setAtmosphereDensityDirect(Math.min(Math.max(Integer.parseInt(planetPropertyNode.getTextContent()), DimensionProperties.MIN_ATM_PRESSURE), DimensionProperties.MAX_ATM_PRESSURE));
				} catch (NumberFormatException e) {
					AdvancedRocketry.logger.warning("Invalid atmosphereDensity specified"); //TODO: more detailed error msg
				}
			}
			else if(planetPropertyNode.getNodeName().equalsIgnoreCase("gravitationalmultiplier")) {

				try {
					properties.gravitationalMultiplier = Math.min(Math.max(Integer.parseInt(planetPropertyNode.getTextContent()), DimensionProperties.MIN_GRAVITY), DimensionProperties.MAX_GRAVITY)/100f;
				} catch (NumberFormatException e) {
					AdvancedRocketry.logger.warning("Invalid gravitationalMultiplier specified"); //TODO: more detailed error msg
				}
			}
			else if(planetPropertyNode.getNodeName().equalsIgnoreCase("orbitaldistance")) {

				try {
					properties.orbitalDist = Math.min(Math.max(Integer.parseInt(planetPropertyNode.getTextContent()), DimensionProperties.MIN_DISTANCE), DimensionProperties.MAX_DISTANCE);
				} catch (NumberFormatException e) {
					AdvancedRocketry.logger.warning("Invalid orbitalDist specified"); //TODO: more detailed error msg
				}
			}
			else if(planetPropertyNode.getNodeName().equalsIgnoreCase("orbitaltheta")) {

				try {
					properties.orbitTheta = (Integer.parseInt(planetPropertyNode.getTextContent()) % 360) * 2/Math.PI;
				} catch (NumberFormatException e) {
					AdvancedRocketry.logger.warning("Invalid orbitalTheta specified"); //TODO: more detailed error msg
				}
			}
			else if(planetPropertyNode.getNodeName().equalsIgnoreCase("rotationalperiod")) {
				try {
					int rotationalPeriod =  Integer.parseInt(planetPropertyNode.getTextContent());
					if(properties.rotationalPeriod > 0)
						properties.rotationalPeriod = rotationalPeriod;
					else
						AdvancedRocketry.logger.warning("rotational Period must be greater than 0"); //TODO: more detailed error msg
				} catch (NumberFormatException e) {
					AdvancedRocketry.logger.warning("Invalid rotational period specified"); //TODO: more detailed error msg
				}
			}
			else if(planetPropertyNode.getNodeName().equalsIgnoreCase("biomeids")) {

				String biomeList[] = planetPropertyNode.getTextContent().split(",");
				for(int j = 0; j < biomeList.length; j++) {
					try {
						int biome =  Integer.parseInt(biomeList[j]);

						if(!properties.addBiome(biome))
							AdvancedRocketry.logger.warning(biomeList[j] + " is not a valid biome id"); //TODO: more detailed error msg
					} catch (NumberFormatException e) {
						AdvancedRocketry.logger.warning(biomeList[j] + " is not a valid biome id"); //TODO: more detailed error msg
					}
				}
			}
			else if(planetPropertyNode.getNodeName().equalsIgnoreCase("planet")) {
				List<DimensionProperties> childList = readPlanetFromNode(planetPropertyNode);
				if(childList.size() > 0) {
					DimensionProperties child = childList.get(childList.size()-1); // Last entry in the list is the child planet
					properties.addChildPlanet(child);
					list.addAll(childList);
				}
			}

			planetPropertyNode = planetPropertyNode.getNextSibling();
		}

		//If no biomes are specified add some!
		if(properties.getBiomes().isEmpty())
			properties.addBiomes(properties.getViableBiomes());
		
		return list;
	}

	public List<DimensionProperties> readAllPlanets() {
		List<DimensionProperties> list = new ArrayList<DimensionProperties>();

		Node masterNode = doc.getElementsByTagName("planets").item(0);
		NodeList planetNodeList = masterNode.getChildNodes();

		Node planetNode = planetNodeList.item(0);
		//readPlanetFromNode changes value
		//Yes it's hacky but that's another reason why it's private
		int offset = DimensionManager.dimOffset;
		
		while(planetNode != null) {
			if(planetNode.getNodeName().equalsIgnoreCase("planet"))
				list.addAll(readPlanetFromNode(planetNode));
			planetNode = planetNode.getNextSibling();
		}
		
		DimensionManager.dimOffset = offset; //Set back to its prev value
		
		return list;
	}
}
