package zmaster587.advancedRocketry.util;

import net.minecraft.item.ItemStack;

import java.util.LinkedList;
import java.util.List;
import java.util.Random;

public class Asteroid {
	public String ID;
	public int distance;					//distance from the star, impacts fuelcost
	public int mass;						//factor of the amount of material total
	public int minLevel;					//Minimum level of the telescope required
	public float massVariability;			//variability of mass
	public float richness;					//factor of the ratio of ore to stone
	public float richnessVariability;		//variability of richness
	public float probability;				//probability of the asteroid spawning
	public float timeMultiplier;
	public ItemStack baseStack;
	public List<Float> stackProbabilities;	//
	public List<ItemStack> itemStacks;
	public static Random rand = new Random();
	private static final int precision = 1000;
	

	public Asteroid() {
		stackProbabilities = new LinkedList<>();
		itemStacks = new LinkedList<>();
	}
	
	public String getName() {return ID;}
	public int getDistance() { return distance; }
	public int getMass() {return mass;}
	public int getMinLevel() {return minLevel;}
	public float getProbability() {return probability;}

	public List<StackEntry> getHarvest(long seed) {
		return getHarvest(seed, 1);
	}
	
	
	/**
	 * @param seed to use in RNG
	 * @param uncertainty how uncertain the outcome should be, 1 is default settings, 0 is 100% known
	 * @return the list of stacks in StackEntry form for the harvest
	 */
	public List<StackEntry> getHarvest(long seed, float uncertainty) {
		
		List<StackEntry> entries = new LinkedList<>();
		rand.setSeed(seed);
		
		int myMass = (int)(mass + ((rand.nextFloat()*massVariability)*mass) - massVariability*mass/2f);
		int numOres = (int) (myMass*(richness + rand.nextFloat()*richnessVariability - richnessVariability/2f));
		
		StackEntry entry = new StackEntry();
		baseStack.setCount( myMass - numOres);
		entry.stack = baseStack;
		entry.variability = (int)(uncertainty*entry.stack.getCount());
		entry.midpoint =  (int)(entry.variability *rand.nextFloat() - uncertainty*entry.variability /2f);
		
		if(entry.midpoint + myMass - numOres < entry.variability)
			entry.midpoint = entry.variability;
		else
			entry.midpoint += myMass - numOres;
		entries.add(entry);
		
		int[] ores = new int[itemStacks.size()];
		
		float normFactor = 0;
		for(Float prob : stackProbabilities)
			normFactor += prob;
		 //normFactor /= stackProbabilities.size();
		
		for(int i = 0; i < numOres; i++) {
			
			float probability = rand.nextFloat();
			
			float currentLocation = 0;
			int counter = 0;
			
			for(Float prob : stackProbabilities) {
				if(probability <= currentLocation + prob/normFactor && currentLocation <= probability) {
					break;
				}
				
				counter++;
				currentLocation += prob/normFactor;
			}
			
			if(counter < itemStacks.size())
				ores[counter]++;
		}
		
		for(int i = 0; i < itemStacks.size(); i++) {
			
			int num = ores[i];
			
			if(num == 0)
				continue;
			
			ItemStack stack = new ItemStack(itemStacks.get(i).getItem(), num);
			entry = new StackEntry();
			
			entry.stack = stack;
			entry.variability = (int) (uncertainty*rand.nextFloat()*num);
			
			entry.midpoint =  (int)(entry.variability *rand.nextFloat() - entry.variability /2f);
			
			if(entry.midpoint + num < entry.variability)
				entry.midpoint = entry.variability;
			else
				entry.midpoint += num;
			
			entries.add(entry);
		}
		
		return entries;
	}
	
	
	public static class StackEntry {
		public ItemStack stack;
		public int variability;
		public int midpoint;
		public boolean isKnown;
	}
}
