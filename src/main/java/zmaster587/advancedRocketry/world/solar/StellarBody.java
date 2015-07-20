package zmaster587.advancedRocketry.world.solar;

import java.util.List;

import net.minecraft.util.MathHelper;

public class StellarBody {
	
	private int temperature;
	private List<Planet> planets;
	
	public StellarBody(int temperature) {
		this.temperature = temperature;
		
	}
	
	public int getTemperature() {
		return temperature;
	}
	
	//Thank you to http://www.tannerhelland.com/4435/convert-temperature-rgb-algorithm-code/
	public float[] getColor() {
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
		
		return color;
		
	}
	
	public List<Planet> getPlanets() {
		return planets;
	}
}
