package zmaster587.advancedRocketry.api.dimension.solar;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.common.util.Constants.NBT;
import zmaster587.advancedRocketry.api.dimension.IDimensionProperties;
import zmaster587.advancedRocketry.dimension.DimensionProperties;
import zmaster587.advancedRocketry.util.SpacePosition;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

public class StellarBody {

	private int temperature;
	private HashMap<ResourceLocation,IDimensionProperties> planets;
	int numPlanets;
	int discoveredPlanets;
	float[] color;
	ResourceLocation id;
	float size;
	String name;
	short posX, posZ;
	public List<StellarBody> subStars;
	float starSeperation;
	private boolean isBlackHole;
	StellarBody parentStar;

	public StellarBody() {
		planets = new HashMap<>();
		size = 1f;
		subStars = new LinkedList<>();
		starSeperation = 5f;
		isBlackHole = false;
	}
	
	public List<StellarBody> getSubStars() {
		return subStars;
	}

	public void addSubStar(StellarBody star) {
		if(star.name == null)
			star.setName(name + "-" + (subStars.size() + 1));
		star.setId(this.id);
		subStars.add(star);
		star.parentStar = this;
	}
	
	public boolean isBlackHole() {
		return isBlackHole;
	}
	
	public void setBlackHole(boolean isBlackHole) {
		this.isBlackHole = isBlackHole;
	}
	
	public int getDisplayRadius() {
		return (int)(100*size);
	}
	
	//Returns the distance between the star and sub stars
	public float getStarSeparation() {
		return starSeperation;
	}
	
	public void setStarSeparation(float seperation) {
		this.starSeperation = seperation;
	}
	
	public float getSize() {
		return size;
	}
	
	public void setSize(float size) {
		this.size = size;
	}

	public void setPosX(int x) {
		posX = (short)x;
	}

	public void setPosZ(int x) {
		posZ = (short)x;
	}

	public int getPosX() {
		return posX;
	}

	public int getPosZ() {
		return posZ;
	}

	/**
	 * @param temp the temperature, in Kelvin, of this star
	 */
	public void setTemperature(int temp) {
		temperature = temp;
		color = getColor();
	}

	/**
	 * @param planet registers this planet to be in orbit around this star
	 */
	public void addPlanet(IDimensionProperties planet) {
		if(!planets.containsKey(planet.getId()))
			numPlanets++;
		planets.put(planet.getId(), planet);
	}

	/**
	 * @param planet
	 * @return the {@link DimensionProperties} of the planet orbiting this star, or null if the planet does not exist
	 */
	public IDimensionProperties removePlanet(IDimensionProperties planet) {
		numPlanets--;
		return planets.remove(planet.getId());
	}

	/**
	 * @return the number of planets orbiting this star
	 */
	public int getNumPlanets() {
		if(parentStar != null)
			return parentStar.getNumPlanets();
		return numPlanets;
	}

	/**
	 * @return returns the unique id of this star
	 */
	public ResourceLocation getId() {
		return id;
	}

	/**
	 * @param id the new id of this star
	 */
	public void setId(ResourceLocation id) {
		this.id = id;
	}

	/**
	 * @return the temperature, in kelvin, of the star
	 */
	public int getTemperature() {
		return temperature;
	}

	/**
	 * @return the RGB color of this star represented as an int
	 */
	public int getColorRGB8() {
		if(color == null) {
			color = getColor();
		}

		return (int)(color[0]*0xFF) | ((int)(color[1]*0xFF) << 8) | ((int)(color[2]*0xFF) << 16);
	}

	//Thank you to http://www.tannerhelland.com/4435/convert-temperature-rgb-algorithm-code/
	/**
	 * @return the color of the star as an array of floats with length 3
	 */
	public float[] getColor() {


		//Define
		float[] color = new float[3];
		float temperature = ((getTemperature() * .477f) + 10f); //0 -> 10 100 -> 57.7

		//Find red
		if(temperature < 66)
			color[0] = 1f;
		else {
			color[0] = temperature - 60;
			color[0] = 329.69f * (float)Math.pow(color[0], -0.1332f);

			color[0] = MathHelper.clamp(color[0]/255f, 0f, 1f);
		}

		//Calc Green
		if(temperature < 66) {
			color[1] = temperature;
			color[1] = (float) (99.47f * Math.log(color[1]) - 161.1f);
		}
		else {
			color[1] = temperature - 60;
			color[1] = 288f * (float)Math.pow(color[1], -0.07551);

		}
		color[1] = MathHelper.clamp(color[1]/255f, 0f, 1f);


		//Calculate Blue
		if(temperature > 67)
			color[2] = 1f;
		else if(temperature <= 19){
			color[2] = 0f;
		}
		else {
			color[2] = temperature - 10;
			color[2] = (float) (138.51f * Math.log(color[2]) - 305.04f);
			color[2] = MathHelper.clamp(color[2]/255f, 0f, 1f);
		}

		return color;
	}

	public String getName() {
		return name;
	}

	public void setName(String str) {
		name = str;
	}

	/**
	 * @return List of {@link DimensionProperties} of planets orbiting this star
	 */
	public List<IDimensionProperties> getPlanets() {
		return new ArrayList<>(planets.values());
	}

	public void writeToNBT(CompoundNBT nbt) {
		nbt.putString("id", this.id.toString());
		nbt.putInt("temperature", temperature);
		nbt.putString("name", name);
		nbt.putShort("posX", posX);
		nbt.putShort("posZ", posZ);
		nbt.putFloat("size", size);
		nbt.putFloat("seperation", starSeperation);
		nbt.putBoolean("isBlackHole", isBlackHole);
		
		ListNBT list = new ListNBT();
		
		for(StellarBody body : subStars) {
			CompoundNBT tag = new CompoundNBT();
			body.writeToNBT(tag);
			list.add(tag);
		}
		
		if(!list.isEmpty())
			nbt.put("subStars", list);
	}

	public void readFromNBT(CompoundNBT nbt) {
		id = new ResourceLocation(nbt.getString("id"));
		temperature = nbt.getInt("temperature");
		name = nbt.getString("name");
		posX = nbt.getShort("posX");
		posZ = nbt.getShort("posZ");
		isBlackHole = nbt.getBoolean("isBlackHole");
		
		if(nbt.contains("size"))
			size = nbt.getFloat("size");
		
		if(nbt.contains("seperation"))
			starSeperation = nbt.getFloat("seperation");
		
		subStars.clear();
		if(nbt.contains("subStars")) {
			ListNBT list = nbt.getList("subStars", NBT.TAG_COMPOUND);
			
			for(int i = 0; i < list.size(); i++) {
				StellarBody star = new StellarBody();
				star.readFromNBT(list.getCompound(i));
				subStars.add(star);
				star.parentStar = this;
			}
		}
	}
	
	public SpacePosition getSpacePosition() {
		//TODO
		return new SpacePosition();
	}
}
