package zmaster587.advancedRocketry.api;

import net.minecraft.item.ItemStack;
import zmaster587.advancedRocketry.api.MaterialRegistry.AllowedProducts;


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
