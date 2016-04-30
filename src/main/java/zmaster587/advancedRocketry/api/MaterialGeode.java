package zmaster587.advancedRocketry.api;

import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;

public class MaterialGeode extends Material {

	public static final MaterialGeode geode = new MaterialGeode(MapColor.obsidianColor);
	
	public MaterialGeode(MapColor p_i2116_1_) {
		super(p_i2116_1_);
		this.setRequiresTool();
	}

}
