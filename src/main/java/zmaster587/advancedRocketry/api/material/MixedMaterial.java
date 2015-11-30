package zmaster587.advancedRocketry.api.material;

import net.minecraft.item.ItemStack;


public class MixedMaterial {
	
	ItemStack[] product;
	Object input;
	Class process;
	
	public MixedMaterial(Class process, Object input, ItemStack[] product) {
		this.product = product;
		this.process = process;
		this.input = input;
	}
	
	public ItemStack[] getProducts() {
		return product;
	}
	
	public Object getInput() {
		return input;
	}
	
	public Class getMachine() {
		return process;
	}
}
