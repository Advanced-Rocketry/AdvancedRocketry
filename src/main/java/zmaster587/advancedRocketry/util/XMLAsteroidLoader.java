package zmaster587.advancedRocketry.util;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;
import zmaster587.advancedRocketry.AdvancedRocketry;

import org.jetbrains.annotations.NotNull;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

public class XMLAsteroidLoader {

	private Document doc;

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

	public XMLAsteroidLoader() {
		doc = null;
	}

	/**
	 * Load the property file looking for combinations of temp and pressure
	 * @return  list of singleEntry (order MUST be preserved)
	 */
	public List<Asteroid> loadPropertyFile() {
		Node childNode = doc.getFirstChild().getFirstChild();
		List<Asteroid> mapping = new LinkedList<>();

		while(childNode != null) {

			if(childNode.getNodeType() != Node.ELEMENT_NODE || !childNode.getNodeName().equalsIgnoreCase("asteroid")) { 
				childNode = childNode.getNextSibling();
				continue;
			}

			Asteroid asteroid = new Asteroid();

			if(childNode.hasAttributes()) {
				NamedNodeMap att = childNode.getAttributes();

				Node node = att.getNamedItem("name");

				if(node != null) {
					asteroid.ID = node.getTextContent();
				}

				node = att.getNamedItem("distance");
				if(node != null) {
					try {
						asteroid.distance = Integer.parseInt(node.getTextContent());
					} catch(NumberFormatException e) {
						AdvancedRocketry.logger.warn("Asteroid " + asteroid.ID + " has invalid distance value");
					}
				}

				node = att.getNamedItem("mass");
				if(node != null) {
					try {
						asteroid.mass = Integer.parseInt(node.getTextContent());
					} catch(NumberFormatException e) {
						AdvancedRocketry.logger.warn("Asteroid " + asteroid.ID + " has invalid mass value");
					}
				}

				node = att.getNamedItem("minLevel");
				if(node != null) {
					try {
						asteroid.minLevel = Integer.parseInt(node.getTextContent());
					} catch(NumberFormatException e) {
						AdvancedRocketry.logger.warn("Asteroid " + asteroid.ID + " has invalid minLevel value");
					}
				}

				node = att.getNamedItem("massVariability");
				if(node != null) {
					try {
						asteroid.massVariability = Float.parseFloat(node.getTextContent());
					} catch(NumberFormatException e) {
						AdvancedRocketry.logger.warn("Asteroid " + asteroid.ID + " has invalid massVariability value");
					}
				}

				node = att.getNamedItem("richness");
				if(node != null) {
					try {
						asteroid.richness = Float.parseFloat(node.getTextContent());
					} catch(NumberFormatException e) {
						AdvancedRocketry.logger.warn("Asteroid " + asteroid.ID + " has invalid richness value");
					}
				}

				node = att.getNamedItem("richnessVariability");
				if(node != null) {
					try {
						asteroid.richnessVariability = Float.parseFloat(node.getTextContent());
					} catch(NumberFormatException e) {
						AdvancedRocketry.logger.warn("Asteroid " + asteroid.ID + " has invalid richnessVariability value");
					}
				}

				node = att.getNamedItem("probability");
				if(node != null) {
					try {
						asteroid.probability = Float.parseFloat(node.getTextContent());
					} catch(NumberFormatException e) {
						AdvancedRocketry.logger.warn("Asteroid " + asteroid.ID + " has invalid probability value");
					}
				}
				
				node = att.getNamedItem("timeMultiplier");
				if(node != null) {
					try {
						asteroid.timeMultiplier = Float.parseFloat(node.getTextContent());
					} catch(NumberFormatException e) {
						AdvancedRocketry.logger.warn("Asteroid " + asteroid.ID + " has invalid timeMultiplier value");
					}
				}
				else
					asteroid.timeMultiplier = 1f;

				node = att.getNamedItem("baseStack");
				if(node != null) {
					ItemStack stack = getStack(node.getTextContent());
					if(!stack.isEmpty())
						asteroid.baseStack = (stack);
					else {
						AdvancedRocketry.logger.warn("Asteroid " + asteroid.ID + " has invalid baseStack: " + node.getTextContent());
					}
				}
				else
					asteroid.baseStack = new ItemStack(Blocks.COBBLESTONE);
			}
			Node asteroidNode = childNode.getFirstChild();

			while(asteroidNode != null) {
				if(asteroidNode.getNodeType() != Node.ELEMENT_NODE || !asteroidNode.getNodeName().equalsIgnoreCase("ore")) { 
					asteroidNode = asteroidNode.getNextSibling();
					continue;
				}

				if(asteroidNode.getNodeName().equalsIgnoreCase("ore")) {
					NamedNodeMap att = asteroidNode.getAttributes();

					//Add itemStacks
					Node nodeStack = att.getNamedItem("itemStack");
					Node nodeChance = att.getNamedItem("chance");
					if(nodeStack != null && nodeChance != null)
					{
						ItemStack stack = getStack(nodeStack.getTextContent());
						if(!stack.isEmpty())
							asteroid.itemStacks.add(stack);
						else {
							AdvancedRocketry.logger.warn("Asteroid " + asteroid.ID + " has invalid ore: " + nodeStack.getTextContent());
							//Don't need to remove anything here
							asteroidNode = asteroidNode.getNextSibling();
							continue;
						}
						
						try {
							asteroid.stackProbabilities.add(Float.parseFloat(nodeChance.getTextContent()));
						} catch (NumberFormatException e) {
							AdvancedRocketry.logger.warn("Asteroid " + asteroid.ID + " has invalid stack probability: " + nodeChance.getTextContent());
							//Make sure the list size syncs
							asteroid.itemStacks.remove(asteroid.itemStacks.size()-1);
							asteroidNode = asteroidNode.getNextSibling();
							continue;
						}
					}
					else
						AdvancedRocketry.logger.warn("Asteroid " + asteroid.ID + " expected 'itemStack' and 'chance' tags, at least one is missing");
				}

				asteroidNode = asteroidNode.getNextSibling();
			}

			mapping.add(asteroid);

			childNode = childNode.getNextSibling();
		}

		return mapping;
	}

	@NotNull
	public static ItemStack getStack(String text) {
		//Backwards compat, " " used to be the delimiter
		String[] splitStr = text.contains(";") ? text.split(";") : text.split(" ");
		
		int meta = 0;
		int size = 1;
		//format: "name;meta;size"
		if(splitStr.length > 1) {
			try {
				meta = Integer.parseInt(splitStr[1].trim());
			} catch( NumberFormatException e) {
				AdvancedRocketry.logger.warn("Unable to parse int in asteroid config: \"" + splitStr[1] + "\"");
			}
		}

		ItemStack stack = ItemStack.EMPTY;
		Block block = Block.getBlockFromName(splitStr[0].trim());
		if(block == null) {
			Item item = Item.getByNameOrId(splitStr[0].trim());
			if(item != null)
				stack = new ItemStack(item, size, meta);
		}
		else
			stack = new ItemStack(block, size, meta);
	
		return stack;
	}
}
