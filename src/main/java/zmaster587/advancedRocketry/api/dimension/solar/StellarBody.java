package zmaster587.advancedRocketry.api.dimension.solar;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import zmaster587.advancedRocketry.api.dimension.DimensionProperties;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.MathHelper;

public class StellarBody {

	private int temperature;
	private HashMap<Integer,DimensionProperties> planets;
	int numPlanets;
	int discoveredPlanets;
	float color[];
	int id;

	public StellarBody() {
		planets = new HashMap<Integer,DimensionProperties>();
	}
	
	public int getDisplayRadius() {
		return 200;
	}
	
	/**
	 * @param temp the temperature, in Kelvin, of this star
	 */
	public void setTemperature(int temp) {
		temperature = temp;
	}
	
	/**
	 * @param planet registers this planet to be in orbit around this star
	 */
	public void addPlanet(DimensionProperties planet) {
		planets.put(planet.getId(), planet);
	}
	
	/**
	 * @param planet
	 * @return the {@link DimensionProperties} of the planet orbiting this star, or null if the planet does not exist
	 */
	public DimensionProperties removePlanet(DimensionProperties planet) {
		return planets.remove(planet.getId());
	}

	/**
	 * @return the number of planets orbiting this star
	 */
	public int getNumPlanets() {
		return numPlanets;
	}

	/**
	 * @return returns the unique id of this star
	 */
	public int getId() {
		return id;
	}
	
	/**
	 * @param id the new id of this star
	 */
	public void setId(int id) {
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
		float[] color = getColor();
		
		return (int)(color[0]*0xFF) | ((int)(color[1]*0xFF) << 8) | ((int)(color[2]*0xFF) << 16);
	}
	
	//Thank you to http://www.tannerhelland.com/4435/convert-temperature-rgb-algorithm-code/
	/**
	 * @return the color of the star as an array of floats with length 3
	 */
	public float[] getColor() {

		if(color == null) {
			//Define
			float color[] = new float[3];
			float temperature = ((getTemperature() * .477f) + 10f); //0 -> 10 100 -> 57.7

			//Find red
			if(temperature < 66)
				color[0] = 1f;
			else {
				color[0] = temperature - 60;
				color[0] = 329.69f * (float)Math.pow(color[0], -0.1332f);

				color[0] = MathHelper.clamp_float(color[0]/255f, 0f, 1f);
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
			color[1] = MathHelper.clamp_float(color[1]/255f, 0f, 1f);


			//Calculate Blue
			if(temperature > 67)
				color[2] = 1f;
			else if(temperature <= 19){
				color[2] = 0f;
			}
			else {
				color[2] = temperature - 10;
				color[2] = (float) (138.51f * Math.log(color[2]) - 305.04f);
				color[2] = MathHelper.clamp_float(color[2]/255f, 0f, 1f);
			}

			this.color = color;
		}
		return color;
	}

	/**
	 * @return List of {@link DimensionProperties} of planets orbiting this star
	 */
	public List<DimensionProperties> getPlanets() {
		return new ArrayList<DimensionProperties>(planets.values());
	}
	
	public void writeToNBT(NBTTagCompound nbt) {
		nbt.setInteger("id", this.id);
		nbt.setInteger("temperature", temperature);
		nbt.setInteger("numPlanets", numPlanets);
	}
	
	public void readFromNBT(NBTTagCompound nbt) {
		id = nbt.getInteger("id");
		temperature = nbt.getInteger("temperature");
		numPlanets = nbt.getInteger("numPlanets");
	}
}
